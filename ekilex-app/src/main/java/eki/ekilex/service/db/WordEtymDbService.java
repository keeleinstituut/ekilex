package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY_RELATION;

import java.util.List;

import org.jooq.CommonTableExpression;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymNodeTuple;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordEtymologyRelation;

@Component
public class WordEtymDbService extends AbstractDataDbService {

	public List<WordEtymNodeTuple> getWordEtymTuples(Long wordId) {

		Word w = WORD.as("w");
		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");

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
						.from(we.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID)))
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
												.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID)))
								.where(DSL.field("werec.related_word_id", Long.class).ne(DSL.all(DSL.field("werec.related_word_ids", Long[].class))))
								.orderBy(we.ORDER_BY, wer.ORDER_BY)));

		Field<JSONB> wert = DSL
				.select(
						DSL.jsonbArrayAgg(DSL
								.jsonObject(
										DSL.key("wordEtymRelId").value(wer.ID),
										DSL.key("comment").value(wer.COMMENT_PRESE),
										DSL.key("questionable").value(wer.IS_QUESTIONABLE),
										DSL.key("compound").value(wer.IS_COMPOUND),
										DSL.key("relatedWordId").value(wer.RELATED_WORD_ID))))
				.from(wer)
				.where(
						wer.WORD_ETYM_ID.eq(werec.field("word_etym_id", Long.class))
								.and(wer.RELATED_WORD_ID.ne(werec.field("word_id", Long.class))))
				.groupBy(wer.WORD_ETYM_ID)
				.asField();

		List<WordEtymNodeTuple> wordEtymTuples = create
				.withRecursive(werec)
				.select(
						werec.field("word_id", Long.class),
						werec.field("word_etym_id", Long.class),
						werec.field("word_etym_word_id", Long.class),
						w.VALUE.as("word_etym_word"),
						w.LANG.as("word_etym_word_lang"),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT,
						we.COMMENT_PRESE,
						we.IS_QUESTIONABLE,
						wert.as("word_etym_relations"))
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

		return wordEtymTuples;
	}

}
