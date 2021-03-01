package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.api.ParadigmWrapper;
import eki.ekilex.service.api.MorphologyService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class MorphologyController extends AbstractPrivatePageController {

	@Autowired
	private MorphologyService morphologyService;

	@GetMapping(MORPHOLOGY_URI)
	public String init() throws Exception {
		boolean isUserAdmin = userContext.isUserAdmin();
		if (!isUserAdmin) {
			return "redirect:" + HOME_URI;
		}
		return MORPHOLOGY_PAGE;
	}

	@PostMapping(MORPHOLOGY_URI + "/upload")
	public String upload(@RequestParam("morphFile") MultipartFile morphFile, RedirectAttributes redirectAttributes) throws Exception {

		List<String> validationMessages = new ArrayList<>();

		if (morphFile.isEmpty()) {
			validationMessages.add("Fail puudub");
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			ParadigmWrapper paradigmWrapper;
			try {
				paradigmWrapper = objectMapper.readValue(morphFile.getBytes(), ParadigmWrapper.class);
				morphologyService.replace(paradigmWrapper);
				redirectAttributes.addFlashAttribute("loadSuccess", Boolean.TRUE);
			} catch (UnrecognizedPropertyException e) {
				validationMessages.add("Paradigmale mittevastav struktuur: " + e.getMessage());
			}
		}
		if (CollectionUtils.isNotEmpty(validationMessages)) {
			redirectAttributes.addFlashAttribute("validationMessages", validationMessages);
		}
		return "redirect:" + MORPHOLOGY_URI;
	}

}
