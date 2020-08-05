package eki.ekilex.service;

import java.math.BigDecimal;
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
import eki.common.constant.GlobalConstant;
import eki.common.constant.LifecycleEntity;
import eki.common.constant.LifecycleEventType;
import eki.common.constant.LifecycleProperty;
import eki.common.constant.RelationStatus;
import eki.common.constant.WordRelationGroupType;
import eki.common.service.TextDecorationService;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ListData;
import eki.ekilex.data.LogData;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.Tag;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordLexemeMeaningDetails;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@PreAuthorize("authentication.principal.datasetCrudPermissionsExist")
@Component
public class CudService extends AbstractService implements GlobalConstant {

	private static final String RAW_RELATION_TYPE = "raw";

	private static final String USER_ADDED_WORD_RELATION_NAME = "user";

	private static final String UNDEFINED_RELATION_STATUS = RelationStatus.UNDEFINED.name();

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	// --- UPDATE ---

	@Transactional
	public void updateWordValue(Long wordId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, valuePrese);
		createLifecycleLog(logData);
		SimpleWord originalWord = cudDbService.getSimpleWord(wordId);
		String lang = originalWord.getLang();
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String valueAsWord = textDecorationService.removeAccents(value, lang);
		cudDbService.updateWordValue(wordId, value, valuePrese);
		if (StringUtils.isNotEmpty(valueAsWord)) {
			cudDbService.updateAsWordValue(wordId, valueAsWord);
		}
		SimpleWord updatedWord = new SimpleWord(wordId, value, lang);
		cudDbService.adjustWordHomonymNrs(originalWord);
		cudDbService.adjustWordHomonymNrs(updatedWord);
	}

	@Transactional
	public void updateWordVocalForm(Long wordId, String vocalForm) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.VOCAL_FORM, wordId, vocalForm);
		createLifecycleLog(logData);
		cudDbService.updateWordVocalForm(wordId, vocalForm);
	}

	@Transactional
	public void updateWordType(Long wordId, String currentTypeCode, String newTypeCode) {
		Long wordWordTypeId = cudDbService.updateWordType(wordId, currentTypeCode, newTypeCode);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, currentTypeCode, newTypeCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateWordAspect(Long wordId, String typeCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.ASPECT, wordId, typeCode);
		createLifecycleLog(logData);
		cudDbService.updateWordAspect(wordId, typeCode);
	}

	@Transactional
	public void updateWordGender(Long wordId, String genderCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.GENDER, wordId, genderCode);
		createLifecycleLog(logData);
		cudDbService.updateWordGender(wordId, genderCode);
	}

	@Transactional
	public void updateWordLang(Long wordId, String langCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.LANG, wordId, langCode);
		createLifecycleLog(logData);
		cudDbService.updateWordLang(wordId, langCode);
	}

	@Transactional
	public void updateWordNote(Long noteFreeformId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.NOTE, noteFreeformId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(noteFreeformId, value, valuePrese);
	}

	@Transactional
	public void updateWordLexemesTagComplete(Long wordId, String userRoleDatasetCode, Tag tag) {

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();
		List<Long> updatedLexemeIds;
		LifecycleEventType eventType;

		if (removeToComplete) {
			updatedLexemeIds = cudDbService.deleteWordLexemesTag(wordId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.DELETE;
		} else {
			updatedLexemeIds = cudDbService.createWordLexemesTag(wordId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.CREATE;
		}

		updatedLexemeIds.forEach(lexemeId -> {
			LogData logData = new LogData(eventType, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(logData);
		});
	}

	@Transactional
	public void updateMeaningLexemesTagComplete(Long meaningId, String userRoleDatasetCode, Tag tag) {

		String tagName = tag.getName();
		boolean removeToComplete = tag.isRemoveToComplete();
		List<Long> updatedLexemeIds;
		LifecycleEventType eventType;

		if (removeToComplete) {
			updatedLexemeIds = cudDbService.deleteMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.DELETE;
		} else {
			updatedLexemeIds = cudDbService.createMeaningLexemesTag(meaningId, userRoleDatasetCode, tagName);
			eventType = LifecycleEventType.CREATE;
		}

		updatedLexemeIds.forEach(lexemeId -> {
			LogData logData = new LogData(eventType, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
			createLifecycleLog(logData);
		});
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.WORD_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateWordRelationOrderby(item);
		}
	}

	@Transactional
	public void updateWordEtymologyOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.WORD_ETYMOLOGY, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateWordEtymologyOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningDomainOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateMeaningDomainOrderby(item);
		}
	}

	@Transactional
	public void updateGovernmentOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.GOVERNMENT, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateFreeformOrderby(item);
		}
	}

	@Transactional
	public void updateUsageOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.USAGE, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateFreeformOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeNoteOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateFreeformOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningNoteOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateFreeformOrderby(item);
		}
	}

	@Transactional
	public void updateDefinitionNoteOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateFreeformOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeMeaningWordOrdering(List<ListData> items, Long lexemeId) {
		LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.MEANING_WORD, lexemeId);
		createLifecycleLog(logData);
		for (ListData item : items) {
			cudDbService.updateLexemeOrderby(item);
		}
	}

	//@PreAuthorize("hasPermission(#id, 'USAGE', 'DATASET:CRUD')")
	@Transactional
	public void updateUsageValue(Long id, String valuePrese, Complexity complexity, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeform(id, value, valuePrese, complexity, isPublic);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateLexemeOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateLexemeOrderby(item);
		}
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) {
			return;
		}

		List<WordLexeme> lexemes = lookupDbService.getWordPrimaryLexemes(lexemeId);
		lexemeLevelCalcUtil.recalculateLevels(lexemeId, lexemes, action);
		for (WordLexeme lexeme : lexemes) {
			String logEntry = StringUtils.joinWith(".", lexeme.getLevel1(), lexeme.getLevel2());
			LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.LEVEL, lexeme.getLexemeId(), logEntry);
			createLifecycleLog(logData);
			cudDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2());
		}
	}

	@Transactional
	public void updateLexemeGovernment(Long id, String value, Complexity complexity) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, value);
		createLifecycleLog(logData);
		cudDbService.updateFreeformTextValueAndComplexity(id, value, value, complexity);
	}

	@Transactional
	public void updateLexemeGrammar(Long id, String valuePrese, Complexity complexity) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValueAndComplexity(id, value, valuePrese, complexity);
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String freqGroupCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.FREQUENCY_GROUP, lexemeId, freqGroupCode);
		createLifecycleLog(logData);
		cudDbService.updateLexemeFrequencyGroup(lexemeId, freqGroupCode);
	}

	@Transactional
	public void updateLexemeComplexity(Long lexemeId, String complexity) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.COMPLEXITY, lexemeId, complexity);
		createLifecycleLog(logData);
		cudDbService.updateLexemeComplexity(lexemeId, complexity);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		Long lexemePosId = cudDbService.updateLexemePos(lexemeId, currentPos, newPos);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, currentPos, newPos);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeDeriv(Long lexemeId, String currentDeriv, String newDeriv) {
		Long lexemeDerivid = cudDbService.updateLexemeDeriv(lexemeId, currentDeriv, newDeriv);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivid, currentDeriv, newDeriv);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeRegister(Long lexemeId, String currentRegister, String newRegister) {
		Long lexemeRegisterId = cudDbService.updateLexemeRegister(lexemeId, currentRegister, newRegister);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, currentRegister, newRegister);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeRegion(Long lexemeId, String currentRegion, String newRegion) {
		Long lexemeRegionId = cudDbService.updateLexemeRegion(lexemeId, currentRegion, newRegion);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, currentRegion, newRegion);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateLexemeNote(Long id, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeform(id, value, valuePrese, lang, complexity, isPublic);
	}

	@Transactional
	public void updateLexemeProcessState(Long lexemeId, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.PUBLICITY, lexemeId, String.valueOf(isPublic));
		createLifecycleLog(logData);
		cudDbService.updateLexemeProcessState(lexemeId, isPublic);
	}

	@Transactional
	public void updateLexemeValueState(Long lexemeId, String valueStateCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE_STATE, lexemeId, valueStateCode);
		createLifecycleLog(logData);
		cudDbService.updateLexemeValueState(lexemeId, valueStateCode);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateLexemeRelationOrderby(item);
		}
	}

	@Transactional
	public void updateDefinition(Long id, String valuePrese, String lang, Complexity complexity, String typeCode, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateDefinition(id, value, valuePrese, lang, complexity, typeCode, isPublic);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.DEFINITION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateDefinitionOrderby(item);
		}
	}

	@Transactional
	public void updateDefinitionNote(Long id, String valuePrese, String lang, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeform(id, value, valuePrese, lang, isPublic);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) {
		for (ListData item : items) {
			LogData logData = new LogData(LifecycleEventType.ORDER_BY, LifecycleEntity.MEANING_RELATION, LifecycleProperty.ID, item);
			createListOrderingLifecycleLog(logData);
			cudDbService.updateMeaningRelationOrderby(item);
		}
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		Long meaningDomainId = cudDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, currentDomain.getCode(), newDomain.getCode());
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateMeaningLearnerComment(Long id, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(id, value, valuePrese);
	}

	@Transactional
	public void updateMeaningNote(Long id, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, id, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeform(id, value, valuePrese, lang, complexity, isPublic);
	}

	@Transactional
	public void updateMeaningSemanticType(Long meaningId, String currentSemanticType, String newSemanticType) {
		Long meaningSemanticTypeId = cudDbService.updateMeaningSemanticType(meaningId, currentSemanticType, newSemanticType);
		LogData logData = new LogData(
				LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, currentSemanticType, newSemanticType);
		createLifecycleLog(logData);
	}

	@Transactional
	public void updateImageTitle(Long imageId, String valuePrese) {
		String recent = lookupDbService.getImageTitle(imageId);
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, imageId, recent, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateImageTitle(imageId, value);
	}

	@Transactional
	public void updateOdWordRecommendation(Long freeformId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, freeformId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(freeformId, value, valuePrese);
	}

	@Transactional
	public void updateOdLexemeRecommendation(Long freeformId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, freeformId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(freeformId, value, valuePrese);
	}

	@Transactional
	public void updateOdUsageDefinition(Long freeformId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, freeformId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(freeformId, value, valuePrese);
	}

	@Transactional
	public void updateOdUsageAlternative(Long freeformId, String valuePrese) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, freeformId, valuePrese);
		createLifecycleLog(logData);
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.updateFreeformTextValue(freeformId, value, valuePrese);
	}

	@Transactional
	public void updateLexemeWeight(Long lexemeId, String lexemeWeightStr) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.LEXEME, LifecycleProperty.WEIGHT, lexemeId, lexemeWeightStr);
		createLifecycleLog(logData);
		BigDecimal lexemeWeight = new BigDecimal(lexemeWeightStr);
		cudDbService.updateLexemeWeight(lexemeId, lexemeWeight);
	}

	@Transactional
	public void updateWordDataAndLexemeWeight(WordLexemeMeaningDetails wordDataAndLexemeWeight) {

		Long wordId = wordDataAndLexemeWeight.getWordId();
		Long lexemeId = wordDataAndLexemeWeight.getLexemeId();
		String morphCode = wordDataAndLexemeWeight.getMorphCode();
		String lexemeWeight = wordDataAndLexemeWeight.getLexemeWeight();
		String wordValuePrese = wordDataAndLexemeWeight.getWordValuePrese();

		updateWordValue(wordId, wordValuePrese);
		updateLexemeWeight(lexemeId, lexemeWeight);
		updateWordMorphCode(wordId, morphCode);
	}

	private void updateWordMorphCode(Long wordId, String morphCode) {
		LogData logData = new LogData(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.MORPH_CODE, wordId, morphCode);
		createLifecycleLog(logData);
		cudDbService.updateWordMorphCode(wordId, morphCode);
	}

	// --- CREATE ---

	@Transactional
	public void createWord(WordLexemeMeaningDetails wordDetails) {

		String value = wordDetails.getWordValue();
		String language = wordDetails.getLanguage();
		String morphCode = wordDetails.getMorphCode();
		String dataset = wordDetails.getDataset();
		Long meaningId = wordDetails.getMeaningId();

		value = textDecorationService.removeEkiElementMarkup(value);
		String valueAsWord = textDecorationService.removeAccents(value, language);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, value, valueAsWord, language, morphCode, dataset, PUBLICITY_PUBLIC, meaningId);

		Long wordId = wordLexemeMeaningId.getWordId();
		Long lexemeId = wordLexemeMeaningId.getLexemeId();

		LogData wordLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, value);
		createLifecycleLog(wordLogData);
		LogData lexemeLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, dataset);
		createLifecycleLog(lexemeLogData);
	}

	@Transactional
	public void createWordType(Long wordId, String typeCode) {
		Long wordTypeId = cudDbService.createWordType(wordId, typeCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordTypeId, typeCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createWordRelation(Long wordId, Long targetWordId, String relationTypeCode, String oppositeRelationTypeCode) {
		Optional<WordRelationGroupType> wordRelationGroupType = WordRelationGroupType.toRelationGroupType(relationTypeCode);
		if (wordRelationGroupType.isPresent()) {
			boolean doLogging = false;
			String previousLogValue = null;
			Long groupId = lookupDbService.getWordRelationGroupId(relationTypeCode, wordId);
			if (groupId == null) {
				groupId = cudDbService.createWordRelationGroup(relationTypeCode);
				cudDbService.createWordRelationGroupMember(groupId, wordId);
				cudDbService.createWordRelationGroupMember(groupId, targetWordId);
				doLogging = true;
			} else {
				if (!lookupDbService.isMemberOfWordRelationGroup(groupId, targetWordId)) {
					List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
					previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
					cudDbService.createWordRelationGroupMember(groupId, targetWordId);
					doLogging = true;
				}
			}
			if (doLogging) {
				List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
				String logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
				for (Map<String, Object> member : wordRelationGroupMembers) {
					Long memberId = (Long) member.get("id");
					LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION_GROUP_MEMBER, LifecycleProperty.VALUE, memberId,
							previousLogValue, logValue);
					createLifecycleLog(logData);
				}
			}
		} else {
			Long relationId = cudDbService.createWordRelation(wordId, targetWordId, relationTypeCode, null);
			LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			createLifecycleLog(relationLogData);
			if (StringUtils.isNotEmpty(oppositeRelationTypeCode)) {
				boolean oppositeRelationExists = lookupDbService.wordRelationExists(targetWordId, wordId, oppositeRelationTypeCode);
				if (oppositeRelationExists) {
					return;
				}
				Long oppositeRelationId = cudDbService.createWordRelation(targetWordId, wordId, oppositeRelationTypeCode, null);
				LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, oppositeRelationId);
				createLifecycleLog(oppositeRelationLogData);
			}
		}
	}

	@Transactional
	public void createWordNote(Long wordId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long wordFreeformId = cudDbService.createWordNote(wordId, value, valuePrese, LANGUAGE_CODE_EST, Complexity.DETAIL, false);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.NOTE, wordFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexeme(Long wordId, String datasetCode, Long meaningId) {
		int currentLexemesMaxLevel1 = lookupDbService.getWordLexemesMaxLevel1(wordId, datasetCode);
		int lexemeLevel1 = currentLexemesMaxLevel1 + 1;

		Long lexemeId = cudDbService.createLexeme(wordId, datasetCode, meaningId, lexemeLevel1);
		if (lexemeId == null) {
			return;
		}
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createUsage(Long lexemeId, String valuePrese, String languageCode, Complexity complexity, boolean isPublic) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long usageId = cudDbService.createUsage(lexemeId, value, valuePrese, languageCode, complexity, isPublic);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createUsageTranslation(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long usageTranslationId = cudDbService.createUsageTranslation(usageId, value, valuePrese, languageCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createUsageDefinition(Long usageId, String valuePrese, String languageCode) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long usageDefinitionId = cudDbService.createUsageDefinition(usageId, value, valuePrese, languageCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemePos(Long lexemeId, String posCode) {
		Long lexemePosId = cudDbService.createLexemePos(lexemeId, posCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeTag(Long lexemeId, String tagName) {
		cudDbService.createLexemeTag(lexemeId, tagName);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeDeriv(Long lexemeId, String derivCode) {
		Long lexemeDerivId = cudDbService.createLexemeDeriv(lexemeId, derivCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeRegister(Long lexemeId, String registerCode) {
		Long lexemeRegisterId = cudDbService.createLexemeRegister(lexemeId, registerCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeRegion(Long lexemeId, String regionCode) {
		Long lexemeRegionId = cudDbService.createLexemeRegion(lexemeId, regionCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.REGION, lexemeRegionId, regionCode);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeGovernment(Long lexemeId, String government, Complexity complexity) {
		Long governmentId = cudDbService.createLexemeGovernment(lexemeId, government, complexity);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, governmentId, government);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeGrammar(Long lexemeId, String valuePrese, Complexity complexity) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long grammarId = cudDbService.createLexemeGrammar(lexemeId, value, valuePrese, complexity);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, grammarId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeNote(Long lexemeId, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long lexemeFreeformId = cudDbService.createLexemeNote(lexemeId, value, valuePrese, lang, complexity, isPublic);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, lexemeFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createLexemeRelation(Long lexemeId1, Long lexemeId2, String relationType, String oppositeRelationType) {
		Long relationId = cudDbService.createLexemeRelation(lexemeId1, lexemeId2, relationType);
		LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId, relationType);
		createLifecycleLog(relationLogData);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.lexemeRelationExists(lexemeId2, lexemeId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			Long oppositeRelationId = cudDbService.createLexemeRelation(lexemeId2, lexemeId1, oppositeRelationType);
			LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, oppositeRelationId, oppositeRelationType);
			createLifecycleLog(oppositeRelationLogData);
		}
	}

	@Transactional
	public void createMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = cudDbService.createMeaningDomain(meaningId, domain);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
		createLifecycleLog(logData);
	}

	@Transactional
	public void createMeaningRelation(Long meaningId1, Long meaningId2, String relationType, String oppositeRelationType) {
		Long relationId = cudDbService.createMeaningRelation(meaningId1, meaningId2, relationType);
		LogData relationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId, relationType);
		createLifecycleLog(relationLogData);
		if (StringUtils.isNotEmpty(oppositeRelationType)) {
			boolean oppositeRelationExists = lookupDbService.meaningRelationExists(meaningId2, meaningId1, oppositeRelationType);
			if (oppositeRelationExists) {
				return;
			}
			Long oppositeRelationId = cudDbService.createMeaningRelation(meaningId2, meaningId1, oppositeRelationType);
			LogData oppositeRelationLogData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, oppositeRelationId, oppositeRelationType);
			createLifecycleLog(oppositeRelationLogData);
		}
	}

	@Transactional
	public void createMeaningLearnerComment(Long meaningId, String valuePrese, String lang) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Complexity complexity = Complexity.SIMPLE;
		Long meaningFreeformId = cudDbService.createMeaningLearnerComment(meaningId, value, valuePrese, lang, complexity);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, meaningFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createMeaningNote(Long meaningId, String valuePrese, String lang, Complexity complexity, boolean isPublic) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long meaningFreeformId = cudDbService.createMeaningNote(meaningId, value, valuePrese, lang, complexity, isPublic);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, meaningFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createMeaningSemanticType(Long meaningId, String semanticTypeCode) {
		Long meaningSemanticTypeId = cudDbService.createMeaningSemanticType(meaningId, semanticTypeCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, semanticTypeCode);
		createLifecycleLog(logData);

	}

	@Transactional
	public void createDefinition(Long meaningId, String valuePrese, String languageCode, String datasetCode, Complexity complexity, String typeCode, boolean isPublic) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long definitionId = cudDbService.createDefinition(meaningId, value, valuePrese, languageCode, typeCode, complexity, isPublic);
		cudDbService.createDefinitionDataset(definitionId, datasetCode);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createDefinitionNote(Long definitionId, String valuePrese, String lang, boolean isPublic) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long definitionFreeformId = cudDbService.createDefinitionNote(definitionId, value, valuePrese, lang, isPublic);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, definitionFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	

	@Transactional
	public void createImageTitle(Long imageId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		cudDbService.createImageTitle(imageId, value);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, imageId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createOdWordRecommendation(Long wordId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Complexity complexity = Complexity.DETAIL;
		Long wordFreeformId = cudDbService.createOdWordRecommendation(wordId, value, valuePrese, complexity);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, wordFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createOdLexemeRecommendation(Long lexemeId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Complexity complexity = Complexity.DETAIL;
		Long lexemeFreeformId = cudDbService.createOdLexemeRecommendation(lexemeId, value, valuePrese, complexity);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, lexemeFreeformId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createOdUsageDefinition(Long usageId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long odUsageDefinitionId = cudDbService.createOdUsageDefinition(usageId, value, valuePrese);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, odUsageDefinitionId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createOdUsageAlternative(Long usageId, String valuePrese) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		Long odUsageAlternativeId = cudDbService.createOdUsageAlternative(usageId, value, valuePrese);
		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, odUsageAlternativeId, valuePrese);
		createLifecycleLog(logData);
	}

	@Transactional
	public void createWordAndSynRelation(Long existingWordId, String valuePrese, String datasetCode, String language, String morphCode, String weightStr) {
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		String valueAsWord = textDecorationService.removeAccents(value, language);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = cudDbService
				.createWordAndLexeme(value, valuePrese, valueAsWord, language, morphCode, datasetCode, PUBLICITY_PRIVATE, null);
		// TODO set automatically tags - yogesh
		Long createdWordId = wordLexemeMeaningId.getWordId();

		LogData logData = new LogData(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, createdWordId, valuePrese);
		createLifecycleLog(logData);

		Long createdRelationId = cudDbService.createWordRelation(existingWordId, createdWordId, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedRelationToFirst(existingWordId, createdRelationId);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
	}

	@Transactional
	public void createSynRelation(Long word1Id, Long word2Id, String weightStr, String datasetCode) {

		boolean word2DatasetLexemeExists = lookupDbService.wordLexemeExists(word2Id, datasetCode);
		if (!word2DatasetLexemeExists) {
			createLexeme(word2Id, datasetCode, null);
		}
		Long createdRelationId = cudDbService.createWordRelation(word1Id, word2Id, RAW_RELATION_TYPE, UNDEFINED_RELATION_STATUS);
		moveCreatedRelationToFirst(word1Id, createdRelationId);
		BigDecimal weight = new BigDecimal(weightStr);
		cudDbService.createWordRelationParam(createdRelationId, USER_ADDED_WORD_RELATION_NAME, weight);
	}

	// --- DELETE ---

	@Transactional
	public void deleteWord(Long wordId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId);
		createLifecycleLog(logData);
		SimpleWord word = lookupDbService.getSimpleWord(wordId);
		cudDbService.deleteWord(word);
	}

	@Transactional
	public void deleteWordType(Long wordId, String typeCode) {
		if (StringUtils.isNotBlank(typeCode)) {
			Long wordWordTypeId = lookupDbService.getWordWordTypeId(wordId, typeCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordWordTypeId, typeCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteWordWordType(wordWordTypeId);
		}
	}

	@Transactional
	public void deleteWordRelation(Long relationId) {
		Long groupId = lookupDbService.getWordRelationGroupId(relationId);
		if (groupId == null) {
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION, LifecycleProperty.VALUE, relationId);
			createLifecycleLog(logData);
			cudDbService.deleteWordRelation(relationId);
		} else {
			List<Map<String, Object>> wordRelationGroupMembers = lookupDbService.getWordRelationGroupMembers(groupId);
			String relationTypeCode = wordRelationGroupMembers.get(0).get("word_rel_type_code").toString();
			String previousLogValue = relationTypeCode + " : " + wordRelationGroupMembers.stream().map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			String logValue = null;
			if (wordRelationGroupMembers.size() > 2) {
				logValue = relationTypeCode + " : " + wordRelationGroupMembers.stream()
						.filter(m -> !relationId.equals(m.get("id")))
						.map(m -> m.get("value").toString()).collect(Collectors.joining(","));
			}
			for (Map<String, Object> member : wordRelationGroupMembers) {
				Long memberId = (Long) member.get("id");
				LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD_RELATION_GROUP_MEMBER, LifecycleProperty.VALUE, memberId,
						previousLogValue, logValue);
				createLifecycleLog(logData);
			}
			cudDbService.deleteWordRelationGroupMember(relationId);
			if (wordRelationGroupMembers.size() <= 2) {
				cudDbService.deleteWordRelationGroup(groupId);
			}
		}
	}

	@Transactional
	public void deleteWordNote(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.NOTE, id);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteDefinition(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteDefinition(id);
	}

	@Transactional
	public void deleteDefinitionNote(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.NOTE, id);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexeme(Long lexemeId) {

		boolean isOnlyLexemeForMeaning = lookupDbService.isOnlyLexemeForMeaning(lexemeId);
		boolean isOnlyLexemeForWord = lookupDbService.isOnlyLexemeForWord(lexemeId);
		WordLexemeMeaningIdTuple wordLexemeMeaningId = commonDataDbService.getWordLexemeMeaningId(lexemeId);
		Long wordId = wordLexemeMeaningId.getWordId();
		Long meaningId = wordLexemeMeaningId.getMeaningId();

		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.VALUE, lexemeId);
		createLifecycleLog(logData);
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
	public void deleteLexemeAndMeaningLexemes(Long lexemeId, String meaningLexemesLang, String datasetCode) {
		Long meaningId = lookupDbService.getLexemeMeaningId(lexemeId);
		List<Long> lexemeIdsToDelete = lookupDbService.getMeaningLexemeIds(meaningId, meaningLexemesLang, datasetCode);
		if (!lexemeIdsToDelete.contains(lexemeId)) {
			lexemeIdsToDelete.add(lexemeId);
		}

		for (Long lexemeIdToDelete : lexemeIdsToDelete) {
			deleteLexeme(lexemeIdToDelete);
		}
	}

	@Transactional
	public void deleteUsage(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageTranslation(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageDefinition(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeGovernment(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeGrammar(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemeNote(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.NOTE, id);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = lookupDbService.getLexemePosId(lexemeId, posCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteLexemePos(lexemePosId);
		}
	}

	@Transactional
	public void deleteLexemeTag(Long lexemeId, String tagName) {
		if (StringUtils.isNotBlank(tagName)) {
			Long lexemeTagId = lookupDbService.getLexemeTagId(lexemeId, tagName);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.TAG, lexemeId, tagName, null);
			createLifecycleLog(logData);
			cudDbService.deleteLexemeTag(lexemeTagId);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = lookupDbService.getLexemeDerivId(lexemeId, derivCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteLexemeDeriv(lexemeDerivId);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = lookupDbService.getLexemeRegisterId(lexemeId, registerCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteLexemeRegister(lexemeRegisterId);
		}
	}

	@Transactional
	public void deleteLexemeRegion(Long lexemeId, String regionCode) {
		if (StringUtils.isNotBlank(regionCode)) {
			Long lexemeRegionId = lookupDbService.getLexemeRegionId(lexemeId, regionCode);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegionId, regionCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteLexemeRegion(lexemeRegionId);
		}
	}

	@Transactional
	public void deleteLexemeRelation(Long relationId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_RELATION, LifecycleProperty.VALUE, relationId);
		createLifecycleLog(logData);
		cudDbService.deleteLexemeRelation(relationId);
	}

	@Transactional
	public void deleteMeaningAndLexemes(Long meaningId, String datasetCode) {

		List<WordLexemeMeaningIdTuple> wordLexemeMeaningIds = lookupDbService.getWordLexemeMeaningIds(meaningId, datasetCode);
		for (WordLexemeMeaningIdTuple wordLexemeMeaningId : wordLexemeMeaningIds) {
			Long lexemeId = wordLexemeMeaningId.getLexemeId();
			deleteLexeme(lexemeId);
		}
	}

	@Transactional
	public void deleteMeaning(Long meaningId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.VALUE, meaningId);
		createLifecycleLog(logData);
		cudDbService.deleteMeaning(meaningId);
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId, Classifier domain) {
		if (domain != null) {
			Long meaningDomainId = lookupDbService.getMeaningDomainId(meaningId, domain);
			LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode(), null);
			createLifecycleLog(logData);
			cudDbService.deleteMeaningDomain(meaningDomainId);
		}
	}

	@Transactional
	public void deleteMeaningSemanticType(Long meaningId, String semanticTypeCode) {
		if (StringUtils.isNotBlank(semanticTypeCode)) {
			Long meaningSemanticTypeId = lookupDbService.getMeaningSemanticTypeId(meaningId, semanticTypeCode);
			LogData logData = new LogData(
					LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.SEMANTIC_TYPE, meaningSemanticTypeId, semanticTypeCode, null);
			createLifecycleLog(logData);
			cudDbService.deleteMeaningSemanticType(meaningSemanticTypeId);
		}
	}

	@Transactional
	public void deleteMeaningRelation(Long relationId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING_RELATION, LifecycleProperty.VALUE, relationId);
		createLifecycleLog(logData);
		cudDbService.deleteMeaningRelation(relationId);
	}

	@Transactional
	public void deleteMeaningLearnerComment(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEARNER_COMMENT, LifecycleProperty.VALUE, id);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteMeaningNote(Long id) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.NOTE, id);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteImageTitle(Long imageId) {
		String recent = lookupDbService.getImageTitle(imageId);
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE_TITLE, imageId, recent, null);
		createLifecycleLog(logData);
		cudDbService.deleteImageTitle(imageId);
	}

	@Transactional
	public void deleteMeaningImage(Long freeformId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.IMAGE, freeformId);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(freeformId);
	}

	@Transactional
	public void deleteOdWordRecommendation(Long freeformId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.WORD, LifecycleProperty.OD_RECOMMENDATION, freeformId);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(freeformId);
	}

	@Transactional
	public void deleteOdLexemeRecommendation(Long freeformId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.OD_RECOMMENDATION, freeformId);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(freeformId);
	}

	@Transactional
	public void deleteOdUsageDefinition(Long freeformId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.OD_DEFINITION, freeformId, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(freeformId);
	}

	@Transactional
	public void deleteOdUsageAlternative(Long freeformId) {
		LogData logData = new LogData(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.OD_ALTERNATIVE, freeformId, null);
		createLifecycleLog(logData);
		cudDbService.deleteFreeform(freeformId);
	}

	private void moveCreatedRelationToFirst(Long wordId, Long relationId) {
		List<Relation> existingRelations = lookupDbService.getWordRelations(wordId, RAW_RELATION_TYPE);
		if (existingRelations.size() > 1) {

			Relation firstRelation = existingRelations.get(0);
			List<Long> existingOrderByValues = existingRelations.stream().map(Relation::getOrderBy).collect(Collectors.toList());

			cudDbService.updateWordRelationOrderBy(relationId, firstRelation.getOrderBy());
			existingRelations.remove(existingRelations.size() - 1);
			existingOrderByValues.remove(0);

			int relIdx = 0;
			for (Relation relation : existingRelations) {
				cudDbService.updateWordRelationOrderBy(relation.getId(), existingOrderByValues.get(relIdx));
				relIdx++;
			}
		}
	}
}
