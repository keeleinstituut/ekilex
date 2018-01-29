package eki.ekilex.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceOwner;
import eki.common.constant.ReferenceType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.RefLink;
import eki.ekilex.data.Source;
import eki.ekilex.service.RefLinkService;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class RefLinkController {

	private static final Logger logger = LoggerFactory.getLogger(RefLinkController.class);

	@Autowired
	private RefLinkService refLinkService;

	@Autowired
	private SourceService sourceService;

	@GetMapping("/" + ContentKey.FREEFORM_REF_LINK + ":{refLinkId}")
	public String ffRefLink(@PathVariable("refLinkId") String refLinkIdStr, Model model) {

		logger.debug("Requested freeform ref link \"{}\"", refLinkIdStr);

		Long refLinkId = Long.valueOf(refLinkIdStr);

		return handleRefLink(refLinkId, ReferenceOwner.FREEFORM, model);
	}

	@GetMapping("/" + ContentKey.DEFINITION_REF_LINK + ":{refLinkId}")
	public String defRefLink(@PathVariable("refLinkId") String refLinkIdStr, Model model) {

		logger.debug("Requested definition ref link \"{}\"", refLinkIdStr);

		Long refLinkId = Long.valueOf(refLinkIdStr);

		return handleRefLink(refLinkId, ReferenceOwner.DEFINITION, model);
	}

	private String handleRefLink(Long refLinkId, ReferenceOwner referenceOwner, Model model) {

		RefLink refLink = refLinkService.getRefLink(refLinkId, referenceOwner);

		ReferenceType refType = refLink.getRefType();
		Long refId = refLink.getRefId();

		if (ReferenceType.SOURCE.equals(refType)) {
			Source source = sourceService.getSource(refId);
			model.addAttribute("source", source);
		} else {
			//TODO other type of handling...
		}

		return "sourceview";
	}
}
