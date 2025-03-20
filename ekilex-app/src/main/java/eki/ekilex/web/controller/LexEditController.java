package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.MeaningWordCandidates;
import eki.ekilex.data.Response;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.CompositionService;
import eki.ekilex.service.CudService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.LookupService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class LexEditController extends AbstractPrivatePageController {

	private static final Logger logger = LoggerFactory.getLogger(LexEditController.class);

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private CompositionService compositionService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private CudService cudService;

	@GetMapping(LEX_JOIN_INIT_URI + "/{targetLexemeId}")
	public String lexJoinInit(
			@PathVariable("targetLexemeId") Long targetLexemeId,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		UserContextData userContextData = getUserContextData();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		Lexeme targetLexeme = lexSearchService.getWordLexeme(targetLexemeId, languagesOrder, userProfile, user, true);
		List<Lexeme> sourceLexemes = lookupService.getWordLexemesOfJoinCandidates(user, datasetCodes, targetLexeme, tagNames, null);
		String searchFilter = targetLexeme.getLexemeWord().getWordValue();

		model.addAttribute("targetLexeme", targetLexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sourceLexemes", sourceLexemes);

		return LEX_JOIN_PAGE;
	}

	@PostMapping(LEX_JOIN_SEARCH_URI)
	public String lexJoinSearch(
			@RequestParam("targetLexemeId") Long targetLexemeId,
			@RequestParam("searchFilter") String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		UserContextData userContextData = getUserContextData();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		Lexeme targetLexeme = lexSearchService.getWordLexeme(targetLexemeId, languagesOrder, userProfile, user, true);
		List<Lexeme> sourceLexemes = lookupService.getWordLexemesOfJoinCandidates(user, datasetCodes, targetLexeme, tagNames, searchFilter);

		model.addAttribute("targetLexeme", targetLexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sourceLexemes", sourceLexemes);

		return LEX_JOIN_PAGE;
	}

	@PostMapping(LEX_JOIN_VALIDATE_URI)
	@ResponseBody
	public Response lexJoinValidate(
			@RequestParam("targetLexemeId") Long targetLexemeId,
			@RequestParam("sourceLexemeIds") List<Long> sourceLexemeIds) {

		Response response = new Response();
		Locale locale = LocaleContextHolder.getLocale();
		List<Long> meaningIds = new ArrayList<>();
		Long targeLexemeMeaningId = lookupService.getMeaningId(targetLexemeId);
		meaningIds.add(targeLexemeMeaningId);
		sourceLexemeIds.forEach(lexemeId -> {
			Long meaningId = lookupService.getMeaningId(lexemeId);
			meaningIds.add(meaningId);
		});
		Map<String, Integer[]> invalidWords = lookupService.getMeaningsWordsWithMultipleHomonymNumbers(meaningIds);

		if (MapUtils.isNotEmpty(invalidWords)) {
			String message = messageSource.getMessage("lexjoin.validation.homonyms.exist", new Object[0], locale);

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

			String messageEnd = messageSource.getMessage("lexjoin.validation.join.homonyms", new Object[0], locale);
			message += " " + messageEnd;
			response.setStatus(ResponseStatus.INVALID);
			response.setMessage(message);
		} else {
			response.setStatus(ResponseStatus.VALID);
		}
		return response;
	}

	@PostMapping(LEX_JOIN_URI)
	public String lexJoin(
			@RequestParam("targetLexemeId") Long targetLexemeId,
			@RequestParam("sourceLexemeIds") List<Long> sourceLexemeIds,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		compositionService.joinLexemes(targetLexemeId, sourceLexemeIds, roleDatasetCode, isManualEventOnUpdateEnabled);
		SimpleWord lexemeSimpleWord = lookupService.getLexemeSimpleWord(targetLexemeId);
		String lexemeWordValue = lexemeSimpleWord.getWordValue();
		Long lexemeWordId = lexemeSimpleWord.getWordId();
		List<String> datasets = getUserPreferredDatasetCodes();
		String searchUri = searchHelper.composeSearchUriAndAppendId(datasets, lexemeWordValue, lexemeWordId);

		return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping(LEX_DUPLICATE_URI + "/{lexemeId}")
	public Response duplicateLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) {

		Locale locale = LocaleContextHolder.getLocale();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		boolean success;
		try {
			success = compositionService.cloneLexemeMeaningAndLexemes(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
		} catch (Exception e) {
			logger.warn("Cloning lexeme meaning failed", e);
			success = false;
		}

		Response response = new Response();
		if (success) {
			String message = messageSource.getMessage("lex.duplicate.lexeme.success", new Object[0], locale);
			response.setStatus(ResponseStatus.OK);
			response.setMessage(message);
		} else {
			String message = messageSource.getMessage("lex.duplicate.lexeme.fail", new Object[0], locale);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(message);
		}
		return response;
	}

	@ResponseBody
	@PostMapping(EMPTY_LEX_DUPLICATE_URI + "/{lexemeId}")
	public Response duplicateEmptyLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		compositionService.cloneEmptyLexemeAndMeaning(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		String message = messageSource.getMessage("lex.duplicate.meaning", new Object[0], locale);

		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);

		return response;
	}

	@ResponseBody
	@PostMapping(MEANING_WORD_AND_LEX_DUPLICATE_URI + "/{lexemeId}")
	public String duplicateMeaningWordAndLexeme(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		compositionService.cloneLexemeAndWord(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER1;
	}

	// TODO under construction
	@ResponseBody
	@PostMapping(WORD_DUPLICATE_URI + "/{lexemeId}")
	public Response duplicateWordAndMoveLexeme(@PathVariable("lexemeId") Long lexemeId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		IdPair wordIds = compositionService.cloneWordAndMoveLexeme(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);

		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage("lex.duplicate.word", new Object[0], locale);

		Response response = new Response();
		response.setStatus(ResponseStatus.OK);
		response.setMessage(message);
		response.setId(wordIds.getId1());
		response.setId2(wordIds.getId2());

		return response;
	}

	@GetMapping(WORD_JOIN_URI)
	public String showWordJoin(@RequestParam("wordId") Long wordId, Model model) {

		EkiUser user = userContext.getUser();
		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		List<String> userPreferredDatasetCodes = userContextData.getPreferredDatasetCodes();

		List<String> userVisibleDatasetCodes = permissionService.getUserVisibleDatasetCodes(userId);
		WordDetails targetWordDetails = lookupService.getWordJoinDetails(user, wordId);
		Word targetWord = targetWordDetails.getWord();

		List<WordDetails> sourceWordDetailsList = lookupService.getWordDetailsOfJoinCandidates(user, targetWord, userPreferredDatasetCodes, userVisibleDatasetCodes);

		model.addAttribute("targetWordDetails", targetWordDetails);
		model.addAttribute("sourceWordDetailsList", sourceWordDetailsList);
		return WORD_JOIN_PAGE;
	}

	@PostMapping(WORD_JOIN_URI)
	public String joinWords(
			@RequestParam("targetWordId") Long targetWordId,
			@RequestParam("sourceWordIds") List<Long> sourceWordIds,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		Long joinedWordId = compositionService.joinWords(targetWordId, sourceWordIds, roleDatasetCode, isManualEventOnUpdateEnabled);

		return REDIRECT_PREF + WORD_BACK_URI + "/" + joinedWordId;
	}

	@PostMapping(LEX_CREATE_WORD_URI)
	public String createWord(
			WordLexemeMeaningDetails wordDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			String language = wordDetails.getLanguage();
			String dataset = wordDetails.getDataset();
			List<String> allDatasets = commonDataService.getVisibleDatasetCodes();
			Long wordId;

			sessionBean.setRecentLanguage(language);

			int wordCount = lexSearchService.countWords(wordValue, allDatasets);
			if (wordCount == 0) {
				boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
				WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(wordDetails, roleDatasetCode, isManualEventOnUpdateEnabled);
				wordId = wordLexemeMeaningId.getWordId();
			} else {
				attributes.addFlashAttribute("wordDetails", wordDetails);
				return REDIRECT_PREF + WORD_SELECT_URI;
			}

			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(dataset)) {
				Long userId = userContext.getUserId();
				selectedDatasets.add(dataset);
				userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
			}
			searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		}
		addRedirectSuccessMessage(attributes, "lex.create.word.success");

		return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
	}

	@PostMapping(CREATE_HOMONYM_URI)
	public String createWord(
			WordLexemeMeaningDetails wordDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		String roleDatasetCode = getRoleDatasetCode();
		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			String dataset = wordDetails.getDataset();
			boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(wordDetails, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long wordId = wordLexemeMeaningId.getWordId();
			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(dataset)) {
				Long userId = userContext.getUserId();
				selectedDatasets.add(dataset);
				userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
			}
			searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		}
		return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
	}

	@GetMapping(WORD_SELECT_URI)
	public String meaningWordCandidates(
			@ModelAttribute("wordDetails") WordLexemeMeaningDetails wordDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		Long meaningId = wordDetails.getMeaningId();
		String wordValue = wordDetails.getWordValue();
		String language = wordDetails.getLanguage();

		UserContextData userContextData = getUserContextData();
		EkiUser user = userContextData.getUser();
		List<String> tagNames = userContextData.getTagNames();

		MeaningWordCandidates meaningWordCandidates = lookupService.getMeaningWordCandidates(user, wordValue, language, meaningId, tagNames);
		model.addAttribute("meaningWordCandidates", meaningWordCandidates);

		return WORD_SELECT_PAGE;
	}

	@GetMapping(WORD_SELECT_URI + "/{dataset}/{wordId}/{meaningId}")
	public String selectWord(
			@PathVariable("dataset") String dataset,
			@PathVariable("wordId") Long wordId,
			@PathVariable("meaningId") String meaningIdCode,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();
		String roleDatasetCode = getRoleDatasetCode();
		Long meaningId = NumberUtils.isDigits(meaningIdCode) ? NumberUtils.toLong(meaningIdCode) : null;
		cudService.createLexeme(wordId, dataset, meaningId, roleDatasetCode, isManualEventOnUpdateEnabled);
		Word word = lexSearchService.getWord(wordId);
		String wordValue = word.getWordValue();
		List<String> selectedDatasets = getUserPreferredDatasetCodes();
		if (!selectedDatasets.contains(dataset)) {
			Long userId = userContext.getUserId();
			selectedDatasets.add(dataset);
			userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
		}
		String searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		return REDIRECT_PREF + LEX_SEARCH_URI + searchUri;
	}

	@PostMapping(UPDATE_WORD_ACTIVE_TAG_COMPLETE_URI + "/{wordId}")
	@ResponseBody
	public String updateWordLexemesActiveTagComplete(@PathVariable Long wordId, @ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		UserContextData userContextData = getUserContextData();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		Tag activeTag = userContextData.getActiveTag();
		String activeTagName = activeTag.getName();
		boolean isManualEventOnUpdateEnabled = sessionBean.isManualEventOnUpdateEnabled();

		logger.debug("Updating word (id: {}} lexemes active tag \"{}\" complete", wordId, activeTagName);
		cudService.updateWordLexemesTagComplete(wordId, userRoleDatasetCode, activeTag, isManualEventOnUpdateEnabled);

		return RESPONSE_OK_VER2;
	}

}
