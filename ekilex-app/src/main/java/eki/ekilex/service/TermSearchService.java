package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.DefSourceAndPublicNoteSourceTuple;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.LexemeWordTuple;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.NoteLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.ProcessDbService;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class TermSearchService extends AbstractSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public TermSearchResult getTermSearchResult(String searchFilter, List<String> selectedDatasetCodes, SearchResultMode resultMode, String resultLang, boolean fetchAll, int offset) {

		TermSearchResult termSearchResult;
		if (StringUtils.isBlank(searchFilter)) {
			termSearchResult = new TermSearchResult();
			termSearchResult.setResults(Collections.emptyList());
			termSearchResult.setMeaningCount(0);
			termSearchResult.setWordCount(0);
			termSearchResult.setResultCount(0);
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, fetchAll, offset);
			conversionUtil.cleanTermMeanings(termSearchResult.getResults());
		}
		int resultCount = termSearchResult.getResultCount();
		boolean resultExist = resultCount > 0;
		boolean showPaging = resultCount > DEFAULT_MAX_RESULTS_LIMIT;
		termSearchResult.setResultExist(resultExist);
		termSearchResult.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, DEFAULT_MAX_RESULTS_LIMIT, resultCount, termSearchResult);
		}

		return termSearchResult;
	}

	@Transactional
	public TermSearchResult getTermSearchResult(
			SearchFilter searchFilter, List<String> selectedDatasetCodes, SearchResultMode resultMode, String resultLang, boolean fetchAll, int offset) throws Exception {

		TermSearchResult termSearchResult;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			termSearchResult = new TermSearchResult();
			termSearchResult.setResults(Collections.emptyList());
			termSearchResult.setMeaningCount(0);
			termSearchResult.setWordCount(0);
			termSearchResult.setResultCount(0);
		} else {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
			termSearchResult = termSearchDbService.getTermSearchResult(searchFilter, searchDatasetsRestriction, resultMode, resultLang, fetchAll, offset);
			conversionUtil.cleanTermMeanings(termSearchResult.getResults());
		}
		int resultCount = termSearchResult.getResultCount();
		boolean resultExist = resultCount > 0;
		boolean showPaging = resultCount > DEFAULT_MAX_RESULTS_LIMIT;
		termSearchResult.setResultExist(resultExist);
		termSearchResult.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, DEFAULT_MAX_RESULTS_LIMIT, resultCount, termSearchResult);
		}

		return termSearchResult;
	}

	@Transactional
	public String getMeaningFirstWordValue(Long meaningId, List<String> datasets) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		return termSearchDbService.getMeaningFirstWord(meaningId, searchDatasetsRestriction);
	}

	@Transactional
	public Meaning getMeaning(Long meaningId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder, EkiUserProfile userProfile,
			EkiUser user) throws Exception {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.PUBLIC_NOTE.name(), FreeformType.SEMANTIC_TYPE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(),
				FreeformType.PUBLIC_NOTE.name(), FreeformType.OD_LEXEME_RECOMMENDATION.name()};

		DatasetPermission userRole = user.getRecentRole();
		Long userId = user.getId();
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();

		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		if (meaning == null) {
			return null;
		}

		permCalculator.applyCrud(meaning, userRole);
		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.applyCrud(definitions, userRole);
		permCalculator.filterVisibility(definitions, userId);
		List<DefSourceAndPublicNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndPublicNoteSourceTuples(meaningId);
		conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
		for (Definition definition : definitions) {
			List<DefinitionNote> definitionPublicNotes = definition.getPublicNotes();
			permCalculator.filterVisibility(definitionPublicNotes, userId);
		}
		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId);
		List<Classifier> semanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		domains = conversionUtil.removeOrderedClassifierDuplicates(domains);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<ImageSourceTuple> imageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Image> images = conversionUtil.composeMeaningImages(imageSourceTuples);
		List<NoteSourceTuple> meaningPublicNoteSourceTuples = commonDataDbService.getMeaningPublicNoteSourceTuples(meaningId);
		List<MeaningNote> meaningPublicNotes = conversionUtil.composeNotes(MeaningNote.class, meaningId, meaningPublicNoteSourceTuples);
		permCalculator.filterVisibility(meaningPublicNotes, userId);
		List<NoteLangGroup> meaningPublicNoteLangGroups = conversionUtil.composeNoteLangGroups(meaningPublicNotes, languagesOrder);
		List<String> meaningWordPreferredOrderDatasetCodes = new ArrayList<>(selectedDatasetCodes);
		List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, meaningWordPreferredOrderDatasetCodes, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<List<Relation>> viewRelations = conversionUtil.composeViewMeaningRelations(meaningRelations, userProfile, null, languagesOrder);
		Integer meaningProcessLogCount = processDbService.getLogCountForMeaning(meaningId);
		Timestamp latestLogEventTime = lifecycleLogDbService.getLatestLogTimeForMeaning(meaningId);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();

		for (Long lexemeId : lexemeIds) {

			LexemeWordTuple lexemeWordTuple = termSearchDbService.getLexemeWordTuple(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			Lexeme lexeme = conversionUtil.composeLexeme(lexemeWordTuple);
			permCalculator.applyCrud(lexeme, userRole);
			Long wordId = lexeme.getWordId();
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			permCalculator.applyCrud(usages, userRole);
			permCalculator.filterVisibility(usages, userId);
			List<NoteSourceTuple> lexemePublicNoteSourceTuples = commonDataDbService.getLexemePublicNoteSourceTuples(lexemeId);
			List<LexemeNote> lexemePublicNotes = conversionUtil.composeNotes(LexemeNote.class, lexemeId, lexemePublicNoteSourceTuples);
			permCalculator.filterVisibility(lexemePublicNotes, userId);
			List<NoteLangGroup> lexemePublicNoteLangGroups = conversionUtil.composeNoteLangGroups(lexemePublicNotes, languagesOrder);
			List<FreeForm> lexemeGrammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
			List<String> lexemeTags = commonDataDbService.getLexemeTags(lexemeId);

			Word word = lexeme.getWord();
			permCalculator.applyCrud(word, userRole);
			boolean classifiersExist = StringUtils.isNotBlank(word.getGenderCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
					|| CollectionUtils.isNotEmpty(wordTypes)
					|| CollectionUtils.isNotEmpty(lexeme.getPos())
					|| CollectionUtils.isNotEmpty(lexeme.getDerivs())
					|| CollectionUtils.isNotEmpty(lexeme.getRegisters())
					|| CollectionUtils.isNotEmpty(lexemeGrammars);

			String dataset = lexeme.getDatasetCode();
			dataset = datasetNameMap.get(dataset);

			lexeme.setDatasetCode(dataset);
			lexeme.setWordTypes(wordTypes);
			lexeme.setFreeforms(lexemeFreeforms);
			lexeme.setPublicNoteLangGroups(lexemePublicNoteLangGroups);
			lexeme.setUsages(usages);
			lexeme.setGrammars(lexemeGrammars);
			lexeme.setClassifiersExist(classifiersExist);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setTags(lexemeTags);
			lexemes.add(lexeme);
		}

		List<LexemeLangGroup> lexemeLangGroups = conversionUtil.composeLexemeLangGroups(lexemes, languagesOrder);

		meaning.setDefinitionLangGroups(definitionLangGroups);
		meaning.setDomains(domains);
		meaning.setSemanticTypes(semanticTypes);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setImages(images);
		meaning.setPublicNoteLangGroups(meaningPublicNoteLangGroups);
		meaning.setLexemeLangGroups(lexemeLangGroups);
		meaning.setRelations(meaningRelations);
		meaning.setViewRelations(viewRelations);
		meaning.setMeaningProcessLogCount(meaningProcessLogCount);
		meaning.setLastChangedOn(latestLogEventTime);

		return meaning;
	}

}
