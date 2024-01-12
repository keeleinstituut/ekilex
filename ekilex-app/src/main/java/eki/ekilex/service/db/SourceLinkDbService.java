package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.Tables.WORD_FREEFORM;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.api.FreeformOwner;
import eki.ekilex.data.db.tables.DefinitionFreeform;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.WordFreeform;

@Component
public class SourceLinkDbService {

	@Autowired
	private DSLContext create;

	public FreeformOwner getFreeformOwner(Long freeformId) {

		Freeform ff = FREEFORM.as("ff");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		WordFreeform wff = WORD_FREEFORM.as("wff");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		final String fieldNameEntity = "entity";
		final String fieldNameEntityId = "entity_id";

		Table<Record2<String,Long>> xff = DSL
				.select(
						DSL.field(DSL.val(ActivityEntity.LEXEME.name())).as(fieldNameEntity),
						lff.LEXEME_ID.as(fieldNameEntityId))
				.from(lff)
				.where(lff.FREEFORM_ID.eq(freeformId))
				.unionAll(DSL
						.select(
								DSL.field(DSL.val(ActivityEntity.MEANING.name())).as(fieldNameEntity),
								mff.MEANING_ID.as(fieldNameEntityId))
						.from(mff)
						.where(mff.FREEFORM_ID.eq(freeformId)))
				.unionAll(DSL
						.select(
								DSL.field(DSL.val(ActivityEntity.WORD.name())).as(fieldNameEntity),
								wff.WORD_ID.as(fieldNameEntityId))
						.from(wff)
						.where(wff.FREEFORM_ID.eq(freeformId)))
				.unionAll(DSL
						.select(
								DSL.field(DSL.val(ActivityEntity.SOURCE.name())).as(fieldNameEntity),
								sff.SOURCE_ID.as(fieldNameEntityId))
						.from(sff)
						.where(sff.FREEFORM_ID.eq(freeformId)))
				.unionAll(DSL
						.select(
								DSL.field(DSL.val(ActivityEntity.DEFINITION.name())).as(fieldNameEntity),
								dff.DEFINITION_ID.as(fieldNameEntityId))
						.from(dff)
						.where(dff.FREEFORM_ID.eq(freeformId)))
				.asTable("xff");

		return create
				.select(
						ff.TYPE,
						xff.field(fieldNameEntity),
						xff.field(fieldNameEntityId))
				.from(ff, xff)
				.where(ff.ID.eq(freeformId))
				.fetchSingleInto(FreeformOwner.class);
	}

	public SourceLink getFreeformSourceLink(Long sourceLinkId) {

		return create
				.select(
						DSL.field(DSL.val(ReferenceOwner.FREEFORM.name())).as("owner"),
						FREEFORM_SOURCE_LINK.FREEFORM_ID.as("owner_id"),
						FREEFORM_SOURCE_LINK.ID,
						FREEFORM_SOURCE_LINK.TYPE,
						FREEFORM_SOURCE_LINK.NAME,
						FREEFORM_SOURCE_LINK.SOURCE_ID,
						SOURCE.NAME.as("source_name"))
				.from(FREEFORM_SOURCE_LINK, SOURCE)
				.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId).and(FREEFORM_SOURCE_LINK.SOURCE_ID.eq(SOURCE.ID)))
				.fetchOneInto(SourceLink.class);
	}

	public SourceLink getDefinitionSourceLink(Long sourceLinkId) {

		return create
				.select(
						DSL.field(DSL.val(ReferenceOwner.DEFINITION.name())).as("owner"),
						DEFINITION_SOURCE_LINK.DEFINITION_ID.as("owner_id"),
						DEFINITION_SOURCE_LINK.ID,
						DEFINITION_SOURCE_LINK.TYPE,
						DEFINITION_SOURCE_LINK.NAME,
						DEFINITION_SOURCE_LINK.VALUE,
						DEFINITION_SOURCE_LINK.SOURCE_ID,
						SOURCE.NAME.as("source_name"))
				.from(DEFINITION_SOURCE_LINK, SOURCE)
				.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId).and(DEFINITION_SOURCE_LINK.SOURCE_ID.eq(SOURCE.ID)))
				.fetchOneInto(SourceLink.class);
	}

	public SourceLink getLexemeSourceLink(Long sourceLinkId) {

		return create
				.select(
						DSL.field(DSL.val(ReferenceOwner.LEXEME.name())).as("owner"),
						LEXEME_SOURCE_LINK.LEXEME_ID.as("owner_id"),
						LEXEME_SOURCE_LINK.ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME,
						LEXEME_SOURCE_LINK.VALUE,
						LEXEME_SOURCE_LINK.SOURCE_ID,
						SOURCE.NAME.as("source_name"))
				.from(LEXEME_SOURCE_LINK, SOURCE)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId).and(LEXEME_SOURCE_LINK.SOURCE_ID.eq(SOURCE.ID)))
				.fetchOneInto(SourceLink.class);
	}

	public Long createLexemeSourceLink(Long lexemeId, Long sourceId, ReferenceType refType, String sourceLinkName) {
		return create
				.insertInto(
						LEXEME_SOURCE_LINK,
						LEXEME_SOURCE_LINK.LEXEME_ID,
						LEXEME_SOURCE_LINK.SOURCE_ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME)
				.values(
						lexemeId,
						sourceId,
						refType.name(),
						sourceLinkName)
				.returning(LEXEME_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateLexemeSourceLink(Long sourceLinkId, String sourceLinkName) {
		create
				.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.NAME, sourceLinkName)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void deleteLexemeSourceLink(Long sourceLinkId) {
		create.delete(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public Long createFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType refType, String sourceLinkName) {
		return create
				.insertInto(
						FREEFORM_SOURCE_LINK,
						FREEFORM_SOURCE_LINK.FREEFORM_ID,
						FREEFORM_SOURCE_LINK.SOURCE_ID,
						FREEFORM_SOURCE_LINK.TYPE,
						FREEFORM_SOURCE_LINK.NAME)
				.values(
						freeformId,
						sourceId,
						refType.name(),
						sourceLinkName)
				.returning(FREEFORM_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateFreeformSourceLink(Long sourceLinkId, String sourceLinkName) {
		create
				.update(FREEFORM_SOURCE_LINK)
				.set(FREEFORM_SOURCE_LINK.NAME, sourceLinkName)
				.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void deleteFreeformSourceLink(Long sourceLinkId) {
		create.delete(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public Long createDefinitionSourceLink(Long definitionId, Long sourceId, ReferenceType refType, String sourceLinkName) {
		return create
				.insertInto(
						DEFINITION_SOURCE_LINK,
						DEFINITION_SOURCE_LINK.DEFINITION_ID,
						DEFINITION_SOURCE_LINK.SOURCE_ID,
						DEFINITION_SOURCE_LINK.TYPE,
						DEFINITION_SOURCE_LINK.NAME)
				.values(
						definitionId,
						sourceId,
						refType.name(),
						sourceLinkName)
				.returning(DEFINITION_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateDefinitionSourceLink(Long sourceLinkId, String sourceLinkName) {
		create
				.update(DEFINITION_SOURCE_LINK)
				.set(DEFINITION_SOURCE_LINK.NAME, sourceLinkName)
				.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		create.delete(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}
}
