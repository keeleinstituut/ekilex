package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Response;
import eki.ekilex.data.WordVariant;
import eki.ekilex.data.WordVariantCandidates;
import eki.ekilex.service.VariantService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class VariantEditController extends AbstractPrivatePageController {

	@Autowired
	private VariantService variantService;

	@PostMapping("create_word_variant")
	@ResponseBody
	public Response createWordVariant(
			WordVariant wordVariant,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = variantService.createWordVariant(wordVariant, roleDatasetCode, isManualEventOnUpdateEnabled);
		return response;
	}

	@PostMapping("search_word_variant")
	public String searchVariantWord(WordVariant wordVariant, Model model) throws Exception {

		WordVariantCandidates wordVariantCandidates = variantService.getWordVariantCandidates(wordVariant);
		model.addAttribute("wordVariantCandidates", wordVariantCandidates);

		return "variant" + PAGE_FRAGMENT_ELEM + "select_word_variant_content";
	}
}
