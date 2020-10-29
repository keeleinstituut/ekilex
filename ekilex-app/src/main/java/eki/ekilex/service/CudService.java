package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.RelationStatus;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.ListData;
import eki.ekilex.data.LogData;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
@Component
public class CudService extends AbstractService implements GlobalConstant {

	private static final String RAW_RELATION_TYPE = "raw";

	private static final String USER_ADDED_WORD_RELATION_NAME = "user";

	private static final String UNDEFINED_RELATION_STATUS = RelationStatus.UNDEFINED.name();

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	// --- UPDATE ---

	@Transactional
	public void updateWordValue(Long wordId, String valuePrese) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, valuePrese);
		createLifecycleLog(logData);
		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String valueAsWord = textDecorationService.removeAccents(value, lang);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValue", wordId, LifecycleLogOwner.WORD);
		cudDbService.updateWordValue(wordId, value, valuePrese);
		if (StringUtils.isNotEmpty(valueAsWord)) {
			cudDbService.updateAsWordValue(wordId, valueAsWord);
		}
		SimpleWord updatedWord = new SimpleWord(wordId, value, lang);
		cudDbService.adjustWordHomonymNrs(originalWord);
		cudDbService.adjustWordHomonymNrs(updatedWord);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordVocalForm(Long wordId, String vocalForm) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.VOCAL_FORM, wordId, vocalForm);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordVocalForm", wordId, LifecycleLogOwner.WORD);
		Long formId = cudDbService.updateWordVocalForm(wordId, vocalForm);
		activityLogService.createActivityLog(activityLog, formId, ActivityEntity.FORM);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordType", wordId, LifecycleLogOwner.WORD);
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, currentTypeCode, newTypeCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateWordAspect(Long wordId, String typeCode) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.ASPECT, wordId, typeCode);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordAspect", wordId, LifecycleLogOwner.WORD);
		cudDbService.updateWordAspect(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.GENDER, wordId, genderCode);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordGender", wordId, LifecycleLogOwner.WORD);
		cudDbService.updateWordGender(wordId, genderCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordLang(Long wordId, String langCode) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.LANG, wordId, langCode);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLang", wordId, LifecycleLogOwner.WORD);
		cudDbService.updateWordLang(wordId, langCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordNote(Long wordNoteId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(wordNoteId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.WORD, ActivityEntity.WORD_NOTE, freeform);
	}

	@Transactional
	public void updateWordLexemesTagComplete(Long wordId, String userRoleDatasetCode, Tag tag) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLexemesTagComplete", wordId, LifecycleLogOwner.WORD);

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();
		List<Long> updatedLexemeIds;
		LifecycleEventType eventType;

		if (removeToComplete) {
			updatedLexemeIds = cudDbService.deleteWordLexemesTag(wordId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.DELETE;
		} else {
			updatedLexemeIds = cudDbService.createWordLexemesTag(wordId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.CREATE;
		}

		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.TAG);

		updatedLexemeIds.forEach(lexemeId -> {
			LogData logData = new LogData(eventType, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(logData);
		});
	}

	@Transactional
	public void updateMeaningLexemesTagComplete(Long meaningId, String userRoleDatasetCode, Tag tag) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningLexemesTagComplete", meaningId, LifecycleLogOwner.MEANING);

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();
		List<Long> updatedLexemeIds;
		LifecycleEventType eventType;

		if (removeToComplete) {
			updatedLexemeIds = cudDbService.deleteMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.DELETE;
		} else {
			updatedLexemeIds = cudDbService.createMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.CREATE;
		}

		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.TAG);

		updatedLexemeIds.forEach(lexemeId -> {
			LogData logData = new LogData(eventType, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(logData);
		});
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.WORD_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long wordRelationId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRelationOrdering", wordId, LifecycleLogOwner.WORD);
			cudDbService.updateWordRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.WORD_ETYMOLOGY, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long wordEtymId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordEtymologyOrdering", wordId, LifecycleLogOwner.WORD);
			cudDbService.updateWordEtymologyOrderby(item);
			activityLogService.createActivityLog(activityLog, wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
		}
	}

	@Transactional
	public void updateMeaningDomainOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, item);
			createListOrderingLifecycleLog(logData);
			Long meaningDomainId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningDomainId, ActivityEntity.DOMAIN);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomainOrdering", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.updateMeaningDomainOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void updateGovernmentOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.GOVERNMENT, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long governmentId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateGovernmentOrdering", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
		}
	}

	@Transactional
	public void updateUsageOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.USAGE, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long usageId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageOrdering", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}

	@Transactional
	public void updateLexemeNoteOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			Long lexemeNoteId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteOrdering", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		}
	}

	@Transactional
	public void updateMeaningNoteOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			Long meaningNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteOrdering", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
		}
	}

	@Transactional
	public void updateDefinitionNoteOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteOrdering", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION_NOTE);
		}
	}

	@Transactional
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.MEANING_WORD, lexemeId);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeMeaningWordOrdering", lexemeId, LifecycleLogOwner.LEXEME);
		for (ListData item : items) {
			cudDbService.updateLexemeOrderby(item);
		}
		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.MEANING_WORD);
	}

	@Transactional
	public void updateUsageValue(Long usageId, String valuePrese, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageId);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.USAGE, freeform);
	}

	@Transactional
	public void updateUsageTranslationValue(Long usageTranslationId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageTranslationId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.USAGE_TRANSLATION, freeform);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long usageDefinitionId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageDefinitionId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long lexemeId = item.getId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeOrdering", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateLexemeOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordPrimaryLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2());
			Long otherLexemeId = lexeme.getLexemeId();
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, otherLexemeId, logEntry);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeGovernment(Long lexemeGovernmentId, String value, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGovernmentId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, value);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.GOVERNMENT, freeform);
	}

	@Transactional
	public void updateLexemeGrammar(Long lexemeGrammarId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGrammarId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.GRAMMAR, freeform);
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String freqGroupCode) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.FREQUENCY_GROUP, lexemeId, freqGroupCode);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeFrequencyGroup", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.updateLexemeFrequencyGroup(lexemeId, freqGroupCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeComplexity(Long lexemeId, String complexity) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.COMPLEXITY, lexemeId, complexity);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeComplexity", lexemeId, LifecycleLogOwner.LEXEME);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = commonDataDbService.getWordLexemeMeaningId(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		cudDbService.updateLexemeComplexity(lexemeId, complexity);
		cudDbService.adjustWordSecondaryLexemesComplexity(wordId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePos", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, currentPos, newPos);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeDeriv", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		activityLogService.createActivityLog(activityLog, lexemeDerivid, ActivityEntity.DERIV);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivid, currentDeriv, newDeriv);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegister", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, currentRegister, newRegister);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegion", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, currentRegion, newRegion);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeNote(Long lexemeNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.LEXEME_NOTE, freeform);
	}

	@Transactional
	public void updateLexemePublicity(Long lexemeId, boolean isPublic) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.PUBLICITY, lexemeId, String.valueOf(isPublic));
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePublicity", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.updateLexemeProcessState(lexemeId, isPublic);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE_STATE, lexemeId, valueStateCode);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeValueState", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long lexemeRelationId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeRelationId, ActivityEntity.LEXEME_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRelationOrdering", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.updateLexemeRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void updateDefinition(Long definitionId, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.updateDefinition(definitionId, value, valuePrese, lang, complexity, typeCode, isPublic);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.DEFINITION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionOrdering", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.updateDefinitionOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	@Transactional
	public void updateDefinitionNote(Long definitionNoteId, String valuePrese, String lang, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(definitionNoteId);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.MEANING, ActivityEntity.DEFINITION_NOTE, freeform);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			Long meaningRelationId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationOrdering", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.updateMeaningRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomain", meaningId, LifecycleLogOwner.MEANING);
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, currentDomain.getCode(), newDomain.getCode());
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateMeaningLearnerComment(Long learnerCommentId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(learnerCommentId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.MEANING, ActivityEntity.LEARNER_COMMENT, freeform);
	}

	@Transactional
	public void updateMeaningNote(Long meaningNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(meaningNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.MEANING, ActivityEntity.MEANING_NOTE, freeform);
	}

	@Transactional
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningSemanticType", meaningId, LifecycleLogOwner.MEANING);
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, currentSemanticType, newSemanticType);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateImageTitle(Long imageId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateChildFreeform(LifecycleLogOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform);
	}

	@Transactional
	public void updateOdWordRecommendation(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.WORD, ActivityEntity.OD_WORD_RECOMMENDATION, freeform);
	}

	@Transactional
	public void updateOdLexemeRecommendation(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.OD_LEXEME_RECOMMENDATION, freeform);
	}

	@Transactional
	public void updateOdUsageDefinition(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.OD_USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void updateOdUsageAlternative(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(LifecycleLogOwner.LEXEME, ActivityEntity.OD_USAGE_ALTERNATIVE, freeform);
	}

	@Transactional
	public void updateLexemeWeight(Long lexemeId, String lexemeWeightStr) throws Exception {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.WEIGHT, lexemeId, lexemeWeightStr);
		createLifecycleLog(logData);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeWeight", lexemeId, LifecycleLogOwner.LEXEME);
		BigDecimal lexemeWeight = new BigDecimal(lexemeWeightStr);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateWordDataAndLexemeWeight(WordLexemeMeaningDetails wordDataAndLexemeWeight) throws Exception {

		Long wordId = wordDataAndLexemeWeight.getWordId();
		Long lexemeId = wordDataAndLexemeWeight.getLexemeId();
		String lexemeWeight = wordDataAndLexemeWeight.getLexemeWeight();
		String wordValuePrese = wordDataAndLexemeWeight.getWordValuePrese();

		updateWordValue(wordId, wordValuePrese);
		updateLexemeWeight(lexemeId, lexemeWeight);
	}

	private void updateFreeform(LifecycleLogOwner logOwner, ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long freeformId = freeform.getId();
		Long ownerId = activityLogService.getOwnerId(freeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.LEXEME_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.MEANING_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.WORD_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.NOTE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_WORD_RECOMMENDATION)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_LEXEME_RECOMMENDATION)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.LEARNER_COMMENT)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.GRAMMAR)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.GOVERNMENT)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.USAGE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.USAGE_TRANSLATION)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.USAGE_DEFINITION)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_USAGE_DEFINITION)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_USAGE_ALTERNATIVE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, freeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.DEFINITION_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, freeformId, valuePrese);
			createLifecycleLog(logData);
		}

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, logOwner);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, activityEntity);
	}

	private void updateChildFreeform(LifecycleLogOwner logOwner, ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long parentId = freeform.getParentId();
		Long ownerId = activityLogService.getOwnerId(parentId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		if (activityEntity.equals(ActivityEntity.IMAGE_FILE)) {
			String recent = lookupDbService.getImageTitle(parentId);
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, parentId, recent, valuePrese);
			createLifecycleLog(logData);
		}

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateChildFreeformValue", ownerId, logOwner);
		cudDbService.updateChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, parentId, activityEntity);
	}

	// --- CREATE ---

	@Transactional
	public void createWord(WordLexemeMeaningDetails wordDetails) throws Exception {

		String value = wordDetails.getWordValue();
		String language = wordDetails.getLanguage();
		String dataset = wordDetails.getDataset();
		Long meaningId = wordDetails.getMeaningId();

		value = textDecorationService.removeEkiElementMarkup(value);
		String valueAsWord = textDecorationService.removeAccents(value, language);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, value, valueAsWord, language, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();

		List<String> tagNames = cudDbService.createLexemeAutomaticTags(lexemeId);
		LogData wordLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, value);
		createLifecycleLog(wordLogData);
		LogData lexemeLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, dataset);
		createLifecycleLog(lexemeLogData);
		tagNames.forEach(tagName -> {
			LogData tagLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(tagLogData);
		});

		activityLogService.createActivityLog("createWord", wordId, LifecycleLogOwner.WORD);
		activityLogService.createActivityLog("createWord", lexemeId, LifecycleLogOwner.LEXEME);
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordType", wordId, LifecycleLogOwner.WORD);
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordTypeId, typeCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode) throws Exception {
		ActivityLogData activityLog;
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, LifecycleLogOwner.WORD);
			boolean doLogging = false;
			String previousLogValue = null;
			Long groupId = lookupDbService.getWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = cudDbService.createWordRelationGroup(relationTypeCode);
				cudDbService.createWordRelationGroupMember(groupId, wordId);
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
				doLogging = true;
			} else {
				if (!lookupDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
					List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
					previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
					cudDbService.createWordRelationGroupMember(groupId, targetWordId);
					doLogging = true;
				}
			}
			if (doLogging) {
				List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
				String logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
				for (Map<String, Object> member : wordRelationGroupMembers) {
					Long memberId = (Long) member.get("id");
					LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION_GROUP_MEMBER, LifecycleProperty.VALUE, memberId,
							previousLogValue, logValue);
					createLifecycleLog(logData);
				}
			}
			activityLogService.createActivityLog(activityLog, targetWordId, ActivityEntity.WORD_RELATION_GROUP_MEMBER);
		} else {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, LifecycleLogOwner.WORD);
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
			LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			createLifecycleLog(relationLogData);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordRelation", targetWordId, LifecycleLogOwner.WORD);
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.WORD_RELATION);
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, oppositeRelationId);
				createLifecycleLog(oppositeRelationLogData);
			}
		}
	}

	@Transactional
	public void createWordNote(Long wordId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(LANGUAGE_CODE_EST);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(false);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createWordFreeform(ActivityEntity.WORD_NOTE, wordId, freeform);
	}

	@Transactional
	public void createLexeme(Long wordId, String datasetCode, Long meaningId) throws Exception {
		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, LifecycleLogOwner.WORD);
		Long lexemeId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1);
		if (lexemeId == null) {
			return;
		}
		cudDbService.adjustWordSecondaryLexemesComplexity(wordId);
		List<String> tagNames = cudDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);

		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
		createLifecycleLog(logData);
		tagNames.forEach(tagName -> {
			LogData tagLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(tagLogData);
		});
	}

	@Transactional
	public void createUsage(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.USAGE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.USAGE, lexemeId, freeform);
	}

	@Transactional
	public void createUsageTranslation(Long usageId, String valuePrese, String lang) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_TRANSLATION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_TRANSLATION, freeform);
	}

	@Transactional
	public void createUsageDefinition(Long usageId, String valuePrese, String lang) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_DEFINITION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeTag(Long lexemeId, String tagName) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
		activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeDeriv", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegister", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegion", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, regionCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeGovernment(Long lexemeId, String government, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GOVERNMENT);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, government);

		createLexemeFreeform(ActivityEntity.GOVERNMENT, lexemeId, freeform);
	}

	@Transactional
	public void createLexemeGrammar(Long lexemeId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GRAMMAR);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.GRAMMAR, lexemeId, freeform);
	}

	@Transactional
	public void createLexemeNote(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.LEXEME_NOTE, lexemeId, freeform);
	}

	@Transactional
	public void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String oppositeRelationType) throws Exception {
		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId1, LifecycleLogOwner.LEXEME);
		Long relationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
		LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId, relationType);
		createLifecycleLog(relationLogData);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.lexemeRelationExists(lexemeId2, lexemeId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId2, LifecycleLogOwner.LEXEME);
			Long oppositeRelationId = cudDbService.createLexemeRelation(lexemeId2, lexemeId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.LEXEME_RELATION);
			LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, oppositeRelationId, oppositeRelationType);
			createLifecycleLog(oppositeRelationLogData);
		}
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, LifecycleLogOwner.MEANING);
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
		createLifecycleLog(logData);
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, String oppositeRelationType) throws Exception {
		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId1, LifecycleLogOwner.MEANING);
		Long relationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
		LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId, relationType);
		createLifecycleLog(relationLogData);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.meaningRelationExists(meaningId2, meaningId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId2, LifecycleLogOwner.MEANING);
			Long oppositeRelationId = cudDbService.createMeaningRelation(meaningId2, meaningId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
			LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, oppositeRelationId, oppositeRelationType);
			createLifecycleLog(oppositeRelationLogData);
		}
	}

	@Transactional
	public void createMeaningLearnerComment(Long meaningId, String valuePrese, String lang) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.LEARNER_COMMENT);
		freeform.setLang(lang);
		freeform.setComplexity(Complexity.SIMPLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.LEARNER_COMMENT, meaningId, freeform);
	}

	@Transactional
	public void createMeaningNote(Long meaningId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.MEANING_NOTE, meaningId, freeform);
	}

	@Transactional
	public void createMeaningSemanticType(Long meaningId, String semanticTypeCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningSemanticType", meaningId, LifecycleLogOwner.MEANING);
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, semanticTypeCode);
		createLifecycleLog(logData);

	}

	@Transactional
	public void createDefinition(
			Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity, String typeCode, boolean isPublic) throws Exception {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, LifecycleLogOwner.MEANING);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, complexity, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createDefinitionNote(Long definitionId, String valuePrese, String lang, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createDefinitionFreeform(ActivityEntity.DEFINITION_NOTE, definitionId, freeform);
	}

	@Transactional
	public void createImageTitle(Long imageFreeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageFreeformId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeformChildFreeform(ActivityEntity.IMAGE_FILE, freeform);
	}

	@Transactional
	public void createOdWordRecommendation(Long wordId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.OD_WORD_RECOMMENDATION);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(true);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createWordFreeform(ActivityEntity.OD_WORD_RECOMMENDATION, wordId, freeform);
	}

	@Transactional
	public void createOdLexemeRecommendation(Long lexemeId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.OD_LEXEME_RECOMMENDATION);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(true);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.OD_LEXEME_RECOMMENDATION, lexemeId, freeform);
	}

	@Transactional
	public void createOdUsageDefinition(Long usageId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.OD_USAGE_DEFINITION);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.OD_USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void createOdUsageAlternative(Long usageId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.OD_USAGE_ALTERNATIVE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.OD_USAGE_ALTERNATIVE, freeform);
	}

	@Transactional
	public void createWordAndSynRelation(
			Long existingWordId, String valuePrese, String datasetCode, String language, String weightStr) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String valueAsWord = textDecorationService.removeAccents(value, language);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, valuePrese, valueAsWord, language, datasetCode, PUBLICITY_PRIVATE, null);
		Long createdWordId = wordLexemeMeaningId.getWordId();
		Long createdLexemeId = wordLexemeMeaningId.getLexemeId();

		List<String> createdTagNames = cudDbService.createLexemeAutomaticTags(createdLexemeId);
		createdTagNames.forEach(tagName -> {
			LogData tagLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, createdLexemeId, tagName);
			createLifecycleLog(tagLogData);
		});

		activityLogService.createActivityLog("createWordAndSynRelation", createdWordId, LifecycleLogOwner.WORD);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, createdWordId, valuePrese);
		createLifecycleLog(logData);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordAndSynRelation", existingWordId, LifecycleLogOwner.WORD);
		Long createdRelationId = cudDbService.createWordRelation(existingWordId, createdWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedRelationToFirst(existingWordId, createdRelationId);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void createSynRelation(Long word1Id, Long word2Id, String weightStr, String datasetCode) throws Exception {

		boolean word2DatasetLexemeExists = lookupDbService.wordLexemeExists(word2Id, datasetCode);
		if (!word2DatasetLexemeExists) {
			createLexeme(word2Id, datasetCode, null);
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynRelation", word1Id, LifecycleLogOwner.WORD);
		Long createdRelationId = cudDbService.createWordRelation(word1Id, word2Id, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedRelationToFirst(word1Id, createdRelationId);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	private void createWordFreeform(ActivityEntity activityEntity, Long wordId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, LifecycleLogOwner.WORD);
		Long wordFreeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, wordFreeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.WORD_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.NOTE, wordFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_WORD_RECOMMENDATION)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, wordFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	private void createLexemeFreeform(ActivityEntity activityEntity, Long lexemeId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, LifecycleLogOwner.LEXEME);
		Long lexemeFreeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, lexemeFreeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.OD_LEXEME_RECOMMENDATION)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, lexemeFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.LEXEME_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, lexemeFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.GRAMMAR)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, lexemeFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.GOVERNMENT)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, lexemeFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.USAGE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, lexemeFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	private void createMeaningFreeform(ActivityEntity activityEntity, Long meaningId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, LifecycleLogOwner.MEANING);
		Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, meaningFreeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.LEARNER_COMMENT)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, meaningFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.MEANING_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, meaningFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	private void createDefinitionFreeform(ActivityEntity activityEntity, Long definitionId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionFreeform", meaningId, LifecycleLogOwner.MEANING);
		Long definitionFreeformId = cudDbService.createDefinitionFreeform(definitionId, freeform, userName);
		activityLogService.createActivityLog(activityLog, definitionFreeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.DEFINITION_NOTE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, definitionFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	private void createMeaningFreeformChildFreeform(ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long meaningFreeformId = freeform.getParentId();
		Long meaningId = activityLogService.getOwnerId(meaningFreeformId, activityEntity);
		String valuePrese = freeform.getValuePrese();

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createChildFreeform", meaningId, LifecycleLogOwner.MEANING);
		Long childFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, childFreeformId, activityEntity);

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.IMAGE_FILE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, meaningFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	private void createUsageChildFreeform(ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = freeform.getParentId();
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		String valuePrese = freeform.getValuePrese();

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageChildFreeform", lexemeId, LifecycleLogOwner.LEXEME);
		Long usageChildFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, usageChildFreeformId, activityEntity);

		// remove when lifecycle log is no longer needed
		if (activityEntity.equals(ActivityEntity.USAGE_TRANSLATION)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageChildFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.USAGE_DEFINITION)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageChildFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_USAGE_DEFINITION)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, usageChildFreeformId, valuePrese);
			createLifecycleLog(logData);
		} else if (activityEntity.equals(ActivityEntity.OD_USAGE_ALTERNATIVE)) {
			LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, usageChildFreeformId, valuePrese);
			createLifecycleLog(logData);
		}
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId);
		createLifecycleLog(logData);
		activityLogService.createActivityLog("deleteWord", wordId, LifecycleLogOwner.WORD);
		SimpleWord word = lookupDbService.getSimpleWord(wordId);
		cudDbService.deleteWord(word);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) throws Exception {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, typeCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordType", wordId, LifecycleLogOwner.WORD);
			cudDbService.deleteWordWordType(wordWordTypeId);
			activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) throws Exception {
		Long wordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordRelation", wordId, LifecycleLogOwner.WORD);
		Long groupId = lookupDbService.getWordRelationGroupId(relationId);
		if (groupId == null) {
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			createLifecycleLog(logData);
			cudDbService.deleteWordRelation(relationId);
		} else {
			List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
			String relationTypeCode = wordRelationGroupMembers.get(0).get("word_rel_type_code").toString();
			String previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			String logValue = null;
			if (wordRelationGroupMembers.size() > 2) {
				logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream()
						.filter(m -> !relationId.equals(m.get("id")))
						.map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			}
			for (Map<String, Object> member : wordRelationGroupMembers) {
				Long memberId = (Long) member.get("id");
				LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION_GROUP_MEMBER, LifecycleProperty.VALUE, memberId,
						previousLogValue, logValue);
				createLifecycleLog(logData);
			}
			cudDbService.deleteWordRelationGroupMember(relationId);
			if (wordRelationGroupMembers.size() <= 2) {
				cudDbService.deleteWordRelationGroup(groupId);
			}
		}
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void deleteWordNote(Long wordNoteId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.NOTE, wordNoteId);
		createLifecycleLog(logData);
		Long wordId = activityLogService.getOwnerId(wordNoteId, ActivityEntity.WORD_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordNote", wordId, LifecycleLogOwner.WORD);
		cudDbService.deleteFreeform(wordNoteId);
		activityLogService.createActivityLog(activityLog, wordNoteId, ActivityEntity.WORD_NOTE);
	}

	@Transactional
	public void deleteDefinition(Long definitionId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, null);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinition", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteDefinition(definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void deleteDefinitionNote(Long definitionNoteId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, definitionNoteId);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNote", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteFreeform(definitionNoteId);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId) throws Exception {

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = commonDataDbService.getWordLexemeMeaningId(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();

		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
		createLifecycleLog(logData);
		updateLexemeLevels(lexemeId, "delete");

		activityLogService.createActivityLog("deleteLexeme", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteLexeme(lexemeId);
		cudDbService.adjustWordSecondaryLexemesComplexity(wordId);
		if (isOnlyLexemeForMeaning) {
			deleteMeaning(meaningId);
		}
		if (isOnlyLexemeForWord) {
			deleteWord(wordId);
		}
	}

	@Transactional
	public void deleteLexemeAndMeaningLexemes(Long lexemeId, String meaningLexemesLang, String datasetCode) throws Exception {
		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, datasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			deleteLexeme(lexemeIdToDelete);
		}
	}

	@Transactional
	public void deleteUsage(Long usageId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsage", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional
	public void deleteUsageTranslation(Long usageTranslationId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageTranslation", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(usageTranslationId);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional
	public void deleteUsageDefinition(Long usageDefinitionId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageDefinition", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(usageDefinitionId);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional
	public void deleteLexemeGovernment(Long governmentId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, governmentId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGovernment", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional
	public void deleteLexemeGrammar(Long grammarId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, grammarId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGrammar", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemeNote(Long lexemeNoteId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, lexemeNoteId);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNote", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) throws Exception {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemePos", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.deleteLexemePos(lexemePosId);
			activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		}
	}

	@Transactional
	public void deleteLexemeTag(Long lexemeId, String tagName) throws Exception {
		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeTag", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.deleteLexemeTag(lexemeTagId);
			activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) throws Exception {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeDeriv", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
			activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) throws Exception {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegister", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
			activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode) throws Exception {
		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, regionCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegion", lexemeId, LifecycleLogOwner.LEXEME);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
			activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(relationId, ActivityEntity.LEXEME_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRelation", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteLexemeRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId, String datasetCode) throws Exception {
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId);
		}
	}

	@Transactional
	public void deleteMeaning(Long meaningId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId);
		createLifecycleLog(logData);
		activityLogService.createActivityLog("deleteMeaning", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain) throws Exception {
		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode(), null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.deleteMeaningDomain(meaningDomainId);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode) throws Exception {
		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			LogData logData = new LogData(
					LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, semanticTypeCode, null);
			createLifecycleLog(logData);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, LifecycleLogOwner.MEANING);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
			activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		}
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(relationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteMeaningRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long learnerCommentId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, learnerCommentId);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningLearnerComment", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteFreeform(learnerCommentId);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional
	public void deleteMeaningNote(Long meaningNoteId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, meaningNoteId);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNote", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteFreeform(meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional
	public void deleteImageTitle(Long imageId) throws Exception {
		String recent = lookupDbService.getImageTitle(imageId);
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, imageId, recent, null);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteImageTitle", meaningId, LifecycleLogOwner.MEANING);
		Long imageTitleId = cudDbService.deleteImageTitle(imageId);
		activityLogService.createActivityLog(activityLog, imageTitleId, ActivityEntity.IMAGE_TITLE);
	}

	@Transactional
	public void deleteMeaningImage(Long imageId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE, imageId);
		createLifecycleLog(logData);
		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, LifecycleLogOwner.MEANING);
		cudDbService.deleteFreeform(imageId);
		activityLogService.createActivityLog(activityLog, imageId, ActivityEntity.IMAGE_FILE);
	}

	@Transactional
	public void deleteOdWordRecommendation(Long freeformId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, freeformId);
		createLifecycleLog(logData);
		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdWordRecommendation", wordId, LifecycleLogOwner.WORD);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
	}

	@Transactional
	public void deleteOdLexemeRecommendation(Long freeformId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, freeformId);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_LEXEME_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdLexemeRecommendation", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_LEXEME_RECOMMENDATION);
	}

	@Transactional
	public void deleteOdUsageDefinition(Long freeformId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, freeformId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdUsageDefinition", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_USAGE_DEFINITION);
	}

	@Transactional
	public void deleteOdUsageAlternative(Long freeformId) throws Exception {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, freeformId, null);
		createLifecycleLog(logData);
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_USAGE_ALTERNATIVE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdUsageAlternative", lexemeId, LifecycleLogOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_USAGE_ALTERNATIVE);
	}

	private void moveCreatedRelationToFirst(Long wordId, Long relationId) {
		List<Relation> existingRelations = lookupDbService.getWordRelations(wordId, RAW_RELATION_TYPE);
		if (existingRelations.size() > 1) {

			Relation firstRelation = existingRelations.get(0);
			List<Long> existingOrderByValues = existingRelations.stream().map(Relation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, firstRelation.getOrderBy());
			existingRelations.remove(existingRelations.size() - 1);
			existingOrderByValues.remove(0);

			int relIdx = 0;
			for (Relation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}

	private void setFreeformValueTextAndValuePrese(FreeForm freeform, String valuePrese) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
	}
}
