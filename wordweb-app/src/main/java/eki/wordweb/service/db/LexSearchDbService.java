package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_AS_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYMOLOGY;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;

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
import eki.wordweb.data.WordEtymology;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
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
						MVIEW_WW_WORD.TYPE_CODE,
						MVIEW_WW_WORD.ASPECT_CODE,
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
		Condition awlc = MVIEW_WW_AS_WORD.AS_WORD.lower().like(wordPrefixLower + '%').and(MVIEW_WW_WORD.LANG.eq(lang).and(MVIEW_WW_AS_WORD.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)));
		Condition flc = MVIEW_WW_FORM.FORM.lower().eq(wordPrefixLower).and(MVIEW_WW_FORM.MODE.eq(FormMode.FORM.name())).and(MVIEW_WW_FORM.LANG.eq(lang));

		Table<Record2<String, String>> woft = DSL
			.selectDistinct(MVIEW_WW_WORD.WORD.as("value"), iswtf)
			.from(MVIEW_WW_WORD)
			.where(wlc.and(wdc))
			.orderBy(MVIEW_WW_WORD.WORD)
			.limit(maxWordCount)
			.unionAll(DSL
			.selectDistinct(MVIEW_WW_WORD.WORD.as("value"), iswtf)
			.from(MVIEW_WW_WORD, MVIEW_WW_AS_WORD)
			.where(awlc.and(wdc))
			.orderBy(MVIEW_WW_WORD.WORD)
			.limit(maxWordCount))
			.unionAll(DSL
			.selectDistinct(MVIEW_WW_FORM.WORD.as("value"), iswff)
			.from(MVIEW_WW_FORM)
			.where(flc.and(fdc))
			.orderBy(MVIEW_WW_FORM.WORD)
			.limit(maxWordCount))
			.asTable("woft");

		return (Map<String, List<WordOrForm>>) create
				.selectDistinct(woft.field("value"), woft.field("group"))
				.from(woft)
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
						MVIEW_WW_WORD.TYPE_CODE,
						MVIEW_WW_WORD.ASPECT_CODE,
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

	public WordEtymology findWordEtymology(Long wordId) {

		return create
				.select(
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ID,
						MVIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_YEAR,
						MVIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_TYPE_CODE,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_SOURCES,
						MVIEW_WW_WORD_ETYMOLOGY.ETYM_LINEUP
						)
				.from(MVIEW_WW_WORD_ETYMOLOGY)
				.where(MVIEW_WW_WORD_ETYMOLOGY.WORD_ID.eq(wordId))
				.fetchOneInto(WordEtymology.class);
	}

	public List<WordRelationTuple> findWordRelationTuples(Long wordId) {

		return create
				.select(
						MVIEW_WW_WORD_RELATION.WORD_ID,
						MVIEW_WW_WORD_RELATION.RELATED_WORDS,
						MVIEW_WW_WORD_RELATION.WORD_GROUP_ID,
						MVIEW_WW_WORD_RELATION.WORD_REL_TYPE_CODE,
						MVIEW_WW_WORD_RELATION.WORD_GROUP_MEMBERS
						)
				.from(MVIEW_WW_WORD_RELATION)
				.where(MVIEW_WW_WORD_RELATION.WORD_ID.eq(wordId))
				.fetch()
				.into(WordRelationTuple.class);
	}

	public List<LexemeDetailsTuple> findLexemeDetailsTuples(Long wordId, String[] datasets) {

		MviewWwLexeme l1 = MVIEW_WW_LEXEME.as("l1");
		MviewWwLexeme l2 = MVIEW_WW_LEXEME.as("l2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");

		return create
				.select(
						l1.LEXEME_ID,
						l1.WORD_ID,
						l1.MEANING_ID,
						l1.DATASET_CODE,
						l1.DS_ORDER_BY,
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3,
						l1.LEX_ORDER_BY,
						l1.REGISTER_CODES,
						l1.POS_CODES,
						l1.DERIV_CODES,
						l1.ADVICE_NOTES,
						l1.PUBLIC_NOTES,
						l1.GRAMMARS,
						l1.GOVERNMENTS,
						l1.USAGES,
						l2.GOVERNMENTS.as("meaning_lexeme_governments"),
						l2.REGISTER_CODES.as("meaning_lexeme_register_codes"),
						w2.WORD_ID.as("meaning_word_id"),
						w2.WORD.as("meaning_word"),
						w2.HOMONYM_NR.as("meaning_word_homonym_nr"),
						w2.ASPECT_CODE.as("meaning_word_aspect_code"),
						w2.LANG.as("meaning_word_lang"),
						lr.RELATED_LEXEMES
						)
				.from(l1
						.leftOuterJoin(l2).on(l2.MEANING_ID.eq(l1.MEANING_ID).and(l2.LEXEME_ID.ne(l1.LEXEME_ID)).and(l2.DATASET_CODE.in(datasets)))
						.leftOuterJoin(w2).on(l2.WORD_ID.eq(w2.WORD_ID))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l1.LEXEME_ID)))
				.where(
						l1.WORD_ID.eq(wordId)
						.and(l1.DATASET_CODE.in(datasets))
						)
				.orderBy(
						l1.DS_ORDER_BY,
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3,
						l1.LEX_ORDER_BY,
						l2.DS_ORDER_BY,
						l2.LEX_ORDER_BY)
				.fetch()
				.into(LexemeDetailsTuple.class);
	}

	public List<LexemeMeaningTuple> findLexemeMeaningTuples(Long wordId, String[] datasets) {
	
		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");
	
		return create
				.select(
						l.LEXEME_ID,
						m.MEANING_ID,
						m.DOMAIN_CODES,
						m.IMAGE_FILES,
						m.SYSTEMATIC_POLYSEMY_PATTERNS,
						m.SEMANTIC_TYPES,
						m.LEARNER_COMMENTS,
						m.DEFINITIONS,
						mr.RELATED_MEANINGS
						)
				.from(
						l.innerJoin(m).on(m.MEANING_ID.eq(l.MEANING_ID))
						.leftOuterJoin(mr).on(mr.MEANING_ID.eq(m.MEANING_ID))
						)
				.where(l.WORD_ID.eq(wordId).and(l.DATASET_CODE.in(datasets)))
				.orderBy(m.MEANING_ID, l.LEXEME_ID)
				.fetch()
				.into(LexemeMeaningTuple.class);
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
				.where(
						MVIEW_WW_FORM.WORD_ID.eq(wordId)
						.and(MVIEW_WW_FORM.MODE.in(FormMode.WORD.name(), FormMode.FORM.name())))
				.orderBy(MVIEW_WW_FORM.PARADIGM_ID, MVIEW_WW_FORM.ORDER_BY)
				.fetchGroups(MVIEW_WW_FORM.PARADIGM_ID, Form.class);
	}

}
