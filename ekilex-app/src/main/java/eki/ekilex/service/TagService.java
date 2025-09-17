package eki.ekilex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.TagType;
import eki.ekilex.data.Tag;
import eki.ekilex.service.db.TagDbService;

@Component
public class TagService {

	@Autowired
	private TagDbService tagDbService;

	@Autowired
	private MaintenanceService maintenanceService;

	@Transactional
	public Tag getTag(String tagName) {
		return tagDbService.getTag(tagName);
	}

	@Transactional
	public List<Tag> getTags() {
		return tagDbService.getTags();
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean createTag(String tagName, TagType tagType, boolean setAutomatically, boolean removeToComplete) {

		boolean tagExists = tagDbService.tagExists(tagName);
		if (tagExists) {
			return false;
		}

		tagDbService.createTag(tagName, tagType, setAutomatically, removeToComplete);
		maintenanceService.clearTagCache();

		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean updateTag(String currentTagName, String tagName, Long tagOrder, boolean setAutomatically, boolean removeToComplete) {

		Long currentOrderBy = tagDbService.getTagOrderBy(currentTagName);
		Long newOrderBy = tagDbService.getTagOrderByOrMaxOrderBy(tagOrder);

		if (!currentOrderBy.equals(newOrderBy)) {
			boolean isIncrease = newOrderBy > currentOrderBy;
			List<Long> orderByList;

			if (isIncrease) {
				orderByList = tagDbService.getTagOrderByIntervalList(currentOrderBy, newOrderBy);
				orderByList.remove(0);
				tagDbService.decreaseTagOrderBys(orderByList);
			} else {
				orderByList = tagDbService.getTagOrderByIntervalList(newOrderBy, currentOrderBy);
				orderByList.remove(orderByList.size() - 1);
				tagDbService.increaseTagOrderBys(orderByList);
			}
		}

		tagDbService.updateTag(currentTagName, tagName, setAutomatically, removeToComplete, newOrderBy);
		maintenanceService.clearTagCache();

		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteTag(String tagName) {
		tagDbService.deleteTag(tagName);
		maintenanceService.clearTagCache();
	}
}
