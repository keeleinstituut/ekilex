package eki.wordweb.service.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;

import eki.wordweb.constant.CollocMemberGroup;
import eki.wordweb.data.Colloc;
import eki.wordweb.data.CollocMember;
import eki.wordweb.data.CollocPosGroup;
import eki.wordweb.data.CollocRelGroup;
import eki.wordweb.data.DisplayColloc;
import eki.wordweb.data.LexemeWord;
import eki.wordweb.data.SearchContext;
import eki.wordweb.data.Usage;
import eki.wordweb.data.WordCollocPosGroups;

@Component
public class CollocConversionUtil extends AbstractConversionUtil {

	@Autowired
	private ClassifierUtil classifierUtil;

	public void composeDisplay(Long wordId, List<LexemeWord> lexemeWords, List<WordCollocPosGroups> wordCollocPosGroups, SearchContext searchContext, String displayLang) {

		if (CollectionUtils.isEmpty(lexemeWords)) {
			return;
		}
		if (CollectionUtils.isEmpty(wordCollocPosGroups)) {
			return;
		}

		Map<Long, WordCollocPosGroups> lexemeCollocPosGroupsMap = wordCollocPosGroups.stream()
				.collect(Collectors.toMap(WordCollocPosGroups::getLexemeId, row -> row));

		for (LexemeWord lexemeWord : lexemeWords) {

			Long lexemeId = lexemeWord.getLexemeId();
			WordCollocPosGroups lexemeCollocPosGroups = lexemeCollocPosGroupsMap.get(lexemeId);
			if (lexemeCollocPosGroups == null) {
				continue;
			}
			List<CollocPosGroup> collocPosGroups = compensateDataQualityIssues(lexemeWord, lexemeCollocPosGroups);
			classifierUtil.applyClassifiers(collocPosGroups, displayLang);
			lexemeWord.setCollocPosGroups(collocPosGroups);
			divideCollocRelGroupsByCollocMemberForms(wordId, lexemeWord);
			transformCollocPosGroupsForDisplay(wordId, lexemeWord);
		}
	}

	private List<CollocPosGroup> compensateDataQualityIssues(LexemeWord lexemeWord, WordCollocPosGroups lexemeCollocPosGroups) {

		List<CollocPosGroup> collocPosGroups = lexemeCollocPosGroups.getPosGroups();
		List<CollocPosGroup> cleanCollocPosGroups = new ArrayList<>();

		for (CollocPosGroup collocPosGroup : collocPosGroups) {

			List<CollocRelGroup> collocRelGroups = collocPosGroup.getRelGroups();
			if (CollectionUtils.isEmpty(collocRelGroups)) {
				continue;
			}
			for (CollocRelGroup collocRelGroup : collocRelGroups) {

				List<Colloc> collocations = collocRelGroup.getCollocations();
				List<String> collocValues = collocations.stream()
						.map(Colloc::getWordValue)
						.distinct()
						.collect(Collectors.toList());
				Map<String, List<Colloc>> collocVersionMap = collocations.stream()
						.collect(Collectors.groupingBy(Colloc::getWordValue));
				List<Colloc> cleanCollocations = new ArrayList<>();

				for (String collocValue : collocValues) {

					List<Colloc> collocVersions = collocVersionMap.get(collocValue);
					Colloc preferredColloc = null;
					if (collocVersions.size() == 1) {
						preferredColloc = collocVersions.get(0);
					} else {
						for (Colloc collocVersion : collocVersions) {
							if (preferredColloc == null) {
								preferredColloc = collocVersion;
							} else {
								if (collocVersion.getMembers().size() > preferredColloc.getMembers().size()) {
									preferredColloc = collocVersion;
								}
							}
						}
					}
					cleanCollocations.add(preferredColloc);
				}
				collocRelGroup.setCollocations(cleanCollocations);
			}
			cleanCollocPosGroups.add(collocPosGroup);
		}

		return cleanCollocPosGroups;
	}

	private void divideCollocRelGroupsByCollocMemberForms(Long wordId, LexemeWord lexemeWord) {

		List<CollocPosGroup> collocPosGroups = lexemeWord.getCollocPosGroups();
		for (CollocPosGroup collocPosGroup : collocPosGroups) {

			List<CollocRelGroup> collocRelGroups = collocPosGroup.getRelGroups();
			List<CollocRelGroup> dividedCollocRelGroups = new ArrayList<>();

			for (CollocRelGroup collocRelGroup : collocRelGroups) {

				List<Colloc> collocs = collocRelGroup.getCollocations();

				Map<String, List<Colloc>> collocRelGroupDivisionMap = collocs.stream()
						.collect(Collectors.groupingBy(col -> col.getMembers().stream()
								.filter(colm -> colm.getWordId().equals(wordId))
								.map(CollocMember::getFormValue)
								.findFirst()
								.orElse("-")));

				if (MapUtils.size(collocRelGroupDivisionMap) == 1) {
					dividedCollocRelGroups.add(collocRelGroup);
				} else {
					for (List<Colloc> dividedCollocs : collocRelGroupDivisionMap.values()) {

						CollocRelGroup dividedCollocRelGroup = new CollocRelGroup();
						dividedCollocRelGroup.setRelGroupCode(collocRelGroup.getRelGroupCode());
						dividedCollocRelGroup.setCollocations(dividedCollocs);
						dividedCollocRelGroups.add(dividedCollocRelGroup);
					}
				}
			}
			collocPosGroup.setRelGroups(dividedCollocRelGroups);
		}
	}

