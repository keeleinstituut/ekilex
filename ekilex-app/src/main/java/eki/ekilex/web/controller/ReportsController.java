package eki.ekilex.web.controller;

import java.util.ArrayList;
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
import eki.ekilex.data.TermDatasetReportParameters;
import eki.ekilex.service.ReportService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN})
public class ReportsController extends AbstractPrivatePageController {

	@Autowired
	private ReportService reportService;

	@GetMapping(REPORTS_URI)
	public String init(Model model) {

		EkiUser user = userContext.getUser();
		if (!user.isAdmin() && !user.isDatasetCrudPermissionsExist()) {
			return REDIRECT_PREF + HOME_URI;
		}

		List<ReportType> accessibleReportTypes = getAccessibleReportTypes();
		model.addAttribute("accessibleReportTypes", accessibleReportTypes);

		return REPORTS_PAGE;
	}

	@GetMapping(REPORTS_URI + "/{reportType}")
	public String getReportsByType(@PathVariable ReportType reportType, Model model) {

		List<ReportType> accessibleReportTypes = getAccessibleReportTypes();
		if (!accessibleReportTypes.contains(reportType)) {
			return REDIRECT_PREF + HOME_URI;
		}

		List<Report> reports = reportService.getReports(reportType);

		model.addAttribute("selectedReportType", reportType);
		model.addAttribute("accessibleReportTypes", accessibleReportTypes);
		model.addAttribute("reports", reports);

		addReportTypeSpecificAttributes(reportType, model);

		return REPORTS_PAGE;
	}

	@PostMapping(REPORTS_URI + "/generate/term-dataset")
	public String generateTermDatasetReport(
			@ModelAttribute(name = "termDatasetReportParameters") TermDatasetReportParameters parameters) {

		ReportType reportType = ReportType.TERM_DATASET;
		EkiUser user = userContext.getUser();
		reportService.createReport(user, reportType, parameters);

		return REDIRECT_PREF + REPORTS_URI + "/" + reportType.name();
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

	@GetMapping(REPORTS_URI + "/delete/{reportId}")
	public String deleteReport(@PathVariable Long reportId, Model model) {

		Report report = reportService.getReport(reportId);
		ReportType reportType = report.getType();

		reportService.deleteReport(reportId);

		List<Report> reports = reportService.getReports(reportType);

		model.addAttribute("reports", reports);

		return REPORTS_PAGE + PAGE_FRAGMENT_ELEM + "reports";
	}

	private List<ReportType> getAccessibleReportTypes() {

		EkiUser user = userContext.getUser();
		List<ReportType> accessibleTypes = new ArrayList<>();

		if (user.isAdmin() || user.isDatasetCrudPermissionsExist()) {
			accessibleTypes.add(ReportType.TERM_DATASET);
		}

		return accessibleTypes;
	}

	private void addReportTypeSpecificAttributes(ReportType reportType, Model model) {
		if (reportType == ReportType.TERM_DATASET) {
			EkiUser user = userContext.getUser();
			List<Dataset> datasets = reportService.getAccessibleTermDatasets(user);
			model.addAttribute("reportDatasets", datasets);
		}
	}

}
