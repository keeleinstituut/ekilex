package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TableName;
import eki.common.service.db.BasicDbService;

@Component
public class GameDataLoaderRunner implements TableName {

	private static Logger logger = LoggerFactory.getLogger(GameDataLoaderRunner.class);

	@Autowired
	protected BasicDbService basicDbService;

	@Transactional
	public void execute(String nonWordsFilePath, String lang) throws Exception {

		File nowWordsFile = new File(nonWordsFilePath);
		FileInputStream nonWordsFileInputStream = new FileInputStream(nowWordsFile);
		List<String> nonWordLines = IOUtils.readLines(nonWordsFileInputStream, StandardCharsets.UTF_8);
		nonWordsFileInputStream.close();

		int nonwordCount = 0;
		for (String nonword : nonWordLines) {
			if (StringUtils.isEmpty(nonword)) {
				continue;
			}
			nonword = StringUtils.trim(nonword);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("word", nonword);
			tableRowParamMap.put("lang", lang);
			basicDbService.create(GAME_NONWORD, tableRowParamMap);
			nonwordCount++;
		}
		logger.debug("Loaded {} nonwords", nonwordCount);
	}
}
