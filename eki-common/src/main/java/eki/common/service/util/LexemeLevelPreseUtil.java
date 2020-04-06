package eki.common.service.util;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.data.LexemeLevel;

@Component
public class LexemeLevelPreseUtil {

	public void combineLevels(List<? extends LexemeLevel> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		String previousLexemeDatasetCode = "";
		Integer previousLevel1 = null;
		Integer previousLevel2 = null;
		Integer currentLevel1ValuePrese = null;
		Integer currentLevel2ValuePrese = null;

		for (LexemeLevel lexeme : lexemes) {

			String levels;
			String lexemeDatasetCode = lexeme.getDatasetCode();
			int lexemeLevel1 = lexeme.getLevel1();
			int lexemeLevel2 = lexeme.getLevel2();
			boolean isLevel1Changed = !Objects.equals(previousLevel1, lexemeLevel1);
			boolean isLevel2Changed = !Objects.equals(previousLevel2, lexemeLevel2);
			boolean isLexemeDatasetChanged = !StringUtils.equals(lexemeDatasetCode, previousLexemeDatasetCode);
			boolean level2exists = lexemes.stream()
					.filter(otherLexeme -> otherLexeme.getLevel1().equals(lexemeLevel1) && StringUtils.equals(otherLexeme.getDatasetCode(), lexemeDatasetCode))
					.count() > 1;

			if (isLexemeDatasetChanged) {
				isLevel1Changed = true;
				isLevel2Changed = true;
				currentLevel1ValuePrese = 0;
				currentLevel2ValuePrese = 0;
				previousLexemeDatasetCode = lexemeDatasetCode;
			}

			if (isLevel1Changed) {
				currentLevel1ValuePrese++;
				currentLevel2ValuePrese = 0;
				previousLevel1 = lexemeLevel1;
				previousLevel2 = lexemeLevel2;
			}

			if (level2exists) {
				if (!isLevel1Changed && isLevel2Changed) {
					currentLevel2ValuePrese++;
					previousLevel2 = lexemeLevel2;
				}

				if (currentLevel2ValuePrese == 0) {
					levels = String.valueOf(currentLevel1ValuePrese);
				} else {
					levels = currentLevel1ValuePrese + "." + currentLevel2ValuePrese;
				}
			} else {
				levels = String.valueOf(currentLevel1ValuePrese);
			}
			lexeme.setLevels(levels);
		}
	}
}
