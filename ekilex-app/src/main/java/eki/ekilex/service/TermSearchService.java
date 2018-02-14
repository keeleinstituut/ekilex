package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import eki.common.constant.FreeformType;
import eki.ekilex.data.Relation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Government;
import eki.ekilex.data.GovernmentUsageTranslationDefinitionTuple;
import eki.ekilex.data.TermDetails;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.db.TermSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class TermSearchService {

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public TermDetails getTermDetails(Long wordId, List<String> selectedDatasets) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";

		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		List<Meaning> meanings = termSearchDbService.findWordMeanings(wordId, selectedDatasets).into(Meaning.class);

		for (Meaning meaning : meanings) {

			Long meaningId = meaning.getMeaningId();
			List<Long> lexemeIds = meaning.getLexemeIds();

			List<Definition> definitions = lexSearchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			List<Classifier> domains = lexSearchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			List<FreeForm> meaningFreeforms = lexSearchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			List<Relation> meaningRelations = lexSearchDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip).into(Relation.class);
			List<Lexeme> lexemes = new ArrayList<>();

			boolean contentExists =
					StringUtils.isNotBlank(meaning.getTypeCode())
					|| StringUtils.isNotBlank(meaning.getProcessStateCode())
					|| StringUtils.isNotBlank(meaning.getStateCode())
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

			for (Long lexemeId : lexemeIds) {

				// lexeme is duplicated if many form.is_word-s different by value
				List<Classifier> lexemePos = lexSearchDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Classifier> lexemeDerivs = lexSearchDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Classifier> lexemeRegisters = lexSearchDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Lexeme> lexemeWords = termSearchDbService.getLexemeWords(lexemeId).into(Lexeme.class);
				List<FreeForm> lexemeFreeforms = lexSearchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
				List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples =
						lexSearchDbService.findGovernmentUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
								.into(GovernmentUsageTranslationDefinitionTuple.class);
				List<Government> governments = conversionUtil.composeGovernments(governmentUsageTranslationDefinitionTuples);
				List<String> lexemeGrammars = lexSearchDbService.findLexemeGrammars(lexemeId).into(String.class);

				for (Lexeme lexeme : lexemeWords) {

					boolean classifiersExist =
							StringUtils.isNotBlank(lexeme.getTypeCode())
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
					lexeme.setGovernments(governments);
					lexeme.setGrammars(lexemeGrammars);
					lexeme.setClassifiersExist(classifiersExist);
					lexeme.setSources(extractSources(lexemeFreeforms));
					lexemes.add(lexeme);
				}
			}
		}

		TermDetails termDetails = new TermDetails();
		termDetails.setMeanings(meanings);

		return termDetails;
	}

	private List<String> extractSources(List<FreeForm> lexemeFreeforms) {
		List<String> sources = Collections.emptyList();
		if (lexemeFreeforms != null) {
			sources = lexemeFreeforms.stream()
					.filter(f -> f.getType().equals(FreeformType.SOURCE))
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
