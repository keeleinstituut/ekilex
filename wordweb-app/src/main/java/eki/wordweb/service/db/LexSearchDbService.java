package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;

import org.jooq.DSLContext;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LexSearchDbService {

	@Autowired
	private DSLContext create;

	public Result<Record9<Long,String,Integer,String,String,String,String[],Integer,String[]>> findWords(String searchFilter) {

		return create
				.select(
						MVIEW_WW_WORD.WORD_ID,
						MVIEW_WW_WORD.WORD,
						MVIEW_WW_WORD.HOMONYM_NR,
						MVIEW_WW_WORD.LANG,
						MVIEW_WW_WORD.MORPH_CODE,
						MVIEW_WW_WORD.DISPLAY_MORPH_CODE,
						MVIEW_WW_WORD.DATASET_CODES,
						MVIEW_WW_WORD.MEANING_COUNT,
						MVIEW_WW_WORD.MEANING_WORDS
						)
				.from(MVIEW_WW_WORD)
				.where(
						DSL.exists(
								DSL.select(MVIEW_WW_FORM.WORD_ID)
								.from(MVIEW_WW_FORM)
								.where(
										MVIEW_WW_FORM.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)
										.and(MVIEW_WW_FORM.FORM.equalIgnoreCase(searchFilter)))
								)
						)
				.orderBy(MVIEW_WW_WORD.LANG, MVIEW_WW_WORD.HOMONYM_NR)
				.fetch();
	}

}
