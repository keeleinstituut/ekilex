package eki.ekilex.service.db;

import eki.ekilex.constant.DbConstant;
import eki.ekilex.data.db.tables.records.LexRelationRecord;
import eki.ekilex.data.db.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.tables.records.LexemePosRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.data.db.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.tables.records.LexemeSourceLinkRecord;
import eki.ekilex.service.db.util.LifecycleLogDbServiceHelper;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static eki.ekilex.data.db.tables.Lexeme.LEXEME;
import static eki.ekilex.data.db.tables.LexemeDeriv.LEXEME_DERIV;
import static eki.ekilex.data.db.tables.LexemeFreeform.LEXEME_FREEFORM;
import static eki.ekilex.data.db.tables.LexemePos.LEXEME_POS;
import static eki.ekilex.data.db.tables.LexemeRegister.LEXEME_REGISTER;
import static eki.ekilex.data.db.tables.LexemeSourceLink.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.tables.LexRelation.LEX_RELATION;

@Service
public class LexemeDbService implements DbConstant {

	final private DSLContext create;

	final private UpdateDbService updateDbService;

	final private LifecycleLogDbServiceHelper logHelper;

	public LexemeDbService(DSLContext create, UpdateDbService updateDbService, LifecycleLogDbServiceHelper logHelper) {
		this.create = create;
		this.updateDbService = updateDbService;
		this.logHelper = logHelper;
	}

	public LexemeRecord findLexeme(Long lexemeId) {
		return create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
	}

	public List<LexemeRecord> findMeaningLexemes(Long meaningId) {
		return create.selectFrom(LEXEME).where(LEXEME.MEANING_ID.eq(meaningId)).fetch();
	}

	public Long cloneLexeme(Long lexemeId, Long meaningId) {

		LexemeRecord lexeme = create.selectFrom(LEXEME).where(LEXEME.ID.eq(lexemeId)).fetchOne();
		LexemeRecord clonedLexeme = lexeme.copy();
		clonedLexeme.setMeaningId(meaningId);
		clonedLexeme.changed(LEXEME.ORDER_BY, false);
		clonedLexeme.setLevel1(lexeme.getLevel1());
		clonedLexeme.setLevel2(calculateLevel2(lexeme));
		clonedLexeme.setLevel3(1);
		clonedLexeme.store();
		return clonedLexeme.getId();
	}

	public void cloneLexemeDerivatives(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeDerivRecord> lexemeDerivatives = create.selectFrom(LEXEME_DERIV).where(LEXEME_DERIV.LEXEME_ID.eq(lexemeId)).fetch();
		lexemeDerivatives.stream().map(LexemeDerivRecord::copy).forEach(clonedLexemeDeriv -> {
			clonedLexemeDeriv.setLexemeId(clonedLexemeId);
			clonedLexemeDeriv.store();
		});
	}

	public void cloneLexemeFreeforms(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeFreeformRecord> lexemeFreeforms =
				create.selectFrom(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(lexemeId)).fetch();
		lexemeFreeforms.forEach(lexemeFreeform -> {
			Long clonedFreeformId = updateDbService.cloneFreeform(lexemeFreeform.getFreeformId(), null);
			LexemeFreeformRecord clonedLexemeFreeform = create.newRecord(LEXEME_FREEFORM);
			clonedLexemeFreeform.setLexemeId(clonedLexemeId);
			clonedLexemeFreeform.setFreeformId(clonedFreeformId);
			clonedLexemeFreeform.store();
		});
	}

	public void cloneLexemePoses(Long lexemeId, Long clonedLexemeId) {

		Result<LexemePosRecord> lexemePoses =
				create.selectFrom(LEXEME_POS).where(LEXEME_POS.LEXEME_ID.eq(lexemeId)).fetch();
		lexemePoses.stream().map(LexemePosRecord::copy).forEach(clonedLexemePos -> {
			clonedLexemePos.setLexemeId(clonedLexemeId);
			clonedLexemePos.changed(LEXEME_POS.ORDER_BY, false);
			clonedLexemePos.store();
		});
	}

	public void cloneLexemeRegisters(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeRegisterRecord> lexemeRegisters =
				create.selectFrom(LEXEME_REGISTER).where(LEXEME_REGISTER.LEXEME_ID.eq(lexemeId)).fetch();
		lexemeRegisters.stream().map(LexemeRegisterRecord::copy).forEach(clonedLexemeRegister -> {
			clonedLexemeRegister.setLexemeId(clonedLexemeId);
			clonedLexemeRegister.changed(LEXEME_REGISTER.ORDER_BY, false);
			clonedLexemeRegister.store();
		});
	}

	public void cloneLexemeSoureLinks(Long lexemeId, Long clonedLexemeId) {

		Result<LexemeSourceLinkRecord> lexemeSourceLinks =
				create.selectFrom(LEXEME_SOURCE_LINK).where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(lexemeId)).fetch();
		lexemeSourceLinks.stream().map(LexemeSourceLinkRecord::copy).forEach(clonedLexemeSourceLink -> {
			clonedLexemeSourceLink.setLexemeId(clonedLexemeId);
			clonedLexemeSourceLink.changed(LEXEME_SOURCE_LINK.ORDER_BY, false);
			clonedLexemeSourceLink.store();
		});
	}

	public void cloneLexemeRelations(Long lexemeId, Long clonedLexemeId) {

		Result<LexRelationRecord> lexemeRelations = create.selectFrom(LEX_RELATION)
				.where(LEX_RELATION.LEXEME1_ID.eq(lexemeId).or(LEX_RELATION.LEXEME2_ID.eq(lexemeId)))
				.fetch();
		lexemeRelations.stream().map(LexRelationRecord::copy).forEach(clonedRelation -> {
			if (clonedRelation.getLexeme1Id().equals(lexemeId)) {
				clonedRelation.setLexeme1Id(clonedLexemeId);
			} else {
				clonedRelation.setLexeme2Id(clonedLexemeId);
			}
			clonedRelation.changed(LEX_RELATION.ORDER_BY, false);
			clonedRelation.store();
		});
	}

	public String getLogStringForLexeme(Long lexemeId) {
		Map<String, Object> lexemeData = logHelper.getLexemeData(create, lexemeId);
		String levels = String.join(".", lexemeData.get("level1").toString(), lexemeData.get("level2").toString(), lexemeData.get("level3").toString());
		return lexemeData.get("value") + " [" + levels + "]";
	}

	private int calculateLevel2(LexemeRecord lexeme) {
		Result<LexemeRecord> lexemes = create.selectFrom(LEXEME).where(LEXEME.WORD_ID.eq(lexeme.getWordId()).and(LEXEME.LEVEL1.eq(lexeme.getLevel1()))).fetch();
		return lexemes.stream().mapToInt(LexemeRecord::getLevel2).max().orElse(0) + 1;
	}

}
