package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwWord;

@Component
public class LexSearchDbService {

	@Autowired
	private DSLContext create;

	public List<Word> findWords(String searchFilter, String lang, String[] datasets) {

		String searchFilterLower = StringUtils.lowerCase(searchFilter);
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
														.and(MVIEW_WW_FORM.FORM.lower().eq(searchFilterLower))))))
				.orderBy(MVIEW_WW_WORD.LANG, MVIEW_WW_WORD.HOMONYM_NR)
				.fetch()
				.into(Word.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordOrForm>> findWordsByPrefix(String wordPrefix, String lang, String[] datasets, int maxWordCount) {

		String wordPrefixLower = StringUtils.lowerCase(wordPrefix);
		Field<String> iswtf = DSL.field(DSL.value("prefWords")).as("group");
		Field<String> iswff = DSL.field(DSL.value("formWords")).as("group");
		Condition wdc = DSL.condition("{0} && {1}", MVIEW_WW_WORD.DATASET_CODES, DSL.val(datasets));
		Condition fdc = DSL.condition("{0} && {1}", MVIEW_WW_FORM.DATASET_CODES, DSL.val(datasets));
		Condition wlc = MVIEW_WW_WORD.WORD.lower().like(wordPrefixLower + '%').and(MVIEW_WW_WORD.LANG.eq(lang));
		Condition flc = MVIEW_WW_FORM.FORM.lower().eq(wordPrefixLower).and(MVIEW_WW_FORM.MODE.eq(FormMode.FORM.name())).and(MVIEW_WW_FORM.LANG.eq(lang));

		Table<Record2<String, String>> woft = DSL
			.selectDistinct(MVIEW_WW_WORD.WORD.as("value"), iswtf)
			.from(MVIEW_WW_WORD)
			.where(wlc.and(wdc))
			.orderBy(MVIEW_WW_WORD.WORD)
			.limit(maxWordCount)
			.unionAll(DSL
			.selectDistinct(MVIEW_WW_FORM.WORD.as("value"), iswff)
			.from(MVIEW_WW_FORM)
			.where(flc.and(fdc))
			.orderBy(MVIEW_WW_FORM.WORD)
			.limit(maxWordCount))
			.asTable("woft");

		return (Map<String, List<WordOrForm>>) create
				.selectFrom(woft)
				.fetchGroups("group", WordOrForm.class);
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
						MVIEW_WW_WORD.DEFINITIONS,
						MVIEW_WW_WORD_RELATION.RELATED_WORDS
						)
				.from(MVIEW_WW_WORD
						.leftOuterJoin(MVIEW_WW_WORD_RELATION).on(MVIEW_WW_WORD_RELATION.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)))
				.where(MVIEW_WW_WORD.WORD_ID.eq(wordId))
				.fetchOne()
				.into(Word.class);
	}

	public List<LexemeMeaningTuple> findLexemeMeaningTuples(Long wordId, String[] datasets) {

		MviewWwMeaning m1 = MVIEW_WW_MEANING.as("m1");
		MviewWwMeaning m2 = MVIEW_WW_MEANING.as("m2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");
		MviewWwDataset m1ds = MVIEW_WW_DATASET.as("m1ds");

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
						.innerJoin(m1ds).on(m1ds.CODE.eq(m1.DATASET_CODE))
						.leftOuterJoin(m2).on(m2.MEANING_ID.eq(m1.MEANING_ID).and(m2.WORD_ID.ne(m1.WORD_ID)))
						.leftOuterJoin(w2).on(w2.WORD_ID.eq(m2.WORD_ID))
						)
				.where(
						m1.WORD_ID.eq(wordId)
						.and(m1.DATASET_CODE.in(datasets)))
				.orderBy(m1ds.ORDER_BY, m1.LEVEL1, m1.LEVEL2, m1.LEVEL3, w2.WORD)
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
						MVIEW_WW_LEXEME.GOVERNMENTS,
						MVIEW_WW_LEXEME.USAGES,
						MVIEW_WW_LEXEME_RELATION.RELATED_LEXEMES,
						MVIEW_WW_MEANING_RELATION.RELATED_MEANINGS
						)
				.from(MVIEW_WW_LEXEME
						.leftOuterJoin(MVIEW_WW_LEXEME_RELATION).on(MVIEW_WW_LEXEME_RELATION.LEXEME_ID.eq(MVIEW_WW_LEXEME.LEXEME_ID))
						.leftOuterJoin(MVIEW_WW_MEANING_RELATION).on(MVIEW_WW_MEANING_RELATION.LEXEME_ID.eq(MVIEW_WW_LEXEME.LEXEME_ID)))
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

	public List<CollocationTuple> findCollocations(Long wordId, String[] datasets) {

		return create
				.select(
						MVIEW_WW_COLLOCATION.LEXEME_ID,
						MVIEW_WW_COLLOCATION.WORD_ID,
						MVIEW_WW_COLLOCATION.POS_GROUP_ID,
						MVIEW_WW_COLLOCATION.POS_GROUP_CODE,
						MVIEW_WW_COLLOCATION.REL_GROUP_ID,
						MVIEW_WW_COLLOCATION.REL_GROUP_NAME,
						MVIEW_WW_COLLOCATION.COLLOC_ID,
						MVIEW_WW_COLLOCATION.COLLOC_VALUE,
						MVIEW_WW_COLLOCATION.COLLOC_DEFINITION,
						MVIEW_WW_COLLOCATION.COLLOC_USAGES,
						MVIEW_WW_COLLOCATION.COLLOC_MEMBERS)
				.from(MVIEW_WW_COLLOCATION)
				.where(
						MVIEW_WW_COLLOCATION.WORD_ID.eq(wordId)
						.and(MVIEW_WW_COLLOCATION.DATASET_CODE.in(datasets)))
				.orderBy(
						MVIEW_WW_COLLOCATION.LEVEL1,
						MVIEW_WW_COLLOCATION.LEVEL2,
						MVIEW_WW_COLLOCATION.LEVEL3,
						MVIEW_WW_COLLOCATION.POS_GROUP_ORDER_BY,
						MVIEW_WW_COLLOCATION.REL_GROUP_ORDER_BY,
						MVIEW_WW_COLLOCATION.COLLOC_GROUP_ORDER,
						MVIEW_WW_COLLOCATION.COLLOC_ID
						)
				.fetch()
				.into(CollocationTuple.class);
	}

	public Map<Long, List<Form>> findWordForms(Long wordId) {

		return create
				.select(
						MVIEW_WW_FORM.PARADIGM_ID,
						MVIEW_WW_FORM.FORM_ID,
						MVIEW_WW_FORM.MODE,
						MVIEW_WW_FORM.FORM,
						MVIEW_WW_FORM.MORPH_CODE,
						MVIEW_WW_FORM.COMPONENTS,
						MVIEW_WW_FORM.DISPLAY_FORM,
						MVIEW_WW_FORM.VOCAL_FORM,
						MVIEW_WW_FORM.SOUND_FILE
						)
				.from(MVIEW_WW_FORM)
				.where(MVIEW_WW_FORM.WORD_ID.eq(wordId))
				.orderBy(MVIEW_WW_FORM.PARADIGM_ID, MVIEW_WW_FORM.FORM_ID)
				.fetchGroups(MVIEW_WW_FORM.PARADIGM_ID, Form.class);
	}

}
