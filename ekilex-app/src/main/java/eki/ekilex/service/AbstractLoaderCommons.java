package eki.ekilex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.constant.TableName;
import eki.common.service.XmlReader;
import eki.ekilex.service.db.BasicDbService;

public abstract class AbstractLoaderCommons implements LoaderConstant, GlobalConstant, TableName {

	@Autowired
	protected XmlReader xmlReader;

	@Autowired
	protected BasicDbService basicDbService;

	protected String toReadableFormat(long timeMillis) {
		long secondMillis = 1000;
		long minuteMillis = 60000;
		String timeLog;
		if (timeMillis < secondMillis) {
			timeLog = timeMillis + " millis";
		} else if (timeMillis < minuteMillis) {
			float timeSeconds = (float) timeMillis / (float) secondMillis;
			BigDecimal timeSecondsRound = new BigDecimal(timeSeconds);
			timeSecondsRound = timeSecondsRound.setScale(2, RoundingMode.HALF_UP);
			timeLog = timeSecondsRound.toString() + " seconds";
		} else {
			float timeMinutes = (float) timeMillis / (float) minuteMillis;
			BigDecimal timeMinutesRound = new BigDecimal(timeMinutes);
			timeMinutesRound = timeMinutesRound.setScale(2, RoundingMode.HALF_UP);
			timeLog = timeMinutesRound.toString() + " minutes";
		}
		return timeLog;
	}

	protected String getContent(InputStream resourceInputStream) throws Exception {
		String content = IOUtils.toString(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return content;
	}

	protected List<String> readFileLines(String sourcePath) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(sourcePath)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

	protected List<String> readFileLines(File sourceFile) throws Exception {
		try (InputStream resourceInputStream = new FileInputStream(sourceFile)) {
			return IOUtils.readLines(resourceInputStream, UTF_8);
		}
	}

	protected List<String> getContentLines(InputStream resourceInputStream) throws Exception {
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}
}
