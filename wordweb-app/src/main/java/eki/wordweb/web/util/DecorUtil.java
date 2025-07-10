package eki.wordweb.web.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.common.constant.TextDecoration;
import eki.wordweb.data.DecoratedWordType;

@Component
public class DecorUtil {

	public String getWordValueMarkup(DecoratedWordType word) {

		String wordValuePrese = new String(word.getValuePrese());
		if (word.isSuffixoid()) {
			wordValuePrese = "-" + wordValuePrese;
		} else if (word.isPrefixoid()) {
			wordValuePrese = wordValuePrese + "-";
		}
		StringBuilder htmlBuf = new StringBuilder();
		htmlBuf.append("<span>");
		String foreignMarkupCode = TextDecoration.FOREIGN.getCode();
		if (word.isForeignWord() && !StringUtils.contains(wordValuePrese, foreignMarkupCode)) {
			htmlBuf.append('<');
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
			htmlBuf.append(wordValuePrese);
			htmlBuf.append("</");
			htmlBuf.append(foreignMarkupCode);
			htmlBuf.append('>');
		} else {
			htmlBuf.append(wordValuePrese);
		}
		htmlBuf.append("</span>");
		return htmlBuf.toString();
	}
}
