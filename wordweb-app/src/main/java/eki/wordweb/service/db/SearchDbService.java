package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_COLLOCATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_COUNTS;
import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_FREEFORM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_MEANING_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYMOLOGY;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYM_SOURCE_LINK;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_SEARCH;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.LinkedWordSearchElement;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordForm;
import eki.wordweb.data.WordRelationsTuple;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.db.Routines;
import eki.wordweb.data.db.tables.MviewWwCollocation;
import eki.wordweb.data.db.tables.MviewWwCounts;
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
import eki.wordweb.data.db.tables.MviewWwWordSearch;
import eki.wordweb.data.db.udt.records.TypeLangComplexityRecord;

@Component
public class SearchDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext create;

	public String getRandomWord(String lang) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwCounts c = MVIEW_WW_COUNTS.as("c");

		Table<Record2<String, Integer>> ww = DSL
				.select(w.WORD, DSL.rowNumber().over().as("rownum"))
				.from(w)
				.where(w.LANG.eq(lang))
				.asTable("w");
		Table<Record1<BigDecimal>> cc = DSL
				.select(DSL.round(PostgresDSL.rand().multiply(c.WORD_VALUE_COUNT)).as("rndrownum"))
				.from(c)
				.where(
						c.LANG.eq(lang)
								.and(c.DATASET_CODE.eq(DATASET_ALL)))
				.asTable("c");
		return create
				.select(ww.field("word", String.class))
				.from(ww, cc)
				.where(ww.field("rownum", Integer.class).eq(cc.field("rndrownum", Integer.class)))
				.fetchOneInto(String.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordSearchElement>> getWordsByInfixLev(
			String wordInfix, String wordInfixUnaccent, SearchContext searchContext, int maxWordCount) {

		Field<String> wordInfixLowerField = DSL.lower(wordInfix);
		Field<String> wordInfixLowerLikeField = DSL.lower('%' + wordInfix + '%');
		Field<String> wordInfixLowerUnaccentLikeField;
		if (StringUtils.isBlank(wordInfixUnaccent)) {
			wordInfixLowerUnaccentLikeField = wordInfixLowerLikeField;
		} else {
			wordInfixLowerUnaccentLikeField = DSL.lower('%' + wordInfixUnaccent + '%');
		}
		List<String> destinLangs = searchContext.getDestinLangs();

		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		MviewWwWordSearch f = MVIEW_WW_WORD_SEARCH.as("f");
		Field<String> wgf = DSL.field(DSL.val(WORD_SEARCH_GROUP_WORD));

		Condition wwhere = applyWordLangFilter(w, destinLangs, DSL.noCondition())
				.and(w.SGROUP.eq(WORD_SEARCH_GROUP_WORD))
				.and(w.CRIT.like(wordInfixLowerLikeField));

		Condition awwhere = applyWordLangFilter(aw, destinLangs, DSL.noCondition())
				.and(aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD))
				.and(aw.CRIT.like(wordInfixLowerUnaccentLikeField));

		Condition fwhere = applyWordLangFilter(f, destinLangs, DSL.noCondition())
				.and(f.SGROUP.eq(WORD_SEARCH_GROUP_FORM))
				.and(f.CRIT.eq(wordInfixLowerField));

		Table<Record5<String, String, String, Long, TypeLangComplexityRecord[]>> ws = DSL
				.select(
						wgf.as("sgroup"),
						w.WORD,
						w.CRIT,
						w.LANG_ORDER_BY,
						w.LANG_COMPLEXITIES)
				.from(w)
				.where(wwhere)
				.unionAll(DSL
						.select(
								wgf.as("sgroup"),
								aw.WORD,
								aw.CRIT,
								aw.LANG_ORDER_BY,
								aw.LANG_COMPLEXITIES)
						.from(aw)
						.where(awwhere))
				.asTable("ws");

		Field<Integer> wlf = DSL.field(Routines.levenshtein1(ws.field("word", String.class), wordInfixLowerField));

		Condition wsWhere = applyContainingLangComplexityDatasetFilter(ws, searchContext, DSL.noCondition());

		Table<Record3<String, String, Integer>> wfs = DSL
				.select(
						ws.field("sgroup", String.class),
						ws.field("word", String.class),
						wlf.as("lev"))
				.from(ws)
				.where(wsWhere)
				.orderBy(
						ws.field("lang_order_by"),
						DSL.field("lev"))
				.limit(maxWordCount)
				.unionAll(DSL
						.select(
								f.SGROUP,
								f.WORD,
								DSL.field(DSL.val(0)).as("lev"))
						.from(f)
						.where(fwhere)
						.orderBy(f.WORD)
						.limit(maxWordCount))
				.asTable("wfs");

		return (Map<String, List<WordSearchElement>>) create
				.select(
						wfs.field("sgroup", String.class),
						wfs.field("word", String.class))
				.from(wfs)
				.fetchGroups("sgroup", WordSearchElement.class);
	}

	public WordsMatch getWordsWithMask(String searchWord, SearchContext searchContext) {

		searchWord = StringUtils.trim(searchWord);
		searchWord = StringUtils.replace(searchWord, SEARCH_MASK_CHARS, "%");
		searchWord = StringUtils.replace(searchWord, SEARCH_MASK_CHAR, "_");
		Field<String> searchWordLowerField = DSL.lower(searchWord);
		List<String> destinLangs = searchContext.getDestinLangs();

		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		Condition where = w.SGROUP.eq(WORD_SEARCH_GROUP_WORD).and(w.CRIT.like(searchWordLowerField));
		where = applyContainingLangComplexityDatasetFilter(w, searchContext, where);
		where = applyWordLangFilter(w, destinLangs, where);

		List<String> wordValues = create
				.select(w.WORD)
				.from(w)
				.where(where)
				.orderBy(w.WORD)
				.limit(MASKED_SEARCH_RESULT_LIMIT)
				.fetchInto(String.class);

		int resultCount = create
				.selectCount()
				.from(w)
				.where(where)
				.fetchSingleInto(int.class);

		boolean resultsExist = resultCount > 0;
		boolean singleResult = resultCount == 1;

		return new WordsMatch(wordValues, resultsExist, singleResult, resultCount);
	}

	public List<Word> getWords(String searchWord, SearchContext searchContext, boolean ignoreForms) {

		if (StringUtils.equals(searchWord, ILLEGAL_FORM_VALUE)) {
			return Collections.emptyList();
		}

		Field<String> searchWordLowerField = DSL.lower(searchWord);
		List<String> destinLangs = searchContext.getDestinLangs();
		boolean fiCollationExists = searchContext.isFiCollationExists();
		boolean excludeQuestionable = searchContext.isExcludeQuestionable();

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwForm f = MVIEW_WW_FORM.as("f");
		Table<Record> ww;

		if (ignoreForms) {

			ww = DSL
					.select(w.fields())
					.select(DSL.field(DSL.val(true)).as("word_match"))
					.select(DSL.field(DSL.val(false)).as("form_match"))
					.from(w)
					.where(DSL.or(
							DSL.lower(w.WORD).eq(searchWordLowerField),
							DSL.lower(w.AS_WORD).eq(searchWordLowerField)))
					.asTable("w");

		} else {

			Condition whereForm = f.WORD_ID.eq(w.WORD_ID)
					.and(DSL.lower(f.VALUE).eq(searchWordLowerField))
					.and(f.VALUE.ne(f.WORD))
					.and(f.MORPH_CODE.ne(UNKNOWN_FORM_CODE))
					.and(f.MORPH_EXISTS.isTrue());

			if (excludeQuestionable) {
				whereForm = whereForm.and(f.IS_QUESTIONABLE.isFalse());
			}
			ww = DSL
					.select(w.fields())
					.select(DSL.field(DSL.val(true)).as("word_match"))
					.select(DSL.field(DSL.val(false)).as("form_match"))
					.from(w)
					.where(DSL.or(
							DSL.lower(w.WORD).eq(searchWordLowerField),
							DSL.lower(w.AS_WORD).eq(searchWordLowerField)))
					.unionAll(DSL
							.select(w.fields())
							.select(DSL.field(DSL.val(false)).as("word_match"))
							.select(DSL.field(DSL.val(true)).as("form_match"))
							.from(w)
							.whereExists(DSL
									.select(f.WORD_ID)
									.from(f)
									.where(whereForm)))
					.asTable("w");
		}

		Condition where = applyContainingLangComplexityDatasetFilter(ww, searchContext, DSL.noCondition());

		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				where = where.and(ww.field("lang_filt", String.class).eq(destinLang));
			} else {
				where = where.and(ww.field("lang_filt", String.class).in(destinLangs));
			}
		}

		Field<String> wvobf;
		if (fiCollationExists) {
			wvobf = ww.field("word", String.class).collate("fi_FI");
		} else {
			wvobf = ww.field("word", String.class);
		}
		return create
				.selectFrom(ww)
				.where(where)
				.orderBy(
						ww.field("min_ds_order_by"),
						ww.field("lang_order_by"),
						wvobf,
						ww.field("word_type_order_by"),
						ww.field("homonym_nr"))
				.fetchInto(Word.class);
	}

	public LanguagesDatasets getAvailableLanguagesDatasets(String searchWord, Complexity lexComplexity) {

		Field<String> searchWordLowerField = DSL.lower(searchWord);

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");

		List<String> complexityNames = composeFilteringComplexityNames(lexComplexity);

		Table<?> lc = DSL.unnest(w.LANG_COMPLEXITIES).as("lc", "lang", "dataset_code", "lex_complexity", "data_complexity");
		Condition lcWhere = lc.field("lex_complexity", String.class).in(complexityNames)
				.and(lc.field("data_complexity", String.class).in(complexityNames));

		return create
				.select(
						DSL.arrayAggDistinct(w.LANG).as("language_codes"),
						DSL.arrayAggDistinct(l.DATASET_CODE).as("dataset_codes"))
				.from(w, l)
				.where(DSL.or(
						DSL.lower(w.WORD).eq(searchWordLowerField),
						DSL.lower(w.AS_WORD).eq(searchWordLowerField))
						.and(l.WORD_ID.eq(w.WORD_ID))
						.andExists(DSL.select(DSL.val(1)).from(lc).where(lcWhere)))
				.fetchOptionalInto(LanguagesDatasets.class)
				.orElse(null);
	}

	public List<LexemeWord> getWordLexemes(Long wordId, SearchContext searchContext) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");
		MviewWwLexemeSourceLink lsl = MVIEW_WW_LEXEME_SOURCE_LINK.as("lsl");
		MviewWwLexemeFreeformSourceLink ffsl = MVIEW_WW_LEXEME_FREEFORM_SOURCE_LINK.as("ffsl");

		Condition where = composeLexemeJoinCond(l, searchContext).and(l.WORD_ID.eq(wordId));

		return create
				.select(
						l.WORD_ID,
						l.LEXEME_ID,
						l.MEANING_ID,
						l.DATASET_CODE,
						l.DATASET_TYPE,
						l.DATASET_NAME,
						l.VALUE_STATE_CODE,
						l.PROFICIENCY_LEVEL_CODE,
						l.RELIABILITY,
						l.LEVEL1,
						l.LEVEL2,
						l.COMPLEXITY,
						l.WEIGHT,
						l.DATASET_ORDER_BY,
						l.LEXEME_ORDER_BY,
						l.REGISTER_CODES,
						l.POS_CODES,
						l.DERIV_CODES,
						l.MEANING_WORDS,
						l.ADVICE_NOTES,
						l.NOTES.as("lexeme_notes"),
						l.GRAMMARS,
						l.GOVERNMENTS,
						l.USAGES,
						lsl.SOURCE_LINKS.as("lexeme_source_links"),
						ffsl.SOURCE_LINKS.as("lexeme_freeform_source_links"),
						lr.RELATED_LEXEMES)
				.from(l
						.leftOuterJoin(lsl).on(lsl.LEXEME_ID.eq(l.LEXEME_ID))
						.leftOuterJoin(ffsl).on(ffsl.LEXEME_ID.eq(l.LEXEME_ID))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l.LEXEME_ID)))
				.where(where)
				.fetchInto(LexemeWord.class);
	}

	public List<LexemeWord> getMeaningsLexemes(Long wordId, SearchContext searchContext) {

		MviewWwLexeme l1 = MVIEW_WW_LEXEME.as("l1");
		MviewWwLexeme l2 = MVIEW_WW_LEXEME.as("l2");
		MviewWwWord w2 = MVIEW_WW_WORD.as("w2");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");
		MviewWwLexemeSourceLink lsl = MVIEW_WW_LEXEME_SOURCE_LINK.as("lsl");
		MviewWwLexemeFreeformSourceLink ffsl = MVIEW_WW_LEXEME_FREEFORM_SOURCE_LINK.as("ffsl");

		Condition whereL1 = composeLexemeJoinCond(l1, searchContext).and(l1.WORD_ID.eq(wordId));
		Condition whereL2 = composeLexemeJoinCond(l2, searchContext);

		return create
				.select(
						l2.WORD_ID,
						l2.LEXEME_ID,
						l2.MEANING_ID,
						l2.DATASET_CODE,
						l2.DATASET_TYPE,
						l2.DATASET_NAME,
						l2.VALUE_STATE_CODE,
						l2.RELIABILITY,
						l2.LEVEL1,
						l2.LEVEL2,
						l2.COMPLEXITY,
						l2.WEIGHT,
						l2.DATASET_ORDER_BY,
						l2.LEXEME_ORDER_BY,
						l2.REGISTER_CODES,
						l2.POS_CODES,
						l2.REGION_CODES,
						l2.DERIV_CODES,
						l2.MEANING_WORDS,
						l2.ADVICE_NOTES,
						l2.NOTES.as("lexeme_notes"),
						l2.GRAMMARS,
						l2.GOVERNMENTS,
						l2.USAGES,
						w2.WORD,
						w2.WORD_PRESE,
						w2.AS_WORD,
						w2.HOMONYM_NR,
						w2.LANG,
						w2.GENDER_CODE,
						w2.ASPECT_CODE,
						w2.WORD_TYPE_CODES,
						lsl.SOURCE_LINKS.as("lexeme_source_links"),
						ffsl.SOURCE_LINKS.as("lexeme_freeform_source_links"),
						lr.RELATED_LEXEMES)
				.from(l1
						.innerJoin(l2).on(l2.MEANING_ID.eq(l1.MEANING_ID).and(whereL2))
						.innerJoin(w2).on(w2.WORD_ID.eq(l2.WORD_ID))
						.leftOuterJoin(lsl).on(lsl.LEXEME_ID.eq(l2.LEXEME_ID))
						.leftOuterJoin(ffsl).on(ffsl.LEXEME_ID.eq(l2.LEXEME_ID))
						.leftOuterJoin(lr).on(lr.LEXEME_ID.eq(l2.LEXEME_ID)))
				.where(whereL1)
				.fetchInto(LexemeWord.class);
	}

	public LinkedWordSearchElement getFirstMeaningWord(Long meaningId, SearchContext searchContext) {

		List<String> destinLangs = searchContext.getDestinLangs();
		List<String> datasetCodes = searchContext.getDatasetCodes();

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwWord w = MVIEW_WW_WORD.as("w");
		Condition where = l.WORD_ID.eq(w.WORD_ID).and(l.MEANING_ID.eq(meaningId));

		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				where = where.and(w.LANG_FILT.eq(destinLang));
			} else {
				where = where.and(w.LANG_FILT.in(destinLangs));
			}
		}
		if (CollectionUtils.isNotEmpty(datasetCodes)) {
			if (datasetCodes.size() == 1) {
				String datasetCode = datasetCodes.get(0);
				where = where.and(l.DATASET_CODE.eq(datasetCode));
			} else {
				where = where.and(l.DATASET_CODE.in(datasetCodes));
			}
		}

		return create
				.select(w.WORD, w.HOMONYM_NR, l.LEXEME_ID, l.MEANING_ID)
				.from(l, w)
				.where(where)
				.orderBy(l.DATASET_ORDER_BY, w.LANG_ORDER_BY, l.LEXEME_ORDER_BY)
				.limit(1)
				.fetchOptionalInto(LinkedWordSearchElement.class)
				.orElse(null);
	}

	private Condition composeLexemeJoinCond(MviewWwLexeme l, SearchContext searchContext) {

		DatasetType datasetType = searchContext.getDatasetType();
		List<String> datasetCodes = searchContext.getDatasetCodes();
		Complexity lexComplexity = searchContext.getLexComplexity();
		List<String> complexityNames = composeFilteringComplexityNames(lexComplexity);

		Condition where = l.COMPLEXITY.in(complexityNames);
		if (datasetType != null) {
			where = where.and(l.DATASET_TYPE.eq(datasetType.name()));
		}
		if (CollectionUtils.isNotEmpty(datasetCodes)) {
			if (datasetCodes.size() == 1) {
				String datasetCode = datasetCodes.get(0);
				where = where.and(l.DATASET_CODE.eq(datasetCode));
			} else {
				where = where.and(l.DATASET_CODE.in(datasetCodes));
			}
		}
		where = applyContainingLangComplexityDatasetFilter(l, searchContext, where);
		return where;
	}

	private List<String> composeFilteringComplexityNames(Complexity lexComplexity) {
		return Arrays.asList(Complexity.ANY.name(), lexComplexity.name());
	}

	private Condition applyContainingLangComplexityDatasetFilter(Table<?> lcTable, SearchContext searchContext, Condition where) {

		//List<String> destinLangs = searchContext.getDestinLangs();
		List<String> datasetCodes = searchContext.getDatasetCodes();
		Complexity lexComplexity = searchContext.getLexComplexity();
		List<String> complexityNames = composeFilteringComplexityNames(lexComplexity);

		Table<?> lc = DSL.unnest(lcTable.field("lang_complexities")).as("lc", "lang", "dataset_code", "lex_complexity", "data_complexity");
		Condition lcWhere = lc.field("lex_complexity", String.class).in(complexityNames)
				.and(lc.field("data_complexity", String.class).in(complexityNames));
		/*
		 * filtering by containing data language is now disabled
		 * 
		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				lcWhere = lcWhere.and(lc.field("lang", String.class).eq(destinLang));
			} else {
				lcWhere = lcWhere.and(lc.field("lang", String.class).in(destinLangs));
			}
		}
		*/
		if (CollectionUtils.isNotEmpty(datasetCodes)) {
			if (datasetCodes.size() == 1) {
				String datasetCode = datasetCodes.get(0);
				lcWhere = lcWhere.and(lc.field("dataset_code", String.class).eq(datasetCode));
			} else {
				lcWhere = lcWhere.and(lc.field("dataset_code", String.class).in(datasetCodes));
			}
		}
		where = where.andExists(DSL.select(DSL.val(1)).from(lc).where(lcWhere));
		return where;
	}

	private Condition applyWordLangFilter(MviewWwWordSearch w, List<String> destinLangs, Condition where) {

		if (CollectionUtils.isEmpty(destinLangs)) {
			return where;
		} else if (destinLangs.size() == 1) {
			String destinLang = destinLangs.get(0);
			where = where.and(DSL.value(destinLang).eq(DSL.any(w.LANGS_FILT)));
		} else {
			where = where.and(DSL.condition(toSqlArray(destinLangs) + "::varchar(10)[] @> {0}", w.LANGS_FILT));
		}
		return where;
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
						w.LANG,
						w.WORD_TYPE_CODES,
						w.DISPLAY_MORPH_CODE,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.VOCAL_FORM,
						w.MANUAL_EVENT_ON,
						w.LAST_ACTIVITY_EVENT_ON,
						w.MEANING_WORDS,
						w.DEFINITIONS,
						wesl.SOURCE_LINKS.as("word_etym_source_links"),
						w.OD_WORD_RECOMMENDATIONS,
						w.FORMS_EXIST)
				.from(w.leftOuterJoin(wesl).on(wesl.WORD_ID.eq(wordId)))
				.where(w.WORD_ID.eq(wordId))
				.fetchOneInto(Word.class);
	}

	public LinkedWordSearchElement getWordValue(Long wordId) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");

		return create
				.select(w.WORD, w.HOMONYM_NR)
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOptionalInto(LinkedWordSearchElement.class)
				.orElse(null);
	}

	public List<Form> getParadigmForms(Long paradigmId, Integer maxDisplayLevel, boolean excludeQuestionable) {

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		Condition where = f.PARADIGM_ID.eq(paradigmId);
		if (maxDisplayLevel != null) {
			where = where.and(f.DISPLAY_LEVEL.le(maxDisplayLevel));
		}
		if (excludeQuestionable) {
			where = where.and(f.IS_QUESTIONABLE.isFalse());
		}

		return create
				.selectFrom(f)
				.where(where)
				.orderBy(f.ORDER_BY, f.FORM_ID)
				.fetchInto(Form.class);
	}

	public List<Form> getWordForms(Long wordId, SearchContext searchContext) {

		Integer maxDisplayLevel = searchContext.getMaxDisplayLevel();
		boolean excludeQuestionable = searchContext.isExcludeQuestionable();

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		Condition where = f.WORD_ID.eq(wordId);
		if (maxDisplayLevel != null) {
			where = where.and(f.DISPLAY_LEVEL.le(maxDisplayLevel));
		}
		if (excludeQuestionable) {
			where = where.and(f.IS_QUESTIONABLE.isFalse());
		}
		return create
				.selectFrom(f)
				.where(where)
				.orderBy(f.PARADIGM_ID, f.ORDER_BY, f.FORM_ID)
				.fetchInto(Form.class);
	}

	@Cacheable(value = CACHE_KEY_NULL_WORD, key = "{#wordId, #tokens}")
	public List<WordForm> getWordFormCandidates(Long wordId, List<String> tokens) {

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		return create
				.select(
						f.WORD,
						f.VALUE.as("form"))
				.from(f)
				.where(
						f.WORD_ID.eq(wordId)
								.and(f.VALUE.in(tokens)))
				.fetchInto(WordForm.class);
	}

	public List<Meaning> getMeanings(Long wordId) {

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwMeaning m = MVIEW_WW_MEANING.as("m");
		MviewWwMeaningRelation mr = MVIEW_WW_MEANING_RELATION.as("mr");
		MviewWwMeaningFreeformSourceLink ffsl = MVIEW_WW_MEANING_FREEFORM_SOURCE_LINK.as("ffsl");

		Condition where = l.WORD_ID.eq(wordId);

		return create
				.select(
						l.LEXEME_ID,
						m.MEANING_ID,
						m.MANUAL_EVENT_ON.as("meaning_manual_event_on"),
						m.LAST_APPROVE_OR_EDIT_EVENT_ON.as("meaning_last_activity_event_on"),
						m.DOMAIN_CODES,
						m.IMAGE_FILES,
						m.MEDIA_FILES,
						m.SYSTEMATIC_POLYSEMY_PATTERNS,
						m.SEMANTIC_TYPES,
						m.LEARNER_COMMENTS,
						m.NOTES,
						m.DEFINITIONS,
						mr.RELATED_MEANINGS,
						ffsl.SOURCE_LINKS.as("freeform_source_links"))
				.from(
						l.innerJoin(m).on(m.MEANING_ID.eq(l.MEANING_ID))
								.leftOuterJoin(mr).on(mr.MEANING_ID.eq(m.MEANING_ID))
								.leftOuterJoin(ffsl).on(ffsl.MEANING_ID.eq(m.MEANING_ID)))
				.where(where)
				.orderBy(m.MEANING_ID, l.LEXEME_ID)
				.fetchInto(Meaning.class);
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

	private String toSqlArray(List<String> values) {
		String sqlArray = "(array['" + StringUtils.join(values, "','") + "'])";
		return sqlArray;
	}
}
