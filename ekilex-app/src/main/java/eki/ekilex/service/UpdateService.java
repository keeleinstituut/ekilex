package eki.ekilex.service;

import eki.common.constant.FreeformType;
import eki.ekilex.data.ListData;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.UpdateDbService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateService {

	private final UpdateDbService updateDbService;

	public UpdateService(UpdateDbService updateDbService) {
		this.updateDbService  = updateDbService;
	}

	@Transactional
	public void updateUsageValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String value) {
		updateDbService.updateDefinitionValue(id, value);
	}

	@Transactional
	public void updateDefinitionOrdering(List<ListData> items) {
		updateDbService.updateDefinitionOrderby(items);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<ListData> items) {
		updateDbService.updateLexemeRelationOrderby(items);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<ListData> items) {
		updateDbService.updateMeaningRelationOrderby(items);
	}

	@Transactional
	public void updateWordRelationOrdering(List<ListData> items) {
		updateDbService.updateWordRelationOrderby(items);
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, String action) {

		if (lexemeId == null) return;

		List<WordLexeme> lexemes = updateDbService.findConnectedLexemes(lexemeId).into(WordLexeme.class);
		changeLevels(lexemes, lexemeId, action);
		for (WordLexeme lexeme: lexemes) {
			updateDbService.updateLexemeLevels(lexeme.getLexemeId(), lexeme.getLevel1(), lexeme.getLevel2(), lexeme.getLevel3());
		}
	}

	@Transactional
	public void updateLexemeFrequencyGroup(Long lexemeId, String groupCode) {
		updateDbService.updateLexemeFrequencyGroup(lexemeId, groupCode);
	}

	@Transactional
	public void updateLexemePos(Long lexemeId, String currentPos, String newPos) {
		updateDbService.updateLexemePos(lexemeId, currentPos, newPos);
	}

	@Transactional
	public void updateMeaningDomain(Long meaningId, Classifier currentDomain, Classifier newDomain) {
		updateDbService.updateMeaningDomain(meaningId, currentDomain, newDomain);
	}

	@Transactional
	public void updateGovernment(Long governmentId, String government) {
		updateDbService.updateGovernment(governmentId, government);
	}

	@Transactional
	public void addLexemePos(Long lexemeId, String posCode) {
		updateDbService.addLexemePos(lexemeId, posCode);
	}

	@Transactional
	public void addMeaningDomain(Long meaningId, Classifier domain) {
		updateDbService.addMeaningDomain(meaningId, domain);
	}

	@Transactional
	public void addGovernment(Long lexemeId, String government) {
		updateDbService.addGovernment(lexemeId, government);
	}

	@Transactional
	public void addWord(String word, String datasetCode, String language, String morphCode) {
		updateDbService.addWord(word, datasetCode, language, morphCode);
	}

	@Transactional
	public void addWordToDataset(Long wordId, String datasetCode) {
		updateDbService.addWordToDataset(wordId, datasetCode);
	}

	@Transactional
	public void joinLexemeMeanings(Long lexemeId, Long lexemeId2) {
		updateDbService.joinLexemeMeanings(lexemeId, lexemeId2);
	}

	@Transactional
	public void separateLexemeMeanings(Long lexemeId) {
		updateDbService.separateLexemeMeanings(lexemeId);
	}

	@Transactional
	public void removeUsage(Long id) {
		updateDbService.removeFreeform(id);
	}

	@Transactional
	public void removeUsageTranslation(Long id) {
		updateDbService.removeFreeform(id);
	}

	@Transactional
	public void removeUsageDefinition(Long id) {
		updateDbService.removeFreeform(id);
	}

	@Transactional
	public void removeDefinition(Long id) {
		updateDbService.removeDefinition(id);
	}

	@Transactional
	public void removeGovernment(Long governmentId) {
		updateDbService.removeLexemeFreeform(governmentId);
		updateDbService.removeFreeform(governmentId);
	}

	@Transactional
	public void addDefinition(Long meaningId, String value, String languageCode) {
		updateDbService.addDefinition(meaningId, value, languageCode);
	}

	@Transactional
	public void addUsageMember(Long id, String usageMemberType, String value, String languageCode) {
		if ("USAGE_MEANING".equals(usageMemberType)) {
			Long usageMeaningId = updateDbService.addUsageMeaning(id);
			updateDbService.addUsageMeaningMember(usageMeaningId, FreeformType.USAGE.name(), value, languageCode);
		} else {
			updateDbService.addUsageMeaningMember(id, usageMemberType, value, languageCode);
		}
	}

	@Transactional
	public void removeLexemePos(Long lexemeId, String posCode) {
		if (posCode != null) {
			updateDbService.removeLexemePos(lexemeId, posCode);
		}
	}

	@Transactional
	public void removeMeaningDomain(Long meaningId,  Classifier domain) {
		if (domain != null) {
			updateDbService.removeMeaningDomain(meaningId, domain);
		}
	}

	void changeLevels(List<WordLexeme> lexemes, Long lexemeId, String action) {
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
