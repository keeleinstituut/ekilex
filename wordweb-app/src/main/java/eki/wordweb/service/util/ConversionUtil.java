package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.Word;
import eki.wordweb.service.db.ClassifierDbService;

@Component
public class ConversionUtil {

	@Autowired
	private ClassifierDbService classifierDbService;

	public List<Lexeme> composeLexemes(List<LexemeMeaningTuple> lexemeMeaningTuples, String classifierValueLang) {

		List<Lexeme> lexemes = new ArrayList<>();
		Map<Long, Lexeme> lexemeMap = new HashMap<>();
		List<Long> definitionIds = null;
		List<Long> meaningWordIds = null;
		List<Classifier> classifiers;
		List<String> classifierCodes;
		Classifier classifier;
		String classifierCode;
		List<TypeDomain> domainCodes;

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
				classifierCode = tuple.getLexemeTypeCode();
				classifier = classifierDbService.getClassifier(ClassifierName.LEXEME_TYPE, classifierCode, classifierValueLang);
				lexeme.setLexemeType(classifier);
				classifierCodes = tuple.getRegisterCodes();
				classifiers = classifierDbService.getClassifiers(ClassifierName.REGISTER, classifierCodes, classifierValueLang);
				lexeme.setRegisters(classifiers);
				classifierCodes = tuple.getPosCodes();
				classifiers = classifierDbService.getClassifiers(ClassifierName.POS, classifierCodes, classifierValueLang);
				lexeme.setPoses(classifiers);
				classifierCodes = tuple.getDerivCodes();
				classifiers = classifierDbService.getClassifiers(ClassifierName.DERIV, classifierCodes, classifierValueLang);
				lexeme.setDerivs(classifiers);
				domainCodes = tuple.getDomainCodes();
				classifiers = classifierDbService.getClassifiersWithOrigin(ClassifierName.DOMAIN, domainCodes, classifierValueLang);
				lexeme.setDomains(classifiers);
				lexeme.setDefinitions(new ArrayList<>());
				lexeme.setMeaningWords(new ArrayList<>());
				lexemeMap.put(lexemeId, lexeme);
				lexemes.add(lexeme);
				definitionIds = new ArrayList<>();
				meaningWordIds = new ArrayList<>();
			}
			Long definitionId = tuple.getDefinitionId();
			if ((definitionId != null) && !definitionIds.contains(definitionId)) {
				String definition = tuple.getDefinition();
				lexeme.getDefinitions().add(definition);
				definitionIds.add(definitionId);
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
		return lexemes;
	}

	
}
