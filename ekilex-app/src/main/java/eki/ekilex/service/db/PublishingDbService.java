package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.PUBLISHING;

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

	public void createPublishing(eki.ekilex.data.Publishing publishing) {

		mainDb
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
				.execute();
	}

	public void deletePublishing(Long publishingId) {

		mainDb
				.deleteFrom(PUBLISHING)
				.where(PUBLISHING.ID.eq(publishingId))
				.execute();
	}
}
