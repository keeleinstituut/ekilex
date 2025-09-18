package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.OS_LEXEME_MEANING;
import static eki.wordweb.data.db.Tables.OS_WORD;
import static eki.wordweb.data.db.Tables.OS_WORD_EKI_RECOMMENDATION;
import static eki.wordweb.data.db.Tables.OS_WORD_OS_MORPH;
import static eki.wordweb.data.db.Tables.OS_WORD_OS_USAGE;
import static eki.wordweb.data.db.Tables.OS_WORD_RELATION;
import static eki.wordweb.data.db.Tables.OS_WORD_RELATION_IDX;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.WordsMatch;
import eki.wordweb.data.db.Routines;
import eki.wordweb.data.db.tables.OsLexemeMeaning;
import eki.wordweb.data.db.tables.OsWord;
import eki.wordweb.data.db.tables.OsWordEkiRecommendation;
import eki.wordweb.data.db.tables.OsWordOsMorph;
import eki.wordweb.data.db.tables.OsWordOsUsage;
import eki.wordweb.data.db.tables.OsWordRelation;
import eki.wordweb.data.db.tables.OsWordRelationIdx;

@Component
public class OsDbService implements SystemConstant, GlobalConstant {

	@Autowired
	private DSLContext create;

	public List<eki.wordweb.data.os.OsWord> getWords(String searchValue, boolean fiCollationExists) {

		Field<String> searchValueLowerField = DSL.lower(searchValue);

		OsWord w = OS_WORD.as("w");
		Condition where = DSL.lower(w.VALUE).eq(searchValueLowerField);

		return getWords(w, where, fiCollationExists);
	}

	public List<eki.wordweb.data.os.OsWord> getRelatedWords(String searchValue, boolean fiCollationExists) {

		Field<String> searchValueLowerField = DSL.lower(searchValue);

		OsWord w = OS_WORD.as("w");
		OsWordRelationIdx wr = OS_WORD_RELATION_IDX.as("wr");
		Condition where = DSL
				.exists(DSL
						.select(wr.WORD_ID)
						.from(wr)
						.where(
								wr.WORD_ID.eq(w.WORD_ID)
										.and(DSL.lower(wr.VALUE).eq(searchValueLowerField)))

				);

		return getWords(w, where, fiCollationExists);
	}

	private List<eki.wordweb.data.os.OsWord> getWords(OsWord w, Condition where, boolean fiCollationExists) {

		OsLexemeMeaning lm = OS_LEXEME_MEANING.as("lm");

		Field<JSON> lmf = DSL
				.select(lm.LEXEME_MEANINGS)
				.from(lm)
				.where(lm.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<String> wvobf;
		if (fiCollationExists) {
			wvobf = w.VALUE.collate("fi_FI");
		} else {
			wvobf = w.VALUE;
		}

		return create
				.select(w.fields())
				.select(lmf.as("lexeme_meanings"))
				.from(w)
				.where(where)
				.orderBy(
						wvobf,
						w.HOMONYM_NR)
				.fetchInto(eki.wordweb.data.os.OsWord.class);
	}

	public eki.wordweb.data.os.OsWord getWord(Long wordId) {

		OsWord w = OS_WORD.as("w");
		OsWordOsMorph wom = OS_WORD_OS_MORPH.as("wom");
		OsWordOsUsage wou = OS_WORD_OS_USAGE.as("wou");
		OsWordEkiRecommendation wer = OS_WORD_EKI_RECOMMENDATION.as("wer");
		OsLexemeMeaning lm = OS_LEXEME_MEANING.as("lm");
		OsWordRelation wr = OS_WORD_RELATION.as("wr");

		Field<JSON> womf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("wordId").value(wom.WORD_ID),
								DSL.key("wordOsMorphId").value(wom.WORD_OS_MORPH_ID),
								DSL.key("value").value(wom.VALUE),
								DSL.key("valuePrese").value(wom.VALUE_PRESE)))
				.from(wom)
				.where(wom.WORD_ID.eq(w.WORD_ID))
				.limit(1)
				.asField();

