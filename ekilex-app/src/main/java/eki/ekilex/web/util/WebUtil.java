package eki.ekilex.web.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Lexeme;
import eki.ekilex.data.SynWord;
import eki.ekilex.data.Synonym;
import eki.ekilex.data.SynonymLangGroup;

@Component
public class WebUtil {

	@Autowired
	private SearchHelper searchHelper;

	public void applySynWordsSimpleSearchLinks(String searchPageUri, List<String> datasetCodes, List<Lexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		for (Lexeme lexeme : lexemes) {

			List<SynonymLangGroup> synonymLangGroups = lexeme.getSynonymLangGroups();
			if (CollectionUtils.isEmpty(synonymLangGroups)) {
				continue;
			}

			for (SynonymLangGroup synonymLangGroup : synonymLangGroups) {

				List<Synonym> synonyms = synonymLangGroup.getSynonyms();
				if (CollectionUtils.isEmpty(synonyms)) {
					continue;
				}

				for (Synonym synonym : synonyms) {

					List<SynWord> synWords = synonym.getWords();
					if (CollectionUtils.isEmpty(synWords)) {
						continue;
					}

					for (SynWord synWord : synWords) {

						String wordValue = synWord.getWordValue();
						String searchUri = searchPageUri + searchHelper.composeSimpleSearchUri(datasetCodes, wordValue);
						synWord.setSearchUri(searchUri);
					}
				}
			}
		}
	}
}
