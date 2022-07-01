package eki.ekilex.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.FreeformType;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeRelation;
import eki.ekilex.data.LexemeWordTuple;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.Media;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordNote;
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
		} else if (StringUtils.equals(searchFilter, QUERY_MULTIPLE_CHARACTERS_SYM)) {
			throw new OperationDeniedException("Please be more specific. Use other means to dump data");
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, offset, noLimit);
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
		boolean showPaging = resultCount > DEFAULT_MAX_RESULTS_LIMIT;
		boolean resultDownloadNow = resultExist && (resultCount < DEFAULT_MAX_DOWNLOAD_LIMIT);
		boolean resultDownloadLater = resultExist && (resultCount >= DEFAULT_MAX_DOWNLOAD_LIMIT);
		termSearchResult.setResultExist(resultExist);
		termSearchResult.setShowPaging(showPaging);
		termSearchResult.setResultDownloadNow(resultDownloadNow);
		termSearchResult.setResultDownloadLater(resultDownloadLater);
		if (showPaging) {
			setPagingData(offset, DEFAULT_MAX_RESULTS_LIMIT, resultCount, termSearchResult);
		}
	}

	@Transactional
	public Meaning getMeaning(
			Long meaningId,
			List<String> selectedDatasetCodes,
			List<ClassifierSelect> languagesOrder,
			EkiUserProfile userProfile,
			EkiUser user,
			Tag activeTag) throws Exception {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.NOTE.name(), FreeformType.SEMANTIC_TYPE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(),
				FreeformType.NOTE.name()};

		DatasetPermission userRole = user.getRecentRole();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes, user.getId());
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();

		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		if (meaning == null) {
			return null;
		}

		permCalculator.applyCrud(userRole, meaning);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.applyCrud(userRole, definitions);
		permCalculator.filterVisibility(userRole, definitions);
		List<DefSourceAndNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndNoteSourceTuples(meaningId);
		conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
		for (Definition definition : definitions) {
			List<DefinitionNote> definitionNotes = definition.getNotes();
			permCalculator.filterVisibility(userRole, definitionNotes);
		}
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Classifier> semanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<ImageSourceTuple> imageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Media> images = conversionUtil.composeMeaningImages(imageSourceTuples);
		List<Media> medias = commonDataDbService.getMeaningMedias(meaningId);
		List<NoteSourceTuple> meaningNoteSourceTuples = commonDataDbService.getMeaningNoteSourceTuples(meaningId);
		List<MeaningNote> meaningNotes = conversionUtil.composeNotes(MeaningNote.class, meaningId, meaningNoteSourceTuples);
		permCalculator.filterVisibility(userRole, meaningNotes);
		List<NoteLangGroup> meaningNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningNotes, languagesOrder);
		List<String> meaningWordPreferredOrderDatasetCodes = new ArrayList<>(selectedDatasetCodes);
		List<MeaningRelation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<List<MeaningRelation>> viewRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, null, languagesOrder);
		List<String> meaningTags = commonDataDbService.getMeaningTags(meaningId);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();

		for (Long lexemeId : lexemeIds) {

			LexemeWordTuple lexemeWordTuple = termSearchDbService.getLexemeWordTuple(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			Lexeme lexeme = conversionUtil.composeLexeme(lexemeWordTuple);
			permCalculator.applyCrud(userRole, lexeme);
			Long wordId = lexeme.getWordId();
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<NoteSourceTuple> wordNoteSourceTuples = commonDataDbService.getWordNoteSourceTuples(wordId);
			List<WordNote> wordNotes = conversionUtil.composeNotes(WordNote.class, wordId, wordNoteSourceTuples);
			permCalculator.filterVisibility(userRole, wordNotes);
			List<FreeForm> odWordRecommendations = commonDataDbService.getOdWordRecommendations(wordId);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			permCalculator.applyCrud(userRole, usages);
			permCalculator.filterVisibility(userRole, usages);
			List<NoteSourceTuple> lexemeNoteSourceTuples = commonDataDbService.getLexemeNoteSourceTuples(lexemeId);
			List<LexemeNote> lexemeNotes = conversionUtil.composeNotes(LexemeNote.class, lexemeId, lexemeNoteSourceTuples);
			permCalculator.filterVisibility(userRole, lexemeNotes);
			List<NoteLangGroup> lexemeNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemeNotes, languagesOrder);
			List<FreeForm> lexemeGrammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			List<LexemeRelation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<String> lexemeTags = commonDataDbService.getLexemeTags(lexemeId);

			Word word = lexeme.getWord();
			word.setNotes(wordNotes);
			word.setOdWordRecommendations(odWordRecommendations);
			permCalculator.applyCrud(userRole, word);

			boolean classifiersExist = StringUtils.isNotBlank(word.getGenderCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
					|| CollectionUtils.isNotEmpty(wordTypes)
					|| CollectionUtils.isNotEmpty(lexeme.getPos())
					|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
					|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
					|| CollectionUtils.isNotEmpty(lexemeGrammars);

			String datasetCode = lexeme.getDatasetCode();
			String datasetName = datasetNameMap.get(datasetCode);

			lexeme.setDatasetName(datasetName);
			lexeme.setWordTypes(wordTypes);
			lexeme.setFreeforms(lexemeFreeforms);
			lexeme.setNoteLangGroups(lexemeNoteLangGroups);
			lexeme.setUsages(usages);
			lexeme.setGrammars(lexemeGrammars);
			lexeme.setClassifiersExist(classifiersExist);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setTags(lexemeTags);
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
		meaning.setNoteLangGroups(meaningNoteLangGroups);
		meaning.setLexemeLangGroups(lexemeLangGroups);
		meaning.setRelations(meaningRelations);
		meaning.setViewRelations(viewRelations);
		meaning.setTags(meaningTags);
		meaning.setActiveTagComplete(isActiveTagComplete);

		return meaning;
	}

	@Transactional
	public String getMeaningFirstWordValue(Long meaningId, List<String> datasets) {
	
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		return termSearchDbService.getMeaningFirstWord(meaningId, searchDatasetsRestriction);
	}

}
