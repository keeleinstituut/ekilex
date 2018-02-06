package eki.ekilex.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.exception.DataLoadingException;
import eki.ekilex.data.transform.MabData;
import eki.ekilex.data.transform.Paradigm;
import eki.ekilex.runner.MabLoaderRunner;

@Service
public class MabService {

	private Map<String, List<Paradigm>> wordParadigmsMap;

	private Map<String, List<String>> formWordsMap;

	@Autowired
	private MabLoaderRunner mabLoader;

	public void loadParadigms(String dataXmlFilePath, String dataLang, boolean doReports) throws Exception {
		MabData mabData = mabLoader.execute(dataXmlFilePath, dataLang, doReports);
		wordParadigmsMap = mabData.getWordParadigmsMap();
		formWordsMap = mabData.getFormWordsMap();
	}

	public boolean isMabLoaded() {
		return MapUtils.isNotEmpty(wordParadigmsMap) && MapUtils.isNotEmpty(formWordsMap);
	}

	public boolean paradigmsExist(String word) throws Exception {
		if (MapUtils.isEmpty(wordParadigmsMap)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		return wordParadigmsMap.containsKey(word);
	}

	public boolean isSingleParadigm(String word) throws Exception {
		if (MapUtils.isEmpty(wordParadigmsMap)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		List<Paradigm> paradigms = wordParadigmsMap.get(word);
		boolean isSingleParadigm = paradigms.size() == 1;
		return isSingleParadigm;
	}

	public boolean isSingleHomonym(String word) throws Exception {
		if (!paradigmsExist(word)) {
			return true;
		}
		if (isSingleParadigm(word)) {
			return true;
		}
		List<Paradigm> paradigms = wordParadigmsMap.get(word);
		Integer firstHomonymNumber = paradigms.get(0).getHomonymNr();
		boolean allParadigmsMatchHomonymNumber = paradigms.stream().allMatch(p -> Objects.equals(firstHomonymNumber, p.getHomonymNr()));
		return allParadigmsMatchHomonymNumber;
	}

	public List<Paradigm> getWordParadigms(String word) throws Exception {
		if (paradigmsExist(word)) {
			return wordParadigmsMap.get(word);
		} else {
			return emptyList();
		}
	}

	public List<Paradigm> getWordParadigmsForHomonym(String word, Integer homonymNumber) throws Exception {
		if (paradigmsExist(word)) {
			return wordParadigmsMap.get(word).stream().filter(p -> Objects.equals(homonymNumber, p.getHomonymNr())).collect(toList());
		} else {
			return emptyList();
		}
	}

	public boolean isKnownForm(String form) throws Exception {
		if (MapUtils.isEmpty(formWordsMap)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		return formWordsMap.containsKey(form);
	}

	public boolean isSingleWordForm(String form) throws Exception {
		if (MapUtils.isEmpty(formWordsMap)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		List<String> words = formWordsMap.get(form);
		if (words == null) {
			return false;
		}
		boolean isSingleWordForm = words.size() == 1;
		return isSingleWordForm;
	}

	public String getSingleWordFormWord(String form) throws Exception {
		if (isSingleWordForm(form)) {
			return formWordsMap.get(form).get(0);
		}
		return null;
	}

	public List<String> getFormWords(String form) throws Exception {
		if (MapUtils.isEmpty(formWordsMap)) {
			throw new DataLoadingException("MAB not loaded!");
		}
		return formWordsMap.get(form);
	}
}
