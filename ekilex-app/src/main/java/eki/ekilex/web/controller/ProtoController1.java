package eki.ekilex.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.WordEtymTree;
import eki.ekilex.service.WordEtymService;

@Controller
public class ProtoController1 implements WebConstant {

	@Autowired
	private WordEtymService wordEtymService;

	@GetMapping(PROTO_URI + "/etym")
	public String initEtymPage() {

		return "proto-etym";
	}

	@GetMapping(PROTO_URI + "/etym/{wordId}")
	public String getEtymPage(@PathVariable("wordId") Long wordId, Model model) {

		WordEtymTree wordEtymTree = wordEtymService.getWordEtymTree(wordId);
		model.addAttribute("wordEtymTree", wordEtymTree);

		return "proto-etym";
	}

	@GetMapping(PROTO_URI + "/wordetymtree/{wordId}")
	@ResponseBody
	public WordEtymTree getWordEtymTree(@PathVariable("wordId") Long wordId) {

		WordEtymTree wordEtymTree = wordEtymService.getWordEtymTree(wordId);

		return wordEtymTree;
	}
}
