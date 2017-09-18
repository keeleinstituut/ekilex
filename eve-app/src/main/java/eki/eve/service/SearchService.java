package eki.eve.service;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static eki.eve.db.Tables.FORM;

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
}
