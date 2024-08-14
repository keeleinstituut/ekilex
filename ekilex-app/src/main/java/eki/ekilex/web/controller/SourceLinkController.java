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

	@GetMapping("/{sourceLinkContentKey}:{sourceLinkId}")
	public String sourceLinkDetails(
			@PathVariable("sourceLinkContentKey") String sourceLinkContentKey,
			@PathVariable("sourceLinkId") String sourceLinkIdStr,
			Model model) {

		logger.debug("Requested source link \"{}:{}\"", sourceLinkContentKey, sourceLinkIdStr);
		Long sourceLinkId = Long.valueOf(sourceLinkIdStr);

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkContentKey, sourceLinkId);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);

		model.addAttribute("sourceLink", sourceLink);
		model.addAttribute("source", source);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_link_details";
	}

	// TODO rename path to edit
	@GetMapping(SOURCE_AND_SOURCE_LINK_URI + "/{sourceLinkContentKey}/{sourceLinkId}")
	public String editSourceLink(
			@PathVariable("sourceLinkContentKey") String sourceLinkContentKey,
			@PathVariable("sourceLinkId") Long sourceLinkId,
			Model model) {

		logger.debug("Requested {} type source link '{}' and source", sourceLinkContentKey, sourceLinkId);

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkContentKey, sourceLinkId);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);

		model.addAttribute("sourceLink", sourceLink);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "edit_source_link_dlg";
	}
}
