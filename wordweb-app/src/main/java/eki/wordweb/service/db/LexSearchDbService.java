package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record17;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.udt.records.TypeDomainRecord;

@Component
public class LexSearchDbService {

	@Autowired
	private DSLContext create;

	public Result<Record9<Long,String,Integer,String,String,String,String[],Integer,String[]>> findWords(String searchFilter) {

		String theFilter = searchFilter.replace("*", "%").replace("?", "_");
		Condition formEqualsCondition;
		if (StringUtils.containsAny(theFilter, '%', '_')) {
			formEqualsCondition = MVIEW_WW_FORM.FORM.likeIgnoreCase(theFilter);
		} else {
			formEqualsCondition = MVIEW_WW_FORM.FORM.equalIgnoreCase(theFilter);
		}

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
										.and(formEqualsCondition))
								)
						)
				.orderBy(MVIEW_WW_WORD.LANG, MVIEW_WW_WORD.HOMONYM_NR)
				.fetch();
	}

	public Result<Record17<Long,Long,Long,String,String,Integer,Integer,Integer,String,String[],String[],String[],TypeDomainRecord[],Long,String,Integer,String>> findLexemeMeaningTuples(Long wordId) {

		MviewWwMeaning m1 = MVIEW_WW_MEANING.as("m1");
		MviewWwMeaning m2 = MVIEW_WW_MEANING.as("m2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");

		return create
				.select(
						m1.LEXEME_ID,
						m1.MEANING_ID,
						m1.DEFINITION_ID,
						m1.DEFINITION,
						m1.DATASET_CODE,
						m1.LEVEL1,
						m1.LEVEL2,
						m1.LEVEL3,
						m1.LEXEME_TYPE_CODE,
						m1.REGISTER_CODES,
						m1.POS_CODES,
						m1.DERIV_CODES,
						m1.DOMAIN_CODES,
						m2.WORD_ID.as("meaning_word_id"),
						w2.WORD.as("meaning_word"),
						w2.HOMONYM_NR.as("meaning_word_homonym_nr"),
						w2.LANG.as("meaning_word_lang")
						)
				.from(m1
						.leftOuterJoin(m2).on(m2.MEANING_ID.eq(m1.MEANING_ID).and(m2.WORD_ID.ne(m1.WORD_ID)))
						.leftOuterJoin(w2).on(w2.WORD_ID.eq(m2.WORD_ID))
						)
				.where(m1.WORD_ID.eq(wordId))
				.orderBy(m1.DATASET_CODE, m1.LEVEL1, m1.LEVEL2, m1.LEVEL3)
				.fetch();
	}

}
