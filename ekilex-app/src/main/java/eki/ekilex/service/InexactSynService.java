package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.RelationStatus;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.InexactSynMeaning;
import eki.ekilex.data.InexactSynMeaningRequest;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;

@Component
public class InexactSynService extends AbstractSynSearchService {

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private CompositionDbService compositionDbService;

	@Transactional
	public Word getSynCandidateWord(Long wordRelationId) {
		return synSearchDbService.getSynCandidateWord(wordRelationId);
	}

	@Transactional
	public List<InexactSynMeaning> getInexactSynMeaningCandidates(Long wordRelationId, String targetLang, String targetLangWordValue, String datasetCode) {

		List<InexactSynMeaning> inexactSynMeaningCandidates = new ArrayList<>();
		Word translationSynWord = synSearchDbService.getSynCandidateWord(wordRelationId);
		String translationLang = translationSynWord.getLang();
		String translationSynWordValue = translationSynWord.getWordValue();

		List<Long> meaningIds = lookupDbService.getMeaningIds(translationSynWordValue, translationLang, datasetCode);

		boolean includeTargetLangWord = StringUtils.isNotBlank(targetLangWordValue);
		if (includeTargetLangWord) {
			List<Long> targetLangWordMeaningIds = lookupDbService.getMeaningIds(targetLangWordValue, targetLang, datasetCode);
			targetLangWordMeaningIds.removeAll(meaningIds);
			meaningIds.addAll(targetLangWordMeaningIds);
		}

		for (Long meaningId : meaningIds) {

			Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
			String datasetName = datasetNameMap.get(datasetCode);
			List<WordLexeme> translationLangWords = lookupDbService.getMeaningWords(meaningId, datasetCode, translationLang);
			List<WordLexeme> targetLangWords = lookupDbService.getMeaningWords(meaningId, datasetCode, targetLang);
			List<Definition> definitions = synSearchDbService.getInexactSynMeaningDefinitions(meaningId, translationLang, targetLang);

			InexactSynMeaning inexactSynMeaningCandidate = new InexactSynMeaning();
			inexactSynMeaningCandidate.setMeaningId(meaningId);
			inexactSynMeaningCandidate.setDatasetCode(datasetCode);
			inexactSynMeaningCandidate.setDatasetName(datasetName);
			inexactSynMeaningCandidate.setTranslationLangWords(translationLangWords);
			inexactSynMeaningCandidate.setTargetLangWords(targetLangWords);
			inexactSynMeaningCandidate.setDefinitions(definitions);
			inexactSynMeaningCandidates.add(inexactSynMeaningCandidate);
		}
		return inexactSynMeaningCandidates;
	}

	@Transactional
	public InexactSynMeaning initNewInexactSynMeaning(
			String targetLangWordValue, String targetLang, String translationLangWordValue, String translationLang, DatasetPermission userRole) {

		InexactSynMeaning meaning = new InexactSynMeaning();
		String datasetCode = userRole.getDatasetCode();
		String datasetName = userRole.getDatasetName();
		boolean createTargetLangWord = StringUtils.isNotBlank(targetLangWordValue);

		List<WordDescript> translationLangWordCandidates = getWordCandidates(userRole, translationLangWordValue, translationLang, datasetCode);

		List<Definition> definitions = new ArrayList<>();
		List<String> meaningWordValues = new ArrayList<>();
		List<WordDescript> targetLangWordCandidates = new ArrayList<>();
		if (createTargetLangWord) {
			targetLangWordCandidates = getWordCandidates(userRole, targetLangWordValue, targetLang, datasetCode);

			if (translationLangWordCandidates.isEmpty() && targetLangWordCandidates.isEmpty()) {
				meaningWordValues.add(translationLangWordValue);
				meaningWordValues.add(targetLangWordValue);
				meaning.setComplete(true);
			}
		} else {
			meaning.setInexactSynDefinitionMandatory(true);
		}

		meaning.setMeaningId(null);
		meaning.setDatasetCode(datasetCode);
		meaning.setDatasetName(datasetName);
		meaning.setDefinitions(definitions);
		meaning.setMeaningWordValues(meaningWordValues);
		meaning.setTranslationLangWordValue(translationLangWordValue);
		meaning.setTranslationLangWordCandidates(translationLangWordCandidates);
		meaning.setTargetLangWordValue(targetLangWordValue);
		meaning.setTargetLangWordCandidates(targetLangWordCandidates);

		return meaning;
	}

