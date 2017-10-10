package eki.ekilex.runner;

import eki.common.service.db.BasicDbService;
import eki.ekilex.service.TermekiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static eki.common.constant.TableName.DATASET;

@Component
public class TermekiRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	@Autowired
	private TermekiService termekiService;

	@Autowired
	private BasicDbService basicDbService;

	public void execute(Integer baseId, String dataset) throws Exception {

		logger.debug("Connecting to Termeki...");
		hasTermDatabaseAndIsKnownDataset(baseId, dataset);
		logger.debug("Done.");
	}

	private boolean hasTermDatabaseAndIsKnownDataset(Integer baseId, String dataset) throws Exception {
		return termekiService.hasTermDatabase(baseId) && isKnownDataset(dataset);
	}

	private boolean isKnownDataset(String dataset) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put("code", dataset);
		Map<String, Object> selectedDataset = basicDbService.select(DATASET, params);
		if (selectedDataset == null) {
			logger.info("No datset with id {} defined in EKILEX", dataset);
		}
		return selectedDataset != null;
	}
}
