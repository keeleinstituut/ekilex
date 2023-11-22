package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordEtymNode;
import eki.ekilex.data.WordEtymTree;
import eki.ekilex.service.WordEtymService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class WordEtymController extends AbstractPrivateSearchController {

	@Autowired
	private WordEtymService wordEtymService;

	@GetMapping("/wordetym/{wordId}")
	public String wordEtymDetails(@PathVariable("wordId") Long wordId, Model model) {

		WordEtymTree wordEtymTree = wordEtymService.getWordEtymTree(wordId);
		WordEtymNode wordEtym = wordEtymTree.getRoot();
		model.addAttribute("wordEtym", wordEtym);

		return "wordetym";
	}
}
