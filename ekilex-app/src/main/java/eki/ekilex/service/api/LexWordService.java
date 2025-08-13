package eki.ekilex.service.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Usage;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.Definition;
import eki.ekilex.data.api.Forum;
import eki.ekilex.data.api.LexMeaning;
import eki.ekilex.data.api.LexWord;
import eki.ekilex.data.api.Word;
import eki.ekilex.data.api.WordRelation;
import eki.ekilex.service.db.TagDbService;
import eki.ekilex.service.db.api.WordDbService;

@Component
public class LexWordService extends AbstractApiCudService {

	private static final boolean DEFAULT_PUBLICITY = PUBLICITY_PUBLIC;

	@Autowired
	private TagDbService tagDbService;

	@Autowired
	private WordDbService wordDbService;

	@Transactional
	public List<Word> getPublicWords(String datasetCode, String tagName) {
		return wordDbService.getPublicWords(datasetCode, tagName);
	}

	@Transactional
	public List<Long> getWordIds(String wordValue, String datasetCode, String lang) {
		return wordDbService.getWordsIds(wordValue, datasetCode, lang);
	}

	@Transactional
	public LexWord getLexWord(Long wordId, String datasetCode) {

		if (wordId == null) {
			return null;
		}
		if (StringUtils.isBlank(datasetCode)) {
			return null;
		}
		return wordDbService.getLexWord(wordId, datasetCode);
	}

	@Transactional(rollbackOn = Exception.class)
	public Long saveLexWord(LexWord word, String roleDatasetCode) throws Exception {

		final String updateFunctName = "updateLexWord";
		final String createFunctName = "createLexWord";

		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		String userName = user.getName();

		Long wordId = word.getWordId();
		String wordValue = word.getWordValue();
		wordValue = textDecorationService.removeEkiElementMarkup(wordValue);
		String valueAsWord = textDecorationService.getValueAsWord(wordValue);
		List<String> wordTypeCodes = word.getWordTypeCodes();
		List<Forum> wordForums = word.getForums();
		List<WordRelation> wordRelations = word.getRelations();
		String lang = word.getLang();
		String datasetCode = word.getDatasetCode();
		List<LexMeaning> meanings = word.getMeanings();

		ActivityLogData activityLog;

		if (wordId == null) {
			wordId = wordDbService.createWord(word, valueAsWord);
			activityLogService.createActivityLog(createFunctName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		} else {
			activityLog = activityLogService.prepareActivityLog(updateFunctName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);

			SimpleWord originalWord = wordDbService.getSimpleWord(wordId);
			wordDbService.updateWord(word, valueAsWord);
			SimpleWord updatedWord = new SimpleWord(wordId, wordValue, lang);
			cudDbService.adjustWordHomonymNrs(originalWord);
			cudDbService.adjustWordHomonymNrs(updatedWord);

			activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
		}

		if (CollectionUtils.isNotEmpty(wordTypeCodes)) {

			List<String> existingWordTypeCodes = lookupDbService.getWordTypeCodes(wordId);
			for (String wordTypeCode : wordTypeCodes) {

				if (!existingWordTypeCodes.contains(wordTypeCode)) {
					activityLog = activityLogService.prepareActivityLog("createWordType", wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					Long wordTypeId = cudDbService.createWordType(wordId, wordTypeCode);
					activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(wordForums)) {

			for (Forum wordForum : wordForums) {

				Long wordForumId = wordForum.getId();
				if (wordForumId == null) {
					String wordForumValue = wordForum.getValue();
					cudDbService.createWordForum(wordId, wordForumValue, wordForumValue, userId, userName);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(wordRelations)) {

			for (WordRelation wordRelation : wordRelations) {

				Long wordRelationId = wordRelation.getId();
				if (wordRelationId == null) {
					Long wordRelationTargetWordId = wordRelation.getTargetWordId();
					String relationTypeCode = wordRelation.getRelationTypeCode();
					String oppositeRelationTypeCode = wordRelation.getOppositeRelationTypeCode();
					createWordRelation(wordId, wordRelationTargetWordId, relationTypeCode, oppositeRelationTypeCode, user, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				}
			}
		}

		List<WordLexemeMeaningIdTuple> existingWordMeaningIds = lookupDbService.getWordLexemeMeaningIdsByWord(wordId, datasetCode);
		List<Long> existingMeaningIds = existingWordMeaningIds.stream().map(WordLexemeMeaningIdTuple::getMeaningId).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(meanings)) {
			if (existingMeaningIds.isEmpty()) {
				WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexemeWithCreateOrSelectMeaning(wordId, datasetCode, null, 1, null, PUBLICITY_PUBLIC);
				Long lexemeId = wordLexemeMeaningId.getLexemeId();
				Long meaningId = wordLexemeMeaningId.getMeaningId();
				tagDbService.createLexemeAutomaticTags(lexemeId);
				activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				activityLogService.createActivityLog(createFunctName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			}
			return wordId;
		}

		for (LexMeaning meaning : meanings) {

			Long lexemeId;
			Long meaningId = meaning.getMeaningId();
			List<Definition> definitions = meaning.getDefinitions();
			List<Usage> usages = meaning.getUsages();
			int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
			int newLexemeLevel1 = currentLexemesMaxLevel1 + 1;

			if (meaningId == null) {
				WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexemeWithCreateOrSelectMeaning(wordId, datasetCode, null, newLexemeLevel1, null, PUBLICITY_PUBLIC);
				lexemeId = wordLexemeMeaningId.getLexemeId();
				meaningId = wordLexemeMeaningId.getMeaningId();
				tagDbService.createLexemeAutomaticTags(lexemeId);
				activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				activityLogService.createActivityLog(createFunctName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
			} else {
				if (existingMeaningIds.contains(meaningId)) {
					lexemeId = lookupDbService.getLexemeId(wordId, meaningId);
				} else {
					WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createLexemeWithCreateOrSelectMeaning(wordId, datasetCode, meaningId, newLexemeLevel1, null, PUBLICITY_PUBLIC);
					lexemeId = wordLexemeMeaningId.getLexemeId();
					tagDbService.createLexemeAutomaticTags(lexemeId);
					activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				}
			}

			if (CollectionUtils.isNotEmpty(definitions)) {

				for (Definition definition : definitions) {
					createOrUpdateDefinition(definition, meaningId, datasetCode, DEFAULT_PUBLICITY, roleDatasetCode);
				}
			}

			if (CollectionUtils.isNotEmpty(usages)) {

				for (Usage usage : usages) {
					createOrUpdateUsage(lexemeId, usage, roleDatasetCode);
				}
			}
		}
		return wordId;
	}

}
