package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eki.ekilex.data.Dataset;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.QueueItem;
import eki.ekilex.data.QueueStat;
import eki.ekilex.service.DatasetService;
import eki.ekilex.service.FedTermUploadService;
import eki.ekilex.service.QueueService;

@ConditionalOnWebApplication
@Controller
public class FedTermUploadController extends AbstractPrivatePageController {

	@Value("${fedterm.browser.url:null}")
	private String fedTermBrowserUrl;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private FedTermUploadService fedTermUploadService;

	@Autowired
	private QueueService queueService;

	@GetMapping(FEDTERM_UPLOAD_URI)
	public String initPage(Model model) {

		boolean isFedTermAccessEnabled = fedTermUploadService.isFedTermAccessEnabled();
		List<Dataset> datasets = datasetService.getDatasets();
		List<QueueStat> queueStats = queueService.getQueueStats();
		model.addAttribute("isFedTermAccessEnabled", isFedTermAccessEnabled);
		model.addAttribute("fedTermBrowserUrl", fedTermBrowserUrl);
		model.addAttribute("datasets", datasets);
		model.addAttribute("queueStats", queueStats);

		return FEDTERM_UPLOAD_PAGE;
	}

	@PostMapping(value = FEDTERM_UPLOAD_URI, params = "upload")
	public String uploadDataset(@RequestParam("datasetCode") String datasetCode) throws Exception {

		EkiUser user = userContext.getUser();
		List<QueueItem> fedTermUploadQueueSteps = fedTermUploadService.composeFedTermUploadQueueSteps(user, datasetCode);
		queueService.queue(fedTermUploadQueueSteps);

		return REDIRECT_PREF + FEDTERM_UPLOAD_URI;
	}

	@PostMapping(value = FEDTERM_UPLOAD_URI, params = "delete")
	public String deleteDataset(@RequestParam("datasetCode") String datasetCode) throws Exception {

		fedTermUploadService.deleteFedTermCollection(datasetCode);

		return REDIRECT_PREF + FEDTERM_UPLOAD_URI;		
	}
}