	@Transactional
	public InexactSynMeaning initExistingInexactSynMeaning(
			Long meaningId, String targetLangWordValue, String targetLang, String translationLangWordValue, String translationLang, DatasetPermission userRole) {

		InexactSynMeaning meaning = new InexactSynMeaning();
		String datasetCode = userRole.getDatasetCode();
		String datasetName = userRole.getDatasetName();
		boolean createTargetLangWord = StringUtils.isNotBlank(targetLangWordValue);

		List<String> meaningWordValues = synSearchDbService.getMeaningWordValues(meaningId, translationLang, targetLang);
		List<Definition> definitions = synSearchDbService.getInexactSynMeaningDefinitions(meaningId, translationLang, targetLang);
		List<WordDescript> targetLangWordCandidates = new ArrayList<>();
		List<WordDescript> translationLangWordCandidates = new ArrayList<>();

		if (meaningWordValues.contains(translationLangWordValue)) {
			if (createTargetLangWord) {
				targetLangWordCandidates = getWordCandidates(userRole, targetLangWordValue, targetLang, datasetCode);
				if (targetLangWordCandidates.isEmpty()) {
					// meaningWordValues.add(targetLangWordValue);
					meaning.setComplete(true);
				}
			} else {
				meaning.setInexactSynDefinitionMandatory(true); // TODO remove, not used in html?
			}
		}

		if (meaningWordValues.contains(targetLangWordValue)) {
			translationLangWordCandidates = getWordCandidates(userRole, translationLangWordValue, translationLang, datasetCode);
			if (translationLangWordCandidates.isEmpty()) {
				// meaningWordValues.add(translationLangWordValue);
				meaning.setComplete(true);
			}
		}

		meaning.setMeaningId(meaningId);
		meaning.setDatasetCode(datasetCode);
		meaning.setDatasetName(datasetName);
		meaning.setDefinitions(definitions);
		meaning.setMeaningWordValues(meaningWordValues);
		meaning.setTranslationLangWordValue(translationLangWordValue);
		meaning.setTranslationLangWordCandidates(translationLangWordCandidates);
		meaning.setTargetLangWordValue(targetLangWordValue);
		meaning.setTargetLangWordCandidates(targetLangWordCandidates);

		return meaning;
	}

	@Transactional
	public InexactSynMeaningRequest initCompletedInexactSynMeaning(InexactSynMeaningRequest inexactSynMeaningRequest) {

		Long targetMeaningId = inexactSynMeaningRequest.getTargetMeaningId();
		Long inexactSynMeaningId = inexactSynMeaningRequest.getInexactSynMeaningId();
		Long wordRelationId = inexactSynMeaningRequest.getWordRelationId();
		String inexactSynDefValue = inexactSynMeaningRequest.getInexactSynDef();
		String targetLangWordValue = inexactSynMeaningRequest.getTargetLangWordValue();
		String targetLang = inexactSynMeaningRequest.getTargetLang();
		Word translationLangWord = getSynCandidateWord(wordRelationId);
		String translationLangWordValue = translationLangWord.getWordValue();
		String translationLang = translationLangWord.getLang();

		boolean isInexactSynDef = StringUtils.isNotBlank(inexactSynDefValue);
		boolean createNewMeaning = inexactSynMeaningId == null;

		List<String> targetMeaningWordValues = synSearchDbService.getMeaningWordValues(targetMeaningId, translationLang, targetLang);
		List<Definition> targetMeaningDefinitions = synSearchDbService.getInexactSynMeaningDefinitions(targetMeaningId, translationLang, targetLang);
		List<String> inexactSynMeaningWordValues = new ArrayList<>();
		List<Definition> inexactSynMeaningDefinitions = new ArrayList<>();

		Definition inexactSynDefinition = new Definition();
		inexactSynDefinition.setValue(inexactSynDefValue);
		inexactSynDefinition.setTypeCode(DEFINITION_TYPE_CODE_INEXACT_SYN);

		if (createNewMeaning) {
			inexactSynMeaningWordValues.add(translationLangWordValue);
			if (isInexactSynDef) {
				inexactSynMeaningDefinitions.add(inexactSynDefinition);
			} else {
				inexactSynMeaningWordValues.add(targetLangWordValue);
			}
		} else {
			inexactSynMeaningWordValues = synSearchDbService.getMeaningWordValues(inexactSynMeaningId, translationLang, targetLang);
			inexactSynMeaningDefinitions = synSearchDbService.getInexactSynMeaningDefinitions(inexactSynMeaningId, translationLang, targetLang);

			if (!inexactSynMeaningWordValues.contains(translationLangWordValue)) {
				inexactSynMeaningWordValues.add(translationLangWordValue);
			}
			if (isInexactSynDef) {
				inexactSynMeaningDefinitions.removeIf(definition -> definition.getTypeCode().equals(DEFINITION_TYPE_CODE_INEXACT_SYN));
				inexactSynMeaningDefinitions.add(inexactSynDefinition);
			} else {
				if (!inexactSynMeaningWordValues.contains(targetLangWordValue)) {
					inexactSynMeaningWordValues.add(targetLangWordValue);
				}
			}
		}

		inexactSynMeaningRequest.setInexactSynMeaningWordValues(inexactSynMeaningWordValues);
		inexactSynMeaningRequest.setInexactSynMeaningDefinitions(inexactSynMeaningDefinitions);
		inexactSynMeaningRequest.setTargetMeaningWordValues(targetMeaningWordValues);
		inexactSynMeaningRequest.setTargetMeaningDefinitions(targetMeaningDefinitions);

		return inexactSynMeaningRequest;
	}

