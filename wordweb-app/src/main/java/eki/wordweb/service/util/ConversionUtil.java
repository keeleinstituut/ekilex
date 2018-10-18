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
import eki.common.constant.ReferenceType;
import eki.common.data.Classifier;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.data.Collocation;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationRelGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.Form;
import eki.wordweb.data.FormPair;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.SourceLink;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeUsage;
import eki.wordweb.data.TypeWord;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.service.db.CommonDataDbService;

@Component
public class ConversionUtil {

	private static final char RAW_VALUE_ELEMENTS_SEPARATOR = '|';

	private static final Float COLLOC_MEMBER_CONTEXT_WEIGHT = 0.5F;

	@Autowired
	private CommonDataDbService commonDataDbService;

	public void filterIrrelevantValues(List<Word> words, final String destinLang) {
		for (Word word : words) {
			List<TypeWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				TypeWord firstMeaningWord = meaningWords.get(0);
				if (StringUtils.isNotBlank(firstMeaningWord.getValue())) {
					Long lexemeId = firstMeaningWord.getLexemeId();
					List<String> meaningWordValues = meaningWords.stream()
							.filter(meaningWord -> meaningWord.getLexemeId().equals(lexemeId))
							.filter(meaningWord -> StringUtils.equals(meaningWord.getLang(), destinLang))
							.map(meaningWord -> meaningWord.getValue())
							.distinct()
							.collect(Collectors.toList());
					String meaningWordsWrapup = StringUtils.join(meaningWordValues, ", ");
					word.setMeaningWordsWrapup(meaningWordsWrapup);
				}
			}
			List<TypeDefinition> definitions = word.getDefinitions();
			if (CollectionUtils.isNotEmpty(definitions)) {
				TypeDefinition firstDefinition = definitions.get(0);
				if (StringUtils.isNotBlank(firstDefinition.getValue())) {
					Long lexemeId = firstDefinition.getLexemeId();
					List<String> definitionValues = definitions.stream()
							.filter(definition -> definition.getLexemeId().equals(lexemeId))
							.filter(definition -> StringUtils.equals(definition.getLang(), destinLang))
							.map(definition -> definition.getValue())
							.collect(Collectors.toList());
					String definitionsWrapup = StringUtils.join(definitionValues, ", ");
					word.setDefinitionsWrapup(definitionsWrapup);
				}
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
			Word word,
			List<LexemeMeaningTuple> lexemeMeaningTuples,
			List<LexemeDetailsTuple> lexemeDetailsTuples,
			List<CollocationTuple> collocTuples,
			String sourceLang, String destinLang, String displayLang) {

		Long wordId = word.getWordId();
		List<Lexeme> lexemes = new ArrayList<>();
		Map<Long, Lexeme> lexemeMap = new HashMap<>();
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
			lexeme.setGovernments(tuple.getGovernments());

			populateUsages(lexeme, tuple, displayLang);
			populateRelatedLexemes(lexeme, tuple, displayLang);
			populateRelatedMeanings(lexeme, tuple, displayLang);
		}

		for (CollocationTuple tuple : collocTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);

			CollocationPosGroup collocPosGroup = populateCollocPosGroup(lexeme, tuple, collocPosGroupMap, displayLang);
			CollocationRelGroup collocRelGroup = populateCollocRelGroup(collocPosGroup, tuple, collocRelGroupMap);
			Collocation collocation = populateCollocation(tuple);

			if (collocPosGroup == null) {
				lexeme.getSecondaryCollocations().add(collocation);
			} else {
				collocRelGroup.getCollocations().add(collocation);
			}
		}

