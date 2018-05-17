package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.Collocation;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationRelGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.Government;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeWord;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.UsageMeaning;
import eki.wordweb.data.Word;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ConversionUtil {

	@Autowired
	private CommonDataDbService commonDataDbService;

	public void filterLanguageValues(List<Word> words, final String destinLang) {
		for (Word word : words) {
			List<TypeWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				List<String> meaningWordValues = meaningWords.stream()
						.filter(meaningWord -> StringUtils.equals(meaningWord.getLang(), destinLang))
						.map(meaningWord -> meaningWord.getValue())
						.collect(Collectors.toList());
				String meaningWordsWrapup = StringUtils.join(meaningWordValues, ", ");
				word.setMeaningWordsWrapup(meaningWordsWrapup);
			}
			List<TypeDefinition> definitions = word.getDefinitions();
			if (CollectionUtils.isNotEmpty(definitions)) {
				List<String> definitionValues = definitions.stream()
						.filter(definition -> StringUtils.equals(definition.getLang(), destinLang))
						.map(definition -> definition.getValue())
						.collect(Collectors.toList());
				String definitionsWrapup = StringUtils.join(definitionValues, ", ");
				word.setDefinitionsWrapup(definitionsWrapup);
			}
		}
	}

	public void selectHomonym(List<Word> words, Integer homonymNr) {

		if (homonymNr == null) {
			return;
		}
		for (Word word : words) {
			if (homonymNr.equals(word.getHomonymNr())) {
				word.setSelected(true);
				break;
			}
		}
	}

	public List<Lexeme> composeLexemes(
			List<LexemeMeaningTuple> lexemeMeaningTuples,
			List<LexemeDetailsTuple> lexemeDetailsTuples,
			List<CollocationTuple> collocTuples,
			String sourceLang, String destinLang, String displayLang) {

		List<Lexeme> lexemes = new ArrayList<>();
		Map<Long, Lexeme> lexemeMap = new HashMap<>();
		Map<Long, Government> governmentMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		Map<Long, CollocationPosGroup> collocPosGroupMap = new HashMap<>();
		Map<Long, CollocationRelGroup> collocRelGroupMap = new HashMap<>();
		List<Long> meaningWordIds = null;

		for (LexemeMeaningTuple tuple : lexemeMeaningTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			if (lexeme == null) {
				lexeme = populateLexemeMeaning(lexemeId, tuple, displayLang);
				lexemeMap.put(lexemeId, lexeme);
				lexemes.add(lexeme);
				meaningWordIds = new ArrayList<>();
			}
			populateMeaningWord(lexeme, tuple, meaningWordIds, sourceLang, destinLang);
		}

		for (LexemeDetailsTuple tuple : lexemeDetailsTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			lexeme.setAdviceNotes(tuple.getAdviceNotes());
			lexeme.setPublicNotes(tuple.getPublicNotes());
			lexeme.setGrammars(tuple.getGrammars());

			Government government = populateGovernment(lexeme, tuple, governmentMap);
			populateUsageMeaning(government, tuple, usageMeaningMap, displayLang);
			populateRelatedLexemes(lexeme, tuple, displayLang);
			populateRelatedMeanings(lexeme, tuple, displayLang);
		}

		for (CollocationTuple tuple : collocTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);

			CollocationPosGroup collocPosGroup = populateCollocPosGroup(lexeme, tuple, collocPosGroupMap);
			CollocationRelGroup collocRelGroup = populateCollocRelGroup(collocPosGroup, tuple, collocRelGroupMap);
			Collocation collocation = populateCollocation(tuple);

			if (collocPosGroup == null) {
				lexeme.getSecondaryCollocations().add(collocation);
			} else {
				collocRelGroup.getCollocations().add(collocation);
			}
		}
		return lexemes;
	}

	private Lexeme populateLexemeMeaning(Long lexemeId, LexemeMeaningTuple tuple, String displayLang) {
		List<Classifier> classifiers;
		List<String> classifierCodes;
		Lexeme lexeme = new Lexeme();
		lexeme.setLexemeId(lexemeId);
		lexeme.setMeaningId(tuple.getMeaningId());
		lexeme.setDatasetCode(tuple.getDatasetCode());
		lexeme.setLevel1(tuple.getLevel1());
		lexeme.setLevel2(tuple.getLevel2());
		lexeme.setLevel3(tuple.getLevel3());
		String datasetCode = tuple.getDatasetCode();
		String datasetName = getDatasetName(datasetCode, displayLang);
		lexeme.setDatasetName(datasetName);
		classifierCodes = tuple.getRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		lexeme.setRegisters(classifiers);
		classifierCodes = tuple.getPosCodes();
		classifiers = getClassifiers(ClassifierName.POS, classifierCodes, displayLang);
		lexeme.setPoses(classifiers);
		classifierCodes = tuple.getDerivCodes();
		classifiers = getClassifiers(ClassifierName.DERIV, classifierCodes, displayLang);
		lexeme.setDerivs(classifiers);
		List<TypeDomain> domainCodes = tuple.getDomainCodes();
		classifiers = getClassifiersWithOrigin(ClassifierName.DOMAIN, domainCodes, displayLang);
		lexeme.setDomains(classifiers);
		lexeme.setImageFiles(tuple.getImageFiles());
		lexeme.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
		lexeme.setSemanticTypes(tuple.getSemanticTypes());
		lexeme.setLearnerComments(tuple.getLearnerComments());
		lexeme.setDefinitions(tuple.getDefinitions());
		lexeme.setSynonymWords(new ArrayList<>());
		lexeme.setDestinLangMatchWords(new ArrayList<>());
		lexeme.setOtherLangMatchWords(new ArrayList<>());
		lexeme.setGovernments(new ArrayList<>());
		lexeme.setCollocationPosGroups(new ArrayList<>());
		lexeme.setSecondaryCollocations(new ArrayList<>());
		return lexeme;
	}

	private void populateMeaningWord(Lexeme lexeme, LexemeMeaningTuple tuple, List<Long> meaningWordIds, String sourceLang, String destinLang) {
		Long meaningWordId = tuple.getMeaningWordId();
		if ((meaningWordId != null) && !meaningWordIds.contains(meaningWordId)) {
			Word meaningWord = new Word();
			meaningWord.setWordId(meaningWordId);
			meaningWord.setWord(tuple.getMeaningWord());
			meaningWord.setHomonymNr(tuple.getMeaningWordHomonymNr());
			meaningWord.setLang(tuple.getMeaningWordLang());
			if (StringUtils.equals(tuple.getMeaningWordLang(), sourceLang)) {
				lexeme.getSynonymWords().add(meaningWord);
			} else if (StringUtils.equals(tuple.getMeaningWordLang(), destinLang)) {
				lexeme.getDestinLangMatchWords().add(meaningWord);
			} else {
				lexeme.getOtherLangMatchWords().add(meaningWord);
			}
			meaningWordIds.add(meaningWordId);
		}
	}

	private Government populateGovernment(Lexeme lexeme, LexemeDetailsTuple tuple, Map<Long, Government> governmentMap) {
		Long governmentId = tuple.getGovernmentId();
		Government government = governmentMap.get(governmentId);
		if (government == null) {
			government = new Government();
			government.setGovernmentId(governmentId);
			government.setGovernment(tuple.getGovernment());
			government.setUsageMeanings(new ArrayList<>());
			governmentMap.put(governmentId, government);
			lexeme.getGovernments().add(government);
		}
		return government;
	}

	private void populateUsageMeaning(Government government, LexemeDetailsTuple tuple, Map<Long, UsageMeaning> usageMeaningMap, String displayLang) {
		String classifierCode;
		Classifier classifier;
		Long usageMeaningId = tuple.getUsageMeaningId();
		UsageMeaning usageMeaning = usageMeaningMap.get(usageMeaningId);
		if (usageMeaning == null) {
			usageMeaning = new UsageMeaning();
			usageMeaning.setUsageMeaningId(usageMeaningId);
			classifierCode = tuple.getUsageMeaningTypeCode();
			classifier = getClassifier(ClassifierName.USAGE_TYPE, classifierCode, displayLang);
			usageMeaning.setUsageMeaningType(classifier);
			usageMeaning.setUsages(tuple.getUsages());
			usageMeaning.setUsageTranslations(tuple.getUsageTranslations());
			usageMeaning.setUsageDefinitions(tuple.getUsageDefinitions());
			usageMeaningMap.put(usageMeaningId, usageMeaning);
			government.getUsageMeanings().add(usageMeaning);
		}
	}

	private void populateRelatedLexemes(Lexeme lexeme, LexemeDetailsTuple tuple, String displayLang) {
		if (CollectionUtils.isNotEmpty(lexeme.getRelatedLexemes())) {
			return;
		}
		String classifierCode;
		Classifier classifier;
		List<TypeLexemeRelation> relatedLexemes = tuple.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			for (TypeLexemeRelation lexemeRelation : relatedLexemes) {
				classifierCode = lexemeRelation.getLexRelTypeCode();
				classifier = getClassifier(ClassifierName.LEX_REL_TYPE, classifierCode, displayLang);
				lexemeRelation.setLexRelType(classifier);
			}
		}
		lexeme.setRelatedLexemes(relatedLexemes);
	}

	private void populateRelatedMeanings(Lexeme lexeme, LexemeDetailsTuple tuple, String displayLang) {
		if (CollectionUtils.isNotEmpty(lexeme.getRelatedMeanings())) {
			return;
		}
		String classifierCode;
		Classifier classifier;
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			for (TypeMeaningRelation meaningRelation : relatedMeanings) {
				classifierCode = meaningRelation.getMeaningRelTypeCode();
				classifier = getClassifier(ClassifierName.MEANING_REL_TYPE, classifierCode, displayLang);
				meaningRelation.setMeaningRelType(classifier);
			}
		}
		lexeme.setRelatedMeanings(relatedMeanings);
	}

	public List<Paradigm> composeParadigms(Map<Long, List<Form>> paradigmFormsMap, String displayLang) {

		List<Paradigm> paradigms = new ArrayList<>();
		String classifierCode;
		Classifier classifier;
		for (Entry<Long, List<Form>> paradigmFormsEntry : paradigmFormsMap.entrySet()) {
			Long paradigmId = paradigmFormsEntry.getKey();
			List<Form> forms = paradigmFormsEntry.getValue();
			for (Form form : forms) {
				classifierCode = form.getMorphCode();
				classifier = getClassifier(ClassifierName.MORPH, classifierCode, displayLang);
				form.setMorph(classifier);
			}
			Paradigm paradigm = new Paradigm();
			paradigm.setParadigmId(paradigmId);
			paradigm.setForms(forms);
			paradigms.add(paradigm);
		}
		paradigms.sort(Comparator.comparing(Paradigm::getParadigmId));
		return paradigms;
	}

	private CollocationPosGroup populateCollocPosGroup(Lexeme lexeme, CollocationTuple tuple, Map<Long, CollocationPosGroup> collocPosGroupMap) {
		CollocationPosGroup collocPosGroup = null;
		Long posGroupId = tuple.getPosGroupId();
		if (posGroupId != null) {
			collocPosGroup = collocPosGroupMap.get(posGroupId);
			if (collocPosGroup == null) {
				collocPosGroup = new CollocationPosGroup();
				collocPosGroup.setName(tuple.getPosGroupName());
				collocPosGroup.setRelationGroups(new ArrayList<>());
				collocPosGroupMap.put(posGroupId, collocPosGroup);
				lexeme.getCollocationPosGroups().add(collocPosGroup);
			}
		}
		return collocPosGroup;
	}

	private CollocationRelGroup populateCollocRelGroup(CollocationPosGroup collocPosGroup, CollocationTuple tuple, Map<Long, CollocationRelGroup> collocRelGroupMap) {
		CollocationRelGroup collocRelGroup = null;
		Long relGroupId = tuple.getRelGroupId();
		if (relGroupId != null) {
			collocRelGroup = collocRelGroupMap.get(relGroupId);
			if (collocRelGroup == null) {
				collocRelGroup = new CollocationRelGroup();
				collocRelGroup.setName(tuple.getRelGroupName());
				collocRelGroup.setCollocations(new ArrayList<>());
				collocRelGroupMap.put(relGroupId, collocRelGroup);
				collocPosGroup.getRelationGroups().add(collocRelGroup);
			}
		}
		return collocRelGroup;
	}

	private Collocation populateCollocation(CollocationTuple tuple) {
		Collocation collocation = new Collocation();
		collocation.setValue(tuple.getCollocValue());
		collocation.setDefinition(tuple.getCollocDefinition());
		collocation.setCollocUsages(tuple.getCollocUsages());
		collocation.setCollocMembers(tuple.getCollocMembers());
		return collocation;
	}

	public void populateWordRelationClassifiers(Word word, String displayLang) {

		if (CollectionUtils.isEmpty(word.getRelatedWords())) {
			return;
		}
		String classifierCode;
		Classifier classifier;
		for (TypeWordRelation wordRelation : word.getRelatedWords()) {
			classifierCode = wordRelation.getWordRelTypeCode();
			classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
			wordRelation.setWordRelType(classifier);
		}
	}

	private String getDatasetName(String code, String lang) {
		String name = commonDataDbService.getDatasetName(code, lang);
		if (StringUtils.isBlank(name)) {
			//TODO try with default lang first, then...
			//fallback to code as value
			name = code;
		}
		return name;
	}

	private Classifier getClassifier(ClassifierName name, String code, String lang) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		Classifier classifier = commonDataDbService.getClassifier(name, code, lang);
		if (classifier == null) {
			//TODO try with default lang first, then...
			//fallback to code as value
			classifier = new Classifier(name.name(), null, null, code, code, lang);
		}
		return classifier;
	}

	private List<Classifier> getClassifiers(ClassifierName name, List<String> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getClassifiers(name, codes, lang);
		if (CollectionUtils.isEmpty(classifiers) || (classifiers.size() != codes.size())) {
			//TODO try with default lang first, then...
			//fallback to code as value
			classifiers = new ArrayList<>();
			for (String code : codes) {
				Classifier classifier = new Classifier(name.name(), null, null, code, code, lang);
				classifiers.add(classifier);
			}
		}
		return classifiers;
	}

	private List<Classifier> getClassifiersWithOrigin(ClassifierName name, List<TypeDomain> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getClassifiersWithOrigin(name, codes, lang);
		if (CollectionUtils.isEmpty(classifiers) || (classifiers.size() != codes.size())) {
			//TODO try with default lang first, then...
			//fallback to code as value
			classifiers = new ArrayList<>();
			for (TypeDomain code : codes) {
				Classifier classifier = new Classifier(name.name(), code.getOrigin(), null, code.getCode(), code.getCode(), lang);
				classifiers.add(classifier);
			}
		}
		return classifiers;
	}

}
