package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.LanguageFilterElement;
import eki.wordweb.service.db.CommonDataDbService;
import eki.wordweb.service.util.ClassifierUtil;

@Component
public class CommonDataService implements SystemConstant {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private MessageSource messageSource;

	@Transactional
	public Map<String, LanguageData> getLangDataMap() {
		return commonDataDbService.getLangDataMap();
	}

	@Transactional
	public List<LanguageFilterElement> getLangFilter(String displayLang) {
		Locale locale = LocaleContextHolder.getLocale();
		String allLangsLabel = messageSource.getMessage("label.search.lang.all", new Object[0], locale);
		String otherLangsLabel = messageSource.getMessage("label.search.lang.other", new Object[0], locale);
		List<String> langCodes = Arrays.asList(DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_RUS);
		List<LanguageFilterElement> langFilter = new ArrayList<>();
		langFilter.add(new LanguageFilterElement(DESTIN_LANG_ALL, allLangsLabel, true));
		List<Classifier> classifiers = classifierUtil.getClassifiers(ClassifierName.LANGUAGE, langCodes, displayLang);
		classifiers.forEach(classifier -> {
			langFilter.add(new LanguageFilterElement(classifier.getCode(), classifier.getValue(), false));
		});
		langFilter.add(new LanguageFilterElement(DESTIN_LANG_OTHER, otherLangsLabel, false));
		return langFilter;
	}
}
