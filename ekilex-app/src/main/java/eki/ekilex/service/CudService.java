package eki.ekilex.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import eki.common.constant.FreeformConstant;
import eki.common.constant.PermConstant;
import eki.common.data.AsWordResult;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.CollocMemberOrder;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Freeform;
import eki.ekilex.data.Government;
import eki.ekilex.data.Grammar;
import eki.ekilex.data.LearnerComment;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.ListData;
import eki.ekilex.data.MeaningImage;
import eki.ekilex.data.MeaningMedia;
import eki.ekilex.data.Note;
import eki.ekilex.data.Response;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageDefinition;
import eki.ekilex.data.UsageTranslation;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordOsMorph;
import eki.ekilex.data.WordOsRecommendation;
import eki.ekilex.data.WordOsUsage;
import eki.ekilex.security.EkilexPermissionEvaluator;
import eki.ekilex.service.db.CompositionDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
@Component
public class CudService extends AbstractCudService implements PermConstant, ActivityFunct, FreeformConstant {

	@Autowired
	private CompositionDbService compositionDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Autowired
	private EkilexPermissionEvaluator ekilexPermissionEvaluator;

	// --- CREATE ---

	@Transactional(rollbackOn = Exception.class)
	public WordLexemeMeaningIdTuple createWord(WordLexemeMeaningDetails wordDetails, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = wordDetails.getWordValue();
		String language = wordDetails.getLanguage();
		String dataset = wordDetails.getDataset();
		Long meaningId = wordDetails.getMeaningId();
		boolean isMeaningCreate = meaningId == null;

		value = textDecorationService.removeEkiElementMarkup(value);
		String valueAsWord = textDecorationService.getValueAsWord(value);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createWordAndLexemeAndMeaning(value, value, valueAsWord, value, language, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		meaningId = wordLexemeMeaningId.getMeaningId();
		tagDbService.createLexemeAutomaticTags(lexemeId);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME, lexemeId);
		activityLogService.createActivityLog("createWord", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWord", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		if (isMeaningCreate) {
			activityLogService.createActivityLog("createWord", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		}

		return wordLexemeMeaningId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordType(Long wordId, String typeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void createWordTag(Long wordId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordTag", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordTagId = cudDbService.createWordTag(wordId, tagName);
		activityLogService.createActivityLog(activityLog, wordTagId, ActivityEntity.WORD_TAG);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordForum(Long wordId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long userId = user.getId();
		String userName = user.getName();

		cudDbService.createWordForum(wordId, value, valuePrese, userId, userName);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordFreeform(Long wordId, String valuePrese, String freeformTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setFreeformTypeCode(freeformTypeCode);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		createWordFreeform(ActivityEntity.FREEFORM, wordId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createUsageTranslation(Long usageId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		UsageTranslation usageTranslation = new UsageTranslation();
		usageTranslation.setValuePrese(valuePrese);
		usageTranslation.setLang(lang);
		setValueAndPrese(usageTranslation);
		applyCreateUpdate(usageTranslation);

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageTranslation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long usageTranslationId = cudDbService.createUsageTranslation(usageId, usageTranslation);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createUsageDefinition(Long usageId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		UsageDefinition usageDefinition = new UsageDefinition();
		usageDefinition.setValuePrese(valuePrese);
		usageDefinition.setLang(lang);
		setValueAndPrese(usageDefinition);
		applyCreateUpdate(usageDefinition);

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageDefinition", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long usageDefinitionId = cudDbService.createUsageDefinition(usageId, usageDefinition);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeTag(Long lexemeId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
		activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningTag(Long meaningId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningTag", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningTagId = cudDbService.createMeaningTag(meaningId, tagName);
		activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeDeriv(Long lexemeId, String derivCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeRegister(Long lexemeId, String registerCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeRegion(Long lexemeId, String regionCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeGovernment(Long lexemeId, String value, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Government government = new Government();
		government.setValue(value);

		createLexemeGovernment(lexemeId, government, user, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeGovernment(Long lexemeId, Government government, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		applyCreateUpdate(government);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeGovernment", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long governmentId = cudDbService.createGovernment(lexemeId, government);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_GOVERNMENT, governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);

		return governmentId;
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeGrammar(Long lexemeId, String valuePrese, String lang, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Grammar usage = new Grammar();
		usage.setValuePrese(valuePrese);
		usage.setLang(lang);

		return createLexemeGrammar(lexemeId, usage, user, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createLexemeGrammar(Long lexemeId, Grammar grammar, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(grammar);
		applyCreateUpdate(grammar);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeGrammar", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long grammarId = cudDbService.createGrammar(lexemeId, grammar);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_GRAMMAR, grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);

		return grammarId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeNote(Long lexemeId, String valuePrese, String lang, boolean isPublic, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyCreateUpdate(note);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeNoteId = cudDbService.createLexemeNote(lexemeId, note);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_LEXEME_NOTE, lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createLexemeFreeform(Long lexemeId, String valuePrese, String freeformTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setFreeformTypeCode(freeformTypeCode);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long freeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningDomain(Long meaningId, Classifier domain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningLearnerComment(Long meaningId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LearnerComment learnerComment = new LearnerComment();
		learnerComment.setValuePrese(valuePrese);

		return createMeaningLearnerComment(meaningId, learnerComment, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long createMeaningLearnerComment(Long meaningId, LearnerComment learnerComment, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(learnerComment);
		applyCreateUpdate(learnerComment);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeGrammar", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long learnerCommentId = cudDbService.createLearnerComment(meaningId, learnerComment);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);

		return learnerCommentId;
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningNote(Long meaningId, String valuePrese, String lang, boolean isPublic, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyCreateUpdate(note);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningNoteId = cudDbService.createMeaningNote(meaningId, note);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_MEANING_NOTE, meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningForum(Long meaningId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long userId = user.getId();
		String userName = user.getName();
		cudDbService.createMeaningForum(meaningId, value, valuePrese, userId, userName);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningSemanticType(Long meaningId, String semanticTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningSemanticType", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);

	}

	@Transactional(rollbackOn = Exception.class)
	public void createDefinitionNote(Long definitionId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyCreateUpdate(note);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long definitionNoteId = cudDbService.createDefinitionNote(definitionId, note);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningImage(Long meaningId, String url, String title, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		MeaningImage meaningImage = new MeaningImage();
		meaningImage.setTitle(title);
		meaningImage.setUrl(url);
		meaningImage.setPublic(PUBLICITY_PUBLIC);
		applyCreateUpdate(meaningImage);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningImageId = cudDbService.createMeaningImage(meaningId, meaningImage);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_IMAGE, meaningImageId);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningMedia(Long meaningId, String url, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		MeaningMedia meaningMedia = new MeaningMedia();
		meaningMedia.setUrl(url);
		applyCreateUpdate(meaningMedia);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningMediaId = cudDbService.createMeaningMedia(meaningId, meaningMedia);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_LITE, ENTITY_NAME_MEANING_MEDIA, meaningMediaId);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createMeaningFreeform(Long meaningId, String valuePrese, String freeformTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setFreeformTypeCode(freeformTypeCode);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyCreateUpdate(freeform);

		createMeaningFreeform(ActivityEntity.FREEFORM, meaningId, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordOsMorph(Long wordId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsMorph wordOsMorph = new WordOsMorph();
		wordOsMorph.setValuePrese(valuePrese);
		wordOsMorph.setPublic(true);
		setValueAndPrese(wordOsMorph);
		applyCreateUpdate(wordOsMorph);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordOsMorph", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordOsMorphId = cudDbService.createWordOsMorph(wordId, wordOsMorph);
		activityLogService.createActivityLog(activityLog, wordOsMorphId, ActivityEntity.WORD_OS_MORPH);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordOsRecommendation(Long wordId, String valuePrese, EkiUser user, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsRecommendation wordOsRecommendation = new WordOsRecommendation();
		wordOsRecommendation.setValuePrese(valuePrese);
		wordOsRecommendation.setPublic(true);
		setValueAndPrese(wordOsRecommendation);
		applyCreateUpdate(wordOsRecommendation);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordOsRecommendation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordOsRecommendationId = cudDbService.createWordOsRecommendation(wordId, wordOsRecommendation);
		createPublishing(user, roleDatasetCode, TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_OS_RECOMMENDATION, wordOsRecommendationId);
		activityLogService.createActivityLog(activityLog, wordOsRecommendationId, ActivityEntity.WORD_OS_RECOMMENDATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void createWordOsUsage(Long wordId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsUsage wordOsUsage = new WordOsUsage();
		wordOsUsage.setValuePrese(valuePrese);
		wordOsUsage.setPublic(true);
		setValueAndPrese(wordOsUsage);
		applyCreateUpdate(wordOsUsage);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordOsUsage", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordOsUsageId = cudDbService.createWordOsUsage(wordId, wordOsUsage);
		activityLogService.createActivityLog(activityLog, wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
	}

	private void createWordFreeform(ActivityEntity activityEntity, Long wordId, Freeform freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createWordFreeform", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordFreeformId = cudDbService.createWordFreeform(wordId, freeform, userName);
		activityLogService.createActivityLog(activityLog, wordFreeformId, activityEntity);
	}

	private void createMeaningFreeform(ActivityEntity activityEntity, Long meaningId, Freeform freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
		activityLogService.createActivityLog(activityLog, meaningFreeformId, activityEntity);
	}

	// --- UPDATE ---

	@Transactional(rollbackOn = Exception.class)
	public void updateWordValue(Long wordId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		AsWordResult asWordResult = textDecorationService.getAsWordResult(value);
		String valueAsWord = asWordResult.getValueAsWord();
		boolean valueAsWordExists = asWordResult.isValueAsWordExists();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordValue", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordValue(wordId, value, valuePrese);
		if (valueAsWordExists) {
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
		compositionDbService.cloneWordRelationsAndSubdata(wordId, duplicateWordId);
		compositionDbService.cloneWordGroupMembers(wordId, duplicateWordId);
		compositionDbService.cloneWordFreeformsAndSubdata(wordId, duplicateWordId);
		compositionDbService.cloneWordEtymologyAndSubdata(wordId, duplicateWordId);
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

	@Transactional(rollbackOn = Exception.class)
	public void updateWordVocalForm(Long wordId, String vocalForm, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordVocalForm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordVocalForm(wordId, vocalForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordMorphophonoForm(Long wordId, String morphophonoForm, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordMorphophonoForm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordMorphophonoForm(wordId, morphophonoForm);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void updateWordAspect(Long wordId, String aspectCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordAspect", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordAspect(wordId, aspectCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordDisplayMorph(Long wordId, String displayMorphCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordDisplayMorph", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordDisplayMorph(wordId, displayMorphCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordGender(Long wordId, String genderCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordGender", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordGender(wordId, genderCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void updateWordRegYear(Long wordId, String regYearStr, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Integer regYear = null;
		if (StringUtils.isNotBlank(regYearStr)) {
			regYear = Integer.valueOf(regYearStr);
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRegYear", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordRegYear(wordId, regYear);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordLang(Long wordId, String langCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordLang", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordLang(wordId, langCode);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordForum(Long wordForumId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String userName = user.getName();

		cudDbService.updateWordForum(wordForumId, value, valuePrese, userName);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordFreeform(Long freeformId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setId(freeformId);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyUpdate(freeform);

		updateFreeform(ActivityOwner.WORD, ActivityEntity.FREEFORM, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordMorphComment(Long wordId, String value, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordMorphComment", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordMorphComment(wordId, value);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordManualEventOn(Long wordId, String eventOnStr, String roleDatasetCode) throws Exception {

		LocalDateTime eventOn = conversionUtil.dateStrToDateTime(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordManualEventOn", wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateWordManualEventOn(wordId, eventOn);
		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void updateWordRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordRelationId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordRelationId, ActivityEntity.WORD_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordRelationOrdering", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordEtymologyOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordEtymId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordEtymologyOrdering", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordEtymologyOrderby(item);
			activityLogService.createActivityLog(activityLog, wordEtymId, ActivityEntity.WORD_ETYMOLOGY);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordOsUsageOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long wordOsUsageId = item.getId();
			Long wordId = activityLogService.getOwnerId(wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordOsUsageOrdering", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateWordOsUsageOrderby(item);
			activityLogService.createActivityLog(activityLog, wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningManualEventOn(Long meaningId, String eventOnStr, String roleDatasetCode) throws Exception {

		LocalDateTime eventOn = conversionUtil.dateStrToDateTime(eventOnStr);
		ActivityLogData activityLog = activityLogService.prepareActivityLog(UPDATE_MEANING_MANUAL_EVENT_ON_FUNCT, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		activityLogDbService.updateMeaningManualEventOn(meaningId, eventOn);
		activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningDomainOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningDomainId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningDomainId, ActivityEntity.DOMAIN);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomainOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningDomainOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateGovernmentOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long governmentId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateGovernmentOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsageOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long usageId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateUsageOrderby(item);
			activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeFreeformOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long freeformId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeFreeformOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningFreeformOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long freeformId = item.getId();
			Long meaningId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningFreeformOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateFreeformOrderby(item);
			activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeNoteId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeNoteOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningNoteOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinitionNoteOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionNoteId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNoteOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateDefinitionNoteOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeMeaningWordOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		for (ListData item : items) {
			cudDbService.updateLexemeOrderby(item);
		}
		activityLogService.createActivityLogUnknownEntity(activityLog, ActivityEntity.MEANING_WORD);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateCollocMemberGroupOrder(Long collocLexemeId, Long memberLexemeId, String direction, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateCollocMemberGroupOrder", memberLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		List<CollocMemberOrder> collocMembers = lookupDbService.getCollocMemberOrdersOfRelGroup(collocLexemeId, memberLexemeId);
		int collocMemberCount = collocMembers.size();
		CollocMemberOrder sourceCollocMember = collocMembers.stream()
				.filter(collocMember -> collocMember.getCollocLexemeId().equals(collocLexemeId))
				.findFirst()
				.get();
		int sourceCollocMemberIndex = collocMembers.indexOf(sourceCollocMember);
		if (sourceCollocMemberIndex < 0) {
			return;
		}
		CollocMemberOrder targetCollocMember = null;
		if (StringUtils.equalsIgnoreCase(direction, "up") && (sourceCollocMemberIndex > 0)) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex - 1);
		} else if (StringUtils.equalsIgnoreCase(direction, "down") && (sourceCollocMemberIndex < (collocMemberCount - 1))) {
			targetCollocMember = collocMembers.get(sourceCollocMemberIndex + 1);
		}
		if (targetCollocMember != null) {
			Long sourceCollocMemberId = sourceCollocMember.getId();
			Integer sourceCollocGroupOrder = sourceCollocMember.getGroupOrder();
			Long targetCollocMemberId = targetCollocMember.getId();
			Integer targetCollocGroupOrder = targetCollocMember.getGroupOrder();
			cudDbService.updateLexemeCollocMemberGroupOrder(sourceCollocMemberId, targetCollocGroupOrder);
			cudDbService.updateLexemeCollocMemberGroupOrder(targetCollocMemberId, sourceCollocGroupOrder);
		}
		activityLogService.createActivityLog(activityLog, memberLexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsage(Long usageId, String valuePrese, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Usage usage = new Usage();
		usage.setId(usageId);
		usage.setValuePrese(valuePrese);
		usage.setPublic(isPublic);

		updateUsage(usage, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsage(Usage usage, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(usage);
		applyUpdate(usage);

		Long usageId = usage.getId();
		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateUsage(usage);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsageTranslation(Long usageTranslationId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		UsageTranslation usageTranslation = new UsageTranslation();
		usageTranslation.setId(usageTranslationId);
		usageTranslation.setValuePrese(valuePrese);
		usageTranslation.setLang(lang);
		setValueAndPrese(usageTranslation);
		applyUpdate(usageTranslation);

		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageTranslation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateUsageTranslation(usageTranslation);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateUsageDefinition(Long usageDefinitionId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		UsageDefinition usageDefinition = new UsageDefinition();
		usageDefinition.setId(usageDefinitionId);
		usageDefinition.setValuePrese(valuePrese);
		setValueAndPrese(usageDefinition);
		applyUpdate(usageDefinition);

		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageDefinition", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateUsageDefinition(usageDefinition);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeId = item.getId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeLevels(Long lexemeId, String action, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<Lexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (Lexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeLevels(Long lexemeId, int lexemePosition, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (lexemeId == null) {
			return;
		}

		List<Lexeme> lexemes = lookupDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, lexemePosition);
		for (Lexeme lexeme : lexemes) {
			Long otherLexemeId = lexeme.getLexemeId();
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeLevels", otherLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeLevels(otherLexemeId, lexeme.getLevel1(), lexeme.getLevel2());
			activityLogService.createActivityLog(activityLog, otherLexemeId, ActivityEntity.LEXEME);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeGrammar(Long grammarId, String valuePrese, String lang, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Grammar grammar = new Grammar();
		grammar.setId(grammarId);
		grammar.setValuePrese(valuePrese);
		grammar.setLang(lang);

		updateLexemeGrammar(grammar, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeGrammar(Grammar grammar, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(grammar);
		applyUpdate(grammar);

		Long grammarId = grammar.getId();
		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeGrammar", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateGrammar(grammar);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeGovernment(Long governmentId, String value, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Government government = new Government();
		government.setId(governmentId);
		government.setValue(value);

		updateLexemeGovernment(government, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeGovernment(Government government, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		applyUpdate(government);

		Long governmentId = government.getId();
		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeGovernment", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateGovernment(government);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		activityLogService.createActivityLog(activityLog, lexemeDerivid, ActivityEntity.DERIV);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeFreeform(Long freeformId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setId(freeformId);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyUpdate(freeform);

		updateFreeform(ActivityOwner.LEXEME, ActivityEntity.FREEFORM, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeNote(Long lexemeNoteId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyUpdate(note);
		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeNote(lexemeNoteId, note);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemePublicity(Long lexemeId, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemePublicity", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemePublicity(lexemeId, isPublic);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeIsWord(Long lexemeId, boolean isWord, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeIsWord", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeIsWord(lexemeId, isWord);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeIsCollocation(Long lexemeId, boolean isCollocation, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeIsWord", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeIsCollocation(lexemeId, isCollocation);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeValueState(Long lexemeId, String valueStateCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeValueState", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeProficiencyLevel(Long lexemeId, String proficiencyLevelCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeProficiencyLevel", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeProficiencyLevel(lexemeId, proficiencyLevelCode);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long lexemeRelationId = item.getId();
			Long lexemeId = activityLogService.getOwnerId(lexemeRelationId, ActivityEntity.LEXEME_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeRelationOrdering", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateLexemeRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, lexemeRelationId, ActivityEntity.LEXEME_RELATION);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinition(Long definitionId, String valuePrese, String lang, String typeCode, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateDefinition(definitionId, value, valuePrese, lang, typeCode, isPublic);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinitionOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long definitionId = item.getId();
			Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateDefinitionOrderby(item);
			activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateDefinitionNote(Long definitionNoteId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyUpdate(note);
		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateDefinitionNote(definitionNoteId, note);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningRelationOrdering(List<ListData> items, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		for (ListData item : items) {
			Long meaningRelationId = item.getId();
			Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationOrdering", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.updateMeaningRelationOrderby(item);
			activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningLearnerComment(Long learnerCommentId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		LearnerComment learnerComment = new LearnerComment();
		learnerComment.setId(learnerCommentId);
		learnerComment.setValuePrese(valuePrese);
		setValueAndPrese(learnerComment);
		applyUpdate(learnerComment);

		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningLearnerComment", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLearnerComment(learnerCommentId, learnerComment);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningNote(Long meaningNoteId, String valuePrese, String lang, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Note note = initNote(valuePrese, lang, isPublic);
		applyUpdate(note);
		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningNote(meaningNoteId, note);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningForum(Long meaningForumId, String valuePrese, EkiUser user) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String userName = user.getName();

		cudDbService.updateMeaningForum(meaningForumId, value, valuePrese, userName);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningSemanticType", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningImage(Long meaningImageId, String url, String title, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		MeaningImage meaningImage = new MeaningImage();
		meaningImage.setUrl(url);
		meaningImage.setTitle(title);
		//meaningImage.setPublic(isPublic); not yet implemented in UI
		applyUpdate(meaningImage);

		Long meaningId = activityLogService.getOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningImage(meaningImageId, meaningImage);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningMedia(Long meaningMediaId, String url, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		MeaningMedia meaningImage = new MeaningMedia();
		meaningImage.setUrl(url);
		applyUpdate(meaningImage);

		Long meaningId = activityLogService.getOwnerId(meaningMediaId, ActivityEntity.MEANING_MEDIA);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningMedia(meaningMediaId, meaningImage);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningFreeform(Long freeformId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Freeform freeform = new Freeform();
		freeform.setId(freeformId);
		freeform.setValuePrese(valuePrese);
		setValueAndPrese(freeform);
		applyUpdate(freeform);

		updateFreeform(ActivityOwner.MEANING, ActivityEntity.FREEFORM, freeform, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordOsMorph(Long wordOsMorphId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsMorph wordOsMorph = new WordOsMorph();
		wordOsMorph.setId(wordOsMorphId);
		wordOsMorph.setValuePrese(valuePrese);
		setValueAndPrese(wordOsMorph);
		applyUpdate(wordOsMorph);

		Long wordId = activityLogService.getOwnerId(wordOsMorphId, ActivityEntity.WORD_OS_MORPH);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordOsMorph", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordOsMorph(wordOsMorph);
		activityLogService.createActivityLog(activityLog, wordOsMorphId, ActivityEntity.WORD_OS_MORPH);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordOsRecommendation(Long wordOsRecommendationId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsRecommendation wordOsRecommendation = new WordOsRecommendation();
		wordOsRecommendation.setId(wordOsRecommendationId);
		wordOsRecommendation.setValuePrese(valuePrese);
		setValueAndPrese(wordOsRecommendation);
		applyUpdate(wordOsRecommendation);

		Long wordId = activityLogService.getOwnerId(wordOsRecommendationId, ActivityEntity.WORD_OS_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordOsRecommendation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordOsRecommendation(wordOsRecommendation);
		activityLogService.createActivityLog(activityLog, wordOsRecommendationId, ActivityEntity.WORD_OS_RECOMMENDATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordOsUsage(Long wordOsUsageId, String valuePrese, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		WordOsUsage wordOsUsage = new WordOsUsage();
		wordOsUsage.setId(wordOsUsageId);
		wordOsUsage.setValuePrese(valuePrese);
		setValueAndPrese(wordOsUsage);
		applyUpdate(wordOsUsage);

		Long wordId = activityLogService.getOwnerId(wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWordOsUsage", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateWordOsUsage(wordOsUsage);
		activityLogService.createActivityLog(activityLog, wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateLexemeWeight(Long lexemeId, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeWeight", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateMeaningRelationWeight(Long meaningRelationId, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningRelationId, ActivityEntity.MEANING_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningRelationWeight", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateMeaningRelationWeight(meaningRelationId, lexemeWeight);
		activityLogService.createActivityLog(activityLog, meaningRelationId, ActivityEntity.MEANING_RELATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateWordDataAndLexemeWeight(Long lexemeId, Long wordId, String wordValuePrese, BigDecimal lexemeWeight, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		updateWordValue(wordId, wordValuePrese, roleDatasetCode, isManualEventOnUpdateEnabled);
		updateLexemeWeight(lexemeId, lexemeWeight, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	private void updateFreeform(ActivityOwner logOwner, ActivityEntity activityEntity, Freeform freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		Long freeformId = freeform.getId();
		Long ownerId = activityLogService.getOwnerId(freeformId, activityEntity);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateFreeform", ownerId, logOwner, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.updateFreeform(freeform, userName);
		activityLogService.createActivityLog(activityLog, freeformId, activityEntity);
	}

	// --- DELETE ---

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordType(Long wordId, String typeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordType", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteWordWordType(wordWordTypeId);
			activityLogService.createActivityLog(activityLog, wordWordTypeId, ActivityEntity.WORD_TYPE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordTag(Long wordId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long wordTagId = lookupDbService.getWordTagId(wordId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordTag", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteWordTag(wordTagId);
			activityLogService.createActivityLog(activityLog, wordTagId, ActivityEntity.WORD_TAG);
		}
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordForum(Long wordForumId) {

		cudDbService.deleteWordForum(wordForumId);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordFreeform(Long freeformId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordFreeform", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteDefinition(Long definitionId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionId, ActivityEntity.DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteDefinition(definitionId);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteDefinitionNote(Long definitionNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(definitionNoteId, ActivityEntity.DEFINITION_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteDefinitionNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteDefinitionNote(definitionNoteId);
		activityLogService.createActivityLog(activityLog, definitionNoteId, ActivityEntity.DEFINITION_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexeme(Long lexemeId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = lookupDbService.getWordLexemeMeaningIdByLexeme(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		updateLexemeLevels(lexemeId, "delete", roleDatasetCode, isManualEventOnUpdateEnabled);
		if (isOnlyLexemeForMeaning) {
			activityLogService.createActivityLog("deleteMeaning", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
		if (isOnlyLexemeForWord) {
			activityLogService.createActivityLog("deleteWord", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
		activityLogService.createActivityLog("deleteLexeme", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexemeCollocMembers(lexemeId);
		cudDbService.deleteLexeme(lexemeId);
		if (isOnlyLexemeForMeaning) {
			cudDbService.deleteMeaning(meaningId);
		}
		if (isOnlyLexemeForWord) {
			SimpleWord word = lookupDbService.getSimpleWord(wordId);
			cudDbService.deleteWord(word);
		}
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void deleteUsage(Long usageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageId, ActivityEntity.USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteUsage(usageId);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteUsageTranslation(Long usageTranslationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageTranslation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteUsageTranslation(usageTranslationId);
		activityLogService.createActivityLog(activityLog, usageTranslationId, ActivityEntity.USAGE_TRANSLATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteUsageDefinition(Long usageDefinitionId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteUsageDefinition", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteUsageDefinition(usageDefinitionId);
		activityLogService.createActivityLog(activityLog, usageDefinitionId, ActivityEntity.USAGE_DEFINITION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeFreeform(Long freeformId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeFreeform", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeGovernment(Long governmentId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(governmentId, ActivityEntity.GOVERNMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGovernment", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteGovernment(governmentId);
		activityLogService.createActivityLog(activityLog, governmentId, ActivityEntity.GOVERNMENT);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeGrammar(Long grammarId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(grammarId, ActivityEntity.GRAMMAR);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeGrammar", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteGrammar(grammarId);
		activityLogService.createActivityLog(activityLog, grammarId, ActivityEntity.GRAMMAR);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeNote(Long lexemeNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(lexemeNoteId, ActivityEntity.LEXEME_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexemeNote(lexemeNoteId);
		activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemePos(Long lexemeId, String posCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemePos(lexemePosId);
			activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeTag(Long lexemeId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeTag", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeTag(lexemeTagId);
			activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteCollocMember(Long collocMemberId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(collocMemberId, ActivityEntity.COLLOC_MEMBER);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteCollocMember", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteCollocMember(collocMemberId);
		activityLogService.createActivityLog(activityLog, collocMemberId, ActivityEntity.COLLOC_MEMBER);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningTag(Long meaningId, String tagName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(tagName)) {
			Long meaningTagId = lookupDbService.getMeaningTagId(meaningId, tagName);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningTag", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningTag(meaningTagId);
			activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeDeriv(Long lexemeId, String derivCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeDeriv", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
			activityLogService.createActivityLog(activityLog, lexemeDerivId, ActivityEntity.DERIV);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeRegister(Long lexemeId, String registerCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegister", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
			activityLogService.createActivityLog(activityLog, lexemeRegisterId, ActivityEntity.REGISTER);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeRegion(Long lexemeId, String regionCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRegion", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
			activityLogService.createActivityLog(activityLog, lexemeRegionId, ActivityEntity.REGION);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteLexemeRelation(Long relationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getOwnerId(relationId, ActivityEntity.LEXEME_RELATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeRelation", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLexemeRelation(relationId);
		activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.LEXEME_RELATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningAndLexemes(Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIdsByMeaning(meaningId, roleDatasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId, roleDatasetCode, isManualEventOnUpdateEnabled);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningDomain(Long meaningId, Classifier domain, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningDomain(meaningDomainId);
			activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
			activityLogService.createActivityLog(activityLog, meaningSemanticTypeId, ActivityEntity.SEMANTIC_TYPE);
		}
	}

	@Transactional(rollbackOn = Exception.class)
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

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningLearnerComment(Long learnerCommentId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(learnerCommentId, ActivityEntity.LEARNER_COMMENT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningLearnerComment", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteLearnerComment(learnerCommentId);
		activityLogService.createActivityLog(activityLog, learnerCommentId, ActivityEntity.LEARNER_COMMENT);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningNote(Long meaningNoteId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningNoteId, ActivityEntity.MEANING_NOTE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningNote(meaningNoteId);
		activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningForum(Long meaningForumId) {

		cudDbService.deleteMeaningForum(meaningForumId);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningImage(Long meaningImageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningImageId, ActivityEntity.MEANING_IMAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningImage", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningImage(meaningImageId);
		activityLogService.createActivityLog(activityLog, meaningImageId, ActivityEntity.MEANING_IMAGE);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningMedia(Long meaningMediaId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(meaningMediaId, ActivityEntity.MEANING_MEDIA);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningMedia", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteMeaningMedia(meaningMediaId);
		activityLogService.createActivityLog(activityLog, meaningMediaId, ActivityEntity.MEANING_MEDIA);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteMeaningFreeform(Long freeformId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long meaningId = activityLogService.getOwnerId(freeformId, ActivityEntity.FREEFORM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteMeaningFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteFreeform(freeformId);
		activityLogService.createActivityLog(activityLog, freeformId, ActivityEntity.FREEFORM);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordOsMorph(Long wordOsMorphId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(wordOsMorphId, ActivityEntity.WORD_OS_MORPH);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordOsMorph", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteWordOsMorph(wordOsMorphId);
		activityLogService.createActivityLog(activityLog, wordOsMorphId, ActivityEntity.WORD_OS_MORPH);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordOsRecommendation(Long wordOsRecommendationId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(wordOsRecommendationId, ActivityEntity.WORD_OS_RECOMMENDATION);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordOsRecommendation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteWordOsRecommendation(wordOsRecommendationId);
		activityLogService.createActivityLog(activityLog, wordOsRecommendationId, ActivityEntity.WORD_OS_RECOMMENDATION);
	}

	@Transactional(rollbackOn = Exception.class)
	public void deleteWordOsUsage(Long wordOsUsageId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteWordOsUsage", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteWordOsUsage(wordOsUsageId);
		activityLogService.createActivityLog(activityLog, wordOsUsageId, ActivityEntity.WORD_OS_USAGE);
	}

	// TODO what about forms linked to colloc membs?
	@Transactional(rollbackOn = Exception.class)
	public void deleteParadigm(Long paradigmId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = activityLogService.getOwnerId(paradigmId, ActivityEntity.PARADIGM);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteParadigm", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		cudDbService.deleteParadigm(paradigmId);
		activityLogService.createActivityLog(activityLog, paradigmId, ActivityEntity.PARADIGM);
	}
}
