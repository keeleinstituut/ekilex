package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_LIFECYCLE_LOG;
import static eki.ekilex.data.db.Tables.WORD_LIFECYCLE_LOG;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.ekilex.data.EkiUser;
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

	public void addLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId) {
		addLog(eventType, entity, property, entityId, null, null);
	}

	public void addLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String entry) {
		addLog(eventType, entity, property, entityId, null, entry);
	}

	public void addLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String recent, String entry) {
		String userName = getUserName();
		if (LifecycleEntity.USAGE.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFirstDepthFreeformData(create, entityId, FreeformType.USAGE);
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
		} else if (LifecycleEntity.USAGE_TRANSLATION.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getSecondDepthFreeformData(create, entityId, FreeformType.USAGE_TRANSLATION);
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
		} else if (LifecycleEntity.USAGE_DEFINITION.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getSecondDepthFreeformData(create, entityId, FreeformType.USAGE_DEFINITION);
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
		} else if (LifecycleEntity.LEXEME.equals(entity)) {
			if (LifecycleProperty.FREQUENCY_GROUP.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeData(create, entityId);
				recent = (String) entityData.get("frequency_group");
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
			} else if (LifecycleProperty.DATASET.equals(property)) {
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(entityId, lifecycleLogId);
			}
		} else if (LifecycleEntity.WORD.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(entityId, lifecycleLogId);
			} else if (LifecycleProperty.GENDER.equals(property)) {
				Map<String, Object> entityData = helper.getWordData(create, entityId);
				recent = (String) entityData.get("gender_code");
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
			}
		} else if (LifecycleEntity.LEXEME_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getLexemeSourceLinkData(create, entityId);
				recent = (String) entityData.get("value");
				Long lexemeId = (Long) entityData.get("lexeme_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createLexemeLifecycleLog(lexemeId, lifecycleLogId);
			}
		} else if (LifecycleEntity.DEFINITION_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getDefinitionSourceLinkData(create, entityId);
				recent = (String) entityData.get("value");
				Long meaningId = (Long) entityData.get("meaning_id");
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createMeaningLifecycleLog(meaningId, lifecycleLogId);
			}
		} else if (LifecycleEntity.FREEFORM_SOURCE_LINK.equals(entity)) {
			if (LifecycleProperty.VALUE.equals(property)) {
				Map<String, Object> entityData = helper.getFreeformSourceLinkData(create, entityId);
				//TODO why null value for authors?
				recent = (String) entityData.get("value");
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
		}
	}

	private boolean isUpdate(LifecycleEventType eventType) {
		return LifecycleEventType.UPDATE.equals(eventType);
	}

	public void addLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, ListData item) {
		String userName = getUserName();
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
				String recent = String.valueOf(prevOrderBy) + ") " + definitionValue;
				String entry = String.valueOf(newOrderby) + ") " + definitionValue;
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
				String recent = String.valueOf(prevOrderBy) + ") " + relTypeCode;
				String entry = String.valueOf(newOrderby) + ") " + relTypeCode;
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
				String recent = String.valueOf(prevOrderBy) + ") " + relTypeCode;
				String entry = String.valueOf(newOrderby) + ") " + relTypeCode;
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
				String recent = String.valueOf(prevOrderBy) + ") " + relTypeCode;
				String entry = String.valueOf(newOrderby) + ") " + relTypeCode;
				Long lifecycleLogId = createLifecycleLog(userName, eventType, entity, property, entityId, recent, entry);
				createWordLifecycleLog(wordId, lifecycleLogId);
			}
		}
	}

	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName;
		if (authentication != null) {
			EkiUser user = (EkiUser) authentication.getPrincipal();
			userName = user.getName();
		} else {
			userName = "N/A";
		}
		return userName;
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

}
