package eki.wordweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.wordweb.constant.WebConstant;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.DatasetHomeData;
import eki.wordweb.data.WordData;
import eki.wordweb.data.WordsData;
import eki.wordweb.service.DatasetContentService;
import eki.wordweb.web.bean.SessionBean;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class DatasetHomeController extends AbstractController {

	@Autowired
	private DatasetContentService datasetContentService;

	@GetMapping(DATASET_HOME_URI)
	public String home() {
		return REDIRECT_PREF + HOME_URI;
	}

	@GetMapping(DATASET_HOME_URI + "/{datasetCode}")
	public String home(
			@PathVariable(name = "datasetCode") String datasetCode,
			HttpServletRequest request,
			Model model) {
		DatasetHomeData datasetHomeData = datasetContentService.getDatasetHomeData(datasetCode);
		if (!datasetHomeData.isValidDataset()) {
			return REDIRECT_PREF + HOME_URI;
		}
		populateSearchModel(datasetHomeData, request, model);
		return DATASET_HOME_PAGE;
	}

	@GetMapping(DATASET_HOME_URI + "/{datasetCode}/{firstLetter}")
	public String openLetter(
			@PathVariable(name = "datasetCode") String datasetCode,
			@PathVariable(name = "firstLetter") String firstLetterStr,
			HttpServletRequest request,
			Model model) {

		DatasetHomeData datasetHomeData = datasetContentService.getDatasetHomeData(datasetCode);
		if (!datasetHomeData.isValidDataset()) {
			return REDIRECT_PREF + HOME_URI;
		}
		Character firstLetter = firstLetterStr.charAt(0);
		if (StringUtils.length(firstLetterStr) > 1) {
			return REDIRECT_PREF + webUtil.composeDatasetFirstLetterSearchUri(datasetCode, firstLetter);
		}
		List<String> datasetWords = datasetContentService.getDatasetWords(datasetCode, firstLetter);
		populateSearchModel(datasetHomeData, request, model);
		model.addAttribute("datasetWords", datasetWords);
		return DATASET_HOME_PAGE;
	}

	private void populateSearchModel(DatasetHomeData datasetHomeData, HttpServletRequest request, Model model) {

		Dataset dataset = datasetHomeData.getDataset();
		String datasetCode = dataset.getCode();

		SessionBean sessionBean = populateCommonModel(true, request, model);
		if (sessionBean.getDatasetCodes() == null) {
			List<String> datasetCodes = new ArrayList<>(Arrays.asList(datasetCode));
			sessionBean.setDatasetCodes(datasetCodes);
		}
		if (sessionBean.getDestinLangs() == null) {
			List<String> destinLangs = new ArrayList<>(Arrays.asList(DESTIN_LANG_ALL));
			sessionBean.setDestinLangs(destinLangs);
		}

		model.addAttribute("searchUri", SEARCH_URI + UNIF_URI);
		model.addAttribute("searchMode", SEARCH_MODE_DETAIL);
		model.addAttribute("searchWord", "");
		model.addAttribute("wordsData", new WordsData());
		model.addAttribute("wordData", new WordData());
		model.addAttribute("destinLangsStr", DESTIN_LANG_ALL);
		model.addAttribute("datasetCodesStr", datasetCode);
		model.addAttribute("datasetHomeData", datasetHomeData);
	}
}
