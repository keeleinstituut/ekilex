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
import eki.ekilex.constant.SystemConstant;

@Component
public class GameDataLoaderRunner implements TableName, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(GameDataLoaderRunner.class);

	@Autowired
	protected BasicDbService basicDbService;

	@Transactional
	public void execute(String nonWordsFilePath) throws Exception {

		File nowWordsFile = new File(nonWordsFilePath);
		FileInputStream nonWordsFileInputStream = new FileInputStream(nowWordsFile);
		List<String> nonWordLines = IOUtils.readLines(nonWordsFileInputStream, StandardCharsets.UTF_8);
		nonWordsFileInputStream.close();

		//remove headers
		nonWordLines.remove(0);

		int nonwordCount = 0;
		for (String nonwordLine : nonWordLines) {
			if (StringUtils.isEmpty(nonwordLine)) {
				continue;
			}
			String[] nonwordLineParts = StringUtils.split(nonwordLine, CSV_SEPARATOR);
			String nonword = StringUtils.trim(nonwordLineParts[0]);
			String lang = StringUtils.trim(nonwordLineParts[1]);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("word", nonword);
			tableRowParamMap.put("lang", lang);
			basicDbService.create(GAME_NONWORD, tableRowParamMap);
			nonwordCount++;
		}
		logger.debug("Loaded {} nonwords", nonwordCount);
	}
}
