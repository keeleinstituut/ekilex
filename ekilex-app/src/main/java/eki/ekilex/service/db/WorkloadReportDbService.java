package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityOwner;
import eki.ekilex.constant.CrudType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.WorkloadReportCount;
import eki.ekilex.data.db.main.tables.ActivityLog;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class WorkloadReportDbService implements SystemConstant {

	private static final String[] CREATE_FUNCT_NAMES = new String[] {
			"createWord", "createLexeme", "createWordAndSynRelation", "createSynMeaningWord", "duplicateEmptyLexemeAndMeaning", "duplicateLexemeData", "duplicateMeaningData"};

	private static final String[] DELETE_FUNCT_NAMES = new String[] {
			"deleteLexeme", "deleteMeaning", "deleteWord"};

	@Autowired
	private DSLContext mainDb;

	public List<WorkloadReportCount> getWorkloadReportUserCounts(
			LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, boolean includeUnspecifiedDatasets, List<String> userNames) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		LocalDateTime from = dateFrom.atStartOfDay();
		LocalDateTime until = dateUntil.plusDays(1).atStartOfDay();

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.ne(ActivityOwner.SOURCE.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(where, al, userNames);

		Table<Record4<String, String, Long, String>> wl = DSL
				.select(
						al.OWNER_NAME.as("activity_owner"),
						al.EVENT_BY.as("user_name"),
						al.OWNER_ID.as("owner_id"),
						(DSL
								.when(al.FUNCT_NAME.in(CREATE_FUNCT_NAMES), CrudType.CREATE.name())
								.when(al.FUNCT_NAME.in(DELETE_FUNCT_NAMES), CrudType.DELETE.name())
								.otherwise(CrudType.UPDATE.name()).as("activity_type")))
				.from(al)
				.where(where)
				.asTable("wl");

		return mainDb
				.select(
						wl.field("activity_owner"),
						wl.field("activity_type"),
						wl.field("user_name"),
						DSL.countDistinct(wl.field("owner_id")).as("count"))
				.from(wl)
				.groupBy(wl.field("activity_owner"), wl.field("activity_type"), wl.field("user_name"))
				.fetchInto(WorkloadReportCount.class);
	}

	public List<WorkloadReportCount> getWorkloadReportTotalCounts(
			LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, boolean includeUnspecifiedDatasets, List<String> userNames) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		LocalDateTime from = dateFrom.atStartOfDay();
		LocalDateTime until = dateUntil.plusDays(1).atStartOfDay();

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.ne(ActivityOwner.SOURCE.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(where, al, userNames);

		Table<Record3<String, Long, String>> wl = DSL
				.select(
						al.OWNER_NAME.as("activity_owner"),
						al.OWNER_ID.as("owner_id"),
						(DSL
								.when(al.FUNCT_NAME.in(CREATE_FUNCT_NAMES), CrudType.CREATE.name())
								.when(al.FUNCT_NAME.in(DELETE_FUNCT_NAMES), CrudType.DELETE.name())
								.otherwise(CrudType.UPDATE.name()).as("activity_type")))
				.from(al)
				.where(where)
				.asTable("wl");

		return mainDb
				.select(
						wl.field("activity_owner"),
						wl.field("activity_type"),
						DSL.countDistinct(wl.field("owner_id")).as("count"))
				.from(wl)
				.groupBy(wl.field("activity_owner"), wl.field("activity_type"))
				.orderBy(
						wl.field("activity_owner", String.class).sortAsc(
								ActivityOwner.WORD.name(),
								ActivityOwner.LEXEME.name(),
								ActivityOwner.MEANING.name()),
						wl.field("activity_type", String.class).sortAsc(
								CrudType.CREATE.name(),
								CrudType.UPDATE.name(),
								CrudType.DELETE.name()))
				.fetchInto(WorkloadReportCount.class);
	}

	public List<WorkloadReportCount> getWorkloadReportFunctionCounts(
			LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, boolean includeUnspecifiedDatasets, List<String> userNames, ActivityOwner activityOwner, CrudType activityType) {

		ActivityLog al = ACTIVITY_LOG.as("al");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");

		LocalDateTime from = dateFrom.atStartOfDay();
		LocalDateTime until = dateUntil.plusDays(1).atStartOfDay();

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.eq(activityOwner.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(where, al, userNames);
		where = addActivityTypeCondition(where, al, activityType);

		if (ActivityOwner.LEXEME.equals(activityOwner)) {
			return mainDb
					.select(
							al.FUNCT_NAME,
							al.OWNER_NAME.as("activity_owner"),
							al.ENTITY_NAME.as("activity_entity"),
							al.EVENT_BY.as("user_name"),
							DSL.countDistinct(al.OWNER_ID).as("count"),
							DSL.arrayAggDistinct(al.OWNER_ID).as("owner_ids"),
							DSL.arrayAggDistinct(l.WORD_ID).as("word_ids"),
							DSL.arrayAggDistinct(l.MEANING_ID).as("meaning_ids"),
							DSL.arrayAggDistinct(w.VALUE).as("wordValues"))
					.from(al
							.leftOuterJoin(l).on(al.OWNER_ID.eq(l.ID))
							.leftOuterJoin(w).on(l.WORD_ID.eq(w.ID)))
					.where(where)
					.groupBy(al.FUNCT_NAME, al.OWNER_NAME, al.ENTITY_NAME, al.EVENT_BY)
					.fetchInto(WorkloadReportCount.class);

		} else if (ActivityOwner.WORD.equals(activityOwner)) {
			return mainDb
					.select(
							al.FUNCT_NAME,
							al.OWNER_NAME.as("activity_owner"),
							al.ENTITY_NAME.as("activity_entity"),
							al.EVENT_BY.as("user_name"),
							DSL.countDistinct(al.OWNER_ID).as("count"),
							DSL.arrayAggDistinct(al.OWNER_ID).as("owner_ids"),
							DSL.arrayAggDistinct(w.VALUE).as("wordValues"))
					.from(al
							.leftOuterJoin(w).on(al.OWNER_ID.eq(w.ID)))
					.where(where)
					.groupBy(al.FUNCT_NAME, al.OWNER_NAME, al.ENTITY_NAME, al.EVENT_BY)
					.fetchInto(WorkloadReportCount.class);
		} else {
			return mainDb
					.select(
							al.FUNCT_NAME,
							al.OWNER_NAME.as("activity_owner"),
							al.ENTITY_NAME.as("activity_entity"),
							al.EVENT_BY.as("user_name"),
							DSL.countDistinct(al.OWNER_ID).as("count"),
							DSL.arrayAggDistinct(al.OWNER_ID).as("owner_ids"))
					.from(al)
					.where(where)
					.groupBy(al.FUNCT_NAME, al.OWNER_NAME, al.ENTITY_NAME, al.EVENT_BY)
					.fetchInto(WorkloadReportCount.class);
		}
	}

	@Cacheable(value = CACHE_KEY_WORKLOAD_REPORT, key = "{#root.methodName, #activityOwner, #activityType}")
	public List<String> getFunctionNames(ActivityOwner activityOwner, CrudType activityType) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		Condition where = al.OWNER_NAME.eq(activityOwner.name());
		where = addActivityTypeCondition(where, al, activityType);

		return mainDb
				.select(al.FUNCT_NAME)
				.from(al)
				.where(where)
				.groupBy(al.FUNCT_NAME)
				.orderBy(al.FUNCT_NAME)
				.fetchInto(String.class);
	}

	private Condition addUserNamesConditon(Condition where, ActivityLog al, List<String> userNames) {

		if (CollectionUtils.isNotEmpty(userNames)) {
			// using % wildcard to find API users as well
			String userNamesSimilarCrit = "(" + StringUtils.join(userNames, '|') + ")%";
			where = where.and(al.EVENT_BY.similarTo(userNamesSimilarCrit));
		}
		return where;
	}

	private Condition addDatasetsCondition(Condition where, ActivityLog al, List<String> datasetCodes, boolean includeUnspecifiedDatasets) {

		if (includeUnspecifiedDatasets) {
			if (CollectionUtils.isEmpty(datasetCodes)) {
				where = where.and(al.DATASET_CODE.isNull());
			} else {
				where = where.and(DSL.or(al.DATASET_CODE.in(datasetCodes), al.DATASET_CODE.isNull()));
			}
		} else {
			where = where.and(al.DATASET_CODE.in(datasetCodes));
		}
		return where;
	}

	private Condition addActivityTypeCondition(Condition where, ActivityLog al, CrudType activityType) {

		if (CrudType.CREATE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.in(CREATE_FUNCT_NAMES));
		} else if (CrudType.DELETE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.in(DELETE_FUNCT_NAMES));
		} else if (CrudType.UPDATE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.notIn(CREATE_FUNCT_NAMES)).and(al.FUNCT_NAME.notIn(DELETE_FUNCT_NAMES));
		}
		return where;
	}
}
