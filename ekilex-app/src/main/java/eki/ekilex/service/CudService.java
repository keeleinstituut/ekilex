package eki.ekilex.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.PermConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.ListData;
import eki.ekilex.data.Response;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
@Component
public class CudService extends AbstractCudService implements GlobalConstant, PermConstant, ActivityFunct {

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private EkilexPermissionEvaluator ekilexPermissionEvaluator;

	// --- UPDATE ---

	@Transactional
	public void updateWordValue(Long wordId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValue", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordValue(wordId, value, valuePrese);
		if (StringUtils.isNotEmpty(valueAsWord)) {
			cudDbService.updateAsWordValue(wordId, valueAsWord);
		}
		SimpleWord updatedWord = new SimpleWord(wordId, value, lang);
		cudDbService.adjustWordHomonymNrs(originalWord);
		cudDbService.adjustWordHomonymNrs(updatedWord);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	private Long duplicateWordData(Long wordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord simpleWord = compositionDbService.getSimpleWord(wordId);
		Long duplicateWordId = compositionDbService.cloneWord(simpleWord);
		compositionDbService.cloneWordParadigmsAndForms(wordId, duplicateWordId);
		compositionDbService.cloneWordTypes(wordId, duplicateWordId);
		compositionDbService.cloneWordRelations(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeforms(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymology(wordId, duplicateWordId);
		activityLogService.createActivityLog("duplicateWordData", duplicateWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);

		return duplicateWordId;
	}

	private void updateWordLexemesWordId(Long currentWordId, Long newWordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("updateWordLexemesWordId", currentWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("updateWordLexemesWordId", newWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordLexemesWordId(currentWordId, newWordId, roleDatasetCode);
		activityLogService.createActivityLog(activityLog1, currentWordId, ActivityEntity.WORD);
		activityLogService.createActivityLog(activityLog2, newWordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordVocalForm(Long wordId, String vocalForm, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordVocalForm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordVocalForm(wordId, vocalForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordMorphophonoForm(Long wordId, String morphophonoForm, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordMorphophonoForm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordMorphophonoForm(wordId, morphophonoForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void updateWordTypeWithDuplication(
			Long wordId, String currentTypeCode, String newTypeCode, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String roleDatasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, roleDatasetCode, wordId);
		if (isWordCrudGrant) {
			updateWordType(wordId, currentTypeCode, newTypeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordType(duplicateWordId, currentTypeCode, newTypeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void updateWordAspect(Long wordId, String aspectCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordAspect", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordAspect(wordId, aspectCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordDisplayMorph(Long wordId, String displayMorphCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordDisplayMorph", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordDisplayMorph(wordId, displayMorphCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordGender", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordGender(wordId, genderCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGenderWithDuplication(Long wordId, String genderCode, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String roleDatasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, roleDatasetCode, wordId);
		if (isWordCrudGrant) {
			updateWordGender(wordId, genderCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordGender(duplicateWordId, genderCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void updateWordLang(Long wordId, String langCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLang", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordLang(wordId, langCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordForum(Long wordForumId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String userName = user.getName();

		cudDbService.updateWordForum(wordForumId, value, valuePrese, userName);
	}

	@Transactional
	public void updateWordManualEventOn(Long wordId, String eventOnStr, String roleDatasetCode) throws Exception {

		Timestamp eventOn = conversionUtil.dateStrToTimestamp(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordManualEventOn", wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateWordManualEventOn(wordId, eventOn);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateMeaningManualEventOn(Long meaningId, String eventOnStr, String roleDatasetCode) throws Exception {

		Timestamp eventOn = conversionUtil.dateStrToTimestamp(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog(UPDATE_MEANING_MANUAL_EVENT_ON_FUNCT, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateMeaningManualEventOn(meaningId, eventOn);
		activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void updateWordLexemesTagComplete(Long wordId, String userRoleDatasetCode, Tag tag, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLexemesTagComplete", wordId, ActivityOwner.WORD, userRoleDatasetCode, isManualEventOnUpdateEnabled);

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

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningLexemesTagComplete", meaningId, ActivityOwner.MEANING, userRoleDatasetCode, isManualEventOnUpdateEnabled);

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
	public void updateWordRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordRelationId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRelationOrdering", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordEtymId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordEtymologyOrdering", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordEtymologyOrderby(item);
			activityLogService.createActivityLog(activityLog, wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
		}
	}

	@Transactional
	public void updateMeaningDomainOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningDomainId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningDomainId, ActivityEntity.DOMAIN);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomainOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningDomainOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void updateGovernmentOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long governmentId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateGovernmentOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
		}
	}

	@Transactional
	public void updateUsageOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long usageId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}

	@Transactional
	public void updateLexemeNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeNoteId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		}
	}

	@Transactional
	public void updateMeaningNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
		}
	}

	@Transactional
	public void updateDefinitionNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION_NOTE);
		}
	}

	@Transactional
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeMeaningWordOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		for (ListData item : items) {
			cudDbService.updateLexemeOrderby(item);
		}
		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.MEANING_WORD);
	}

	@Transactional
	public void updateUsageValue(Long usageId, String valuePrese, Complexity complexity, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageId);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateUsageTranslation(Long usageTranslationId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageTranslationId);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_TRANSLATION, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long usageDefinitionId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageDefinitionId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_DEFINITION, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeId = item.getId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, int lexemePosition, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, lexemePosition);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeGovernment(Long lexemeGovernmentId, String value, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGovernmentId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, value);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GOVERNMENT, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeGrammar(Long lexemeGrammarId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGrammarId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GRAMMAR, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeComplexity(Long lexemeId, String complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeComplexity", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeComplexity(lexemeId, complexity);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		activityLogService.createActivityLog(activityLog, lexemeDerivid, ActivityEntity.DERIV);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister,String roleDatasetCode,  boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional
	public void updateLexemeReliability(Long lexemeId, String reliabilityStr, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeReliability", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
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
			Long lexemeNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.LEXEME_NOTE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemePublicity(Long lexemeId, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePublicity", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemePublicity(lexemeId, isPublic);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeValueState", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeProficiencyLevel(Long lexemeId, String proficiencyLevelCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeProficiencyLevel", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeProficiencyLevel(lexemeId, proficiencyLevelCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeRelationId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeRelationId, ActivityEntity.LEXEME_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRelationOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void updateDefinition(
			Long definitionId, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateDefinition(definitionId, value, valuePrese, lang, typeCode, complexity, isPublic);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateDefinitionOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	@Transactional
	public void updateDefinitionNote(Long definitionNoteId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(definitionNoteId);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.DEFINITION_NOTE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningRelationId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void updateMeaningLearnerComment(Long learnerCommentId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(learnerCommentId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.LEARNER_COMMENT, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningNote(Long meaningNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic, String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(meaningNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEANING_NOTE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningForum(Long meaningForumId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String userName = user.getName();

		cudDbService.updateMeaningForum(meaningForumId, value, valuePrese, userName);
	}

	@Transactional
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningSemanticType", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
	}

	@Transactional
	public void updateMeaningImage(Long imageId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(imageId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateImageTitle(Long imageId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateChildFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateMeaningMedia(Long mediaId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(mediaId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEDIA_FILE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateOdWordRecommendation(Long freeformId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.OD_WORD_RECOMMENDATION, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void updateLexemeWeight(Long lexemeId, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeWeight", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateMeaningRelationWeight(Long meaningRelationId, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationWeight", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningRelationWeight(meaningRelationId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void updateWordDataAndLexemeWeight(Long lexemeId, Long wordId, String wordValuePrese, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		updateWordValue(wordId, wordValuePrese, roleDatasetCode, isManualEventOnUpdateEnabled);
		updateLexemeWeight(lexemeId, lexemeWeight, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	private void updateFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long freeformId = freeform.getId();
		Long ownerId = activityLogService.getOwnerId(freeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, logOwner, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, activityEntity);
	}

	private void updateChildFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long parentId = freeform.getParentId();
		Long ownerId = activityLogService.getOwnerId(parentId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateChildFreeform", ownerId, logOwner, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, parentId, activityEntity);
	}

	// --- CREATE ---

	@Transactional
	public WordLexemeMeaningIdTuple createWord(WordLexemeMeaningDetails wordDetails, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

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
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createWordAndLexemeAndMeaning(value, value, valueAsWord, value, language, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		meaningId = wordLexemeMeaningId.getMeaningId();
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createWord", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWord", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		if (isMeaningCreate) {
			activityLogService.createActivityLog("createWord", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		}

		return wordLexemeMeaningId;
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void createWordTypeWithDuplication(Long wordId, String typeCode, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String roleDatasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, roleDatasetCode, wordId);
		if (isWordCrudGrant) {
			createWordType(wordId, typeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			createWordType(duplicateWordId, typeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void createWordForum(Long wordId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long userId = user.getId();
		String userName = user.getName();

		cudDbService.createWordForum(wordId, value, valuePrese, userId, userName);
	}

	@Transactional
	public void createUsageTranslation(Long usageId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_TRANSLATION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_TRANSLATION, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createUsageDefinition(Long usageId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(usageId);
		freeform.setType(FreeformType.USAGE_DEFINITION);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createUsageChildFreeform(ActivityEntity.USAGE_DEFINITION, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeTag(Long lexemeId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
		activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createMeaningTag(Long meaningId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningTag", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningTagId = cudDbService.createMeaningTag(meaningId, tagName);
		activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional
	public void createLexemeGovernment(Long lexemeId, String government, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GOVERNMENT);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, government);

		createLexemeFreeform(ActivityEntity.GOVERNMENT, lexemeId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeGrammar(Long lexemeId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.GRAMMAR);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.GRAMMAR, lexemeId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeNote(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createLexemeFreeform(ActivityEntity.LEXEME_NOTE, lexemeId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String oppositeRelationType, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId1, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long relationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.lexemeRelationExists(lexemeId2, lexemeId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId2, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long oppositeRelationId = cudDbService.createLexemeRelation(lexemeId2, lexemeId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, String oppositeRelationType, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId1, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long relationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.meaningRelationExists(meaningId2, meaningId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId2, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long oppositeRelationId = cudDbService.createMeaningRelation(meaningId2, meaningId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void createMeaningLearnerComment(Long meaningId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.LEARNER_COMMENT);
		freeform.setLang(lang);
		freeform.setComplexity(Complexity.SIMPLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.LEARNER_COMMENT, meaningId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningNote(Long meaningId, String valuePrese, String lang, Complexity complexity, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeform(ActivityEntity.MEANING_NOTE, meaningId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningForum(Long meaningId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long userId = user.getId();
		String userName = user.getName();

		cudDbService.createMeaningForum(meaningId, value, valuePrese, userId, userName);
	}

	@Transactional
	public void createMeaningSemanticType(Long meaningId, String semanticTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningSemanticType", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);

	}

	@Transactional
	public void createDefinitionNote(Long definitionId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.NOTE);
		freeform.setLang(lang);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createDefinitionFreeform(ActivityEntity.DEFINITION_NOTE, definitionId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningImage(Long meaningId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.IMAGE_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.IMAGE_FILE, meaningId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createImageTitle(Long imageFreeformId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageFreeformId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createMeaningFreeformChildFreeform(ActivityEntity.IMAGE_FILE, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createMeaningMedia(Long meaningId, String valuePrese, Complexity complexity, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.MEDIA_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.MEDIA_FILE, meaningId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public void createOdWordRecommendation(Long wordId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.OD_WORD_RECOMMENDATION);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(true);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		createWordFreeform(ActivityEntity.OD_WORD_RECOMMENDATION, wordId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	private void createWordFreeform(ActivityEntity activityEntity, Long wordId, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordFreeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, wordFreeformId, activityEntity);
	}

	private void createMeaningFreeform(ActivityEntity activityEntity, Long meaningId, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, meaningFreeformId, activityEntity);
	}

	private void createDefinitionFreeform(ActivityEntity activityEntity, Long definitionId, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long definitionFreeformId = cudDbService.createDefinitionFreeform(definitionId, freeform, userName);
		activityLogService.createActivityLog(activityLog, definitionFreeformId, activityEntity);
	}

	private void createMeaningFreeformChildFreeform(ActivityEntity activityEntity, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long meaningFreeformId = freeform.getParentId();
		Long meaningId = activityLogService.getOwnerId(meaningFreeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createChildFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long childFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, childFreeformId, activityEntity);
	}

	private void createUsageChildFreeform(ActivityEntity activityEntity, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = freeform.getParentId();
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageChildFreeform", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long usageChildFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, usageChildFreeformId, activityEntity);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog("deleteWord", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		SimpleWord word = lookupDbService.getSimpleWord(wordId);
		cudDbService.deleteWord(word);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteWordWordType(wordWordTypeId);
			activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		}
	}

	@Transactional
	public void deleteWordTypeWithDuplication(Long wordId, String typeCode, EkiUser user, boolean isManualEventOnUpdateEnabled) throws Exception {

		DatasetPermission userRole = user.getRecentRole();
		String roleDatasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = ekilexPermissionEvaluator.isWordCrudGranted(user, roleDatasetCode, wordId);
		if (isWordCrudGrant) {
			deleteWordType(wordId, typeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		} else {
			Long duplicateWordId = duplicateWordData(wordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			updateWordLexemesWordId(wordId, duplicateWordId, roleDatasetCode, isManualEventOnUpdateEnabled);
			deleteWordType(duplicateWordId, typeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordRelation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
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
	public void deleteWordForum(Long wordForumId) {

		cudDbService.deleteWordForum(wordForumId);
	}

	@Transactional
	public void deleteDefinition(Long definitionId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteDefinition(definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void deleteDefinitionNote(Long definitionNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(definitionNoteId);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = lookupDbService.getWordLexemeMeaningIdByLexeme(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		updateLexemeLevels(lexemeId, "delete", roleDatasetCode, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("deleteLexeme", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexeme(lexemeId);
		if (isOnlyLexemeForMeaning) {
			deleteMeaning(meaningId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
		if (isOnlyLexemeForWord) {
			deleteWord(wordId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteLexemeAndMeaningLexemes(Long lexemeId, String meaningLexemesLang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, roleDatasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			deleteLexeme(lexemeIdToDelete, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteUsage(Long usageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional
	public void deleteUsageTranslation(Long usageTranslationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageTranslation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageTranslationId);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional
	public void deleteUsageDefinition(Long usageDefinitionId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageDefinition", lexemeId, ActivityOwner.LEXEME,roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(usageDefinitionId);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional
	public void deleteLexemeGovernment(Long governmentId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGovernment", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional
	public void deleteLexemeGrammar(Long grammarId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGrammar", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemeNote(Long lexemeNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemePos(lexemePosId);
			activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		}
	}

	@Transactional
	public void deleteLexemeTag(Long lexemeId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeTag", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeTag(lexemeTagId);
			activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteMeaningTag(Long meaningId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long meaningTagId = lookupDbService.getMeaningTagId(meaningId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningTag", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningTag(meaningTagId);
			activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
			activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
			activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
			activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(relationId, ActivityEntity.LEXEME_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRelation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexemeRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean isSuperiorPermission = StringUtils.equals(DATASET_XXX, roleDatasetCode);
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds;
		if (isSuperiorPermission) {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIdsByMeaning(meaningId);
		} else {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIdsByMeaning(meaningId, roleDatasetCode);
		}
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional
	public void deleteMeaning(Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		activityLogService.createActivityLog("deleteMeaning", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningDomain(meaningDomainId);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
			activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		}
	}

	@Transactional
	public Response deleteMeaningRelation(Long relationId, Response response, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Locale locale = LocaleContextHolder.getLocale();
		List<Long> oppositeRelationIds = lookupDbService.getMeaningRelationOppositeRelationIds(relationId);
		if (oppositeRelationIds.size() == 1) {
			Long oppositeRelationId = oppositeRelationIds.get(0);
			Long oppositeMeaningId = activityLogService.getOwnerId(oppositeRelationId, ActivityEntity.MEANING_RELATION);
			activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", oppositeMeaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningRelation(oppositeRelationId);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
		} else if (oppositeRelationIds.size() > 1) {
			String message = messageSource.getMessage("delete.meaning.relation.multiple.opposite", new Object[0], locale);
			response.setMessage(message);
		}

		Long meaningId = activityLogService.getOwnerId(relationId, ActivityEntity.MEANING_RELATION);
		activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);

		return response;
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long learnerCommentId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningLearnerComment", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(learnerCommentId);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional
	public void deleteMeaningNote(Long meaningNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional
	public void deleteMeaningForum(Long meaningForumId) {

		cudDbService.deleteMeaningForum(meaningForumId);
	}

	@Transactional
	public void deleteImageTitle(Long imageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteImageTitle", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long imageTitleId = cudDbService.deleteImageTitle(imageId);
		activityLogService.createActivityLog(activityLog, imageTitleId, ActivityEntity.IMAGE_TITLE);
	}

	@Transactional
	public void deleteMeaningImage(Long imageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(imageId);
		activityLogService.createActivityLog(activityLog, imageId, ActivityEntity.IMAGE_FILE);
	}

	@Transactional
	public void deleteMeaningMedia(Long mediaId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(mediaId, ActivityEntity.MEDIA_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(mediaId);
		activityLogService.createActivityLog(activityLog, mediaId, ActivityEntity.MEDIA_FILE);
	}

	@Transactional
	public void deleteOdWordRecommendation(Long freeformId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdWordRecommendation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
	}

	@Transactional
	public void deleteParadigm(Long paradigmId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(paradigmId, ActivityEntity.PARADIGM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteParadigm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteParadigm(paradigmId);
		activityLogService.createActivityLog(activityLog, paradigmId, ActivityEntity.PARADIGM);
	}

}
