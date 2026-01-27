package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Definition;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeVariantBean;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Response;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordCandidates;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.VariantDbService;
import eki.ekilex.service.util.ValueUtil;

@Component
public class VariantService implements SystemConstant, GlobalConstant {

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private VariantDbService variantDbService;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private ValueUtil valueUtil;

	@Autowired
	private MessageSource messageSource;

	@Transactional(rollbackFor = Exception.class)
	public Response createLexemeVariant(
			LexemeVariantBean lexemeVariantBean,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		String providedVariantWordValue = lexemeVariantBean.getWordValue();
		Long headwordLexemeId = lexemeVariantBean.getHeadwordLexemeId();
		String variantTypeCode = lexemeVariantBean.getVariantTypeCode();
		boolean isForceHomonym = lexemeVariantBean.isForceHomonym();
		Response response;

		String variantWordValuePrese = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(providedVariantWordValue);
		String variantWordValue = textDecorationService.removeEkiElementMarkup(variantWordValuePrese);
		WordLexemeMeaningDetails headword = lookupDbService.getWordLexemeMeaningDetailsByLexemeId(headwordLexemeId);

		if (headword == null) {

			String message = "No such headword lexeme";
			response = composeResponse(ResponseStatus.ERROR, message);

		} else {

			String lang = headword.getLanguage();
			boolean variantWordExists = lookupDbService.wordExists(variantWordValue, lang);

			if (isForceHomonym) {

				response = createLexemeVariant(headword, variantWordValue, variantWordValuePrese, variantTypeCode, roleDatasetCode, isManualEventOnUpdateEnabled);

			} else if (variantWordExists) {

				String message = "Variant word already exists";
				response = composeResponse(ResponseStatus.INVALID, message);

			} else {

				response = createLexemeVariant(headword, variantWordValue, variantWordValuePrese, variantTypeCode, roleDatasetCode, isManualEventOnUpdateEnabled);
			}
		}

		return response;
	}

	private Response createLexemeVariant(
			WordLexemeMeaningDetails headword,
			String variantWordValue,
			String variantWordValuePrese,
			String variantTypeCode,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		Long headwordLexemeId = headword.getLexemeId();
		Long meaningId = headword.getMeaningId();
		String lang = headword.getLanguage();
		String datasetCode = headword.getDataset();
		String variantWordValueAsWord = textDecorationService.getValueAsWord(variantWordValue);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeVariant", headwordLexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		int variantWordHomNr = cudDbService.getWordNextHomonymNr(variantWordValue, lang);
		Long variantWordId = cudDbService.createWord(variantWordValue, variantWordValuePrese, variantWordValueAsWord, lang, variantWordHomNr);
		Long variantLexemeId = cudDbService.createLexeme(variantWordId, meaningId, datasetCode, 1, null, PUBLICITY_PUBLIC);
		Long lexemeVariantId = variantDbService.createLexemeVariant(headwordLexemeId, variantLexemeId, variantTypeCode);
		activityLogService.createActivityLog(activityLog, lexemeVariantId, ActivityEntity.LEXEME_VARIANT);

		Locale locale = LocaleContextHolder.getLocale();
		String message = messageSource.getMessage("lex.variant.created", new Object[0], locale);
		Response response = composeResponse(ResponseStatus.OK, message);
		return response;
	}

	public void deleteLexemeVariant(Long lexemeVariantId, String roleDatasetCode, boolean isManualEventOnUpdateEnabled) throws Exception {

		Long lexemeId = activityLogService.getActivityOwnerId(lexemeVariantId, ActivityEntity.LEXEME_VARIANT);
		ActivityLogData activityLog = activityLogService.prepareActivityLog("deleteLexemeVariant", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		Long lexemeVariantLexemeId = variantDbService.getLexemeVariantLexemeId(lexemeVariantId);
		variantDbService.deleteLexemeVariant(lexemeVariantId);
		cudDbService.deleteLexeme(lexemeVariantLexemeId);
		activityLogService.createActivityLog(activityLog, lexemeVariantId, ActivityEntity.LEXEME_VARIANT);
	}

	private Response composeResponse(ResponseStatus responseStatus, String message) {
		Response response = new Response();
		response.setStatus(responseStatus);
		response.setMessage(message);
		return response;
	}

	@Transactional
	public WordCandidates getLexemeVariantWordCandidates(LexemeVariantBean lexemeVariantBean) throws Exception {

		String wordValue = lexemeVariantBean.getWordValue();
		Long headwordLexemeId = lexemeVariantBean.getHeadwordLexemeId();

		wordValue = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(wordValue);
		wordValue = textDecorationService.removeEkiElementMarkup(wordValue);
		WordLexemeMeaningDetails headword = lookupDbService.getWordLexemeMeaningDetailsByLexemeId(headwordLexemeId);
		String lang = headword.getLanguage();
		String datasetCode = headword.getDataset();

		List<Word> words = variantDbService.getWords(wordValue, lang);
		List<WordDescript> wordDescripts = new ArrayList<>();

		for (Word word : words) {

			Long wordId = word.getWordId();
			List<Lexeme> lexemes = variantDbService.getWordLexemes(wordId, CLASSIF_LABEL_LANG_EST);

			List<String> allDefinitionValues = new ArrayList<>();
			lexemes.forEach(lexeme -> {
				Long lexemeId = lexeme.getLexemeId();
				Long meaningId = lexeme.getMeaningId();
				List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId);
				lexeme.setMeaningWords(meaningWords);
				lexeme.setLexemeWord(word);
				List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST);
				List<String> lexemeDefinitionValues = definitions.stream()
						.map(Definition::getValue)
						.collect(Collectors.toList());
				allDefinitionValues.addAll(lexemeDefinitionValues);
			});
			List<String> distinctDefinitionValues = allDefinitionValues.stream()
					.distinct()
					.collect(Collectors.toList());
			WordDescript wordCandidate = new WordDescript();
			wordCandidate.setWord(word);
			wordCandidate.setLexemes(lexemes);
			wordCandidate.setDefinitions(distinctDefinitionValues);
			wordDescripts.add(wordCandidate);
		}

		WordCandidates wordCandidates = new WordCandidates();
		wordCandidates.setLexemeVariantBean(lexemeVariantBean);
		wordCandidates.setWords(wordDescripts);

		return wordCandidates;
	}
}
