package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_OS_MORPH;
import static eki.ekilex.data.db.main.Tables.WORD_OS_USAGE;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PublishingConstant;
import eki.ekilex.data.db.main.tables.WordOsMorph;
import eki.ekilex.data.db.main.tables.WordOsUsage;

@Component
public class OsDataDbService implements PublishingConstant {

	@Autowired
	private DSLContext mainDb;

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
