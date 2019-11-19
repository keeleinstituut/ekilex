package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.imp.ParadigmWrapper;
import eki.ekilex.service.MorphologyService;
import eki.ekilex.service.UserService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class MorphologyController extends AbstractPageController {

	private static final Logger logger = LoggerFactory.getLogger(MorphologyController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private MorphologyService morphologyService;

	@GetMapping(MORPHOLOGY_URI)
	public String init() throws Exception {
		EkiUser user = userService.getAuthenticatedUser();
		if (!user.isAdmin()) {
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
