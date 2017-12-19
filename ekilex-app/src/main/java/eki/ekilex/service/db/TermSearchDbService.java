package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Component
public class TermSearchDbService {

	private DSLContext create;

	@Autowired
	public TermSearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record5<Long, String, String, String, Long[]>> findFormMeanings(Long formId, List<String> datasets) {

		Form f = FORM.as("f");
		Paradigm p = PARADIGM.as("p");
		Word w = WORD.as("w");
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
				.from(m, l1, l2, w, p, f)
				.where(
						f.ID.eq(formId)
						.and(f.PARADIGM_ID.eq(p.ID))
						.and(p.WORD_ID.eq(w.ID))
						.and(l1.WORD_ID.eq(w.ID))
						.and(l1.MEANING_ID.eq(m.ID))
						.and(l2.MEANING_ID.eq(m.ID))
						.and(l1.DATASET_CODE.in(datasets))
						.and(l2.DATASET_CODE.in(datasets))
						)
				.groupBy(m.ID)
				.orderBy(m.ID)
				.fetch();
	}

	public Result<Record11<String,String,Long,Long,Long,String,Integer,Integer,Integer,String,String>> getLexemeWords(Long lexemeId) {

		return create
				.select(
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						WORD.ID.as("word_id"),
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
				.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetch();
	}
}
