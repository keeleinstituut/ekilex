package eki.ekilex.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierSelect;
import eki.ekilex.data.DefSourceAndNoteSourceTuple;
import eki.ekilex.data.Definition;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.InexactSynonym;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningRelation;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.SearchLangsRestriction;
import eki.ekilex.data.SynonymLangGroup;
import eki.ekilex.data.Usage;
import eki.ekilex.data.UsageTranslationDefinitionTuple;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.SynSearchDbService;
import eki.ekilex.service.util.PermCalculator;

@Component
public abstract class AbstractSynSearchService extends AbstractWordSearchService {

	@Autowired
	protected SynSearchDbService synSearchDbService;

	@Autowired
	protected PermCalculator permCalculator;

	protected void populateLexeme(
			WordLexeme lexeme,
			List<ClassifierSelect> languagesOrder,
			String headwordLanguage,
			List<String> meaningWordLangs,
			EkiUser user,
			EkiUserProfile userProfile) {

		Long lexemeId = lexeme.getLexemeId();
		Long meaningId = lexeme.getMeaningId();
		String datasetCode = lexeme.getDatasetCode();
		SearchLangsRestriction meaningWordLangsRestriction = composeLangsRestriction(meaningWordLangs);

		permCalculator.applyCrud(user, lexeme);

		List<MeaningRelation> synMeaningRelations = commonDataDbService.getSynMeaningRelations(meaningId, datasetCode);
		appendLexemeLevels(synMeaningRelations);
		List<MeaningWord> meaningWords = commonDataDbService.getMeaningWords(lexemeId, meaningWordLangsRestriction);
		List<InexactSynonym> inexactSynonyms = lookupDbService.getMeaningInexactSynonyms(meaningId, headwordLanguage, datasetCode);
		List<SynonymLangGroup> synonymLangGroups = conversionUtil.composeSynonymLangGroups(synMeaningRelations, meaningWords, inexactSynonyms, userProfile, headwordLanguage, languagesOrder);

		List<Definition> definitions = commonDataDbService.getMeaningDefinitions(meaningId, datasetCode, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		List<DefSourceAndNoteSourceTuple> definitionsDataTuples = commonDataDbService.getMeaningDefSourceAndNoteSourceTuples(meaningId);
		conversionUtil.composeMeaningDefinitions(definitions, definitionsDataTuples);
		permCalculator.filterVisibility(user, definitions);

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
