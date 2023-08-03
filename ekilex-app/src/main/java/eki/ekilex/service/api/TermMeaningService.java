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
import eki.common.constant.GlobalConstant;
import eki.common.constant.ReferenceType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.api.SourceLink;
import eki.ekilex.data.api.TermClassifier;
import eki.ekilex.data.api.TermDefinition;
import eki.ekilex.data.api.TermForum;
import eki.ekilex.data.api.TermFreeform;
import eki.ekilex.data.api.TermMeaning;
import eki.ekilex.data.api.TermWord;
import eki.ekilex.service.AbstractService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.SourceLinkDbService;
import eki.ekilex.service.db.api.TermMeaningDbService;

@Component
public class TermMeaningService extends AbstractService implements GlobalConstant {

	private static final Complexity DEFAULT_COMPLEXITY = Complexity.DETAIL;

	private static final boolean DEFAULT_LEXEME_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_MEANING_NOTE_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_LEXEME_NOTE_PUBLICITY = PUBLICITY_PUBLIC;

	private static final boolean DEFAULT_USAGE_PUBLICITY = PUBLICITY_PUBLIC;

	@Autowired
	private TermMeaningDbService termMeaningDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private SourceLinkDbService sourceLinkDbService;

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

		List<TermClassifier> domains = termMeaning.getDomains();
		List<TermFreeform> meaningNotes = termMeaning.getNotes();
		List<TermForum> meaningForums = termMeaning.getForums();

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
				List<SourceLink> definitionSourceLinks = definition.getSourceLinks();
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

				if (CollectionUtils.isNotEmpty(definitionSourceLinks)) {

					for (SourceLink definitionSourceLink : definitionSourceLinks) {
						Long sourceLinkId = definitionSourceLink.getSourceLinkId();
						Long sourceId = definitionSourceLink.getSourceId();
						String sourceLinkValue = definitionSourceLink.getValue();
						if (sourceLinkId == null) {
							createDefinitionSourceLink(definitionId, sourceId, meaningId, sourceLinkValue, functName);
						} else {
							updateDefinitionSourceLink(sourceLinkId, meaningId, sourceLinkValue, functName);
						}
					}
				}
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
				List<TermFreeform> lexemeNotes = word.getLexemeNotes();
				List<TermFreeform> usages = word.getUsages();
				boolean isLexemePublic = isPublic(word.getLexemePublicity(), DEFAULT_LEXEME_PUBLICITY);
				List<SourceLink> lexemeSourceLinks = word.getLexemeSourceLinks();

				if ((wordId == null) && StringUtils.isAnyBlank(wordValue, wordLang)) {
					continue;
				} else if ((wordId != null) && StringUtils.isAnyBlank(wordValue, wordLang)) {

					if (existingWordIds.contains(wordId)) {
						lexemeId = lookupDbService.getLexemeId(wordId, meaningId);
					} else {
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
						lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
					}

				} else {

					String cleanValue = textDecorationService.unifyToApostrophe(wordValue);
					String valueAsWord = textDecorationService.removeAccents(cleanValue);

					if (wordId == null) {
						int synWordHomNr = cudDbService.getWordNextHomonymNr(wordValue, wordLang);
						wordId = cudDbService.createWord(wordValue, wordValue, valueAsWord, wordLang, synWordHomNr);
						WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
						lexemeId = idTuple.getLexemeId();
						activityLogService.createActivityLog(functName, wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_ENABLED);
						activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
					} else {
						if (existingWordIds.contains(wordId)) {
							lexemeId = lookupDbService.getLexemeId(wordId, meaningId);
						} else {
							WordLexemeMeaningIdTuple idTuple = cudDbService.createLexeme(wordId, datasetCode, meaningId, 1);
							lexemeId = idTuple.getLexemeId();
							activityLogService.createActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
						}
						activityLog = activityLogService.prepareActivityLog(functName, wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_ENABLED);
						cudDbService.updateWordValueAndAsWordAndLang(wordId, wordValue, wordValue, valueAsWord, wordLang);
						activityLogService.createActivityLog(activityLog, wordId, ActivityEntity.WORD);
					}
				}

