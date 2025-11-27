package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

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

	@PostMapping(COLLOC_MEMBER_MOVE_URI)
	@ResponseBody
	public String moveCollocMember(
			@RequestParam("collocLexemeIds") List<Long> collocLexemeIds,
			@RequestParam("sourceCollocMemberLexemeId") Long sourceCollocMemberLexemeId,
			@RequestParam("targetCollocMemberLexemeId") Long targetCollocMemberLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();

		collocationService.moveCollocMember(collocLexemeIds, sourceCollocMemberLexemeId, targetCollocMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
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
