package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_OS_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OS_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OS_USAGE;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.main.tables.WordOsMorph;
import eki.ekilex.data.db.main.tables.WordOsRecommendation;
import eki.ekilex.data.db.main.tables.WordOsUsage;

@Component
public class OsDataDbService {

	@Autowired
	protected DSLContext mainDb;

	public eki.ekilex.data.WordOsRecommendation getWordOsRecommendation(Long wordId) {

		WordOsRecommendation wor = WORD_OS_RECOMMENDATION.as("wor");

		return mainDb
				.selectFrom(wor)
				.where(wor.WORD_ID.eq(wordId))
				.orderBy(wor.ID)
				.limit(1)
				.fetchOptionalInto(eki.ekilex.data.WordOsRecommendation.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.WordOsUsage> getWordOsUsages(Long wordId) {

		WordOsUsage wou = WORD_OS_USAGE.as("wou");

		return mainDb
				.selectFrom(wou)
				.where(wou.WORD_ID.eq(wordId))
				.orderBy(wou.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordOsUsage.class);
	}

	public eki.ekilex.data.WordOsMorph getWordOsMorph(Long wordId) {

		WordOsMorph wom = WORD_OS_MORPH.as("wom");

		return mainDb
				.selectFrom(wom)
				.where(wom.WORD_ID.eq(wordId))
				.limit(1)
				.fetchOptionalInto(eki.ekilex.data.WordOsMorph.class)
				.orElse(null);
	}
}
