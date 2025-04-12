package eki.ekilex.service.cli;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.common.data.OrderedMap;
import eki.common.data.transport.ForeignKey;
import eki.common.data.transport.TableColumn;
import eki.common.data.util.ForeignKeyRowMapper;
import eki.common.data.util.TableColumnRowMapper;
import eki.ekilex.service.AbstractLoaderCommons;

// DO NOT USE! OUTDATED!!
@Deprecated
@Component
public class TransportService extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(TransportService.class);

	private static final String SQL_SELECT_TABLES_COLUMNS = "sql/select_tables_columns.sql";

	private static final String SQL_SELECT_TABLES_FOREIGN_KEYS = "sql/select_tables_foreign_keys.sql";

	private String sqlSelectTablesColumns;

	private String sqlSelectTablesForeignKeys;

	private DateFormat timestampFormat;

	private List<String> rootTables;

	private List<String> exportTableNames;

	private List<String> importTableNamesMin;

	private List<String> importTableNamesMax;

	private Map<String, Map<String, TableColumn>> tablesColumnsMapForExport;

	private Map<String, Map<String, TableColumn>> tablesColumnsMapForImport;

	private Map<String, List<ForeignKey>> referringForeignKeysMapForExport;

	private Map<String, List<ForeignKey>> referringForeignKeysMapForImport;

	private Map<String, List<String>> referringTableNamesMapForImport;

	private Map<String, List<String>> uniqueConstraintsColumnsMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_TABLES_COLUMNS);
		sqlSelectTablesColumns = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_TABLES_FOREIGN_KEYS);
		sqlSelectTablesForeignKeys = getContent(resourceFileInputStream);

		timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Transactional
	public void initialise() throws Exception {

		String[] rootTableNamesArr = new String[] {DATASET, WORD, PARADIGM, MEANING, COLLOCATION, LEXEME};
		rootTableNamesArr = toLowerCase(rootTableNamesArr);
		rootTables = Arrays.asList(rootTableNamesArr);

		// TODO move all word_ from min to max

		this.importTableNamesMin = Arrays.asList(toLowerCase(
				DATASET, FREEFORM, WORD, WORD_ACTIVITY_LOG,
				MEANING, MEANING_FREEFORM, MEANING_NR, MEANING_DOMAIN, MEANING_RELATION, MEANING_ACTIVITY_LOG,
				LEXEME, LEXEME_FREEFORM, LEXEME_POS, LEXEME_DERIV, LEXEME_REGISTER, LEXEME_REGION, LEXEME_FREQUENCY, LEXEME_RELATION, LEXEME_SOURCE_LINK, LEXEME_TAG, LEXEME_ACTIVITY_LOG,
				DEFINITION, DEFINITION_FREEFORM, DEFINITION_SOURCE_LINK, DEFINITION_DATASET, FREEFORM_SOURCE_LINK));
		this.importTableNamesMax = new ArrayList<>(importTableNamesMin);
		this.importTableNamesMax.addAll(Arrays.asList(toLowerCase(
				WORD_WORD_TYPE, WORD_GUID, WORD_RELATION, WORD_GROUP_MEMBER, WORD_GROUP, WORD_FREEFORM, WORD_ETYMOLOGY, WORD_ETYMOLOGY_RELATION, WORD_ETYMOLOGY_SOURCE_LINK,
				COLLOCATION, LEX_COLLOC, LEX_COLLOC_POS_GROUP, LEX_COLLOC_REL_GROUP, PARADIGM, PARADIGM_FORM, FORM, FORM_FREQUENCY)));
		this.exportTableNames = new ArrayList<>(importTableNamesMax);
		this.exportTableNames.addAll(Arrays.asList(toLowerCase(SOURCE, SOURCE_FREEFORM, /*WORD_ACTIVITY_LOG, MEANING_ACTIVITY_LOG, LEXEME_ACTIVITY_LOG, */ ACTIVITY_LOG)));

		Map<String, Object> paramMap = new HashMap<>();

		paramMap.clear();
		paramMap.put("constraintTypePk", "PRIMARY KEY");
		paramMap.put("constraintTypeFk", "FOREIGN KEY");
		paramMap.put("tableNames", exportTableNames);
		List<TableColumn> allTablesColumns = basicDbService.getResults(sqlSelectTablesColumns, paramMap, new TableColumnRowMapper());

		// use composeFullTableColumnName(TABLE_NAME, "column_name") when necessary
		String[] ignoreColumnNamesForExport = new String[] {};
		String[] ignoreColumnNamesForImport = new String[] {};
		ignoreColumnNamesForExport = toLowerCase(ignoreColumnNamesForExport);
		ignoreColumnNamesForImport = toLowerCase(ignoreColumnNamesForImport);
		this.tablesColumnsMapForExport = mapTablesColumns(allTablesColumns, Arrays.asList(ignoreColumnNamesForExport));
		this.tablesColumnsMapForImport = mapTablesColumns(allTablesColumns, Arrays.asList(ignoreColumnNamesForImport));

		List<ForeignKey> foreignKeys;
		String[] ignoreForeignKeys = new String[] {"word2_id", "meaning2_id", "lexeme2_id"};
		paramMap.clear();
		paramMap.put("constraintTypeFk", "FOREIGN KEY");
		paramMap.put("ignoreFks", Arrays.asList(ignoreForeignKeys));
		paramMap.put("tableNames", exportTableNames);
		foreignKeys = basicDbService.getResults(sqlSelectTablesForeignKeys, paramMap, new ForeignKeyRowMapper());
		this.referringForeignKeysMapForExport = mapTablesForeignKeys(foreignKeys);

		paramMap.clear();
		paramMap.put("constraintTypeFk", "FOREIGN KEY");
		paramMap.put("ignoreFks", Arrays.asList("nothing"));
		paramMap.put("tableNames", importTableNamesMax);
		foreignKeys = basicDbService.getResults(sqlSelectTablesForeignKeys, paramMap, new ForeignKeyRowMapper());
		this.referringForeignKeysMapForImport = mapTablesForeignKeys(foreignKeys);
		this.referringTableNamesMapForImport = mapReferringTableNames(foreignKeys);

		logger.debug("Tables descriptions collected");
	}

	public String format(Timestamp timestamp) {
		return timestampFormat.format(timestamp);
	}

	public Timestamp parse(String timestamp) throws Exception {
		long timestampMs = timestampFormat.parse(timestamp).getTime();
		return new Timestamp(timestampMs);
	}

	public List<String> getRootTables() {
		return rootTables;
	}

	public List<String> getExportTableNames() {
		return exportTableNames;
	}

	public List<String> getImportTableNamesMin() {
		return importTableNamesMin;
	}

	public List<String> getImportTableNamesMax() {
		return importTableNamesMax;
	}

	public Map<String, Map<String, TableColumn>> getTablesColumnsMapForExport() {
		return tablesColumnsMapForExport;
	}

	public Map<String, Map<String, TableColumn>> getTablesColumnsMapForImport() {
		return tablesColumnsMapForImport;
	}

	public Map<String, List<ForeignKey>> getReferringForeignKeysMapForExport() {
		return referringForeignKeysMapForExport;
	}

	public Map<String, List<ForeignKey>> getReferringForeignKeysMapForImport() {
		return referringForeignKeysMapForImport;
	}

	public Map<String, List<String>> getReferringTableNamesMapForImport() {
		return referringTableNamesMapForImport;
	}

	public Map<String, List<String>> getUniqueConstraintsColumnsMap() {
		return uniqueConstraintsColumnsMap;
	}

	public List<String> getReferredTableNames(Map<String, TableColumn> tableColumnsMap) {
		return tableColumnsMap.values().stream()
				.filter(tableColumn -> StringUtils.isNotEmpty(tableColumn.getFkTableName()))
				.map(TableColumn::getFkTableName).collect(Collectors.toList());
	}

	private Map<String, Map<String, TableColumn>> mapTablesColumns(List<TableColumn> tablesColumns, List<String> ignoreColumnNames) {

		Map<String, Map<String, TableColumn>> tablesColumnsMap = new HashMap<>();
		for (TableColumn tableColumn : tablesColumns) {
			String tableName = tableColumn.getTableName();
			String columnName = tableColumn.getColumnName();
			Map<String, TableColumn> tableColumnsMap = tablesColumnsMap.get(tableName);
			if (tableColumnsMap == null) {
				tableColumnsMap = new OrderedMap<>();
				tablesColumnsMap.put(tableName, tableColumnsMap);
			}
			String fullTableColumnName = composeFullTableColumnName(tableName, columnName);
			if (ignoreColumnNames.contains(fullTableColumnName)) {
				continue;
			}
			tableColumnsMap.put(columnName, tableColumn);
		}
		return tablesColumnsMap;
	}

	private Map<String, List<ForeignKey>> mapTablesForeignKeys(List<ForeignKey> tablesForeignKeys) {

		Map<String, List<ForeignKey>> tablesForeignKeysMap = new HashMap<>();
		for (ForeignKey foreignKey : tablesForeignKeys) {
			String pkTableName = foreignKey.getPkTableName();
			List<ForeignKey> tableForeignKeys = tablesForeignKeysMap.get(pkTableName);
			if (tableForeignKeys == null) {
				tableForeignKeys = new ArrayList<>();
				tablesForeignKeysMap.put(pkTableName, tableForeignKeys);
			}
			tableForeignKeys.add(foreignKey);
		}
		return tablesForeignKeysMap;
	}

	private Map<String, List<String>> mapReferringTableNames(List<ForeignKey> tablesForeignKeys) {

		Map<String, List<String>> tablesForeignKeyTablesMap = new HashMap<>();
		for (ForeignKey foreignKey : tablesForeignKeys) {
			String pkTableName = foreignKey.getPkTableName();
			List<String> tableForeignKeyTables = tablesForeignKeyTablesMap.get(pkTableName);
			if (tableForeignKeyTables == null) {
				tableForeignKeyTables = new ArrayList<>();
				tablesForeignKeyTablesMap.put(pkTableName, tableForeignKeyTables);
			}
			String fkTableName = foreignKey.getFkTableName();
			if (!tableForeignKeyTables.contains(fkTableName)) {
				tableForeignKeyTables.add(fkTableName);
			}
		}
		return tablesForeignKeyTablesMap;
	}

	private String composeFullTableColumnName(String tableName, String columnName) {
		return tableName + "." + columnName;
	}

	private String[] toLowerCase(String... values) {
		for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
			values[valueIndex] = values[valueIndex].toLowerCase();
		}
		return values;
	}
}
