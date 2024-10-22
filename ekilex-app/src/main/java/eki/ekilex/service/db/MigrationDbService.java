package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION;
import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_FREEFORM;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.main.Tables.LEXEME_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.main.Tables.LEX_COLLOC_REL_GROUP;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.SOURCE_ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_SOURCE_LINK;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.main.tables.Collocation;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.DefinitionFreeform;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.FreeformSourceLink;
import eki.ekilex.data.db.main.tables.LexColloc;
import eki.ekilex.data.db.main.tables.LexCollocPosGroup;
import eki.ekilex.data.db.main.tables.LexCollocRelGroup;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeFreeform;
import eki.ekilex.data.db.main.tables.LexemeSourceLink;
import eki.ekilex.data.db.main.tables.MeaningFreeform;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.SourceActivityLog;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordEtymologySourceLink;
import eki.ekilex.data.migra.CollocationMember;
import eki.ekilex.data.migra.MigraForm;
import eki.ekilex.data.migra.MigraSourceLink;
import eki.ekilex.data.migra.MigraWord;
import eki.ekilex.data.migra.SourceLinkOwner;

// temporary for data migration tools
@Component
public class MigrationDbService extends AbstractDataDbService {

	@Autowired
	private DSLContext mainDb;

	public boolean wordExists(String value) {

		Word w = WORD.as("w");

		return mainDb
				.fetchExists(DSL.select(w.ID).from(w).where(w.VALUE.eq(value)));
	}

	public List<MigraWord> getWords(String value, String languageCode) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Field<Long[]> lif = DSL.select(DSL.arrayAgg(l.ID)).from(l).where(l.WORD_ID.eq(w.ID)).asField();
		Field<Long[]> elif = DSL.select(DSL.arrayAgg(l.ID)).from(l).where(l.WORD_ID.eq(w.ID).and(l.DATASET_CODE.eq(DATASET_EKI))).asField();
		Field<String[]> dsf = DSL.select(DSL.arrayAggDistinct(l.DATASET_CODE)).from(l).where(l.WORD_ID.eq(w.ID)).asField();

