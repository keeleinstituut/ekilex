package eki.ekilex.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.PermConstant;
import eki.common.constant.RelationStatus;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.ListData;
import eki.ekilex.data.Response;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordRelation;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.TagDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
@Component
public class CudService extends AbstractService implements GlobalConstant, PermConstant {

	private static final String RAW_RELATION_TYPE = "raw";

	private static final String USER_ADDED_WORD_RELATION_NAME = "user";

	private static final String UNDEFINED_RELATION_STATUS = RelationStatus.UNDEFINED.name();

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private TagDbService tagDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	// --- UPDATE ---

	@Transactional
	public void updateWordValue(Long wordId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValue", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
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
	public void updateWordValueWithDuplication(Long wordId, String valuePrese, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordValue(wordId, valuePrese, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			updateWordValue(duplicateWordId, valuePrese, isManualEventOnUpdateEnabled);
		}
	}

	private Long duplicateWordData(Long wordId, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord simpleWord = compositionDbService.getSimpleWord(wordId);
		Long duplicateWordId = compositionDbService.cloneWord(simpleWord);
		compositionDbService.cloneWordParadigmsAndForms(wordId, duplicateWordId);
		compositionDbService.cloneWordTypes(wordId, duplicateWordId);
		compositionDbService.cloneWordRelations(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeforms(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymology(wordId, duplicateWordId);
		activityLogService.createActivityLog("duplicateWordData", duplicateWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);

		return duplicateWordId;
	}

	private void updateWordLexemesWordId(Long currentWordId, Long newWordId, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("updateWordLexemesWordId", currentWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("updateWordLexemesWordId", newWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordLexemesWordId(currentWordId, newWordId, datasetCode);
		activityLogService.createActivityLog(activityLog1, currentWordId, ActivityEntity.WORD);
		activityLogService.createActivityLog(activityLog2, newWordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordVocalForm(Long wordId, String vocalForm, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordVocalForm", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordVocalForm(wordId, vocalForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordMorphophonoForm(Long wordId, String morphophonoForm, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordMorphophonoForm", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordMorphophonoForm(wordId, morphophonoForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordType", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void updateWordTypeWithDuplication(
			Long wordId, String currentTypeCode, String newTypeCode, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordType(wordId, currentTypeCode, newTypeCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			updateWordType(duplicateWordId, currentTypeCode, newTypeCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void updateWordAspect(Long wordId, String aspectCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordAspect", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordAspect(wordId, aspectCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordDisplayMorph(Long wordId, String displayMorphCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordDisplayMorph", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordDisplayMorph(wordId, displayMorphCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordGender", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordGender(wordId, genderCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGenderWithDuplication(Long wordId, String genderCode, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordGender(wordId, genderCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			updateWordGender(duplicateWordId, genderCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void updateWordLang(Long wordId, String langCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLang", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.updateWordLang(wordId, langCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordNote(Long wordNoteId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(wordNoteId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.WORD_NOTE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateWordManualEventOn(Long wordId, String eventOnStr) throws Exception {

		Timestamp eventOn = conversionUtil.dateStrToTimestamp(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordManualEventOn", wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateWordManualEventOn(wordId, eventOn);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateMeaningManualEventOn(Long meaningId, String eventOnStr) throws Exception {

		Timestamp eventOn = conversionUtil.dateStrToTimestamp(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningManualEventOn", meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateMeaningManualEventOn(meaningId, eventOn);
		activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void updateWordLexemesTagComplete(Long wordId, String userRoleDatasetCode, Tag tag, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLexemesTagComplete", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();

		if (removeToComplete) {
			cudDbService.deleteWordLexemesTag(wordId, userRoleDatasetCode, tagName);
		} else {
			cudDbService.createWordLexemesTag(wordId, userRoleDatasetCode, tagName);
		}

		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.TAG);
	}

	@Transactional
	public void updateMeaningLexemesTagComplete(Long meaningId, String userRoleDatasetCode, Tag tag, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningLexemesTagComplete", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();

		if (removeToComplete) {
			cudDbService.deleteMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
		} else {
			cudDbService.createMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
		}

		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.TAG);
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordRelationId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRelationOrdering", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
			cudDbService.updateWordRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordEtymId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordEtymologyOrdering", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
			cudDbService.updateWordEtymologyOrderby(item);
			activityLogService.createActivityLog(activityLog, wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
		}
	}

	@Transactional
	public void updateMeaningDomainOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningDomainId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningDomainId, ActivityEntity.DOMAIN);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomainOrdering", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningDomainOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void updateGovernmentOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long governmentId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateGovernmentOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
		}
	}

	@Transactional
	public void updateUsageOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long usageId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}

	@Transactional
	public void updateLexemeNoteOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeNoteId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		}
	}

	@Transactional
	public void updateMeaningNoteOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteOrdering", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
		}
	}

	@Transactional
	public void updateDefinitionNoteOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteOrdering", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION_NOTE);
		}
	}

	@Transactional
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeMeaningWordOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		for (ListData item : items) {
			cudDbService.updateLexemeOrderby(item);
		}
		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.MEANING_WORD);
	}

	@Transactional
	public void updateUsageValue(Long usageId, String valuePrese, Complexity complexity, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageId);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateUsageTranslation(Long usageTranslationId, String valuePrese, String lang, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageTranslationId);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_TRANSLATION, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long usageDefinitionId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageDefinitionId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_DEFINITION, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeId = item.getId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, int lexemePosition, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, lexemePosition);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeGovernment(Long lexemeGovernmentId, String value, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGovernmentId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, value);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GOVERNMENT, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeGrammar(Long lexemeGrammarId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGrammarId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GRAMMAR, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeComplexity(Long lexemeId, String complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeComplexity", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeComplexity(lexemeId, complexity);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePos", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeDeriv", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		activityLogService.createActivityLog(activityLog, lexemeDerivid, ActivityEntity.DERIV);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegister", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegion", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional
	public void updateLexemeReliability(Long lexemeId, String reliabilityStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeReliability", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Integer reliability;
		try {
			reliability = Integer.parseInt(reliabilityStr);
		} catch (NumberFormatException e) {
			reliability = null;
		}
		cudDbService.updateLexemeReliability(lexemeId, reliability);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeNote(
			Long lexemeNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.LEXEME_NOTE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemePublicity(Long lexemeId, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePublicity", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeProcessState(lexemeId, isPublic);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeValueState", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeProficiencyLevel(Long lexemeId, String proficiencyLevelCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeProficiencyLevel", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeProficiencyLevel(lexemeId, proficiencyLevelCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeRelationId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeRelationId, ActivityEntity.LEXEME_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRelationOrdering", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void updateDefinition(
			Long definitionId, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.updateDefinition(definitionId, value, valuePrese, lang, complexity, typeCode, isPublic);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionOrdering", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.updateDefinitionOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	@Transactional
	public void updateDefinitionNote(Long definitionNoteId, String valuePrese, String lang, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(definitionNoteId);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.DEFINITION_NOTE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningRelationId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationOrdering", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomain", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void updateMeaningLearnerComment(Long learnerCommentId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(learnerCommentId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.LEARNER_COMMENT, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningNote(Long meaningNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(meaningNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEANING_NOTE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningSemanticType", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
	}

	@Transactional
	public void updateMeaningImage(Long imageId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(imageId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateImageTitle(Long imageId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateChildFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningMedia(Long mediaId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(mediaId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEDIA_FILE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateOdWordRecommendation(Long freeformId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.OD_WORD_RECOMMENDATION, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeWeight(Long lexemeId, String lexemeWeightStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeWeight", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		BigDecimal lexemeWeight = new BigDecimal(lexemeWeightStr);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateMeaningRelationWeight(Long meaningRelationId, String relationWeightStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationWeight", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		BigDecimal relationWeight = new BigDecimal(relationWeightStr);
		cudDbService.updateMeaningRelationWeight(meaningRelationId, relationWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void updateWordDataAndLexemeWeight(WordLexemeMeaningDetails wordDataAndLexemeWeight, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = wordDataAndLexemeWeight.getWordId();
		Long lexemeId = wordDataAndLexemeWeight.getLexemeId();
		String lexemeWeight = wordDataAndLexemeWeight.getLexemeWeight();
		String wordValuePrese = wordDataAndLexemeWeight.getWordValuePrese();

		updateWordValue(wordId, wordValuePrese, isManualEventOnUpdateEnabled);
		updateLexemeWeight(lexemeId, lexemeWeight, isManualEventOnUpdateEnabled);
	}

	private void updateFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long freeformId = freeform.getId();
		Long ownerId = activityLogService.getOwnerId(freeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, logOwner, isManualEventOnUpdateEnabled);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, activityEntity);
	}

	private void updateChildFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long parentId = freeform.getParentId();
		Long ownerId = activityLogService.getOwnerId(parentId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateChildFreeform", ownerId, logOwner, isManualEventOnUpdateEnabled);
		cudDbService.updateChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, parentId, activityEntity);
	}

	// --- CREATE ---

	@Transactional
	public WordLexemeMeaningIdTuple createWord(WordLexemeMeaningDetails wordDetails, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = wordDetails.getWordValue();
		String language = wordDetails.getLanguage();
		String dataset = wordDetails.getDataset();
		Long meaningId = wordDetails.getMeaningId();
		boolean isMeaningCreate = meaningId == null;

		value = textDecorationService.removeEkiElementMarkup(value);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, value, valueAsWord, value, language, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		meaningId = wordLexemeMeaningId.getMeaningId();
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createWord", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWord", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		if (isMeaningCreate) {
			activityLogService.createActivityLog("createWord", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		}

		return wordLexemeMeaningId;
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordType", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void createWordTypeWithDuplication(Long wordId, String typeCode, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			createWordType(wordId, typeCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			createWordType(duplicateWordId, typeCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
			Long groupId = lookupDbService.getWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = cudDbService.createWordRelationGroup(relationTypeCode);
				cudDbService.createWordRelationGroupMember(groupId, wordId);
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
			} else if (!lookupDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
			}
			activityLogService.createActivityLog(activityLog, targetWordId, ActivityEntity.WORD_RELATION_GROUP_MEMBER);
		} else {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordRelation", targetWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.WORD_RELATION);
			}
		}
	}

	@Transactional
	public void createWordNote(Long wordId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(LANGUAGE_CODE_EST);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(false);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createWordFreeform(ActivityEntity.WORD_NOTE, wordId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createWordNoteWithDuplication(Long wordId, String valuePrese, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			createWordNote(wordId, valuePrese, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			createWordNote(duplicateWordId, valuePrese, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void createLexeme(Long wordId, String datasetCode, Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long lexemeId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1);
		if (lexemeId == null) {
			return;
		}
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void createUsage(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.USAGE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.USAGE, lexemeId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createUsageTranslation(Long usageId, String valuePrese, String lang, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_TRANSLATION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_TRANSLATION, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createUsageDefinition(Long usageId, String valuePrese, String lang, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_DEFINITION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_DEFINITION, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public void createLexemeTag(Long lexemeId, String tagName, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
		activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createMeaningTag(Long meaningId, String tagName, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningTag", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningTagId = cudDbService.createMeaningTag(meaningId, tagName);
		activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeDeriv", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegister", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegion", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional
	public void createLexemeGovernment(Long lexemeId, String government, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GOVERNMENT);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, government);

		createLexemeFreeform(ActivityEntity.GOVERNMENT, lexemeId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeGrammar(Long lexemeId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GRAMMAR);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.GRAMMAR, lexemeId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeNote(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.LEXEME_NOTE, lexemeId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String oppositeRelationType, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId1, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long relationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.lexemeRelationExists(lexemeId2, lexemeId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId2, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			Long oppositeRelationId = cudDbService.createLexemeRelation(lexemeId2, lexemeId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, String oppositeRelationType, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId1, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long relationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.meaningRelationExists(meaningId2, meaningId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId2, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			Long oppositeRelationId = cudDbService.createMeaningRelation(meaningId2, meaningId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void createMeaningLearnerComment(Long meaningId, String valuePrese, String lang, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.LEARNER_COMMENT);
		freeform.setLang(lang);
		freeform.setComplexity(Complexity.SIMPLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.LEARNER_COMMENT, meaningId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningNote(Long meaningId, String valuePrese, String lang, Complexity complexity, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.MEANING_NOTE, meaningId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningSemanticType(Long meaningId, String semanticTypeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningSemanticType", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);

	}

	@Transactional
	public void createDefinition(
			Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity, String typeCode, boolean isPublic,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, complexity, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void createDefinitionNote(Long definitionId, String valuePrese, String lang, boolean isPublic, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createDefinitionFreeform(ActivityEntity.DEFINITION_NOTE, definitionId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningImage(Long meaningId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.IMAGE_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.IMAGE_FILE, meaningId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createImageTitle(Long imageFreeformId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageFreeformId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeformChildFreeform(ActivityEntity.IMAGE_FILE, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningMedia(Long meaningId, String valuePrese, Complexity complexity, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.MEDIA_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.MEDIA_FILE, meaningId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createOdWordRecommendation(Long wordId, String valuePrese, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.OD_WORD_RECOMMENDATION);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(true);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createWordFreeform(ActivityEntity.OD_WORD_RECOMMENDATION, wordId, freeform, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createWordAndSynRelation(
			Long existingWordId, String valuePrese, String datasetCode, String language, String weightStr, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, valuePrese, valueAsWord, value, language, datasetCode, PUBLICITY_PRIVATE, null);
		Long createdWordId = wordLexemeMeaningId.getWordId();
		Long createdLexemeId = wordLexemeMeaningId.getLexemeId();
		tagDbService.createLexemeAutomaticTags(createdLexemeId);

		activityLogService.createActivityLog("createWordAndSynRelation", createdWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordAndSynRelation", existingWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long createdRelationId = cudDbService.createWordRelation(existingWordId, createdWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(existingWordId, createdRelationId, RAW_RELATION_TYPE);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void createSynWordRelation(Long targetWordId, Long sourceWordId, String weightStr, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean word2DatasetLexemeExists = lookupDbService.wordLexemeExists(sourceWordId, datasetCode);
		if (!word2DatasetLexemeExists) {
			createLexeme(sourceWordId, datasetCode, null, isManualEventOnUpdateEnabled);
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynWordRelation", targetWordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long createdRelationId = cudDbService.createWordRelation(targetWordId, sourceWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(targetWordId, createdRelationId, RAW_RELATION_TYPE);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	private void createWordFreeform(ActivityEntity activityEntity, Long wordId, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long wordFreeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, wordFreeformId, activityEntity);
	}

	private void createLexemeFreeform(ActivityEntity activityEntity, Long lexemeId, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeFreeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, lexemeFreeformId, activityEntity);
	}

	private void createMeaningFreeform(ActivityEntity activityEntity, Long meaningId, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, meaningFreeformId, activityEntity);
	}

	private void createDefinitionFreeform(ActivityEntity activityEntity, Long definitionId, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionFreeform", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long definitionFreeformId = cudDbService.createDefinitionFreeform(definitionId, freeform, userName);
		activityLogService.createActivityLog(activityLog, definitionFreeformId, activityEntity);
	}

	private void createMeaningFreeformChildFreeform(ActivityEntity activityEntity, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long meaningFreeformId = freeform.getParentId();
		Long meaningId = activityLogService.getOwnerId(meaningFreeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createChildFreeform", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long childFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, childFreeformId, activityEntity);
	}

	private void createUsageChildFreeform(ActivityEntity activityEntity, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = freeform.getParentId();
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageChildFreeform", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long usageChildFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, usageChildFreeformId, activityEntity);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog("deleteWord", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		SimpleWord word = lookupDbService.getSimpleWord(wordId);
		cudDbService.deleteWord(word);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordType", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
			cudDbService.deleteWordWordType(wordWordTypeId);
			activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		}
	}

	@Transactional
	public void deleteWordTypeWithDuplication(Long wordId, String typeCode, Long userId, DatasetPermission userRole, boolean isManualEventOnUpdateEnabled) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			deleteWordType(wordId, typeCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode, isManualEventOnUpdateEnabled);
			deleteWordType(duplicateWordId, typeCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordRelation", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		Long groupId = lookupDbService.getWordRelationGroupId(relationId);
		if (groupId == null) {
			cudDbService.deleteWordRelation(relationId);
		} else {
			List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
			cudDbService.deleteWordRelationGroupMember(relationId);
			if (wordRelationGroupMembers.size() <= 2) {
				cudDbService.deleteWordRelationGroup(groupId);
			}
		}
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void deleteWordNote(Long wordNoteId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(wordNoteId, ActivityEntity.WORD_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordNote", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(wordNoteId);
		activityLogService.createActivityLog(activityLog, wordNoteId, ActivityEntity.WORD_NOTE);
	}

	@Transactional
	public void deleteDefinition(Long definitionId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinition", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteDefinition(definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void deleteDefinitionNote(Long definitionNoteId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNote", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(definitionNoteId);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = commonDataDbService.getWordLexemeMeaningId(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		updateLexemeLevels(lexemeId, "delete", isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("deleteLexeme", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexeme(lexemeId);
		if (isOnlyLexemeForMeaning) {
			deleteMeaning(meaningId, isManualEventOnUpdateEnabled);
		}
		if (isOnlyLexemeForWord) {
			deleteWord(wordId, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteLexemeAndMeaningLexemes(Long lexemeId, String meaningLexemesLang, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, datasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			deleteLexeme(lexemeIdToDelete, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteUsage(Long usageId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsage", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional
	public void deleteUsageTranslation(Long usageTranslationId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageTranslation", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageTranslationId);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional
	public void deleteUsageDefinition(Long usageDefinitionId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageDefinition", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageDefinitionId);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional
	public void deleteLexemeGovernment(Long governmentId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGovernment", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional
	public void deleteLexemeGrammar(Long grammarId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGrammar", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemeNote(Long lexemeNoteId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNote", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemePos", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemePos(lexemePosId);
			activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		}
	}

	@Transactional
	public void deleteLexemeTag(Long lexemeId, String tagName, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeTag", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeTag(lexemeTagId);
			activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteMeaningTag(Long meaningId, String tagName, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long meaningTagId = lookupDbService.getMeaningTagId(meaningId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningTag", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningTag(meaningTagId);
			activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeDeriv", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
			activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegister", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
			activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegion", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
			activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(relationId, ActivityEntity.LEXEME_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRelation", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexemeRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId, String datasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean isSuperiorPermission = StringUtils.equals(DATASET_XXX, datasetCode);
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds;
		if (isSuperiorPermission) {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId);
		} else {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		}
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteMeaning(Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog("deleteMeaning", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningDomain(meaningDomainId);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
			activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		}
	}

	@Transactional
	public Response deleteMeaningRelation(Long relationId, Response response, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Locale locale = LocaleContextHolder.getLocale();
		List<Long> oppositeRelationIds = lookupDbService.getMeaningRelationOppositeRelationIds(relationId);
		if (oppositeRelationIds.size() == 1) {
			Long oppositeRelationId = oppositeRelationIds.get(0);
			Long oppositeMeaningId = activityLogService.getOwnerId(oppositeRelationId, ActivityEntity.MEANING_RELATION);
			activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", oppositeMeaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningRelation(oppositeRelationId);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
		} else if (oppositeRelationIds.size() > 1) {
			String message = messageSource.getMessage("delete.meaning.relation.multiple.opposite", new Object[0], locale);
			response.setMessage(message);
		}

		Long meaningId = activityLogService.getOwnerId(relationId, ActivityEntity.MEANING_RELATION);
		activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);

		return response;
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long learnerCommentId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningLearnerComment", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(learnerCommentId);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional
	public void deleteMeaningNote(Long meaningNoteId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNote", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional
	public void deleteImageTitle(Long imageId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteImageTitle", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		Long imageTitleId = cudDbService.deleteImageTitle(imageId);
		activityLogService.createActivityLog(activityLog, imageTitleId, ActivityEntity.IMAGE_TITLE);
	}

	@Transactional
	public void deleteMeaningImage(Long imageId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(imageId);
		activityLogService.createActivityLog(activityLog, imageId, ActivityEntity.IMAGE_FILE);
	}

	@Transactional
	public void deleteMeaningMedia(Long mediaId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(mediaId, ActivityEntity.MEDIA_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningMedia", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(mediaId);
		activityLogService.createActivityLog(activityLog, mediaId, ActivityEntity.MEDIA_FILE);
	}

	@Transactional
	public void deleteOdWordRecommendation(Long freeformId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdWordRecommendation", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
	}

	@Transactional
	public void deleteParadigm(Long paradigmId, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(paradigmId, ActivityEntity.PARADIGM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteParadigm", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		cudDbService.deleteParadigm(paradigmId);
		activityLogService.createActivityLog(activityLog, paradigmId, ActivityEntity.PARADIGM);
	}

	private void moveCreatedWordRelationToFirst(Long wordId, Long relationId, String relTypeCode) {
		List<WordRelation> existingRelations = lookupDbService.getWordRelations(wordId, relTypeCode);
		if (existingRelations.size() > 1) {

			WordRelation firstRelation = existingRelations.get(0);
			List<Long> existingOrderByValues = existingRelations.stream().map(WordRelation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, firstRelation.getOrderBy());
			existingRelations.remove(existingRelations.size() - 1);
			existingOrderByValues.remove(0);

			int relIdx = 0;
			for (WordRelation relation : existingRelations) {
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
