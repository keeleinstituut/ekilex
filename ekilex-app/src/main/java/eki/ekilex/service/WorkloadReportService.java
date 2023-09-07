package eki.ekilex.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityOwner;
import eki.ekilex.constant.CrudType;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.data.WorkloadReportCount;
import eki.ekilex.data.WorkloadReportRow;
import eki.ekilex.data.WorkloadReportUserCount;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.service.db.WorkloadReportDbService;
import eki.ekilex.service.util.DatasetUtil;

@Component
public class WorkloadReportService {

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

		List<WorkloadReportCount> totalCounts = workloadReportDbService.getWorkloadReportTotalCounts(dateFrom, dateUntil, datasetCode, userNames);
		List<WorkloadReportCount> allUserCounts = workloadReportDbService.getWorkloadReportUserCounts(dateFrom, dateUntil, datasetCode, userNames);
		List<String> allUserNames = allUserCounts.stream().map(WorkloadReportCount::getUserName).distinct().collect(Collectors.toList());

		WorkloadReport workloadReport = new WorkloadReport();
		List<WorkloadReportRow> reportRows = new ArrayList<>();

		for (WorkloadReportCount totalCount : totalCounts) {
			ActivityOwner activityOwner = totalCount.getActivityOwner();
			CrudType activityType = totalCount.getActivityType();
			int totalCountValue = totalCount.getCount();

			List<WorkloadReportUserCount> userCounts = new ArrayList<>();
			for (String userName : allUserNames) {
				int userCount = allUserCounts.stream()
						.filter(uc -> uc.getUserName().equals(userName))
						.filter(uc -> uc.getActivityOwner().equals(activityOwner))
						.filter(uc -> uc.getActivityType().equals(activityType))
						.map(WorkloadReportCount::getCount)
						.findFirst()
						.orElse(0);

				WorkloadReportUserCount reportUserCount = new WorkloadReportUserCount();
				reportUserCount.setUserName(userName);
				reportUserCount.setCount(userCount);
				userCounts.add(reportUserCount);
			}

			List<WorkloadReportRow> functionRows = new ArrayList<>();
			List<WorkloadReportCount> functionCounts = workloadReportDbService.getWorkloadReportFunctionCounts(dateFrom, dateUntil, datasetCode, userNames, activityOwner, activityType);
			List<String> functionNames = functionCounts.stream().map(WorkloadReportCount::getFunctName).distinct().collect(Collectors.toList());
			for (String functionName : functionNames) {
				List<WorkloadReportUserCount> functionUserCounts = new ArrayList<>();
				for (String userName : allUserNames) {
					int userFunctionCount = functionCounts.stream()
							.filter(fc -> fc.getFunctName().equals(functionName))
							.filter(fc -> fc.getUserName().equals(userName))
							.map(WorkloadReportCount::getCount)
							.findFirst()
							.orElse(0);

					WorkloadReportUserCount functionUserCount = new WorkloadReportUserCount();
					functionUserCount.setUserName(userName);
					functionUserCount.setCount(userFunctionCount);
					functionUserCounts.add(functionUserCount);
				}

				WorkloadReportRow functionRow = new WorkloadReportRow();
				functionRow.setActivityOwner(activityOwner);
				// functionRow.setActivityEntity(todo); // TODO from first result?
				functionRow.setUserCounts(functionUserCounts);
				functionRow.setFunctName(functionName);

				functionRows.add(functionRow);
			}

			WorkloadReportRow reportRow = new WorkloadReportRow();
			reportRow.setActivityOwner(activityOwner);
			reportRow.setActivityType(activityType);
			reportRow.setTotalCount(totalCountValue);
			reportRow.setUserCounts(userCounts);
			reportRow.setFunctionRows(functionRows);

			reportRows.add(reportRow);
		}

		workloadReport.setUserNames(allUserNames);
		workloadReport.setReportRows(reportRows);
		return workloadReport;
	}
}
