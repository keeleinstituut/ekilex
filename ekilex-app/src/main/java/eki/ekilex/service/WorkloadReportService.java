package eki.ekilex.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityOwner;
import eki.common.constant.PermConstant;
import eki.ekilex.constant.CrudType;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.WorkloadActivityReport;
import eki.ekilex.data.WorkloadFunctionReport;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.data.WorkloadReportCount;
import eki.ekilex.data.WorkloadReportUser;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.UserDbService;
import eki.ekilex.service.db.WorkloadReportDbService;
import eki.ekilex.service.util.DatasetUtil;

@Component
public class WorkloadReportService implements PermConstant {

	private static final String DATASET_NONE = "dataset-none";

	@Autowired
	private WorkloadReportDbService workloadReportDbService;

	@Autowired
	private UserDbService userDbService;

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private DatasetUtil datasetUtil;

	@Autowired
	private WorkbookService workbookService;

	@Transactional
	public List<EkiUser> getUsersByDatasetCrudPermission(List<String> datasetCodes) {

		List<EkiUser> users;
		if (datasetCodes.contains(DATASET_NONE)) {
			users = userDbService.getUsersWithAnyDatasetPermission(AUTH_OPS_CRUD);
		} else {
			users = userDbService.getUsersByDatasetPermission(datasetCodes, AUTH_OPS_CRUD);
		}
		return users;
	}

	@Transactional
	public List<Dataset> getDatasets() {
		List<Dataset> datasets = datasetDbService.getDatasets();
		datasets = datasetUtil.resortPriorityDatasets(datasets);
		return datasets;
	}

	@Transactional
	public WorkloadReport getWorkloadReport(LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, List<String> userNames) {

		boolean includeUnspecifiedDatasets = false;
		List<String> filteredDatasetCodes;
		if (datasetCodes.contains(DATASET_NONE)) {
			includeUnspecifiedDatasets = true;
			filteredDatasetCodes = datasetCodes.stream().filter(datasetCode -> !StringUtils.equals(datasetCode, DATASET_NONE)).collect(Collectors.toList());
		} else {
			filteredDatasetCodes = datasetCodes.stream().collect(Collectors.toList());
		}

		List<WorkloadReportCount> totalCounts = workloadReportDbService.getWorkloadReportTotalCounts(dateFrom, dateUntil, filteredDatasetCodes, includeUnspecifiedDatasets, userNames);
		List<WorkloadReportCount> allUserCounts = workloadReportDbService.getWorkloadReportUserCounts(dateFrom, dateUntil, filteredDatasetCodes, includeUnspecifiedDatasets, userNames);
		List<String> allUserNames = allUserCounts.stream().map(WorkloadReportCount::getUserName).distinct().sorted(String::compareToIgnoreCase).collect(Collectors.toList());

		WorkloadReport workloadReport = new WorkloadReport();
		List<WorkloadActivityReport> activityReports = new ArrayList<>();

		for (WorkloadReportCount totalCount : totalCounts) {
			ActivityOwner activityOwner = totalCount.getActivityOwner();
			CrudType activityType = totalCount.getActivityType();
			int totalCountValue = totalCount.getCount();

			List<WorkloadReportUser> activityReportUsers = new ArrayList<>();
			for (String userName : allUserNames) {
				int activityReportUserCount = allUserCounts.stream()
						.filter(uc -> uc.getUserName().equals(userName))
						.filter(uc -> uc.getActivityOwner().equals(activityOwner))
						.filter(uc -> uc.getActivityType().equals(activityType))
						.map(WorkloadReportCount::getCount)
						.findFirst()
						.orElse(0);

				WorkloadReportUser activityReportUser = new WorkloadReportUser();
				activityReportUser.setUserName(userName);
				activityReportUser.setCount(activityReportUserCount);
				activityReportUsers.add(activityReportUser);
			}

			List<WorkloadFunctionReport> functionReports = new ArrayList<>();
			List<String> functionNames = workloadReportDbService.getFunctionNames(activityOwner, activityType);
			List<WorkloadReportCount> functionCounts = workloadReportDbService.getWorkloadReportFunctionCounts(dateFrom, dateUntil, filteredDatasetCodes, includeUnspecifiedDatasets, userNames, activityOwner, activityType);
			for (String functionName : functionNames) {
				List<WorkloadReportUser> functionReportUsers = new ArrayList<>();
				for (String userName : allUserNames) {
					WorkloadReportCount userFunctionCount = functionCounts.stream()
							.filter(fc -> fc.getFunctName().equals(functionName))
							.filter(fc -> fc.getUserName().equals(userName))
							.findFirst()
							.orElse(null);

					int functionReportUserCount = 0;
					List<String> wordValues = new ArrayList<>();
					List<Long> ownerIds = new ArrayList<>();
					List<Long> lexSearchIds = new ArrayList<>();
					List<Long> termSearchIds = new ArrayList<>();

					if (userFunctionCount != null) {
						functionReportUserCount = userFunctionCount.getCount();
						if (userFunctionCount.getWordValues() != null) {
							wordValues = userFunctionCount.getWordValues();
						}
						if (userFunctionCount.getOwnerIds() != null) {
							ownerIds = userFunctionCount.getOwnerIds();
						}

						if (ActivityOwner.LEXEME.equals(activityOwner)) {
							List<Long> wordIds = userFunctionCount.getWordIds() == null ? new ArrayList<>() : userFunctionCount.getWordIds();
							List<Long> meaningIds = userFunctionCount.getMeaningIds() == null ? new ArrayList<>() : userFunctionCount.getMeaningIds();

							lexSearchIds = wordIds;
							termSearchIds = meaningIds;
						} else {
							lexSearchIds = ownerIds;
							termSearchIds = ownerIds;
						}
					}

					WorkloadReportUser functionReportUser = new WorkloadReportUser();
					functionReportUser.setUserName(userName);
					functionReportUser.setCount(functionReportUserCount);
					functionReportUser.setWordValues(wordValues);
					functionReportUser.setOwnerIds(ownerIds);
					functionReportUser.setLexSearchIds(lexSearchIds);
					functionReportUser.setTermSearchIds(termSearchIds);
					functionReportUsers.add(functionReportUser);
				}

				WorkloadFunctionReport functionReport = new WorkloadFunctionReport();
				functionReport.setFunctionReportUsers(functionReportUsers);
				functionReport.setFunctName(functionName);

				functionReports.add(functionReport);
			}

			WorkloadActivityReport activityReport = new WorkloadActivityReport();
			activityReport.setActivityOwner(activityOwner);
			activityReport.setActivityType(activityType);
			activityReport.setTotalCount(totalCountValue);
			activityReport.setActivityReportUsers(activityReportUsers);
			activityReport.setFunctionReports(functionReports);

			activityReports.add(activityReport);
		}

		int resultCount = activityReports.size();
		workloadReport.setUserNames(allUserNames);
		workloadReport.setActivityReports(activityReports);
		workloadReport.setResultCount(resultCount);
		return workloadReport;
	}

	@Transactional
	public byte[] getWorkloadReportFileBytes(LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes, List<String> userNames) throws Exception {

		WorkloadReport workloadReport = getWorkloadReport(dateFrom, dateUntil, datasetCodes, userNames);
		Workbook workbook = workbookService.toWorkbook(workloadReport, dateFrom, dateUntil, datasetCodes);

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		workbook.write(byteStream);
		byteStream.flush();
		byte[] bytes = byteStream.toByteArray();
		byteStream.close();
		return bytes;
	}
}
