package eki.eve.web.controller;

import eki.eve.service.CorporaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ConditionalOnWebApplication
@Controller
public class CorporaController {

	@Autowired
	private CorporaService corporaService;

	@GetMapping("/korp/{sentence}")
	@ResponseBody
	public String generateSoundFileUrl(@PathVariable String sentence, Model model) {

		Map<String, Object> textCorpus = corporaService.fetch(sentence);

		return String.valueOf(textCorpus.get("hits"));
	}

}
