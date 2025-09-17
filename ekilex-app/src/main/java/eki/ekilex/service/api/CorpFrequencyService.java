package eki.ekilex.service.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.data.api.FormFreq;
import eki.ekilex.data.api.FreqCorp;
import eki.ekilex.data.api.FreqCorpId;
import eki.ekilex.data.api.MorphFreq;
import eki.ekilex.data.api.WordFreq;
import eki.ekilex.service.db.CorpFrequencyDbService;

@Component
public class CorpFrequencyService {

	@Autowired
	private CorpFrequencyDbService corpFrequencyDbService;

	@Transactional
	public List<FreqCorpId> getFreqCorps() {
		return corpFrequencyDbService.getFreqCorps();
	}

	@Transactional(rollbackFor = Exception.class)
	public Long createFreqCorp(FreqCorp freqCorp) {
		return corpFrequencyDbService.createCorpFreq(freqCorp);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateFreqCorp(FreqCorpId freqCorp) {
		corpFrequencyDbService.updateFreqCorp(freqCorp);
	}

	@Transactional(rollbackFor = Exception.class)
	public void createFormFreqs(List<FormFreq> formFreqs) {
		for (FormFreq formFreq : formFreqs) {
			corpFrequencyDbService.createFormFreqs(formFreq);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void createMorphFreqs(List<MorphFreq> morphFreqs) {
		for (MorphFreq morphFreq : morphFreqs) {
			corpFrequencyDbService.createMorphFreq(morphFreq);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void createWordFreqs(List<WordFreq> wordFreqs) {
		for (WordFreq wordFreq : wordFreqs) {
			corpFrequencyDbService.createWordFreq(wordFreq);
		}
	}

}
