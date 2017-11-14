package eki.eve.service;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eki.common.data.Classifier;
import eki.eve.data.Definition;
import eki.eve.data.FreeForm;
import eki.eve.data.RectionUsageTranslationDefinitionTuple;
import eki.eve.data.UsageMeaning;
import eki.eve.data.UsageMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.eve.data.Form;
import eki.eve.data.WordLexeme;
import eki.eve.data.Rection;
import eki.eve.data.Word;
import eki.eve.data.WordDetails;
import eki.eve.service.db.SearchDbService;

@Service
public class SearchService {

	@Autowired
	private SearchDbService searchDbService;

	public Map<String, String> getDatasets() {
		return searchDbService.getDatasetNameMap();
	}

	public List<Word> findWords(String searchFilter) {
		return searchDbService.findWords(searchFilter).into(Word.class);
	}

	public WordDetails findWordDetails(Long formId) {

		Map<String, String> datasetNameMap = searchDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = searchDbService.findFormMeanings(formId).into(WordLexeme.class);
		List<Form> connectedForms = searchDbService.findConnectedForms(formId).into(Form.class);

		lexemes.forEach(lexeme -> {
			List<String> datasets = lexeme.getDatasets();
			datasets = convertToNames(datasets, datasetNameMap);
			lexeme.setDatasets(datasets);

			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Definition> definitions = searchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			lexeme.setDefinitions(definitions);

			List<Form> words = searchDbService.findConnectedWords(meaningId).into(Form.class);
			lexeme.setWords(words);

			List<Classifier> domains = searchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			lexeme.setDomains(domains);

			List<FreeForm> meaningFreeforms = searchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			lexeme.setMeaningFreeforms(meaningFreeforms);

			List<FreeForm> lexemeFreeforms = searchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			lexeme.setLexemeFreeforms(lexemeFreeforms);

			List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples =
					searchDbService.findRectionUsageTranslationDefinitionTuples(lexemeId).into(RectionUsageTranslationDefinitionTuple.class);

			List<Rection> rections = composeRections(rectionUsageTranslationDefinitionTuples);
			lexeme.setRections(rections);

		});
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setLexemes(lexemes);
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