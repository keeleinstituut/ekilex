package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.MeaningActivityLog;
import eki.ekilex.data.db.main.tables.MeaningDomain;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class TermDatasetReportDbService implements GlobalConstant {

	private static final String INITIAL_CAP_PATTERN = "[[:upper:]]%";
	private static final String SPECIFIC_CHAR_PATTERN = "%(/|\\*|\\(|\\)|;|,|  )%";

	@Autowired
	private DSLContext mainDb;

	public List<eki.ekilex.data.Dataset> getDatasets(List<String> datasetCodes) {

		return mainDb
				.select(DATASET.CODE, DATASET.NAME)
				.from(DATASET)
				.where(DATASET.CODE.in(datasetCodes))
				.orderBy(DATASET.NAME)
				.fetchInto(eki.ekilex.data.Dataset.class);
	}

	public Map<String, Integer> getPublicMeaningCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Condition publicWordExistsCondition = getPublicWordExistsCondition(m, l, w, ds);

		Field<Integer> publicMeaningCount = DSL
				.selectCount()
				.from(m)
				.where(publicWordExistsCondition)
				.asField();

		return executeCountByDataset(ds, publicMeaningCount, datasetCodes);
	}

	public Map<String, Integer> getAllMeaningCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");

		Field<Integer> allMeaningCount = DSL
				.selectCount()
				.from(m)
				.where(DSL.exists(
						DSL.selectOne()
								.from(l)
								.where(
										l.MEANING_ID.eq(m.ID)
												.and(l.DATASET_CODE.eq(ds.CODE))
												.and(l.IS_WORD.isTrue()))))
				.asField();

		return executeCountByDataset(ds, allMeaningCount, datasetCodes);
	}

	public Map<String, Integer> getPublicTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<Integer> publicTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.and(DSL.exists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())))))
				.asField();

		return executeCountByDataset(ds, publicTermCount, datasetCodes);
	}

	public Map<String, Integer> getAllTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<Integer> allTermCount = DSL
				.selectCount()
				.from(w)
				.where(DSL.exists(
						DSL.selectOne()
								.from(l)
								.where(
										l.WORD_ID.eq(w.ID)
												.and(l.DATASET_CODE.eq(ds.CODE))
												.and(l.IS_WORD.isTrue()))))
				.asField();

		return executeCountByDataset(ds, allTermCount, datasetCodes);
	}

	public Map<String, Integer> getCreateMeaningCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");

		Condition publicTermExistsCondition = getPublicWordExistsCondition(m, l, w, ds);

		Field<LocalDateTime> firstEventOn = DSL
				.select(al.EVENT_ON)
				.from(mal, al)
				.where(
						mal.MEANING_ID.eq(m.ID)
								.and(mal.ACTIVITY_LOG_ID.eq(al.ID)))
				.orderBy(al.EVENT_ON.asc())
				.limit(1)
				.asField("first_event_on");

		Field<Integer> createMeaningCount = DSL
				.selectCount()
				.from(DSL.select(m.ID, firstEventOn)
						.from(m)
						.where(publicTermExistsCondition)
						.asTable("m"))
				.where(firstEventOn.ge(from))
				.and(firstEventOn.lt(until))
				.asField();

		return executeCountByDataset(ds, createMeaningCount, datasetCodes);
	}

	public Map<String, Integer> getUpdateMeaningCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");

		Condition publicWordExistsCondition = getPublicWordExistsCondition(m, l, w, ds);
		Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);

		Field<Integer> updateMeaningCount = DSL
				.selectCount()
				.from(m)
				.where(
						publicWordExistsCondition
								.and(meaningUpdatedCondition))
				.asField();

		return executeCountByDataset(ds, updateMeaningCount, datasetCodes);
	}

	public Map<String, Integer> getWithDomainMeaningCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		MeaningDomain md = MEANING_DOMAIN.as("md");

		Condition publicWordExistsCondition = getPublicWordExistsCondition(m, l, w, ds);

		Field<Integer> wDomainMeaningCount = DSL
				.selectCount()
				.from(m)
				.where(
						publicWordExistsCondition
								.and(DSL.exists(
										DSL.selectOne()
												.from(md)
												.where(md.MEANING_ID.eq(m.ID)))))
				.asField();

		return executeCountByDataset(ds, wDomainMeaningCount, datasetCodes);
	}

	public Map<String, Integer> getWithDomainUpdateMeaningCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		MeaningDomain md = MEANING_DOMAIN.as("md");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");

		Condition publicWordExistsCondition = getPublicWordExistsCondition(m, l, w, ds);
		Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);

		Field<Integer> wDomainUpdateMeaningCount = DSL
				.selectCount()
				.from(m)
				.where(
						publicWordExistsCondition
								.and(DSL.exists(
										DSL.selectOne()
												.from(md)
												.where(md.MEANING_ID.eq(m.ID))))
								.and(meaningUpdatedCondition))
				.asField();

		return executeCountByDataset(ds, wDomainUpdateMeaningCount, datasetCodes);
	}

	public Map<String, String> getWithoutDomainTermSamples(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		MeaningDomain md = MEANING_DOMAIN.as("md");

		Field<String> wordValue = DSL.arrayGet(DSL.arrayAgg(w.VALUE).orderBy(w.ID), 1).as("word_value");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		Table<Record1<String>> rw = DSL
				.select(wordValue)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(publicWordCondition)
								.and(w.LANG.eq(GlobalConstant.LANGUAGE_CODE_EST))
								.and(DSL.notExists(
										DSL.selectOne()
												.from(md)
												.where(md.MEANING_ID.eq(m.ID)))))
				.groupBy(m.ID)
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("rw");

		Field<String> sampleField = getSampleField(rw, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	public Map<String, Integer> getSingleTermMeaningCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Field<Integer> wordCount = DSL.count(w.ID).as("word_count");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		Table<Record2<Long, Integer>> ms = DSL
				.select(m.ID, wordCount)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(publicWordCondition))
				.groupBy(m.ID)
				.asTable("ms");

		Field<Integer> singleTermMeaningCount = DSL
				.selectCount()
				.from(ms)
				.where(ms.field(wordCount).eq(1))
				.asField();

		return executeCountByDataset(ds, singleTermMeaningCount, datasetCodes);
	}

	public Map<String, String> getSingleTermMeaningTermSamples(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Field<Integer> wordCount = DSL.count(w.ID).as("word_count");
		Field<String> wordValue = DSL.arrayGet(DSL.arrayAgg(w.VALUE), 1).as("word_value");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		Table<Record3<Long, Integer, String>> msInner = DSL
				.select(m.ID, wordCount, wordValue)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(publicWordCondition))
				.groupBy(m.ID)
				.asTable("ms_inner");

		Table<Record1<String>> ms = DSL
				.select(msInner.field(wordValue))
				.from(msInner)
				.where(msInner.field(wordCount).eq(1))
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("ms");

		Field<String> sampleField = getSampleField(ms, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	public Map<String, Integer> getSingleLangMeaningCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Field<Integer> langCount = DSL.countDistinct(w.LANG).as("lang_count");

		Table<Record2<Long, Integer>> ms = DSL
				.select(m.ID, langCount)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.DATASET_CODE.eq(ds.CODE))
								.and(l.IS_WORD.isTrue()))
				.groupBy(m.ID)
				.asTable("ms");

		Field<Integer> singleLangMeaningCount = DSL
				.selectCount()
				.from(ms)
				.where(ms.field(langCount).eq(1))
				.asField();

		return executeCountByDataset(ds, singleLangMeaningCount, datasetCodes);
	}

	public Map<String, String> getSingleLangMeaningTermSamples(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Field<Integer> langCount = DSL.countDistinct(w.LANG).as("lang_count");
		Field<String> wordValue = DSL.arrayGet(DSL.arrayAgg(w.VALUE).orderBy(w.ID), 1).as("word_value");

		Table<Record3<Long, Integer, String>> msInner = DSL
				.select(m.ID, langCount, wordValue)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(l.WORD_ID.eq(w.ID))
								.and(l.DATASET_CODE.eq(ds.CODE))
								.and(l.IS_WORD.isTrue()))
				.groupBy(m.ID)
				.asTable("ms_inner");

		Table<Record1<String>> ms = DSL
				.select(msInner.field(wordValue))
				.from(msInner)
				.where(msInner.field(langCount).eq(1))
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("ms");

		Field<String> sampleField = getSampleField(ms, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	public Map<String, Integer> getSpecificCharTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<Integer> specificCharTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.VALUE.similarTo(SPECIFIC_CHAR_PATTERN)
								.and(w.IS_PUBLIC.isTrue())
								.and(DSL.exists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())))))
				.asField();

		return executeCountByDataset(ds, specificCharTermCount, datasetCodes);
	}

	public Map<String, String> getSpecificCharTermSamples(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");

		Field<String> wordValue = DSL.arrayGet(DSL.arrayAgg(w.VALUE).orderBy(w.ID), 1).as("word_value");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		Table<Record1<String>> rw = DSL
				.select(wordValue)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(publicWordCondition)
								.and(w.VALUE.similarTo(SPECIFIC_CHAR_PATTERN)))
				.groupBy(m.ID)
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("rw");

		Field<String> sampleField = getSampleField(rw, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	public Map<String, Integer> getInitialCapTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<Integer> initCapTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.VALUE.similarTo(INITIAL_CAP_PATTERN)
								.and(w.IS_PUBLIC.isTrue())
								.and(DSL.exists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())))))
				.asField();

		return executeCountByDataset(ds, initCapTermCount, datasetCodes);
	}

	public Map<String, String> getInitialCapTermSamples(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Field<String> wordValue = w.VALUE.as("word_value");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		Table<Record1<String>> ws = DSL
				.select(wordValue)
				.from(l, w)
				.where(
						publicWordCondition
								.and(w.VALUE.similarTo(INITIAL_CAP_PATTERN)))
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("ws");

		Field<String> sampleField = getSampleField(ws, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	public Map<String, Integer> getWithSourceLinkTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition lexemeSourceLinkExistsCondition = getLexemeSourceLinkExistsCondition(l, lsl);

		Field<Integer> withSourceLinkTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.and(DSL.exists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())
																.and(lexemeSourceLinkExistsCondition)))))
				.asField();

		return executeCountByDataset(ds, withSourceLinkTermCount, datasetCodes);
	}

	public Map<String, Integer> getWithSourceLinkMeaningUpdateTermCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);
		Condition lexemeSourceLinkExistsCondition = getLexemeSourceLinkExistsCondition(l, lsl);

		Field<Integer> withSourceLinkMeaningUpdateTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.and(DSL.exists(
										DSL.selectOne()
												.from(l, m)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.MEANING_ID.eq(m.ID))
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())
																.and(lexemeSourceLinkExistsCondition)
																.and(meaningUpdatedCondition)))))
				.asField();

		return executeCountByDataset(ds, withSourceLinkMeaningUpdateTermCount, datasetCodes);
	}

	public Map<String, Integer> getWithoutSourceLinkTermCounts(List<String> datasetCodes) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition lexemeSourceLinkExistsCondition = getLexemeSourceLinkExistsCondition(l, lsl);

		Field<Integer> withoutSourceLinkTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.and(DSL.exists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue()))))
								.and(DSL.notExists(
										DSL.selectOne()
												.from(l)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())
																.and(lexemeSourceLinkExistsCondition)))))
				.asField();

		return executeCountByDataset(ds, withoutSourceLinkTermCount, datasetCodes);
	}

	public Map<String, Integer> getWithoutSourceLinkMeaningUpdateTermCounts(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition lexemeSourceLinkNotExistsCondition = getLexemeSourceLinkExistsCondition(l, lsl).not();
		Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);

		Field<Integer> withoutSourceLinkMeaningUpdateTermCount = DSL
				.selectCount()
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.and(DSL.exists(
										DSL.selectOne()
												.from(l, m)
												.where(
														l.WORD_ID.eq(w.ID)
																.and(l.MEANING_ID.eq(m.ID))
																.and(l.DATASET_CODE.eq(ds.CODE))
																.and(l.IS_WORD.isTrue())
																.and(l.IS_PUBLIC.isTrue())
																.and(lexemeSourceLinkNotExistsCondition)
																.and(meaningUpdatedCondition)))))
				.asField();

		return executeCountByDataset(ds, withoutSourceLinkMeaningUpdateTermCount, datasetCodes);
	}

	public Map<String, String> getWithoutSourceLinkMeaningUpdateTermSamples(List<String> datasetCodes, LocalDateTime from, LocalDateTime until) {

		Dataset ds = DATASET.as("ds");
		Lexeme l = LEXEME.as("l");
		Meaning m = MEANING.as("m");
		Word w = WORD.as("w");
		ActivityLog al = ACTIVITY_LOG.as("al");
		MeaningActivityLog mal = MEANING_ACTIVITY_LOG.as("mal");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);
		Condition lexemeSourceLinkNotExistsCondition = getLexemeSourceLinkExistsCondition(l, lsl).not();
		Condition meaningUpdatedCondition = getMeaningUpdatedInPeriodCondition(m, al, mal, from, until);

		Field<String> wordValue = DSL.arrayGet(DSL.arrayAgg(w.VALUE), 1).as("word_value");

		Table<Record1<String>> rw = DSL
				.select(wordValue)
				.from(m, l, w)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(publicWordCondition)
								.and(lexemeSourceLinkNotExistsCondition)
								.and(meaningUpdatedCondition))
				.groupBy(m.ID)
				.orderBy(DSL.rand())
				.limit(3)
				.asTable("rw");

		Field<String> sampleField = getSampleField(rw, wordValue);

		return executeFetchSampleByDataset(ds, sampleField, datasetCodes);
	}

	private Field<String> getSampleField(Table<?> sampleTable, Field<String> valueField) {

		Field<String> sampleValue = sampleTable.field(valueField);

		return DSL
				.select(DSL.field("array_to_string({0}, ' | ')", String.class, DSL.arrayAggDistinct(sampleValue)))
				.from(sampleTable)
				.asField();
	}

	private Condition getPublicWordExistsCondition(Meaning m, Lexeme l, Word w, Dataset ds) {

		Condition publicWordCondition = getPublicWordCondition(l, w, ds);

		return DSL.exists(
				DSL.selectOne()
						.from(l, w)
						.where(
								l.MEANING_ID.eq(m.ID)
										.and(publicWordCondition)));
	}

	private Condition getPublicWordCondition(Lexeme l, Word w, Dataset ds) {

		return l.WORD_ID.eq(w.ID)
				.and(l.DATASET_CODE.eq(ds.CODE))
				.and(l.IS_WORD.isTrue())
				.and(l.IS_PUBLIC.isTrue())
				.and(w.IS_PUBLIC.isTrue());
	}

	private Condition getLexemeSourceLinkExistsCondition(Lexeme l, LexemeSourceLink lsl) {

		return DSL.exists(
				DSL.selectOne()
						.from(lsl)
						.where(lsl.LEXEME_ID.eq(l.ID)));
	}

	private Condition getMeaningUpdatedInPeriodCondition(
			Meaning m, ActivityLog al, MeaningActivityLog mal, LocalDateTime from, LocalDateTime until) {

		return DSL.or(
				DSL.exists(
						DSL.selectOne()
								.from(mal, al)
								.where(
										mal.MEANING_ID.eq(m.ID)
												.and(mal.ACTIVITY_LOG_ID.eq(al.ID))
												.and(al.OWNER_NAME.in(
														ActivityOwner.MEANING.name(),
														ActivityOwner.LEXEME.name()))
												.and(al.EVENT_ON.ge(from))
												.and(al.EVENT_ON.lt(until)))),
				DSL.exists(
						DSL.selectOne()
								.from(mal, al)
								.where(
										mal.MEANING_ID.eq(m.ID)
												.and(mal.ACTIVITY_LOG_ID.eq(al.ID))
												.and(al.OWNER_NAME.eq(ActivityOwner.WORD.name()))
												.and(al.ENTITY_NAME.notIn(
														ActivityEntity.GRAMMAR.name(),
														ActivityEntity.WORD_TYPE.name(),
														ActivityEntity.WORD_TAG.name(),
														ActivityEntity.WORD_NOTE.name(),
														ActivityEntity.WORD_RELATION.name(),
														ActivityEntity.WORD_RELATION_GROUP_MEMBER.name(),
														ActivityEntity.WORD_ETYMOLOGY.name(),
														ActivityEntity.WORD_OS_MORPH.name(),
														ActivityEntity.WORD_OS_USAGE.name(),
														ActivityEntity.PARADIGM.name(),
														ActivityEntity.FORM.name(),
														ActivityEntity.WORD_EKI_RECOMMENDATION.name(),
														ActivityEntity.TAG.name(),
														ActivityEntity.PUBLISHING.name()))
												.and(al.FUNCT_NAME.notLike("%join%"))
												.and(al.EVENT_ON.ge(from))
												.and(al.EVENT_ON.lt(until)))));
	}

	private Map<String, Integer> executeCountByDataset(Dataset ds, Field<Integer> countField, List<String> datasetCodes) {

		return mainDb
				.select(ds.CODE, countField, DSL.val(IGNORE_QUERY_LOG))
				.from(ds)
				.where(ds.CODE.in(datasetCodes))
				.fetchMap(ds.CODE, countField);
	}

	private Map<String, String> executeFetchSampleByDataset(Dataset ds, Field<String> sampleField, List<String> datasetCodes) {

		return mainDb
				.select(ds.CODE, sampleField, DSL.val(IGNORE_QUERY_LOG))
				.from(ds)
				.where(ds.CODE.in(datasetCodes))
				.fetchMap(ds.CODE, sampleField);
	}
}
