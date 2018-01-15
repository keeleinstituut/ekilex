package eki.ekilex.service;

import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class MabService {

	private Map<String, List<Paradigm>> wordParadigms = new HashMap<>();

	@Autowired
	private MabLoaderRunner mabLoader;

	public void loadParadigms(String dataXmlFilePath, String dataLang, boolean doReports) throws Exception {
		wordParadigms = mabLoader.execute(dataXmlFilePath, dataLang, doReports);
	}

	public boolean wordHasParadigms(String word) {
		return wordParadigms.containsKey(word);
	}

	public boolean wordHasOnlyOneHomonym(String word) {
		if (!wordHasParadigms(word)) {
			return true;
		}
		List<Paradigm> paradigms = wordParadigms.get(word);
		Integer homonymNumber = paradigms.get(0).getHomonymNr();
		return paradigms.size() == 1 || paradigms.stream().allMatch(p -> Objects.equals(homonymNumber, p.getHomonymNr()));
	}

	public List<Paradigm> getWordParadigmsForHomonym(String word, Integer homonymNumber) {
		if (wordHasParadigms(word)) {
			return wordParadigms.get(word).stream().filter(p -> Objects.equals(homonymNumber, p.getHomonymNr())).collect(toList());
		} else {
			return emptyList();
		}
	}

	public List<Paradigm> getWordParadigms(String word) {
		if (wordHasParadigms(word)) {
			return wordParadigms.get(word);
		} else {
			return emptyList();
		}
	}

}
