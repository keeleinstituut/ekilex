package eki.ekilex.service.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.AuthorityItem;
import eki.common.constant.AuthorityOperation;
import eki.ekilex.data.AbstractCrudEntity;
import eki.ekilex.data.AbstractPublicEntity;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.Definition;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.Source;
import eki.ekilex.data.Usage;
import eki.ekilex.data.Word;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.service.db.PermissionDbService;

@Component
public class PermCalculator {

	private final List<String> crudAuthOps = Arrays.asList(AuthorityOperation.OWN.name(), AuthorityOperation.CRUD.name());

	private final List<String> readAuthOps = Arrays.asList(AuthorityOperation.OWN.name(), AuthorityOperation.CRUD.name(), AuthorityOperation.READ.name());

	private final String authItemDataset = AuthorityItem.DATASET.name();

	@Autowired
	private PermissionDbService permissionDbService;

	public void applyCrud(List<? extends AbstractCrudEntity> crudEntities, DatasetPermission userRole) {

		if (userRole == null) {
			for (AbstractCrudEntity crudEntity : crudEntities) {
				crudEntity.setCrudGrant(false);
				crudEntity.setReadGrant(false);
				crudEntity.setSubGrant(false);
				crudEntity.setAnyGrant(false);
			}
			return;
		}

		Long userId = userRole.getUserId();
		String datasetCode = userRole.getDatasetCode();
		String lang = userRole.getAuthLang();

		for (AbstractCrudEntity crudEntity : crudEntities) {
			Boolean crudGrant = null;
			Boolean readGrant = null;
			Boolean subGrant = null;
			Boolean anyGrant = null;

			if (crudEntity instanceof Definition) {
				Definition definition = (Definition) crudEntity;
				Long definitionId = definition.getId();
				crudGrant = permissionDbService.isGrantedForDefinition(definitionId, datasetCode, lang);
			} else if (crudEntity instanceof Usage) {
				Usage usage = (Usage) crudEntity;
				Long usageId = usage.getId();
				crudGrant = permissionDbService.isGrantedForUsage(usageId, datasetCode, lang);
			} else if (crudEntity instanceof Source) {
				Source source = (Source) crudEntity;
				Long sourceId = source.getSourceId();
				crudGrant = permissionDbService.isGrantedForSource(userId, sourceId, authItemDataset, crudAuthOps);
			}

			crudEntity.setCrudGrant(crudGrant);
			crudEntity.setReadGrant(readGrant);
			crudEntity.setSubGrant(subGrant);
			crudEntity.setAnyGrant(anyGrant);
		}
	}

	public void applyCrud(AbstractCrudEntity crudEntity, DatasetPermission userRole) {

		if (userRole == null) {
			crudEntity.setCrudGrant(false);
			crudEntity.setReadGrant(false);
			crudEntity.setSubGrant(false);
			crudEntity.setAnyGrant(false);
			return;
		}

		Long userId = userRole.getUserId();
		String datasetCode = userRole.getDatasetCode();
		Boolean crudGrant = null;
		Boolean readGrant = null;
		Boolean subGrant = null;
		Boolean anyGrant = null;

		if (crudEntity instanceof Word) {
			Word word = (Word) crudEntity;
			Long wordId = word.getWordId();
			crudGrant = permissionDbService.isGrantedForWord(userId, wordId, datasetCode, authItemDataset, crudAuthOps);
			readGrant = permissionDbService.isGrantedForWord(userId, wordId, datasetCode, authItemDataset, readAuthOps);
			subGrant = permissionDbService.wordDatasetExists(wordId, datasetCode);
		} else if (crudEntity instanceof Lexeme) {
			Lexeme lexeme = (Lexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			crudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof WordLexeme) {
			WordLexeme lexeme = (WordLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			crudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof WordSynLexeme) {
			WordSynLexeme lexeme = (WordSynLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			crudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof Meaning) {
			Meaning meaning = (Meaning) crudEntity;
			Long meaningId = meaning.getMeaningId();
			crudGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, datasetCode, authItemDataset, crudAuthOps);
			readGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, datasetCode, authItemDataset, readAuthOps);
			subGrant = permissionDbService.meaningDatasetExists(meaningId, datasetCode);
			anyGrant = permissionDbService.isMeaningAnyLexemeCrudGranted(meaningId, userId);
		}

		crudEntity.setCrudGrant(crudGrant);
		crudEntity.setReadGrant(readGrant);
		crudEntity.setSubGrant(subGrant);
		crudEntity.setAnyGrant(anyGrant);
	}

	public void filterVisibility(List<? extends AbstractPublicEntity> publicEntities, Long userId) {

		if (userId == null) {
			publicEntities.removeIf(entity -> !entity.isPublic());
			return;
		}
		publicEntities.removeIf(entity -> !isEntityVisible(entity, userId));
	}

	private boolean isEntityVisible(AbstractPublicEntity entity, Long userId) {

		if (entity.isPublic()) {
			return true;
		}

		boolean isVisible = false;

		if (entity.getCrudGrant() != null) {
			isVisible = entity.getCrudGrant();
			if (isVisible) {
				return true;
			}
		}

		if (entity.getReadGrant() != null) {
			isVisible = entity.getReadGrant();
			if (isVisible) {
				return true;
			}
		}

		if (entity instanceof Definition) {
			Definition definition = (Definition) entity;
			Long definitionId = definition.getId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, definitionId, authItemDataset, readAuthOps);
		} else if (entity instanceof Usage) {
			Usage usage = (Usage) entity;
			Long usageId = usage.getId();
			isVisible = permissionDbService.isGrantedForUsage(userId, usageId, authItemDataset, readAuthOps);
		}
		return isVisible;
	}
}
