package eki.ekilex.cli.runner;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class MeaningForumImporterRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(MeaningForumImporterRunner.class);

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String importFilePath) throws Exception {

		File importFile = new File(importFilePath);
		List<String> meaningForumMappingLines = readFileLines(importFile);
		meaningForumMappingLines.remove(0);//remove header

		int lineCount = meaningForumMappingLines.size();
		int succesCounter = 0;
		int errorCounter = 0;
		int lineCounter = 0;

		for (String meaningForumMappingLine : meaningForumMappingLines) {

			lineCounter++;
			if (StringUtils.isBlank(meaningForumMappingLine)) {
				continue;
			}

			String[] meaningForumMappingCells = StringUtils.splitPreserveAllTokens(meaningForumMappingLine, CSV_SEPARATOR);
			Long meaningId;
			String value;
			String createdBy = null;
			Timestamp createdOn = null;

			if (meaningForumMappingCells.length < 2 || meaningForumMappingCells.length > 4) {
				logger.warn("# {} - Incorrect line format: \"{}\"", lineCounter, meaningForumMappingLine);
				errorCounter++;
				continue;
			}

			meaningId = Long.valueOf(meaningForumMappingCells[0].trim());
			value = meaningForumMappingCells[1].trim();

			if (meaningForumMappingCells.length >= 3) {
				String createdByCellValue = meaningForumMappingCells[2].trim();
				if (StringUtils.isNotBlank(createdByCellValue)) {
					createdBy = createdByCellValue;
				}
			}

			if (meaningForumMappingCells.length == 4) {
				String createdOnCellValue = meaningForumMappingCells[3].trim();
				if (StringUtils.isNotBlank(createdOnCellValue)) {
					createdOn = conversionUtil.dateStrToTimestamp(createdOnCellValue);
				}
			}

			cudDbService.createMeaningForum(meaningId, value, createdOn, createdBy);
			succesCounter++;
		}

		logger.info("There were {} mappings, {} meaning forums created, altogether {} errors", lineCount, succesCounter, errorCounter);
	}

}
