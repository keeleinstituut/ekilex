package eki.ekilex.cli.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.data.WordOsMorph;
import eki.ekilex.service.db.OsDataDbService;

@Component
public class WordOsMorphLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(WordOsMorphLoaderRunner.class);

	@Autowired
	private OsDataDbService osDataDbService;

	@Transactional(rollbackFor = Exception.class)
	public void execute(String morphTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		createSecurityContext();

		List<String> morphTsvLines = readFileLines(morphTsvFilePath);

		int createCount = 0;
		int existCount = 0;
		List<Long> missingWordIds = new ArrayList<>();

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

			boolean wordExists = migrationDbService.wordExists(wordId);

			if (wordExists) {

				WordOsMorph wordOsMorph = osDataDbService.getWordOsMorph(wordId);

				if (wordOsMorph != null) {
					Long wordOsMorphId = wordOsMorph.getId();
					cudDbService.deleteWordOsMorph(wordOsMorphId);
					existCount++;
				}

				wordOsMorph = new WordOsMorph();
				wordOsMorph.setWordId(wordId);
				wordOsMorph.setValuePrese(morphValuePrese);
				wordOsMorph.setPublic(true);

				setValueAndPrese(wordOsMorph);
				setCreateUpdate(wordOsMorph);

				cudDbService.createWordOsMorph(wordId, wordOsMorph);
				createCount++;

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

		logger.info("Completed load. Out of {} lines, morph create count: {}, morph exist count: {}, missing word count: {}",
				morphTsvLines.size(), createCount, existCount, missingWordIds.size());
	}
}
