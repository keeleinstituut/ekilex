package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;
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
import org.springframework.stereotype.Component;

import eki.common.constant.DatasetType;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordOrForm;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwWord;

@Component
public class TermSearchDbService extends AbstractSearchDbService {

	public List<Word> getWords(String searchWord) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");

		String searchWordLower = StringUtils.lowerCase(searchWord);
		Condition where = w.TERM_DATASET_EXISTS.isTrue() 
				.and(w.WORD.lower().eq(searchWordLower));

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
	public Map<String, List<WordOrForm>> getWordsByPrefix(String wordPrefix, int maxWordCount) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");

		String wordPrefixLower = StringUtils.lowerCase(wordPrefix);
		Field<String> iswtf = DSL.field(DSL.value("prefWords")).as("group");
		Condition wlc = w.TERM_DATASET_EXISTS.isTrue().and(w.WORD.lower().like(wordPrefixLower + '%'));

		Table<Record2<String, String>> woft = DSL
				.selectDistinct(w.WORD.as("value"), iswtf)
				.from(w)
				.where(wlc)
				.orderBy(w.WORD)
				.limit(maxWordCount)
				.asTable("woft");

		return (Map<String, List<WordOrForm>>) create
				.selectDistinct(woft.field("value"), woft.field("group"))
				.from(woft)
				.fetchGroups("group", WordOrForm.class);
	}

	public List<Lexeme> getLexemes(Long wordId) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");

		Condition where = l.WORD_ID.eq(wordId)
				.and(l.DATASET_TYPE.eq(DatasetType.TERM.name()));

		return create
				.select(
						l.LEXEME_ID,
						l.MEANING_ID,
						ds.NAME.as("dataset_name"),
						l.LEVEL1,
						l.LEVEL2,
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
				.from(l
						.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l.LEXEME_ID)))
				.where(where)
				.orderBy(
						l.LEVEL1,
						l.LEVEL2,
						l.LEX_ORDER_BY)
				.fetch()
				.into(Lexeme.class);
	}

	public List<LexemeMeaningTuple> getLexemeMeaningTuples(Long wordId) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");

		Condition where = l.WORD_ID.eq(wordId)
				.and(l.DATASET_TYPE.eq(DatasetType.TERM.name()));

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
}
