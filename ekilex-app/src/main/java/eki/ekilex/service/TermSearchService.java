package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningsResult;
import eki.ekilex.data.Relation;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.TermMeaning;
import eki.ekilex.data.TermMeaningWordTuple;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
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
	public MeaningsResult findMeanings(String searchFilter, List<String> datasets, String resultLang, boolean fetchAll) {

		List<TermMeaning> termMeanings;
		if (StringUtils.isBlank(searchFilter)) {
			termMeanings = Collections.emptyList();
		} else {
			List<TermMeaningWordTuple> termMeaningWordTuples = termSearchDbService.findMeanings(searchFilter, datasets, resultLang, fetchAll);
			termMeanings = conversionUtil.composeTermMeanings(termMeaningWordTuples);
		}
		int meaningCount = termSearchDbService.countMeanings(searchFilter, datasets);
		int wordCount = termSearchDbService.countWords(searchFilter, datasets, resultLang);
		boolean resultExist = meaningCount > 0;
		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setMeaningCount(meaningCount);
		meaningsResult.setWordCount(wordCount);
		meaningsResult.setTermMeanings(termMeanings);
		meaningsResult.setResultExist(resultExist);
		return meaningsResult;
	}

	@Transactional
	public MeaningsResult findMeanings(SearchFilter searchFilter, List<String> datasets, String resultLang, boolean fetchAll) throws Exception {

		List<TermMeaning> termMeanings;
		if (CollectionUtils.isEmpty(searchFilter.getCriteriaGroups())) {
			termMeanings = Collections.emptyList();
		} else {
			List<TermMeaningWordTuple> termMeaningWordTuples = termSearchDbService.findMeanings(searchFilter, datasets, resultLang, fetchAll);
			termMeanings = conversionUtil.composeTermMeanings(termMeaningWordTuples);
		}
		int meaningCount = termSearchDbService.countMeanings(searchFilter, datasets);
		int wordCount = termSearchDbService.countWords(searchFilter, datasets, resultLang);
		boolean resultExist = meaningCount > 0;
		MeaningsResult meaningsResult = new MeaningsResult();
		meaningsResult.setMeaningCount(meaningCount);
		meaningsResult.setWordCount(wordCount);
		meaningsResult.setTermMeanings(termMeanings);
		meaningsResult.setResultExist(resultExist);
		return meaningsResult;
	}

	@Transactional
	public String getMeaningFirstWord(Long meaningId, List<String> selectedDatasets) {
		return termSearchDbService.getMeaningFirstWord(meaningId, selectedDatasets).into(String.class);
	}

	@Transactional
	public Meaning getMeaning(Long meaningId, List<String> selectedDatasets, List<String> langCodeOrder) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";

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
			List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
					commonDataDbService.findUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
							.into(UsageTranslationDefinitionTuple.class);
			List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
			List<FreeForm> lexemeGrammars = commonDataDbService.findLexemeGrammars(lexemeId).into(FreeForm.class);
			List<SourceLink> lexemeRefLinks = commonDataDbService.findLexemeSourceLinks(lexemeId).into(SourceLink.class);

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

				lexeme.setLevels(levels);
				lexeme.setDataset(dataset);
				lexeme.setPos(lexemePos);
				lexeme.setDerivs(lexemeDerivs);
				lexeme.setRegisters(lexemeRegisters);
				lexeme.setFreeforms(lexemeFreeforms);
				lexeme.setUsages(usages);
				lexeme.setGrammars(lexemeGrammars);
				lexeme.setClassifiersExist(classifiersExist);
				lexeme.setSourceLinks(lexemeRefLinks);
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
