package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.WordTuple;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Component
public class TermSearchDbService {

	public static final int MAX_RESULTS_LIMIT = 50;

	private DSLContext create;

	@Autowired
	public TermSearchDbService(DSLContext context) {
		create = context;
	}

	public Map<Long, List<WordTuple>> findMeaningsAsMap(String wordWithMetaCharacters, List<String> datasets, boolean fetchAll) {

		Table<Record1<Long>> m = getFilteredMeanings(wordWithMetaCharacters, datasets, fetchAll);

		Form f2 = FORM.as("f2");
		Paradigm p2 = PARADIGM.as("p2");
		Word w2 = WORD.as("w2");
		Lexeme l2 = LEXEME.as("l2");
		Table<Record5<Long, Long, String, Integer, String>> w = DSL
				.select(
					l2.MEANING_ID,
					w2.ID.as("word_id"),
					w2.LANG.as("word_lang"),
					w2.HOMONYM_NR,
					f2.VALUE.as("word"))
				.from(f2, p2, w2, l2)
				.where(
					f2.IS_WORD.isTrue()
					.and(f2.PARADIGM_ID.eq(p2.ID))
					.and(p2.WORD_ID.eq(w2.ID))
					.and(l2.WORD_ID.eq(w2.ID))
				).asTable("w");

		Table<Record2<Long, String>> c = DSL
				.select(
					MEANING_FREEFORM.MEANING_ID,
					FREEFORM.VALUE_TEXT.as("concept_id"))
				.from(MEANING_FREEFORM, FREEFORM)
				.where(
					MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)
					.and(FREEFORM.TYPE.eq(FreeformType.CONCEPT_ID.name()))
				).asTable("c");

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class)))
				.groupBy(w.field("word_id")));

		Table<Record7<Long, Long, String, Integer, String, String, String[]>> ww = DSL
				.select(
					m.field("meaning_id", Long.class),
					w.field("word_id", Long.class), 
					w.field("word_lang", String.class),
					w.field("homonym_nr", Integer.class),
					w.field("word", String.class),
					DSL.coalesce(c.field("concept_id"), "#").as("concept_id").cast(String.class),
					dscf.as("dataset_codes").cast(String[].class))
				.from(
					m.leftOuterJoin(w).on(w.field("meaning_id").cast(Long.class).eq(m.field("meaning_id").cast(Long.class)))
					.leftOuterJoin(c).on(c.field("meaning_id").cast(Long.class).eq(m.field("meaning_id").cast(Long.class)))
				).asTable("ww");

		Map<Long, List<WordTuple>> result = create
				.selectFrom(ww)
				.fetchGroups(ww.field("meaning_id", Long.class), WordTuple.class);
		return result;
	}

	public int countMeanings(String wordWithMetaCharacters, List<String> datasets) {

		Table<Record1<Long>> m = getFilteredMeanings(wordWithMetaCharacters, datasets, true);

		return create.fetchCount(m);
	}

	private Table<Record1<Long>> getFilteredMeanings(String wordWithMetaCharacters, List<String> datasets, boolean fetchAll) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");

		Condition where1 = FORM.IS_WORD.isTrue();
		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where1 = where1.and(FORM.VALUE.likeIgnoreCase(theFilter));			
		} else {
			where1 = where1.and(FORM.VALUE.equalIgnoreCase(theFilter));
		}
		Paradigm p1 = PARADIGM.as("p1");
		Word w1 = WORD.as("w1");
		Lexeme l1 = LEXEME.as("l1");
		Table<Record1<Long>> f1 = DSL.select(FORM.PARADIGM_ID).from(FORM).where(where1).asTable("f1");

		Condition where11 = f1.field(FORM.PARADIGM_ID).eq(p1.ID).and(p1.WORD_ID.eq(w1.ID)).and(l1.WORD_ID.eq(w1.ID));

		if (CollectionUtils.isNotEmpty(datasets)) {
			where11 = where11.and(l1.DATASET_CODE.in(datasets));
		}

		int limit = MAX_RESULTS_LIMIT;
		if (fetchAll) {
			limit = Integer.MAX_VALUE;
		}

		Table<Record1<Long>> m = DSL
				.select(l1.MEANING_ID)
				.from(f1, p1, w1, l1)
				.where(where11)
				.groupBy(l1.MEANING_ID)
				.orderBy(l1.MEANING_ID)
				.limit(limit)
				.asTable("m");
		return m;
	}

	public Result<Record5<Long, String, String, String, Long[]>> findWordMeanings(Long wordId, List<String> datasets) {

		Meaning m = MEANING.as("m");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");

		return create
				.select(
						m.ID.as("meaning_id"),
						m.TYPE_CODE.as("meaning_type_code"),
						m.PROCESS_STATE_CODE.as("meaning_process_state_code"),
						m.STATE_CODE.as("meaning_state_code"),
						DSL.arrayAggDistinct(l2.ID).as("lexeme_ids"))
				.from(m, l1, l2)
				.where(
						l1.WORD_ID.eq(wordId)
						.and(l1.MEANING_ID.eq(m.ID))
						.and(l2.MEANING_ID.eq(m.ID))
						.and(l1.DATASET_CODE.in(datasets))
						.and(l2.DATASET_CODE.in(datasets))
						)
				.groupBy(m.ID)
				.orderBy(m.ID)
				.fetch();
	}

	public Result<Record13<String,Integer,String,Long,String,Long,Long,String,Integer,Integer,Integer,String,String>> getLexemeWords(Long lexemeId) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG.as("word_lang"),
						WORD.ID.as("word_id"),
						WORD.GENDER_CODE.as("gender_code"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.MEANING_ID,
						LEXEME.DATASET_CODE.as("dataset"),
						LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.TYPE_CODE.as("lexeme_type_code"),
						LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"))
				.from(FORM, PARADIGM, WORD, LEXEME)
				.where(
						LEXEME.ID.eq(lexemeId)
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.groupBy(LEXEME.ID, WORD.ID, FORM.VALUE)
				.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}
}
