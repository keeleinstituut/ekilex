package eki.ekilex.service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.DatasetType;
import eki.common.constant.PermConstant;
import eki.ekilex.constant.QueueAction;
import eki.ekilex.constant.ReportStatus;
import eki.ekilex.constant.ReportType;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.QueueItem;
import eki.ekilex.data.Report;
import eki.ekilex.data.ReportContent;
import eki.ekilex.data.ReportParameters;
import eki.ekilex.data.ReportQueueContent;
import eki.ekilex.data.TermDatasetReportContent;
import eki.ekilex.service.db.DatasetDbService;
import eki.ekilex.service.db.ReportDbService;

@Component
public class ReportService implements PermConstant {

	private static final List<String> TERM_DATASET_REPORT_EXCLUDED_DATASETS = Arrays.asList(
			"kce", "eki", "ety", "gal", "iht_200915", "ing", "konstr", "üliõpsõnad", "linguae", "les", "neen", "p3m_vana",
			"vrk", "default", "vkk-amet", "vke", "ÕS2025");

	@Autowired
	private ReportDbService reportDbService;

	@Autowired
	private QueueService queueService;

	@Autowired
	private DatasetDbService datasetDbService;

	@Autowired
	private WorkbookService workbookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Transactional
	public List<Report> getReports(ReportType reportType) {

		List<Report> reports = reportDbService.getReports(reportType);

		reports.forEach(report -> {

			ReportStatus status = report.getStatus();
			boolean pending = status == ReportStatus.PENDING;
			boolean completed = status == ReportStatus.COMPLETED;
			boolean deletable = status == ReportStatus.COMPLETED || status == ReportStatus.FAILED;

			report.setPending(pending);
			report.setCompleted(completed);
			report.setDeletable(deletable);
		});

		return reports;
	}

	@Transactional
	public Report getReport(Long id) {
		Report report = reportDbService.getReport(id);
		return report;
	}

	@Transactional(rollbackFor = Exception.class)
	public void createReport(EkiUser ekiUser, ReportType reportType, ReportParameters parameters) {

		Long reportId = reportDbService.createReport(reportType, ekiUser.getId());

		ReportQueueContent queueContent = new ReportQueueContent();
		queueContent.setReportId(reportId);
		queueContent.setType(reportType);
		queueContent.setParameters(parameters);

		QueueAction queueAction = QueueAction.REPORT;
		String groupId = queueAction.name() + " " + reportType.name();

		QueueItem queueItem = new QueueItem();
		queueItem.setGroupId(groupId);
		queueItem.setAction(queueAction);
		queueItem.setUser(ekiUser);
		queueItem.setContent(queueContent);

		queueService.queue(queueItem);
	}

	public ReportContent deserializeContent(Report report) throws Exception {

		ReportType reportType = report.getType();

		ReportContent content = switch (reportType) {
		case TERM_DATASET -> objectMapper.readValue(report.getContent(), TermDatasetReportContent.class);
		default -> throw new IllegalArgumentException("Content not implemented for report type: " + report.getType());
		};

		return content;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteReport(Long reportId) {
		reportDbService.deleteReport(reportId);
	}

	@Transactional
	public byte[] getReportFileBytes(Long reportId) throws Exception {

		Report report = reportDbService.getReport(reportId);
		ReportContent content = deserializeContent(report);

		Workbook workbook = switch (report.getType()) {
		case TERM_DATASET -> workbookService.toTermDatasetWorkbook((TermDatasetReportContent) content);
		default -> throw new IllegalArgumentException("File download not implemented for report type: " + report.getType());
		};

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		workbook.write(byteStream);
		workbook.close();
		return byteStream.toByteArray();
	}

	public List<Dataset> getAccessibleTermDatasets(EkiUser user) {

		List<Dataset> allDatasets = datasetDbService.getDatasets();

		List<String> userAccessibleDatasetCodes = user.getDatasetPermissions().stream()
				.filter(permission -> AUTH_OPS_CRUD.contains(permission.getAuthOperation().name()))
				.map(DatasetPermission::getDatasetCode)
				.toList();

		List<Dataset> accessibleDatasets = allDatasets.stream()
				.filter(dataset -> dataset.getType() == DatasetType.TERM)
				.filter(dataset -> !TERM_DATASET_REPORT_EXCLUDED_DATASETS.contains(dataset.getCode()))
				.filter(dataset -> user.isAdmin() || userAccessibleDatasetCodes.contains(dataset.getCode()))
				.toList();

		return accessibleDatasets;
	}
}
