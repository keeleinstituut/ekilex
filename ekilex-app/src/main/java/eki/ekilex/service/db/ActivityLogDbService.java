package eki.ekilex.service.db;

import static eki.ekilex.data.db.arch.Tables.ACTIVITY_LOG_BULK;
import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.SOURCE_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.SOURCE_FREEFORM;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.main.Tables.WORD_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_OD_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OD_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OD_USAGE;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LastActivityType;
import eki.ekilex.data.TypeActivityLogDiff;
import eki.ekilex.data.WordLexemeMeaningIds;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionFreeform;
import eki.ekilex.data.db.main.tables.Freeform;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeFreeform;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningActivityLog;
import eki.ekilex.data.db.main.tables.MeaningFreeform;
import eki.ekilex.data.db.main.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.main.tables.SourceActivityLog;
import eki.ekilex.data.db.main.tables.SourceFreeform;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordActivityLog;
import eki.ekilex.data.db.main.tables.WordFreeform;
import eki.ekilex.data.db.main.tables.WordLastActivityLog;
import eki.ekilex.data.db.main.udt.records.TypeActivityLogDiffRecord;

@Component
public class ActivityLogDbService implements GlobalConstant, ActivityFunct {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private DSLContext archDb;

	public List<eki.ekilex.data.ActivityLog> getActivityLog(
			Long ownerId, ActivityOwner owner, ActivityEntity entity, String functName) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		Condition where = al.OWNER_ID.eq(ownerId)
				.and(al.OWNER_NAME.eq(owner.name()))
				.and(al.ENTITY_NAME.eq(entity.name()));

		if (StringUtils.contains(functName, "%")) {
			where = where.and(al.FUNCT_NAME.like(functName));
		} else {
			where = where.and(al.FUNCT_NAME.eq(functName));
		}

