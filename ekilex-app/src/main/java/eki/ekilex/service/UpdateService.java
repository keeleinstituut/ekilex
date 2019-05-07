package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.LifecycleLogDbService;
import eki.ekilex.service.db.UpdateDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetPermissionsExist")
@Component
public class UpdateService {

	private static final String DEFAULT_DEFINITION_TYPE_CODE = "määramata";

	@Autowired
	private UpdateDbService updateDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private LifecycleLogDbService lifecycleLogDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private UserService userService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	// --- UPDATE ---

	//@PreAuthorize("hasPermission(#id, 'USAGE', 'DATASET:CRUD')")
	@Transactional
	public void updateUsageValue(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateGovernment(Long id, String value) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value, null);
	}

	@Transactional
	public void updateGrammar(Long id, String value) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value, null);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateDefinitionValue(id, value, valuePrese);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateDefinitionOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateLexemeRelationOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateMeaningRelationOrderby(item);
		}
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateWordRelationOrderby(item);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_ETYMOLOGY, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateWordEtymologyOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) {
		for (ListData item : items) {
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.ORDER_BY, item);
			updateDbService.updateLexemeOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = updateDbService.findWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			updateDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
		}
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.GENDER, wordId, genderCode);
		updateDbService.updateWordGender(wordId, genderCode);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = updateDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, currentTypeCode, newTypeCode);
	}

	@Transactional
	public void updateWordAspect(Long wordId, String typeCode) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.ASPECT, wordId, typeCode);
		updateDbService.updateWordAspect(wordId, typeCode);
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String freqGroupCode) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.FREQUENCY_GROUP, lexemeId, freqGroupCode);
		updateDbService.updateLexemeFrequencyGroup(lexemeId, freqGroupCode);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = updateDbService.updateLexemePos(lexemeId, currentPos, newPos);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, currentPos, newPos);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivid = updateDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivid, currentDeriv, newDeriv);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = updateDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, currentRegister, newRegister);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) {
		Long lexemeRegionId = updateDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, currentRegion, newRegion);
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = updateDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, currentDomain.getCode(), newDomain.getCode());
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE_STATE, lexemeId, valueStateCode);
		updateDbService.updateLexemeValueState(lexemeId, valueStateCode);
	}

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, String processStateCode) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.PROCESS_STATE, lexemeId, processStateCode);
		updateDbService.updateLexemeProcessState(lexemeId, processStateCode);
	}

	@Transactional
	public void updateLearnerComment(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateLexemePublicNote(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateMeaningPublicNote(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateMeaningPrivateNote(Long id, String valuePrese) {
		addLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_PRIVATE_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		updateDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	// --- ADD ---

	@Transactional
	public void addWord(String valuePrese, String datasetCode, String language, String morphCode, Long meaningId) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long wordId = updateDbService.addWord(value, valuePrese, datasetCode, language, morphCode, meaningId);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, valuePrese);
	}

	@Transactional
	public void addWordToDataset(Long wordId, String datasetCode, Long meaningId) {
		Long lexemeId = updateDbService.addWordToDataset(wordId, datasetCode, meaningId);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
	}

	@Transactional
	public void addWordType(Long wordId, String typeCode) {
		Long lexemePosId = updateDbService.addWordType(wordId, typeCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, lexemePosId, typeCode);
	}

	@Transactional
	public void addLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = updateDbService.addLexemePos(lexemeId, posCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
	}

	@Transactional
	public void addLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = updateDbService.addLexemeDeriv(lexemeId, derivCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
	}

	@Transactional
	public void addLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = updateDbService.addLexemeRegister(lexemeId, registerCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
	}

	@Transactional
	public void addLexemeRegion(Long lexemeId, String regionCode) {
		Long lexemeRegionId = updateDbService.addLexemeRegion(lexemeId, regionCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, regionCode);
	}

	@Transactional
	public void addGovernment(Long lexemeId, String government) {
		Long governmentId = updateDbService.addGovernment(lexemeId, government);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, governmentId, government);
	}

	@Transactional
	public void addLexemeGrammar(Long lexemeId, String value) {
		Long grammarId = updateDbService.addLexemeGrammar(lexemeId, value);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, grammarId, value);
	}

	@Transactional
	public void addUsage(Long lexemeId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageId = updateDbService.addUsage(lexemeId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, valuePrese);
	}

	@Transactional
	public void addUsageTranslation(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageTranslationId = updateDbService.addUsageTranslation(usageId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, valuePrese);
	}

	@Transactional
	public void addUsageDefinition(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addUsageDefinition(usageId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = updateDbService.addMeaningDomain(meaningId, domain);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
	}

	@Transactional
	public void addDefinition(Long meaningId, String valuePrese, String languageCode, String datasetCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long definitionId = updateDbService.addDefinition(meaningId, value, valuePrese, languageCode, DEFAULT_DEFINITION_TYPE_CODE);
		updateDbService.addDefinitionDataset(definitionId, datasetCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
	}

	@Transactional
	public void addFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		Long sourceLinkId = updateDbService.addFreeformSourceLink(freeformId, sourceId, refType, sourceValue, sourceName);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void addLexemeSourceLink(Long lexemeId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = updateDbService.addLexemeSourceLink(lexemeId, sourceId, refType, sourceValue, sourceName);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void addDefinitionSourceLink(Long definitionId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = updateDbService.addDefinitionSourceLink(definitionId, sourceId, refType, sourceValue, sourceName);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
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
					addLifecycleLog(
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
			addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
		}
	}

	@Transactional
	public void addLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		Long lexemeRelationId = updateDbService.addLexemeRelation(lexemeId1, lexemeId2, relationType);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, lexemeRelationId, relationType);
	}

	@Transactional
	public void addMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		Long meaningRelationId = updateDbService.addMeaningRelation(meaningId1, meaningId2, relationType);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, meaningRelationId, relationType);
	}

	@Transactional
	public void addLearnerComment(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addLearnerComment(meaningId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addLexemePublicNote(Long lexemeId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addLexemePublicNote(lexemeId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addMeaningPublicNote(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addMeaningPublicNote(meaningId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void addMeaningPrivateNote(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = updateDbService.addMeaningPrivateNote(meaningId, value, valuePrese, languageCode);
		addLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_PRIVATE_NOTE, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void joinLexemes(Long lexemeId, Long lexemeId2) {
		LexemeRecord lexeme = updateDbService.getLexeme(lexemeId);
		LexemeRecord lexeme2 = updateDbService.getLexeme(lexemeId2);
		if (lexeme.getDatasetCode().equals(lexeme2.getDatasetCode()) && lexeme.getWordId().equals(lexeme2.getWordId())) {
			updateLexemeLevels(lexemeId2, "delete");
			String logEntrySource = StringUtils.joinWith(".", lexeme2.getLevel1(), lexeme2.getLevel2(), lexeme2.getLevel3());
			String logEntryTarget = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			addLifecycleLog(LifecycleEventType.JOIN, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId, logEntrySource, logEntryTarget);
		}
		String logEntrySource = updateDbService.getFirstDefinitionOfMeaning(lexeme2.getMeaningId());
		String logEntryTarget = updateDbService.getFirstDefinitionOfMeaning(lexeme.getMeaningId());
		addLifecycleLog(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, lexeme.getMeaningId(), logEntrySource, logEntryTarget);
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
		addLifecycleLog(LifecycleEventType.JOIN, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId, logEntrySource, logEntryTarget);
		updateDbService.joinMeanings(meaningId, sourceMeaningId);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = updateDbService.findWordWordTypeId(wordId, typeCode);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, typeCode, null);
			updateDbService.deleteWordWordType(wordWordTypeId);
		}
	}

	@Transactional
	public void deleteUsage(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageTranslation(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageDefinition(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGovernment(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGrammar(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteDefinition(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, null);
		updateDbService.deleteDefinition(id);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteDefinitionRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteFreeformRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		updateDbService.deleteLexemeRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = updateDbService.findLexemePosId(lexemeId, posCode);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode, null);
			updateDbService.deleteLexemePos(lexemePosId);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = updateDbService.findLexemeDerivId(lexemeId, derivCode);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode, null);
			updateDbService.deleteLexemeDeriv(lexemeDerivId);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = updateDbService.findLexemeRegisterId(lexemeId, registerCode);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode, null);
			updateDbService.deleteLexemeRegister(lexemeRegisterId);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode) {
		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = updateDbService.findLexemeRegionId(lexemeId, regionCode);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, regionCode, null);
			updateDbService.deleteLexemeRegion(lexemeRegionId);
		}
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain) {
		if (domain != null) {
			Long meaningDomainId = updateDbService.findMeaningDomainId(meaningId, domain);
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode(), null);
			updateDbService.deleteMeaningDomain(meaningDomainId);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) {
		Long groupId = updateDbService.findWordRelationGroupId(relationId);
		if (groupId == null) {
			addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
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
				addLifecycleLog(
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
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId);
		updateDbService.deleteLexemeRelation(relationId);
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId);
		updateDbService.deleteMeaningRelation(relationId);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId) {
		if (lexSearchDbService.isTheOnlyLexemeForMeaning(lexemeId)) {
			return;
		}
		Long wordId = null;
		boolean isLastLexeme = lexSearchDbService.isOnlyLexemeForWord(lexemeId);
		if (isLastLexeme) {
			wordId = lexSearchDbService.findLexeme(lexemeId).getWordId();
		}
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
		updateLexemeLevels(lexemeId, "delete");
		updateDbService.deleteLexeme(lexemeId);
		if (isLastLexeme) {
			deleteWord(wordId);
		}
	}

	@Transactional
	public void deleteWord(Long wordId) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId);
		updateDbService.deleteWord(wordId);
	}

	@Transactional
	public void deleteLearnerComment(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemePublicNote(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteMeaningPublicNote(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteMeaningPrivateNote(Long id) {
		addLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_PRIVATE_NOTE, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	private void addLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId) {
		addLifecycleLog(eventType, entity, property, entityId, null, null);
	}

	private void addLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String entry) {
		addLifecycleLog(eventType, entity, property, entityId, null, entry);
	}

	private void addLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, Long entityId, String recent, String entry) {
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.addLog(userName, eventType, entity, property, entityId, recent, entry);
	}

	private void addLifecycleLog(LifecycleEventType eventType, LifecycleEntity entity, LifecycleProperty property, ListData item) {
		String userName = userService.getAuthenticatedUser().getName();
		lifecycleLogDbService.addLog(userName, eventType, entity, property, item);
	}
}
