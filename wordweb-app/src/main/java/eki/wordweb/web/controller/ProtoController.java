package eki.wordweb.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.ProtoSearchResult;
import eki.wordweb.service.ProtoService;

@Controller
public class ProtoController implements WebConstant {

	@Autowired
	private ProtoService protoService;

	@GetMapping(PROTO_URI + "/fuzzy")
	public String initFuzzyPage() {

		return "proto-fuzzy";
	}

	@GetMapping(PROTO_URI + "/fuzzy/searchbyalgo")
	@ResponseBody
	public ProtoSearchResult searchByAlgo(
			@RequestParam("wordCrit") String wordCrit,
			@RequestParam("algo") String algo) {

		return protoService.searchByAlgo(wordCrit, algo);
	}
}
