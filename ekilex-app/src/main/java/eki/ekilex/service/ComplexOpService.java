package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LexemeType;
import eki.ekilex.data.ConfirmationRequest;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.LookupDbService;

@Component
public class ComplexOpService {

	@Autowired
	private LookupDbService lookupDbService;

	@Transactional
	public ConfirmationRequest validateLexemeDelete(Long lexemeId) {

		List<String> questions = new ArrayList<>();
		String question;
		String validationMessage = "";
		boolean isValid = true;

		LexemeType lexemeType = lookupDbService.getLexemeType(lexemeId);
		boolean isPrimaryLexeme = lexemeType.equals(LexemeType.PRIMARY);
		if (!isPrimaryLexeme) {
			return createConfirmationRequest(questions, validationMessage, isValid);
		}

		boolean isOnlyPrimaryLexemeForMeaning = lookupDbService.isOnlyPrimaryLexemeForMeaning(lexemeId);
		if (isOnlyPrimaryLexemeForMeaning) {
			boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
			if (isOnlyLexemeForMeaning) {
				question = "Valitud ilmik on tähenduse ainus ilmik. Palun kinnita tähenduse kustutamine";
				questions.add(question);
			} else {
				isValid = false;
				validationMessage += "Valitud ilmik on tähenduse ainus ilmik. Ilmikut ei saa kustutada, sest selle keelend on märgitud sünonüümiks. ";
			}
		}

		boolean isOnlyPrimaryLexemeForWord = lookupDbService.isOnlyPrimaryLexemeForWord(lexemeId);
		if (isOnlyPrimaryLexemeForWord) {
			boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
			if (isOnlyLexemeForWord) {
				question = "Valitud ilmik on keelendi ainus ilmik. Palun kinnita keelendi kustutamine";
				questions.add(question);
			} else {
				isValid = false;
				validationMessage += "Valitud ilmik on keelendi ainus ilmik. Ilmikut ei saa kustutada, sest selle keelend on märgitud sünonüümiks. ";
			}
		}

		return createConfirmationRequest(questions, validationMessage, isValid);
	}

	@Transactional
	public ConfirmationRequest validateMeaningDelete(Long meaningId, DatasetPermission userRole) {

		List<String> questions = new ArrayList<>();
		String question;
		String validationMessage = "";
		boolean isValid = true;

		if (userRole == null) {
			isValid = false;
			validationMessage += "Mõiste kustutamine pole ilma rollita õigustatud.";
			return createConfirmationRequest(questions, validationMessage, isValid);
		}

		String datasetCode = userRole.getDatasetCode();
		boolean secondaryMeaningLexemeExists = lookupDbService.secondaryMeaningLexemeExists(meaningId, datasetCode);
		if (secondaryMeaningLexemeExists) {
			isValid = false;
			validationMessage += "Valitud mõistel on osasünonüüme. Mõistet ei saa kustutada.";
			return createConfirmationRequest(questions, validationMessage, isValid);
		}

		boolean isOnlyLexemesForMeaning = lookupDbService.isOnlyLexemesForMeaning(meaningId, datasetCode);
		if (isOnlyLexemesForMeaning) {
			question = "Valitud mõistel pole rohkem kasutust. Palun kinnita mõiste kustutamine";
			questions.add(question);
		}

		boolean isOnlyPrimaryLexemesForWords = lookupDbService.isOnlyPrimaryLexemesForWords(meaningId, datasetCode);
		if (isOnlyPrimaryLexemesForWords) {
			List<Long> wordIdsToDelete = getWordIdsToBeDeleted(meaningId, datasetCode);
			boolean secondaryWordLexemeExists = lookupDbService.secondaryWordLexemeExists(wordIdsToDelete, datasetCode);
			if (secondaryWordLexemeExists) {
				isValid = false;
				validationMessage += "Valitud mõiste termin on märgitud osasünonüümiks. Mõistet ei saa kustutada.";
				return createConfirmationRequest(questions, validationMessage, isValid);
			}

			List<String> wordValuesToDelete = lookupDbService.getWordsValues(wordIdsToDelete);
			String joinedWords = StringUtils.join(wordValuesToDelete, ", ");
			question = "Valitud mõiste kustutamisel jäävad järgnevad terminid mõisteta: ";
			question += joinedWords;
			questions.add(question);
			question = "Palun kinnita terminite kustutamine";
			questions.add(question);
		}

		return createConfirmationRequest(questions, validationMessage, isValid);
	}

	private ConfirmationRequest createConfirmationRequest(List<String> questions, String validationMessage, boolean isValid) {

		ConfirmationRequest confirmationRequest = new ConfirmationRequest();
		confirmationRequest.setValid(isValid);

		if (isValid) {
			boolean unconfirmed = CollectionUtils.isNotEmpty(questions);
			confirmationRequest.setUnconfirmed(unconfirmed);
			confirmationRequest.setQuestions(questions);
		} else {
			confirmationRequest.setValidationMessage(validationMessage);
		}

		return confirmationRequest;
	}

	private List<Long> getWordIdsToBeDeleted(Long meaningId, String datasetCode) {

		List<Long> wordIdsToBeDeleted = new ArrayList<>();
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			Long wordId = wordLexemeMeaningId.getWordId();
			boolean isOnlyPrimaryLexemeForWord = lookupDbService.isOnlyPrimaryLexemeForWord(lexemeId);
			if (isOnlyPrimaryLexemeForWord) {
				wordIdsToBeDeleted.add(wordId);
			}
		}
		return wordIdsToBeDeleted;
	}

}
