package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.Tag;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordForum;
import eki.ekilex.data.WordRelationDetails;

@Component
public class PartSynSearchService extends AbstractSynSearchService {

	@Transactional
	public WordDetails getWordPartSynDetails(
			Long wordId, List<ClassifierSelect> languagesOrder, List<String> synCandidateLangCodes, List<String> synMeaningWordLangCodes, Tag activeTag,
			EkiUser user, EkiUserProfile userProfile) {

		DatasetPermission userRole = user.getRecentRole();
		String synCandidateDatasetCode = userRole.getDatasetCode();
		String datasetCode = userRole.getDatasetCode();
		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);

		Word word = synSearchDbService.getWord(wordId);
		permCalculator.applyCrud(user, word);
		List<WordForum> wordForums = commonDataDbService.getWordForums(wordId);
		permCalculator.applyCrud(user, wordForums);
		word.setForums(wordForums);

		List<Lexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST);
		synLexemes.forEach(lexeme -> populateLexeme(lexeme, word, languagesOrder, synMeaningWordLangCodes, user, userProfile));
		lexemeLevelPreseUtil.combineLevels(synLexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(synLexemes, activeTag);

		List<SynRelation> synRelations = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(synCandidateLangCodes)) {
			synRelations = synSearchDbService.getWordPartSynRelations(wordId, WORD_REL_TYPE_CODE_RAW, synCandidateDatasetCode, synCandidateLangCodes);
		}
		WordRelationDetails wordRelationDetails = new WordRelationDetails();
		wordRelationDetails.setWordSynRelations(synRelations);

		WordDetails wordDetails = new WordDetails();
		wordDetails.setWord(word);
		wordDetails.setLexemes(synLexemes);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

}
