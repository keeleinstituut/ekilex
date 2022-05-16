package eki.ekilex.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
	public void updateWordValue(Long wordId, String valuePrese) throws Exception {
		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValue", wordId, ActivityOwner.WORD);
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
	public void updateWordValueWithDuplication(Long wordId, String valuePrese, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordValue(wordId, valuePrese);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			updateWordValue(duplicateWordId, valuePrese);
		}
	}

	private Long duplicateWordData(Long wordId) throws Exception {

		SimpleWord simpleWord = compositionDbService.getSimpleWord(wordId);
		Long duplicateWordId = compositionDbService.cloneWord(simpleWord);
		compositionDbService.cloneWordParadigmsAndForms(wordId, duplicateWordId);
		compositionDbService.cloneWordTypes(wordId, duplicateWordId);
		compositionDbService.cloneWordRelations(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeforms(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymology(wordId, duplicateWordId);
		activityLogService.createActivityLog("duplicateWordData", duplicateWordId, ActivityOwner.WORD);

		return duplicateWordId;
	}

	private void updateWordLexemesWordId(Long currentWordId, Long newWordId, String datasetCode) throws Exception {
		ActivityLogData activityLog1 = activityLogService.prepareActivityLog("updateWordLexemesWordId", currentWordId, ActivityOwner.WORD);
		ActivityLogData activityLog2 = activityLogService.prepareActivityLog("updateWordLexemesWordId", newWordId, ActivityOwner.WORD);
		cudDbService.updateWordLexemesWordId(currentWordId, newWordId, datasetCode);
		activityLogService.createActivityLog(activityLog1, currentWordId, ActivityEntity.WORD);
		activityLogService.createActivityLog(activityLog2, newWordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordVocalForm(Long wordId, String vocalForm) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordVocalForm", wordId, ActivityOwner.WORD);
		cudDbService.updateWordVocalForm(wordId, vocalForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordType", wordId, ActivityOwner.WORD);
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void updateWordTypeWithDuplication(
			Long wordId, String currentTypeCode, String newTypeCode, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordType(wordId, currentTypeCode, newTypeCode);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			updateWordType(duplicateWordId, currentTypeCode, newTypeCode);
		}
	}

	@Transactional
	public void updateWordAspect(Long wordId, String aspectCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordAspect", wordId, ActivityOwner.WORD);
		cudDbService.updateWordAspect(wordId, aspectCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordDisplayMorph(Long wordId, String displayMorphCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordDisplayMorph", wordId, ActivityOwner.WORD);
		cudDbService.updateWordDisplayMorph(wordId, displayMorphCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordGender", wordId, ActivityOwner.WORD);
		cudDbService.updateWordGender(wordId, genderCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordGenderWithDuplication(Long wordId, String genderCode, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			updateWordGender(wordId, genderCode);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			updateWordGender(duplicateWordId, genderCode);
		}
	}

	@Transactional
	public void updateWordLang(Long wordId, String langCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLang", wordId, ActivityOwner.WORD);
		cudDbService.updateWordLang(wordId, langCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordNote(Long wordNoteId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(wordNoteId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.WORD_NOTE, freeform);
	}

	@Transactional
	public void updateWordManualUpdateOn(Long wordId, Timestamp manualUpdateOn) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordManualUpdateOn", wordId, ActivityOwner.WORD);
		cudDbService.updateWordManualUpdateOn(wordId, manualUpdateOn);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional
	public void updateWordLexemesTagComplete(Long wordId, String userRoleDatasetCode, Tag tag) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLexemesTagComplete", wordId, ActivityOwner.WORD);

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
	public void updateMeaningLexemesTagComplete(Long meaningId, String userRoleDatasetCode, Tag tag) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningLexemesTagComplete", meaningId, ActivityOwner.MEANING);

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
	public void updateWordRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long wordRelationId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRelationOrdering", wordId, ActivityOwner.WORD);
			cudDbService.updateWordRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long wordEtymId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordEtymologyOrdering", wordId, ActivityOwner.WORD);
			cudDbService.updateWordEtymologyOrderby(item);
			activityLogService.createActivityLog(activityLog, wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
		}
	}

	@Transactional
	public void updateMeaningDomainOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long meaningDomainId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningDomainId, ActivityEntity.DOMAIN);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomainOrdering", meaningId, ActivityOwner.MEANING);
			cudDbService.updateMeaningDomainOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void updateGovernmentOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			Long governmentId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateGovernmentOrdering", lexemeId, ActivityOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
		}
	}

	@Transactional
	public void updateUsageOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			Long usageId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageOrdering", lexemeId, ActivityOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}

	@Transactional
	public void updateLexemeNoteOrdering(List<ListData> items) throws Exception {

		for (ListData item : items) {
			Long lexemeNoteId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteOrdering", lexemeId, ActivityOwner.LEXEME);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		}
	}

	@Transactional
	public void updateMeaningNoteOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long meaningNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteOrdering", meaningId, ActivityOwner.MEANING);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
		}
	}

	@Transactional
	public void updateDefinitionNoteOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteOrdering", meaningId, ActivityOwner.MEANING);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION_NOTE);
		}
	}

	@Transactional
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeMeaningWordOrdering", lexemeId, ActivityOwner.LEXEME);
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

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE, freeform);
	}

	@Transactional
	public void updateUsageTranslation(Long usageTranslationId, String valuePrese, String lang) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageTranslationId);
		freeform.setLang(lang);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_TRANSLATION, freeform);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long usageDefinitionId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(usageDefinitionId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long lexemeId = item.getId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeOrdering", lexemeId, ActivityOwner.LEXEME);
			cudDbService.updateLexemeOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, int lexemePosition) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, lexemePosition);
		for (WordLexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME);
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

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GOVERNMENT, freeform);
	}

	@Transactional
	public void updateLexemeGrammar(Long lexemeGrammarId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeGrammarId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.GRAMMAR, freeform);
	}

	@Transactional
	public void updateLexemeComplexity(Long lexemeId, String complexity) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeComplexity", lexemeId, ActivityOwner.LEXEME);
		cudDbService.updateLexemeComplexity(lexemeId, complexity);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePos", lexemeId, ActivityOwner.LEXEME);
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeDeriv", lexemeId, ActivityOwner.LEXEME);
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		activityLogService.createActivityLog(activityLog, lexemeDerivid, ActivityEntity.DERIV);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegister", lexemeId, ActivityOwner.LEXEME);
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegion", lexemeId, ActivityOwner.LEXEME);
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional
	public void updateLexemeReliability(Long lexemeId, String reliabilityStr) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeReliability", lexemeId, ActivityOwner.LEXEME);
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
	public void updateLexemeNote(Long lexemeNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(lexemeNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.LEXEME_NOTE, freeform);
	}

	@Transactional
	public void updateLexemePublicity(Long lexemeId, boolean isPublic) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePublicity", lexemeId, ActivityOwner.LEXEME);
		cudDbService.updateLexemeProcessState(lexemeId, isPublic);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeValueState", lexemeId, ActivityOwner.LEXEME);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeProficiencyLevel(Long lexemeId, String proficiencyLevelCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeProficiencyLevel", lexemeId, ActivityOwner.LEXEME);
		cudDbService.updateLexemeProficiencyLevel(lexemeId, proficiencyLevelCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long lexemeRelationId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeRelationId, ActivityEntity.LEXEME_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRelationOrdering", lexemeId, ActivityOwner.LEXEME);
			cudDbService.updateLexemeRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void updateDefinition(Long definitionId, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic) throws Exception {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING);
		cudDbService.updateDefinition(definitionId, value, valuePrese, lang, complexity, typeCode, isPublic);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionOrdering", meaningId, ActivityOwner.MEANING);
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

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.DEFINITION_NOTE, freeform);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) throws Exception {
		for (ListData item : items) {
			Long meaningRelationId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationOrdering", meaningId, ActivityOwner.MEANING);
			cudDbService.updateMeaningRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomain", meaningId, ActivityOwner.MEANING);
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void updateMeaningLearnerComment(Long learnerCommentId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(learnerCommentId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.LEARNER_COMMENT, freeform);
	}

	@Transactional
	public void updateMeaningNote(Long meaningNoteId, String valuePrese, String lang, Complexity complexity, boolean isPublic) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(meaningNoteId);
		freeform.setLang(lang);
		freeform.setComplexity(complexity);
		freeform.setPublic(isPublic);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEANING_NOTE, freeform);
	}

	@Transactional
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningSemanticType", meaningId, ActivityOwner.MEANING);
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
	}

	@Transactional
	public void updateMeaningManualUpdateOn(Long meaningId, Timestamp manualUpdateOn) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningManualUpdateOn", meaningId, ActivityOwner.MEANING);
		cudDbService.updateMeaningManualUpdateOn(meaningId, manualUpdateOn);
		activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
	}

	@Transactional
	public void updateMeaningImage(Long imageId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(imageId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform);
	}

	@Transactional
	public void updateImageTitle(Long imageId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setParentId(imageId);
		freeform.setType(FreeformType.IMAGE_TITLE);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateChildFreeform(ActivityOwner.MEANING, ActivityEntity.IMAGE_FILE, freeform);
	}

	@Transactional
	public void updateMeaningMedia(Long mediaId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(mediaId);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.MEDIA_FILE, freeform);
	}

	@Transactional
	public void updateOdWordRecommendation(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.OD_WORD_RECOMMENDATION, freeform);
	}

	@Transactional
	public void updateOdLexemeRecommendation(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.OD_LEXEME_RECOMMENDATION, freeform);
	}

	@Transactional
	public void updateOdUsageDefinition(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.OD_USAGE_DEFINITION, freeform);
	}

	@Transactional
	public void updateOdUsageAlternative(Long freeformId, String valuePrese) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setId(freeformId);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.OD_USAGE_ALTERNATIVE, freeform);
	}

	@Transactional
	public void updateLexemeWeight(Long lexemeId, String lexemeWeightStr) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeWeight", lexemeId, ActivityOwner.LEXEME);
		BigDecimal lexemeWeight = new BigDecimal(lexemeWeightStr);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional
	public void updateMeaningRelationWeight(Long meaningRelationId, String relationWeightStr) throws Exception {
		Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationWeight", meaningId, ActivityOwner.MEANING);
		BigDecimal relationWeight = new BigDecimal(relationWeightStr);
		cudDbService.updateMeaningRelationWeight(meaningRelationId, relationWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
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

	private void updateFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long freeformId = freeform.getId();
		Long ownerId = activityLogService.getOwnerId(freeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, logOwner);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, activityEntity);
	}

	private void updateChildFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long parentId = freeform.getParentId();
		Long ownerId = activityLogService.getOwnerId(parentId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateChildFreeform", ownerId, logOwner);
		cudDbService.updateChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, parentId, activityEntity);
	}

	// --- CREATE ---

	@Transactional
	public WordLexemeMeaningIdTuple createWord(WordLexemeMeaningDetails wordDetails) throws Exception {

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
				.createWordAndLexeme(value, value, valueAsWord, language, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		meaningId = wordLexemeMeaningId.getMeaningId();
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createWord", wordId, ActivityOwner.WORD);
		activityLogService.createActivityLog("createWord", lexemeId, ActivityOwner.LEXEME);
		if (isMeaningCreate) {
			activityLogService.createActivityLog("createWord", meaningId, ActivityOwner.MEANING);
		}

		return wordLexemeMeaningId;
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordType", wordId, ActivityOwner.WORD);
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional
	public void createWordTypeWithDuplication(Long wordId, String typeCode, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			createWordType(wordId, typeCode);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			createWordType(duplicateWordId, typeCode);
		}
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode) throws Exception {
		ActivityLogData activityLog;
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD);
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
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD);
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordRelation", targetWordId, ActivityOwner.WORD);
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.WORD_RELATION);
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
	public void createWordNoteWithDuplication(Long wordId, String valuePrese, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			createWordNote(wordId, valuePrese);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			createWordNote(duplicateWordId, valuePrese);
		}
	}

	@Transactional
	public void createLexeme(Long wordId, String datasetCode, Long meaningId) throws Exception {
		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, ActivityOwner.WORD);
		Long lexemeId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1);
		if (lexemeId == null) {
			return;
		}
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
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
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, ActivityOwner.LEXEME);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public void createLexemeTag(Long lexemeId, String tagName) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, ActivityOwner.LEXEME);
		Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
		activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createMeaningTag(Long meaningId, String tagName) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningTag", meaningId, ActivityOwner.MEANING);
		Long meaningTagId = cudDbService.createMeaningTag(meaningId, tagName);
		activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeDeriv", lexemeId, ActivityOwner.LEXEME);
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegister", lexemeId, ActivityOwner.LEXEME);
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegion", lexemeId, ActivityOwner.LEXEME);
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
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
		activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId1, ActivityOwner.LEXEME);
		Long relationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.lexemeRelationExists(lexemeId2, lexemeId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createLexemeRelation", lexemeId2, ActivityOwner.LEXEME);
			Long oppositeRelationId = cudDbService.createLexemeRelation(lexemeId2, lexemeId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain) throws Exception {
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, ActivityOwner.MEANING);
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, String oppositeRelationType) throws Exception {
		ActivityLogData activityLog;
		activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId1, ActivityOwner.MEANING);
		Long relationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.meaningRelationExists(meaningId2, meaningId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			activityLog = activityLogService.prepareActivityLog("createMeaningRelation", meaningId2, ActivityOwner.MEANING);
			Long oppositeRelationId = cudDbService.createMeaningRelation(meaningId2, meaningId1, oppositeRelationType);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
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
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningSemanticType", meaningId, ActivityOwner.MEANING);
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);

	}

	@Transactional
	public void createDefinition(
			Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity, String typeCode, boolean isPublic) throws Exception {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, ActivityOwner.MEANING);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, complexity, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
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
	public void createMeaningImage(Long meaningId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.IMAGE_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.IMAGE_FILE, meaningId, freeform);
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
	public void createMeaningMedia(Long meaningId, String valuePrese, Complexity complexity) throws Exception {

		FreeForm freeform = new FreeForm();
		freeform.setType(FreeformType.MEDIA_FILE);
		freeform.setComplexity(complexity);
		setFreeformValueTextAndValuePrese(freeform, valuePrese);
		createMeaningFreeform(ActivityEntity.MEDIA_FILE, meaningId, freeform);
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
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, valuePrese, valueAsWord, language, datasetCode, PUBLICITY_PRIVATE, null);
		Long createdWordId = wordLexemeMeaningId.getWordId();
		Long createdLexemeId = wordLexemeMeaningId.getLexemeId();
		tagDbService.createLexemeAutomaticTags(createdLexemeId);

		activityLogService.createActivityLog("createWordAndSynRelation", createdWordId, ActivityOwner.WORD);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordAndSynRelation", existingWordId, ActivityOwner.WORD);
		Long createdRelationId = cudDbService.createWordRelation(existingWordId, createdWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(existingWordId, createdRelationId, RAW_RELATION_TYPE);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	@Transactional
	public void createSynWordRelation(Long targetWordId, Long sourceWordId, String weightStr, String datasetCode) throws Exception {

		boolean word2DatasetLexemeExists = lookupDbService.wordLexemeExists(sourceWordId, datasetCode);
		if (!word2DatasetLexemeExists) {
			createLexeme(sourceWordId, datasetCode, null);
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createSynWordRelation", targetWordId, ActivityOwner.WORD);
		Long createdRelationId = cudDbService.createWordRelation(targetWordId, sourceWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(targetWordId, createdRelationId, RAW_RELATION_TYPE);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
		activityLogService.createActivityLog(activityLog, createdRelationId, ActivityEntity.WORD_RELATION);
	}

	private void createWordFreeform(ActivityEntity activityEntity, Long wordId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, ActivityOwner.WORD);
		Long wordFreeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, wordFreeformId, activityEntity);
	}

	private void createLexemeFreeform(ActivityEntity activityEntity, Long lexemeId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME);
		Long lexemeFreeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, lexemeFreeformId, activityEntity);
	}

	private void createMeaningFreeform(ActivityEntity activityEntity, Long meaningId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING);
		Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, meaningFreeformId, activityEntity);
	}

	private void createDefinitionFreeform(ActivityEntity activityEntity, Long definitionId, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionFreeform", meaningId, ActivityOwner.MEANING);
		Long definitionFreeformId = cudDbService.createDefinitionFreeform(definitionId, freeform, userName);
		activityLogService.createActivityLog(activityLog, definitionFreeformId, activityEntity);
	}

	private void createMeaningFreeformChildFreeform(ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long meaningFreeformId = freeform.getParentId();
		Long meaningId = activityLogService.getOwnerId(meaningFreeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createChildFreeform", meaningId, ActivityOwner.MEANING);
		Long childFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, childFreeformId, activityEntity);
	}

	private void createUsageChildFreeform(ActivityEntity activityEntity, FreeForm freeform) throws Exception {

		String userName = userContext.getUserName();
		Long usageId = freeform.getParentId();
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageChildFreeform", lexemeId, ActivityOwner.LEXEME);
		Long usageChildFreeformId = cudDbService.createChildFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, usageChildFreeformId, activityEntity);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId) throws Exception {
		activityLogService.createActivityLog("deleteWord", wordId, ActivityOwner.WORD);
		SimpleWord word = lookupDbService.getSimpleWord(wordId);
		cudDbService.deleteWord(word);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) throws Exception {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordType", wordId, ActivityOwner.WORD);
			cudDbService.deleteWordWordType(wordWordTypeId);
			activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		}
	}

	@Transactional
	public void deleteWordTypeWithDuplication(Long wordId, String typeCode, Long userId, DatasetPermission userRole) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		boolean isWordCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		if (isWordCrudGrant) {
			deleteWordType(wordId, typeCode);
		} else {
			Long duplicateWordId = duplicateWordData(wordId);
			updateWordLexemesWordId(wordId, duplicateWordId, datasetCode);
			deleteWordType(duplicateWordId, typeCode);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) throws Exception {
		Long wordId = activityLogService.getOwnerId(relationId, ActivityEntity.WORD_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordRelation", wordId, ActivityOwner.WORD);
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
	public void deleteWordNote(Long wordNoteId) throws Exception {
		Long wordId = activityLogService.getOwnerId(wordNoteId, ActivityEntity.WORD_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordNote", wordId, ActivityOwner.WORD);
		cudDbService.deleteFreeform(wordNoteId);
		activityLogService.createActivityLog(activityLog, wordNoteId, ActivityEntity.WORD_NOTE);
	}

	@Transactional
	public void deleteDefinition(Long definitionId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinition", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteDefinition(definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional
	public void deleteDefinitionNote(Long definitionNoteId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNote", meaningId, ActivityOwner.MEANING);
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
		updateLexemeLevels(lexemeId, "delete");
		activityLogService.createActivityLog("deleteLexeme", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteLexeme(lexemeId);
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
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsage", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional
	public void deleteUsageTranslation(Long usageTranslationId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageTranslation", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(usageTranslationId);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional
	public void deleteUsageDefinition(Long usageDefinitionId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageDefinition", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(usageDefinitionId);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional
	public void deleteLexemeGovernment(Long governmentId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGovernment", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional
	public void deleteLexemeGrammar(Long grammarId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGrammar", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemeNote(Long lexemeNoteId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNote", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.GRAMMAR);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) throws Exception {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemePos", lexemeId, ActivityOwner.LEXEME);
			cudDbService.deleteLexemePos(lexemePosId);
			activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		}
	}

	@Transactional
	public void deleteLexemeTag(Long lexemeId, String tagName) throws Exception {
		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeTag", lexemeId, ActivityOwner.LEXEME);
			cudDbService.deleteLexemeTag(lexemeTagId);
			activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteMeaningTag(Long meaningId, String tagName) throws Exception {
		if (StringUtils.isNotBlank(tagName)) {
			Long meaningTagId = lookupDbService.getMeaningTagId(meaningId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningTag", meaningId, ActivityOwner.MEANING);
			cudDbService.deleteMeaningTag(meaningTagId);
			activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) throws Exception {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeDeriv", lexemeId, ActivityOwner.LEXEME);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
			activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) throws Exception {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegister", lexemeId, ActivityOwner.LEXEME);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
			activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode) throws Exception {
		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegion", lexemeId, ActivityOwner.LEXEME);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
			activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(relationId, ActivityEntity.LEXEME_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRelation", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteLexemeRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId, String datasetCode) throws Exception {
		boolean isSuperiorPermission = StringUtils.equals(DATASET_XXX, datasetCode);
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds;
		if (isSuperiorPermission) {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId);
		} else {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		}
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId);
		}
	}

	@Transactional
	public void deleteMeaning(Long meaningId) throws Exception {
		activityLogService.createActivityLog("deleteMeaning", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain) throws Exception {
		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING);
			cudDbService.deleteMeaningDomain(meaningDomainId);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode) throws Exception {
		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
			activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		}
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(relationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningRelation", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteMeaningRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void deleteSynMeaningRelation(Long relationId) throws Exception {
		ActivityLogData activityLog;
		Long oppositeRelationId = lookupDbService.getMeaningRelationSameTypeOppositeRelationId(relationId);
		if (oppositeRelationId != null) {
			Long oppositeMeaningId = activityLogService.getOwnerId(oppositeRelationId, ActivityEntity.MEANING_RELATION);
			activityLog = activityLogService.prepareActivityLog("deleteSynMeaningRelation", oppositeMeaningId, ActivityOwner.MEANING);
			cudDbService.deleteMeaningRelation(oppositeRelationId);
			activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.MEANING_RELATION);
		}

		Long meaningId = activityLogService.getOwnerId(relationId, ActivityEntity.MEANING_RELATION);
		activityLog = activityLogService.prepareActivityLog("deleteSynMeaningRelation", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteMeaningRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long learnerCommentId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningLearnerComment", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteFreeform(learnerCommentId);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional
	public void deleteMeaningNote(Long meaningNoteId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNote", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteFreeform(meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional
	public void deleteImageTitle(Long imageId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteImageTitle", meaningId, ActivityOwner.MEANING);
		Long imageTitleId = cudDbService.deleteImageTitle(imageId);
		activityLogService.createActivityLog(activityLog, imageTitleId, ActivityEntity.IMAGE_TITLE);
	}

	@Transactional
	public void deleteMeaningImage(Long imageId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(imageId, ActivityEntity.IMAGE_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteFreeform(imageId);
		activityLogService.createActivityLog(activityLog, imageId, ActivityEntity.IMAGE_FILE);
	}

	@Transactional
	public void deleteMeaningMedia(Long mediaId) throws Exception {
		Long meaningId = activityLogService.getOwnerId(mediaId, ActivityEntity.MEDIA_FILE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningMedia", meaningId, ActivityOwner.MEANING);
		cudDbService.deleteFreeform(mediaId);
		activityLogService.createActivityLog(activityLog, mediaId, ActivityEntity.MEDIA_FILE);
	}

	@Transactional
	public void deleteOdWordRecommendation(Long freeformId) throws Exception {
		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdWordRecommendation", wordId, ActivityOwner.WORD);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_WORD_RECOMMENDATION);
	}

	@Transactional
	public void deleteOdLexemeRecommendation(Long freeformId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_LEXEME_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdLexemeRecommendation", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_LEXEME_RECOMMENDATION);
	}

	@Transactional
	public void deleteOdUsageDefinition(Long freeformId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdUsageDefinition", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_USAGE_DEFINITION);
	}

	@Transactional
	public void deleteOdUsageAlternative(Long freeformId) throws Exception {
		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.OD_USAGE_ALTERNATIVE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteOdUsageAlternative", lexemeId, ActivityOwner.LEXEME);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.OD_USAGE_ALTERNATIVE);
	}

	@Transactional
	public void deleteParadigm(Long paradigmId) throws Exception {
		Long wordId = activityLogService.getOwnerId(paradigmId, ActivityEntity.PARADIGM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteParadigm", wordId, ActivityOwner.WORD);
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
