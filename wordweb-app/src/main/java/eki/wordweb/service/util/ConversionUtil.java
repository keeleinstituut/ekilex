package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.Government;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.UsageMeaning;
import eki.wordweb.data.Word;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ConversionUtil {

	@Autowired
	private CommonDataDbService commonDataDbService;

	public List<Lexeme> composeLexemes(List<LexemeMeaningTuple> lexemeMeaningTuples, List<LexemeDetailsTuple> lexemeDetailsTuples, String dataLang) {

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
				datasetName = getDatasetName(datasetCode, dataLang);
				lexeme.setDatasetName(datasetName);
				classifierCodes = tuple.getRegisterCodes();
				classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, dataLang);
				lexeme.setRegisters(classifiers);
				classifierCodes = tuple.getPosCodes();
				classifiers = getClassifiers(ClassifierName.POS, classifierCodes, dataLang);
				lexeme.setPoses(classifiers);
				classifierCodes = tuple.getDerivCodes();
				classifiers = getClassifiers(ClassifierName.DERIV, classifierCodes, dataLang);
				lexeme.setDerivs(classifiers);
				domainCodes = tuple.getDomainCodes();
				classifiers = getClassifiersWithOrigin(ClassifierName.DOMAIN, domainCodes, dataLang);
				lexeme.setDomains(classifiers);
				lexeme.setImageFiles(tuple.getImageFiles());
				lexeme.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
				lexeme.setSemanticTypes(tuple.getSemanticTypes());
				lexeme.setLearnerComments(tuple.getLearnerComments());
				lexeme.setDefinitions(tuple.getDefinitions());
				lexeme.setMeaningWords(new ArrayList<>());
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
				lexeme.getMeaningWords().add(meaningWord);
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
				classifier = getClassifier(ClassifierName.USAGE_TYPE, classifierCode, dataLang);
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
