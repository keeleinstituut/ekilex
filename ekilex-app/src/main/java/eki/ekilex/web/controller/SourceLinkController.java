package eki.ekilex.web.controller;

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

	@Autowired
	private SourceLinkService sourceLinkService;

	@Autowired
	private SourceService sourceService;

	@GetMapping("/{sourceLinkContentKey}:{sourceLinkId}")
	public String sourceLinkDetails(
			@PathVariable("sourceLinkContentKey") String sourceLinkContentKey,
			@PathVariable("sourceLinkId") String sourceLinkIdStr,
			Model model) {

		Long sourceLinkId = Long.valueOf(sourceLinkIdStr);

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkContentKey, sourceLinkId);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);

		model.addAttribute("sourceLink", sourceLink);
		model.addAttribute("source", source);

		return COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "source_link_details";
	}

	@GetMapping(EDIT_SOURCE_LINK_URI + "/{sourceLinkContentKey}/{sourceLinkId}")
	public String editSourceLink(
			@PathVariable("sourceLinkContentKey") String sourceLinkContentKey,
			@PathVariable("sourceLinkId") Long sourceLinkId,
			Model model) {

		SourceLink sourceLink = sourceLinkService.getSourceLink(sourceLinkContentKey, sourceLinkId);
		Long sourceId = sourceLink.getSourceId();
		Source source = sourceService.getSource(sourceId);

		model.addAttribute("sourceLink", sourceLink);
		model.addAttribute("source", source);

		return SOURCE_COMPONENTS_PAGE + PAGE_FRAGMENT_ELEM + "edit_source_link_dlg";
	}
}
