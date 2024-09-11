package eki.ekilex.service.api;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityFunct;
import eki.common.constant.ActivityOwner;
import eki.common.constant.Complexity;
import eki.common.constant.FreeformConstant;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.data.ActivityLog;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.Usage;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.Classifier;
import eki.ekilex.data.api.Definition;
import eki.ekilex.data.api.Forum;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.api.TermWord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.api.TermMeaningDbService;

@Component
public class TermMeaningService extends AbstractApiCudService implements ActivityFunct, FreeformConstant {

	private static final Complexity DEFAULT_COMPLEXITY = Complexity.DETAIL;

	private static final boolean DEFAULT_PUBLICITY = PUBLICITY_PUBLIC;

	@Autowired
	private TermMeaningDbService termMeaningDbService;

	@Transactional
	public TermMeaning getTermMeaning(Long meaningId, String datasetCode) {

		if (meaningId == null) {
			return null;
		}
		if (StringUtils.isBlank(datasetCode)) {
			return null;
		}
		TermMeaning termMeaning = termMeaningDbService.getTermMeaning(meaningId, datasetCode);
		if (termMeaning != null) {
			List<ActivityLog> firstCreateActivityLog = activityLogDbService.getActivityLog(meaningId, ActivityOwner.MEANING, ActivityEntity.MEANING, LIKE_CREATE);
			List<ActivityLog> manualUpdateActivityLog = activityLogDbService.getActivityLog(meaningId, ActivityOwner.MEANING, ActivityEntity.MEANING, UPDATE_MEANING_MANUAL_EVENT_ON_FUNCT);
			if (CollectionUtils.isNotEmpty(firstCreateActivityLog)) {
				ActivityLog activityLog = firstCreateActivityLog.get(0);
				LocalDateTime eventOn = activityLog.getEventOn().toLocalDateTime();
				String eventBy = activityLog.getEventBy();
				termMeaning.setFirstCreateEventOn(eventOn);
				termMeaning.setFirstCreateEventBy(eventBy);
			}
			if (CollectionUtils.isNotEmpty(manualUpdateActivityLog)) {
				ActivityLog activityLog = manualUpdateActivityLog.get(manualUpdateActivityLog.size() - 1);
				String eventBy = activityLog.getEventBy();
				termMeaning.setManualEventBy(eventBy);
			}
		}
		return termMeaning;
	}

