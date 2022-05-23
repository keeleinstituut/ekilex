package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LimitedTermEditController extends AbstractMutableDataPageController {

	private final List<String> limitedDatasets = new ArrayList<>(Arrays.asList(DATASET_LIMITED));

	@Autowired
	private CudService cudService;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@PostMapping(LIM_TERM_CREATE_WORD_URI)
	public String createWord(WordLexemeMeaningDetails wordDetails, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			String language = wordDetails.getLanguage();
			boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
			sessionBean.setRecentLanguage(language);
			searchUri = searchHelper.composeSearchUri(limitedDatasets, wordValue);
			cudService.createWord(wordDetails, isManualEventOnUpdateEnabled);
		}
		return "redirect:" + LIM_TERM_SEARCH_URI + searchUri;
	}

	@RequestMapping(LIM_TERM_MEANING_JOIN_URI + "/{targetMeaningId}")
	public String search(@PathVariable("targetMeaningId") Long targetMeaningId, @RequestParam(name = "searchFilter", required = false) String searchFilter,
			Model model, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();

		if (searchFilter == null) {
			String targetMeaningFirstWord = termSearchService.getMeaningFirstWordValue(targetMeaningId, limitedDatasets);
			searchFilter = targetMeaningFirstWord;
		}

		Meaning targetMeaning = lookupService.getMeaningOfJoinTarget(userRole, targetMeaningId, languagesOrder);
		List<Meaning> sourceMeanings = lookupService.getMeaningsOfJoinCandidates(userRole, limitedDatasets, searchFilter, languagesOrder, targetMeaningId);

		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetMeaning", targetMeaning);
		model.addAttribute("sourceMeanings", sourceMeanings);

		return LIM_TERM_MEANING_JOIN_PAGE;
	}

	@PostMapping(LIM_TERM_MEANING_JOIN_URI)
	public String joinMeanings(
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		compositionService.joinMeanings(targetMeaningId, sourceMeaningIds, isManualEventOnUpdateEnabled);

		String wordValue = termSearchService.getMeaningFirstWordValue(targetMeaningId, limitedDatasets);
		String searchUri = searchHelper.composeSearchUriAndAppendId(limitedDatasets, wordValue, targetMeaningId);

		return "redirect:" + LIM_TERM_SEARCH_URI + searchUri;
	}

}
