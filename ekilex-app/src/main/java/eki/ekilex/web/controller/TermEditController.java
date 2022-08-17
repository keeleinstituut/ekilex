package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Response;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermCreateWordAndMeaningDetails;
import eki.ekilex.data.TermCreateWordAndMeaningRequest;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.UserMessage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TermEditController extends AbstractMutableDataPageController {

	private static final Logger logger = LoggerFactory.getLogger(TermEditController.class);

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private CudService cudService;

	@RequestMapping(MEANING_JOIN_URI + "/{targetMeaningId}")
	public String search(@PathVariable("targetMeaningId") Long targetMeaningId, @RequestParam(name = "searchFilter", required = false) String searchFilter,
			Model model, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> userPrefDatasetCodes = getUserPreferredDatasetCodes();
		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();

		if (searchFilter == null) {
			String targetMeaningFirstWord = termSearchService.getMeaningFirstWordValue(targetMeaningId, userPrefDatasetCodes);
			searchFilter = targetMeaningFirstWord;
		}

		Meaning targetMeaning = lookupService.getMeaningOfJoinTarget(userRole, targetMeaningId, languagesOrder);
		List<Meaning> sourceMeanings = lookupService.getMeaningsOfJoinCandidates(userRole, userPrefDatasetCodes, searchFilter, languagesOrder, targetMeaningId);

		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("targetMeaningId", targetMeaningId);
		model.addAttribute("targetMeaning", targetMeaning);
		model.addAttribute("sourceMeanings", sourceMeanings);

		return MEANING_JOIN_PAGE;
	}

	@PostMapping(VALIDATE_MEANING_JOIN_URI)
	@ResponseBody
	public Response validateMeaningJoin(@RequestParam("targetMeaningId") Long targetMeaningId, @RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds) {

		Response response = new Response();
		List<Long> allMeaningIds = new ArrayList<>(sourceMeaningIds);
		allMeaningIds.add(targetMeaningId);
		Map<String, Integer[]> invalidWords = lookupService.getMeaningsWordsWithMultipleHomonymNumbers(allMeaningIds);
		Locale locale = LocaleContextHolder.getLocale();

		if (MapUtils.isNotEmpty(invalidWords)) {
			String message = messageSource.getMessage("meaningjoin.invalid.words.1", new Object[0], locale);

			Iterator<Map.Entry<String, Integer[]>> wordIterator = invalidWords.entrySet().iterator();
			while (wordIterator.hasNext()) {
				String wordValue = wordIterator.next().getKey();
				message += " " + wordValue;
				wordIterator.remove();
				if (wordIterator.hasNext()) {
					message += ",";
				} else {
					message += ".";
				}
			}

			message += " " + messageSource.getMessage("meaningjoin.invalid.words.2", new Object[0], locale);
			response.setStatus(ResponseStatus.INVALID);
			response.setMessage(message);
		} else {
			response.setStatus(ResponseStatus.VALID);
		}
		return response;
	}

	@PostMapping(MEANING_JOIN_URI)
	public String joinMeanings(
			@RequestParam("targetMeaningId") Long targetMeaningId,
			@RequestParam("sourceMeaningIds") List<Long> sourceMeaningIds,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		compositionService.joinMeanings(targetMeaningId, sourceMeaningIds, isManualEventOnUpdateEnabled);

		List<String> datasets = getUserPreferredDatasetCodes();
		String wordValue = termSearchService.getMeaningFirstWordValue(targetMeaningId, datasets);
		String searchUri = searchHelper.composeSearchUriAndAppendId(datasets, wordValue, targetMeaningId);

		return "redirect:" + TERM_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping("/duplicatemeaning/{meaningId}")
	public Response duplicateMeaning(@PathVariable("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		Locale locale = LocaleContextHolder.getLocale();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		Optional<Long> clonedMeaning = Optional.empty();
		try {
			clonedMeaning = compositionService.optionalDuplicateMeaningWithLexemes(meaningId, isManualEventOnUpdateEnabled);
		} catch (Exception ignore) {
			logger.error("", ignore);
		}

		Response response = new Response();
		if (clonedMeaning.isPresent()) {
			String message = messageSource.getMessage("term.duplicate.meaning.success", new Object[0], locale);
			Long duplicateMeaningId = clonedMeaning.get();
			response.setStatus(ResponseStatus.OK);
			response.setMessage(message);
			response.setId(duplicateMeaningId);
		} else {
			String message = messageSource.getMessage("term.duplicate.meaning.fail", new Object[0], locale);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(message);
		}
		return response;
	}

	@PostMapping(TERM_CREATE_WORD_AND_MEANING_URI)
	public String createWordAndMeaning(
			TermCreateWordAndMeaningRequest requestData,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		Long wordId = requestData.getWordId();
		String wordValue = requestData.getWordValue();
		String datasetCode = requestData.getDatasetCode();
		String language = requestData.getLanguage();
		String searchUri = requestData.getSearchUri();
		boolean clearResults = requestData.isClearResults();

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		model.addAttribute("searchUri", searchUri);

		if (clearResults || StringUtils.isAnyBlank(wordValue, datasetCode, language)) {
			TermCreateWordAndMeaningDetails details = lookupService.getDetailsForMeaningAndWordCreate(userRole, wordValue, language, datasetCode, false);
			model.addAttribute("details", details);
			return TERM_CREATE_WORD_AND_MEANING_PAGE;
		}

		sessionBean.setRecentLanguage(language);

		if (wordId != null) {
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createLexeme(wordId, datasetCode, null, isManualEventOnUpdateEnabled);
			Long meaningId = wordLexemeMeaningId.getMeaningId();
			addRedirectSuccessMessage(redirectAttributes, "termcreatemeaning.usermessage.meaning.created");
			String redirectToMeaning = composeRedirectToMeaning(meaningId);
			return redirectToMeaning;
		}

		boolean wordExists = lookupService.wordExists(wordValue, language);
		if (!wordExists) {
			WordLexemeMeaningDetails createWordDetails = new WordLexemeMeaningDetails();
			createWordDetails.setWordValue(wordValue);
			createWordDetails.setLanguage(language);
			createWordDetails.setDataset(datasetCode);
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(createWordDetails, isManualEventOnUpdateEnabled);
			Long meaningId = wordLexemeMeaningId.getMeaningId();
			addRedirectSuccessMessage(redirectAttributes, "termcreatemeaning.usermessage.word.and.meaning.created");
			String redirectToMeaning = composeRedirectToMeaning(meaningId);
			return redirectToMeaning;
		}

		TermCreateWordAndMeaningDetails details = lookupService.getDetailsForMeaningAndWordCreate(userRole, wordValue, language, datasetCode, true);
		model.addAttribute("details", details);
		return TERM_CREATE_WORD_AND_MEANING_PAGE;
	}

	@PostMapping(TERM_CREATE_WORD_URI)
	public String createWord(
			TermCreateWordAndMeaningRequest requestData,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes redirectAttributes,
			Model model) throws Exception {

		Long meaningId = requestData.getMeaningId();
		Long wordId = requestData.getWordId();
		String wordValue = requestData.getWordValue();
		String datasetCode = requestData.getDatasetCode();
		String language = requestData.getLanguage();
		String searchUri = requestData.getSearchUri();
		boolean clearResults = requestData.isClearResults();

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		model.addAttribute("searchUri", searchUri);

		if (clearResults || StringUtils.isAnyBlank(wordValue, datasetCode, language)) {
			TermCreateWordAndMeaningDetails details = lookupService.getDetailsForWordCreate(userRole, meaningId, wordValue, language, false);
			model.addAttribute("details", details);
			return TERM_CREATE_WORD_PAGE;
		}

		sessionBean.setRecentLanguage(language);

		if (wordId != null) {
			cudService.createLexeme(wordId, datasetCode, meaningId, isManualEventOnUpdateEnabled);
			addRedirectSuccessMessage(redirectAttributes, "termcreateword.usermessage.lexeme.created");
			String redirectToMeaning = composeRedirectToMeaning(meaningId);
			return redirectToMeaning;
		}

		boolean meaningHasWord = lookupService.meaningHasWord(meaningId, wordValue, language);
		if (meaningHasWord) {
			addRedirectWarningMessage(redirectAttributes, "termcreateword.usermessage.meaning.word.exists");
			String redirectToMeaning = composeRedirectToMeaning(meaningId);
			return redirectToMeaning;
		}

		boolean wordExists = lookupService.wordExists(wordValue, language);
		if (!wordExists) {
			WordLexemeMeaningDetails wordDetails = new WordLexemeMeaningDetails();
			wordDetails.setMeaningId(meaningId);
			wordDetails.setWordValue(wordValue);
			wordDetails.setLanguage(language);
			wordDetails.setDataset(datasetCode);
			cudService.createWord(wordDetails, isManualEventOnUpdateEnabled);
			addRedirectSuccessMessage(redirectAttributes, "termcreateword.usermessage.word.and.lexeme.created");
			String redirectToMeaning = composeRedirectToMeaning(meaningId);
			return redirectToMeaning;
		}

		boolean isOtherDatasetOnlyWord = lookupService.isOtherDatasetOnlyWord(wordValue, language, datasetCode);
		if (isOtherDatasetOnlyWord) {
			List<Word> words = lookupService.getWords(wordValue, language);
			Long onlyWordId = words.get(0).getWordId();
			cudService.createLexeme(onlyWordId, datasetCode, meaningId, isManualEventOnUpdateEnabled);
			addRedirectSuccessMessage(redirectAttributes, "termcreateword.usermessage.lexeme.created");
			String meaningIdUri = "?id=" + meaningId;
			return REDIRECT_PREF + TERM_SEARCH_URI + meaningIdUri;
		}

		TermCreateWordAndMeaningDetails details = lookupService.getDetailsForWordCreate(userRole, meaningId, wordValue, language, true);
		model.addAttribute("details", details);
		return TERM_CREATE_WORD_PAGE;
	}

	@PostMapping(UPDATE_MEANING_ACTIVE_TAG_COMPLETE_URI + "/{meaningId}")
	@ResponseBody
	public String updateMeaningLexemesActiveTagComplete(@PathVariable Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		UserContextData userContextData = getUserContextData();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		Tag activeTag = userContextData.getActiveTag();
		String activeTagName = activeTag.getName();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		logger.debug("Updating meaning (id: {}} lexemes active tag \"{}\" complete", meaningId, activeTagName);
		cudService.updateMeaningLexemesTagComplete(meaningId, userRoleDatasetCode, activeTag, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	@PostMapping(APPROVE_MEANING)
	@ResponseBody
	public String approveMeaning(@RequestParam("meaningId") Long meaningId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		compositionService.approveMeaning(meaningId, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

	private void addRedirectSuccessMessage(RedirectAttributes redirectAttributes, String successMessageKey) {

		UserMessage userMessage = new UserMessage();
		userMessage.setSuccessMessageKey(successMessageKey);
		redirectAttributes.addFlashAttribute("userMessage", userMessage);
	}

	private void addRedirectWarningMessage(RedirectAttributes redirectAttributes, String warningMessageKey) {

		UserMessage userMessage = new UserMessage();
		userMessage.setWarningMessageKey(warningMessageKey);
		redirectAttributes.addFlashAttribute("userMessage", userMessage);
	}

	private String composeRedirectToMeaning(Long meaningId) {

		String meaningIdUri = "?id=" + meaningId;
		return REDIRECT_PREF + TERM_SEARCH_URI + meaningIdUri;
	}
}
