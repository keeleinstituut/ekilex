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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocWeight;
import eki.ekilex.service.CollocationService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class CollocationEditController extends AbstractPrivatePageController {

	private static final Logger logger = LoggerFactory.getLogger(CollocationEditController.class);

	@Autowired
	private CollocationService collocationService;

	@PostMapping(COLLOC_MEMBER_MOVE_URI)
	@ResponseBody
	public String collocMemberMove(
			@RequestParam("collocLexemeIds") List<Long> collocLexemeIds,
			@RequestParam("sourceCollocMemberLexemeId") Long sourceCollocMemberLexemeId,
			@RequestParam("targetCollocMemberLexemeId") Long targetCollocMemberLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();

		collocationService.moveCollocMember(collocLexemeIds, sourceCollocMemberLexemeId, targetCollocMemberLexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	@PostMapping(COLLOC_MEMBER_SEARCH_URI)
	public String collocMemberSearch(
			@RequestParam("collocLexemeId") Long collocLexemeId,
			@RequestParam("formValue") String formValue,
			@RequestParam("lang") String lang,
			@RequestParam("datasetCode") String datasetCode,
			Model model) {

		List<CollocMemberForm> collocMemberForms = collocationService.getCollocMemberForms(formValue, lang, datasetCode);
		List<Classifier> posGroups = commonDataService.getPosGroups();
		List<Classifier> relGroups = commonDataService.getRelGroups();
		List<CollocWeight> collocWeights = collocationService.getCollocWeights();

		boolean collocMemberFormsExist = CollectionUtils.isNotEmpty(collocMemberForms);
		model.addAttribute("collocLexemeId", collocLexemeId);
		model.addAttribute("formValue", formValue);
		model.addAttribute("lang", lang);
		model.addAttribute("collocMemberForms", collocMemberForms);
		model.addAttribute("collocMemberFormsExist", collocMemberFormsExist);
		model.addAttribute("posGroups", posGroups);
		model.addAttribute("relGroups", relGroups);
		model.addAttribute("collocWeights", collocWeights);

		return "colloc" + PAGE_FRAGMENT_ELEM + "colloc_member_forms";
	}

	@PostMapping(COLLOC_MEMBER_CREATE_URI)
	@ResponseBody
	public String collocMemberCreate() {
		
		return RESPONSE_OK_VER1;
	}
}
