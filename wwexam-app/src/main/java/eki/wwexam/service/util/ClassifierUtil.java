package eki.wwexam.service.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wwexam.data.os.OsLexemeClassifiers;
import eki.wwexam.data.os.OsLexemeMeaning;
import eki.wwexam.data.os.OsLexemeWord;
import eki.wwexam.data.os.OsMeaning;
import eki.wwexam.data.os.OsWord;
import eki.wwexam.data.os.OsWordRelation;
import eki.wwexam.data.os.OsWordRelationGroup;
import eki.wwexam.service.db.CommonDataDbService;

@Component
public class ClassifierUtil {

	@Autowired
	private CommonDataDbService commonDataDbService;

	public void applyOsClassifiers(OsWord word, String displayLang) {
		String classifierCode;
		Classifier classifier;
		applyOsWordClassifiers(word);
		List<OsLexemeMeaning> lexemeMeanings = word.getLexemeMeanings();
		for (OsLexemeMeaning lexemeMeaning : lexemeMeanings) {
			applyOsLexemeClassifiers(lexemeMeaning);
			OsMeaning meaning = lexemeMeaning.getMeaning();
			List<OsLexemeWord> lexemeWords = meaning.getLexemeWords();
			if (CollectionUtils.isNotEmpty(lexemeWords)) {
				for (OsLexemeWord lexemeWord : lexemeWords) {
					applyOsLexemeClassifiers(lexemeWord);
					applyOsWordClassifiers(lexemeWord);
				}
			}
		}
		List<OsWordRelationGroup> wordRelationGroups = word.getWordRelationGroups();
		if (CollectionUtils.isNotEmpty(wordRelationGroups)) {
			for (OsWordRelationGroup wordRelationGroup : wordRelationGroups) {
				classifierCode = wordRelationGroup.getWordRelTypeCode();
				classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
				wordRelationGroup.setWordRelType(classifier);
				List<OsWordRelation> relatedWords = wordRelationGroup.getRelatedWords();
				if (CollectionUtils.isNotEmpty(relatedWords)) {
					for (OsWordRelation wordRelation : relatedWords) {
						classifierCode = wordRelation.getWordRelTypeCode();
						classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
						wordRelation.setWordRelType(classifier);
						applyOsWordClassifiers(wordRelation);
					}
				}
			}
		}
	}

	private void applyOsLexemeClassifiers(OsLexemeClassifiers lexeme) {
		List<String> classifierCodes = lexeme.getRegisterCodes();
		List<Classifier> classifiers = getOsClassifiers(ClassifierName.REGISTER, classifierCodes);
		lexeme.setRegisters(classifiers);
		String classifierCode = lexeme.getValueStateCode();
		Classifier classifier = getOsClassifier(ClassifierName.VALUE_STATE, classifierCode);
		lexeme.setValueState(classifier);
	}

	private void applyOsWordClassifiers(OsWord word) {
		String classifierCode = word.getDisplayMorphCode();
		Classifier classifier = getOsClassifier(ClassifierName.DISPLAY_MORPH, classifierCode);
		word.setDisplayMorph(classifier);
		List<String> classifierCodes = word.getWordTypeCodes();
		List<Classifier> classifiers = getOsClassifiers(ClassifierName.WORD_TYPE, classifierCodes);
		word.setWordTypes(classifiers);
	}

	private Classifier getOsClassifier(ClassifierName name, String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		Classifier classifier = commonDataDbService.getOsClassifier(name, code);
		return classifier;
	}

	private List<Classifier> getOsClassifiers(ClassifierName name, List<String> codes) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = commonDataDbService.getOsClassifiersInProvidedOrder(name, codes);
		return classifiers;
	}

	private Classifier getClassifier(ClassifierName name, String code, String lang) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		Classifier classifier = commonDataDbService.getClassifier(name, code, lang);
		return classifier;
	}

}
