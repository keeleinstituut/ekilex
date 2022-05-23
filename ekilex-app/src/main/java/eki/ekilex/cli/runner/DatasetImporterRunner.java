package eki.ekilex.cli.runner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.DatasetType;
import eki.common.constant.ActivityOwner;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.data.transport.ForeignKey;
import eki.common.data.transport.TableColumn;
import eki.common.exception.DataLoadingException;
import eki.common.service.AbstractLoaderCommons;
import eki.common.service.TransportService;
import eki.common.util.CodeGenerator;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.ActivityLogService;

@Component
public class DatasetImporterRunner extends AbstractLoaderCommons {

	private static Logger logger = LoggerFactory.getLogger(DatasetImporterRunner.class);

	private static final String DATASET_TYPE_COMPENSATION = DatasetType.TERM.name();

	private static final String IMPORT_PK_MAP = "temp_ds_import_pk_map";

	private static final String IMPORT_QUEUE = "temp_ds_import_queue";

	private static final String SQL_SELECT_MAPPED_PK = "select "
			+ "target_pk "
			+ "from " + IMPORT_PK_MAP + " "
			+ "where "
			+ "import_code = :importCode "
			+ "and table_name = :pkTableName "
			+ "and source_pk = :pkValue";

	private static final String EKI_LINK_TEXT_FRAGMENT = "<eki-link";

	private static final String[] TABLE_NAMES_THAT_PREREQUISIT_PARENT_DATA = new String[] {
			DEFINITION, LEXEME_RELATION, MEANING_RELATION, WORD_RELATION, WORD_ETYMOLOGY_RELATION
	};

	private static final String COMBINED_ENTRY_NAME = "everything";

	@Autowired
	private TransportService transportService;

	@Autowired
	private ActivityLogService activityLogService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(boolean isCreate, boolean isAppend, String importFilePath) throws Exception {

		String importCode = CodeGenerator.generateUniqueId();
		Context context = new Context(isCreate, isAppend, importCode);
		createSecurityContext();

		logger.info("Starting import \"{}\" from \"{}\"", importCode, importFilePath);

		File zippedImportFile = new File(importFilePath);
		ZipFile zipFile = new ZipFile(zippedImportFile);
		ObjectMapper objectMapper = new ObjectMapper();
		ZipEntry zipEntry;
		InputStream zipEntryStream;
		BufferedInputStream jsonInputStream;
		Object rootData;
		long t1, t2;

		List<String> importEntryNames = new ArrayList<>(transportService.getRootTables());
		importEntryNames.add(COMBINED_ENTRY_NAME);
		importEntryNames = importEntryNames.stream().map(importEntryName -> importEntryName + ".json").collect(Collectors.toList());

		for (String importEntryName : importEntryNames) {
			zipEntry = zipFile.getEntry(importEntryName);
			if (zipEntry != null) {
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
				logger.info("Current record count {}", context.getCreatedRecordCount().getValue());
				jsonInputStream.close();
				zipEntryStream.close();
			}
		}
		zipFile.close();

		resolveQueue(context);
		createActivityLogs(context);

		logger.info("In total created {} records", context.getCreatedRecordCount().getValue());
		logger.info("Recursively resolved {} records", context.getRecursivelyResolvedRecordCount().getValue());
		logger.info("Remaining unresolved {} records", context.getUnresolvedRecordCount().getValue());
		logger.info("Resolved {} meaning links", context.getResolvedMeaningLinkCount().getValue());
		logger.info("Unresolved {} meaning links", context.getUnresolvedMeaningLinkCount().getValue());
		logger.info("Ignored tables: {}", context.getIgnoredTableNames());

		logger.info("Done with import");
	}

