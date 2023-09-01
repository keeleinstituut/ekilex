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
import eki.common.constant.FreeformType;
import eki.common.exception.OperationDeniedException;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.Classifier;
import eki.ekilex.data.api.Definition;
import eki.ekilex.data.api.Forum;
import eki.ekilex.data.api.Freeform;
import eki.ekilex.data.api.SourceLink;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.api.TermWord;
import eki.ekilex.service.db.api.TermMeaningDbService;

@Component
public class TermMeaningService extends AbstractApiCudService {

	private static final Complexity DEFAULT_COMPLEXITY = Complexity.DETAIL;

	private static final boolean DEFAULT_LEXEME_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_MEANING_NOTE_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_LEXEME_NOTE_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_USAGE_PUBLICITY = PUBLICITY_PUBLIC;

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
		return termMeaningDbService.getTermMeaning(meaningId, datasetCode);
	}

	@Transactional
	public Long saveTermMeaning(TermMeaning termMeaning, String roleDatasetCode) throws Exception {

		final String functName = "saveTermMeaning";

		Long meaningId = termMeaning.getMeaningId();
		String datasetCode = termMeaning.getDatasetCode();
		List<Definition> definitions = termMeaning.getDefinitions();
		List<TermWord> words = termMeaning.getWords();

		List<Classifier> domains = termMeaning.getDomains();
		List<Freeform> meaningNotes = termMeaning.getNotes();
		List<Forum> meaningForums = termMeaning.getForums();

		ActivityLogData activityLog;

		if (meaningId == null) {
			meaningId = cudDbService.createMeaning();
			activityLogService.createActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		}

		if (CollectionUtils.isNotEmpty(definitions)) {

			for (Definition definition : definitions) {
				createOrUpdateDefinition(definition, meaningId, datasetCode, DEFAULT_COMPLEXITY, functName, roleDatasetCode);
			}
		}

		if (CollectionUtils.isNotEmpty(words)) {

			List<WordLexeme> meaningWords = lookupDbService.getMeaningWords(meaningId, datasetCode, null);
			List<Long> existingWordIds = meaningWords.stream().map(WordLexeme::getWordId).collect(Collectors.toList());

			for (TermWord word : words) {

				Long wordId = word.getWordId();
				String wordValue = StringUtils.trim(word.getValue());
				String wordLang = word.getLang();
				List<String> wordTypeCodes = word.getWordTypeCodes();

				Long lexemeId;
				String lexemeValueStateCode = word.getLexemeValueStateCode();
				List<Freeform> lexemeNotes = word.getLexemeNotes();
				List<Freeform> usages = word.getUsages();
				boolean isLexemePublic = isPublic(word.getLexemePublicity(), DEFAULT_LEXEME_PUBLICITY);
				List<SourceLink> lexemeSourceLinks = word.getLexemeSourceLinks();

				if (StringUtils.isAnyBlank(wordValue, wordLang)) {
					if (wordId == null) {
						throw new OperationDeniedException("Word value and lang missing. Unable to create a new word");
					} else {
						if (existingWordIds.contains(wordId)) {
							lexemeId = lookupDbService.getLexemeId(wordId, meaningId);
						} else {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
							lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
					}
				} else {
					String cleanValue = textDecorationService.unifyToApostrophe(wordValue);
					String valueAsWord = textDecorationService.removeAccents(cleanValue);

					if (wordId == null) {
						int synWordHomNr = cudDbService.getWordNextHomonymNr(wordValue, wordLang);
						wordId = cudDbService.createWord(wordValue, wordValue, valueAsWord, wordLang, synWordHomNr);
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
						lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(functName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					} else {
						if (existingWordIds.contains(wordId)) {
							lexemeId = lookupDbService.getLexemeId(wordId, meaningId);
						} else {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
							lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
						activityLog = activityLogService.prepareActivityLog(functName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						cudDbService.updateWordValueAndAsWordAndLang(wordId, wordValue, wordValue, valueAsWord, wordLang);
						activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
					}
				}

				if (CollectionUtils.isNotEmpty(wordTypeCodes)) {

					List<String> existingWordTypeCodes = lookupDbService.getWordTypeCodes(wordId);
					for (String wordTypeCode : wordTypeCodes) {
						if (!existingWordTypeCodes.contains(wordTypeCode)) {
							activityLog = activityLogService.prepareActivityLog(functName, wordId, ActivityOwner.WORD, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
							Long wordTypeId = cudDbService.createWordType(wordId, wordTypeCode);
							activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
						}
					}
				}

				if (StringUtils.isNoneBlank(wordValue, wordLang)) {
					activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					cudDbService.updateLexemeValueState(lexemeId, lexemeValueStateCode);
					cudDbService.updateLexemePublicity(lexemeId, isLexemePublic);
					activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
				}

				if (CollectionUtils.isNotEmpty(lexemeNotes)) {

					String userName = userContext.getUserName();
					for (Freeform lexemeNote : lexemeNotes) {

						Long lexemeNoteId = lexemeNote.getId();
						String lexemeNoteValue = lexemeNote.getValue();
						String lexemeNoteLang = lexemeNote.getLang();
						boolean isLexemeNotePublic = isPublic(lexemeNote.getPublicity(), DEFAULT_LEXEME_NOTE_PUBLICITY);
						List<SourceLink> lexemeNoteSourceLinks = lexemeNote.getSourceLinks();

						FreeForm freeform = initFreeform(FreeformType.NOTE, lexemeNoteValue, lexemeNoteLang, isLexemeNotePublic);

						activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
						if (lexemeNoteId == null) {
							lexemeNoteId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
						} else {
							freeform.setId(lexemeNoteId);
							cudDbService.updateFreeform(freeform, userName);
						}
						activityLogService.createActivityLog(activityLog, lexemeNoteId, ActivityEntity.LEXEME_NOTE);

						if (CollectionUtils.isNotEmpty(lexemeNoteSourceLinks)) {

							for (SourceLink lexemeNoteSourceLink : lexemeNoteSourceLinks) {
								Long sourceLinkId = lexemeNoteSourceLink.getSourceLinkId();
								Long sourceId = lexemeNoteSourceLink.getSourceId();
								String sourceLinkValue = lexemeNoteSourceLink.getValue();
								if (sourceLinkId == null) {
									createFreeformSourceLink(
											lexemeNoteId, sourceId, sourceLinkValue, DEFAULT_SOURCE_LINK_REF_TYPE, ActivityOwner.LEXEME,
											lexemeId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK, functName, roleDatasetCode);
								} else {
									updateFreeformSourceLink(
											sourceLinkId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK,
											functName, roleDatasetCode);
								}
							}
						}
					}
				}

				if (CollectionUtils.isNotEmpty(usages)) {

					for (Freeform usage : usages) {
						createOrUpdateUsage(usage, lexemeId, DEFAULT_USAGE_PUBLICITY, functName, roleDatasetCode);
					}
				}

				if (CollectionUtils.isNotEmpty(lexemeSourceLinks)) {

					for (SourceLink lexemeSourceLink : lexemeSourceLinks) {
						Long sourceLinkId = lexemeSourceLink.getSourceLinkId();
						Long sourceId = lexemeSourceLink.getSourceId();
						String sourceLinkValue = lexemeSourceLink.getValue();
						if (sourceLinkId == null) {
							createLexemeSourceLink(lexemeId, sourceId, sourceLinkValue, functName, roleDatasetCode);
						} else {
							updateLexemeSourceLink(sourceLinkId, lexemeId, sourceLinkValue, functName, roleDatasetCode);
						}
					}
				}

			}
		}

		if (CollectionUtils.isNotEmpty(domains)) {

			for (Classifier domain : domains) {

				String domainCode = domain.getCode();
				String domainOrigin = domain.getOrigin();

				boolean meaningDomainExists = lookupDbService.meaningDomainExists(meaningId, domainCode, domainOrigin);
				if (!meaningDomainExists) {
					eki.ekilex.data.Classifier classifier = new eki.ekilex.data.Classifier();
					classifier.setCode(domainCode);
					classifier.setOrigin(domainOrigin);

					activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
					Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, classifier);
					activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningNotes)) {

			String userName = userContext.getUserName();
			for (Freeform meaningNote : meaningNotes) {

				Long meaningNoteId = meaningNote.getId();
				String meaningNoteValue = meaningNote.getValue();
				String meaningNoteLang = meaningNote.getLang();
				boolean isMeaningNotePublic = isPublic(meaningNote.getPublicity(), DEFAULT_MEANING_NOTE_PUBLICITY);
				List<SourceLink> meaningNoteSourceLinks = meaningNote.getSourceLinks();

				FreeForm freeform = initFreeform(FreeformType.NOTE, meaningNoteValue, meaningNoteLang, isMeaningNotePublic);

				activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
				if (meaningNoteId == null) {
					meaningNoteId = cudDbService.createMeaningFreeform(meaningId, freeform, userName);
				} else {
					freeform.setId(meaningNoteId);
					cudDbService.updateFreeform(freeform, userName);
				}
				activityLogService.createActivityLog(activityLog, meaningNoteId, ActivityEntity.MEANING_NOTE);

				if (CollectionUtils.isNotEmpty(meaningNoteSourceLinks)) {

					for (SourceLink meaningNoteSourceLink : meaningNoteSourceLinks) {
						Long sourceLinkId = meaningNoteSourceLink.getSourceLinkId();
						Long sourceId = meaningNoteSourceLink.getSourceId();
						String sourceLinkValue = meaningNoteSourceLink.getValue();
						if (sourceLinkId == null) {
							createFreeformSourceLink(
									meaningNoteId, sourceId, sourceLinkValue, DEFAULT_SOURCE_LINK_REF_TYPE, ActivityOwner.MEANING, meaningId,
									ActivityEntity.MEANING_NOTE_SOURCE_LINK, functName, roleDatasetCode);
						} else {
							updateFreeformSourceLink(
									sourceLinkId, sourceLinkValue, ActivityOwner.MEANING, meaningId, ActivityEntity.MEANING_NOTE_SOURCE_LINK,
									functName, roleDatasetCode);
						}
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningForums)) {

			Long userId = userContext.getUserId();
			String userName = userContext.getUserName();
			for (Forum meaningForum : meaningForums) {

				Long meaningForumId = meaningForum.getId();
				if (meaningForumId == null) {
					String meaningForumValue = meaningForum.getValue();
					cudDbService.createMeaningForum(meaningId, meaningForumValue, meaningForumValue, userId, userName);
				}
			}
		}

		return meaningId;
	}

	private void createLexemeSourceLink(Long lexemeId, Long sourceId, String sourceLinkValue, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceId, DEFAULT_SOURCE_LINK_REF_TYPE, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	private void updateLexemeSourceLink(Long sourceLinkId, Long lexemeId, String sourceLinkValue, String functName, String roleDatasetCode) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, roleDatasetCode, MANUAL_EVENT_ON_UPDATE_ENABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

}
