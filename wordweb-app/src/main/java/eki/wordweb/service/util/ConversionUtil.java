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
import eki.wordweb.data.Form;
import eki.wordweb.data.Government;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.Relation;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.TypeWord;
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

	public List<Lexeme> composeLexemes(
			List<LexemeMeaningTuple> lexemeMeaningTuples, List<LexemeDetailsTuple> lexemeDetailsTuples,
			String sourceLang, String destinLang, String displayLang) {

		List<Lexeme> lexemes = new ArrayList<>();
		Map<Long, Lexeme> lexemeMap = new HashMap<>();
		Map<Long, Government> governmentMap = new HashMap<>();
		Map<Long, UsageMeaning> usageMeaningMap = new HashMap<>();
		List<Long> meaningWordIds = null;
		List<Classifier> classifiers;
		List<String> classifierCodes;
		String classifierCode, datasetCode, datasetName;
		List<TypeDomain> domainCodes;
		Classifier classifier;

		for (LexemeMeaningTuple tuple : lexemeMeaningTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			if (lexeme == null) {
				lexeme = new Lexeme();
				lexeme.setLexemeId(lexemeId);
				lexeme.setMeaningId(tuple.getMeaningId());
				lexeme.setDatasetCode(tuple.getDatasetCode());
				lexeme.setLevel1(tuple.getLevel1());
				lexeme.setLevel2(tuple.getLevel2());
				lexeme.setLevel3(tuple.getLevel3());
				datasetCode = tuple.getDatasetCode();
				datasetName = getDatasetName(datasetCode, displayLang);
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
				domainCodes = tuple.getDomainCodes();
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
				lexemeMap.put(lexemeId, lexeme);
				lexemes.add(lexeme);
				meaningWordIds = new ArrayList<>();
			}

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

		for (LexemeDetailsTuple tuple : lexemeDetailsTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			lexeme.setAdviceNotes(tuple.getAdviceNotes());
			lexeme.setPublicNotes(tuple.getPublicNotes());
			lexeme.setGrammars(tuple.getGrammars());

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
		return lexemes;
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

	public void composeRelations(List<Relation> relations, ClassifierName relationType, String lang) {
		relations.forEach(relation -> {
			Classifier relationTypeClassifier = getClassifier(relationType, relation.getRelationTypeCode(), lang);
			relation.setRelationType(relationTypeClassifier);
		});
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
