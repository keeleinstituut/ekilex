package eki.eve.service.db;

import static eki.eve.data.db.Tables.DATASET;
import static eki.eve.data.db.Tables.DEFINITION;
import static eki.eve.data.db.Tables.FORM;
import static eki.eve.data.db.Tables.LEXEME;
import static eki.eve.data.db.Tables.MEANING;
import static eki.eve.data.db.Tables.MORPH_LABEL;
import static eki.eve.data.db.Tables.PARADIGM;
import static eki.eve.data.db.Tables.WORD;

import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.conf.RenderNameStyle;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.eve.data.db.tables.Definition;
import eki.eve.data.db.tables.Form;
import eki.eve.data.db.tables.Lexeme;
import eki.eve.data.db.tables.Meaning;
import eki.eve.data.db.tables.MorphLabel;
import eki.eve.data.db.tables.Paradigm;
import eki.eve.data.db.tables.Word;

@Service
public class SearchDbService {

	private DSLContext create;

	@Autowired
	public SearchDbService(DSLContext context) {
		create = context;
		create.settings().setRenderSchema(false);
		create.settings().setRenderFormatted(true);
		create.settings().setRenderNameStyle(RenderNameStyle.AS_IS);
	}

	public Result<Record4<Long, String, Integer, String>> findWords(String searchFilter) {
		String theFilter = searchFilter.toLowerCase().replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID, FORM.VALUE, WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.VALUE.lower().like(theFilter)
						.and(FORM.IS_WORD.isTrue())
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.orderBy(FORM.VALUE, WORD.HOMONYM_NR)
				.fetch();
	}

	public Record4<Long, String, Integer, String> findWord(Long id) {
		return create
				.select(FORM.ID, FORM.VALUE, WORD.HOMONYM_NR, WORD.LANG)
				.from(FORM, PARADIGM, WORD)
				.where(
						FORM.ID.eq(id)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)))
				.fetchOne();
	}

	public Result<Record6<Long,String,String,String,String,String>> findConnectedForms(Long formId) {
		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p = PARADIGM.as("p");
		MorphLabel m = MORPH_LABEL.as("m");
		return create
				.select(f2.ID, f2.VALUE, f2.DISPLAY_FORM, f2.VOCAL_FORM, f2.MORPH_CODE, m.VALUE.as("morph_value"))
				.from(f1 , f2, p, m)
				.where(f1.ID.eq(formId)
						.and(f1.PARADIGM_ID.eq(p.ID))
						.and(f2.PARADIGM_ID.eq(p.ID))
						.and(m.CODE.eq(f2.MORPH_CODE))
						.and(m.LANG.eq("est"))
						.and(m.TYPE.eq("descrip")))
				.fetch();
	}

	public Result<Record7<String[], Integer, Integer, Integer, Long, String[], String[]>> findFormMeanings(Long formId) {
		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p1 = PARADIGM.as("p1");
		Paradigm p2 = PARADIGM.as("p2");
		Word w1 = WORD.as("w1");
		Word w2 = WORD.as("w2");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		return create.select(
					arrayAggDistinct(f2.VALUE).as("words"),
					l1.LEVEL1.as("level1"),
					l1.LEVEL2.as("level2"),
					l1.LEVEL2.as("level3"),
					m.ID.as("meaning_id"),
					m.DATASET.as("datasets"),
					arrayAggDistinct(d.VALUE).as("definitions"))
				.from(f1, f2, p1, p2, w1, w2, l1, l2, m.leftOuterJoin(d).on(d.MEANING_ID.eq(m.ID)))
				.where(
						f1.ID.eq(formId)
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(p1.WORD_ID.eq(w1.ID))
						.and(l1.WORD_ID.eq(w1.ID))
						.and(l1.MEANING_ID.eq(m.ID))
						.and(l2.MEANING_ID.eq(m.ID))
						.and(l2.WORD_ID.eq(w2.ID))
						.and(p2.WORD_ID.eq(w2.ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.IS_WORD.isTrue()))
				.groupBy(l1.ID, m.ID)
				.fetch();
	}

	public Map<String, String> allDatasetsAsMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	private static <T> Field<T[]> arrayAggDistinct(Field<T> f) {
		return DSL.field("array_agg(distinct {0})", f.getDataType().getArrayDataType(), f);
	}
}
