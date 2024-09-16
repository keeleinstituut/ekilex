package eki.wordweb.service.util;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.Form;
import eki.wordweb.data.LanguagesDatasets;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.Meaning;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordTypeData;
import eki.wordweb.data.type.TypeDomain;
import eki.wordweb.data.type.TypeLexemeRelation;
import eki.wordweb.data.type.TypeMeaningRelation;
import eki.wordweb.data.type.TypeMeaningWord;
import eki.wordweb.data.type.TypeWordRelation;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ClassifierUtil {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CommonDataDbService commonDataDbService;

	public Classifier reValue(Classifier classifier, String messageKey, Locale displayLocale) {
		String newValue = messageSource.getMessage(messageKey, new Object[0], displayLocale);
		Classifier copy = new Classifier(classifier.getName(), classifier.getOrigin(), classifier.getParent(), classifier.getCode(), newValue, classifier.getLang());
		return copy;
	}

	public void applyClassifiers(WordTypeData word, String displayLang) {
		String classifierCode;
		Classifier classifier;
		List<Classifier> classifiers;
		List<String> classifierCodes;
		classifierCode = word.getDisplayMorphCode();
		classifier = getClassifier(ClassifierName.DISPLAY_MORPH, classifierCode, displayLang);
		word.setDisplayMorph(classifier);
		classifierCodes = word.getWordTypeCodes();
		classifiers = getClassifiers(ClassifierName.WORD_TYPE, classifierCodes, displayLang);
		word.setWordTypes(classifiers);
		classifierCode = word.getGenderCode();
		classifier = getClassifier(ClassifierName.GENDER, classifierCode, displayLang);
		word.setGender(classifier);
		classifierCode = word.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		word.setAspect(classifier);
	}

	public void applyClassifiers(LanguagesDatasets languagesDatasets, String displayLang) {
		List<String> classifierCodes = languagesDatasets.getLanguageCodes();
		List<Classifier> classifiers = getClassifiers(ClassifierName.LANGUAGE, classifierCodes, displayLang);
		languagesDatasets.setLanguages(classifiers);
		List<String> datasetCodes = languagesDatasets.getDatasetCodes();
		List<Dataset> datasets = getDatasets(datasetCodes);
		languagesDatasets.setDatasets(datasets);
	}

	public String applyClassifiers(Form form, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = form.getMorphCode();
		classifier = getClassifier(ClassifierName.MORPH, classifierCode, displayLang);
		form.setMorph(classifier);
		return classifierCode;
	}

	public void applyClassifiers(LexemeWord lexemeWord, String displayLang) {
		String classifierCode;
		Classifier classifier;
		List<Classifier> classifiers;
		List<String> classifierCodes;
		classifierCode = lexemeWord.getValueStateCode();
		classifier = getClassifier(ClassifierName.VALUE_STATE, classifierCode, displayLang);
		lexemeWord.setValueState(classifier);
		classifierCodes = lexemeWord.getRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		lexemeWord.setRegisters(classifiers);
		classifierCodes = lexemeWord.getPosCodes();
		classifiers = getClassifiers(ClassifierName.POS, classifierCodes, displayLang);
		lexemeWord.setPoses(classifiers);
		classifierCodes = lexemeWord.getRegionCodes();
		classifiers = getClassifiers(ClassifierName.REGION, classifierCodes, displayLang);
		lexemeWord.setRegions(classifiers);
		classifierCodes = lexemeWord.getDerivCodes();
		classifiers = getClassifiers(ClassifierName.DERIV, classifierCodes, displayLang);
		lexemeWord.setDerivs(classifiers);
	}

	public void applyClassifiers(TypeMeaningWord meaningWord, String displayLang) {
		String classifierCode;
		List<String> classifierCodes;
		Classifier classifier;
		List<Classifier> classifiers;
		classifierCode = meaningWord.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		meaningWord.setAspect(classifier);
		classifierCodes = meaningWord.getWordTypeCodes();
		classifiers = getClassifiers(ClassifierName.WORD_TYPE, classifierCodes, displayLang);
		meaningWord.setWordTypes(classifiers);
		classifierCodes = meaningWord.getMwLexRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		meaningWord.setMwLexRegisters(classifiers);
		classifierCode = meaningWord.getMwLexValueStateCode();
		classifier = getClassifier(ClassifierName.VALUE_STATE, classifierCode, displayLang);
		meaningWord.setMwLexValueState(classifier);
	}

	public void applyClassifiers(Meaning tuple, LexemeWord lexemeWord, String displayLang) {
		List<Classifier> classifiers;
		List<TypeDomain> domainCodes = tuple.getDomainCodes();
		classifiers = getClassifiersWithOrigin(ClassifierName.DOMAIN, domainCodes, displayLang);
		lexemeWord.setDomains(classifiers);
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
		List<String> classifierCodes;
		Classifier classifier;
		List<Classifier> classifiers;
		classifierCode = meaningRelation.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		meaningRelation.setAspect(classifier);
		classifierCodes = meaningRelation.getLexValueStateCodes();
		classifiers = getClassifiers(ClassifierName.VALUE_STATE, classifierCodes, displayLang);
		meaningRelation.setLexValueStates(classifiers);
		classifierCodes = meaningRelation.getWordTypeCodes();
		classifiers = getClassifiers(ClassifierName.WORD_TYPE, classifierCodes, displayLang);
		meaningRelation.setWordTypes(classifiers);
		classifierCodes = meaningRelation.getLexRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		meaningRelation.setLexRegisters(classifiers);
		classifierCode = meaningRelation.getMeaningRelTypeCode();
		classifier = getClassifier(ClassifierName.MEANING_REL_TYPE, classifierCode, displayLang);
		meaningRelation.setMeaningRelType(classifier);
	}

	public void applyClassifiers(TypeWordRelation wordRelation, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = wordRelation.getWordRelTypeCode();
		classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
		wordRelation.setWordRelType(classifier);
		classifierCode = wordRelation.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		wordRelation.setAspect(classifier);
	}

	public void applyClassifiers(WordEtymTuple tuple, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = tuple.getWordEtymWordLang();
		classifier = getClassifier(ClassifierName.LANGUAGE, classifierCode, displayLang);
		tuple.setWordEtymWordLanguage(classifier);
		classifierCode = tuple.getEtymologyTypeCode();
		classifier = getClassifier(ClassifierName.ETYMOLOGY_TYPE, classifierCode, displayLang);
		tuple.setEtymologyType(classifier);
	}

	public void applyClassifiers(CollocationTuple tuple, CollocationPosGroup collocPosGroup, String displayLang) {
		String classifierCode = tuple.getPosGroupCode();
		Classifier classifier = getClassifier(ClassifierName.POS_GROUP, classifierCode, displayLang);
		collocPosGroup.setPosGroup(classifier);
	}

	private Classifier getClassifier(ClassifierName name, String code, String lang) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		Classifier classifier = commonDataDbService.getClassifier(name, code, lang);
		return classifier;
	}

	public List<Classifier> getClassifiers(ClassifierName name, String lang) {
		List<Classifier> classifiers = commonDataDbService.getClassifiers(name, lang);
		return classifiers;
	}

	public List<Classifier> getClassifiers(ClassifierName name, List<String> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getClassifiers(name, codes, lang);
		return classifiers;
	}

	private List<Classifier> getClassifiersWithOrigin(ClassifierName name, List<TypeDomain> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getClassifiersWithOrigin(name, codes, lang);
		return classifiers;
	}

	private List<Dataset> getDatasets(List<String> codes) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Dataset> datasets = commonDataDbService.getDatasets(codes);
		return datasets;
	}
}
