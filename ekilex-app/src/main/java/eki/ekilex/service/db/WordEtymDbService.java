package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_SOURCE_LINK;

import java.util.List;

import org.jooq.CommonTableExpression;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymNodeTuple;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordEtymology;
import eki.ekilex.data.db.main.tables.WordEtymologyRelation;
import eki.ekilex.data.db.main.tables.WordEtymologySourceLink;

@Component
public class WordEtymDbService extends AbstractDataDbService {

	public List<WordEtymNodeTuple> getWordEtymTuples(Long wordId) {

		Word w = WORD.as("w");
		Word w2 = WORD.as("w2");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Meaning m = MEANING.as("m");
		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");
		WordEtymologySourceLink wesl = WORD_ETYMOLOGY_SOURCE_LINK.as("wesl");

		CommonTableExpression<Record5<Long, Long, Long, Long, Long[]>> werec = DSL
				.name("werec")
				.fields(
						"word_id",
						"word_etym_word_id",
						"word_etym_id",
						"related_word_id",
						"related_word_ids")
				.as(DSL
						.select(
								we.WORD_ID,
								we.WORD_ID.as("word_etym_word_id"),
								we.ID.as("word_etym_id"),
								wer.RELATED_WORD_ID,
								DSL.array(we.WORD_ID).as("related_word_ids"))
						.from(we.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID).and(wer.RELATED_WORD_ID.ne(we.WORD_ID))))
						.where(we.WORD_ID.eq(wordId))
						.orderBy(we.ORDER_BY, wer.ORDER_BY)
						.unionAll(DSL
								.select(
										DSL.field("werec.word_id", Long.class),
										we.WORD_ID.as("word_etym_word_id"),
										we.ID.as("word_etym_id"),
										wer.RELATED_WORD_ID,
										PostgresDSL.arrayAppend(DSL.field("werec.related_word_ids", Long[].class), we.WORD_ID).as("related_word_ids"))
								.from(
										DSL.table("werec")
												.innerJoin(we).on(we.WORD_ID.eq(DSL.field("werec.related_word_id", Long.class)))
												.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID).and(wer.RELATED_WORD_ID.ne(we.WORD_ID))))
								.where(DSL.field("werec.related_word_id", Long.class).ne(DSL.all(DSL.field("werec.related_word_ids", Long[].class))))
								.orderBy(we.ORDER_BY, wer.ORDER_BY)));

		Field<JSONB> rsf = DSL
				.select(
						DSL.jsonbArrayAgg(DSL
								.jsonObject(
										DSL.key("wordEtymRelId").value(wer.ID),
										DSL.key("commentPrese").value(wer.COMMENT_PRESE),
										DSL.key("questionable").value(wer.IS_QUESTIONABLE),
										DSL.key("compound").value(wer.IS_COMPOUND),
										DSL.key("relatedWordId").value(wer.RELATED_WORD_ID))))
				.from(wer)
				.where(
						wer.WORD_ETYM_ID.eq(werec.field("word_etym_id", Long.class))
								.and(wer.RELATED_WORD_ID.ne(werec.field("word_id", Long.class))))
				.groupBy(wer.WORD_ETYM_ID)
				.asField();

		Field<JSONB> slf = DSL
				.select(
						DSL.jsonbArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wesl.ID),
										DSL.key("wordEtymId").value(wesl.WORD_ETYM_ID),
										DSL.key("sourceId").value(wesl.SOURCE_ID),
										DSL.key("value").value(wesl.VALUE))))
				.from(wesl)
				.where(wesl.WORD_ETYM_ID.eq(werec.field("word_etym_id", Long.class)))
				.groupBy(wesl.WORD_ETYM_ID)
				.asField();

		Field<JSONB> mwf = DSL
				.select(
						DSL.jsonbArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(w2.ID),
										DSL.key("wordValue").value(w2.VALUE),
										DSL.key("wordLang").value(w2.LANG))))
				.from(l1, l2, w2, m)
				.where(
						l1.WORD_ID.eq(werec.field("word_etym_word_id", Long.class))
								.and(l1.MEANING_ID.eq(m.ID))
								.and(l1.IS_PUBLIC.isTrue())
								.and(l2.MEANING_ID.eq(m.ID))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.IS_PUBLIC.isTrue())
								.and(l2.DATASET_CODE.eq(DATASET_ETY))
								.and(w2.ID.ne(l1.WORD_ID))
								.and(w2.LANG.eq(LANGUAGE_CODE_EST)))
				.groupBy(l1.WORD_ID)
				.asField();

		List<WordEtymNodeTuple> tuples = mainDb
				.withRecursive(werec)
				.select(
						werec.field("word_id", Long.class),
						werec.field("word_etym_id", Long.class),
						werec.field("word_etym_word_id", Long.class),
						w.VALUE.as("word_etym_word_value"),
						w.LANG.as("word_etym_word_lang"),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT_PRESE,
						we.IS_QUESTIONABLE,
						rsf.as("relations"),
						slf.as("source_links"),
						mwf.as("meaning_words"))
				.from(
						werec
								.innerJoin(w).on(w.ID.eq(werec.field("word_etym_word_id", Long.class)))
								.innerJoin(we).on(we.ID.eq(werec.field("word_etym_id", Long.class))))
				.groupBy(
						werec.field("word_id"),
						werec.field("word_etym_id"),
						werec.field("word_etym_word_id"),
						w.ID,
						we.ID)
				.orderBy(
						werec.field("word_id"),
						we.ORDER_BY)
				.fetchInto(WordEtymNodeTuple.class);

		return tuples;
	}

}
