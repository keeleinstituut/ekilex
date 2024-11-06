package eki.ekilex.cli.runner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class DomainFixerRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(DomainFixerRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String domainTsvFilePath) throws Exception {

		logger.info("Starting fixing...");

		List<String> domainTsvLines = readFileLines(domainTsvFilePath);

		int updateCount = 0;
		int createCount = 0;

		for (String domainTsvLine : domainTsvLines) {

			if (StringUtils.isBlank(domainTsvLine)) {
				continue;
			}

			String[] domainTsvCells = StringUtils.splitPreserveAllTokens(domainTsvLine, CSV_SEPARATOR);
			String code = StringUtils.trim(domainTsvCells[0]);
			String origin = StringUtils.trim(domainTsvCells[1]);
			String lang = StringUtils.trim(domainTsvCells[2]);
			String defaultType = StringUtils.trim(domainTsvCells[3]);
			String value = StringUtils.trim(domainTsvCells[4]);
			String comment = StringUtils.trim(domainTsvCells[5]);
			String commentType = "comment";

			migrationDbService.updateDomainLabelValue(code, origin, value, lang, defaultType);
			updateCount++;
			boolean isCreate = migrationDbService.createDomainLabel(code, origin, comment, lang, commentType);
			if (isCreate) {
				createCount++;
			}
		}

		logger.info("Completed fixing. Update count: {}; create count: {}", updateCount, createCount);
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
