package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.ReportType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Report;
import eki.ekilex.data.ReportParameters;
import eki.ekilex.data.SynWorkReportParameters;
import eki.ekilex.data.TermDatasetReportParameters;
import eki.ekilex.service.QueueService;
import eki.ekilex.service.ReportService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN})
public class ReportsController extends AbstractPrivatePageController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private QueueService queueService;

	@GetMapping(REPORTS_URI)
	public String init(Model model) {

		EkiUser user = userContext.getUser();
		if (!user.isAdmin() && !user.isDatasetCrudPermissionsExist()) {
			return REDIRECT_PREF + HOME_URI;
		}

		List<ReportType> accessibleReportTypes = reportService.getAccessibleReportTypes(user);
		model.addAttribute("accessibleReportTypes", accessibleReportTypes);

		return REPORTS_PAGE;
	}

	@GetMapping(REPORTS_URI + "/{reportType}")
	public String getReportsByType(@PathVariable ReportType reportType, Model model) {

		EkiUser user = userContext.getUser();

		List<ReportType> accessibleReportTypes = reportService.getAccessibleReportTypes(user);
		if (!accessibleReportTypes.contains(reportType)) {
			return REDIRECT_PREF + HOME_URI;
		}

		List<Report> reports = reportService.getReports(reportType, user);
		int queueItemCount = queueService.getQueueItemCount();

		model.addAttribute("selectedReportType", reportType);
		model.addAttribute("accessibleReportTypes", accessibleReportTypes);
		model.addAttribute("reports", reports);
		model.addAttribute("queueItemCount", queueItemCount);
		model.addAttribute("reportRetentionDays", DELETE_REPORTS_OLDER_THAN_DAYS);

		addReportTypeSpecificAttributes(reportType, model);

		return REPORTS_PAGE;
	}

	@PostMapping(REPORTS_URI + "/generate/syn-work")
	public String generateSynWorkReport(
			@ModelAttribute(name = "synWorkReportParameters") SynWorkReportParameters parameters) {

		return generateReport(ReportType.SYN_WORK, parameters);
	}

	@PostMapping(REPORTS_URI + "/generate/term-dataset")
	public String generateTermDatasetReport(
			@ModelAttribute(name = "termDatasetReportParameters") TermDatasetReportParameters parameters) {

		return generateReport(ReportType.TERM_DATASET, parameters);
	}

	@GetMapping(REPORTS_URI + DOWNLOAD_URI + "/{reportId}")
	public ResponseEntity<byte[]> downloadReport(@PathVariable Long reportId) throws Exception {

		byte[] fileBytes = reportService.getReportFileBytes(reportId);
		String filename = "report-" + reportId + ".xlsx";

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.body(fileBytes);
	}

	private void addReportTypeSpecificAttributes(ReportType reportType, Model model) {
		if (reportType == ReportType.TERM_DATASET) {
			EkiUser user = userContext.getUser();
			List<Dataset> datasets = reportService.getAccessibleTermDatasets(user);
			model.addAttribute("reportDatasets", datasets);
		}
	}

	private String generateReport(ReportType reportType, ReportParameters parameters) {

		EkiUser user = userContext.getUser();
		reportService.createReport(user, reportType, parameters);

		return REDIRECT_PREF + REPORTS_URI + "/" + reportType.name();
	}

}
