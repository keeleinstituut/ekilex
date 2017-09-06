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

		executeScriptFile(scriptFilePath1);
		executeScriptFile(scriptFilePath2);
		executeScriptFile(scriptFilePath3);
		executeScriptFile(scriptFilePath4);
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
