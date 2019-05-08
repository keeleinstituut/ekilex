package eki.ekilex.service.util;

import static java.lang.Math.max;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordLexeme;

@Component
public class LexemeLevelCalcUtil {

	public void combineLevels(List<WordLexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		lexemes.forEach(lexeme -> {
			if (lexeme.getLevel1() == 0) {
				lexeme.setLevels(null);
				return;
			}
			String levels;
			long nrOfLexemesWithSameLevel1 = lexemes.stream()
					.filter(otherLexeme -> otherLexeme.getLevel1().equals(lexeme.getLevel1())
							&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme -> otherLexeme.getLevel1().equals(lexeme.getLevel1())
								&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
								&& StringUtils.equals(otherLexeme.getDatasetCode(), lexeme.getDatasetCode()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					int level2 = max(lexeme.getLevel2() - 1, 0);
					levels = lexeme.getLevel1() + (level2 == 0 ? "" : "." + level2);
				} else {
					int level3 = max(lexeme.getLevel3() - 1, 0);
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + (level3 == 0 ? "" : "." + level3);
				}
			}
			lexeme.setLevels(levels);
		});
	}

	public void recalculateLevels(Long lexemeId, List<WordLexeme> lexemes, String action) {

		WordLexeme lexemeToMove = lexemes.stream().filter(l -> l.getLexemeId().equals(lexemeId)).findFirst().get();
		int lexemePos = lexemes.indexOf(lexemeToMove);
		int levelToChange = getLevelToChange(lexemes, lexemeToMove);
		switch (action) {
		case "up":
			if (lexemePos != 0) {
				WordLexeme targetLexeme = lexemes.get(lexemePos - 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "down":
			if (lexemePos != lexemes.size() - 1) {
				WordLexeme targetLexeme = lexemes.get(lexemePos + 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "pop":
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
		case "push":
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
						.filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1()) &&
								l.getLevel2().equals(lexemeToMove.getLevel2()) &&
								l.getLevel3().equals(currentLexLevel))
						.forEach(l -> l.setLevel3(999));
				lexemes.stream().filter(l -> l.getLevel1().equals(targetLexeme.getLevel1()) &&
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
		case 1:
			return lex.getLevel1();
		case 2:
			return lex.getLevel2();
		case 3:
			return lex.getLevel3();
		}
		return 0;
	}
}
