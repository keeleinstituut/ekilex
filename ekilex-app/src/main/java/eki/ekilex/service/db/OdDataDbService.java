package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_OD_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OD_USAGE;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.main.tables.WordOdRecommendation;
import eki.ekilex.data.db.main.tables.WordOdUsage;

@Component
public class OdDataDbService {

	@Autowired
	protected DSLContext mainDb;

	public eki.ekilex.data.WordOdRecommendation getWordOdRecommendation(Long wordId) {

		WordOdRecommendation wor = WORD_OD_RECOMMENDATION.as("wor");

		return mainDb
				.selectFrom(wor)
				.where(wor.WORD_ID.eq(wordId))
				.orderBy(wor.ID)
				.limit(1)
				.fetchOptionalInto(eki.ekilex.data.WordOdRecommendation.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.WordOdUsage> getWordOdUsages(Long wordId) {

		WordOdUsage wou = WORD_OD_USAGE.as("wou");

		return mainDb
				.selectFrom(wou)
				.where(wou.WORD_ID.eq(wordId))
				.orderBy(wou.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordOdUsage.class);
	}
}
