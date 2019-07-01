package eki.ekilex.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.ReferenceType;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetPermissionsExist")
@Component
public class CudService extends AbstractService {

	private static final String DEFAULT_DEFINITION_TYPE_CODE = "määramata";

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	// --- UPDATE ---

	@Transactional
	public void updateWordValue(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateWordValue(id, value, valuePrese);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, currentTypeCode, newTypeCode);
	}

	@Transactional
	public void updateWordAspect(Long wordId, String typeCode) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.ASPECT, wordId, typeCode);
		cudDbService.updateWordAspect(wordId, typeCode);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.GENDER, wordId, genderCode);
		cudDbService.updateWordGender(wordId, genderCode);
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateWordRelationOrderby(item);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD_ETYMOLOGY, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateWordEtymologyOrderby(item);
		}
	}

	//@PreAuthorize("hasPermission(#id, 'USAGE', 'DATASET:CRUD')")
	@Transactional
	public void updateUsageValue(Long id, String valuePrese, Complexity complexity) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValueAndComplexity(id, value, valuePrese, complexity);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateLexemeOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = cudDbService.getWordLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
		}
	}

	@Transactional
	public void updateLexemeGovernment(Long id, String value, Complexity complexity) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, value);
		cudDbService.updateFreeformTextValueAndComplexity(id, value, null, complexity);
	}

	@Transactional
	public void updateLexemeGrammar(Long id, String value, Complexity complexity) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, value);
		cudDbService.updateFreeformTextValueAndComplexity(id, value, null, complexity);
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String freqGroupCode) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.FREQUENCY_GROUP, lexemeId, freqGroupCode);
		cudDbService.updateLexemeFrequencyGroup(lexemeId, freqGroupCode);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, currentPos, newPos);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivid, currentDeriv, newDeriv);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, currentRegister, newRegister);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) {
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, currentRegion, newRegion);
	}

	@Transactional
	public void updateLexemePublicNote(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE_STATE, lexemeId, valueStateCode);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateLexemeRelationOrderby(item);
		}
	}

	@Transactional
	public void updateDefinition(Long id, String valuePrese, Complexity complexity) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateDefinition(id, value, valuePrese, complexity);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateDefinitionOrderby(item);
		}
	}

	@Transactional
	public void updateDefinitionPublicNote(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION_PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.ORDER_BY, item);
			cudDbService.updateMeaningRelationOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, currentDomain.getCode(), newDomain.getCode());
	}

	@Transactional
	public void updateMeaningLearnerComment(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateMeaningPublicNote(Long id, String valuePrese) {
		createLifecycleLog(LifecycleEventType.UPDATE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, id, valuePrese);
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	// --- CREATE ---

	@Transactional
	public void createWord(String valuePrese, String datasetCode, String language, String morphCode, Long meaningId) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long wordId = cudDbService.createWordAndLexeme(value, valuePrese, datasetCode, language, morphCode, meaningId);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, valuePrese);
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode) {
		Long lexemePosId = cudDbService.createWordType(wordId, typeCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, lexemePosId, typeCode);
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode) {
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			boolean doLogging = false;
			String previousLogValue = null;
			Long groupId = cudDbService.getWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = cudDbService.createWordRelationGroup(relationTypeCode);
				cudDbService.createWordRelationGroupMember(groupId, wordId);
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
				doLogging = true;
			} else {
				if (!cudDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
					List<Map<String, Object>> wordRelationGroupMembers = cudDbService.getWordRelationGroupMembers(groupId);
					previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
					cudDbService.createWordRelationGroupMember(groupId, targetWordId);
					doLogging = true;
				}
			}
			if (doLogging) {
				List<Map<String, Object>> wordRelationGroupMembers = cudDbService.getWordRelationGroupMembers(groupId);
				String logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
				for (Map<String, Object> member : wordRelationGroupMembers) {
					createLifecycleLog(
							LifecycleEventType.CREATE,
							LifecycleEntity.WORD_RELATION_GROUP_MEMBER,
							LifecycleProperty.VALUE,
							(Long) member.get("id"),
							previousLogValue,
							logValue);
				}
			}
		} else {
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode);
			createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
		}
	}

	@Transactional
	public void createLexeme(Long wordId, String datasetCode, Long meaningId) {
		Long lexemeId = cudDbService.createLexeme(wordId, datasetCode, meaningId);
		if (lexemeId == null) {
			return;
		}
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
	}

	@Transactional
	public void createUsage(Long lexemeId, String valuePrese, String languageCode, Complexity complexity) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageId = cudDbService.createUsage(lexemeId, value, valuePrese, languageCode, complexity);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, valuePrese);
	}

	@Transactional
	public void createUsageTranslation(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageTranslationId = cudDbService.createUsageTranslation(usageId, value, valuePrese, languageCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, valuePrese);
	}

	@Transactional
	public void createUsageDefinition(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long usageDefinitionId = cudDbService.createUsageDefinition(usageId, value, valuePrese, languageCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode) {
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, regionCode);
	}

	@Transactional
	public void createLexemeGovernment(Long lexemeId, String government, Complexity complexity) {
		Long governmentId = cudDbService.createLexemeGovernment(lexemeId, government, complexity);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, governmentId, government);
	}

	@Transactional
	public void createLexemeGrammar(Long lexemeId, String value, Complexity complexity) {
		Long grammarId = cudDbService.createLexemeGrammar(lexemeId, value, complexity);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, grammarId, value);
	}

	@Transactional
	public void createLexemePublicNote(Long lexemeId, String valuePrese) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long lexemeFreeformId = cudDbService.createLexemePublicNote(lexemeId, value, valuePrese);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, lexemeFreeformId, valuePrese);
	}

	@Transactional
	public void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType) {
		Long lexemeRelationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, lexemeRelationId, relationType);
	}

	@Transactional
	public void createLexemeSourceLink(Long lexemeId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = cudDbService.createLexemeSourceLink(lexemeId, sourceId, refType, sourceValue, sourceName);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType) {
		Long meaningRelationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, meaningRelationId, relationType);
	}

	@Transactional
	public void createMeaningLearnerComment(Long meaningId, String valuePrese, String languageCode) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long meaningFreeformId = cudDbService.createMeaningLearnerComment(meaningId, value, valuePrese, languageCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, meaningFreeformId, valuePrese);
	}

	@Transactional
	public void createMeaningPublicNote(Long meaningId, String valuePrese) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long meaningFreeformId = cudDbService.createMeaningPublicNote(meaningId, value, valuePrese);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, meaningFreeformId, valuePrese);
	}

	@Transactional
	public void createDefinition(Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, DEFAULT_DEFINITION_TYPE_CODE, complexity);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
	}

	@Transactional
	public void createDefinitionPublicNote(Long definitionId, String valuePrese) {
		String value = textDecorationService.cleanEkiElementMarkup(valuePrese);
		Long definitionFreeformId = cudDbService.createDefinitionPublicNote(definitionId, value, valuePrese);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION_PUBLIC_NOTE, LifecycleProperty.VALUE, definitionFreeformId, valuePrese);
	}

	@Transactional
	public void createDefinitionSourceLink(Long definitionId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
		Long sourceLinkId = cudDbService.createDefinitionSourceLink(definitionId, sourceId, refType, sourceValue, sourceName);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	@Transactional
	public void createFreeformSourceLink(Long freeformId, Long sourceId, ReferenceType refType, String sourceValue, String sourceName) {
		Long sourceLinkId = cudDbService.createFreeformSourceLink(freeformId, sourceId, refType, sourceValue, sourceName);
		createLifecycleLog(LifecycleEventType.CREATE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, sourceValue);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId);
		cudDbService.deleteWord(wordId);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = cudDbService.getWordWordTypeId(wordId, typeCode);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, typeCode, null);
			cudDbService.deleteWordWordType(wordWordTypeId);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) {
		Long groupId = cudDbService.getWordRelationGroupId(relationId);
		if (groupId == null) {
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			cudDbService.deleteWordRelation(relationId);
		} else {
			List<Map<String, Object>> wordRelationGroupMembers = cudDbService.getWordRelationGroupMembers(groupId);
			String relationTypeCode = wordRelationGroupMembers.get(0).get("word_rel_type_code").toString();
			String previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			String logValue = null;
			if (wordRelationGroupMembers.size() > 2) {
				logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream()
						.filter(m -> !relationId.equals(m.get("id")))
						.map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			}
			for (Map<String, Object> member : wordRelationGroupMembers) {
				createLifecycleLog(
						LifecycleEventType.DELETE,
						LifecycleEntity.WORD_RELATION_GROUP_MEMBER,
						LifecycleProperty.VALUE,
						(Long) member.get("id"),
						previousLogValue,
						logValue);
			}
			cudDbService.deleteWordRelationGroupMember(relationId);
			if (wordRelationGroupMembers.size() <= 2) {
				cudDbService.deleteWordRelationGroup(groupId);
			}
		}
	}

	@Transactional
	public void deleteDefinition(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteDefinition(id);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		cudDbService.deleteDefinitionRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteDefinitionPublicNote(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION_PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId) {
		boolean isOnlyLexemeForMeaning = lexSearchDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lexSearchDbService.isOnlyLexemeForWord(lexemeId);
		WordLexeme lexeme = lexSearchDbService.getLexeme(lexemeId);
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();

		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
		updateLexemeLevels(lexemeId, "delete");
		cudDbService.deleteLexeme(lexemeId);
		if (isOnlyLexemeForMeaning) {
			deleteMeaning(meaningId);
		}
		if (isOnlyLexemeForWord) {
			deleteWord(wordId);
		}
	}

	@Transactional
	public void deleteUsage(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageTranslation(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageDefinition(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeGovernment(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeGrammar(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, null);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemePublicNote(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		cudDbService.deleteLexemeRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = cudDbService.getLexemePosId(lexemeId, posCode);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode, null);
			cudDbService.deleteLexemePos(lexemePosId);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = cudDbService.getLexemeDerivId(lexemeId, derivCode);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode, null);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = cudDbService.getLexemeRegisterId(lexemeId, registerCode);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode, null);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode) {
		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = cudDbService.getLexemeRegionId(lexemeId, regionCode);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, regionCode, null);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId);
		cudDbService.deleteLexemeRelation(relationId);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId) {
		// TODO delete only lexemes that are allowed by role check. If there are lexemes that are not allowed to delete, then do not delete meaning.
		List<Long> lexemeIds = termSearchDbService.getMeaningLexemeIds(meaningId);
		for (Long lexemeId : lexemeIds) {
			boolean isOnlyLexemeForWord = termSearchDbService.isOnlyLexemeForWord(lexemeId);
			Long wordId = cudDbService.getLexemeWordId(lexemeId);

			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
			updateLexemeLevels(lexemeId, "delete");
			cudDbService.deleteLexeme(lexemeId);

			if (isOnlyLexemeForWord) {
				deleteWord(wordId);
			}
		}
		deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaning(Long meaningId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain) {
		if (domain != null) {
			Long meaningDomainId = cudDbService.getMeaningDomainId(meaningId, domain);
			createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode(), null);
			cudDbService.deleteMeaningDomain(meaningDomainId);
		}
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId);
		cudDbService.deleteMeaningRelation(relationId);
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteMeaningPublicNote(Long id) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING_PUBLIC_NOTE, LifecycleProperty.VALUE, id);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId) {
		createLifecycleLog(LifecycleEventType.DELETE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId, null);
		cudDbService.deleteFreeformRefLink(sourceLinkId);
	}

}
