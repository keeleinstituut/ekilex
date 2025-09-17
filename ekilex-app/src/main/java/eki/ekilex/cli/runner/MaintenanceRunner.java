package eki.ekilex.cli.runner;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.data.AsWordResult;
import eki.common.data.Count;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.service.db.MaintenanceDbService;

@Component
public class MaintenanceRunner {

	private static final Logger logger = LoggerFactory.getLogger(MaintenanceRunner.class);

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private MaintenanceDbService maintenanceDbService;

	@Transactional(rollbackFor = Exception.class)
	public void unifySymbolsAndRecalcAccents() {

		logger.info("Unifying symbols and updating accents...");

		Count createCount = new Count();
		Count updateCount = new Count();
		Count removeCount = new Count();

		List<WordRecord> wordRecords = maintenanceDbService.getWordRecords();
		int wordCount = wordRecords.size();
		boolean changesExist = false;

		for (WordRecord wordRecord : wordRecords) {

			String value = wordRecord.getValue();
			String valueAsWordSrc = wordRecord.getValueAsWord();
			AsWordResult asWordResult = textDecorationService.getAsWordResult(value);
			String valueAsWordTrgt = asWordResult.getValueAsWord();
			boolean isValueAsWordExists = asWordResult.isValueAsWordExists();
			if (StringUtils.isBlank(valueAsWordSrc) && isValueAsWordExists) {
				wordRecord.setValueAsWord(valueAsWordTrgt);
				wordRecord.update();
				createCount.increment();
				changesExist = true;
			} else if (StringUtils.isNotBlank(valueAsWordSrc) && !isValueAsWordExists) {
				wordRecord.setValueAsWord(null);
				wordRecord.update();
				removeCount.increment();
				changesExist = true;
			} else if (StringUtils.isNotBlank(valueAsWordSrc)
					&& isValueAsWordExists
					&& !StringUtils.equals(valueAsWordSrc, valueAsWordTrgt)) {
				wordRecord.setValueAsWord(valueAsWordTrgt);
				wordRecord.update();
				updateCount.increment();
				changesExist = true;
			}
		}

		if (changesExist) {
			logger.info("Out of {} words, unified symbols and accent recalc create count: {}, update count: {}, remove count: {}",
					wordCount, createCount.getValue(), updateCount.getValue(), removeCount.getValue());
		} else {
			logger.info("Already up-to-date");
		}

		logger.info("...unified symbols and accents done");
	}
}
