package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.HttpClientErrorException;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.FreeformType;
import eki.common.constant.GlobalConstant;
import eki.common.constant.SourceType;
import eki.common.constant.TagType;
import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ComplexitySelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.TagService;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.ValueUtil;

@PreAuthorize("isAuthenticated() && @permEval.isActiveTermsAgreed(authentication)")
public abstract class AbstractAuthActionController implements WebConstant, SystemConstant, GlobalConstant {

	@Autowired
	protected UserContext userContext;

	@Autowired
	protected UserProfileService userProfileService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected LookupService lookupService;

	@Autowired
	protected TagService tagService;

	@Autowired
	protected ValueUtil valueUtil;

	@Autowired
	protected MessageSource messageSource;

	protected String getDatasetCodeFromRole() {
		EkiUser user = userContext.getUser();
		DatasetPermission role = user.getRecentRole();
		if (role == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Role has to be selected");
		}
		return role.getDatasetCode();
	}

	protected UserContextData getUserContextData() {
		EkiUser user = userContext.getUser();
		Long userId = user.getId();
		String userName = user.getName();
		DatasetPermission userRole = user.getRecentRole();
		String userRoleDatasetCode = null;
		if (userRole != null) {
			userRoleDatasetCode = userRole.getDatasetCode();
		}
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		String activeTagName = userProfile.getActiveTagName();
		Tag activeTag = tagService.getTag(activeTagName);
		List<String> preferredTagNames = userProfile.getPreferredTagNames();
		List<String> preferredDatasetCodes = userProfile.getPreferredDatasets();
		List<String> partSynCandidateLangCodes = userProfile.getPreferredPartSynCandidateLangs();
		List<String> synMeaningWordLangCodes = userProfile.getPreferredSynLexMeaningWordLangs();
		String fullSynCandidateLangCode = userProfile.getPreferredFullSynCandidateLang();
		String fullSynCandidateDatasetCode = userProfile.getPreferredFullSynCandidateDatasetCode();

		return new UserContextData(
				userId, userName, userRole, userRoleDatasetCode, activeTag, preferredTagNames, preferredDatasetCodes,
				partSynCandidateLangCodes, synMeaningWordLangCodes, fullSynCandidateLangCode, fullSynCandidateDatasetCode);
	}

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
			sessionBean.setManualEventOnUpdateEnabled(true);
			model.addAttribute(SESSION_BEAN, sessionBean);
		}
		return sessionBean;
	}

	protected List<String> getUserPreferredDatasetCodes() {

		Long userId = userContext.getUserId();
		EkiUserProfile userProfile = userProfileService.getUserProfile(userId);
		return userProfile.getPreferredDatasets();
	}

	@ModelAttribute("allDatasets")
	public List<Dataset> getAllDatasets() {
		return commonDataService.getVisibleDatasets();
	}

	@ModelAttribute("userVisibleDatasets")
	public List<Dataset> getUserVisibleDatasets() {
		Long userId = userContext.getUserId();
		return permissionService.getUserVisibleDatasets(userId);
	}

	@ModelAttribute("userRoleLanguages")
	public List<Classifier> getUserRoleLanguages() {

		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		if (userRole.isSuperiorPermission()) {
			return commonDataService.getLanguages();
		}
		Long userId = user.getId();
		String datasetCode = userRole.getDatasetCode();
		String authLang = userRole.getAuthLang();

		List<Classifier> userPermLanguages = permissionService.getUserDatasetLanguages(userId, datasetCode);
		List<Classifier> datasetLanguages = commonDataService.getDatasetLanguages(datasetCode);

		if (StringUtils.isNotBlank(authLang)) {
			datasetLanguages.removeIf(classifier -> !StringUtils.equals(classifier.getCode(), authLang));
		}
		List<Classifier> accessibleLanguages = userPermLanguages.stream().filter(datasetLanguages::contains).collect(Collectors.toList());
		return accessibleLanguages;
	}

	@ModelAttribute("userOwnedDatasets")
	public List<Dataset> getUserOwnedDatasets() {
		Long userId = userContext.getUserId();
		return permissionService.getUserOwnedDatasets(userId);
	}

	@ModelAttribute("userDatasetPermissions")
	public List<DatasetPermission> getUserDatasetPermissions() {
		Long userId = userContext.getUserId();
		return permissionService.getUserDatasetPermissions(userId);
	}

	@ModelAttribute("allTags")
	public List<String> getAllTags() {
		return commonDataService.getAllTags();
	}

	@ModelAttribute("lexemeTags")
	public List<String> getLexemeTags() {
		return commonDataService.getLexemeTags();
	}

	@ModelAttribute("meaningTags")
	public List<String> getMeaningTags() {
		return commonDataService.getMeaningTags();
	}

	@ModelAttribute("tagTypes")
	public List<TagType> getTagTypes() {
		return Arrays.asList(TagType.class.getEnumConstants());
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("ekiMarkupElements")
	public CodeValue[] getEkiMarkupElements() {
		return TextDecoration.EKI_MARKUP_ELEMENTS;
	}

	@ModelAttribute("sourceTypes")
	public List<SourceType> getSourceTypes() {
		return Arrays.asList(SourceType.class.getEnumConstants());
	}

	@ModelAttribute("sourcePropertyTypes")
	public List<FreeformType> getSourcePropertyTypes() {

		return Arrays.asList(FreeformType.SOURCE_AUTHOR, FreeformType.SOURCE_NAME, FreeformType.EXTERNAL_SOURCE_ID, FreeformType.NOTE,
				FreeformType.SOURCE_ISBN, FreeformType.SOURCE_ISSN, FreeformType.SOURCE_WWW, FreeformType.SOURCE_FILE, FreeformType.SOURCE_PUBLISHER,
				FreeformType.SOURCE_PUBLICATION_NAME, FreeformType.SOURCE_PUBLICATION_PLACE, FreeformType.SOURCE_PUBLICATION_YEAR, FreeformType.SOURCE_CELEX,
				FreeformType.SOURCE_RT, FreeformType.SOURCE_EXPLANATION, FreeformType.SOURCE_ARTICLE_TITLE, FreeformType.SOURCE_ARTICLE_AUTHOR);
	}

	@ModelAttribute("complexities")
	public List<ComplexitySelect> getComplexities() {

		List<ComplexitySelect> complexities = new ArrayList<>();
		complexities.add(new ComplexitySelect(Complexity.ANY, false));
		complexities.add(new ComplexitySelect(Complexity.SIMPLE, false));
		complexities.add(new ComplexitySelect(Complexity.DETAIL, false));
		complexities.add(new ComplexitySelect(Complexity.SIMPLE1, true));
		complexities.add(new ComplexitySelect(Complexity.DETAIL1, true));
		complexities.add(new ComplexitySelect(Complexity.SIMPLE2, true));
		complexities.add(new ComplexitySelect(Complexity.DETAIL2, true));
		return complexities;
	}

	@ModelAttribute("enabledComplexities")
	public List<Complexity> getEnabledComplexities() {
		return Arrays.asList(Complexity.ANY, Complexity.SIMPLE, Complexity.DETAIL);
	}

	@ModelAttribute("enabledDatasetTypes")
	public List<DatasetType> getEnabledDatasetTypes() {
		return Arrays.asList(DatasetType.LEX, DatasetType.TERM);
	}

	@ModelAttribute("wordGenders")
	public List<Classifier> getWordGenders() {
		return commonDataService.getGenders();
	}

	@ModelAttribute("wordAspects")
	public List<Classifier> getWordAspect() {
		return commonDataService.getAspects();
	}

	@ModelAttribute("wordRelationTypes")
	public List<Classifier> getWordRelationTypes() {
		return commonDataService.getWordRelationTypes();
	}

	@ModelAttribute("wordDisplayMorphs")
	public List<Classifier> getWordDisplayMorphs() {
		return commonDataService.getDisplayMorphs();
	}

	@ModelAttribute("groupWordRelationTypes")
	public List<Classifier> getGroupWordRelationTypes() {
		List<Classifier> allWordRelationTypes = commonDataService.getWordRelationTypes();
		return allWordRelationTypes.stream()
				.filter(wordRelType -> !ArrayUtils.contains(PRIMARY_WORD_REL_TYPE_CODES, wordRelType.getCode()))
				.collect(Collectors.toList());
	}

	@ModelAttribute("lexemeReliabilities")
	public List<Integer> getLexemeReliabilities() {
		List<Integer> lexemeReliabilities = new ArrayList<>();
		lexemeReliabilities.add(null);
		lexemeReliabilities.add(1);
		lexemeReliabilities.add(2);
		lexemeReliabilities.add(3);
		lexemeReliabilities.add(4);
		lexemeReliabilities.add(5);
		return lexemeReliabilities;
	}

	@ModelAttribute("lexemeRelationTypes")
	public List<Classifier> getLexemeRelationTypes() {
		return commonDataService.getLexemeRelationTypes();
	}

	@ModelAttribute("meaningRelationTypes")
	public List<Classifier> getMeaningRelationTypes() {
		List<Classifier> meaningRelationTypes = commonDataService.getMeaningRelationTypes();
		return meaningRelationTypes.stream()
				.filter(meaningRelType -> !StringUtils.equals(meaningRelType.getCode(), MEANING_REL_TYPE_CODE_SIMILAR))
				.collect(Collectors.toList());
	}
}
