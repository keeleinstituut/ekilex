package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_IMAGE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.USAGE_SOURCE_LINK;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ReferenceType;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceLinkOwner;
import eki.ekilex.data.db.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.tables.LexemeSourceLink;
import eki.ekilex.data.db.tables.MeaningImageSourceLink;
import eki.ekilex.data.db.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.UsageSourceLink;

@Component
public class SourceLinkDbService {

	@Autowired
	private DSLContext create;

	public SourceLink getDefinitionSourceLink(Long sourceLinkId) {

		DefinitionSourceLink sl = DEFINITION_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getDefinitionNoteSourceLink(Long sourceLinkId) {

		DefinitionNoteSourceLink sl = DEFINITION_NOTE_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getLexemeSourceLink(Long sourceLinkId) {

		LexemeSourceLink sl = LEXEME_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getLexemeNoteSourceLink(Long sourceLinkId) {

		LexemeNoteSourceLink sl = LEXEME_NOTE_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getUsageSourceLink(Long sourceLinkId) {

		UsageSourceLink sl = USAGE_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getMeaningImageSourceLink(Long sourceLinkId) {

		MeaningImageSourceLink sl = MEANING_IMAGE_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	public SourceLink getMeaningNoteSourceLink(Long sourceLinkId) {

		MeaningNoteSourceLink sl = MEANING_NOTE_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

	private SourceLink getSourceLink(Long sourceLinkId, Table<?> sl) {

		Source s = SOURCE.as("s");

		return create
				.select(
						sl.field("id", Long.class),
						sl.field("type", String.class),
						sl.field("name", String.class),
						sl.field("source_id", Long.class),
						s.NAME.as("source_name"))
				.from(sl, s)
				.where(
						sl.field("id", Long.class).eq(sourceLinkId)
								.and(sl.field("source_id", Long.class).eq(s.ID)))
				.fetchOneInto(SourceLink.class);
	}

	public SourceLinkOwner getDefinitionSourceLinkOwner(Long sourceLinkId) {

		DefinitionSourceLink sl = DEFINITION_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.DEFINITION_ID);
	}

	public SourceLinkOwner getDefinitionNoteSourceLinkOwner(Long sourceLinkId) {

		DefinitionNoteSourceLink sl = DEFINITION_NOTE_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.DEFINITION_NOTE_ID);
	}

	public SourceLinkOwner getLexemeSourceLinkOwner(Long sourceLinkId) {

		LexemeSourceLink sl = LEXEME_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.LEXEME_ID);
	}

	public SourceLinkOwner getLexemeNoteSourceLinkOwner(Long sourceLinkId) {

		LexemeNoteSourceLink sl = LEXEME_NOTE_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.LEXEME_NOTE_ID);
	}

	public SourceLinkOwner getUsageSourceLinkOwner(Long sourceLinkId) {

		UsageSourceLink sl = USAGE_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.USAGE_ID);
	}

	public SourceLinkOwner getMeaningImageSourceLinkOwner(Long sourceLinkId) {

		MeaningImageSourceLink sl = MEANING_IMAGE_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.MEANING_IMAGE_ID);
	}

	public SourceLinkOwner getMeaningNoteSourceLinkOwner(Long sourceLinkId) {

		MeaningNoteSourceLink sl = MEANING_NOTE_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.MEANING_NOTE_ID);
	}