	@Transactional
	public Long saveTermMeaning(TermMeaning termMeaning, String roleDatasetCode) throws Exception {

		final String updateFunctName = "updateTermMeaning";
		final String createFunctName = "createTermMeaning";

		String userName = userContext.getUserName();
		Long meaningId = termMeaning.getMeaningId();
		boolean isMeaningCreate = meaningId == null;
		String datasetCode = termMeaning.getDatasetCode();
		List<Definition> definitions = termMeaning.getDefinitions();
		List<TermWord> words = termMeaning.getWords();

		List<Classifier> domains = termMeaning.getDomains();
		List<MeaningNote> meaningNotes = termMeaning.getNotes();
		List<Forum> meaningForums = termMeaning.getForums();
		List<String> meaningTags = termMeaning.getTags();
		List<String> conceptIds = termMeaning.getConceptIds();
		LocalDateTime manualEventOn = termMeaning.getManualEventOn();
		String manualEventBy = termMeaning.getManualEventBy();
		LocalDateTime firstCreateEventOn = termMeaning.getFirstCreateEventOn();
		String firstCreateEventBy = termMeaning.getFirstCreateEventBy();

		ActivityLogData activityLog;

		if (isMeaningCreate) {
			meaningId = cudDbService.createMeaning();
		}

		if (CollectionUtils.isNotEmpty(definitions)) {

			for (Definition definition : definitions) {
				createOrUpdateDefinition(definition, meaningId, datasetCode, DEFAULT_COMPLEXITY, DEFAULT_PUBLICITY, roleDatasetCode);
			}
		}

		if (CollectionUtils.isNotEmpty(words)) {

			List<WordLexeme> meaningWords = lookupDbService.getMeaningWords(meaningId, datasetCode, null);
			List<Long> existingWordIds = meaningWords.stream().map(WordLexeme::getWordId).collect(Collectors.toList());

			for (TermWord word : words) {

				Long wordId = word.getWordId();
				String wordValuePrese = StringUtils.trim(word.getValuePrese());
				String wordLang = word.getLang();
				List<String> wordTypeCodes = word.getWordTypeCodes();
				boolean isValueOrLangMissing = StringUtils.isAnyBlank(wordValuePrese, wordLang);

				Long lexemeId;
				LexemeRecord lexeme = null;
				String lexemeValueStateCode = word.getLexemeValueStateCode();
				List<LexemeNote> lexemeNotes = word.getLexemeNotes();
				List<String> lexemeTags = word.getLexemeTags();
				List<Usage> usages = word.getUsages();
				boolean isPublic = word.isPublic();
				List<SourceLink> lexemeSourceLinks = word.getLexemeSourceLinks();

				if (isValueOrLangMissing) {
					if (wordId == null) {
						throw new OperationDeniedException("Word value and lang missing. Unable to create a new word");
					} else {
						if (existingWordIds.contains(wordId)) {
							lexeme = termMeaningDbService.getLexeme(wordId, meaningId, datasetCode);
							lexemeId = lexeme.getId();
						} else {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1, lexemeValueStateCode, isPublic);
							lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
					}
				} else {
					String wordValue = textDecorationService.removeEkiElementMarkup(wordValuePrese);
					String cleanValue = textDecorationService.unifyToApostrophe(wordValue);
					String valueAsWord = textDecorationService.removeAccents(cleanValue);

					if (wordId == null) {
						int synWordHomNr = cudDbService.getWordNextHomonymNr(wordValue, wordLang);
						wordId = cudDbService.createWord(wordValue, wordValuePrese, valueAsWord, wordLang, synWordHomNr);
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1, lexemeValueStateCode, isPublic);
						lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(createFunctName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					} else {
						if (existingWordIds.contains(wordId)) {
							lexeme = termMeaningDbService.getLexeme(wordId, meaningId, datasetCode);
							lexemeId = lexeme.getId();
						} else {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1, lexemeValueStateCode, isPublic);
							lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(createFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
						activityLog = activityLogService.prepareActivityLog(updateFunctName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						cudDbService.updateWordValueAndAsWordAndLang(wordId, wordValue, wordValuePrese, valueAsWord, wordLang);
						activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
					}
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

				boolean isUpdateLexeme = false;
				if (lexeme != null) {
					String existingValueStateCode = lexeme.getValueStateCode();
					Boolean existingIsPublic = lexeme.getIsPublic();
					isUpdateLexeme = !StringUtils.equals(existingValueStateCode, lexemeValueStateCode) || (existingIsPublic != isPublic);
				}
				if (isUpdateLexeme) {
					activityLog = activityLogService.prepareActivityLog(updateFunctName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					cudDbService.updateLexemeValueState(lexemeId, lexemeValueStateCode);
					cudDbService.updateLexemePublicity(lexemeId, isPublic);
					activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
				}

				if (CollectionUtils.isNotEmpty(lexemeNotes)) {

					for (LexemeNote lexemeNote : lexemeNotes) {

						Long lexemeNoteId = lexemeNote.getId();
						setValueAndPrese(lexemeNote);
						List<SourceLink> lexemeNoteSourceLinks = lexemeNote.getSourceLinks();

						if (lexemeNoteId == null) {
							applyCreateUpdate(lexemeNote);
							activityLog = activityLogService.prepareActivityLog("createLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
							lexemeNoteId = cudDbService.createLexemeNote(lexemeId, lexemeNote);
						} else {
							applyUpdate(lexemeNote);
							activityLog = activityLogService.prepareActivityLog("updateLexemeNote", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
							cudDbService.updateLexemeNote(lexemeNoteId, lexemeNote);
						}
						activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);

						if (CollectionUtils.isNotEmpty(lexemeNoteSourceLinks)) {

							for (SourceLink lexemeNoteSourceLink : lexemeNoteSourceLinks) {

								Long sourceLinkId = lexemeNoteSourceLink.getId();
								String sourceLinkName = lexemeNoteSourceLink.getName();
								if (sourceLinkId == null) {
									createLexemeNoteSourceLink(lexemeId, lexemeNoteId, lexemeNoteSourceLink, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
								} else {
									updateLexemeNoteSourceLink(lexemeId, sourceLinkId, sourceLinkName, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
								}
							}
						}
					}
				}

				if (CollectionUtils.isNotEmpty(lexemeTags)) {

					for (String tagName : lexemeTags) {

						boolean lexemeTagExists = lookupDbService.lexemeTagExists(lexemeId, tagName);
						if (!lexemeTagExists) {
							activityLog = activityLogService.prepareActivityLog("createLexemeTag", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
							Long lexemeTagId = cudDbService.createLexemeTag(lexemeId, tagName);
							activityLogService.createActivityLog(activityLog, lexemeTagId, ActivityEntity.TAG);
						}
					}
				}

				if (CollectionUtils.isNotEmpty(usages)) {

					for (Usage usage : usages) {
						createOrUpdateUsage(lexemeId, usage, DEFAULT_COMPLEXITY, DEFAULT_PUBLICITY, roleDatasetCode);
					}
				}

				if (CollectionUtils.isNotEmpty(lexemeSourceLinks)) {

					for (SourceLink lexemeSourceLink : lexemeSourceLinks) {

						Long sourceLinkId = lexemeSourceLink.getId();
						Long sourceId = lexemeSourceLink.getSourceId();
						String sourceLinkName = lexemeSourceLink.getName();
						if (sourceLinkId == null) {
							createLexemeSourceLink(lexemeId, sourceId, sourceLinkName, roleDatasetCode);
						} else {
							updateLexemeSourceLink(lexemeId, sourceLinkId, sourceLinkName, roleDatasetCode);
						}
					}
				}

			}
		}

		if (isMeaningCreate) {
			activityLogService.createActivityLog(createFunctName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}

		if (CollectionUtils.isNotEmpty(domains)) {

			for (Classifier domain : domains) {

				String domainCode = domain.getCode();
				String domainOrigin = domain.getOrigin();

				boolean meaningDomainExists = lookupDbService.meaningDomainExists(meaningId, domainCode, domainOrigin);
				if (meaningDomainExists) {
					continue;
				}

				eki.ekilex.data.Classifier classifier = new eki.ekilex.data.Classifier();
				classifier.setCode(domainCode);
				classifier.setOrigin(domainOrigin);

				activityLog = activityLogService.prepareActivityLog("createMeaningDomain", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, classifier);
				activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
			}
		}

		if (CollectionUtils.isNotEmpty(meaningNotes)) {

			for (MeaningNote meaningNote : meaningNotes) {

				Long meaningNoteId = meaningNote.getId();
				setValueAndPrese(meaningNote);
				List<SourceLink> meaningNoteSourceLinks = meaningNote.getSourceLinks();

				if (meaningNoteId == null) {
					applyCreateUpdate(meaningNote);
					activityLog = activityLogService.prepareActivityLog("createMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					meaningNoteId = cudDbService.createMeaningNote(meaningId, meaningNote);
				} else {
					applyUpdate(meaningNote);
					activityLog = activityLogService.prepareActivityLog("updateMeaningNote", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					cudDbService.updateMeaningNote(meaningNoteId, meaningNote);
				}
				activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);

				if (CollectionUtils.isNotEmpty(meaningNoteSourceLinks)) {

					for (SourceLink meaningNoteSourceLink : meaningNoteSourceLinks) {

						Long sourceLinkId = meaningNoteSourceLink.getId();
						String sourceLinkName = meaningNoteSourceLink.getName();
						if (sourceLinkId == null) {
							createMeaningNoteSourceLink(meaningId, meaningNoteId, meaningNoteSourceLink, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						} else {
							updateMeaningNoteSourceLink(meaningId, sourceLinkId, sourceLinkName, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningForums)) {

			Long userId = userContext.getUserId();
			for (Forum meaningForum : meaningForums) {

				Long meaningForumId = meaningForum.getId();
				if (meaningForumId == null) {
					String meaningForumValue = meaningForum.getValue();
					if (StringUtils.isBlank(meaningForumValue)) {
						throw new OperationDeniedException("Meaning forum value missing");
					}
					cudDbService.createMeaningForum(meaningId, meaningForumValue, meaningForumValue, userId, userName);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningTags)) {

			for (String tagName : meaningTags) {
				if (StringUtils.isBlank(tagName)) {
					throw new OperationDeniedException("Meaning tag value missing");
				}
				boolean meaningTagExists = lookupDbService.meaningTagExists(meaningId, tagName);
				if (!meaningTagExists) {
					activityLog = activityLogService.prepareActivityLog("createMeaningTag", meaningId, ActivityOwner.MEANING, roleDatasetCode,
							MANUAL_EVENT_ON_UPDATE_ENABLED);
					Long meaningTagId = cudDbService.createMeaningTag(meaningId, tagName);
					activityLogService.createActivityLog(activityLog, meaningTagId, ActivityEntity.TAG);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(conceptIds)) {

			for (String conceptId : conceptIds) {
				boolean meaningConceptIdExists = lookupDbService.meaningFreeformExists(meaningId, conceptId, CONCEPT_ID_CODE);
				if (!meaningConceptIdExists) {
					FreeForm freeform = new FreeForm();
					freeform.setFreeformTypeCode(CONCEPT_ID_CODE);
					freeform.setValueText(conceptId);
					freeform.setValuePrese(conceptId);
					freeform.setLang(null);
					freeform.setComplexity(Complexity.DETAIL);
					freeform.setPublic(PUBLICITY_PUBLIC);
					activityLog = activityLogService.prepareActivityLog("createMeaningFreeform", meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					Long meaningFreeformId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
					activityLogService.createActivityLog(activityLog, meaningFreeformId, ActivityEntity.CONCEPT_ID);
				}
			}
		}

		if (manualEventOn != null) {

			Timestamp manualEventOnTs = Timestamp.valueOf(manualEventOn);
			activityLog = activityLogService.prepareActivityLog(UPDATE_MEANING_MANUAL_EVENT_ON_FUNCT, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_DISABLED);
			if (StringUtils.isNotBlank(manualEventBy)) {
				activityLog.setEventBy(manualEventBy);
			}
			activityLogDbService.updateMeaningManualEventOn(meaningId, manualEventOnTs);
			activityLogService.createActivityLog(activityLog, meaningId, ActivityEntity.MEANING);
		}

		if (firstCreateEventOn != null) {

			Timestamp firstCreateEventOnTs = Timestamp.valueOf(firstCreateEventOn);
			if (StringUtils.isBlank(firstCreateEventBy)) {
				firstCreateEventBy = userName;
			}
			activityLogDbService.updateMeaningFirstCreateEvent(meaningId, firstCreateEventOnTs, firstCreateEventBy);
		}

		return meaningId;
	}

	private void createLexemeSourceLink(Long lexemeId, Long sourceId, String sourceLinkName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("createLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceId, DEFAULT_SOURCE_LINK_REF_TYPE, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	private void updateLexemeSourceLink(Long lexemeId, Long sourceLinkId, String sourceLinkName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("updateLexemeSourceLink", lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

}
