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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiRunner.class);

	private static final String SQL_UPDATE_DOMAIN_DATSETS = "update " + DOMAIN + " set datasets = :datsets where code = :code and origin = :origin";

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
		List<Map<String, Object>> subjects = termekiService.getSubjects(baseId);
		logger.info("Found {} subjects.", subjects.size());
		doImport(terms, definitions, subjects, dataset);

		t2 = System.currentTimeMillis();
		logger.debug("Done in {} ms", (t2 - t1));
	}

	@Transactional
	void doImport(
			List<Map<String, Object>> terms,
			List<Map<String, Object>> definitions,
			List<Map<String, Object>> subjects,
			String dataset) throws Exception {

		final String defaultWordMorphCode = "SgN";
		Count wordDuplicateCount = new Count();
		Map<Integer, Long> conceptMeanings = new HashMap<>();

		sortSubjects(subjects, 0);

		for (Map<String, Object> subject : subjects) {
			String code = subject.get("subject_id").toString();
			String language = unifyLang((String)subject.get("lang"));
			String name = (String)subject.get("subject_name");
			String parentCode = getParentCode(subject);
			saveDomainAndLabel(code, name, parentCode, "termeki", language, dataset);
		}

		long count = 0;
		for (Map<String, Object> term : terms) {
			String language = unifyLang((String)term.get("lang"));
			String wordValue = (String)term.get("term");
			int homonymNr = getWordMaxHomonymNr(wordValue, language) + 1;
			Word word = new Word(wordValue,language, null, null, null, homonymNr, defaultWordMorphCode);
			Long wordId = saveWord(word, null, wordDuplicateCount);

			Integer conceptId = (Integer) term.get("concept_id");
			if (!conceptMeanings.containsKey(conceptId)) {
				Long meaningId = createMeaning(dataset);
				conceptMeanings.put(conceptId, meaningId);
				Integer domainId = (Integer) term.get("subject_id");
				if (domainId != null) {
					createMeaningDomain(meaningId, domainId.toString(), "termeki");
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

	private void sortSubjects(List<Map<String, Object>> subjects, int pos) {
		if (subjects.isEmpty()) return;
		if (subjects.get(pos).get("parent_id") != null) {
			swapWithParent(subjects, pos);
		}
		pos++;
		if (pos < subjects.size()) {
			sortSubjects(subjects, pos);
		}
	}

	private void swapWithParent(List<Map<String, Object>> subjects, int pos) {
		Object parentId = subjects.get(pos).get("parent_id");
		int parentPos = pos;
		while (parentPos < subjects.size() && !parentId.equals(subjects.get(parentPos).get("subject_id"))) {
			parentPos++;
		}
		if (parentPos < subjects.size()) {
			Collections.swap(subjects, pos, parentPos);
			if (subjects.get(pos).get("parent_id") != null) {
				swapWithParent(subjects, pos);
			}
		}
	}

	private String getParentCode(Map<String, Object> subject) {
		return subject.get("parent_id") == null ? null : subject.get("parent_id").toString();
	}

	private void saveDomainAndLabel(String code, String name, String parentCode, String origin, String language, String dataset) throws Exception {
		Map<String, Object> domain = getDomain(code, origin);
		if (domain == null) {
			String[] datasets = new String[] {dataset};
			createDomain(code, origin, parentCode, origin, datasets);
		} else {
			updateDomainDatsetsIfNeeded(code, origin, dataset, domain);
		}
		Map<String, Object> domainLabel = getDomainLabel(code, origin, language);
		if (domainLabel == null && isNotBlank(name)) {
			createDomainLabel(code, origin, name, language, "descrip");
		}
	}

	private void updateDomainDatsetsIfNeeded(String code, String origin, String dataset, Map<String, Object> domain) throws Exception {
		List<String> datasets = Arrays.asList((String[])((PgArray)domain.get("datasets")).getArray());
		if (!datasets.contains(dataset)) {
			datasets.add(dataset);
			Map<String, Object> tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("code", code);
			tableRowParamMap.put("origin", origin);
			tableRowParamMap.put("datasets", new PgVarcharArray(datasets));
			basicDbService.executeScript(SQL_UPDATE_DOMAIN_DATSETS, tableRowParamMap);
		}
	}

	private boolean hasTermDatabaseAndIsKnownDataset(Integer baseId, String dataset) throws Exception {
		return termekiService.hasTermDatabase(baseId) && isKnownDataset(dataset);
	}

	private void createDomain(String code, String origin, String parentCode, String parentOrigin, String[] datasets) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		if (isNotBlank(parentCode)) {
			tableRowParamMap.put("parent_code", parentCode);
		}
		if (isNotBlank(parentOrigin)) {
			tableRowParamMap.put("parent_origin", parentOrigin);
		}
		tableRowParamMap.put("datasets", new PgVarcharArray(datasets));
		basicDbService.createWithoutId(DOMAIN, tableRowParamMap);
	}

	private Map<String, Object> getDomain(String code, String origin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		Map<String, Object> tableRowValueMap = basicDbService.select(DOMAIN, tableRowParamMap);
		return tableRowValueMap;
	}

	private void createDomainLabel(String code, String origin, String value, String language, String type) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		tableRowParamMap.put("value", value);
		tableRowParamMap.put("lang", language);
		tableRowParamMap.put("type", type);
		basicDbService.createWithoutId(DOMAIN_LABEL, tableRowParamMap);
	}

	private Map<String, Object> getDomainLabel(String code, String origin, String language) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("code", code);
		tableRowParamMap.put("origin", origin);
		tableRowParamMap.put("lang", language);
		Map<String, Object> tableRowValueMap = basicDbService.select(DOMAIN_LABEL, tableRowParamMap);
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
	void initialise() throws Exception {
	}

}
