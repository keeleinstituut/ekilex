package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_ETYM;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO separate service until new etym generation is introduced
@Component
public class EtymDbService {

	@Autowired
	protected DSLContext mainDb;

	public Long createWordEtym(Long wordId, eki.ekilex.data.etym2.WordEtym wordEtym) {

		Long wordEtymId = mainDb
				.insertInto(
						WORD_ETYM,
						WORD_ETYM.WORD_ID,
						WORD_ETYM.ETYMOLOGY_TYPE_CODE,
						WORD_ETYM.ETYMOLOGY_YEAR,
						WORD_ETYM.IS_QUESTIONABLE)
				.values(
						wordId,
						wordEtym.getEtymologyTypeCode(),
						wordEtym.getEtymologyYear(),
						wordEtym.isQuestionable())
				.returning(WORD_ETYM.ID)
				.fetchOne()
				.getId();

		return wordEtymId;
	}
}
