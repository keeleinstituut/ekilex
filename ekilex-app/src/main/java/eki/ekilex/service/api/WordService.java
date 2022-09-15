package eki.ekilex.service.api;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.Word;
import eki.ekilex.service.AbstractService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.TagDbService;

@Component
public class WordService extends AbstractService {

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private TagDbService tagDbService;

	@Transactional
	public Long createWord(Word word, boolean isManualEventOnUpdateEnabled) throws Exception {

		String value = word.getValue();
		String valueAsWord = getValueAsWord(value);
		Long meaningId = word.getMeaningId();
		boolean isMeaningCreate = meaningId == null;

		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createWordAndLexemeAndMeaning(word, valueAsWord);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		meaningId = wordLexemeMeaningId.getMeaningId();

		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createWord", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createWord", lexemeId, ActivityOwner.LEXEME, isManualEventOnUpdateEnabled);
		if (isMeaningCreate) {
			activityLogService.createActivityLog("createWord", meaningId, ActivityOwner.MEANING, isManualEventOnUpdateEnabled);
		}

		return wordId;
	}

	@Transactional
	public void updateWord(Word word, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long wordId = word.getWordId();
		String value = word.getValue();
		String lang = word.getLang();
		String valueAsWord = getValueAsWord(value);

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateWord", wordId, ActivityOwner.WORD, isManualEventOnUpdateEnabled);

		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		cudDbService.updateWord(word, valueAsWord);
		SimpleWord updatedWord = new SimpleWord(wordId, value, lang);

		cudDbService.adjustWordHomonymNrs(originalWord);
		cudDbService.adjustWordHomonymNrs(updatedWord);

		activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
	}

	private String getValueAsWord(String value) {

		String valueAsWord;
		value = textDecorationService.removeEkiElementMarkup(value);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		return valueAsWord;
	}
}
