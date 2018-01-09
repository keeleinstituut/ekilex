package eki.ekilex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.Form;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Rection;
import eki.ekilex.data.RectionUsageTranslationDefinitionTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Service
public class LexSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	public Map<String, String> getDatasetNameMap() {
		return lexSearchDbService.getDatasetNameMap();
	}

	public List<Word> findWordsInDatasets(String searchFilter, List<String> datasets) {
		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		return lexSearchDbService.findWordsInDatasets(searchFilter, datasets).into(Word.class);
	}

	public WordDetails findWordDetailsInDatasets(Long formId, List<String> selectedDatasets) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";
		final String classifierLabelTypeFull = "full";

		Map<String, String> datasetNameMap = lexSearchDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = lexSearchDbService.findFormMeaningsInDatasets(formId, selectedDatasets).into(WordLexeme.class);
		List<Form> connectedForms = lexSearchDbService.findConnectedForms(formId, classifierLabelLang, classifierLabelTypeDescrip).into(Form.class);
		List<Relation> formRelations = lexSearchDbService.findFormRelations(formId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);

		lexemes.forEach(lexeme -> {

			String dataset = lexeme.getDataset();
			dataset = datasetNameMap.get(dataset);
			lexeme.setDataset(dataset);

			Long wordId = lexeme.getWordId();
			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Form> words = lexSearchDbService.findConnectedWordsInDatasets(
					meaningId, selectedDatasets, classifierLabelLang, classifierLabelTypeDescrip).into(Form.class);
			lexeme.setWords(words);

			List<Classifier> meaningDomains = lexSearchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			lexeme.setMeaningDomains(meaningDomains);

			List<Definition> meaningDefinitions = lexSearchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			lexeme.setDefinitions(meaningDefinitions);

			List<FreeForm> meaningFreeforms = lexSearchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			lexeme.setMeaningFreeforms(meaningFreeforms);

			List<FreeForm> lexemeFreeforms = lexSearchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			lexeme.setLexemeFreeforms(lexemeFreeforms);

			List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples =
					lexSearchDbService.findRectionUsageTranslationDefinitionTuples(lexemeId).into(RectionUsageTranslationDefinitionTuple.class);

			List<Rection> rections = conversionUtil.composeRections(rectionUsageTranslationDefinitionTuples);
			lexeme.setRections(rections);

			List<Relation> lexemeRelations = lexSearchDbService.findLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
			lexeme.setLexemeRelations(lexemeRelations);

			List<Relation> wordRelations = lexSearchDbService.findWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
			lexeme.setWordRelations(wordRelations);

			boolean lexemeOrMeaningClassifiersExist =
					StringUtils.isNotBlank(lexeme.getLexemeTypeCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningTypeCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningProcessStateCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningStateCode())
					|| CollectionUtils.isNotEmpty(meaningDomains);
			lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
		});
		combineLevels(lexemes);
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setLexemes(lexemes);
			d.setRelations(formRelations);
		});
	}

	private void combineLevels(List<WordLexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		lexemes.forEach(lexeme -> {
			if (lexeme.getLevel1() == 0) {
				lexeme.setLevels(null);
				return;
			}
			String levels;
			long nrOfLexemesWithSameLevel1 = lexemes.stream()
					.filter(otherLexeme ->
							otherLexeme.getLevel1().equals(lexeme.getLevel1())
							&& StringUtils.equals(otherLexeme.getDataset(), lexeme.getDataset()))
					.count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				levels = String.valueOf(lexeme.getLevel1());
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(otherLexeme ->
								otherLexeme.getLevel1().equals(lexeme.getLevel1())
								&& otherLexeme.getLevel2().equals(lexeme.getLevel2())
								&& StringUtils.equals(otherLexeme.getDataset(), lexeme.getDataset()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2();
				} else {
					levels = lexeme.getLevel1() + "." + lexeme.getLevel2() + "." + lexeme.getLevel3();
				}
			}
			lexeme.setLevels(levels);
		});
	}

}