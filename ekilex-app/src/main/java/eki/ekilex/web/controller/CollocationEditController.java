package eki.ekilex.web.controller;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.CollocConjunct;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocWeight;
import eki.ekilex.data.Response;
import eki.ekilex.data.UpdateCollocOrderRequest;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.CollocationService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CollocationEditController extends AbstractPrivatePageController {

	private static final Logger logger = LoggerFactory.getLogger(CollocationEditController.class);

	@Autowired
	private CollocationService collocationService;

	@ResponseBody
	@PostMapping(UPDATE_COLLOC_MEMBER_GROUP_ORDER_URI)
	public String updateCollocMemberGroupOrder(
			@RequestBody UpdateCollocOrderRequest updateCollocOrderRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Update colloc member group order for {}", updateCollocOrderRequest);

		UserContextData userContextData = getUserContextData();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Long collocLexemeId = updateCollocOrderRequest.getCollocLexemeId();
		Long memberLexemeId = updateCollocOrderRequest.getMemberLexemeId();
		String direction = updateCollocOrderRequest.getDirection();

		collocationService.updateCollocMemberGroupOrder(collocLexemeId, memberLexemeId, direction, userRoleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	@ResponseBody
	@PostMapping(UPDATE_COLLOC_MEMBER_ORDER_URI)
	public String updateCollocMemberOrder(
			@RequestBody UpdateCollocOrderRequest updateCollocOrderRequest,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Update colloc member order for {}", updateCollocOrderRequest);

		UserContextData userContextData = getUserContextData();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Long collocLexemeId = updateCollocOrderRequest.getCollocLexemeId();
		Long memberLexemeId = updateCollocOrderRequest.getMemberLexemeId();
		String direction = updateCollocOrderRequest.getDirection();

		collocationService.updateCollocMemberOrder(collocLexemeId, memberLexemeId, direction, userRoleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(COLLOC_MEMBER_MOVE_OR_COPY_SELECTED_URI)
	@ResponseBody
	public Response moveOrCopySelectedCollocMembers(
			@RequestParam("opName") String opName,
			@RequestParam("collocLexemeIds") List<Long> collocLexemeIds,
			@RequestParam("sourceMemberLexemeId") Long sourceMemberLexemeId,
			@RequestParam("targetMemberLexemeId") Long targetMemberLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Making collocation members \"{}\"", opName);

		Locale locale = LocaleContextHolder.getLocale();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		String message = null;

		if (CollectionUtils.isEmpty(collocLexemeIds)) {
			message = messageSource.getMessage("colloc.message.nomembers", new Object[0], locale);
		} else if (StringUtils.equalsIgnoreCase(opName, "move")) {
			collocationService.moveCollocMembers(collocLexemeIds, sourceMemberLexemeId, targetMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
			message = messageSource.getMessage("colloc.message.collocs.movemember", new Object[0], locale);
		} else if (StringUtils.equalsIgnoreCase(opName, "copy")) {
			collocationService.copyCollocsAndReplaceMembers(collocLexemeIds, sourceMemberLexemeId, targetMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
			message = messageSource.getMessage("colloc.message.collocs.copymember", new Object[0], locale);
		} else {
			message = "n/a";
		}
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);
		return response;
	}

	@PostMapping(COLLOC_MEMBER_MOVE_OR_COPY_ALL_URI)
	@ResponseBody
	public Response moveOrCopyAllCollocMembers(
			@RequestParam("opName") String opName,
			@RequestParam("sourceMemberLexemeId") Long sourceMemberLexemeId,
			@RequestParam("targetMemberLexemeId") Long targetMemberLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		logger.debug("Making collocation members \"{}\"", opName);

		Locale locale = LocaleContextHolder.getLocale();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		String message = null;

		if (StringUtils.equalsIgnoreCase(opName, "move")) {
			collocationService.moveCollocMembers(null, sourceMemberLexemeId, targetMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
			message = messageSource.getMessage("colloc.message.collocs.movemember", new Object[0], locale);
		} else if (StringUtils.equalsIgnoreCase(opName, "copy")) {
			collocationService.copyCollocsAndReplaceMembers(null, sourceMemberLexemeId, targetMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
			message = messageSource.getMessage("colloc.message.collocs.copymember", new Object[0], locale);
		} else {
			message = "n/a";
		}
		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);
		return response;
	}

	@PostMapping(COLLOC_MEMBER_FORM_SEARCH_URI)
	public String searchCollocMemberForm(
			@RequestParam("collocLexemeId") Long collocLexemeId,
			@RequestParam("formValue") String formValue,
			@RequestParam("lang") String lang,
			@RequestParam("datasetCode") String datasetCode,
			Model model) {

		CollocMember collocMember = new CollocMember();
		collocMember.setCollocLexemeId(collocLexemeId);
		List<CollocMemberForm> collocMemberForms = collocationService.getCollocMemberForms(formValue, lang, datasetCode);
		populateCollocMemberModel(collocMember, collocMemberForms, model);

		return "colloc" + PAGE_FRAGMENT_ELEM + "colloc_member_forms";
	}

	@PostMapping(COLLOC_MEMBER_MEANING_SEARCH_URI)
	public String searchCollocMembeMeaning(@RequestParam("id") Long collocMemberId, Model model) {

		CollocMember collocMember = collocationService.getCollocMember(collocMemberId);
		List<CollocMemberForm> collocMemberForms = collocationService.getCollocMemberForms(collocMember);
		populateCollocMemberModel(collocMember, collocMemberForms, model);

		return "colloc" + PAGE_FRAGMENT_ELEM + "colloc_member_forms";
	}

	private void populateCollocMemberModel(CollocMember collocMember, List<CollocMemberForm> collocMemberForms, Model model) {

		List<CollocWeight> collocWeights = collocationService.getCollocWeights();
		List<CollocConjunct> collocConjuncts = collocationService.getCollocConjuncts();
		boolean collocMemberFormsExist = CollectionUtils.isNotEmpty(collocMemberForms);

		model.addAttribute("collocMember", collocMember);
		model.addAttribute("collocMemberForms", collocMemberForms);
		model.addAttribute("collocMemberFormsExist", collocMemberFormsExist);
		model.addAttribute("collocWeights", collocWeights);
		model.addAttribute("collocConjuncts", collocConjuncts);
	}

	@PostMapping(COLLOC_MEMBER_SAVE_URI)
	@ResponseBody
	public Response saveCollocMember(CollocMember collocMember, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = collocationService.saveCollocMember(collocMember, roleDatasetCode, isManualEventOnUpdateEnabled);
		return response;
	}
}
