package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.Form;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwWord;

@Component
public class LexSearchDbService {

	@Autowired
	private DSLContext create;

	public List<Word> findWords(String searchFilter, String lang, String[] datasets) {

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
						MVIEW_WW_WORD.MEANING_WORDS,
						MVIEW_WW_WORD.DEFINITIONS)
				.from(MVIEW_WW_WORD)
				.where(
						MVIEW_WW_WORD.LANG.eq(lang)
						.and(DSL.condition("{0} && {1}", MVIEW_WW_WORD.DATASET_CODES, DSL.val(datasets)))
						.and(DSL.exists(DSL.select(MVIEW_WW_FORM.WORD_ID)
												.from(MVIEW_WW_FORM)
												.where(MVIEW_WW_FORM.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)
														.and(MVIEW_WW_FORM.FORM.equalIgnoreCase(searchFilter))))))
				.orderBy(MVIEW_WW_WORD.LANG, MVIEW_WW_WORD.HOMONYM_NR)
				.fetch()
				.into(Word.class);
	}

	public Word getWord(Long wordId) {

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
						MVIEW_WW_WORD.MEANING_WORDS,
						MVIEW_WW_WORD.DEFINITIONS
						)
				.from(MVIEW_WW_WORD)
				.where(MVIEW_WW_WORD.WORD_ID.eq(wordId))
				.fetchOne()
				.into(Word.class);
	}

	public List<LexemeMeaningTuple> findLexemeMeaningTuples(Long wordId, String[] datasets) {

		MviewWwMeaning m1 = MVIEW_WW_MEANING.as("m1");
		MviewWwMeaning m2 = MVIEW_WW_MEANING.as("m2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");

		return create
				.select(
						m1.LEXEME_ID,
						m1.MEANING_ID,
						m1.DATASET_CODE,
						m1.LEVEL1,
						m1.LEVEL2,
						m1.LEVEL3,
						m1.REGISTER_CODES,
						m1.POS_CODES,
						m1.DERIV_CODES,
						m1.DOMAIN_CODES,
						m1.IMAGE_FILES,
						m1.SYSTEMATIC_POLYSEMY_PATTERNS,
						m1.SEMANTIC_TYPES,
						m1.LEARNER_COMMENTS,
						m1.DEFINITIONS,
						m2.WORD_ID.as("meaning_word_id"),
						w2.WORD.as("meaning_word"),
						w2.HOMONYM_NR.as("meaning_word_homonym_nr"),
						w2.LANG.as("meaning_word_lang")
						)
				.from(m1
						.leftOuterJoin(m2).on(m2.MEANING_ID.eq(m1.MEANING_ID).and(m2.WORD_ID.ne(m1.WORD_ID)))
						.leftOuterJoin(w2).on(w2.WORD_ID.eq(m2.WORD_ID))
						)
				.where(
						m1.WORD_ID.eq(wordId)
						.and(m1.DATASET_CODE.in(datasets)))
				.orderBy(m1.DATASET_CODE, m1.LEVEL1, m1.LEVEL2, m1.LEVEL3)
				.fetch()
				.into(LexemeMeaningTuple.class);
	}

	public List<LexemeDetailsTuple> findLexemeDetailsTuples(Long wordId, String[] datasets) {

		return create
				.select(
						MVIEW_WW_LEXEME.LEXEME_ID,
						MVIEW_WW_LEXEME.MEANING_ID,
						MVIEW_WW_LEXEME.ADVICE_NOTES,
						MVIEW_WW_LEXEME.PUBLIC_NOTES,
						MVIEW_WW_LEXEME.GRAMMARS,
						MVIEW_WW_LEXEME.GOVERNMENT_ID,
						MVIEW_WW_LEXEME.GOVERNMENT,
						MVIEW_WW_LEXEME.USAGE_MEANING_ID,
						MVIEW_WW_LEXEME.USAGE_MEANING_TYPE_CODE,
						MVIEW_WW_LEXEME.USAGES,
						MVIEW_WW_LEXEME.USAGE_TRANSLATIONS,
						MVIEW_WW_LEXEME.USAGE_DEFINITIONS
						)
				.from(MVIEW_WW_LEXEME)
				.where(
						MVIEW_WW_LEXEME.WORD_ID.eq(wordId)
						.and(DSL.exists(DSL
								.select(MVIEW_WW_MEANING.LEXEME_ID)
								.from(MVIEW_WW_MEANING)
								.where(
										MVIEW_WW_MEANING.LEXEME_ID.eq(MVIEW_WW_LEXEME.LEXEME_ID))
										.and(MVIEW_WW_MEANING.DATASET_CODE.in(datasets)))
										)
						)
				.orderBy(MVIEW_WW_LEXEME.LEXEME_ID)
				.fetch()
				.into(LexemeDetailsTuple.class);
	}

	public Map<Long, List<Form>> findWordForms(Long wordId) {

		return create
				.select(
						MVIEW_WW_FORM.PARADIGM_ID,
						MVIEW_WW_FORM.FORM_ID,
						MVIEW_WW_FORM.FORM,
						MVIEW_WW_FORM.MORPH_CODE,
						MVIEW_WW_FORM.COMPONENTS,
						MVIEW_WW_FORM.DISPLAY_FORM,
						MVIEW_WW_FORM.VOCAL_FORM,
						MVIEW_WW_FORM.SOUND_FILE,
						MVIEW_WW_FORM.IS_WORD
						)
				.from(MVIEW_WW_FORM)
				.where(MVIEW_WW_FORM.WORD_ID.eq(wordId))
				.orderBy(MVIEW_WW_FORM.PARADIGM_ID, MVIEW_WW_FORM.FORM_ID)
				.fetchGroups(MVIEW_WW_FORM.PARADIGM_ID, Form.class);
	}
}
