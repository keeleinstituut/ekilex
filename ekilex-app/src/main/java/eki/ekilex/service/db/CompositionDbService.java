package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE;
import static eki.ekilex.data.db.main.Tables.DEFINITION_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE;
import static eki.ekilex.data.db.main.Tables.LEXEME_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_POS;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.main.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC_REL_GROUP;
import static eki.ekilex.data.db.main.Tables.LEX_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.main.Tables.MEANING_FORUM;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE;
import static eki.ekilex.data.db.main.Tables.MEANING_IMAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE;
import static eki.ekilex.data.db.main.Tables.MEANING_NOTE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.main.Tables.MEANING_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_DEFINITION;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.USAGE_TRANSLATION;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static eki.ekilex.data.db.main.Tables.WORD_FREEFORM;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.LexCollocationGroupTuple;
import eki.ekilex.data.LexCollocationTuple;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.main.tables.Collocation;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.LexColloc;
import eki.ekilex.data.db.main.tables.LexCollocPosGroup;
import eki.ekilex.data.db.main.tables.LexCollocRelGroup;
import eki.ekilex.data.db.main.tables.LexRelation;
import eki.ekilex.data.db.main.tables.LexemeActivityLog;
import eki.ekilex.data.db.main.tables.LexemeNote;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.MeaningActivityLog;
import eki.ekilex.data.db.main.tables.MeaningNote;
import eki.ekilex.data.db.main.tables.MeaningRelation;
import eki.ekilex.data.db.main.tables.MeaningSemanticType;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordActivityLog;
import eki.ekilex.data.db.main.tables.WordEtymology;
import eki.ekilex.data.db.main.tables.WordEtymologyRelation;
import eki.ekilex.data.db.main.tables.WordGroupMember;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.db.main.tables.WordWordType;
import eki.ekilex.data.db.main.tables.records.DefinitionDatasetRecord;
import eki.ekilex.data.db.main.tables.records.DefinitionFreeformRecord;
import eki.ekilex.data.db.main.tables.records.DefinitionNoteRecord;
import eki.ekilex.data.db.main.tables.records.DefinitionNoteSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.DefinitionRecord;
import eki.ekilex.data.db.main.tables.records.DefinitionSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.FreeformRecord;
import eki.ekilex.data.db.main.tables.records.FreeformSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.LexCollocRecord;
import eki.ekilex.data.db.main.tables.records.LexemeDerivRecord;
import eki.ekilex.data.db.main.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.main.tables.records.LexemeNoteRecord;
import eki.ekilex.data.db.main.tables.records.LexemeNoteSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.LexemePosRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRegionRecord;
import eki.ekilex.data.db.main.tables.records.LexemeRegisterRecord;
import eki.ekilex.data.db.main.tables.records.LexemeSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.LexemeTagRecord;
import eki.ekilex.data.db.main.tables.records.MeaningDomainRecord;
import eki.ekilex.data.db.main.tables.records.MeaningForumRecord;
import eki.ekilex.data.db.main.tables.records.MeaningFreeformRecord;
import eki.ekilex.data.db.main.tables.records.MeaningImageRecord;
import eki.ekilex.data.db.main.tables.records.MeaningImageSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.MeaningNoteRecord;
import eki.ekilex.data.db.main.tables.records.MeaningNoteSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.MeaningRecord;
import eki.ekilex.data.db.main.tables.records.MeaningRelationRecord;
import eki.ekilex.data.db.main.tables.records.MeaningSemanticTypeRecord;
import eki.ekilex.data.db.main.tables.records.MeaningTagRecord;
import eki.ekilex.data.db.main.tables.records.ParadigmFormRecord;
import eki.ekilex.data.db.main.tables.records.ParadigmRecord;
import eki.ekilex.data.db.main.tables.records.UsageDefinitionRecord;
import eki.ekilex.data.db.main.tables.records.UsageRecord;
import eki.ekilex.data.db.main.tables.records.UsageSourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.UsageTranslationRecord;
import eki.ekilex.data.db.main.tables.records.WordEtymologyRecord;
import eki.ekilex.data.db.main.tables.records.WordEtymologyRelationRecord;
import eki.ekilex.data.db.main.tables.records.WordEtymologySourceLinkRecord;
import eki.ekilex.data.db.main.tables.records.WordForumRecord;
import eki.ekilex.data.db.main.tables.records.WordFreeformRecord;
import eki.ekilex.data.db.main.tables.records.WordGroupMemberRecord;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.data.db.main.tables.records.WordRelationRecord;
import eki.ekilex.data.db.main.tables.records.WordTagRecord;
import eki.ekilex.data.db.main.tables.records.WordWordTypeRecord;

@Component
public class CompositionDbService extends AbstractDataDbService implements GlobalConstant {

	public void joinMeanings(Long targetMeaningId, Long sourceMeaningId) {

		moveLexemes(targetMeaningId, sourceMeaningId);
		moveMeaningDefinitions(targetMeaningId, sourceMeaningId);
		moveMeaningDomains(targetMeaningId, sourceMeaningId);
		moveMeaningFreeforms(targetMeaningId, sourceMeaningId);
		moveMeaningRelations(targetMeaningId, sourceMeaningId);
		moveMeaningSemanticTypes(targetMeaningId, sourceMeaningId);
		moveMeaningActivityLogs(targetMeaningId, sourceMeaningId);
		moveMeaningNotes(targetMeaningId, sourceMeaningId);

		mainDb
				.delete(MEANING)
				.where(MEANING.ID.eq(sourceMeaningId))
				.execute();
	}

	public void joinLexemes(Long targetLexemeId, Long sourceLexemeId) {

		moveLexemeCollocations(targetLexemeId, sourceLexemeId);
		moveLexemeSourceLinks(targetLexemeId, sourceLexemeId);
		moveLexemeRegisters(targetLexemeId, sourceLexemeId);
		moveLexemePos(targetLexemeId, sourceLexemeId);
		moveLexemeFreeforms(targetLexemeId, sourceLexemeId);
		moveLexemeDerivs(targetLexemeId, sourceLexemeId);
		moveLexemeRegions(targetLexemeId, sourceLexemeId);
		moveLexemeRelations(targetLexemeId, sourceLexemeId);
		moveLexemeTags(targetLexemeId, sourceLexemeId);
		moveLexemeNotes(targetLexemeId, sourceLexemeId);
		moveLexemeUsages(targetLexemeId, sourceLexemeId);
		moveCollocationMembers(targetLexemeId, sourceLexemeId);
		moveLexemeActivityLog(targetLexemeId, sourceLexemeId);
		mergeLexemeFields(targetLexemeId, sourceLexemeId);

		mainDb
				.delete(LEXEME)
				.where(LEXEME.ID.eq(sourceLexemeId))
				.execute();
	}

	public void joinWordData(Long targetWordId, Long sourceWordId) {

		mergeWordFields(targetWordId, sourceWordId);
		moveWordFreeforms(targetWordId, sourceWordId);
		moveWordRelations(targetWordId, sourceWordId);
		moveWordTypeCodes(targetWordId, sourceWordId);
		moveWordGroupMembers(targetWordId, sourceWordId);
		moveWordEtymologyAndEtymologyRelations(targetWordId, sourceWordId);
		moveWordActivityLogs(targetWordId, sourceWordId);
	}

	private void mergeLexemeFields(Long targetLexemeId, Long sourceLexemeId) {

		LexemeRecord sourceLexeme = mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.ID.eq(sourceLexemeId))
				.fetchOne();

