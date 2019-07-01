package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.service.db.LexSearchDbService;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Transactional
	public WordSynDetails getWordSynDetails(Long wordId, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);

		List<WordSynLexeme> synLexemes = synSearchDbService.getWordSynLexemes(wordId, searchDatasetsRestriction);
		synLexemes.forEach(lexeme -> populateSynLexeme(lexeme, searchDatasetsRestriction));
		lexemeLevelCalcUtil.combineLevels(synLexemes);

		List<SynRelationParamTuple> relationTuples =
				synSearchDbService.getWordSynRelations(wordId, RAW_RELATION_CODE, classifierLabelLang, classifierLabelTypeDescrip);
		List<SynRelation> relations = conversionUtil.composeSynRelations(relationTuples);

		WordSynDetails wordDetails = new WordSynDetails();
		wordDetails.setLexemes(synLexemes);
		wordDetails.setRelations(relations);

		return wordDetails;
	}

	private void populateSynLexeme(WordSynLexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction) {

		Long lexemeId = lexeme.getLexemeId();
		Long wordId = lexeme.getWordId();
		Long meaningId = lexeme.getMeaningId();

		List<Word> meaningWords = lexSearchDbService.getMeaningWords(wordId, meaningId, searchDatasetsRestriction);
		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Definition> definitions = conversionUtil.composeMeaningDefinitions(definitionRefTuples);

		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);

		lexeme.setPos(lexemePos);
		lexeme.setMeaningWords(meaningWords);
		lexeme.setDefinitions(definitions);
		lexeme.setUsages(usages);

	}

	@Transactional
	public void changeRelationStatus(Long id, String status) {
		synSearchDbService.changeRelationStatus(id, status);
	}
}