	private void createSecurityContext() {

		EkiUser user = new EkiUser();
		user.setName("Sõnakogude laadur");
		user.setAdmin(true);
		user.setEnabled(Boolean.TRUE);

		GrantedAuthority authority = new SimpleGrantedAuthority("import");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("dsimp", user, Arrays.asList(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void extractRoot(Context context, Object rootData) throws Exception {

		@SuppressWarnings("unchecked")
		Map<String, Object> rootDataMap = (Map<String, Object>) rootData;
		for (String tableName : rootDataMap.keySet()) {
			Object data = rootDataMap.get(tableName);
			extractTablesData(context, tableName, data, null, new ArrayList<>());
		}
	}

	private void extractTablesData(Context context, String tableName, Object data, Long queueId, List<Step> hierarchy) throws Exception {

		if (data == null) {
			return;
		}
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) data;
			saveTablesData(context, tableName, dataMap, queueId, hierarchy);
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
			Iterator<Map<String, Object>> dataListIter = dataList.iterator();
			while (dataListIter.hasNext()) {
				Map<String, Object> dataMap = dataListIter.next();
				saveTablesData(context, tableName, dataMap, queueId, new ArrayList<>(hierarchy));
				dataListIter.remove();
			}
		} else {
			throw new DataLoadingException("Unknown data type: " + data.getClass());
		}
	}

	private void saveTablesData(Context context, String tableName, Map<String, Object> dataMap, Long queueId, List<Step> hierarchy) throws Exception {

		List<String> supportedTableNames = transportService.getImportTableNames();
		boolean forceToQueue = ArrayUtils.contains(TABLE_NAMES_THAT_PREREQUISIT_PARENT_DATA, tableName) && (queueId == null);
		if (!supportedTableNames.contains(tableName)) {
			return;
		} else if (StringUtils.equalsIgnoreCase(DATASET, tableName)) {
			dataMap = compensateFieldsAndData(context, DATASET, dataMap);
			handleDataset(context, dataMap);
		} else if (forceToQueue) {
			createQueue(context.getImportCode(), tableName, dataMap);
			return;
		} else {
			handleCurrentData(context, tableName, dataMap, queueId, hierarchy);
		}
		handleReferringData(context, tableName, dataMap, hierarchy);
		handleNestedData(context, tableName, dataMap, hierarchy);
	}

	private void handleDataset(Context context, Map<String, Object> dataMap) throws Exception {
		String code = dataMap.get("code").toString();
		boolean recordExists = recordExists(DATASET, "code", code);
		if (recordExists) {
			if (context.isCreate()) {
				throw new DataLoadingException("Can't create dataset that already exists");
			}
		} else {
			createDataset(context, dataMap);
		}
	}

	private void handleCurrentData(Context context, String tableName, Map<String, Object> dataMap, Long queueId, List<Step> hierarchy) throws Exception {

		boolean isAppend = context.isAppend();
		String importCode = context.getImportCode();

		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForImport().get(tableName);
		List<ForeignKey> referringForeignKeys = transportService.getReferringForeignKeysMapForImport().get(tableName);
		boolean queueExists = queueId != null;

		FkReassignResult fkReassignResult = reassignFks(importCode, dataMap, tableColumnsMap, context);
		if (fkReassignResult.isIgnore()) {
			List<String> ignoredTableNames = context.getIgnoredTableNames();
			if (!ignoredTableNames.contains(tableName)) {
				ignoredTableNames.add(tableName);
			}
		} else if (fkReassignResult.isSuccess()) {
			Map<String, Object> reassignedDataMap = fkReassignResult.getReassignedDataMap();
			Map<String, Object> truncDataMap = truncate(reassignedDataMap, tableColumnsMap);
			Long targetId = null;
			// default behaviour - if there are no referrers, it is assumed the record is meant to be created
			if (CollectionUtils.isEmpty(referringForeignKeys)) {
				if (StringUtils.equalsIgnoreCase(tableName, DEFINITION_DATASET)) {
					basicDbService.createWithoutId(tableName, truncDataMap);
				} else {
					targetId = basicDbService.create(tableName, truncDataMap);
					collectLoggingId(context, targetId, tableName);
				}
				context.getCreatedRecordCount().increment();
			} else {
				Long sourceId = getPk(dataMap, tableColumnsMap);
				boolean isCreate = false;
				if (isAppend) {
					boolean recordExists = recordExists(tableName, "id", sourceId);
					isCreate = !recordExists;
				} else {
					isCreate = true;
				}
				if (isCreate) {
					if (MapUtils.isEmpty(truncDataMap)) {
						targetId = basicDbService.create(tableName);
					} else {
						truncDataMap = compensateFieldsAndData(context, tableName, truncDataMap);
						targetId = basicDbService.create(tableName, truncDataMap);
					}
					collectLoggingId(context, targetId, tableName);
					context.getCreatedRecordCount().increment();
				} else {
					targetId = Long.valueOf(sourceId);
				}
				List<Object> pkMapping = getPkMapping(importCode, tableName, sourceId);
				if (CollectionUtils.isEmpty(pkMapping)) {
					createPkMap(importCode, tableName, sourceId, targetId);
				}
			}
			addToHierarchy(tableName, targetId, reassignedDataMap, hierarchy);
			if (queueExists) {
				deleteQueue(queueId);
				context.getRecursivelyResolvedRecordCount().increment();
			}
		} else if (fkReassignResult.isMissingReference()) {
			if (!queueExists) {
				createQueue(importCode, tableName, dataMap);
			}
		}
	}

	private void collectLoggingId(Context context, Long recordId, String tableName) {
		if (StringUtils.equalsIgnoreCase(tableName, WORD)) {
			context.getLoggingWordIds().add(recordId);
		} else if (StringUtils.equalsIgnoreCase(tableName, LEXEME)) {
			context.getLoggingLexemeIds().add(recordId);
		} else if (StringUtils.equalsIgnoreCase(tableName, MEANING)) {
			context.getLoggingMeaningIds().add(recordId);
		}
	}

	private void addToHierarchy(String tableName, Long id, Map<String, Object> dataMap, List<Step> hierarchy) {
		Step step = new Step();
		step.setTableName(tableName);
		step.setId(id);
		step.setDataMap(new HashMap<>(dataMap));
		hierarchy.add(step);
	}

	private void handleReferringData(Context context, String tableName, Map<String, Object> dataMap, List<Step> hierarchy) throws Exception {

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
			extractTablesData(context, referringTableName, referringData, null, new ArrayList<>(hierarchy));
		}
	}

