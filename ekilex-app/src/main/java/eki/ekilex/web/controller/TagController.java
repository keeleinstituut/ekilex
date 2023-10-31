package eki.ekilex.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import eki.common.constant.TagType;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.Tag;
import eki.ekilex.service.TagService;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TagController extends AbstractPrivatePageController {

	@Autowired
	private TagService tagService;

	@GetMapping(TAGS_URI)
	public String tags(Model model) {

		List<Tag> tags = tagService.getTags();
		model.addAttribute("tags", tags);
		return TAGS_PAGE;
	}

	@PostMapping(CREATE_TAG_URI)
	@ResponseBody
	public String createTag(
			@RequestParam("tagName") String tagName,
			@RequestParam("tagType") TagType tagType,
			@RequestParam(name = "setAutomatically", required = false) boolean setAutomatically,
			@RequestParam(name = "removeToComplete", required = false) boolean removeToComplete) {

		boolean isSuccessful = tagService.createTag(tagName, tagType, setAutomatically, removeToComplete);
		if (isSuccessful) {
			return RESPONSE_OK_VER1;
		}
		return RESPONSE_FAIL;
	}

	@PostMapping(UPDATE_TAG_URI)
	@ResponseBody
	public String updateTag(
			@RequestParam("currentTagName") String currentTagName,
			@RequestParam("tagName") String tagName,
			@RequestParam("tagOrder") Long tagOrder,
			@RequestParam(name = "setAutomatically", required = false) boolean setAutomatically,
			@RequestParam(name = "removeToComplete", required = false) boolean removeToComplete) {

		boolean isSuccessful = tagService.updateTag(currentTagName, tagName, tagOrder, setAutomatically, removeToComplete);
		if (isSuccessful) {
			return RESPONSE_OK_VER1;
		}
		return RESPONSE_FAIL;
	}

	@PostMapping(DELETE_TAG_URI)
	@ResponseBody
	public String deleteTag(@RequestParam("tagName") String tagName) {

		tagService.deleteTag(tagName);
		return RESPONSE_OK_VER1;
	}

}
