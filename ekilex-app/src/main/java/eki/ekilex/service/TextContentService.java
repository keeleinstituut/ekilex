package eki.ekilex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.TextContent;
import eki.ekilex.service.db.TextContentDbService;

@Component
public class TextContentService implements GlobalConstant {

	@Autowired
	private TextContentDbService textContentDbService;

	@Transactional
	public String getText(String textName, String lang) {

		TextContent textContent = textContentDbService.getTextContent(textName, lang);
		if (textContent != null) {
			return textContent.getValue();
		}

		textContent = textContentDbService.getTextContent(textName, LANGUAGE_CODE_EST);
		if (textContent != null) {
			return textContent.getValue();
		}

		return null;
	}
}
