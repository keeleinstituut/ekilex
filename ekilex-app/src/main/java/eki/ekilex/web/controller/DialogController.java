package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.EkiUser;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class DialogController extends AbstractPageController {

	@GetMapping(COMPONENT_URI + "/commonwordlangselect/{datasetCode}")
	public String getCommonWordLangSelect(@PathVariable("datasetCode") String datasetCode, Model model) {

		populateUserPermLanguagesModel(datasetCode, model);

		return "common :: word_perm_lang_select";
	}

	@GetMapping(COMPONENT_URI + "/termdeflangselect/{datasetCode}")
	public String getTermDefLangSelect(@PathVariable("datasetCode") String datasetCode, Model model) {

		populateUserPermLanguagesModel(datasetCode, model);

		return "termdialog :: definition_perm_lang_select";
	}

	private void populateUserPermLanguagesModel(String datasetCode, Model model) {
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, datasetCode);
		model.addAttribute("userPermLanguages", userPermLanguages);
	}
}
