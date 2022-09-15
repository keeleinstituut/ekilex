package eki.ekilex.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.NoteSourceTuple;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Tag;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordDetails;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordNote;
import eki.ekilex.data.WordRelationDetails;
import eki.ekilex.service.db.SynSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public class SynSearchService extends AbstractWordSearchService {

	private static final String RAW_RELATION_CODE = "raw";

	@Value("#{${relation.weight.multipliers}}")
	private Map<String, Float> relationWeightMultiplierMap;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private PermCalculator permCalculator;

	@Transactional
	public WordDetails getWordSynDetails(Long wordId, List<ClassifierSelect> languagesOrder, List<String> synCandidateLangCodes,
			List<String> synMeaningWordLangCodes, Tag activeTag, DatasetPermission userRole, EkiUserProfile userProfile) throws Exception {

		String datasetCode = userRole.getDatasetCode();
		List<String> datasetCodeList = new ArrayList<>(Collections.singletonList(datasetCode));
		SearchDatasetsRestriction searchDatasetsRestriction = composeDatasetsRestriction(datasetCodeList);
		Word word = synSearchDbService.getWord(wordId);
		permCalculator.applyCrud(userRole, word);
		List<NoteSourceTuple> wordNoteSourceTuples = commonDataDbService.getWordNoteSourceTuples(wordId);
		List<WordNote> wordNotes = conversionUtil.composeNotes(WordNote.class, wordId, wordNoteSourceTuples);
		permCalculator.filterVisibility(userRole, wordNotes);
		String wordLang = word.getLang();

		List<WordLexeme> synLexemes = synSearchDbService.getWordPrimarySynonymLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		synLexemes.forEach(lexeme -> populateLexeme(lexeme, languagesOrder, wordLang, synMeaningWordLangCodes, userRole, userProfile));
		lexemeLevelPreseUtil.combineLevels(synLexemes);
		boolean isActiveTagComplete = conversionUtil.isLexemesActiveTagComplete(synLexemes, activeTag);

		List<SynRelation> synRelations = Collections.emptyList();
		if (CollectionUtils.isNotEmpty(synCandidateLangCodes)) {
			synRelations = synSearchDbService.getWordSynRelations(wordId, RAW_RELATION_CODE, datasetCode, synCandidateLangCodes, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		}
		WordRelationDetails wordRelationDetails = new WordRelationDetails();
		wordRelationDetails.setWordSynRelations(synRelations);

		WordDetails wordDetails = new WordDetails();
		word.setNotes(wordNotes);
		wordDetails.setWord(word);
		wordDetails.setLexemes(synLexemes);
		wordDetails.setWordRelationDetails(wordRelationDetails);
		wordDetails.setActiveTagComplete(isActiveTagComplete);

		return wordDetails;
	}

	private void populateLexeme(
			WordLexeme lexeme, List<ClassifierSelect> languagesOrder, String headwordLanguage, List<String> meaningWordLangs, DatasetPermission userRole,
			EkiUserProfile userProfile) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		SearchLangsRestriction meaningWordLangsRestriction = composeLangsRestriction(meaningWordLangs);

		permCalculator.applyCrud(userRole, lexeme);

		List<MeaningRelation> synMeaningRelations = commonDataDbService.getSynMeaningRelations(meaningId, datasetCode);
		appendLexemeLevels(synMeaningRelations);
		List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId, meaningWordLangsRestriction);
		List<SynonymLangGroup> synonymLangGroups = conversionUtil.composeSynonymLangGroups(synMeaningRelations, meaningWords, userProfile, headwordLanguage, languagesOrder);

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		permCalculator.filterVisibility(userRole, definitions);

		List<UsageTranslationDefinitionTuple> usageTranslationDefinitionTuples =
				commonDataDbService.getLexemeUsageTranslationDefinitionTuples(lexemeId, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<Usage> usages = conversionUtil.composeUsages(usageTranslationDefinitionTuples);
		usages = usages.stream().filter(Usage::isPublic).collect(Collectors.toList());

		List<String> lexemeTags = commonDataDbService.getLexemeTags(lexemeId);

		lexeme.setWordLang(headwordLanguage);
		lexeme.setSynonymLangGroups(synonymLangGroups);
		lexeme.setUsages(usages);
		lexeme.setTags(lexemeTags);
		Meaning meaning = new Meaning();
		meaning.setMeaningId(meaningId);
		meaning.setDefinitions(definitions);
		lexeme.setMeaning(meaning);
	}
}
