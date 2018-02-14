package eki.ekilex.data.transform;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MabData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, List<Paradigm>> wordParadigmsMap;

	private Map<String, List<String>> formWordsMap;

	public MabData(Map<String, List<Paradigm>> wordParadigmsMap, Map<String, List<String>> formWordsMap) {
		this.wordParadigmsMap = wordParadigmsMap;
		this.formWordsMap = formWordsMap;
	}

	public Map<String, List<Paradigm>> getWordParadigmsMap() {
		return wordParadigmsMap;
	}

	public Map<String, List<String>> getFormWordsMap() {
		return formWordsMap;
	}

}