		LexemeRecord targetLexeme = mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.ID.eq(targetLexemeId))
				.fetchOne();

		boolean isPublic = sourceLexeme.getIsPublic() || targetLexeme.getIsPublic();
		boolean isWord = sourceLexeme.getIsWord() || targetLexeme.getIsWord();
		boolean isCollocation = sourceLexeme.getIsCollocation() || targetLexeme.getIsCollocation();
		Complexity complexity = Complexity.ANY;
		mainDb
				.update(LEXEME)
				.set(LEXEME.IS_PUBLIC, isPublic)
				.set(LEXEME.IS_WORD, isWord)
				.set(LEXEME.IS_COLLOCATION, isCollocation)
				.set(LEXEME.COMPLEXITY, complexity.name())
				.where(LEXEME.ID.eq(targetLexemeId))
				.execute();
	}

	private void moveLexemeActivityLog(Long targetLexemeId, Long sourceLexemeId) {

		LexemeActivityLog lals = LEXEME_ACTIVITY_LOG.as("lals");
		LexemeActivityLog lalt = LEXEME_ACTIVITY_LOG.as("lalt");
		mainDb
				.update(lals)
				.set(lals.LEXEME_ID, targetLexemeId)
				.where(
						lals.LEXEME_ID.eq(sourceLexemeId)
								.andNotExists(DSL
										.select(lalt.ID)
										.from(lalt)
										.where(
												lalt.LEXEME_ID.eq(targetLexemeId)
														.and(lalt.ACTIVITY_LOG_ID.eq(lals.ACTIVITY_LOG_ID)))))
				.execute();
	}

	private void moveLexemeTags(Long targetLexemeId, Long sourceLexemeId) {

		LexemeTag lt1 = LEXEME_TAG.as("lt1");
		LexemeTag lt2 = LEXEME_TAG.as("lt2");
		mainDb
				.update(lt1)
				.set(lt1.LEXEME_ID, targetLexemeId)
				.where(lt1.LEXEME_ID.eq(sourceLexemeId))
				.andNotExists(DSL
						.select(lt2.ID)
						.from(lt2)
						.where(lt2.LEXEME_ID.eq(targetLexemeId)
								.and(lt2.TAG_NAME.eq(lt1.TAG_NAME))))
				.execute();
	}

	private void moveLexemeCollocations(Long targetLexemeId, Long sourceLexemeId) {

		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		mainDb
				.update(lc1)
				.set(lc1.LEXEME_ID, targetLexemeId)
				.where(lc1.LEXEME_ID.eq(sourceLexemeId))
				.andNotExists(DSL
						.select(lc2.ID)
						.from(lc2)
						.where(lc2.LEXEME_ID.eq(targetLexemeId)
								.and(lc2.COLLOCATION_ID.eq(lc1.COLLOCATION_ID))))
				.execute();

		mainDb
				.update(LEX_COLLOC_POS_GROUP)
				.set(LEX_COLLOC_POS_GROUP.LEXEME_ID, targetLexemeId)
				.where(LEX_COLLOC_POS_GROUP.LEXEME_ID.eq(sourceLexemeId))
				.execute();
	}

	private void moveLexemeUsages(Long targetLexemeId, Long sourceLexemeId) {

		Usage u = USAGE.as("u");
		mainDb
				.update(u)
				.set(u.LEXEME_ID, targetLexemeId)
				.where(u.LEXEME_ID.eq(sourceLexemeId))
				.execute();
	}

	private void moveLexemeNotes(Long targetLexemeId, Long sourceLexemeId) {

		LexemeNote n = LEXEME_NOTE.as("n");
		mainDb
				.update(n)
				.set(n.LEXEME_ID, targetLexemeId)
				.where(n.LEXEME_ID.eq(sourceLexemeId))
				.execute();
	}

	private void moveCollocationMembers(Long targetLexemeId, Long sourceLexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		mainDb
				.update(cm)
				.set(cm.MEMBER_LEXEME_ID, targetLexemeId)
				.where(cm.MEMBER_LEXEME_ID.eq(sourceLexemeId))
				.execute();

		mainDb
				.update(cm)
				.set(cm.COLLOC_LEXEME_ID, targetLexemeId)
				.where(cm.COLLOC_LEXEME_ID.eq(sourceLexemeId))
				.execute();
	}

	private void moveLexemeRegions(Long targetLexemeId, Long sourceLexemeId) {

		mainDb
				.update(LEXEME_REGION)
				.set(LEXEME_REGION.LEXEME_ID, targetLexemeId)
				.where(
						LEXEME_REGION.LEXEME_ID.eq(sourceLexemeId)
								.and(LEXEME_REGION.REGION_CODE.notIn(
										DSL.select(LEXEME_REGION.REGION_CODE).from(LEXEME_REGION).where(LEXEME_REGION.LEXEME_ID.eq(targetLexemeId)))))
				.execute();
	}

	private void moveLexemeRelations(Long targetLexemeId, Long sourceLexemeId) {

		LexRelation lr1 = LEX_RELATION.as("lr1");
		LexRelation lr2 = LEX_RELATION.as("lr2");

		mainDb
				.update(lr1)
				.set(lr1.LEXEME1_ID, targetLexemeId)
				.where(
						lr1.LEXEME1_ID.eq(sourceLexemeId)
								.and(lr1.LEXEME2_ID.ne(targetLexemeId)))
				.andNotExists(DSL
						.select(lr2.ID)
						.from(lr2)
						.where(
								lr2.LEXEME1_ID.eq(targetLexemeId)
										.and(lr2.LEXEME2_ID.eq(lr1.LEXEME2_ID))
										.and(lr2.LEX_REL_TYPE_CODE.eq(lr1.LEX_REL_TYPE_CODE))))
				.execute();

		mainDb
				.update(lr1)
				.set(lr1.LEXEME2_ID, targetLexemeId)
				.where(
						lr1.LEXEME2_ID.eq(sourceLexemeId)
								.and(lr1.LEXEME1_ID.ne(targetLexemeId)))
				.andNotExists(DSL
						.select(lr2.ID)
						.from(lr2)
						.where(
								lr2.LEXEME2_ID.eq(targetLexemeId)
										.and(lr2.LEXEME1_ID.eq(lr1.LEXEME1_ID))
										.and(lr2.LEX_REL_TYPE_CODE.eq(lr1.LEX_REL_TYPE_CODE))))
				.execute();
	}

	private void moveLexemeDerivs(Long targetLexemeId, Long sourceLexemeId) {

		mainDb
				.update(LEXEME_DERIV)
				.set(LEXEME_DERIV.LEXEME_ID, targetLexemeId)
				.where(LEXEME_DERIV.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_DERIV.DERIV_CODE.notIn(
								DSL.select(LEXEME_DERIV.DERIV_CODE).from(LEXEME_DERIV).where(LEXEME_DERIV.LEXEME_ID.eq(targetLexemeId)))))
				.execute();
	}

	private void moveLexemeFreeforms(Long targetLexemeId, Long sourceLexemeId) {

		Result<FreeformRecord> lexemeFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(LEXEME_FREEFORM.FREEFORM_ID).from(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(targetLexemeId))))
				.fetch();
		Result<FreeformRecord> sourceLexemeFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(LEXEME_FREEFORM.FREEFORM_ID).from(LEXEME_FREEFORM).where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId))))
				.fetch();

		List<Long> nonDublicateFreeformIds = getNonDuplicateFreeformIds(lexemeFreeforms, sourceLexemeFreeforms);

		mainDb
				.update(LEXEME_FREEFORM)
				.set(LEXEME_FREEFORM.LEXEME_ID, targetLexemeId)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_FREEFORM.FREEFORM_ID.in(nonDublicateFreeformIds)))
				.execute();

		mainDb
				.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(LEXEME_FREEFORM.FREEFORM_ID)
						.from(LEXEME_FREEFORM)
						.where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId))))
				.execute();

		mainDb
				.delete(LEXEME_FREEFORM)
				.where(LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId))
				.execute();
	}

	private void moveLexemePos(Long targetLexemeId, Long sourceLexemeId) {

		mainDb
				.update(LEXEME_POS)
				.set(LEXEME_POS.LEXEME_ID, targetLexemeId)
				.where(LEXEME_POS.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_POS.POS_CODE.notIn(DSL
								.select(LEXEME_POS.POS_CODE)
								.from(LEXEME_POS)
								.where(LEXEME_POS.LEXEME_ID.eq(targetLexemeId)))))
				.execute();
	}

	private void moveLexemeRegisters(Long targetLexemeId, Long sourceLexemeId) {

		mainDb
				.update(LEXEME_REGISTER)
				.set(LEXEME_REGISTER.LEXEME_ID, targetLexemeId)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(sourceLexemeId)
						.and(LEXEME_REGISTER.REGISTER_CODE.notIn(
								DSL.select(LEXEME_REGISTER.REGISTER_CODE).from(LEXEME_REGISTER).where(LEXEME_REGISTER.LEXEME_ID.eq(targetLexemeId)))))
				.execute();
	}

	private void moveLexemeSourceLinks(Long targetLexemeId, Long sourceLexemeId) {

		LexemeSourceLink lsl1 = LEXEME_SOURCE_LINK.as("lsl1");
		LexemeSourceLink lsl2 = LEXEME_SOURCE_LINK.as("lsl2");
		mainDb
				.update(lsl1)
				.set(lsl1.LEXEME_ID, targetLexemeId)
				.where(lsl1.LEXEME_ID.eq(sourceLexemeId))
				.andNotExists(DSL
						.select(lsl2.ID)
						.from(lsl2)
						.where(lsl2.LEXEME_ID.eq(targetLexemeId)
								.and(lsl2.SOURCE_ID.eq(lsl1.SOURCE_ID))))
				.execute();
	}

	private void moveLexemes(Long targetMeaningId, Long sourceMeaningId) {
		mainDb
				.update(LEXEME)
				.set(LEXEME.MEANING_ID, targetMeaningId)
				.where(LEXEME.MEANING_ID.eq(sourceMeaningId))
				.execute();
	}

	private void moveMeaningSemanticTypes(Long targetMeaningId, Long sourceMeaningId) {

		MeaningSemanticType mst1 = MEANING_SEMANTIC_TYPE.as("mst1");
		MeaningSemanticType mst2 = MEANING_SEMANTIC_TYPE.as("mst2");

		mainDb
				.update(mst1)
				.set(mst1.MEANING_ID, targetMeaningId)
				.where(mst1.MEANING_ID.eq(sourceMeaningId))
				.andNotExists(DSL
						.select(mst2.ID)
						.from(mst2)
						.where(
								mst2.MEANING_ID.eq(targetMeaningId)
										.and(mst2.SEMANTIC_TYPE_CODE.eq(mst1.SEMANTIC_TYPE_CODE))))
				.execute();
	}

	private void moveMeaningRelations(Long targetMeaningId, Long sourceMeaningId) {

		MeaningRelation mr1 = MEANING_RELATION.as("mr1");
		MeaningRelation mr2 = MEANING_RELATION.as("mr2");

		mainDb
				.update(mr1)
				.set(mr1.MEANING1_ID, targetMeaningId)
				.where(
						mr1.MEANING1_ID.eq(sourceMeaningId)
								.and(mr1.MEANING2_ID.ne(targetMeaningId)))
				.andNotExists(DSL
						.select(mr2.ID)
						.from(mr2)
						.where(
								mr2.MEANING1_ID.eq(targetMeaningId)
										.and(mr2.MEANING2_ID.eq(mr1.MEANING2_ID))
										.and(mr2.MEANING_REL_TYPE_CODE.eq(mr1.MEANING_REL_TYPE_CODE))))
				.execute();

		mainDb
				.update(mr1)
				.set(mr1.MEANING2_ID, targetMeaningId)
				.where(
						mr1.MEANING2_ID.eq(sourceMeaningId)
								.and(mr1.MEANING1_ID.ne(targetMeaningId)))
				.andNotExists(DSL
						.select(mr2.ID)
						.from(mr2)
						.where(
								mr2.MEANING2_ID.eq(targetMeaningId)
										.and(mr2.MEANING1_ID.eq(mr1.MEANING1_ID))
										.and(mr2.MEANING_REL_TYPE_CODE.eq(mr1.MEANING_REL_TYPE_CODE))))
				.execute();
	}

	private void moveMeaningFreeforms(Long targetMeaningId, Long sourceMeaningId) {

		Result<FreeformRecord> meaningFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID).from(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(targetMeaningId))))
				.fetch();
		Result<FreeformRecord> sourceMeaningFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(MEANING_FREEFORM.FREEFORM_ID).from(MEANING_FREEFORM).where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId))))
				.fetch();

		List<Long> nonDublicateFreeformIds = getNonDuplicateFreeformIds(meaningFreeforms, sourceMeaningFreeforms);

		mainDb
				.update(MEANING_FREEFORM)
				.set(MEANING_FREEFORM.MEANING_ID, targetMeaningId)
				.where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId)
						.and(MEANING_FREEFORM.FREEFORM_ID.in(nonDublicateFreeformIds)))
				.execute();

		mainDb
				.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(MEANING_FREEFORM.FREEFORM_ID)
						.from(MEANING_FREEFORM)
						.where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId))))
				.execute();

		mainDb
				.delete(MEANING_FREEFORM)
				.where(MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId))
				.execute();
	}

	private void moveMeaningDomains(Long targetMeaningId, Long sourceMeaningId) {
		mainDb
				.update(MEANING_DOMAIN)
				.set(MEANING_DOMAIN.MEANING_ID, targetMeaningId)
				.where(
						MEANING_DOMAIN.MEANING_ID.eq(sourceMeaningId)
								.and(DSL.row(MEANING_DOMAIN.DOMAIN_CODE, MEANING_DOMAIN.DOMAIN_ORIGIN).notIn(
										DSL.select(MEANING_DOMAIN.DOMAIN_CODE, MEANING_DOMAIN.DOMAIN_ORIGIN).from(MEANING_DOMAIN).where(MEANING_DOMAIN.MEANING_ID.eq(targetMeaningId)))))
				.execute();
	}

	private void moveMeaningDefinitions(Long targetMeaningId, Long sourceMeaningId) {

		Definition def1 = DEFINITION.as("def1");
		Definition def2 = DEFINITION.as("def2");

		mainDb
				.update(def1)
				.set(def1.MEANING_ID, targetMeaningId)
				.where(def1.MEANING_ID.eq(sourceMeaningId))
				.andNotExists(DSL
						.select(def2.ID)
						.from(def2)
						.where(def2.MEANING_ID.eq(targetMeaningId)
								.and(def2.VALUE.eq(def1.VALUE))
								.and(def2.COMPLEXITY.eq(def1.COMPLEXITY))))
				.execute();
	}

	private void moveMeaningNotes(Long targetMeaningId, Long sourceMeaningId) {

		MeaningNote n = MEANING_NOTE.as("n");
		mainDb
				.update(n)
				.set(n.MEANING_ID, targetMeaningId)
				.where(n.MEANING_ID.eq(sourceMeaningId))
				.execute();
	}

	private void moveMeaningActivityLogs(Long targetMeaningId, Long sourceMeaningId) {

		MeaningActivityLog mals = MEANING_ACTIVITY_LOG.as("mals");
		MeaningActivityLog malt = MEANING_ACTIVITY_LOG.as("malt");

		mainDb
				.update(mals)
				.set(mals.MEANING_ID, targetMeaningId)
				.where(
						mals.MEANING_ID.eq(sourceMeaningId)
								.andNotExists(DSL
										.select(malt.ID)
										.from(malt)
										.where(
												malt.MEANING_ID.eq(targetMeaningId)
														.and(malt.ACTIVITY_LOG_ID.eq(mals.ACTIVITY_LOG_ID)))))
				.execute();
	}

	public Long cloneLexeme(Long sourceLexemeId, Long targetMeaningId, Long targetWordId) {

		LexemeRecord sourceLexeme = mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.ID.eq(sourceLexemeId))
				.fetchOne();
		LexemeRecord targetLexeme = sourceLexeme.copy();
		if (targetMeaningId != null) {
			targetLexeme.setMeaningId(targetMeaningId);
		}
		if (targetWordId != null) {
			targetLexeme.setWordId(targetWordId);
		}
		targetLexeme.changed(LEXEME.ORDER_BY, false);
		targetLexeme.store();
		return targetLexeme.getId();
	}

	public Long cloneEmptyLexeme(Long sourceLexemeId, Long targetMeaningId) {

		LexemeRecord sourceLexeme = mainDb
				.selectFrom(LEXEME)
				.where(LEXEME.ID.eq(sourceLexemeId))
				.fetchOne();
		LexemeRecord targetLexeme = sourceLexeme.copy();
		targetLexeme.setMeaningId(targetMeaningId);
		targetLexeme.changed(LEXEME.ORDER_BY, false);
		targetLexeme.setIsPublic(PUBLICITY_PUBLIC);
		targetLexeme.store();
		return targetLexeme.getId();
	}

	// TODO working on lexemes

	public void cloneLexemeUsages(Long sourceLexemeId, Long targetLexemeId) {

		Result<UsageRecord> sourceUsages = mainDb
				.selectFrom(USAGE)
				.where(USAGE.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(USAGE.ORDER_BY)
				.fetch();

		sourceUsages.forEach(sourceUsage -> {

			UsageRecord targetUsage = sourceUsage.copy();
			targetUsage.setLexemeId(targetLexemeId);
			targetUsage.changed(USAGE.ORDER_BY, false);
			targetUsage.store();
			Long targetUsageId = targetUsage.getId();
			Long sourceUsageId = sourceUsage.getId();

			Result<UsageSourceLinkRecord> sourceUsageSourceLinks = mainDb
					.selectFrom(USAGE_SOURCE_LINK)
					.where(USAGE_SOURCE_LINK.USAGE_ID.eq(sourceUsageId))
					.orderBy(USAGE_SOURCE_LINK.ORDER_BY)
					.fetch();

			sourceUsageSourceLinks.stream()
					.map(UsageSourceLinkRecord::copy)
					.forEach(targetUsageSourceLink -> {
						targetUsageSourceLink.setUsageId(targetUsageId);
						targetUsageSourceLink.changed(USAGE_SOURCE_LINK.ORDER_BY, false);
						targetUsageSourceLink.store();
					});

			Result<UsageDefinitionRecord> sourceUsageDefinitions = mainDb
					.selectFrom(USAGE_DEFINITION)
					.where(USAGE_DEFINITION.USAGE_ID.eq(sourceUsageId))
					.orderBy(USAGE_DEFINITION.ORDER_BY)
					.fetch();

			sourceUsageDefinitions.stream()
					.map(UsageDefinitionRecord::copy)
					.forEach(targetUsageDefinition -> {
						targetUsageDefinition.setUsageId(targetUsageId);
						targetUsageDefinition.store();
					});

			Result<UsageTranslationRecord> sourceUsageTranslations = mainDb
					.selectFrom(USAGE_TRANSLATION)
					.where(USAGE_TRANSLATION.USAGE_ID.eq(sourceUsageId))
					.orderBy(USAGE_TRANSLATION.ORDER_BY)
					.fetch();

			sourceUsageTranslations.stream()
					.map(UsageTranslationRecord::copy)
					.forEach(targetUsageTranslation -> {
						targetUsageTranslation.setUsageId(targetUsageId);
						targetUsageTranslation.store();
					});
		});
	}

	public void cloneLexemeNotes(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeNoteRecord> sourceLexemeNotes = mainDb
				.selectFrom(LEXEME_NOTE)
				.where(LEXEME_NOTE.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_NOTE.ORDER_BY)
				.fetch();

		sourceLexemeNotes.forEach(sourceLexemeNote -> {

			LexemeNoteRecord targetLexemeNote = sourceLexemeNote.copy();
			targetLexemeNote.setLexemeId(targetLexemeId);
			targetLexemeNote.changed(LEXEME_NOTE.ORDER_BY, false);
			targetLexemeNote.store();
			Long targetLexemeNoteId = targetLexemeNote.getId();
			Long sourceLexemeNoteId = sourceLexemeNote.getId();

			Result<LexemeNoteSourceLinkRecord> sourceLexemeNoteSourceLinks = mainDb
					.selectFrom(LEXEME_NOTE_SOURCE_LINK)
					.where(LEXEME_NOTE_SOURCE_LINK.LEXEME_NOTE_ID.eq(sourceLexemeNoteId))
					.orderBy(LEXEME_NOTE_SOURCE_LINK.ORDER_BY)
					.fetch();

			sourceLexemeNoteSourceLinks.stream()
					.map(LexemeNoteSourceLinkRecord::copy)
					.forEach(targetLexemeNoteSourceLink -> {
						targetLexemeNoteSourceLink.setLexemeNoteId(targetLexemeNoteId);
						targetLexemeNoteSourceLink.changed(LEXEME_NOTE_SOURCE_LINK.ORDER_BY, false);
						targetLexemeNoteSourceLink.store();
					});
		});
	}

	public void cloneLexemeTags(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeTagRecord> sourceLexemeTags = mainDb
				.selectFrom(LEXEME_TAG)
				.where(LEXEME_TAG.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_TAG.ID)
				.fetch();
		sourceLexemeTags.stream()
				.map(LexemeTagRecord::copy)
				.forEach(targetLexemeTag -> {
					targetLexemeTag.setLexemeId(targetLexemeId);
					targetLexemeTag.store();
				});
	}

	public void cloneLexemeDerivs(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeDerivRecord> sourceLexemeDerivs = mainDb
				.selectFrom(LEXEME_DERIV)
				.where(LEXEME_DERIV.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_DERIV.ORDER_BY)
				.fetch();
		sourceLexemeDerivs.stream()
				.map(LexemeDerivRecord::copy)
				.forEach(targetLexemeDeriv -> {
					targetLexemeDeriv.setLexemeId(targetLexemeId);
					targetLexemeDeriv.changed(LEXEME_DERIV.ORDER_BY, false);
					targetLexemeDeriv.store();
				});
	}

	public void cloneLexemeRegions(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeRegionRecord> sourceLexemeRegions = mainDb
				.selectFrom(LEXEME_REGION)
				.where(LEXEME_REGION.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_REGION.ORDER_BY)
				.fetch();
		sourceLexemeRegions.stream()
				.map(LexemeRegionRecord::copy)
				.forEach(targetLexemeRegion -> {
					targetLexemeRegion.setLexemeId(targetLexemeId);
					targetLexemeRegion.changed(LEXEME_REGION.ORDER_BY, false);
					targetLexemeRegion.store();
				});
	}

	public void cloneLexemeFreeforms(Long sourceLexemeId, Long targetLexemeId, boolean isPublicDataOnly) {

		Condition where = LEXEME_FREEFORM.LEXEME_ID.eq(sourceLexemeId)
				.and(LEXEME_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID));
		if (isPublicDataOnly) {
			where = where.and(FREEFORM.IS_PUBLIC.isTrue());
		}

		Result<FreeformRecord> sourceFreeforms = mainDb
				.select(FREEFORM.fields())
				.from(FREEFORM, LEXEME_FREEFORM)
				.where(where)
				.orderBy(LEXEME_FREEFORM.ORDER_BY)
				.fetchInto(FREEFORM);

		sourceFreeforms.forEach(sourceFreeform -> {

			FreeformRecord targetFreeform = sourceFreeform.copy();
			targetFreeform.setParentId(null);
			targetFreeform.changed(FREEFORM.ORDER_BY, false);
			targetFreeform.store();
			Long targetFreeformId = targetFreeform.getId();
			Long sourceFreeformId = sourceFreeform.getId();

			LexemeFreeformRecord targetLexemeFreeform = mainDb.newRecord(LEXEME_FREEFORM);
			targetLexemeFreeform.setLexemeId(targetLexemeId);
			targetLexemeFreeform.setFreeformId(targetFreeformId);
			targetLexemeFreeform.store();

			cloneFreeformChildren(sourceFreeformId, targetFreeformId);
			cloneFreeformSourceLinks(sourceFreeformId, targetFreeformId);
		});
	}

	public void cloneLexemePoses(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemePosRecord> sourceLexemePoses = mainDb
				.selectFrom(LEXEME_POS)
				.where(LEXEME_POS.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_POS.ORDER_BY)
				.fetch();
		sourceLexemePoses.stream()
				.map(LexemePosRecord::copy)
				.forEach(targetLexemePos -> {
					targetLexemePos.setLexemeId(targetLexemeId);
					targetLexemePos.changed(LEXEME_POS.ORDER_BY, false);
					targetLexemePos.store();
				});
	}

	public void cloneLexemeRegisters(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeRegisterRecord> sourceLexemeRegisters = mainDb
				.selectFrom(LEXEME_REGISTER)
				.where(LEXEME_REGISTER.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_REGISTER.ORDER_BY)
				.fetch();
		sourceLexemeRegisters.stream()
				.map(LexemeRegisterRecord::copy)
				.forEach(targetLexemeRegister -> {
					targetLexemeRegister.setLexemeId(targetLexemeId);
					targetLexemeRegister.changed(LEXEME_REGISTER.ORDER_BY, false);
					targetLexemeRegister.store();
				});
	}

	public void cloneLexemeSoureLinks(Long sourceLexemeId, Long targetLexemeId) {

		Result<LexemeSourceLinkRecord> sourceLexemeSourceLinks = mainDb
				.selectFrom(LEXEME_SOURCE_LINK)
				.where(LEXEME_SOURCE_LINK.LEXEME_ID.eq(sourceLexemeId))
				.orderBy(LEXEME_SOURCE_LINK.ORDER_BY)
				.fetch();
		sourceLexemeSourceLinks.stream()
				.map(LexemeSourceLinkRecord::copy)
				.forEach(targetLexemeSourceLink -> {
					targetLexemeSourceLink.setLexemeId(targetLexemeId);
					targetLexemeSourceLink.changed(LEXEME_SOURCE_LINK.ORDER_BY, false);
					targetLexemeSourceLink.store();
				});
	}

	// TODO outdated
	@Deprecated
	public void cloneLexemeCollocations(Long lexemeId, Long clonedLexemeId) {

		Map<Long, Long> collocIdMap = new HashMap<>();
		Map<Long, Long> relGroupIdMap = new HashMap<>();
		Map<Long, Long> posGroupIdMap = new HashMap<>();
		List<LexCollocationTuple> lexCollocationTuples = getLexCollocationTuples(lexemeId);
		List<LexCollocationGroupTuple> lexCollocationGroupTuples = getLexCollocationGroupTuples(lexemeId);
		Map<Long, LexCollocationGroupTuple> lexCollocationGroupTupleMap = lexCollocationGroupTuples.stream()
				.collect(Collectors.toMap(lexCollocationGroupTuple -> lexCollocationGroupTuple.getRelGroupId(), lexCollocationGroupTuple -> lexCollocationGroupTuple));

		for (LexCollocationTuple lexCollocationTuple : lexCollocationTuples) {
			Long lexCollocId = lexCollocationTuple.getLexCollocId();
			Long collocId = lexCollocationTuple.getCollocId();
			Long relGroupId = lexCollocationTuple.getRelGroupId();

			Long clonedCollocId = collocIdMap.get(collocId);
			if (clonedCollocId == null) {
				clonedCollocId = createCollocation(lexCollocationTuple);
				collocIdMap.put(collocId, clonedCollocId);

				Result<LexCollocRecord> relatedLexCollocs = mainDb.selectFrom(LEX_COLLOC).where(LEX_COLLOC.COLLOCATION_ID.eq(collocId).and(LEX_COLLOC.ID.ne(lexCollocId))).fetch();
				for (LexCollocRecord relatedLexColloc : relatedLexCollocs) {
					LexCollocRecord clonedRelatedLexColloc = relatedLexColloc.copy();
					clonedRelatedLexColloc.setCollocationId(clonedCollocId);
					clonedRelatedLexColloc.store();
				}
			}

			Long clonedRelGroupId = null;
			if (relGroupId != null) {
				clonedRelGroupId = relGroupIdMap.get(relGroupId);
				if (clonedRelGroupId == null) {
					LexCollocationGroupTuple lexCollocationGroupTuple = lexCollocationGroupTupleMap.get(relGroupId);
					Long posGroupId = lexCollocationGroupTuple.getPosGroupId();
					Long clonedPosGroupId = posGroupIdMap.get(posGroupId);
					if (clonedPosGroupId == null) {
						clonedPosGroupId = createPosGroup(lexCollocationGroupTuple, clonedLexemeId);
						posGroupIdMap.put(posGroupId, clonedPosGroupId);
					}
					clonedRelGroupId = createRelGroup(lexCollocationGroupTuple, clonedPosGroupId);
					relGroupIdMap.put(relGroupId, clonedRelGroupId);
				}
			}

			createLexColloc(lexCollocationTuple, clonedLexemeId, clonedRelGroupId, clonedCollocId);
		}
	}

	// TODO outdated
	@Deprecated
	private List<LexCollocationTuple> getLexCollocationTuples(Long lexemeId) {

		LexColloc lc = LEX_COLLOC.as("lc");
		Collocation c = COLLOCATION.as("c");

		return mainDb
				.select(
						lc.ID.as("lex_colloc_id"),
						lc.REL_GROUP_ID.as("rel_group_id"),
						lc.MEMBER_FORM.as("member_form"),
						lc.CONJUNCT.as("conjunct"),
						lc.WEIGHT.as("weight"),
						lc.MEMBER_ORDER.as("member_order"),
						lc.GROUP_ORDER.as("group_order"),
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						c.COMPLEXITY.as("colloc_complexity"))
				.from(lc, c)
				.where(lc.LEXEME_ID.eq(lexemeId)
						.and(c.ID.eq(lc.COLLOCATION_ID)))
				.fetchInto(LexCollocationTuple.class);
	}

	// TODO outdated
	@Deprecated
	private List<LexCollocationGroupTuple> getLexCollocationGroupTuples(Long lexemeId) {

		LexCollocRelGroup lcrg = LEX_COLLOC_REL_GROUP.as("lcrg");
		LexCollocPosGroup lcpg = LEX_COLLOC_POS_GROUP.as("lcpg");

		return mainDb
				.select(
						lcpg.ID.as("pos_group_id"),
						lcpg.POS_GROUP_CODE.as("pos_group_code"),
						lcpg.ORDER_BY.as("pos_group_order_by"),
						lcrg.ID.as("rel_group_id"),
						lcrg.NAME.as("rel_group_name"),
						lcrg.FREQUENCY.as("rel_group_frequency"),
						lcrg.SCORE.as("rel_group_score"),
						lcrg.ORDER_BY.as("rel_group_order_by"))
				.from(LEXEME
						.innerJoin(lcpg).on(lcpg.LEXEME_ID.eq(LEXEME.ID))
						.innerJoin(lcrg).on(lcrg.POS_GROUP_ID.eq(lcpg.ID)))
				.where(LEXEME.ID.eq(lexemeId))
				.fetchInto(LexCollocationGroupTuple.class);
	}

	// TODO outdated
	@Deprecated
	private Long createCollocation(LexCollocationTuple lexCollocationTuple) {

		String value = lexCollocationTuple.getCollocValue();
		String definition = lexCollocationTuple.getCollocDefinition();
		BigDecimal frequency = lexCollocationTuple.getCollocFrequency() == null ? null : BigDecimal.valueOf(lexCollocationTuple.getCollocFrequency());
		BigDecimal score = lexCollocationTuple.getCollocScore() == null ? null : BigDecimal.valueOf(lexCollocationTuple.getCollocScore());
		String[] usages = lexCollocationTuple.getCollocUsages() == null ? null : lexCollocationTuple.getCollocUsages().toArray(new String[0]);
		String complexity = lexCollocationTuple.getCollocComplexity();

		return mainDb
				.insertInto(COLLOCATION,
						COLLOCATION.VALUE,
						COLLOCATION.DEFINITION,
						COLLOCATION.FREQUENCY,
						COLLOCATION.SCORE,
						COLLOCATION.USAGES,
						COLLOCATION.COMPLEXITY)
				.values(value, definition, frequency, score, usages, complexity)
				.returning(COLLOCATION.ID)
				.fetchOne()
				.getId();
	}

	// TODO outdated
	@Deprecated
	private Long createPosGroup(LexCollocationGroupTuple lexCollocationGroupTuple, Long lexemeId) {

		String posGroupCode = lexCollocationGroupTuple.getPosGroupCode();
		Long orderBy = lexCollocationGroupTuple.getPosGroupOrderBy();

		return mainDb
				.insertInto(LEX_COLLOC_POS_GROUP,
						LEX_COLLOC_POS_GROUP.LEXEME_ID,
						LEX_COLLOC_POS_GROUP.POS_GROUP_CODE,
						LEX_COLLOC_POS_GROUP.ORDER_BY)
				.values(lexemeId, posGroupCode, orderBy)
				.returning(LEX_COLLOC_POS_GROUP.ID)
				.fetchOne()
				.getId();
	}

	// TODO outdated
	@Deprecated
	private Long createRelGroup(LexCollocationGroupTuple lexCollocationGroupTuple, Long posGroupId) {

		String name = lexCollocationGroupTuple.getRelGroupName();
		BigDecimal frequency = lexCollocationGroupTuple.getRelGroupFrequency() == null ? null : BigDecimal.valueOf(lexCollocationGroupTuple.getRelGroupFrequency());
		BigDecimal score = lexCollocationGroupTuple.getRelGroupScore() == null ? null : BigDecimal.valueOf(lexCollocationGroupTuple.getRelGroupScore());
		Long orderBy = lexCollocationGroupTuple.getRelGroupOrderBy();

		return mainDb
				.insertInto(LEX_COLLOC_REL_GROUP,
						LEX_COLLOC_REL_GROUP.POS_GROUP_ID,
						LEX_COLLOC_REL_GROUP.NAME,
						LEX_COLLOC_REL_GROUP.FREQUENCY,
						LEX_COLLOC_REL_GROUP.SCORE,
						LEX_COLLOC_REL_GROUP.ORDER_BY)
				.values(posGroupId, name, frequency, score, orderBy)
				.returning(LEX_COLLOC_REL_GROUP.ID)
				.fetchOne()
				.getId();
	}

	// TODO outdated
	@Deprecated
	private Long createLexColloc(LexCollocationTuple lexCollocationTuple, Long lexemeId, Long relGroupId, Long collocationId) {

		String memberForm = lexCollocationTuple.getMemberForm();
		String conjunct = lexCollocationTuple.getConjunct();
		BigDecimal weight = lexCollocationTuple.getWeight() == null ? null : BigDecimal.valueOf(lexCollocationTuple.getWeight());
		Integer memberOrder = lexCollocationTuple.getMemberOrder();
		Integer groupOrder = lexCollocationTuple.getGroupOrder();

		return mainDb
				.insertInto(LEX_COLLOC,
						LEX_COLLOC.LEXEME_ID,
						LEX_COLLOC.REL_GROUP_ID,
						LEX_COLLOC.COLLOCATION_ID,
						LEX_COLLOC.MEMBER_FORM,
						LEX_COLLOC.CONJUNCT,
						LEX_COLLOC.WEIGHT,
						LEX_COLLOC.MEMBER_ORDER,
						LEX_COLLOC.GROUP_ORDER)
				.values(lexemeId, relGroupId, collocationId, memberForm, conjunct, weight, memberOrder, groupOrder)
				.returning(LEX_COLLOC.ID)
				.fetchOne()
				.getId();
	}

	public Long cloneMeaning(Long meaningId) {

		MeaningRecord sourceMeaning = mainDb
				.selectFrom(MEANING)
				.where(MEANING.ID.eq(meaningId))
				.fetchOne();
		MeaningRecord targetMeaning;
		if (sourceMeaning.fields().length == 1) {
			targetMeaning = mainDb
					.insertInto(MEANING)
					.defaultValues()
					.returning(MEANING.ID)
					.fetchOne();
		} else {
			targetMeaning = sourceMeaning.copy();
			targetMeaning.store();
		}
		Long targetMeaningId = targetMeaning.getId();
		return targetMeaningId;
	}

	public void cloneMeaningDomains(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningDomainRecord> sourceMeaningDomains = mainDb
				.selectFrom(MEANING_DOMAIN)
				.where(MEANING_DOMAIN.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_DOMAIN.ORDER_BY)
				.fetch();
		sourceMeaningDomains.stream()
				.map(MeaningDomainRecord::copy)
				.forEach(targetMeaningDomain -> {
					targetMeaningDomain.setMeaningId(targetMeaningId);
					targetMeaningDomain.changed(MEANING_DOMAIN.ORDER_BY, false);
					targetMeaningDomain.store();
				});
	}

	public void cloneMeaningSemanticType(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningSemanticTypeRecord> sourceMeaningSemanticTypes = mainDb
				.selectFrom(MEANING_SEMANTIC_TYPE)
				.where(MEANING_SEMANTIC_TYPE.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_SEMANTIC_TYPE.ORDER_BY)
				.fetch();

		sourceMeaningSemanticTypes.stream()
				.map(MeaningSemanticTypeRecord::copy)
				.forEach(targetMeaningSemanticType -> {
					targetMeaningSemanticType.setMeaningId(targetMeaningId);
					targetMeaningSemanticType.changed(MEANING_SEMANTIC_TYPE.ORDER_BY, false);
					targetMeaningSemanticType.store();
				});
	}

	public void cloneMeaningTags(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningTagRecord> sourceMeaningTags = mainDb
				.selectFrom(MEANING_TAG)
				.where(MEANING_TAG.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_TAG.ID)
				.fetch();
		sourceMeaningTags.stream()
				.map(MeaningTagRecord::copy)
				.forEach(targetMeaningTag -> {
					targetMeaningTag.setMeaningId(targetMeaningId);
					targetMeaningTag.store();
				});
	}

	public void cloneMeaningForums(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningForumRecord> sourceMeaningForums = mainDb
				.selectFrom(MEANING_FORUM)
				.where(MEANING_FORUM.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_FORUM.ORDER_BY)
				.fetch();
		sourceMeaningForums.stream()
				.map(MeaningForumRecord::copy)
				.forEach(targetMeaningForum -> {
					targetMeaningForum.setMeaningId(targetMeaningId);
					targetMeaningForum.changed(MEANING_FORUM.ORDER_BY, false);
					targetMeaningForum.store();
				});
	}

	public void cloneMeaningNotes(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningNoteRecord> sourceMeaningNotes = mainDb
				.selectFrom(MEANING_NOTE)
				.where(MEANING_NOTE.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_NOTE.ORDER_BY)
				.fetch();

		sourceMeaningNotes.forEach(sourceMeaningNote -> {

			MeaningNoteRecord targetMeaningNote = sourceMeaningNote.copy();
			targetMeaningNote.setMeaningId(targetMeaningId);
			targetMeaningNote.changed(MEANING_NOTE.ORDER_BY, false);
			targetMeaningNote.store();
			Long targetMeaningNoteId = targetMeaningNote.getId();
			Long sourceMeaningNoteId = sourceMeaningNote.getId();

			Result<MeaningNoteSourceLinkRecord> sourceMeaningNoteSourceLinks = mainDb
					.selectFrom(MEANING_NOTE_SOURCE_LINK)
					.where(MEANING_NOTE_SOURCE_LINK.MEANING_NOTE_ID.eq(sourceMeaningNoteId))
					.orderBy(MEANING_NOTE_SOURCE_LINK.ORDER_BY)
					.fetch();

			sourceMeaningNoteSourceLinks.stream()
					.map(MeaningNoteSourceLinkRecord::copy)
					.forEach(targetMeaningNoteSourceLink -> {
						targetMeaningNoteSourceLink.setMeaningNoteId(targetMeaningNoteId);
						targetMeaningNoteSourceLink.changed(MEANING_NOTE_SOURCE_LINK.ORDER_BY, false);
						targetMeaningNoteSourceLink.store();
					});
		});
	}

	public void cloneMeaningImages(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningImageRecord> sourceMeaningImages = mainDb
				.selectFrom(MEANING_IMAGE)
				.where(MEANING_IMAGE.MEANING_ID.eq(sourceMeaningId))
				.orderBy(MEANING_IMAGE.ORDER_BY)
				.fetch();

		sourceMeaningImages.forEach(sourceMeaningImage -> {

			MeaningImageRecord targetMeaningImage = sourceMeaningImage.copy();
			targetMeaningImage.setMeaningId(targetMeaningId);
			targetMeaningImage.changed(MEANING_IMAGE.ORDER_BY, false);
			targetMeaningImage.store();
			Long targetMeaningImageId = targetMeaningImage.getId();
			Long sourceMeaningImageId = sourceMeaningImage.getId();

			Result<MeaningImageSourceLinkRecord> sourceMeaningImageSourceLinks = mainDb
					.selectFrom(MEANING_IMAGE_SOURCE_LINK)
					.where(MEANING_IMAGE_SOURCE_LINK.MEANING_IMAGE_ID.eq(sourceMeaningImageId))
					.orderBy(MEANING_IMAGE_SOURCE_LINK.ORDER_BY)
					.fetch();

			sourceMeaningImageSourceLinks.stream()
					.map(MeaningImageSourceLinkRecord::copy)
					.forEach(targetMeaningImageSourceLink -> {
						targetMeaningImageSourceLink.setMeaningImageId(targetMeaningImageId);
						targetMeaningImageSourceLink.changed(MEANING_IMAGE_SOURCE_LINK.ORDER_BY, false);
						targetMeaningImageSourceLink.store();
					});
		});

	}

	public void cloneMeaningRelations(Long sourceMeaningId, Long targetMeaningId) {

		Result<MeaningRelationRecord> sourceMeaningRelations;
		sourceMeaningRelations = mainDb
				.selectFrom(MEANING_RELATION)
				.where(MEANING_RELATION.MEANING1_ID.eq(sourceMeaningId))
				.orderBy(MEANING_RELATION.ORDER_BY)
				.fetch();
		sourceMeaningRelations.stream()
				.map(MeaningRelationRecord::copy)
				.forEach(targetMeaningRelation -> {
					targetMeaningRelation.setMeaning1Id(targetMeaningId);
					targetMeaningRelation.changed(MEANING_RELATION.ORDER_BY, false);
					targetMeaningRelation.store();
				});

		sourceMeaningRelations = mainDb
				.selectFrom(MEANING_RELATION)
				.where(MEANING_RELATION.MEANING2_ID.eq(sourceMeaningId))
				.orderBy(MEANING_RELATION.ORDER_BY)
				.fetch();
		sourceMeaningRelations.stream()
				.map(MeaningRelationRecord::copy)
				.forEach(targetMeaningRelation -> {
					targetMeaningRelation.setMeaning2Id(targetMeaningId);
					targetMeaningRelation.changed(MEANING_RELATION.ORDER_BY, false);
					targetMeaningRelation.store();
				});
	}

	public void cloneMeaningFreeforms(Long sourceMeaningId, Long targetMeaningId, boolean isPublicDataOnly) {

		Condition where = MEANING_FREEFORM.MEANING_ID.eq(sourceMeaningId)
				.and(MEANING_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID));
		if (isPublicDataOnly) {
			where = where.and(FREEFORM.IS_PUBLIC.isTrue());
		}

		Result<FreeformRecord> sourceFreeforms = mainDb
				.select(FREEFORM.fields())
				.from(FREEFORM, MEANING_FREEFORM)
				.where(where)
				.orderBy(MEANING_FREEFORM.ID)
				.fetchInto(FREEFORM);

		sourceFreeforms.forEach(sourceFreeform -> {

			FreeformRecord targetFreeform = sourceFreeform.copy();
			targetFreeform.setParentId(null);
			targetFreeform.store();
			Long targetFreeformId = targetFreeform.getId();
			Long sourceFreeformId = sourceFreeform.getId();

			MeaningFreeformRecord targetMeaningFreeform = mainDb.newRecord(MEANING_FREEFORM);
			targetMeaningFreeform.setMeaningId(targetMeaningId);
			targetMeaningFreeform.setFreeformId(targetFreeformId);
			targetMeaningFreeform.store();

			cloneFreeformChildren(sourceFreeformId, targetFreeformId);
			cloneFreeformSourceLinks(sourceFreeformId, targetFreeformId);
		});
	}

	public void cloneMeaningDefinitions(Long sourceMeaningId, Long targetMeaningId, boolean isPublicDataOnly) {

		Condition where = DEFINITION.MEANING_ID.eq(sourceMeaningId);
		if (isPublicDataOnly) {
			where = where.and(DEFINITION.IS_PUBLIC.isTrue());
		}
		Result<DefinitionRecord> sourceDefinitions = mainDb
				.selectFrom(DEFINITION)
				.where(where)
				.orderBy(DEFINITION.ORDER_BY)
				.fetch();

		sourceDefinitions.forEach(sourceDefinition -> {

			DefinitionRecord targetDefinition = sourceDefinition.copy();
			targetDefinition.setMeaningId(targetMeaningId);
			targetDefinition.changed(DEFINITION.ORDER_BY, false);
			targetDefinition.store();
			Long targetDefinitionId = targetDefinition.getId();
			Long sourceDefinitionId = sourceDefinition.getId();

			cloneDefinitionDatasets(sourceDefinitionId, targetDefinitionId);
			cloneDefinitionNotes(sourceDefinitionId, targetDefinitionId);
			cloneDefinitionFreeforms(sourceDefinitionId, targetDefinitionId, isPublicDataOnly);
			cloneDefinitionSourceLinks(sourceDefinitionId, targetDefinitionId);
		});
	}

	private void cloneDefinitionDatasets(Long sourceDefinitionId, Long targetDefinintionId) {

		Result<DefinitionDatasetRecord> sourceDefinitionDatasets = mainDb
				.selectFrom(DEFINITION_DATASET)
				.where(DEFINITION_DATASET.DEFINITION_ID.eq(sourceDefinitionId))
				.fetch();
		sourceDefinitionDatasets.forEach(sourceDefinitionDataset -> {
			DefinitionDatasetRecord targetDefinitionDataset = sourceDefinitionDataset.copy();
			targetDefinitionDataset.setDefinitionId(targetDefinintionId);
			targetDefinitionDataset.setDatasetCode(sourceDefinitionDataset.getDatasetCode());
			targetDefinitionDataset.store();
		});
	}

	private void cloneDefinitionNotes(Long sourceDefinitionId, Long targetDefinintionId) {

		Result<DefinitionNoteRecord> sourceDefinitionNotes = mainDb
				.selectFrom(DEFINITION_NOTE)
				.where(DEFINITION_NOTE.DEFINITION_ID.eq(sourceDefinitionId))
				.orderBy(DEFINITION_NOTE.ORDER_BY)
				.fetch();

		sourceDefinitionNotes.forEach(sourceDefinitionNote -> {

			DefinitionNoteRecord targetDefinitionNote = sourceDefinitionNote.copy();
			targetDefinitionNote.setDefinitionId(targetDefinintionId);
			targetDefinitionNote.changed(DEFINITION_NOTE.ORDER_BY, false);
			targetDefinitionNote.store();
			Long targetDefinitionNoteId = targetDefinitionNote.getId();
			Long sourceDefinitionNoteId = sourceDefinitionNote.getId();

			Result<DefinitionNoteSourceLinkRecord> sourceDefinitionNoteSourceLinks = mainDb
					.selectFrom(DEFINITION_NOTE_SOURCE_LINK)
					.where(DEFINITION_NOTE_SOURCE_LINK.DEFINITION_NOTE_ID.eq(sourceDefinitionNoteId))
					.orderBy(DEFINITION_NOTE_SOURCE_LINK.ORDER_BY)
					.fetch();

			sourceDefinitionNoteSourceLinks.stream()
					.map(DefinitionNoteSourceLinkRecord::copy)
					.forEach(targetDefinitionNoteSourceLink -> {
						targetDefinitionNoteSourceLink.setDefinitionNoteId(targetDefinitionNoteId);
						targetDefinitionNoteSourceLink.changed(DEFINITION_NOTE_SOURCE_LINK.ORDER_BY, false);
						targetDefinitionNoteSourceLink.store();
					});
		});
	}

	private void cloneDefinitionFreeforms(Long sourceDefinitionId, Long targetDefinintionId, boolean isPublicDataOnly) {

		Condition where = DEFINITION_FREEFORM.DEFINITION_ID.eq(sourceDefinitionId)
				.and(DEFINITION_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID));
		if (isPublicDataOnly) {
			where = where.and(FREEFORM.IS_PUBLIC.isTrue());
		}

		Result<FreeformRecord> sourceFreeforms = mainDb
				.select(FREEFORM.fields())
				.from(FREEFORM, DEFINITION_FREEFORM)
				.where(where)
				.orderBy(DEFINITION_FREEFORM.ID)
				.fetchInto(FREEFORM);

		sourceFreeforms.forEach(sourceFreeform -> {

			FreeformRecord targetFreeform = sourceFreeform.copy();
			targetFreeform.setParentId(null);
			targetFreeform.store();
			Long targetFreeformId = targetFreeform.getId();
			Long sourceFreeformId = sourceFreeform.getId();

			DefinitionFreeformRecord targetDefinitionFreeform = mainDb.newRecord(DEFINITION_FREEFORM);
			targetDefinitionFreeform.setDefinitionId(targetDefinintionId);
			targetDefinitionFreeform.setFreeformId(targetFreeformId);
			targetDefinitionFreeform.store();

			cloneFreeformChildren(sourceFreeformId, targetFreeformId);
			cloneFreeformSourceLinks(sourceFreeformId, targetFreeformId);
		});
	}

	private void cloneDefinitionSourceLinks(Long sourceDefinitionId, Long targetDefinintionId) {

		Result<DefinitionSourceLinkRecord> definitionSourceLinks = mainDb
				.selectFrom(DEFINITION_SOURCE_LINK)
				.where(DEFINITION_SOURCE_LINK.DEFINITION_ID.eq(sourceDefinitionId))
				.orderBy(DEFINITION_SOURCE_LINK.ORDER_BY)
				.fetch();
		definitionSourceLinks.stream()
				.map(DefinitionSourceLinkRecord::copy)
				.forEach(targetDefinitionSourceLink -> {
					targetDefinitionSourceLink.setDefinitionId(targetDefinintionId);
					targetDefinitionSourceLink.changed(DEFINITION_SOURCE_LINK.ORDER_BY, false);
					targetDefinitionSourceLink.store();
				});
	}

	private void cloneFreeformChildren(Long sourceFreeformId, Long targetFreeformId) {

		Result<FreeformRecord> sourceChildren = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.PARENT_ID.eq(sourceFreeformId))
				.orderBy(FREEFORM.ORDER_BY)
				.fetch();
		sourceChildren.stream()
				.map(FreeformRecord::copy)
				.forEach(targetChild -> {
					targetChild.setParentId(targetFreeformId);
					targetChild.changed(FREEFORM.ORDER_BY, false);
					targetChild.store();
				});
	}

	private void cloneFreeformSourceLinks(Long sourceFreeformId, Long targetFreeformId) {

		Result<FreeformSourceLinkRecord> sourceFreeformSourceLinks = mainDb
				.selectFrom(FREEFORM_SOURCE_LINK)
				.where(FREEFORM_SOURCE_LINK.FREEFORM_ID.eq(sourceFreeformId))
				.orderBy(FREEFORM_SOURCE_LINK.ORDER_BY)
				.fetch();
		sourceFreeformSourceLinks.stream()
				.map(FreeformSourceLinkRecord::copy)
				.forEach(targetFreeformSourceLink -> {
					targetFreeformSourceLink.setFreeformId(targetFreeformId);
					targetFreeformSourceLink.changed(FREEFORM_SOURCE_LINK.ORDER_BY, false);
					targetFreeformSourceLink.store();
				});
	}

	public Long cloneWord(SimpleWord simpleWord) {

		Long wordId = simpleWord.getWordId();
		String wordValue = simpleWord.getWordValue();
		String lang = simpleWord.getLang();

		Integer currentHomonymNumber = mainDb
				.select(DSL.max(WORD.HOMONYM_NR))
				.from(WORD)
				.where(
						WORD.LANG.eq(lang)
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchOneInto(Integer.class);

		int homonymNumber = currentHomonymNumber + 1;

		WordRecord word = mainDb.selectFrom(WORD).where(WORD.ID.eq(wordId)).fetchOne();
		WordRecord clonedWord = word.copy();
		clonedWord.setHomonymNr(homonymNumber);
		clonedWord.store();
		return clonedWord.getId();
	}

	public void cloneWordParadigmsAndForms(Long sourceWordId, Long targetWordId) {

		Result<ParadigmRecord> sourceParadigms = mainDb
				.selectFrom(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(sourceWordId))
				.fetch();

		sourceParadigms.forEach(sourceParadigm -> {

			ParadigmRecord targetParadigm = sourceParadigm.copy();
			targetParadigm.setWordId(targetWordId);
			targetParadigm.store();

			Long sourceParadigmId = sourceParadigm.getId();
			Long targetParadigmId = targetParadigm.getId();

			Result<ParadigmFormRecord> sourceParadigmForms = mainDb
					.selectFrom(PARADIGM_FORM)
					.where(PARADIGM_FORM.PARADIGM_ID.eq(sourceParadigmId))
					.orderBy(PARADIGM_FORM.ORDER_BY)
					.fetch();

			sourceParadigmForms.stream()
					.map(ParadigmFormRecord::copy)
					.forEach(targetParadigmForm -> {
						targetParadigmForm.setParadigmId(targetParadigmId);
						targetParadigmForm.changed(PARADIGM_FORM.ORDER_BY, false);
						targetParadigmForm.store();
					});
		});
	}

	public void cloneWordTypes(Long sourceWordId, Long targetWordId) {

		Result<WordWordTypeRecord> sourceWordWordTypes = mainDb
				.selectFrom(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(sourceWordId))
				.orderBy(WORD_WORD_TYPE.ORDER_BY)
				.fetch();
		sourceWordWordTypes.stream()
				.map(WordWordTypeRecord::copy)
				.forEach(targetWordWordType -> {
					targetWordWordType.setWordId(targetWordId);
					targetWordWordType.changed(WORD_WORD_TYPE.ORDER_BY, false);
					targetWordWordType.store();
				});
	}

	public void cloneWordTags(Long sourceWordId, Long targetWordId) {

		Result<WordTagRecord> sourceWordTags = mainDb
				.selectFrom(WORD_TAG)
				.where(WORD_TAG.WORD_ID.eq(sourceWordId))
				.orderBy(WORD_TAG.ID)
				.fetch();
		sourceWordTags.stream()
				.map(WordTagRecord::copy)
				.forEach(targetWordTag -> {
					targetWordTag.setWordId(targetWordId);
					targetWordTag.store();
				});
	}

	public void cloneWordForums(Long sourceWordId, Long targetWordId) {

		Result<WordForumRecord> sourceWordForums = mainDb
				.selectFrom(WORD_FORUM)
				.where(WORD_FORUM.WORD_ID.eq(sourceWordId))
				.orderBy(WORD_FORUM.ID)
				.fetch();
		sourceWordForums.stream()
				.map(WordForumRecord::copy)
				.forEach(targetWordForum -> {
					targetWordForum.setWordId(targetWordId);
					targetWordForum.store();
				});
	}

	public void cloneWordRelations(Long sourceWordId, Long targetWordId) {

		Result<WordRelationRecord> sourceWordRelations = mainDb
				.selectFrom(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(sourceWordId)
								.or(WORD_RELATION.WORD2_ID.eq(sourceWordId)))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetch();
		sourceWordRelations.stream()
				.map(WordRelationRecord::copy)
				.forEach(targetWordRelation -> {
					if (targetWordRelation.getWord1Id().equals(sourceWordId)) {
						targetWordRelation.setWord1Id(targetWordId);
					} else {
						targetWordRelation.setWord2Id(targetWordId);
					}
					targetWordRelation.changed(WORD_RELATION.ORDER_BY, false);
					targetWordRelation.store();
				});
	}

	public void cloneWordFreeforms(Long sourceWordId, Long targetWordId) {

		Result<FreeformRecord> sourceFreeforms = mainDb
				.select(FREEFORM.fields())
				.from(FREEFORM, WORD_FREEFORM)
				.where(WORD_FREEFORM.WORD_ID.eq(sourceWordId)
						.and(WORD_FREEFORM.FREEFORM_ID.eq(FREEFORM.ID)))
				.orderBy(WORD_FREEFORM.ORDER_BY)
				.fetchInto(FREEFORM);

		sourceFreeforms.forEach(sourceFreeform -> {

			FreeformRecord targetFreeform = sourceFreeform.copy();
			targetFreeform.setParentId(null);
			targetFreeform.store();
			Long targetFreeformId = targetFreeform.getId();
			Long sourceFreeformId = sourceFreeform.getId();

			WordFreeformRecord targetWordFreeform = mainDb.newRecord(WORD_FREEFORM);
			targetWordFreeform.setWordId(targetWordId);
			targetWordFreeform.setFreeformId(targetFreeformId);
			targetWordFreeform.changed(WORD_FREEFORM.ORDER_BY, false);
			targetWordFreeform.store();

			cloneFreeformChildren(sourceFreeformId, targetFreeformId);
			cloneFreeformSourceLinks(sourceFreeformId, targetFreeformId);
		});
	}

	public void cloneWordGroupMembers(Long sourceWordId, Long targetWordId) {

		Result<WordGroupMemberRecord> sourceWordGroupMembers = mainDb
				.selectFrom(WORD_GROUP_MEMBER)
				.where(WORD_GROUP_MEMBER.WORD_ID.eq(sourceWordId))
				.orderBy(WORD_GROUP_MEMBER.ORDER_BY)
				.fetch();
		sourceWordGroupMembers.stream()
				.map(WordGroupMemberRecord::copy)
				.forEach(targetWordGroupMember -> {
					targetWordGroupMember.setWordId(targetWordId);
					targetWordGroupMember.changed(WORD_GROUP_MEMBER.ORDER_BY, false);
					targetWordGroupMember.store();
				});
	}

	public void cloneWordEtymology(Long sourceWordId, Long targetWordId) {

		Result<WordEtymologyRecord> sourceWordEtyms = mainDb
				.selectFrom(WORD_ETYMOLOGY)
				.where(WORD_ETYMOLOGY.WORD_ID.eq(sourceWordId))
				.orderBy(WORD_ETYMOLOGY.ORDER_BY)
				.fetch();

		sourceWordEtyms.forEach(sourceWordEtym -> {

			WordEtymologyRecord targetWordEtym = sourceWordEtym.copy();
			targetWordEtym.setWordId(targetWordId);
			targetWordEtym.changed(WORD_ETYMOLOGY.ORDER_BY, false);
			targetWordEtym.store();

			Long sourceWordEtymId = sourceWordEtym.getId();
			Long targetWordEtymId = targetWordEtym.getId();

			Result<WordEtymologyRelationRecord> sourceWordEtymRelations = mainDb
					.selectFrom(WORD_ETYMOLOGY_RELATION)
					.where(WORD_ETYMOLOGY_RELATION.WORD_ETYM_ID.eq(sourceWordEtymId))
					.orderBy(WORD_ETYMOLOGY_RELATION.ORDER_BY)
					.fetch();
			sourceWordEtymRelations.stream()
					.map(WordEtymologyRelationRecord::copy)
					.forEach(targetWordEtymRelation -> {
						targetWordEtymRelation.setWordEtymId(targetWordEtymId);
						targetWordEtymRelation.changed(WORD_ETYMOLOGY_RELATION.ORDER_BY, false);
						targetWordEtymRelation.store();
					});

			Result<WordEtymologySourceLinkRecord> sourceWordEtymSourceLinks = mainDb
					.selectFrom(WORD_ETYMOLOGY_SOURCE_LINK)
					.where(WORD_ETYMOLOGY_SOURCE_LINK.WORD_ETYM_ID.eq(sourceWordEtymId))
					.orderBy(WORD_ETYMOLOGY_SOURCE_LINK.ORDER_BY)
					.fetch();
			sourceWordEtymSourceLinks.stream()
					.map(WordEtymologySourceLinkRecord::copy)
					.forEach(targetWordEtymSourceLink -> {
						targetWordEtymSourceLink.setWordEtymId(targetWordEtymId);
						targetWordEtymSourceLink.changed(WORD_ETYMOLOGY_SOURCE_LINK.ORDER_BY, false);
						targetWordEtymSourceLink.store();
					});
		});
	}

	private void mergeWordFields(Long targetWordId, Long sourceWordId) {

		Word w = WORD.as("w");
		WordRecord targetWord = mainDb.selectFrom(w).where(w.ID.eq(targetWordId)).fetchOne();
		WordRecord sourceWord = mainDb.selectFrom(w).where(w.ID.eq(sourceWordId)).fetchOne();

		String targetWordAspectCode = targetWord.getAspectCode();
		String targetWordDisplayMorphCode = targetWord.getDisplayMorphCode();
		String targetWordVocalForm = targetWord.getVocalForm();
		String targetWordMorphophonoForm = targetWord.getMorphophonoForm();
		String targetWordGenderCode = targetWord.getGenderCode();

		String sourceWordAspectCode = sourceWord.getAspectCode();
		String sourceWordDisplayMorphCode = sourceWord.getDisplayMorphCode();
		String sourceWordVocalForm = sourceWord.getVocalForm();
		String sourceWordMorphophonoForm = sourceWord.getMorphophonoForm();
		String sourceWordGenderCode = sourceWord.getGenderCode();

		boolean isUpdate = false;
		if (StringUtils.isBlank(targetWordAspectCode) && StringUtils.isNotBlank(sourceWordAspectCode)) {
			targetWord.setAspectCode(sourceWordAspectCode);
			isUpdate = true;
		}
		if (StringUtils.isBlank(targetWordDisplayMorphCode) && StringUtils.isNotBlank(sourceWordDisplayMorphCode)) {
			targetWord.setDisplayMorphCode(sourceWordDisplayMorphCode);
			isUpdate = true;
		}
		if (StringUtils.isBlank(targetWordVocalForm) && StringUtils.isNotBlank(sourceWordVocalForm)) {
			targetWord.setVocalForm(sourceWordVocalForm);
			isUpdate = true;
		}
		if (StringUtils.isBlank(targetWordMorphophonoForm) && StringUtils.isNotBlank(sourceWordMorphophonoForm)) {
			targetWord.setMorphophonoForm(sourceWordMorphophonoForm);
			isUpdate = true;
		}
		if (StringUtils.isBlank(targetWordGenderCode) && StringUtils.isNotBlank(sourceWordGenderCode)) {
			targetWord.setGenderCode(sourceWordGenderCode);
			isUpdate = true;
		}

		if (isUpdate) {
			targetWord.store();
		}
	}

	private void moveWordActivityLogs(Long targetWordId, Long sourceWordId) {

		WordActivityLog wals = WORD_ACTIVITY_LOG.as("wals");
		WordActivityLog walt = WORD_ACTIVITY_LOG.as("walt");

		mainDb
				.update(wals)
				.set(wals.WORD_ID, targetWordId)
				.where(wals.WORD_ID.eq(sourceWordId)
						.andNotExists(DSL
								.select(walt.ID)
								.from(walt)
								.where(
										walt.WORD_ID.eq(targetWordId)
												.and(walt.ACTIVITY_LOG_ID.eq(wals.ACTIVITY_LOG_ID)))))
				.execute();
	}

	private void moveWordEtymologyAndEtymologyRelations(Long targetWordId, Long sourceWordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologyRelation wer1 = WORD_ETYMOLOGY_RELATION.as("wer1");
		WordEtymologyRelation wer2 = WORD_ETYMOLOGY_RELATION.as("wer2");

		mainDb
				.update(we)
				.set(we.WORD_ID, targetWordId)
				.where(we.WORD_ID.eq(sourceWordId))
				.execute();

		mainDb
				.update(wer1)
				.set(wer1.RELATED_WORD_ID, targetWordId)
				.where(wer1.RELATED_WORD_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wer2.ID)
						.from(wer2)
						.where(
								wer2.RELATED_WORD_ID.eq(targetWordId)
										.and(wer2.WORD_ETYM_ID.eq(wer1.WORD_ETYM_ID))))
				.execute();
	}

	private void moveWordGroupMembers(Long targetWordId, Long sourceWordId) {

		WordGroupMember wgm1 = WORD_GROUP_MEMBER.as("wgm1");
		WordGroupMember wgm2 = WORD_GROUP_MEMBER.as("wgm2");

		mainDb
				.update(wgm1)
				.set(wgm1.WORD_ID, targetWordId)
				.where(wgm1.WORD_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wgm2.ID)
						.from(wgm2)
						.where(
								wgm2.WORD_ID.eq(targetWordId)
										.and(wgm2.WORD_GROUP_ID.eq(wgm1.WORD_GROUP_ID))))
				.execute();
	}

	private void moveWordFreeforms(Long targetWordId, Long sourceWordId) {

		Result<FreeformRecord> wordFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(WORD_FREEFORM.FREEFORM_ID).from(WORD_FREEFORM).where(WORD_FREEFORM.WORD_ID.eq(targetWordId))))
				.fetch();
		Result<FreeformRecord> sourceWordFreeforms = mainDb
				.selectFrom(FREEFORM)
				.where(FREEFORM.ID.in(DSL.select(WORD_FREEFORM.FREEFORM_ID).from(WORD_FREEFORM).where(WORD_FREEFORM.WORD_ID.eq(sourceWordId))))
				.fetch();

		List<Long> nonDublicateFreeformIds = getNonDuplicateFreeformIds(wordFreeforms, sourceWordFreeforms);

		mainDb
				.update(WORD_FREEFORM)
				.set(WORD_FREEFORM.WORD_ID, targetWordId)
				.where(WORD_FREEFORM.WORD_ID.eq(sourceWordId)
						.and(WORD_FREEFORM.FREEFORM_ID.in(nonDublicateFreeformIds)))
				.execute();

		mainDb
				.delete(FREEFORM)
				.where(FREEFORM.ID.in(DSL
						.select(WORD_FREEFORM.FREEFORM_ID)
						.from(WORD_FREEFORM)
						.where(WORD_FREEFORM.WORD_ID.eq(sourceWordId))))
				.execute();

		mainDb
				.delete(WORD_FREEFORM)
				.where(WORD_FREEFORM.WORD_ID.eq(sourceWordId))
				.execute();
	}

	private void moveWordRelations(Long targetWordId, Long sourceWordId) {

		WordRelation wr1 = WORD_RELATION.as("wr1");
		WordRelation wr2 = WORD_RELATION.as("wr2");

		mainDb
				.update(wr1)
				.set(wr1.WORD1_ID, targetWordId)
				.where(
						wr1.WORD1_ID.eq(sourceWordId)
								.and(wr1.WORD2_ID.ne(targetWordId)))
				.andNotExists(DSL
						.select(wr2.ID)
						.from(wr2)
						.where(
								wr2.WORD1_ID.eq(targetWordId)
										.and(wr2.WORD2_ID.eq(wr1.WORD2_ID))
										.and(wr2.WORD_REL_TYPE_CODE.eq(wr1.WORD_REL_TYPE_CODE))))
				.execute();

		mainDb
				.update(wr1)
				.set(wr1.WORD2_ID, targetWordId)
				.where(
						wr1.WORD2_ID.eq(sourceWordId)
								.and(wr1.WORD1_ID.ne(targetWordId)))
				.andNotExists(DSL
						.select(wr2.ID)
						.from(wr2)
						.where(
								wr2.WORD2_ID.eq(targetWordId)
										.and(wr2.WORD1_ID.eq(wr1.WORD1_ID))
										.and(wr2.WORD_REL_TYPE_CODE.eq(wr1.WORD_REL_TYPE_CODE))))
				.execute();
	}

	private void moveWordTypeCodes(Long targetWordId, Long sourceWordId) {

		WordWordType wwt1 = WORD_WORD_TYPE.as("wwt1");
		WordWordType wwt2 = WORD_WORD_TYPE.as("wwt2");

		mainDb
				.update(wwt1)
				.set(wwt1.WORD_ID, targetWordId)
				.where(wwt1.WORD_ID.eq(sourceWordId))
				.andNotExists(DSL
						.select(wwt2.ID)
						.from(wwt2)
						.where(wwt2.WORD_ID.eq(targetWordId)
								.and(wwt2.WORD_TYPE_CODE.eq(wwt1.WORD_TYPE_CODE))))
				.execute();
	}

	public Integer getWordHomonymNum(Long wordId) {

		return mainDb.select(WORD.HOMONYM_NR)
				.from(WORD)
				.where(WORD.ID.eq(wordId))
				.fetchOneInto(Integer.class);
	}

	public void moveParadigms(Long targetWordId, Long sourceWordId) {

		mainDb
				.delete(PARADIGM)
				.where(PARADIGM.WORD_ID.eq(targetWordId))
				.execute();

		mainDb
				.update(PARADIGM)
				.set(PARADIGM.WORD_ID, targetWordId)
				.where(PARADIGM.WORD_ID.eq(sourceWordId))
				.execute();
	}

	private List<Long> getNonDuplicateFreeformIds(List<FreeformRecord> targetFreeforms, List<FreeformRecord> sourceFreeforms) {

		return sourceFreeforms.stream()
				.filter(sf -> targetFreeforms.stream()
						.noneMatch(
								tf -> StringUtils.equals(tf.getFreeformTypeCode(), sf.getFreeformTypeCode()) &&
										((Objects.nonNull(tf.getValue()) && StringUtils.equals(tf.getValue(), sf.getValue())))))
				.map(FreeformRecord::getId)
				.collect(Collectors.toList());
	}

	public void updateLexemeWordIdAndLevels(Long lexemeId, Long wordId, int level1, int level2) {

		mainDb
				.update(LEXEME)
				.set(LEXEME.WORD_ID, wordId)
				.set(LEXEME.LEVEL1, level1)
				.set(LEXEME.LEVEL2, level2)
				.where(LEXEME.ID.eq(lexemeId))
				.execute();
	}

}
