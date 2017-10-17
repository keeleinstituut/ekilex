package eki.ekilex.service;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ReportComposer {

	private Map<String, FileOutputStream> reportStreamsMap;

	public ReportComposer(String reportGroupName, String... reportNames) throws Exception {

		reportGroupName = fileNameCleanup(reportGroupName);
		reportStreamsMap = new HashMap<>();
		for (String reportName : reportNames) {
			reportName = fileNameCleanup(reportName);
			String reportFilePath = "./" + reportGroupName + "-" + reportName + ".txt";
			FileOutputStream reportStream = new FileOutputStream(reportFilePath);
			reportStreamsMap.put(reportName, reportStream);
		}
	}

	private String fileNameCleanup(String value) {
		value = StringUtils.replaceChars(value, ' ', '_');
		value = StringUtils.replaceChars(value, '.', '_');
		value = StringUtils.replaceChars(value, '\'', '_');
		value = StringUtils.replaceChars(value, '\"', '_');
		return value;
	}

	public void append(String reportName, String logRow) throws Exception {

		FileOutputStream reportStream = reportStreamsMap.get(reportName);
		IOUtils.write(logRow + '\n', reportStream, StandardCharsets.UTF_8);
	}

	public void end() throws Exception {

		for (FileOutputStream reportStream : reportStreamsMap.values()) {
			reportStream.flush();
			reportStream.close();
		}
	}
}
