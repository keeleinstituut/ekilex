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

		final String scriptFilePath4 = "sql/test_data.sql";

		initDatabaseWithoutTestData();
		executeSqlScriptFile(scriptFilePath4);
	}

	//FIXME restore create views
	public void initDatabaseWithoutTestData() throws Exception {

		final String scriptFilePath1 = "sql/drop_all.sql";
		final String scriptFilePath2 = "sql/create_tables.sql";
		// final String scriptFilePath3 = "sql/create_views.sql";
		final String scriptFilePath4 = "sql/create_functions.sql";
		final String scriptFilePath5 = "sql/classifier-manual.sql";
		final String scriptFilePath6 = "sql/classifier-main.sql";
		final String scriptFilePath7 = "sql/classifier-domain.sql";
		final String[] scriptFilePaths = new String[] {
				scriptFilePath1, scriptFilePath2, /*scriptFilePath3, */scriptFilePath4, scriptFilePath5, scriptFilePath6, scriptFilePath7
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