		List<TypeWordRelation> relatedWords = word.getRelatedWords();
		List<String> allRelatedWordValues = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relatedWords)) {
			List<String> relatedWordValues = relatedWords.stream().map(TypeWordRelation::getWord).distinct().collect(Collectors.toList());
			allRelatedWordValues.addAll(relatedWordValues);
		}
		List<WordGroup> wordGroups = word.getWordGroups();
		if (CollectionUtils.isNotEmpty(wordGroups)) {
			for (WordGroup wordGroup : wordGroups) {
				List<String> relatedWordValues = wordGroup.getWordGroupMembers().stream().map(TypeWordRelation::getWord).distinct().collect(Collectors.toList());
				allRelatedWordValues.addAll(relatedWordValues);
			}
		}

		for (Lexeme lexeme : lexemes) {
			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
			if (isEmptyLexeme) {
				continue;
			}
			filterMeaningWords(allRelatedWordValues, lexeme);
			List<String> existingCollocationValues = new ArrayList<>();
			transformCollocationPosGroupsForDisplay(wordId, lexeme, existingCollocationValues);
			transformSecondaryCollocationsForDisplay(wordId, lexeme, existingCollocationValues);
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
		lexeme.setMeaningWords(new ArrayList<>());
		lexeme.setDestinLangMatchWords(new ArrayList<>());
		lexeme.setOtherLangMatchWords(new ArrayList<>());
		lexeme.setGovernments(new ArrayList<>());
		lexeme.setUsages(new ArrayList<>());
		lexeme.setCollocationPosGroups(new ArrayList<>());
		lexeme.setSecondaryCollocations(new ArrayList<>());
		return lexeme;
	}

	private void populateMeaningWord(Lexeme lexeme, LexemeMeaningTuple tuple, List<Long> meaningWordIds, String sourceLang, String destinLang) {
		Long meaningWordId = tuple.getMeaningWordId();
		if (meaningWordId == null) {
			return;
		}
		if (CollectionUtils.isNotEmpty(meaningWordIds) && meaningWordIds.contains(meaningWordId)) {
			return;
		}
		Word meaningWord = new Word();
		meaningWord.setWordId(meaningWordId);
		meaningWord.setWord(tuple.getMeaningWord());
		meaningWord.setHomonymNr(tuple.getMeaningWordHomonymNr());
		meaningWord.setLang(tuple.getMeaningWordLang());
		if (StringUtils.equals(tuple.getMeaningWordLang(), sourceLang)) {
			lexeme.getMeaningWords().add(meaningWord);
		} else if (StringUtils.equals(tuple.getMeaningWordLang(), destinLang)) {
			lexeme.getDestinLangMatchWords().add(meaningWord);
		} else {
			lexeme.getOtherLangMatchWords().add(meaningWord);
		}
		meaningWordIds.add(meaningWordId);
	}

	private void populateUsages(Lexeme lexeme, LexemeDetailsTuple tuple, String displayLang) {
	
		String classifierCode;
		Classifier classifier;
		List<TypeUsage> usages = tuple.getUsages();
		if (CollectionUtils.isNotEmpty(usages)) {
			for (TypeUsage usage : usages) {
				classifierCode = usage.getUsageTypeCode();
				classifier = getClassifier(ClassifierName.USAGE_TYPE, classifierCode, displayLang);
				usage.setUsageType(classifier);
				usage.setUsageAuthors(new ArrayList<>());
				List<String> usageAuthorsRaw = usage.getUsageAuthorsRaw();
				if (CollectionUtils.isNotEmpty(usageAuthorsRaw)) {
					for (String usageAuthorRaw : usageAuthorsRaw) {
						String[] usageAuthorElements = StringUtils.split(usageAuthorRaw, RAW_VALUE_ELEMENTS_SEPARATOR);
						String type = usageAuthorElements[0];
						String name = usageAuthorElements[1];
						boolean isTranslator = StringUtils.equalsIgnoreCase(ReferenceType.TRANSLATOR.name(), type);
						SourceLink usageAuthor = new SourceLink();
						usageAuthor.setType(type);
						usageAuthor.setName(name);
						usageAuthor.setTranslator(isTranslator);
						usage.getUsageAuthors().add(usageAuthor);
					}
				}
			}
		}
		lexeme.setUsages(usages);
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
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
			lexeme.setRelatedLexemesByType(relatedLexemesByType);
		}
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
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			Map<Classifier, List<TypeMeaningRelation>> relatedMeaningsByType = relatedMeanings.stream().collect(Collectors.groupingBy(TypeMeaningRelation::getMeaningRelType));
			lexeme.setRelatedMeaningsByType(relatedMeaningsByType);
		}
	}

	private CollocationPosGroup populateCollocPosGroup(Lexeme lexeme, CollocationTuple tuple, Map<Long, CollocationPosGroup> collocPosGroupMap, String displayLang) {
		CollocationPosGroup collocPosGroup = null;
		Long posGroupId = tuple.getPosGroupId();
		if (posGroupId != null) {
			collocPosGroup = collocPosGroupMap.get(posGroupId);
			if (collocPosGroup == null) {
				collocPosGroup = new CollocationPosGroup();
				String classifierCode = tuple.getPosGroupCode();
				Classifier classifier = getClassifier(ClassifierName.POS_GROUP, classifierCode, displayLang);
				collocPosGroup.setPosGroupId(posGroupId);
				collocPosGroup.setPosGroup(classifier);
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
				collocRelGroup.setRelGroupId(relGroupId);
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

	private void filterMeaningWords(List<String> allRelatedWordValues, Lexeme lexeme) {
	
		List<Word> meaningWords = lexeme.getMeaningWords();
		if (CollectionUtils.isNotEmpty(allRelatedWordValues)) {
			meaningWords = meaningWords.stream().filter(meaningWord -> !allRelatedWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		List<TypeLexemeRelation> relatedLexemes = lexeme.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			List<String> relatedLexemeWordValues = relatedLexemes.stream().map(TypeLexemeRelation::getWord).distinct().collect(Collectors.toList());
			meaningWords = meaningWords.stream().filter(meaningWord -> !relatedLexemeWordValues.contains(meaningWord.getWord())).collect(Collectors.toList());
		}
		lexeme.setMeaningWords(meaningWords);
	}

	private void transformCollocationPosGroupsForDisplay(Long wordId, Lexeme lexeme, List<String> existingCollocationValues) {

		List<Collocation> collocations;
		List<DisplayColloc> displayCollocs;
		List<CollocationPosGroup> collocationPosGroups = lexeme.getCollocationPosGroups();
		for (CollocationPosGroup collocationPosGroup : collocationPosGroups) {
			List<CollocationRelGroup> collocationRelGroups = collocationPosGroup.getRelationGroups();
			for (CollocationRelGroup collocationRelGroup : collocationRelGroups) {
				displayCollocs = new ArrayList<>();
				collocationRelGroup.setDisplayCollocs(displayCollocs);
				List<String> allUsages = new ArrayList<>();
				collocationRelGroup.setAllUsages(allUsages);
				collocations = collocationRelGroup.getCollocations();
				transformCollocationsForDisplay(wordId, collocations, displayCollocs, allUsages, existingCollocationValues);
			}
		}
	}

	private void transformSecondaryCollocationsForDisplay(Long wordId, Lexeme lexeme, List<String> existingCollocationValues) {

		List<Collocation> collocations = lexeme.getSecondaryCollocations();
		List<DisplayColloc> displayCollocs = new ArrayList<>();
		lexeme.setSecondaryDisplayCollocs(displayCollocs);
		transformCollocationsForDisplay(wordId, collocations, displayCollocs, null, existingCollocationValues);
	}

	private void transformCollocationsForDisplay(
			Long wordId, List<Collocation> collocations, List<DisplayColloc> displayCollocs, List<String> allUsages, List<String> existingCollocationValues) {

		List<TypeCollocMember> collocMembers;
		List<CollocMemberGroup> existingMemberGroupOrder;
		List<String> collocMemberForms;
		DisplayColloc displayColloc;
		Map<String, DisplayColloc> collocMemberGroupMap = new HashMap<>();
	
		for (Collocation colloc : collocations) {
			String collocValue = colloc.getValue();
			if (existingCollocationValues.contains(collocValue)) {
				continue;
			}
			existingCollocationValues.add(collocValue);
			if ((allUsages != null) && CollectionUtils.isNotEmpty(colloc.getCollocUsages())) {
				colloc.getCollocUsages().removeAll(allUsages);
				allUsages.addAll(colloc.getCollocUsages());
			}
			collocMembers = colloc.getCollocMembers();
			String collocMemberGroupKey = composeCollocMemberGroupKey(collocMembers);
			displayColloc = collocMemberGroupMap.get(collocMemberGroupKey);
			if (displayColloc == null) {
				displayColloc = new DisplayColloc();
				displayColloc.setMemberGroupOrder(new ArrayList<>());
				displayColloc.setPrimaryMembers(new ArrayList<>());
				displayColloc.setContextMembers(new ArrayList<>());
				displayColloc.setCollocMemberForms(new ArrayList<>());
				collocMemberGroupMap.put(collocMemberGroupKey, displayColloc);
				displayCollocs.add(displayColloc);
			}
			CollocMemberGroup recentCollocMemberGroup = null;
			CollocMemberGroup currentCollocMemberGroup;
			boolean headwordOrPrimaryMemberOccurred = false;
			List<CollocMemberGroup> currentMemberGroupOrder = new ArrayList<>();
			for (TypeCollocMember collocMember : collocMembers) {
				String conjunct = collocMember.getConjunct();
				Float weight = collocMember.getWeight();
				boolean isHeadword = collocMember.getWordId().equals(wordId);
				boolean isPrimary = !isHeadword && weight.compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT) > 0;
				boolean isContext = weight.compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT) == 0;
				if (StringUtils.isNotBlank(conjunct)) {
					if (headwordOrPrimaryMemberOccurred) {
						collocMember.setPreConjunct(true);
					} else {
						collocMember.setPostConjunct(true);
					}
				}
				currentCollocMemberGroup = null;
				if (isHeadword) {
					currentCollocMemberGroup = CollocMemberGroup.HEADWORD;
					headwordOrPrimaryMemberOccurred = true;
				} else if (isPrimary) {
					currentCollocMemberGroup = CollocMemberGroup.PRIMARY;
					headwordOrPrimaryMemberOccurred = true;
				} else if (isContext) {
					currentCollocMemberGroup = CollocMemberGroup.CONTEXT;
				}
				collocMemberForms = displayColloc.getCollocMemberForms();
				if (CollectionUtils.isEmpty(currentMemberGroupOrder)) {
					recentCollocMemberGroup = currentCollocMemberGroup;
					currentMemberGroupOrder.add(currentCollocMemberGroup);
				} else {
					recentCollocMemberGroup = currentMemberGroupOrder.get(currentMemberGroupOrder.size() - 1);
				}
				if (!recentCollocMemberGroup.equals(currentCollocMemberGroup)) {
					if (!currentMemberGroupOrder.contains(currentCollocMemberGroup)) {
						currentMemberGroupOrder.add(currentCollocMemberGroup);
					}
				}
				if (CollocMemberGroup.HEADWORD.equals(currentCollocMemberGroup)) {
					if (displayColloc.getHeadwordMember() == null) {
						displayColloc.setHeadwordMember(collocMember);
						collocMemberForms.add(collocMember.getForm());
					}
				} else if (CollocMemberGroup.PRIMARY.equals(currentCollocMemberGroup)) {
					if (!collocMemberForms.contains(collocMember.getForm())) {
						displayColloc.getPrimaryMembers().add(collocMember);
						collocMemberForms.add(collocMember.getForm());
					}
				} else if (CollocMemberGroup.CONTEXT.equals(currentCollocMemberGroup)) {
					if (!collocMemberForms.contains(collocMember.getForm())) {
						displayColloc.getContextMembers().add(collocMember);
						collocMemberForms.add(collocMember.getForm());
					}
				}
			}
			if (CollectionUtils.isEmpty(displayColloc.getMemberGroupOrder())) {
				displayColloc.setMemberGroupOrder(currentMemberGroupOrder);
			}
			existingMemberGroupOrder = displayColloc.getMemberGroupOrder();
			if (!StringUtils.equals(currentMemberGroupOrder.toString(), existingMemberGroupOrder.toString())) {
				if (currentMemberGroupOrder.size() > existingMemberGroupOrder.size()) {
					displayColloc.setMemberGroupOrder(currentMemberGroupOrder);
				}
			}
		}
	}

	private String composeCollocMemberGroupKey(List<TypeCollocMember> collocMembers) {
		List<String> headwordAndPrimaryMemberForms = collocMembers.stream()
				.filter(collocMember -> collocMember.getWeight().compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT) > 0)
				.map(collocMember -> {
					String memberKey = "";
					if (StringUtils.isNotBlank(collocMember.getConjunct())) {
						memberKey = collocMember.getConjunct() + "|";
					}
					memberKey += collocMember.getForm();
					return memberKey;
				})
				.collect(Collectors.toList());
		String collocMemberGroupKey = StringUtils.join(headwordAndPrimaryMemberForms, '-');
		return collocMemberGroupKey;
	}

	public void composeWordRelations(Word word, List<WordRelationTuple> wordRelationTuples, String displayLang) {

		if (CollectionUtils.isEmpty(wordRelationTuples)) {
			return;
		}
		word.setWordGroups(new ArrayList<>());
		String classifierCode;
		Classifier classifier;
		for (WordRelationTuple tuple : wordRelationTuples) {
			List<TypeWordRelation> relatedWords = tuple.getRelatedWords();
			for (TypeWordRelation wordRelation : relatedWords) {
				classifierCode = wordRelation.getWordRelTypeCode();
				classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
				wordRelation.setWordRelType(classifier);
			}
			word.setRelatedWords(relatedWords);
			if (CollectionUtils.isNotEmpty(tuple.getWordGroupMembers())) {
				WordGroup wordGroup = new WordGroup();
				wordGroup.setWordGroupId(tuple.getWordGroupId());
				wordGroup.setWordRelTypeCode(tuple.getWordRelTypeCode());
				wordGroup.setWordGroupMembers(tuple.getWordGroupMembers());
				classifierCode = tuple.getWordRelTypeCode();
				classifier = getClassifier(ClassifierName.WORD_REL_TYPE, classifierCode, displayLang);
				wordGroup.setWordRelType(classifier);
				word.getWordGroups().add(wordGroup);
			}
		}
		boolean wordRelationsExist = CollectionUtils.isNotEmpty(word.getRelatedWords()) || CollectionUtils.isNotEmpty(word.getWordGroups());
		word.setWordRelationsExist(wordRelationsExist);
	}

	public List<Paradigm> composeParadigms(Map<Long, List<Form>> paradigmFormsMap, String displayLang) {

		List<Paradigm> paradigms = new ArrayList<>();
		Map<String, Form> formMap;
		List<FormPair> compactForms;
		String classifierCode;
		Classifier classifier;
		for (Entry<Long, List<Form>> paradigmFormsEntry : paradigmFormsMap.entrySet()) {
			Long paradigmId = paradigmFormsEntry.getKey();
			List<Form> forms = paradigmFormsEntry.getValue();
			formMap = new HashMap<>();
			for (Form form : forms) {
				classifierCode = form.getMorphCode();
				classifier = getClassifier(ClassifierName.MORPH, classifierCode, displayLang);
				form.setMorph(classifier);
				formMap.put(classifierCode, form);
			}
			compactForms = composeCompactForms(formMap);
			Paradigm paradigm = new Paradigm();
			paradigm.setParadigmId(paradigmId);
			paradigm.setForms(forms);
			paradigm.setCompactForms(compactForms);
			paradigms.add(paradigm);
		}
		paradigms.sort(Comparator.comparing(Paradigm::getParadigmId));
		return paradigms;
	}

	private List<FormPair> composeCompactForms(Map<String, Form> formMap) {

		final String[] orderedMorphPairCodes1 = new String[] {"SgN", "SgG", "SgP"};
		final String[] orderedMorphPairCodes2 = new String[] {"PlN", "PlG", "PlP"};
		final String[] unorderedMorphPairCodes = new String[] {"Sup", "Inf", "IndPrSg3", "PtsPtIps", "ID"};

		List<FormPair> compactForms = new ArrayList<>();
		FormPair formPair;
		for (int orderedMorphPairIndex = 0; orderedMorphPairIndex < orderedMorphPairCodes1.length; orderedMorphPairIndex++) {
			String morphCode1 = orderedMorphPairCodes1[orderedMorphPairIndex];
			Form form1 = formMap.get(morphCode1);
			String morphCode2 = orderedMorphPairCodes2[orderedMorphPairIndex];
			Form form2 = formMap.get(morphCode2);
			if ((form1 != null) || (form2 != null)) {
				formPair = new FormPair();
				formPair.setForm1(form1);
				formPair.setForm2(form2);
				compactForms.add(formPair);
			}
		}
		formPair = null;
		for (String morphCode : unorderedMorphPairCodes) {
			Form form = formMap.get(morphCode);
			if (form == null) {
				continue;
			}
			if (formPair == null) {
				formPair = new FormPair();
			}
			if (formPair.getForm1() == null) {
				formPair.setForm1(form);
				compactForms.add(formPair);
			} else if (formPair.getForm2() == null) {
				formPair.setForm2(form);
				formPair = null;
			}
		}
		return compactForms;
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

	private boolean isEmptyLexeme(Lexeme lexeme) {
		return CollectionUtils.isEmpty(lexeme.getDefinitions()) &&
				CollectionUtils.isEmpty(lexeme.getSecondaryCollocations()) &&
				CollectionUtils.isEmpty(lexeme.getCollocationPosGroups()) &&
				CollectionUtils.isEmpty(lexeme.getDomains()) &&
				CollectionUtils.isEmpty(lexeme.getGovernments()) &&
				CollectionUtils.isEmpty(lexeme.getUsages()) &&
				CollectionUtils.isEmpty(lexeme.getOtherLangMatchWords()) &&
				CollectionUtils.isEmpty(lexeme.getDestinLangMatchWords()) &&
				CollectionUtils.isEmpty(lexeme.getRegisters());
	}

}
