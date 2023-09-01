package eki.ekilex.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.ResponseStatus;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Response;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.SourceLink;
import eki.ekilex.data.api.SynCandidacy;
import eki.ekilex.data.api.SynCandidateWord;
import eki.ekilex.data.api.TextWithSource;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SourceLinkDbService;
import eki.ekilex.service.db.SynSearchDbService;

@Component
public class SynCandidateService extends AbstractCudService implements GlobalConstant {

	private static final Logger logger = LoggerFactory.getLogger(SynCandidateService.class);

	private static final BigDecimal DEFAULT_SYN_CANDIDATE_WEIGHT = BigDecimal.valueOf(1);

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private SourceLinkDbService sourceLinkDbService;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Transactional
	public void createFullSynCandidacy(SynCandidacy synCandidacy, String roleDatasetCode) throws Exception {

		String headwordValue = synCandidacy.getHeadwordValue();
		String headwordLang = synCandidacy.getHeadwordLang();
		String synCandidateDatasetCode = synCandidacy.getSynCandidateDatasetCode();
		List<SynCandidateWord> synCandidateWords = synCandidacy.getSynCandidateWords();

		logger.info("Creating syn {} candidates for \"{}\" ({}) in \"{}\"", synCandidateWords.size(), headwordValue, headwordLang, synCandidateDatasetCode);

		List<Word> existingHeadwords = lookupDbService.getWords(headwordValue, headwordLang);

		for (SynCandidateWord synCandidateWord : synCandidateWords) {

			BigDecimal synLexemeWeight = synCandidateWord.getWeight();

			WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId = handleSynCandidateWord(synCandidateDatasetCode, synCandidateWord, roleDatasetCode);

			for (Word headword : existingHeadwords) {
				Long headwordId = headword.getWordId();
				Long synCandidateWordId = synCandidateWordLexemeMeaningId.getWordId();
				createSynCandidateWordRelation(headwordId, synCandidateWordId, synLexemeWeight);
			}
		}
	}

	@Transactional
	public Response createFullSynCandidate(Long headwordId, String synCandidateWordValue, String synCandidateWordLang, String synCandidateDatasetCode, String roleDatasetCode) throws Exception {

		Locale locale = LocaleContextHolder.getLocale();
		Response response = new Response();

		boolean synCandidateWordRelationExists = synSearchDbService
				.synCandidateWordRelationExists(headwordId, synCandidateWordValue, WORD_REL_TYPE_CODE_RAW, synCandidateWordLang, synCandidateDatasetCode);
		if (synCandidateWordRelationExists) {
			String errorMessage = messageSource.getMessage("fullsyn.candidate.exists", new Object[0], locale);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(errorMessage);
			return response;
		}

		WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId = createSynCandidateWordAndLexemeAndMeaning(synCandidateWordValue, synCandidateWordLang, synCandidateDatasetCode, roleDatasetCode);
		Long synCandidateWordId = synCandidateWordLexemeMeaningId.getWordId();
		createSynCandidateWordRelation(headwordId, synCandidateWordId, DEFAULT_SYN_CANDIDATE_WEIGHT);

		String successMessage = messageSource.getMessage("common.create.success", new Object[0], locale);
		response.setStatus(ResponseStatus.OK);
		response.setMessage(successMessage);
		return response;
	}

