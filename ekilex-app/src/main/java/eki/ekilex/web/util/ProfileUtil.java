package eki.ekilex.web.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Relation;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.UserService;

@Component
public class ProfileUtil {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonDataService commonDataService;

	public boolean showMeaningRelationMeaningId() {

		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		boolean showMeaningRelationMeaningId = userProfile.isShowMeaningRelationMeaningId();
		return showMeaningRelationMeaningId;
	}

	public List<Relation> getFilteredMeaningRelations(List<Relation> meaningRelations) {
		return getFilteredMeaningRelations(meaningRelations, null);
	}

	public List<Relation> getFilteredMeaningRelations(List<Relation> meaningRelations, String sourceLang) {

		List<String> allLangs = commonDataService.getLanguageCodes();
		Long userId = userService.getAuthenticatedUser().getId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		boolean showFirstWordOnly = userProfile.isShowMeaningRelationFirstWordOnly();
		boolean showDatasets = userProfile.isShowMeaningRelationWordDatasets();
		boolean showSourceLangWords = userProfile.isShowLexMeaningRelationSourceLangWords();

		List<String> prefWordLangs;
		if (showSourceLangWords && StringUtils.isNotEmpty(sourceLang)) {
			prefWordLangs = Collections.singletonList(sourceLang);
		} else {
			prefWordLangs = userProfile.getPreferredMeaningRelationWordLangs();
		}

		filterLangs(prefWordLangs, allLangs, meaningRelations, showFirstWordOnly);

		if (!showDatasets) {
			meaningRelations.forEach(relation -> relation.setWordLexemeDatasetCodes(Collections.emptyList()));
		}

		return meaningRelations;
	}

	private void filterLangs(List<String> wordLangs, List<String> allLangs, List<Relation> meaningRelations, boolean showFirstWordOnly) {

		boolean relationLangIsInWordLangs = meaningRelations.stream().anyMatch(relation -> wordLangs.contains(relation.getWordLang()));
		if (relationLangIsInWordLangs) {
			meaningRelations.removeIf(relation -> !wordLangs.contains(relation.getWordLang()));
		} else {
			for (String lang : allLangs) {
				boolean relationLangEqualsLang = meaningRelations.stream().anyMatch(relation -> lang.equals(relation.getWordLang()));
				if (relationLangEqualsLang) {
					meaningRelations.removeIf(relation -> !lang.equals(relation.getWordLang()));
					break;
				}
			}
		}

		if (showFirstWordOnly) {
			Iterator<Relation> relationIterator = meaningRelations.iterator();
			Set<String> occurredLangs = new HashSet<>();
			while (relationIterator.hasNext()) {
				String iteratorLang = relationIterator.next().getWordLang();
				if (occurredLangs.contains(iteratorLang)) {
					relationIterator.remove();
				} else {
					occurredLangs.add(iteratorLang);
				}
			}
		}
	}

}
