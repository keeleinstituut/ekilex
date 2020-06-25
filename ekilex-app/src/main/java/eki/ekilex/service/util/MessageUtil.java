package eki.ekilex.service.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;

@Component
public class MessageUtil implements InitializingBean, GlobalConstant {

	private static final String MOTIVATIONAL_TXT_FILE_PATH = "messages/motivational.txt";

	@Autowired
	private ResourceLoader resourceLoader;

	private List<String> positiveQuotes;

	@Override
	public void afterPropertiesSet() throws Exception {
		Resource txtFileResource = resourceLoader.getResource("classpath:" + MOTIVATIONAL_TXT_FILE_PATH);
		InputStream txtFileResInputStream = txtFileResource.getInputStream();
		positiveQuotes = IOUtils.readLines(txtFileResInputStream, UTF_8);
		txtFileResInputStream.close();
	}

	public synchronized String getPositiveQuote() {
		Collections.shuffle(positiveQuotes);
		return positiveQuotes.get(0);
	}
}
