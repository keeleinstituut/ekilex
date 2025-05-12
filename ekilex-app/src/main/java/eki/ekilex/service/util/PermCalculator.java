package eki.ekilex.service.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.PermConstant;
import eki.ekilex.data.AbstractGrantEntity;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Forum;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.Source;
import eki.ekilex.data.TypeMtDefinition;
import eki.ekilex.data.TypeMtLexeme;
import eki.ekilex.data.TypeMtLexemeFreeform;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermCalculator implements PermConstant {

	@Autowired
	private PermissionDbService permissionDbService;

	public void applyCrud(EkiUser user, List<? extends AbstractGrantEntity> crudEntities) {

		if (CollectionUtils.isEmpty(crudEntities)) {
			return;
		}
		if (user == null) {
			return;
		}

		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();

		for (AbstractGrantEntity crudEntity : crudEntities) {

			boolean isCrudGrant = false;
			boolean isAnyGrant = false;

			if (user.isMaster()) {
				isCrudGrant = isAnyGrant = true;
			} else if (userRole == null) {
				isCrudGrant = isAnyGrant = false;
			} else {

				AuthorityOperation authOperation = userRole.getAuthOperation();
				boolean isCrudRole = AUTH_OPS_CRUD.contains(authOperation.name());

				if (!isCrudRole) {
					isCrudGrant = isAnyGrant = false;
				} else if (crudEntity instanceof Definition) {
					Definition definition = (Definition) crudEntity;
					Long definitionId = definition.getId();
					isCrudGrant = permissionDbService.isGrantedForDefinition(userId, userRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof Usage) {
					Usage usage = (Usage) crudEntity;
					Long usageId = usage.getId();
					isCrudGrant = permissionDbService.isGrantedForUsage(userId, userRole, usageId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof Source) {
					Source source = (Source) crudEntity;
					Long sourceId = source.getId();
					isCrudGrant = permissionDbService.isGrantedForSource(userId, userRole, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof Meaning) {
					Meaning meaning = (Meaning) crudEntity;
					Long meaningId = meaning.getMeaningId();
					isAnyGrant = permissionDbService.isGrantedForMeaningByAnyLexeme(userId, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof TypeMtLexeme) {
					TypeMtLexeme lexeme = (TypeMtLexeme) crudEntity;
					Long lexemeId = lexeme.getLexemeId();
					isCrudGrant = permissionDbService.isGrantedForLexeme(userId, userRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof TypeMtDefinition) {
					TypeMtDefinition definition = (TypeMtDefinition) crudEntity;
					Long definitionId = definition.getDefinitionId();
					isCrudGrant = permissionDbService.isGrantedForDefinition(userId, userRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof TypeMtLexemeFreeform) {
					TypeMtLexemeFreeform usage = (TypeMtLexemeFreeform) crudEntity;
					Long usageId = usage.getFreeformId();
					isCrudGrant = permissionDbService.isGrantedForUsage(userId, userRole, usageId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				} else if (crudEntity instanceof Forum) {
					if (user.isAdmin()) {
						isCrudGrant = true;
					} else {
						Forum forum = (Forum) crudEntity;
						Long creatorId = forum.getCreatorId();
						if (userId.equals(creatorId)) {
							isCrudGrant = true;
						}
					}
				}
			}

			crudEntity.setCrudGrant(isCrudGrant);
			crudEntity.setAnyGrant(isAnyGrant);
		}
	}

	public void applyCrud(EkiUser user, AbstractGrantEntity crudEntity) {

		if (user == null) {
			return;
		}

		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();

		boolean isReadGrant = false;
		boolean isCrudGrant = false;
		boolean isSubGrant = false;
		boolean isAnyGrant = false;

		if (user.isMaster()) {
			isReadGrant = isCrudGrant = isSubGrant = isAnyGrant = true;
		} else if (userRole == null) {
			// nothing
		} else if (crudEntity instanceof Word) {
			Word word = (Word) crudEntity;
			Long wordId = word.getWordId();
			isReadGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.isGrantedForWordByLexeme(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Lexeme) {
			Lexeme lexeme = (Lexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, userRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Meaning) {
			Meaning meaning = (Meaning) crudEntity;
			Long meaningId = meaning.getMeaningId();
			isReadGrant = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.isGrantedForMeaningByLexeme(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isAnyGrant = permissionDbService.isGrantedForMeaningByAnyLexeme(userId, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Source) {
			Source source = (Source) crudEntity;
			Long sourceId = source.getId();
			if (userRole.isSuperiorDataset()) {
				isCrudGrant = true;
			} else {
				isCrudGrant = permissionDbService.isGrantedForSource(userId, userRole, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			}
		}

		crudEntity.setReadGrant(isReadGrant);
		crudEntity.setCrudGrant(isCrudGrant);
		crudEntity.setSubGrant(isSubGrant);
		crudEntity.setAnyGrant(isAnyGrant);
	}

	public void filterVisibility(EkiUser user, List<? extends AbstractGrantEntity> publicEntities) {

		if (CollectionUtils.isEmpty(publicEntities)) {
			return;
		}

		Long userId = user.getId();
		DatasetPermission userRole = user.getRecentRole();

		if (user.isMaster()) {
			return;
		}
		if (userRole == null) {
			publicEntities.removeIf(entity -> !entity.isPublic());
			return;
		}
		publicEntities.removeIf(entity -> !isEntityVisible(userId, userRole, entity));
	}

	private boolean isEntityVisible(Long userId, DatasetPermission userRole, AbstractGrantEntity entity) {

		if (entity.isPublic()) {
			return true;
		}
		if (entity.isCrudGrant()) {
			return true;
		}
		if (entity.isReadGrant()) {
			return true;
		}

		boolean isVisible = false;
		if (entity instanceof Definition) {
			Definition definition = (Definition) entity;
			Long definitionId = definition.getId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, userRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof Usage) {
			Usage usage = (Usage) entity;
			Long usageId = usage.getId();
			isVisible = permissionDbService.isGrantedForUsage(userId, userRole, usageId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof LexemeNote) {
			LexemeNote lexemeNote = (LexemeNote) entity;
			Long lexemeId = lexemeNote.getLexemeId();
			isVisible = permissionDbService.isGrantedForLexeme(userId, userRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof MeaningNote) {
			MeaningNote meaningNote = (MeaningNote) entity;
			Long meaningId = meaningNote.getMeaningId();
			isVisible = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof DefinitionNote) {
			DefinitionNote definitionNote = (DefinitionNote) entity;
			Long definitionId = definitionNote.getDefinitionId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, userRole, definitionId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		}
		return isVisible;
	}
}
