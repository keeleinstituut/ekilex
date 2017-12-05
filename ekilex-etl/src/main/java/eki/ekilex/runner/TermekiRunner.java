package eki.ekilex.runner;

import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.ekilex.data.transform.Lexeme;
import eki.ekilex.data.transform.Word;
import eki.ekilex.service.TermekiService;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	private static final String SQL_UPDATE_DOMAIN_DATSETS = "update " + DOMAIN + " set datasets = :datasets where code = :code and origin = :origin";

	@Autowired
	private TermekiService termekiService;

	public void execute(Integer baseId, String dataset) throws Exception {

		if (!hasTermDatabaseAndIsKnownDataset(baseId, dataset)) {
			return;
		}
		logger.debug("Start import from Termeki...");
		long t1, t2;
		t1 = System.currentTimeMillis();

		List<Map<String, Object>> terms = termekiService.getTerms(baseId);
		logger.info("Found {} terms.", terms.size());
		List<Map<String, Object>> definitions = termekiService.getDefinitions(baseId);
		logger.info("Found {} definitions.", definitions.size());
		doImport(terms, definitions, dataset);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	@Transactional
	void doImport(
			List<Map<String, Object>> terms,
			List<Map<String, Object>> definitions,
			String dataset) throws Exception {

		final String defaultWordMorphCode = "SgN";
		Count wordDuplicateCount = new Count();
		Map<Integer, Long> conceptMeanings = new HashMap<>();
		long count = 0;

		for (Map<String, Object> term : terms) {
			String language = unifyLang((String)term.get("lang"));
			String wordValue = (String)term.get("term");
			int homonymNr = getWordMaxHomonymNr(wordValue, language) + 1;
			Word word = new Word(wordValue,language, null, null, null, null, homonymNr, defaultWordMorphCode, null);
			Long wordId = saveWord(word, null, null, wordDuplicateCount);

			Integer conceptId = (Integer) term.get("concept_id");
			if (!conceptMeanings.containsKey(conceptId)) {
				Long meaningId = createMeaning(dataset);
				conceptMeanings.put(conceptId, meaningId);
				String domainCode = (String) term.get("domain_code");
				if (isNotBlank(domainCode)) {
					Map<String, Object> domain = getDomain(domainCode, "termeki");
					if (domain == null) {
						logger.info("Invalid domain code : {}", domainCode);
					} else {
						createMeaningDomain(meaningId, domainCode, "termeki");
						updateDomainDatsetsIfNeeded(domain, dataset);
					}
				}
			}

			Long meaningId = conceptMeanings.get(conceptId);
			Lexeme lexeme = new Lexeme();
			lexeme.setWordId(wordId);
			lexeme.setMeaningId(meaningId);
			lexeme.setLevel1(0);
			lexeme.setLevel2(0);
			lexeme.setLevel3(0);
			createLexeme(lexeme, dataset);
			if (++count % 100 == 0) {
				System.out.print(".");
			}
		}
		System.out.println();
		logger.info("{} words imported", terms.size());
		logger.info("{} duplicate words found", wordDuplicateCount.getValue());
		logger.info("{} meanings created", conceptMeanings.size());

		int definitionsCount = 0;
		for (Map<String, Object> definition : definitions) {
			String language = unifyLang((String)definition.get("lang"));
			Integer conceptId = (Integer) definition.get("concept_id");
			if (conceptMeanings.containsKey(conceptId)) {
				Long meaningId = conceptMeanings.get(conceptId);
				createDefinition(meaningId, (String)definition.get("definition"), language, dataset);
				definitionsCount++;
			}
		}
		logger.info("{} definitions created", definitionsCount);
	}

	private void updateDomainDatsetsIfNeeded(Map<String, Object> domain, String dataset) throws Exception {

		List<String> datasets = Arrays.asList((String[])((PgArray)domain.get("datasets")).getArray());
		if (!datasets.contains(dataset)) {
			List<String> updatedDataset = new ArrayList<>();
			updatedDataset.addAll(datasets);
			updatedDataset.add(dataset);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("code", domain.get("code"));
			tableRowParamMap.put("origin", domain.get("origin"));
			tableRowParamMap.put("datasets", new PgVarcharArray(updatedDataset.toArray(new String[updatedDataset.size()])));
			basicDbService.executeScript(SQL_UPDATE_DOMAIN_DATSETS, tableRowParamMap);
		}
	}

	private boolean hasTermDatabaseAndIsKnownDataset(Integer baseId, String dataset) throws Exception {
		return termekiService.hasTermDatabase(baseId) && isKnownDataset(dataset);
	}

	private Map<String, Object> getDomain(String code, String origin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		Map<String, Object> tableRowValueMap = basicDbService.select(DOMAIN, tableRowParamMap);
		return tableRowValueMap;
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
	void initialise() {
	}

}
