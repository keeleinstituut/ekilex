package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.common.constant.Complexity;
import eki.common.constant.FreeformType;
import eki.common.constant.SourceType;
import eki.common.constant.TextDecoration;
import eki.common.data.CodeValue;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ComplexitySelect;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.EkiUserProfile;
import eki.ekilex.service.CommonDataService;
import eki.ekilex.service.PermissionService;
import eki.ekilex.web.bean.SessionBean;
import eki.ekilex.web.util.ValueUtil;

@PreAuthorize("principal.enabled == true && isAuthenticated()")
public abstract class AbstractPrivatePageController extends AbstractAuthActionController {

	@Autowired
	protected CommonDataService commonDataService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected ValueUtil valueUtil;

	protected SessionBean getSessionBean(Model model) {
		SessionBean sessionBean = (SessionBean) model.asMap().get(SESSION_BEAN);
		if (sessionBean == null) {
			sessionBean = new SessionBean();
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

	@ModelAttribute("tags")
	public List<String> getTags() {
		return commonDataService.getTags();
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
