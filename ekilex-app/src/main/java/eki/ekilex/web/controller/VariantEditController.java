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
import eki.ekilex.data.LexemeVariantBean;
import eki.ekilex.data.WordCandidates;
import eki.ekilex.service.VariantService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class VariantEditController extends AbstractPrivatePageController {

	@Autowired
	private VariantService variantService;

	@PostMapping("create_lexeme_variant")
	@ResponseBody
	public Response createLexemeVariant(
			LexemeVariantBean lexemeVariantBean,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		Response response = variantService.createLexemeVariant(lexemeVariantBean, roleDatasetCode, isManualEventOnUpdateEnabled);
		return response;
	}

	@PostMapping("search_lexeme_variant")
	public String searchLexemeVariant(LexemeVariantBean lexemeVariantBean, Model model) throws Exception {

		WordCandidates wordCandidates = variantService.getLexemeVariantWordCandidates(lexemeVariantBean);
		model.addAttribute("wordCandidates", wordCandidates);

		return "variant" + PAGE_FRAGMENT_ELEM + "select_lexeme_variant_content";
	}
}
