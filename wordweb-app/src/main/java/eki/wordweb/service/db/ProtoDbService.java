package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_SEARCH;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.db.Routines;
import eki.wordweb.data.db.tables.MviewWwWordSearch;

@Component
public class ProtoDbService implements SystemConstant {

	private static final int MAX_RESULTS = 15;

	@Autowired
	private DSLContext create;

	public List<String> searchInfixLevenshteinOrder(String wordCrit, String wordCritUnaccent) {

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		Field<String> wordCritLowerLikeField = DSL.lower('%' + wordCrit + '%');
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Integer>> ws = null;

		Field<Integer> wlf = DSL.field(Routines.levenshtein1(w.WORD_VALUE, wordCritLowerField));

		SelectConditionStep<Record3<String, Long, Integer>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wlf.as("lev"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(w.CRIT.like(wordCritLowerLikeField)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String> wordCritLowerUnaccentLikeField = DSL.lower('%' + wordCritUnaccent + '%');
			Field<Integer> awlf = DSL.field(Routines.levenshtein1(aw.WORD_VALUE, wordCritLowerField));

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awlf.as("lev"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(aw.CRIT.like(wordCritLowerUnaccentLikeField))))
					.asTable("ws");
		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("lev"))
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}

	public List<String> searchTrigramSimilarity(String wordCrit, String wordCritUnaccent) {

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Float>> ws = null;

		Field<Float> wsf = DSL.field(Routines.similarity(w.WORD_VALUE, wordCritLowerField));

		SelectConditionStep<Record3<String, Long, Float>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wsf.as("sim"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(DSL.condition("{0} %> {1}", w.CRIT, wordCritLowerField)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String> wordCritLowerUnaccentField = DSL.lower(wordCritUnaccent);
			Field<Float> awsf = DSL.field(Routines.similarity(aw.WORD_VALUE, wordCritLowerField));

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awsf.as("sim"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(DSL.condition("{0} %> {1}", aw.CRIT, wordCritLowerUnaccentField))))
					.asTable("ws");

		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("sim").desc())
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}

	public List<String> searchLevenshteinDistance(String wordCrit, String wordCritUnaccent) {

		final int MAX_LEV_DIST = 2;

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Integer>> ws = null;

		Field<Integer> wsf = DSL.field(Routines.levenshtein1(w.WORD_VALUE, wordCritLowerField));

		SelectConditionStep<Record3<String, Long, Integer>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wsf.as("lev"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(Routines.levenshtein1(w.CRIT, wordCritLowerField).le(MAX_LEV_DIST)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String> wordCritLowerUnaccentField = DSL.lower(wordCritUnaccent);
			Field<Integer> awsf = DSL.field(Routines.levenshtein1(aw.WORD_VALUE, wordCritLowerField));

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awsf.as("lev"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(Routines.levenshtein1(aw.CRIT, wordCritLowerUnaccentField).le(MAX_LEV_DIST))))
					.asTable("ws");

		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("lev"))
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}

	public List<String> searchLevenshteinLessDistance(String wordCrit, String wordCritUnaccent) {

		final int D_MAX = 2;
		final int MAX_LEV_DIST = 2;

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Integer>> ws = null;

		Field<Integer> wsf = DSL.field(Routines.levenshteinLessEqual1(w.WORD_VALUE, wordCritLowerField, DSL.val(MAX_LEV_DIST)));

		SelectConditionStep<Record3<String, Long, Integer>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wsf.as("lev"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(Routines.levenshteinLessEqual1(w.CRIT, wordCritLowerField, DSL.val(D_MAX)).le(MAX_LEV_DIST)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String> wordCritLowerUnaccentField = DSL.lower(wordCritUnaccent);
			Field<Integer> awsf = DSL.field(Routines.levenshteinLessEqual1(aw.WORD_VALUE, wordCritLowerField, DSL.val(MAX_LEV_DIST)));

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awsf.as("lev"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(Routines.levenshteinLessEqual1(aw.CRIT, wordCritLowerUnaccentField, DSL.val(D_MAX)).le(MAX_LEV_DIST))))
					.asTable("ws");

		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("lev"))
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}

	public List<String> searchMetaphoneMatchSimilarityOrder(String wordCrit, String wordCritUnaccent) {

		final int METAPHONE_PRECISION = 5;
		final int CRIT_MAX_LENGTH = 100;

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		Field<String> wordCritMetaphoneField = Routines.metaphone(DSL.lower(wordCrit), DSL.val(METAPHONE_PRECISION));
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Float>> ws = null;

		Field<Float> wsf = DSL.field("{0} <-> {1}", Float.class, w.CRIT, wordCritLowerField);

		SelectConditionStep<Record3<String, Long, Float>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wsf.as("dist"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(Routines.metaphone(DSL.substring(w.CRIT, 1, CRIT_MAX_LENGTH), DSL.val(METAPHONE_PRECISION)).eq(wordCritMetaphoneField)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String> wordCritUnaccentMetaphoneField = Routines.metaphone(DSL.lower(wordCritUnaccent), DSL.val(METAPHONE_PRECISION));
			Field<Float> awsf = DSL.field("{0} <-> {1}", Float.class, aw.CRIT, wordCritLowerField);

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awsf.as("dist"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(Routines.metaphone(DSL.substring(aw.CRIT, 1, CRIT_MAX_LENGTH), DSL.val(METAPHONE_PRECISION)).eq(wordCritUnaccentMetaphoneField))))
					.asTable("ws");

		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("dist"))
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}

	// Requires PG ver 16 or more!
	public List<String> searchDaitchMokotoffSoundexMatchSimilarityOrder(String wordCrit, String wordCritUnaccent) {

		Field<String> wordCritLowerField = DSL.lower(wordCrit);
		Field<String[]> wordCritDaitchMokotoffField = Routines.daitchMokotoff(DSL.lower(wordCrit));
		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		Table<Record3<String, Long, Float>> ws = null;

		Field<Float> wsf = DSL.field("{0} <-> {1}", Float.class, w.CRIT, wordCritLowerField);

		SelectConditionStep<Record3<String, Long, Float>> wselect = DSL
				.select(
						w.WORD_VALUE,
						w.LANG_ORDER_BY,
						wsf.as("dist"))
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
						.and(Routines.daitchMokotoff(w.CRIT).eq(wordCritDaitchMokotoffField)));

		boolean includeAsWordSearch = StringUtils.isNotBlank(wordCritUnaccent);

		if (includeAsWordSearch) {

			Field<String[]> wordCritUnaccentDaitchMokotoffField = Routines.daitchMokotoff(DSL.lower(wordCritUnaccent));
			Field<Float> awsf = DSL.field("{0} <-> {1}", Float.class, aw.CRIT, wordCritLowerField);

			ws = wselect
					.unionAll(DSL
							.select(
									aw.WORD_VALUE,
									aw.LANG_ORDER_BY,
									awsf.as("dist"))
							.from(aw)
							.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
									.and(Routines.daitchMokotoff(aw.CRIT).eq(wordCritUnaccentDaitchMokotoffField))))
					.asTable("ws");

		} else {

			ws = wselect
					.asTable("ws");
		}

		return create
				.select(ws.field("word_value"))
				.from(ws)
				.orderBy(
						ws.field("lang_order_by"),
						ws.field("dist"))
				.limit(MAX_RESULTS)
				.fetchInto(String.class);
	}
}
