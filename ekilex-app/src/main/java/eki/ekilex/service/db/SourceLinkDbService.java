package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ReferenceType;
import eki.ekilex.data.ListData;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceLinkOwner;
import eki.ekilex.data.db.main.tables.DefinitionNoteSourceLink;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.FreeformSourceLink;
import eki.ekilex.data.db.main.tables.LexemeNoteSourceLink;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.MeaningImageSourceLink;
import eki.ekilex.data.db.main.tables.MeaningNoteSourceLink;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.UsageSourceLink;

@Component
public class SourceLinkDbService {

	@Autowired
	private DSLContext mainDb;

	public SourceLink getFreeformSourceLink(Long sourceLinkId) {

		FreeformSourceLink sl = FREEFORM_SOURCE_LINK.as("sl");

		return getSourceLink(sourceLinkId, sl);
	}

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

		return mainDb
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

	public SourceLinkOwner getFreeformSourceLinkOwner(Long sourceLinkId) {

		FreeformSourceLink sl = FREEFORM_SOURCE_LINK.as("sl");

		return getOwner(sourceLinkId, sl, sl.FREEFORM_ID);
	}

	private SourceLinkOwner getOwner(Long sourceLinkId, Table<?> sl, Field<Long> parentIdField) {

		return mainDb
				.select(
						parentIdField.as("owner_id"),
						sl.field("source_id", Long.class))
				.from(sl)
				.where(sl.field("id", Long.class).eq(sourceLinkId))
				.fetchOptionalInto(SourceLinkOwner.class)
				.orElse(null);
	}

