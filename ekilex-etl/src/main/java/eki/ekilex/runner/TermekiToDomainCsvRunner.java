package eki.ekilex.runner;

import eki.ekilex.data.transform.ClassifierMapping;
import eki.ekilex.service.TermekiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@ConditionalOnBean(name = "dataSourceTermeki")
public class TermekiToDomainCsvRunner extends AbstractDomainRunner {

	private static Logger logger = LoggerFactory.getLogger(TermekiToDomainCsvRunner.class);

	@Autowired
	private TermekiService termekiService;

	@Override
	void initialise() {
	}

	public void execute() throws Exception {

		List<ClassifierMapping> sourceClassifiers = loadTermekiClassifiers();
		List<ClassifierMapping> existingClassifiers = loadExistingDomainClassifierMappings();
		existingClassifiers = removeTermekiClassifiers(existingClassifiers);
		List<ClassifierMapping> targetClassifiers = merge(sourceClassifiers, existingClassifiers);
		targetClassifiers.sort(Comparator.comparing(ClassifierMapping::getEkiOrigin).thenComparing(ClassifierMapping::getOrder));

		writeDomainClassifierCsvFile(targetClassifiers);

		logger.debug("Done. Recompiled {} rows", targetClassifiers.size());
	}

	private List<ClassifierMapping> removeTermekiClassifiers(List<ClassifierMapping> existingClassifiers) {
		return existingClassifiers.stream()
				.filter(c -> !termekiService.getTermbaseCodes().contains(c.getEkiOrigin()) || c.getEkiOrigin().equals("get"))
				.collect(toList());
	}

	private List<ClassifierMapping> loadTermekiClassifiers() {

		List<Map<String, Object>> domains = termekiService.getDomainsForLanguage("et");
		Map<Object, Object> domainCodes = domains.stream()
				.filter(d -> isNotBlank((String) d.get("code")))
				.collect(toMap(d -> d.get("subject_id"), d -> d.get("code")));
		List<Map<String, Object>> domainsForEng = termekiService.getDomainsForLanguage("en");
		Map<Object, Object> domainCodesInEnglish = domainsForEng.stream()
				.filter(d -> isNotBlank((String) d.get("code")))
				.collect(toMap(d -> d.get("subject_id"), d -> d.get("code")));

		sortDomainsParentsFirst(domains, 0);

		List<ClassifierMapping> classifierMappings = new ArrayList<>();
		for (Map<String, Object> domain : domains) {
			ClassifierMapping classifierMapping = createClassifierMapping(domainCodes, domain, "et");
			classifierMappings.add(classifierMapping);
			if (domainCodesInEnglish.containsKey(domain.get("subject_id"))) {
				classifierMapping = createClassifierMapping(domainCodes, domain, "en");
				classifierMapping.setEkiValue((String) domainCodesInEnglish.get(domain.get("subject_id")));
				classifierMappings.add(classifierMapping);
			}
		}
		return classifierMappings;
	}

	private ClassifierMapping createClassifierMapping(Map<Object, Object> domainCodes, Map<String, Object> domain, String language) {
		ClassifierMapping classifierMapping = new ClassifierMapping();
		classifierMapping.setEkiOrigin((String) domain.get("termbase_code"));
		classifierMapping.setEkiCode((String) domain.get("code"));
		classifierMapping.setEkiValue((String) domain.get("code"));
		classifierMapping.setEkiValueLang(language);
		if (Objects.nonNull(domain.get("parent_id"))) {
			classifierMapping.setEkiParentCode((String) domainCodes.get(domain.get("parent_id")));
		}
		return classifierMapping;
	}

	private void sortDomainsParentsFirst(List<Map<String, Object>> subjects, int pos) {
		if (subjects.isEmpty()) return;
		if (subjects.get(pos).get("parent_id") != null) {
			swapWithParent(subjects, pos);
		}
		pos++;
		if (pos < subjects.size()) {
			sortDomainsParentsFirst(subjects, pos);
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

}
