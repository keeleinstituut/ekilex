package eki.wordweb.service.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeMeaningWord;
import eki.wordweb.data.TypeUsage;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordGroup;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ClassifierUtil {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CommonDataDbService commonDataDbService;

	public Classifier reValue(Classifier classifier, String messageKey) {
		String newValue = messageSource.getMessage(messageKey, new Object[0], LocaleContextHolder.getLocale());
		Classifier copy = new Classifier(classifier.getName(), classifier.getOrigin(), classifier.getParent(), classifier.getCode(), newValue, classifier.getLang());
		return copy;
	}

	public void applyClassifiers(Word word, String displayLang) {
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
		classifierCode = word.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
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

	public void applyClassifiers(Lexeme lexeme, String displayLang) {
		String classifierCode;
		Classifier classifier;
		List<Classifier> classifiers;
		List<String> classifierCodes;
		classifierCode = lexeme.getValueStateCode();
		classifier = getClassifier(ClassifierName.VALUE_STATE, classifierCode, displayLang);
		lexeme.setValueState(classifier);
		classifierCodes = lexeme.getRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		lexeme.setRegisters(classifiers);
		classifierCodes = lexeme.getPosCodes();
		classifiers = getClassifiers(ClassifierName.POS, classifierCodes, displayLang);
		lexeme.setPoses(classifiers);
		classifierCodes = lexeme.getDerivCodes();
		classifiers = getClassifiers(ClassifierName.DERIV, classifierCodes, displayLang);
		lexeme.setDerivs(classifiers);
	}

	public void applyClassifiers(TypeMeaningWord meaningWord, String displayLang) {
		String classifierCode;
		List<String> classifierCodes;
		Classifier classifier;
		List<Classifier> classifiers;
		classifierCode = meaningWord.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		meaningWord.setAspect(classifier);
		classifierCodes = meaningWord.getMwLexRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		meaningWord.setMwLexRegisters(classifiers);
		classifierCode = meaningWord.getMwLexValueStateCode();
		classifier = getClassifier(ClassifierName.VALUE_STATE, classifierCode, displayLang);
		meaningWord.setMwLexValueState(classifier);
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
		List<String> classifierCodes;
		Classifier classifier;
		List<Classifier> classifiers;
		classifierCode = meaningRelation.getAspectCode();
		classifier = getClassifier(ClassifierName.ASPECT, classifierCode, displayLang);
		meaningRelation.setAspect(classifier);
		classifierCodes = meaningRelation.getLexValueStateCodes();
		classifiers = getClassifiers(ClassifierName.VALUE_STATE, classifierCodes, displayLang);
		meaningRelation.setLexValueStates(classifiers);
		classifierCodes = meaningRelation.getLexRegisterCodes();
		classifiers = getClassifiers(ClassifierName.REGISTER, classifierCodes, displayLang);
		meaningRelation.setLexRegisters(classifiers);
		classifierCode = meaningRelation.getMeaningRelTypeCode();
		classifier = getClassifier(ClassifierName.MEANING_REL_TYPE, classifierCode, displayLang);
		meaningRelation.setMeaningRelType(classifier);
	}

	public void applyClassifiers(WordGroup wordGroup, String displayLang) {
		String classifierCode;
		Classifier classifier;
		classifierCode = wordGroup.getWordRelTypeCode();
		classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
		wordGroup.setWordRelType(classifier);
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
		if (classifier == null) {
			classifier = new Classifier(name.name(), null, null, code, code, lang);
		}
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
		if (CollectionUtils.isEmpty(classifiers) || (classifiers.size() != codes.size())) {
			classifiers = codes.stream()
					.map(code -> new Classifier(name.name(), null, null, code, code, lang))
					.collect(Collectors.toList());
		}
		return classifiers;
	}

	private List<Classifier> getClassifiersWithOrigin(ClassifierName name, List<TypeDomain> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getClassifiersWithOrigin(name, codes, lang);
		if (CollectionUtils.isEmpty(classifiers) || (classifiers.size() != codes.size())) {
			classifiers = codes.stream()
					.map(code -> new Classifier(name.name(), code.getOrigin(), null, code.getCode(), code.getCode(), lang))
					.collect(Collectors.toList());
		}
		return classifiers;
	}
}
