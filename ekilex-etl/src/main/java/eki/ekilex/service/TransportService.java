package eki.ekilex.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.ekilex.data.transport.ForeignKey;
import eki.ekilex.data.transport.OrderedMap;
import eki.ekilex.data.transport.SimpleTableColumn;
import eki.ekilex.data.transport.TableColumn;
import eki.ekilex.data.util.ForeignKeyRowMapper;
import eki.ekilex.data.util.SimpleTableColumnRowMapper;
import eki.ekilex.data.util.TableColumnRowMapper;
import eki.ekilex.runner.AbstractLoaderCommons;

@Component
public class TransportService extends AbstractLoaderCommons implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(TransportService.class);

	private static final String SQL_SELECT_TABLES_COLUMNS = "sql/select_tables_columns.sql";

	private static final String SQL_SELECT_TABLES_FOREIGN_KEYS = "sql/select_tables_foreign_keys.sql";

	private static final String SQL_SELECT_UNIQUE_CONSTRAINT_TABLES_COLUMNS = "sql/select_unique_constr_tables_columns.sql";

	private String sqlSelectTablesColumns;

	private String sqlSelectTablesForeignKeys;

	private String sqlSelectUniqueConstraintTableColumns;

	private List<String> supportedTableNames;

	private Map<String, Map<String, TableColumn>> tablesColumnsMapForExport;

	private Map<String, Map<String, TableColumn>> tablesColumnsMapForImport;

	private Map<String, List<ForeignKey>> referringForeignKeysMap;

	private Map<String, List<String>> uniqueConstraintsColumnsMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_TABLES_COLUMNS);
		sqlSelectTablesColumns = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_TABLES_FOREIGN_KEYS);
		sqlSelectTablesForeignKeys = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_UNIQUE_CONSTRAINT_TABLES_COLUMNS);
		sqlSelectUniqueConstraintTableColumns = getContent(resourceFileInputStream);
	}

	@Transactional
	public void initialize() throws Exception {

		logger.debug("Loading tables descriptions...");

		String[] supportedTableNamesArr = new String[] {
				DATASET,
				MEANING, MEANING_FREEFORM, MEANING_NR, MEANING_DOMAIN, MEANING_RELATION, MEANING_LIFECYCLE_LOG, MEANING_PROCESS_LOG,
				WORD, WORD_GUID, WORD_WORD_TYPE, WORD_ETYMOLOGY, WORD_ETYMOLOGY_RELATION, WORD_ETYMOLOGY_SOURCE_LINK, WORD_RELATION,
				WORD_GROUP_MEMBER, WORD_GROUP, WORD_LIFECYCLE_LOG, WORD_PROCESS_LOG,
				LEXEME, LEXEME_FREEFORM, LEXEME_POS, LEXEME_DERIV, LEXEME_REGISTER, LEXEME_REGION, LEXEME_FREQUENCY, LEXEME_RELATION,
				LEXEME_SOURCE_LINK, LEXEME_LIFECYCLE_LOG, LEXEME_PROCESS_LOG, LEX_COLLOC, LEX_COLLOC_POS_GROUP, LEX_COLLOC_REL_GROUP,
				DEFINITION, DEFINITION_FREEFORM, DEFINITION_SOURCE_LINK, DEFINITION_DATASET,
				COLLOCATION, COLLOCATION_FREEFORM, PARADIGM, FORM, FORM_FREQUENCY, 
				LIFECYCLE_LOG, PROCESS_LOG, PROCESS_LOG_SOURCE_LINK,
				FREEFORM, FREEFORM_SOURCE_LINK,
				SOURCE, SOURCE_FREEFORM};
		supportedTableNamesArr = toLowerCase(supportedTableNamesArr);
		this.supportedTableNames = Arrays.asList(supportedTableNamesArr);

		Map<String, Object> paramMap = new HashMap<>();

		paramMap.clear();
		paramMap.put("constraintTypePk", "PRIMARY KEY");
		paramMap.put("constraintTypeFk", "FOREIGN KEY");
		paramMap.put("tableNames", supportedTableNames);
		List<TableColumn> allTablesColumns = basicDbService.getResults(sqlSelectTablesColumns, paramMap, new TableColumnRowMapper());

		// use composeFullTableColumnName(TABLE_NAME, "column_name") when necessary
		String[] ignoreColumnNamesForExport = new String[] {};
		String[] ignoreColumnNamesForImport = new String[] {};
		ignoreColumnNamesForExport = toLowerCase(ignoreColumnNamesForExport);
		this.tablesColumnsMapForExport = mapTablesColumns(allTablesColumns, Arrays.asList(ignoreColumnNamesForExport));
		this.tablesColumnsMapForImport = mapTablesColumns(allTablesColumns, Arrays.asList(ignoreColumnNamesForImport));

		paramMap.clear();
		paramMap.put("constraintTypeFk", "FOREIGN KEY");
		// add when necessary
		//paramMap.put("ignoreFks", Arrays.asList(ignoreForeignKeys));
		paramMap.put("tableNames", supportedTableNames);
		List<ForeignKey> allForeignKeys = basicDbService.getResults(sqlSelectTablesForeignKeys, paramMap, new ForeignKeyRowMapper());
		this.referringForeignKeysMap = mapTablesForeignKeys(allForeignKeys);

		paramMap.clear();
		paramMap.put("constraintTypeUnique", "UNIQUE");
		paramMap.put("tableNames", supportedTableNames);
		List<SimpleTableColumn> uniqueConstraintTablesColumns = basicDbService.getResults(sqlSelectUniqueConstraintTableColumns, paramMap, new SimpleTableColumnRowMapper());
		this.uniqueConstraintsColumnsMap = mapTablesUniqueConstraintsColumns(uniqueConstraintTablesColumns);

		logger.debug("...done");
	}

	public List<String> getSupportedTableNames() {
		return supportedTableNames;
	}

	public Map<String, Map<String, TableColumn>> getTablesColumnsMapForExport() {
		return tablesColumnsMapForExport;
	}

	public Map<String, Map<String, TableColumn>> getTablesColumnsMapForImport() {
		return tablesColumnsMapForImport;
	}

	public Map<String, List<ForeignKey>> getReferringForeignKeysMap() {
		return referringForeignKeysMap;
	}

	public Map<String, List<String>> getUniqueConstraintsColumnsMap() {
		return uniqueConstraintsColumnsMap;
	}

	private Map<String, Map<String, TableColumn>> mapTablesColumns(List<TableColumn> tablesColumns, List<String> ignoreColumnNames) {

		Map<String, Map<String, TableColumn>> tablesColumnsMap = new HashMap<String, Map<String, TableColumn>>();
		for (TableColumn tableColumn : tablesColumns) {
			String tableName = tableColumn.getTableName();
			String columnName = tableColumn.getColumnName();
			Map<String, TableColumn> tableColumnsMap = tablesColumnsMap.get(tableName);
			if (tableColumnsMap == null) {
				tableColumnsMap = new OrderedMap<String, TableColumn>();
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

		Map<String, List<ForeignKey>> tablesForeignKeysMap = new HashMap<String, List<ForeignKey>>();
		for (ForeignKey foreignKey : tablesForeignKeys) {
			String pkTableName = foreignKey.getPkTableName();
			List<ForeignKey> tableForeignKeys = tablesForeignKeysMap.get(pkTableName);
			if (tableForeignKeys == null) {
				tableForeignKeys = new ArrayList<ForeignKey>();
				tablesForeignKeysMap.put(pkTableName, tableForeignKeys);
			}
			tableForeignKeys.add(foreignKey);
		}
		return tablesForeignKeysMap;
	}

	private Map<String, List<String>> mapTablesUniqueConstraintsColumns(List<SimpleTableColumn> uniqueConstraintTablesColumns) {

		Map<String, List<String>> tablesUniqueConstraintsColumnsMap = new HashMap<>();
		List<String> columnNames;
		for (SimpleTableColumn uniqueConstraintTableColumn : uniqueConstraintTablesColumns) {
			String tableName = uniqueConstraintTableColumn.getTableName();
			String columnName = uniqueConstraintTableColumn.getColumnName();
			columnNames = tablesUniqueConstraintsColumnsMap.get(tableName);
			if (columnNames == null) {
				columnNames = new ArrayList<>();
				tablesUniqueConstraintsColumnsMap.put(tableName, columnNames);
			}
			columnNames.add(columnName);
		}
		return tablesUniqueConstraintsColumnsMap;
	}

	public String composeFullTableColumnName(String tableName, String columnName) {
		return tableName + "." + columnName;
	}

	public String[] toLowerCase(String... values) {
		for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
			values[valueIndex] = values[valueIndex].toLowerCase();
		}
		return values;
	}
}
