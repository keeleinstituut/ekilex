package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningCreateWordDetails;
import eki.ekilex.data.UserMessage;
import eki.ekilex.data.Response;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermCreateMeaningRequest;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordMeaningRelationsDetails;
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

	public static final String DEFAULT_CANDIDATE_MEANING_REL = "duplikaadikandidaat";

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
			TermCreateMeaningRequest details,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes redirectAttributes) throws Exception {

		Long wordId = details.getWordId();
		String wordValue = details.getWordValue();
		String dataset = details.getDataset();
		String language = details.getLanguage();
		String searchUri = details.getSearchUri();
		boolean clearResults = details.isClearResults();

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		redirectAttributes.addFlashAttribute("searchUri", searchUri);
		redirectAttributes.addFlashAttribute("details", details);

		if (wordId != null && StringUtils.isNotBlank(dataset)) {
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createLexeme(wordId, dataset, null, isManualEventOnUpdateEnabled);
			Long meaningId = wordLexemeMeaningId.getMeaningId();
			UserMessage userMessage = new UserMessage();
			userMessage.setSuccessMessageKey("termcreatemeaning.usermessage.meaning.created");
			redirectAttributes.addFlashAttribute("userMessage", userMessage);
			String meaningIdUri = "?id=" + meaningId;
			return REDIRECT_PREF + TERM_SEARCH_URI + meaningIdUri;
		}

		if (clearResults || StringUtils.isAnyBlank(wordValue, dataset, language)) {
			return REDIRECT_PREF + TERM_CREATE_WORD_AND_MEANING_URI;
		}

		sessionBean.setRecentLanguage(language);
		boolean wordExists = lookupService.wordExists(wordValue, language);

		if (!wordExists) {
			WordLexemeMeaningDetails wordDetails = new WordLexemeMeaningDetails();
			wordDetails.setWordValue(wordValue);
			wordDetails.setLanguage(language);
			wordDetails.setDataset(dataset);
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(wordDetails, isManualEventOnUpdateEnabled);
			Long meaningId = wordLexemeMeaningId.getMeaningId();
			UserMessage userMessage = new UserMessage();
			userMessage.setSuccessMessageKey("termcreatemeaning.usermessage.word.and.meaning.created");
			redirectAttributes.addFlashAttribute("userMessage", userMessage);
			String meaningIdUri = "?id=" + meaningId;
			return REDIRECT_PREF + TERM_SEARCH_URI + meaningIdUri;
		}

		List<MeaningCreateWordDetails> wordCandidates = lookupService.getWordCandidatesForMeaningCreate(userRole, wordValue, language, dataset);
		redirectAttributes.addFlashAttribute("wordCandidates", wordCandidates);
		return REDIRECT_PREF + TERM_CREATE_WORD_AND_MEANING_URI;
	}

	@GetMapping(TERM_CREATE_WORD_AND_MEANING_URI)
	public String initCreateWordAndMeaning(Model model) {

		boolean detailsExists = model.asMap().get("details") != null;
		if (!detailsExists) {
			model.addAttribute("details", new TermCreateMeaningRequest());
		}

		return TERM_CREATE_WORD_AND_MEANING_PAGE;
	}

	@PostMapping(TERM_CREATE_WORD_URI)
	public String createWord(
			WordLexemeMeaningDetails wordDetails,
			@RequestParam("backUri") String backUri,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		if (StringUtils.isNotBlank(wordValue)) {
			String language = wordDetails.getLanguage();
			Long meaningId = wordDetails.getMeaningId();
			String dataset = wordDetails.getDataset();
			DatasetPermission userRole = userContext.getUserRole();
			Long userId = userContext.getUserId();
			List<String> userPrefDatasetCodes = getUserPreferredDatasetCodes();
			List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
			boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
			WordLexemeMeaningIdTuple wordLexemeMeaningId = null;

			sessionBean.setRecentLanguage(language);

			boolean meaningHasWord = lookupService.meaningHasWord(meaningId, wordValue, language);
			if (!meaningHasWord) {
				List<Meaning> relationCandidates = lookupService.getMeaningsOfRelationCandidates(userRole, wordValue, meaningId, languagesOrder);
				if (CollectionUtils.isNotEmpty(relationCandidates)) {
					attributes.addFlashAttribute("dataset", dataset);
					attributes.addFlashAttribute("wordValue", wordValue);
					attributes.addFlashAttribute("language", language);
					attributes.addFlashAttribute("meaningId", meaningId);
					attributes.addFlashAttribute("relationCandidates", relationCandidates);
					attributes.addFlashAttribute("backUri", backUri);
					return "redirect:" + MEANING_REL_SELECT_URI;
				} else {
					wordLexemeMeaningId = cudService.createWord(wordDetails, isManualEventOnUpdateEnabled);
				}
			}

			if (!userPrefDatasetCodes.contains(dataset)) {
				userPrefDatasetCodes.add(dataset);
				userProfileService.updateUserPreferredDatasets(userPrefDatasetCodes, userId);
			}
			if (meaningId == null && wordLexemeMeaningId != null) {
				meaningId = wordLexemeMeaningId.getMeaningId();
			}
			backUri += "?id=" + meaningId;
		}
		return "redirect:" + TERM_SEARCH_URI + backUri;
	}

	@GetMapping(MEANING_REL_SELECT_URI)
	public String listMeaningRelationCandidates(
			@ModelAttribute(name = "dataset") String dataset,
			@ModelAttribute(name = "wordValue") String wordValue,
			@ModelAttribute(name = "language") String language,
			@ModelAttribute(name = "meaningId") Long meaningId,
			@ModelAttribute(name = "relationCandidates") List<Meaning> relationCandidates,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) {

		model.addAttribute("defaultMeaningRelation", DEFAULT_CANDIDATE_MEANING_REL);
		return MEANING_REL_SELECT_PAGE;
	}

	@PostMapping(CREATE_WORD_AND_MEANING_AND_REL_URI)
	@ResponseBody
	public Response createWordAndMeaningAndRelations(
			@RequestBody WordMeaningRelationsDetails wordMeaningRelationsDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordMeaningRelationsDetails);

		Response response = new Response();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String wordValue = wordMeaningRelationsDetails.getWordValue();
		Long meaningId = wordMeaningRelationsDetails.getMeaningId();
		String datasetCode = wordMeaningRelationsDetails.getDataset();
		String backUri = wordMeaningRelationsDetails.getBackUri();
		String searchUri;
		if (StringUtils.isNotBlank(wordValue)) {

			EkiUser user = userContext.getUser();
			Long userId = user.getId();
			String userName = user.getName();
			List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);

			wordMeaningRelationsDetails.setDataset(datasetCode);
			wordMeaningRelationsDetails.setUserName(userName);
			wordMeaningRelationsDetails.setUserPermDatasetCodes(userPermDatasetCodes);

			WordLexemeMeaningIdTuple wordLexemeMeaningId = compositionService
					.createWordAndMeaningAndRelations(wordMeaningRelationsDetails, isManualEventOnUpdateEnabled);

			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(datasetCode)) {
				selectedDatasets.add(datasetCode);
				userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
			}
			if (meaningId == null) {
				Long createdMeaningId = wordLexemeMeaningId.getMeaningId();
				searchUri = searchHelper.composeSearchUri(selectedDatasets, wordValue);
				searchUri = searchUri + "?id=" + createdMeaningId;
			} else {
				searchUri = backUri + "?id=" + meaningId;
			}
			response.setStatus(ResponseStatus.VALID);
			response.setUri(searchUri);
		} else {
			response.setStatus(ResponseStatus.INVALID);
			response.setMessage("Viga! Terminil puudub väärtus");
		}

		return response;
	}

	@GetMapping(VALIDATE_MEANING_DATA_IMPORT_URI + "/{meaningId}")
	@ResponseBody
	public String validateMeaningDataImport(@PathVariable("meaningId") Long meaningId) {

		logger.debug("Validating meaning data import, meaning id: {}", meaningId);
		Long userId = userContext.getUserId();
		List<String> userPermDatasetCodes = permissionService.getUserPermDatasetCodes(userId);

		boolean isImportValid = compositionService.validateMeaningDataImport(meaningId, userPermDatasetCodes);
		if (isImportValid) {
			return RESPONSE_OK_VER1;
		} else {
			return "invalid";
		}
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
}
