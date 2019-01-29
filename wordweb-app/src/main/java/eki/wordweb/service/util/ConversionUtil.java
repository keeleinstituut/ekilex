package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.ReferenceType;
import eki.common.data.Classifier;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Collocation;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationRelGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.Form;
import eki.wordweb.data.Lexeme;
import eki.wordweb.data.LexemeDetailsTuple;
import eki.wordweb.data.LexemeMeaningTuple;
import eki.wordweb.data.MeaningWord;
import eki.wordweb.data.Paradigm;
import eki.wordweb.data.ParadigmGroup;
import eki.wordweb.data.SourceLink;
import eki.wordweb.data.TypeCollocMember;
import eki.wordweb.data.TypeDefinition;
import eki.wordweb.data.TypeLexemeRelation;
import eki.wordweb.data.TypeMeaningRelation;
import eki.wordweb.data.TypeUsage;
import eki.wordweb.data.TypeWord;
import eki.wordweb.data.TypeWordEtym;
import eki.wordweb.data.TypeWordRelation;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymology;
import eki.wordweb.data.WordGroup;
import eki.wordweb.data.WordRelationGroup;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.WordTypeData;

@Component
public class ConversionUtil implements WebConstant, SystemConstant {

	private static final char RAW_VALUE_ELEMENTS_SEPARATOR = '|';

	@Autowired
	private ClassifierUtil classifierUtil;

