package eki.ekilex.web.controller;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.ClassifierName;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.data.ClassifierLabel;
import eki.ekilex.service.ClassifierService;
import eki.ekilex.service.MaintenanceService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ClassifierController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(ClassifierController.class);

	@Autowired
	private ClassifierService classifierService;

	@Autowired
	private MaintenanceService maintenanceService;
	
	@GetMapping(CLASSIFIERS_URI)
	public String classifiers(Model model) {

		Map<ClassifierName, List<Classifier>> classifiersMap = new LinkedHashMap<>();
		EnumSet<ClassifierName> classifierNames = EnumSet.allOf(ClassifierName.class);
		classifierNames.remove(ClassifierName.LABEL_TYPE);
		classifierNames.remove(ClassifierName.DOMAIN); // TODO (origin, parent_code, parent_origin) - yogesh

		for (ClassifierName classifierName : classifierNames) {
			List<Classifier> classifiers = commonDataService.getClassifiers(classifierName);
			boolean hasLabel = classifierName.hasLabel();
			if (hasLabel && classifiers != null) {
				classifierService.applyLabelsTextAgg(classifiers);
			}
			classifiersMap.put(classifierName, classifiers);
		}

		model.addAttribute("classifiersMap", classifiersMap);

		return CLASSIFIERS_PAGE;
	}

	@GetMapping(CLASSIFIER_URI + "/{classifierName}/{classifierCode}")
	@ResponseBody
	public ClassifierFull getClassifier(@PathVariable ClassifierName classifierName, @PathVariable String classifierCode) {

		logger.debug("Fetching classifier by name {} and code {}", classifierName.name(), classifierCode);

		ClassifierFull classifier = classifierService.getClassifier(classifierName, classifierCode);
		return classifier;
	}

	@GetMapping(EMPTY_CLASSIFIER_URI + "/{classifierName}")
	@ResponseBody
	public ClassifierFull getEmptyClassifier(@PathVariable ClassifierName classifierName) {

		logger.debug("Fetching empty classifier by name {}", classifierName.name());

		ClassifierFull classifier = classifierService.getEmptyClassifier(classifierName);
		return classifier;
	}

	@PostMapping(CREATE_CLASSIFIER_AND_LABELS_URI)
	@ResponseBody
	public String createClassifierAndLabels(@RequestBody List<ClassifierLabel> classifierLabels) {

		logger.debug("Creating classifier and labels");

		boolean isSuccessful = classifierService.createClassifier(classifierLabels);
		if (isSuccessful) {
			maintenanceService.clearClassifCache();
			return RESPONSE_OK_VER1;
		}
		return "fail";
	}

	@PostMapping(CREATE_CLASSIFIER_URI)
	@ResponseBody
	public String createClassifier(@RequestParam("classifierName") String classifierName, @RequestParam("classifierCode") String classifierCode) {

		logger.debug("Creating classifier with name {} and code {}", classifierName, classifierCode);

		boolean isSuccessful = classifierService.createClassifier(classifierName, classifierCode);
		if (isSuccessful) {
			maintenanceService.clearClassifCache();
			return RESPONSE_OK_VER1;
		}
		return "fail";
	}

	@PostMapping(UPDATE_CLASSIFIER_URI)
	@ResponseBody
	public String updateClassifier(@RequestBody List<ClassifierLabel> classifierLabels) {

		logger.debug("Updating classifier");

		classifierService.updateClassifier(classifierLabels);
		maintenanceService.clearClassifCache();
		return RESPONSE_OK_VER1;
	}
}
