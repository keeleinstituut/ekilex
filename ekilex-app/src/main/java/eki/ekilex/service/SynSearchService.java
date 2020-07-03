package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.Complexity;
import eki.common.constant.LexemeType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.RelationStatus;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.LogData;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.MeaningWordLangGroup;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.Tag;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordNote;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SynSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	private static final float DEFAULT_LEXEME_WEIGHT = 1;

	@Value("#{${relation.weight.multipliers}}")
	private Map<String, Float> relationWeightMultiplierMap;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public WordSynDetails getWordSynDetails(
			Long wordId, String datasetCode, List<String> synCandidateLangCodes, List<String> synMeaningWordLangCodes,
			Tag activeTag, Long userId, DatasetPermission userRole) throws Exception {

		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);
		Word word = synSearchDbService.getWordDetails(wordId);
		permCalculator.applyCrud(userRole, word);
		List<NoteSourceTuple> wordNoteSourceTuples = commonDataDbService.getWordNoteSourceTuples(wordId);
		List<WordNote> wordNotes = conversionUtil.composeNotes(WordNote.class, wordId, wordNoteSourceTuples);
		permCalculator.filterVisibility(userRole, wordNotes);
		String headwordLang = word.getLang();

		List<WordSynLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, classifierLabelLang, classifierLabelTypeDescrip);
		synLexemes.forEach(lexeme -> populateLexeme(lexeme, headwordLang, synMeaningWordLangCodes, userId, userRole));
		lexemeLevelPreseUtil.combineLevels(synLexemes);
		boolean isActiveTagComplete = isActiveTagComplete(synLexemes, activeTag);

		List<SynRelation> relations = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(synCandidateLangCodes)) {
			relations = synSearchDbService.getWordSynRelations(wordId, RAW_RELATION_CODE, datasetCode, synCandidateLangCodes);
		}

		WordSynDetails wordDetails = new WordSynDetails();
		word.setNotes(wordNotes);
		wordDetails.setWord(word);
		wordDetails.setLexemes(synLexemes);
		wordDetails.setRelations(relations);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	private void populateLexeme(WordSynLexeme lexeme, String headwordLanguage, List<String> meaningWordLangs, Long userId, DatasetPermission userRole) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();

		permCalculator.applyCrud(userRole, lexeme);
		List<MeaningWordLangGroup> meaningWordLangGroups = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(meaningWordLangs)) {
			List<LexemeType> lexemeTypes = Arrays.asList(LexemeType.PRIMARY, LexemeType.SECONDARY);
			List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, meaningWordLangs, lexemeTypes);
			meaningWordLangGroups = conversionUtil.composeMeaningWordLangGroups(meaningWords, headwordLanguage);
		}

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
		permCalculator.filterVisibility(userRole, definitions);

		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		permCalculator.filterVisibility(userRole, usages);

		List<String> tags = commonDataDbService.getLexemeTags(lexemeId);

		lexeme.setMeaningWordLangGroups(meaningWordLangGroups);
		lexeme.setDefinitions(definitions);
		lexeme.setUsages(usages);
		lexeme.setTags(tags);
	}

	@Transactional
	public void changeRelationStatus(Long relationId, String relationStatus) {

		LogData logData;
		if (RelationStatus.DELETED.name().equals(relationStatus)) {
			moveChangedRelationToLast(relationId);
			logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		} else {
			logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, relationStatus);
		}
		createLifecycleLog(logData);
		synSearchDbService.changeRelationStatus(relationId, relationStatus);
	}

	@Transactional
	public void createSecondarySynLexeme(Long meaningId, Long wordId, String datasetCode, Long existingLexemeId, Long relationId) {

		List<TypeWordRelParam> typeWordRelParams = synSearchDbService.getWordRelationParams(relationId);
		Float lexemeWeight = getCalculatedLexemeWeight(typeWordRelParams);

		boolean simpleComplexityExists = lookupDbService.wordPrimaryLexemesComplexityExists(wordId, Complexity.SIMPLE);
		Complexity complexity = simpleComplexityExists ? Complexity.SIMPLE : Complexity.DETAIL;

		Long lexemeId = synSearchDbService.createLexeme(wordId, meaningId, datasetCode, LexemeType.SECONDARY, lexemeWeight, complexity);
		SimpleWord synWord = lookupDbService.getSimpleWord(wordId);
		String synWordValue = synWord.getWordValue();
		LogData matchLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.MEANING_WORD, existingLexemeId, synWordValue);
		createLifecycleLog(matchLogData);

		LogData relationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, relationId, RelationStatus.PROCESSED.name());
		createLifecycleLog(relationLogData);
		synSearchDbService.changeRelationStatus(relationId, RelationStatus.PROCESSED.name());

		Word word = synSearchDbService.getWordDetails(wordId);
		List<MeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, Collections.singletonList(word.getLang()), Collections.singletonList(LexemeType.PRIMARY));

		for (MeaningWord meaningWord : meaningWords) {
			Long meaningWordRelationId = synSearchDbService.getRelationId(meaningWord.getWordId(), wordId, RAW_RELATION_CODE);

			if (meaningWordRelationId != null) {
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.STATUS, meaningWordRelationId,
						RelationStatus.PROCESSED.name());
				createLifecycleLog(oppositeRelationLogData);
				synSearchDbService.changeRelationStatus(meaningWordRelationId, RelationStatus.PROCESSED.name());
			}
		}
	}

	private void moveChangedRelationToLast(Long relationId) {
		List<SynRelation> existingRelations = synSearchDbService.getExistingFollowingRelationsForWord(relationId, RAW_RELATION_CODE);

		if (existingRelations.size() > 1) {
			SynRelation lastRelation = existingRelations.get(existingRelations.size() - 1);
			List<Long> existingOrderByValues = existingRelations.stream().map(SynRelation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, lastRelation.getOrderBy());
			existingRelations.remove(0);

			existingOrderByValues.remove(existingOrderByValues.size() - 1);

			int relIdx = 0;
			for (SynRelation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}

	private Float getCalculatedLexemeWeight(List<TypeWordRelParam> typeWordRelParams) {

		if (typeWordRelParams.isEmpty()) {
			return DEFAULT_LEXEME_WEIGHT;
		}

		float dividend = 0;
		float divisor = 0;

		for (TypeWordRelParam typeWordRelParam : typeWordRelParams) {
			String relationParamName = typeWordRelParam.getName();
			Float relationParamValue = typeWordRelParam.getValue();
			Float relationParamWeightMultiplier = relationWeightMultiplierMap.get(relationParamName);

			if (relationParamWeightMultiplier == null) {
				throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Unknown relation weight name: " + relationParamName);
			}

			dividend += (relationParamValue * relationParamWeightMultiplier);
			divisor += relationParamWeightMultiplier;
		}

		return dividend / divisor;
	}
}
