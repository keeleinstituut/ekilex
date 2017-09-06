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

		final String scriptFilePath1 = "./fileresources/sql/create_tables.sql";
		final String scriptFilePath2 = "./fileresources/sql/classifier_data_manual.sql";
		final String scriptFilePath3 = "./fileresources/sql/classifier_data_autom.sql";
		final String scriptFilePath4 = "./fileresources/sql/test_data.sql";

		executeSqlScriptFile(scriptFilePath1);
		executeSqlScriptFile(scriptFilePath2);
		executeSqlScriptFile(scriptFilePath3);
		executeSqlScriptFile(scriptFilePath4);
	}

	public String getSqlScript(String sqlScriptFilePath) throws Exception {

		File sqlScriptFile = new File(sqlScriptFilePath);
		if (!sqlScriptFile.exists()) {
			throw new FileNotFoundException("Could not find specified database script: " + sqlScriptFilePath);
		}
		FileInputStream sqlScriptFileInputStream = new FileInputStream(sqlScriptFile);
		String sqlScriptFileContent = IOUtils.toString(sqlScriptFileInputStream, SystemConstant.UTF_8);
		sqlScriptFileInputStream.close();
		return sqlScriptFileContent;
	}

	public void executeSqlScriptFile(String sqlScriptFilePath) throws Exception {

		String scriptFileContent = getSqlScript(sqlScriptFilePath);
		basicDbService.executeScript(scriptFileContent);
	}

}
