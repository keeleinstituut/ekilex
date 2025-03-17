package eki.wordweb.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.ProtoSearchResult;
import eki.wordweb.service.ProtoService;

// disabled until next POCs
//@Controller
public class ProtoController implements WebConstant {

	private static final String SEARCHBYALGO_URI = "/searchbyalgo";

	@Autowired
	private ProtoService protoService;

	@GetMapping(PROTO_URI + "/fuzzy")
	public String initFuzzyPage(HttpServletRequest request, Model model) {

		String fuzzySearchByAlgoUrl = request.getContextPath() + request.getServletPath() + SEARCHBYALGO_URI;
		model.addAttribute("fuzzySearchByAlgoUrl", fuzzySearchByAlgoUrl);

		return "proto-fuzzy";
	}

	@GetMapping(PROTO_URI + "/fuzzy" + SEARCHBYALGO_URI)
	@ResponseBody
	public ProtoSearchResult searchByAlgo(
			@RequestParam("wordCrit") String wordCrit,
			@RequestParam("algo") String algo) {

		return protoService.searchByAlgo(wordCrit, algo);
	}
}
