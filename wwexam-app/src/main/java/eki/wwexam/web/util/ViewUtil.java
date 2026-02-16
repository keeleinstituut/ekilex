package eki.wwexam.web.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.wwexam.constant.SystemConstant;
import eki.wwexam.constant.WebConstant;

@Component
public class ViewUtil implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	private WebUtil webUtil;

	public String getSearchUri(String wordValue, Integer homonymNr) {
		String uri = webUtil.composeSearchUri(wordValue, homonymNr);
		return uri;
	}

	public boolean enumEquals(Enum<?> enum1, Enum<?> enum2) {
		if (enum1 == null) {
			return false;
		}
		if (enum2 == null) {
			return false;
		}
		return StringUtils.equals(enum1.name(), enum2.name());
	}

}
