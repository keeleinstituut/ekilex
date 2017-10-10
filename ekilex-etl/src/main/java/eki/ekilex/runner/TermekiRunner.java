package eki.ekilex.runner;

import eki.ekilex.service.TermekiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TermekiRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	@Autowired
	private TermekiService termekiService;

	public void execute(Integer baseId, String dataset) throws Exception {

		if (!hasTermDatabaseAndIsKnownDataset(baseId, dataset)) {
			return;
		}
		logger.debug("Start import from Termeki...");
		List<Map<String, Object>> terms = termekiService.getTerms(baseId);
		logger.info("Found {} terms.", terms.size());
		List<Map<String, Object>> definitions = termekiService.getDefinitions(baseId);
		logger.info("Found {} definitions.", definitions.size());
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

	@Override
	void initialise() throws Exception {
	}

}
