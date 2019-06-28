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

	public final static String RAW_RELATION_SQL_PATH = "./fileresources/sql/insert-raw-relations.sql";
	public final static String RAW_RELATION_CSV_PATH = "./fileresources/csv/raw-grave-18.csv";

	private static final String INSERT_RAW_RELATION_TEMPLATE = "insert into word_relation (word1_id, word2_id, word_rel_type_code, relation_status) values (${word1_id}, ${word2_id}, 'raw', 'UNDEFINED');";
	private static final String INSERT_RELATION_PARAM_TEMPLATE = "insert into word_relation_param (word_relation_id, name, value) select id, '${name}', '${value}' from word_relation where word1_id = ${word1_id} and word2_id = ${word2_id} and word_rel_type_code = 'raw';";


	public void execute() throws Exception {
		File rawRelationCsvFile = new File(RAW_RELATION_CSV_PATH);
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
		logger.debug("Raw relations sql created.");
	}

	private void writeToFile(StringBuffer insertRelationsBuf) throws Exception {

		FileOutputStream rawRelationSqlStream = new FileOutputStream(RAW_RELATION_SQL_PATH);
		IOUtils.write(insertRelationsBuf, rawRelationSqlStream, StandardCharsets.UTF_8);

		rawRelationSqlStream.flush();
		rawRelationSqlStream.close();
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
}
