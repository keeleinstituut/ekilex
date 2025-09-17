package eki.ekilex.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.exception.OperationDeniedException;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.Grammar;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningForum;
import eki.ekilex.data.MeaningImage;
import eki.ekilex.data.MeaningMedia;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordOsRecommendation;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class TermSearchService extends AbstractSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public byte[] serialiseTermSearchResult(
			String searchFilter,
			List<String> selectedDatasetCodes,
			List<ClassifierSelect> languagesOrder,
			String resultLang,
			EkiUserProfile userProfile,
			EkiUser user) throws Exception {

		if (StringUtils.isBlank(searchFilter)) {
			return new byte[0];
		}
		final SearchResultMode resultMode = SearchResultMode.MEANING;
		final int offset = DEFAULT_OFFSET;
		final boolean noLimit = false;
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes, user.getId());
		TermSearchResult termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
		List<TermMeaning> termMeanings = termSearchResult.getResults();
		return collectAndSerialiseMeanings(termMeanings, selectedDatasetCodes, languagesOrder, userProfile, user);
	}

	@Transactional
	public TermSearchResult getTermSearchResult(
			String searchFilter, List<String> selectedDatasetCodes, SearchResultMode resultMode, String resultLang, int offset, boolean noLimit) throws Exception {

		TermSearchResult termSearchResult;
		if (StringUtils.isBlank(searchFilter)) {
			termSearchResult = getEmptyResult();
		} else if (StringUtils.containsOnly(searchFilter, SEARCH_MASK_CHARS)) {
			throw new OperationDeniedException("Please be more specific. Use other means to dump data");
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
			int resultCount = termSearchResult.getResultCount();
			if (CollectionUtils.isEmpty(termSearchResult.getResults()) && resultCount > 0) {
				int lastPageOffset = getLastPageOffset(resultCount);
				termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, lastPageOffset, noLimit);
			}
			conversionUtil.cleanTermMeanings(termSearchResult.getResults());
		}
		completeResultParams(termSearchResult, offset);

		return termSearchResult;
	}

	@Transactional
	public byte[] serialiseTermSearchResult(
			SearchFilter searchFilter,
			List<String> selectedDatasetCodes,
			List<ClassifierSelect> languagesOrder,
			String resultLang,
			EkiUserProfile userProfile,
			EkiUser user) throws Exception {

		if (!isValidSearchFilter(searchFilter)) {
			return new byte[0];
		}
		final SearchResultMode resultMode = SearchResultMode.MEANING;
		final int offset = DEFAULT_OFFSET;
		final boolean noLimit = true;
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes, user.getId());
		TermSearchResult termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
		List<TermMeaning> termMeanings = termSearchResult.getResults();
		return collectAndSerialiseMeanings(termMeanings, selectedDatasetCodes, languagesOrder, userProfile, user);
	}

	@Transactional
	public TermSearchResult getTermSearchResult(
			SearchFilter searchFilter, List<String> selectedDatasetCodes, SearchResultMode resultMode, String resultLang, int offset, boolean noLimit) throws Exception {

		TermSearchResult termSearchResult;
		if (!isValidSearchFilter(searchFilter)) {
			termSearchResult = getEmptyResult();
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
			int resultCount = termSearchResult.getResultCount();
			if (CollectionUtils.isEmpty(termSearchResult.getResults()) && resultCount > 0) {
				int lastPageOffset = getLastPageOffset(resultCount);
				termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, lastPageOffset, noLimit);
			}
			conversionUtil.cleanTermMeanings(termSearchResult.getResults());
		}
		completeResultParams(termSearchResult, offset);

		return termSearchResult;
	}

	private byte[] collectAndSerialiseMeanings(
			List<TermMeaning> termMeanings,
			List<String> selectedDatasetCodes,
			List<ClassifierSelect> languagesOrder,
			EkiUserProfile userProfile,
			EkiUser user) throws Exception {

		List<Meaning> meanings = new ArrayList<>();
		for (TermMeaning termMeaning : termMeanings) {
			Long meaningId = termMeaning.getMeaningId();
			Meaning meaning = getMeaning(meaningId, selectedDatasetCodes, languagesOrder, userProfile, user, null);
			meanings.add(meaning);
		}
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(byteStream);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonGenerator.setCodec(objectMapper);
		jsonGenerator.writeObject(meanings);
		jsonGenerator.close();
		byteStream.flush();
		byte[] bytes = byteStream.toByteArray();
		byteStream.close();
		return bytes;
	}

	private TermSearchResult getEmptyResult() {
		TermSearchResult termSearchResult;
		termSearchResult = new TermSearchResult();
		termSearchResult.setResults(Collections.emptyList());
		termSearchResult.setMeaningCount(0);
		termSearchResult.setWordCount(0);
		termSearchResult.setResultCount(0);
		termSearchResult.setResultExist(false);
		termSearchResult.setShowPaging(false);
		termSearchResult.setResultDownloadNow(false);
		termSearchResult.setResultDownloadLater(false);
		return termSearchResult;
	}

	private void completeResultParams(TermSearchResult termSearchResult, int offset) {

		int resultCount = termSearchResult.getResultCount();
		boolean resultExist = resultCount > 0;
		boolean resultDownloadNow = resultExist && (resultCount < DEFAULT_MAX_DOWNLOAD_LIMIT);
		boolean resultDownloadLater = resultExist && (resultCount >= DEFAULT_MAX_DOWNLOAD_LIMIT);

		termSearchResult.setResultExist(resultExist);
		termSearchResult.setResultDownloadNow(resultDownloadNow);
		termSearchResult.setResultDownloadLater(resultDownloadLater);
		setPagingData(offset, DEFAULT_MAX_RESULTS_LIMIT, resultCount, termSearchResult);
	}

	@Transactional
	public Meaning getMeaning(
			Long meaningId,
			List<String> selectedDatasetCodes,
			List<ClassifierSelect> languagesOrder,
			EkiUserProfile userProfile,
			EkiUser user,
			Tag activeTag) throws Exception {

		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes, userId);

		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		if (meaning == null) {
			return null;
		}

		permCalculator.applyCrud(user, meaning);
		List<Definition> definitions = composeDefinitions(user, meaningId);
		for (Definition definition : definitions) {
			List<DefinitionNote> definitionNotes = definition.getNotes();
			permCalculator.filterVisibility(user, definitionNotes);
		}
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Classifier> semanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST);
		List<Freeform> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, CLASSIF_LABEL_LANG_EST);
		List<MeaningImage> images = commonDataDbService.getMeaningImages(meaningId);
		List<MeaningMedia> medias = commonDataDbService.getMeaningMedias(meaningId);
		List<MeaningForum> meaningForums = commonDataDbService.getMeaningForums(meaningId);
		permCalculator.applyCrud(user, meaningForums);
		List<MeaningNote> meaningNotes = commonDataDbService.getMeaningNotes(meaningId);
		permCalculator.filterVisibility(user, meaningNotes);
		List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, languagesOrder);
		List<String> meaningWordPreferredOrderDatasetCodes = new ArrayList<>(selectedDatasetCodes);
		List<MeaningRelation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, CLASSIF_LABEL_LANG_EST);
		List<List<MeaningRelation>> viewRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, null, languagesOrder);
		List<String> meaningTags = commonDataDbService.getMeaningTags(meaningId);
		String meaningFirstWordValue = termSearchDbService.getMeaningFirstWordValueOrderedByLang(meaningId, searchDatasetsRestriction);
		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();

		for (Long lexemeId : lexemeIds) {

			Lexeme lexeme = composeLexeme(user, lexemeId);
			Long wordId = lexeme.getWordId();
			List<Freeform> wordFreeforms = commonDataDbService.getWordFreeforms(wordId, CLASSIF_LABEL_LANG_EST);
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST);
			List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
			permCalculator.applyCrud(user, wordForums);
			List<WordOsRecommendation> wordOsRecommendations = osDataDbService.getWordOsRecommendations(wordId);
			List<Freeform> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, CLASSIF_LABEL_LANG_EST);
			List<LexemeNote> lexemeNotes = lexeme.getNotes();
			permCalculator.filterVisibility(user, lexemeNotes);
			List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, languagesOrder);
			List<Grammar> grammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<LexemeRelation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST);
			List<Usage> usages = lexeme.getUsages();
			permCalculator.applyCrud(user, usages);
			permCalculator.filterVisibility(user, usages);
			List<CollocMember> collocationMembers = commonDataDbService.getCollocationMembers(lexemeId);

			Word word = lexeme.getLexemeWord();
			word.setWordTypes(wordTypes);
			word.setFreeforms(wordFreeforms);
			word.setForums(wordForums);
			word.setWordOsRecommendations(wordOsRecommendations);

			boolean classifiersExist = StringUtils.isNotBlank(word.getGenderCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
					|| CollectionUtils.isNotEmpty(lexeme.getPos())
					|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
					|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
					|| CollectionUtils.isNotEmpty(grammars);

			lexeme.setFreeforms(lexemeFreeforms);
			lexeme.setNoteLangGroups(lexemeNoteLangGroups);
			lexeme.setGrammars(grammars);
			lexeme.setClassifiersExist(classifiersExist);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setCollocationMembers(collocationMembers);
			lexemes.add(lexeme);
		}

		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(userRole, lexemes, activeTag);
		List<LexemeLangGroup> lexemeLangGroups = conversionUtil.composeLexemeLangGroups(lexemes, languagesOrder);

		meaning.setDefinitionLangGroups(definitionLangGroups);
		meaning.setDomains(domains);
		meaning.setSemanticTypes(semanticTypes);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setImages(images);
		meaning.setMedias(medias);
		meaning.setForums(meaningForums);
		meaning.setNoteLangGroups(meaningNoteLangGroups);
		meaning.setLexemeLangGroups(lexemeLangGroups);
		meaning.setRelations(meaningRelations);
		meaning.setViewRelations(viewRelations);
		meaning.setTags(meaningTags);
		meaning.setActiveTagComplete(isActiveTagComplete);
		meaning.setFirstWordValue(meaningFirstWordValue);

		return meaning;
	}

	@Transactional
	public String getMeaningFirstWordValue(Long meaningId, List<String> datasets) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		return termSearchDbService.getMeaningFirstWordValue(meaningId, searchDatasetsRestriction);
	}

	private Lexeme composeLexeme(EkiUser user, Long lexemeId) {

		Lexeme lexeme = termSearchDbService.getLexeme(lexemeId, CLASSIF_LABEL_LANG_EST);
		Long wordId = lexeme.getWordId();
		Word word = termSearchDbService.getWord(wordId);
		lexeme.setLexemeWord(word);
		permCalculator.applyCrud(user, lexeme);
		permCalculator.applyCrud(user, word);
		return lexeme;
	}

	private List<Definition> composeDefinitions(EkiUser user, Long meaningId) {

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST);
		permCalculator.applyCrud(user, definitions);
		permCalculator.filterVisibility(user, definitions);
		definitions.forEach(definition -> {
			if (StringUtils.equals(definition.getTypeCode(), DEFINITION_TYPE_CODE_INEXACT_SYN)) {
				definition.setEditDisabled(true);
			}
		});
		return definitions;
	}

}
