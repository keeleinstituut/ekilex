package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.FORM_FREQ;
import static eki.ekilex.data.db.main.Tables.FREQ_CORP;
import static eki.ekilex.data.db.main.Tables.MORPH_FREQ;
import static eki.ekilex.data.db.main.Tables.WORD_FREQ;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.FormFreq;
import eki.ekilex.data.api.FreqCorp;
import eki.ekilex.data.api.FreqCorpId;
import eki.ekilex.data.api.MorphFreq;
import eki.ekilex.data.api.WordFreq;

@Component
public class CorpFrequencyDbService {

	@Autowired
	private DSLContext mainDb;

	public List<FreqCorpId> getFreqCorps() {
		return mainDb.selectFrom(FREQ_CORP).orderBy(FREQ_CORP.CORP_DATE).fetchInto(FreqCorpId.class);
	}

	public Long createCorpFreq(FreqCorp freqCorp) {
		return mainDb
				.insertInto(FREQ_CORP, FREQ_CORP.NAME, FREQ_CORP.CORP_DATE, FREQ_CORP.IS_PUBLIC)
				.values(freqCorp.getName(), freqCorp.getCorpDate(), freqCorp.isPublic())
				.returning(FREQ_CORP.ID)
				.fetchOne()
				.getId();
	}

	public void updateFreqCorp(FreqCorpId freqCorp) {
		mainDb
				.update(FREQ_CORP)
				.set(FREQ_CORP.NAME, freqCorp.getName())
				.set(FREQ_CORP.CORP_DATE, freqCorp.getCorpDate())
				.set(FREQ_CORP.IS_PUBLIC, freqCorp.isPublic())
				.where(FREQ_CORP.ID.eq(freqCorp.getId()))
				.execute();
	}

	public void createFormFreqs(FormFreq formFreq) {
		mainDb
				.insertInto(FORM_FREQ, FORM_FREQ.FREQ_CORP_ID, FORM_FREQ.FORM_ID, FORM_FREQ.VALUE, FORM_FREQ.RANK)
				.values(formFreq.getFreqCorpId(), formFreq.getFormId(), BigDecimal.valueOf(formFreq.getValue()), formFreq.getRank())
				.execute();
	}

	public void createMorphFreq(MorphFreq morphFreq) {
		mainDb
				.insertInto(MORPH_FREQ, MORPH_FREQ.FREQ_CORP_ID, MORPH_FREQ.MORPH_CODE, MORPH_FREQ.VALUE, MORPH_FREQ.RANK)
				.values(morphFreq.getFreqCorpId(), morphFreq.getMorphCode(), BigDecimal.valueOf(morphFreq.getValue()), morphFreq.getRank())
				.execute();
	}

	public void createWordFreq(WordFreq wordFreq) {
		mainDb
				.insertInto(WORD_FREQ, WORD_FREQ.FREQ_CORP_ID, WORD_FREQ.WORD_ID, WORD_FREQ.VALUE, WORD_FREQ.RANK)
				.values(wordFreq.getFreqCorpId(), wordFreq.getWordId(), BigDecimal.valueOf(wordFreq.getValue()), wordFreq.getRank())
				.execute();
	}

}
