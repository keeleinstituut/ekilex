package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record14;
import org.jooq.Record16;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.data.db.Routines;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.LexemePos;
import eki.ekilex.data.db.tables.LexemeRegister;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordRelationParam;
import eki.ekilex.data.db.tables.records.DefinitionRecord;
import eki.ekilex.data.db.tables.records.DefinitionSourceLinkRecord;
import eki.ekilex.data.db.tables.records.FreeformRecord;
import eki.ekilex.data.db.tables.records.FreeformSourceLinkRecord;
import eki.ekilex.data.db.tables.records.LexemeFreeformRecord;
import eki.ekilex.data.db.tables.records.LexemePosRecord;
import eki.ekilex.data.db.tables.records.LexemeRecord;
import eki.ekilex.data.db.tables.records.WordRecord;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelMeaningRecord;
import eki.ekilex.data.db.udt.records.TypeWordRelParamRecord;
import eki.ekilex.service.db.util.JooqBugCompensator;
import eki.ekilex.service.db.util.SearchFilterHelper;

@Component
public class SynSearchDbService extends AbstractDataDbService {

	private static final int DEFAULT_LEXEME_LEVEL = 1;

	@Autowired
	private SearchFilterHelper searchFilterHelper;

	@Autowired
	private JooqBugCompensator jooqBugCompensator;

