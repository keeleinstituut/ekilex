package eki.ekilex.runner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleLogOwner;
import eki.common.constant.LifecycleProperty;
import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.runner.AbstractLoaderRunner.ArticleLogData;

@Deprecated
public abstract class AbstractLifecycleLogger extends AbstractLoaderCommons {

	private final static String CREATION_END = "(koostamise lõpp)";
	private final static String MODIFICATION_END = "(toimetamise lõpp)";
	private final static String CHIEF_EDITING = "(artikli peatoimetamine)";

	abstract String getLogEventBy();

	protected LifecycleEntity translate(FreeformType freeformType) {
		LifecycleEntity lifecycleEntity;
		try {
			lifecycleEntity = LifecycleEntity.valueOf(freeformType.name());
		} catch (Exception e) {
			lifecycleEntity = LifecycleEntity.ATTRIBUTE_FREEFORM;
		}
		return lifecycleEntity;
	}

	protected void createLifecycleLog(LifecycleLogOwner logOwner, Long ownerId, Long entityId, LifecycleEntity entity,
			LifecycleProperty property, LifecycleEventType eventType, String entry) throws Exception {

		createLifecycleLog(logOwner, ownerId, entityId, entity, property, eventType, null, null, null, entry);
	}

	protected void createLifecycleLog(LifecycleLogOwner logOwner, Long ownerId, Long entityId, LifecycleEntity entity,
			LifecycleProperty property, LifecycleEventType eventType, String eventBy, Timestamp eventOn, String entry) throws Exception {

		createLifecycleLog(logOwner, ownerId, entityId, entity, property, eventType, eventBy, eventOn, null, entry);
	}

	protected void createLifecycleLog(LifecycleLogOwner logOwner, Long ownerId, Long entityId, LifecycleEntity entity,
			LifecycleProperty property, LifecycleEventType eventType, String eventBy, Timestamp eventOn, String recent, String entry) throws Exception {

		if (eventBy == null) {
			eventBy = getLogEventBy();
		}

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("entity_id", entityId);
		tableRowParamMap.put("entity_name", entity.name());
		tableRowParamMap.put("entity_prop", property.name());
		tableRowParamMap.put("event_type", eventType.name());
		tableRowParamMap.put("event_by", eventBy);
		if (eventOn != null) {
			tableRowParamMap.put("event_on", eventOn);
		}
		tableRowParamMap.put("recent", recent);
		tableRowParamMap.put("entry", entry);
		Long lifecycleLogId = basicDbService.create(LIFECYCLE_LOG, tableRowParamMap);

		if (LifecycleLogOwner.LEXEME.equals(logOwner)) {
			createLexemeLifecycleLog(ownerId, lifecycleLogId);
		} else if (LifecycleLogOwner.MEANING.equals(logOwner)) {
			createMeaningLifecycleLog(ownerId, lifecycleLogId);
		} else if (LifecycleLogOwner.WORD.equals(logOwner)) {
			createWordLifecycleLog(ownerId, lifecycleLogId);
		} else if (LifecycleLogOwner.SOURCE.equals(logOwner)) {
			createSourceLifecycleLog(ownerId, lifecycleLogId);
		}
	}

	private void createMeaningLifecycleLog(Long meaningId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(MEANING_LIFECYCLE_LOG, tableRowParamMap);
	}

	private void createWordLifecycleLog(Long wordId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(WORD_LIFECYCLE_LOG, tableRowParamMap);
	}

	private void createLexemeLifecycleLog(Long lexemeId, Long lifecycleLogId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(LEXEME_LIFECYCLE_LOG, tableRowParamMap);
	}

	private void createSourceLifecycleLog(Long sourceId, Long lifecycleLogId) throws Exception {

		Map<String, Object> sourceLifecycleLogMap = new HashMap<>();
		sourceLifecycleLogMap.put("source_id", sourceId);
		sourceLifecycleLogMap.put("lifecycle_log_id", lifecycleLogId);
		basicDbService.create(SOURCE_LIFECYCLE_LOG, sourceLifecycleLogMap);
	}

	protected void createWordLifecycleLog(List<Long> wordIds, ArticleLogData logData, String dataset) throws Exception {

		for (Long wordId : wordIds) {
			if (logData.getCreatedBy() != null && logData.getCreatedOn() != null) {
				createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.CREATE, logData.getCreatedBy(),
						logData.getCreatedOn(), dataset);
			}
			if (logData.getCreatedBy() != null && logData.getCreationEnd() != null) {
				String message = dataset + " " + CREATION_END;
				createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, logData.getCreatedBy(),
						logData.getCreationEnd(), message);
			}
			if (logData.getModifiedBy() != null && logData.getModifiedOn() != null) {
				createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, logData.getModifiedBy(),
						logData.getModifiedOn(), dataset);
			}
			if (logData.getModifiedBy() != null && logData.getModificationEnd() != null) {
				String message = dataset + " " + MODIFICATION_END;
				createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, logData.getModifiedBy(),
						logData.getModificationEnd(), message);
			}
			if (logData.getChiefEditedBy() != null && logData.getChiefEditedOn() != null) {
				String message = dataset + " " + CHIEF_EDITING;
				createLifecycleLog(LifecycleLogOwner.WORD, wordId, wordId, LifecycleEntity.WORD, LifecycleProperty.VALUE, LifecycleEventType.UPDATE, logData.getChiefEditedBy(),
						logData.getChiefEditedOn(), message);
			}
		}
	}
}
