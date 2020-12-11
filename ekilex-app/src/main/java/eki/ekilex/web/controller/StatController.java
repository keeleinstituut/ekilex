package eki.ekilex.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.service.StatDataService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class StatController extends AbstractPageController {

	@Autowired
	private StatDataService statDataService;

	@GetMapping(STAT_URI)
	public String stat() {

		// TODO move home page ekilex stats here? - yogesh
		return STAT_PAGE;
	}

	@GetMapping(WW_STAT_URI)
	public String getWwSearchStat(
			@RequestParam("datasetCode") String datasetCode,
			@RequestParam("lang") String lang,
			@RequestParam("searchMode") String searchMode,
			@RequestParam("resultsFrom") String resultsFrom,
			@RequestParam("resultsUntil") String resultsUntil,
			Model model) {

		Map<String, Integer> searchStatMap = statDataService.getSearchStat(datasetCode, lang, searchMode, resultsFrom, resultsUntil);
		model.addAttribute("searchStatMap", searchStatMap);
		return STAT_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "wwsearchstat";
	}

}
