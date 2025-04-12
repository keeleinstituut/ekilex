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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eki.common.constant.Complexity;
import eki.common.constant.DatasetType;
import eki.common.constant.FreeformConstant;
import eki.common.constant.FreeformOwner;
import eki.common.constant.GlobalConstant;
import eki.common.constant.SourceType;
import eki.common.constant.TagType;
import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.data.Origin;
import eki.ekilex.data.Tag;
import eki.ekilex.data.UserContextData;
import eki.ekilex.data.UserMessage;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.LookupService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.service.TagService;
import eki.ekilex.service.UserProfileService;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.util.ValueUtil;
import eki.ekilex.web.bean.SessionBean;

@PreAuthorize("isAuthenticated() && @permEval.isActiveTermsAgreed(authentication)")
public abstract class AbstractAuthActionController implements WebConstant, SystemConstant, GlobalConstant, FreeformConstant {

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

	protected String getRoleDatasetCode() {
		EkiUser user = userContext.getUser();
		DatasetPermission role = user.getRecentRole();
		if (role == null) {
			return DATASET_NA;
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
				userId, userName, user, userRole, userRoleDatasetCode, activeTag, preferredTagNames, preferredDatasetCodes,
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

	@ModelAttribute("userVisibleNonPublicDatasets")
	public List<Dataset> getUserVisibleNonPublicDatasets() {
		Long userId = userContext.getUserId();
		return permissionService.userVisibleNonPublicDatasets(userId);
	}

	@ModelAttribute("datasetsWithOwner")
	public List<Dataset> getDatasetsWithOwner() {
		return commonDataService.getVisibleDatasetsWithOwner();
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

	@ModelAttribute("availableFreeformTypes")
	public List<Classifier> getAvailableFreeformTypes() {
		return commonDataService.getAvailableFreeformTypes();
	}

	@ModelAttribute("wordFreeformTypes")
	public List<Classifier> getWordFreeformTypes() {
		return commonDataService.getFreeformTypes(FreeformOwner.WORD);
	}

	@ModelAttribute("userRoleWordFreeformTypes")
	public List<Classifier> getUserRoleWordFreeformTypes() {
		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		String datasetCode = userRole.getDatasetCode();
		return commonDataService.getFreeformTypes(datasetCode, FreeformOwner.WORD);
	}

	@ModelAttribute("lexemeFreeformTypes")
	public List<Classifier> getLexemeFreeformTypes() {
		return commonDataService.getFreeformTypes(FreeformOwner.LEXEME);
	}

	@ModelAttribute("userRoleLexemeFreeformTypes")
	public List<Classifier> getUserRoleLexemeFreeformTypes() {
		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		String datasetCode = userRole.getDatasetCode();
		return commonDataService.getFreeformTypes(datasetCode, FreeformOwner.LEXEME);
	}

	@ModelAttribute("meaningFreeformTypes")
	public List<Classifier> getMeaningFreeformTypes() {
		return commonDataService.getFreeformTypes(FreeformOwner.MEANING);
	}

	@ModelAttribute("userRoleMeaningFreeformTypes")
	public List<Classifier> getUserRoleMeaningFreeformTypes() {
		EkiUser user = userContext.getUser();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return Collections.emptyList();
		}
		String datasetCode = userRole.getDatasetCode();
		return commonDataService.getFreeformTypes(datasetCode, FreeformOwner.MEANING);
	}

	@ModelAttribute("languages")
	public List<Classifier> getLanguages() {
		return commonDataService.getLanguages();
	}

	@ModelAttribute("userRoleLanguagesExtended")
	public List<Classifier> getUserRoleLanguagesExtended() {

		EkiUser user = userContext.getUser();
		List<Classifier> userRoleLanguages = userProfileService.getUserRoleLanguagesExtended(user);
		return userRoleLanguages;
	}

	@ModelAttribute("userRoleLanguagesLimited")
	public List<Classifier> getUserRoleLanguagesLimited() {

		EkiUser user = userContext.getUser();
		List<Classifier> userRoleLanguages = userProfileService.getUserRoleLanguagesLimited(user);
		return userRoleLanguages;
	}

	@ModelAttribute("origins")
	public List<Origin> getOrigins() {
		return commonDataService.getDomainOrigins();
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

	@ModelAttribute("wordTags")
	public List<String> getWordTags() {
		return commonDataService.getWordTags();
	}

	@ModelAttribute("tagTypes")
	public List<TagType> getTagTypes() {
		return Arrays.asList(TagType.class.getEnumConstants());
	}

	@ModelAttribute("ekiMarkupElements")
	public CodeValue[] getEkiMarkupElements() {
		return TextDecoration.EKI_MARKUP_ELEMENTS;
	}

	@ModelAttribute("sourceTypes")
	public List<SourceType> getSourceTypes() {
		return Arrays.asList(SourceType.class.getEnumConstants());
	}

	@ModelAttribute("complexities")
	public List<Complexity> getComplexities() {
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

	protected void addRedirectSuccessMessage(RedirectAttributes redirectAttributes, String successMessageKey) {

		UserMessage userMessage = new UserMessage();
		userMessage.setSuccessMessageKey(successMessageKey);
		redirectAttributes.addFlashAttribute("userMessage", userMessage);
	}

	protected void addRedirectWarningMessage(RedirectAttributes redirectAttributes, String warningMessageKey) {

		UserMessage userMessage = new UserMessage();
		userMessage.setWarningMessageKey(warningMessageKey);
		redirectAttributes.addFlashAttribute("userMessage", userMessage);
	}

}
