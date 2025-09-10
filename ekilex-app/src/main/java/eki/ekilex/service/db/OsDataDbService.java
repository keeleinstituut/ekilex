package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_OS_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OS_RECOMMENDATION;
import static eki.ekilex.data.db.main.Tables.WORD_OS_USAGE;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PublishingConstant;
import eki.ekilex.data.db.main.tables.WordOsMorph;
import eki.ekilex.data.db.main.tables.WordOsRecommendation;
import eki.ekilex.data.db.main.tables.WordOsUsage;
import eki.ekilex.service.db.util.QueryHelper;

@Component
public class OsDataDbService implements PublishingConstant {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private QueryHelper queryHelper;

	public List<eki.ekilex.data.WordOsRecommendation> getWordOsRecommendations(Long wordId) {

		WordOsRecommendation wor = WORD_OS_RECOMMENDATION.as("wor");

		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_OS_RECOMMENDATION, wor.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_WORD_OS_RECOMMENDATION, wor.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_WORD_OS_RECOMMENDATION, wor.ID);

		return mainDb
				.select(wor.fields())
				.select(
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"))
				.from(wor)
				.where(wor.WORD_ID.eq(wordId))
				.orderBy(wor.ID)
				.fetchInto(eki.ekilex.data.WordOsRecommendation.class);
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
