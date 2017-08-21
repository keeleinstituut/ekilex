package eki.ekilex.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;

@Component
public class TestEnvInitialiser {

	@Autowired
	private BasicDbService basicDbService;

	public void initDatabase() throws Exception {

		final String createTablesScriptFilePath = "./fileresources/sql/create_tables.sql";
		final String testDataScriptFilePath = "./fileresources/sql/test_data.sql";

		executeScriptFile(createTablesScriptFilePath);
		executeScriptFile(testDataScriptFilePath);
	}

	public void executeScriptFile(String scriptFilePath) throws Exception {

		File scriptFile = new File(scriptFilePath);
		if (!scriptFile.exists()) {
			throw new FileNotFoundException("Could not find specified database script: " + scriptFilePath);
		}
		FileInputStream scriptFileInputStream = new FileInputStream(scriptFile);
		String scriptFileContent = IOUtils.toString(scriptFileInputStream, SystemConstant.UTF_8);
		scriptFileInputStream.close();
		basicDbService.executeScript(scriptFileContent);
	}
}
