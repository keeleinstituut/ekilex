package eki.wordweb.web.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Word;

@Component
public class LinkUtil implements GlobalConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private WebUtil webUtil;

	public void applyLexDetailSearchUrl(Word word, List<String> destinLangs) {

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();
		String wordValue = word.getValue();
		Integer homonymNr = word.getHomonymNr();
		String lang = word.getLang();
		String datasetCode = DATASET_EKI;
		String searchUri = webUtil.composeAndEncodeDetailSearchUri(destinLangs, datasetCode, wordValue, homonymNr, lang);
		String searchUrl = baseUrl + searchUri;
		word.setLexSearchUrl(searchUrl);
	}

	public void applyLexSimpleSearchUrl(Word word, List<String> destinLangs) {

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();
		String wordValue = word.getValue();
		Integer homonymNr = word.getHomonymNr();
		String lang = word.getLang();
		String searchUri = webUtil.composeAndEncodeSimpleSearchUri(destinLangs, wordValue, homonymNr, lang);
		String searchUrl = baseUrl + searchUri;
		word.setLexSearchUrl(searchUrl);
	}

	public void applyDetailSearchUrl(List<LexemeWord> lexemes, List<String> destinLangs) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		AppData appData = appDataHolder.getAppData();
		String baseUrl = appData.getBaseUrl();

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
