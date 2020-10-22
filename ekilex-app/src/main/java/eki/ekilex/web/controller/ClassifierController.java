package eki.ekilex.web.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.data.ClassifierLabel;
import eki.ekilex.service.ClassifierService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ClassifierController extends AbstractPageController {

	@Autowired
	private ClassifierService classifierService;

	@GetMapping({
			CLASSIFIERS_URI,
			CLASSIFIERS_URI + "/{classifierName}",
			CLASSIFIERS_URI + "/{classifierName}/{domainOriginCode}"})
	public String classifiers(
			@PathVariable(name = "classifierName", required = false) ClassifierName classifierName,
			@PathVariable(name = "domainOriginCode", required = false) String domainOriginCode,
			Model model) {

		List<String> allClassifierNames = classifierService.getEditableClassifierNames();
		List<String> allDomainOriginCodes = classifierService.getDomainOriginCodes();

		model.addAttribute("allClassifierNames", allClassifierNames);
		model.addAttribute("allDomainOriginCodes", allDomainOriginCodes);

		if (classifierName == null) {
			return CLASSIFIERS_PAGE;
		}

		List<ClassifierFull> classifiers = classifierService.getClassifiers(classifierName, domainOriginCode);

		model.addAttribute("classifiers", classifiers);
		model.addAttribute("classifierName", classifierName.name());
		model.addAttribute("hasLabel", classifierName.hasLabel());
		model.addAttribute("domainOriginCode", domainOriginCode);

		return CLASSIFIERS_PAGE;
	}

	@PostMapping(CREATE_CLASSIFIER_URI)
	@ResponseBody
	public String createClassifier(
			@RequestParam("classifierName") String classifierName,
			@RequestParam("classifierCode") String classifierCode,
			@RequestParam(name = "domainOriginCode", required = false) String domainOriginCode) {

		boolean isSuccessful;
		if (StringUtils.isNotBlank(domainOriginCode)) {
			isSuccessful = classifierService.createDomainClassifier(domainOriginCode, classifierCode);
		} else {
			isSuccessful = classifierService.createClassifier(classifierName, classifierCode);
		}
		if (isSuccessful) {
			return RESPONSE_OK_VER1;
		}
		return "fail";
	}

	@PostMapping(UPDATE_CLASSIFIER_URI)
	@ResponseBody
	public String updateClassifier(@RequestBody List<ClassifierLabel> classifierLabels) {

		classifierService.updateClassifier(classifierLabels);
		return RESPONSE_OK_VER1;
	}
}
