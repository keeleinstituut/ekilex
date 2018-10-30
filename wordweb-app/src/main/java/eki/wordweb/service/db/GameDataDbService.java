package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_DATA;
import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_RESULT;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQL;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.LexicalDecisionGameResult;
import eki.wordweb.data.LexicalDecisionGameRow;

@Component
public class GameDataDbService {

	@Autowired
	private DSLContext create;

	public List<LexicalDecisionGameRow> getLexicDecisGameBatch(String lang, int gameBatchSize) {
		return create
				.select(
						LEXICAL_DECISION_DATA.ID.as("dataId"),
						LEXICAL_DECISION_DATA.WORD.as("suggestedWordValue"),
						LEXICAL_DECISION_DATA.IS_WORD.as("word")
						)
				.from(LEXICAL_DECISION_DATA)
				.where(LEXICAL_DECISION_DATA.LANG.eq(lang))
				.orderBy(DSL.rand())
				.limit(gameBatchSize)
				.fetch()
				.into(LexicalDecisionGameRow.class);
	}

	public void submitLexicDecisGameRow(LexicalDecisionGameRow lexicalDecisionGameRow) {
		create
			.insertInto(LEXICAL_DECISION_RESULT)
			.columns(
					LEXICAL_DECISION_RESULT.DATA_ID,
					LEXICAL_DECISION_RESULT.REMOTE_ADDR,
					LEXICAL_DECISION_RESULT.LOCAL_ADDR,
					LEXICAL_DECISION_RESULT.SESSION_ID,
					LEXICAL_DECISION_RESULT.ANSWER,
					LEXICAL_DECISION_RESULT.DELAY
					)
			.values(
					lexicalDecisionGameRow.getDataId(),
					lexicalDecisionGameRow.getRemoteAddr(),
					lexicalDecisionGameRow.getLocalAddr(),
					lexicalDecisionGameRow.getSessionId(),
					lexicalDecisionGameRow.isAnswer(),
					lexicalDecisionGameRow.getDelay()
					)
			.execute();
	}

	public List<LexicalDecisionGameResult> getLexicDecisGameResults() {

		AggregateFunction<Integer> resCountField = DSL.count(LEXICAL_DECISION_RESULT.ID);
		Table<Record2<String, Integer>> r_cnt = DSL
				.select(
						LEXICAL_DECISION_RESULT.SESSION_ID,
						resCountField.as("res_count"))
				.from(LEXICAL_DECISION_DATA, LEXICAL_DECISION_RESULT)
				.where(LEXICAL_DECISION_RESULT.DATA_ID.eq(LEXICAL_DECISION_DATA.ID))
				.groupBy(LEXICAL_DECISION_RESULT.SESSION_ID)
				.asTable("r_cnt");
		Table<Record2<String, Integer>> r_cor = DSL
				.select(
						LEXICAL_DECISION_RESULT.SESSION_ID,
						resCountField.as("res_count"))
				.from(LEXICAL_DECISION_DATA, LEXICAL_DECISION_RESULT)
				.where(
						LEXICAL_DECISION_RESULT.DATA_ID.eq(LEXICAL_DECISION_DATA.ID)
						.and(LEXICAL_DECISION_DATA.IS_WORD.eq(LEXICAL_DECISION_RESULT.ANSWER))
						)
				.groupBy(LEXICAL_DECISION_RESULT.SESSION_ID)
				.asTable("r_cor");
		Table<Record2<String, Integer>> r_inc = DSL
				.select(
						LEXICAL_DECISION_RESULT.SESSION_ID,
						resCountField.as("res_count"))
				.from(LEXICAL_DECISION_DATA, LEXICAL_DECISION_RESULT)
				.where(
						LEXICAL_DECISION_RESULT.DATA_ID.eq(LEXICAL_DECISION_DATA.ID)
						.and(LEXICAL_DECISION_DATA.IS_WORD.ne(LEXICAL_DECISION_RESULT.ANSWER))
						)
				.groupBy(LEXICAL_DECISION_RESULT.SESSION_ID)
				.asTable("r_inc");
		Table<Record2<String,BigDecimal>> r_avg = DSL
				.select(
						LEXICAL_DECISION_RESULT.SESSION_ID,
						DSL.avg(LEXICAL_DECISION_RESULT.DELAY).as("avg_delay"))
				.from(LEXICAL_DECISION_DATA, LEXICAL_DECISION_RESULT)
				.where(LEXICAL_DECISION_RESULT.DATA_ID.eq(LEXICAL_DECISION_DATA.ID))
				.groupBy(LEXICAL_DECISION_RESULT.SESSION_ID)
				.asTable("r_avg");
		SQL corrAnsPercentSql = DSL.sql("((coalesce(r_cor.res_count, 0) * 100) / coalesce(r_cnt.res_count, 0))");
		return create
				.select(
					r_cnt.field("session_id", String.class),
					DSL.coalesce(r_cor.field("res_count", Integer.class), 0).as("correct_answers_count"),
					DSL.coalesce(r_inc.field("res_count", Integer.class), 0).as("incorrect_answers_count"),
					r_cnt.field("res_count", Integer.class).as("all_answers_count"),
					DSL.field(corrAnsPercentSql, Integer.class).as("correct_answers_percent"),
					r_avg.field("avg_delay", Long.class).as("average_answer_delay")
					)
				.from(
						r_cnt
						.leftOuterJoin(r_cor).on(r_cor.field("session_id", String.class).eq(r_cnt.field("session_id", String.class)))
						.leftOuterJoin(r_inc).on(r_inc.field("session_id", String.class).eq(r_cnt.field("session_id", String.class)))
						.leftOuterJoin(r_avg).on(r_avg.field("session_id", String.class).eq(r_cnt.field("session_id", String.class)))
						)
				.fetch()
				.into(LexicalDecisionGameResult.class);
	}

}
