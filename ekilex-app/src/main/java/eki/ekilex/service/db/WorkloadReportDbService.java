package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;

import java.sql.Timestamp;
import java.time.LocalDate;
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
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityOwner;
import eki.common.service.db.AbstractDbService;
import eki.ekilex.constant.CrudType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.WorkloadReportCount;
import eki.ekilex.data.db.tables.ActivityLog;

@Component
public class WorkloadReportDbService extends AbstractDbService implements SystemConstant {

	private static final String[] CREATE_FUNCT_NAMES = new String[] {
			"createWord", "createLexeme", "createWordAndSynRelation", "createSynMeaningWord", "duplicateEmptyLexemeAndMeaning", "duplicateLexemeData", "duplicateMeaningData"};

	private static final String[] DELETE_FUNCT_NAMES = new String[] {
			"deleteLexeme", "deleteMeaning", "deleteWord"};

	@Autowired
	private DSLContext create;

	public List<WorkloadReportCount> getWorkloadReportUserCounts(
			LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, boolean includeUnspecifiedDatasets, List<String> userNames) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		Timestamp from = Timestamp.valueOf(dateFrom.atStartOfDay());
		Timestamp until = Timestamp.valueOf(dateUntil.plusDays(1).atStartOfDay());

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.ne(ActivityOwner.SOURCE.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(userNames, where, al);

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

		return create
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

		Timestamp from = Timestamp.valueOf(dateFrom.atStartOfDay());
		Timestamp until = Timestamp.valueOf(dateUntil.plusDays(1).atStartOfDay());

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.ne(ActivityOwner.SOURCE.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(userNames, where, al);

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

		return create
				.select(
						wl.field("activity_owner"),
						wl.field("activity_type"),
						DSL.countDistinct(wl.field("owner_id")).as("count"))
				.from(wl)
				.groupBy(wl.field("activity_owner"), wl.field("activity_type"))
				.fetchInto(WorkloadReportCount.class);
	}

	public List<WorkloadReportCount> getWorkloadReportFunctionCounts(
			LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, boolean includeUnspecifiedDatasets, List<String> userNames, ActivityOwner activityOwner, CrudType activityType) {

		ActivityLog al = ACTIVITY_LOG.as("al");

		Timestamp from = Timestamp.valueOf(dateFrom.atStartOfDay());
		Timestamp until = Timestamp.valueOf(dateUntil.plusDays(1).atStartOfDay());

		Condition where = al.EVENT_ON.ge(from)
				.and(al.EVENT_ON.lt(until))
				.and(al.OWNER_NAME.eq(activityOwner.name()));

		where = addDatasetsCondition(where, al, datasetCodes, includeUnspecifiedDatasets);
		where = addUserNamesConditon(userNames, where, al);

		if (CrudType.CREATE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.in(CREATE_FUNCT_NAMES));
		} else if (CrudType.DELETE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.in(DELETE_FUNCT_NAMES));
		} else if (CrudType.UPDATE.equals(activityType)) {
			where = where.and(al.FUNCT_NAME.notIn(CREATE_FUNCT_NAMES)).and(al.FUNCT_NAME.notIn(DELETE_FUNCT_NAMES));
		}

		return create
				.select(
						al.FUNCT_NAME,
						al.OWNER_NAME.as("activity_owner"),
						al.ENTITY_NAME.as("activity_entity"),
						al.EVENT_BY.as("user_name"),
						DSL.countDistinct(al.ENTITY_ID).as("count"))
				.from(al)
				.where(where)
				.groupBy(al.FUNCT_NAME, al.OWNER_NAME, al.ENTITY_NAME, al.EVENT_BY)
				.fetchInto(WorkloadReportCount.class);
	}

	private Condition addUserNamesConditon(List<String> userNames, Condition where, ActivityLog al) {
		if (CollectionUtils.isNotEmpty(userNames)) {
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
}