	public List<SynRelation> getWordPartSynRelations(Long wordId, String relationType, String datasetCode, List<String> wordLangs) {

		boolean isPublicDataOnly = true;
		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		LexemePos lp = LEXEME_POS.as("lp");

		Field<TypeWordRelParamRecord[]> relpf = getWordRelationParamField(r.ID);
		Field<String[]> uf = getUsagesField(l2.ID, isPublicDataOnly);
		Field<String[]> df = getDefinitionsField(l2.MEANING_ID, isPublicDataOnly);
		Field<String[]> lrcf = getLexRegisterCodesField(l2.ID);
		Field<String[]> lpcf = getLexPosCodesField(l2.ID);

		Table<Record6<Long, Long, String[], String[], String[], String[]>> relmt = DSL
				.select(
						l2.MEANING_ID,
						l2.ID.as("lexeme_id"),
						df.as("definitions"),
						uf.as("usages"),
						lrcf.as("lex_register_codes"),
						lpcf.as("lex_pos_codes"))
				.from(l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode)))
				.groupBy(l2.ID)
				.asTable("relmt");

		Field<TypeWordRelMeaningRecord[]> relm = DSL
				.select(DSL.field("array_agg(row(relmt.meaning_id, relmt.lexeme_id, relmt.definitions, relmt.usages, relmt.lex_register_codes, relmt.lex_pos_codes)::type_word_rel_meaning)", TypeWordRelMeaningRecord[].class))
				.from(relmt)
				.asField("relm");

		Field<String[]> rwlp = DSL
				.select(DSL.arrayAggDistinct(lp.POS_CODE))
				.from(lp, l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode))
								.and(lp.LEXEME_ID.eq(l2.ID)))
				.asField();

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Table<Record16<Long, String, String, TypeWordRelParamRecord[], Long, Long, String, String, Integer, String, String[], Boolean, Boolean, Boolean, TypeWordRelMeaningRecord[], String[]>> rr = DSL
				.select(
						r.ID,
						r.RELATION_STATUS,
						oppr.RELATION_STATUS.as("opposite_relation_status"),
						relpf.as("relation_params"),
						r.ORDER_BY,
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.HOMONYM_NR.as("word_homonym_nr"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						relm.as("word_meanings"),
						rwlp.as("word_lexemes_poses"))
				.from(r
						.innerJoin(w2).on(
								r.WORD2_ID.eq(w2.ID)
										.andExists(DSL
												.select(l2.ID)
												.from(l2)
												.where(l2.WORD_ID.eq(w2.ID))))
						.leftOuterJoin(oppr).on(
								oppr.WORD1_ID.eq(r.WORD2_ID)
										.and(oppr.WORD2_ID.eq(r.WORD1_ID))
										.and(oppr.WORD_REL_TYPE_CODE.eq(r.WORD_REL_TYPE_CODE))))
				.where(
						r.WORD1_ID.eq(wordId)
								.and(r.WORD_REL_TYPE_CODE.eq(relationType))
								.and(w2.LANG.in(wordLangs))
								.andExists(DSL
										.select(l2.ID)
										.from(l2)
										.where(
												l2.WORD_ID.eq(w2.ID)
														.and(l2.DATASET_CODE.eq(datasetCode)))))
				.groupBy(r.ID, w2.ID, oppr.RELATION_STATUS)
				.asTable("r");

		Field<Boolean> rwhe = DSL
				.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
				.from(wh)
				.where(
						wh.VALUE.eq(rr.field("word_value", String.class))
								.and(wh.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID).and(lh.DATASET_CODE.eq(datasetCode)))))
				.groupBy(wh.VALUE)
				.asField();

		return create
				.select(
						rr.field("id"),
						rr.field("relation_status"),
						rr.field("opposite_relation_status"),
						rr.field("relation_params"),
						rr.field("order_by"),
						rr.field("word_id"),
						rr.field("word_value"),
						rr.field("word_value_prese"),
						rr.field("word_homonym_nr"),
						rwhe.as("homonyms_exist"),
						rr.field("word_lang"),
						rr.field("word_type_codes"),
						rr.field("prefixoid"),
						rr.field("suffixoid"),
						rr.field("foreign"),
						rr.field("word_meanings"),
						rr.field("word_lexemes_poses"))
				.from(rr)
				.orderBy(rr.field("order_by"))
				.fetch(record -> {
					SynRelation pojo = record.into(SynRelation.class);
					jooqBugCompensator.decodeWordMeaning(pojo.getWordMeanings());
					return pojo;
				});
	}

	public List<SynRelation> getWordFullSynRelations(Long wordId, String relationType, String datasetCode, String lang) {

		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");

		Field<TypeWordRelParamRecord[]> relpf = getWordRelationParamField(r.ID);
		Field<String[]> lpcf = getLexPosCodesField(l2.ID);

		Table<Record3<Long, Long, String[]>> relmt = DSL
				.select(
						l2.MEANING_ID,
						l2.ID.as("lexeme_id"),
						lpcf.as("lex_pos_codes"))
				.from(l2)
				.where(
						l2.WORD_ID.eq(r.WORD2_ID)
								.and(l2.DATASET_CODE.eq(datasetCode)))
				.groupBy(l2.ID)
				.asTable("relmt");

		Field<TypeWordRelMeaningRecord[]> relm = DSL
				.select(DSL.field("array_agg(row(relmt.meaning_id, relmt.lexeme_id, null, null, null, relmt.lex_pos_codes)::type_word_rel_meaning)", TypeWordRelMeaningRecord[].class))
				.from(relmt)
				.asField("relm");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

		Table<Record14<Long, String, String, TypeWordRelParamRecord[], Long, Long, String, String, String, String[], Boolean, Boolean, Boolean, TypeWordRelMeaningRecord[]>> rr = DSL
				.select(
						r.ID,
						r.RELATION_STATUS,
						oppr.RELATION_STATUS.as("opposite_relation_status"),
						relpf.as("relation_params"),
						r.ORDER_BY,
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						relm.as("word_meanings"))
				.from(r
						.innerJoin(w2).on(
								r.WORD2_ID.eq(w2.ID)
										.andExists(DSL
												.select(l2.ID)
												.from(l2)
												.where(l2.WORD_ID.eq(w2.ID))))
						.leftOuterJoin(oppr).on(
								oppr.WORD1_ID.eq(r.WORD2_ID)
										.and(oppr.WORD2_ID.eq(r.WORD1_ID))
										.and(oppr.WORD_REL_TYPE_CODE.eq(r.WORD_REL_TYPE_CODE))))
				.where(
						r.WORD1_ID.eq(wordId)
								.and(r.WORD_REL_TYPE_CODE.eq(relationType))
								.and(w2.LANG.eq(lang))
								.andExists(DSL
										.select(l2.ID)
										.from(l2)
										.where(
												l2.WORD_ID.eq(w2.ID)
														.and(l2.DATASET_CODE.eq(datasetCode)))))
				.groupBy(r.ID, w2.ID, oppr.RELATION_STATUS)
				.asTable("r");

		Field<Integer> rwsvpwcf = DSL
				.select(DSL.field(DSL.count(wh.ID)))
				.from(wh)
				.where(
						wh.VALUE.eq(rr.field("word_value", String.class))
								.and(wh.LANG.eq(rr.field("word_lang", String.class)))
								.and(wh.IS_PUBLIC.isTrue())
								.andExists(DSL
										.select(lh.ID)
										.from(lh)
										.where(lh.WORD_ID.eq(wh.ID))))
				.groupBy(wh.VALUE)
				.asField();

		return create
				.select(
						rr.field("id"),
						rr.field("relation_status"),
						rr.field("opposite_relation_status"),
						rr.field("relation_params"),
						rr.field("order_by"),
						rr.field("word_id"),
						rr.field("word_value"),
						rr.field("word_value_prese"),
						rwsvpwcf.as("same_value_public_word_count"),
						rr.field("word_lang"),
						rr.field("word_type_codes"),
						rr.field("prefixoid"),
						rr.field("suffixoid"),
						rr.field("foreign"),
						rr.field("word_meanings"),
						rr.field("word_lexemes_poses"))
				.from(rr)
				.orderBy(rr.field("order_by"))
				.fetchInto(SynRelation.class);
	}

	public List<WordLexeme> getWordPrimarySynonymLexemes(
			Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, String classifierLabelLang, String classifierLabelTypeCode) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");

		Condition dsWhere = searchFilterHelper.applyDatasetRestrictions(l, searchDatasetsRestriction, null);

		Field<TypeClassifierRecord[]> lposf = getLexemePosField(l.ID, classifierLabelLang, classifierLabelTypeCode);
		Field<TypeClassifierRecord[]> lregf = getLexemeRegistersField(l.ID, classifierLabelLang, classifierLabelTypeCode);

		return create
				.select(
						l.MEANING_ID,
						l.WORD_ID,
						l.ID.as("lexeme_id"),
						l.DATASET_CODE,
						l.LEVEL1,
						l.LEVEL2,
						l.WEIGHT,
						lposf.as("pos"),
						lregf.as("registers"))
				.from(l.innerJoin(ds).on(ds.CODE.eq(l.DATASET_CODE)))
				.where(l.WORD_ID.eq(wordId).and(dsWhere))
				.orderBy(ds.ORDER_BY, l.LEVEL1, l.LEVEL2)
				.fetchInto(WordLexeme.class);
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		Field<String[]> wtf = getWordTypesField(w.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w.ID);
		Field<Boolean> wtz = getWordIsForeignField(w.ID);

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.LANG,
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"))
				.from(w)
				.where(w.ID.eq(wordId)
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID))))
				.fetchOneInto(eki.ekilex.data.Word.class);
	}

	public List<eki.ekilex.data.WordRelation> getExistingFollowingRelationsForWord(Long relationId, String relTypeCode) {
		WordRelation wr2 = WORD_RELATION.as("wr2");

		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
				.from(WORD_RELATION, wr2)
				.where(
						WORD_RELATION.WORD1_ID.eq(wr2.WORD1_ID)
								.and(wr2.ID.eq(relationId))
								.and(WORD_RELATION.ORDER_BY.ge(wr2.ORDER_BY))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode)))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	public eki.ekilex.data.Word getSynCandidateWord(Long wordRelationId) {

		return create
				.select(
						WORD.ID.as("word_id"),
						WORD.VALUE.as("word_value"),
						WORD.VALUE_PRESE.as("word_value_prese"),
						WORD.LANG)
				.from(WORD_RELATION, WORD)
				.where(
						WORD_RELATION.ID.eq(wordRelationId)
								.and(WORD.ID.eq(WORD_RELATION.WORD2_ID)))
				.fetchOneInto(eki.ekilex.data.Word.class);
	}

	public Long getMeaningFirstWordLexemeId(Long meaningId, String datasetCode, String wordValue, String language) {

		return create
				.select(LEXEME.ID)
				.from(LEXEME, WORD)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.LANG.eq(language))
								.and(WORD.IS_PUBLIC.isTrue()))
				.orderBy(WORD.HOMONYM_NR)
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public BigDecimal getWordRelationParamValue(Long wordRelationId, String paramName) {

		return create
				.select(WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(
						WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId)
								.and(WORD_RELATION_PARAM.NAME.eq(paramName)))
				.fetchOneInto(BigDecimal.class);
	}

	public List<TypeWordRelParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(TypeWordRelParam.class);
	}

	public List<eki.ekilex.data.Definition> getInexactSynMeaningDefinitions(Long meaningId, String... langs) {

		Condition wherePublic =
				DEFINITION.LANG.in(langs)
						.and(DEFINITION.IS_PUBLIC.eq(PUBLICITY_PUBLIC))
						.and(DEFINITION.DEFINITION_TYPE_CODE.ne(DEFINITION_TYPE_CODE_INEXACT_SYN));

		Condition whereInexact = DEFINITION.DEFINITION_TYPE_CODE.eq(DEFINITION_TYPE_CODE_INEXACT_SYN);

		return create
				.select(
						DEFINITION.VALUE_PRESE.as("value"),
						DEFINITION.LANG,
						DEFINITION.DEFINITION_TYPE_CODE.as("type_code"))
				.from(DEFINITION)
				.where(
						DEFINITION.MEANING_ID.eq(meaningId)
								.and(DSL.or(wherePublic, whereInexact)))
				.orderBy(DEFINITION.ORDER_BY)
				.fetchInto(eki.ekilex.data.Definition.class);
	}

	public eki.ekilex.data.Definition getMeaningDefinition(Long meaningId, String definitionTypeCode) {

		return create
				.select(
						DEFINITION.ID,
						DEFINITION.VALUE_PRESE.as("value"),
						DEFINITION.LANG,
						DEFINITION.DEFINITION_TYPE_CODE.as("type_code"))
				.from(DEFINITION)
				.where(
						DEFINITION.MEANING_ID.eq(meaningId)
								.and(DEFINITION.DEFINITION_TYPE_CODE.eq(definitionTypeCode)))
				.fetchOptionalInto(eki.ekilex.data.Definition.class)
				.orElse(null);
	}

	private Field<TypeWordRelParamRecord[]> getWordRelationParamField(Field<Long> wordRelationIdField) {

		WordRelationParam rp = WORD_RELATION_PARAM.as("rp");

		Field<TypeWordRelParamRecord[]> relp = DSL.field(DSL
				.select(DSL.field("array_agg(row(rp.name, rp.value)::type_word_rel_param)", TypeWordRelParamRecord[].class))
				.from(rp)
				.where(rp.WORD_RELATION_ID.eq(wordRelationIdField))
				.groupBy(rp.WORD_RELATION_ID));
		return relp;
	}

	private Field<String[]> getUsagesField(Field<Long> lexemeIdField, boolean isPublicDataOnly) {

		Freeform u = FREEFORM.as("u");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");

		Condition where =
				lff.LEXEME_ID.eq(lexemeIdField)
						.and(lff.FREEFORM_ID.eq(u.ID))
						.and(u.TYPE.eq(FreeformType.USAGE.name()));

		if (isPublicDataOnly) {
			where = where.and(u.IS_PUBLIC.isTrue());
		}

		Field<String[]> uf = DSL.field(DSL
				.select(DSL.arrayAgg(u.VALUE_PRESE).orderBy(u.ORDER_BY))
				.from(u, lff)
				.where(where)
				.groupBy(lexemeIdField));
		return uf;
	}

	private Field<String[]> getLexRegisterCodesField(Field<Long> lexemeIdField) {

		LexemeRegister lr = LEXEME_REGISTER.as("lr");

		Field<String[]> lrcf = DSL.field(DSL
				.select(DSL.arrayAgg(lr.REGISTER_CODE).orderBy(lr.ORDER_BY))
				.from(lr)
				.where(lr.LEXEME_ID.eq(lexemeIdField))
				.groupBy(lexemeIdField));
		return lrcf;
	}

	private Field<String[]> getDefinitionsField(Field<Long> meaningIdField, boolean isPublicDataOnly) {

		Definition d = DEFINITION.as("d");

		Condition where = d.MEANING_ID.eq(meaningIdField);
		if (isPublicDataOnly) {
			where = where.and(d.IS_PUBLIC.isTrue());
		}

		Field<String[]> df = DSL.field(DSL
				.select(DSL.arrayAgg(Routines.encodeText(d.VALUE_PRESE)).orderBy(d.ORDER_BY))
				.from(d)
				.where(where)
				.groupBy(meaningIdField));
		return df;
	}

	private Field<String[]> getLexPosCodesField(Field<Long> lexemeIdField) {

		LexemePos lp = LEXEME_POS.as("lp");

		Field<String[]> lpcf = DSL.field(DSL
				.select(DSL.arrayAgg(lp.POS_CODE).orderBy(lp.ORDER_BY))
				.from(lp)
				.where(lp.LEXEME_ID.eq(lexemeIdField))
				.groupBy(lexemeIdField));
		return lpcf;
	}

	public void updateRelationStatus(Long id, String status) {
		create.update(WORD_RELATION)
				.set(WORD_RELATION.RELATION_STATUS, status)
				.where(WORD_RELATION.ID.eq(id))
				.execute();
	}

	public Long createSynWord(Long sourceWordId, int wordHomNr) {

		WordRecord word = create.selectFrom(WORD).where(WORD.ID.eq(sourceWordId)).fetchOne();
		WordRecord synWord = word.copy();
		synWord.setHomonymNr(wordHomNr);
		synWord.setIsPublic(PUBLICITY_PUBLIC);
		synWord.store();
		Long synWordId = synWord.getId();
		return synWordId;
	}

	public void createSynLexeme(Long sourceLexemeId, Long wordId, int synLexemeLevel1, Long meaningId, String datasetCode, BigDecimal weight) {

		LexemeRecord sourceLexeme = create.selectFrom(LEXEME).where(LEXEME.ID.eq(sourceLexemeId)).fetchOne();
		LexemeRecord synLexeme = sourceLexeme.copy();
		synLexeme.setMeaningId(meaningId);
		synLexeme.setWordId(wordId);
		synLexeme.setDatasetCode(datasetCode);
		synLexeme.setWeight(weight);
		synLexeme.setLevel1(synLexemeLevel1);
		synLexeme.setLevel2(DEFAULT_LEXEME_LEVEL);
		synLexeme.setIsPublic(PUBLICITY_PUBLIC);
		synLexeme.changed(LEXEME.ORDER_BY, false);
		synLexeme.store();

		Long synLexemeId = synLexeme.getId();
		cloneSynLexemeData(synLexemeId, sourceLexemeId);
	}

	public void cloneSynLexemeData(Long targetLexemeId, Long sourceLexemeId) {

		LexemePos tgtpos = LEXEME_POS.as("tgtpos");
		LexemePos srcpos = LEXEME_POS.as("srcpos");
		Freeform tgtu = FREEFORM.as("tgtu");
		Freeform srcu = FREEFORM.as("srcu");
		LexemeFreeform lexff = LEXEME_FREEFORM.as("lexff");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");

		Result<LexemePosRecord> sourceLexemePoses = create
				.selectFrom(srcpos)
				.where(
						srcpos.LEXEME_ID.eq(sourceLexemeId)
								.andNotExists(DSL
										.select(tgtpos.ID)
										.from(tgtpos)
										.where(
												tgtpos.LEXEME_ID.eq(targetLexemeId)
														.and(tgtpos.POS_CODE.eq(srcpos.POS_CODE)))))
				.orderBy(srcpos.ORDER_BY)
				.fetch();

		for (LexemePosRecord sourceLexemePos : sourceLexemePoses) {
			LexemePosRecord targetLexemePos = sourceLexemePos.copy();
			targetLexemePos.setLexemeId(targetLexemeId);
			targetLexemePos.changed(LEXEME_POS.ORDER_BY, false);
			targetLexemePos.store();
		}

		Result<FreeformRecord> sourceUsages = create
				.select(srcu.fields())
				.from(srcu, lexff)
				.where(
						lexff.LEXEME_ID.eq(sourceLexemeId)
								.and(lexff.FREEFORM_ID.eq(srcu.ID))
								.and(srcu.TYPE.eq(FreeformType.USAGE.name()))
								.andNotExists(DSL
										.select(tgtu.ID)
										.from(tgtu, lexff)
										.where(
												lexff.LEXEME_ID.eq(targetLexemeId)
														.and(lexff.FREEFORM_ID.eq(tgtu.ID))
														.and(tgtu.TYPE.eq(FreeformType.USAGE.name()))
														.and(tgtu.VALUE_TEXT.eq(srcu.VALUE_TEXT)))))
				.orderBy(srcu.ID)
				.fetchInto(FREEFORM);

		for (FreeformRecord sourceUsage : sourceUsages) {
			Long sourceUsageId = sourceUsage.getId();
			FreeformRecord targetUsage = sourceUsage.copy();
			targetUsage.setIsPublic(PUBLICITY_PUBLIC);
			targetUsage.changed(FREEFORM.ORDER_BY, false);
			targetUsage.store();
			Long targetUsageId = targetUsage.getId();

			Result<FreeformSourceLinkRecord> sourceUsageSourceLinks = create
					.selectFrom(ffsl)
					.where(ffsl.FREEFORM_ID.eq(sourceUsageId))
					.orderBy(ffsl.ORDER_BY)
					.fetch();

			for (FreeformSourceLinkRecord sourceUsageSourceLink : sourceUsageSourceLinks) {
				FreeformSourceLinkRecord targetUsageSourceLink = sourceUsageSourceLink.copy();
				targetUsageSourceLink.setFreeformId(targetUsageId);
				targetUsageSourceLink.changed(FREEFORM_SOURCE_LINK.ORDER_BY, false);
				targetUsageSourceLink.store();
			}

			LexemeFreeformRecord targetLexemeFreeform = create.newRecord(lexff);
			targetLexemeFreeform.setLexemeId(targetLexemeId);
			targetLexemeFreeform.setFreeformId(targetUsageId);
			targetLexemeFreeform.store();
		}
	}

	public void cloneSynMeaningData(Long targetMeaningId, Long sourceMeaningId, String datasetCode) {

		Definition tgtdef = DEFINITION.as("tgtdef");
		Definition srcdef = DEFINITION.as("srcdef");
		DefinitionSourceLink defsl = DEFINITION_SOURCE_LINK.as("defsl");

		Result<DefinitionRecord> sourceDefinitions = create
				.selectFrom(srcdef)
				.where(
						srcdef.MEANING_ID.eq(sourceMeaningId)
								.andNotExists(DSL
										.select(tgtdef.ID)
										.from(tgtdef)
										.where(
												tgtdef.MEANING_ID.eq(targetMeaningId)
														.and(tgtdef.VALUE.eq(srcdef.VALUE)))))
				.orderBy(srcdef.ORDER_BY)
				.fetch();

		for (DefinitionRecord sourceDefinition : sourceDefinitions) {
			Long sourceDefinitionId = sourceDefinition.getId();
			DefinitionRecord synDefinition = sourceDefinition.copy();
			synDefinition.setMeaningId(targetMeaningId);
			synDefinition.setIsPublic(PUBLICITY_PUBLIC);
			synDefinition.changed(DEFINITION.ORDER_BY, false);
			synDefinition.store();
			Long synDefinitionId = synDefinition.getId();

			create
					.insertInto(DEFINITION_DATASET, DEFINITION_DATASET.DEFINITION_ID, DEFINITION_DATASET.DATASET_CODE)
					.values(synDefinitionId, datasetCode)
					.execute();

			Result<DefinitionSourceLinkRecord> sourceDefinitionSourceLinks = create
					.selectFrom(defsl)
					.where(defsl.DEFINITION_ID.eq(sourceDefinitionId))
					.orderBy(defsl.ORDER_BY)
					.fetch();

			for (DefinitionSourceLinkRecord sourceDefinitionSourceLink : sourceDefinitionSourceLinks) {
				DefinitionSourceLinkRecord synDefinitionSourceLink = sourceDefinitionSourceLink.copy();
				synDefinitionSourceLink.setDefinitionId(synDefinitionId);
				synDefinitionSourceLink.changed(DEFINITION_SOURCE_LINK.ORDER_BY, false);
				synDefinitionSourceLink.store();
			}
		}
	}
}
