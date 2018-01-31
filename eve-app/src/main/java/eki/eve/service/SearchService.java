package eki.eve.service;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.common.data.Classifier;
import eki.eve.data.Definition;
import eki.eve.data.Form;
import eki.eve.data.FreeForm;
import eki.eve.data.Government;
import eki.eve.data.GovernmentUsageTranslationDefinitionTuple;
import eki.eve.data.UsageMeaning;
import eki.eve.data.UsageMember;
import eki.eve.data.Word;
import eki.eve.data.WordDetails;
import eki.eve.data.WordLexeme;
import eki.eve.service.db.SearchDbService;

@Service
public class SearchService implements InitializingBean {

	@Autowired
	private SearchDbService searchDbService;

	private List<String> supportedDatasets;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.supportedDatasets = asList("qq2", "psv");
	}

	public List<Word> findWords(String searchFilter) {
		if (StringUtils.isBlank(searchFilter)) {
			return new ArrayList<>();
		}
		return searchDbService.findWords(searchFilter, supportedDatasets).into(Word.class);
	}

	public WordDetails findWordDetails(Long formId) {

		Map<String, String> datasetNameMap = searchDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = searchDbService.findFormMeanings(formId, supportedDatasets).into(WordLexeme.class);
		List<Form> connectedForms = searchDbService.findConnectedForms(formId).into(Form.class);

		lexemes.forEach(lexeme -> {

			String dataset = lexeme.getDataset();
			dataset = datasetNameMap.get(dataset);
			lexeme.setDataset(dataset);

			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Definition> definitions = searchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			lexeme.setDefinitions(definitions);

			List<Form> words = searchDbService.findConnectedWords(meaningId, supportedDatasets).into(Form.class);
			lexeme.setWords(words);

			List<Classifier> domains = searchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			lexeme.setDomains(domains);

			List<FreeForm> meaningFreeforms = searchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			lexeme.setMeaningFreeforms(meaningFreeforms);

			List<FreeForm> lexemeFreeforms = searchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			lexeme.setLexemeFreeforms(lexemeFreeforms);

			List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples =
					searchDbService.findGovernmentUsageTranslationDefinitionTuples(lexemeId).into(GovernmentUsageTranslationDefinitionTuple.class);

			List<Government> governments = composeGovernments(governmentUsageTranslationDefinitionTuples);
			lexeme.setGovernments(governments);

		});
		return new WordDetails(d -> {
			d.setForms(connectedForms);
			d.setLexemes(lexemes);
		});
	}

	private List<Government> composeGovernments(List<GovernmentUsageTranslationDefinitionTuple> governmentUsageTranslationDefinitionTuples) {

		List<Government> governments = new ArrayList<>();

		Map<Long, Government> governmentMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		Map<Long, UsageMember> usageMap = new HashMap<>();
		Map<Long, UsageMember> usageTranslationMap = new HashMap<>();
		Map<Long, UsageMember> usageDefinitionMap = new HashMap<>();

		for (GovernmentUsageTranslationDefinitionTuple tuple : governmentUsageTranslationDefinitionTuples) {

			Long governmentId = tuple.getGovernmentId();
			Long usageMeaningId = tuple.getUsageMeaningId();
			Long usageId = tuple.getUsageId();
			Long usageTranslationId = tuple.getUsageTranslationId();
			Long usageDefinitionId = tuple.getUsageDefinitionId();

			Government government = governmentMap.get(governmentId);
			if (government == null) {
				government = new Government();
				government.setId(governmentId);
				government.setValue(tuple.getGovernmentValue());
				government.setUsageMeanings(new ArrayList<>());
				governmentMap.put(governmentId, government);
				governments.add(government);
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
				government.getUsageMeanings().add(usageMeaning);
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
		return governments;
	}
}