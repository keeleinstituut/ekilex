package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.LogData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class CompositionService extends AbstractService {

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Autowired
	private UserService userService;

	@Transactional
	public Optional<Long> optionalDuplicateMeaning(Long meaningId) {
		return Optional.of(duplicateMeaningWithLexemes(meaningId));
	}

	@Transactional
	public Optional<Long> optionalDuplicateLexemeAndMeaning(Long lexemeId) {
		return Optional.of(duplicateLexemeAndMeaning(lexemeId));
	}

	@Transactional
	public Long duplicateEmptyLexemeAndMeaning(Long lexemeId) {
		Long duplicateMeaningId = cudDbService.createMeaning();
		Long duplicateLexemeId = compositionDbService.cloneEmptyLexeme(lexemeId, duplicateMeaningId);
		String userName = userService.getAuthenticatedUser().getName();
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

	private Long duplicateLexemeAndMeaning(Long lexemeId) {
	
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		Long duplicateMeaningId = duplicateMeaningData(lexeme.getMeaningId());
		Long duplicateLexemeId = duplicateLexemeData(lexemeId, duplicateMeaningId);
		return duplicateLexemeId;
	}

	private Long duplicateMeaningWithLexemes(Long meaningId) {

		Long duplicateMeaningId = duplicateMeaningData(meaningId);
		List<LexemeRecord> meaningLexemes = compositionDbService.getMeaningLexemes(meaningId);
		meaningLexemes.forEach(meaningLexeme -> duplicateLexemeData(meaningLexeme.getId(), duplicateMeaningId));
		return duplicateMeaningId;
	}

	private Long duplicateLexemeData(Long lexemeId, Long meaningId) {
		
		Long duplicateLexemeId = compositionDbService.cloneLexeme(lexemeId, meaningId);
		compositionDbService.cloneLexemeDerivs(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeFreeforms(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemePoses(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRegisters(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeSoureLinks(lexemeId, duplicateLexemeId);
		compositionDbService.cloneLexemeRelations(lexemeId, duplicateLexemeId);
		String userName = userService.getAuthenticatedUser().getName();
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

	private Long duplicateMeaningData(Long meaningId) {

		Long duplicateMeaningId = compositionDbService.cloneMeaning(meaningId);
		compositionDbService.cloneMeaningDomains(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningRelations(meaningId, duplicateMeaningId);
		compositionDbService.cloneMeaningFreeforms(meaningId, duplicateMeaningId);
		duplicateMeaningDefinitions(meaningId, duplicateMeaningId);
		String userName = userService.getAuthenticatedUser().getName();
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
	public void joinMeanings(Long meaningId, Long sourceMeaningId) {
		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(meaningId);
		String logEntryTarget = compositionDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		boolean success = compositionDbService.joinMeanings(meaningId, sourceMeaningId);
		if (success) {
			LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId, logEntrySource, logEntryTarget);
			createLifecycleLog(logData);
		}
	}

	//TODO lifecycle log
	@Transactional
	public void separateLexemeMeanings(Long lexemeId) {
		compositionDbService.separateLexemeMeanings(lexemeId);
	}

	@Transactional
	public List<String> validateLexemeJoin(Long lexemeId, Long lexemeId2) {
		List<String> validationMessages = new ArrayList<>();
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		LexemeRecord lexeme2 = compositionDbService.getLexeme(lexemeId2);
		if (lexeme.getDatasetCode().equals(lexeme2.getDatasetCode()) && lexeme.getWordId().equals(lexeme2.getWordId())) {
			if (!Objects.equals(lexeme.getFrequencyGroupCode(), lexeme2.getFrequencyGroupCode())) {
				validationMessages.add("Ilmikute sagedusrühmad on erinevad.");
			}
		}
		return validationMessages;
	}

	@Transactional
	public void joinLexemes(Long lexemeId, Long lexemeId2) {
		LexemeRecord lexeme = compositionDbService.getLexeme(lexemeId);
		LexemeRecord lexeme2 = compositionDbService.getLexeme(lexemeId2);
		if (lexeme.getDatasetCode().equals(lexeme2.getDatasetCode()) && lexeme.getWordId().equals(lexeme2.getWordId())) {
			updateLexemeLevels(lexemeId2, "delete");
			String logEntrySource = StringUtils.joinWith(".", lexeme2.getLevel1(), lexeme2.getLevel2(), lexeme2.getLevel3());
			String logEntryTarget = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			LogData logData = new LogData(LifecycleEventType.JOIN, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId, logEntrySource, logEntryTarget);
			createLifecycleLog(logData);
		}
		String logEntrySource = compositionDbService.getFirstDefinitionOfMeaning(lexeme2.getMeaningId());
		String logEntryTarget = compositionDbService.getFirstDefinitionOfMeaning(lexeme.getMeaningId());
		LogData logData = new LogData(
				LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, lexeme.getMeaningId(), logEntrySource, logEntryTarget);
		createLifecycleLog(logData);
		compositionDbService.joinLexemeMeanings(lexemeId, lexemeId2);
	}

	private void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = cudDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			createLifecycleLog(logData);
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
		}
	}

}
