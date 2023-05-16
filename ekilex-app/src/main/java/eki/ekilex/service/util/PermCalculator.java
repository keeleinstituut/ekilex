package eki.ekilex.service.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityOperation;
import eki.common.constant.PermConstant;
import eki.ekilex.data.AbstractCrudEntity;
import eki.ekilex.data.AbstractPublicEntity;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.DefinitionNote;
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
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermCalculator implements PermConstant {

	@Autowired
	private PermissionDbService permissionDbService;

	public void applyCrud(DatasetPermission userRole, List<? extends AbstractCrudEntity> crudEntities) {

		if (userRole == null) {
			return;
		}

		Long userId = userRole.getUserId();
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRole = AUTH_OPS_CRUD.contains(authOperation.name());

		if (!isCrudRole) {
			return;
		}

		for (AbstractCrudEntity crudEntity : crudEntities) {

			boolean isCrudGrant = false;
			boolean isAnyGrant = false;

			if (userRole.isSuperiorPermission()) {
				isCrudGrant = isAnyGrant = true;
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
				if (userRole.isSuperiorDataset()) {
					isCrudGrant = true;
				} else {
					isCrudGrant = permissionDbService.isGrantedForSource(userId, userRole, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
				}
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
			}

		crudEntity.setCrudGrant(isCrudGrant);
			crudEntity.setAnyGrant(isAnyGrant);
		}
	}

	public void applyCrud(DatasetPermission userRole, boolean isAdmin, List<? extends AbstractCrudEntity> crudEntities) {

		if (userRole == null) {
			return;
		}

		Long userId = userRole.getUserId();
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRole = AUTH_OPS_CRUD.contains(authOperation.name());

		if (!isCrudRole) {
			return;
		}

		for (AbstractCrudEntity crudEntity : crudEntities) {
			boolean isCrudGrant = false;

			if (crudEntity instanceof Forum) {
				if (isAdmin) {
					isCrudGrant = true;
				} else {
					Forum forum = (Forum) crudEntity;
					Long creatorId = forum.getCreatorId();
					if (userId.equals(creatorId)) {
						isCrudGrant = true;
					}
				}
			}

			crudEntity.setCrudGrant(isCrudGrant);
		}
	}

	public void applyCrud(DatasetPermission userRole, AbstractCrudEntity crudEntity) {

		if (userRole == null) {
			return;
		}

		Long userId = userRole.getUserId();
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRole = AUTH_OPS_CRUD.contains(authOperation.name());

		boolean isReadGrant = false;
		boolean isCrudGrant = false;
		boolean isSubGrant = false;
		boolean isAnyGrant = false;

		if (userRole.isSuperiorPermission()) {
			isReadGrant = true;
			if (isCrudRole) {
				isCrudGrant = isSubGrant = isAnyGrant = true;
			}
		} else if (crudEntity instanceof Word) {
			Word word = (Word) crudEntity;
			Long wordId = word.getWordId();
			boolean isMasterUser = permissionDbService.isMasterUser(userId);
			isReadGrant = isMasterUser || permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForWord(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.isGrantedForWordByLexeme(userId, userRole, wordId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Lexeme) {
			Lexeme lexeme = (Lexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, userRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof WordLexeme) {
			WordLexeme lexeme = (WordLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, userRole, lexemeId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Meaning) {
			Meaning meaning = (Meaning) crudEntity;
			Long meaningId = meaning.getMeaningId();
			boolean isMasterUser = permissionDbService.isMasterUser(userId);
			isReadGrant = isMasterUser || permissionDbService.isGrantedForMeaning(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForMeaning(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.isGrantedForMeaningByLexeme(userId, userRole, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isAnyGrant = permissionDbService.isGrantedForMeaningByAnyLexeme(userId, meaningId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		}

		crudEntity.setReadGrant(isReadGrant);
		crudEntity.setCrudGrant(isCrudGrant);
		crudEntity.setSubGrant(isSubGrant);
		crudEntity.setAnyGrant(isAnyGrant);
	}

	public void filterVisibility(DatasetPermission userRole, List<? extends AbstractPublicEntity> publicEntities) {

		if (userRole == null) {
			publicEntities.removeIf(entity -> !entity.isPublic());
			return;
		}
		if (userRole.isSuperiorPermission()) {
			return;
		}

		Long userId = userRole.getUserId();
		publicEntities.removeIf(entity -> !isEntityVisible(userId, userRole, entity));
	}

	private boolean isEntityVisible(Long userId, DatasetPermission userRole, AbstractPublicEntity entity) {

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
