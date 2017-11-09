package org.apache.lucene.morphology.russian;

public final class RussianWordProcessing {

	private static final int RUSSIAN_CHAR_CODE_MIN = 1040;

	private static final int RUSSIAN_CHAR_CODE_MAX = 1103;

	private static final int RUSSIAN_CHAR_CODE_Ё = 1025;

	private static final int RUSSIAN_CHAR_CODE_ё = 1105;

	// accepts only А-Я, а-я, Ё, ё, -
	public static String stripIllegalLetters(String s) {
		StringBuilder builder = new StringBuilder();
		char[] chars = s.toCharArray();
		int chInt;
		for (char ch : chars) {
			chInt = (int) ch;
			if (chInt == 45) {
				builder.append(ch);
			} else if (chInt >= RUSSIAN_CHAR_CODE_MIN
					&& chInt <= RUSSIAN_CHAR_CODE_MAX) {
				builder.append(ch);
			} else if (chInt == RUSSIAN_CHAR_CODE_Ё) {
				builder.append(ch);
			} else if (chInt == RUSSIAN_CHAR_CODE_ё) {
				builder.append(ch);
			}
		}
		return builder.toString();
	}
}
