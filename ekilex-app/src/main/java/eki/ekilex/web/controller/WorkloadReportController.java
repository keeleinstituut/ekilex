package eki.ekilex.web.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.service.WorkloadReportService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes({WebConstant.SESSION_BEAN})
public class WorkloadReportController extends AbstractPrivatePageController {

	@Autowired
	private WorkloadReportService workloadReportService;

	@GetMapping(WORKLOAD_REPORT_URI)
	public String init(Model model) {

		EkiUser user = userContext.getUser();
		if (!user.isAdmin()) {
			return REDIRECT_PREF + HOME_URI;
		}

		List<Dataset> workloadReportDatasets = workloadReportService.getDatasets();
		model.addAttribute("workloadReportDatasets", workloadReportDatasets);

		return WORKLOAD_REPORT_PAGE;
	}

	@GetMapping(WORKLOAD_REPORT_URI + "/dataset_users/{datasetCodes}")
	public String getDatasetUsers(@PathVariable List<String> datasetCodes, Model model) {

		List<EkiUser> datasetUsers = workloadReportService.getUsersByDatasetPermission(datasetCodes);
		model.addAttribute("datasetUsers", datasetUsers);

		return WORKLOAD_REPORT_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "dataset_users_select";
	}

	@PostMapping(WORKLOAD_REPORT_URI + SEARCH_URI)
	public String search(
			@RequestParam("dateFrom") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateFrom,
			@RequestParam("dateUntil") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateUntil,
			@RequestParam("datasetCodes") List<String> datasetCodes,
			@RequestParam(name = "userNames", required = false) List<String> userNames,
			Model model) {

		WorkloadReport workloadReport = workloadReportService.getWorkloadReport(dateFrom, dateUntil, datasetCodes, userNames);
		List<Dataset> workloadReportDatasets = workloadReportService.getDatasets();
		List<EkiUser> datasetUsers = workloadReportService.getUsersByDatasetPermission(datasetCodes);

		if (userNames == null) {
			userNames = new ArrayList<>();
		}

		model.addAttribute("dateFrom", dateFrom);
		model.addAttribute("dateUntil", dateUntil);
		model.addAttribute("datasetCodes", datasetCodes);
		model.addAttribute("userNames", userNames);

		model.addAttribute("workloadReport", workloadReport);
		model.addAttribute("workloadReportDatasets", workloadReportDatasets);
		model.addAttribute("datasetUsers", datasetUsers);

		return WORKLOAD_REPORT_PAGE;
	}
}
