package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DEFINITION_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_FREEFORM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYMOLOGY;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;

import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import eki.common.constant.FormMode;
import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.db.tables.MviewWwCollocation;
import eki.wordweb.data.db.tables.MviewWwDefinitionSourceLink;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeFreeformSourceLink;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwLexemeSourceLink;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningFreeformSourceLink;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordEtymSourceLink;
import eki.wordweb.data.db.tables.MviewWwWordEtymology;
import eki.wordweb.data.db.tables.MviewWwWordRelation;

public abstract class AbstractSearchDbService implements GlobalConstant, SystemConstant {

	@Autowired
	protected DSLContext create;

	public abstract Map<String, List<WordSearchElement>> getWordsByInfixLev(String wordInfix, List<String> destinLangs, int maxWordCount);

	public abstract List<Word> getWords(String searchWord, DataFilter dataFilter);

	public abstract List<Lexeme> getLexemes(Long wordId, DataFilter dataFilter);

	protected List<Word> getWords(MviewWwWord wordTable, Condition where) {

		return create
				.select(
						wordTable.WORD_ID,
						wordTable.WORD,
						wordTable.WORD_PRESE,
						wordTable.AS_WORD,
						wordTable.WORD_CLASS,
						wordTable.LANG,
						wordTable.HOMONYM_NR,
						wordTable.WORD_TYPE_CODES,
						wordTable.MORPH_CODE,
						wordTable.DISPLAY_MORPH_CODE,
						wordTable.ASPECT_CODE,
						wordTable.MEANING_WORDS,
						wordTable.DEFINITIONS,
						wordTable.OD_WORD_RECOMMENDATIONS,
						wordTable.LEX_DATASET_EXISTS,
						wordTable.TERM_DATASET_EXISTS,
						wordTable.FORMS_EXIST)
				.from(wordTable)
				.where(where)
				.orderBy(wordTable.LEX_DATASET_EXISTS.desc(), wordTable.LANG_ORDER_BY, wordTable.HOMONYM_NR)
				.fetchInto(Word.class);
	}

