package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME;
import static eki.wordweb.data.db.Tables.MVIEW_WW_LEXEME_RELATION;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_SEARCH;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.wordweb.data.DataFilter;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordSearchElement;
import eki.wordweb.data.db.Routines;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwLexeme;
import eki.wordweb.data.db.tables.MviewWwLexemeRelation;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordSearch;
import eki.wordweb.data.db.udt.records.TypeLangComplexityRecord;

@Component
public class SimpleSearchDbService extends AbstractSearchDbService {

	@SuppressWarnings("unchecked")
	public Map<String, List<WordSearchElement>> getWordsByInfixLev(String wordInfix, List<String> destinLangs, int maxWordCount) {

		String wordInfixLower = StringUtils.lowerCase(wordInfix);
		String wordInfixCrit = '%' + wordInfixLower + '%';
		String wordInfixCritUnaccent = '%' + StringUtils.stripAccents(wordInfixLower) + '%';

		MviewWwWordSearch w = MVIEW_WW_WORD_SEARCH.as("w");
		MviewWwWordSearch aw = MVIEW_WW_WORD_SEARCH.as("aw");
		MviewWwWordSearch f = MVIEW_WW_WORD_SEARCH.as("f");
		Field<String> wgf = DSL.field(DSL.val(WORD_SEARCH_GROUP_WORD));

		Table<Record5<String, String, String, Long, TypeLangComplexityRecord[]>> ws = DSL
				.select(
						wgf.as("sgroup"),
						w.WORD,
						w.CRIT,
						w.LANG_ORDER_BY,
						w.LANG_COMPLEXITIES)
				.from(w)
				.where(
						w.SGROUP.eq(WORD_SEARCH_GROUP_WORD)
								.and(w.UNACRIT.like(wordInfixCritUnaccent))
								.and(w.SIMPLE_EXISTS.isTrue()))
				.unionAll(DSL
						.select(
								wgf.as("sgroup"),
								aw.WORD,
								aw.CRIT,
								aw.LANG_ORDER_BY,
								aw.LANG_COMPLEXITIES)
						.from(aw)
						.where(
								aw.SGROUP.eq(WORD_SEARCH_GROUP_AS_WORD)
										.and(aw.UNACRIT.like(wordInfixCritUnaccent))
										.and(aw.SIMPLE_EXISTS.isTrue())))
				.asTable("ws");

		Field<Integer> wlf = DSL.field(Routines.levenshtein1(ws.field("word", String.class), DSL.inline(wordInfixLower)));
		Table<?> lc = DSL.unnest(ws.field("lang_complexities")).as("lc", "lang", "complexity");
		Condition langCompWhere = lc.field("complexity", String.class).eq(Complexity.SIMPLE.name());
		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).eq(destinLang));
			} else {
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).in(destinLangs));
			}
		}

		Table<Record3<String, String, Integer>> wfs = DSL
				.select(
						ws.field("sgroup", String.class),
						ws.field("word", String.class),
						wlf.as("lev"))
				.from(ws)
				.where(
						ws.field("crit").like(wordInfixCrit)
						.andExists(DSL.selectFrom(lc).where(langCompWhere))
						)
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
						.where(
								f.SGROUP.eq(WORD_SEARCH_GROUP_FORM)
								.and(f.CRIT.eq(wordInfixLower))
								.and(f.SIMPLE_EXISTS.isTrue()))
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

	public List<Word> getWords(String searchWord, DataFilter dataFilter) {

		List<String> destinLangs = dataFilter.getDestinLangs();
		List<String> datasetCodes = dataFilter.getDatasetCodes();

		MviewWwWord w = MVIEW_WW_WORD.as("w");
		MviewWwForm f = MVIEW_WW_FORM.as("f");

		String searchWordLower = StringUtils.lowerCase(searchWord);
		Condition where = DSL.exists(DSL
				.select(f.WORD_ID)
				.from(f)
				.where(f.WORD_ID.eq(w.WORD_ID)
						.and(DSL.lower(f.FORM).eq(searchWordLower))));

		if (CollectionUtils.isNotEmpty(datasetCodes)) {
			String[] datasetCodesArr = datasetCodes.toArray(new String[0]);
			where = where.and(PostgresDSL.arrayOverlap(w.DATASET_CODES, datasetCodesArr));
		}

		Table<?> lc = DSL.unnest(w.LANG_COMPLEXITIES).as("lc", "lang", "complexity");
		where = where.and(w.LEX_DATASET_EXISTS.isTrue());
		Condition langCompWhere = lc.field("complexity", String.class).eq(Complexity.SIMPLE.name());
		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).eq(destinLang));
			} else {
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).in(destinLangs));
			}
		}
		where = where.andExists(DSL.selectFrom(lc).where(langCompWhere));

		return getWords(w, where);
	}

	@Override
	public List<Lexeme> getLexemes(Long wordId, DataFilter dataFilter) {

		DatasetType datasetType = dataFilter.getDatasetType();
		List<String> destinLangs = dataFilter.getDestinLangs();
		List<String> datasetCodes = dataFilter.getDatasetCodes();

		MviewWwLexeme l = MVIEW_WW_LEXEME.as("l");
		MviewWwLexemeRelation lr = MVIEW_WW_LEXEME_RELATION.as("lr");

		Condition where = l.WORD_ID.eq(wordId);
		if (datasetType != null) {
			where = where.and(l.DATASET_TYPE.eq(datasetType.name()));
		}
		if (CollectionUtils.isNotEmpty(datasetCodes)) {
			where = where.and(l.DATASET_CODE.in(datasetCodes));
		}
		Table<?> lc = DSL.unnest(l.LANG_COMPLEXITIES).as("lc", "lang", "complexity");
		Condition langCompWhere = lc.field("complexity", String.class).eq(Complexity.SIMPLE.name());
		if (CollectionUtils.isNotEmpty(destinLangs)) {
			if (destinLangs.size() == 1) {
				String destinLang = destinLangs.get(0);
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).eq(destinLang));
			} else {
				langCompWhere = langCompWhere.and(lc.field("lang", String.class).in(destinLangs));
			}
		}
		where = where.andExists(DSL.selectFrom(lc).where(langCompWhere));

		return getLexemes(l, lr, where);
	}
}
