package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_RELATION;

import java.util.List;

import org.jooq.CommonTableExpression;
import org.jooq.JSONB;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymPOCTuple;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordEtymologyRelation;

@Component
public class WordEtymDbService extends AbstractDataDbService {

	private static final String DATASET_TYPE_LEX = "LEX";

	public List<WordEtymPOCTuple> getWordEtymTuples(Long wordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");
		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		CommonTableExpression<Record5<Long, Long, Long, Long, Long[]>> wordEtymRecursion = DSL.
				name("word_etym_recursion").fields(
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
								.from(we.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID)))
								.where(we.WORD_ID.eq(wordId))
								.orderBy(we.ORDER_BY, wer.ORDER_BY)
						.unionAll(DSL
								.select(
										DSL.field("rec.word_id", Long.class),
										we.WORD_ID.as("word_etym_word_id"),
										we.ID.as("word_etym_id"),
										wer.RELATED_WORD_ID,
										PostgresDSL.arrayAppend(DSL.field("rec.related_word_ids", Long[].class), we.WORD_ID).as("related_word_ids"))
								.from(
										DSL.table("word_etym_recursion").as("rec")
												.innerJoin(we).on(we.WORD_ID.eq(DSL.field("rec.related_word_id", Long.class)))
												.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID)))
								.where(DSL.field("rec.related_word_id", Long.class).ne(DSL.all(DSL.field("rec.related_word_ids", Long[].class)))) // TODO DSL.all works to avoid endless loop?
								.orderBy(we.ORDER_BY, wer.ORDER_BY)));

		Table<Record3<Long, String, String>> wt = DSL
				.select(
						w.ID,
						w.LANG.as("word_etym_word_lang"),
						w.VALUE.as("word_etym_word"))
				.from(w)
				.whereExists(DSL
						.select(l.ID)
						.from(l, ds)
						.where(
								l.WORD_ID.eq(w.ID)
										.and(l.IS_PUBLIC.eq(PUBLICITY_PUBLIC))
										.and(ds.CODE.eq(l.DATASET_CODE))
										.and(ds.TYPE.eq(DATASET_TYPE_LEX))))
				.groupBy(w.ID)
				.asTable("wt");

		Table<Record3<Long, Long, JSONB>> wert = DSL
				.select(
						wer.WORD_ETYM_ID,
						wer.RELATED_WORD_ID,
						DSL.field(DSL
								.jsonbArrayAgg(DSL
										.jsonObject(
												DSL.key("wordEtymRelId").value(wer.ID),
												DSL.key("comment").value(wer.COMMENT_PRESE),
												DSL.key("questionable").value(wer.IS_QUESTIONABLE),
												DSL.key("compound").value(wer.IS_COMPOUND),
												DSL.key("relatedWordId").value(wer.RELATED_WORD_ID))))
								.as("word_etym_relations"))
				.from(wer)
				.groupBy(wer.WORD_ETYM_ID, wer.RELATED_WORD_ID)
				.asTable("wert");

		List<WordEtymPOCTuple> wordEtymTuples = create
				.withRecursive(wordEtymRecursion)
				.select(
						wordEtymRecursion.field("word_id", Long.class),
						wordEtymRecursion.field("word_etym_id", Long.class),
						wordEtymRecursion.field("word_etym_word_id", Long.class),
						wt.field("word_etym_word", String.class),
						wt.field("word_etym_word_lang", String.class),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT_PRESE.as("word_etym_comment"),
						we.IS_QUESTIONABLE.as("word_etym_is_questionable"),
						we.ORDER_BY.as("word_etym_order_by"),
						wert.field("word_etym_relations", JSONB.class))
				.from(
						wordEtymRecursion
								.innerJoin(we).on(we.ID.eq(wordEtymRecursion.field("word_etym_id", Long.class)))
								.innerJoin(wt).on(wt.field("id", Long.class).eq(wordEtymRecursion.field("word_etym_word_id", Long.class)))
								.leftOuterJoin(wert).on(
										wert.field("word_etym_id", Long.class).eq(wordEtymRecursion.field("word_etym_id", Long.class))
												.and(wert.field("related_word_id", Long.class).ne(wordEtymRecursion.field("word_id", Long.class)))))
				.groupBy(
						wordEtymRecursion.field("word_id"),
						wordEtymRecursion.field("word_etym_id"),
						wordEtymRecursion.field("word_etym_word_id"),
						we.ID,
						wt.field("ID"),
						wt.field("word_etym_word"),
						wt.field("word_etym_word_lang"),
						wert.field("word_etym_relations", JSONB.class))
				.orderBy(
						wordEtymRecursion.field("word_id"),
						we.ORDER_BY)
				.fetchInto(WordEtymPOCTuple.class);

		return wordEtymTuples;
	}

}
