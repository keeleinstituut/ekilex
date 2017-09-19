package eki.eve.service;

import eki.eve.db.tables.Form;
import eki.eve.db.tables.MorphLabel;
import eki.eve.db.tables.Paradigm;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static eki.eve.db.Tables.*;

@Service
public class SearchService {

	private DSLContext create;

	@Autowired
	public SearchService(DSLContext context) {
		create = context;
		create.settings().setRenderSchema(false);
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

	public Result<Record1<String>> findFormDefinitions(Long formId) {
		return create.select(DEFINITION.VALUE)
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING, DEFINITION)
				.where(FORM.ID.eq(formId)
				.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
				.and(PARADIGM.WORD_ID.eq(WORD.ID))
				.and(LEXEME.WORD_ID.eq(WORD.ID))
				.and(LEXEME.MEANING_ID.eq(MEANING.ID))
				.and(DEFINITION.MEANING_ID.eq(MEANING.ID)))
				.fetch();
	}
}
