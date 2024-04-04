package eki.ekilex.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Response;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceProperty;
import eki.ekilex.data.SourceRequest;
import eki.ekilex.data.SourceSearchResult;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.service.SourceService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.ValueUtil;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SourceEditController extends AbstractMutableDataPageController {

	private static final Logger logger = LoggerFactory.getLogger(SourceEditController.class);

	private static final int AUTOCOMPLETE_MAX_RESULTS_LIMIT = 15;

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

	@GetMapping(SEARCH_SOURCES_URI)
	public String sourceSearch(@RequestParam String searchFilter, Model model) {

		logger.debug("Searching by : \"{}\"", searchFilter);

		searchFilter = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(searchFilter);
		SourceSearchResult sourceSearchResult = sourceService.getSourceSearchResult(searchFilter);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sourceSearchResult", sourceSearchResult);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_link_dlg";
	}

	@PostMapping(UPDATE_SOURCE_PROPERTY_URI)
	public String updateSourceProperty(
			@RequestParam("sourcePropertyId") Long sourcePropertyId,
			@RequestParam("valueText") String valueText,
			Model model) throws Exception {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String roleDatasetCode = userRole.getDatasetCode();
		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);

		Long sourceId = sourceService.getSourceId(sourcePropertyId);
		logger.debug("Updating source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.updateSourceProperty(sourcePropertyId, valueText, roleDatasetCode);
		Source source = sourceService.getSource(sourceId, userRole);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(CREATE_SOURCE_PROPERTY_URI)
	public String createSourceProperty(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("type") FreeformType type,
			@RequestParam("valueText") String valueText,
			Model model) throws Exception {

		logger.debug("Creating property for source with id: {}", sourceId);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String roleDatasetCode = userRole.getDatasetCode();

		valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
		sourceService.createSourceProperty(sourceId, type, valueText, roleDatasetCode);
		Source source = sourceService.getSource(sourceId, userRole);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@GetMapping(DELETE_SOURCE_PROPERTY_URI + "/{sourcePropertyId}")
	public String deleteSourceProperty(@PathVariable("sourcePropertyId") Long sourcePropertyId, Model model) throws Exception {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String roleDatasetCode = userRole.getDatasetCode();
		Long sourceId = sourceService.getSourceId(sourcePropertyId);
		logger.debug("Deleting source property with id: {}, source id: {}", sourcePropertyId, sourceId);

		sourceService.deleteSourceProperty(sourcePropertyId, roleDatasetCode);
		Source source = sourceService.getSource(sourceId, userRole);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(UPDATE_SOURCE_URI)
	public String updateSource(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("type") SourceType type,
			@RequestParam("name") String name,
			@RequestParam("valuePrese") String valuePrese,
			@RequestParam("comment") String comment,
			@RequestParam(name = "public", required = false) boolean isPublic,
			Model model) throws Exception {

		logger.debug("Updating source type, source id: {}", sourceId);

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		String roleDatasetCode = userRole.getDatasetCode();
		valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);

		sourceService.updateSource(sourceId, type, name, valuePrese, comment, isPublic, roleDatasetCode);
		Source source = sourceService.getSource(sourceId, userRole);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + SOURCE_SEARCH_RESULT;
	}

	@PostMapping(CREATE_SOURCE_URI)
	@ResponseBody
	public String createSource(@RequestBody SourceRequest source) throws Exception {

		String roleDatasetCode = getDatasetCodeFromRole();
		SourceType sourceType = source.getType();
		String name = source.getName();
		String valuePrese = source.getValuePrese();
		valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
		String comment = source.getComment();
		boolean isPublic = source.isPublic();
		logger.debug("Creating new source, name: {}", name);

		List<SourceProperty> sourceProperties = processSourceProperties(source);
		Long sourceId = sourceService.createSource(sourceType, name, valuePrese, comment, isPublic, sourceProperties, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		return String.valueOf(sourceId);
	}

	@PostMapping(CREATE_SOURCE_AND_SOURCE_LINK_URI)
	@ResponseBody
	public String createSourceAndSourceLink(@RequestBody SourceRequest source, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		SourceType sourceType = source.getType();
		String name = source.getName();
		String valuePrese = source.getValuePrese();
		valuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valuePrese);
		String comment = source.getComment();
		boolean isPublic = source.isPublic();
		Long sourceLinkOwnerId = source.getId();
		String sourceLinkOwnerCode = source.getOpCode();
		String roleDatasetCode = getDatasetCodeFromRole();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		logger.debug("Creating new source and source link, name: {}", name);

		List<SourceProperty> sourceProperties = processSourceProperties(source);
		sourceLinkService.createSourceAndSourceLink(
				sourceType, name, valuePrese, comment, isPublic, sourceProperties, sourceLinkOwnerId, sourceLinkOwnerCode, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	@GetMapping(VALIDATE_DELETE_SOURCE_URI + "/{sourceId}")
	@ResponseBody
	public Response validateSourceDelete(@PathVariable("sourceId") Long sourceId) {

		logger.debug("Validating source delete, source id: {}", sourceId);
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

		logger.debug("Deleting source with id: {}", sourceId);
		String roleDatasetCode = getDatasetCodeFromRole();

		sourceService.deleteSource(sourceId, roleDatasetCode);
		return RESPONSE_OK_VER1;
	}

	@PostMapping(SOURCE_JOIN_URI)
	public String joinSources(
			@RequestParam("sourceId") Long sourceId,
			@RequestParam("previousSearch") String previousSearch,
			Model model) {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		Source targetSource = sourceService.getSource(sourceId, userRole);
		model.addAttribute("targetSource", targetSource);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

	@PostMapping(JOIN_SOURCES_URI)
	public String joinSources(
			@RequestParam("targetSourceId") Long targetSourceId,
			@RequestParam("originSourceId") Long originSourceId) throws Exception {

		String roleDatasetCode = getDatasetCodeFromRole();
		sourceService.joinSources(targetSourceId, originSourceId, roleDatasetCode);
		return REDIRECT_PREF + SOURCE_SEARCH_URI + "/" + targetSourceId;
	}

	@PostMapping(SEARCH_SOURCES_URI)
	public String searchSources(
			@RequestParam("targetSourceId") Long targetSourceId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@RequestParam("previousSearch") String previousSearch,
			Model model) {

		// TODO remove join sources functionality after removing source attributes?
		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		Source targetSource = sourceService.getSource(targetSourceId, userRole);
		List<Source> sources = sourceService.getSourcesExcludingOne(searchFilter, targetSource, userRole);
		model.addAttribute("targetSource", targetSource);
		model.addAttribute("sources", sources);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("previousSearch", previousSearch);

		return SOURCE_JOIN_PAGE;
	}

	private List<SourceProperty> processSourceProperties(SourceRequest source) {

		List<SourceProperty> sourceProperties = source.getProperties();

		sourceProperties.forEach(property -> {
			String valueText = property.getValueText();
			valueText = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(valueText);
			property.setValueText(valueText);
		});

		sourceProperties = sourceProperties.stream()
				.filter(sourceProperty -> StringUtils.isNotBlank(sourceProperty.getValueText()))
				.collect(Collectors.toList());

		return sourceProperties;
	}

}
