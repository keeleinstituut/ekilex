package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.LexemeDeleteConfirmation;
import eki.ekilex.data.MeaningDeleteConfirmation;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.LookupDbService;

@Component
public class ComplexOpService {

	@Autowired
	private LookupDbService lookupDbService;

	@Transactional
	public LexemeDeleteConfirmation validateLexemeDelete(Long lexemeId, DatasetPermission userRole) {

		LexemeDeleteConfirmation deleteConfirmation = new LexemeDeleteConfirmation();

		if (userRole == null) {
			deleteConfirmation.setUserRoleExist(false);
			deleteConfirmation.setShowConfirmation(true);
			return deleteConfirmation;
		} else {
			deleteConfirmation.setUserRoleExist(true);
		}

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		if (isOnlyLexemeForMeaning) {
			deleteConfirmation.setMeaningDelete(true);
			deleteConfirmation.setShowConfirmation(true);
		}

		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		if (isOnlyLexemeForWord) {
			deleteConfirmation.setWordDelete(true);
			deleteConfirmation.setShowConfirmation(true);
		}

		return deleteConfirmation;
	}

	@Transactional
	public MeaningDeleteConfirmation validateMeaningDelete(Long meaningId, DatasetPermission userRole) {

		MeaningDeleteConfirmation deleteConfirmation = new MeaningDeleteConfirmation();

		if (userRole == null) {
			deleteConfirmation.setUserRoleExist(false);
			return deleteConfirmation;
		} else {
			deleteConfirmation.setUserRoleExist(true);
		}

		String datasetCode = userRole.getDatasetCode();
		boolean isSuperiorPermission = userRole.isSuperiorPermission();

		boolean isOnlyLexemesForMeaning = lookupDbService.isOnlyLexemesForMeaning(meaningId, datasetCode);
		if (isOnlyLexemesForMeaning || isSuperiorPermission) {
			deleteConfirmation.setMeaningDelete(true);
			Map<Long, String[]> relatedMeaningsDatasetsMap = lookupDbService.getMeaningRelationDatasetCodes(meaningId);
			deleteConfirmation.setRelatedMeaningsDatasetsMap(relatedMeaningsDatasetsMap);
		}

		List<Long> wordIdsToDelete = getWordIdsToBeDeleted(meaningId, datasetCode, isSuperiorPermission);
		List<String> wordValues = lookupDbService.getWordsValues(wordIdsToDelete);
		deleteConfirmation.setWordValues(wordValues);

		return deleteConfirmation;
	}

	@Transactional
	public LexemeDeleteConfirmation validateLexemeAndMeaningLexemesDelete(Long lexemeId, String meaningLexemesLang, DatasetPermission userRole) {

		LexemeDeleteConfirmation deleteConfirmation = new LexemeDeleteConfirmation();
		deleteConfirmation.setShowConfirmation(true);

		if (userRole == null) {
			deleteConfirmation.setUserRoleExist(false);
			return deleteConfirmation;
		} else {
			deleteConfirmation.setUserRoleExist(true);
		}

		String datasetCode = userRole.getDatasetCode();
		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, datasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		boolean areOnlyLexemesForMeaning = lookupDbService.areOnlyLexemesForMeaning(lexemeIdsToDelete);
		if (areOnlyLexemesForMeaning) {
			deleteConfirmation.setMeaningDelete(true);
		}

		boolean isWordDelete = false;
		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeIdToDelete);
			if (isOnlyLexemeForWord) {
				isWordDelete = true;
				break;
			}
		}
		deleteConfirmation.setWordDelete(isWordDelete);

		List<String> lexemesWordValues = lookupDbService.getLexemesWordValues(lexemeIdsToDelete);
		deleteConfirmation.setLexemesWordValues(lexemesWordValues);

		return deleteConfirmation;
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
