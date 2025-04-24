package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.ActivityOwner;
import eki.common.constant.ClassifierName;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.service.ClassifierService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class ClassifierController extends AbstractPrivatePageController {

	@Autowired
	private ClassifierService classifierService;

	@PreAuthorize("principal.admin")
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

	@PreAuthorize("principal.admin")
	@PostMapping(CREATE_CLASSIFIER_URI)
	@ResponseBody
	public String createClassifier(@RequestBody ClassifierFull classifier) {

		boolean isSuccessful = classifierService.createClassifier(classifier);
		if (isSuccessful) {
			return RESPONSE_OK_VER1;
		}
		return RESPONSE_FAIL;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(UPDATE_CLASSIFIER_URI)
	@ResponseBody
	public String updateClassifier(@RequestBody ClassifierFull classifier) {

		classifierService.updateClassifier(classifier);
		return RESPONSE_OK_VER1;
	}

	@PreAuthorize("principal.admin")
	@PostMapping(DELETE_CLASSIFIER_URI)
	@ResponseBody
	public String deleteClassifier(@RequestBody ClassifierFull classifier) {

		boolean isSuccessful = classifierService.deleteClassifier(classifier);
		if (isSuccessful) {
			return RESPONSE_OK_VER1;
		}
		return RESPONSE_FAIL;
	}

	@GetMapping(OPPOSITE_RELATIONS_URI)
	@ResponseBody
	public List<Classifier> getOppositeRelations(
			@RequestParam("entity") ActivityOwner owner,
			@RequestParam("relationType") String relationTypeCode) {
		return lookupService.getOppositeRelations(owner, relationTypeCode);
	}
}