		return mainDb
				.selectFrom(al)
				.where(where)
				.orderBy(al.ID)
				.fetchInto(eki.ekilex.data.ActivityLog.class);
	}

	public List<eki.ekilex.data.ActivityLog> getWordActivityLog(Long wordId) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		WordActivityLog wal = WORD_ACTIVITY_LOG.as("wal");
		Field<String> wvf = getWordValueField(al);

		return mainDb
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME,
						al.PREV_DIFFS,
						al.CURR_DIFFS,
						wvf.as("word_value"))
				.from(al, wal)
				.where(
						wal.WORD_ID.eq(wordId)
								.and(wal.ACTIVITY_LOG_ID.eq(al.ID)))
				.orderBy(al.EVENT_ON.desc())
				.fetchInto(eki.ekilex.data.ActivityLog.class);
	}

	public List<eki.ekilex.data.ActivityLog> getMeaningActivityLog(Long meaningId) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		Field<String> wvf = getWordValueField(al);

		return mainDb
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME,
						al.PREV_DIFFS,
						al.CURR_DIFFS,
						wvf.as("word_value"))
				.from(al, mal)
				.where(
						mal.MEANING_ID.eq(meaningId)
								.and(mal.ACTIVITY_LOG_ID.eq(al.ID)))
				.orderBy(al.EVENT_ON.desc())
				.fetchInto(eki.ekilex.data.ActivityLog.class);
	}

	private Field<String> getWordValueField(ActivityLog al) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Field<String> wvf = DSL.field(DSL
				.select(w.VALUE)
				.from(w, l)
				.where(
						l.ID.eq(al.OWNER_ID)
								.and(al.OWNER_NAME.eq(ActivityOwner.LEXEME.name()))
								.and(w.ID.eq(l.WORD_ID)))
				.unionAll(DSL
						.select(w.VALUE)
						.from(w)
						.where(
								w.ID.eq(al.OWNER_ID)
										.and(al.OWNER_NAME.eq(ActivityOwner.WORD.name())))));
		return wvf;
	}

	public List<eki.ekilex.data.ActivityLog> getSourceActivityLog(Long sourceId) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		SourceActivityLog sal = SOURCE_ACTIVITY_LOG.as("sal");

		return mainDb
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME,
						al.PREV_DIFFS,
						al.CURR_DIFFS)
				.from(al, sal)
				.where(
						sal.SOURCE_ID.eq(sourceId)
								.and(sal.ACTIVITY_LOG_ID.eq(al.ID)))
				.orderBy(al.EVENT_ON.desc())
				.fetchInto(eki.ekilex.data.ActivityLog.class);
	}

	public LocalDateTime getActivityLogEventOn(Long activityLogId) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		return mainDb
				.select(al.EVENT_ON)
				.from(al)
				.where(al.ID.eq(activityLogId))
				.fetchOneInto(LocalDateTime.class);
	}

	public Long create(eki.ekilex.data.ActivityLog activityLog) {

		Long activityLogId = mainDb
				.insertInto(
						ACTIVITY_LOG,
						ACTIVITY_LOG.EVENT_BY,
						ACTIVITY_LOG.DATASET_CODE,
						ACTIVITY_LOG.FUNCT_NAME,
						ACTIVITY_LOG.OWNER_ID,
						ACTIVITY_LOG.OWNER_NAME,
						ACTIVITY_LOG.ENTITY_ID,
						ACTIVITY_LOG.ENTITY_NAME,
						ACTIVITY_LOG.PREV_DIFFS,
						ACTIVITY_LOG.CURR_DIFFS)
				.values(
						activityLog.getEventBy(),
						activityLog.getDatasetCode(),
						activityLog.getFunctName(),
						activityLog.getOwnerId(),
						activityLog.getOwnerName().name(),
						activityLog.getEntityId(),
						activityLog.getEntityName().name(),
						convert(activityLog.getPrevDiffs()),
						convert(activityLog.getCurrDiffs()))
				.returning(ACTIVITY_LOG.ID)
				.fetchOne()
				.getId();

		archDb
				.insertInto(
						ACTIVITY_LOG_BULK,
						ACTIVITY_LOG_BULK.ACTIVITY_LOG_ID,
						ACTIVITY_LOG_BULK.OWNER_ID,
						ACTIVITY_LOG_BULK.OWNER_NAME,
						ACTIVITY_LOG_BULK.ENTITY_ID,
						ACTIVITY_LOG_BULK.ENTITY_NAME,
						ACTIVITY_LOG_BULK.PREV_DATA,
						ACTIVITY_LOG_BULK.CURR_DATA)
				.values(
						activityLogId,
						activityLog.getOwnerId(),
						activityLog.getOwnerName().name(),
						activityLog.getEntityId(),
						activityLog.getEntityName().name(),
						JSONB.valueOf(activityLog.getPrevData()),
						JSONB.valueOf(activityLog.getCurrData()))
				.execute();

		return activityLogId;
	}

	public void createLexemesActivityLogs(Long activityLogId, Long... lexemeIds) {

		for (Long lexemeId : lexemeIds) {
			mainDb
					.insertInto(
							LEXEME_ACTIVITY_LOG,
							LEXEME_ACTIVITY_LOG.LEXEME_ID,
							LEXEME_ACTIVITY_LOG.ACTIVITY_LOG_ID)
					.select(DSL
							.select(DSL.val(lexemeId), DSL.val(activityLogId))
							.whereExists(DSL.select(LEXEME.ID).from(LEXEME).where(LEXEME.ID.eq(lexemeId))))
					.execute();
		}
	}

	public void createWordsActivityLogs(Long activityLogId, Long... wordIds) {

		for (Long wordId : wordIds) {
			mainDb
					.insertInto(
							WORD_ACTIVITY_LOG,
							WORD_ACTIVITY_LOG.WORD_ID,
							WORD_ACTIVITY_LOG.ACTIVITY_LOG_ID)
					.select(DSL
							.select(DSL.val(wordId), DSL.val(activityLogId))
							.whereExists(DSL.select(WORD.ID).from(WORD).where(WORD.ID.eq(wordId))))
					.execute();
		}
	}

	public void createMeaningsActivityLogs(Long activityLogId, Long... meaningIds) {

		for (Long meaningId : meaningIds) {
			mainDb
					.insertInto(
							MEANING_ACTIVITY_LOG,
							MEANING_ACTIVITY_LOG.MEANING_ID,
							MEANING_ACTIVITY_LOG.ACTIVITY_LOG_ID)
					.select(DSL
							.select(DSL.val(meaningId), DSL.val(activityLogId))
							.whereExists(DSL.select(MEANING.ID).from(MEANING).where(MEANING.ID.eq(meaningId))))
					.execute();
		}
	}

	public Long createOrUpdateWordLastActivityLog(Long wordId) {

		WordActivityLog wal = WORD_ACTIVITY_LOG.as("wal");
		WordLastActivityLog wlal = WORD_LAST_ACTIVITY_LOG.as("wlal");
		ActivityLog al = ACTIVITY_LOG.as("al");

		Long activityLogId = mainDb
				.select(al.ID)
				.from(wal, al)
				.where(
						wal.WORD_ID.eq(wordId)
								.and(wal.ACTIVITY_LOG_ID.eq(al.ID))
								.and(al.OWNER_NAME.in(ActivityOwner.WORD.name(), ActivityOwner.LEXEME.name())))
				.orderBy(al.EVENT_ON.desc())
				.limit(1)
				.fetchOptionalInto(Long.class).orElse(null);

		if (activityLogId == null) {
			return null;
		}

		mainDb
				.update(wlal)
				.set(wlal.ACTIVITY_LOG_ID, activityLogId)
				.where(wlal.WORD_ID.eq(wordId))
				.execute();

		mainDb
				.insertInto(wlal, wlal.WORD_ID, wlal.ACTIVITY_LOG_ID)
				.select(DSL
						.select(DSL.val(wordId), DSL.val(activityLogId))
						.whereNotExists(DSL
								.select(wlal.ID)
								.from(wlal)
								.where(wlal.WORD_ID.eq(wordId))))
				.execute();

		return activityLogId;
	}

	public void createOrUpdateMeaningLastActivityLog(Long meaningId, LastActivityType lastActivityType) {

		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		MeaningLastActivityLog mlal = MEANING_LAST_ACTIVITY_LOG.as("mlal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Long activityLogId = null;

		if (LastActivityType.EDIT.equals(lastActivityType)) {

			final String[] conceptActivityWordFunctNamesOfInterest = new String[] {"createWord", "updateWordValue", "deleteWord"};
			final String conceptActivityWordFunctNamesCrit = "(" + StringUtils.join(conceptActivityWordFunctNamesOfInterest, '|') + ")";

			Table<Record2<Long, LocalDateTime>> lmlwal = DSL
					.select(al.ID, al.EVENT_ON)
					.from(mal, al)
					.where(
							mal.MEANING_ID.eq(meaningId)
									.and(mal.ACTIVITY_LOG_ID.eq(al.ID))
									.and(al.OWNER_NAME.in(ActivityOwner.MEANING.name(), ActivityOwner.LEXEME.name()))
									.and(al.FUNCT_NAME.ne(FUNCT_NAME_APPROVE_MEANING)))
					.orderBy(al.EVENT_ON.desc())
					.limit(1)
					.unionAll(DSL
							.select(al.ID, al.EVENT_ON)
							.from(mal, al)
							.where(
									mal.MEANING_ID.eq(meaningId)
											.and(mal.ACTIVITY_LOG_ID.eq(al.ID))
											.and(al.OWNER_NAME.eq(ActivityOwner.WORD.name()))
											.and(al.ENTITY_NAME.eq(ActivityEntity.WORD.name()))
											.and(al.FUNCT_NAME.similarTo(conceptActivityWordFunctNamesCrit)))
							.orderBy(al.EVENT_ON.desc())
							.limit(1))
					.asTable("lmlwal");

			activityLogId = mainDb
					.select(lmlwal.field("id", Long.class))
					.from(lmlwal)
					.orderBy(lmlwal.field("event_on").desc())
					.limit(1)
					.fetchOptionalInto(Long.class).orElse(null);

			// fallback compensation
			if (activityLogId == null) {
				activityLogId = mainDb
						.select(al.ID)
						.from(mal, al)
						.where(
								mal.MEANING_ID.eq(meaningId)
										.and(mal.ACTIVITY_LOG_ID.eq(al.ID)))
						.orderBy(al.EVENT_ON.desc())
						.limit(1)
						.fetchOptionalInto(Long.class).orElse(null);
			}

		} else if (LastActivityType.APPROVE.equals(lastActivityType)) {

			activityLogId = mainDb
					.select(al.ID)
					.from(mal, al)
					.where(
							mal.MEANING_ID.eq(meaningId)
									.and(mal.ACTIVITY_LOG_ID.eq(al.ID))
									.and(al.FUNCT_NAME.eq(FUNCT_NAME_APPROVE_MEANING)))
					.orderBy(al.EVENT_ON.desc())
					.limit(1)
					.fetchOptionalInto(Long.class).orElse(null);
		}

		if (activityLogId == null) {
			return;
		}

		mainDb
				.update(mlal)
				.set(mlal.ACTIVITY_LOG_ID, activityLogId)
				.where(mlal.MEANING_ID.eq(meaningId).and(mlal.TYPE.eq(lastActivityType.name())))
				.execute();

		mainDb
				.insertInto(mlal, mlal.MEANING_ID, mlal.ACTIVITY_LOG_ID, mlal.TYPE)
				.select(DSL
						.select(DSL.val(meaningId), DSL.val(activityLogId), DSL.val(lastActivityType.name()))
						.whereNotExists(DSL
								.select(mlal.ID)
								.from(mlal)
								.where(mlal.MEANING_ID.eq(meaningId).and(mlal.TYPE.eq(lastActivityType.name())))))
				.execute();
	}

	public void createSourceActivityLog(Long activityLogId, Long sourceId) {

		mainDb
				.insertInto(
						SOURCE_ACTIVITY_LOG,
						SOURCE_ACTIVITY_LOG.SOURCE_ID,
						SOURCE_ACTIVITY_LOG.ACTIVITY_LOG_ID)
				.select(DSL
						.select(DSL.val(sourceId), DSL.val(activityLogId))
						.whereExists(DSL.select(SOURCE.ID).from(SOURCE).where(SOURCE.ID.eq(sourceId))))
				.execute();
	}

	public void updateWordManualEventOn(Long wordId, LocalDateTime eventOn) {

		mainDb
				.update(WORD)
				.set(WORD.MANUAL_EVENT_ON, eventOn)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public void updateMeaningManualEventOn(Long meaningId, LocalDateTime eventOn) {

		mainDb
				.update(MEANING)
				.set(MEANING.MANUAL_EVENT_ON, eventOn)
				.where(MEANING.ID.eq(meaningId))
				.execute();
	}

	public void updateMeaningFirstCreateEvent(Long meaningId, LocalDateTime eventOn, String eventBy) {

		ActivityLog al1 = ACTIVITY_LOG.as("al1");
		ActivityLog al2 = ACTIVITY_LOG.as("al2");

		Table<Record1<Long>> alt = DSL
				.select(al2.ID.as("first_al_id"))
				.from(al2)
				.where(
						al2.OWNER_ID.eq(meaningId)
								.and(al2.OWNER_NAME.eq(ActivityOwner.MEANING.name()))
								.and(al2.ENTITY_NAME.eq(ActivityEntity.MEANING.name()))
								.and(al2.FUNCT_NAME.like(LIKE_CREATE)))
				.orderBy(al2.EVENT_ON)
				.limit(1)
				.asTable("alt");

		mainDb
				.update(al1)
				.set(al1.EVENT_ON, eventOn)
				.set(al1.EVENT_BY, eventBy)
				.from(alt)
				.where(al1.ID.eq(alt.field("first_al_id", Long.class)))
				.execute();
	}

	private TypeActivityLogDiffRecord[] convert(List<TypeActivityLogDiff> logDiffs) {
		if (CollectionUtils.isEmpty(logDiffs)) {
			return new TypeActivityLogDiffRecord[0];
		}
		int logSize = logDiffs.size();
		TypeActivityLogDiffRecord[] diffRecords = new TypeActivityLogDiffRecord[logSize];
		for (int recordIndex = 0; recordIndex < logSize; recordIndex++) {
			TypeActivityLogDiff diffObj = logDiffs.get(recordIndex);
			diffRecords[recordIndex] = new TypeActivityLogDiffRecord(diffObj.getOp(), diffObj.getPath(), diffObj.getValue());
		}
		return diffRecords;
	}

	public WordLexemeMeaningIds getLexemeMeaningIds(Long wordId) {

		Word w = WORD.as("w");
		Condition entityIdCond = w.ID.eq(wordId);
		return getWordLexemeMeaningIds(entityIdCond, w.ID);
	}

	public WordLexemeMeaningIds getWordMeaningIds(Long lexemeId) {

		Lexeme l = LEXEME.as("l");
		Condition entityIdCond = l.ID.eq(lexemeId);
		return getWordLexemeMeaningIds(entityIdCond, l.ID);
	}

	public WordLexemeMeaningIds getLexemeWordIds(Long meaningId) {

		Meaning m = MEANING.as("m");
		Condition entityIdCond = m.ID.eq(meaningId);
		return getWordLexemeMeaningIds(entityIdCond, m.ID);
	}

	private WordLexemeMeaningIds getWordLexemeMeaningIds(Condition entityIdCond, Field<Long> groupByField) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");

		return mainDb
				.select(
						DSL.arrayAggDistinct(l.ID).as("lexeme_ids"),
						DSL.arrayAggDistinct(w.ID).as("word_ids"),
						DSL.arrayAggDistinct(m.ID).as("meaning_ids"))
				.from(w, l, m)
				.where(
						entityIdCond
								.and(l.WORD_ID.eq(w.ID))
								.and(l.MEANING_ID.eq(m.ID)))
				.groupBy(groupByField)
				.fetchOptionalInto(WordLexemeMeaningIds.class)
				.orElse(new WordLexemeMeaningIds());
	}

	public Map<String, Object> getFirstDepthFreeformOwnerDataMap(Long freeformId) {

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		WordFreeform wff = WORD_FREEFORM.as("wff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		Definition d = DEFINITION.as("d");
		Freeform ff = FREEFORM.as("ff");

		return mainDb
				.select(
						ff.FREEFORM_TYPE_CODE,
						lff.LEXEME_ID,
						wff.WORD_ID,
						mff.MEANING_ID,
						d.MEANING_ID.as("d_meaning_id"),
						sff.SOURCE_ID)
				.from(
						ff
								.leftOuterJoin(lff).on(lff.FREEFORM_ID.eq(ff.ID))
								.leftOuterJoin(wff).on(wff.FREEFORM_ID.eq(ff.ID))
								.leftOuterJoin(mff).on(mff.FREEFORM_ID.eq(ff.ID))
								.leftOuterJoin(sff).on(sff.FREEFORM_ID.eq(ff.ID))
								.leftOuterJoin(dff).on(dff.FREEFORM_ID.eq(ff.ID))
								.leftOuterJoin(d).on(d.ID.eq(dff.DEFINITION_ID)))
				.where(ff.ID.eq(freeformId))
				.fetchOptionalMap()
				.orElse(null);
	}

	public Map<String, Object> getSecondDepthFreeformOwnerDataMap(Long freeformId) {

		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		WordFreeform wff = WORD_FREEFORM.as("wff");
		MeaningFreeform mff = MEANING_FREEFORM.as("mff");
		SourceFreeform sff = SOURCE_FREEFORM.as("sff");
		DefinitionFreeform dff = DEFINITION_FREEFORM.as("dff");
		Definition d = DEFINITION.as("d");
		Freeform ff = FREEFORM.as("ff");

		return mainDb
				.select(
						ff.FREEFORM_TYPE_CODE,
						lff.LEXEME_ID,
						wff.WORD_ID,
						mff.MEANING_ID,
						d.MEANING_ID.as("d_meaning_id"),
						sff.SOURCE_ID)
				.from(
						ff
								.leftOuterJoin(lff).on(lff.FREEFORM_ID.eq(ff.PARENT_ID))
								.leftOuterJoin(wff).on(wff.FREEFORM_ID.eq(ff.PARENT_ID))
								.leftOuterJoin(mff).on(mff.FREEFORM_ID.eq(ff.PARENT_ID))
								.leftOuterJoin(sff).on(sff.FREEFORM_ID.eq(ff.PARENT_ID))
								.leftOuterJoin(dff).on(dff.FREEFORM_ID.eq(ff.PARENT_ID))
								.leftOuterJoin(d).on(d.ID.eq(dff.DEFINITION_ID)))
				.where(ff.ID.eq(freeformId))
				.fetchOptionalMap()
				.orElse(null);
	}

	public Long getFreeformSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(FREEFORM_SOURCE_LINK.FREEFORM_ID)
				.from(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordRelationOwnerId(Long wordRelationId) {
		return mainDb
				.select(WORD_RELATION.WORD1_ID)
				.from(WORD_RELATION)
				.where(WORD_RELATION.ID.eq(wordRelationId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordTypeOwnerId(Long wordTypeId) {
		return mainDb
				.select(WORD_WORD_TYPE.WORD_ID)
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.ID.eq(wordTypeId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordEtymologyOwnerId(Long entityId) {
		return mainDb
				.select(WORD_ETYMOLOGY.WORD_ID)
				.from(WORD_ETYMOLOGY)
				.where(WORD_ETYMOLOGY.ID.eq(entityId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordOdMorphOwnerId(Long entityId) {
		return mainDb
				.select(WORD_OD_MORPH.WORD_ID)
				.from(WORD_OD_MORPH)
				.where(WORD_OD_MORPH.ID.eq(entityId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordOdRecommendationOwnerId(Long entityId) {
		return mainDb
				.select(WORD_OD_RECOMMENDATION.WORD_ID)
				.from(WORD_OD_RECOMMENDATION)
				.where(WORD_OD_RECOMMENDATION.ID.eq(entityId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getWordOdUsageOwnerId(Long entityId) {
		return mainDb
				.select(WORD_OD_USAGE.WORD_ID)
				.from(WORD_OD_USAGE)
				.where(WORD_OD_USAGE.ID.eq(entityId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningDomainOwnerId(Long meaningDomainId) {
		return mainDb
				.select(MEANING_DOMAIN.MEANING_ID)
				.from(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.ID.eq(meaningDomainId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningImageOwnerId(Long meaningImageId) {
		return mainDb
				.select(MEANING_IMAGE.MEANING_ID)
				.from(MEANING_IMAGE)
				.where(MEANING_IMAGE.ID.eq(meaningImageId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningImageSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(MEANING_IMAGE.MEANING_ID)
				.from(MEANING_IMAGE, MEANING_IMAGE_SOURCE_LINK)
				.where(
						MEANING_IMAGE_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(MEANING_IMAGE_SOURCE_LINK.MEANING_IMAGE_ID.eq(MEANING_IMAGE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningNoteOwnerId(Long noteId) {
		return mainDb
				.select(MEANING_NOTE.MEANING_ID)
				.from(MEANING_NOTE)
				.where(MEANING_NOTE.ID.eq(noteId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningNoteSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(MEANING_NOTE.MEANING_ID)
				.from(MEANING_NOTE, MEANING_NOTE_SOURCE_LINK)
				.where(
						MEANING_NOTE_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(MEANING_NOTE_SOURCE_LINK.MEANING_NOTE_ID.eq(MEANING_NOTE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getDefinitionOwnerId(Long definitionId) {
		return mainDb
				.select(DEFINITION.MEANING_ID)
				.from(DEFINITION)
				.where(DEFINITION.ID.eq(definitionId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getDefinitionNoteOwnerId(Long noteId) {
		return mainDb
				.select(DEFINITION.MEANING_ID)
				.from(DEFINITION, DEFINITION_NOTE)
				.where(
						DEFINITION_NOTE.ID.eq(noteId)
								.and(DEFINITION_NOTE.DEFINITION_ID.eq(DEFINITION.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getDefinitionNoteSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(DEFINITION.MEANING_ID)
				.from(DEFINITION, DEFINITION_NOTE, DEFINITION_NOTE_SOURCE_LINK)
				.where(
						DEFINITION_NOTE_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(DEFINITION_NOTE_SOURCE_LINK.DEFINITION_NOTE_ID.eq(DEFINITION_NOTE.ID))
								.and(DEFINITION_NOTE.DEFINITION_ID.eq(DEFINITION.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getLexemeRelationOwnerId(Long lexemeRelationId) {
		return mainDb
				.select(LEX_RELATION.LEXEME1_ID)
				.from(LEX_RELATION)
				.where(LEX_RELATION.ID.eq(lexemeRelationId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getMeaningRelationOwnerId(Long meaningRelationId) {
		return mainDb
				.select(MEANING_RELATION.MEANING1_ID)
				.from(MEANING_RELATION)
				.where(MEANING_RELATION.ID.eq(meaningRelationId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getLexemeSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(LEXEME_SOURCE_LINK.LEXEME_ID)
				.from(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getLexemeNoteOwnerId(Long noteId) {
		return mainDb
				.select(LEXEME_NOTE.LEXEME_ID)
				.from(LEXEME_NOTE)
				.where(LEXEME_NOTE.ID.eq(noteId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getLexemeNoteSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(LEXEME_NOTE.LEXEME_ID)
				.from(LEXEME_NOTE, LEXEME_NOTE_SOURCE_LINK)
				.where(
						LEXEME_NOTE_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(LEXEME_NOTE_SOURCE_LINK.LEXEME_NOTE_ID.eq(LEXEME_NOTE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getUsageOwnerId(Long usageId) {
		return mainDb
				.select(USAGE.LEXEME_ID)
				.from(USAGE)
				.where(USAGE.ID.eq(usageId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getUsageTranslationOwnerId(Long usageTranslationId) {
		return mainDb
				.select(USAGE.LEXEME_ID)
				.from(USAGE, USAGE_TRANSLATION)
				.where(
						USAGE_TRANSLATION.ID.eq(usageTranslationId)
								.and(USAGE_TRANSLATION.USAGE_ID.eq(USAGE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getUsageDefinitionOwnerId(Long usageDefinitionId) {
		return mainDb
				.select(USAGE.LEXEME_ID)
				.from(USAGE, USAGE_DEFINITION)
				.where(
						USAGE_DEFINITION.ID.eq(usageDefinitionId)
								.and(USAGE_DEFINITION.USAGE_ID.eq(USAGE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getUsageSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(USAGE.LEXEME_ID)
				.from(USAGE, USAGE_SOURCE_LINK)
				.where(
						USAGE_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(USAGE_SOURCE_LINK.USAGE_ID.eq(USAGE.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getDefinitionSourceLinkOwnerId(Long sourceLinkId) {
		return mainDb
				.select(DEFINITION.MEANING_ID)
				.from(DEFINITION, DEFINITION_SOURCE_LINK)
				.where(
						DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId)
								.and(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(DEFINITION.ID)))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public Long getParadigmOwnerId(Long paradigmId) {
		return mainDb
				.select(PARADIGM.WORD_ID)
				.from(PARADIGM)
				.where(PARADIGM.ID.eq(paradigmId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public LocalDateTime getMeaningLastActivityLog(Long meaningId, LastActivityType lastActivityType) {
		return mainDb
				.select(ACTIVITY_LOG.EVENT_ON)
				.from(MEANING_LAST_ACTIVITY_LOG, ACTIVITY_LOG)
				.where(
						MEANING_LAST_ACTIVITY_LOG.MEANING_ID.eq(meaningId)
								.and(MEANING_LAST_ACTIVITY_LOG.ACTIVITY_LOG_ID.eq(ACTIVITY_LOG.ID))
								.and(MEANING_LAST_ACTIVITY_LOG.TYPE.eq(lastActivityType.name())))
				.fetchOptionalInto(LocalDateTime.class)
				.orElse(null);
	}

	public void deleteMeaningLastActivityLog(Long meaningId, LastActivityType lastActivityType) {
		mainDb
				.deleteFrom(MEANING_LAST_ACTIVITY_LOG)
				.where(
						MEANING_LAST_ACTIVITY_LOG.MEANING_ID.eq(meaningId)
								.and(MEANING_LAST_ACTIVITY_LOG.TYPE.eq(lastActivityType.name())))
				.execute();
	}

	public void moveMeaningLastActivityLog(Long targetMeaningId, Long sourceMeaningId, LastActivityType lastActivityType) {
		mainDb
				.update(MEANING_LAST_ACTIVITY_LOG)
				.set(MEANING_LAST_ACTIVITY_LOG.MEANING_ID, targetMeaningId)
				.where(
						MEANING_LAST_ACTIVITY_LOG.MEANING_ID.eq(sourceMeaningId)
								.and(MEANING_LAST_ACTIVITY_LOG.TYPE.eq(lastActivityType.name())))
				.execute();
	}

}
