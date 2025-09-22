package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;
import static eki.ekilex.data.db.main.Tables.USAGE;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.main.tables.Publishing;

@Component
public class PublishingDbService {

	@Autowired
	private DSLContext mainDb;

	public void updateLexemePublicity(Long lexemeId, boolean isPublic) {

		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

	public void updateLexemeNotePublicity(Long lexemeNoteId, boolean isPublic) {

		mainDb
				.update(LEXEME_NOTE)
				.set(LEXEME_NOTE.IS_PUBLIC, isPublic)
				.where(LEXEME_NOTE.ID.eq(lexemeNoteId))
				.execute();
	}

	public void updateUsagePublicity(Long usageId, boolean isPublic) {

		mainDb
				.update(USAGE)
				.set(USAGE.IS_PUBLIC, isPublic)
				.where(USAGE.ID.eq(usageId))
				.execute();
	}

	public void updateMeaningNotePublicity(Long meaningNoteId, boolean isPublic) {

		mainDb
				.update(MEANING_NOTE)
				.set(MEANING_NOTE.IS_PUBLIC, isPublic)
				.where(MEANING_NOTE.ID.eq(meaningNoteId))
				.execute();
	}

	public void updateDefinitionPublicity(Long definitionId, boolean isPublic) {

		mainDb
				.update(DEFINITION)
				.set(DEFINITION.IS_PUBLIC, isPublic)
				.where(DEFINITION.ID.eq(definitionId))
				.execute();
	}

	public Long getPublishingId(String targetName, String entityName, Long entityId) {

		Publishing p = PUBLISHING.as("p");

		return mainDb
				.select(p.ID)
				.from(p)
				.where(
						p.TARGET_NAME.eq(targetName)
								.and(p.ENTITY_NAME.eq(entityName))
								.and(p.ENTITY_ID.eq(entityId)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long createPublishing(eki.ekilex.data.Publishing publishing) {

		Long publishingId = mainDb
				.select(PUBLISHING.ID)
				.from(PUBLISHING)
				.where(
						PUBLISHING.TARGET_NAME.eq(publishing.getTargetName())
								.and(PUBLISHING.ENTITY_NAME.eq(publishing.getEntityName()))
								.and(PUBLISHING.ENTITY_ID.eq(publishing.getEntityId())))
				.limit(1)
				.fetchOptionalInto(Long.class)
				.orElse(null);

		if (publishingId != null) {
			return publishingId;
		}

		return mainDb
				.insertInto(
						PUBLISHING,
						PUBLISHING.EVENT_BY,
						PUBLISHING.EVENT_ON,
						PUBLISHING.TARGET_NAME,
						PUBLISHING.ENTITY_NAME,
						PUBLISHING.ENTITY_ID)
				.values(
						publishing.getEventBy(),
						publishing.getEventOn(),
						publishing.getTargetName(),
						publishing.getEntityName(),
						publishing.getEntityId())
				.returning(PUBLISHING.ID)
				.fetchOne()
				.getId();
	}

	public void deletePublishing(Long publishingId) {

		mainDb
				.deleteFrom(PUBLISHING)
				.where(PUBLISHING.ID.eq(publishingId))
				.execute();
	}
}