	public Long createFreeformSourceLink(Long freeformId, SourceLink sourceLink) {
		return mainDb
				.insertInto(
						FREEFORM_SOURCE_LINK,
						FREEFORM_SOURCE_LINK.FREEFORM_ID,
						FREEFORM_SOURCE_LINK.SOURCE_ID,
						FREEFORM_SOURCE_LINK.TYPE,
						FREEFORM_SOURCE_LINK.NAME)
				.values(
						freeformId,
						sourceLink.getSourceId(),
						sourceLink.getType().name(),
						sourceLink.getName())
				.returning(FREEFORM_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public void updateFreeformSourceLink(Long freeformSourceLinkId, String name) {
		mainDb
				.update(FREEFORM_SOURCE_LINK)
				.set(FREEFORM_SOURCE_LINK.NAME, name)
				.where(FREEFORM_SOURCE_LINK.ID.eq(freeformSourceLinkId))
				.execute();
	}

	public void updateFreeformSourceLinkOrderby(ListData item) {
		mainDb
				.update(FREEFORM_SOURCE_LINK)
				.set(FREEFORM_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(FREEFORM_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteFreeformSourceLink(Long freeformSourceLinkId) {
		mainDb
				.deleteFrom(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.ID.eq(freeformSourceLinkId))
				.execute();
	}

	public Long createLexemeSourceLink(Long lexemeId, Long sourceId, ReferenceType refType, String sourceLinkName) {
		return mainDb
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
		return mainDb
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
		mainDb
				.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.NAME, name)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void updateLexemeSourceLinkOrderby(ListData item) {
		mainDb
				.update(LEXEME_SOURCE_LINK)
				.set(LEXEME_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(LEXEME_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteLexemeSourceLink(Long sourceLinkId) {
		mainDb
				.delete(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public Long createLexemeNoteSourceLink(Long lexemeNoteId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(LEXEME_NOTE_SOURCE_LINK)
				.set(LEXEME_NOTE_SOURCE_LINK.NAME, name)
				.where(LEXEME_NOTE_SOURCE_LINK.ID.eq(lexemeNoteSourceLinkId))
				.execute();
	}

	public void updateLexemeNoteSourceLinkOrderby(ListData item) {
		mainDb
				.update(LEXEME_NOTE_SOURCE_LINK)
				.set(LEXEME_NOTE_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(LEXEME_NOTE_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteLexemeNoteSourceLink(Long lexemeNoteSourceLinkId) {
		mainDb
				.deleteFrom(LEXEME_NOTE_SOURCE_LINK)
				.where(LEXEME_NOTE_SOURCE_LINK.ID.eq(lexemeNoteSourceLinkId))
				.execute();
	}

	public Long createUsageSourceLink(Long usageId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(USAGE_SOURCE_LINK)
				.set(USAGE_SOURCE_LINK.NAME, name)
				.where(USAGE_SOURCE_LINK.ID.eq(usageSourceLinkId))
				.execute();
	}

	public void updateUsageSourceLinkOrderby(ListData item) {
		mainDb
				.update(USAGE_SOURCE_LINK)
				.set(USAGE_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(USAGE_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteUsageSourceLink(Long usageSourceLinkId) {
		mainDb
				.deleteFrom(USAGE_SOURCE_LINK)
				.where(USAGE_SOURCE_LINK.ID.eq(usageSourceLinkId))
				.execute();
	}

	public Long createMeaningNoteSourceLink(Long meaningNoteId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(MEANING_NOTE_SOURCE_LINK)
				.set(MEANING_NOTE_SOURCE_LINK.NAME, name)
				.where(MEANING_NOTE_SOURCE_LINK.ID.eq(meaningNoteSourceLinkId))
				.execute();
	}

	public void updateMeaningNoteSourceLinkOrderby(ListData item) {
		mainDb
				.update(MEANING_NOTE_SOURCE_LINK)
				.set(MEANING_NOTE_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(MEANING_NOTE_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteMeaningNoteSourceLink(Long meaningNoteSourceLinkId) {
		mainDb
				.deleteFrom(MEANING_NOTE_SOURCE_LINK)
				.where(MEANING_NOTE_SOURCE_LINK.ID.eq(meaningNoteSourceLinkId))
				.execute();
	}

	public Long createMeaningImageSourceLink(Long meaningImageId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(MEANING_IMAGE_SOURCE_LINK)
				.set(MEANING_IMAGE_SOURCE_LINK.NAME, name)
				.where(MEANING_IMAGE_SOURCE_LINK.ID.eq(meaningImageSourceLinkId))
				.execute();
	}

	public void updateMeaningImageSourceLinkOrderby(ListData item) {
		mainDb
				.update(MEANING_IMAGE_SOURCE_LINK)
				.set(MEANING_IMAGE_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(MEANING_IMAGE_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteMeaningImageSourceLink(Long meaningImageSourceLinkId) {
		mainDb
				.deleteFrom(MEANING_IMAGE_SOURCE_LINK)
				.where(MEANING_IMAGE_SOURCE_LINK.ID.eq(meaningImageSourceLinkId))
				.execute();
	}

	public Long createDefinitionSourceLink(Long definitionId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(DEFINITION_SOURCE_LINK)
				.set(DEFINITION_SOURCE_LINK.NAME, sourceLinkName)
				.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public void updateDefinitionSourceLinkOrderby(ListData item) {
		mainDb
				.update(DEFINITION_SOURCE_LINK)
				.set(DEFINITION_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(DEFINITION_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		mainDb.delete(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId))
				.execute();
	}

	public Long createDefinitionNoteSourceLink(Long definitionNoteId, SourceLink sourceLink) {
		return mainDb
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
		mainDb
				.update(DEFINITION_NOTE_SOURCE_LINK)
				.set(DEFINITION_NOTE_SOURCE_LINK.NAME, name)
				.where(DEFINITION_NOTE_SOURCE_LINK.ID.eq(definitionNoteSourceLinkId))
				.execute();
	}

	public void updateDefinitionNoteSourceLinkOrderby(ListData item) {
		mainDb
				.update(DEFINITION_NOTE_SOURCE_LINK)
				.set(DEFINITION_NOTE_SOURCE_LINK.ORDER_BY, item.getOrderby())
				.where(DEFINITION_NOTE_SOURCE_LINK.ID.eq(item.getId()))
				.execute();
	}

	public void deleteDefinitionNoteSourceLink(Long definitionNoteSourceLinkId) {
		mainDb
				.deleteFrom(DEFINITION_NOTE_SOURCE_LINK)
				.where(DEFINITION_NOTE_SOURCE_LINK.ID.eq(definitionNoteSourceLinkId))
				.execute();
	}

}
