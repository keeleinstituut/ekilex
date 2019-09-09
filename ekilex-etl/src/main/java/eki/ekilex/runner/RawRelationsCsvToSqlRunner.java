package eki.ekilex.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RawRelationsCsvToSqlRunner extends AbstractClassifierRunner {

	private static Logger logger = LoggerFactory.getLogger(ClassifierCsvToSqlRunner.class);

	private final static String OUTPUT_FILE_EXTENSION = ".sql";

	private static final String INSERT_RAW_RELATION_TEMPLATE = "insert into word_relation (word1_id, word2_id, word_rel_type_code, relation_status) values (${word1_id}, ${word2_id}, 'raw', 'UNDEFINED') on conflict do nothing;";
	private static final String INSERT_RELATION_PARAM_TEMPLATE = "insert into word_relation_param (word_relation_id, name, value) select id, '${name}', '${value}' from word_relation where word1_id = ${word1_id} and word2_id = ${word2_id} and word_rel_type_code = 'raw';";

	private String inputFileFullPath;

	public void execute() throws Exception {
		File rawRelationCsvFile = new File(inputFileFullPath);
		logger.info("Reading file {}", inputFileFullPath);
		if (!rawRelationCsvFile.exists()) {
			logger.error("-----------------------------------------------------------------");
			logger.error("The specified file {} does not exist.", inputFileFullPath);
			logger.error("-----------------------------------------------------------------");
			return;
		}
		StringBuffer insertRawRelationsBuf = new StringBuffer();

		List<String> rawRelationFileLines = readFileLines(rawRelationCsvFile);

		for (String rawRelationLine : rawRelationFileLines) {
			String[] rawRelationLineCells = StringUtils.split(rawRelationLine, CSV_SEPARATOR);
			String word1Id = rawRelationLineCells[1];
			String word2Id = rawRelationLineCells[3];

			String paramName = rawRelationLineCells[4];
			String paramValue = rawRelationLineCells[5];

			String insertRelSql = composeInsertWordRelation(word1Id, word2Id);
			String insertParamSql = composeInsertWordRelationParam(word1Id, word2Id, paramName, paramValue);

			insertRawRelationsBuf.append(insertRelSql).append('\n');
			insertRawRelationsBuf.append(insertParamSql).append('\n');
		}

		writeToFile(insertRawRelationsBuf);

	}

	private void writeToFile(StringBuffer insertRelationsBuf) throws Exception {

		String outputFileName = StringUtils.substringBefore(inputFileFullPath, ".") + OUTPUT_FILE_EXTENSION;

		FileOutputStream rawRelationSqlStream = new FileOutputStream(outputFileName);
		IOUtils.write(insertRelationsBuf, rawRelationSqlStream, StandardCharsets.UTF_8);

		rawRelationSqlStream.flush();
		rawRelationSqlStream.close();

		logger.info("=======================================");
		logger.info("SQL file generated to: {}", outputFileName);
		logger.info("=======================================");
	}


	private String composeInsertWordRelation(String word1Id, String word2Id) {
		String sql = INSERT_RAW_RELATION_TEMPLATE;

		sql = StringUtils.replace(sql, "${word1_id}", word1Id);
		sql = StringUtils.replace(sql, "${word2_id}", word2Id);

		return sql;
	}

	private String composeInsertWordRelationParam(String word1Id, String word2Id, String name, String value) {
		String sql = INSERT_RELATION_PARAM_TEMPLATE;

		sql = StringUtils.replace(sql, "${word1_id}", word1Id);
		sql = StringUtils.replace(sql, "${word2_id}", word2Id);

		sql = StringUtils.replace(sql, "${name}", name);
		sql = StringUtils.replace(sql, "${value}", value);

		return sql;
	}

	@Override
	void initialise() throws Exception {

	}

	public void setInputFileFullPath(String inputFileFullPath) {
		this.inputFileFullPath = inputFileFullPath;
	}
}
