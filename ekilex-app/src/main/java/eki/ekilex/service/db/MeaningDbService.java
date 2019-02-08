package eki.ekilex.service.db;

import eki.ekilex.constant.DbConstant;
import eki.ekilex.data.db.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.tables.records.MeaningRecord;
import eki.ekilex.data.db.tables.records.MeaningRelationRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.UpdatableRecordImpl;
import org.springframework.stereotype.Service;

import static eki.ekilex.data.db.tables.Meaning.MEANING;
import static eki.ekilex.data.db.tables.MeaningDomain.MEANING_DOMAIN;
import static eki.ekilex.data.db.tables.MeaningRelation.MEANING_RELATION;
import static eki.ekilex.data.db.tables.MeaningFreeform.MEANING_FREEFORM;

@Service
public class MeaningDbService implements DbConstant {

	final private DSLContext create;

	final private UpdateDbService updateDbService;

	public MeaningDbService(DSLContext create, UpdateDbService updateDbService) {
		this.create = create;
		this.updateDbService = updateDbService;
	}

	public Long cloneMeaning(Long meaningId) {

		MeaningRecord meaning = create.selectFrom(MEANING).where(MEANING.ID.eq(meaningId)).fetchOne();
		MeaningRecord clonedMeaning = meaning.copy();
		clonedMeaning.store();
		return clonedMeaning.getId();
	}

	public void cloneMeaningDomains(Long meaningId, Long clonedMeaningId) {

		Result<MeaningDomainRecord> meaningDomains = create.selectFrom(MEANING_DOMAIN).where(MEANING_DOMAIN.MEANING_ID.eq(meaningId)).fetch();
		meaningDomains.stream().map(MeaningDomainRecord::copy).forEach(clonedDomain -> {
			clonedDomain.setMeaningId(clonedMeaningId);
			clonedDomain.changed(MEANING_DOMAIN.ORDER_BY, false);
			clonedDomain.store();
		});
	}

	public void cloneMeaningRelations(Long meaningId, Long clonedMeaningId) {

		Result<MeaningRelationRecord> meaningRelations = create.selectFrom(MEANING_RELATION)
				.where(MEANING_RELATION.MEANING1_ID.eq(meaningId).or(MEANING_RELATION.MEANING2_ID.eq(meaningId)))
				.fetch();
		meaningRelations.stream().map(MeaningRelationRecord::copy).forEach(clonedRelation -> {
			if (clonedRelation.getMeaning1Id().equals(meaningId)) {
				clonedRelation.setMeaning1Id(clonedMeaningId);
			} else {
				clonedRelation.setMeaning2Id(clonedMeaningId);
			}
			clonedRelation.changed(MEANING_RELATION.ORDER_BY, false);
			clonedRelation.store();
		});
	}

	public void cloneMeaningFreeforms(Long meaningId, Long clonedMeaningId) {

		Result<MeaningFreeformRecord> meaningFreeforms = create.selectFrom(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(meaningId)).fetch();
		for (MeaningFreeformRecord meaningFreeform : meaningFreeforms) {
			Long clonedFreeformId = updateDbService.cloneFreeform(meaningFreeform.getFreeformId(), null);
			MeaningFreeformRecord clonedMeaningFreeform = create.newRecord(MEANING_FREEFORM);
			clonedMeaningFreeform.setMeaningId(clonedMeaningId);
			clonedMeaningFreeform.setFreeformId(clonedFreeformId);
			clonedMeaningFreeform.store();
		}
	}

}
