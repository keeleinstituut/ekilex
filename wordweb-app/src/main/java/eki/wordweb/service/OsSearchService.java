package eki.wordweb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wordweb.data.os.OsSearchResult;
import eki.wordweb.data.os.OsWord;
import eki.wordweb.service.db.OsDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.LanguageContext;
import eki.wordweb.service.util.OsConversionUtil;

@Component
public class OsSearchService implements GlobalConstant {

	@Autowired
	private OsDbService osDbService;

	@Autowired
	private OsConversionUtil osConversionUtil;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public OsSearchResult search(String searchValue, Integer selectedHomonymNr) {

		String displayLang = languageContext.getDisplayLang();
		boolean fiCollationExists = osDbService.fiCollationExists();
		List<OsWord> words = osDbService.getWords(searchValue, fiCollationExists);
		OsWord selectedWord = null;

		if (CollectionUtils.isNotEmpty(words)) {
			osConversionUtil.applyGenericConversions(words, searchValue, selectedHomonymNr);
			Long wordId = osConversionUtil.getSelectedWordId(words);
			selectedWord = osDbService.getOdWord(wordId);
			classifierUtil.applyOdClassifiers(selectedWord, displayLang);
			osConversionUtil.applyWordRelationConversions(selectedWord);
			osConversionUtil.setWordTypeFlags(selectedWord);
			osConversionUtil.setContentExistsFlags(selectedWord);
		}

		int resultCount = CollectionUtils.size(words);
		boolean resultExists = CollectionUtils.isNotEmpty(words);
		boolean singleResult = resultCount == 1;

		OsSearchResult searchResult = new OsSearchResult(words, selectedWord, resultExists, singleResult, resultCount);

		return searchResult;
	}

}
