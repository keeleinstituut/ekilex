package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DEFINITION_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FREEFORM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_SEARCH;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.common.constant.DatasetType;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.SourceLinksWrapper;
import eki.wordweb.data.TypeSourceLink;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.db.tables.MviewWwCollocation;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwDefinitionSourceLink;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwFreeformSourceLink;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwLexemeSourceLink;
import eki.wordweb.data.db.tables.MviewWwMeaning;
import eki.wordweb.data.db.tables.MviewWwMeaningRelation;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordSearch;

@Component
public class UnifSearchDbService extends AbstractSearchDbService {

	public List<Word> getWords(String searchWord) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwForm f = MVIEW_WW_FORM.as("f");

		String searchWordLower = StringUtils.lowerCase(searchWord);
		Condition where = DSL.exists(DSL
				.select(f.WORD_ID)
				.from(f)
				.where(f.WORD_ID.eq(w.WORD_ID)
						.and(f.FORM.lower().eq(searchWordLower))));

		return create
				.select(
						w.WORD_ID,
						w.WORD,
						w.AS_WORD,
						w.WORD_CLASS,
						w.LANG,
						w.HOMONYM_NR,
						w.WORD_TYPE_CODES,
						w.MORPH_CODE,
						w.DISPLAY_MORPH_CODE,
						w.ASPECT_CODE,
						w.MEANING_WORDS,
						w.DEFINITIONS,
						w.OD_WORD_RECOMMENDATIONS,
						w.LEX_DATASET_EXISTS,
						w.TERM_DATASET_EXISTS,
						w.FORMS_EXIST)
				.from(w)
				.where(where)
				.orderBy(w.LEX_DATASET_EXISTS.desc(), w.LANG, w.HOMONYM_NR)
				.fetch()
				.into(Word.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordSearchElement>> getWordsByPrefix(String wordPrefix, int maxWordCount) {

		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		MviewWwWordSearch f = MVIEW_WW_WORD_SEARCH.as("f");

		String wordPrefixLower = StringUtils.lowerCase(wordPrefix);
		Table<Record2<String, String>> ws = DSL
				.select(w.SGROUP, w.WORD)
				.from(w)
				.where(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD).and(w.CRIT.like(wordPrefixLower + '%')))
				.orderBy(w.WORD)
				.limit(maxWordCount)
				.unionAll(DSL.select(aw.SGROUP, aw.WORD)
						.from(aw)
						.where(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD).and(aw.CRIT.like(wordPrefixLower + '%')))
						.orderBy(aw.WORD)
						.limit(maxWordCount))
				.unionAll(DSL.select(f.SGROUP, f.WORD)
						.from(f)
						.where(f.SGROUP.eq(WORD_SEARCH_GROUP_FORM).and(f.CRIT.eq(wordPrefixLower)))
						.orderBy(f.WORD)
						.limit(maxWordCount))
				.asTable("ws");

		return (Map<String, List<WordSearchElement>>) create
				.select(ws.field("sgroup"), ws.field("word"))
				.from(ws)
				.fetchGroups("sgroup", WordSearchElement.class);
	}

	public List<Lexeme> getLexemes(Long wordId, DataFilter dataFilter) {

		DatasetType datasetType = dataFilter.getDatasetType();
		List<String> destinLangs = dataFilter.getDestinLangs();

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");

		Condition where = l.WORD_ID.eq(wordId);
		if (datasetType != null) {
			where = where.and(l.DATASET_TYPE.eq(datasetType.name()));
		}
		if (CollectionUtils.isNotEmpty(destinLangs)) {
			String[] destinLangsArr = destinLangs.toArray(new String[0]);
			where = where.and(PostgresDSL.arrayOverlap(l.LANG_FILTER, destinLangsArr));
		}

		return create
				.select(
						l.LEXEME_ID,
						l.MEANING_ID,
						l.DATASET_CODE,
						ds.NAME.as("dataset_name"),
						l.DATASET_TYPE,
						l.LEVEL1,
						l.LEVEL2,
						l.COMPLEXITY,
						l.WEIGHT,
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
						l.OD_LEXEME_RECOMMENDATIONS,
						lr.RELATED_LEXEMES)
				.from(l
						.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l.LEXEME_ID)))
				.where(where)
				.orderBy(
						l.DATASET_TYPE,
						l.LEVEL1,
						l.LEVEL2,
						l.LEX_ORDER_BY)
				.fetch()
				.into(Lexeme.class);
	}

	public List<LexemeMeaningTuple> getLexemeMeaningTuples(Long wordId, DataFilter dataFilter) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");
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
						m.DEFINITIONS,
						mr.RELATED_MEANINGS,
						dsl.SOURCE_LINKS.as("definition_source_links"))
				.from(
						l.innerJoin(m).on(m.MEANING_ID.eq(l.MEANING_ID))
								.leftOuterJoin(mr).on(mr.MEANING_ID.eq(m.MEANING_ID))
								.leftOuterJoin(dsl).on(dsl.MEANING_ID.eq(m.MEANING_ID)))
				.where(where)
				.orderBy(m.MEANING_ID, l.LEXEME_ID)
				.fetch()
				.into(LexemeMeaningTuple.class);
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
				.fetch()
				.into(CollocationTuple.class);
	}

	public List<TypeSourceLink> getLexemeSourceLinks(Long wordId) {

		MviewWwLexemeSourceLink sl = MVIEW_WW_LEXEME_SOURCE_LINK.as("sl");

		SourceLinksWrapper sourceLinksWrapper = create
				.select(sl.SOURCE_LINKS)
				.from(sl)
				.where(sl.WORD_ID.eq(wordId))
				.fetchOptionalInto(SourceLinksWrapper.class).orElse(null);
		if (sourceLinksWrapper == null) {
			return null;
		}
		return sourceLinksWrapper.getSourceLinks();
	}

	public List<TypeSourceLink> getFreeformSourceLinks(Long wordId) {

		MviewWwFreeformSourceLink sl = MVIEW_WW_FREEFORM_SOURCE_LINK.as("sl");

		SourceLinksWrapper sourceLinksWrapper = create
				.select(sl.SOURCE_LINKS)
				.from(sl)
				.where(sl.WORD_ID.eq(wordId))
				.fetchOptionalInto(SourceLinksWrapper.class).orElse(null);
		if (sourceLinksWrapper == null) {
			return null;
		}
		return sourceLinksWrapper.getSourceLinks();
	}
}
