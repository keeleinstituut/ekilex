package eki.common.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.service.db.BasicDbService;

@Component
public class TestEnvInitialiser {

	@Autowired
	private BasicDbService basicDbService;

	public void initDatabase() throws Exception {

		initDatabaseWithoutTestData();
		executeSqlScriptFile("sql/test_data.sql");
	}

	//FIXME restore create views
	public void initDatabaseWithoutTestData() throws Exception {

		final String[] scriptFilePaths = new String[] {
				"sql/drop_all.sql",
				"sql/create_types.sql",
				"sql/create_tables.sql",
				"sql/create_indexes.sql",
				//"sql/create_views.sql",
				"sql/create_functions.sql",
				"sql/classifier-manual.sql",
				"sql/classifier-main.sql",
				"sql/classifier-domain.sql"
		};

		for (String scriptFilePath : scriptFilePaths) {
			executeSqlScriptFile(scriptFilePath);
		}
	}

	public String getSqlScript(String sqlScriptFilePath) throws Exception {

		File sqlScriptFile = new File(sqlScriptFilePath);
		if (!sqlScriptFile.exists()) {
			throw new FileNotFoundException("Could not find specified database script: " + sqlScriptFilePath);
		}
		try (FileInputStream sqlScriptFileInputStream = new FileInputStream(sqlScriptFile)) {
			String sqlScriptFileContent = IOUtils.toString(sqlScriptFileInputStream, StandardCharsets.UTF_8.name());
			return sqlScriptFileContent;
		}
	}

	public String getSqlScriptFromClasspath(String sqlScriptFilePath) throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		try (InputStream resourceFileInputStream = classLoader.getResourceAsStream(sqlScriptFilePath)) {
			String sqlScriptFileContent = IOUtils.toString(resourceFileInputStream, StandardCharsets.UTF_8.name());
			return sqlScriptFileContent;
		} catch (Exception e) {
			throw new FileNotFoundException("Could not find specified database script: " + sqlScriptFilePath);
		}
	}

	private void executeSqlScriptFile(String sqlScriptFilePath) throws Exception {

		String scriptFileContent = getSqlScriptFromClasspath(sqlScriptFilePath);
		basicDbService.executeScript(scriptFileContent);
	}

}
