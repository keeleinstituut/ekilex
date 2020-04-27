package eki.ekilex.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eki.common.constant.ClassifierName;
import eki.common.data.AppData;
import eki.common.web.AppDataHolder;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Origin;
import eki.ekilex.data.Source;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.data.imp.Paradigm;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.FileService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.MorphologyService;
import eki.ekilex.service.SourceService;
import eki.ekilex.service.TermSearchService;

@ConditionalOnWebApplication
@RestController
public class DataController implements SystemConstant, WebConstant {

	@Autowired
	private AppDataHolder appDataHolder;

	@Autowired
	private FileService fileService;

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private MorphologyService morphologyService;

	@Autowired
	private SourceService sourceService;

	@GetMapping(REST_SERVICES_URI + "/app")
	public AppData getAppData() {
		return appDataHolder.getAppData();
	}

	@GetMapping("/files/images/{fileName:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {

		String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
		Resource resource = fileService.getFileAsResource(fileNameWithoutExtension);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@GetMapping({
			REST_SERVICES_URI + LEX_SEARCH_URI + "/{word}",
			REST_SERVICES_URI + LEX_SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public WordsResult lexSearch(@PathVariable("word") String word, @PathVariable(value = "datasets", required = false) String datasetsStr) {

		boolean fetchAll = true;
		List<String> datasets = parseDatasets(datasetsStr);
		WordsResult results = lexSearchService.getWords(word, datasets, fetchAll, DEFAULT_OFFSET);
		return results;
	}

	@GetMapping(REST_SERVICES_URI + SOURCE_SEARCH_URI + "/{searchFilter}")
	@ResponseBody
	public List<Source> sourceSearch(@PathVariable("searchFilter") String searchFilter) {

		List<Source> sources = sourceService.getSources(searchFilter);
		return sources;
	}

	@GetMapping({
			REST_SERVICES_URI + WORD_DETAILS_URI + "/{wordId}",
			REST_SERVICES_URI + WORD_DETAILS_URI + "/{wordId}/{datasets}"
	})
	@ResponseBody
	public WordDetails getWordDetails(@PathVariable("wordId") String wordIdStr, @PathVariable(value = "datasets", required = false) String datasetsStr) {

		if (!StringUtils.isNumeric(wordIdStr)) {
			return null;
		}
		Long wordId = Long.valueOf(wordIdStr);
		List<String> datasets = parseDatasets(datasetsStr);
		WordDetails result = lexSearchService.getWordDetails(wordId, datasets, null, null);
		return result;
	}

	@GetMapping(REST_SERVICES_URI + PARADIGMS_URI + "/{wordId}")
	public List<Paradigm> getParadigms(@PathVariable("wordId") String wordIdStr) {

		if (!StringUtils.isNumeric(wordIdStr)) {
			return null;
		}
		Long wordId = Long.valueOf(wordIdStr);
		return morphologyService.getParadigms(wordId);
	}

	@GetMapping({
			REST_SERVICES_URI + TERM_SEARCH_URI + "/{word}",
			REST_SERVICES_URI + TERM_SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public TermSearchResult termSearch(@PathVariable("word") String word, @PathVariable(value = "datasets", required = false) String datasetsStr) {

		boolean fetchAll = true;
		List<String> datasets = parseDatasets(datasetsStr);
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		TermSearchResult results = termSearchService.getTermSearchResult(word, datasets, resultMode, resultLang, fetchAll, DEFAULT_OFFSET);
		return results;
	}

	@GetMapping({
			REST_SERVICES_URI + MEANING_DETAILS_URI + "/{meaningId}",
			REST_SERVICES_URI + MEANING_DETAILS_URI + "/{meaningId}/{datasets}"
	})
	@ResponseBody
	public Meaning getMeaningDetails(@PathVariable("meaningId") String meaningIdStr, @PathVariable(value = "datasets", required = false) String datasetsStr) {

		if (!StringUtils.isNumeric(meaningIdStr)) {
			return null;
		}
		Long meaningId = Long.valueOf(meaningIdStr);
		List<String> datasets = parseDatasets(datasetsStr);
		List<Classifier> allLanguages = commonDataService.getLanguages();
		List<ClassifierSelect> languagesOrder = convert(allLanguages);
		Meaning meaning = termSearchService.getMeaning(meaningId, datasets, languagesOrder, null);
		return meaning;
	}

	@GetMapping(REST_SERVICES_URI + CLASSIFIERS_URI + "/{classifierName}")
	public List<Classifier> getClassifiers(@PathVariable("classifierName") String classifierNameStr) {

		ClassifierName classifierName = null;
		try {
			classifierNameStr = classifierNameStr.toUpperCase();
			classifierName = ClassifierName.valueOf(classifierNameStr);
		} catch (Exception e) {
			return null;
		}
		return commonDataService.getClassifiers(classifierName);
	}

	@GetMapping(REST_SERVICES_URI + DOMAIN_ORIGINS_URI)
	public List<Origin> getDomainOrigins() {
		return commonDataService.getDomainOrigins();
	}

	@GetMapping(REST_SERVICES_URI + DOMAINS_URI + "/{origin}")
	public List<Classifier> getDomains(@PathVariable("origin") String origin) {
		return commonDataService.getDomains(origin);
	}

	@GetMapping(REST_SERVICES_URI + DATASETS_URI)
	public List<Dataset> getDatasets() {
		return commonDataService.getDatasets();
	}

	private List<ClassifierSelect> convert(List<Classifier> allLanguages) {
		List<ClassifierSelect> languagesOrder = allLanguages.stream().map(language -> {
			ClassifierSelect languageSelect = new ClassifierSelect();
			languageSelect.setCode(language.getCode());
			languageSelect.setValue(language.getValue());
			languageSelect.setSelected(true);
			return languageSelect;
		}).collect(Collectors.toList());
		return languagesOrder;
	}


	private List<String> parseDatasets(String datasetsStr) {
		List<String> datasets;
		if (StringUtils.isNotBlank(datasetsStr)) {
			String[] datasetsArr = StringUtils.split(datasetsStr, ',');
			datasets = Arrays.asList(datasetsArr);
		} else {
			datasets = commonDataService.getDatasetCodes();
		}
		return datasets;
	}
}
