package eki.ekilex.service;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_RELATION_PARAM;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.data.WordSynLexeme;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.service.db.AbstractSearchDbService;

@Component
public class SynSearchDbService extends AbstractSearchDbService {
	private DSLContext create;

	public SynSearchDbService(DSLContext context) {
		create = context;
	}

	public List<SynRelationParamTuple> getWordSynRelations(Long wordId, String relationType, String classifierLabelLang, String classifierLabelTypeCode) {
		WordRelation opposite = WORD_RELATION.as("opposite");

		return create
				.selectDistinct(
						WORD_RELATION.ID.as("relation_id"),
						WORD.ID.as("word_id"),
						FORM.VALUE.as("word"),
						WORD_RELATION.RELATION_STATUS.as("relation_status"),
						opposite.RELATION_STATUS.as("opposite_relation_status"),
						WORD_RELATION_PARAM.NAME.as("param_name"),
						WORD_RELATION_PARAM.VALUE.as("param_value"),
						WORD_RELATION.ORDER_BY.as("order_by"))
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
										.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode))))
								.leftOuterJoin(opposite)
								.on(
									opposite.WORD2_ID.eq(WORD_RELATION.WORD1_ID))
										.and(opposite.WORD1_ID.eq(WORD_RELATION.WORD2_ID)
										.and(opposite.WORD_REL_TYPE_CODE.eq(WORD_RELATION.WORD_REL_TYPE_CODE))
								)
								.leftOuterJoin(WORD_RELATION_PARAM).on(WORD_RELATION_PARAM.WORD_RELATION_ID.eq(WORD_RELATION.ID)),
						WORD,
						PARADIGM,
						FORM)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
								.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetchInto(SynRelationParamTuple.class);
	}


	public List<WordSynLexeme> getWordSynLexemes(Long wordId, SearchDatasetsRestriction searchDatasetsRestriction) {

		Condition dsWhere = composeLexemeDatasetsCondition(LEXEME, searchDatasetsRestriction);

		return create.select(
					WORD.ID.as("word_id"),
					LEXEME.ID.as("lexeme_id"),
					LEXEME.MEANING_ID,
					LEXEME.DATASET_CODE.as("dataset"),
					LEXEME.LEVEL1,
					LEXEME.LEVEL2,
					LEXEME.LEVEL3
				)
				.from(FORM, PARADIGM, WORD, LEXEME, MEANING, DATASET)
				.where(
						WORD.ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name()))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(LEXEME.WORD_ID.eq(WORD.ID))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.DATASET_CODE.eq(DATASET.CODE))
								.and(dsWhere)
				)
				.groupBy(WORD.ID, LEXEME.ID, MEANING.ID, DATASET.CODE)
				.orderBy(WORD.ID, DATASET.ORDER_BY, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
				.fetchInto(WordSynLexeme.class);
	}


	public void changeRelationStatus(Long id, String status) {
		create.update(WORD_RELATION)
				.set(WORD_RELATION.RELATION_STATUS, status)
				.where(WORD_RELATION.ID.eq(id))
				.execute();
	}

	public void createLexeme(Long wordId, Long meaningId, String datasetCode, Long existingLexemeId
			//,
	//		Integer level1, Integer level2, Integer level3, String processStateCode, String valueStateCode, String frequencyGroupCode, BigDecimal corpusFrequency
		) {
		create.insertInto(LEXEME,
				LEXEME.WORD_ID, LEXEME.MEANING_ID, LEXEME.DATASET_CODE,
				LEXEME.PROCESS_STATE_CODE, LEXEME.VALUE_STATE_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
				LEXEME.FREQUENCY_GROUP_CODE, LEXEME.CORPUS_FREQUENCY)
				.select(create.select(DSL.val(wordId), DSL.val(meaningId), DSL.val(datasetCode),
						LEXEME.PROCESS_STATE_CODE, LEXEME.VALUE_STATE_CODE
						, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3,
						LEXEME.FREQUENCY_GROUP_CODE, LEXEME.CORPUS_FREQUENCY
						).from(LEXEME).where(LEXEME.ID.eq(existingLexemeId))
				).execute();

	}
}