	@Transactional
	public void saveInexactSynMeaningAndRelation(InexactSynMeaningRequest inexactSynMeaningRequest, String datasetCode) throws Exception {

		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;

		Long inexactSynMeaningId = inexactSynMeaningRequest.getInexactSynMeaningId();
		String relationType = inexactSynMeaningRequest.getRelationType();
		Long targetMeaningId = inexactSynMeaningRequest.getTargetMeaningId();
		Long wordRelationId = inexactSynMeaningRequest.getWordRelationId();
		String inexactSynDefValue = inexactSynMeaningRequest.getInexactSynDef();
		String targetLangWordValue = inexactSynMeaningRequest.getTargetLangWordValue();
		String targetLang = inexactSynMeaningRequest.getTargetLang();
		Long translationLangWordId = inexactSynMeaningRequest.getTranslationLangWordId();
		Long targetLangWordId = inexactSynMeaningRequest.getTargetLangWordId();
		Word translationLangWord = getSynCandidateWord(wordRelationId);
		String translationLangWordValue = translationLangWord.getWordValue();
		String translationLang = translationLangWord.getLang();

		boolean isInexactSynDef = StringUtils.isNotBlank(inexactSynDefValue);
		boolean createNewMeaning = inexactSynMeaningId == null;
		boolean createNewTranslationLangWord = translationLangWordId == null;
		boolean createNewTargetLangWord = targetLangWordId == null;

		if (createNewMeaning) {
			inexactSynMeaningId = cudDbService.createMeaning();
		}

		Long inexactSynTransltionLangLexemeId;
		if (createNewTranslationLangWord) {
			inexactSynTransltionLangLexemeId = createInexactSynWordAndLexeme(
					inexactSynMeaningId, translationLangWordValue, translationLang, datasetCode, isManualEventOnUpdateEnabled);
		} else {
			inexactSynTransltionLangLexemeId = createInexactSynLexeme(inexactSynMeaningId, translationLangWordId, datasetCode, isManualEventOnUpdateEnabled);
		}

		if (isInexactSynDef) {
			Definition meaningInexactSynDef = synSearchDbService.getMeaningDefinition(inexactSynMeaningId, DEFINITION_TYPE_CODE_INEXACT_SYN);
			if (meaningInexactSynDef != null) {
				Long meaningInexactSynDefId = meaningInexactSynDef.getId();
				updateInexactSynDefinitionValue(inexactSynMeaningId, meaningInexactSynDefId, inexactSynDefValue, isManualEventOnUpdateEnabled);
			} else {
				createInexactSynDefinition(inexactSynMeaningId, inexactSynDefValue, targetLang, datasetCode, isManualEventOnUpdateEnabled);
			}
		} else {
			if (createNewTargetLangWord) {
				createInexactSynWordAndLexeme(inexactSynMeaningId, targetLangWordValue, targetLang, datasetCode, isManualEventOnUpdateEnabled);
			} else {
				createInexactSynLexeme(inexactSynMeaningId, targetLangWordId, datasetCode, isManualEventOnUpdateEnabled);
			}
		}

		cloneCandidateData(inexactSynMeaningId, wordRelationId, inexactSynTransltionLangLexemeId, datasetCode);

		if (StringUtils.equals(MEANING_REL_TYPE_CODE_NARROW, relationType)) {
			createInexactSynMeaningRelation(targetMeaningId, inexactSynMeaningId, isManualEventOnUpdateEnabled);
		} else if (StringUtils.equals(MEANING_REL_TYPE_CODE_WIDE, relationType)) {
			createInexactSynMeaningRelation(inexactSynMeaningId, targetMeaningId, isManualEventOnUpdateEnabled);
		}

		setRelationStatusProcessed(wordRelationId, isManualEventOnUpdateEnabled);
	}

