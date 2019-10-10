package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_AS_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.FormMode;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.db.tables.MviewWwAsWord;
import eki.wordweb.data.db.tables.MviewWwCollocation;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwWord;

@Component
public class LexSearchDbService extends AbstractSearchDbService {

	public List<Word> getWords(String searchWord, DataFilter dataFilter) {

		String sourceLang = dataFilter.getSourceLang();
		String destinLang = dataFilter.getDestinLang();
		Complexity lexComplexity = dataFilter.getLexComplexity();
		Complexity dataComplexity = dataFilter.getDataComplexity();
		String[] filtComplexities = new String[] {lexComplexity.name(), dataComplexity.name()};

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwForm f = MVIEW_WW_FORM.as("f");
		Table<?> lc = DSL.unnest(w.LANG_COMPLEXITIES).as("lc", "lang", "complexity");

		String searchWordLower = StringUtils.lowerCase(searchWord);
		Condition where = w.LEX_DATASET_EXISTS.isTrue() 
				.and(w.LANG.eq(sourceLang))
				.andExists(DSL
						.select(f.WORD_ID)
						.from(f)
						.where(f.WORD_ID.eq(w.WORD_ID)
								.and(f.FORM.lower().eq(searchWordLower))))
				.andExists(DSL
						.selectFrom(lc)
						.where(
								lc.field("lang", String.class).eq(destinLang)
								.and(lc.field("complexity", String.class).in(filtComplexities))));

		return create
				.select(
						w.WORD_ID,
						w.WORD,
						w.WORD_CLASS,
						w.LANG,
						w.HOMONYM_NR,
						w.WORD_TYPE_CODES,
						w.MORPH_CODE,
						w.DISPLAY_MORPH_CODE,
						w.ASPECT_CODE,
						w.MEANING_WORDS,
						w.DEFINITIONS)
				.from(w)
				.where(where)
				.orderBy(w.LANG, w.HOMONYM_NR)
				.fetch()
				.into(Word.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordOrForm>> getWordsByPrefix(String wordPrefix, String lang, int maxWordCount) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwAsWord aw = MVIEW_WW_AS_WORD.as("aw");
		MviewWwForm f = MVIEW_WW_FORM.as("f");

		String wordPrefixLower = StringUtils.lowerCase(wordPrefix);
		Field<String> iswtf = DSL.field(DSL.value("prefWords")).as("group");
		Field<String> iswff = DSL.field(DSL.value("formWords")).as("group");
		Condition wlc = w.LEX_DATASET_EXISTS.isTrue().and(w.WORD.lower().like(wordPrefixLower + '%')).and(w.LANG.eq(lang));
		Condition awlc = w.LEX_DATASET_EXISTS.isTrue().and(aw.AS_WORD.lower().like(wordPrefixLower + '%')).and(w.LANG.eq(lang).and(aw.WORD_ID.eq(w.WORD_ID)));
		Condition flc = f.FORM.lower().eq(wordPrefixLower).and(f.MODE.eq(FormMode.FORM.name())).and(f.LANG.eq(lang));

		Table<Record2<String, String>> woft = DSL
				.selectDistinct(w.WORD.as("value"), iswtf)
				.from(w)
				.where(wlc)
				.orderBy(w.WORD)
				.limit(maxWordCount)
				.unionAll(DSL
						.selectDistinct(w.WORD.as("value"), iswtf)
						.from(w, aw)
						.where(awlc)
						.orderBy(w.WORD)
						.limit(maxWordCount))
				.unionAll(DSL
						.selectDistinct(f.WORD.as("value"), iswff)
						.from(f)
						.where(flc)
						.orderBy(f.WORD)
						.limit(maxWordCount))
				.asTable("woft");

		return (Map<String, List<WordOrForm>>) create
				.selectDistinct(woft.field("value"), woft.field("group"))
				.from(woft)
				.fetchGroups("group", WordOrForm.class);
	}

	public List<Lexeme> getLexemes(Long wordId, DataFilter dataFilter) {

		String destinLang = dataFilter.getDestinLang();
		Complexity lexComplexity = dataFilter.getLexComplexity();
		Complexity dataComplexity = dataFilter.getDataComplexity();
		String[] filtComplexities = new String[] {lexComplexity.name(), dataComplexity.name()};

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");
		Table<?> lc = DSL.unnest(l.LANG_COMPLEXITIES).as("lc", "lang", "complexity");

		Condition where = l.WORD_ID.eq(wordId)
				.and(l.DATASET_TYPE.eq(DatasetType.LEX.name()))
				.andExists(DSL
						.selectFrom(lc)
						.where(lc.field("lang", String.class).eq(destinLang)
								.and(lc.field("complexity", String.class).in(filtComplexities))));
		return create
				.select(
						l.LEXEME_ID,
						l.MEANING_ID,
						l.LEVEL1,
						l.LEVEL2,
						l.LEVEL3,
						l.COMPLEXITY,
						l.LEX_ORDER_BY,
						l.REGISTER_CODES,
						l.POS_CODES,
						l.DERIV_CODES,
						l.MEANING_WORDS,
						l.ADVICE_NOTES,
						l.PUBLIC_NOTES,
						l.GRAMMARS,
						l.GOVERNMENTS,
						l.USAGES,
						lr.RELATED_LEXEMES)
				.from(l.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l.LEXEME_ID)))
				.where(where)
				.orderBy(
						l.LEVEL1,
						l.LEVEL2,
						l.LEVEL3,
						l.LEX_ORDER_BY)
				.fetch()
				.into(Lexeme.class);
	}

	public List<LexemeMeaningTuple> getLexemeMeaningTuples(Long wordId, DataFilter dataFilter) {

		String destinLang = dataFilter.getDestinLang();
		Complexity lexComplexity = dataFilter.getLexComplexity();
		Complexity dataComplexity = dataFilter.getDataComplexity();
		String[] filtComplexities = new String[] {lexComplexity.name(), dataComplexity.name()};

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");
		Table<?> lc = DSL.unnest(l.LANG_COMPLEXITIES).as("lc", "lang", "complexity");

		Condition where = l.WORD_ID.eq(wordId)
				.and(l.DATASET_TYPE.eq(DatasetType.LEX.name()))
				.andExists(DSL
						.selectFrom(lc)
						.where(lc.field("lang", String.class).eq(destinLang)
								.and(lc.field("complexity", String.class).in(filtComplexities))));

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

		MviewWwCollocation c = MVIEW_WW_COLLOCATION.as("c");

		Condition where = c.WORD_ID.eq(wordId).and(c.COMPLEXITY.eq(complexity.name()));

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
				.fetch()
				.into(CollocationTuple.class);
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
}
