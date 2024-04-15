package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG_FDW;
import static eki.ekilex.data.db.Tables.ASPECT;
import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DERIV;
import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.GENDER;
import static eki.ekilex.data.db.Tables.LANGUAGE;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MORPH;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.POS;
import static eki.ekilex.data.db.Tables.REGISTER;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.WORD;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.StatData;
import eki.ekilex.data.StatDataRow;

@Component
public class StatDataDbService implements GlobalConstant {

	@Autowired
	private DSLContext create;

	public StatData getMainEntityStatData() {

		Table<Record1<Integer>> wc = DSL.select(DSL.field(DSL.count(WORD.ID)).as("word_count")).from(WORD).asTable("wc");
		Table<Record1<Integer>> lc = DSL.select(DSL.field(DSL.count(LEXEME.ID)).as("lexeme_count")).from(LEXEME).asTable("lc");
		Table<Record1<Integer>> pc = DSL.select(DSL.field(DSL.count(PARADIGM.ID)).as("paradigm_count")).from(PARADIGM).asTable("pc");
		Table<Record1<Integer>> fc = DSL.select(DSL.field(DSL.count(FORM.ID)).as("form_count")).from(FORM).asTable("fc");
		Table<Record1<Integer>> mnc = DSL.select(DSL.field(DSL.count(MEANING.ID)).as("meaning_count")).from(MEANING).asTable("mnc");
		Table<Record1<Integer>> dc = DSL.select(DSL.field(DSL.count(DEFINITION.ID)).as("definition_count")).from(DEFINITION).asTable("dc");
		Table<Record1<Integer>> datc = DSL.select(DSL.field(DSL.count(DATASET.CODE)).as("dataset_count")).from(DATASET).asTable("datc");
		Table<Record1<Integer>> sc = DSL.select(DSL.field(DSL.count(SOURCE.ID)).as("source_count")).from(SOURCE).asTable("sc");
		Table<Record1<Integer>> cc = DSL.select(DSL.field(DSL.count(COLLOCATION.ID)).as("collocation_count")).from(COLLOCATION).asTable("cc");
		Table<Record1<Integer>> langc = DSL.select(DSL.field(DSL.count(LANGUAGE.CODE)).as("language_count")).from(LANGUAGE).asTable("langc");
		Table<Record1<Integer>> rc = DSL.select(DSL.field(DSL.count(REGISTER.CODE)).as("register_count")).from(REGISTER).asTable("rc");
		Table<Record1<Integer>> ac = DSL.select(DSL.field(DSL.count(ASPECT.CODE)).as("aspect_count")).from(ASPECT).asTable("ac");
		Table<Record1<Integer>> gc = DSL.select(DSL.field(DSL.count(GENDER.CODE)).as("gender_count")).from(GENDER).asTable("gc");
		Table<Record1<Integer>> posc = DSL.select(DSL.field(DSL.count(POS.CODE)).as("pos_count")).from(POS).asTable("posc");
		Table<Record1<Integer>> mc = DSL.select(DSL.field(DSL.count(MORPH.CODE)).as("morph_count")).from(MORPH).asTable("mc");
		Table<Record1<Integer>> derc = DSL.select(DSL.field(DSL.count(DERIV.CODE)).as("deriv_count")).from(DERIV).asTable("derc");
		Table<Record1<Integer>> domc = DSL.select(DSL.field(DSL.count(DOMAIN.CODE)).as("domain_count")).from(DOMAIN).asTable("domc");

		return create
				.select(
						DSL.field(IGNORE_QUERY_LOG).as("comment"),
						wc.field("word_count", long.class),
						lc.field("lexeme_count", long.class),
						pc.field("paradigm_count", long.class),
						fc.field("form_count", long.class),
						mnc.field("meaning_count", long.class),
						dc.field("definition_count", long.class),
						datc.field("dataset_count", long.class),
						sc.field("source_count", long.class),
						cc.field("collocation_count", long.class),
						langc.field("language_count", long.class),
						rc.field("register_count", long.class),
						ac.field("aspect_count", long.class),
						gc.field("gender_count", long.class),
						posc.field("pos_count", long.class),
						mc.field("morph_count", long.class),
						derc.field("deriv_count", long.class),
						domc.field("domain_count", long.class))
				.from(wc, lc, pc, fc, mnc, dc, datc, sc, cc, langc, rc, ac, gc, posc, mc, derc, domc)
				.fetchSingleInto(StatData.class);
	}

	public List<StatDataRow> getFreeformStatData() {

		Field<Integer> rowCount = DSL.field((DSL.count(FREEFORM.ID)).as("row_count"));
		return create
				.select(
						DSL.field(IGNORE_QUERY_LOG).as("comment"),
						DSL.field(FREEFORM.TYPE).as("name"),
						rowCount)
				.from(FREEFORM)
				.groupBy(FREEFORM.TYPE)
				.orderBy(rowCount.desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getLexemeDatasetStatData() {

		Field<Integer> rowCount = DSL.field((DSL.count(LEXEME.ID)).as("row_count"));
		return create
				.select(
						DSL.field(IGNORE_QUERY_LOG).as("comment"),
						DSL.field(LEXEME.DATASET_CODE).as("name"),
						rowCount)
				.from(LEXEME)
				.groupBy(LEXEME.DATASET_CODE)
				.orderBy(rowCount.desc())
				.fetchInto(StatDataRow.class);
	}

	public List<StatDataRow> getActivityStatData(Timestamp from) {

		Field<Integer> rowCount = DSL.field((DSL.count(ACTIVITY_LOG_FDW.ID)).as("row_count"));
		return create
				.select(
						DSL.field(IGNORE_QUERY_LOG).as("comment"),
						DSL.field(ACTIVITY_LOG_FDW.EVENT_BY).as("name"),
						rowCount)
				.from(ACTIVITY_LOG_FDW)
				.where(DSL.field(ACTIVITY_LOG_FDW.EVENT_ON).gt(from))
				.groupBy(ACTIVITY_LOG_FDW.EVENT_BY)
				.orderBy(rowCount.desc())
				.fetchInto(StatDataRow.class);
	}
}
