package eki.wordweb.web.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.SearchFilter;
import eki.wordweb.data.Word;

@Component
public class LinkUtil implements GlobalConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private WebUtil webUtil;

	public void applySearchUris(List<Word> words, SearchFilter searchFilter, boolean isWwUnif, boolean isWwLite) {

		if (CollectionUtils.isEmpty(words)) {
			return;
		}

		for (Word word : words) {

			applySearchUris(word, searchFilter, isWwUnif, isWwLite);
		}
	}

	public void applySearchUris(Word word, SearchFilter searchFilter, boolean isWwUnif, boolean isWwLite) {

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();
		baseUrl = StringUtils.replace(baseUrl, "http:", "https:");

		List<String> destinLangs = searchFilter.getDestinLangs();
		List<String> datasetCodes = searchFilter.getDatasetCodes();

		String wordValue = word.getValue();
		Integer homonymNr = word.getHomonymNr();
		String lang = word.getLang();
		String searchUri = null;
		String lexSearchUrl = null;
		if (isWwUnif) {
			searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangs, datasetCodes, wordValue, homonymNr, lang);
			String lexSearchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangs, DATASET_EKI, wordValue, homonymNr, lang);
			lexSearchUrl = baseUrl + lexSearchUri;
		} else if (isWwLite) {
			searchUri = webUtil.composeAndEncodeSimpleSearchUri(destinLangs, wordValue, homonymNr, lang);
			lexSearchUrl = baseUrl + searchUri;
		}
		word.setSearchUri(searchUri);
		word.setLexSearchUrl(lexSearchUrl);
	}

	public void applyDetailSearchUrl(List<LexemeWord> lexemes, SearchContext searchContext) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		List<String> destinLangs = searchContext.getDestinLangs();

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();
		baseUrl = StringUtils.replace(baseUrl, "http:", "https:");

		for (LexemeWord lexeme : lexemes) {

			String wordValue = lexeme.getValue();
			Integer homonymNr = lexeme.getHomonymNr();
			String lang = lexeme.getLang();
			String datasetCode = lexeme.getDatasetCode();
			String searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangs, datasetCode, wordValue, homonymNr, lang);
			String searchUrl = baseUrl + searchUri;
			lexeme.setDatasetSearchUrl(searchUrl);
		}
	}
}
