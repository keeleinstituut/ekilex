package eki.ekilex.cli.runner;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Word;
import eki.ekilex.service.AbstractLoaderCommons;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.WordStressDbService;

@Component
public class WordStressCorrectorRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(WordStressCorrectorRunner.class);

	private static final String EKI_STRESS_OPEN_TAG = "<eki-stress>";

	private static final String EKI_STRESS_CLOSE_TAG = "</eki-stress>";

	private static final char APOSTROPHE = '\'';

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private WordStressDbService wordStressDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute() throws Exception {

		correctWordValuePreseStress();
		correctWordMorphophonoFormStress();
	}

	private void correctWordValuePreseStress() {

		List<Word> missingValuePreseStressWords = wordStressDbService.getWordsWithMissingValuePreseStress();
		logger.info("Found {} words with missing value prese stress markup", CollectionUtils.size(missingValuePreseStressWords));

		for (Word missingValuePreseStressWord : missingValuePreseStressWords) {
			Long wordId = missingValuePreseStressWord.getWordId();
			String morphophonoFormWithStress = missingValuePreseStressWord.getMorphophonoForm();

			StringBuilder valuePreseBuilder = new StringBuilder();
			int valuePreseBuilderCharIndex = 0;

			for (int mFormCharIndex = 0; mFormCharIndex < morphophonoFormWithStress.length(); mFormCharIndex++) {
				char mFormChar = morphophonoFormWithStress.charAt(mFormCharIndex);
				boolean isStressChar = mFormChar == APOSTROPHE;

				if (isStressChar) {
					valuePreseBuilder.insert(valuePreseBuilderCharIndex, EKI_STRESS_CLOSE_TAG);
					valuePreseBuilder.insert(valuePreseBuilderCharIndex - 1, EKI_STRESS_OPEN_TAG);
					valuePreseBuilderCharIndex = valuePreseBuilderCharIndex + EKI_STRESS_OPEN_TAG.length() + EKI_STRESS_CLOSE_TAG.length();
				} else {
					valuePreseBuilder.insert(valuePreseBuilderCharIndex, mFormChar);
					valuePreseBuilderCharIndex++;
				}
			}

			String valuePreseCorrectedStress = valuePreseBuilder.toString();
			cudDbService.updateWordValuePrese(wordId, valuePreseCorrectedStress);
		}
		logger.info("Words value prese stress markup fixed");
	}

	private void correctWordMorphophonoFormStress() {

		List<Word> missingMorphophonoFormStressWords = wordStressDbService.getWordsWithMissingMorphophonoFormStress();
		logger.info("Found {} words with missing morphophono form stress", CollectionUtils.size(missingMorphophonoFormStressWords));

		for (Word missingMorphophonoFormStressWord : missingMorphophonoFormStressWords) {
			Long wordId = missingMorphophonoFormStressWord.getWordId();
			String valuePreseWithStress = missingMorphophonoFormStressWord.getWordValuePrese();

			String morphophonoFormCorrectedStress = StringUtils.replace(valuePreseWithStress, EKI_STRESS_OPEN_TAG, "");
			morphophonoFormCorrectedStress = StringUtils.replace(morphophonoFormCorrectedStress, EKI_STRESS_CLOSE_TAG, Character.toString(APOSTROPHE));

			cudDbService.updateWordMorphophonoForm(wordId, morphophonoFormCorrectedStress);
		}
		logger.info("Words morphophono form stress fixed");
	}

}
