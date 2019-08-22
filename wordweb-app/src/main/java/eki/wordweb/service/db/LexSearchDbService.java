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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.Complexity;
import eki.common.constant.DbConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwWord;

@Component
public class LexSearchDbService implements DbConstant, SystemConstant {

	@Autowired
	private DSLContext create;

	public List<Word> getWords(String searchFilter, String lang, Complexity complexity) {

		String searchFilterLower = StringUtils.lowerCase(searchFilter);
		Condition where = MVIEW_WW_WORD.LANG.eq(lang)
				.and(DSL.exists(DSL.select(MVIEW_WW_FORM.WORD_ID)
						.from(MVIEW_WW_FORM)
						.where(MVIEW_WW_FORM.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)
								.and(MVIEW_WW_FORM.FORM.lower().eq(searchFilterLower)))));
		if (complexity != null) {
			where = where.and(MVIEW_WW_WORD.COMPLEXITY.eq(complexity.name()));
		}
		return create
				.select(
						MVIEW_WW_WORD.WORD_ID,
						MVIEW_WW_WORD.WORD,
						MVIEW_WW_WORD.WORD_CLASS,
						MVIEW_WW_WORD.LANG,
						MVIEW_WW_WORD.HOMONYM_NR,
						MVIEW_WW_WORD.WORD_TYPE_CODES,
						MVIEW_WW_WORD.MORPH_CODE,
						MVIEW_WW_WORD.DISPLAY_MORPH_CODE,
						MVIEW_WW_WORD.ASPECT_CODE,
						MVIEW_WW_WORD.COMPLEXITY,
						MVIEW_WW_WORD.MEANING_COUNT,
						MVIEW_WW_WORD.MEANING_WORDS,
						MVIEW_WW_WORD.DEFINITIONS)
				.from(MVIEW_WW_WORD)
				.where(where)
				.orderBy(MVIEW_WW_WORD.LANG, MVIEW_WW_WORD.HOMONYM_NR)
				.fetch()
				.into(Word.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordOrForm>> getWordsByPrefix(String wordPrefix, String lang, int maxWordCount) {

		String wordPrefixLower = StringUtils.lowerCase(wordPrefix);
		Field<String> iswtf = DSL.field(DSL.value("prefWords")).as("group");
		Field<String> iswff = DSL.field(DSL.value("formWords")).as("group");
		Condition wlc = MVIEW_WW_WORD.WORD.lower().like(wordPrefixLower + '%').and(MVIEW_WW_WORD.LANG.eq(lang));
		Condition awlc = MVIEW_WW_AS_WORD.AS_WORD.lower().like(wordPrefixLower + '%').and(MVIEW_WW_WORD.LANG.eq(lang).and(MVIEW_WW_AS_WORD.WORD_ID.eq(MVIEW_WW_WORD.WORD_ID)));
		Condition flc = MVIEW_WW_FORM.FORM.lower().eq(wordPrefixLower).and(MVIEW_WW_FORM.MODE.eq(FormMode.FORM.name())).and(MVIEW_WW_FORM.LANG.eq(lang));

		Table<Record2<String, String>> woft = DSL
				.selectDistinct(MVIEW_WW_WORD.WORD.as("value"), iswtf)
				.from(MVIEW_WW_WORD)
				.where(wlc)
				.orderBy(MVIEW_WW_WORD.WORD)
				.limit(maxWordCount)
				.unionAll(DSL
						.selectDistinct(MVIEW_WW_WORD.WORD.as("value"), iswtf)
						.from(MVIEW_WW_WORD, MVIEW_WW_AS_WORD)
						.where(awlc)
						.orderBy(MVIEW_WW_WORD.WORD)
						.limit(maxWordCount))
				.unionAll(DSL
						.selectDistinct(MVIEW_WW_FORM.WORD.as("value"), iswff)
						.from(MVIEW_WW_FORM)
						.where(flc)
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
						MVIEW_WW_WORD.WORD_CLASS,
						MVIEW_WW_WORD.LANG,
						MVIEW_WW_WORD.WORD_TYPE_CODES,
						MVIEW_WW_WORD.MORPH_CODE,
						MVIEW_WW_WORD.DISPLAY_MORPH_CODE,
						MVIEW_WW_WORD.ASPECT_CODE,
						MVIEW_WW_WORD.COMPLEXITY,
						MVIEW_WW_WORD.MEANING_COUNT,
						MVIEW_WW_WORD.MEANING_WORDS,
						MVIEW_WW_WORD.DEFINITIONS)
				.from(MVIEW_WW_WORD)
				.where(MVIEW_WW_WORD.WORD_ID.eq(wordId))
				.fetchOne()
				.into(Word.class);
	}

	public List<WordEtymTuple> getWordEtymologyTuples(Long wordId) {

		return create
				.select(
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ID,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_ID,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_WORD_ID,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_WORD,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_WORD_LANG,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_WORD_MEANING_WORDS,
						MVIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_TYPE_CODE,
						MVIEW_WW_WORD_ETYMOLOGY.ETYMOLOGY_YEAR,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_COMMENT,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_IS_QUESTIONABLE,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_SOURCES,
						MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_RELATIONS)
				.from(MVIEW_WW_WORD_ETYMOLOGY)
				.where(MVIEW_WW_WORD_ETYMOLOGY.WORD_ID.eq(wordId))
				.orderBy(MVIEW_WW_WORD_ETYMOLOGY.WORD_ETYM_ORDER_BY)
				.fetchInto(WordEtymTuple.class);
	}

	public List<WordRelationTuple> getWordRelationTuples(Long wordId) {

		return create
				.select(
						MVIEW_WW_WORD_RELATION.WORD_ID,
						MVIEW_WW_WORD_RELATION.RELATED_WORDS,
						MVIEW_WW_WORD_RELATION.WORD_GROUP_ID,
						MVIEW_WW_WORD_RELATION.WORD_REL_TYPE_CODE,
						MVIEW_WW_WORD_RELATION.WORD_GROUP_MEMBERS)
				.from(MVIEW_WW_WORD_RELATION)
				.where(MVIEW_WW_WORD_RELATION.WORD_ID.eq(wordId))
				.fetch()
				.into(WordRelationTuple.class);
	}

	public List<LexemeDetailsTuple> getLexemeDetailsTuples(Long wordId, Complexity complexity) {

		MviewWwLexeme l1 = MVIEW_WW_LEXEME.as("l1");
		MviewWwLexeme l2 = MVIEW_WW_LEXEME.as("l2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");

		Condition l2Join = l2.MEANING_ID.eq(l1.MEANING_ID)
				.and(l2.LEXEME_ID.ne(l1.LEXEME_ID))
				.and(l2.WORD_ID.ne(l1.WORD_ID));
		Condition where = l1.WORD_ID.eq(wordId);
		if (complexity != null) {
			l2Join = l2Join.and(l2.COMPLEXITY.eq(complexity.name()));
			where = where.and(l1.COMPLEXITY.eq(complexity.name()));
		}
		return create
				.select(
						l1.LEXEME_ID,
						l1.WORD_ID,
						l1.MEANING_ID,
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3,
						l1.COMPLEXITY,
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
						w2.LANG.as("meaning_word_lang"),
						w2.WORD_TYPE_CODES.as("meaning_word_type_codes"),
						w2.ASPECT_CODE.as("meaning_word_aspect_code"),
						lr.RELATED_LEXEMES)
				.from(l1
						.leftOuterJoin(l2).on(l2Join)
						.leftOuterJoin(w2).on(l2.WORD_ID.eq(w2.WORD_ID))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l1.LEXEME_ID)))
				.where(where)
				.orderBy(
						l1.LEVEL1,
						l1.LEVEL2,
						l1.LEVEL3,
						l1.LEX_ORDER_BY,
						l2.LEX_ORDER_BY)
				.fetch()
				.into(LexemeDetailsTuple.class);
	}

	public List<LexemeMeaningTuple> getLexemeMeaningTuples(Long wordId, Complexity complexity) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");

		Condition where = l.WORD_ID.eq(wordId);
		if (complexity != null) {
			where = where.and(l.COMPLEXITY.eq(complexity.name()));
		}
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
						mr.RELATED_MEANINGS)
				.from(
						l.innerJoin(m).on(m.MEANING_ID.eq(l.MEANING_ID))
								.leftOuterJoin(mr).on(mr.MEANING_ID.eq(m.MEANING_ID)))
				.where(where)
				.orderBy(m.MEANING_ID, l.LEXEME_ID)
				.fetch()
				.into(LexemeMeaningTuple.class);
	}

	public List<CollocationTuple> getCollocations(Long wordId, Complexity complexity) {

		Condition where = MVIEW_WW_COLLOCATION.WORD_ID.eq(wordId);
		if (complexity != null) {
			where = where.and(MVIEW_WW_COLLOCATION.COMPLEXITY.eq(complexity.name()));
		}

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
						MVIEW_WW_COLLOCATION.COLLOC_MEMBERS,
						MVIEW_WW_COLLOCATION.COMPLEXITY)
				.from(MVIEW_WW_COLLOCATION)
				.where(where)
				.orderBy(
						MVIEW_WW_COLLOCATION.POS_GROUP_ORDER_BY,
						MVIEW_WW_COLLOCATION.REL_GROUP_ORDER_BY,
						MVIEW_WW_COLLOCATION.COLLOC_GROUP_ORDER,
						MVIEW_WW_COLLOCATION.COLLOC_ID)
				.fetch()
				.into(CollocationTuple.class);
	}

	public Map<Long, List<Form>> getWordForms(Long wordId, Integer maxDisplayLevel) {

		Condition where = MVIEW_WW_FORM.WORD_ID.eq(wordId).and(MVIEW_WW_FORM.MODE.in(FormMode.WORD.name(), FormMode.FORM.name()));
		if (maxDisplayLevel != null) {
			where = where.and(MVIEW_WW_FORM.DISPLAY_LEVEL.le(maxDisplayLevel));
		}

		return create
				.select(
						MVIEW_WW_FORM.PARADIGM_ID,
						MVIEW_WW_FORM.INFLECTION_TYPE,
						MVIEW_WW_FORM.FORM_ID,
						MVIEW_WW_FORM.MODE,
						MVIEW_WW_FORM.MORPH_GROUP1,
						MVIEW_WW_FORM.MORPH_GROUP2,
						MVIEW_WW_FORM.MORPH_GROUP3,
						MVIEW_WW_FORM.DISPLAY_LEVEL,
						MVIEW_WW_FORM.MORPH_CODE,
						MVIEW_WW_FORM.MORPH_EXISTS,
						MVIEW_WW_FORM.FORM,
						MVIEW_WW_FORM.COMPONENTS,
						MVIEW_WW_FORM.DISPLAY_FORM,
						MVIEW_WW_FORM.VOCAL_FORM,
						MVIEW_WW_FORM.AUDIO_FILE,
						MVIEW_WW_FORM.ORDER_BY)
				.from(MVIEW_WW_FORM)
				.where(where)
				.orderBy(MVIEW_WW_FORM.PARADIGM_ID, MVIEW_WW_FORM.ORDER_BY, MVIEW_WW_FORM.FORM_ID)
				.fetchGroups(MVIEW_WW_FORM.PARADIGM_ID, Form.class);
	}

	@Cacheable(value = CACHE_KEY_NULL_WORD, key = "{#wordId, #tokens}")
	public List<WordForm> getWordFormCandidates(Long wordId, List<String> tokens) {

		return create
				.select(
						MVIEW_WW_FORM.WORD,
						MVIEW_WW_FORM.FORM)
				.from(MVIEW_WW_FORM)
				.where(
						MVIEW_WW_FORM.WORD_ID.eq(wordId)
								.and(MVIEW_WW_FORM.FORM.in(tokens)))
				.fetchInto(WordForm.class);
	}
}
