package eki.ekilex.service;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eki.ekilex.data.Definition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Form;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Rection;
import eki.ekilex.data.RectionUsageTranslationDefinitionTuple;
import eki.ekilex.data.UsageMeaning;
import eki.ekilex.data.UsageMember;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.SearchDbService;

@Service
public class SearchService {

	@Autowired
	private SearchDbService searchDbService;

	public Map<String, String> getDatasets() {
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

			List<String> datasets = lexeme.getDatasets();
			datasets = convertToNames(datasets, datasetNameMap);
			lexeme.setDatasets(datasets);

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

			List<Rection> rections = composeRections(rectionUsageTranslationDefinitionTuples);
			lexeme.setRections(rections);

		});
		combineLevels(lexemes);
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setLexemes(lexemes);
		});
	}

	private void combineLevels(List<WordLexeme> lexemes) {

		if (lexemes == null || lexemes.isEmpty()) return;

		lexemes.forEach(lex -> {
			if (lex.getLevel1() == 0) {
				lex.setLevels("");
				return;
			}
			long nrOfLexemesWithSameLevel1 = lexemes.stream().filter(l -> l.getLevel1().equals(lex.getLevel1())).count();
			if (nrOfLexemesWithSameLevel1 == 1) {
				lex.setLevels(String.valueOf(lex.getLevel1()));
			} else {
				long nrOfLexemesWithSameLevel2 = lexemes.stream()
						.filter(l -> l.getLevel1().equals(lex.getLevel1()) && l.getLevel2().equals(lex.getLevel2()))
						.count();
				if (nrOfLexemesWithSameLevel2 == 1) {
					lex.setLevels(lex.getLevel1() + "." + lex.getLevel2());
				} else {
					lex.setLevels(lex.getLevel1() + "." + lex.getLevel2() + "." + lex.getLevel3());
				}
			}
		});
	}

	private List<Rection> composeRections(List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples) {

		List<Rection> rections = new ArrayList<>();

		Map<Long, Rection> rectionMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		Map<Long, UsageMember> usageMap = new HashMap<>();
		Map<Long, UsageMember> usageTranslationMap = new HashMap<>();
		Map<Long, UsageMember> usageDefinitionMap = new HashMap<>();

		for (RectionUsageTranslationDefinitionTuple tuple : rectionUsageTranslationDefinitionTuples) {

			Long rectionId = tuple.getRectionId();
			Long usageMeaningId = tuple.getUsageMeaningId();
			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();

			Rection rection = rectionMap.get(rectionId);
			if (rection == null) {
				rection = new Rection();
				rection.setId(rectionId);
				rection.setValue(tuple.getRectionValue());
				rection.setUsageMeanings(new ArrayList<>());
				rectionMap.put(rectionId, rection);
				rections.add(rection);
			}
			if (usageMeaningId == null) {
				continue;
			}
			UsageMeaning usageMeaning = usageMeaningMap.get(usageMeaningId);
			if (usageMeaning == null) {
				usageMeaning = new UsageMeaning();
				usageMeaning.setId(usageMeaningId);
				usageMeaning.setUsages(new ArrayList<>());
				usageMeaning.setUsageTranslations(new ArrayList<>());
				usageMeaning.setUsageDefinitions(new ArrayList<>());
				usageMeaningMap.put(usageMeaningId, usageMeaning);
				rection.getUsageMeanings().add(usageMeaning);
			}
			if (usageId != null) {
				UsageMember usage = usageMap.get(usageId);
				if (usage == null) {
					usage = new UsageMember();
					usage.setId(usageId);
					usage.setValue(tuple.getUsageValue());
					usage.setLang(tuple.getUsageLang());
					usageMap.put(usageId, usage);
					usageMeaning.getUsages().add(usage);
				}
			}
			if (usageTranslationId != null) {
				UsageMember usageTranslation = usageTranslationMap.get(usageTranslationId);
				if (usageTranslation == null) {
					usageTranslation = new UsageMember();
					usageTranslation.setId(usageTranslationId);
					usageTranslation.setValue(tuple.getUsageTranslationValue());
					usageTranslation.setLang(tuple.getUsageTranslationLang());
					usageTranslationMap.put(usageTranslationId, usageTranslation);
					usageMeaning.getUsageTranslations().add(usageTranslation);
				}
			}
			if (usageDefinitionId != null) {
				UsageMember usageDefinition = usageDefinitionMap.get(usageDefinitionId);
				if (usageDefinition == null) {
					usageDefinition = new UsageMember();
					usageDefinition.setId(usageDefinitionId);
					usageDefinition.setValue(tuple.getUsageDefinitionValue());
					usageDefinition.setLang(tuple.getUsageDefinitionLang());
					usageDefinitionMap.put(usageDefinitionId, usageDefinition);
					usageMeaning.getUsageDefinitions().add(usageDefinition);
				}
			}
		}
		return rections;
	}

	private List<String> convertToNames(List<String> datasets, Map<String, String> datasetMap) {

		if (datasets == null) {
			return emptyList();
		}
		return datasets.stream().map(datasetMap::get).collect(Collectors.toList());
	}

}