	private void transformCollocPosGroupsForDisplay(Long wordId, LexemeWord lexemeWord) {

		List<DisplayColloc> displayCollocs;
		List<DisplayColloc> limitedPrimaryDisplayCollocs = new ArrayList<>();
		List<CollocPosGroup> collocPosGroups = lexemeWord.getCollocPosGroups();
		List<String> existingCollocationValues = new ArrayList<>();
		List<String> allUsageValues;
		for (CollocPosGroup collocationPosGroup : collocPosGroups) {

			List<CollocRelGroup> collocationRelGroups = collocationPosGroup.getRelGroups();
			for (CollocRelGroup collocationRelGroup : collocationRelGroups) {

				displayCollocs = new ArrayList<>();
				allUsageValues = new ArrayList<>();
				collocationRelGroup.setDisplayCollocs(displayCollocs);
				collocationRelGroup.setAllUsageValues(allUsageValues);
				transformCollocsForDisplay(wordId, collocationRelGroup, existingCollocationValues);
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

	private void transformCollocsForDisplay(
			Long wordId,
			CollocRelGroup collocRelGroup,
			List<String> existingCollocValues) {

		List<Colloc> collocations = collocRelGroup.getCollocations();
		List<DisplayColloc> displayCollocs = collocRelGroup.getDisplayCollocs();
		List<String> allUsageValues = collocRelGroup.getAllUsageValues();

		List<Usage> usages;
		List<CollocMember> collocMembers;
		List<CollocMemberGroup> existingMemberGroupOrder;
		List<String> collocMemberFormValues;
		DisplayColloc displayColloc;
		Map<String, DisplayColloc> collocMemberGroupMap = new HashMap<>();

		for (Colloc colloc : collocations) {

			String collocValue = colloc.getWordValue();
			usages = colloc.getUsages();
			collocMembers = colloc.getMembers();

			if (existingCollocValues.contains(collocValue)) {
				continue;
			}
			existingCollocValues.add(collocValue);

			if (CollectionUtils.isNotEmpty(usages)) {
				List<String> usageValues = usages.stream()
						.map(Usage::getValue)
						.collect(Collectors.toList());
				usageValues.removeAll(allUsageValues);
				allUsageValues.addAll(usageValues);
			}

			String collocMemberGroupKey = composeCollocMemberGroupKey(collocMembers);
			displayColloc = collocMemberGroupMap.get(collocMemberGroupKey);
			if (displayColloc == null) {
				displayColloc = new DisplayColloc();
				displayColloc.setMemberGroupOrder(new ArrayList<>());
				displayColloc.setPrimaryMembers(new ArrayList<>());
				displayColloc.setContextMembers(new ArrayList<>());
				displayColloc.setCollocMemberFormValues(new ArrayList<>());
				displayCollocs.add(displayColloc);
				collocMemberGroupMap.put(collocMemberGroupKey, displayColloc);
			}
			CollocMemberGroup recentCollocMemberGroup;
			CollocMemberGroup currentCollocMemberGroup;
			boolean headwordOrPrimaryMemberOccurred = false;
			List<CollocMemberGroup> currentMemberGroupOrder = new ArrayList<>();

			for (CollocMember collocMember : collocMembers) {

				String conjunct = collocMember.getConjunct();
				BigDecimal weight = collocMember.getWeight();
				boolean isHeadword = collocMember.getWordId().equals(wordId);
				boolean isPrimary = !isHeadword && weight.compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT_TRESHOLD) > 0;
				boolean isContext = weight.compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT_TRESHOLD) == 0;
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
				collocMemberFormValues = displayColloc.getCollocMemberFormValues();
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
						collocMemberFormValues.add(collocMember.getFormValue());
					}
				} else if (CollocMemberGroup.PRIMARY.equals(currentCollocMemberGroup)) {
					if (!collocMemberFormValues.contains(collocMember.getFormValue())) {
						displayColloc.getPrimaryMembers().add(collocMember);
						collocMemberFormValues.add(collocMember.getFormValue());
					}
				} else if (CollocMemberGroup.CONTEXT.equals(currentCollocMemberGroup)) {
					if (!collocMemberFormValues.contains(collocMember.getFormValue())) {
						displayColloc.getContextMembers().add(collocMember);
						collocMemberFormValues.add(collocMember.getFormValue());
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

	private String composeCollocMemberGroupKey(List<CollocMember> collocMembers) {
		List<String> headwordAndPrimaryMemberForms = collocMembers.stream()
				.filter(collocMember -> collocMember.getWeight().compareTo(COLLOC_MEMBER_CONTEXT_WEIGHT_TRESHOLD) > 0)
				.map(collocMember -> {
					String memberKey = "";
					if (StringUtils.isNotBlank(collocMember.getConjunct())) {
						memberKey = collocMember.getConjunct() + "|";
					}
					memberKey += collocMember.getFormValue();
					return memberKey;
				})
				.collect(Collectors.toList());
		String collocMemberGroupKey = StringUtils.join(headwordAndPrimaryMemberForms, '-');
		return collocMemberGroupKey;
	}

}
