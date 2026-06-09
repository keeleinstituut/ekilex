package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.etym2.WordEtymTree;
import eki.ekilex.service.EtymService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class EtymController extends AbstractPrivateSearchController {

	@Autowired
	private EtymService etymService;

	@GetMapping("/wordetymtree/{wordId}")
	@ResponseBody
	public WordEtymTree getWordEtymTree(@PathVariable("wordId") Long wordId) {
		return etymService.getWordEtymTree(wordId);
	}
}