	private SourceLinkOwner getOwner(Long sourceLinkId, Table<?> sl, Field<Long> parentIdField) {

		return create
				.select(
						parentIdField.as("owner_id"),
						sl.field("source_id", Long.class))
				.from(sl)
				.where(sl.field("id", Long.class).eq(sourceLinkId))
				.fetchOptionalInto(SourceLinkOwner.class)
				.orElse(null);
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

	public Long createLexemeSourceLink(Long lexemeId, SourceLink sourceLink) {
		return create
				.insertInto(
						LEXEME_SOURCE_LINK,
						LEXEME_SOURCE_LINK.LEXEME_ID,
						LEXEME_SOURCE_LINK.SOURCE_ID,
						LEXEME_SOURCE_LINK.TYPE,
						LEXEME_SOURCE_LINK.NAME)
				.values(
						lexemeId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(LEXEME_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateLexemeSourceLink(Long sourceLinkId, String name) {
		create
				.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.NAME, name)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void deleteLexemeSourceLink(Long sourceLinkId) {
		create.delete(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public Long createLexemeNoteSourceLink(Long lexemeNoteId, SourceLink sourceLink) {
		return create
				.insertInto(
						LEXEME_NOTE_SOURCE_LINK,
						LEXEME_NOTE_SOURCE_LINK.LEXEME_NOTE_ID,
						LEXEME_NOTE_SOURCE_LINK.SOURCE_ID,
						LEXEME_NOTE_SOURCE_LINK.TYPE,
						LEXEME_NOTE_SOURCE_LINK.NAME)
				.values(
						lexemeNoteId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(LEXEME_NOTE_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateLexemeNoteSourceLink(Long lexemeNoteSourceLinkId, String name) {
		create
				.update(LEXEME_NOTE_SOURCE_LINK)
				.set(LEXEME_NOTE_SOURCE_LINK.NAME, name)
				.where(LEXEME_NOTE_SOURCE_LINK.ID.eq(lexemeNoteSourceLinkId))
				.execute();
	}

	public void deleteLexemeNoteSourceLink(Long lexemeNoteSourceLinkId) {
		create
				.deleteFrom(LEXEME_NOTE_SOURCE_LINK)
				.where(LEXEME_NOTE_SOURCE_LINK.ID.eq(lexemeNoteSourceLinkId))
				.execute();
	}

	public Long createUsageSourceLink(Long usageId, SourceLink sourceLink) {
		return create
				.insertInto(
						USAGE_SOURCE_LINK,
						USAGE_SOURCE_LINK.USAGE_ID,
						USAGE_SOURCE_LINK.SOURCE_ID,
						USAGE_SOURCE_LINK.TYPE,
						USAGE_SOURCE_LINK.NAME)
				.values(
						usageId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(USAGE_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateUsageSourceLink(Long usageSourceLinkId, String name) {
		create
				.update(USAGE_SOURCE_LINK)
				.set(USAGE_SOURCE_LINK.NAME, name)
				.where(USAGE_SOURCE_LINK.ID.eq(usageSourceLinkId))
				.execute();
	}

	public void deleteUsageSourceLink(Long usageSourceLinkId) {
		create
				.deleteFrom(USAGE_SOURCE_LINK)
				.where(USAGE_SOURCE_LINK.ID.eq(usageSourceLinkId))
				.execute();
	}

	public Long createMeaningNoteSourceLink(Long meaningNoteId, SourceLink sourceLink) {
		return create
				.insertInto(
						MEANING_NOTE_SOURCE_LINK,
						MEANING_NOTE_SOURCE_LINK.MEANING_NOTE_ID,
						MEANING_NOTE_SOURCE_LINK.SOURCE_ID,
						MEANING_NOTE_SOURCE_LINK.TYPE,
						MEANING_NOTE_SOURCE_LINK.NAME)
				.values(
						meaningNoteId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(MEANING_NOTE_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateMeaningNoteSourceLink(Long meaningNoteSourceLinkId, String name) {
		create
				.update(MEANING_NOTE_SOURCE_LINK)
				.set(MEANING_NOTE_SOURCE_LINK.NAME, name)
				.where(MEANING_NOTE_SOURCE_LINK.ID.eq(meaningNoteSourceLinkId))
				.execute();
	}

	public void deleteMeaningNoteSourceLink(Long meaningNoteSourceLinkId) {
		create
				.deleteFrom(MEANING_NOTE_SOURCE_LINK)
				.where(MEANING_NOTE_SOURCE_LINK.ID.eq(meaningNoteSourceLinkId))
				.execute();
	}

	public Long createMeaningImageSourceLink(Long meaningImageId, SourceLink sourceLink) {
		return create
				.insertInto(
						MEANING_IMAGE_SOURCE_LINK,
						MEANING_IMAGE_SOURCE_LINK.MEANING_IMAGE_ID,
						MEANING_IMAGE_SOURCE_LINK.SOURCE_ID,
						MEANING_IMAGE_SOURCE_LINK.TYPE,
						MEANING_IMAGE_SOURCE_LINK.NAME)
				.values(
						meaningImageId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(MEANING_IMAGE_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateMeaningImageSourceLink(Long meaningImageSourceLinkId, String name) {
		create
				.update(MEANING_IMAGE_SOURCE_LINK)
				.set(MEANING_IMAGE_SOURCE_LINK.NAME, name)
				.where(MEANING_IMAGE_SOURCE_LINK.ID.eq(meaningImageSourceLinkId))
				.execute();
	}

	public void deleteMeaningImageSourceLink(Long meaningImageSourceLinkId) {
		create
				.deleteFrom(MEANING_IMAGE_SOURCE_LINK)
				.where(MEANING_IMAGE_SOURCE_LINK.ID.eq(meaningImageSourceLinkId))
				.execute();
	}

	public Long createDefinitionSourceLink(Long definitionId, SourceLink sourceLink) {
		return create
				.insertInto(
						DEFINITION_SOURCE_LINK,
						DEFINITION_SOURCE_LINK.DEFINITION_ID,
						DEFINITION_SOURCE_LINK.SOURCE_ID,
						DEFINITION_SOURCE_LINK.TYPE,
						DEFINITION_SOURCE_LINK.NAME)
				.values(
						definitionId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
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

	public Long createDefinitionNoteSourceLink(Long definitionNoteId, SourceLink sourceLink) {
		return create
				.insertInto(
						DEFINITION_NOTE_SOURCE_LINK,
						DEFINITION_NOTE_SOURCE_LINK.DEFINITION_NOTE_ID,
						DEFINITION_NOTE_SOURCE_LINK.SOURCE_ID,
						DEFINITION_NOTE_SOURCE_LINK.TYPE,
						DEFINITION_NOTE_SOURCE_LINK.NAME)
				.values(
						definitionNoteId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(DEFINITION_NOTE_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateDefinitionNoteSourceLink(Long definitionNoteSourceLinkId, String name) {
		create
				.update(DEFINITION_NOTE_SOURCE_LINK)
				.set(DEFINITION_NOTE_SOURCE_LINK.NAME, name)
				.where(DEFINITION_NOTE_SOURCE_LINK.ID.eq(definitionNoteSourceLinkId))
				.execute();
	}

	public void deleteDefinitionNoteSourceLink(Long definitionNoteSourceLinkId) {
		create
				.deleteFrom(DEFINITION_NOTE_SOURCE_LINK)
				.where(DEFINITION_NOTE_SOURCE_LINK.ID.eq(definitionNoteSourceLinkId))
				.execute();
	}

}
