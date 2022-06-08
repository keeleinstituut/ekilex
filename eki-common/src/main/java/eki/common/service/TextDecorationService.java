package eki.common.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.common.data.TextDecorationDescriptor;

@Component
public class TextDecorationService implements InitializingBean, TextDecoration {

	private static final String EKI_MARKUP_PATTERN_FOREIGN = "(&ema;(.+?)&eml;)";

	private static final String EKI_MARKUP_PATTERN_HIGHLIGHT = "(&ba;(.+?)&bl;)";

	private static final String EKI_MARKUP_PATTERN_SUB = "(&suba;(.+?)&subl;)";

	private static final String EKI_MARKUP_PATTERN_SUP = "(&supa;(.+?)&supl;)";

	private static final String EKI_MARKUP_PATTERN_META_V = "(&(v);)";

	private static final String EKI_MARKUP_PATTERN_META_ETC = "(&(ehk|Hrl|hrl|ja|jne|jt|ka|nt|puudub|vm|vms|vrd|vt|напр.|и др.|и т. п.|г.);)";

	private static final String EKI_MARKUP_PATTERN_RUSSIAN_STRESS_1 = "[\\\"\\x{201e}][\\x{0400}-\\x{04ff}]";

	private static final String EKI_MARKUP_PATTERN_RUSSIAN_STRESS_2 = "[\\x{0401}\\x{0451}]";

	private static final int REPLACE_MARKUP = 1;

	private static final int SURROUND_CHAR_BY_MARKUP = 2;

	private static final char[] IGNORED_DIACRITIC_CHARS = new char[] {'õ', 'ä', 'ö', 'ü', 'š', 'ž', 'й', 'Õ', 'Ä', 'Ö', 'Ü', 'Š', 'Ž', 'Й'};

	private static final char[] ACCENT_APOSTROPHES = new char[] {'´', '`', '‘', '’'};

	private static final char APOSTROPHE = '\'';

	private List<TextDecorationDescriptor> allEkiMarkupDescriptors;

	private List<TextDecorationDescriptor> uniLangEkiMarkupDescriptors;

	private Map<Character, String> symbolSimplificationMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		allEkiMarkupDescriptors = new ArrayList<>();
		uniLangEkiMarkupDescriptors = new ArrayList<>();

