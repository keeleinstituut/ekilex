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
import eki.ekilex.data.DefinitionNote;
import eki.ekilex.data.Lexeme;
import eki.ekilex.data.LexemeNote;
import eki.ekilex.data.Meaning;
import eki.ekilex.data.MeaningNote;
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
			return;
		}

		Long userId = userRole.getUserId();
		String datasetCode = userRole.getDatasetCode();
		String lang = userRole.getAuthLang();

		for (AbstractCrudEntity crudEntity : crudEntities) {
			boolean isCrudGrant = false;

			if (crudEntity instanceof Definition) {
				Definition definition = (Definition) crudEntity;
				Long definitionId = definition.getId();
				isCrudGrant = permissionDbService.isGrantedForDefinition(definitionId, datasetCode, lang);
			} else if (crudEntity instanceof Usage) {
				Usage usage = (Usage) crudEntity;
				Long usageId = usage.getId();
				isCrudGrant = permissionDbService.isGrantedForUsage(usageId, datasetCode, lang);
			} else if (crudEntity instanceof Source) {
				Source source = (Source) crudEntity;
				Long sourceId = source.getSourceId();
				isCrudGrant = permissionDbService.isGrantedForSource(userId, sourceId, authItemDataset, crudAuthOps);
			}

			crudEntity.setCrudGrant(isCrudGrant);
		}
	}

	public void applyCrud(AbstractCrudEntity crudEntity, DatasetPermission userRole) {

		if (userRole == null) {
			return;
		}

		Long userId = userRole.getUserId();
		String datasetCode = userRole.getDatasetCode();
		boolean isCrudGrant = false;
		boolean isReadGrant = false;
		boolean isSubGrant = false;
		boolean isAnyGrant = false;

		if (crudEntity instanceof Word) {
			Word word = (Word) crudEntity;
			Long wordId = word.getWordId();
			isCrudGrant = permissionDbService.isGrantedForWord(userId, wordId, datasetCode, authItemDataset, crudAuthOps);
			isReadGrant = permissionDbService.isGrantedForWord(userId, wordId, datasetCode, authItemDataset, readAuthOps);
			isSubGrant = permissionDbService.wordDatasetExists(wordId, datasetCode);
		} else if (crudEntity instanceof Lexeme) {
			Lexeme lexeme = (Lexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof WordLexeme) {
			WordLexeme lexeme = (WordLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof WordSynLexeme) {
			WordSynLexeme lexeme = (WordSynLexeme) crudEntity;
			Long lexemeId = lexeme.getLexemeId();
			isCrudGrant = permissionDbService.isGrantedForLexeme(lexemeId, datasetCode);
		} else if (crudEntity instanceof Meaning) {
			Meaning meaning = (Meaning) crudEntity;
			Long meaningId = meaning.getMeaningId();
			isCrudGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, datasetCode, authItemDataset, crudAuthOps);
			isReadGrant = permissionDbService.isGrantedForMeaning(userId, meaningId, datasetCode, authItemDataset, readAuthOps);
			isSubGrant = permissionDbService.meaningDatasetExists(meaningId, datasetCode);
			isAnyGrant = permissionDbService.isMeaningAnyLexemeCrudGranted(userId, meaningId);
		}

		crudEntity.setCrudGrant(isCrudGrant);
		crudEntity.setReadGrant(isReadGrant);
		crudEntity.setSubGrant(isSubGrant);
		crudEntity.setAnyGrant(isAnyGrant);
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
			isVisible = permissionDbService.isGrantedForDefinition(userId, definitionId, authItemDataset, readAuthOps);
		} else if (entity instanceof Usage) {
			Usage usage = (Usage) entity;
			Long usageId = usage.getId();
			isVisible = permissionDbService.isGrantedForUsage(userId, usageId, authItemDataset, readAuthOps);
		} else if (entity instanceof LexemeNote) {
			LexemeNote lexemeNote = (LexemeNote) entity;
			Long lexemeId = lexemeNote.getLexemeId();
			isVisible = permissionDbService.isGrantedForLexeme(userId, lexemeId, authItemDataset, readAuthOps);
		} else if (entity instanceof MeaningNote) {
			MeaningNote meaningNote = (MeaningNote) entity;
			Long meaningId = meaningNote.getMeaningId();
			isVisible = permissionDbService.isGrantedForMeaning(userId, meaningId, null, authItemDataset, readAuthOps);
		} else if (entity instanceof DefinitionNote) {
			DefinitionNote definitionNote = (DefinitionNote) entity;
			Long definitionId = definitionNote.getDefinitionId();
			isVisible = permissionDbService.isGrantedForDefinition(userId, definitionId, authItemDataset, readAuthOps);
		}
		return isVisible;
	}
}
