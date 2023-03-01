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
import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.TermDefinition;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.api.TermWord;
import eki.ekilex.service.AbstractService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.api.TermMeaningDbService;

@Component
public class TermMeaningService extends AbstractService implements GlobalConstant {

	private static final Complexity DEFAULT_COMPLEXITY = Complexity.DETAIL;

	@Autowired
	private TermMeaningDbService termMeaningDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	protected TextDecorationService textDecorationService;

	@Transactional
	public TermMeaning getTermMeaning(Long meaningId, String datasetCode) {

		if (meaningId == null) {
			return null;
		}
		if (StringUtils.isBlank(datasetCode)) {
			return null;
		}
		return termMeaningDbService.getTermMeaning(meaningId, datasetCode);
	}

	@Transactional
	public Long saveTermMeaning(TermMeaning termMeaning) throws Exception {

		final String functName = "saveTermMeaning";
		Long meaningId = termMeaning.getMeaningId();
		String datasetCode = termMeaning.getDatasetCode();
		List<TermDefinition> definitions = termMeaning.getDefinitions();
		List<TermWord> words = termMeaning.getWords();
		ActivityLogData activityLog;

		if (meaningId == null) {
			meaningId = cudDbService.createMeaning();
			activityLogService.createActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}
		if (CollectionUtils.isNotEmpty(definitions)) {

			for (TermDefinition definition : definitions) {

				Long definitionId = definition.getDefinitionId();
				String definitionValue = StringUtils.trim(definition.getValue());
				String definitionLang = definition.getLang();
				String definitionTypeCode = definition.getDefinitionTypeCode();
				if (StringUtils.isAnyBlank(definitionValue, definitionLang)) {
					continue;
				}
				activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_ENABLED);
				if (definitionId == null) {
					definitionId = cudDbService.createDefinition(meaningId, definitionValue, definitionValue, definitionLang, definitionTypeCode, DEFAULT_COMPLEXITY, PUBLICITY_PUBLIC);
					cudDbService.createDefinitionDataset(definitionId, datasetCode);
				} else {
					cudDbService.updateDefinition(definitionId, definitionValue, definitionValue, definitionLang, definitionTypeCode, DEFAULT_COMPLEXITY, PUBLICITY_PUBLIC);
				}
				activityLogService.createActivityLog(activityLog, definitionId, ActivityEntity.DEFINITION);
			}
		}
		if (CollectionUtils.isNotEmpty(words)) {

			List<WordLexeme> meaningWords = lookupDbService.getMeaningWords(meaningId, datasetCode, null);
			List<Long> existingWordIds = meaningWords.stream().map(WordLexeme::getWordId).collect(Collectors.toList());

			for (TermWord word : words) {

				Long wordId = word.getWordId();
				String wordValue = StringUtils.trim(word.getValue());
				String wordLang = word.getLang();

				if ((wordId == null) && StringUtils.isAnyBlank(wordValue, wordLang)) {
					continue;
				} else if ((wordId != null) && StringUtils.isAnyBlank(wordValue, wordLang)) {

					if (!existingWordIds.contains(wordId)) {
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
						Long lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
					}

				} else {

					String cleanValue = textDecorationService.unifyToApostrophe(wordValue);
					String valueAsWord = textDecorationService.removeAccents(cleanValue);

					if (wordId == null) {
						int synWordHomNr = cudDbService.getWordNextHomonymNr(wordValue, wordLang);
						wordId = cudDbService.createWord(wordValue, wordValue, valueAsWord, wordLang, synWordHomNr);
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
						Long lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(functName, wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_ENABLED);
						activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
					} else {
						if (!existingWordIds.contains(wordId)) {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
							Long lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
						activityLog = activityLogService.prepareActivityLog(functName, wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_ENABLED);
						cudDbService.updateWordValueAndAsWordAndLang(wordId, wordValue, wordValue, valueAsWord, wordLang);
						activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
					}
				}
			}
		}

		return meaningId;
	}
}
