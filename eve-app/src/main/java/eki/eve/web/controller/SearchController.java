package eki.eve.web.controller;

import eki.eve.service.SearchService;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	SearchService search;

	@GetMapping("/search")
	public String search(@RequestParam(required = false) String searchFilter, Model model) {
		logger.debug("doing search");
		if (isNotBlank(searchFilter)) {
			model.addAttribute("searchResults", search.findForms(searchFilter));
			model.addAttribute("searchFilter", searchFilter);
		}
		return "search";
	}

	@GetMapping("/details/{id}")
	@ResponseBody
	public String details(@PathVariable("id") Long id) {
		logger.debug("doing details");
		StringBuilder html = new StringBuilder("<div name=\"" + id + "_details\" class=\"pl-4\">");
		html.append("<div class=\"float-left pr-2\">");
		for (Record rec : (Result<Record>)search.findConnectedForms(id)) {
			html.append(rec.getValue("value"))
					.append(" (")
					.append(rec.get("morph_code")).append("-").append(rec.get("morph_value"))
					.append("; ").append(rec.get("display_form"))
					.append("; ").append(rec.get("vocal_form"))
					.append(")")
					.append("<br/>");
		}
		html.append("</div>").append("<div class=\"float-left w-75\">").append("<ul>");
  		for (Record rec : search.findFormDefinitions(id)) {
			html.append("<li>").append(rec.getValue("value")).append("</li>");
		}
		html.append("</ul>").append("</div>").append("</div>");
		return html.toString();
	}

}