		Field<JSON> wouf = DSL
				.select(wou.WORD_OS_USAGES)
				.from(wou)
				.where(wou.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<JSON> worf = DSL
				.select(wer.WORD_EKI_RECOMMENDATIONS)
				.from(wer)
				.where(wer.WORD_ID.eq(w.WORD_ID))
				.limit(1)
				.asField();

		Field<JSON> lmf = DSL
				.select(lm.LEXEME_MEANINGS)
				.from(lm)
				.where(lm.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<JSON> wrf = DSL
				.select(wr.WORD_RELATION_GROUPS)
				.from(wr)
				.where(wr.WORD_ID.eq(w.WORD_ID))
				.asField();

		return create
				.select(w.fields())
				.select(
						womf.as("word_os_morph"),
						wouf.as("word_os_usages"),
						worf.as("word_eki_recommendations"),
						lmf.as("lexeme_meanings"),
						wrf.as("word_relation_groups"))
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOptionalInto(eki.wordweb.data.os.OsWord.class)
				.orElse(null);
	}

	public WordsMatch getWordsWithMask(String searchValue) {

		searchValue = StringUtils.trim(searchValue);
		searchValue = StringUtils.replace(searchValue, SEARCH_MASK_CHARS, "%");
		searchValue = StringUtils.replace(searchValue, SEARCH_MASK_CHAR, "_");
		Field<String> searchValueLowerField = DSL.lower(searchValue);

		OsWord w = OS_WORD.as("w");
		Condition where = DSL.lower(w.VALUE).like(searchValueLowerField);

		List<String> wordValues = create
				.selectDistinct(w.VALUE)
				.from(w)
				.where(where)
				.orderBy(w.VALUE)
				.limit(MASKED_SEARCH_RESULT_LIMIT)
				.fetchInto(String.class);

		int resultCount = create
				.select(DSL.countDistinct(w.VALUE))
				.from(w)
				.where(where)
				.fetchSingleInto(int.class);

		boolean resultExists = resultCount > 0;
		boolean singleResult = resultCount == 1;

		return new WordsMatch(wordValues, resultExists, singleResult, resultCount);
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<WordSearchElement>> getWordsByInfixLev(String wordInfix, String wordInfixUnaccent, int maxWordCount) {

		Field<String> wordInfixLowerField = DSL.lower(wordInfix);
		Field<String> wordInfixLowerLikeField = DSL.lower('%' + wordInfix + '%');
		Field<String> wordInfixLowerUnaccentLikeField;
		if (StringUtils.isBlank(wordInfixUnaccent)) {
			wordInfixLowerUnaccentLikeField = wordInfixLowerLikeField;
		} else {
			wordInfixLowerUnaccentLikeField = DSL.lower('%' + wordInfixUnaccent + '%');
		}

		OsWord w = OS_WORD.as("w");
		OsWord aw = OS_WORD.as("aw");
		OsWordRelationIdx wr = OS_WORD_RELATION_IDX.as("wr");

		Table<Record2<String, String>> ws = DSL
				.select(
						DSL.val(WORD_SEARCH_GROUP_WORD).as("sgroup"),
						w.VALUE.as("word_value"))
				.from(w)
				.where(DSL.lower(w.VALUE).like(wordInfixLowerLikeField))
				.unionAll(DSL
						.select(
								DSL.val(WORD_SEARCH_GROUP_WORD).as("sgroup"),
								aw.VALUE.as("word_value"))
						.from(aw)
						.where(DSL.lower(aw.VALUE_AS_WORD).like(wordInfixLowerUnaccentLikeField)))
				.asTable("ws");

		Table<Record2<String, String>> wrs = DSL
				.select(
						DSL.val(WORD_SEARCH_GROUP_WORD_RELATION).as("sgroup"),
						w.VALUE.as("word_value"))
				.from(w)
				.whereExists(DSL
						.select(wr.WORD_ID)
						.from(wr)
						.where(
								wr.WORD_ID.eq(w.WORD_ID)
										.and(DSL.lower(wr.VALUE).eq(wordInfixLowerField))))
				.orderBy(w.VALUE)
				.asTable("wrs");

		Field<Integer> wlf = DSL.field(Routines.levenshtein1(ws.field("word_value", String.class), wordInfixLowerField));

		Table<Record3<String, String, Integer>> wst = DSL
				.select(
						ws.field("sgroup", String.class),
						ws.field("word_value", String.class),
						wlf.as("lev"))
				.from(ws)
				.orderBy(DSL.field("lev"))
				.limit(maxWordCount)
				.unionAll(DSL
						.select(
								wrs.field("sgroup", String.class),
								wrs.field("word_value", String.class),
								DSL.val(0).as("lev"))
						.from(wrs)
						.orderBy(DSL.field("word_value"))
						.limit(maxWordCount))
				.asTable("wst");

		return (Map<String, List<WordSearchElement>>) create
				.select(
						wst.field("sgroup", String.class),
						wst.field("word_value", String.class))
				.from(wst)
				.fetchGroups("sgroup", WordSearchElement.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public boolean fiCollationExists() {

		Integer fiCollationCnt = create
				.selectCount()
				.from("pg_collation where lower(collcollate) = 'fi_fi.utf8'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