	protected List<Lexeme> getLexemes(MviewWwLexeme l, MviewWwLexemeRelation lr, Condition where) {

		MviewWwLexemeSourceLink lsl = MVIEW_WW_LEXEME_SOURCE_LINK.as("lsl");
		MviewWwLexemeFreeformSourceLink ffsl = MVIEW_WW_LEXEME_FREEFORM_SOURCE_LINK.as("ffsl");

		return create
				.select(
						l.WORD_ID,
						l.LEXEME_ID,
						l.MEANING_ID,
						l.DATASET_CODE,
						l.DATASET_TYPE,
						l.DATASET_NAME,
						l.VALUE_STATE_CODE,
						l.LEVEL1,
						l.LEVEL2,
						l.COMPLEXITY,
						l.WEIGHT,
						l.DATASET_ORDER_BY,
						l.LEXEME_ORDER_BY,
						l.VALUE_STATE_ORDER_BY,
						l.REGISTER_CODES,
						l.POS_CODES,
						l.DERIV_CODES,
						l.MEANING_WORDS,
						l.ADVICE_NOTES,
						l.PUBLIC_NOTES.as("lexeme_public_notes"),
						l.GRAMMARS,
						l.GOVERNMENTS,
						l.USAGES,
						l.OD_LEXEME_RECOMMENDATIONS,
						lsl.SOURCE_LINKS.as("lexeme_source_links"),
						ffsl.SOURCE_LINKS.as("lexeme_freeform_source_links"),
						lr.RELATED_LEXEMES)
				.from(l
						.leftOuterJoin(lsl).on(lsl.LEXEME_ID.eq(l.LEXEME_ID))
						.leftOuterJoin(ffsl).on(ffsl.LEXEME_ID.eq(l.LEXEME_ID))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l.LEXEME_ID)))
				.where(where)
				.fetchInto(Lexeme.class);
	}

	public Word getWord(Long wordId) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwWordEtymSourceLink wesl = MVIEW_WW_WORD_ETYM_SOURCE_LINK.as("wesl");

		return create
				.select(
						w.WORD_ID,
						w.WORD,
						w.WORD_PRESE,
						w.AS_WORD,
						w.HOMONYM_NR,
						w.WORD_CLASS,
						w.LANG,
						w.WORD_TYPE_CODES,
						w.MORPH_CODE,
						w.DISPLAY_MORPH_CODE,
						w.ASPECT_CODE,
						w.MEANING_WORDS,
						w.DEFINITIONS,
						wesl.SOURCE_LINKS.as("word_etym_source_links"),
						w.OD_WORD_RECOMMENDATIONS,
						w.LEX_DATASET_EXISTS,
						w.TERM_DATASET_EXISTS,
						w.FORMS_EXIST)
				.from(w.leftOuterJoin(wesl).on(wesl.WORD_ID.eq(wordId)))
				.where(w.WORD_ID.eq(wordId))
				.fetchOneInto(Word.class);
	}

	public Map<Long, List<Form>> getWordForms(Long wordId, Integer maxDisplayLevel) {

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		Condition where = f.WORD_ID.eq(wordId).and(f.MODE.in(FormMode.WORD.name(), FormMode.FORM.name()));
		if (maxDisplayLevel != null) {
			where = where.and(f.DISPLAY_LEVEL.le(maxDisplayLevel));
		}

		return create
				.select(
						f.PARADIGM_ID,
						f.INFLECTION_TYPE,
						f.FORM_ID,
						f.MODE,
						f.MORPH_GROUP1,
						f.MORPH_GROUP2,
						f.MORPH_GROUP3,
						f.DISPLAY_LEVEL,
						f.MORPH_CODE,
						f.MORPH_EXISTS,
						f.FORM,
						f.COMPONENTS,
						f.DISPLAY_FORM,
						f.VOCAL_FORM,
						f.AUDIO_FILE,
						f.ORDER_BY)
				.from(f)
				.where(where)
				.orderBy(f.PARADIGM_ID, f.ORDER_BY, f.FORM_ID)
				.fetchGroups(f.PARADIGM_ID, Form.class);
	}

	@Cacheable(value = CACHE_KEY_NULL_WORD, key = "{#wordId, #tokens}")
	public List<WordForm> getWordFormCandidates(Long wordId, List<String> tokens) {

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		return create
				.select(
						f.WORD,
						f.FORM)
				.from(f)
				.where(
						f.WORD_ID.eq(wordId)
								.and(f.FORM.in(tokens)))
				.fetchInto(WordForm.class);
	}

	public List<LexemeMeaningTuple> getLexemeMeaningTuples(Long wordId) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");
		MviewWwMeaningFreeformSourceLink ffsl = MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK.as("ffsl");
		MviewWwDefinitionSourceLink dsl = MVIEW_WW_DEFINITION_SOURCE_LINK.as("dsl");

		Condition where = l.WORD_ID.eq(wordId);

		return create
				.select(
						l.LEXEME_ID,
						m.MEANING_ID,
						m.DOMAIN_CODES,
						m.IMAGE_FILES,
						m.SYSTEMATIC_POLYSEMY_PATTERNS,
						m.SEMANTIC_TYPES,
						m.LEARNER_COMMENTS,
						m.PUBLIC_NOTES,
						m.DEFINITIONS,
						mr.RELATED_MEANINGS,
						ffsl.SOURCE_LINKS.as("freeform_source_links"),
						dsl.SOURCE_LINKS.as("definition_source_links"))
				.from(
						l.innerJoin(m).on(m.MEANING_ID.eq(l.MEANING_ID))
								.leftOuterJoin(mr).on(mr.MEANING_ID.eq(m.MEANING_ID))
								.leftOuterJoin(ffsl).on(ffsl.MEANING_ID.eq(m.MEANING_ID))
								.leftOuterJoin(dsl).on(dsl.MEANING_ID.eq(m.MEANING_ID)))
				.where(where)
				.orderBy(m.MEANING_ID, l.LEXEME_ID)
				.fetchInto(LexemeMeaningTuple.class);
	}

	public WordRelationsTuple getWordRelationsTuple(Long wordId) {

		MviewWwWordRelation wr = MVIEW_WW_WORD_RELATION.as("wr");

		return create
				.select(
						wr.WORD_ID,
						wr.RELATED_WORDS,
						wr.WORD_GROUP_MEMBERS)
				.from(wr)
				.where(wr.WORD_ID.eq(wordId))
				.fetchOptionalInto(WordRelationsTuple.class)
				.orElse(null);
	}

	public List<WordEtymTuple> getWordEtymologyTuples(Long wordId) {

		MviewWwWordEtymology we = MVIEW_WW_WORD_ETYMOLOGY.as("we");

		return create
				.select(
						we.WORD_ID,
						we.WORD_ETYM_ID,
						we.WORD_ETYM_WORD_ID,
						we.WORD_ETYM_WORD,
						we.WORD_ETYM_WORD_LANG,
						we.WORD_ETYM_WORD_MEANING_WORDS,
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.WORD_ETYM_COMMENT,
						we.WORD_ETYM_IS_QUESTIONABLE,
						we.WORD_ETYM_RELATIONS)
				.from(we)
				.where(we.WORD_ID.eq(wordId))
				.orderBy(we.WORD_ETYM_ORDER_BY)
				.fetchInto(WordEtymTuple.class);
	}

	public List<CollocationTuple> getCollocations(Long wordId) {

		MviewWwCollocation c = MVIEW_WW_COLLOCATION.as("c");

		Condition where = c.WORD_ID.eq(wordId);

		return create
				.select(
						c.LEXEME_ID,
						c.WORD_ID,
						c.POS_GROUP_ID,
						c.POS_GROUP_CODE,
						c.REL_GROUP_ID,
						c.REL_GROUP_NAME,
						c.COLLOC_ID,
						c.COLLOC_VALUE,
						c.COLLOC_DEFINITION,
						c.COLLOC_USAGES,
						c.COLLOC_MEMBERS,
						c.COMPLEXITY)
				.from(c)
				.where(where)
				.orderBy(
						c.POS_GROUP_ORDER_BY,
						c.REL_GROUP_ORDER_BY,
						c.COLLOC_GROUP_ORDER,
						c.COLLOC_ID)
				.fetchInto(CollocationTuple.class);
	}
}
