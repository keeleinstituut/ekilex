package eki.ekilex.service.db;

import static eki.ekilex.data.db.arch.Tables.ACTIVITY_LOG_BULK;
import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.GlobalConstant;
import eki.ekilex.data.ActivityLogHistory;
import eki.ekilex.data.db.arch.tables.ActivityLogBulk;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class HistoryDbService implements GlobalConstant {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private DSLContext archDb;

	public List<ActivityLogHistory> getWordsHistory(int offset, int limit) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		ActivityLogBulk alb = ACTIVITY_LOG_BULK.as("alb");
		Word w = WORD.as("w");

		List<ActivityLogHistory> wordsHistory = mainDb
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME)
				.from(al)
				.where(
						al.ENTITY_NAME.eq(ActivityEntity.WORD.name())
								.and(al.FUNCT_NAME.in("deleteWord", "joinWords", "join"))
								.andNotExists(DSL.select(w.ID).from(w).where(w.ID.eq(al.ENTITY_ID))))
				.orderBy(al.EVENT_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ActivityLogHistory.class);

		List<Long> entityIds = wordsHistory.stream()
				.map(ActivityLogHistory::getEntityId)
				.distinct()
				.collect(Collectors.toList());

		Map<Long, String[]> wordValuesMap = archDb
				.select(
						alb.ENTITY_ID,
						DSL.arrayAggDistinct(DSL.field("{0}->>{1}", String.class, alb.CURR_DATA, "wordValue")).as("word_values"))
				.from(alb)
				.where(
						alb.ENTITY_NAME.eq(ActivityEntity.WORD.name())
								.and(DSL.field("{0}->>{1}", Long.class, alb.CURR_DATA, "wordId").cast(Long.class).in(entityIds))
								.and(alb.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.groupBy(alb.ENTITY_ID)
				.fetchMap(alb.ENTITY_ID, DSL.field("word_values", String[].class));

		wordsHistory.forEach(historyRow -> {
			Long entityId = historyRow.getEntityId();
			String[] wordValueArr = wordValuesMap.get(entityId);
			if (wordValueArr != null) {
				List<String> wordValues = Arrays.asList(wordValueArr);
				historyRow.setWordValues(wordValues);
			}
		});

		return wordsHistory;
	}

	public List<ActivityLogHistory> getMeaningsHistory(int offset, int limit) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		ActivityLogBulk alb = ACTIVITY_LOG_BULK.as("alb");
		//ActivityLog ala = ACTIVITY_LOG.as("ala");
		Meaning m = MEANING.as("m");

		/*
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
		*/

		List<ActivityLogHistory> meaningsHistory = mainDb
				.select(
						al.ID,
						al.EVENT_BY,
						al.EVENT_ON,
						al.FUNCT_NAME,
						al.OWNER_ID,
						al.OWNER_NAME,
						al.ENTITY_ID,
						al.ENTITY_NAME)
				.from(al)
				.where(
						al.ENTITY_NAME.eq(ActivityEntity.MEANING.name())
								.and(al.FUNCT_NAME.in("deleteMeaning", "joinLexemes", "joinMeanings", "join"))
								.andNotExists(DSL.select(m.ID).from(m).where(m.ID.eq(al.ENTITY_ID))))
				.orderBy(al.EVENT_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(ActivityLogHistory.class);

		List<Long> entityIds = meaningsHistory.stream()
				.map(ActivityLogHistory::getEntityId)
				.distinct()
				.collect(Collectors.toList());

		Table<Record2<Long, String>> ald = DSL
				.select(
						alb.ENTITY_ID,
						DSL
								.field("jsonb_extract_path_text({0}, {1})", String.class, DSL
										.field("jsonb_array_elements({0})", JSONB.class, DSL
												.field("jsonb_extract_path({0}, {1})", JSONB.class, alb.CURR_DATA, "definitions")),
										"value")
								.as("definition_value"))
				.from(alb)
				.where(
						alb.ENTITY_NAME.eq(ActivityEntity.MEANING.name())
								.and(DSL.field("{0}->>{1}", Long.class, alb.CURR_DATA, "meaningId").cast(Long.class).in(entityIds))
								.and(alb.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asTable("ald");

		Map<Long, String[]> definitionValuesMap = archDb
				.select(
						ald.field("entity_id", Long.class),
						DSL.arrayAggDistinct(ald.field("definition_value", String.class)).as("definition_values"))
				.from(ald)
				.groupBy(ald.field("entity_id"))
				.fetchMap(ald.field("entity_id", Long.class), DSL.field("definition_values", String[].class));

		Table<Record2<Long, Long>> alx = DSL
				.select(
						alb.ENTITY_ID,
						DSL
								.field("jsonb_array_elements({0})", Long.class, DSL
										.field("jsonb_extract_path({0}, {1})", JSONB.class, alb.CURR_DATA, "lexemeIds"))
								.as("lexeme_id"))
				.from(alb)
				.where(
						alb.ENTITY_NAME.eq(ActivityEntity.MEANING.name())
								.and(DSL.field("{0}->>{1}", Long.class, alb.CURR_DATA, "meaningId").cast(Long.class).in(entityIds))
								.and(DSL.field("{0}->>{1}", Long[].class, alb.CURR_DATA, "lexemeIds").isNotNull())
								.and(alb.CURR_DATA.ne(DSL.field("{0}", JSONB.class, JSONB.valueOf("{}")))))
				.asTable("alx");

		Map<Long, Long[]> lexemeIdsMap = archDb
				.select(
						alx.field("entity_id", Long.class),
						DSL.arrayAggDistinct(DSL.cast(alx.field("lexeme_id"), Long.class)).as("lexeme_ids"))
				.from(alx)
				.groupBy(alx.field("entity_id"))
				.fetchMap(alx.field("entity_id", Long.class), DSL.field("lexeme_ids", Long[].class));

		meaningsHistory.forEach(historyRow -> {
			Long entityId = historyRow.getEntityId();
			String[] definitionValueArr = definitionValuesMap.get(entityId);
			if (definitionValueArr != null) {
				List<String> definitionValues = Arrays.asList(definitionValueArr);
				historyRow.setDefinitionValues(definitionValues);
			}
			Long[] lexemeIdArr = lexemeIdsMap.get(entityId);
			if (lexemeIdArr != null) {
				List<Long> lexemeIds = Arrays.asList(lexemeIdArr);
				historyRow.setLexemeIds(lexemeIds);
			}
		});

		return meaningsHistory;
	}
}
