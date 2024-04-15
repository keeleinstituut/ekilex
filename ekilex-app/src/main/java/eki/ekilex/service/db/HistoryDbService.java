package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG_FDW;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.ActivityLogHistory;
import eki.ekilex.data.db.tables.ActivityLogFdw;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Word;

@Component
public class HistoryDbService implements GlobalConstant {

	@Autowired
	private DSLContext create;

	public List<ActivityLogHistory> getWordsHistory(int offset, int limit) {

		ActivityLogFdw al = ACTIVITY_LOG_FDW.as("al");
		ActivityLogFdw ala = ACTIVITY_LOG_FDW.as("ala");
		Word w = WORD.as("w");

		Field<String[]> wvf = DSL
				.select(DSL.arrayAggDistinct(DSL.field("{0}->>{1}", String.class, ala.CURR_DATA, "wordValue")))
				.from(ala)
				.where(
						DSL
								.field("{0}->>{1}", Long.class, ala.CURR_DATA, "wordId").cast(Long.class).eq(al.ENTITY_ID)
								.and(ala.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asField();

		return create
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME,
						wvf.as("word_values"))
				.from(al)
				.where(
						al.ENTITY_NAME.eq(ActivityEntity.WORD.name())
								.and(al.FUNCT_NAME.in("deleteWord", "joinWords", "join"))
								.andNotExists(DSL.select(w.ID).from(w).where(w.ID.eq(al.ENTITY_ID))))
				.orderBy(al.EVENT_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ActivityLogHistory.class);
	}

	public List<ActivityLogHistory> getMeaningsHistory(int offset, int limit) {

		ActivityLogFdw al = ACTIVITY_LOG_FDW.as("al");
		ActivityLogFdw ala = ACTIVITY_LOG_FDW.as("ala");
		Meaning m = MEANING.as("m");

		Field<String[]> wvf = DSL
				.select(DSL.arrayAggDistinct(DSL.field("{0}->>{1}", String.class, ala.CURR_DATA, "wordValue")))
				.from(ala)
				.where(
						DSL
								.field("{0}->>{1}", Long.class, ala.CURR_DATA, "meaningId").cast(Long.class).eq(al.ENTITY_ID)
								.and(ala.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asField();

		Table<Record1<String>> ald = DSL
				.select(
						DSL
								.field("jsonb_extract_path_text({0}, {1})", String.class, DSL
										.field("jsonb_array_elements({0})", JSONB.class, DSL
												.field("jsonb_extract_path({0}, {1})", JSONB.class, ala.CURR_DATA, "definitions")),
										"value")
								.as("definition"))
				.from(ala)
				.where(
						DSL.field("{0}->>{1}", Long.class, ala.CURR_DATA, "meaningId").cast(Long.class).eq(al.ENTITY_ID)
								.and(ala.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asTable("ald");

		Field<String[]> dvf = DSL
				.select(DSL.arrayAggDistinct(ald.field("definition", String.class)))
				.from(ald)
				.asField();

		Table<Record1<Long>> alx = DSL
				.select(DSL
						.field("jsonb_array_elements({0})", Long.class, DSL
								.field("jsonb_extract_path({0}, {1})", JSONB.class, ala.CURR_DATA, "lexemeIds"))
						.as("lexeme_id"))
				.from(ala)
				.where(
						DSL.field("{0}->>{1}", Long.class, ala.CURR_DATA, "meaningId").cast(Long.class).eq(al.ENTITY_ID)
								.and(DSL.field("{0}->>{1}", Long[].class, ala.CURR_DATA, "lexemeIds").isNotNull())
								.and(ala.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asTable("alx");

		Field<Long[]> lif = DSL.select(DSL.arrayAgg(alx.field("lexeme_id", Long.class)))
				.from(alx)
				.asField();

		return create
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME,
						wvf.as("word_values"),
						dvf.as("definition_values"),
						lif.as("lexeme_ids"))
				.from(al)
				.where(
						al.ENTITY_NAME.eq(ActivityEntity.MEANING.name())
								.and(al.FUNCT_NAME.in("deleteMeaning", "joinLexemes", "joinMeanings", "join"))
								.andNotExists(DSL.select(m.ID).from(m).where(m.ID.eq(al.ENTITY_ID))))
				.orderBy(al.EVENT_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ActivityLogHistory.class);
	}
}
