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

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class DefinitionLoaderRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(DefinitionLoaderRunner.class);

	private static final String DATASET_OD_TECH_CODE = "ÕS-tehn";

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String definitionTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		List<String> definitionTsvLines = readFileLines(definitionTsvFilePath);
		definitionTsvLines.remove(0);

		int existCount = 0;
		int createCount = 0;
		int missingMeaningCount = 0;

		for (String definitionTsvLine : definitionTsvLines) {

			if (StringUtils.isBlank(definitionTsvLine)) {
				continue;
			}

			String[] definitionTsvCells = StringUtils.splitPreserveAllTokens(definitionTsvLine, CSV_SEPARATOR);
			Long meaningId = Long.valueOf(StringUtils.trim(definitionTsvCells[0]));
			String definitionValue = StringUtils.trim(definitionTsvCells[1]);
			Complexity complexity = Complexity.valueOf(StringUtils.trim(definitionTsvCells[2]));
			boolean isPublic = Boolean.valueOf(StringUtils.trim(definitionTsvCells[3]));

			boolean meaningExists = migrationDbService.meaningExists(meaningId);
			if (meaningExists) {

				Long definitionId = migrationDbService.getDefinitionId(meaningId, definitionValue, DATASET_EKI);
				if (definitionId == null) {
					definitionId = migrationDbService.getDefinitionId(meaningId, definitionValue);
					if (definitionId == null) {
						definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, LANGUAGE_CODE_EST, DEFINITION_TYPE_CODE_UNDEFINED, complexity, isPublic);
						createCount++;
					} else {
						existCount++;
					}
					cudDbService.createDefinitionDataset(definitionId, DATASET_EKI);
				} else {
					existCount++;
				}
				boolean definitionDatasetExists = migrationDbService.definitionDatasetExists(definitionId, DATASET_OD_TECH_CODE);
				if (!definitionDatasetExists) {
					cudDbService.createDefinitionDataset(definitionId, DATASET_OD_TECH_CODE);
				}

			} else {
				missingMeaningCount++;
			}
		}

		logger.info("Completed load. Out of {} lines, def create count: {}, def exist count: {}, missing meaning count: {}",
				definitionTsvLines.size(), createCount, existCount, missingMeaningCount);
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
