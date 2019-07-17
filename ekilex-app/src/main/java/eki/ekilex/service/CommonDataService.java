package eki.ekilex.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationPosGroup;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.Image;
import eki.ekilex.data.ImageSourceTuple;
import eki.ekilex.data.OrderedClassifier;
import eki.ekilex.data.Origin;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordEtym;
import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.WordGroup;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.util.ConversionUtil;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class CommonDataService extends AbstractWordSearchService {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	protected ConversionUtil conversionUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Transactional
	public List<Dataset> getDatasets() {
		return commonDataDbService.getDatasets();
	}

	@Transactional
	public List<String> getDatasetCodes() {
		List<String> datasetCodes = getDatasets().stream().map(Dataset::getCode).collect(Collectors.toList());
		return datasetCodes;
	}

	@Transactional
	public List<Classifier> getLanguages() {
		return commonDataDbService.getLanguages(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public Map<String, List<Classifier>> getDomainsInUseByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomainsInUse();
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public Map<String, List<Classifier>> getDatasetDomainsByOrigin(String datasetCode) {

		List<Classifier> domains = commonDataDbService.getDatasetClassifiers(
				ClassifierName.DOMAIN, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		domains = conversionUtil.removeClassifierDuplicates(domains);
		Map<String, List<Classifier>> datasetDomainsByOrigin = domains.stream().collect(groupingBy(Classifier::getOrigin));
		return datasetDomainsByOrigin;
	}

	@Transactional
	public Map<String, List<Classifier>> getAllDomainsByOrigin() {
		List<Classifier> domains = commonDataDbService.getDomains();
		return domains.stream().collect(groupingBy(Classifier::getOrigin));
	}

	@Transactional
	public List<Classifier> getFrequencyGroups() {
		return commonDataDbService.getFrequencyGroups();
	}

	@Transactional
	public List<Classifier> getMorphs() {
		return commonDataDbService.getMorphs(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getGenders() {
		return commonDataDbService.getGenders(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getWordTypes() {
		return commonDataDbService.getWordTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getAspects() {
		return commonDataDbService.getAspects(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getWordRelationTypes() {
		return commonDataDbService.getWordRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
	}

	@Transactional
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataDbService.getLexemeRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_FULL);
	}

	@Transactional
	public List<Classifier> getMeaningRelationTypes() {
		return commonDataDbService.getMeaningRelationTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getDefinitionTypes() {
		return commonDataDbService.getDefinitionTypes(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getPoses() {
		return commonDataDbService.getPoses(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getRegisters() {
		return commonDataDbService.getRegisters(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getRegions() {
		return commonDataDbService.getRegions();
	}

	@Transactional
	public List<Classifier> getDerivs() {
		return commonDataDbService.getDerivs(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getValueStates() {
		return commonDataDbService.getValueStates(CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getProcessStates() {
		return commonDataDbService.getProcessStates();
	}

	@Transactional
	public List<Classifier> getProcessStatesByDataset(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.PROCESS_STATE, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Classifier> getClassifiers(ClassifierName classifierName) {
		if (classifierName == null) {
			return null;
		}
		if (ClassifierName.LANGUAGE.equals(classifierName)) {
			return getLanguages();
		}
		if (ClassifierName.FREQUENCY_GROUP.equals(classifierName)) {
			return getFrequencyGroups();
		}
		if (ClassifierName.MORPH.equals(classifierName)) {
			return getMorphs();
		}
		if (ClassifierName.GENDER.equals(classifierName)) {
			return getGenders();
		}
		if (ClassifierName.WORD_TYPE.equals(classifierName)) {
			return getWordTypes();
		}
		if (ClassifierName.ASPECT.equals(classifierName)) {
			return getAspects();
		}
		if (ClassifierName.WORD_REL_TYPE.equals(classifierName)) {
			return getWordRelationTypes();
		}
		if (ClassifierName.LEX_REL_TYPE.equals(classifierName)) {
			return getLexemeRelationTypes();
		}
		if (ClassifierName.MEANING_REL_TYPE.equals(classifierName)) {
			return getMeaningRelationTypes();
		}
		if (ClassifierName.DEFINITION_TYPE.equals(classifierName)) {
			return getDefinitionTypes();
		}
		if (ClassifierName.POS.equals(classifierName)) {
			return getPoses();
		}
		if (ClassifierName.REGISTER.equals(classifierName)) {
			return getRegisters();
		}
		if (ClassifierName.REGION.equals(classifierName)) {
			return getRegions();
		}
		if (ClassifierName.DERIV.equals(classifierName)) {
			return getDerivs();
		}
		if (ClassifierName.VALUE_STATE.equals(classifierName)) {
			return getValueStates();
		}
		if (ClassifierName.PROCESS_STATE.equals(classifierName)) {
			return getProcessStates();
		}
		return null;
	}

	@Transactional
	public Word getWord(Long wordId) {
		return commonDataDbService.getWord(wordId);
	}

	@Transactional
	public List<Classifier> getDatasetLanguages(String datasetCode) {
		return commonDataDbService.getDatasetClassifiers(ClassifierName.LANGUAGE, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
	}

	@Transactional
	public List<Origin> getAllOrigins() {
		return commonDataDbService.getAllDomainOrigins();
	}

	@Transactional
	public boolean isOnlyLexemeForWord(Long lexemeId) {
		return commonDataDbService.isOnlyLexemeForWord(lexemeId);
	}

	@Transactional
	public boolean isOnlyLexemeForMeaning(Long lexemeId) {
		return commonDataDbService.isOnlyLexemeForMeaning(lexemeId);
	}

	@Transactional
	public boolean isOnlyLexemesForMeaning(Long meaningId, String datasetCode) {
		return commonDataDbService.isOnlyLexemesForMeaning(meaningId, datasetCode);
	}

	@Transactional
	public boolean isOnlyLexemesForWords(Long meaningId, String datasetCode) {
		return commonDataDbService.isOnlyLexemesForWords(meaningId, datasetCode);
	}

	@Transactional
	public List<String> getWordsToBeDeleted(Long meaningId, String datasetCode) {

		List<String> wordsToBeDeleted = new ArrayList<>();
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = commonDataDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			Long wordId = wordLexemeMeaningId.getWordId();
			boolean isOnlyLexemeForWord = commonDataDbService.isOnlyLexemeForWord(lexemeId);
			if (isOnlyLexemeForWord) {
				String wordValue = commonDataDbService.getWordValue(wordId);
				wordsToBeDeleted.add(wordValue);
			}
		}
		return wordsToBeDeleted;
	}

	@Transactional
	public WordLexeme getWordLexeme(Long lexemeId) {

		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId);
		if (lexeme != null) {
			SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(singletonList(lexeme.getDatasetCode()));
			Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
			populateLexeme(lexeme, searchDatasetsRestriction, datasetNameMap);
		}
		return lexeme;
	}

	@Transactional
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Word word = commonDataDbService.getWord(wordId);
		List<Classifier> wordTypes = commonDataDbService.getWordTypes(wordId, classifierLabelLang, classifierLabelTypeDescrip);
		conversionUtil.setWordTypeFlags(word, wordTypes);
		List<WordLexeme> lexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.getParadigmFormTuples(wordId, word.getValue(), classifierLabelLang, classifierLabelTypeDescrip);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples);
		List<Relation> wordRelations = lexSearchDbService.getWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordEtymTuple> wordEtymTuples = lexSearchDbService.getWordEtymology(wordId);
		List<WordEtym> wordEtymology = conversionUtil.composeWordEtymology(wordEtymTuples);
		List<Relation> wordGroupMembers = lexSearchDbService.getWordGroupMembers(wordId, classifierLabelLang, classifierLabelTypeFull);
		List<WordGroup> wordGroups = conversionUtil.composeWordGroups(wordGroupMembers);

		lexemes.forEach(lexeme -> populateLexeme(lexeme, searchDatasetsRestriction, datasetNameMap));
		lexemeLevelCalcUtil.combineLevels(lexemes);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setWordTypes(wordTypes);
		wordDetails.setParadigms(paradigms);
		wordDetails.setLexemes(lexemes);
		wordDetails.setWordRelations(wordRelations);
		wordDetails.setWordEtymology(wordEtymology);
		wordDetails.setWordGroups(wordGroups);

		return wordDetails;
	}

	@Transactional
	public List<WordDetails> getWordDetailsOfJoinCandidates(String wordValue, Long wordIdToExclude) {

		List<WordDetails> wordDetailsList = new ArrayList<>();
		EkiUser user = userService.getAuthenticatedUser();
		Long userId = user.getId();
		List<Dataset> userPermDatasets = permissionDbService.getUserPermDatasets(userId);
		List<String> userPermDatasetCodes = userPermDatasets.stream().map(Dataset::getCode).collect(Collectors.toList());

		List<Long> wordIds = commonDataDbService.getNonaffixoidWordIds(wordValue);
		wordIds.remove(wordIdToExclude);
		wordIds.removeIf(wordId -> !permissionDbService.isGrantedForWord(wordId, userPermDatasetCodes));

		for (Long wordId : wordIds) {
			WordDetails wordDetails = getWordDetails(wordId, userPermDatasetCodes);
			wordDetailsList.add(wordDetails);
		}

		return wordDetailsList;
	}

	@Transactional
	public void joinWords(Long firstWordId, Long secondWordId) {

		String wordValue = commonDataDbService.getWordValue(firstWordId);
		createLifecycleLog(LifecycleEventType.JOIN, LifecycleEntity.WORD, LifecycleProperty.VALUE, firstWordId, wordValue, wordValue);

		commonDataDbService.joinWords(firstWordId, secondWordId);
	}

	private void populateLexeme(WordLexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction, Map<String, String> datasetNameMap) {

		final String[] excludeMeaningAttributeTypes = new String[] {FreeformType.LEARNER_COMMENT.name()};
		final String[] excludeLexemeAttributeTypes = new String[] {FreeformType.GOVERNMENT.name(), FreeformType.GRAMMAR.name(), FreeformType.USAGE.name(), FreeformType.PUBLIC_NOTE.name()};

		String datasetName = datasetNameMap.get(lexeme.getDatasetCode());
		lexeme.setDataset(datasetName);

		Long wordId = lexeme.getWordId();
		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();

		List<String> vocalForms = lexeme.getVocalForms();
		vocalForms = cleanUpVocalForms(vocalForms);

		List<Word> meaningWords = lexSearchDbService.getMeaningWords(wordId, meaningId, searchDatasetsRestriction);
		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeDerivs = commonDataDbService.getLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Classifier> lexemeRegisters = commonDataDbService.getLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<OrderedClassifier> meaningDomains = commonDataDbService.getMeaningDomains(meaningId);
		meaningDomains = conversionUtil.removeOrderedClassifierDuplicates(meaningDomains);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);
		List<FreeForm> meaningFreeforms = commonDataDbService.getMeaningFreeforms(meaningId, excludeMeaningAttributeTypes);
		List<FreeForm> meaningLearnerComments = commonDataDbService.getMeaningLearnerComments(meaningId);
		List<ImageSourceTuple> meaningImageSourceTuples = commonDataDbService.getMeaningImageSourceTuples(meaningId);
		List<Image> meaningImages = conversionUtil.composeMeaningImages(meaningImageSourceTuples);
		List<FreeForm> lexemeFreeforms = commonDataDbService.getLexemeFreeforms(lexemeId, excludeLexemeAttributeTypes);
		List<FreeForm> lexemePublicNotes = commonDataDbService.getLexemePublicNotes(lexemeId);
		List<Government> governments = commonDataDbService.getLexemeGovernments(lexemeId);
		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		List<Relation> lexemeRelations = commonDataDbService.getLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull);
		List<Relation> meaningRelations = commonDataDbService.getMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<List<Relation>> groupedMeaningRelations = conversionUtil.groupRelationsById(meaningRelations);
		List<FreeForm> lexemeGrammars = commonDataDbService.getLexemeGrammars(lexemeId);
		List<CollocationTuple> primaryCollocTuples = lexSearchDbService.getPrimaryCollocationTuples(lexemeId);
		List<CollocationPosGroup> collocationPosGroups = conversionUtil.composeCollocPosGroups(primaryCollocTuples);
		List<CollocationTuple> secondaryCollocTuples = lexSearchDbService.getSecondaryCollocationTuples(lexemeId);
		List<Collocation> secondaryCollocations = conversionUtil.composeCollocations(secondaryCollocTuples);
		List<SourceLink> lexemeSourceLinks = commonDataDbService.getLexemeSourceLinks(lexemeId);

		lexeme.setPos(lexemePos);
		lexeme.setDerivs(lexemeDerivs);
		lexeme.setRegisters(lexemeRegisters);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setMeaningDomains(meaningDomains);
		lexeme.setDefinitions(definitions);
		lexeme.setMeaningFreeforms(meaningFreeforms);
		lexeme.setMeaningLearnerComments(meaningLearnerComments);
		lexeme.setMeaningImages(meaningImages);
		lexeme.setLexemeFreeforms(lexemeFreeforms);
		lexeme.setLexemePublicNotes(lexemePublicNotes);
		lexeme.setGovernments(governments);
		lexeme.setUsages(usages);
		lexeme.setLexemeRelations(lexemeRelations);
		lexeme.setMeaningRelations(meaningRelations);
		lexeme.setGrammars(lexemeGrammars);
		lexeme.setCollocationPosGroups(collocationPosGroups);
		lexeme.setSecondaryCollocations(secondaryCollocations);
		lexeme.setVocalForms(vocalForms);
		lexeme.setSourceLinks(lexemeSourceLinks);
		lexeme.setGroupedMeaningRelations(groupedMeaningRelations);

		boolean lexemeOrMeaningClassifiersExist =
				StringUtils.isNotBlank(lexeme.getLexemeValueStateCode())
						|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
						|| StringUtils.isNotBlank(lexeme.getLexemeProcessStateCode())
						|| CollectionUtils.isNotEmpty(lexemePos)
						|| CollectionUtils.isNotEmpty(lexemeDerivs)
						|| CollectionUtils.isNotEmpty(lexemeRegisters)
						|| CollectionUtils.isNotEmpty(meaningDomains)
						|| CollectionUtils.isNotEmpty(lexemeGrammars)
						|| CollectionUtils.isNotEmpty(lexeme.getLexemeFrequencies());
		lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
	}

	private List<String> cleanUpVocalForms(List<String> vocalForms) {
		return vocalForms.stream().filter(Objects::nonNull).collect(toList());
	}
}
