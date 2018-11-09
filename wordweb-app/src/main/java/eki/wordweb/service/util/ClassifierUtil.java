package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.MeaningWord;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeUsage;
import eki.wordweb.data.TypeWordEtym;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ClassifierUtil {

	@Autowired
	private CommonDataDbService commonDataDbService;

	public void applyClassifiers(Word word, String displayLang) {

		String classifierCode;
		Classifier classifier;
		classifierCode = word.getTypeCode();
		classifier = getClassifier(ClassifierName.WORD_TYPE, classifierCode, displayLang);
		word.setType(classifier);
		classifierCode = word.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT_TYPE, classifierCode, displayLang);
		word.setAspect(classifier);
	}

	public String applyClassifiers(Form form, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = form.getMorphCode();
		classifier = getClassifier(ClassifierName.MORPH, classifierCode, displayLang);
		form.setMorph(classifier);
		return classifierCode;
	}

	public void applyClassifiers(LexemeDetailsTuple tuple, Lexeme lexeme, String displayLang) {
		List<Classifier> classifiers;
		List<String> classifierCodes;
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
	}


	public void applyClassifiers(LexemeDetailsTuple tuple, MeaningWord meaningWord, String displayLang) {
		String classifierCode;
		List<String> classifierCodes;
		Classifier classifier;
		List<Classifier> classifiers;
		classifierCode = tuple.getMeaningWordAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT_TYPE, classifierCode, displayLang);
		meaningWord.setAspect(classifier);
		classifierCodes = tuple.getMeaningLexemeRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		meaningWord.setRegisters(classifiers);
	}

	public void applyClassifiers(LexemeMeaningTuple tuple, Lexeme lexeme, String displayLang) {
		List<Classifier> classifiers;
		List<TypeDomain> domainCodes = tuple.getDomainCodes();
		classifiers = getClassifiersWithOrigin(ClassifierName.DOMAIN, domainCodes, displayLang);
		lexeme.setDomains(classifiers);
	}

	public void applyClassifiers(TypeUsage usage, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = usage.getUsageTypeCode();
		classifier = getClassifier(ClassifierName.USAGE_TYPE, classifierCode, displayLang);
		usage.setUsageType(classifier);
	}

	public void applyClassifiers(TypeLexemeRelation lexemeRelation, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = lexemeRelation.getLexRelTypeCode();
		classifier = getClassifier(ClassifierName.LEX_REL_TYPE, classifierCode, displayLang);
		lexemeRelation.setLexRelType(classifier);
	}

	public void applyClassifiers(TypeMeaningRelation meaningRelation, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = meaningRelation.getMeaningRelTypeCode();
		classifier = getClassifier(ClassifierName.MEANING_REL_TYPE, classifierCode, displayLang);
		meaningRelation.setMeaningRelType(classifier);
	}

	public void applyClassifiers(WordRelationTuple tuple, WordGroup wordGroup, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = tuple.getWordRelTypeCode();
		classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
		wordGroup.setWordRelType(classifier);
	}

	public void applyClassifiers(TypeWordRelation wordRelation, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = wordRelation.getWordRelTypeCode();
		classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
		wordRelation.setWordRelType(classifier);
	}

	public void applyClassifiers(TypeWordEtym wordEtym, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = wordEtym.getEtymWordLang();
		classifier = getClassifier(ClassifierName.LANGUAGE, classifierCode, displayLang);
		wordEtym.setEtymWordLanguage(classifier);
		//TODO there are no etym type labels yet
		classifierCode = wordEtym.getEtymTypeCode();
		//...
	}

	public void applyClassifiers(CollocationTuple tuple, CollocationPosGroup collocPosGroup, String displayLang) {
		String classifierCode = tuple.getPosGroupCode();
		Classifier classifier = getClassifier(ClassifierName.POS_GROUP, classifierCode, displayLang);
		collocPosGroup.setPosGroup(classifier);
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
