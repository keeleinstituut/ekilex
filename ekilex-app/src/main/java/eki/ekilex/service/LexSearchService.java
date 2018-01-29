package eki.ekilex.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.FormRelation;
import eki.ekilex.data.FreeForm;
import eki.ekilex.data.Paradigm;
import eki.ekilex.data.ParadigmFormTuple;
import eki.ekilex.data.Rection;
import eki.ekilex.data.RectionUsageTranslationDefinitionTuple;
import eki.ekilex.data.Relation;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.CommonDataDbService;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.ConversionUtil;

@Service
public class LexSearchService {

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Transactional
	public WordDetails getWordDetails(Long wordId, List<String> selectedDatasets) {

		final String classifierLabelLang = "est";
		final String classifierLabelTypeDescrip = "descrip";
		final String classifierLabelTypeFull = "full";

		Map<String, String> datasetNameMap = commonDataDbService.getDatasetNameMap();
		List<WordLexeme> lexemes = lexSearchDbService.findFormMeanings(wordId, selectedDatasets).into(WordLexeme.class);
		List<ParadigmFormTuple> paradigmFormTuples = lexSearchDbService.findParadigmFormTuples(wordId, classifierLabelLang, classifierLabelTypeDescrip).into(ParadigmFormTuple.class);
		List<FormRelation> wordFormRelations = lexSearchDbService.findWordFormRelations(wordId, classifierLabelLang, classifierLabelTypeFull).into(FormRelation.class);
		List<Paradigm> paradigms = conversionUtil.composeParadigms(paradigmFormTuples, wordFormRelations);

		lexemes.forEach(lexeme -> {

			String datasetCode = lexeme.getDataset();
			String datasetName = datasetNameMap.get(datasetCode);
			lexeme.setDataset(datasetName);

			Long lexemeId = lexeme.getLexemeId();
			Long meaningId = lexeme.getMeaningId();

			List<Word> meaningWords = lexSearchDbService.findMeaningWords(wordId, meaningId, selectedDatasets).into(Word.class);
			List<Classifier> lexemePos = lexSearchDbService.findLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<Classifier> lexemeDerivs = lexSearchDbService.findLexemeDerivs(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<Classifier> lexemeRegisters = lexSearchDbService.findLexemeRegisters(lexemeId, classifierLabelLang, classifierLabelTypeDescrip).into(Classifier.class);
			List<Classifier> meaningDomains = lexSearchDbService.findMeaningDomains(meaningId).into(Classifier.class);
			List<Definition> meaningDefinitions = lexSearchDbService.findMeaningDefinitions(meaningId).into(Definition.class);
			List<FreeForm> meaningFreeforms = lexSearchDbService.findMeaningFreeforms(meaningId).into(FreeForm.class);
			List<FreeForm> lexemeFreeforms = lexSearchDbService.findLexemeFreeforms(lexemeId).into(FreeForm.class);
			List<RectionUsageTranslationDefinitionTuple> rectionUsageTranslationDefinitionTuples =
					lexSearchDbService.findRectionUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip)
							.into(RectionUsageTranslationDefinitionTuple.class);
			List<Rection> rections = conversionUtil.composeRections(rectionUsageTranslationDefinitionTuples);
			List<Relation> lexemeRelations = lexSearchDbService.findLexemeRelations(lexemeId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
			List<Relation> wordRelations = lexSearchDbService.findWordRelations(wordId, classifierLabelLang, classifierLabelTypeFull).into(Relation.class);
			List<Relation> meaningRelations = lexSearchDbService.findMeaningRelations(meaningId, classifierLabelLang, classifierLabelTypeDescrip).into(Relation.class);

			lexeme.setLexemePos(lexemePos);
			lexeme.setLexemeDerivs(lexemeDerivs);
			lexeme.setLexemeRegisters(lexemeRegisters);
			lexeme.setMeaningWords(meaningWords);
			lexeme.setMeaningDomains(meaningDomains);
			lexeme.setDefinitions(meaningDefinitions);
			lexeme.setMeaningFreeforms(meaningFreeforms);
			lexeme.setLexemeFreeforms(lexemeFreeforms);
			lexeme.setRections(rections);
			lexeme.setLexemeRelations(lexemeRelations);
			lexeme.setWordRelations(wordRelations);
			lexeme.setMeaningRelations(meaningRelations);

			boolean lexemeOrMeaningClassifiersExist =
					StringUtils.isNotBlank(lexeme.getLexemeTypeCode())
					|| StringUtils.isNotBlank(lexeme.getLexemeFrequencyGroupCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningTypeCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningProcessStateCode())
					|| StringUtils.isNotBlank(lexeme.getMeaningStateCode())
					|| CollectionUtils.isNotEmpty(lexemePos)
					|| CollectionUtils.isNotEmpty(lexemeDerivs)
					|| CollectionUtils.isNotEmpty(lexemeRegisters)
					|| CollectionUtils.isNotEmpty(meaningDomains);
			lexeme.setLexemeOrMeaningClassifiersExist(lexemeOrMeaningClassifiersExist);
		});
		combineLevels(lexemes);
		return new WordDetails(d -> {
			d.setParadigms(paradigms);
			d.setLexemes(lexemes);
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