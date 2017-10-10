package eki.ekilex.runner;

import eki.common.service.db.BasicDbService;
import eki.ekilex.service.TermekiDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eki.common.constant.TableName.DATASET;

@Component
public class TermekiRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	@Autowired
	private TermekiDbService termekiDbService;

	@Autowired
	private BasicDbService basicDbService;

	public void execute(Integer baseId, String dataset) throws Exception {

		logger.debug("Connecting to Termeki...");
		hasTermDatabaseAndIsKnownDataset(baseId, dataset);
		logger.debug("Done.");
	}

	private boolean hasTermDatabaseAndIsKnownDataset(Integer baseId, String dataset) throws Exception {
		return hasTermDatabase(baseId) && isKnownDataset(dataset);
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

	private boolean hasTermDatabase(Integer baseId) {

		Map<String, Object> params = new HashMap<>();
		params.put("baseId", baseId);
		List<Map<String, Object>> result = termekiDbService.queryList("select * from termeki_termbases where termbase_id=:baseId", params);
		if (!result.isEmpty()) {
			logger.debug("Connection success, termeki base \"{}\".", result.get(0).get("termbase_name"));
		} else {
			logger.info("No termeki base with id found", baseId);
		}
		return !result.isEmpty();
	}
}
