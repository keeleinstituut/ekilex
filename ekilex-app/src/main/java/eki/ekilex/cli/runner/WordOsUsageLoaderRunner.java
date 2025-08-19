package eki.ekilex.cli.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordOsUsage;

@Component
public class WordOsUsageLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(WordOsUsageLoaderRunner.class);

	@Transactional(rollbackOn = Exception.class)
	public void execute(String usageTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		createSecurityContext();

		List<String> usageTsvLines = readFileLines(usageTsvFilePath);

		int createCount = 0;
		int existCount = 0;
		List<Long> missingWordIds = new ArrayList<>();

		int lineCounter = 0;
		int lineCount = usageTsvLines.size();
		int progressIndicator = lineCount / Math.min(lineCount, 100);

		for (String usageTsvLine : usageTsvLines) {

			if (StringUtils.isBlank(usageTsvLine)) {
				continue;
			}

			String[] usageTsvCells = StringUtils.splitPreserveAllTokens(usageTsvLine, CSV_SEPARATOR);
			Long wordId = Long.valueOf(StringUtils.trim(usageTsvCells[0]));
			String usageValuePrese = StringUtils.trim(usageTsvCells[1]);
			usageValuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(usageValuePrese);

			boolean wordExists = migrationDbService.wordExists(wordId);

			if (wordExists) {

				WordOsUsage wordOsUsage = new WordOsUsage();
				wordOsUsage.setWordId(wordId);
				wordOsUsage.setValuePrese(usageValuePrese);
				wordOsUsage.setPublic(true);

				setValueAndPrese(wordOsUsage);
				setCreateUpdate(wordOsUsage);

				boolean wordOsUsageExists = migrationDbService.wordOsUsageExists(wordId, wordOsUsage.getValue());
				if (wordOsUsageExists) {
					existCount++;
				} else {
					cudDbService.createWordOsUsage(wordId, wordOsUsage);
					createCount++;
				}

			} else {
				logger.warn("Missing word id: {}", wordId);
				missingWordIds.add(wordId);
			}

			lineCounter++;
			if (lineCounter % progressIndicator == 0) {
				int progressPercent = lineCounter / progressIndicator;
				logger.info("{}% - {} lines processed", progressPercent, lineCounter);
			}
		}

		missingWordIds = missingWordIds.stream().distinct().collect(Collectors.toList());

		logger.info("Completed load. Out of {} lines, usage create count: {}, usage exist count: {}, missing word count: {}",
				usageTsvLines.size(), createCount, existCount, missingWordIds.size());
	}
}
