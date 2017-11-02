package eki.ekilex.service;

import eki.common.constant.FreeformType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Form;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.Rection;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.service.db.SearchDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class SearchService {

	@Autowired
	private SearchDbService searchDbService;

	public Map<String, String> getDatasets() {
		return searchDbService.getDatasetNameMap();
	}

	public List<Word> findWordsInDatasets(String searchFilter, List<String> datasets) {
		return searchDbService.findWordsInDatasets(searchFilter, datasets).into(Word.class);
	}

	public WordDetails findWordDetailsInDatasets(Long formId, List<String> selectedDatasets) {

		Map<String, String> datasetNameMap = searchDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = searchDbService.findFormMeaningsInDatasets(formId, selectedDatasets).into(WordLexeme.class);
		List<Form> connectedForms = searchDbService.findConnectedForms(formId).into(Form.class);

		lexemes.forEach(lexeme -> {

			List<String> datasets = lexeme.getDatasets();
			datasets = convertToNames(datasets, datasetNameMap);
			lexeme.setDatasets(datasets);

			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Form> words = searchDbService.findConnectedWordsInDatasets(meaningId, selectedDatasets).into(Form.class);
			lexeme.setWords(words);

			List<Classifier> domains = searchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			lexeme.setDomains(domains);

			List<FreeForm> meaningFreeforms = searchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			lexeme.setMeaningFreeforms(meaningFreeforms);

			List<FreeForm> lexemeFreeforms = searchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			lexeme.setLexemeFreeforms(lexemeFreeforms);

			populateRections(lexeme);
		});
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setMeanings(lexemes);
		});
	}

	private void populateRections(WordLexeme lexeme) {
		List<Rection> rections = searchDbService.findConnectedRections(lexeme.getLexemeId()).into(Rection.class);
		removeNullUsages(rections);
		List<FreeForm> lexemeRections = lexeme.getLexemeFreeforms().stream().filter(f -> f.getType().equals(FreeformType.RECTION)).collect(Collectors.toList());
		for (FreeForm freeForm : lexemeRections) {
			Rection rection = new Rection();
			rection.setValue(freeForm.getValueText());
			List<FreeForm> usages = searchDbService.findFreeformChilds(freeForm.getId()).into(FreeForm.class);
			if (!usages.isEmpty()) {
				String[][] arrayOfUsages = new String[usages.size()][];
				int index = 0;
				for (FreeForm usage : usages) {
					arrayOfUsages[index++] = new String[]{usage.getValueText()};
				}
				rection.setUsages(arrayOfUsages);
			}
			rections.add(rection);
		}
		lexeme.setRections(rections);
	}

	private void removeNullUsages(List<Rection> rections) {
		rections.forEach(rection -> {
			if (rection.getUsages().length == 1 && Arrays.stream(rection.getUsages()[0]).allMatch(Objects::isNull)) {
				rection.setUsages(null);
			}
		});
	}

	private List<String> convertToNames(List<String> datasets, Map<String, String> datasetMap) {

		if (datasets == null) {
			return emptyList();
		}
		return datasets.stream().map(datasetMap::get).collect(Collectors.toList());
	}

}