	private WordLexemeMeaningIdTuple handleSynCandidateWord(String synCandidateDatasetCode, SynCandidateWord synCandidateWord, String roleDatasetCode) throws Exception {

		String synCandidateWordValue = synCandidateWord.getValue();
		String synCandidateWordLang = synCandidateWord.getLang();
		List<String> synPosCodes = synCandidateWord.getPosCodes();
		List<TextWithSource> synDefinitions = synCandidateWord.getDefinitions();
		List<TextWithSource> synUsages = synCandidateWord.getUsages();

		WordLexemeMeaningIdTuple synCandidateWordLexemeMeaningId = createSynCandidateWordAndLexemeAndMeaning(synCandidateWordValue, synCandidateWordLang, synCandidateDatasetCode, roleDatasetCode);

		Long synCandidateLexemeId = synCandidateWordLexemeMeaningId.getLexemeId();
		Long synCandidateMeaningId = synCandidateWordLexemeMeaningId.getMeaningId();

		if (CollectionUtils.isNotEmpty(synPosCodes)) {
			for (String posCode : synPosCodes) {
				createLexemePos(synCandidateLexemeId, posCode, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
			}
		}
		if (CollectionUtils.isNotEmpty(synUsages)) {
			for (TextWithSource usage : synUsages) {
				String usageValue = usage.getValue();
				List<SourceLink> usageSourceLinks = usage.getSourceLinks();
				Long usageId = createUsage(synCandidateLexemeId, usageValue, synCandidateWordLang, Complexity.DETAIL, PUBLICITY_PRIVATE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
				if (CollectionUtils.isNotEmpty(usageSourceLinks)) {
					for (SourceLink usageSourceLink : usageSourceLinks) {
						Long sourceId = usageSourceLink.getSourceId();
						String sourceLinkValue = usageSourceLink.getValue();
						createUsageSourceLink(usageId, sourceId, synCandidateLexemeId, sourceLinkValue, roleDatasetCode);
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(synDefinitions)) {
			for (TextWithSource definition : synDefinitions) {
				String definitionValue = definition.getValue();
				List<SourceLink> definitionSourceLinks = definition.getSourceLinks();
				Long definitionId = createDefinition(
						synCandidateMeaningId, definitionValue, synCandidateWordLang, synCandidateDatasetCode, Complexity.DETAIL,
						DEFINITION_TYPE_CODE_UNDEFINED, PUBLICITY_PRIVATE, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
				if (CollectionUtils.isNotEmpty(definitionSourceLinks)) {
					for (SourceLink definitionSourceLink : definitionSourceLinks) {
						Long sourceId = definitionSourceLink.getSourceId();
						String sourceLinkValue = definitionSourceLink.getValue();
						createDefinitionSourceLink(definitionId, sourceId, synCandidateMeaningId, sourceLinkValue, roleDatasetCode);
					}
				}
			}
		}

		return synCandidateWordLexemeMeaningId;
	}

	private WordLexemeMeaningIdTuple createSynCandidateWordAndLexemeAndMeaning(String value, String lang, String datasetCode, String roleDatasetCode) throws Exception {

		value = textDecorationService.removeEkiElementMarkup(value);
		String cleanValue = textDecorationService.unifyToApostrophe(value);
		String valueAsWord = textDecorationService.removeAccents(cleanValue);
		if (StringUtils.isBlank(valueAsWord) && !StringUtils.equals(value, cleanValue)) {
			valueAsWord = cleanValue;
		}
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService.createPrivateWordAndLexemeAndMeaning(value, value, valueAsWord, value, lang, datasetCode);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();
		boolean isManualEventOnUpdateEnabled = MANUAL_EVENT_ON_UPDATE_DISABLED;
		tagDbService.createLexemeAutomaticTags(lexemeId);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", wordId, ActivityOwner.WORD, roleDatasetCode, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, isManualEventOnUpdateEnabled);
		activityLogService.createActivityLog("createSynCandidateWordAndLexemeAndMeaning", meaningId, ActivityOwner.MEANING, roleDatasetCode, isManualEventOnUpdateEnabled);

		return wordLexemeMeaningId;
	}

	private void createSynCandidateWordRelation(Long headwordId, Long synCandidateWordId, BigDecimal weight) {

		Long createdRelationId = cudDbService.createWordRelation(headwordId, synCandidateWordId, WORD_REL_TYPE_CODE_RAW, UNDEFINED_RELATION_STATUS);
		moveCreatedWordRelationToFirst(headwordId, createdRelationId, WORD_REL_TYPE_CODE_RAW);
		cudDbService.createWordRelationParam(createdRelationId, WORD_RELATION_PARAM_NAME_SYN_CANDIDATE, weight);
	}

	private void createUsageSourceLink(Long usageId, Long sourceId, Long lexemeId, String sourceLinkValue, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createUsageSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(usageId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.USAGE_SOURCE_LINK);
	}

	private void createDefinitionSourceLink(Long definitionId, Long sourceId, Long meaningId, String sourceLinkValue, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createDefinitionSourceLink", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}
}