		Pattern entityMatchPattern;
		String preDecoration, postDecoration;
		TextDecorationDescriptor textDecorationDescriptor;

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_FOREIGN);
		preDecoration = asXmlElemStart(FOREIGN);
		postDecoration = asXmlElemEnd(FOREIGN);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_HIGHLIGHT);
		preDecoration = asXmlElemStart(HIGHLIGHT);
		postDecoration = asXmlElemEnd(HIGHLIGHT);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_SUB);
		preDecoration = asXmlElemStart(SUB);
		postDecoration = asXmlElemEnd(SUB);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_SUP);
		preDecoration = asXmlElemStart(SUP);
		postDecoration = asXmlElemEnd(SUP);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_META_V);
		preDecoration = asXmlElemStart(META) + "~" + asXmlElemEnd(META);
		postDecoration = null;
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_META_ETC);
		preDecoration = asXmlElemStart(META);
		postDecoration = asXmlElemEnd(META);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, REPLACE_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);
		uniLangEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_RUSSIAN_STRESS_1);
		preDecoration = asXmlElemStart(STRESS);
		postDecoration = asXmlElemEnd(STRESS);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, SURROUND_CHAR_BY_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile(EKI_MARKUP_PATTERN_RUSSIAN_STRESS_2);
		preDecoration = asXmlElemStart(STRESS);
		postDecoration = asXmlElemEnd(STRESS);
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration, SURROUND_CHAR_BY_MARKUP);
		allEkiMarkupDescriptors.add(textDecorationDescriptor);

		symbolSimplificationMap = new HashMap<>();
		symbolSimplificationMap.put('ı', Character.toString('i'));
		symbolSimplificationMap.put('ß', "ss");
		symbolSimplificationMap.put('Ə', Character.toString('Ä'));
		symbolSimplificationMap.put('ə', Character.toString('ä'));
		symbolSimplificationMap.put('Æ', "AE");
		symbolSimplificationMap.put('æ', "ae");
		symbolSimplificationMap.put('Œ', "OE");
		symbolSimplificationMap.put('œ', "oe");
		symbolSimplificationMap.put('Þ', "TH");
		symbolSimplificationMap.put('þ', "th");
		symbolSimplificationMap.put('Ð', "DH");
		symbolSimplificationMap.put('ð', "dh");
		symbolSimplificationMap.put('Ł', Character.toString('L'));
		symbolSimplificationMap.put('ł', Character.toString('l'));
		symbolSimplificationMap.put('ŋ', Character.toString('n'));
		symbolSimplificationMap.put('Ø', Character.toString('O'));
		symbolSimplificationMap.put('ø', Character.toString('o'));
	}

	public String removeEkiElementMarkup(String originalText) {
		if (StringUtils.isBlank(originalText)) {
			return originalText;
		}
		String cleanText = RegExUtils.removePattern(originalText, "<[/]?eki-[^>]*>|<[/]?ext-link[^>]*>");
		return cleanText;
	}

	public String removeHtmlAndSkipEkiElementMarkup(String originalText) {
		if (StringUtils.isBlank(originalText)) {
			return originalText;
		}
		String cleanText = RegExUtils.removePattern(originalText, "(?!<[/]?eki-[^>]*>)(?!<[/]?ext-link[^>]*>)<[^>]*>");
		return cleanText;
	}

	public String removeEkiEntityMarkup(String originalText) {
		return removeEkiEntityMarkup(originalText, false);
	}

	public String removeEkiEntityMarkupSkipStress(String originalText) {
		return removeEkiEntityMarkup(originalText, true);
	}

	private String removeEkiEntityMarkup(String originalText, boolean skipStress) {
		if (StringUtils.isBlank(originalText)) {
			return originalText;
		}
		String convertedText = new String(originalText);
		List<TextDecorationDescriptor> ekiMarkupDescriptors;
		if (skipStress) {
			ekiMarkupDescriptors = new ArrayList<>(uniLangEkiMarkupDescriptors);
		} else {
			ekiMarkupDescriptors = new ArrayList<>(allEkiMarkupDescriptors);
		}
		Pattern pattern;
		int applyMethod;
		for (TextDecorationDescriptor textDecorationDescriptor : ekiMarkupDescriptors) {
			pattern = textDecorationDescriptor.getEntityMatchPattern();
			applyMethod = textDecorationDescriptor.getApplyMethod();
			if (REPLACE_MARKUP == applyMethod) {
				convertedText = cleanByReplacingPattern(pattern, convertedText);
			} else if (SURROUND_CHAR_BY_MARKUP == applyMethod) {
				convertedText = cleanBySurroundingCharPattern(pattern, convertedText);
			}
		}
		return convertedText;
	}

	public String convertEkiEntityMarkup(String originalText) {

		if (StringUtils.isEmpty(originalText)) {
			return originalText;
		}

		String convertedText = new String(originalText);
		Pattern pattern;
		String preDecoration;
		String postDecoration;
		int applyMethod;

		for (TextDecorationDescriptor textDecorationDescriptor : allEkiMarkupDescriptors) {
			pattern = textDecorationDescriptor.getEntityMatchPattern();
			preDecoration = textDecorationDescriptor.getPreDecoration();
			postDecoration = textDecorationDescriptor.getPostDecoration();
			applyMethod = textDecorationDescriptor.getApplyMethod();
			if (REPLACE_MARKUP == applyMethod) {
				convertedText = replaceByPattern(pattern, convertedText, preDecoration, postDecoration);
			} else if (SURROUND_CHAR_BY_MARKUP == applyMethod) {
				convertedText = surroundCharByPattern(pattern, convertedText, preDecoration, postDecoration);
			}
		}
		return convertedText;
	}

	public String composeLinkMarkup(String linkType, String linkId, String linkValue) {
		StringBuffer markupBuf = new StringBuffer();
		markupBuf.append("<");
		markupBuf.append(LINK.getCode());
		markupBuf.append(" link-type='");
		markupBuf.append(linkType);
		markupBuf.append("'");
		markupBuf.append(" link-id='");
		markupBuf.append(linkId);
		markupBuf.append("'");
		markupBuf.append(">");
		markupBuf.append(linkValue);
		markupBuf.append(asXmlElemEnd(LINK));
		return markupBuf.toString();
	}

	public String applyPattern(Pattern pattern, String text, CodeValue codeValue) {
		String preDecoration = asXmlElemStart(codeValue);
		String postDecoration = asXmlElemEnd(codeValue);
		return replaceByPattern(pattern, text, preDecoration, postDecoration);
	}

	public boolean isDecorated(String text) {
		Pattern pattern = Pattern.compile("<eki-[^>]*>.*?</eki-[^>]*>");
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	public String unifyToApostrophe(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		StringBuffer cleanValueBuf = new StringBuffer();
		char[] chars = value.toCharArray();
		for (char c : chars) {
			if (ArrayUtils.contains(ACCENT_APOSTROPHES, c)) {
				cleanValueBuf.append(APOSTROPHE);
			} else {
				cleanValueBuf.append(c);
			}
		}
		return cleanValueBuf.toString();
	}

	public String removeAccents(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		boolean needsSimplification = StringUtils.containsAny(value, StringUtils.join(symbolSimplificationMap.keySet()));
		boolean isAlreadyNormalized = Normalizer.isNormalized(value, Normalizer.Form.NFD);
		if (isAlreadyNormalized && !needsSimplification) {
			return null;
		}
		StringBuffer cleanValueBuf = new StringBuffer();
		char[] chars = value.toCharArray();
		String decomposedChars;
		String charAsStr;
		String simpleStr;
		char primaryChar;
		for (char c : chars) {
			boolean isIgnoredChar = ArrayUtils.contains(IGNORED_DIACRITIC_CHARS, c);
			if (isIgnoredChar) {
				cleanValueBuf.append(c);
			} else {
				charAsStr = Character.toString(c);
				decomposedChars = Normalizer.normalize(charAsStr, Normalizer.Form.NFD);
				if (decomposedChars.length() > 1) {
					primaryChar = decomposedChars.charAt(0);
					cleanValueBuf.append(primaryChar);
				} else {
					simpleStr = symbolSimplificationMap.get(c);
					if (simpleStr == null) {
						cleanValueBuf.append(c);						
					} else {
						cleanValueBuf.append(simpleStr);
					}
				}
			}
		}
		String cleanValue = cleanValueBuf.toString();
		if (StringUtils.equals(value, cleanValue)) {
			return null;
		}
		return cleanValue;
	}

	private String replaceByPattern(Pattern pattern, String text, String preDecoration, String postDecoration) {
		return replaceByPattern(pattern, text, preDecoration, postDecoration, null);
	}

	private String replaceByPattern(Pattern pattern, String text, String preDecoration, String postDecoration, String fixedMatchReplacement) {

		StringBuffer decorBuf = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		int textLength = text.length();
		int textStart = 0;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;
		while (matcher.find()) {
			matchStart = matcher.start();
			matchEnd = matcher.end();
			cleanFragment = StringUtils.substring(text, textStart, matchStart);
			decorBuf.append(cleanFragment);
			if (fixedMatchReplacement == null) {
				if (matcher.groupCount() > 1) {
					matchFragment = matcher.group(matcher.groupCount());
				} else {
					matchFragment = null;
				}
			} else {
				matchFragment = fixedMatchReplacement;
			}
			if ((preDecoration == null) && (postDecoration == null)) {
				decorBuf.append(matchFragment);
			} else if ((preDecoration != null) && (postDecoration == null)) {
				decorBuf.append(preDecoration);
			} else {
				decorBuf.append(preDecoration);
				decorBuf.append(matchFragment);
				decorBuf.append(postDecoration);
			}
			textStart = matchEnd;
		}
		if (textStart < textLength) {
			cleanFragment = StringUtils.substring(text, textStart, textLength);
			decorBuf.append(cleanFragment);
		}
		text = decorBuf.toString();
		return text;
	}

	private String cleanByReplacingPattern(Pattern pattern, String text) {

		StringBuffer decorBuf = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		int textLength = text.length();
		int textStart = 0;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;
		while (matcher.find()) {
			matchStart = matcher.start();
			matchEnd = matcher.end();
			cleanFragment = StringUtils.substring(text, textStart, matchStart);
			matchFragment = matcher.group(matcher.groupCount());
			decorBuf.append(cleanFragment);
			decorBuf.append(matchFragment);
			textStart = matchEnd;
		}
		if (textStart < textLength) {
			cleanFragment = StringUtils.substring(text, textStart, textLength);
			decorBuf.append(cleanFragment);
		}
		text = decorBuf.toString();
		return text;
	}

	private String surroundCharByPattern(Pattern pattern, String text, String preDecoration, String postDecoration) {

		StringBuffer decorBuf = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		int textLength = text.length();
		int textStart = 0;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;
		char[] matchFragmentChars;
		while (matcher.find()) {
			matchStart = matcher.start();
			matchEnd = matcher.end();
			cleanFragment = StringUtils.substring(text, textStart, matchStart);
			matchFragment = matcher.group(0);
			matchFragmentChars = matchFragment.toCharArray();
			char matchFragmentChar = matchFragmentChars[matchFragmentChars.length - 1];
			decorBuf.append(cleanFragment);
			decorBuf.append(preDecoration);
			decorBuf.append(matchFragmentChar);
			decorBuf.append(postDecoration);
			textStart = matchEnd;
		}
		if (textStart < textLength) {
			cleanFragment = StringUtils.substring(text, textStart, textLength);
			decorBuf.append(cleanFragment);
		}
		text = decorBuf.toString();
		return text;
	}

	private String cleanBySurroundingCharPattern(Pattern pattern, String text) {

		StringBuffer decorBuf = new StringBuffer();
		Matcher matcher = pattern.matcher(text);
		int textLength = text.length();
		int textStart = 0;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;
		char[] matchFragmentChars;
		while (matcher.find()) {
			matchStart = matcher.start();
			matchEnd = matcher.end();
			cleanFragment = StringUtils.substring(text, textStart, matchStart);
			matchFragment = matcher.group(0);
			matchFragmentChars = matchFragment.toCharArray();
			char matchFragmentChar = matchFragmentChars[matchFragmentChars.length - 1];
			decorBuf.append(cleanFragment);
			decorBuf.append(matchFragmentChar);
			textStart = matchEnd;
		}
		if (textStart < textLength) {
			cleanFragment = StringUtils.substring(text, textStart, textLength);
			decorBuf.append(cleanFragment);
		}
		text = decorBuf.toString();
		return text;
	}

	private String asXmlElemStart(CodeValue codeValue) {
		return "<" + codeValue.getCode() + ">";
	}

	private String asXmlElemEnd(CodeValue codeValue) {
		return "</" + codeValue.getCode() + ">";
	}
}
