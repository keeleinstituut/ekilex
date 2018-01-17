package eki.ekilex.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;

@Service
public class MabService {

	private Map<String, List<Paradigm>> wordParadigms = new HashMap<>();

	@Autowired
	private MabLoaderRunner mabLoader;

	public void loadParadigms(String dataXmlFilePath, String dataLang, boolean doReports) throws Exception {
		wordParadigms = mabLoader.execute(dataXmlFilePath, dataLang, doReports);
	}

	public boolean isMabLoaded() {
		return MapUtils.isNotEmpty(wordParadigms);
	}

	public boolean paradigmsExist(String word) {
		return wordParadigms.containsKey(word);
	}

	public boolean isSingleParadigm(String word) {
		List<Paradigm> paradigms = wordParadigms.get(word);
		boolean isSingleParadigm = paradigms.size() == 1;
		return isSingleParadigm;
	}

	public boolean isSingleHomonym(String word) {
		if (!paradigmsExist(word)) {
			return true;
		}
		if (isSingleParadigm(word)) {
			return true;
		}
		List<Paradigm> paradigms = wordParadigms.get(word);
		Integer firstHomonymNumber = paradigms.get(0).getHomonymNr();
		boolean allParadigmsMatchHomonymNumber = paradigms.stream().allMatch(p -> Objects.equals(firstHomonymNumber, p.getHomonymNr()));
		return allParadigmsMatchHomonymNumber;
	}

	public List<Paradigm> getWordParadigms(String word) {
		if (paradigmsExist(word)) {
			return wordParadigms.get(word);
		} else {
			return emptyList();
		}
	}

	public List<Paradigm> getWordParadigmsForHomonym(String word, Integer homonymNumber) {
		if (paradigmsExist(word)) {
			return wordParadigms.get(word).stream().filter(p -> Objects.equals(homonymNumber, p.getHomonymNr())).collect(toList());
		} else {
			return emptyList();
		}
	}
}
