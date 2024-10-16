package eki.wordweb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.DatasetType;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.UiFilterElement;
import eki.wordweb.service.db.CommonDataDbService;
import eki.wordweb.service.util.ClassifierUtil;
import eki.wordweb.service.util.LanguageContext;

@Component
public class CommonDataService implements SystemConstant {

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ClassifierUtil classifierUtil;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LanguageContext languageContext;

	@Transactional
	public Map<String, LanguageData> getLangDataMap() {
		String displayLang = languageContext.getDisplayLang();
		return commonDataDbService.getLangDataMap(displayLang);
	}

	@Transactional
	public List<UiFilterElement> getUnifLangFilter() {
		Locale locale = languageContext.getDisplayLocale();
		String displayLang = languageContext.getDisplayLang();
		String allLangsLabel = messageSource.getMessage("label.search.lang.all", new Object[0], locale);
		String otherLangsLabel = messageSource.getMessage("label.search.lang.other", new Object[0], locale);
		List<String> langCodes = Arrays.asList(DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_RUS, DESTIN_LANG_FRA, DESTIN_LANG_UKR);
		List<UiFilterElement> langFilter = new ArrayList<>();
		langFilter.add(new UiFilterElement(DESTIN_LANG_ALL, allLangsLabel, true));
		List<Classifier> classifiers = classifierUtil.getClassifiers(ClassifierName.LANGUAGE, langCodes, displayLang);
		classifiers.forEach(classifier -> {
			langFilter.add(new UiFilterElement(classifier.getCode(), classifier.getValue(), false));
		});
		langFilter.add(new UiFilterElement(DESTIN_LANG_OTHER, otherLangsLabel, false));
		return langFilter;
	}

	@Transactional
	public List<UiFilterElement> getSimpleLangFilter() {
		Locale locale = languageContext.getDisplayLocale();
		String displayLang = languageContext.getDisplayLang();
		String allLangsLabel = messageSource.getMessage("label.search.lang.all", new Object[0], locale);
		List<String> langCodes = Arrays.asList(DESTIN_LANG_EST, DESTIN_LANG_ENG, DESTIN_LANG_RUS, DESTIN_LANG_FRA, DESTIN_LANG_UKR);
		List<UiFilterElement> langFilter = new ArrayList<>();
		langFilter.add(new UiFilterElement(DESTIN_LANG_ALL, allLangsLabel, true));
		List<Classifier> classifiers = classifierUtil.getClassifiers(ClassifierName.LANGUAGE, langCodes, displayLang);
		classifiers.forEach(classifier -> {
			langFilter.add(new UiFilterElement(classifier.getCode(), classifier.getValue(), false));
		});
		return langFilter;
	}

	@Transactional
	public List<UiFilterElement> getDatasetFilter() {
		Locale locale = languageContext.getDisplayLocale();
		String allDatasetsLabel = messageSource.getMessage("label.search.dataset.all", new Object[0], locale);
		List<UiFilterElement> datasetFilter = new ArrayList<>();
		datasetFilter.add(new UiFilterElement(DATASET_ALL, allDatasetsLabel, true));
		List<Dataset> datasets = commonDataDbService.getDatasets();
		datasets.forEach(dataset -> {
			datasetFilter.add(new UiFilterElement(dataset.getCode(), dataset.getName(), false));
		});
		return datasetFilter;
	}

	@Transactional
	public List<String> getSupportedDatasetCodes() {
		List<String> supportedDatasetCodes = new ArrayList<>();
		List<String> datasetCodes = commonDataDbService.getDatasetCodes();
		supportedDatasetCodes.add(DATASET_ALL);
		supportedDatasetCodes.addAll(datasetCodes);
		return supportedDatasetCodes;
	}

	@Transactional
	public List<Dataset> getTermDatasets() {
		List<Dataset> allDatasets = commonDataDbService.getDatasets();
		List<Dataset> termDatasets = allDatasets.stream()
				.filter(dataset -> DatasetType.TERM.equals(dataset.getType()))
				.collect(Collectors.toList());
		return termDatasets;
	}

	@Transactional
	public NewsArticle getLatestWordwebNewsArticle() {
		String displayLang = languageContext.getDisplayLang();
		NewsArticle newsArticle = commonDataDbService.getLatestWordwebNewsArticle(displayLang);
		String content = newsArticle.getContent();
		String contentCut = StringUtils.substringBefore(content, '.');
		newsArticle.setContentCut(contentCut);
		return newsArticle;
	}

	@Transactional
	public List<NewsArticle> getWordwebNewsArticles() {
		String displayLang = languageContext.getDisplayLang();
		return commonDataDbService.getWordwebNewsArticles(displayLang);
	}
}
