package eki.ekilex.web.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.service.CommonDataService;

@Component
public class ViewUtil {

	@Autowired
	private CommonDataService commonDataService;

	private Map<String, String> languageIso2Map = null;

	public String getLangIso2(String langIso3) {
		if (StringUtils.isBlank(langIso3)) {
			return "-";
		}
		if (languageIso2Map == null) {
			languageIso2Map = commonDataService.getLanguageIso2Map();
		}
		String langIso2 = languageIso2Map.get(langIso3);
		if (StringUtils.isBlank(langIso2)) {
			return "?";
		}
		return langIso2;
	}
}