	private Long createInexactSynWordAndLexeme(
			Long inexactSynMeaningId, String wordValue, String lang, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexemeAndMeaning(wordValue, wordValue, null, wordValue, lang, datasetCode, PUBLICITY_PUBLIC, inexactSynMeaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		activityLogService.createActivityLog("createInexactSynWordAndLexeme", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createInexactSynWordAndLexeme", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		return lexemeId;
	}

	private Long createInexactSynLexeme(
			Long inexactSynMeaningId, Long translationLangWordId, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("createLexeme", translationLangWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		int currentWordLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(translationLangWordId, datasetCode);
		int lexemeLevel1 = currentWordLexemesMaxLevel1 + 1;
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexeme(translationLangWordId, datasetCode, inexactSynMeaningId, lexemeLevel1);
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		return lexemeId;
	}

	private void createInexactSynDefinition(
			Long inexactSynMeaningId, String inexactSynDefValue, String targetLang, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("createInexactSynDefinition", inexactSynMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long definitionId = cudDbService
				.createDefinition(inexactSynMeaningId, inexactSynDefValue, inexactSynDefValue, targetLang, DEFINITION_TYPE_CODE_INEXACT_SYN, Complexity.DETAIL, PUBLICITY_PUBLIC);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	private void updateInexactSynDefinitionValue(
			Long inexactSynMeaningId, Long inexactSynDefId, String inexactSynDefValue, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService
				.prepareActivityLog("updateInexactSynDefinitionValue", inexactSynMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.updateDefinitionValue(inexactSynDefId, inexactSynDefValue, inexactSynDefValue);
		activityLogService.createActivityLog(activityLog, inexactSynDefId, ActivityEntity.DEFINITION);
	}

	private void cloneCandidateData(
			Long inexactSynMeaningId, Long wordRelationId, Long inexactSynTransltionLangLexemeId, String datasetCode) throws Exception {

		Word translationSynSourceWord = synSearchDbService.getSynCandidateWord(wordRelationId);
		Long sourceWordWordId = translationSynSourceWord.getWordId();

		List<LexemeRecord> sourceWordLexemes = compositionDbService.getWordLexemes(sourceWordWordId);
		if (sourceWordLexemes.size() != 1) {
			throw new OperationDeniedException();
		}
		LexemeRecord sourceWordLexeme = sourceWordLexemes.get(0);
		Long sourceLexemeId = sourceWordLexeme.getId();
		Long sourceMeaningId = sourceWordLexeme.getMeaningId();

		synSearchDbService.cloneSynLexemeData(inexactSynTransltionLangLexemeId, sourceLexemeId);
		synSearchDbService.cloneSynMeaningData(inexactSynMeaningId, sourceMeaningId, datasetCode);
	}

	private void createInexactSynMeaningRelation(Long narrowMeaningId, Long wideMeaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Long meaningRelationId;

		boolean relationExists = lookupDbService.meaningRelationExists(narrowMeaningId, wideMeaningId, MEANING_REL_TYPE_CODE_NARROW);
		if (!relationExists) {
			activityLog = activityLogService.prepareActivityLog("createInexactSynMeaningRelation", narrowMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			meaningRelationId = cudDbService.createMeaningRelation(narrowMeaningId, wideMeaningId, MEANING_REL_TYPE_CODE_NARROW, null); // TODO check if correct direction?
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}

		boolean oppositeRelationExists = lookupDbService.meaningRelationExists(wideMeaningId, narrowMeaningId, MEANING_REL_TYPE_CODE_WIDE);
		if (!oppositeRelationExists) {
			activityLog = activityLogService.prepareActivityLog("createInexactSynMeaningRelation", wideMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			meaningRelationId = cudDbService.createMeaningRelation(wideMeaningId, narrowMeaningId, MEANING_REL_TYPE_CODE_WIDE, null); // TODO check if correct direction?
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	private void setRelationStatusProcessed(Long wordRelationId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long relationWordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("setRelationStatusProcessed", relationWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		synSearchDbService.updateRelationStatus(wordRelationId, RelationStatus.PROCESSED.name());
		activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
	}
}
