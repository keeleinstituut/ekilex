package eki.ekilex.service.util;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import eki.ekilex.data.Lexeme;

@Component
public class LexemeLevelCalcUtil {

	public void recalculateLevels(Long sourceLexemeId, List<Lexeme> lexemes, int targetPosition) {

		Lexeme sourceLexeme = lexemes.stream().filter(l -> l.getLexemeId().equals(sourceLexemeId)).findFirst().get();
		int sourcePosition = lexemes.indexOf(sourceLexeme);
		int levelToChange = getLevelToChange(lexemes, sourceLexeme);
		boolean isSourceLexemeLevelIncrease = targetPosition > sourcePosition;
		boolean isSourceLexemeLevelDecrease = targetPosition < sourcePosition;

		if (isSourceLexemeLevelIncrease) {
			if (levelToChange == 1) {
				List<Lexeme> lexemesToIncrease = getSameLevel1Lexemes(lexemes, sourceLexeme);

				List<Lexeme> lexemesToDecrease = lexemes.subList(sourcePosition, targetPosition);
				lexemesToDecrease = lexemesToDecrease.stream()
						.filter(lexeme -> !lexemesToIncrease.contains(lexeme))
						.collect(Collectors.toList());
				Lexeme lastLevel1LexemeToDecrease = lexemes.get(targetPosition);
				Integer maxLevel1 = lastLevel1LexemeToDecrease.getLevel1();
				List<Lexeme> lastLexemesToDecrease = getSameLevel1Lexemes(lexemes, lastLevel1LexemeToDecrease);
				lexemesToDecrease.addAll(lastLexemesToDecrease);

				lexemesToDecrease.forEach(lexeme -> {
					Integer currentLevel1 = lexeme.getLevel1();
					lexeme.setLevel1(currentLevel1 - 1);
				});
				lexemesToIncrease.forEach(lexeme -> lexeme.setLevel1(maxLevel1));
			} else if (levelToChange == 2) {
				List<Lexeme> lexemesToDecrease = lexemes.subList(sourcePosition + 1, targetPosition + 1);
				Integer maxLevel2 = lexemesToDecrease.get(lexemesToDecrease.size() - 1).getLevel2();

				sourceLexeme.setLevel2(maxLevel2);
				lexemesToDecrease.forEach(lexeme -> {
					Integer currentLevel2 = lexeme.getLevel2();
					lexeme.setLevel2(currentLevel2 - 1);
				});
			}
		} else if (isSourceLexemeLevelDecrease) {
			if (levelToChange == 1) {
				List<Lexeme> lexemesToDecrease = getSameLevel1Lexemes(lexemes, sourceLexeme);
				List<Lexeme> lexemesToIncrease = lexemes.subList(targetPosition, sourcePosition);
				Integer minLevel1 = lexemesToIncrease.get(0).getLevel1();

				lexemesToIncrease.forEach(lexeme -> {
					Integer currentLevel1 = lexeme.getLevel1();
					lexeme.setLevel1(currentLevel1 + 1);
				});
				lexemesToDecrease.forEach(lexeme -> lexeme.setLevel1(minLevel1));
			} else if (levelToChange == 2) {
				List<Lexeme> lexemesToIncrease = lexemes.subList(targetPosition, sourcePosition);
				Integer minLevel2 = lexemesToIncrease.get(0).getLevel2();

				sourceLexeme.setLevel2(minLevel2);
				lexemesToIncrease.forEach(lexeme -> {
					Integer currentLevel2 = lexeme.getLevel2();
					lexeme.setLevel2(currentLevel2 + 1);
				});
			}
		}
	}

