package eki.ekilex.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ReportComposer {

	private Map<String, FileOutputStream> reportStreamsMap;

	private List<String> reportFilePaths;

	private String activeStream;

	public ReportComposer(String reportGroupName, String... reportNames) throws Exception {

		reportGroupName = fileNameCleanup(reportGroupName);
		reportStreamsMap = new HashMap<>();
		reportFilePaths = new ArrayList<>();
		for (String reportName : reportNames) {
			reportName = fileNameCleanup(reportName);
			String reportFilePath = "./" + reportGroupName + "-" + reportName + ".txt";
			FileOutputStream reportStream = new FileOutputStream(reportFilePath);
			reportStreamsMap.put(reportName, reportStream);
			reportFilePaths.add(reportFilePath);
		}
		activeStream = reportNames[0];
	}

	private String fileNameCleanup(String value) {
		value = StringUtils.replaceChars(value, ' ', '_');
		value = StringUtils.replaceChars(value, '.', '_');
		value = StringUtils.replaceChars(value, '\'', '_');
		value = StringUtils.replaceChars(value, '\"', '_');
		return value;
	}

	public void setActiveStream(String activeStreamName) {
		this.activeStream = fileNameCleanup(activeStreamName);
	}

	public void append(String logRow) throws Exception {
		append(activeStream, logRow);
	}

	public void append(String reportName, String logRow) throws Exception {

		if (StringUtils.isBlank(logRow)) {
			return;
		}
		FileOutputStream reportStream = reportStreamsMap.get(reportName);
		IOUtils.write(logRow + '\n', reportStream, StandardCharsets.UTF_8);
	}

	public void end() throws Exception {

		// flush and close streams
		for (FileOutputStream reportStream : reportStreamsMap.values()) {
			reportStream.flush();
			reportStream.close();
		}

		// delete empty files
		for (String reportFilePath : reportFilePaths) {
			File reportFile = new File(reportFilePath);
			if (reportFile.length() == 0) {
				reportFile.delete();
			}
		}
	}
}
