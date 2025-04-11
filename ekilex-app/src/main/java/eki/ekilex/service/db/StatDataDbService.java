package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.API_ERROR_COUNT;
import static eki.ekilex.data.db.main.Tables.API_REQUEST_COUNT;
import static eki.ekilex.data.db.main.Tables.ASPECT;
import static eki.ekilex.data.db.main.Tables.COLLOCATION;
import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DERIV;
import static eki.ekilex.data.db.main.Tables.DOMAIN;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.FREEFORM;
import static eki.ekilex.data.db.main.Tables.GENDER;
import static eki.ekilex.data.db.main.Tables.LANGUAGE;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.MORPH;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.POS;
import static eki.ekilex.data.db.main.Tables.REGISTER;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;

@Component
public class StatDataDbService implements GlobalConstant {

	@Autowired
	private DSLContext mainDb;

	public StatData getMainEntityStatData() {

		return mainDb
				.select(
						DSL.field(IGNORE_QUERY_LOG),
						DSL.select(DSL.count(WORD.ID)).from(WORD).asField("word_count"),
						DSL.select(DSL.count(LEXEME.ID)).from(LEXEME).asField("lexeme_count"),
						DSL.select(DSL.count(PARADIGM.ID)).from(PARADIGM).asField("paradigm_count"),
						DSL.select(DSL.count(FORM.ID)).from(FORM).asField("form_count"),
						DSL.select(DSL.count(MEANING.ID)).from(MEANING).asField("meaning_count"),
						DSL.select(DSL.count(DEFINITION.ID)).from(DEFINITION).asField("definition_count"),
						DSL.select(DSL.count(DATASET.CODE)).from(DATASET).asField("dataset_count"),
						DSL.select(DSL.count(SOURCE.ID)).from(SOURCE).asField("source_count"),
						DSL.select(DSL.count(COLLOCATION.ID)).from(COLLOCATION).asField("collocation_count"),
						DSL.select(DSL.count(LANGUAGE.CODE)).from(LANGUAGE).asField("language_count"),
						DSL.select(DSL.count(REGISTER.CODE)).from(REGISTER).asField("register_count"),
						DSL.select(DSL.count(ASPECT.CODE)).from(ASPECT).asField("aspect_count"),
						DSL.select(DSL.count(GENDER.CODE)).from(GENDER).asField("gender_count"),
						DSL.select(DSL.count(POS.CODE)).from(POS).asField("pos_count"),
						DSL.select(DSL.count(MORPH.CODE)).from(MORPH).asField("morph_count"),
						DSL.select(DSL.count(DERIV.CODE)).from(DERIV).asField("deriv_count"),
						DSL.select(DSL.count(DOMAIN.CODE)).from(DOMAIN).asField("domain_count"))
				.fetchSingleInto(StatData.class);
	}

	public List<StatDataRow> getFreeformStatData() {

		return mainDb
				.select(
						DSL.val(IGNORE_QUERY_LOG),
						FREEFORM.FREEFORM_TYPE_CODE.as("name"),
						DSL.count(FREEFORM.ID).as("row_count"))
				.from(FREEFORM)
				.groupBy(FREEFORM.FREEFORM_TYPE_CODE)
				.orderBy(DSL.field("row_count").desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getLexemeDatasetStatData() {

		return mainDb
				.select(
						DSL.val(IGNORE_QUERY_LOG),
						LEXEME.DATASET_CODE.as("name"),
						DSL.count(LEXEME.ID).as("row_count"))
				.from(LEXEME)
				.groupBy(LEXEME.DATASET_CODE)
				.orderBy(DSL.field("row_count").desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getActivityStatData(LocalDateTime from) {

		return mainDb
				.select(
						DSL.val(IGNORE_QUERY_LOG),
						ACTIVITY_LOG.EVENT_BY.as("name"),
						DSL.count(ACTIVITY_LOG.ID).as("row_count"))
				.from(ACTIVITY_LOG)
				.where(DSL.field(ACTIVITY_LOG.EVENT_ON).gt(from))
				.groupBy(ACTIVITY_LOG.EVENT_BY)
				.orderBy(DSL.field("row_count").desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getApiRequestStat() {

		return mainDb
				.select(
						DSL.val(IGNORE_QUERY_LOG),
						API_REQUEST_COUNT.AUTH_NAME.as("name"),
						DSL.count(API_REQUEST_COUNT.ID).as("row_count"))
				.from(API_REQUEST_COUNT)
				.groupBy(API_REQUEST_COUNT.AUTH_NAME)
				.orderBy(DSL.field("row_count").desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getApiErrorStat() {

		return mainDb
				.select(
						DSL.val(IGNORE_QUERY_LOG),
						API_ERROR_COUNT.AUTH_NAME.as("name"),
						DSL.count(API_ERROR_COUNT.ID).as("row_count"))
				.from(API_ERROR_COUNT)
				.groupBy(API_ERROR_COUNT.AUTH_NAME)
				.orderBy(DSL.field("row_count").desc())
				.fetchInto(StatDataRow.class);
	}
}
