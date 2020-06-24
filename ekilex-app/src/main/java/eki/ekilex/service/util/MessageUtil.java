package eki.ekilex.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import eki.common.constant.GlobalConstant;

@Component
public class MessageUtil implements InitializingBean, GlobalConstant {

	private static final String MOTIVATIONAL_TXT_FILE_PATH = "messages/motivational.txt";

	private List<String> positiveQuotes;

	@Override
	public void afterPropertiesSet() throws Exception {
		File txtFile = ResourceUtils.getFile("classpath:" + MOTIVATIONAL_TXT_FILE_PATH);
		FileInputStream txtFileStream = new FileInputStream(txtFile);
		positiveQuotes = IOUtils.readLines(txtFileStream, UTF_8);
		txtFileStream.close();
	}

	public synchronized String getPositiveQuote() {
		Collections.shuffle(positiveQuotes);
		return positiveQuotes.get(0);
	}
}
