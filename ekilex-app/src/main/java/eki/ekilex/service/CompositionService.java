package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.LogData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class CompositionService extends AbstractService {

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Transactional
	public Optional<Long> optionalDuplicateMeaning(Long meaningId, String userName) {
		return Optional.of(duplicateMeaningWithLexemes(meaningId, userName));
	}

	@Transactional
	public List<Long> duplicateLexemeAndMeaningWithSameDatasetLexemes(Long lexemeId, String userName) {

		List<Long> duplicateLexemeIds = new ArrayList<>();
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		String datasetCode = lexeme.getDatasetCode();
		Long meaningId = lexeme.getMeaningId();

		Long duplicateMeaningId = duplicateMeaningData(meaningId, userName);

		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId, datasetCode);
		meaningLexemes.forEach(meaningLexeme -> {
			Long duplicateLexemeId = duplicateLexemeData(meaningLexeme.getId(), duplicateMeaningId, userName);
			duplicateLexemeIds.add(duplicateLexemeId);
		});

		return duplicateLexemeIds;
	}

	@Transactional
	public Long duplicateEmptyLexemeAndMeaning(Long lexemeId, String userName) {
		Long duplicateMeaningId = cudDbService.createMeaning();
		Long duplicateLexemeId = compositionDbService.cloneEmptyLexeme(lexemeId, duplicateMeaningId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		String targetLexemeDescription = lifecycleLogDbService.getSimpleLexemeDescription(duplicateLexemeId);

		LogData meaningLogData = new LogData();
		meaningLogData.setUserName(userName);
		meaningLogData.setEventType(LifecycleEventType.CREATE);
		meaningLogData.setEntityName(LifecycleEntity.MEANING);
		meaningLogData.setProperty(LifecycleProperty.VALUE);
		meaningLogData.setEntityId(duplicateMeaningId);
		meaningLogData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(meaningLogData);

		LogData lexemeLogData = new LogData();
		lexemeLogData.setUserName(userName);
		lexemeLogData.setEventType(LifecycleEventType.CREATE);
		lexemeLogData.setEntityName(LifecycleEntity.LEXEME);
		lexemeLogData.setProperty(LifecycleProperty.VALUE);
		lexemeLogData.setEntityId(duplicateLexemeId);
		lexemeLogData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(lexemeLogData);

		return duplicateLexemeId;
	}

	private Long duplicateMeaningWithLexemes(Long meaningId, String userName) {

		Long duplicateMeaningId = duplicateMeaningData(meaningId, userName);
		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId);
		meaningLexemes.forEach(meaningLexeme -> duplicateLexemeData(meaningLexeme.getId(), duplicateMeaningId, userName));
		return duplicateMeaningId;
	}

	private Long duplicateLexemeData(Long lexemeId, Long meaningId, String userName) {

		Long duplicateLexemeId = compositionDbService.cloneLexeme(lexemeId, meaningId);
		updateLexemeLevelsAfterDuplication(duplicateLexemeId);
		compositionDbService.cloneLexemeDerivs(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeFreeforms(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemePoses(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRegisters(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeSoureLinks(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRelations(lexemeId, duplicateLexemeId);
		String sourceLexemeDescription = lifecycleLogDbService.getSimpleLexemeDescription(lexemeId);
		String targetLexemeDescription = lifecycleLogDbService.getExtendedLexemeDescription(duplicateLexemeId);

		LogData logData = new LogData();
		logData.setUserName(userName);
		logData.setEventType(LifecycleEventType.CLONE);
		logData.setEntityName(LifecycleEntity.LEXEME);
		logData.setProperty(LifecycleProperty.VALUE);
		logData.setEntityId(duplicateLexemeId);
		logData.setRecent(sourceLexemeDescription);
		logData.setEntry(targetLexemeDescription);
		lifecycleLogDbService.createLog(logData);

		return duplicateLexemeId;
	}

	private Long duplicateMeaningData(Long meaningId, String userName) {

		Long duplicateMeaningId = compositionDbService.cloneMeaning(meaningId);
		compositionDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId);
		duplicateMeaningDefinitions(meaningId, duplicateMeaningId);
		String targetMeaningDescription = lifecycleLogDbService.getCombinedMeaningDefinitions(duplicateMeaningId);

		LogData logData = new LogData();
		logData.setUserName(userName);
		logData.setEventType(LifecycleEventType.CLONE);
		logData.setEntityName(LifecycleEntity.MEANING);
		logData.setProperty(LifecycleProperty.VALUE);
		logData.setEntityId(duplicateMeaningId);
		logData.setEntry(targetMeaningDescription);
		lifecycleLogDbService.createLog(logData);
		return duplicateMeaningId;
	}

	private void duplicateMeaningDefinitions(Long meaningId, Long duplicateMeaningId) {

		List<DefinitionRecord> meaningDefinitions = compositionDbService.getMeaningDefinitions(meaningId);
		meaningDefinitions.forEach(meaningDefinition -> {
			Long duplicateDefinintionId = compositionDbService.cloneMeaningDefinition(meaningDefinition.getId(), duplicateMeaningId);
			compositionDbService.cloneDefinitionFreeforms(meaningDefinition.getId(), duplicateDefinintionId);
			compositionDbService.cloneDefinitionDatasets(meaningDefinition.getId(), duplicateDefinintionId);
			compositionDbService.cloneDefinitionSourceLinks(meaningDefinition.getId(), duplicateDefinintionId);
		});
	}

	@Transactional
	public void joinMeanings(Long targetMeaningId, List<Long> sourceMeaningIds) {
		for (Long sourceMeaningId : sourceMeaningIds) {
			joinMeanings(targetMeaningId, sourceMeaningId);
		}
	}

	private void joinMeanings(Long targetMeaningId, Long sourceMeaningId) {

		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		String logEntryTarget  = compositionDbService.getFirstDefinitionOfMeaning(targetMeaningId);
		LogData logData = new LogData(
				LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, targetMeaningId, logEntrySource, logEntryTarget);
		createLifecycleLog(logData);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
	}

	//TODO lifecycle log
	@Transactional
	public void separateLexemeMeanings(Long lexemeId) {
		compositionDbService.separateLexemeMeanings(lexemeId);
	}

	@Transactional
	public void joinLexemes(Long targetLexemeId, List<Long> sourceLexemeIds) {
		for (Long sourceLexemeId: sourceLexemeIds) {
			joinLexemes(targetLexemeId, sourceLexemeId);
		}
	}

	private void joinLexemes(Long targetLexemeId, Long sourceLexemeId) {

		LexemeRecord targetLexeme = compositionDbService.getLexeme(targetLexemeId);
		LexemeRecord sourceLexeme = compositionDbService.getLexeme(sourceLexemeId);
		if (sourceLexeme == null) {
			return;
		}
		Long targetMeaningId = targetLexeme.getMeaningId();
		Long sourceMeaningId = sourceLexeme.getMeaningId();

		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		String logEntryTarget = compositionDbService.getFirstDefinitionOfMeaning(targetMeaningId);
		LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, targetMeaningId, logEntrySource,
				logEntryTarget);
		createLifecycleLog(logData);

		joinMeaningsCommonWordsLexemes(targetMeaningId, sourceMeaningId);
		compositionDbService.joinMeanings(targetMeaningId, sourceMeaningId);
	}

	@Transactional
	public Long joinWords(Long targetWordId, List<Long> sourceWordIds) {
		for (Long sourceWordId : sourceWordIds) {
			targetWordId = joinWords(targetWordId, sourceWordId);
		}
		return targetWordId;
	}

	private Long joinWords(Long firstWordId, Long secondWordId) {

		String wordValue = lookupDbService.getWordValue(firstWordId);

		Integer firstWordHomonymNum = compositionDbService.getWordHomonymNum(firstWordId);
		Integer secondWordHomonymNum = compositionDbService.getWordHomonymNum(secondWordId);
		Long wordId = firstWordHomonymNum <= secondWordHomonymNum ? firstWordId : secondWordId;
		Long sourceWordId = secondWordHomonymNum >= firstWordHomonymNum? secondWordId : firstWordId;

		LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, wordValue, wordValue);
		createLifecycleLog(logData);

		compositionDbService.joinWordData(wordId, sourceWordId);
		joinLexemeData(wordId, sourceWordId);
		joinParadigms(wordId, sourceWordId);
		cudDbService.deleteWord(sourceWordId);

		return wordId;
	}

	private void joinLexemeData(Long wordId, Long sourceWordId) {

		List<LexemeRecord> sourceWordLexemes = compositionDbService.getWordLexemes(sourceWordId);
		for (LexemeRecord sourceWordLexeme : sourceWordLexemes) {
			Long sourceWordLexemeId = sourceWordLexeme.getId();
			Long sourceWordLexemeMeaningId = sourceWordLexeme.getMeaningId();
			String sourceWordLexemeDatasetCode = sourceWordLexeme.getDatasetCode();

			Long wordLexemeId = compositionDbService.getLexemeId(wordId, sourceWordLexemeMeaningId, sourceWordLexemeDatasetCode);
			boolean lexemeExists = wordLexemeId != null;

			if (lexemeExists) {
				compositionDbService.joinLexemes(wordLexemeId, sourceWordLexemeId);
			} else {
				Integer currentMaxLevel = compositionDbService.getWordLexemesMaxFirstLevel(wordId);
				int level1 = currentMaxLevel + 1;
				compositionDbService.updateLexemeWordIdAndLevels(sourceWordLexemeId, wordId, level1, DEFAULT_LEXEME_LEVEL);
			}
		}
	}

	private void joinParadigms(Long wordId, Long sourceWordId) {

		boolean wordHasForms = compositionDbService.wordHasForms(wordId);
		if (wordHasForms) {
			return;
		}
		boolean sourceWordHasForms = compositionDbService.wordHasForms(sourceWordId);
		if (sourceWordHasForms) {
			compositionDbService.joinParadigms(wordId, sourceWordId);
		}
	}

	private void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = cudDbService.getWordPrimaryLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2());
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			createLifecycleLog(logData);
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2());
		}
	}

	private void updateLexemeLevelsAfterDuplication(Long duplicateLexemeId) { // TODO refactor - Yogesh

		LexemeRecord duplicatedLexeme = compositionDbService.getLexeme(duplicateLexemeId);
		Integer level1 = duplicatedLexeme.getLevel1();
		Integer level2 = duplicatedLexeme.getLevel2();
		Long wordId = duplicatedLexeme.getWordId();

		if (level2 > 1) {
			List<LexemeRecord> lexemesWithLargerLevel2 = compositionDbService.getLexemesWithHigherLevel2(wordId, level1, level2);
			int increasedDuplicatedLexemeLevel2 = level2 + 1;
			compositionDbService.updateLexemeLevel2(duplicateLexemeId, increasedDuplicatedLexemeLevel2);
			for (LexemeRecord lexeme : lexemesWithLargerLevel2) {
				Long lexemeId = lexeme.getId();
				int increasedLevel2 = lexeme.getLevel2() + 1;
				compositionDbService.updateLexemeLevel2(lexemeId, increasedLevel2);
			}
		} else {
			List<LexemeRecord> lexemesWithLargerLevel1 = compositionDbService.getLexemesWithHigherLevel1(wordId, level1);
			int increasedDuplicatedLexemeLevel1 = level1 + 1;
			compositionDbService.updateLexemeLevel1(duplicateLexemeId, increasedDuplicatedLexemeLevel1);
			for (LexemeRecord lexeme : lexemesWithLargerLevel1) {
				Long lexemeId = lexeme.getId();
				int increasedLevel1 = lexeme.getLevel1() + 1;
				compositionDbService.updateLexemeLevel1(lexemeId, increasedLevel1);
			}
		}
	}

	private void joinMeaningsCommonWordsLexemes(Long targetMeaningId, Long sourceMeaningId) {
		List<IdPair> meaningsCommonWordsLexemeIdPairs = compositionDbService.getMeaningsCommonWordsLexemeIdPairs(targetMeaningId, sourceMeaningId);
		boolean meaningsShareCommonWord = CollectionUtils.isNotEmpty(meaningsCommonWordsLexemeIdPairs);
		if (meaningsShareCommonWord) {
			for (IdPair lexemeIdPair : meaningsCommonWordsLexemeIdPairs) {
				Long targetLexemeId = lexemeIdPair.getId1();
				Long sourceLexemeId = lexemeIdPair.getId2();
				LexemeRecord targetLexeme = compositionDbService.getLexeme(targetLexemeId);
				LexemeRecord sourceLexeme = compositionDbService.getLexeme(sourceLexemeId);

				updateLexemeLevels(sourceLexemeId, "delete");

				String logEntrySource = StringUtils.joinWith(".", sourceLexeme.getLevel1(), sourceLexeme.getLevel2());
				String logEntryTarget = StringUtils.joinWith(".", targetLexeme.getLevel1(), targetLexeme.getLevel2());
				LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, targetLexemeId, logEntrySource,
						logEntryTarget);
				createLifecycleLog(logData);

				compositionDbService.joinLexemes(targetLexemeId, sourceLexemeId);
			}
		}
	}
}