	private void handleNestedData(Context context, String tableName, Map<String, Object> dataMap, List<Step> hierarchy) throws Exception {

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
				extractTablesData(context, dataColumnName, dataColumnValue, null, new ArrayList<>(hierarchy));
			}
		}
	}

	private void resolveQueue(Context context) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("import_code", context.getImportCode());
		List<Map<String, Object>> queueResults = basicDbService.selectAll(IMPORT_QUEUE, paramMap);
		if (CollectionUtils.isEmpty(queueResults)) {
			logger.info("Clean import, no leftovers. Good!");
			deleteCurrentPkMap(context);
			return;
		}
		List<String> unresolvedTableNames = queueResults.stream().map(queueRow -> (String) queueRow.get("table_name")).distinct().collect(Collectors.toList());
		List<Long> queueIds = queueResults.stream().map(queueRow -> (Long) queueRow.get("id")).sorted().collect(Collectors.toList());
		String queueSignature = queueIds.toString();
		int queueBatchSize = queueResults.size();

		if (StringUtils.equals(queueSignature, context.getRecentQueueSignature())) {
			logger.info("There are still {} records that could not be imported. Interrupting recursion", queueBatchSize);
			logger.info("Remaining unimported data is in tables: {}", unresolvedTableNames);
			deleteCurrentPkMap(context);
			deleteCurrentQueue(context);
			return;
		}

		logger.info("Attempting to resolve {} queue batch", queueBatchSize);
		logger.info("Queue data is in tables: {}", unresolvedTableNames);
		context.setRecentQueueSignature(queueSignature);

		ObjectMapper objectMapper = new ObjectMapper();

		long queueRowCounter = 0;
		long progressIndicator = queueBatchSize / Math.min(queueBatchSize, 100);

		for (Map<String, Object> queueRow : queueResults) {

			Long queueId = (Long) queueRow.get("id");
			String tableName = (String) queueRow.get("table_name");
			String content = (String) queueRow.get("content");
			Object data = objectMapper.readValue(content, Object.class);
			extractTablesData(context, tableName, data, queueId, new ArrayList<>());

			// progress
			queueRowCounter++;
			if (queueRowCounter % progressIndicator == 0) {
				long progressPercent = queueRowCounter / progressIndicator;
				logger.debug("{}% - {} queue rows iterated", progressPercent, queueRowCounter);
			}
		}

		resolveQueue(context);
	}

	private void createActivityLogs(Context context) throws Exception {

		final String functName = "importDataset";
		List<Long> loggingWordIds = context.getLoggingWordIds();
		List<Long> loggingLexemeIds = context.getLoggingLexemeIds();
		List<Long> loggingMeaningIds = context.getLoggingMeaningIds();

		logger.info("Creating activity logs for {} words, {} lexemes, {} meanings ...", loggingWordIds.size(), loggingLexemeIds.size(), loggingMeaningIds.size());

		createActivityLogs(functName, loggingLexemeIds, ActivityOwner.LEXEME);
		createActivityLogs(functName, loggingWordIds, ActivityOwner.WORD);
		createActivityLogs(functName, loggingMeaningIds, ActivityOwner.MEANING);

		logger.info("Activity logs created");
	}

	private void createActivityLogs(String functName, List<Long> ownerIds, ActivityOwner ownerName) throws Exception {
		for (Long ownerId : ownerIds) {
			activityLogService.createActivityLog(functName, ownerId, ownerName, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}
	}

	// not to be used on composite pk-s!
	private Long getPk(Map<String, Object> dataMap, Map<String, TableColumn> tableColumnsMap) {
		Long sourceId = dataMap.entrySet().stream().filter(entry -> {
			String dataColumnName = entry.getKey();
			TableColumn tableColumn = tableColumnsMap.get(dataColumnName);
			if (tableColumn == null) {
				return false;
			}
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

	private FkReassignResult reassignFks(String importCode, Map<String, Object> dataMap, Map<String, TableColumn> tableColumnsMap, Context context) throws Exception {

		boolean isCreate = context.isCreate();

		Map<String, Map<String, List<TableColumn>>> fkColumnsMap = tableColumnsMap.values().stream()
				.filter(tableColumn -> StringUtils.isNotBlank(tableColumn.getFkTableName()))
				.filter(tableColumn -> dataMap.get(tableColumn.getColumnName()) != null)
				.collect(Collectors.groupingBy(TableColumn::getFkTableName, Collectors.groupingBy(TableColumn::getFkColumnName)));

		Map<String, Object> reassignedDataMap = new HashMap<>(dataMap);
		boolean fkReassignSuccess = false;
		boolean missingReference = false;
		boolean ignore = false;

		if (MapUtils.isEmpty(fkColumnsMap)) {
			fkReassignSuccess = true;
		} else {
			List<String> supportedTableNames = transportService.getImportTableNames();
			Map<String, Object> tempFkMap = new HashMap<>();
			for (Entry<String, Map<String, List<TableColumn>>> fkCompositeMapEntry : fkColumnsMap.entrySet()) {
				String fkTableName = fkCompositeMapEntry.getKey();
				Map<String, List<TableColumn>> fkCompositeMap = fkCompositeMapEntry.getValue();
				if (fkCompositeMap.size() == 1) {
					//single
					List<TableColumn> singleFkColumns = fkCompositeMap.values().iterator().next();
					for (TableColumn tableFkColumn : singleFkColumns) {
						String fkColumnName = tableFkColumn.getFkColumnName();
						boolean isNotNullable = !tableFkColumn.isNullable();
						String columnName = tableFkColumn.getColumnName();
						Object columnValue = dataMap.get(columnName);
						if (!isNotNullable && (columnValue == null)) {
							String tableName = tableFkColumn.getTableName();
							throw new DataLoadingException("Not nullable field is empty: " + tableName + "." + columnName);
						}
						if (!supportedTableNames.contains(fkTableName)) {
							boolean recordExists = recordExists(fkTableName, fkColumnName, columnValue);
							if (recordExists) {
								tempFkMap.put(columnName, columnValue);
							} else if (isNotNullable) {
								throw new DataLoadingException("Missing not nullable reference: " + fkTableName + "." + fkColumnName + "=" + columnValue);
								//set ignore instead if want to skip unavailable dependencies
								//ignore = true;
							} else {
								ignore = true;
							}
						} else if (StringUtils.equalsIgnoreCase(DATASET, fkTableName)) {
							tempFkMap.put(columnName, columnValue);
						} else {
							List<Object> pkMapping = getPkMapping(importCode, fkTableName, columnValue);
							if (CollectionUtils.isEmpty(pkMapping)) {
								if (isCreate) {
									missingReference = true;
								} else {
									boolean recordExists = recordExists(fkTableName, fkColumnName, columnValue);
									if (recordExists) {
										tempFkMap.put(columnName, columnValue);
									} else {
										missingReference = true;
									}
								}
							} else {
								tempFkMap.put(columnName, pkMapping.get(0));
							}
						}
					}
				} else {
					//composite
					List<TableColumn> compositeFkColumns = new ArrayList<>();
					fkCompositeMap.values().forEach(fkTableColumns -> compositeFkColumns.addAll(fkTableColumns));
					boolean recordExists = recordExists(compositeFkColumns, dataMap);
					boolean isNotNullable = compositeFkColumns.stream().anyMatch(tableColumn -> !tableColumn.isNullable());
					if (!supportedTableNames.contains(fkTableName)) {
						if (recordExists) {
							for (TableColumn tableFkColumn : compositeFkColumns) {
								String columnName = tableFkColumn.getColumnName();
								Object columnValue = dataMap.get(columnName);
								tempFkMap.put(columnName, columnValue);
							}
						} else if (isNotNullable) {
							List<String> fkColumnValues = compositeFkColumns.stream().map(tableFkColumn -> {
								String fkColumnName = tableFkColumn.getFkColumnName();
								String columnName = tableFkColumn.getColumnName();
								Object columnValue = dataMap.get(columnName);
								return fkColumnName + "=" + columnValue;
							}).collect(Collectors.toList());
							throw new DataLoadingException("Missing not nullable reference: " + fkTableName + "." + fkColumnValues);
						} else {
							ignore = true;
						}
					} else {
						throw new DataLoadingException("Unable to handle composite fk mapping");
					}
				}
			}
			fkReassignSuccess = !missingReference && !ignore;
			if (fkReassignSuccess) {
				tempFkMap.forEach((columnName, columnValue) -> reassignedDataMap.put(columnName, columnValue));
			}
		}
		FkReassignResult fkReassignResult = new FkReassignResult();
		fkReassignResult.setSuccess(fkReassignSuccess);
		fkReassignResult.setMissingReference(missingReference);
		fkReassignResult.setIgnore(ignore);
		fkReassignResult.setReassignedDataMap(reassignedDataMap);
		return fkReassignResult;
	}

	private Map<String, Object> compensateFieldsAndData(Context context, String tableName, Map<String, Object> dataMap) throws Exception {

		Map<String, TableColumn> tableColumnsMap = transportService.getTablesColumnsMapForImport().get(tableName);

		Map<String, Object> dataMapCopy = new HashMap<>(dataMap);
		if (StringUtils.equalsIgnoreCase(DATASET, tableName)) {
			if (!dataMapCopy.containsKey("type") && tableColumnsMap.containsKey("type")) {
				dataMapCopy.put("type", DATASET_TYPE_COMPENSATION);
			}
		} else if (StringUtils.equalsIgnoreCase(DEFINITION, tableName)) {
			String definitionValuePrese = (String) dataMap.get("value_prese");
			if (StringUtils.contains(definitionValuePrese, EKI_LINK_TEXT_FRAGMENT)) {
				definitionValuePrese = handleContainedMeaningLinks(context, definitionValuePrese);
				dataMapCopy.put("value_prese", definitionValuePrese);
			}
		}
		return dataMapCopy;
	}

	private String handleContainedMeaningLinks(Context context, String valuePrese) throws Exception {

		String importCode = context.getImportCode();
		Document valueHtmlDoc = Jsoup.parse(valuePrese);
		Elements ekiMeaningLinks = valueHtmlDoc.select("eki-link[link-type='meaning_link']");
		for (Element ekiMeaningLink : ekiMeaningLinks) {
			String sourceMeaningIdStr = ekiMeaningLink.attr("link-id");
			if (StringUtils.isBlank(sourceMeaningIdStr)) {
				continue;
			}
			Long sourceMeaningId = Long.valueOf(sourceMeaningIdStr);
			String targetMeaningIdStr;
			List<Object> pkMapping = getPkMapping(importCode, MEANING, sourceMeaningId);
			if (CollectionUtils.isEmpty(pkMapping)) {
				targetMeaningIdStr = new String(sourceMeaningIdStr);
				boolean recordExists = recordExists(MEANING, "id", sourceMeaningId);
				if (!recordExists) {
					context.getUnresolvedMeaningLinkCount().increment();
				}
			} else {
				targetMeaningIdStr = pkMapping.get(0).toString();
				context.getResolvedMeaningLinkCount().increment();
			}
			ekiMeaningLink.attr("link-id", targetMeaningIdStr);
		}
		valuePrese = valueHtmlDoc.body().html();
		return valuePrese;
	}

	private boolean recordExists(List<TableColumn> tableFkColumns, Map<String, Object> dataMap) throws Exception {
		TableColumn tableFirstFkColumn = tableFkColumns.get(0);
		String fkTableName = tableFirstFkColumn.getFkTableName();
		Map<String, Object> fkRecordParamMap = new HashMap<>();
		for (TableColumn tableFkColumn : tableFkColumns) {
			String columnName = tableFkColumn.getColumnName();
			String fkColumnName = tableFkColumn.getFkColumnName();
			Object columnValue = dataMap.get(columnName);
			fkRecordParamMap.put(fkColumnName, columnValue);
		}
		Map<String, Object> result = basicDbService.select(fkTableName, fkRecordParamMap);
		return MapUtils.isNotEmpty(result);
	}

	private boolean recordExists(String tableName, String idFieldName, Object id) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(idFieldName, id);
		Map<String, Object> result = basicDbService.select(tableName, paramMap);
		return MapUtils.isNotEmpty(result);
	}

	private List<Object> getPkMapping(String importCode, String pkTableName, Object pkValue) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("importCode", importCode);
		paramMap.put("pkTableName", pkTableName);
		paramMap.put("pkValue", pkValue);
		List<Object> results = basicDbService.queryList(SQL_SELECT_MAPPED_PK, paramMap, Object.class);
		return results;
	}

	private void createDataset(Context context, Map<String, Object> dataMap) throws Exception {
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

	private void deleteCurrentPkMap(Context context) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("importCode", context.getImportCode());

		String sql = "delete from " + IMPORT_PK_MAP + " where import_code = :importCode";
		int rowCount = basicDbService.executeScript(sql, paramMap);
		logger.info("Deleted {} rows from {}", rowCount, IMPORT_PK_MAP);
	}

	private void deleteCurrentQueue(Context context) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("importCode", context.getImportCode());

		String sql = "delete from " + IMPORT_QUEUE + " where import_code = :importCode";
		int rowCount = basicDbService.executeScript(sql, paramMap);
		logger.info("Deleted {} rows from {}", rowCount, IMPORT_QUEUE);
		context.getUnresolvedRecordCount().increment(rowCount);
	}

	class FkReassignResult extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private boolean success;

		private boolean missingReference;

		private boolean ignore;

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

		public boolean isIgnore() {
			return ignore;
		}

		public void setIgnore(boolean ignore) {
			this.ignore = ignore;
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

		private boolean create;

		private boolean append;

		private String importCode;

		private List<Long> loggingWordIds;

		private List<Long> loggingLexemeIds;

		private List<Long> loggingMeaningIds;

		private Count createdRecordCount;

		private Count recursivelyResolvedRecordCount;

		private Count unresolvedRecordCount;

		private Count resolvedMeaningLinkCount;

		private Count unresolvedMeaningLinkCount;

		private String recentQueueSignature;

		private List<String> ignoredTableNames;

		public Context(boolean create, boolean append, String importCode) {
			this.create = create;
			this.append = append;
			this.importCode = importCode;
			this.loggingWordIds = new ArrayList<Long>();
			this.loggingLexemeIds = new ArrayList<Long>();
			this.loggingMeaningIds = new ArrayList<Long>();
			this.createdRecordCount = new Count();
			this.recursivelyResolvedRecordCount = new Count();
			this.unresolvedRecordCount = new Count();
			this.resolvedMeaningLinkCount = new Count();
			this.unresolvedMeaningLinkCount = new Count();
			this.recentQueueSignature = null;
			this.ignoredTableNames = new ArrayList<>();
		}

		public boolean isCreate() {
			return create;
		}

		public boolean isAppend() {
			return append;
		}

		public String getImportCode() {
			return importCode;
		}

		public List<Long> getLoggingWordIds() {
			return loggingWordIds;
		}

		public List<Long> getLoggingLexemeIds() {
			return loggingLexemeIds;
		}

		public List<Long> getLoggingMeaningIds() {
			return loggingMeaningIds;
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

		public Count getResolvedMeaningLinkCount() {
			return resolvedMeaningLinkCount;
		}

		public Count getUnresolvedMeaningLinkCount() {
			return unresolvedMeaningLinkCount;
		}

		public void setRecentQueueSignature(String recentQueueSignature) {
			this.recentQueueSignature = recentQueueSignature;
		}

		public String getRecentQueueSignature() {
			return recentQueueSignature;
		}

		public List<String> getIgnoredTableNames() {
			return ignoredTableNames;
		}
	}

	class Step extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String tableName;

		private Long id;

		private Map<String, Object> dataMap;

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Map<String, Object> getDataMap() {
			return dataMap;
		}

		public void setDataMap(Map<String, Object> dataMap) {
			this.dataMap = dataMap;
		}

	}
}
