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
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionLangGroup;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeLangGroup;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.Note;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.ProcessDbService;
import eki.ekilex.service.db.TermSearchDbService;

@Component
public class TermSearchService extends AbstractSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private ProcessDbService processDbService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

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
		boolean showPaging = resultCount > MAX_RESULTS_LIMIT;
		termSearchResult.setResultExist(resultExist);
		termSearchResult.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, resultCount, termSearchResult);
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
		boolean showPaging = resultCount > MAX_RESULTS_LIMIT;
		termSearchResult.setResultExist(resultExist);
		termSearchResult.setShowPaging(showPaging);
		if (showPaging) {
			setPagingData(offset, resultCount, termSearchResult);
		}

		return termSearchResult;
	}

	@Transactional
	public String getMeaningFirstWordValue(Long meaningId, List<String> datasets) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasets);
		return termSearchDbService.getMeaningFirstWord(meaningId, searchDatasetsRestriction);
	}

	@Transactional
	public Meaning getMeaning(Long meaningId, List<String> selectedDatasetCodes, List<ClassifierSelect> languagesOrder) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name(), FreeformType.PUBLIC_NOTE.name(), FreeformType.SEMANTIC_TYPE.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(), FreeformType.PUBLIC_NOTE.name()};

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();

		Meaning meaning = termSearchDbService.getMeaning(meaningId, searchDatasetsRestriction);
		List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.getMeaningDefinitionRefTuples(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		for (Definition definition : definitions) {
			Long definitionId = definition.getId();
			List<NoteSourceTuple> definitionPublicNoteSourceTuples = commonDataDbService.getDefinitionPublicNoteSourceTuples(definitionId);
			List<Note> definitionPublicNotes = conversionUtil.composeNotes(definitionPublicNoteSourceTuples);
			definition.setPublicNotes(definitionPublicNotes);
		}

		List<DefinitionLangGroup> definitionLangGroups = conversionUtil.composeMeaningDefinitionLangGroups(definitions, languagesOrder);
		List<OrderedClassifier> domains = commonDataDbService.getMeaningDomains(meaningId);
		List<Classifier> semanticTypes = commonDataDbService.getMeaningSemanticTypes(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		domains = conversionUtil.removeOrderedClassifierDuplicates(domains);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<FreeForm> learnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
		List<ImageSourceTuple> imageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Image> images = conversionUtil.composeMeaningImages(imageSourceTuples);
		List<NoteSourceTuple> meaningPublicNoteSourceTuples = commonDataDbService.getMeaningPublicNoteSourceTuples(meaningId);
		List<Note> meaningPublicNotes = conversionUtil.composeNotes(meaningPublicNoteSourceTuples);
		List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<List<Relation>> groupedRelations = conversionUtil.groupRelationsById(meaningRelations);
		Integer meaningProcessLogCount = processDbService.getLogCountForMeaning(meaningId);
		Timestamp latestLogEventTime = lifecycleLogDbService.getLatestLogTimeForMeaning(meaningId);

		List<Long> lexemeIds = meaning.getLexemeIds();
		List<Lexeme> lexemes = new ArrayList<>();

		for (Long lexemeId : lexemeIds) {

			Lexeme lexeme = termSearchDbService.getLexeme(lexemeId);
			List<Classifier> wordTypes = commonDataDbService.getWordTypes(lexeme.getWordId(), CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Classifier> lexemeDerivs = commonDataDbService.getLexemeDerivs(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Classifier> lexemeRegisters = commonDataDbService.getLexemeRegisters(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Classifier> lexemeRegions = commonDataDbService.getLexemeRegions(lexemeId);
			List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			List<NoteSourceTuple> lexemePublicNoteSourceTuples = commonDataDbService.getLexemePublicNoteSourceTuples(lexemeId);
			List<Note> lexemePublicNotes = conversionUtil.composeNotes(lexemePublicNoteSourceTuples);
			List<FreeForm> lexemeGrammars = commonDataDbService.getLexemeGrammars(lexemeId);
			List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);
			List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);

			boolean classifiersExist = StringUtils.isNotBlank(lexeme.getWordGenderCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
					|| CollectionUtils.isNotEmpty(wordTypes)
					|| CollectionUtils.isNotEmpty(lexemePos)
					|| CollectionUtils.isNotEmpty(lexemeDerivs)
					|| CollectionUtils.isNotEmpty(lexemeRegisters)
					|| CollectionUtils.isNotEmpty(lexemeGrammars);

			String dataset = lexeme.getDataset();
			dataset = datasetNameMap.get(dataset);

			lexeme.setDataset(dataset);
			lexeme.setWordTypes(wordTypes);
			lexeme.setPos(lexemePos);
			lexeme.setDerivs(lexemeDerivs);
			lexeme.setRegisters(lexemeRegisters);
			lexeme.setRegions(lexemeRegions);
			lexeme.setFreeforms(lexemeFreeforms);
			lexeme.setPublicNotes(lexemePublicNotes);
			lexeme.setUsages(usages);
			lexeme.setGrammars(lexemeGrammars);
			lexeme.setClassifiersExist(classifiersExist);
			lexeme.setSourceLinks(lexemeSourceLinks);
			lexeme.setLexemeRelations(lexemeRelations);
			lexemes.add(lexeme);
		}

		List<LexemeLangGroup> lexemeLangGroups = conversionUtil.composeLexemeLangGroups(lexemes, languagesOrder);

		meaning.setDefinitionLangGroups(definitionLangGroups);
		meaning.setDomains(domains);
		meaning.setSemanticTypes(semanticTypes);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLearnerComments(learnerComments);
		meaning.setImages(images);
		meaning.setPublicNotes(meaningPublicNotes);
		meaning.setLexemeLangGroups(lexemeLangGroups);
		meaning.setRelations(meaningRelations);
		meaning.setGroupedRelations(groupedRelations);
		meaning.setMeaningProcessLogCount(meaningProcessLogCount);
		meaning.setLastChangedOn(latestLogEventTime);

		return meaning;
	}

	private void setPagingData(int offset, int resultCount, TermSearchResult termSearchResult) {

		int currentPage = offset / MAX_RESULTS_LIMIT + 1;
		int totalPages = (resultCount + MAX_RESULTS_LIMIT - 1) / MAX_RESULTS_LIMIT;
		boolean previousPageExists = currentPage > 1;
		boolean nextPageExists = currentPage < totalPages;

		termSearchResult.setCurrentPage(currentPage);
		termSearchResult.setTotalPages(totalPages);
		termSearchResult.setPreviousPageExists(previousPageExists);
		termSearchResult.setNextPageExists(nextPageExists);
	}

}