	public void recalculateLevels(Long lexemeId, List<Lexeme> lexemes, String action) {

		Optional<Lexeme> lexemeOptional = lexemes.stream().filter(l -> l.getLexemeId().equals(lexemeId)).findFirst();
		if (!lexemeOptional.isPresent()) {
			return;
		}

		Lexeme lexemeToMove = lexemeOptional.get();
		int lexemePos = lexemes.indexOf(lexemeToMove);
		int levelToChange = getLevelToChange(lexemes, lexemeToMove);
		switch (action) {
		case "up":
			if (lexemePos != 0) {
				Lexeme targetLexeme = lexemes.get(lexemePos - 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "down":
			if (lexemePos != lexemes.size() - 1) {
				Lexeme targetLexeme = lexemes.get(lexemePos + 1);
				moveUpDown(lexemes, lexemeToMove, targetLexeme);
			}
			break;
		case "pop":
			if (levelToChange == 2) {
				Integer maxLevel1 = lexemes.stream().map(Lexeme::getLevel1).max(Comparator.comparingInt(Integer::valueOf)).get();
				Integer currentLevel1 = lexemeToMove.getLevel1();
				lexemeToMove.setLevel1(maxLevel1 + 1);
				lexemeToMove.setLevel2(1);
				List<Lexeme> lexemesToCorrect = lexemes.stream().filter(l -> l.getLevel1().equals(currentLevel1)).collect(Collectors.toList());
				Integer oldLevel2 = 999;
				Integer newLevel2 = 0;
				for (Lexeme lexeme : lexemesToCorrect) {
					if (!lexeme.getLevel2().equals(oldLevel2)) {
						newLevel2++;
						oldLevel2 = lexeme.getLevel2();
					}
					lexeme.setLevel2(newLevel2);
				}
			}
			break;
		case "push":
			if (levelToChange == 1 && lexemes.size() > 1) {
				Lexeme targetLexeme = lexemes.get(lexemePos == 0 ? lexemePos + 1 : lexemePos - 1);
				Integer level1 = lexemeToMove.getLevel1();
				Integer maxLevel2 = lexemes.stream().filter(l -> l.getLevel1().equals(targetLexeme.getLevel1())).map(Lexeme::getLevel2)
						.max(Comparator.comparingInt(Integer::valueOf)).get();
				lexemeToMove.setLevel1(targetLexeme.getLevel1());
				lexemeToMove.setLevel2(maxLevel2 + 1);
				lexemes.stream().filter(l -> l.getLevel1() > level1).forEach(l -> l.setLevel1(l.getLevel1() - 1));
			}
			if (levelToChange == 2 && lexemes.size() > 1) {
				List<Lexeme> level2lexemes = lexemes.stream().filter(l -> l.getLevel1().equals(lexemeToMove.getLevel1())).collect(Collectors.toList());
				lexemePos = level2lexemes.indexOf(lexemeToMove);
				Lexeme targetLexeme = lexemes.get(lexemePos == 0 ? lexemePos + 1 : lexemePos - 1);
				Integer level2 = lexemeToMove.getLevel2();
				lexemeToMove.setLevel2(targetLexeme.getLevel2());
				level2lexemes.stream().filter(l -> l.getLevel2() > level2).forEach(l -> l.setLevel2(l.getLevel2() - 1));
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
			break;
		}
	}

	private void moveUpDown(List<Lexeme> lexemes, Lexeme lexemeToMove, Lexeme targetLexeme) {

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
	}

	private int getLevelToChange(List<Lexeme> lexemes, Lexeme lexeme) {

		Optional<Lexeme> minLevelLexeme = lexemes.stream()
				.filter(l -> l.getLevel1().equals(lexeme.getLevel1()))
				.min(Comparator.comparing(Lexeme::getLevel2));

		if (minLevelLexeme.isPresent()) {
			Long minLevelLexemeId = minLevelLexeme.get().getLexemeId();
			Long lexemeId = lexeme.getLexemeId();
			if (Objects.equals(lexemeId, minLevelLexemeId)) {
				return 1;
			}
		}
		return 2;
	}

	private int numberAtLevel(int level, Lexeme lex) {

		if (level == 1) {
			return lex.getLevel1();
		} else {
			return lex.getLevel2();
		}
	}

	private List<Lexeme> getSameLevel1Lexemes(List<Lexeme> lexemes, Lexeme lexeme) {

		return lexemes.stream()
				.filter(l -> l.getLevel1().equals(lexeme.getLevel1()))
				.collect(Collectors.toList());
	}

}
