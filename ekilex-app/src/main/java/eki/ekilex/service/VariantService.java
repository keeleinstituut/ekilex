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

import eki.common.constant.GlobalConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Definition;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.Response;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDescript;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordVariant;
import eki.ekilex.data.WordVariantCandidates;
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
	private ValueUtil valueUtil;

	@Autowired
	private MessageSource messageSource;

	@Transactional(rollbackFor = Exception.class)
	public Response createWordVariant(
			WordVariant wordVariant,
			String roleDatasetCode,
			boolean isManualEventOnUpdateEnabled) throws Exception {

		String providedVariantWordValue = wordVariant.getWordValue();
		Long headwordLexemeId = wordVariant.getHeadwordLexemeId();
		String variantTypeCode = wordVariant.getVariantTypeCode();
		boolean isForceHomonym = wordVariant.isForceHomonym();
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

				response = createWordVariant(headword, variantWordValue, variantWordValuePrese, variantTypeCode);

			} else if (variantWordExists) {

				String message = "Variant word already exists";
				response = composeResponse(ResponseStatus.INVALID, message);

			} else {

				response = createWordVariant(headword, variantWordValue, variantWordValuePrese, variantTypeCode);
			}
		}

		return response;
	}

	private Response createWordVariant(WordLexemeMeaningDetails headword, String variantWordValue, String variantWordValuePrese, String variantTypeCode) {

		Locale locale = LocaleContextHolder.getLocale();
		Long headwordLexemeId = headword.getLexemeId();
		Long meaningId = headword.getMeaningId();
		String lang = headword.getLanguage();
		String datasetCode = headword.getDataset();
		String variantWordValueAsWord = textDecorationService.getValueAsWord(variantWordValue);
		int variantWordHomNr = cudDbService.getWordNextHomonymNr(variantWordValue, lang);
		Long variantWordId = cudDbService.createWord(variantWordValue, variantWordValuePrese, variantWordValueAsWord, lang, variantWordHomNr);
		Long variantLexemeId = cudDbService.createLexeme(variantWordId, meaningId, datasetCode, 1, null, PUBLICITY_PUBLIC);
		variantDbService.createLexemeVariant(headwordLexemeId, variantLexemeId, variantTypeCode);
		String message = messageSource.getMessage("lex.variant.created", new Object[0], locale);

		// TODO create activity log

		Response response = composeResponse(ResponseStatus.OK, message);
		return response;
	}

	private Response composeResponse(ResponseStatus responseStatus, String message) {
		Response response = new Response();
		response.setStatus(responseStatus);
		response.setMessage(message);
		return response;
	}

	@Transactional
	public WordVariantCandidates getWordVariantCandidates(WordVariant wordVariant) throws Exception {

		String wordValue = wordVariant.getWordValue();
		Long headwordLexemeId = wordVariant.getHeadwordLexemeId();

		wordValue = valueUtil.trimAndCleanAndRemoveHtmlAndLimit(wordValue);
		wordValue = textDecorationService.removeEkiElementMarkup(wordValue);
		WordLexemeMeaningDetails headword = lookupDbService.getWordLexemeMeaningDetailsByLexemeId(headwordLexemeId);
		String lang = headword.getLanguage();
		String datasetCode = headword.getDataset();

		List<Word> words = variantDbService.getWords(wordValue, lang, datasetCode);
		List<WordDescript> wordCandidates = new ArrayList<>();

		for (Word word : words) {

			Long wordId = word.getWordId();
			List<Lexeme> lexemes = variantDbService.getWordLexemes(wordId, datasetCode, CLASSIF_LABEL_LANG_EST);

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
			wordCandidates.add(wordCandidate);
		}

		WordVariantCandidates wordVariantCandidates = new WordVariantCandidates();
		wordVariantCandidates.setWordVariant(wordVariant);
		wordVariantCandidates.setWords(wordCandidates);

		return wordVariantCandidates;
	}
}
