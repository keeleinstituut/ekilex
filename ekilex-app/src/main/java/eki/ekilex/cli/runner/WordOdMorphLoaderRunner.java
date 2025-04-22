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
import eki.common.data.Count;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.util.ValueUtil;

@Component
public class WordOdMorphLoaderRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(WordOdMorphLoaderRunner.class);

	private static final String USER_NAME_LOADER = "Laadur";

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private ValueUtil valueUtil;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String morphTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		LocalDateTime now = LocalDateTime.now();
		logger.info("time: " + now);
		logger.info("hr: " + now.getHour());

		List<String> morphTsvLines = readFileLines(morphTsvFilePath);

		int createCount = 0;
		int existCount = 0;
		int missingWordCount = 0;

		int lineCounter = 0;
		int lineCount = morphTsvLines.size();
		int progressIndicator = lineCount / Math.min(lineCount, 100);

		for (String morphTsvLine : morphTsvLines) {

			if (StringUtils.isBlank(morphTsvLine)) {
				continue;
			}

			String[] morphTsvCells = StringUtils.splitPreserveAllTokens(morphTsvLine, CSV_SEPARATOR);
			Long wordId = Long.valueOf(StringUtils.trim(morphTsvCells[0]));
			String morphValuePrese = StringUtils.trim(morphTsvCells[1]);
			morphValuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(morphValuePrese);
			String morphValue = textDecorationService.removeEkiElementMarkup(morphValuePrese);

			boolean wordExists = migrationDbService.wordExists(wordId);

			if (wordExists) {

				// TODO do stuff

			} else {
				logger.warn("Missing word id: {}", wordId);
				missingWordCount++;
			}

			lineCounter++;
			if (lineCounter % progressIndicator == 0) {
				int progressPercent = lineCounter / progressIndicator;
				logger.info("{}% - {} lines iterated", progressPercent, lineCounter);
			}
		}

		logger.info("Completed load. Out of {} lines, morph create count: {}, morph exist count: {}, missing word count: {}",
				morphTsvLines.size(), createCount, existCount, missingWordCount);
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
