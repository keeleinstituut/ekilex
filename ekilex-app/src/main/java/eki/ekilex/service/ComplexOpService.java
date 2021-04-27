package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		if (isOnlyLexemeForMeaning) {
			question = "Valitud ilmik on tähenduse ainus ilmik. Palun kinnita tähenduse kustutamine";
			questions.add(question);
		}

		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		if (isOnlyLexemeForWord) {
			question = "Valitud ilmik on keelendi ainus ilmik. Palun kinnita keelendi kustutamine";
			questions.add(question);
		}

		return createConfirmationRequest(questions, validationMessage, isValid);
	}

	@Transactional
	public ConfirmationRequest validateMeaningDelete(Long meaningId, DatasetPermission userRole) {

		List<String> questions = new ArrayList<>();
		String question;
		StringBuffer validatonMessageBuf = new StringBuffer();
		boolean isValid = true;

		if (userRole == null) {
			isValid = false;
			validatonMessageBuf.append("Mõiste kustutamine pole ilma rollita õigustatud.");
			return createConfirmationRequest(questions, validatonMessageBuf.toString(), isValid);
		}

		String datasetCode = userRole.getDatasetCode();
		boolean isSuperiorPermission = userRole.isSuperiorPermission();

		boolean meaningRelationsExist = lookupDbService.meaningRelationsExist(meaningId);
		if (meaningRelationsExist) {
			isValid = false;
			validatonMessageBuf.append("Valitud mõistel on seoseid. Mõistet ei saa kustutada.");
			return createConfirmationRequest(questions, validatonMessageBuf.toString(), isValid);
		}

		boolean isOnlyLexemesForMeaning = lookupDbService.isOnlyLexemesForMeaning(meaningId, datasetCode);
		if (isOnlyLexemesForMeaning || isSuperiorPermission) {
			question = "Valitud mõistel pole rohkem kasutust. Palun kinnita mõiste kustutamine";
			questions.add(question);
		}

		boolean isOnlyLexemesForWords;
		if (isSuperiorPermission) {
			isOnlyLexemesForWords = lookupDbService.isOnlyLexemesForWords(meaningId);
		} else {
			isOnlyLexemesForWords = lookupDbService.isOnlyLexemesForWords(meaningId, datasetCode);
		}
		if (isOnlyLexemesForWords) {
			List<Long> wordIdsToDelete = getWordIdsToBeDeleted(meaningId, datasetCode, isSuperiorPermission);
			List<String> wordValuesToDelete = lookupDbService.getWordsValues(wordIdsToDelete);
			String joinedWords = StringUtils.join(wordValuesToDelete, ", ");
			question = "Valitud mõiste kustutamisel jäävad järgnevad terminid mõisteta: ";
			question += joinedWords;
			questions.add(question);
			question = "Palun kinnita terminite kustutamine";
			questions.add(question);
		}

		return createConfirmationRequest(questions, validatonMessageBuf.toString(), isValid);
	}

	@Transactional
	public ConfirmationRequest validateLexemeAndMeaningLexemesDelete(Long lexemeId, String meaningLexemesLang, DatasetPermission userRole) {

		List<String> questions = new ArrayList<>();
		String question;
		String validationMessage = "";
		boolean isValid = true;

		if (userRole == null) {
			isValid = false;
			validationMessage += "Ilmikute kustutamine pole ilma rollita õigustatud.";
			return createConfirmationRequest(questions, validationMessage, isValid);
		}

		String datasetCode = userRole.getDatasetCode();
		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, datasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		boolean areOnlyLexemesForMeaning = lookupDbService.areOnlyLexemesForMeaning(lexemeIdsToDelete);
		if (areOnlyLexemesForMeaning) {
			question = "Ilmikute kustutamisega kaasneb ka tähenduse kustutamine. Palun kinnita tähenduse kustutamine";
			questions.add(question);
		}

		boolean isWordDelete = false;
		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeIdToDelete);
			if (isOnlyLexemeForWord) {
				isWordDelete = true;
				break;
			}
		}
		 if (isWordDelete) {
			question = "Ilmikute kustutamisega kaasneb ka keelendi(te) kustutamine. Palun kinnita keelendi(te) kustutamine";
			questions.add(question);
		}

		List<String> lexemeWordValuesToDelete = lookupDbService.getLexemesWordValues(lexemeIdsToDelete);
		String joinedLexemeWords = StringUtils.join(lexemeWordValuesToDelete, ", ");
		question = "Kustuvad järgnevad ilmikud: ";
		question += joinedLexemeWords;
		questions.add(question);
		question = "Palun kinnita ilmikute kustutamine";
		questions.add(question);

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

	private List<Long> getWordIdsToBeDeleted(Long meaningId, String datasetCode, boolean isSuperiorPermission) {

		List<Long> wordIdsToBeDeleted = new ArrayList<>();
		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds;
		if (isSuperiorPermission) {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId);
		} else {
			wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		}
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			Long wordId = wordLexemeMeaningId.getWordId();
			boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
			if (isOnlyLexemeForWord) {
				wordIdsToBeDeleted.add(wordId);
			}
		}
		return wordIdsToBeDeleted;
	}

}
