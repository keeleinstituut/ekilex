package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_DATA;
import static eki.wordweb.data.db.Tables.LEXICAL_DECISION_RESULT;
import static eki.wordweb.data.db.Tables.SIMILARITY_JUDGEMENT_DATA;
import static eki.wordweb.data.db.Tables.SIMILARITY_JUDGEMENT_RESULT;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.SQL;
import org.jooq.Table;
import org.jooq.WindowFinalStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.GameResult;
import eki.wordweb.data.LexicalDecisionGameRow;
import eki.wordweb.data.SimilarityJudgementGameRow;
import eki.wordweb.data.db.tables.SimilarityJudgementData;
import eki.wordweb.data.db.tables.SimilarityJudgementResult;

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
					LEXICAL_DECISION_RESULT.SESSION_ID,
					LEXICAL_DECISION_RESULT.ANSWER,
					LEXICAL_DECISION_RESULT.DELAY
					)
			.values(
					lexicalDecisionGameRow.getDataId(),
					lexicalDecisionGameRow.getRemoteAddr(),
					lexicalDecisionGameRow.getSessionId(),
					lexicalDecisionGameRow.isAnswer(),
					lexicalDecisionGameRow.getDelay()
					)
			.execute();
	}

	public List<GameResult> getLexicDecisGameResults() {

		AggregateFunction<Integer> resCountField = DSL.count(LEXICAL_DECISION_RESULT.ID);
		Table<Record2<String, Integer>> r_cnt = DSL
				.select(
						LEXICAL_DECISION_RESULT.SESSION_ID,
						resCountField.as("res_count"))
				.from(LEXICAL_DECISION_RESULT)
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
				.from(LEXICAL_DECISION_RESULT)
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
				.into(GameResult.class);
	}

	public List<SimilarityJudgementGameRow> getSimilJudgeGameBatch(String dataset, boolean isSameLang, int gameBatchSize) {

		final int subqueryOptLimit = 300;
		WindowFinalStep<Integer> rownum = DSL.rowNumber().over(DSL.orderBy(DSL.rand()));
		Table<Record5<Integer, Long, Long, String, String>> w = DSL
			.select(
				rownum.as("row_num"),
				SIMILARITY_JUDGEMENT_DATA.ID.as("data_id"),
				SIMILARITY_JUDGEMENT_DATA.MEANING_ID,
				SIMILARITY_JUDGEMENT_DATA.WORD,
				SIMILARITY_JUDGEMENT_DATA.LANG
				)
			.from(SIMILARITY_JUDGEMENT_DATA)
			.where(SIMILARITY_JUDGEMENT_DATA.DATASET_CODE.eq(dataset))
			.orderBy(DSL.rand())
			.limit(subqueryOptLimit)
			.asTable();
		Table<Record5<Integer, Long, Long, String, String>> ww1 = w.as("w1");
		Table<Record5<Integer, Long, Long, String, String>> ww2 = w.as("w2");
		Condition rndWordLangEqualityCond;
		if (isSameLang) {
			rndWordLangEqualityCond = ww1.field("lang", String.class).eq(ww2.field("lang", String.class));
		} else {
			rndWordLangEqualityCond = ww1.field("lang", String.class).ne(ww2.field("lang", String.class));
		}
		Table<Record7<Integer, Long, Long, String, Long, Long, String>> rnd = DSL
			.select(
					rownum.as("row_num"),
					ww1.field("data_id", Long.class).as("data_id1"),
					ww1.field("meaning_id", Long.class).as("meaning_id1"),
					ww1.field("word", String.class).as("word1"),
					ww2.field("data_id", Long.class).as("data_id2"),
					ww2.field("meaning_id", Long.class).as("meaning_id2"),
					ww2.field("word", String.class).as("word2")
					)
			.from(ww1, ww2)
			.where(
					ww1.field("row_num", Integer.class).eq(ww2.field("row_num", Integer.class))
					.and(ww1.field("meaning_id", Long.class).ne(ww2.field("meaning_id", Long.class)))
					.and(ww1.field("word", String.class).ne(ww2.field("word", String.class)))
					.and(rndWordLangEqualityCond)
					)
			.asTable("rnd");
		SimilarityJudgementData w1 = SIMILARITY_JUDGEMENT_DATA.as("w1");
		SimilarityJudgementData w2 = SIMILARITY_JUDGEMENT_DATA.as("w2");
		Condition synWordLangEqualityCond;
		if (isSameLang) {
			synWordLangEqualityCond = w1.LANG.eq(w2.LANG);
		} else {
			synWordLangEqualityCond = w1.LANG.ne(w2.LANG);
		}
		Table<Record6<Integer, Long, Long, String, Long, String>> syn = DSL
			.select(
					rownum.as("row_num"),
					w1.MEANING_ID,
					w1.ID.as("data_id1"),
					w1.WORD.as("word1"),
					w2.ID.as("data_id2"),
					w2.WORD.as("word2")
					)
			.from(w1, w2)
			.where(
					w1.MEANING_ID.eq(w2.MEANING_ID)
					.and(w1.DATASET_CODE.eq(dataset))
					.and(w2.DATASET_CODE.eq(dataset))
					.and(w1.WORD.ne(w2.WORD))
					.and(synWordLangEqualityCond)
					)
			.orderBy(DSL.rand())
			.asTable("syn");
		return create
					.select(
							syn.field("data_id1", Long.class).as("syn_data_id1"),
							syn.field("word1", String.class).as("syn_word1"),
							syn.field("data_id2", Long.class).as("syn_data_id2"),
							syn.field("word2", String.class).as("syn_word2"),
							rnd.field("data_id1", Long.class).as("rnd_data_id1"),
							rnd.field("word1", String.class).as("rnd_word1"),
							rnd.field("data_id2", Long.class).as("rnd_data_id2"),
							rnd.field("word2", String.class).as("rnd_word2")
							)
					.from(syn, rnd)
					.where(
							syn.field("row_num", Integer.class).eq(rnd.field("row_num", Integer.class))
							.and(syn.field("meaning_id", Long.class).ne(rnd.field("meaning_id1", Long.class)))
							.and(syn.field("meaning_id", Long.class).ne(rnd.field("meaning_id2", Long.class)))
							)
					.limit(gameBatchSize)
					.fetch()
					.into(SimilarityJudgementGameRow.class);
	}

	public void submitSimilJudgeGameRow(SimilarityJudgementGameRow similarityJudgementGameRow) {
		create
			.insertInto(SIMILARITY_JUDGEMENT_RESULT)
			.columns(
					SIMILARITY_JUDGEMENT_RESULT.GAME_KEY,
					SIMILARITY_JUDGEMENT_RESULT.PAIR11_DATA_ID,
					SIMILARITY_JUDGEMENT_RESULT.PAIR12_DATA_ID,
					SIMILARITY_JUDGEMENT_RESULT.PAIR21_DATA_ID,
					SIMILARITY_JUDGEMENT_RESULT.PAIR22_DATA_ID,
					SIMILARITY_JUDGEMENT_RESULT.REMOTE_ADDR,
					SIMILARITY_JUDGEMENT_RESULT.SESSION_ID,
					SIMILARITY_JUDGEMENT_RESULT.ANSWER_PAIR1,
					SIMILARITY_JUDGEMENT_RESULT.ANSWER_PAIR2,
					SIMILARITY_JUDGEMENT_RESULT.DELAY
					)
			.values(
					similarityJudgementGameRow.getGameKey(),
					similarityJudgementGameRow.getWordPair1().getDataId1(),
					similarityJudgementGameRow.getWordPair1().getDataId2(),
					similarityJudgementGameRow.getWordPair2().getDataId1(),
					similarityJudgementGameRow.getWordPair2().getDataId2(),
					similarityJudgementGameRow.getRemoteAddr(),
					similarityJudgementGameRow.getSessionId(),
					similarityJudgementGameRow.isAnswerPair1(),
					similarityJudgementGameRow.isAnswerPair2(),
					similarityJudgementGameRow.getDelay()
					)
			.execute();
	}

	public List<GameResult> getSimilJudgeGameResults(String gameKey) {

		SimilarityJudgementResult r = SIMILARITY_JUDGEMENT_RESULT.as("r");
		SimilarityJudgementData dp11 = SIMILARITY_JUDGEMENT_DATA.as("dp11");
		SimilarityJudgementData dp12 = SIMILARITY_JUDGEMENT_DATA.as("dp12");
		SimilarityJudgementData dp21 = SIMILARITY_JUDGEMENT_DATA.as("dp21");
		SimilarityJudgementData dp22 = SIMILARITY_JUDGEMENT_DATA.as("dp22");
		Table<Record2<String, Integer>> r_cnt = DSL
				.select(
						SIMILARITY_JUDGEMENT_RESULT.SESSION_ID,
						DSL.count(SIMILARITY_JUDGEMENT_RESULT.ID).as("res_count"))
				.from(SIMILARITY_JUDGEMENT_RESULT)
				.where(SIMILARITY_JUDGEMENT_RESULT.GAME_KEY.eq(gameKey))
				.groupBy(SIMILARITY_JUDGEMENT_RESULT.SESSION_ID)
				.asTable("r_cnt");
		Table<Record2<String, Integer>> r_cor = DSL
				.select(
						r.SESSION_ID,
						DSL.count(r.ID).as("res_count"))
				.from(r, dp11, dp12, dp21, dp22)
				.where(
						r.GAME_KEY.eq(gameKey)
						.and(r.PAIR11_DATA_ID.eq(dp11.ID))
						.and(r.PAIR12_DATA_ID.eq(dp12.ID))
						.and(r.PAIR21_DATA_ID.eq(dp21.ID))
						.and(r.PAIR22_DATA_ID.eq(dp22.ID))
						.and(
								(r.ANSWER_PAIR1.isTrue().and(dp11.MEANING_ID.eq(dp12.MEANING_ID)))
								.or((r.ANSWER_PAIR2.isTrue().and(dp21.MEANING_ID.eq(dp22.MEANING_ID))))
								)
						)
				.groupBy(r.SESSION_ID)
				.asTable("r_cor");
		Table<Record2<String, Integer>> r_inc = DSL
				.select(
						r.SESSION_ID,
						DSL.count(r.ID).as("res_count"))
				.from(r, dp11, dp12, dp21, dp22)
				.where(
						r.GAME_KEY.eq(gameKey)
						.and(r.PAIR11_DATA_ID.eq(dp11.ID))
						.and(r.PAIR12_DATA_ID.eq(dp12.ID))
						.and(r.PAIR21_DATA_ID.eq(dp21.ID))
						.and(r.PAIR22_DATA_ID.eq(dp22.ID))
						.and(
								(r.ANSWER_PAIR1.isTrue().and(dp11.MEANING_ID.ne(dp12.MEANING_ID)))
								.or((r.ANSWER_PAIR2.isTrue().and(dp21.MEANING_ID.ne(dp22.MEANING_ID))))
								)
						)
				.groupBy(r.SESSION_ID)
				.asTable("r_inc");
		Table<Record2<String,BigDecimal>> r_avg = DSL
				.select(
						SIMILARITY_JUDGEMENT_RESULT.SESSION_ID,
						DSL.avg(SIMILARITY_JUDGEMENT_RESULT.DELAY).as("avg_delay"))
				.from(SIMILARITY_JUDGEMENT_RESULT)
				.where(SIMILARITY_JUDGEMENT_RESULT.GAME_KEY.eq(gameKey))
				.groupBy(SIMILARITY_JUDGEMENT_RESULT.SESSION_ID)
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
				.into(GameResult.class);
	}
}
