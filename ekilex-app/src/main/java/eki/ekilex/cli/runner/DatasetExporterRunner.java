package eki.ekilex.cli.runner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.data.Count;
import eki.common.data.transport.ForeignKey;
import eki.common.data.transport.TableColumn;
import eki.ekilex.service.AbstractLoaderCommons;
import eki.ekilex.service.cli.TransportService;

// TODO obsolete
@Component
public class DatasetExporterRunner extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(DatasetExporterRunner.class);

	private static final int PROGRESS_LOG_DELAY = 10000;

	private static final char TABLES_HIERARCHY_PATH_SEPARATOR = '/';

	private static final String SQL_SELECT_WORDS_FOR_DATASET_PATH = "sql/select_words_for_dataset.sql";

	private static final String SQL_SELECT_PARADIGMS_FOR_DATASET_PATH = "sql/select_paradigms_for_dataset.sql";

	private static final String SQL_SELECT_COLLOCATIONS_FOR_DATASET_PATH = "sql/select_collocations_for_dataset.sql";

	private static final String SQL_SELECT_LEXEMES_FOR_DATASET_PATH = "sql/select_lexemes_for_dataset.sql";

	private static final String SQL_SELECT_MEANINGS_FOR_DATASET_PATH = "sql/select_meanings_for_dataset.sql";

	private static final String SQL_SELECT_DATASET = "select * from " + DATASET + " where code = :datasetCode";

	private String sqlSelectWordsForDataset;

	private String sqlSelectParadigmsForDataset;

	private String sqlSelectCollocationsForDataset;

	private String sqlSelectLexemesForDataset;

	private String sqlSelectMeaningsForDataset;

	@Autowired
	private TransportService transportService;

	private Map<String, String> sqlSelectQueryCache;

	private Set<String> tablesHierarchyPaths;

	private Count totalRecordCount;

	@Override
	public void afterPropertiesSet() throws Exception {

		totalRecordCount = new Count();

		sqlSelectQueryCache = new HashMap<String, String>();

		tablesHierarchyPaths = new HashSet<String>();
		tablesHierarchyPaths.add(composePath(DATASET));

		tablesHierarchyPaths.add(composePath(WORD));
		tablesHierarchyPaths.add(composePath(WORD, WORD_GUID));
		tablesHierarchyPaths.add(composePath(WORD, WORD_WORD_TYPE));
		tablesHierarchyPaths.add(composePath(WORD, WORD_RELATION));
		tablesHierarchyPaths.add(composePath(WORD, WORD_GROUP_MEMBER));
		tablesHierarchyPaths.add(composePath(WORD, WORD_GROUP_MEMBER, WORD_GROUP));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ACTIVITY_LOG, ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(WORD, WORD_FREEFORM));
		tablesHierarchyPaths.add(composePath(WORD, WORD_FREEFORM, FREEFORM));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ETYMOLOGY));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ETYMOLOGY, WORD_ETYMOLOGY_RELATION));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ETYMOLOGY, WORD_ETYMOLOGY_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(WORD, WORD_ETYMOLOGY, WORD_ETYMOLOGY_SOURCE_LINK, SOURCE));

		tablesHierarchyPaths.add(composePath(PARADIGM));
		tablesHierarchyPaths.add(composePath(PARADIGM, PARADIGM_FORM));
		tablesHierarchyPaths.add(composePath(PARADIGM, PARADIGM_FORM, FORM));

		tablesHierarchyPaths.add(composePath(MEANING));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_NR));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_DOMAIN));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_RELATION));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_ACTIVITY_LOG, ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_FREEFORM));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_FREEFORM, FREEFORM));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(MEANING, MEANING_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK, SOURCE));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_DATASET));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_SOURCE_LINK, SOURCE));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_FREEFORM));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_FREEFORM, FREEFORM));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(MEANING, DEFINITION, DEFINITION_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK, SOURCE));

		tablesHierarchyPaths.add(composePath(COLLOCATION));

		tablesHierarchyPaths.add(composePath(LEXEME));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_POS));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_DERIV));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_REGION));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_REGISTER));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREQUENCY));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_RELATION));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_TAG));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_ACTIVITY_LOG, ACTIVITY_LOG));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREEFORM));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREEFORM, FREEFORM));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREEFORM, FREEFORM, FREEFORM));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK));
		tablesHierarchyPaths.add(composePath(LEXEME, LEXEME_FREEFORM, FREEFORM, FREEFORM_SOURCE_LINK, SOURCE));
		tablesHierarchyPaths.add(composePath(LEXEME, LEX_COLLOC_POS_GROUP));
		tablesHierarchyPaths.add(composePath(LEXEME, LEX_COLLOC_POS_GROUP, LEX_COLLOC_REL_GROUP));
		tablesHierarchyPaths.add(composePath(LEXEME, LEX_COLLOC));

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORDS_FOR_DATASET_PATH);
		sqlSelectWordsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_PARADIGMS_FOR_DATASET_PATH);
		sqlSelectParadigmsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_COLLOCATIONS_FOR_DATASET_PATH);
		sqlSelectCollocationsForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_LEXEMES_FOR_DATASET_PATH);
		sqlSelectLexemesForDataset = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_MEANINGS_FOR_DATASET_PATH);
		sqlSelectMeaningsForDataset = getContent(resourceFileInputStream);
	}

	@Transactional(rollbackOn = Exception.class)
	public void execute(String datasetCode, boolean isOnlyPublic, String exportFolder) throws Exception {

		logger.info("Starting dataset \"{}\" export...", datasetCode);
		if (isOnlyPublic) {
			logger.info("Exporting only public data");
		}

		long t1, t2;
		t1 = System.currentTimeMillis();

		String exportFileName = datasetCode + ".zip";
		String exportFilePath = exportFolder + exportFileName;
		File exportFile = new File(exportFilePath);
		if (!exportFile.exists()) {
			File folder = exportFile.getParentFile();
			if (folder != null) {
				folder.mkdirs();
			}
			exportFile.createNewFile();
		}
		FileOutputStream exportFileOutputStream = new FileOutputStream(exportFile);
		BufferedOutputStream exportBufferedOutputStream = new BufferedOutputStream(exportFileOutputStream);
		ZipOutputStream jsonZipOutputStream = new ZipOutputStream(exportBufferedOutputStream, Charset.forName(UTF_8));

		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonZipOutputStream);
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
		jsonGenerator.setCodec(objectMapper);

		ZipEntry zipEntry;
		String exportEntryName;
		List<Map<String, Object>> tableRows;
		for (String rootTableName : transportService.getRootTables()) {
			tableRows = getTableRows(rootTableName, datasetCode, isOnlyPublic);
			if (CollectionUtils.isEmpty(tableRows)) {
				continue;
			}
			exportEntryName = rootTableName + ".json";
			logger.info("Starting on file entry \"{}\"", exportEntryName);
			zipEntry = new ZipEntry(exportEntryName);
			jsonZipOutputStream.putNextEntry(zipEntry);
			jsonGenerator.writeStartObject();
			if (tableRows.size() == 1) {
				Map<String, Object> tableRow = tableRows.get(0);
				appendToRoot(rootTableName, tableRow, jsonGenerator);
			} else {
				appendToRoot(rootTableName, tableRows, jsonGenerator);
			}
			jsonGenerator.writeEndObject();
			jsonGenerator.flush();
			jsonZipOutputStream.closeEntry();
		}

		jsonGenerator.close();
		jsonZipOutputStream.flush();
		jsonZipOutputStream.close();
		exportBufferedOutputStream.flush();
		exportBufferedOutputStream.close();
		exportFileOutputStream.flush();
		exportFileOutputStream.close();

		t2 = System.currentTimeMillis();
		long timeMillis = t2 - t1;
		String timeLog = toReadableFormat(timeMillis);

		logger.info("Total exported record count {}", totalRecordCount.getValue());

		logger.info("Dataset \"{}\" export completed at {}", datasetCode, timeLog);
	}

	private void appendToRoot(String tableName, Map<String, Object> tableRow, JsonGenerator jsonGenerator) throws IOException, Exception {
		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForExport().get(tableName);
		List<ForeignKey> referringForeignKeys = transportService.getReferringForeignKeysMapForExport().get(tableName);
		jsonGenerator.writeFieldName(tableName);
		populate(tableName, null, tablesHierarchyPaths, tableRow, tableColumnsMap, referringForeignKeys, jsonGenerator);
	}

	private void appendToRoot(String tableName, List<Map<String, Object>> tableRows, JsonGenerator jsonGenerator) throws IOException, Exception {
		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForExport().get(tableName);
		List<ForeignKey> referringForeignKeys = transportService.getReferringForeignKeysMapForExport().get(tableName);
		jsonGenerator.writeFieldName(tableName);
		populate(tableName, null, tablesHierarchyPaths, tableRows, tableColumnsMap, referringForeignKeys, jsonGenerator);
	}

	private void populate(
			String tableName, String currentPath, Set<String> desiredPaths,
			List<Map<String, Object>> tableRows, Map<String, TableColumn> tableColumnsMap, List<ForeignKey> referringForeignKeys,
			JsonGenerator jsonGenerator) throws Exception {

		jsonGenerator.writeStartArray();
		for (Map<String, Object> tableRow : tableRows) {
			populate(tableName, currentPath, desiredPaths, tableRow, tableColumnsMap, referringForeignKeys, jsonGenerator);
		}
		jsonGenerator.writeEndArray();
	}

	private void populate(
			String tableName, String currentPath, Set<String> desiredPaths,
			Map<String, Object> tableRow, Map<String, TableColumn> tableColumnsMap, List<ForeignKey> referringForeignKeys,
			JsonGenerator jsonGenerator) throws Exception {

		if (tableRow == null) {
			return;
		}
		if (StringUtils.isBlank(currentPath)) {
			currentPath = tableName;
		} else {
			currentPath += TABLES_HIERARCHY_PATH_SEPARATOR + tableName;
		}
		if (!desiredPaths.contains(currentPath)) {
			return;
		}
		List<String> supportedTableNames = transportService.getExportTableNames();
		if (!supportedTableNames.contains(tableName)) {
			logger.error("Export of this table is not supported: \"{}\"", tableName);
			return;
		}
		totalRecordCount.increment();
		jsonGenerator.writeStartObject();
		for (Entry<String, Object> tableRowField : tableRow.entrySet()) {
			Object streamSafeValue = convertIfNecessary(tableRowField.getValue());
			jsonGenerator.writeObjectField(tableRowField.getKey(), streamSafeValue);
		}
		for (TableColumn tableColumn : tableColumnsMap.values()) {
			String fkTableName = tableColumn.getFkTableName();
			// 1:1 referring relations
			if (StringUtils.isNotBlank(fkTableName)) {
				String fkColumnName = tableColumn.getFkColumnName();
				Serializable fkColumnValue = (Serializable) tableRow.get(tableColumn.getColumnName());
				Map<String, TableColumn> fkTableColumnsMap = transportService.getTablesColumnsMapForExport().get(fkTableName);
				List<ForeignKey> fkTableReferringForeignKeys = transportService.getReferringForeignKeysMapForExport().get(fkTableName);
				if (MapUtils.isEmpty(fkTableColumnsMap)) {
					continue;
				}
				String nextPath = currentPath + TABLES_HIERARCHY_PATH_SEPARATOR + fkTableName;
				if (!desiredPaths.contains(nextPath)) {
					continue;
				}
				Map<String, Object> fkTableRow = getTableRowByColumn(fkTableName, fkColumnName, fkColumnValue, fkTableColumnsMap);
				if (MapUtils.isEmpty(fkTableRow)) {
					continue;
				}
				jsonGenerator.writeFieldName(fkTableName);
				populate(fkTableName, currentPath, desiredPaths, fkTableRow, fkTableColumnsMap, fkTableReferringForeignKeys, jsonGenerator);
			}
		}
		if (CollectionUtils.isEmpty(referringForeignKeys)) {
			jsonGenerator.writeEndObject();
			return;
		}
		// 1:* referred relations
		for (ForeignKey referringForeignKey : referringForeignKeys) {
			String fkTableName = referringForeignKey.getFkTableName();
			String fkColumnName = referringForeignKey.getFkColumnName();
			Serializable fkColumnValue = (Serializable) tableRow.get(referringForeignKey.getPkColumnName());
			Map<String, TableColumn> fkTableColumnsMap = transportService.getTablesColumnsMapForExport().get(fkTableName);
			List<ForeignKey> fkTableReferringForeignKeys = transportService.getReferringForeignKeysMapForExport().get(fkTableName);
			if (MapUtils.isEmpty(fkTableColumnsMap)) {
				continue;
			}
			String nextPath = currentPath + TABLES_HIERARCHY_PATH_SEPARATOR + fkTableName;
			if (!desiredPaths.contains(nextPath)) {
				continue;
			}
			List<Map<String, Object>> fkTableRows = getTableRowsByColumn(fkTableName, fkColumnName, fkColumnValue, fkTableColumnsMap);
			if (CollectionUtils.isEmpty(fkTableRows)) {
				continue;
			}
			jsonGenerator.writeFieldName(fkTableName);
			populate(fkTableName, currentPath, desiredPaths, fkTableRows, fkTableColumnsMap, fkTableReferringForeignKeys, jsonGenerator);
		}
		jsonGenerator.writeEndObject();
	}

	private List<Map<String, Object>> getTableRows(String tableName, String datasetCode, boolean isOnlyPublic) throws Exception {

		String sql;
		if (StringUtils.equalsIgnoreCase(DATASET, tableName)) {
			sql = SQL_SELECT_DATASET;
		} else if (StringUtils.equalsIgnoreCase(WORD, tableName)) {
			sql = new String(sqlSelectWordsForDataset);
		} else if (StringUtils.equalsIgnoreCase(PARADIGM, tableName)) {
			sql = new String(sqlSelectParadigmsForDataset);
		} else if (StringUtils.equalsIgnoreCase(MEANING, tableName)) {
			sql = new String(sqlSelectMeaningsForDataset);
		} else if (StringUtils.equalsIgnoreCase(COLLOCATION, tableName)) {
			sql = new String(sqlSelectCollocationsForDataset);
		} else if (StringUtils.equalsIgnoreCase(LEXEME, tableName)) {
			sql = new String(sqlSelectLexemesForDataset);
		} else {
			return null;
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("datasetCode", datasetCode);
		if (isOnlyPublic) {
			sql = StringUtils.remove(sql, "--");
			paramMap.put("publicity", PUBLICITY_PUBLIC);
		}
		List<Map<String, Object>> tableRows = basicDbService.queryList(sql, paramMap);
		return tableRows;
	}

	private Map<String, Object> getTableRowByColumn(String tableName, String columnName, Serializable columnValue, Map<String, TableColumn> tableColumnsMap) {

		List<Map<String, Object>> tableRows = getTableRowsByColumn(tableName, columnName, columnValue, tableColumnsMap);
		if (CollectionUtils.isEmpty(tableRows)) {
			return null;
		}
		Map<String, Object> tableRow = tableRows.get(0);
		return tableRow;
	}

	private List<Map<String, Object>> getTableRowsByColumn(String tableName, String columnName, Serializable columnValue, Map<String, TableColumn> tableColumnsMap) {

		tableName = tableName.toLowerCase();
		String sqlQueryStr = composeSqlSelectByColumn(tableName, columnName, tableColumnsMap);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(columnName, columnValue);
		List<Map<String, Object>> tableRows = basicDbService.queryList(sqlQueryStr, paramMap);
		return tableRows;
	}

	private String composeSqlSelectByColumn(String tableName, String columnName, Map<String, TableColumn> tableColumnsMap) {

		String queryStrKey = tableName + "-" + columnName;
		String queryStr = sqlSelectQueryCache.get(queryStrKey);
		if (queryStr != null) {
			return queryStr;
		}
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append("select ");
		int tableColumnIndex = 0;
		for (TableColumn tableColumn : tableColumnsMap.values()) {
			queryBuf.append(tableColumn.getColumnName());
			if (tableColumnIndex < tableColumnsMap.size() - 1) {
				queryBuf.append(", ");
			}
			tableColumnIndex++;
		}
		queryBuf.append(" ");
		queryBuf.append("from ");
		queryBuf.append(tableName);
		queryBuf.append(" ");
		queryBuf.append("where ");
		queryBuf.append(columnName);
		queryBuf.append(" = :");
		queryBuf.append(columnName);
		queryStr = queryBuf.toString();
		sqlSelectQueryCache.put(queryStrKey, queryStr);

		return queryStr;
	}

	private Object convertIfNecessary(Object value) throws Exception {
		if (value == null) {
			return value;
		} else if (value instanceof PgArray) {
			PgArray array = (PgArray) value;
			return array.getArray();
		} else if (value instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) value;
			return transportService.format(timestamp);
		}
		return value;
	}

	private String composePath(String... tableNames) {
		StringBuffer pathBuf = new StringBuffer();
		for (int tableNameIndex = 0; tableNameIndex < tableNames.length; tableNameIndex++) {
			pathBuf.append(tableNames[tableNameIndex].toLowerCase());
			if (tableNameIndex < tableNames.length - 1) {
				pathBuf.append(TABLES_HIERARCHY_PATH_SEPARATOR);
			}
		}
		return pathBuf.toString();
	}

	@Scheduled(fixedRate = PROGRESS_LOG_DELAY)
	public void autoLogProgress() {
		if (totalRecordCount == null) {
			return;
		}
		long count = totalRecordCount.getValue();
		if (count == 0) {
			return;
		}
		logger.debug("Exported {} records", count);
	}
}
