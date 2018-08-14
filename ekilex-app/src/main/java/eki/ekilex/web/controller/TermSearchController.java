package eki.ekilex.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningsResult;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(TermSearchController.class);

	@Autowired
	private TermSearchService termSearchService;

	@RequestMapping(value = TERM_SEARCH_URI, method = RequestMethod.GET)
	public String initSearch(Model model) throws Exception {

		if (model.containsAttribute(SEARCH_WORD_KEY)) {
			String searchWord = model.asMap().get(SEARCH_WORD_KEY).toString();
			SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
			return termSearch(sessionBean.getSelectedDatasets(), null, false, null, searchWord, null, sessionBean, model);
		}

		initSearchForms(model);

		MeaningsResult meaningsResult = new MeaningsResult();
		model.addAttribute("searchResult", meaningsResult);

		return TERM_SEARCH_PAGE;
	}

	@RequestMapping(value = TERM_SEARCH_URI, method = RequestMethod.POST)
	public String termSearch(
			@RequestParam(name = "selectedDatasets", required = false) List<String> selectedDatasets,
			@RequestParam(name = "searchMode", required = false) String searchMode,
			@RequestParam(name = "fetchAll", required = false) boolean fetchAll,
			@RequestParam(name = "resultLang", required = false) String resultLang,
			@RequestParam(name = "simpleSearchFilter", required = false) String simpleSearchFilter,
			@ModelAttribute(name = "detailSearchFilter") SearchFilter detailSearchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		cleanup(selectedDatasets, resultLang, simpleSearchFilter, detailSearchFilter, sessionBean, model);

		if (StringUtils.isBlank(searchMode)) {
			searchMode = SEARCH_MODE_SIMPLE;
		}
		selectedDatasets = sessionBean.getSelectedDatasets();
		resultLang = sessionBean.getResultLang();
		MeaningsResult meaningsResult;
		if (StringUtils.equals(SEARCH_MODE_DETAIL, searchMode)) {
			meaningsResult = termSearchService.findMeanings(detailSearchFilter, selectedDatasets, resultLang, fetchAll);
		} else {
			meaningsResult = termSearchService.findMeanings(simpleSearchFilter, selectedDatasets, resultLang, fetchAll);
		}
		model.addAttribute("searchMode", searchMode);
		model.addAttribute("searchResult", meaningsResult);

		return TERM_SEARCH_PAGE;
	}

	@GetMapping(MEANING_DETAILS_URI + "/{meaningId}")
	public String details(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean, Model model) {

		logger.debug("Requesting meaning {} details", meaningId);

		List<String> selectedDatasets = sessionBean.getSelectedDatasets();
		if (CollectionUtils.isEmpty(selectedDatasets)) {
			List<Dataset> allDatasets = commonDataService.getDatasets();
			selectedDatasets = allDatasets.stream().map(dataset -> dataset.getCode()).collect(Collectors.toList());
		}
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		Meaning meaning = termSearchService.getMeaning(meaningId, selectedDatasets, languagesOrder);
		model.addAttribute("meaning", meaning);
		model.addAttribute("meaningId", meaningId);

		return TERM_SEARCH_PAGE + " :: details";
	}
}
