package eki.ekilex.service.db;

import eki.ekilex.constant.DbConstant;
import eki.ekilex.data.db.tables.records.DefinitionDatasetRecord;
import eki.ekilex.data.db.tables.records.DefinitionFreeformRecord;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.DefinitionSourceLinkRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;

import static eki.ekilex.data.db.tables.Definition.DEFINITION;
import static eki.ekilex.data.db.tables.DefinitionDataset.DEFINITION_DATASET;
import static eki.ekilex.data.db.tables.DefinitionFreeform.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.tables.DefinitionSourceLink.DEFINITION_SOURCE_LINK;

@Service
public class DefinitionDbService implements DbConstant {

	final private DSLContext create;

	final private UpdateDbService updateDbService;

	public DefinitionDbService(DSLContext create, UpdateDbService updateDbService) {
		this.create = create;
		this.updateDbService = updateDbService;
	}

	public Long cloneMeaningDefinition(Long definitionId, Long meaningId) {

		DefinitionRecord definition = create.selectFrom(DEFINITION).where(DEFINITION.ID.eq(definitionId)).fetchOne();
		DefinitionRecord clonedDefinition = definition.copy();
		clonedDefinition.setMeaningId(meaningId);
		clonedDefinition.changed(DEFINITION.ORDER_BY, false);
		clonedDefinition.store();
		return clonedDefinition.getId();
	}

	public void cloneDefinitionFreeforms(Long definitionId, Long clonedDefinitionId) {

		Result<DefinitionFreeformRecord> definitionFreeforms =
				create.selectFrom(DEFINITION_FREEFORM).where(DEFINITION_FREEFORM.DEFINITION_ID.eq(definitionId)).fetch();
		for (DefinitionFreeformRecord definitionFreeform : definitionFreeforms) {
			Long clonedFreeformId = updateDbService.cloneFreeform(definitionFreeform.getFreeformId(), null);
			DefinitionFreeformRecord clonedDefinitionFreeform = create.newRecord(DEFINITION_FREEFORM);
			clonedDefinitionFreeform.setDefinitionId(clonedDefinitionId);
			clonedDefinitionFreeform.setFreeformId(clonedFreeformId);
			clonedDefinitionFreeform.store();
		}
	}

	public List<DefinitionRecord> findMeaningDefinitions(Long meaningId) {
		return create.selectFrom(DEFINITION).where(DEFINITION.MEANING_ID.eq(meaningId)).fetch();
	}

	public void cloneDefinitionDatasets(Long definitionId, Long clonedDefinintionId) {

		Result<DefinitionDatasetRecord> definitionDatasets =
				create.selectFrom(DEFINITION_DATASET).where(DEFINITION_DATASET.DEFINITION_ID.eq(definitionId)).fetch();
		for (DefinitionDatasetRecord definitionDataset : definitionDatasets) {
			DefinitionDatasetRecord clonedDefinitionDataset = definitionDataset.copy();
			clonedDefinitionDataset.setDefinitionId(clonedDefinintionId);
			clonedDefinitionDataset.setDatasetCode(definitionDataset.getDatasetCode());
			clonedDefinitionDataset.store();
		}
	}

	public void cloneDefinitionSourceLinks(Long definitionId, Long clonedDefinintionId) {

		Result<DefinitionSourceLinkRecord> definitionSourceLinks =
				create.selectFrom(DEFINITION_SOURCE_LINK).where(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(definitionId)).fetch();
		definitionSourceLinks.stream().map(DefinitionSourceLinkRecord::copy).forEach(clonedDefinitionSourceLink -> {
			clonedDefinitionSourceLink.setDefinitionId(clonedDefinintionId);
			clonedDefinitionSourceLink.changed(DEFINITION_SOURCE_LINK.ORDER_BY, false);
			clonedDefinitionSourceLink.store();
		});
	}

}
