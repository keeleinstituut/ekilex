package org.apache.lucene.morphology.russian;

public final class RussianWordProcessing {

	// accepts only А-Я, а-ю, -
	public static String stripIllegalLetters(String s) {
		StringBuilder builder = new StringBuilder();
		char[] chars = s.toCharArray();
		int chInt;
		for (char ch : chars) {
			chInt = (int) ch;
			if (chInt == 45) {
				builder.append(ch);
			} else if (chInt >= 1040 && chInt <= 1102) {
				builder.append(ch);
			}
		}
		return builder.toString();
	}
}
