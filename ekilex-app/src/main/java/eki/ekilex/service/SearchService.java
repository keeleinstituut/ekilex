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
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.SearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Service
public class SearchService {

	@Autowired
	private SearchDbService searchDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	public Map<String, String> getDatasetNameMap() {
		return searchDbService.getDatasetNameMap();
	}

	public List<Word> findWordsInDatasets(String searchFilter, List<String> datasets) {
		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		return searchDbService.findWordsInDatasets(searchFilter, datasets).into(Word.class);
	}

	public WordDetails findWordDetailsInDatasets(Long formId, List<String> selectedDatasets) {

		Map<String, String> datasetNameMap = searchDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = searchDbService.findFormMeaningsInDatasets(formId, selectedDatasets).into(WordLexeme.class);
		List<Form> connectedForms = searchDbService.findConnectedForms(formId).into(Form.class);

		lexemes.forEach(lexeme -> {

			String dataset = lexeme.getDataset();
			dataset = datasetNameMap.get(dataset);
			lexeme.setDataset(dataset);

			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Form> words = searchDbService.findConnectedWordsInDatasets(meaningId, selectedDatasets).into(Form.class);
			lexeme.setWords(words);

			List<Classifier> domains = searchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			lexeme.setDomains(domains);

			List<Definition> meaningDefinitions = searchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			lexeme.setDefinitions(meaningDefinitions);

			List<FreeForm> meaningFreeforms = searchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			lexeme.setMeaningFreeforms(meaningFreeforms);

			List<FreeForm> lexemeFreeforms = searchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			lexeme.setLexemeFreeforms(lexemeFreeforms);

			List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples =
					searchDbService.findRectionUsageTranslationDefinitionTuples(lexemeId).into(RectionUsageTranslationDefinitionTuple.class);

			List<Rection> rections = conversionUtil.composeRections(rectionUsageTranslationDefinitionTuples);
			lexeme.setRections(rections);

		});
		combineLevels(lexemes);
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setLexemes(lexemes);
		});
	}

	private void combineLevels(List<WordLexeme> lexemes) {

		if (CollectionUtils.isEmpty(lexemes)) {
			return;
		}

		lexemes.forEach(lexeme -> {
			if (lexeme.getLevel1() == 0) {
				lexeme.setLevels("");
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