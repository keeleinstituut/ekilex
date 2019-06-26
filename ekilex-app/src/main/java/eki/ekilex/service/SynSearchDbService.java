package eki.ekilex.service;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import eki.common.constant.FormMode;
import eki.ekilex.data.Relation;
import eki.ekilex.data.RelationParam;
import eki.ekilex.service.db.AbstractSearchDbService;

@Component
public class SynSearchDbService extends AbstractSearchDbService {
	private DSLContext create;

	public SynSearchDbService(DSLContext context) {
		create = context;
	}

	public Relation getOppositeRelation(Long wordId, Long relatedWordId, String relTypeCode, String classifierLabelLang, String classifierLabelTypeCode) {
		return create
				.select(
						WORD_RELATION.ID.as("id"),
						WORD_RELATION.RELATION_STATUS.as("relation_status"),
						FORM.VALUE.as("word"),
						WORD.ID.as("word_id"),
						WORD.LANG.as("word_lang"),
						WORD_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						WORD_RELATION.ORDER_BY.as("order_by"))
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
										.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
												.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						WORD,
						PARADIGM,
						FORM)
				.where(
						WORD_RELATION.WORD_REL_TYPE_CODE.eq(relTypeCode).and(
						WORD_RELATION.WORD2_ID.eq(wordId))
								.and(WORD_RELATION.WORD1_ID.eq(relatedWordId))
								.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.fetchOneInto(Relation.class);

	}

	public List<RelationParam> getRelationParameters(Long relationId) {

		//TODO - after generation
		return new ArrayList<>();
	}

}
