package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.MeaningWordCandidates;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
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

	@RequestMapping(LEX_JOIN_URI + "/{targetLexemeId}")
	public String lexJoin(
			@PathVariable("targetLexemeId") Long targetLexemeId,
			@RequestParam(name = "searchFilter", required = false) String searchFilter,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			Model model) throws Exception {

		List<ClassifierSelect> languagesOrder = sessionBean.getLanguagesOrder();
		UserContextData userContextData = getUserContextData();
		Long userId = userContextData.getUserId();
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();
		List<String> datasetCodes = userContextData.getPreferredDatasetCodes();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		WordLexeme targetLexeme = lexSearchService.getWordLexeme(targetLexemeId, languagesOrder, userProfile, userRole, true);
		Long sourceLexemeMeaningId = targetLexeme.getMeaningId();
		String targetLexemeWord = targetLexeme.getWordValue();
		if (searchFilter == null) {
			searchFilter = targetLexemeWord;
		}

		Integer wordHomonymNumber = null;
		if (StringUtils.equals(searchFilter, targetLexemeWord)) {
			wordHomonymNumber = targetLexeme.getWordHomonymNr();
		}

		List<WordLexeme> sourceLexemes = lookupService
				.getWordLexemesOfJoinCandidates(userRole, datasetCodes, searchFilter, wordHomonymNumber, sourceLexemeMeaningId, tagNames);

		model.addAttribute("targetLexeme", targetLexeme);
		model.addAttribute("searchFilter", searchFilter);
		model.addAttribute("sourceLexemes", sourceLexemes);

		return LEX_JOIN_PAGE;
	}

	@PostMapping(VALIDATE_LEX_JOIN_URI)
	@ResponseBody
	public String validateJoin(@RequestParam("targetLexemeId") Long targetLexemeId, @RequestParam("sourceLexemeIds") List<Long> sourceLexemeIds)
			throws Exception {

		Map<String, String> response = new HashMap<>();

		List<Long> meaningIds = new ArrayList<>();
		Long targeLexemeMeaningId = lookupService.getMeaningId(targetLexemeId);
		meaningIds.add(targeLexemeMeaningId);
		sourceLexemeIds.forEach(lexemeId -> {
			Long meaningId = lookupService.getMeaningId(lexemeId);
			meaningIds.add(meaningId);
		});
		Map<String, Integer[]> invalidWords = lookupService.getMeaningsWordsWithMultipleHomonymNumbers(meaningIds);

		if (MapUtils.isNotEmpty(invalidWords)) {
			String message = "Tähendusi ei saa ühendada, sest ühendatavatel tähendustel on järgnevad samakujulised, aga erineva homonüüminumbriga keelendid:";

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

			message += " Palun ühendage enne tähenduste ühendamist need homonüümid.";
			response.put("status", "invalid");
			response.put("message", message);
		} else {
			response.put("status", "valid");
		}
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@PostMapping(LEX_JOIN_URI)
	public String join(
			@RequestParam("targetLexemeId") Long targetLexemeId,
			@RequestParam("sourceLexemeIds") List<Long> sourceLexemeIds,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		compositionService.joinLexemes(targetLexemeId, sourceLexemeIds);
		SimpleWord lexemeSimpleWord = lookupService.getLexemeSimpleWord(targetLexemeId);
		String lexemeWordValue = lexemeSimpleWord.getWordValue();
		Long lexemeWordId = lexemeSimpleWord.getWordId();
		List<String> datasets = getUserPreferredDatasetCodes();
		String searchUri = searchHelper.composeSearchUriAndAppendId(datasets, lexemeWordValue, lexemeWordId);

		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@ResponseBody
	@PostMapping(LEX_DUPLICATE_URI + "/{lexemeId}")
	public String duplicateLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		List<Long> clonedLexemeIds = new ArrayList<>();
		try {
			clonedLexemeIds = compositionService.duplicateLexemeAndMeaningWithSameDatasetLexemes(lexemeId);
		} catch (Exception ignore) {
			logger.error("", ignore);
		}

		Map<String, String> response = new HashMap<>();
		if (CollectionUtils.isNotEmpty(clonedLexemeIds)) {
			response.put("message", "Lekseemi duplikaat lisatud");
			response.put("status", "ok");
		} else {
			response.put("message", "Duplikaadi lisamine ebaõnnestus");
			response.put("status", "error");
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@ResponseBody
	@PostMapping(EMPTY_LEX_DUPLICATE_URI + "/{lexemeId}")
	public String duplicateEmptyLexemeAndMeaning(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		compositionService.duplicateEmptyLexemeAndMeaning(lexemeId);

		Map<String, String> response = new HashMap<>();
		response.put("message", "Uus tähendus loodud");
		response.put("status", "ok");

		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(response);
	}

	@ResponseBody
	@PostMapping(MEANING_WORD_AND_LEX_DUPLICATE_URI + "/{lexemeId}")
	public String duplicateMeaningWordAndLexeme(@PathVariable("lexemeId") Long lexemeId) throws Exception {

		compositionService.duplicateLexemeAndWord(lexemeId);
		return RESPONSE_OK_VER1;
	}

	@GetMapping(WORD_JOIN_URI)
	public String showWordJoin(@RequestParam("wordId") Long wordId, Model model) {

		UserContextData userContextData = getUserContextData();
		DatasetPermission userRole = userContextData.getUserRole();
		Long userId = userContextData.getUserId();
		List<String> userPreferredDatasetCodes = userContextData.getPreferredDatasetCodes();

		List<String> userVisibleDatasetCodes = permissionService.getUserVisibleDatasetCodes(userId);
		WordDetails targetWordDetails = lookupService.getWordJoinDetails(userRole, wordId);
		Word targetWord = targetWordDetails.getWord();

		List<WordDetails> sourceWordDetailsList = lookupService.getWordDetailsOfJoinCandidates(userRole, targetWord, userPreferredDatasetCodes, userVisibleDatasetCodes);

		model.addAttribute("targetWordDetails", targetWordDetails);
		model.addAttribute("sourceWordDetailsList", sourceWordDetailsList);
		return WORD_JOIN_PAGE;
	}

	@PostMapping(WORD_JOIN_URI)
	public String joinWords(@RequestParam("targetWordId") Long targetWordId, @RequestParam("sourceWordIds") List<Long> sourceWordIds) throws Exception {

		Long joinedWordId = compositionService.joinWords(targetWordId, sourceWordIds);
		return "redirect:" + WORD_BACK_URI + "/" + joinedWordId;
	}

	@PostMapping(LEX_CREATE_WORD_URI)
	public String createWord(
			WordLexemeMeaningDetails wordDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean,
			RedirectAttributes attributes) throws Exception {

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
				WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(wordDetails);
				wordId = wordLexemeMeaningId.getWordId();
			} else {
				attributes.addFlashAttribute("wordDetails", wordDetails);
				return "redirect:" + WORD_SELECT_URI;
			}

			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(dataset)) {
				Long userId = userContext.getUserId();
				selectedDatasets.add(dataset);
				userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
			}
			searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		}
		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@PostMapping(CREATE_HOMONYM_URI)
	public String createWord(
			WordLexemeMeaningDetails wordDetails,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDetails);

		String wordValue = wordDetails.getWordValue();
		String searchUri = "";
		if (StringUtils.isNotBlank(wordValue)) {
			String dataset = wordDetails.getDataset();
			WordLexemeMeaningIdTuple wordLexemeMeaningId = cudService.createWord(wordDetails);
			Long wordId = wordLexemeMeaningId.getWordId();
			List<String> selectedDatasets = getUserPreferredDatasetCodes();
			if (!selectedDatasets.contains(dataset)) {
				Long userId = userContext.getUserId();
				selectedDatasets.add(dataset);
				userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
			}
			searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		}
		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@PostMapping(UPDATE_WORD_DATA_AND_LEXEME_WEIGHT_URI)
	@ResponseBody
	public String updateWordDataAndLexemeWeight(@RequestBody WordLexemeMeaningDetails wordDataAndLexemeWeight) throws Exception {

		valueUtil.trimAndCleanAndRemoveHtml(wordDataAndLexemeWeight);
		cudService.updateWordDataAndLexemeWeight(wordDataAndLexemeWeight);

		return RESPONSE_OK_VER2;
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
		DatasetPermission userRole = userContextData.getUserRole();
		List<String> tagNames = userContextData.getTagNames();

		MeaningWordCandidates meaningWordCandidates = lookupService.getMeaningWordCandidates(userRole, wordValue, language, meaningId, tagNames);
		model.addAttribute("meaningWordCandidates", meaningWordCandidates);

		return WORD_SELECT_PAGE;
	}

	@GetMapping(WORD_SELECT_URI + "/{dataset}/{wordId}/{meaningId}")
	public String selectWord(
			@PathVariable("dataset") String dataset,
			@PathVariable("wordId") Long wordId,
			@PathVariable("meaningId") String meaningIdCode,
			@ModelAttribute(name = SESSION_BEAN) SessionBean sessionBean) throws Exception {

		Long meaningId = NumberUtils.isDigits(meaningIdCode) ? NumberUtils.toLong(meaningIdCode) : null;
		cudService.createLexeme(wordId, dataset, meaningId);
		Word word = lexSearchService.getWord(wordId);
		String wordValue = word.getWordValue();
		List<String> selectedDatasets = getUserPreferredDatasetCodes();
		if (!selectedDatasets.contains(dataset)) {
			Long userId = userContext.getUserId();
			selectedDatasets.add(dataset);
			userProfileService.updateUserPreferredDatasets(selectedDatasets, userId);
		}
		String searchUri = searchHelper.composeSearchUriAndAppendId(selectedDatasets, wordValue, wordId);
		return "redirect:" + LEX_SEARCH_URI + searchUri;
	}

	@PostMapping(UPDATE_WORD_ACTIVE_TAG_COMPLETE_URI + "/{wordId}")
	@ResponseBody
	public String updateWordLexemesActiveTagComplete(@PathVariable Long wordId) throws Exception {

		UserContextData userContextData = getUserContextData();
		String userRoleDatasetCode = userContextData.getUserRoleDatasetCode();
		Tag activeTag = userContextData.getActiveTag();
		String activeTagName = activeTag.getName();

		logger.debug("Updating word (id: {}} lexemes active tag \"{}\" complete", wordId, activeTagName);
		cudService.updateWordLexemesTagComplete(wordId, userRoleDatasetCode, activeTag);

		return RESPONSE_OK_VER2;
	}

}
