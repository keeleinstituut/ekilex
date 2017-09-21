package eki.eve.service;

import eki.eve.db.tables.*;
import org.jooq.*;
import org.jooq.conf.RenderNameStyle;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static eki.eve.db.Tables.*;
import static org.jooq.impl.DSL.max;

@Service
public class SearchService {

	private DSLContext create;

	@Autowired
	public SearchService(DSLContext context) {
		create = context;
		create.settings().setRenderSchema(false);
		create.settings().setRenderFormatted(true);
		create.settings().setRenderNameStyle(RenderNameStyle.AS_IS);
	}

	public Result<Record2<Long, String>> findForms(String searchFilter) {
		String theFilter = searchFilter.replace("*", "%").replace("?", "_");
		return create
				.select(FORM.ID, FORM.VALUE)
				.from(FORM)
				.where(FORM.VALUE.like(theFilter).and(FORM.IS_WORD.isTrue()))
				.fetch();
	}

	public Result<Record6<Long,String,String,String,String,String>> findConnectedForms(Long formId) {
		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p = PARADIGM.as("p");
		MorphLabel m = MORPH_LABEL.as("m");
		return create
				.select(f2.ID, f2.VALUE, f2.DISPLAY_FORM, f2.VOCAL_FORM, f2.MORPH_CODE, m.VALUE.as("morph_value"))
				.from(f1 , f2, p, m)
				.where(f1.ID.eq(formId).and(f1.PARADIGM_ID.eq(p.ID)).and(f2.PARADIGM_ID.eq(p.ID))
						.and(m.CODE.eq(f2.MORPH_CODE)).and(m.LANG.eq("est")).and(m.TYPE.eq("descrip")))
				.fetch();
	}

	public Result<Record4<String[], Long, String[], String[]>> findFormDefinitions(Long formId) {
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
		return create.select(arrayAggDistinct(f2.VALUE).as("words"), m.ID.as("meaning_id"), max(m.DATASET).as("datasets"), arrayAggDistinct(d.VALUE).as("definitions"))
				.from(f1, f2, p1, p2, w1, w2, l1, l2, m, d)
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
						.and(d.MEANING_ID.eq(m.ID))
						.and(f2.IS_WORD.isTrue())
				).groupBy(m.ID).fetch();
	}

	public Map<String, String> allDatasetsAsMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	private static <T> Field<T[]> arrayAggDistinct(Field<T> f) {
		return DSL.field("array_agg(distinct {0})", f.getDataType().getArrayDataType(), f);
	}
}
