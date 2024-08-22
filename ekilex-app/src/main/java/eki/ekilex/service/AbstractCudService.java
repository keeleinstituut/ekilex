package eki.ekilex.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.ReferenceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.AbstractCreateUpdateEntity;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Note;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.ValueAndPrese;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordRelation;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SourceLinkDbService;
import eki.ekilex.service.db.TagDbService;

public abstract class AbstractCudService extends AbstractService {

	protected static final ReferenceType DEFAULT_SOURCE_LINK_REF_TYPE = ReferenceType.ANY;

	@Autowired
	protected TextDecorationService textDecorationService;

	@Autowired
	protected CudDbService cudDbService;

	@Autowired
	protected SourceLinkDbService sourceLinkDbService;

	@Autowired
	protected LookupDbService lookupDbService;

	@Autowired
	protected TagDbService tagDbService;

	@Transactional
	public Long createDefinition(
			Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity, String typeCode, boolean isPublic,
			String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinition", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, complexity, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
		return definitionId;
	}

	@Transactional
	public WordLexemeMeaningIdTuple createLexeme(Long wordId, String datasetCode, Long meaningId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexeme", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1, null, PUBLICITY_PUBLIC);
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		if (lexemeId == null) {
			return wordLexemeMeaningId;
		}
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);

		return wordLexemeMeaningId;
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemePos", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		activityLogService.createActivityLog(activityLog, lexemePosId, ActivityEntity.POS);
	}

	@Transactional
	public Long createUsage(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Usage usage = new Usage();
		usage.setValuePrese(valuePrese);
		usage.setLang(lang);
		usage.setComplexity(complexity);
		usage.setPublic(isPublic);

		return createUsage(lexemeId, usage, roleDatasetCode, isManualEventOnUpdateEnabled);
	}

	@Transactional
	public Long createUsage(Long lexemeId, Usage usage, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		setValueAndPrese(usage);
		applyCreateUpdate(usage);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsage", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long usageId = cudDbService.createUsage(lexemeId, usage);
		activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);
		return usageId;
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog;
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
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
			activityLog = activityLogService.prepareActivityLog("createWordRelation", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			activityLogService.createActivityLog(activityLog, relationId, ActivityEntity.WORD_RELATION);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				activityLog = activityLogService.prepareActivityLog("createWordRelation", targetWordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				activityLogService.createActivityLog(activityLog, oppositeRelationId, ActivityEntity.WORD_RELATION);
			}
		}
	}

	protected Long createUsageSourceLink(Long lexemeId, Long usageId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createUsageSourceLink(usageId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateUsageSourceLink(Long lexemeId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateUsageSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	protected Long createLexemeNoteSourceLink(Long lexemeId, Long lexemeNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createLexemeNoteSourceLink(lexemeNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateLexemeNoteSourceLink(Long lexemeId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeNoteSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateLexemeNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK);
	}

	protected Long createMeaningNoteSourceLink(Long meaningId, Long meaningNoteId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createMeaningNoteSourceLink(meaningNoteId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
		return sourceLinkId;
	}

	protected void updateMeaningNoteSourceLink(Long meaningId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateMeaningNoteSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateMeaningNoteSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.MEANING_NOTE_SOURCE_LINK);
	}

	protected void createDefinitionSourceLink(Long meaningId, Long definitionId, SourceLink sourceLink, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceLink);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected void updateDefinitionSourceLink(Long meaningId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	protected Note initNote(String valuePrese, String lang, Complexity complexity, boolean isPublic) {

		Note note = new Note();
		note.setValuePrese(valuePrese);
		note.setLang(lang);
		note.setComplexity(complexity);
		note.setPublic(isPublic);

		setValueAndPrese(note);

		return note;
	}

	protected void applyCreateUpdate(AbstractCreateUpdateEntity entity) {

		String userName = userContext.getUserName();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		entity.setCreatedBy(userName);
		entity.setCreatedOn(now);
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
	}

	protected void applyUpdate(AbstractCreateUpdateEntity entity) {

		String userName = userContext.getUserName();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
	}

	public void setValueAndPrese(ValueAndPrese note) {

		String valuePrese = StringUtils.trim(note.getValuePrese());
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		note.setValue(value);
		note.setValuePrese(valuePrese);
	}

	protected Long createLexemeFreeform(ActivityEntity activityEntity, Long lexemeId, FreeForm freeform, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		String userName = userContext.getUserName();
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeFreeform", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeFreeformId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
		activityLogService.createActivityLog(activityLog, lexemeFreeformId, activityEntity);
		return lexemeFreeformId;
	}

	protected void setFreeformValueTextAndValuePrese(FreeForm freeform, String valuePrese) {

		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		freeform.setValueText(value);
		freeform.setValuePrese(valuePrese);
	}

	protected void moveCreatedWordRelationToFirst(Long wordId, Long relationId, String relTypeCode) {

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
}
