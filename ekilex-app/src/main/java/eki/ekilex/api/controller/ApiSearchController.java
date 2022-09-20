package eki.ekilex.api.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import eki.common.constant.ClassifierName;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Origin;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordsResult;
import eki.ekilex.data.api.ApiEndpointDescription;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;

@ConditionalOnWebApplication
@RestController
public class ApiSearchController extends AbstractApiController {

	@Autowired
	private CommonDataService commonDataService;

	@Autowired
	private LexSearchService lexSearchService;

	@Autowired
	private TermSearchService termSearchService;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Order(101)
	@GetMapping(API_SERVICES_URI + ENDPOINTS_URI)
	@ResponseBody
	public List<ApiEndpointDescription> listEndpoints() {

		Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
		List<ApiEndpointDescription> apiEndpointDescriptions = handlerMethods.entrySet().stream()
				.map(entry -> {
					RequestMappingInfo mappingInfo = entry.getKey();
					HandlerMethod handlerMethod = entry.getValue();
					Method serviceMethod = handlerMethod.getMethod();
					String requestMethod = mappingInfo.getMethodsCondition().getMethods().stream()
							.map(RequestMethod::name)
							.findFirst().orElse(null);
					PatternsRequestCondition patternsCondition = mappingInfo.getPatternsCondition();
					Set<String> allPatterns = patternsCondition.getPatterns();
					List<String> apiPatterns = allPatterns.stream()
							.filter(pattern -> StringUtils.startsWith(pattern, API_SERVICES_URI))
							.collect(Collectors.toList());
					if (CollectionUtils.isEmpty(apiPatterns)) {
						return null;
					}
					Order orderAnnot = serviceMethod.getDeclaredAnnotation(Order.class);
					if (orderAnnot == null) {
						return null;
					}
					int serviceOrder = orderAnnot.value();
					List<String> pathVariables = new ArrayList<>();
					List<String> requestParameters = new ArrayList<>();
					String requestBody = null;
					Parameter[] methodParams = serviceMethod.getParameters();
					for (int paramIndex = 0; paramIndex < methodParams.length; paramIndex++) {
						Parameter methodParam = methodParams[paramIndex];
						Annotation[] declaredAnnotations = methodParam.getDeclaredAnnotations();
						Type[] genericParamTypes = methodParam.getDeclaringExecutable().getGenericParameterTypes();
						PathVariable pathVar = Arrays.stream(declaredAnnotations)
								.filter(paramAnnot -> paramAnnot instanceof PathVariable)
								.map(paramAnnot -> (PathVariable) paramAnnot)
								.findFirst().orElse(null);
						RequestParam reqParam = Arrays.stream(declaredAnnotations)
								.filter(paramAnnot -> paramAnnot instanceof RequestParam)
								.map(paramAnnot -> (RequestParam) paramAnnot)
								.findFirst().orElse(null);
						RequestBody reqBody = Arrays.stream(declaredAnnotations)
								.filter(paramAnnot -> paramAnnot instanceof RequestBody)
								.map(paramAnnot -> (RequestBody) paramAnnot)
								.findFirst().orElse(null);
						if (pathVar != null) {
							String paramName = "?";
							if (StringUtils.isNotBlank(pathVar.name())) {
								paramName = pathVar.name();
							} else if (StringUtils.isNotBlank(pathVar.value())) {
								paramName = pathVar.value();
							}
							String pathVariable = paramName + "::" + genericParamTypes[paramIndex].getTypeName();
							pathVariables.add(pathVariable);
						}
						if (reqParam != null) {
							String paramName = "?";
							if (StringUtils.isNotBlank(reqParam.name())) {
								paramName = reqParam.name();
							} else if (StringUtils.isNotBlank(reqParam.value())) {
								paramName = reqParam.value();
							}
							String requestParameter = paramName + "::" + genericParamTypes[paramIndex].getTypeName();
							requestParameters.add(requestParameter);
						}
						if (reqBody != null) {
							requestBody = genericParamTypes[paramIndex].getTypeName();
						}
					}
					ApiEndpointDescription apiEndpointDescription = new ApiEndpointDescription();
					apiEndpointDescription.setRequestMethod(requestMethod);
					apiEndpointDescription.setUriPatterns(apiPatterns);
					apiEndpointDescription.setPathVariables(pathVariables);
					apiEndpointDescription.setRequestParameters(requestParameters);
					apiEndpointDescription.setRequestBody(requestBody);
					apiEndpointDescription.setOrder(serviceOrder);
					return apiEndpointDescription;
				})
				.filter(apiEndpointDescription -> apiEndpointDescription != null)
				.sorted(Comparator.comparing(ApiEndpointDescription::getOrder))
				.collect(Collectors.toList());
		return apiEndpointDescriptions;
	}

