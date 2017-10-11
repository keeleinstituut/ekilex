package eki.ekilex.runner;

import eki.common.data.Count;
import eki.ekilex.service.TermekiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
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
		doImport(terms, definitions, dataset);
		logger.debug("Done.");
	}

	@Transactional
	void doImport(List<Map<String, Object>> terms, List<Map<String, Object>> definitions, String dataset) throws Exception {

		final String defaultWordMorphCode = "SgN";
		Count wordDuplicateCount = new Count();
		Map<Integer, Long> conceptMeanings = new HashMap<>();
		String[] datasets = new String[] {dataset};

		for (Map<String, Object> term : terms) {
			String language = unifyLang((String)term.get("lang"));
			String word = (String)term.get("term");
			int homonymNr = getWordMaxHomonymNr(word, language) + 1;
			Long wordId = saveWord(word, null, null, homonymNr, defaultWordMorphCode, language, wordDuplicateCount);

			Integer conceptId = (Integer) term.get("concept_id");
			if (!conceptMeanings.containsKey(conceptId)) {
				Long meaningId = createMeaning(datasets);
				conceptMeanings.put(conceptId, meaningId);
			}

			Long meaningId = conceptMeanings.get(conceptId);
			createLexeme(wordId, meaningId, 0, 0, 0, datasets);
		}
		logger.info("{} words imported", terms.size());
		logger.info("{} duplicate words found", wordDuplicateCount.getValue());
		logger.info("{} meanings created", conceptMeanings.size());

		int definitionsCount = 0;
		for (Map<String, Object> definition : definitions) {
			String language = unifyLang((String)definition.get("lang"));
			Integer conceptId = (Integer) definition.get("concept_id");
			if (conceptMeanings.containsKey(conceptId)) {
				Long meaningId = conceptMeanings.get(conceptId);
				createDefinition(meaningId, (String)definition.get("definition"), language, datasets);
				definitionsCount++;
			}
		}
		logger.info("{} definitions created", definitionsCount);
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
