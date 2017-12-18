package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.TermDetails;
import eki.ekilex.service.db.TermSearchDbService;

//TODO under construction!
@Component
public class TermSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Transactional
	public TermDetails findWordDetailsInDatasets(Long formId, List<String> selectedDatasets) {

		List<Meaning> meanings = termSearchDbService.findFormMeanings(formId, selectedDatasets).into(Meaning.class);

		for (Meaning meaning : meanings) {

			Long meaningId = meaning.getMeaningId();

			//TODO collect meaning stuff

			List<Long> lexemeIds = meaning.getLexemeIds();

			for (Long lexemeId : lexemeIds) {

				// lexeme duplicated if many words/forms
				List<Lexeme> lexemes = termSearchDbService.getLexeme(lexemeId).into(Lexeme.class);

				for (Lexeme lexeme : lexemes) {
					//TODO collect lexeme stuff
				}
			}

		}

		return null;
	}
}
