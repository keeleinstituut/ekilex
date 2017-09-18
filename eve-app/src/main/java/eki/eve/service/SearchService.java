package eki.eve.service;

import eki.eve.db.tables.Form;
import eki.eve.db.tables.Paradigm;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
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

	public Result<Record2<Long, String>> findConnectedForms(Long formId) {
		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");
		Paradigm p = PARADIGM.as("p");
		return create
				.select(f2.ID, f2.VALUE)
				.from(f1 , f2, p)
				.where(f1.ID.eq(formId).and(f1.PARADIGM_ID.eq(p.ID)).and(f2.PARADIGM_ID.eq(p.ID)))
				.fetch();
	}

	public Result<Record1<String>> findFormDefinitions(Long formId) {
		return create.select(DEFINITION.VALUE)
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING, DEFINITION)
				.where(FORM.ID.eq(formId))
				.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
				.and(PARADIGM.WORD_ID.eq(WORD.ID))
				.and(LEXEME.WORD_ID.eq(WORD.ID))
				.and(LEXEME.MEANING_ID.eq(MEANING.ID))
				.and(DEFINITION.MEANING_ID.eq(MEANING.ID))
				.fetch();
	}
}
