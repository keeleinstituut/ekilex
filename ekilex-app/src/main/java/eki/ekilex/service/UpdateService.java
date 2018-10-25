package eki.ekilex.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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

	private final LifecycleLogDbService lifecycleLogDbService;

	public UpdateService(UpdateDbService updateDbService, LifecycleLogDbService lifecycleLogDbService) {
		this.updateDbService  = updateDbService;
		this.lifecycleLogDbService = lifecycleLogDbService;
	}

	// --- UPDATE ---

	@Transactional
	public void updateUsageValue(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateGovernment(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateGrammar(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id, value);
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String value) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id, value);
		updateDbService.updateDefinitionValue(id, value);
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
	public void updateWordType(Long wordId, String typeCode) {
		lifecycleLogDbService.addLog(LifecycleEventType.UPDATE, LifecycleEntity.WORD, LifecycleProperty.WORD_TYPE, wordId, typeCode);
		updateDbService.updateWordType(wordId, typeCode);
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

	// --- ADD ---

	@Transactional
	public void addWord(String word, String datasetCode, String language, String morphCode) {
		Long wordId = updateDbService.addWord(word, datasetCode, language, morphCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.WORD, LifecycleProperty.VALUE, wordId, word);
	}

	@Transactional
	public void addWordToDataset(Long wordId, String datasetCode) {
		Long lexemeId = updateDbService.addWordToDataset(wordId, datasetCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.LEXEME, LifecycleProperty.DATASET, lexemeId, datasetCode);
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
	public void addUsage(Long lexemeId, String value, String languageCode) {
		Long usageId = updateDbService.addUsage(lexemeId, value, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, usageId, value);
	}

	@Transactional
	public void addUsageTranslation(Long usageId, String value, String languageCode) {
		Long usageTranslationId = updateDbService.addUsageTranslation(usageId, value, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, usageTranslationId, value);
	}

	@Transactional
	public void addUsageDefinition(Long usageId, String value, String languageCode) {
		Long usageDefinitionId = updateDbService.addUsageDefinition(usageId, value, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, usageDefinitionId, value);
	}

	@Transactional
	public void addMeaningDomain(Long meaningId, Classifier domain) {
		Long meaningDomainId = updateDbService.addMeaningDomain(meaningId, domain);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
	}

	@Transactional
	public void addDefinition(Long meaningId, String value, String languageCode) {
		Long definitionId = updateDbService.addDefinition(meaningId, value, languageCode);
		lifecycleLogDbService.addLog(LifecycleEventType.CREATE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, definitionId, value);
	}

	@Transactional
	public void addFreeformSourceLink(Long freeformId, Long sourceId, String sourceValue, String sourceName) {
		//TODO ref type should also be set user
		ReferenceType refType = ReferenceType.ANY;
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

	//TODO lifecycle log
	@Transactional
	public void joinLexemeMeanings(Long lexemeId, Long lexemeId2) {
		updateDbService.joinLexemeMeanings(lexemeId, lexemeId2);
	}

	//TODO lifecycle log
	@Transactional
	public void separateLexemeMeanings(Long lexemeId) {
		updateDbService.separateLexemeMeanings(lexemeId);
	}

	// --- DELETE ---

	@Transactional
	public void deleteUsage(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageTranslation(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_TRANSLATION, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteUsageDefinition(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.USAGE_DEFINITION, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGovernment(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.GOVERNMENT, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteGrammar(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.GRAMMAR, LifecycleProperty.VALUE, id);
		updateDbService.deleteFreeform(id);
	}

	@Transactional
	public void deleteDefinition(Long id) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION, LifecycleProperty.VALUE, id);
		updateDbService.deleteDefinition(id);
	}

	@Transactional
	public void deleteDefinitionSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.DEFINITION_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId);
		updateDbService.deleteDefinitionRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteFreeformSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.FREEFORM_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId);
		updateDbService.deleteFreeformRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemeSourceLink(Long sourceLinkId) {
		lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME_SOURCE_LINK, LifecycleProperty.VALUE, sourceLinkId);
		updateDbService.deleteLexemeRefLink(sourceLinkId);
	}

	@Transactional
	public void deleteLexemePos(Long lexemeId, String posCode) {
		if (StringUtils.isNotBlank(posCode)) {
			Long lexemePosId = updateDbService.deleteLexemePos(lexemeId, posCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.POS, lexemePosId, posCode);
		}
	}

	@Transactional
	public void deleteLexemeDeriv(Long lexemeId, String derivCode) {
		if (StringUtils.isNotBlank(derivCode)) {
			Long lexemeDerivId = updateDbService.deleteLexemeDeriv(lexemeId, derivCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.DERIV, lexemeDerivId, derivCode);
		}
	}

	@Transactional
	public void deleteLexemeRegister(Long lexemeId, String registerCode) {
		if (StringUtils.isNotBlank(registerCode)) {
			Long lexemeRegisterId = updateDbService.deleteLexemeRegister(lexemeId, registerCode);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.LEXEME, LifecycleProperty.REGISTER, lexemeRegisterId, registerCode);
		}
	}

	@Transactional
	public void deleteMeaningDomain(Long meaningId,  Classifier domain) {
		if (domain != null) {
			Long meaningDomainId = updateDbService.deleteMeaningDomain(meaningId, domain);
			lifecycleLogDbService.addLog(LifecycleEventType.DELETE, LifecycleEntity.MEANING, LifecycleProperty.DOMAIN, meaningDomainId, domain.getCode());
		}
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
