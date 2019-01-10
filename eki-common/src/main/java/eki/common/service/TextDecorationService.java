package eki.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.common.data.TextDecorationDescriptor;

@Component
public class TextDecorationService implements InitializingBean, TextDecoration {

	private List<TextDecorationDescriptor> ekiMarkupDescriptors;

	private Pattern ekiEntityPatternV;

	@Override
	public void afterPropertiesSet() throws Exception {

		ekiMarkupDescriptors = new ArrayList<>();

		Pattern entityMatchPattern;
		String preDecoration, postDecoration;
		TextDecorationDescriptor textDecorationDescriptor;

		entityMatchPattern = Pattern.compile("(&ema;(.+?)&eml;)");
		preDecoration = "<" + EKI_ELEMENT_1 + ">";
		postDecoration = "</" + EKI_ELEMENT_1 + ">";
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration);
		ekiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile("(&ba;(.+?)&bl;)");
		preDecoration = "<" + EKI_ELEMENT_2 + ">";
		postDecoration = "</" + EKI_ELEMENT_2 + ">";
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration);
		ekiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile("(&suba;(.+?)&subl;)");
		preDecoration = "<" + EKI_ELEMENT_3 + ">";
		postDecoration = "</" + EKI_ELEMENT_3 + ">";
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration);
		ekiMarkupDescriptors.add(textDecorationDescriptor);

		entityMatchPattern = Pattern.compile("(&supa;(.+?)&supl;)");
		preDecoration = "<" + EKI_ELEMENT_4 + ">";
		postDecoration = "</" + EKI_ELEMENT_4 + ">";
		textDecorationDescriptor = new TextDecorationDescriptor(entityMatchPattern, preDecoration, postDecoration);
		ekiMarkupDescriptors.add(textDecorationDescriptor);

		// other descriptors to be added later
		// TODO consider file-based decoration mappings
		// ...

		ekiEntityPatternV = Pattern.compile("(&(ehk|Hrl|hrl|ja|jne|jt|ka|nt|puudub|v|vm|vms|vrd|vt|напр.|и др.|и т. п.|г.);)");
	}

	public String cleanEkiEntityMarkup(String originalText) {
		if (StringUtils.isBlank(originalText)) {
			return originalText;
		}
		Matcher matcher = ekiEntityPatternV.matcher(originalText);
		if (matcher.find()) {
			originalText = matcher.replaceAll(matcher.group(2));
		}
		return RegExUtils.removePattern(originalText, "[&]\\w+[;]");
	}

	public String convertEkiEntityMarkup(String originalText) {

		String convertedText = new String(originalText);
		StringBuffer decorBuf;
		int textLength;
		int textStart;
		int matchStart;
		int matchEnd;
		String cleanFragment;
		String matchFragment;
		Pattern pattern;
		Matcher matcher;
		String preDecoration;
		String postDecoration;

		for (TextDecorationDescriptor textDecorationDescriptor : ekiMarkupDescriptors) {
			decorBuf = new StringBuffer();
			textLength = convertedText.length();
			textStart = 0;
			pattern = textDecorationDescriptor.getEntityMatchPattern();
			preDecoration = textDecorationDescriptor.getPreDecoration();
			postDecoration = textDecorationDescriptor.getPostDecoration();
			matcher = pattern.matcher(convertedText);
			while (matcher.find()) {
				matchStart = matcher.start();
				matchEnd = matcher.end();
				cleanFragment = StringUtils.substring(convertedText, textStart, matchStart);
				decorBuf.append(cleanFragment);
				if (postDecoration == null) {
					decorBuf.append(preDecoration);
				} else {
					matchFragment = matcher.group(2);
					decorBuf.append(preDecoration);
					decorBuf.append(matchFragment);
					decorBuf.append(postDecoration);
				}
				textStart = matchEnd;
			}
			if (textStart < textLength) {
				cleanFragment = StringUtils.substring(convertedText, textStart, textLength);
				decorBuf.append(cleanFragment);
			}
			convertedText = decorBuf.toString();
		}
		return convertedText;
	}
}