				if (CollectionUtils.isNotEmpty(wordTypeCodes)) {

					List<String> existingWordTypeCodes = lookupDbService.getWordTypeCodes(wordId);
					for (String wordTypeCode : wordTypeCodes) {
						if (!existingWordTypeCodes.contains(wordTypeCode)) {
							activityLog = activityLogService.prepareActivityLog(functName, wordId, ActivityOwner.WORD, MANUAL_EVENT_ON_UPDATE_ENABLED);
							Long wordTypeId = cudDbService.createWordType(wordId, wordTypeCode);
							activityLogService.createActivityLog(activityLog, wordTypeId, ActivityEntity.WORD_TYPE);
						}
					}
				}

				if (StringUtils.isNoneBlank(wordValue, wordLang)) {
					activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
					cudDbService.updateLexemeValueState(lexemeId, lexemeValueStateCode);
					cudDbService.updateLexemePublicity(lexemeId, isLexemePublic);
					activityLogService.createActivityLog(activityLog, lexemeId, ActivityEntity.LEXEME);
				}

				if (CollectionUtils.isNotEmpty(lexemeNotes)) {

					String userName = userContext.getUserName();
					for (TermFreeform lexemeNote : lexemeNotes) {

						Long lexemeNoteId = lexemeNote.getId();
						String lexemeNoteValue = lexemeNote.getValue();
						String lexemeNoteLang = lexemeNote.getLang();
						boolean isLexemeNotePublic = isPublic(lexemeNote.getPublicity(), DEFAULT_LEXEME_NOTE_PUBLICITY);
						List<SourceLink> lexemeNoteSourceLinks = lexemeNote.getSourceLinks();

						FreeForm freeform = initFreeform(FreeformType.NOTE, lexemeNoteValue, lexemeNoteLang, isLexemeNotePublic);

						activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
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
									createFreeformSourceLink(lexemeNoteId, sourceId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK, functName);
								} else {
									updateFreeformSourceLink(sourceLinkId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.LEXEME_NOTE_SOURCE_LINK, functName);
								}
							}
						}
					}
				}

				if (CollectionUtils.isNotEmpty(usages)) {

					String userName = userContext.getUserName();
					for (TermFreeform usage : usages) {

						Long usageId = usage.getId();
						String usageValue = usage.getValue();
						String usageLang = usage.getLang();
						boolean isUsagePublic = isPublic(usage.getPublicity(), DEFAULT_USAGE_PUBLICITY);
						List<SourceLink> usageSourceLinks = usage.getSourceLinks();

						FreeForm freeform = initFreeform(FreeformType.USAGE, usageValue, usageLang, isUsagePublic);

						activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_ENABLED);
						if (usageId == null) {
							usageId = cudDbService.createLexemeFreeform(lexemeId, freeform, userName);
						} else {
							freeform.setId(usageId);
							cudDbService.updateFreeform(freeform, userName);
						}
						activityLogService.createActivityLog(activityLog, usageId, ActivityEntity.USAGE);

						if (CollectionUtils.isNotEmpty(usageSourceLinks)) {

							for (SourceLink usageSourceLink : usageSourceLinks) {
								Long sourceLinkId = usageSourceLink.getSourceLinkId();
								Long sourceId = usageSourceLink.getSourceId();
								String sourceLinkValue = usageSourceLink.getValue();
								if (sourceLinkId == null) {
									createFreeformSourceLink(usageId, sourceId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, functName);
								} else {
									updateFreeformSourceLink(sourceLinkId, sourceLinkValue, ActivityOwner.LEXEME, lexemeId, ActivityEntity.USAGE_SOURCE_LINK, functName);
								}
							}
						}
					}
				}

				if (CollectionUtils.isNotEmpty(lexemeSourceLinks)) {

					for (SourceLink lexemeSourceLink : lexemeSourceLinks) {
						Long sourceLinkId = lexemeSourceLink.getSourceLinkId();
						Long sourceId = lexemeSourceLink.getSourceId();
						String sourceLinkValue = lexemeSourceLink.getValue();
						if (sourceLinkId == null) {
							createLexemeSourceLink(lexemeId, sourceId, sourceLinkValue, functName);
						} else {
							updateLexemeSourceLink(sourceLinkId, lexemeId, sourceLinkValue, functName);
						}
					}
				}

			}
		}

		if (CollectionUtils.isNotEmpty(domains)) {

			for (TermClassifier domain : domains) {

				String domainCode = domain.getCode();
				String domainOrigin = domain.getOrigin();

				boolean meaningDomainExists = lookupDbService.meaningDomainExists(meaningId, domainCode, domainOrigin);
				if (!meaningDomainExists) {
					Classifier classifier = new Classifier();
					classifier.setCode(domainCode);
					classifier.setOrigin(domainOrigin);

					activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_ENABLED);
					Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, classifier);
					activityLogService.createActivityLog(activityLog, meaningDomainId, ActivityEntity.DOMAIN);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningNotes)) {

			String userName = userContext.getUserName();
			for (TermFreeform meaningNote : meaningNotes) {

				Long meaningNoteId = meaningNote.getId();
				String meaningNoteValue = meaningNote.getValue();
				String meaningNoteLang = meaningNote.getLang();
				boolean isMeaningNotePublic = isPublic(meaningNote.getPublicity(), DEFAULT_MEANING_NOTE_PUBLICITY);
				List<SourceLink> meaningNoteSourceLinks = meaningNote.getSourceLinks();

				FreeForm freeform = initFreeform(FreeformType.NOTE, meaningNoteValue, meaningNoteLang, isMeaningNotePublic);

				activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_ENABLED);
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
							createFreeformSourceLink(meaningNoteId, sourceId, sourceLinkValue, ActivityOwner.MEANING, meaningId, ActivityEntity.MEANING_NOTE_SOURCE_LINK, functName);
						} else {
							updateFreeformSourceLink(sourceLinkId, sourceLinkValue, ActivityOwner.MEANING, meaningId, ActivityEntity.MEANING_NOTE_SOURCE_LINK, functName);
						}
					}
				}
			}
		}

		if (CollectionUtils.isNotEmpty(meaningForums)) {

			Long userId = userContext.getUserId();
			String userName = userContext.getUserName();
			for (TermForum meaningForum : meaningForums) {

				Long meaningForumId = meaningForum.getId();
				if (meaningForumId == null) {
					String meaningForumValue = meaningForum.getValue();
					cudDbService.createMeaningForum(meaningId, meaningForumValue, meaningForumValue, userId, userName);
				}
			}
		}

		return meaningId;
	}

	private FreeForm initFreeform(FreeformType freeformType, String value, String lang, boolean isPublic) {

		FreeForm freeform = new FreeForm();
		freeform.setType(freeformType);
		freeform.setValueText(value);
		freeform.setValuePrese(value);
		freeform.setLang(lang);
		freeform.setComplexity(Complexity.DETAIL);
		freeform.setPublic(isPublic);
		return freeform;
	}

	private boolean isPublic(Boolean itemPublicity, boolean defaultPublicity) {

		return itemPublicity == null ? defaultPublicity : itemPublicity;
	}

	private void createDefinitionSourceLink(Long definitionId, Long sourceId, Long meaningId, String sourceLinkValue, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createDefinitionSourceLink(definitionId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	private void updateDefinitionSourceLink(Long sourceLinkId, Long meaningId, String sourceLinkValue, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, meaningId, ActivityOwner.MEANING, MANUAL_EVENT_ON_UPDATE_DISABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateDefinitionSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.DEFINITION_SOURCE_LINK);
	}

	private void createLexemeSourceLink(Long lexemeId, Long sourceId, String sourceLinkValue, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createLexemeSourceLink(lexemeId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	private void updateLexemeSourceLink(Long sourceLinkId, Long lexemeId, String sourceLinkValue, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, lexemeId, ActivityOwner.LEXEME, MANUAL_EVENT_ON_UPDATE_DISABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateLexemeSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, ActivityEntity.LEXEME_SOURCE_LINK);
	}

	private void createFreeformSourceLink(
			Long freeformId, Long sourceId, String sourceLinkValue, ActivityOwner owner, Long ownerId, ActivityEntity activityEntity, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, ownerId, owner, MANUAL_EVENT_ON_UPDATE_DISABLED);
		ReferenceType refType = ReferenceType.ANY;
		String sourceLinkName = null;
		Long sourceLinkId = sourceLinkDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

	private void updateFreeformSourceLink(
			Long sourceLinkId, String sourceLinkValue, ActivityOwner owner, Long ownerId, ActivityEntity activityEntity, String functName) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog(functName, ownerId, owner, MANUAL_EVENT_ON_UPDATE_DISABLED);
		String sourceLinkName = null;
		sourceLinkDbService.updateFreeformSourceLink(sourceLinkId, sourceLinkValue, sourceLinkName);
		activityLogService.createActivityLog(activityLog, sourceLinkId, activityEntity);
	}

}
