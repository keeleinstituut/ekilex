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
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Source;
import eki.ekilex.data.SourceLink;
import eki.ekilex.service.SourceLinkService;
import eki.ekilex.service.SourceService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class SourceLinkController implements WebConstant {

	private static final Logger logger = LoggerFactory.getLogger(SourceLinkController.class);

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private SourceService sourceService;

	@GetMapping("/" + ContentKey.FREEFORM_SOURCE_LINK + ":{sourceLinkId}")
	public String ffSourceLink(
			@PathVariable("sourceLinkId") String sourceLinkIdStr,
			Model model) {

		logger.debug("Requested freeform source link \"{}\"", sourceLinkIdStr);
		Long sourceLinkId = Long.valueOf(sourceLinkIdStr);
		return handleSourceLink(sourceLinkId, ReferenceOwner.FREEFORM, model);
	}

	@GetMapping("/" + ContentKey.DEFINITION_SOURCE_LINK + ":{sourceLinkId}")
	public String defSourceLink(
			@PathVariable("sourceLinkId") String sourceLinkIdStr,
			Model model) {

		logger.debug("Requested definition source link \"{}\"", sourceLinkIdStr);
		Long sourceLinkId = Long.valueOf(sourceLinkIdStr);
		return handleSourceLink(sourceLinkId, ReferenceOwner.DEFINITION, model);
	}

	@GetMapping("/" + ContentKey.LEXEME_SOURCE_LINK + ":{sourceLinkId}")
	public String lexSourceLink(
			@PathVariable("sourceLinkId") String sourceLinkIdStr,
			Model model) {

		logger.debug("Requested lexeme source link \"{}\"", sourceLinkIdStr);
		Long sourceLinkId = Long.valueOf(sourceLinkIdStr);
		return handleSourceLink(sourceLinkId, ReferenceOwner.LEXEME, model);
	}

	@GetMapping(SOURCE_AND_SOURCE_LINK_URI + "/{sourceLinkContentKey}/{sourceLinkId}")
	public String getSourceAndSourceLink(
			@PathVariable("sourceLinkContentKey") String sourceLinkContentKey,
			@PathVariable("sourceLinkId") Long sourceLinkId,
			Model model) {

		logger.debug("Requested {} type source link '{}' and source", sourceLinkContentKey, sourceLinkId);

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkId, sourceLinkContentKey);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);

		model.addAttribute("sourceLink", sourceLink);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "edit_source_link_dlg";
	}

	private String handleSourceLink(Long sourceLinkId, ReferenceOwner referenceOwner, Model model) {

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkId, referenceOwner);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);
		model.addAttribute("source", source);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_link_details";
	}
}
