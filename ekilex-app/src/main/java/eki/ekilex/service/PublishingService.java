package eki.ekilex.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PublishingConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Publishing;
import eki.ekilex.service.db.PublishingDbService;

@Component
public class PublishingService implements PublishingConstant {

	@Autowired
	private PublishingDbService publishingDbService;

	public void publish(EkiUser user, String targetName, String entityName, Long entityId, boolean isPublicOrPublish) {

		final String[] publishingTargetNames = {TARGET_NAME_WW_UNIF, TARGET_NAME_WW_LITE, TARGET_NAME_WW_OD};

		if (StringUtils.equals(targetName, TARGET_IS_PUBLIC)) {

			if (StringUtils.equals(entityName, ENTITY_NAME_LEXEME)) {
				publishingDbService.updateLexemePublicity(entityId, isPublicOrPublish);
			} else if (StringUtils.equals(entityName, ENTITY_NAME_DEFINITION)) {
				publishingDbService.updateDefinitionPublicity(entityId, isPublicOrPublish);
			}

		} else if (ArrayUtils.contains(publishingTargetNames, targetName)) {

			Long publishingId = publishingDbService.getPublishingId(targetName, entityName, entityId);
			if ((publishingId == null) && isPublicOrPublish) {

				String userName = user.getName();
				LocalDateTime now = LocalDateTime.now();

				Publishing publishing = new Publishing();
				publishing.setEventBy(userName);
				publishing.setEventOn(now);
				publishing.setTargetName(targetName);
				publishing.setEntityName(entityName);
				publishing.setEntityId(entityId);

				publishingDbService.createPublishing(publishing);

			} else if ((publishingId != null) && !isPublicOrPublish) {

				publishingDbService.deletePublishing(publishingId);
			}
		}

	}
}
