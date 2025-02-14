package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.data.StatSearchFilter;
import eki.common.data.StatSearchResult;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.service.StatDataService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class StatController extends AbstractPrivatePageController {

	private static final String[] SUPPORTED_LANG_CODES = new String[] {LANGUAGE_CODE_EST, LANGUAGE_CODE_ENG, LANGUAGE_CODE_RUS, LANGUAGE_CODE_FRA, LANGUAGE_CODE_UKR};

	@Autowired
	private StatDataService statDataService;

	@ModelAttribute("wwLanguages")
	public List<Classifier> getWwLanguages() {

		List<Classifier> allLanguages = commonDataService.getLanguages();
		List<Classifier> wwLanguages = allLanguages.stream()
				.filter(language -> ArrayUtils.contains(SUPPORTED_LANG_CODES, language.getCode()))
				.collect(Collectors.toList());
		return wwLanguages;
	}

	@GetMapping(STAT_URI)
	public String stat() {
		return STAT_PAGE;
	}

	@GetMapping(WW_STAT_URI)
	public String getWwSearchStat(StatSearchFilter statSearchFilter, Model model) {

		StatSearchResult statSearchResult = statDataService.getStatSearchResult(statSearchFilter);
		model.addAttribute("statSearchResult", statSearchResult);

		return STAT_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "wwsearchstat";
	}
}