		return mainDb
				.select(
						w.ID,
						w.VALUE,
						lif.as("lexeme_ids"),
						elif.as("eki_lexeme_ids"),
						dsf.as("dataset_codes"))
				.from(w)
				.where(w.VALUE.eq(value).and(w.LANG.eq(languageCode)))
				.fetchInto(MigraWord.class);
	}

	public void setWordIsCollocation(Long wordId) {

		mainDb
				.update(WORD)
				.set(WORD.IS_COLLOCATION, Boolean.TRUE)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

	public List<String> getCollocationValues() {

		Collocation c = COLLOCATION.as("c");

		return mainDb
				.select(c.VALUE)
				.from(c)
				.groupBy(c.VALUE)
				.fetchInto(String.class);
	}

	public List<Long> getCollocationIds() {

		Collocation c = COLLOCATION.as("c");

		return mainDb
				.select(c.ID)
				.from(c)
				.orderBy(c.ID)
				.fetchInto(Long.class);
	}

	public List<CollocationTuple> getCollocationsAndMembers(String collocationValue) {

		LexCollocPosGroup pgr = LEX_COLLOC_POS_GROUP.as("pgr");
		LexCollocRelGroup rgr = LEX_COLLOC_REL_GROUP.as("rgr");
		LexColloc lc = LEX_COLLOC.as("lc");
		Collocation c = COLLOCATION.as("c");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Table<Record4<Long, String, Long, String>> lcprg = DSL
				.select(
						pgr.LEXEME_ID,
						pgr.POS_GROUP_CODE,
						rgr.ID.as("rel_group_id"),
						rgr.NAME.as("rel_group_name"))
				.from(pgr, rgr)
				.where(rgr.POS_GROUP_ID.eq(pgr.ID))
				.asTable("lcprg");

		return mainDb
				.select(
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.USAGES.as("colloc_usages"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.COMPLEXITY,
						lcprg.field("pos_group_code"),
						lcprg.field("rel_group_name"),
						l.ID.as("colloc_member_lexeme_id"),
						w.ID.as("colloc_member_word_id"),
						w.VALUE.as("colloc_member_word_value"),
						lc.MEMBER_FORM.as("colloc_member_form_value"),
						lc.CONJUNCT.as("colloc_member_conjunct"),
						lc.WEIGHT.as("colloc_member_weight"),
						lc.GROUP_ORDER.as("colloc_group_order"),
						lc.MEMBER_ORDER.as("colloc_member_order"))
				.from(c
						.innerJoin(lc).on(lc.COLLOCATION_ID.eq(c.ID))
						.innerJoin(l).on(l.ID.eq(lc.LEXEME_ID))
						.innerJoin(w).on(w.ID.eq(l.WORD_ID))
						.leftOuterJoin(lcprg).on(
								lcprg.field("lexeme_id", Long.class).eq(l.ID)
										.and(lc.REL_GROUP_ID.eq(lcprg.field("rel_group_id", Long.class)))))
				.where(
						c.VALUE.eq(collocationValue))
				.orderBy(c.ID, lc.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	public eki.ekilex.data.Collocation getCollocation(Long id) {

		Collocation c = COLLOCATION.as("c");

		return mainDb
				.selectFrom(c)
				.where(c.ID.eq(id))
				.fetchOptionalInto(eki.ekilex.data.Collocation.class)
				.orElse(null);
	}

	public List<CollocationTuple> getCollocationAndMembers(Long collocationId) {

		LexCollocPosGroup pgr = LEX_COLLOC_POS_GROUP.as("pgr");
		LexCollocRelGroup rgr = LEX_COLLOC_REL_GROUP.as("rgr");
		LexColloc lc = LEX_COLLOC.as("lc");
		Collocation c = COLLOCATION.as("c");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		Table<Record4<Long, String, Long, String>> lcprg = DSL
				.select(
						pgr.LEXEME_ID,
						pgr.POS_GROUP_CODE,
						rgr.ID.as("rel_group_id"),
						rgr.NAME.as("rel_group_name"))
				.from(pgr, rgr)
				.where(rgr.POS_GROUP_ID.eq(pgr.ID))
				.asTable("lcprg");

		return mainDb
				.select(
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.USAGES.as("colloc_usages"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.COMPLEXITY,
						lcprg.field("pos_group_code"),
						lcprg.field("rel_group_name"),
						l.ID.as("colloc_member_lexeme_id"),
						w.ID.as("colloc_member_word_id"),
						w.VALUE.as("colloc_member_word_value"),
						lc.MEMBER_FORM.as("colloc_member_form_value"),
						lc.CONJUNCT.as("colloc_member_conjunct"),
						lc.WEIGHT.as("colloc_member_weight"),
						lc.GROUP_ORDER.as("colloc_group_order"),
						lc.MEMBER_ORDER.as("colloc_member_order"))
				.from(c
						.innerJoin(lc).on(lc.COLLOCATION_ID.eq(c.ID))
						.innerJoin(l).on(l.ID.eq(lc.LEXEME_ID))
						.innerJoin(w).on(w.ID.eq(l.WORD_ID))
						.leftOuterJoin(lcprg).on(
								lcprg.field("lexeme_id", Long.class).eq(l.ID)
										.and(lc.REL_GROUP_ID.eq(lcprg.field("rel_group_id", Long.class)))))
				.where(
						c.ID.eq(collocationId))
				.orderBy(lc.MEMBER_ORDER)
				.fetchInto(CollocationTuple.class);
	}

	public List<MigraForm> getForms(Long wordId, String formValue, String morphCode) {

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");

		Condition where = w.ID.eq(wordId)
				.and(p.WORD_ID.eq(w.ID))
				.and(pf.PARADIGM_ID.eq(p.ID))
				.and(pf.FORM_ID.eq(f.ID))
				.and(f.VALUE.eq(formValue));

		if (StringUtils.isNotBlank(morphCode)) {
			where = where.and(f.MORPH_CODE.eq(morphCode));
		}

		return mainDb
				.selectDistinct(
						pf.FORM_ID,
						f.VALUE,
						f.MORPH_CODE)
				.from(w, p, pf, f)
				.where(where)
				.fetchInto(MigraForm.class);
	}

	public Long createFormWithBlankParadigm(Long wordId, String formValue, String morphCode) {

		Long paradigmId = mainDb
				.insertInto(PARADIGM, PARADIGM.WORD_ID)
				.values(wordId)
				.returning(PARADIGM.ID)
				.fetchOne()
				.getId();

		Long formId = mainDb
				.insertInto(FORM, FORM.VALUE, FORM.VALUE_PRESE, FORM.MORPH_CODE)
				.values(formValue, formValue, morphCode)
				.returning(FORM.ID)
				.fetchOne()
				.getId();

		mainDb
				.insertInto(PARADIGM_FORM, PARADIGM_FORM.PARADIGM_ID, PARADIGM_FORM.FORM_ID)
				.values(paradigmId, formId)
				.execute();

		return formId;
	}

	public WordLexemeMeaningIdTuple createWordAndLexemeAndMeaning(
			String wordValue,
			String languageCode,
			String datasetCode,
			Complexity complexity,
			boolean isPublic) {

		int homonymNr = getWordNextHomonymNr(wordValue, languageCode);

		Long wordId = mainDb
				.insertInto(
						WORD,
						WORD.VALUE,
						WORD.VALUE_PRESE,
						WORD.HOMONYM_NR,
						WORD.LANG,
						WORD.IS_WORD,
						WORD.IS_COLLOCATION)
				.values(
						wordValue,
						wordValue,
						homonymNr,
						languageCode,
						Boolean.FALSE,
						Boolean.TRUE)
				.returning(WORD.ID)
				.fetchOne()
				.getId();

		Long meaningId = mainDb.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();

		Long lexemeId = mainDb
				.insertInto(
						LEXEME,
						LEXEME.MEANING_ID,
						LEXEME.WORD_ID,
						LEXEME.DATASET_CODE,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.IS_PUBLIC,
						LEXEME.COMPLEXITY)
				.values(
						meaningId,
						wordId,
						datasetCode,
						1,
						1,
						isPublic,
						complexity.name())
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();
		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public WordLexemeMeaningIdTuple createLexemeAndMeaning(
			Long wordId,
			String datasetCode,
			Complexity complexity,
			boolean isPublic) {

		Long meaningId = mainDb.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();

		Long lexemeId = mainDb
				.insertInto(
						LEXEME,
						LEXEME.MEANING_ID,
						LEXEME.WORD_ID,
						LEXEME.DATASET_CODE,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						LEXEME.IS_PUBLIC,
						LEXEME.COMPLEXITY)
				.values(
						meaningId,
						wordId,
						datasetCode,
						1,
						1,
						isPublic,
						complexity.name())
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();
		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public Long createCollocationMember(CollocationMember collocationMember) {

		return mainDb
				.insertInto(
						COLLOCATION_MEMBER,
						COLLOCATION_MEMBER.COLLOC_LEXEME_ID,
						COLLOCATION_MEMBER.MEMBER_LEXEME_ID,
						COLLOCATION_MEMBER.MEMBER_FORM_ID,
						COLLOCATION_MEMBER.POS_GROUP_CODE,
						COLLOCATION_MEMBER.REL_GROUP_CODE,
						COLLOCATION_MEMBER.CONJUNCT,
						COLLOCATION_MEMBER.WEIGHT,
						COLLOCATION_MEMBER.MEMBER_ORDER,
						COLLOCATION_MEMBER.GROUP_ORDER)
				.values(
						collocationMember.getCollocLexemeId(),
						collocationMember.getMemberLexemeId(),
						collocationMember.getMemberFormId(),
						collocationMember.getPosGroupCode(),
						collocationMember.getRelGroupCode(),
						collocationMember.getConjunct(),
						collocationMember.getWeight(),
						collocationMember.getMemberOrder(),
						collocationMember.getGroupOrder())
				.returning(COLLOCATION_MEMBER.ID)
				.fetchOne()
				.getId();
	}

	public List<eki.ekilex.data.Source> getSources() {

		Source s = SOURCE.as("s");
		return mainDb
				.selectFrom(s)
				.orderBy(s.ID)
				.fetchInto(eki.ekilex.data.Source.class);
	}

	@Deprecated
	public List<MigraSourceLink> getSourceLinks(Long sourceId, SourceLinkOwner sourceLinkOwner) {

		FreeformSourceLink fsl = FREEFORM_SOURCE_LINK.as("fsl");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		DefinitionDataset dds = DEFINITION_DATASET.as("dds");
		LexemeSourceLink lsl = LEXEME_SOURCE_LINK.as("lsl");
		WordEtymologySourceLink wesl = WORD_ETYMOLOGY_SOURCE_LINK.as("wesl");
		MeaningFreeform mf = MEANING_FREEFORM.as("mf");
		DefinitionFreeform df = DEFINITION_FREEFORM.as("df");
		LexemeFreeform lf = LEXEME_FREEFORM.as("lf");
		Lexeme l = LEXEME.as("l");

		if (SourceLinkOwner.MEANING_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							fsl.SOURCE_ID,
							fsl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							fsl.FREEFORM_ID.as("source_link_owner_id"),
							l.DATASET_CODE)
					.from(fsl, mf, l)
					.where(
							fsl.SOURCE_ID.eq(sourceId)
									.and(fsl.FREEFORM_ID.eq(mf.FREEFORM_ID))
									.and(mf.MEANING_ID.eq(l.MEANING_ID)))
					.orderBy(fsl.FREEFORM_ID, fsl.ID)
					.fetchInto(MigraSourceLink.class);

		} else if (SourceLinkOwner.DEFINITION_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							fsl.SOURCE_ID,
							fsl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							fsl.FREEFORM_ID.as("source_link_owner_id"),
							dds.DATASET_CODE)
					.from(fsl, df, dds)
					.where(
							fsl.SOURCE_ID.eq(sourceId)
									.and(fsl.FREEFORM_ID.eq(df.FREEFORM_ID))
									.and(df.DEFINITION_ID.eq(dds.DEFINITION_ID)))
					.orderBy(fsl.FREEFORM_ID, fsl.ID)
					.fetchInto(MigraSourceLink.class);

		} else if (SourceLinkOwner.DEFINITION_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							dsl.SOURCE_ID,
							dsl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							dsl.DEFINITION_ID.as("source_link_owner_id"),
							dds.DATASET_CODE)
					.from(dsl, dds)
					.where(
							dsl.SOURCE_ID.eq(sourceId)
									.and(dsl.DEFINITION_ID.eq(dds.DEFINITION_ID)))
					.orderBy(dsl.DEFINITION_ID, dsl.ID)
					.fetchInto(MigraSourceLink.class);

		} else if (SourceLinkOwner.LEXEME_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							fsl.SOURCE_ID,
							fsl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							fsl.FREEFORM_ID.as("source_link_owner_id"),
							l.DATASET_CODE)
					.from(fsl, lf, l)
					.where(
							fsl.SOURCE_ID.eq(sourceId)
									.and(fsl.FREEFORM_ID.eq(lf.FREEFORM_ID))
									.and(lf.LEXEME_ID.eq(l.ID)))
					.orderBy(fsl.FREEFORM_ID, fsl.ID)
					.fetchInto(MigraSourceLink.class);

		} else if (SourceLinkOwner.LEXEME_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							lsl.SOURCE_ID,
							lsl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							lsl.LEXEME_ID.as("source_link_owner_id"),
							l.DATASET_CODE)
					.from(lsl, l)
					.where(
							lsl.SOURCE_ID.eq(sourceId)
									.and(lsl.LEXEME_ID.eq(l.ID)))
					.orderBy(lsl.LEXEME_ID, lsl.ID)
					.fetchInto(MigraSourceLink.class);

		} else if (SourceLinkOwner.WORD_ETYM_SOURCE_LINK.equals(sourceLinkOwner)) {

			return mainDb
					.selectDistinct(
							wesl.SOURCE_ID,
							wesl.ID.as("source_link_id"),
							DSL.field(DSL.val(sourceLinkOwner.name())).as("source_link_owner"),
							wesl.WORD_ETYM_ID.as("source_link_owner_id"),
							DSL.field(DSL.val(DATASET_ETY)).as("dataset_code"))
					.from(wesl)
					.where(wesl.SOURCE_ID.eq(sourceId))
					.orderBy(wesl.WORD_ETYM_ID, wesl.ID)
					.fetchInto(MigraSourceLink.class);
		}
		return null;
	}

	@Deprecated
	public void relinkSourceLink(Long sourceId, MigraSourceLink sourceLink) {

		Long sourceLinkId = sourceLink.getSourceLinkId();
		SourceLinkOwner sourceLinkOwner = sourceLink.getSourceLinkOwner();

		if (SourceLinkOwner.MEANING_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(FREEFORM_SOURCE_LINK)
					.set(FREEFORM_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();

		} else if (SourceLinkOwner.DEFINITION_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(FREEFORM_SOURCE_LINK)
					.set(FREEFORM_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();

		} else if (SourceLinkOwner.DEFINITION_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(DEFINITION_SOURCE_LINK)
					.set(DEFINITION_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(DEFINITION_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();

		} else if (SourceLinkOwner.LEXEME_FREEFORM_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(FREEFORM_SOURCE_LINK)
					.set(FREEFORM_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(FREEFORM_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();

		} else if (SourceLinkOwner.LEXEME_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(LEXEME_SOURCE_LINK)
					.set(LEXEME_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(LEXEME_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();

		} else if (SourceLinkOwner.WORD_ETYM_SOURCE_LINK.equals(sourceLinkOwner)) {

			mainDb
					.update(WORD_ETYMOLOGY_SOURCE_LINK)
					.set(WORD_ETYMOLOGY_SOURCE_LINK.SOURCE_ID, sourceId)
					.where(WORD_ETYMOLOGY_SOURCE_LINK.ID.eq(sourceLinkId))
					.execute();
		}
	}

	public void duplicateActivityLog(Long sourceSourceId, Long targetSourceId) {

		SourceActivityLog sal = SOURCE_ACTIVITY_LOG.as("sal");

		mainDb
				.insertInto(
						SOURCE_ACTIVITY_LOG,
						SOURCE_ACTIVITY_LOG.ACTIVITY_LOG_ID,
						SOURCE_ACTIVITY_LOG.SOURCE_ID)
				.select(
						DSL
								.select(
										sal.ACTIVITY_LOG_ID,
										DSL.val(targetSourceId))
								.from(sal)
								.where(sal.SOURCE_ID.eq(sourceSourceId))
								.orderBy(sal.ID))
				.execute();

	}

	public void setSourceDataset(Long sourceId, String datasetCode) {

		mainDb
				.update(SOURCE)
				.set(SOURCE.DATASET_CODE, datasetCode)
				.where(SOURCE.ID.eq(sourceId))
				.execute();
	}
}
