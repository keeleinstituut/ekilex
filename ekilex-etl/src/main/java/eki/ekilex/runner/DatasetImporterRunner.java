package eki.ekilex.runner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.exception.DataLoadingException;
import eki.common.util.CodeGenerator;
import eki.ekilex.data.transport.ForeignKey;
import eki.ekilex.data.transport.TableColumn;
import eki.ekilex.service.TransportService;

@Component
public class DatasetImporterRunner extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(DatasetImporterRunner.class);

	private static final String DATASET_TYPE_COMPENSATION = DatasetType.TERM.name();

	private static final String LEXEME_COMPLEXITY_COMPENSATION = Complexity.DEFAULT.name();

	private static final String IMPORT_PK_MAP = "temp_ds_import_pk_map";

	private static final String IMPORT_QUEUE = "temp_ds_import_queue";

	private static final String SQL_SELECT_MAPPED_PK =
			"select "
			+ "target_pk "
			+ "from " + IMPORT_PK_MAP + " "
			+ "where "
			+ "import_code = :importCode "
			+ "and table_name = :fkTableName "
			+ "and source_pk = :fkValue";

	@Autowired
	private TransportService transportService;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Transactional
	public void execute(String sourceDatasetCode, String targetDatasetCode, String importFilePath) throws Exception {

		String importCode = CodeGenerator.generateUniqueId();
		Context context = new Context(importCode, sourceDatasetCode, targetDatasetCode);

		logger.debug("Starting import \"{}\" from \"{}\"", importCode, importFilePath);
		logger.debug("Target dataset is \"{}\"", targetDatasetCode);

		File zippedImportFile = new File(importFilePath);
		ZipFile zipFile = new ZipFile(zippedImportFile);
		Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
		ObjectMapper objectMapper = new ObjectMapper();
		ZipEntry zipEntry;
		InputStream zipEntryStream;
		BufferedInputStream jsonInputStream;
		Object rootData;
		long t1, t2;

		while (zipFileEntries.hasMoreElements()) {

			zipEntry = zipFileEntries.nextElement();
			zipEntryStream = zipFile.getInputStream(zipEntry);
			jsonInputStream = new BufferedInputStream(zipEntryStream);
			String zipEntryName = zipEntry.getName();
			logger.debug("Starting on file entry \"{}\"", zipEntryName);
			t1 = System.currentTimeMillis();
			rootData = objectMapper.readValue(jsonInputStream, Object.class);
			extractRoot(context, rootData);
			t2 = System.currentTimeMillis();
			long timeMillis = t2 - t1;
			String timeLog = toReadableFormat(timeMillis);
			logger.debug("File entry resolved at {}", timeLog);
			logger.debug("Current record count {}, queue count {}", context.getCreatedRecordCount().getValue(), context.getUnresolvedRecordCount().getValue());
			jsonInputStream.close();
			zipEntryStream.close();
		}
		zipFile.close();

		resolveQueue(context);

		logger.debug("In total created {} records", context.getCreatedRecordCount().getValue());
		logger.debug("Recursively resolved {} records", context.getRecursivelyResolvedRecordCount().getValue());
		logger.debug("Remaining unresolved {} records", context.getUnresolvedRecordCount().getValue());

		logger.debug("Done with import");
	}

	private void extractRoot(Context context, Object rootData) throws Exception {

		@SuppressWarnings("unchecked")
		Map<String, Object> rootDataMap = (Map<String, Object>) rootData;
		for (String tableName : rootDataMap.keySet()) {
			Object data = rootDataMap.get(tableName);
			extractTablesData(context, tableName, data, null);
		}
	}

	private void extractTablesData(Context context, String tableName, Object data, Long queueId) throws Exception {

		if (data == null) {
			return;
		}
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) data;
			saveTablesData(context, tableName, dataMap, queueId);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
			Iterator<Map<String, Object>> dataListIter = dataList.iterator();
			while (dataListIter.hasNext()) {
				Map<String, Object> dataMap = dataListIter.next();
				saveTablesData(context, tableName, dataMap, queueId);
				dataListIter.remove();
			}
		} else {
			throw new DataLoadingException("Unknown data type: " + data.getClass());
		}
	}

	private void saveTablesData(Context context, String tableName, Map<String, Object> dataMap, Long queueId) throws Exception {

		if (StringUtils.equalsIgnoreCase(DATASET, tableName)) {
			dataMap = compensateMissingFields(DATASET, dataMap);
			createDataset(context, dataMap);
		} else {
			handleCurrentData(context, tableName, dataMap, queueId);
		}
		handleReferringData(context, tableName, dataMap);
		handleNestedData(context, tableName, dataMap);
	}

	private void handleCurrentData(Context context, String tableName, Map<String, Object> dataMap, Long queueId) throws Exception {

		String importCode = context.getImportCode();
		String sourceDatasetCode = context.getSourceDatasetCode();
		String targetDatasetCode = context.getTargetDatasetCode();

		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForImport().get(tableName);
		List<ForeignKey> referringForeignKeys = transportService.getReferringForeignKeysMap().get(tableName);
		boolean queueExists = queueId != null;

		FkReassignResult fkReassignResult = reassignFks(importCode, sourceDatasetCode, targetDatasetCode, dataMap, tableColumnsMap);
		if (fkReassignResult.isSuccess()) {
			Map<String, Object> reassignedDataMap = fkReassignResult.getReassignedDataMap();
			Map<String, Object> truncDataMap = truncate(reassignedDataMap, tableColumnsMap);
			if (CollectionUtils.isNotEmpty(referringForeignKeys)) {
				Long sourceId = getPk(dataMap, tableColumnsMap);
				Long targetId = null;
				if (MapUtils.isEmpty(truncDataMap)) {
					targetId = basicDbService.create(tableName);
				} else {
					truncDataMap = compensateMissingFields(tableName, truncDataMap);
					targetId = basicDbService.create(tableName, truncDataMap);
				}
				context.getCreatedRecordCount().increment();
				createPkMap(importCode, tableName, sourceId, targetId);
			} else {
				basicDbService.createWithoutId(tableName, truncDataMap);
				context.getCreatedRecordCount().increment();
			}
			if (queueExists) {
				deleteQueue(queueId);
				context.getUnresolvedRecordCount().decrement();
				context.getRecursivelyResolvedRecordCount().increment();
			}
		} else if (fkReassignResult.isMissingReference()) {
			if (!queueExists) {
				createQueue(importCode, tableName, dataMap);
				context.getUnresolvedRecordCount().increment();
			}
		}
	}

	private void handleReferringData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		List<ForeignKey> referringForeignKeys = transportService.getReferringForeignKeysMap().get(tableName);
		if (CollectionUtils.isEmpty(referringForeignKeys)) {
			return;
		}
		List<String> supportedTableNames = transportService.getSupportedTableNames();
		for (ForeignKey referringForeignKey : referringForeignKeys) {
			String fkTableName = referringForeignKey.getFkTableName();
			if (!supportedTableNames.contains(fkTableName)) {
				continue;
			}
			Object referringData = dataMap.get(fkTableName);
			extractTablesData(context, fkTableName, referringData, null);
		}
	}

	private void handleNestedData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForImport().get(tableName);

		for (Entry<String, Object> dataEntry : dataMap.entrySet()) {
			Object dataColumnValue = dataEntry.getValue();
			if (dataColumnValue == null) {
				continue;
			}
			String dataColumnName = dataEntry.getKey();
			boolean isNestedData = !tableColumnsMap.containsKey(dataColumnName);
			if (isNestedData) {
				extractTablesData(context, dataColumnName, dataColumnValue, null);
			}
		}
	}

	private void resolveQueue(Context context) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("import_code", context.getImportCode());
		List<Map<String, Object>> queueResults = basicDbService.selectAll(IMPORT_QUEUE, paramMap);
		if (CollectionUtils.isEmpty(queueResults)) {
			logger.debug("No leftovers");
			return;
		}
		int remainingUnresolvedRecordCount = queueResults.size();
		List<String> unresolvedTableNames = queueResults.stream().map(rowMap -> rowMap.get("table_name").toString()).distinct().collect(Collectors.toList());

		if (remainingUnresolvedRecordCount == context.getRecentUnresolvedRecordCount()) {
			logger.debug("There are still {} records that could not be imported. Interrupting recursion", remainingUnresolvedRecordCount);
			logger.debug("Remaining unimported data is in tables: {}", unresolvedTableNames);
			deleteTempData(context);
			return;
		}
		logger.debug("Attempting to resolve {} queue records", remainingUnresolvedRecordCount);
		logger.debug("Unresolved data is in tables: {}", unresolvedTableNames);
		context.setRecentUnresolvedRecordCount(remainingUnresolvedRecordCount);

		ObjectMapper objectMapper = new ObjectMapper();
		for (Map<String, Object> queueRow : queueResults) {
			Long queueId = (Long) queueRow.get("id");
			String tableName = (String) queueRow.get("table_name");
			String content = (String) queueRow.get("content");
			Object data = objectMapper.readValue(content, Object.class);
			extractTablesData(context, tableName, data, queueId);
		}

		resolveQueue(context);
	}

	// not to be used on composite pk-s!
	private Long getPk(Map<String, Object> dataMap, Map<String, TableColumn> tableColumnsMap) {
		Long sourceId = dataMap.entrySet().stream().filter(entry -> {
			String dataColumnName = entry.getKey();
			TableColumn tableColumn = tableColumnsMap.get(dataColumnName);
			return tableColumn.isPrimaryKey();
		}).map(entry -> Long.valueOf(entry.getValue().toString())).findFirst().get();
		return sourceId;
	}

	private Map<String, Object> truncate(Map<String, Object> dataMap, Map<String, TableColumn> tableColumnsMap) throws Exception {
		Map<String, Object> truncDataMap = new HashMap<>();
		for (Entry<String, Object> dataEntry : dataMap.entrySet()) {
			Object dataColumnValue = dataEntry.getValue();
			if (dataColumnValue == null) {
				continue;
			}
			String dataColumnName = dataEntry.getKey();
			TableColumn tableColumn = tableColumnsMap.get(dataColumnName);
			if (tableColumn == null) {
				continue;
			}
			if (tableColumn.isPrimaryKey() && StringUtils.isBlank(tableColumn.getFkTableName())) {
				continue;
			}
			dataColumnValue = convert(dataColumnValue, tableColumn);
			truncDataMap.put(dataColumnName, dataColumnValue);
		}
		return truncDataMap;
	}

	private Object convert(Object value, TableColumn tableColumn) throws Exception {

		if (value == null) {
			return value;
		}
		String dataType = tableColumn.getDataType().toLowerCase();
		String valueStr = value.toString();
		if (StringUtils.equals(dataType, "boolean")) {
			return Boolean.valueOf(valueStr);
		} else if (StringUtils.equals(dataType, "numeric")) {
			return Float.valueOf(valueStr);
		} else if (StringUtils.equals(dataType, "smallint")) {
			return Integer.valueOf(valueStr);
		} else if (StringUtils.equals(dataType, "integer")) {
			return Integer.valueOf(valueStr);
		} else if (StringUtils.equals(dataType, "bigint")) {
			return Long.valueOf(valueStr);
		} else if (StringUtils.equals(dataType, "real")) {
			return Float.valueOf(valueStr);
		} else if (StringUtils.startsWith(dataType, "character")) {
			return valueStr;
		} else if (StringUtils.equals(dataType, "text")) {
			return valueStr;
		} else if (StringUtils.startsWith(dataType, "timestamp")) {
			return transportService.parse(valueStr);
		} else if (StringUtils.equals(dataType, "array")) {
			if (value instanceof List) {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) value;
				String[] array = list.toArray(new String[0]);
				return new PgVarcharArray(array);
			} else if (value instanceof String[]) {
				String[] array = (String[]) value;
				return new PgVarcharArray(array);
			}
		}
		return value;
	}

	private FkReassignResult reassignFks(String importCode, String sourceDatasetCode, String targetDatasetCode, Map<String, Object> dataMap, Map<String, TableColumn> tableColumnsMap) {

		FkReassignResult fkReassignResult = new FkReassignResult();
		Map<String, Object> reassignedDataMap = new HashMap<>(dataMap);
		fkReassignResult.setReassignedDataMap(reassignedDataMap);

		Map<String, TableColumn> fkColumnsMap = tableColumnsMap.entrySet().stream().filter(entry -> {
			return StringUtils.isNotBlank(entry.getValue().getFkTableName());
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		boolean fkReassignSuccess = false;
		boolean missingReference = false;
		if (MapUtils.isEmpty(fkColumnsMap)) {
			fkReassignSuccess = true;
		} else {
			List<String> supportedTableNames = transportService.getSupportedTableNames();
			Map<String, Object> tempFkMap = new HashMap<>();
			int fksCovered = 0;
			for (TableColumn tableFkColumn : fkColumnsMap.values()) {
				String columnName = tableFkColumn.getColumnName();
				String fkTableName = tableFkColumn.getFkTableName();
				Object columnValue = dataMap.get(columnName);
				if (columnValue == null) {
					fksCovered++;
					continue;
				}
				Object mappedPkValue = null;
				if (StringUtils.equalsIgnoreCase(DATASET, fkTableName)) {
					String columnValueStr = columnValue.toString();
					if (StringUtils.equals(columnValueStr, sourceDatasetCode)) {
						mappedPkValue = targetDatasetCode;
					} else {
						mappedPkValue = columnValue;
					}
				} else if (!supportedTableNames.contains(fkTableName)) {
					mappedPkValue = columnValue;
				} else {
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("importCode", importCode);
					paramMap.put("fkTableName", fkTableName);
					paramMap.put("fkValue", columnValue);
					List<Object> results = basicDbService.queryList(SQL_SELECT_MAPPED_PK, paramMap, Object.class);
					if (CollectionUtils.isEmpty(results)) {
						missingReference = true;
						break;
					} else {
						mappedPkValue = results.get(0);
					}
				}
				if (mappedPkValue != null) {
					tempFkMap.put(columnName, mappedPkValue);
					fksCovered++;
				}
			}
			fkReassignSuccess = fksCovered == fkColumnsMap.size();
			if (fkReassignSuccess) {
				tempFkMap.forEach((columnName, columnValue) -> reassignedDataMap.put(columnName, columnValue));
			}
		}
		fkReassignResult.setSuccess(fkReassignSuccess);
		fkReassignResult.setMissingReference(missingReference);
		return fkReassignResult;
	}

	private Map<String, Object> compensateMissingFields(String tableName, Map<String, Object> dataMap) {
		Map<String, Object> dataMapCopy = new HashMap<>(dataMap);
		if (StringUtils.equalsIgnoreCase(DATASET, tableName)) {
			if (!dataMapCopy.containsKey("type")) {
				dataMapCopy.put("type", DATASET_TYPE_COMPENSATION);
			}
		} else if (StringUtils.equalsIgnoreCase(LEXEME, tableName)) {
			if (!dataMapCopy.containsKey("complexity")) {
				dataMapCopy.put("complexity", LEXEME_COMPLEXITY_COMPENSATION);
			}
		}
		return dataMapCopy;
	}

	private void createDataset(Context context, Map<String, Object> dataMap) throws Exception {
		dataMap.put("code", context.getTargetDatasetCode());
		basicDbService.createWithoutId(DATASET, dataMap);
		context.getCreatedRecordCount().increment();
	}

	private void createPkMap(String importCode, String tableName, Long sourceId, Long targetId) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("import_code", importCode);
		paramMap.put("table_name", tableName);
		paramMap.put("source_pk", sourceId);
		paramMap.put("target_pk", targetId);
		basicDbService.create(IMPORT_PK_MAP, paramMap);
	}

	private void createQueue(String importCode, String tableName, Map<String, Object> dataMap) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(dataMap);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("import_code", importCode);
		paramMap.put("table_name", tableName);
		paramMap.put("content", content);
		basicDbService.create(IMPORT_QUEUE, paramMap);
	}

	private void deleteQueue(Long queueId) {
		basicDbService.delete(IMPORT_QUEUE, queueId);
	}

	private void deleteTempData(Context context) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("importCode", context.getImportCode());

		String sql;
		int rowCount;

		sql = "delete from " + IMPORT_PK_MAP + " where import_code = :importCode";
		rowCount = basicDbService.executeScript(sql, paramMap);
		logger.debug("Deleted {} rows from {}", rowCount, IMPORT_PK_MAP);

		sql = "delete from " + IMPORT_QUEUE + " where import_code = :importCode";
		rowCount = basicDbService.executeScript(sql, paramMap);
		logger.debug("Deleted {} rows from {}", rowCount, IMPORT_QUEUE);
	}

	/*
	private Map<String, Object> parseObject(JsonParser jsonParser) throws Exception {

		Map<String, Object> record = new HashMap<>();
		String fieldName;
		Object fieldValue;
		JsonToken jsonToken = jsonParser.nextToken();
		while (!JsonToken.END_OBJECT.equals(jsonToken)) {
			fieldName = jsonParser.getCurrentName();
			fieldValue = null;
			if (JsonToken.FIELD_NAME.equals(jsonToken)) {
				jsonToken = jsonParser.nextToken();
				continue;
			}
			fieldValue = parseField(jsonParser);
			record.put(fieldName, fieldValue);
			jsonToken = jsonParser.nextToken();
		}
		return record;
	}

	private List<Object> parseArray(JsonParser jsonParser) throws Exception {

		List<Object> records = new ArrayList<>();
		Object fieldValue;
		JsonToken jsonToken = jsonParser.nextToken();
		while (!JsonToken.END_ARRAY.equals(jsonToken)) {
			fieldValue = parseField(jsonParser);
			if (fieldValue != null) {
				records.add(fieldValue);
			}
			jsonToken = jsonParser.nextToken();
		}
		return records;
	}

	private Object parseField(JsonParser jsonParser) throws Exception {

		Object fieldValue = null;
		JsonToken jsonToken = jsonParser.getCurrentToken();
		if (JsonToken.START_OBJECT.equals(jsonToken)) {
			fieldValue = parseObject(jsonParser);
		} else if (JsonToken.VALUE_STRING.equals(jsonToken)) {
			fieldValue = jsonParser.getValueAsString();
		} else if (JsonToken.VALUE_NULL.equals(jsonToken)) {
			fieldValue = null;
		} else if (JsonToken.VALUE_TRUE.equals(jsonToken)) {
			fieldValue = jsonParser.getValueAsBoolean();
		} else if (JsonToken.VALUE_NUMBER_INT.equals(jsonToken)) {
			fieldValue = jsonParser.getValueAsLong();
		} else if (JsonToken.VALUE_NUMBER_FLOAT.equals(jsonToken)) {
			fieldValue = jsonParser.getValueAsDouble();
		} else if (JsonToken.START_ARRAY.equals(jsonToken)) {
			fieldValue = parseArray(jsonParser);
		}
		return fieldValue;
	}
	*/

	class FkReassignResult extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private boolean success;

		private boolean missingReference;

		private Map<String, Object> reassignedDataMap;

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public boolean isMissingReference() {
			return missingReference;
		}

		public void setMissingReference(boolean missingReference) {
			this.missingReference = missingReference;
		}

		public Map<String, Object> getReassignedDataMap() {
			return reassignedDataMap;
		}

		public void setReassignedDataMap(Map<String, Object> reassignedDataMap) {
			this.reassignedDataMap = reassignedDataMap;
		}
	}

	class Context extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String importCode;

		private String sourceDatasetCode;

		private String targetDatasetCode;

		private Count createdRecordCount;

		private Count recursivelyResolvedRecordCount;

		private Count unresolvedRecordCount;

		private int recentUnresolvedRecordCount;

		public Context(String importCode, String sourceDatasetCode, String targetDatasetCode) {
			this.importCode = importCode;
			this.sourceDatasetCode = sourceDatasetCode;
			this.targetDatasetCode = targetDatasetCode;
			this.createdRecordCount = new Count();
			this.recursivelyResolvedRecordCount = new Count();
			this.unresolvedRecordCount = new Count();
			this.recentUnresolvedRecordCount = 0;
		}

		public String getImportCode() {
			return importCode;
		}

		public String getSourceDatasetCode() {
			return sourceDatasetCode;
		}

		public String getTargetDatasetCode() {
			return targetDatasetCode;
		}

		public Count getCreatedRecordCount() {
			return createdRecordCount;
		}

		public Count getRecursivelyResolvedRecordCount() {
			return recursivelyResolvedRecordCount;
		}

		public Count getUnresolvedRecordCount() {
			return unresolvedRecordCount;
		}

		public int getRecentUnresolvedRecordCount() {
			return recentUnresolvedRecordCount;
		}

		public void setRecentUnresolvedRecordCount(int recentUnresolvedRecordCount) {
			this.recentUnresolvedRecordCount = recentUnresolvedRecordCount;
		}
	}
}
