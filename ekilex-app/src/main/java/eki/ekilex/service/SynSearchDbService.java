package eki.ekilex.service;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LAYER_STATE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.common.constant.LayerName;
import eki.common.constant.LexemeType;
import eki.ekilex.data.MeaningWord;
import eki.ekilex.data.RelationParam;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.data.WordSynDetails;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.service.db.AbstractSearchDbService;

@Component
public class SynSearchDbService extends AbstractSearchDbService {
	private DSLContext create;

	public SynSearchDbService(DSLContext context) {
		create = context;
	}

	public List<SynRelationParamTuple> getWordSynRelations(Long wordId, String relationType, String datasetCode, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation opposite = WORD_RELATION.as("opposite");

		Table<Record2<String, Integer>> homonymCount = create.select(FORM.VALUE, WORD.HOMONYM_NR)
				.from(FORM, PARADIGM, WORD, LEXEME)
				.where(FORM.PARADIGM_ID.eq(PARADIGM.ID)
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.DATASET_CODE.eq(datasetCode))
						.and(FORM.MODE.eq(FormMode.WORD.name())))
				.asTable("homonymCount");

		return create
				.selectDistinct(
						WORD_RELATION.ID.as("relation_id"),
						WORD_RELATION.WORD2_ID.as("opposite_word_id"),
						WORD_RELATION.RELATION_STATUS.as("relation_status"),
						WORD.ID.as("word_id"),
						WORD.HOMONYM_NR.as("word_homonym_number"),
						FORM.VALUE.as("word"),
						opposite.RELATION_STATUS.as("opposite_relation_status"),
						WORD_RELATION_PARAM.NAME.as("param_name"),
						WORD_RELATION_PARAM.VALUE.as("param_value"),
						WORD_RELATION.ORDER_BY.as("order_by"),
						DEFINITION.VALUE.as("definition_value"),
						DEFINITION.COMPLEXITY.as("definition_complexity"),
						DEFINITION.ORDER_BY.as("definition_order"),
						homonymCount.field("homonym_nr").as("other_homonym_number"),
						DSL.field(
							DSL.exists(
								DSL.select(DSL.arrayAgg(WORD_WORD_TYPE.WORD_TYPE_CODE))
								.from(WORD_WORD_TYPE)
								.where(WORD_WORD_TYPE.WORD_ID.eq(WORD_RELATION.WORD2_ID)
									.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_PREFIXOID)))
								.groupBy(WORD_WORD_TYPE.WORD_ID)))
							.as("is_prefixoid"),
						DSL.field(
								DSL.exists(
										DSL.select(DSL.arrayAgg(WORD_WORD_TYPE.WORD_TYPE_CODE))
												.from(WORD_WORD_TYPE)
												.where(WORD_WORD_TYPE.WORD_ID.eq(WORD_RELATION.WORD2_ID)
														.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_SUFFIXOID)))
												.groupBy(WORD_WORD_TYPE.WORD_ID)))
								.as("is_suffixoid"),
						LEXEME.LEVEL1, LEXEME.LEVEL2
						)
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
										.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode))))
								.leftOuterJoin(opposite)
								.on (opposite.WORD2_ID.eq(WORD_RELATION.WORD1_ID)
										.and(opposite.WORD1_ID.eq(WORD_RELATION.WORD2_ID)
										.and(opposite.WORD_REL_TYPE_CODE.eq(WORD_RELATION.WORD_REL_TYPE_CODE)))
								)
								.leftOuterJoin(WORD_RELATION_PARAM).on(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(WORD_RELATION.ID))
								.leftOuterJoin(LEXEME)
									.on(
										LEXEME.WORD_ID.eq(WORD_RELATION.WORD2_ID)
												.and(LEXEME.DATASET_CODE.eq(datasetCode))
												.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
									)
								.leftOuterJoin(MEANING).on(LEXEME.MEANING_ID.eq(MEANING.ID))
								.leftOuterJoin(DEFINITION).on(DEFINITION.MEANING_ID.eq(MEANING.ID).and(DEFINITION.COMPLEXITY.like("DETAIL%").or(DEFINITION.COMPLEXITY.like("SIMPLE%"))))
						,
						WORD,
						PARADIGM,
						FORM
							.leftOuterJoin(homonymCount).on(homonymCount.field("value", String.class).eq(FORM.VALUE))

				)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
								.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.orderBy(
						WORD_RELATION.ORDER_BY,
						LEXEME.LEVEL1,
						LEXEME.LEVEL2,
						DEFINITION.COMPLEXITY,
						DEFINITION.ORDER_BY
				)
				.fetchInto(SynRelationParamTuple.class);
	}

	public List<WordSynLexeme> getWordPrimarySynonymLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction) {

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
						.leftOuterJoin(LAYER_STATE).on(LAYER_STATE.LEXEME_ID.eq(LEXEME.ID).and(LAYER_STATE.LAYER_NAME.eq(LayerName.SYN.name())))
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

	public List<MeaningWord> getSynMeaningWords(Long lexemeId, String languageCode) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						w2.ID.as("word_id"),
						f2.VALUE,
						w2.HOMONYM_NR.as("homonym_number"),
						w2.LANG.as("language"),
						l2.ID.as("lexeme_id"),
						l2.TYPE.as("lexeme_type"),
						l2.WEIGHT.as("lexeme_weight"),
						l2.ORDER_BY)
				.from(l1, l2, w2, p2, f2)
				.where(
						l1.ID.eq(lexemeId)
								//TODO what lexeme type?
								.and(l2.MEANING_ID.eq(l1.MEANING_ID))
								.and(l2.ID.ne(l1.ID))
								.and(l2.DATASET_CODE.eq(l1.DATASET_CODE))
								.and(l2.WORD_ID.eq(w2.ID))
								.and(p2.WORD_ID.eq(w2.ID))
								.and(f2.PARADIGM_ID.eq(p2.ID))
								.and(f2.MODE.eq(FormMode.WORD.name()))
								.and(w2.LANG.eq(languageCode))
				)
				.groupBy(w2.ID, f2.VALUE, l2.ID)
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

	public List<RelationParam> getWordRelationParams(Long wordRelationId) {

		return create
				.select(WORD_RELATION_PARAM.NAME, WORD_RELATION_PARAM.VALUE)
				.from(WORD_RELATION_PARAM)
				.where(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(wordRelationId))
				.fetchInto(RelationParam.class);
	}
}
