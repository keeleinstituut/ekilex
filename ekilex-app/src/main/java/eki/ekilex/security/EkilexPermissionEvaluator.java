package eki.ekilex.security;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.common.constant.ContentKey;
import eki.common.constant.GlobalConstant;
import eki.common.constant.PermConstant;
import eki.common.exception.TermsNotAcceptedException;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.DefinitionNoteSourceLink;
import eki.ekilex.data.DefinitionSourceLink;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.FreeformSourceLink;
import eki.ekilex.data.LexemeNoteSourceLink;
import eki.ekilex.data.LexemeSourceLink;
import eki.ekilex.data.MeaningImageSourceLink;
import eki.ekilex.data.MeaningNoteSourceLink;
import eki.ekilex.data.SourceLink;
import eki.ekilex.data.SourceLinkOwner;
import eki.ekilex.data.UsageSourceLink;
import eki.ekilex.service.db.ActivityLogDbService;
import eki.ekilex.service.db.PermissionDbService;
import eki.ekilex.service.db.SourceLinkDbService;

@Component("permEval")
public class EkilexPermissionEvaluator implements PermissionEvaluator, PermConstant, GlobalConstant, ContentKey {

	@Autowired
	private PermissionDbService permissionDbService;

	@Autowired
	private SourceLinkDbService sourceLinkDbService;

	@Autowired
	private ActivityLogDbService activityLogDbService;

	// page perm

