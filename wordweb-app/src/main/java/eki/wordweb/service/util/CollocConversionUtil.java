package eki.wordweb.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;

import eki.common.constant.Complexity;
import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.data.Collocation;
import eki.wordweb.data.CollocationPosGroup;
import eki.wordweb.data.CollocationRelGroup;
import eki.wordweb.data.CollocationTuple;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.type.TypeCollocMember;

@Component
public class CollocConversionUtil extends AbstractConversionUtil {

	@Autowired
	private ClassifierUtil classifierUtil;

	public void compose(Long wordId, List<LexemeWord> lexemeWords, List<CollocationTuple> collocTuples, SearchContext searchContext, String displayLang) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		if (CollectionUtils.isEmpty(collocTuples)) {
			return;
		}

		Map<Long, LexemeWord> lexemeMap = new HashMap<>();
		for (LexemeWord lexemeWord : lexemeWords) {
			Long lexemeId = lexemeWord.getLexemeId();
			if (lexemeMap.containsKey(lexemeId)) {
				continue;
			}
			lexemeMap.put(lexemeId, lexemeWord);
		}

		Complexity lexComplexity = searchContext.getLexComplexity();
		collocTuples = filter(collocTuples, lexComplexity);
		collocTuples = filterCollocsByMostMembers(collocTuples);

		Map<Long, CollocationPosGroup> collocPosGroupMap = new HashMap<>();
		Map<Long, CollocationRelGroup> collocRelGroupMap = new HashMap<>();

		for (CollocationTuple tuple : collocTuples) {

			Long lexemeId = tuple.getLexemeId();
			LexemeWord lexemeWord = lexemeMap.get(lexemeId);
			if (lexemeWord == null) {
				continue;
			}

			CollocationPosGroup collocPosGroup = populateCollocPosGroup(lexemeWord, tuple, collocPosGroupMap, displayLang);
			CollocationRelGroup collocRelGroup = populateCollocRelGroup(collocPosGroup, tuple, collocRelGroupMap);
			Collocation collocation = populateCollocation(tuple);

			if (collocPosGroup != null) {
				collocRelGroup.getCollocations().add(collocation);
			}
		}

		for (LexemeWord lexemeWord : lexemeWords) {
			if (CollectionUtils.isEmpty(lexemeWord.getCollocationPosGroups())) {
				continue;
			}
			divideCollocationRelGroupsByCollocMemberForms(wordId, lexemeWord);
			transformCollocationPosGroupsForDisplay(wordId, lexemeWord);
		}
	}

	private List<CollocationTuple> filterCollocsByMostMembers(List<CollocationTuple> collocTuples) {
		Map<Long, Map<String, Optional<Integer>>> collocRelGroupCollocMostMemSizeMap = collocTuples.stream()
				.collect(Collectors
						.groupingBy(CollocationTuple::getRelGroupId, Collectors
								.groupingBy(CollocationTuple::getCollocValue, Collectors
										.mapping(tuple -> tuple.getCollocMembers().size(), Collectors.maxBy(Integer::compare)))));

		collocTuples = collocTuples.stream()
				.filter(tuple -> {
					Map<String, Optional<Integer>> collocRelGroupMap = collocRelGroupCollocMostMemSizeMap.get(tuple.getRelGroupId());
					Integer collocMostMemSize = collocRelGroupMap.get(tuple.getCollocValue()).get();
					boolean isCollocMostMemSize = collocMostMemSize == tuple.getCollocMembers().size();
					return isCollocMostMemSize;
				}).collect(Collectors.toList());
		return collocTuples;
	}

	public CollocationPosGroup populateCollocPosGroup(LexemeWord lexemeWord, CollocationTuple tuple, Map<Long, CollocationPosGroup> collocPosGroupMap, String displayLang) {
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
				lexemeWord.getCollocationPosGroups().add(collocPosGroup);
			}
		}
		return collocPosGroup;
	}

	public CollocationRelGroup populateCollocRelGroup(CollocationPosGroup collocPosGroup, CollocationTuple tuple, Map<Long, CollocationRelGroup> collocRelGroupMap) {
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

	public Collocation populateCollocation(CollocationTuple tuple) {
		Collocation collocation = new Collocation();
		collocation.setValue(tuple.getCollocValue());
		collocation.setDefinition(tuple.getCollocDefinition());
		collocation.setCollocUsages(tuple.getCollocUsages());
		collocation.setCollocMembers(tuple.getCollocMembers());
		return collocation;
	}

	public void divideCollocationRelGroupsByCollocMemberForms(Long wordId, LexemeWord lexemeWord) {

		List<CollocationPosGroup> collocationPosGroups = lexemeWord.getCollocationPosGroups();
		for (CollocationPosGroup collocPosGroup : collocationPosGroups) {
			List<CollocationRelGroup> collocRelGroups = collocPosGroup.getRelationGroups();
			List<CollocationRelGroup> dividedCollocRelGroups = new ArrayList<>();
			for (CollocationRelGroup collocRelGroup : collocRelGroups) {
				List<Collocation> collocs = collocRelGroup.getCollocations();
				Map<String, List<Collocation>> collocRelGroupDivisionMap = collocs.stream()
						.collect(Collectors.groupingBy(col -> col.getCollocMembers().stream()
								.filter(colm -> colm.getWordId().equals(wordId))
								.map(TypeCollocMember::getForm).findFirst().orElse("-")));
				if (MapUtils.size(collocRelGroupDivisionMap) == 1) {
					dividedCollocRelGroups.add(collocRelGroup);
				} else {
					for (List<Collocation> dividedCollocs : collocRelGroupDivisionMap.values()) {
						CollocationRelGroup dividedCollocRelGroup = new CollocationRelGroup();
						dividedCollocRelGroup.setRelGroupId(collocRelGroup.getRelGroupId());
						dividedCollocRelGroup.setName(collocRelGroup.getName());
						dividedCollocRelGroup.setCollocations(dividedCollocs);
						dividedCollocRelGroups.add(dividedCollocRelGroup);
					}
				}
			}
			collocPosGroup.setRelationGroups(dividedCollocRelGroups);
		}
	}

	public void transformCollocationPosGroupsForDisplay(Long wordId, LexemeWord lexemeWord) {

		List<Collocation> collocations;
		List<DisplayColloc> displayCollocs;
		List<DisplayColloc> limitedPrimaryDisplayCollocs = new ArrayList<>();
		List<CollocationPosGroup> collocationPosGroups = lexemeWord.getCollocationPosGroups();
		List<String> existingCollocationValues = new ArrayList<>();
		for (CollocationPosGroup collocationPosGroup : collocationPosGroups) {
			List<CollocationRelGroup> collocationRelGroups = collocationPosGroup.getRelationGroups();
			for (CollocationRelGroup collocationRelGroup : collocationRelGroups) {
				displayCollocs = new ArrayList<>();
				collocationRelGroup.setDisplayCollocs(displayCollocs);
				List<String> allUsages = new ArrayList<>();
				collocationRelGroup.setAllUsages(allUsages);
				collocations = collocationRelGroup.getCollocations();
				collocationRelGroup.setCollocations(collocations);
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
		lexemeWord.setLimitedPrimaryDisplayCollocs(limitedPrimaryDisplayCollocs);
	}

	private void transformCollocationsForDisplay(
			Long wordId,
			List<Collocation> collocations,
			List<DisplayColloc> displayCollocs,
			List<String> allUsages,
			List<String> existingCollocationValues) {

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
			CollocMemberGroup recentCollocMemberGroup;
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
				if (!Objects.equals(recentCollocMemberGroup, currentCollocMemberGroup)) {
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
}
