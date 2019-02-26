package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.LexSearchDbService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.UpdateDbService;

@Service
public class UpdateService {

	private final UpdateDbService updateDbService;

	private final TextDecorationService textDecorationService;

	private final LifecycleLogDbService lifecycleLogDbService;

	private final LexSearchDbService lexSearchDbService;

	public UpdateService(UpdateDbService updateDbService, TextDecorationService textDecorationService, LifecycleLogDbService lifecycleLogDbService,
			LexSearchDbService lexSearchDbService) {
		this.updateDbService  = updateDbService;
		this.textDecorationService = textDecorationService;
		this.lifecycleLogDbService = lifecycleLogDbService;
		this.lexSearchDbService = lexSearchDbService;
	}

	// --- UPDATE ---

	@Transactional
	public void updateUsageValue(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateGovernment(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value, null);
	}

	@Transactional
	public void updateGrammar(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value, null);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateDefinitionValue(id, value, valuePrese);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateDefinitionOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateLexemeRelationOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateMeaningRelationOrderby(item);
		}
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateWordRelationOrderby(item);			
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_ETYMOLOGY, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateWordEtymologyOrderby(item);			
		}
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) {
		for (ListData item : items) {
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateLexemeOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) return;

		List<WordLexeme> lexemes = updateDbService.findWordLexemes(lexemeId).into(WordLexeme.class);
		recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme: lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			updateDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
		}
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.GENDER, wordId, genderCode);
		updateDbService.updateWordGender(wordId, genderCode);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = updateDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, currentTypeCode, newTypeCode);
	}

	@Transactional
	public void updateWordAspect(Long wordId, String typeCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.ASPECT, wordId, typeCode);
		updateDbService.updateWordAspect(wordId, typeCode);
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String freqGroupCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.FREQUENCY_GROUP, lexemeId, freqGroupCode);
		updateDbService.updateLexemeFrequencyGroup(lexemeId, freqGroupCode);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = updateDbService.updateLexemePos(lexemeId, currentPos, newPos);
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, currentPos, newPos);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivid = updateDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivid, currentDeriv, newDeriv);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = updateDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, currentRegister, newRegister);
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = updateDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, currentDomain.getCode(), newDomain.getCode());
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE_STATE, lexemeId, valueStateCode);
		updateDbService.updateLexemeValueState(lexemeId, valueStateCode);
	}

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.PROCESS_STATE, lexemeId, processStateCode);
		updateDbService.updateLexemeProcessState(lexemeId, processStateCode);
	}

	@Transactional
	public void updateLearnerComment(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updatePublicNote(Long id, String valuePrese) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	// --- ADD ---

	@Transactional
	public void addWord(String valuePrese, String datasetCode, String language, String morphCode, Long meaningId) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long wordId = updateDbService.addWord(value, valuePrese, datasetCode, language, morphCode, meaningId);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, valuePrese);
	}

	@Transactional
	public void addWordToDataset(Long wordId, String datasetCode, Long meaningId) {
		Long lexemeId = updateDbService.addWordToDataset(wordId, datasetCode, meaningId);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
	}

	@Transactional
	public void addWordType(Long wordId, String typeCode) {
		Long lexemePosId = updateDbService.addWordType(wordId, typeCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, lexemePosId, typeCode);
	}

	@Transactional
	public void addLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = updateDbService.addLexemePos(lexemeId, posCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
	}

	@Transactional
	public void addLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = updateDbService.addLexemeDeriv(lexemeId, derivCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
	}

	@Transactional
	public void addLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = updateDbService.addLexemeRegister(lexemeId, registerCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
	}

	@Transactional
	public void addGovernment(Long lexemeId, String government) {
		Long governmentId = updateDbService.addGovernment(lexemeId, government);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, governmentId, government);
	}

	@Transactional
	public void addLexemeGrammar(Long lexemeId, String value) {
		Long grammarId = updateDbService.addLexemeGrammar(lexemeId, value);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, grammarId, value);
	}

	@Transactional
	public void addUsage(Long lexemeId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageId = updateDbService.addUsage(lexemeId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, valuePrese);
	}

	@Transactional
	public void addUsageTranslation(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageTranslationId = updateDbService.addUsageTranslation(usageId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, valuePrese);
	}

	@Transactional
	public void addUsageDefinition(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addUsageDefinition(usageId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = updateDbService.addMeaningDomain(meaningId, domain);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
	}

	@Transactional
	public void addDefinition(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.convertEkiEntityMarkup(valuePrese);
		Long definitionId = updateDbService.addDefinition(meaningId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
	}

	@Transactional
	public void addFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		Long sourceLinkId = updateDbService.addFreeformSourceLink(freeformId, sourceId, refType, sourceValue, sourceName);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void addLexemeSourceLink(Long lexemeId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = updateDbService.addLexemeSourceLink(lexemeId, sourceId, refType, sourceValue, sourceName);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void addDefinitionSourceLink(Long definitionId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = updateDbService.addDefinitionSourceLink(definitionId, sourceId, refType, sourceValue, sourceName);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void addWordRelation(Long wordId, Long targetWordId, String relationTypeCode) {
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			boolean doLogging = false;
			String previousLogValue = null;
			Long groupId = updateDbService.findWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = updateDbService.addWordRelationGroup(relationTypeCode);
				updateDbService.addWordRelationGroupMember(groupId, wordId);
				updateDbService.addWordRelationGroupMember(groupId, targetWordId);
				doLogging = true;
			} else {
				if (!updateDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
					List<Map<String, Object>> wordRelationGroupMembers = updateDbService.findWordRelationGroupMembers(groupId);
					previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
					updateDbService.addWordRelationGroupMember(groupId, targetWordId);
					doLogging = true;
				}
			}
			if (doLogging) {
				List<Map<String, Object>> wordRelationGroupMembers = updateDbService.findWordRelationGroupMembers(groupId);
				String logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
				for (Map<String, Object> member : wordRelationGroupMembers) {
					lifecycleLogDbService.addLog(
							LifecycleEventType.CREATE,
							LifecycleEntity.WORD_RELATION_GROUP_MEMBER,
							LifecycleProperty.VALUE,
							(Long) member.get("id"),
							previousLogValue,
							logValue);
				}
			}
		} else {
			Long relationId = updateDbService.addWordRelation(wordId, targetWordId, relationTypeCode);
			lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
		}
	}

	@Transactional
	public void addLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		Long lexemeRelationId = updateDbService.addLexemeRelation(lexemeId1, lexemeId2, relationType);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, lexemeRelationId, relationType);
	}

	@Transactional
	public void addMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		Long meaningRelationId = updateDbService.addMeaningRelation(meaningId1, meaningId2, relationType);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, meaningRelationId, relationType);
	}

	@Transactional
	public void addLearnerComment(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addLearnerComment(meaningId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addPublicNote(Long lexemeId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addPublicNote(lexemeId, value, valuePrese, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.PUBLIC_NOTE, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void joinLexemes(Long lexemeId, Long lexemeId2) {
		LexemeRecord lexeme = updateDbService.getLexeme(lexemeId);
		LexemeRecord lexeme2 = updateDbService.getLexeme(lexemeId2);
		if (lexeme.getDatasetCode().equals(lexeme2.getDatasetCode()) && lexeme.getWordId().equals(lexeme2.getWordId())) {
			updateLexemeLevels(lexemeId2,"delete");
			String logEntrySource = StringUtils.joinWith(".", lexeme2.getLevel1(), lexeme2.getLevel2(), lexeme2.getLevel3());
			String logEntryTarget = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			lifecycleLogDbService.addLog(LifecycleEventType.JOIN, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId, logEntrySource, logEntryTarget);
		}
		String logEntrySource = updateDbService.getFirstDefinitionOfMeaning(lexeme2.getMeaningId());
		String logEntryTarget = updateDbService.getFirstDefinitionOfMeaning(lexeme.getMeaningId());
		lifecycleLogDbService.addLog(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, lexeme.getMeaningId(), logEntrySource, logEntryTarget);
		updateDbService.joinLexemeMeanings(lexemeId, lexemeId2);
	}

	@Transactional
	public List<String> validateLexemeJoin(Long lexemeId, Long lexemeId2) {
		List<String> validationMessages = new ArrayList<>();
		LexemeRecord lexeme = updateDbService.getLexeme(lexemeId);
		LexemeRecord lexeme2 = updateDbService.getLexeme(lexemeId2);
		if (lexeme.getDatasetCode().equals(lexeme2.getDatasetCode()) && lexeme.getWordId().equals(lexeme2.getWordId())) {
			if (!Objects.equals(lexeme.getFrequencyGroupCode(), lexeme2.getFrequencyGroupCode())) {
				validationMessages.add("Ilmikute sagedusrühmad on erinevad.");
			}
		}
		return validationMessages;
	}

	//TODO lifecycle log
	@Transactional
	public void separateLexemeMeanings(Long lexemeId) {
		updateDbService.separateLexemeMeanings(lexemeId);
	}

	@Transactional
	public void joinMeanings(Long meaningId, Long sourceMeaningId) {
		String logEntrySource = updateDbService.getFirstDefinitionOfMeaning(meaningId);
		String logEntryTarget = updateDbService.getFirstDefinitionOfMeaning(sourceMeaningId);
		lifecycleLogDbService.addLog(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId, logEntrySource, logEntryTarget);
		updateDbService.joinMeanings(meaningId, sourceMeaningId);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = updateDbService.findWordWordTypeId(wordId, typeCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, typeCode, null);
			updateDbService.deleteWordWordType(wordWordTypeId);
		}
	}

	@Transactional
	public void deleteUsage(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageTranslation(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageDefinition(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGovernment(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGrammar(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteDefinition(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteDefinition(id);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteDefinitionRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteFreeformRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteLexemeRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = updateDbService.findLexemePosId(lexemeId, posCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode, null);
			updateDbService.deleteLexemePos(lexemePosId);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = updateDbService.findLexemeDerivId(lexemeId, derivCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode, null);
			updateDbService.deleteLexemeDeriv(lexemeDerivId);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = updateDbService.findLexemeRegisterId(lexemeId, registerCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode, null);
			updateDbService.deleteLexemeRegister(lexemeRegisterId);
		}
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId,  Classifier domain) {
		if (domain != null) {
			Long meaningDomainId = updateDbService.findMeaningDomainId(meaningId, domain);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode(), null);
			updateDbService.deleteMeaningDomain(meaningDomainId);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) {
		Long groupId = updateDbService.findWordRelationGroupId(relationId);
		if (groupId == null) {
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			updateDbService.deleteWordRelation(relationId);
		} else {
			List<Map<String, Object>> wordRelationGroupMembers = updateDbService.findWordRelationGroupMembers(groupId);
			String relationTypeCode = wordRelationGroupMembers.get(0).get("word_rel_type_code").toString();
			String previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			String logValue = null;
			if (wordRelationGroupMembers.size() > 2) {
				logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream()
					.filter(m -> !relationId.equals(m.get("id")))
					.map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			}
			for (Map<String, Object> member : wordRelationGroupMembers) {
				lifecycleLogDbService.addLog(
						LifecycleEventType.DELETE,
						LifecycleEntity.WORD_RELATION_GROUP_MEMBER,
						LifecycleProperty.VALUE,
						(Long) member.get("id"),
						previousLogValue,
						logValue);
			}
			updateDbService.deleteWordRelationGroupMember(relationId);
			if (wordRelationGroupMembers.size() <= 2) {
				updateDbService.deleteWordRelationGroup(groupId);
			}
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId);
		updateDbService.deleteLexemeRelation(relationId);
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId);
		updateDbService.deleteMeaningRelation(relationId);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId) {
		if (lexSearchDbService.isTheOnlyLexemeForMeaning(lexemeId)) {
			return;
		}
		Long wordId = null;
		boolean isLastLexeme = lexSearchDbService.isTheOnlyLexemeForWord(lexemeId);
		if (isLastLexeme) {
			wordId = lexSearchDbService.findLexeme(lexemeId).into(WordLexeme.class).getWordId();
		}
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
		updateLexemeLevels(lexemeId,"delete");
		updateDbService.deleteLexeme(lexemeId);
		if (isLastLexeme) {
			deleteWord(wordId);
		}
	}

	@Transactional
	public void deleteWord(Long wordId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId);
		updateDbService.deleteWord(wordId);
	}

	@Transactional
	public void deleteLearnerComment(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deletePublicNote(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	void recalculateLevels(Long lexemeId, List<WordLexeme> lexemes, String action) {
		WordLexeme lexemeToMove = lexemes.stream().filter(l -> l.getLexemeId().equals(lexemeId)).findFirst().get();
		int lexemePos = lexemes.indexOf(lexemeToMove);
		int levelToChange = getLevelToChange(lexemes, lexemeToMove);
		switch (action) {
		case "up" :
			if (lexemePos != 0) {
				WordLexeme targetLexeme = lexemes.get(lexemePos - 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "down" :
			if (lexemePos != lexemes.size() - 1) {
				WordLexeme targetLexeme = lexemes.get(lexemePos + 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "pop" :
			if (levelToChange > 1) {
				if (levelToChange == 2) {
					Integer maxLevel1 = lexemes.stream().map(WordLexeme::getLevel1).max(Comparator.comparingInt(Integer::valueOf)).get();
					Integer currentLevel1 = lexemeToMove.getLevel1();
					lexemeToMove.setLevel1(maxLevel1 + 1);
					lexemeToMove.setLevel2(1);
					lexemeToMove.setLevel3(1);
					List<WordLexeme> lexemesToCorrect = lexemes.stream().filter(l -> l.getLevel1().equals(currentLevel1)).collect(Collectors.toList());
					Integer oldLevel2 = 999;
					Integer newLevel2 = 0;
					Integer newLevel3 = 1;
					for (WordLexeme lexeme : lexemesToCorrect) {
						if (lexeme.getLevel2().equals(oldLevel2)) {
							newLevel3++;
						} else {
							newLevel3 = 1;
							newLevel2++;
							oldLevel2 = lexeme.getLevel2();
						}
						lexeme.setLevel2(newLevel2);
						lexeme.setLevel3(newLevel3);
					}
				} else if (levelToChange == 3) {
					Integer maxLevel2 = lexemes.stream().filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()))
							.map(WordLexeme::getLevel2).max(Comparator.comparingInt(Integer::valueOf)).get();
					Integer currentLevel2 = lexemeToMove.getLevel2();
					lexemeToMove.setLevel2(maxLevel2 + 1);
					lexemeToMove.setLevel3(1);
					List<WordLexeme> lexemesToCorrect = lexemes.stream()
							.filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()) && l.getLevel2().equals(currentLevel2)).collect(Collectors.toList());
					Integer newLevel = 1;
					for (WordLexeme lexeme : lexemesToCorrect) {
						lexeme.setLevel3(newLevel++);
					}
				}
			}
			break;
		case "push" :
			if (levelToChange < 3 && lexemes.size() > 1) {
				if (levelToChange == 1) {
					WordLexeme targetLexeme = lexemes.get(lexemePos == 0 ? lexemePos + 1 : lexemePos - 1);
					Integer level1 = lexemeToMove.getLevel1();
					Integer maxLevel2 = lexemes.stream().filter(l -> l.getLevel1().equals(targetLexeme.getLevel1()))
							.map(WordLexeme::getLevel2).max(Comparator.comparingInt(Integer::valueOf)).get();
					lexemeToMove.setLevel1(targetLexeme.getLevel1());
					lexemeToMove.setLevel2(maxLevel2 + 1);
					lexemes.stream().filter(l -> l.getLevel1() > level1).forEach(l -> l.setLevel1(l.getLevel1() - 1));
				}
				if (levelToChange == 2) {
					List<WordLexeme> level2lexemes = lexemes.stream().filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1())).collect(Collectors.toList());
					lexemePos = level2lexemes.indexOf(lexemeToMove);
					WordLexeme targetLexeme = lexemes.get(lexemePos == 0 ? lexemePos + 1 : lexemePos - 1);
					Integer level2 = lexemeToMove.getLevel2();
					Integer maxLevel3 = lexemes.stream().filter(l -> l.getLevel1().equals(targetLexeme.getLevel1()) && l.getLevel2().equals(targetLexeme.getLevel2()))
							.map(WordLexeme::getLevel3).max(Comparator.comparingInt(Integer::valueOf)).get();
					lexemeToMove.setLevel2(targetLexeme.getLevel2());
					lexemeToMove.setLevel3(maxLevel3 + 1);
					level2lexemes.stream().filter(l -> l.getLevel2() > level2).forEach(l -> l.setLevel2(l.getLevel2() - 1));
				}
			}
			break;
		case "delete":
			if (levelToChange == 1) {
				lexemes.stream()
						.filter(l -> l.getLevel1() > lexemeToMove.getLevel1())
						.forEach(l -> l.setLevel1(l.getLevel1() - 1));
			}
			if (levelToChange == 2) {
				lexemes.stream()
						.filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()) && l.getLevel2() > lexemeToMove.getLevel2())
						.forEach(l -> l.setLevel2(l.getLevel2() - 1));
			}
			if (levelToChange == 3) {
				lexemes.stream()
						.filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()) && l.getLevel2().equals(lexemeToMove.getLevel2()) && l.getLevel3() > lexemeToMove.getLevel3())
						.forEach(l -> l.setLevel3(l.getLevel3() - 1));
			}
			break;
		}
	}

	private void moveUpDown(List<WordLexeme> lexemes, WordLexeme lexemeToMove, WordLexeme targetLexeme) {
		int levelToChange = getLevelToChange(lexemes, lexemeToMove);
		int previousLexLevel = numberAtLevel(levelToChange, targetLexeme);
		int currentLexLevel = numberAtLevel(levelToChange, lexemeToMove);
		if (levelToChange == 1) {
			if (previousLexLevel != currentLexLevel) {
				lexemes.stream().filter(l -> l.getLevel1().equals(currentLexLevel)).forEach(l -> l.setLevel1(999));
				lexemes.stream().filter(l -> l.getLevel1().equals(previousLexLevel)).forEach(l -> l.setLevel1(currentLexLevel));
				lexemes.stream().filter(l -> l.getLevel1().equals(999)).forEach(l -> l.setLevel1(previousLexLevel));
			}
		}
		if (levelToChange == 2) {
			if (lexemeToMove.getLevel1().equals(targetLexeme.getLevel1()) && previousLexLevel != currentLexLevel) {
				lexemes.stream().filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()) && l.getLevel2().equals(currentLexLevel))
						.forEach(l -> l.setLevel2(999));
				lexemes.stream().filter(l -> l.getLevel1().equals(targetLexeme.getLevel1()) && l.getLevel2().equals(previousLexLevel))
						.forEach(l -> l.setLevel2(currentLexLevel));
				lexemes.stream().filter(l -> l.getLevel2().equals(999)).forEach(l -> l.setLevel2(previousLexLevel));
			}
		}
		if (levelToChange == 3) {
			if (lexemeToMove.getLevel1().equals(targetLexeme.getLevel1())
					&& lexemeToMove.getLevel2().equals(targetLexeme.getLevel2())
					&& previousLexLevel != currentLexLevel) {
				lexemes.stream()
						.filter(l ->
								l.getLevel1().equals(lexemeToMove.getLevel1()) &&
								l.getLevel2().equals(lexemeToMove.getLevel2()) &&
								l.getLevel3().equals(currentLexLevel))
						.forEach(l -> l.setLevel3(999));
				lexemes.stream().filter(l ->
								l.getLevel1().equals(targetLexeme.getLevel1()) &&
								l.getLevel2().equals(targetLexeme.getLevel2()) &&
								l.getLevel3().equals(previousLexLevel))
						.forEach(l -> l.setLevel3(currentLexLevel));
				lexemes.stream().filter(l -> l.getLevel3().equals(999)).forEach(l -> l.setLevel3(previousLexLevel));
			}
		}
	}

	private int getLevelToChange(List<WordLexeme> lexemes, WordLexeme lexeme) {
		if (lexemes.stream().filter(l -> l.getLevel1().equals(lexeme.getLevel1())).count() == 1) {
			return 1;
		}
		if (lexemes.stream().filter(l -> l.getLevel1().equals(lexeme.getLevel1()) && l.getLevel2().equals(lexeme.getLevel2())).count() == 1) {
			return 2;
		}
		return 3;
	}

	private int numberAtLevel(int level, WordLexeme lex) {
		switch (level) {
			case 1 : return lex.getLevel1();
			case 2 : return lex.getLevel2();
			case 3 : return lex.getLevel3();
		}
		return 0;
	}

}
