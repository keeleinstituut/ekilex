package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Government;
import eki.ekilex.data.GovernmentUsageTranslationDefinitionTuple;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningsResult;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.WordTuple;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class TermSearchService implements SystemConstant {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public MeaningsResult findMeanings(SearchFilter searchFilter, List<String> datasets, String resultLang, boolean fetchAll) throws Exception {

		Map<Long, List<WordTuple>> termMeaningsMap;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			termMeaningsMap = Collections.emptyMap();
		} else {
			termMeaningsMap = termSearchDbService.findMeaningsAsMap(searchFilter, datasets, resultLang, fetchAll);
		}
		List<TermMeaning> termMeanings = conversionUtil.convert(termMeaningsMap);
		int meaningCount = termMeanings.size();
		if (!fetchAll && meaningCount == MAX_RESULTS_LIMIT) {
			meaningCount = termSearchDbService.countMeanings(searchFilter, datasets);
		}
		boolean resultExist = meaningCount > 0;
		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setTermMeanings(termMeanings);
		meaningsResult.setResultCount(meaningCount);
		meaningsResult.setResultExist(resultExist);
		return meaningsResult;
	}

	@Transactional
	public MeaningsResult findMeanings(String searchFilter, List<String> datasets, String resultLang, boolean fetchAll) {

		Map<Long, List<WordTuple>> termMeaningsMap;
		if (StringUtils.isBlank(searchFilter)) {
			termMeaningsMap = Collections.emptyMap();
		} else {
			termMeaningsMap = termSearchDbService.findMeaningsAsMap(searchFilter, datasets, resultLang, fetchAll);
		}
		List<TermMeaning> termMeanings = conversionUtil.convert(termMeaningsMap);
		int meaningCount = termMeanings.size();
		if (!fetchAll && meaningCount == MAX_RESULTS_LIMIT) {
			meaningCount = termSearchDbService.countMeanings(searchFilter, datasets);
		}
		boolean resultExist = meaningCount > 0;
		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setTermMeanings(termMeanings);
		meaningsResult.setResultCount(meaningCount);
		meaningsResult.setResultExist(resultExist);
		return meaningsResult;
	}

	@Transactional
	public Meaning getMeaning(Long meaningId, List<String> selectedDatasets, List<ClassifierSelect> languagesOrder) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";

		List<String> langCodeOrder = languagesOrder.stream().map(Classifier::getCode).collect(Collectors.toList());
		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		Meaning meaning = termSearchDbService.getMeaning(meaningId, selectedDatasets).into(Meaning.class);

		List<DefinitionRefTuple> definitionRefTuples = commonDataDbService.findMeaningDefinitionRefTuples(meaningId).into(DefinitionRefTuple.class);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples, langCodeOrder);
		List<Classifier> domains = commonDataDbService.findMeaningDomains(meaningId).into(Classifier.class);
		List<FreeForm> meaningFreeforms = commonDataDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
		List<Relation> meaningRelations = commonDataDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip).into(Relation.class);
		List<Lexeme> lexemes = new ArrayList<>();

		boolean contentExists =
				StringUtils.isNotBlank(meaning.getProcessStateCode())
				|| CollectionUtils.isNotEmpty(definitions)
				|| CollectionUtils.isNotEmpty(domains)
				|| CollectionUtils.isNotEmpty(meaningFreeforms)
				|| CollectionUtils.isNotEmpty(meaningRelations)
				;

		meaning.setDefinitions(definitions);
		meaning.setDomains(domains);
		meaning.setFreeforms(meaningFreeforms);
		meaning.setLexemes(lexemes);
		meaning.setRelations(meaningRelations);
		meaning.setContentExists(contentExists);

		List<Long> lexemeIds = meaning.getLexemeIds();

		for (Long lexemeId : lexemeIds) {

			// lexeme is duplicated if many form.is_word-s different by value
			List<Lexeme> lexemeWords = termSearchDbService.getLexemeWords(lexemeId).into(Lexeme.class);
			List<Classifier> lexemePos = commonDataDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<Classifier> lexemeDerivs = commonDataDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<Classifier> lexemeRegisters = commonDataDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<FreeForm> lexemeFreeforms = commonDataDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples =
					commonDataDbService.findGovernmentUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
							.into(GovernmentUsageTranslationDefinitionTuple.class);
			List<Government> governments = conversionUtil.composeGovernments(governmentUsageTranslationDefinitionTuples);
			List<String> lexemeGrammars = commonDataDbService.findLexemeGrammars(lexemeId).into(String.class);

			for (Lexeme lexeme : lexemeWords) {

				boolean classifiersExist =
						StringUtils.isNotBlank(lexeme.getValueStateCode())
						|| StringUtils.isNotBlank(lexeme.getGenderCode())
						|| StringUtils.isNotBlank(lexeme.getFrequencyGroupCode())
						|| CollectionUtils.isNotEmpty(lexemePos)
						|| CollectionUtils.isNotEmpty(lexemeDerivs)
						|| CollectionUtils.isNotEmpty(lexemeRegisters)
						|| CollectionUtils.isNotEmpty(lexemeGrammars);

				String dataset = lexeme.getDataset();
				dataset = datasetNameMap.get(dataset);
				String levels = composeLevels(lexeme);
				List<String> sources = extractSources(lexemeFreeforms);

				lexeme.setLevels(levels);
				lexeme.setDataset(dataset);
				lexeme.setPos(lexemePos);
				lexeme.setDerivs(lexemeDerivs);
				lexeme.setRegisters(lexemeRegisters);
				lexeme.setFreeforms(lexemeFreeforms);
				lexeme.setGovernments(governments);
				lexeme.setGrammars(lexemeGrammars);
				lexeme.setClassifiersExist(classifiersExist);
				lexeme.setSources(sources);
				lexemes.add(lexeme);
			}
		}

		lexemes.sort((Lexeme lexeme1, Lexeme lexeme2) -> {
			int lexeme1LangOrder = langCodeOrder.indexOf(lexeme1.getWordLang());
			int lexeme2LangOrder = langCodeOrder.indexOf(lexeme2.getWordLang());
			return lexeme1LangOrder - lexeme2LangOrder;
			});

		return meaning;
	}

	private List<String> extractSources(List<FreeForm> lexemeFreeforms) {
		List<String> sources = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(lexemeFreeforms)) {
			sources = lexemeFreeforms.stream()
					.filter(freeform -> freeform.getType().equals(FreeformType.SOURCE))
					.map(FreeForm::getValueText)
					.collect(Collectors.toList());
		}
		return sources;
	}

	private String composeLevels(Lexeme lexeme) {

		Integer level1 = lexeme.getLevel1();
		Integer level2 = lexeme.getLevel2();
		Integer level3 = lexeme.getLevel3();
		String levels = null;
		if (level1 > 0) {
			levels = String.valueOf(level1);
			if (level2 > 0) {
				levels += "." + level2;
			}
			if (level3 > 0) {
				levels += "." + level3;
			}
		}
		return levels;
	}
}
