package eki.ekilex.cli.runner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.WordOdUsage;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.util.ValueUtil;

@Component
public class WordOdUsageLoaderRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(WordOdUsageLoaderRunner.class);

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private ValueUtil valueUtil;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String usageTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		List<String> usageTsvLines = readFileLines(usageTsvFilePath);

		int createCount = 0;
		int existCount = 0;
		int missingWordCount = 0;

		for (String usageTsvLine : usageTsvLines) {

			if (StringUtils.isBlank(usageTsvLine)) {
				continue;
			}

			String[] usageTsvCells = StringUtils.splitPreserveAllTokens(usageTsvLine, CSV_SEPARATOR);
			Long wordId = Long.valueOf(StringUtils.trim(usageTsvCells[0]));
			String usageValuePrese = StringUtils.trim(usageTsvCells[1]);
			usageValuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(usageValuePrese);
			String usageValue = textDecorationService.removeEkiElementMarkup(usageValuePrese);

			boolean wordExists = migrationDbService.wordExists(wordId);

			if (wordExists) {

				boolean wordOdUsageExists = migrationDbService.wordOdUsageExists(wordId, usageValue);
				if (wordOdUsageExists) {
					existCount++;
				} else {

					String userName = "Laadur";
					LocalDateTime now = LocalDateTime.now();

					WordOdUsage wordOdUsage = new WordOdUsage();
					wordOdUsage.setWordId(wordId);
					wordOdUsage.setValue(usageValue);
					wordOdUsage.setValuePrese(usageValuePrese);
					wordOdUsage.setPublic(true);
					wordOdUsage.setCreatedBy(userName);
					wordOdUsage.setCreatedOn(now);
					wordOdUsage.setModifiedBy(userName);
					wordOdUsage.setModifiedOn(now);

					cudDbService.createWordOdUsage(wordId, wordOdUsage);

					createCount++;
				}

			} else {
				logger.warn("Missing word id: {}", wordId);
				missingWordCount++;
			}
		}

		logger.info("Completed load. Out of {} lines, usage create count: {}, usage exist count: {}, missing word count: {}",
				usageTsvLines.size(), createCount, existCount, missingWordCount);
	}

	private List<String> readFileLines(String filePath) throws Exception {
		InputStream fileInputStream = new FileInputStream(filePath);
		try {
			return IOUtils.readLines(fileInputStream, StandardCharsets.UTF_8);
		} finally {
			fileInputStream.close();
		}
	}
}
