package eki.ekilex.runner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.RelationStatus;

@Component
public class RawRelationLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(RawRelationLoaderRunner.class);

	private static final String RAW_RELATION_TYPE = "raw";

	private final String sqlCreateWordRelation =
			"insert into " + WORD_RELATION + " (word1_id, word2_id, word_rel_type_code, relation_status) "
			+ "values (:word1_id, :word2_id, :word_rel_type_code, :relation_status) "
			+ "on conflict do nothing;";

	private final String sqlCreateOrUpdateWordRelationParam =
			"insert into " + WORD_RELATION_PARAM + " (word_relation_id, name, value) "
			+ "select id, :param_name, :param_value "
			+ "from " + WORD_RELATION + " "
			+ "where word1_id = :word1_id and word2_id = :word2_id and word_rel_type_code = :word_rel_type_code "
			+ "on conflict (word_relation_id,name) do update "
			+ "set value = :param_value;";

	@Override
	String getDataset() {
		return "rawrel";
	}

	@Override
	Complexity getLexemeComplexity() {
		return null;
	}

	@Override
	Complexity getDefinitionComplexity() {
		return null;
	}

	@Override
	Complexity getFreeformComplexity() {
		return null;
	}

	@Override
	void deleteDatasetData() {
	}

	@Override
	void initialise() throws Exception {
	}

	// TODO @Transactional ?
	public void execute(String inputFileFullPath) throws Exception {

		File rawRelationCsvFile = new File(inputFileFullPath);
		logger.info("Reading file {}", inputFileFullPath);

		if (!rawRelationCsvFile.exists()) {
			logger.error("-----------------------------------------------------------------");
			logger.error("The specified file {} does not exist.", inputFileFullPath);
			logger.error("-----------------------------------------------------------------");
			return;
		}

		start();

		long rawRelationCount;
		try (Stream<String> lines = Files.lines(Paths.get(inputFileFullPath))) {
			rawRelationCount = lines.count();
		}
		logger.debug("Starting to process {} raw relations", rawRelationCount);

		long rawRelationsCounter = 0;
		long progressIndicator = rawRelationCount / Math.min(rawRelationCount, 100);

		LineIterator lineIterator = FileUtils.lineIterator(rawRelationCsvFile, UTF_8);
		try {
			lineIterator.nextLine();//remove heading

			while (lineIterator.hasNext()) {
				String rawRelationLine = lineIterator.nextLine();
				String[] rawRelationLineCells = StringUtils.split(rawRelationLine, CSV_SEPARATOR);
				if (rawRelationLineCells.length != 6) {
					rawRelationsCounter++;
					continue;
				}

				Long word1Id = Long.valueOf(rawRelationLineCells[1]);
				Long word2Id = Long.valueOf(rawRelationLineCells[3]);
				String paramName = rawRelationLineCells[4];
				Float paramValue = Float.valueOf(rawRelationLineCells[5]);

				try {
					createWordRelation(word1Id, word2Id);
					createWordRelationParam(word1Id, word2Id, paramName, paramValue);
				} catch (Exception e) {
					logger.warn("Failed to create relation {} -> {}", word1Id, word2Id);
				}

				rawRelationsCounter++;
				if (rawRelationsCounter % progressIndicator == 0) {
					long progressPercent = rawRelationsCounter / progressIndicator;
					logger.debug("{}% - {} raw relations processed", progressPercent, rawRelationsCounter);
				}
			}
		} finally {
			LineIterator.closeQuietly(lineIterator);
		}
		end();
	}

	private void createWordRelation(Long word1Id, Long word2Id) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("word1_id", word1Id);
		paramMap.put("word2_id", word2Id);
		paramMap.put("word_rel_type_code", RAW_RELATION_TYPE);
		paramMap.put("relation_status", RelationStatus.UNDEFINED.name());

		basicDbService.executeScript(sqlCreateWordRelation, paramMap);
	}

	private void createWordRelationParam(Long word1Id, Long word2Id, String paramName, Float paramValue) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("word1_id", word1Id);
		paramMap.put("word2_id", word2Id);
		paramMap.put("param_name", paramName);
		paramMap.put("param_value", paramValue);
		paramMap.put("word_rel_type_code", RAW_RELATION_TYPE);

		basicDbService.executeScript(sqlCreateOrUpdateWordRelationParam, paramMap);
	}
}
