package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.SOURCE_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record8;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.LifecycleLog;
import eki.ekilex.data.ListData;
import eki.ekilex.service.db.util.LifecycleLogDbServiceHelper;

@Component
public class LifecycleLogDbService {

	private DSLContext create;

	private LifecycleLogDbServiceHelper helper;

	public LifecycleLogDbService(DSLContext context, LifecycleLogDbServiceHelper helper) {
		this.create = context;
		this.helper = helper;
	}

	public List<LifecycleLog> getLogForWord(Long wordId) {
		Table<Record8<Long, String, String, String, String, Timestamp, String, String>> ll = DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(LEXEME, LEXEME_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME_LIFECYCLE_LOG.LEXEME_ID.eq(LEXEME.ID))
						.and(LEXEME_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						)
				.unionAll(DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(WORD_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						WORD_LIFECYCLE_LOG.WORD_ID.eq(wordId)
						.and(WORD_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						))
				.unionAll(DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(LEXEME, MEANING_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						LEXEME.WORD_ID.eq(wordId)
						.and(LEXEME.MEANING_ID.eq(MEANING_LIFECYCLE_LOG.MEANING_ID))
						.and(MEANING_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						))
				.asTable("ll");
		List<LifecycleLog> results = create
				.select(
						ll.field("entity_id", Long.class),
						ll.field("entity_name", String.class),
						ll.field("entity_prop", String.class),
						ll.field("event_type", String.class),
						ll.field("event_by", String.class),
						ll.field("event_on", Timestamp.class),
						ll.field("recent", String.class),
						ll.field("entry", String.class)
						)
				.from(ll)
				.orderBy(ll.field("event_on").desc())
				.fetchInto(LifecycleLog.class);
		return results;
	}

	public List<LifecycleLog> getLogForMeaning(Long meaningId) {
		Table<Record8<Long, String, String, String, String, Timestamp, String, String>> ll = DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(LEXEME, LEXEME_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
						.and(LEXEME_LIFECYCLE_LOG.LEXEME_ID.eq(LEXEME.ID))
						.and(LEXEME_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						)
				.unionAll(DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(LEXEME, WORD_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
						.and(WORD_LIFECYCLE_LOG.WORD_ID.eq(LEXEME.WORD_ID))
						.and(WORD_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						))
				.unionAll(DSL
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY)
				.from(MEANING_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						MEANING_LIFECYCLE_LOG.MEANING_ID.eq(meaningId)
						.and(MEANING_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
						))
				.asTable("ll");
		List<LifecycleLog> results = create
				.select(
						ll.field("entity_id", Long.class),
						ll.field("entity_name", String.class),
						ll.field("entity_prop", String.class),
						ll.field("event_type", String.class),
						ll.field("event_by", String.class),
						ll.field("event_on", Timestamp.class),
						ll.field("recent", String.class),
						ll.field("entry", String.class)
						)
				.from(ll)
				.orderBy(ll.field("event_on").desc())
				.fetchInto(LifecycleLog.class);
		return results;
	}

	public List<LifecycleLog> getLogForSource(Long sourceId) {

		List<LifecycleLog> results = create
				.select(
						LIFECYCLE_LOG.ENTITY_ID,
						LIFECYCLE_LOG.ENTITY_NAME,
						LIFECYCLE_LOG.ENTITY_PROP,
						LIFECYCLE_LOG.EVENT_TYPE,
						LIFECYCLE_LOG.EVENT_BY,
						LIFECYCLE_LOG.EVENT_ON,
						LIFECYCLE_LOG.RECENT,
						LIFECYCLE_LOG.ENTRY
				)
				.from(
						SOURCE_LIFECYCLE_LOG, LIFECYCLE_LOG)
				.where(
						SOURCE_LIFECYCLE_LOG.SOURCE_ID.eq(sourceId)
						.and(SOURCE_LIFECYCLE_LOG.LIFECYCLE_LOG_ID.eq(LIFECYCLE_LOG.ID))
				)
				.orderBy(
						LIFECYCLE_LOG.EVENT_ON.desc()
				)
				.fetchInto(LifecycleLog.class);

		return results;
	}

	public void addLog(String userName, LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String recent, String entry) {
		if (LifecycleEntity.USAGE.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFirstDepthFreeformData(create, entityId, FreeformType.USAGE);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.USAGE_TRANSLATION.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getSecondDepthFreeformData(create, entityId, FreeformType.USAGE_TRANSLATION);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.USAGE_DEFINITION.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getSecondDepthFreeformData(create, entityId, FreeformType.USAGE_DEFINITION);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.GOVERNMENT.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFirstDepthFreeformData(create, entityId, FreeformType.GOVERNMENT);
				recent = (String) entityData.get("value_text");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.GRAMMAR.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFirstDepthFreeformData(create, entityId, FreeformType.GRAMMAR);
				recent = (String) entityData.get("value_text");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.DEFINITION.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getDefinitionData(create, entityId);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.LEXEME.equals(entity)) {
			if (LifecycleProperty.FREQUENCY_GROUP.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				recent = (String) entityData.get("frequency_group_code");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.POS.equals(property)) {
				Long lexemeId = create
						.select(LEXEME_POS.LEXEME_ID)
						.from(LEXEME_POS)
						.where(LEXEME_POS.ID.eq(entityId))
						.fetchSingleInto(Long.class);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			} else if (LifecycleProperty.DERIV.equals(property)) {
				Long lexemeId = create
						.select(LEXEME_DERIV.LEXEME_ID)
						.from(LEXEME_DERIV)
						.where(LEXEME_DERIV.ID.eq(entityId))
						.fetchSingleInto(Long.class);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			} else if (LifecycleProperty.REGISTER.equals(property)) {
				Long lexemeId = create
						.select(LEXEME_REGISTER.LEXEME_ID)
						.from(LEXEME_REGISTER)
						.where(LEXEME_REGISTER.ID.eq(entityId))
						.fetchSingleInto(Long.class);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			} else if (LifecycleProperty.LEVEL.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				recent = StringUtils.joinWith(".", entityData.get("level1"), entityData.get("level2"), entityData.get("level3"));
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
				}
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.DATASET.equals(property)) {
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.VALUE.equals(property)) {
				Long lifecycleLogId;
				if (LifecycleEventType.DELETE == eventType) {
					Map<String, Object> entityData = helper.getLexemeData(create, entityId);
					Map<String, Object> usageData = helper.getLexemeUsageData(create, entityId);
					String logString = lexemeLogString(entityData) + (Objects.equals("null", usageData.get("value_text")) ? "" : " " + usageData.get("value_text"));
					lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, logString, null);
				} else {
					lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				}
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.VALUE_STATE.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				recent = (String) entityData.get("value_state_code");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.PROCESS_STATE.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				recent = (String) entityData.get("process_state_code");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			}
		} else if (LifecycleEntity.WORD.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Long lifecycleLogId;
				if (LifecycleEventType.DELETE == eventType) {
					Map<String, Object> entityData = helper.getWordData(create, entityId);
					String logString = entityData.get("value").toString();
					lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, logString, null);
				} else {
					lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				}
				createWordLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.WORD_TYPE.equals(property)) {
				Long wordId = create
						.select(WORD_WORD_TYPE.WORD_ID)
						.from(WORD_WORD_TYPE)
						.where(WORD_WORD_TYPE.ID.eq(entityId))
						.fetchSingleInto(Long.class);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(wordId, lifecycleLogId);
			} else if (LifecycleProperty.GENDER.equals(property)) {
				Map<String, Object> entityData = helper.getWordData(create, entityId);
				recent = (String) entityData.get("gender_code");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.ASPECT.equals(property)) {
				Map<String, Object> entityData = helper.getWordData(create, entityId);
				recent = (String) entityData.get("aspect_code");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(entityId, lifecycleLogId);
			}
		} else if (LifecycleEntity.MEANING.equals(entity)) {
			if (LifecycleProperty.DOMAIN.equals(property)) {
				Long meaningId = create
						.select(MEANING_DOMAIN.MEANING_ID)
						.from(MEANING_DOMAIN)
						.where(MEANING_DOMAIN.ID.eq(entityId))
						.fetchSingleInto(Long.class);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			} else if (LifecycleProperty.VALUE.equals(property)) {
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(entityId, lifecycleLogId);
			}
		} else if (LifecycleEntity.LEXEME_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeSourceLinkData(create, entityId);
				recent = (String) entityData.get("value");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.DEFINITION_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getDefinitionSourceLinkData(create, entityId);
				recent = (String) entityData.get("value");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.FREEFORM_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFreeformSourceLinkData(create, entityId);
				//TODO why null value for authors?
				recent = (String) entityData.get("value");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				Long lexemeId = (Long) entityData.get("lexeme_id");
				if (lexemeId != null) {
					createLexemeLifecycleLog(lexemeId, lifecycleLogId);
					return;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				if (meaningId != null) {
					createMeaningLifecycleLog(meaningId, lifecycleLogId);
					return;
				}
				meaningId = (Long) entityData.get("definition_meaning_id");
				if (meaningId != null) {
					createMeaningLifecycleLog(meaningId, lifecycleLogId);
					return;
				}
				
			}
		} else if (LifecycleEntity.WORD_RELATION.equals(entity)) {
			Map<String, Object> entityData = helper.getWordRelationData(create, entityId);
			Map<String, Object> relatedWordData = helper.getWordData(create, (Long) entityData.get("word2_id"));
			String logString = entityData.get("word_rel_type_code") + " -> " + relatedWordData.get("value");
			Long lifecycleLogId;
			if (LifecycleEventType.DELETE == eventType) {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, logString, null);
			} else {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, null, logString);
			}
			Long wordId = (Long) entityData.get("word1_id");
			createWordLifecycleLog(wordId, lifecycleLogId);
		} else if (LifecycleEntity.WORD_RELATION_GROUP_MEMBER.equals(entity)) {
			Map<String, Object> memberData = helper.getWordRelationGroupMember(create, entityId);
			Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
			Long wordId = (Long) memberData.get("word_id");
			createWordLifecycleLog(wordId, lifecycleLogId);
		} else if (LifecycleEntity.LEXEME_RELATION.equals(entity)) {
			Map<String, Object> entityData = helper.getLexemeRelationData(create, entityId);
			Map<String, Object> relatedWordData = helper.getWordData(create, (Long) entityData.get("word2_id"));
			String logString = entityData.get("lex_rel_type_code") + " -> " + relatedWordData.get("value");
			Long lifecycleLogId;
			if (LifecycleEventType.DELETE == eventType) {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, logString, null);
			} else {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, null, logString);
			}
			Long lexemeId = (Long) entityData.get("lexeme1_id");
			createLexemeLifecycleLog(lexemeId, lifecycleLogId);
		} else if (LifecycleEntity.MEANING_RELATION.equals(entity)) {
			Map<String, Object> entityData = helper.getMeaningRelationData(create, entityId);
			List<String> meaningWords = helper.getMeaningWords(create, (Long) entityData.get("meaning2_id"));
			String logString = entityData.get("meaning_rel_type_code") + " -> " + String.join(",", meaningWords);
			Long lifecycleLogId;
			if (LifecycleEventType.DELETE == eventType) {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, logString, null);
			} else {
				lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, null, logString);
			}
			Long meaningId = (Long) entityData.get("meaning1_id");
			createMeaningLifecycleLog(meaningId, lifecycleLogId);
		} else if (LifecycleEntity.LEARNER_COMMENT.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getMeaningFreeformData(create, entityId, FreeformType.LEARNER_COMMENT);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.LEXEME_PUBLIC_NOTE.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFirstDepthFreeformData(create, entityId, FreeformType.PUBLIC_NOTE);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.MEANING_PUBLIC_NOTE.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getMeaningFreeformData(create, entityId, FreeformType.PUBLIC_NOTE);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.MEANING_PRIVATE_NOTE.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getMeaningFreeformData(create, entityId, FreeformType.PRIVATE_NOTE);
				recent = (String) entityData.get("value_prese");
				if (StringUtils.equals(recent, entry)) {
					if (isUpdate(eventType)) {
						return;
					}
					recent = null;
				}
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.SOURCE.equals(entity)) {
			if (LifecycleProperty.SOURCE_TYPE.equals(property)) {
				if (LifecycleEventType.UPDATE == eventType) {
					Map<String, Object> entityData = helper.getSourceType(create, entityId);
					recent = (String) entityData.get("type");
				}
			} else if (!LifecycleProperty.VALUE.equals(property)) {
				if (LifecycleEventType.UPDATE == eventType || LifecycleEventType.DELETE == eventType) {
					FreeformType freeformType = FreeformType.valueOf(property.name());
					Map<String, Object> entityData = helper.getSourceFreeformData(create, entityId, freeformType);
					recent = (String) entityData.get("value_prese");
					entityId = (Long) entityData.get("source_id");
					if (StringUtils.equals(recent, entry)) {
						if (isUpdate(eventType)) {
							return;
						}
						recent = null;
					}
				}
			}
			Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
			createSourceLifecycleLog(entityId, lifecycleLogId);
		}
	}

	private boolean isUpdate(LifecycleEventType eventType) {
		return LifecycleEventType.UPDATE.equals(eventType);
	}

	public void addLog(String userName, LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, ListData item) {
		Long entityId = item.getId();
		Long newOrderby = item.getOrderby();
		if (LifecycleEntity.DEFINITION.equals(entity)) {
			if (LifecycleProperty.ORDER_BY.equals(property)) {
				Map<String, Object> entityData = helper.getDefinitionData(create, entityId);
				String definitionValue = (String) entityData.get("value");
				Long meaningId = (Long) entityData.get("meaning_id");
				Long prevOrderBy = (Long) entityData.get("order_by");
				if (isUpdate(eventType) && newOrderby.equals(prevOrderBy)) {
					return;
				}
				String recent = prevOrderBy + ") " + definitionValue;
				String entry = newOrderby + ") " + definitionValue;
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.LEXEME_RELATION.equals(entity)) {
			if (LifecycleProperty.ORDER_BY.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeRelationData(create, entityId);
				String relTypeCode = (String) entityData.get("lex_rel_type_code");
				Long lexemeId = (Long) entityData.get("lexeme1_id");
				Long prevOrderBy = (Long) entityData.get("order_by");
				if (isUpdate(eventType) && newOrderby.equals(prevOrderBy)) {
					return;
				}
				String recent = prevOrderBy + ") " + relTypeCode;
				String entry = newOrderby + ") " + relTypeCode;
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.MEANING_RELATION.equals(entity)) {
			if (LifecycleProperty.ORDER_BY.equals(property)) {
				Map<String, Object> entityData = helper.getMeaningRelationData(create, entityId);
				String relTypeCode = (String) entityData.get("meaning_rel_type_code");
				Long meaningId = (Long) entityData.get("meaning1_id");
				Long prevOrderBy = (Long) entityData.get("order_by");
				if (isUpdate(eventType) && newOrderby.equals(prevOrderBy)) {
					return;
				}
				String recent = prevOrderBy + ") " + relTypeCode;
				String entry = newOrderby + ") " + relTypeCode;
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.WORD_RELATION.equals(entity)) {
			if (LifecycleProperty.ORDER_BY.equals(property)) {
				Map<String, Object> entityData = helper.getWordRelationData(create, entityId);
				String relTypeCode = (String) entityData.get("word_rel_type_code");
				Long wordId = (Long) entityData.get("word1_id");
				Long prevOrderBy = (Long) entityData.get("order_by");
				if (isUpdate(eventType) && newOrderby.equals(prevOrderBy)) {
					return;
				}
				String recent = prevOrderBy + ") " + relTypeCode;
				String entry = newOrderby + ") " + relTypeCode;
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(wordId, lifecycleLogId);
			}
		} else if (LifecycleEntity.LEXEME.equals(entity)) {
			if (LifecycleProperty.ORDER_BY.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				Long prevOrderBy = (Long) entityData.get("order_by");
				if (isUpdate(eventType) && newOrderby.equals(prevOrderBy)) {
					return;
				}
				String recent = prevOrderBy + ") " + lexemeLogString(entityData);
				String entry = newOrderby + ") " + lexemeLogString(entityData);
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			}
		}
	}

	private String lexemeLogString(Map<String, Object> entityData) {
		return entityData.get("value") + " [" + entityData.get("level1") + "." + entityData.get("level2") + "." + entityData.get("level3") + "]";
	}

	private Long createLifecycleLog(String userName, LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String recent, String entry) {

		String entityName = entity.name();
		String entityProp = property.name();
		String eventTypeName = eventType.name();
		Long lifecycleLogId = create
			.insertInto(
					LIFECYCLE_LOG,
					LIFECYCLE_LOG.ENTITY_ID,
					LIFECYCLE_LOG.ENTITY_NAME,
					LIFECYCLE_LOG.ENTITY_PROP,
					LIFECYCLE_LOG.EVENT_TYPE,
					LIFECYCLE_LOG.EVENT_BY,
					LIFECYCLE_LOG.RECENT,
					LIFECYCLE_LOG.ENTRY
					)
			.values(
					entityId,
					entityName,
					entityProp,
					eventTypeName,
					userName,
					recent,
					entry
					)
			.returning(LIFECYCLE_LOG.ID)
			.fetchOne()
			.getId();
		return lifecycleLogId;
	}

	private void createWordLifecycleLog(Long wordId, Long lifecycleLogId) {
		create
		.insertInto(
				WORD_LIFECYCLE_LOG,
				WORD_LIFECYCLE_LOG.WORD_ID,
				WORD_LIFECYCLE_LOG.LIFECYCLE_LOG_ID
				)
		.values(wordId, lifecycleLogId)
		.execute();
	}

	private void createLexemeLifecycleLog(Long lexemeId, Long lifecycleLogId) {
		create
			.insertInto(
					LEXEME_LIFECYCLE_LOG,
					LEXEME_LIFECYCLE_LOG.LEXEME_ID,
					LEXEME_LIFECYCLE_LOG.LIFECYCLE_LOG_ID
					)
			.values(lexemeId, lifecycleLogId)
			.execute();
	}

	private void createMeaningLifecycleLog(Long meaningId, Long lifecycleLogId) {
		create
			.insertInto(
					MEANING_LIFECYCLE_LOG,
					MEANING_LIFECYCLE_LOG.MEANING_ID,
					MEANING_LIFECYCLE_LOG.LIFECYCLE_LOG_ID
					)
			.values(meaningId, lifecycleLogId)
			.execute();
	}

	private void createSourceLifecycleLog(Long sourceId, Long lifecycleLogId) {
		create
				.insertInto(
						SOURCE_LIFECYCLE_LOG,
						SOURCE_LIFECYCLE_LOG.SOURCE_ID,
						SOURCE_LIFECYCLE_LOG.LIFECYCLE_LOG_ID
				)
				.values(sourceId, lifecycleLogId)
				.execute();
	}

}
