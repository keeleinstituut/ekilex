package eki.ekilex.runner;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.ekilex.data.transport.TableColumn;
import eki.ekilex.service.TransportService;

@Component
public class DatasetImportValidator extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(DatasetImportValidator.class);

	private static final String TABLES_PRINTOUT_FILE_PATH = "./tables-desc.txt";

	private static final String IMPORT_VALIDATION_LOG_PATH = "./import-validation.txt";

	@Autowired
	private TransportService transportService;

	public void printTablesDescription() throws Exception {

		List<String> tableNames = transportService.getImportTableNames();
		Map<String, Map<String, TableColumn>> tablesColumnsMap = transportService.getTablesColumnsMapForImport();
		Map<String, List<String>> referringTableNamesMap = transportService.getReferringTableNamesMapForImport();

		FileOutputStream printoutFileOutputStream = new FileOutputStream(TABLES_PRINTOUT_FILE_PATH);
		OutputStreamWriter printoutStreamWriter = new OutputStreamWriter(printoutFileOutputStream, UTF_8);
		BufferedWriter printoutBufferedWriter = new BufferedWriter(printoutStreamWriter);

		StringBuffer rowBuf;

		printoutBufferedWriter.write("================================================================================");
		printoutBufferedWriter.append('\n');

		for (String tableName : tableNames) {

			Map<String, TableColumn> tableColumns = tablesColumnsMap.get(tableName);
			List<String> referringTableNames = referringTableNamesMap.get(tableName);

			printoutBufferedWriter.write(tableName);
			printoutBufferedWriter.append('\n');
			printoutBufferedWriter.write("-----------------------------------columns--------------------------------------");
			printoutBufferedWriter.append('\n');

			for (Entry<String, TableColumn> tableColumnEntry : tableColumns.entrySet()) {

				TableColumn tableColumn = tableColumnEntry.getValue();
				rowBuf = new StringBuffer();
				rowBuf.append(tableColumn.getColumnName());
				rowBuf.append(", ");
				rowBuf.append(tableColumn.getDataType().toLowerCase());
				if (tableColumn.getCharMaxLength() != null) {
					rowBuf.append(" ");
					rowBuf.append(tableColumn.getCharMaxLength());
				}
				rowBuf.append(", ");
				if (tableColumn.isPrimaryKey()) {
					rowBuf.append("PK");
				} else if (tableColumn.isNullable()) {
					rowBuf.append("(optional)");
				} else {
					if (tableColumn.isDefaultExists()) {
						rowBuf.append("(mandatory - default exists)");
					} else {
						rowBuf.append("(mandatory)");
					}
				}
				rowBuf.append(" ");
				if (StringUtils.isNotBlank(tableColumn.getFkTableName())) {
					rowBuf.append("-> ");
					rowBuf.append(tableColumn.getFkTableName());
					rowBuf.append(".");
					rowBuf.append(tableColumn.getFkColumnName());
				}
				printoutBufferedWriter.write(rowBuf.toString());
				printoutBufferedWriter.append('\n');
			}

			if (CollectionUtils.isNotEmpty(referringTableNames)) {

				printoutBufferedWriter.write("----------------------------------referrers-------------------------------------");
				printoutBufferedWriter.append('\n');

				for (String referringTableName : referringTableNames) {
					printoutBufferedWriter.write(referringTableName);
					printoutBufferedWriter.append('\n');
				}
			}

			printoutBufferedWriter.write("================================================================================");
			printoutBufferedWriter.append('\n');
		}

		printoutBufferedWriter.flush();
		printoutBufferedWriter.close();
		printoutStreamWriter.close();
		printoutFileOutputStream.close();

		logger.info("Tables printout complete @ \"{}\"", TABLES_PRINTOUT_FILE_PATH);
	}

	public void validate(String importFilePath) throws Exception {

		FileOutputStream logFileOutputStream = new FileOutputStream(IMPORT_VALIDATION_LOG_PATH);
		OutputStreamWriter logStreamWriter = new OutputStreamWriter(logFileOutputStream, UTF_8);
		BufferedWriter logBufferedWriter = new BufferedWriter(logStreamWriter);

		Context context = initialiseContext();
		context.setWriter(logBufferedWriter);

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
			logger.info("Starting on file entry \"{}\"", zipEntryName);
			t1 = System.currentTimeMillis();
			rootData = objectMapper.readValue(jsonInputStream, Object.class);
			extractRoot(context, rootData);
			t2 = System.currentTimeMillis();
			long timeMillis = t2 - t1;
			String timeLog = toReadableFormat(timeMillis);
			logger.info("File entry resolved at {}", timeLog);
			jsonInputStream.close();
			zipEntryStream.close();
		}
		zipFile.close();

		validateForeignReferences(context);

		logBufferedWriter.flush();
		logBufferedWriter.close();
		logStreamWriter.close();
		logFileOutputStream.close();

		logger.info("Validation complete with {} errors", context.getValidationEntryCount().getValue());
		logger.info("See results @ \"{}\"", IMPORT_VALIDATION_LOG_PATH);
	}

	private Context initialiseContext() {
		List<String> supportedTableNames = transportService.getImportTableNames();
		Context context = new Context();
		Map<String, List<String>> tablesPksMap = context.getTablesPksMap();
		Map<String, List<String>> tablesFksMap = context.getTablesFksMap();
		for (String tableName : supportedTableNames) {
			tablesPksMap.put(tableName, new ArrayList<>());
			tablesFksMap.put(tableName, new ArrayList<>());
		}
		return context;
	}

	private void extractRoot(Context context, Object rootData) throws Exception {

		@SuppressWarnings("unchecked")
		Map<String, Object> rootDataMap = (Map<String, Object>) rootData;
		for (String tableName : rootDataMap.keySet()) {
			Object data = rootDataMap.get(tableName);
			extractTablesData(context, tableName, data);
		}
	}

	private void extractTablesData(Context context, String tableName, Object data) throws Exception {

		if (data == null) {
			return;
		}

		List<String> supportedTableNames = transportService.getImportTableNames();
		if (!supportedTableNames.contains(tableName)) {
			appendRow(context.getWriter(), "Tundmatu tabel", tableName);
			context.getValidationEntryCount().increment();
			return;
		}
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) data;
			validateTablesData(context, tableName, dataMap);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
			Iterator<Map<String, Object>> dataListIter = dataList.iterator();
			while (dataListIter.hasNext()) {
				Map<String, Object> dataMap = dataListIter.next();
				validateTablesData(context, tableName, dataMap);
			}
		} else {
			appendRow(context.getWriter(), "Sobimatu struktuurielement", data);
			context.getValidationEntryCount().increment();
		}
	}

	private void validateTablesData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		List<String> supportedTableNames = transportService.getImportTableNames();
		if (!supportedTableNames.contains(tableName)) {
			appendRow(context.getWriter(), "Tundmatu tabel", tableName);
			context.getValidationEntryCount().increment();
			return;
		}

		collectPkFk(context, tableName, dataMap);

		validateCurrentData(context, tableName, dataMap);
		validateReferringData(context, tableName, dataMap);
		validateNestedData(context, tableName, dataMap);
	}

	private void collectPkFk(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		List<String> supportedTableNames = transportService.getImportTableNames();
		List<String> tablePks = context.getTablesPksMap().get(tableName);
		Map<String, TableColumn> tableColumns = transportService.getTablesColumnsMapForImport().get(tableName);

		List<String> pkValues = tableColumns.values().stream()
				.filter(TableColumn::isPrimaryKey)
				.map(tableColumn -> dataMap.get(tableColumn.getColumnName()).toString())
				.collect(Collectors.toList());
		String pkCompValue = StringUtils.join(pkValues, '-');
		if (tablePks.contains(pkCompValue)) {
			appendRow(context.getWriter(), "Tabelil", tableName, "korduv PK", pkCompValue);
			context.getValidationEntryCount().increment();
		} else {
			tablePks.add(pkCompValue);
		}

		for (TableColumn tableColumn : tableColumns.values()) {
			String columnName = tableColumn.getColumnName();
			String fkTableName = tableColumn.getFkTableName();
			if (StringUtils.isNotBlank(fkTableName)) {
				if (supportedTableNames.contains(fkTableName)) {
					Object fkValue = dataMap.get(columnName);
					if (fkValue != null) {
						String fkValueStr = fkValue.toString();
						List<String> tableFks = context.getTablesFksMap().get(fkTableName);
						if (!tableFks.contains(fkValueStr)) {
							tableFks.add(fkValueStr);
						}
					}
				}
			}
		}
	}

	private void validateCurrentData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		Map<String, TableColumn> tableColumns = transportService.getTablesColumnsMapForImport().get(tableName);
		List<String> referringTableNames = transportService.getReferringTableNamesMapForImport().get(tableName);

		//unknown fields (columns + foreign refs + nested tables)
		Set<String> providedFields = dataMap.keySet();
		Set<String> columnNames = tableColumns.keySet();
		List<String> referredTableNames = transportService.getReferredTableNames(tableColumns);

		List<String> supportedFields = new ArrayList<>();
		supportedFields.addAll(columnNames);
		if (CollectionUtils.isNotEmpty(referringTableNames)) {
			supportedFields.addAll(referringTableNames);
		}
		if (CollectionUtils.isNotEmpty(referredTableNames)) {
			supportedFields.addAll(referredTableNames);			
		}

		Collection<String> unknownProvidedFields = CollectionUtils.subtract(providedFields, supportedFields);
		if (CollectionUtils.isNotEmpty(unknownProvidedFields)) {
			appendRow(context.getWriter(), "Tabeli", tableName, "tundmatud väljad", unknownProvidedFields);
			context.getValidationEntryCount().increment();
		}

		//not nullable fields log
		for (TableColumn tableColumn : tableColumns.values()) {
			if (!tableColumn.isNullable()) {
				String columnName = tableColumn.getColumnName();
				if (!dataMap.containsKey(columnName)) {
					appendRow(context.getWriter(), "Tabelil", tableName, "puudub kohustuslik väli", columnName);
					context.getValidationEntryCount().increment();
				}
			}
		}
	}

	private void validateReferringData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		List<String> referringTableNames = transportService.getReferringTableNamesMapForImport().get(tableName);
		if (CollectionUtils.isEmpty(referringTableNames)) {
			return;
		}
		List<String> supportedTableNames = transportService.getImportTableNames();
		for (String referringTableName : referringTableNames) {
			if (!supportedTableNames.contains(referringTableName)) {
				continue;
			}
			Object referringData = dataMap.get(referringTableName);
			extractTablesData(context, referringTableName, referringData);
		}
	}

	private void validateNestedData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		List<String> supportedTableNames = transportService.getImportTableNames();
		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForImport().get(tableName);
		List<String> referredTableNames = transportService.getReferredTableNames(tableColumnsMap);

		for (Entry<String, Object> dataEntry : dataMap.entrySet()) {
			Object dataColumnValue = dataEntry.getValue();
			if (dataColumnValue == null) {
				continue;
			}
			String dataColumnName = dataEntry.getKey();
			boolean isNestedData = !tableColumnsMap.containsKey(dataColumnName)
					&& referredTableNames.contains(dataColumnName)
					&& supportedTableNames.contains(dataColumnName);
			if (isNestedData) {
				extractTablesData(context, dataColumnName, dataColumnValue);
			}
		}
	}

	private void validateForeignReferences(Context context) throws Exception {

		Map<String, List<String>> tablesPksMap = context.getTablesPksMap();
		Map<String, List<String>> tablesFksMap = context.getTablesFksMap();

		for (String fkTableName : tablesFksMap.keySet()) {
			List<String> tableFks = tablesFksMap.get(fkTableName);
			List<String> tablePks = tablesPksMap.get(fkTableName);
			Collection<String> unassociatedFks = CollectionUtils.subtract(tableFks, tablePks);
			if (CollectionUtils.isNotEmpty(unassociatedFks)) {
				appendRow(context.getWriter(), "Tabelile", fkTableName, "viidatakse olematute võtmetega", unassociatedFks);
				context.getValidationEntryCount().increment();
			}
		}
	}

	private void appendRow(Writer writer, Object... rowElements) throws Exception {
		String row = StringUtils.join(rowElements, ' ');
		writer.write(row);
		writer.write('\n');
	}

	class Context extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Writer writer;

		private Count validationEntryCount;

		private Map<String, List<String>> tablesPksMap;

		private Map<String, List<String>> tablesFksMap;

		public Context() {
			this.validationEntryCount = new Count();
			this.tablesPksMap = new HashedMap<>();
			this.tablesFksMap = new HashedMap<>();
		}

		public Writer getWriter() {
			return writer;
		}

		public void setWriter(Writer writer) {
			this.writer = writer;
		}

		public Count getValidationEntryCount() {
			return validationEntryCount;
		}

		public Map<String, List<String>> getTablesPksMap() {
			return tablesPksMap;
		}

		public Map<String, List<String>> getTablesFksMap() {
			return tablesFksMap;
		}
	}
}
