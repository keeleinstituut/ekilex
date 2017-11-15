package eki.eve.web.controller;

import eki.eve.data.CorporaSentence;
import eki.eve.service.CorporaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@ConditionalOnWebApplication
@Controller
public class CorporaController {

	@Autowired
	private CorporaService corporaService;

	@GetMapping("/korp/{sentence}")
	public String generateSoundFileUrl(@PathVariable String sentence, Model model) {

		List<CorporaSentence> textCorpus = corporaService.fetchSentences(sentence);
		model.addAttribute("sentences", textCorpus);
		return "search :: korp";
	}

}
