package eki.ekilex.runner;

import eki.ekilex.service.TermekiDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TermekiRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	@Autowired
	private TermekiDbService termekiDbService;

	public void execute() {
		logger.debug("Connecting to Termeki...");
		List<Map<String, Object>> result = termekiDbService.queryList("select count(termbase_id) as nr_of_bases from termeki_termbases", null);
		if (!result.isEmpty()) {
			logger.debug("Connection success, there are {} term bases in Termeki.", result.get(0).get("nr_of_bases"));
		}
		logger.debug("Done.");
	}
}
