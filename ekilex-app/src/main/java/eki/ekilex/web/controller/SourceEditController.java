package eki.ekilex.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Response;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceAndSourceLinkRequest;
import eki.ekilex.data.SourceSearchResult;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.service.SourceService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.ValueUtil;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SourceEditController extends AbstractMutableDataPageController {

	@Autowired
	private SourceService sourceService;

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private ValueUtil valueUtil;

	@GetMapping(SOURCE_NAME_SEARCH_URI + "/{nameSearchFilter}")
	@ResponseBody
	public List<String> sourceNameSearch(@PathVariable("nameSearchFilter") String nameSearchFilter) {

		List<String> sourceNames = sourceService.getSourceNames(nameSearchFilter, AUTOCOMPLETE_MAX_RESULTS_LIMIT);

		return sourceNames;
	}

	@GetMapping(SOURCE_QUICK_SEARCH_URI)
	public String sourceSearch(@RequestParam String searchFilter, Model model) {

		EkiUser user = userContext.getUser();
		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);
		SourceSearchResult sourceSearchResult = sourceService.getSourceSearchResult(searchFilter, user);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sourceSearchResult", sourceSearchResult);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_link_dlg";
	}

	@PostMapping(UPDATE_SOURCE_URI)
	public String updateSource(
			@RequestBody Source source,
			Model model) throws Exception {

		Long sourceId = source.getId();
		EkiUser user = userContext.getUser();
		String roleDatasetCode = getDatasetCodeFromRole();
		cleanupSource(source);
		sourceService.updateSource(source, roleDatasetCode);
		source = sourceService.getSource(sourceId, user);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(CREATE_SOURCE_URI)
	@ResponseBody
	public String createSource(@RequestBody Source source) throws Exception {

		String roleDatasetCode = getDatasetCodeFromRole();
		cleanupSource(source);
		Long sourceId = sourceService.createSource(source, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);

		return String.valueOf(sourceId);
	}

	@PostMapping(CREATE_SOURCE_AND_SOURCE_LINK_URI)
	@ResponseBody
	public String createSourceAndSourceLink(@RequestBody SourceAndSourceLinkRequest source, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		Long sourceLinkOwnerId = source.getSourceOwnerId();
		String sourceLinkOwnerName = source.getSourceOwnerName();
		String roleDatasetCode = getDatasetCodeFromRole();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		cleanupSource(source);
		sourceLinkService.createSourceAndSourceLink(source, sourceLinkOwnerId, sourceLinkOwnerName, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	@GetMapping(VALIDATE_DELETE_SOURCE_URI + "/{sourceId}")
	@ResponseBody
	public Response validateSourceDelete(@PathVariable("sourceId") Long sourceId) {

		Locale locale = LocaleContextHolder.getLocale();

		Response response = new Response();
		if (sourceService.validateSourceDelete(sourceId)) {
			response.setStatus(ResponseStatus.OK);
		} else {
			String message = messageSource.getMessage("delete.source.validation.fail", new Object[0], locale);
			response.setStatus(ResponseStatus.INVALID);
			response.setMessage(message);
		}
		return response;
	}

	@GetMapping(DELETE_SOURCE_URI + "/{sourceId}")
	@ResponseBody
	public String deleteSource(@PathVariable("sourceId") Long sourceId) throws Exception {

		String roleDatasetCode = getDatasetCodeFromRole();
		sourceService.deleteSource(sourceId, roleDatasetCode);

		return RESPONSE_OK_VER1;
	}

	@PostMapping(OPEN_SOURCE_JOIN_URI)
	public String sourceJoin(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("previousSearch") String previousSearch,
			Model model) {

		EkiUser user = userContext.getUser();
		Source targetSource = sourceService.getSource(sourceId, user);
		model.addAttribute("targetSource", targetSource);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

	@PostMapping(SOURCE_JOIN_SEARCH_URI)
	public String sourceJoinSearch(
			@RequestParam("targetSourceId") Long targetSourceId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@RequestParam("previousSearch") String previousSearch,
			Model model) {

		EkiUser user = userContext.getUser();
		Source targetSource = sourceService.getSource(targetSourceId, user);
		List<Source> sources = sourceService.getSourcesBasedOnExcludedOne(searchFilter, targetSource, user);
		model.addAttribute("targetSource", targetSource);
		model.addAttribute("sources", sources);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

	@PostMapping(SOURCE_JOIN_URI)
	public String joinSources(
			@RequestParam("targetSourceId") Long targetSourceId,
			@RequestParam("originSourceId") Long originSourceId) throws Exception {

		String roleDatasetCode = getDatasetCodeFromRole();
		sourceService.joinSources(targetSourceId, originSourceId, roleDatasetCode);

		return REDIRECT_PREF + SOURCE_SEARCH_URI + "/" + targetSourceId;
	}

	private void cleanupSource(Source source) {

		String valuePrese = source.getValuePrese();
		valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
		source.setValuePrese(valuePrese);
	}
}
