package eki.ekilex.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityOwner;
import eki.ekilex.constant.CrudType;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.data.WorkloadReportCount;
import eki.ekilex.data.WorkloadReportRow;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.service.db.WorkloadReportDbService;
import eki.ekilex.service.util.DatasetUtil;

@Component
public class WordloadReportService {

	@Autowired
	private WorkloadReportDbService workloadReportDbService;

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private DatasetUtil datasetUtil;

	@Transactional
	public List<EkiUser> getUsersByDatasetPermission(String datasetCode) {
		return userDbService.getUsersByDatasetPermission(datasetCode);
	}

	@Transactional
	public List<Dataset> getDatasets() {
		List<Dataset> datasets = datasetDbService.getDatasets();
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public WorkloadReport getWorkloadReport(LocalDate dateFrom, LocalDate dateUntil, String datasetCode, List<String> userNames) {

		WorkloadReport workloadReport = new WorkloadReport();
		List<WorkloadReportRow> userReports = new ArrayList<>();
		WorkloadReportRow totalReport = new WorkloadReportRow();

		if (CollectionUtils.isNotEmpty(userNames)) {
			List<WorkloadReportCount> allUserCounts = workloadReportDbService.getWorkloadReportUserCounts(dateFrom, dateUntil, datasetCode, userNames);

			Map<String, List<WorkloadReportCount>> countsByUserMap = allUserCounts.stream().collect(Collectors.groupingBy(WorkloadReportCount::getUserName));
			for (Map.Entry<String, List<WorkloadReportCount>> countsByUserMapEntry : countsByUserMap.entrySet()) {
				String userName = countsByUserMapEntry.getKey();
				List<WorkloadReportCount> singleUserCounts = countsByUserMapEntry.getValue();

				WorkloadReportRow userReport = new WorkloadReportRow();
				userReport.setUserName(userName);
				for (WorkloadReportCount userCount : singleUserCounts) {
					ActivityOwner activityOwner = userCount.getActivityOwner();
					CrudType activityType = userCount.getActivityType();
					int count = userCount.getCount();
					setWorkloadReportRowCount(userReport, activityOwner, activityType, count);
				}
				userReports.add(userReport);
			}
		}
		workloadReport.setUserReports(userReports);

		List<WorkloadReportCount> totalCounts = workloadReportDbService.getWorkloadReportTotalCounts(dateFrom, dateUntil, datasetCode, userNames);
		for (WorkloadReportCount totalCount : totalCounts) {
			ActivityOwner activityOwner = totalCount.getActivityOwner();
			CrudType activityType = totalCount.getActivityType();
			int count = totalCount.getCount();
			setWorkloadReportRowCount(totalReport, activityOwner, activityType, count);
		}

		workloadReport.setUserReports(userReports);
		workloadReport.setTotalReport(totalReport);

		return workloadReport;
	}

	private void setWorkloadReportRowCount(WorkloadReportRow workloadReportRow, ActivityOwner activityOwner, CrudType activityType, int count) {

		if (ActivityOwner.WORD.equals(activityOwner)) {
			if (CrudType.CREATE.equals(activityType)) {
				workloadReportRow.setCreatedWordCount(count);
			} else if (CrudType.UPDATE.equals(activityType)) {
				workloadReportRow.setUpdatedWordCount(count);
			} else if (CrudType.DELETE.equals(activityType)) {
				workloadReportRow.setDeletedWordCount(count);
			}
		} else if (ActivityOwner.LEXEME.equals(activityOwner)) {
			if (CrudType.CREATE.equals(activityType)) {
				workloadReportRow.setCreatedLexemeCount(count);
			} else if (CrudType.UPDATE.equals(activityType)) {
				workloadReportRow.setUpdatedLexemeCount(count);
			} else if (CrudType.DELETE.equals(activityType)) {
				workloadReportRow.setDeletedLexemeCount(count);
			}
		} else if (ActivityOwner.MEANING.equals(activityOwner)) {
			if (CrudType.CREATE.equals(activityType)) {
				workloadReportRow.setCreatedMeaningCount(count);
			} else if (CrudType.UPDATE.equals(activityType)) {
				workloadReportRow.setUpdatedMeaningCount(count);
			} else if (CrudType.DELETE.equals(activityType)) {
				workloadReportRow.setDeletedMeaningCount(count);
			}
		}
	}
}
