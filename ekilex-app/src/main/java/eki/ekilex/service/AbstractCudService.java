package eki.ekilex.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.TagDbService;

public abstract class AbstractCudService extends AbstractService {

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected CudDbService cudDbService;

	@Autowired
	protected LookupDbService lookupDbService;

	@Autowired
	protected TagDbService tagDbService;

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
	public WordLexemeMeaningIdTuple createLexeme(Long wordId, String datasetCode, Long meaningId, boolean isManualEventOnUpdateEnabled) throws Exception {

		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1);
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		if (lexemeId == null) {
			return wordLexemeMeaningId;
		}
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);

		return wordLexemeMeaningId;
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
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

	protected void createLexemeFreeform(ActivityEntity activityEntity, Long lexemeId, FreeForm freeform, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		Long lexemeFreeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, lexemeFreeformId, activityEntity);
	}

	protected void setFreeformValueTextAndValuePrese(FreeForm freeform, String valuePrese) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
	}
}
