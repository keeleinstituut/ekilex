package eki.ekilex.api.controller;

import eki.common.constant.ClassifierName;
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.data.*;
import eki.ekilex.data.api.ApiEndpointDescription;
import eki.ekilex.data.api.Word;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LexSearchService;
import eki.ekilex.service.TermSearchService;
import eki.ekilex.service.api.LexWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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
	private LexWordService lexWordService;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Tag(name = "1. System")
	@Operation(
			summary = "List all API endpoints",
			description = "Returns a structured list of all Ekilex API endpoints, including HTTP method, URI patterns, path variables, query parameters and request body type.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successfully retrieved list of API endpoints",
							content = @Content(
									array = @ArraySchema(schema = @Schema(implementation = ApiEndpointDescription.class))
							)
					)
			}
	)
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
					Set<String> allPatterns = mappingInfo.getPatternValues();
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
	@Tag(name = "4. Word Search")
	@Operation(
			summary = "Search for words",
			description = "Performs a lexical search for words matching the given input. " +
					"Results arranged by word.  \n" +
					"**Wildcard Support:**  \n" +
					"- `*` matches zero or more characters (e.g., 'test*' matches 'test', 'testing', 'tested')\n" +
					"- `?` matches exactly one character (e.g., 'te?t' matches 'test', 'tent', 'text')\n",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Search results successfully returned",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = WordsResult.class)
							)
					)
			}
	)
	@Order(102)
	@GetMapping({
			API_SERVICES_URI + WORD_URI + SEARCH_URI + "/{word}",
			API_SERVICES_URI + WORD_URI + SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public WordsResult lexSearch(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "word",
					description = "The word, term or partial word to search for",
					required = true,
					example = "kobar"
			)
			@PathVariable("word") String word,
			@io.swagger.v3.oas.annotations.Parameter(
					name = "datasets",
					description = "Optional comma-separated list of dataset codes to filter the search",
					required = false,
					example = "eki,esterm,mil"
			)
			@PathVariable(value = "datasets", required = false) String datasetsStr,
			Authentication authentication,
			HttpServletRequest request) throws Exception {

		boolean noLimit = true;
		List<String> datasets = parseDatasets(datasetsStr);
		// TODO either specific role or anonymous user should be applied here
		String userRoleDatasetCode = null;
		WordsResult results = lexSearchService.getWords(word, datasets, null, userRoleDatasetCode, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT, noLimit);
		addRequestStat(authentication, request);
		return results;
	}
	@Tag(name = "4. Word Search")
	@Operation(
			summary = "Get comprehensive word details",
			description = "Retrieves extensive information about a specific word including all lexemes, meanings, paradigms, word relations, and linguistic properties. " +
					"Returns complete word morphology, definitions, usage examples, and etymological data when available.",
			parameters = {
					@io.swagger.v3.oas.annotations.Parameter(
							name = "wordId",
							description = "The unique identifier of the word to retrieve details for",
							required = true,
							example = "182736",
							schema = @Schema(type = "integer", format = "int64")
					),
					@io.swagger.v3.oas.annotations.Parameter(
							name = "datasets",
							description = "Comma-separated list of dataset codes to filter lexeme and meaning data. " +
									"See /api/datasets endpoint for available dataset codes and api/word/search/{word} for the " +
									"list of datasets that include the inquired word.",
							required = false,
							example = "eki,esterm,mil",
							schema = @Schema(type = "string")
					)
			},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Word details successfully retrieved.",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = eki.ekilex.data.WordDetails.class)
							)
					)
			})
	@Order(103)
	@GetMapping({
			API_SERVICES_URI + WORD_URI + DETAILS_URI + "/{wordId}",
			API_SERVICES_URI + WORD_URI + DETAILS_URI + "/{wordId}/{datasets}"
	})
	@ResponseBody
	public WordDetails getWordDetails(
			@PathVariable("wordId") Long wordId,
			@PathVariable(value = "datasets", required = false) String datasetsStr,
			Authentication authentication,
			HttpServletRequest request) throws Exception {

		List<String> datasets = parseDatasets(datasetsStr);
		boolean isCollocData = true;
		boolean isFullData = true;
		EkiUser user = userContext.getUser();
		// TODO either specific role or anonymous user should be applied here
		WordDetails result = lexSearchService.getWordDetails(wordId, null, datasets, null, user, null, null, isCollocData, isFullData);
		addRequestStat(authentication, request);
		return result;
	}
	@Tag(name = "5. Meaning Search")
	@Operation(
			summary = "Search for words with the same meaning (includes translations)",
			description = "Search for meanings and their associated terms across terminology databases. " +
					"Use the api/meaning/details/{meaningId} endpoint to retrieve definitions for each meaning.  \n" +
					"This endpoint allows you to find a word or term in any language and retrieve all " +
					"related terms that share the same meaning.  \n" +
					"Results are organized by meanings, making it easy to identify synonymous terms across different languages.  \n" +
					"**Wildcard Support:**\n" +
					"- `*` matches zero or more characters (e.g., 'test*' matches 'test', 'testing', 'tested')\n" +
					"- `?` matches exactly one character (e.g., 'te?t' matches 'test', 'tent', 'text')\n",
			parameters = {
					@io.swagger.v3.oas.annotations.Parameter(
							name = "word",
							description = "The search term or word to find meanings for. ",
							required = true,
							example = "jääkarussell",
							schema = @Schema(type = "string")
					),
					@io.swagger.v3.oas.annotations.Parameter(
							name = "datasets",
							description = "Comma-separated list of dataset codes to filter the results.  \n" +
									"To find, which datasets include the searchable word, use the /api/word/search/{word} " +
									"endpoint and look for words -> datasetCodes ",
							required = false,
							example = "eki",
							schema = @Schema(type = "string")
					)
			},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Meanings and associated terms successfully retrieved.  \n",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											implementation = TermSearchResult.class,
											description = "Structured result containing meanings grouped with their associated terms"
									)
							)
					)
			}
	)
	@Order(104)
	@GetMapping({
			API_SERVICES_URI + MEANING_URI + SEARCH_URI + "/{word}",
			API_SERVICES_URI + MEANING_URI + SEARCH_URI + "/{word}/{datasets}"
	})
	@ResponseBody
	public TermSearchResult termSearch(
			@PathVariable("word") String word,
			@PathVariable(value = "datasets", required = false) String datasetsStr,
			Authentication authentication,
			HttpServletRequest request) throws Exception {

		boolean noLimit = true;
		List<String> datasets = parseDatasets(datasetsStr);
		SearchResultMode resultMode = SearchResultMode.MEANING;
		String resultLang = null;
		TermSearchResult results = termSearchService.getTermSearchResult(word, datasets, resultMode, resultLang, DEFAULT_OFFSET, noLimit);
		addRequestStat(authentication, request);
		return results;
	}

	@Tag(name = "5. Meaning Search")
	@Operation(
			summary = "Get meaning details by meaning ID",
			description = "Retrieves detailed information about a specific meaning including definitions, lexemes, and usage",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Meaning details successfully retrieved",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = Meaning.class)
							)
					)
			}
	)
	@Order(105)
	@GetMapping({
			API_SERVICES_URI + MEANING_URI + DETAILS_URI + "/{meaningId}",
			API_SERVICES_URI + MEANING_URI + DETAILS_URI + "/{meaningId}/{datasets}"
	})
	@ResponseBody
	public Meaning getMeaningDetails(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "meaningId",
					description = "The unique identifier of the meaning to retrieve",
					required = true,
					example = "345678"
			)
			@PathVariable("meaningId") Long meaningId,
			@io.swagger.v3.oas.annotations.Parameter(
					name = "datasets",
					description = "Optional comma-separated list of dataset codes to filter the results",
					required = false,
					example = "eki,esterm"
			)
			@PathVariable(value = "datasets", required = false) String datasetsStr,
			Authentication authentication,
			HttpServletRequest request) throws Exception {

		List<String> datasets = parseDatasets(datasetsStr);
		List<Classifier> allLanguages = commonDataService.getLanguages();
		List<ClassifierSelect> languagesOrder = convert(allLanguages);
		EkiUser user = userContext.getUser();
		Meaning meaning = termSearchService.getMeaning(meaningId, datasets, languagesOrder, null, user, null);
		addRequestStat(authentication, request);
		return meaning;
	}

	@Tag(name = "3. Public Data")
	@Operation(
			summary = "Get public words by dataset",
			description = "Retrieves publicly available words from a specific dataset",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successfully retrieved list public words from a specific dataset",
							content = @Content(
									array = @ArraySchema(schema = @Schema(type = "object",
											description = "Represents a shortened version of word details",
											example = "{ \n \"wordId\": \"169532\",  \n" +
													" \"value\": \"hinnakujundus\",  \n" +
													"\"valuePrese\": \"hinnakujundus\",  \n" +
													"\"lang\": \"est\",  \n" +
													"\"homonymNr\": \"1\",  \n" +
													"\"morphophonoForm\": \"hinna+kujundus\",  \n" +
													"\"morphExists\": \"true\"}"))
							)
					)
			}
	)
	@Order(106)
	@GetMapping({
			API_SERVICES_URI + PUBLIC_WORD_URI + "/{datasetCode}",
			API_SERVICES_URI + PUBLIC_WORD_URI + "/{datasetCode}/{tagName}"
	})
	@ResponseBody
	public List<Word> getPublicWords(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "datasetCode",
					description = "Code of the dataset of interest (case-sensitive). " +
							"See the list of available datasets from the /api/datasets endpoint" +
					"  \n " +
							"Example datasets: 'mar' (Marketing Terminology Database), 'bks' (Dictionary of Biochemistry)",
					required = true,
					example = "mar"
			)
			@PathVariable("datasetCode") String datasetCode,
			@io.swagger.v3.oas.annotations.Parameter(
					name = "tagName",
					description = "Optional parameter: word tag.",
					required = false,
					example = ""
			)
			@PathVariable(value = "tagName", required = false) String tagName,
			Authentication authentication,
			HttpServletRequest request) {

		List<Word> publicWords = lexWordService.getPublicWords(datasetCode, tagName);
		addRequestStat(authentication, request);
		return publicWords;
	}
	@Tag(name = "4. Word Search")
	@Operation(
			summary = "Search for word ID-s by dataset and language",
			description = "Retrieves the unique identifiers (wordIds) for a word value within a specific dataset and language. " +
					"Useful when you know the word value but need its database ID for further operations.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Word IDs successfully retrieved",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(type = "integer", format = "int64", example = "178622"))
							)
					),
			}
	)
	@Order(107)
	@GetMapping(API_SERVICES_URI + WORD_URI + IDS_URI + "/{wordValue}/{datasetCode}/{lang}")
	@ResponseBody
	public List<Long> getWordIds(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "wordValue",
					description = "The word to search for",
					required = true,
					example = "kass"
			)
			@PathVariable("wordValue") String wordValue,
			@io.swagger.v3.oas.annotations.Parameter(
					name = "datasetCode",
					description = "Dataset code to search within. See the list of available datasets from the /api/datasets endpoint.",
					required = true,
					example = "eki"
			)
			@PathVariable("datasetCode") String datasetCode,
			@io.swagger.v3.oas.annotations.Parameter(
					name = "lang",
					description = "Language code. See the list of available language codes from the /api/classifiers/language endpoint.",
					required = true,
					example = "est"
			)
			@PathVariable("lang") String lang,
			Authentication authentication,
			HttpServletRequest request) {

		List<Long> wordIds = lexWordService.getWordIds(wordValue, datasetCode, lang);
		addRequestStat(authentication, request);
		return wordIds;
	}

	@Tag(name = "2. Reference Data")
	@Operation(
			summary = "Get classifier values for linguistic data",
			description = "Retrieves predefined classifier values used throughout the Ekilex database. " +
					"These classifiers define valid values for linguistic properties like part of speech, grammar, etc.  \n" +
					"\n**Available Classifiers:**  \n" +
					"* **LABEL_TYPE** - Label categories  \n" +
					"* **LANGUAGE** - Language codes (e.g. 'est' for Estonian, 'eng' for English)  \n" +
					"* **DOMAIN** - Subject domains (e.g. 'culture', 'mathematics', 'materials')  \n" +
					"* **GOVERNMENT_TYPE** - Government classifications (e.g. object government, subject government)  \n" +
					"* **REGISTER** - Register classifications (dialect, slang, vulgar etc.)  \n" +
					"* **GENDER** - Gender classifications (masculine, feminine, neutral)  \n" +
					"* **POS** - Part of speech (noun, verb, adjective, etc.)  \n" +
					"* **MORPH** - Morphological forms  \n" +
					"* **DERIV** - Derivation types  \n" +
					"* **WORD_TYPE** - Word type classifications (citation, compound verb, formula etc.)  \n" +
					"* **ETYMOLOGY_TYPE** - Etymology source types (e.g. loan word, native word, name etc.)  \n" +
					"* **MEANING_REL_TYPE** - Meaning relationship types (e.g. antonym, co-hyponym, sub-concept etc.)  \n" +
					"* **LEX_REL_TYPE** - Lexical relationship types (e.g. compound, sub-word etc.)  \n" +
					"* **WORD_REL_TYPE** - Word relationship types (e.g. comparative, superlative, derivation etc.) \n" +
					"* **DISPLAY_MORPH** - Morphological forms to be displayed  \n" +
					"* **USAGE_TYPE** - Types of usage (e.g. proverb)  \n" +
					"* **VALUE_STATE** - Value state classifications for terms (e.g. preferred, proposal, questionable)  \n" +
					"* **POS_GROUP** - Part of speech groups  \n" +
					"* **REL_GROUP** - Relationship groups  \n" +
					"* **ASPECT** - Aspect classifications (perfective, imperfective, etc.)  \n" +
					"* **DEFINITION_TYPE** - Definition type classifications  \n" +
					"* **REGION** - Geographic regions  \n" +
					"* **SEMANTIC_TYPE** - Semantic type classifications (e.g. object, occupation, property etc.) \n" +
					"* **PROFICIENCY_LEVEL** - Language proficiency levels (A1, B2 etc.) \n" +
					"* **FREEFORM_TYPE** - Types of freeform text entries",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Classifier values successfully retrieved, or empty array if classifier does not exist",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(
													type = "object",
													description = "Classifier entry containing name, code, and value",
													example = "{\n  \"name\": \"LANGUAGE\",\n  \"code\": \"eng\",\n  \"value\": \"inglise\"\n}"
											)
									)
							)
					),
			}
	)
	@Order(108)
	@GetMapping(API_SERVICES_URI + CLASSIFIERS_URI + "/{classifierName}")
	@ResponseBody
	public List<Classifier> getClassifiers(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "classifierName",
					description = "The classifier name. See description for the list of available classifiers.",
					required = true,
					example = "LANGUAGE"
			)
			@PathVariable("classifierName") String classifierNameStr,
			Authentication authentication,
			HttpServletRequest request) {

		ClassifierName classifierName = null;
		try {
			classifierNameStr = classifierNameStr.toUpperCase();
			classifierName = ClassifierName.valueOf(classifierNameStr);
		} catch (Exception e) {
			return null;
		}
		List<Classifier> classifiers = commonDataService.getClassifiers(classifierName);
		addRequestStat(authentication, request);
		return classifiers;
	}


	@Tag(name = "2. Reference Data")
	@Operation(
			summary = "Get domain origins",
			description = "Retrieves a list of domain origins that categorize the various subject domains available in Ekilex. " +
					"These are high-level categories used to organize specialized terminology databases.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Domain origins successfully retrieved",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(
											schema = @Schema(
													type = "object",
													description = "Domain origin entry containing code and label",
													example = "{\n  \"code\": \"arh\",\n  \"label\": \"Arheoloogia terminibaas\"\n}"
											)
									)
							)
					)
			}
	)
	@Order(109)
	@GetMapping(API_SERVICES_URI + DOMAIN_ORIGINS_URI)
	@ResponseBody
	public List<Origin> getDomainOrigins(Authentication authentication, HttpServletRequest request) {
		List<Origin> domainOrigins = commonDataService.getDomainOrigins();
		addRequestStat(authentication, request);
		return domainOrigins;
	}

	@Tag(name = "2. Reference Data")
	@Operation(
			summary = "Get domains by origin",
			description = "Retrieves a list of subject domains for a specific domain origin. " +
					"Domains are used to classify specialized terminology and subject areas.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Domains successfully retrieved",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(type = "object",
													description = "Domain entry for the specified origin containing code and value",
													example = "{ \n \"name\": \"DOMAIN\",  \n" +
      															 " \"origin\": \"mut\",  \n" + 
       															"\"code\": \"museoloogia\",  \n" +
																"\"value\": \"museoloogia\" \n}")
															)
							)
					)
			}
	)
	@Order(110)
	@GetMapping(API_SERVICES_URI + DOMAINS_URI + "/{origin}")
	@ResponseBody
	public List<Classifier> getDomains(
			@io.swagger.v3.oas.annotations.Parameter(
					name = "origin",
					description = "Domain origin to retrieve domains for. See available origins from the /api/domainorigins endpoint." +
							"  \n" +
							" Example origins: 'mut' (Museuem Terminology Database), 'militerm' (Military Terminology Domain)",
					required = true,
					example = "mut"
			)
			@PathVariable("origin") String origin,
			Authentication authentication,
			HttpServletRequest request) {
		List<Classifier> domains = commonDataService.getDomains(origin);
		addRequestStat(authentication, request);
		return domains;
	}

	@Tag(name = "2. Reference Data")
	@Operation(
			summary = "List all available datasets",
			description = "Retrieves a comprehensive list of all available datasets in Ekilex, including general dictionaries, " +
					"terminology databases, and specialized lexicons. Each dataset has a unique code and metadata.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Datasets successfully retrieved",
							content = @Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = Dataset.class))
							)
					)
			}
	)
	@Order(111)
	@GetMapping(API_SERVICES_URI + DATASETS_URI)
	@ResponseBody
	public List<Dataset> getDatasets(Authentication authentication, HttpServletRequest request) {
		List<Dataset> allDatasets = commonDataService.getAllDatasets();
		addRequestStat(authentication, request);
		return allDatasets;
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
