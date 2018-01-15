package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Rection;
import eki.ekilex.data.RectionUsageTranslationDefinitionTuple;
import eki.ekilex.data.TermDetails;
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
	private ConversionUtil conversionUtil;

	@Transactional
	public TermDetails findWordDetailsInDatasets(Long formId, List<String> selectedDatasets) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";

		Map<String, String> datasetNameMap = lexSearchDbService.getDatasetNameMap();
		List<Meaning> meanings = termSearchDbService.findFormMeanings(formId, selectedDatasets).into(Meaning.class);

		for (Meaning meaning : meanings) {

			Long meaningId = meaning.getMeaningId();
			List<Long> lexemeIds = meaning.getLexemeIds();

			List<Definition> definitions = lexSearchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			List<Classifier> domains = lexSearchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			List<FreeForm> meaningFreeforms = lexSearchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			List<Lexeme> lexemes = new ArrayList<>();

			meaning.setDefinitions(definitions);
			meaning.setDomains(domains);
			meaning.setFreeforms(meaningFreeforms);
			meaning.setLexemes(lexemes);

			for (Long lexemeId : lexemeIds) {

				// lexeme is duplicated if many form.is_word-s
				List<Classifier> lexemePos = lexSearchDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Classifier> lexemeDerivs = lexSearchDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Classifier> lexemeRegisters = lexSearchDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
				List<Lexeme> lexemeWords = termSearchDbService.getLexemeWords(lexemeId).into(Lexeme.class);
				List<FreeForm> lexemeFreeforms = lexSearchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
				List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples =
						lexSearchDbService.findRectionUsageTranslationDefinitionTuples(lexemeId).into(RectionUsageTranslationDefinitionTuple.class);
				List<Rection> rections = conversionUtil.composeRections(rectionUsageTranslationDefinitionTuples);

				for (Lexeme lexeme : lexemeWords) {

					String dataset = lexeme.getDataset();
					dataset = datasetNameMap.get(dataset);
					String levels = composeLevels(lexeme);
					lexeme.setLevels(levels);
					lexeme.setDataset(dataset);
					lexeme.setPos(lexemePos);
					lexeme.setDerivs(lexemeDerivs);
					lexeme.setRegisters(lexemeRegisters);
					lexeme.setFreeforms(lexemeFreeforms);
					lexeme.setRections(rections);
					lexemes.add(lexeme);
				}
			}
		}

		TermDetails termDetails = new TermDetails();
		termDetails.setMeanings(meanings);

		return termDetails;
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
