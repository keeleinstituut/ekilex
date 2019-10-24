package eki.ekilex.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.LexemeType;
import eki.common.constant.RelationStatus;
import eki.common.service.util.LexemeLevelPreseUtil;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionRefTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynMeaningWord;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private LexemeLevelPreseUtil lexemeLevelPreseUtil;

	@Transactional
	public WordSynDetails getWordSynDetails(Long wordId, List<String> selectedDatasetCodes) {

		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(selectedDatasetCodes);
		WordSynDetails wordDetails = synSearchDbService.getWordDetails(wordId);

		List<WordSynLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction);
		synLexemes.forEach(lexeme -> populateSynLexeme(lexeme, wordDetails.getLanguage()));
		lexemeLevelPreseUtil.combineLevels(synLexemes);

		List<SynRelationParamTuple> relationTuples =
				synSearchDbService.getWordSynRelations(wordId, RAW_RELATION_CODE, classifierLabelLang, classifierLabelTypeDescrip);
		List<SynRelation> relations = conversionUtil.composeSynRelations(relationTuples);

		wordDetails.setLexemes(synLexemes);
		wordDetails.setRelations(relations);

		return wordDetails;
	}

	private void populateSynLexeme(WordSynLexeme lexeme, String language) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();

		List<SynMeaningWord> meaningWords = synSearchDbService.getSynMeaningWords(lexemeId, language);
		List<Classifier> lexemePos = commonDataDbService.getLexemePos(lexemeId, classifierLabelLang, classifierLabelTypeDescrip);
		List<DefinitionRefTuple> definitionRefTuples =
				commonDataDbService.getMeaningDefinitionRefTuples(meaningId, datasetCode, classifierLabelLang, classifierLabelTypeDescrip);
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

	@Transactional
	public void createSecondarySynLexeme(Long meaningId, Long wordId, String datasetCode, Long existingLexemeId, Long relationId) {
		synSearchDbService.createLexeme(wordId, meaningId, datasetCode, LexemeType.SECONDARY, existingLexemeId);
		synSearchDbService.changeRelationStatus(relationId, RelationStatus.PROCESSED.name());
	}
}
