package eki.ekilex.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.WebConstant;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Tag;
import eki.ekilex.web.util.SearchHelper;

@ConditionalOnWebApplication
@Controller
@SessionAttributes(WebConstant.SESSION_BEAN)
public class TagController extends AbstractPrivatePageController {

	@Autowired
	private SearchHelper searchHelper;

	@GetMapping(TAGS_URI)
	public String tags(Model model) {

		List<Tag> tags = tagService.getTags();
		addSearchUris(tags);
		model.addAttribute("tags", tags);

		return TAGS_PAGE;
	}

	private void addSearchUris(List<Tag> tags) {

		List<String> selectedDatasetCodes = getUserPreferredDatasetCodes();

		for (Tag tag : tags) {

			String tagName = tag.getName();
			List<String> tagDatasetCodes = tag.getDatasetCodes();

			if (CollectionUtils.isNotEmpty(tagDatasetCodes)) {

				List<String> combinedDatasetCodes = new ArrayList<>();
				combinedDatasetCodes.addAll(selectedDatasetCodes);
				combinedDatasetCodes.addAll(tagDatasetCodes);
				combinedDatasetCodes = combinedDatasetCodes.stream().distinct().collect(Collectors.toList());
				SearchFilter searchFilter = searchHelper.createTagDetailSearchFilter(tagName);
				String detailSearchUri = searchHelper.composeSearchUri(SEARCH_MODE_DETAIL, combinedDatasetCodes, null, searchFilter, SearchResultMode.WORD, null);
				tag.setDetailSearchUri(detailSearchUri);
			}
		}
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
