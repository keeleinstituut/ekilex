package eki.wordweb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wordweb.data.od.OdSearchResult;
import eki.wordweb.data.od.OdWord;
import eki.wordweb.service.db.OdDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.OdConversionUtil;

@Component
public class OdSearchService implements GlobalConstant {

	@Autowired
	private OdDbService odDbService;

	@Autowired
	private OdConversionUtil odConversionUtil;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public OdSearchResult search(String searchValue, Integer selectedHomonymNr) {

		String displayLang = languageContext.getDisplayLang();
		List<OdWord> words = odDbService.getWords(searchValue);
		OdWord selectedWord = null;

		if (CollectionUtils.isNotEmpty(words)) {
			odConversionUtil.applyGenericConversions(words, selectedHomonymNr);
			Long wordId = odConversionUtil.getSelectedWordId(words);
			selectedWord = odDbService.getOdWord(wordId);
			classifierUtil.applyOdClassifiers(selectedWord, displayLang);
			odConversionUtil.applyWordRelationConversions(selectedWord);
			odConversionUtil.setWordTypeFlags(selectedWord);
			odConversionUtil.setContentExistsFlags(selectedWord);
		}

		int resultCount = CollectionUtils.size(words);
		boolean resultExists = CollectionUtils.isNotEmpty(words);
		boolean singleResult = resultCount == 1;

		OdSearchResult searchResult = new OdSearchResult(words, selectedWord, resultExists, singleResult, resultCount);

		return searchResult;
	}

}
