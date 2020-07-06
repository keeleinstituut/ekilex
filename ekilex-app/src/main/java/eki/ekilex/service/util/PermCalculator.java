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
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
import eki.ekilex.data.Source;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordNote;
import eki.ekilex.data.WordSynLexeme;
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
		String userRoleDatasetCode = userRole.getDatasetCode();
		AuthorityOperation authOperation = userRole.getAuthOperation();
		boolean isCrudRole = AUTH_OPS_CRUD.contains(authOperation.name());
		String userRoleLang = userRole.getAuthLang();

		if (!isCrudRole) {
			return;
		}

		for (AbstractCrudEntity crudEntity : crudEntities) {

			boolean isCrudGrant = false;
			boolean isAnyGrant = false;

			if (crudEntity instanceof Definition) {
				Definition definition = (Definition) crudEntity;
				Long definitionId = definition.getId();
				isCrudGrant = permissionDbService.isGrantedForDefinition(userId, definitionId, userRoleDatasetCode, userRoleLang, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			} else if (crudEntity instanceof Usage) {
				Usage usage = (Usage) crudEntity;
				Long usageId = usage.getId();
				isCrudGrant = permissionDbService.isGrantedForUsage(userId, usageId, userRoleDatasetCode, userRoleLang, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			} else if (crudEntity instanceof Source) {
				Source source = (Source) crudEntity;
				Long sourceId = source.getId();
				isCrudGrant = permissionDbService.isGrantedForSource(userId, sourceId, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			} else if (crudEntity instanceof Meaning) {
				Meaning meaning = (Meaning) crudEntity;
				Long meaningId = meaning.getMeaningId();
				isAnyGrant = permissionDbService.isMeaningAnyLexemeCrudGranted(userId, meaningId);
			}

			crudEntity.setCrudGrant(isCrudGrant);
			crudEntity.setAnyGrant(isAnyGrant);
		}
	}

	public void applyCrud(DatasetPermission userRole, AbstractCrudEntity crudEntity) {

		if (userRole == null) {
			return;
		}

		Long userId = userRole.getUserId();
		String userRoleDatasetCode = userRole.getDatasetCode();

		boolean isReadGrant = false;
		boolean isCrudGrant = false;
		boolean isSubGrant = false;
		boolean isAnyGrant = false;

		if (crudEntity instanceof Word) {
			Word word = (Word) crudEntity;
			Long wordId = word.getWordId();
			isReadGrant = permissionDbService.isGrantedForWord(userId, wordId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForWord(userId, wordId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.wordDatasetExists(wordId, userRoleDatasetCode);
		} else if (crudEntity instanceof Lexeme) {
			Lexeme lexeme = (Lexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, lexemeId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof WordLexeme) {
			WordLexeme lexeme = (WordLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, lexemeId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof WordSynLexeme) {
			WordSynLexeme lexeme = (WordSynLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(userId, lexemeId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
		} else if (crudEntity instanceof Meaning) {
			Meaning meaning = (Meaning) crudEntity;
			Long meaningId = meaning.getMeaningId();
			isReadGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
			isCrudGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_CRUD);
			isSubGrant = permissionDbService.meaningDatasetExists(meaningId, userRoleDatasetCode);
			isAnyGrant = permissionDbService.isMeaningAnyLexemeCrudGranted(userId, meaningId);
		}

		crudEntity.setReadGrant(isReadGrant);
		crudEntity.setCrudGrant(isCrudGrant);
		crudEntity.setSubGrant(isSubGrant);
		crudEntity.setAnyGrant(isAnyGrant);
	}

	public void filterVisibility(Long userId, List<? extends AbstractPublicEntity> publicEntities) {

		if (userId == null) {
			publicEntities.removeIf(entity -> !entity.isPublic());
			return;
		}
		publicEntities.removeIf(entity -> !isEntityVisible(userId, entity));
	}

	public void filterVisibility(DatasetPermission userRole, List<? extends AbstractPublicEntity> publicEntities) {

		if (userRole == null) {
			publicEntities.removeIf(entity -> !entity.isPublic());
			return;
		}

		Long userId = userRole.getUserId();
		publicEntities.removeIf(entity -> !isEntityVisible(userId, userRole, entity));
	}

	private boolean isEntityVisible(Long userId, AbstractPublicEntity entity) {
		return isEntityVisible(userId, null, entity);
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

		String userRoleDatasetCode = userRole.getDatasetCode();
		String userRoleLang = userRole.getAuthLang();

		boolean isVisible = false;
		if (entity instanceof Definition) {
			Definition definition = (Definition) entity;
			Long definitionId = definition.getId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, definitionId, userRoleDatasetCode, userRoleLang, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof Usage) {
			Usage usage = (Usage) entity;
			Long usageId = usage.getId();
			isVisible = permissionDbService.isGrantedForUsage(userId, usageId, userRoleDatasetCode, userRoleLang, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof LexemeNote) {
			LexemeNote lexemeNote = (LexemeNote) entity;
			Long lexemeId = lexemeNote.getLexemeId();
			isVisible = permissionDbService.isGrantedForLexeme(userId, lexemeId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof MeaningNote) {
			MeaningNote meaningNote = (MeaningNote) entity;
			Long meaningId = meaningNote.getMeaningId();
			isVisible = permissionDbService.isGrantedForMeaning(userId, meaningId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof DefinitionNote) {
			DefinitionNote definitionNote = (DefinitionNote) entity;
			Long definitionId = definitionNote.getDefinitionId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, definitionId, userRoleDatasetCode, userRoleLang, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		} else if (entity instanceof WordNote) {
			WordNote wordNote = (WordNote) entity;
			Long wordId = wordNote.getWordId();
			isVisible = permissionDbService.isGrantedForWord(userId, wordId, userRoleDatasetCode, AUTH_ITEM_DATASET, AUTH_OPS_READ);
		}
		return isVisible;
	}
}