	@Order(102)
	@GetMapping({
			API_SERVICES_URI + WORD_URI + SEARCH_URI + "/{word}",
			API_SERVICES_URI + WORD_URI + SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public WordsResult lexSearch(
			@PathVariable("word") String word, @PathVariable(value = "datasets", required = false) String datasetsStr) throws Exception {

		boolean noLimit = true;
		List<String> datasets = parseDatasets(datasetsStr);
		WordsResult results = lexSearchService.getWords(word, datasets, null, null, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		return results;
	}

	@Order(103)
	@GetMapping({
			API_SERVICES_URI + WORD_URI + DETAILS_URI + "/{wordId}",
			API_SERVICES_URI + WORD_URI + DETAILS_URI + "/{wordId}/{datasets}"
	})
	@ResponseBody
	public WordDetails getWordDetails(
			@PathVariable("wordId") Long wordId,
			@PathVariable(value = "datasets", required = false) String datasetsStr) throws Exception {

		List<String> datasets = parseDatasets(datasetsStr);
		boolean isFullData = true;
		EkiUser user = userContext.getUser();
		WordDetails result = lexSearchService.getWordDetails(wordId, null, datasets, null, user, null, null, isFullData);
		return result;
	}

	@Order(104)
	@GetMapping({
			API_SERVICES_URI + MEANING_URI + SEARCH_URI + "/{word}",
			API_SERVICES_URI + MEANING_URI + SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public TermSearchResult termSearch(
			@PathVariable("word") String word, @PathVariable(value = "datasets", required = false) String datasetsStr) throws Exception {

		boolean noLimit = true;
		List<String> datasets = parseDatasets(datasetsStr);
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		TermSearchResult results = termSearchService.getTermSearchResult(word, datasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		return results;
	}

	@Order(105)
	@GetMapping({
			API_SERVICES_URI + MEANING_URI + DETAILS_URI + "/{meaningId}",
			API_SERVICES_URI + MEANING_URI + DETAILS_URI + "/{meaningId}/{datasets}"
	})
	@ResponseBody
	public Meaning getMeaningDetails(
			@PathVariable("meaningId") Long meaningId,
			@PathVariable(value = "datasets", required = false) String datasetsStr) throws Exception {

		List<String> datasets = parseDatasets(datasetsStr);
		List<Classifier> allLanguages = commonDataService.getLanguages();
		List<ClassifierSelect> languagesOrder = convert(allLanguages);
		EkiUser user = userContext.getUser();
		Meaning meaning = termSearchService.getMeaning(meaningId, datasets, languagesOrder, null, user, null);
		return meaning;
	}

	@Order(106)
	@GetMapping(API_SERVICES_URI + CLASSIFIERS_URI + "/{classifierName}")
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

	@Order(107)
	@GetMapping(API_SERVICES_URI + DOMAIN_ORIGINS_URI)
	public List<Origin> getDomainOrigins() {
		return commonDataService.getDomainOrigins();
	}

	@Order(108)
	@GetMapping(API_SERVICES_URI + DOMAINS_URI + "/{origin}")
	public List<Classifier> getDomains(@PathVariable("origin") String origin) {
		return commonDataService.getDomains(origin);
	}

	@Order(109)
	@GetMapping(API_SERVICES_URI + DATASETS_URI)
	public List<Dataset> getDatasets() {
		return commonDataService.getAllDatasets();
	}

	private List<ClassifierSelect> convert(List<Classifier> allLanguages) {
		List<ClassifierSelect> languagesOrder = allLanguages.stream()
				.map(language -> {
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
			datasets = commonDataService.getVisibleDatasetCodes();
		}
		return datasets;
	}
}