	public void filterIrrelevantValues(List<Word> words, String destinLang, String[] datasets) {
		for (Word word : words) {
			List<TypeWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				List<TypeWord> primaryMeaningWords = meaningWords.stream()
						.filter(meaningWord -> ArrayUtils.contains(datasets, meaningWord.getDatasetCode()))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryMeaningWords)) {
					TypeWord firstMeaningWord = primaryMeaningWords.get(0);
					if (StringUtils.isNotBlank(firstMeaningWord.getValue())) {
						Long lexemeId = firstMeaningWord.getLexemeId();
						List<String> meaningWordValues = primaryMeaningWords.stream()
								.filter(meaningWord -> meaningWord.getLexemeId().equals(lexemeId))
								.filter(meaningWord -> StringUtils.equals(meaningWord.getLang(), destinLang))
								.map(meaningWord -> {
									if (meaningWord.isPrefixoid()) {
										return meaningWord.getValue() + "-";
									} else if (meaningWord.isSuffixoid()) {
										return "-" + meaningWord.getValue();
									} else {
										return meaningWord.getValue();
									}
								})
								.distinct()
								.collect(Collectors.toList());
						String meaningWordsWrapup = StringUtils.join(meaningWordValues, ", ");
						word.setMeaningWordsWrapup(meaningWordsWrapup);
					}
				}
			}
			List<TypeDefinition> definitions = word.getDefinitions();
			if (CollectionUtils.isNotEmpty(definitions)) {
				List<TypeDefinition> primaryDefinitions = definitions.stream()
						.filter(definition -> ArrayUtils.contains(datasets, definition.getDatasetCode()))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(primaryDefinitions)) {
					TypeDefinition firstDefinition = primaryDefinitions.get(0);
					if (StringUtils.isNotBlank(firstDefinition.getValue())) {
						Long lexemeId = firstDefinition.getLexemeId();
						List<String> definitionValues = primaryDefinitions.stream()
								.filter(definition -> definition.getLexemeId().equals(lexemeId))
								.map(definition -> definition.getValue())
								.collect(Collectors.toList());
						String definitionsWrapup = StringUtils.join(definitionValues, ", ");
						word.setDefinitionsWrapup(definitionsWrapup);
					}
				}
			}
		}
	}

	public void setAffixoidFlags(List<? extends WordTypeData> words) {

		for (WordTypeData word : words) {
			setWordTypeFlags(word);
		}
	}

	public void setWordTypeFlags(WordTypeData wordTypeData) {

		boolean isPrefixoid = false;
		boolean isSuffixoid = false;
		boolean isAbbreviationWord = false;
		List<String> wordTypeCodes = wordTypeData.getWordTypeCodes();
		if (CollectionUtils.isNotEmpty(wordTypeCodes)) {
			isPrefixoid = wordTypeCodes.contains(PREFIXOID_WORD_TYPE_CODE);
			isSuffixoid = wordTypeCodes.contains(SUFFIXOID_WORD_TYPE_CODE);
			isAbbreviationWord = CollectionUtils.containsAny(wordTypeCodes, Arrays.asList(ABBREVIATION_WORD_TYPE_CODES));
		}
		wordTypeData.setPrefixoid(isPrefixoid);
		wordTypeData.setSuffixoid(isSuffixoid);
		wordTypeData.setAbbreviationWord(isAbbreviationWord);

		if (wordTypeData instanceof Word) {
			Word word = (Word) wordTypeData;
			List<TypeWord> meaningWords = word.getMeaningWords();
			if (CollectionUtils.isNotEmpty(meaningWords)) {
				for (TypeWord meaningWord : meaningWords) {
					setWordTypeFlags(meaningWord);
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
			List<LexemeDetailsTuple> lexemeDetailsTuples,
			List<LexemeMeaningTuple> lexemeMeaningTuples,
			List<CollocationTuple> collocTuples,
			String sourceLang, String destinLang, String displayLang) {

		Long wordId = word.getWordId();
		List<Lexeme> lexemes = new ArrayList<>();
		Map<Long, Lexeme> lexemeMap = new HashMap<>();
		Map<Long, CollocationPosGroup> collocPosGroupMap = new HashMap<>();
		Map<Long, CollocationRelGroup> collocRelGroupMap = new HashMap<>();
		List<Long> meaningWordIds = null;

		for (LexemeDetailsTuple tuple : lexemeDetailsTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			if (lexeme == null) {
				lexeme = composeLexeme(lexemeId, tuple, displayLang);
				lexemeMap.put(lexemeId, lexeme);
				lexemes.add(lexeme);
				populateUsages(lexeme, tuple, displayLang);
				populateRelatedLexemes(lexeme, tuple, displayLang);
				meaningWordIds = new ArrayList<>();
			}
			populateMeaningWord(lexeme, tuple, meaningWordIds, sourceLang, destinLang, displayLang);
		}

		for (LexemeMeaningTuple tuple : lexemeMeaningTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);
			populateMeaning(lexeme, tuple, displayLang);
			populateRelatedMeanings(lexeme, tuple, displayLang);
		}

		for (CollocationTuple tuple : collocTuples) {

			Long lexemeId = tuple.getLexemeId();
			Lexeme lexeme = lexemeMap.get(lexemeId);

			CollocationPosGroup collocPosGroup = populateCollocPosGroup(lexeme, tuple, collocPosGroupMap, displayLang);
			CollocationRelGroup collocRelGroup = populateCollocRelGroup(collocPosGroup, tuple, collocRelGroupMap);
			Collocation collocation = populateCollocation(tuple);

			if (collocPosGroup == null) {
				//TODO temporarily disabled
				//lexeme.getSecondaryCollocations().add(collocation);
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

		List<Classifier> summarisedPoses = new ArrayList<>();

		for (Lexeme lexeme : lexemes) {
			boolean isEmptyLexeme = isEmptyLexeme(lexeme);
			lexeme.setEmptyLexeme(isEmptyLexeme);
			if (isEmptyLexeme) {
				continue;
			}
			if (CollectionUtils.isNotEmpty(lexeme.getPoses())) {
				summarisedPoses.addAll(lexeme.getPoses());
			}
			filterMeaningWords(lexeme, allRelatedWordValues);
			List<String> existingCollocationValues = new ArrayList<>();
			transformCollocationPosGroupsForDisplay(wordId, lexeme, existingCollocationValues);
			//TODO temporarily disabled
			//transformSecondaryCollocationsForDisplay(wordId, lexeme, existingCollocationValues);
		}
		summarisedPoses = summarisedPoses.stream().distinct().collect(Collectors.toList());
		word.setSummarisedPoses(summarisedPoses);

		return lexemes;
	}

	private Lexeme composeLexeme(Long lexemeId, LexemeDetailsTuple tuple, String displayLang) {
		Lexeme lexeme = new Lexeme();
		lexeme.setLexemeId(lexemeId);
		lexeme.setMeaningId(tuple.getMeaningId());
		lexeme.setDatasetCode(tuple.getDatasetCode());
		lexeme.setLevel1(tuple.getLevel1());
		lexeme.setLevel2(tuple.getLevel2());
		lexeme.setLevel3(tuple.getLevel3());
		lexeme.setMeaningWords(new ArrayList<>());
		lexeme.setDestinLangMatchWords(new ArrayList<>());
		lexeme.setOtherLangMatchWords(new ArrayList<>());
		lexeme.setGovernments(new ArrayList<>());
		lexeme.setUsages(new ArrayList<>());
		lexeme.setCollocationPosGroups(new ArrayList<>());
		lexeme.setSecondaryCollocations(new ArrayList<>());
		lexeme.setAdviceNotes(tuple.getAdviceNotes());
		lexeme.setPublicNotes(tuple.getPublicNotes());
		lexeme.setGrammars(tuple.getGrammars());
		lexeme.setGovernments(tuple.getGovernments());
		classifierUtil.applyClassifiers(tuple, lexeme, displayLang);
		return lexeme;
	}

	private void populateMeaningWord(Lexeme lexeme, LexemeDetailsTuple tuple, List<Long> meaningWordIds, String sourceLang, String destinLang, String displayLang) {
		Long meaningWordId = tuple.getMeaningWordId();
		if (meaningWordId == null) {
			return;
		}
		if (CollectionUtils.isNotEmpty(meaningWordIds) && meaningWordIds.contains(meaningWordId)) {
			return;
		}
		MeaningWord meaningWord = new MeaningWord();
		meaningWord.setWordId(meaningWordId);
		meaningWord.setWord(tuple.getMeaningWord());
		meaningWord.setHomonymNr(tuple.getMeaningWordHomonymNr());
		meaningWord.setLang(tuple.getMeaningWordLang());
		meaningWord.setWordTypeCodes(tuple.getMeaningWordTypeCodes());
		meaningWord.setGovernments(tuple.getMeaningLexemeGovernments());
		classifierUtil.applyClassifiers(tuple, meaningWord, displayLang);
		setWordTypeFlags(meaningWord);
		boolean additionalDataExists = (meaningWord.getAspect() != null)
				|| CollectionUtils.isNotEmpty(meaningWord.getRegisters())
				|| CollectionUtils.isNotEmpty(meaningWord.getGovernments());
		meaningWord.setAdditionalDataExists(additionalDataExists);
		if (StringUtils.equals(tuple.getMeaningWordLang(), sourceLang)) {
			lexeme.getMeaningWords().add(meaningWord);
		} else if (StringUtils.equals(tuple.getMeaningWordLang(), destinLang)) {
			lexeme.getDestinLangMatchWords().add(meaningWord);
		} else {
			lexeme.getOtherLangMatchWords().add(meaningWord);
		}
		meaningWordIds.add(meaningWordId);
	}

	private void populateMeaning(Lexeme lexeme, LexemeMeaningTuple tuple, String displayLang) {
		lexeme.setImageFiles(tuple.getImageFiles());
		lexeme.setSystematicPolysemyPatterns(tuple.getSystematicPolysemyPatterns());
		lexeme.setSemanticTypes(tuple.getSemanticTypes());
		lexeme.setLearnerComments(tuple.getLearnerComments());
		lexeme.setDefinitions(tuple.getDefinitions());
		classifierUtil.applyClassifiers(tuple, lexeme, displayLang);
	}

	private void populateUsages(Lexeme lexeme, LexemeDetailsTuple tuple, String displayLang) {
		List<TypeUsage> usages = tuple.getUsages();
		boolean isMoreUsages = CollectionUtils.size(usages) > TYPICAL_COLLECTIONS_DISPLAY_LIMIT;
		lexeme.setMoreUsages(isMoreUsages);
		if (CollectionUtils.isNotEmpty(usages)) {
			for (TypeUsage usage : usages) {
				usage.setUsageAuthors(new ArrayList<>());
				classifierUtil.applyClassifiers(usage, displayLang);
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
		List<TypeLexemeRelation> relatedLexemes = tuple.getRelatedLexemes();
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			for (TypeLexemeRelation lexemeRelation : relatedLexemes) {
				classifierUtil.applyClassifiers(lexemeRelation, displayLang);
			}
		}
		lexeme.setRelatedLexemes(relatedLexemes);
		if (CollectionUtils.isNotEmpty(relatedLexemes)) {
			Map<Classifier, List<TypeLexemeRelation>> relatedLexemesByType = relatedLexemes.stream().collect(Collectors.groupingBy(TypeLexemeRelation::getLexRelType));
			lexeme.setRelatedLexemesByType(relatedLexemesByType);
		}
	}

	private void populateRelatedMeanings(Lexeme lexeme, LexemeMeaningTuple tuple, String displayLang) {
		if (CollectionUtils.isNotEmpty(lexeme.getRelatedMeanings())) {
			return;
		}
		List<TypeMeaningRelation> relatedMeanings = tuple.getRelatedMeanings();
		if (CollectionUtils.isNotEmpty(relatedMeanings)) {
			for (TypeMeaningRelation meaningRelation : relatedMeanings) {
				classifierUtil.applyClassifiers(meaningRelation, displayLang);
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
				collocPosGroup.setPosGroupId(posGroupId);
				collocPosGroup.setRelationGroups(new ArrayList<>());
				classifierUtil.applyClassifiers(tuple, collocPosGroup, displayLang);
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

	private void filterMeaningWords(Lexeme lexeme, List<String> allRelatedWordValues) {
	
		List<MeaningWord> meaningWords = lexeme.getMeaningWords();
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
		List<DisplayColloc> limitedPrimaryDisplayCollocs = new ArrayList<>();
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
				if (limitedPrimaryDisplayCollocs.size() < TYPICAL_COLLECTIONS_DISPLAY_LIMIT) {
					limitedPrimaryDisplayCollocs.addAll(displayCollocs);
				}
			}
		}
		boolean needsToLimit = CollectionUtils.size(limitedPrimaryDisplayCollocs) > TYPICAL_COLLECTIONS_DISPLAY_LIMIT;
		if (needsToLimit) {
			limitedPrimaryDisplayCollocs = limitedPrimaryDisplayCollocs.subList(0, TYPICAL_COLLECTIONS_DISPLAY_LIMIT);
		}
		lexeme.setLimitedPrimaryDisplayCollocs(limitedPrimaryDisplayCollocs);
	}

	//TODO temporarily disabled
	private void transformSecondaryCollocationsForDisplay(Long wordId, Lexeme lexeme, List<String> existingCollocationValues) {

		List<Collocation> collocations = lexeme.getSecondaryCollocations();
		List<DisplayColloc> secondaryDisplayCollocs = new ArrayList<>();
		lexeme.setSecondaryDisplayCollocs(secondaryDisplayCollocs);
		transformCollocationsForDisplay(wordId, collocations, secondaryDisplayCollocs, null, existingCollocationValues);
		if (CollectionUtils.isNotEmpty(secondaryDisplayCollocs)) {
			boolean isMoreSecondaryCollocs = CollectionUtils.size(secondaryDisplayCollocs) > TYPICAL_COLLECTIONS_DISPLAY_LIMIT;
			lexeme.setMoreSecondaryCollocs(isMoreSecondaryCollocs);
			List<DisplayColloc> limitedSecondaryDisplayCollocs = new ArrayList<>(secondaryDisplayCollocs);
			if (isMoreSecondaryCollocs) {
				limitedSecondaryDisplayCollocs = limitedSecondaryDisplayCollocs.subList(0, TYPICAL_COLLECTIONS_DISPLAY_LIMIT);
			}
			lexeme.setLimitedSecondaryDisplayCollocs(limitedSecondaryDisplayCollocs);
		}
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

	public void composeWordEtymology(Word word, WordEtymology wordEtymology, String displayLang) {

		if (StringUtils.isBlank(word.getEtymologyYear())
				&& StringUtils.isBlank(word.getEtymologyTypeCode())
				&& (wordEtymology == null)) {
			return;
		}
		if (wordEtymology == null) {
			wordEtymology = new WordEtymology();
		}
		word.setWordEtymology(wordEtymology);
		wordEtymology.setWordEtymologyLineupWrapup(new ArrayList<>());

		StringBuffer wordEtymBuf = new StringBuffer();
		if (word.getEtymologyType() != null) {
			wordEtymBuf.append("<font style='font-variant: small-caps'>");
			wordEtymBuf.append(word.getEtymologyType().getValue());
			wordEtymBuf.append("</font>");
		}
		List<String> wordSources = word.getWordSources();
		if (CollectionUtils.isNotEmpty(wordSources)) {
			if (wordEtymBuf.length() > 0) {
				wordEtymBuf.append(", ");
			}
			wordEtymBuf.append(StringUtils.join(wordSources, ", "));
		}
		String etymologyYear = word.getEtymologyYear();
		if (StringUtils.isNotEmpty(etymologyYear)) {
			if (wordEtymBuf.length() > 0) {
				wordEtymBuf.append(", ");
			}
			wordEtymBuf.append(etymologyYear);
		}
		if (wordEtymBuf.length() > 0) {
			String wordEtymologyWrapup = wordEtymBuf.toString().trim();
			wordEtymology.setWordEtymologyWrapup(wordEtymologyWrapup);
		}
		List<TypeWordEtym> etymLineup = wordEtymology.getEtymLineup();
		if (CollectionUtils.isEmpty(etymLineup)) {
			return;
		}

		for (TypeWordEtym wordEtym : etymLineup) {
			classifierUtil.applyClassifiers(wordEtym, displayLang);
			String[] filteredMeaningWords = ArrayUtils.removeAllOccurences(wordEtym.getEtymMeaningWords(), word.getWord());
			wordEtym.setEtymMeaningWords(filteredMeaningWords);
		}

		List<Long> etymLevelsWordIds = etymLineup.stream().map(TypeWordEtym::getWordId).distinct().collect(Collectors.toList());
		Map<Long, List<TypeWordEtym>> etymLevelsMap = etymLineup.stream().collect(Collectors.groupingBy(TypeWordEtym::getWordId));
		List<String> wordEtymologyLineupWrapup = wordEtymology.getWordEtymologyLineupWrapup();

		for (Long etymLevelWordId : etymLevelsWordIds) {
			List<TypeWordEtym> etymLevelGroup = etymLevelsMap.get(etymLevelWordId);
			boolean membersHaveSameLang = etymLevelGroup.stream().allMatch(
					etymLevelMember -> StringUtils.equals(etymLevelMember.getEtymWordLang(), etymLevelGroup.get(0).getEtymWordLang()));
			boolean membersHaveSameComment = etymLevelGroup.stream().allMatch(
					etymLevelMember -> StringUtils.equals(StringUtils.join(etymLevelMember.getComments()), StringUtils.join(etymLevelGroup.get(0).getComments())));
			StringBuffer etymLevelBuf = new StringBuffer();
			int etymLevelGroupSize = etymLevelGroup.size();
			for (int etymLevelMemberIndex = 0; etymLevelMemberIndex < etymLevelGroupSize; etymLevelMemberIndex++) {
				TypeWordEtym etymLevelMember = etymLevelGroup.get(etymLevelMemberIndex);
				String etymWord = etymLevelMember.getEtymWord();
				Classifier etymWordLanguage = etymLevelMember.getEtymWordLanguage();
				String[] etymMeaningWords = etymLevelMember.getEtymMeaningWords();
				String[] etymWordSources = etymLevelMember.getEtymWordSources();
				String[] comments = etymLevelMember.getComments();
				if (etymLevelMember.isCompound()) {
					etymLevelBuf.append(" + ");
				} else if (etymLevelMemberIndex > 0) {
					etymLevelBuf.append(", ");
				}
				if (etymLevelMember.isQuestionable()) {
					etymLevelBuf.append(" ? ");
				}
				if (etymWordLanguage != null) {
					if (etymLevelMemberIndex == 0) {
						etymLevelBuf.append(etymWordLanguage.getValue());
						etymLevelBuf.append(" ");						
					} else if (!membersHaveSameLang) {
						etymLevelBuf.append(etymWordLanguage.getValue());
						etymLevelBuf.append(" ");
					}
				}
				etymLevelBuf.append("<i>");
				etymLevelBuf.append(etymWord);
				etymLevelBuf.append("</i>");
				if (ArrayUtils.isNotEmpty(etymMeaningWords)) {
					etymLevelBuf.append(' ');
					etymLevelBuf.append('\'');
					etymLevelBuf.append(StringUtils.join(etymMeaningWords, ", "));
					etymLevelBuf.append('\'');
				}
				if (ArrayUtils.isNotEmpty(etymWordSources)) {
					etymLevelBuf.append(' ');
					etymLevelBuf.append('(');
					etymLevelBuf.append(StringUtils.join(etymWordSources, ", "));
					if (StringUtils.isNotBlank(etymLevelMember.getEtymYear())) {
						etymLevelBuf.append(' ');
						etymLevelBuf.append(etymLevelMember.getEtymYear());
					}
					etymLevelBuf.append(')');
				}
				if (ArrayUtils.isNotEmpty(comments)) {
					if (membersHaveSameComment && (etymLevelMemberIndex == etymLevelGroupSize - 1)) {
						etymLevelBuf.append(". ");
						etymLevelBuf.append(StringUtils.join(comments, " "));
					} else if (!membersHaveSameComment) {
						etymLevelBuf.append(". ");
						etymLevelBuf.append(StringUtils.join(comments, " "));
					}
				}
			}
			String etymWrapup = StringUtils.trim(etymLevelBuf.toString());
			if (wordEtymologyLineupWrapup.contains(etymWrapup)) {
				continue;
			}
			wordEtymologyLineupWrapup.add(etymWrapup);
		}
	}

	public void composeWordRelations(Word word, List<WordRelationTuple> wordRelationTuples, String[] datasets, String displayLang) {

		if (CollectionUtils.isEmpty(wordRelationTuples)) {
			return;
		}
		List<String> datasetsList = Arrays.asList(datasets);
		List<Classifier> wordRelTypes = classifierUtil.getClassifiers(ClassifierName.WORD_REL_TYPE, displayLang);
		List<String> wordRelTypeCodes = wordRelTypes.stream().map(Classifier::getCode).collect(Collectors.toList());
		word.setWordGroups(new ArrayList<>());
		word.setRelatedWords(new ArrayList<>());
		for (WordRelationTuple tuple : wordRelationTuples) {
			List<TypeWordRelation> relatedWords = tuple.getRelatedWords();
			if (CollectionUtils.isNotEmpty(relatedWords)) {
				relatedWords = relatedWords.stream().filter(relation -> CollectionUtils.containsAny(relation.getDatasetCodes(), datasetsList)).collect(Collectors.toList());
			}
			if(CollectionUtils.isNotEmpty(relatedWords)) {
				word.getRelatedWords().addAll(relatedWords);
			}
			if (CollectionUtils.isNotEmpty(relatedWords)) {
				for (TypeWordRelation wordRelation : relatedWords) {
					classifierUtil.applyClassifiers(wordRelation, displayLang);
					setWordTypeFlags(wordRelation);
				}
				word.setLimitedRelatedWordTypeGroups(new ArrayList<>());
				word.setRelatedWordTypeGroups(new ArrayList<>());
				Map<String, List<TypeWordRelation>> relatedWordsMap = relatedWords.stream().collect(Collectors.groupingBy(TypeWordRelation::getWordRelTypeCode));
				for (String wordRelTypeCode : wordRelTypeCodes) {
					List<TypeWordRelation> relatedWordsOfType = relatedWordsMap.get(wordRelTypeCode);
					if (CollectionUtils.isNotEmpty(relatedWordsOfType)) {
						Classifier wordRelType = relatedWordsOfType.get(0).getWordRelType();
						WordRelationGroup wordRelationGroup = new WordRelationGroup();
						wordRelationGroup.setWordRelType(wordRelType);
						wordRelationGroup.setRelatedWords(relatedWordsOfType);
						word.getRelatedWordTypeGroups().add(wordRelationGroup);
					}
				}
				int limitedRelatedWordCounter = 0;
				for (WordRelationGroup wordRelationGroup : word.getRelatedWordTypeGroups()) {
					if (limitedRelatedWordCounter >= WORD_RELATIONS_DISPLAY_LIMIT) {
						break;
					}
					wordRelationGroup.getRelatedWords();
					List<TypeWordRelation> relatedWordsOfType = wordRelationGroup.getRelatedWords();
					int maxLimit = Math.min(relatedWordsOfType.size(), WORD_RELATIONS_DISPLAY_LIMIT - limitedRelatedWordCounter);
					List<TypeWordRelation> limitedRelatedWordsOfType = relatedWordsOfType.subList(0, maxLimit);
					if (CollectionUtils.isNotEmpty(limitedRelatedWordsOfType)) {
						WordRelationGroup limitedWordRelationGroup = new WordRelationGroup();
						limitedWordRelationGroup.setWordRelType(wordRelationGroup.getWordRelType());
						limitedWordRelationGroup.setRelatedWords(limitedRelatedWordsOfType);
						word.getLimitedRelatedWordTypeGroups().add(limitedWordRelationGroup);
						limitedRelatedWordCounter += limitedRelatedWordsOfType.size();
					}
				}
			}
			List<TypeWordRelation> wordGroupMembers = tuple.getWordGroupMembers();
			if (CollectionUtils.isNotEmpty(wordGroupMembers)) {
				wordGroupMembers = wordGroupMembers.stream().filter(member -> CollectionUtils.containsAny(member.getDatasetCodes(), datasetsList)).collect(Collectors.toList());
			}
			if (CollectionUtils.isNotEmpty(wordGroupMembers)) {
				for (TypeWordRelation wordGroupMember : wordGroupMembers) {
					classifierUtil.applyClassifiers(wordGroupMember, displayLang);
					setWordTypeFlags(wordGroupMember);
				}
				WordGroup wordGroup = new WordGroup();
				wordGroup.setWordGroupId(tuple.getWordGroupId());
				wordGroup.setWordRelTypeCode(tuple.getWordRelTypeCode());
				wordGroup.setWordGroupMembers(wordGroupMembers);
				classifierUtil.applyClassifiers(wordGroup, displayLang);
				word.getWordGroups().add(wordGroup);
			}
		}
		boolean wordRelationsExist = CollectionUtils.isNotEmpty(word.getRelatedWords()) || CollectionUtils.isNotEmpty(word.getWordGroups());
		word.setWordRelationsExist(wordRelationsExist);
		boolean isMoreWordRelations = CollectionUtils.size(word.getRelatedWords()) > WORD_RELATIONS_DISPLAY_LIMIT;
		word.setMoreWordRelations(isMoreWordRelations);
	}

	public List<Paradigm> composeParadigms(Word word, Map<Long, List<Form>> paradigmFormsMap, String displayLang) {

		final String keyValSep = "-";

		String wordClass = word.getWordClass();

		List<Paradigm> paradigms = new ArrayList<>();
		List<Long> paradigmIds = new ArrayList<>(paradigmFormsMap.keySet());
		Collections.sort(paradigmIds);

		ParadigmGroup paradigmGroup1;
		ParadigmGroup paradigmGroup2;
		ParadigmGroup paradigmGroup3;
		String formGroupKey;
		List<Form> groupForms;
		List<ParadigmGroup> validParadigmGroups;
		Form firstForm;
		List<String> paradigmTitleElements;

		for (Long paradigmId : paradigmIds) {

			List<Form> forms = paradigmFormsMap.get(paradigmId);
			forms.sort(Comparator.comparing(Form::getOrderBy));

			for (Form form : forms) {
				classifierUtil.applyClassifiers(form, displayLang);
			}

			paradigmTitleElements = new ArrayList<>();
			if (StringUtils.isNotBlank(wordClass)) {
				paradigmTitleElements.add(wordClass);
			}
			firstForm = forms.get(0);
			if (StringUtils.isNotBlank(firstForm.getInflectionType())) {
				paradigmTitleElements.add(firstForm.getInflectionType());
			}
			String paradigmTitle = null;
			if (CollectionUtils.isNotEmpty(paradigmTitleElements)) {
				paradigmTitle = StringUtils.join(paradigmTitleElements, ", ");
			}
			boolean isExpandable = forms.stream().anyMatch(form -> form.getDisplayLevel() > 1);

			Paradigm paradigm = new Paradigm();
			paradigm.setParadigmId(paradigmId);
			paradigm.setTitle(paradigmTitle);
			paradigm.setGroups(new ArrayList<>());
			paradigm.setExpandable(isExpandable);
			paradigms.add(paradigm);

			Map<String, List<Form>> formGroupsMap = forms.stream()
					.collect(Collectors.groupingBy(form -> {
						return form.getMorphGroup1() + keyValSep + form.getMorphGroup2() + keyValSep + form.getMorphGroup3();
					}));

			List<String> morphGroup1Names = forms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup1()))
					.map(Form::getMorphGroup1).distinct().collect(Collectors.toList());
			List<String> morphGroup2Names = forms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup2()))
					.map(Form::getMorphGroup2).distinct().collect(Collectors.toList());
			List<String> morphGroup3Names = forms.stream().filter(form -> StringUtils.isNotBlank(form.getMorphGroup3()))
					.map(Form::getMorphGroup3).distinct().collect(Collectors.toList());

			if (CollectionUtils.isEmpty(morphGroup1Names)) {
				ParadigmGroup paradigmGroup = new ParadigmGroup();
				paradigm.getGroups().add(paradigmGroup);
				distributeParadigmGroupForms(null, paradigmGroup, forms);
			} else {
				for (String morphGroup1Name : morphGroup1Names) {
					paradigmGroup1 = newParadigmGroup(morphGroup1Name);
					paradigm.getGroups().add(paradigmGroup1);
					if (CollectionUtils.isEmpty(morphGroup2Names)) {
						formGroupKey = morphGroup1Name + "-null-null";
						groupForms = formGroupsMap.get(formGroupKey);
						if (CollectionUtils.isEmpty(groupForms)) {
							continue;
						}
						distributeParadigmGroupForms(morphGroup1Names, paradigmGroup1, groupForms);
					} else {
						for (String morphGroup2Name : morphGroup2Names) {
							paradigmGroup2 = newParadigmGroup(morphGroup2Name);
							paradigmGroup1.getGroups().add(paradigmGroup2);
							if (CollectionUtils.isEmpty(morphGroup3Names)) {
								formGroupKey = morphGroup1Name + keyValSep + morphGroup2Name + "-null";
								groupForms = formGroupsMap.get(formGroupKey);
								if (CollectionUtils.isEmpty(groupForms)) {
									continue;
								}
								distributeParadigmGroupForms(morphGroup2Names, paradigmGroup2, groupForms);
							} else {
								for (String morphGroup3Name : morphGroup3Names) {
									formGroupKey = morphGroup1Name + keyValSep + morphGroup2Name + keyValSep + morphGroup3Name;
									groupForms = formGroupsMap.get(formGroupKey);
									if (CollectionUtils.isEmpty(groupForms)) {
										continue;
									}
									paradigmGroup3 = newParadigmGroup(morphGroup3Name);
									paradigmGroup2.getGroups().add(paradigmGroup3);
									distributeParadigmGroupForms(morphGroup3Names, paradigmGroup3, groupForms);
									calculateFormDisplayFlags(paradigmGroup3);
								}
							}
							validParadigmGroups = paradigmGroup2.getGroups().stream()
									.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
									.collect(Collectors.toList());
							paradigmGroup2.setGroups(validParadigmGroups);
							calculateFormDisplayFlags(paradigmGroup2);
						}
					}
					validParadigmGroups = paradigmGroup1.getGroups().stream()
							.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
							.collect(Collectors.toList());
					paradigmGroup1.setGroups(validParadigmGroups);
					calculateFormDisplayFlags(paradigmGroup1);
				}
				validParadigmGroups = paradigm.getGroups().stream()
						.filter(paradigmGroup -> paradigmGroup.isFormsExist() || paradigmGroup.isGroupsExist())
						.collect(Collectors.toList());
				paradigm.setGroups(validParadigmGroups);
			}
		}
		return paradigms;
	}

	private void calculateFormDisplayFlags(ParadigmGroup paradigmGroup) {
		List<Form> groupFormsTest = new ArrayList<>();
		groupFormsTest.addAll(paradigmGroup.getForms1());
		groupFormsTest.addAll(paradigmGroup.getForms2());
		boolean formsExist = CollectionUtils.isNotEmpty(groupFormsTest);
		boolean primaryFormsExist = groupFormsTest.stream().anyMatch(form -> form.getDisplayLevel() == 1);
		boolean groupsExist = CollectionUtils.isNotEmpty(paradigmGroup.getGroups());
		paradigmGroup.setFormsExist(formsExist);
		paradigmGroup.setPrimaryFormsExist(primaryFormsExist);
		paradigmGroup.setGroupsExist(groupsExist);
	}

	private ParadigmGroup newParadigmGroup(String morphGroupName) {
		ParadigmGroup paradigmGroup = new ParadigmGroup();
		paradigmGroup.setName(morphGroupName);
		paradigmGroup.setForms1(new ArrayList<>());
		paradigmGroup.setForms2(new ArrayList<>());
		paradigmGroup.setGroups(new ArrayList<>());
		return paradigmGroup;
	}

	private void distributeParadigmGroupForms(List<String> morphGroupNames, ParadigmGroup paradigmGroup, List<Form> groupForms) {

		List<String> groupMorphCodes = groupForms.stream().map(Form::getMorphCode).distinct().collect(Collectors.toList());
		Map<String, List<Form>> groupFormsByMorph = groupForms.stream().collect(Collectors.groupingBy(Form::getMorphCode));
		List<Form> groupedForms = new ArrayList<>();
		List<Form> morphForms;
		Form morphForm;
		List<String> forms;
		List<String> displayForms;
		String formsWrapup;
		String displayFormsWrapup;
		for (String morphCode : groupMorphCodes) {
			morphForms = groupFormsByMorph.get(morphCode);
			if (morphForms.size() > 1) {
				morphForms.sort(Comparator.comparing(Form::getDisplayLevel));
				morphForm = morphForms.get(0);
				forms = morphForms.stream().map(Form::getForm).collect(Collectors.toList());
				formsWrapup = StringUtils.join(forms, ALTERNATIVE_FORMS_SEPARATOR);
				displayForms = morphForms.stream().map(Form::getDisplayForm).collect(Collectors.toList());
				displayFormsWrapup = StringUtils.join(displayForms, ALTERNATIVE_FORMS_SEPARATOR);
			} else {
				morphForm = morphForms.get(0);
				formsWrapup = morphForm.getForm();
				displayFormsWrapup = morphForm.getDisplayForm();
			}
			if (StringUtils.isBlank(displayFormsWrapup)) {
				displayFormsWrapup = "-";
			}
			morphForm.setFormsWrapup(formsWrapup);
			morphForm.setDisplayFormsWrapup(displayFormsWrapup);
			groupedForms.add(morphForm);
		}
		if (CollectionUtils.isEmpty(morphGroupNames)) {
			paradigmGroup.setForms1(groupedForms);
		} else if (morphGroupNames.size() > 2) {
			paradigmGroup.getForms1().addAll(groupedForms);
		} else {
			if (CollectionUtils.isEmpty(paradigmGroup.getForms1())) {
				paradigmGroup.setForms1(groupedForms);
			} else if (CollectionUtils.isEmpty(paradigmGroup.getForms2())) {
				paradigmGroup.setForms2(groupedForms);
			}
		}
	}

	private boolean isEmptyLexeme(Lexeme lexeme) {
		return CollectionUtils.isEmpty(lexeme.getDefinitions()) &&
				//CollectionUtils.isEmpty(lexeme.getSecondaryCollocations()) && //TODO temporarily disabled
				CollectionUtils.isEmpty(lexeme.getCollocationPosGroups()) &&
				CollectionUtils.isEmpty(lexeme.getDomains()) &&
				CollectionUtils.isEmpty(lexeme.getGovernments()) &&
				CollectionUtils.isEmpty(lexeme.getUsages()) &&
				CollectionUtils.isEmpty(lexeme.getOtherLangMatchWords()) &&
				CollectionUtils.isEmpty(lexeme.getDestinLangMatchWords()) &&
				CollectionUtils.isEmpty(lexeme.getRegisters());
	}

}
