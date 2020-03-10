package eki.ekilex.service;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LAYER_STATE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.LayerName;
import eki.common.constant.LexemeType;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.TypeWordRelParam;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordRelationParam;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.udt.records.TypeWordRelParamRecord;
import eki.ekilex.service.db.AbstractSearchDbService;

@Component
public class SynSearchDbService extends AbstractSearchDbService {

	private DSLContext create;

	public SynSearchDbService(DSLContext context) {
		create = context;
	}

	public List<eki.ekilex.data.Word> getWords(String wordWithMetaCharacters, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName, boolean fetchAll, int offset) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Condition where = createSearchCondition(word, paradigm, wordWithMetaCharacters, searchDatasetsRestriction);

		return execute(word, paradigm, where, layerName, searchDatasetsRestriction, fetchAll, offset, create);
	}

	public List<eki.ekilex.data.Word> getWords(SearchFilter searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName, boolean fetchAll, int offset)
			throws Exception {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();
		Word w1 = WORD.as("w1");
		Paradigm p = PARADIGM.as("p");
		Condition wordCondition = createSearchCondition(w1, searchCriteriaGroups, searchDatasetsRestriction);

		return execute(w1, p, wordCondition, layerName, searchDatasetsRestriction, fetchAll, offset, create);
	}
	
	public List<SynRelation> getWordSynRelations(Long wordId, String relationType, String datasetCode, List<String> wordLangs) {

		WordRelation r = WORD_RELATION.as("r");
		WordRelation oppr = WORD_RELATION.as("oppr");
		WordRelationParam rp = WORD_RELATION_PARAM.as("rp");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Paradigm p2 = PARADIGM.as("p2");
		Paradigm ph = PARADIGM.as("ph");
		Form f2 = FORM.as("f2");
		Form fh = FORM.as("fh");
		Lexeme l = LEXEME.as("l");
		Lexeme lh = LEXEME.as("lh");
		Definition d = DEFINITION.as("d");
		WordWordType wt = WORD_WORD_TYPE.as("wt");

		return create.selectDistinct(
				r.ID,
				r.RELATION_STATUS,
				r.ORDER_BY,
				oppr.RELATION_STATUS.as("opposite_relation_status"),
				w2.ID.as("related_word_id"),
				f2.VALUE.as("related_word"),
				w2.HOMONYM_NR.as("related_word_homonym_nr"),
				w2.LANG.as("related_word_lang"),
				DSL.field(DSL.select(DSL.field("array_agg(row(rp.name, rp.value)::type_word_rel_param)", TypeWordRelParamRecord[].class))
						.from(rp)
						.where(rp.WORD_RELATION_ID.eq(r.ID))
						.groupBy(rp.WORD_RELATION_ID)).as("relation_params"),
				DSL.field(DSL.select(DSL.arrayAgg(d.VALUE).orderBy(l.ORDER_BY, d.ORDER_BY))
						.from(l, d)
						.where(
								l.WORD_ID.eq(r.WORD2_ID)
										.and(l.DATASET_CODE.eq(datasetCode))
										.and(l.TYPE.eq(LexemeType.PRIMARY.name()))
										.and(l.MEANING_ID.eq(d.MEANING_ID))
										.and(DSL.or(d.COMPLEXITY.like("DETAIL%"), d.COMPLEXITY.like("SIMPLE%"))))
						.groupBy(l.WORD_ID)).as("related_word_definitions"),
				DSL.field(DSL.exists(
						DSL.select(DSL.arrayAgg(wt.WORD_TYPE_CODE))
								.from(wt)
								.where(wt.WORD_ID.eq(r.WORD2_ID)
										.and(wt.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_PREFIXOID)))
								.groupBy(wt.WORD_ID)))
						.as("related_word_is_prefixoid"),
				DSL.field(DSL.exists(
						DSL.select(DSL.arrayAgg(wt.WORD_TYPE_CODE))
								.from(wt)
								.where(wt.WORD_ID.eq(r.WORD2_ID)
										.and(wt.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_SUFFIXOID)))
								.groupBy(wt.WORD_ID)))
						.as("related_word_is_suffixoid"),
				DSL.field(DSL.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
						.from(fh, ph, wh)
						.where(
								fh.VALUE.eq(f2.VALUE)
										.and(fh.MODE.eq(FormMode.WORD.name()))
										.and(fh.PARADIGM_ID.eq(ph.ID))
										.and(ph.WORD_ID.eq(wh.ID))
										.andExists(DSL
												.select(lh.ID)
												.from(lh)
												.where(
														lh.WORD_ID.eq(wh.ID)
																.and(lh.DATASET_CODE.eq(datasetCode)))))
						.groupBy(fh.VALUE)).as("related_word_homonyms_exist"))
				.from(r
						.leftOuterJoin(oppr).on(
								oppr.WORD1_ID.eq(r.WORD2_ID)
										.and(oppr.WORD2_ID.eq(r.WORD1_ID))
										.and(oppr.WORD_REL_TYPE_CODE.eq(r.WORD_REL_TYPE_CODE)))
						.innerJoin(w2).on(r.WORD2_ID.eq(w2.ID))
						.innerJoin(p2).on(p2.WORD_ID.eq(w2.ID))
						.innerJoin(f2).on(f2.PARADIGM_ID.eq(p2.ID).and(f2.MODE.eq(FormMode.WORD.name()))))
				.where(
						r.WORD1_ID.eq(wordId)
						.and(r.WORD_REL_TYPE_CODE.eq(relationType))
						.and(w2.LANG.in(wordLangs)))
				.orderBy(r.ORDER_BY)
				.fetchInto(SynRelation.class);
	}

	public List<WordSynLexeme> getWordPrimarySynonymLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction, LayerName layerName) {

		Condition dsWhere = composeLexemeDatasetsCondition(LEXEME, searchDatasetsRestriction);

		return create.select(
				LEXEME.MEANING_ID,
				LEXEME.WORD_ID,
				LEXEME.ID.as("lexeme_id"),
				LEXEME.TYPE,
				LEXEME.DATASET_CODE,
				LEXEME.LEVEL1,
				LEXEME.LEVEL2,
				LEXEME.WEIGHT,
				LAYER_STATE.PROCESS_STATE_CODE.as("layer_process_state_code"))
				.from(LEXEME
						.innerJoin(DATASET).on(DATASET.CODE.eq(LEXEME.DATASET_CODE))
						.leftOuterJoin(LAYER_STATE).on(LAYER_STATE.LEXEME_ID.eq(LEXEME.ID).and(LAYER_STATE.LAYER_NAME.eq(layerName.name())))
						)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.TYPE.eq(LexemeType.PRIMARY.name()))
								.and(dsWhere))
				.orderBy(DATASET.ORDER_BY, LEXEME.LEVEL1, LEXEME.LEVEL2)
				.fetchInto(WordSynLexeme.class);
	}

	public void changeRelationStatus(Long id, String status) {
		create.update(WORD_RELATION)
				.set(WORD_RELATION.RELATION_STATUS, status)
				.where(WORD_RELATION.ID.eq(id))
				.execute();
	}

	public Long getRelationId(Long word1Id, Long word2Id, String relationType) {
		Record1<Long> relationRecord = create.select(WORD_RELATION.ID)
				.from(WORD_RELATION)
				.where(WORD_RELATION.WORD1_ID.eq(word1Id)
								.and(WORD_RELATION.WORD2_ID.eq(word2Id))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType))
						)
				.fetchOne();

		return relationRecord != null ? relationRecord.get(WORD_RELATION.ID) : null;

	}

	public Long createLexeme(Long wordId, Long meaningId, String datasetCode, LexemeType lexemeType, Float lexemeWeight, Long existingLexemeId) {
		return create.insertInto(LEXEME,
				LEXEME.WORD_ID, LEXEME.MEANING_ID, LEXEME.DATASET_CODE, LEXEME.TYPE, LEXEME.WEIGHT,
				LEXEME.FREQUENCY_GROUP_CODE, LEXEME.CORPUS_FREQUENCY, LEXEME.LEVEL1, LEXEME.LEVEL2,
				LEXEME.VALUE_STATE_CODE, LEXEME.PROCESS_STATE_CODE, LEXEME.COMPLEXITY)
				.select(DSL.select(DSL.val(wordId), DSL.val(meaningId), DSL.val(datasetCode), DSL.val(lexemeType.name()), DSL.val(BigDecimal.valueOf(lexemeWeight)),
						LEXEME.FREQUENCY_GROUP_CODE, LEXEME.CORPUS_FREQUENCY, LEXEME.LEVEL1, LEXEME.LEVEL2,
						LEXEME.VALUE_STATE_CODE, LEXEME.PROCESS_STATE_CODE, LEXEME.COMPLEXITY)
				.from(LEXEME)
				.where(LEXEME.ID.eq(existingLexemeId)))
				.returning(LEXEME.ID)
				.fetchOne()
				.getId();
	}

	public WordSynDetails getWordDetails(Long wordId) {
		return create.select(
				WORD.ID.as("word_id"),
				DSL.field("array_to_string(array_agg(distinct form.value_prese), ',', '*')", String.class).as("word"),
				DSL.field("array_to_string(array_agg(distinct form.morph_code), ',', '*')", String.class).as("morphCode"),
				WORD.LANG.as("language"))
				.from(WORD, PARADIGM, FORM)
				.where(WORD.ID.eq(wordId)
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.WORD_ID.eq(WORD.ID)
										//TODO what lexeme type?
										)))
				.groupBy(WORD.ID)
				.fetchOneInto(WordSynDetails.class);
	}

	public List<MeaningWord> getSynMeaningWords(Long lexemeId, List<String> meaningWordLangs, List<LexemeType> lexemeTypes) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Lexeme lh = LEXEME.as("lh");
		Word w2 = WORD.as("w2");
		Word wh = WORD.as("wh");
		Paradigm p2 = PARADIGM.as("p2");
		Paradigm ph = PARADIGM.as("ph");
		Form f2 = FORM.as("f2");
		Form fh = FORM.as("fh");

		return create
				.select(
						w2.ID.as("word_id"),
						f2.VALUE,
						f2.VALUE_PRESE,
						w2.HOMONYM_NR.as("homonym_number"),
						DSL.field(DSL.select(DSL.field(DSL.countDistinct(wh.HOMONYM_NR).gt(1)))
								.from(fh, ph, wh)
								.where(
										fh.VALUE.eq(f2.VALUE)
												.and(fh.MODE.eq(FormMode.WORD.name()))
												.and(fh.PARADIGM_ID.eq(ph.ID))
												.and(ph.WORD_ID.eq(wh.ID))
												.andExists(DSL
														.select(lh.ID)
														.from(lh)
														.where(
																lh.WORD_ID.eq(wh.ID)
																.and(lh.DATASET_CODE.eq(l2.DATASET_CODE)))))
								.groupBy(fh.VALUE)).as("word_homonyms_exist"),
						w2.LANG.as("language"),
						l2.ID.as("lexeme_id"),
						l2.TYPE.as("lexeme_type"),
						l2.WEIGHT.as("lexeme_weight"),
						l2.ORDER_BY)
				.from(l1, l2, w2, p2, f2)
				.where(
						l1.ID.eq(lexemeId)
								.and(l2.MEANING_ID.eq(l1.MEANING_ID))
								.and(l2.ID.ne(l1.ID))
								.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(l2.TYPE.in(lexemeTypes))
								.and(p2.WORD_ID.eq(w2.ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.eq(FormMode.WORD.name()))
								.and(w2.LANG.in(meaningWordLangs))
				)
				.groupBy(w2.ID, f2.VALUE, f2.VALUE_PRESE, l2.ID)
				.orderBy(w2.LANG, l2.ORDER_BY)
				.fetchInto(MeaningWord.class);
	}

	public List<SynRelation> getExistingFollowingRelationsForWord(Long relationId, String relTypeCode) {
		WordRelation wr2 = WORD_RELATION.as("wr2");

		return create.select(WORD_RELATION.ID, WORD_RELATION.ORDER_BY)
					.from(WORD_RELATION, wr2)
					.where(
							WORD_RELATION.WORD1_ID.eq(wr2.WORD1_ID)
									.and(wr2.ID.eq(relationId))
									.and(WORD_RELATION.ORDER_BY.ge(wr2.ORDER_BY))
									.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode))
					)
					.orderBy(WORD_RELATION.ORDER_BY)
					.fetchInto(SynRelation.class);
	}

	public List<TypeWordRelParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(TypeWordRelParam.class);
	}
}
