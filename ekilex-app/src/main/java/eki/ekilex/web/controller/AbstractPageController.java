package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.Dataset;
import eki.ekilex.service.CommonDataService;

public abstract class AbstractPageController implements WebConstant {

	@Autowired
	protected CommonDataService commonDataService;

	@ModelAttribute("allDatasets")
	public List<Dataset> getAllDatasets() {
		return commonDataService.getDatasets();
	}

	@ModelAttribute("allLanguages")
	public List<Classifier> getAllLanguages() {
		return commonDataService.getLanguages();
	}

}