	public boolean isDatasetOwnerOrAdmin(Authentication authentication, String datasetCode) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			return false;
		}
		if (user.isAdmin()) {
			return true;
		}
		List<DatasetPermission> datasetPermissions = user.getDatasetPermissions();
		boolean isDatasetCrudPermExists = datasetPermissions.stream()
				.anyMatch(perm -> AUTH_OPS_CRUD.contains(perm.getAuthOperation().name()) && StringUtils.equals(perm.getDatasetCode(), datasetCode));
		return isDatasetCrudPermExists;
	}

	@Transactional
	public boolean isMutableDataPageAccessPermitted(Authentication authentication) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			return false;
		}
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		boolean crudPermExists = datasetPermissions.stream()
				.anyMatch(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& AUTH_OPS_CRUD.contains(datasetPermission.getAuthOperation().name()));
		return crudPermExists;
	}

	@Transactional
	public boolean isPrivatePageAccessPermitted(Authentication authentication) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			return false;
		}
		if (user.isAdmin()) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		boolean privateAccessPermExists = datasetPermissions.stream()
				.anyMatch(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& AUTH_OPS_READ.contains(datasetPermission.getAuthOperation().name())
						&& !StringUtils.equals(datasetPermission.getDatasetCode(), DATASET_LIMITED));
		return privateAccessPermExists;
	}

	@Transactional
	public boolean isLimitedPageAccessPermitted(Authentication authentication) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			return false;
		}
		if (user.isAdmin()) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		boolean limitedAccessPermExists = datasetPermissions.stream()
				.anyMatch(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& AUTH_OPS_READ.contains(datasetPermission.getAuthOperation().name())
						&& StringUtils.equals(datasetPermission.getDatasetCode(), DATASET_LIMITED));
		return limitedAccessPermExists;
	}

	public boolean isSynPageAccessPermitted(Authentication authentication) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		if (AuthorityOperation.READ.equals(userRole.getAuthOperation())) {
			return false;
		}
		return true;
	}

	public boolean isUserRoleSelected(Authentication authentication) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		DatasetPermission userRole = user.getRecentRole();
		if (userRole == null) {
			return false;
		}
		return true;
	}

	@Transactional
	public boolean isActiveTermsAgreed(Authentication authentication) throws Exception {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		boolean activeTermsAgreed = user.isActiveTermsAgreed();
		if (!activeTermsAgreed) {
			throw new TermsNotAcceptedException();
		}
		return true;
	}

	// dataset crud

	@Transactional
	public boolean isDatasetCrudGranted(Authentication authentication, String crudRoleDataset, String datasetCode) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		if (crudRole.isSuperiorDataset()) {
			return true;
		}
		boolean isGranted = StringUtils.equals(crudRoleDataset, datasetCode);
		return isGranted;
	}

	// source crud

	@Transactional
	public boolean isSourceCrudGranted(Authentication authentication, String crudRoleDataset, Long sourceId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		if (crudRole.isSuperiorDataset()) {
			return true;
		}
		boolean isGranted = permissionDbService.isGrantedForSource(userId, crudRole, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	// source link crud

	@Transactional
	public boolean isSourceLinkCrudGranted(Authentication authentication, String crudRoleDataset, String sourceContentKey, SourceLink sourceLink) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		Long sourceId = sourceLink.getSourceId();
		boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
		if (!isSourceCrudGranted) {
			return false;
		}
		if (StringUtils.equals(DEFINITION_SOURCE_LINK, sourceContentKey)) {
			DefinitionSourceLink definitionSourceLink = (DefinitionSourceLink) sourceLink;
			Long definitionId = definitionSourceLink.getDefinitionId();
			return permissionDbService.isGrantedForDefinition(userId, crudRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(DEFINITION_NOTE_SOURCE_LINK, sourceContentKey)) {
			DefinitionNoteSourceLink definitionNoteSourceLink = (DefinitionNoteSourceLink) sourceLink;
			Long definitionNoteId = definitionNoteSourceLink.getDefinitionNoteId();
			return permissionDbService.isGrantedForDefinitionNote(userId, crudRole, definitionNoteId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(LEXEME_SOURCE_LINK, sourceContentKey)) {
			LexemeSourceLink lexemeSourceLink = (LexemeSourceLink) sourceLink;
			Long lexemeId = lexemeSourceLink.getLexemeId();
			return permissionDbService.isGrantedForLexeme(userId, crudRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(LEXEME_NOTE_SOURCE_LINK, sourceContentKey)) {
			LexemeNoteSourceLink lexemeNoteSourceLink = (LexemeNoteSourceLink) sourceLink;
			Long lexemeNoteId = lexemeNoteSourceLink.getLexemeNoteId();
			return permissionDbService.isGrantedForLexemeNote(userId, crudRole, lexemeNoteId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(USAGE_SOURCE_LINK, sourceContentKey)) {
			UsageSourceLink usageSourceLink = (UsageSourceLink) sourceLink;
			Long usageId = usageSourceLink.getUsageId();
			return permissionDbService.isGrantedForUsage(userId, crudRole, usageId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(MEANING_IMAGE_SOURCE_LINK, sourceContentKey)) {
			MeaningImageSourceLink meaningImageSourceLink = (MeaningImageSourceLink) sourceLink;
			Long meaningImageId = meaningImageSourceLink.getMeaningImageId();
			Long meaningId = activityLogDbService.getMeaningImageOwnerId(meaningImageId);
			return permissionDbService.isGrantedForMeaning(userId, crudRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(MEANING_NOTE_SOURCE_LINK, sourceContentKey)) {
			MeaningNoteSourceLink meaningNoteSourceLink = (MeaningNoteSourceLink) sourceLink;
			Long meaningNoteId = meaningNoteSourceLink.getMeaningNoteId();
			Long meaningId = activityLogDbService.getMeaningNoteOwnerId(meaningNoteId);
			return permissionDbService.isGrantedForMeaning(userId, crudRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(FREEFORM_SOURCE_LINK, sourceContentKey)) {
			FreeformSourceLink freeformSourceLink = (FreeformSourceLink) sourceLink;
			Long freeformId = freeformSourceLink.getFreeformId();
			return isFreeformCrudGranted(authentication, crudRoleDataset, freeformId);
		}
		return false;
	}

	@Transactional
	public boolean isSourceLinkCrudGranted(Authentication authentication, String crudRoleDataset, String sourceContentKey, Long sourceLinkId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}

		if (StringUtils.equals(DEFINITION_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getDefinitionSourceLinkOwner(sourceLinkId);
			Long definitionId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return permissionDbService.isGrantedForDefinition(userId, crudRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(DEFINITION_NOTE_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getDefinitionNoteSourceLinkOwner(sourceLinkId);
			Long definitionNoteId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return permissionDbService.isGrantedForDefinitionNote(userId, crudRole, definitionNoteId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(LEXEME_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getLexemeSourceLinkOwner(sourceLinkId);
			Long lexemeId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return permissionDbService.isGrantedForLexeme(userId, crudRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(LEXEME_NOTE_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getLexemeNoteSourceLinkOwner(sourceLinkId);
			Long lexemeNoteId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return permissionDbService.isGrantedForLexemeNote(userId, crudRole, lexemeNoteId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(USAGE_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getUsageSourceLinkOwner(sourceLinkId);
			Long usageId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return permissionDbService.isGrantedForUsage(userId, crudRole, usageId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(MEANING_IMAGE_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getMeaningImageSourceLinkOwner(sourceLinkId);
			Long meaningImageId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			Long meaningId = activityLogDbService.getMeaningImageOwnerId(meaningImageId);
			return permissionDbService.isGrantedForMeaning(userId, crudRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(MEANING_NOTE_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getMeaningNoteSourceLinkOwner(sourceLinkId);
			Long meaningNoteId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			Long meaningId = activityLogDbService.getMeaningNoteOwnerId(meaningNoteId);
			return permissionDbService.isGrantedForMeaning(userId, crudRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (StringUtils.equals(FREEFORM_SOURCE_LINK, sourceContentKey)) {
			SourceLinkOwner sourceLinkOwner = sourceLinkDbService.getFreeformSourceLinkOwner(sourceLinkId);
			Long freeformId = sourceLinkOwner.getOwnerId();
			Long sourceId = sourceLinkOwner.getSourceId();
			boolean isSourceCrudGranted = isSourceCrudGranted(authentication, crudRoleDataset, sourceId);
			if (!isSourceCrudGranted) {
				return false;
			}
			return isFreeformCrudGranted(authentication, crudRoleDataset, freeformId);
		}
		return false;
	}

	// word crud

	@Transactional
	public boolean isWordRelationCrudGranted(Authentication authentication, String crudRoleDataset, Long relationId) {

		Long wordId = activityLogDbService.getWordRelationOwnerId(relationId);
		return isWordCrudGranted(authentication, crudRoleDataset, wordId);
	}

	@Transactional
	public boolean isWordCrudGranted(Authentication authentication, String crudRoleDataset, Long wordId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		return isWordCrudGranted(user, crudRoleDataset, wordId);
	}

	@Transactional
	public boolean isWordCrudGranted(EkiUser user, String crudRoleDataset, Long wordId) {

		if (wordId == null) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		boolean isGranted = permissionDbService.isGrantedForWord(userId, crudRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	@Transactional
	public boolean isWordForumCrudGranted(Authentication authentication, Long wordForumId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		boolean isAdmin = user.isAdmin();
		if (isAdmin) {
			return true;
		}
		boolean isGranted = permissionDbService.isGrantedForWordForum(userId, wordForumId);
		return isGranted;
	}

	@Transactional
	public boolean isMeaningForumCrudGranted(Authentication authentication, Long meaningForumId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		Long userId = user.getId();
		boolean isAdmin = user.isAdmin();
		if (isAdmin) {
			return true;
		}
		boolean isGranted = permissionDbService.isGrantedForMeaningForum(userId, meaningForumId);
		return isGranted;
	}

	@Transactional
	public boolean isLexemeCrudGranted(Authentication authentication, String crudRoleDataset, Long lexemeId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (lexemeId == null) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		boolean isGranted = permissionDbService.isGrantedForLexeme(userId, crudRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	@Transactional
	public boolean isMeaningRelationCrudGranted(Authentication authentication, String crudRoleDataset, Long relationId) {

		Long meaningId = activityLogDbService.getMeaningRelationOwnerId(relationId);
		return isMeaningCrudGranted(authentication, crudRoleDataset, meaningId);
	}

	@Transactional
	public boolean isMeaningCrudGranted(Authentication authentication, String crudRoleDataset, Long meaningId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (meaningId == null) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		boolean isGranted = permissionDbService.isGrantedForMeaning(userId, crudRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		return isGranted;
	}

	@Transactional
	public boolean isFreeformCrudGranted(Authentication authentication, String crudRoleDataset, Long freeformId) {

		EkiUser user = (EkiUser) authentication.getPrincipal();
		if (freeformId == null) {
			return true;
		}
		if (user.isMaster()) {
			return true;
		}
		Long userId = user.getId();
		DatasetPermission crudRole = getCrudRole(userId, crudRoleDataset);
		if (crudRole == null) {
			return false;
		}
		return resolveFreeformOwner(authentication, crudRoleDataset, freeformId);
	}

	private boolean resolveFreeformOwner(Authentication authentication, String crudRoleDataset, Long freeformId) {

		Map<String, Object> freeformOwnerDataMap = activityLogDbService.getFirstDepthFreeformOwnerDataMap(freeformId);
		Long lexemeId = (Long) freeformOwnerDataMap.get("lexeme_id");
		if (lexemeId != null) {
			return isLexemeCrudGranted(authentication, crudRoleDataset, lexemeId);
		}
		Long wordId = (Long) freeformOwnerDataMap.get("word_id");
		if (wordId != null) {
			return isWordCrudGranted(authentication, crudRoleDataset, wordId);
		}
		Long meaningId = (Long) freeformOwnerDataMap.get("meaning_id");
		if (meaningId != null) {
			return isMeaningCrudGranted(authentication, crudRoleDataset, meaningId);
		}
		return false;
	}

	private DatasetPermission getCrudRole(Long userId, String crudRoleDataset) {

		List<DatasetPermission> datasetPermissions = permissionDbService.getDatasetPermissions(userId);
		DatasetPermission crudRole = datasetPermissions.stream()
				.filter(datasetPermission -> AuthorityItem.DATASET.equals(datasetPermission.getAuthItem())
						&& AUTH_OPS_CRUD.contains(datasetPermission.getAuthOperation().name())
						&& StringUtils.equals(datasetPermission.getDatasetCode(), crudRoleDataset))
				.findAny()
				.orElse(null);
		return crudRole;
	}

	//not in use currently
	//hasPermission(#foo, 'write')
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

		return false;
	}

	//not in use currently
	//hasPermission(#id, 'USAGE', 'DATASET:CRUD')
	@Transactional
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

		return false;
	}
}
