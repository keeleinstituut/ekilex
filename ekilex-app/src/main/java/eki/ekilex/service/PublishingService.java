package eki.ekilex.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.PublishingConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Publishing;
import eki.ekilex.service.db.PublishingDbService;

@Component
public class PublishingService implements PublishingConstant {

	private static Logger logger = LoggerFactory.getLogger(PublishingService.class);

	@Autowired
	private PublishingDbService publishingDbService;

	public void publish(EkiUser user, String targetName, String entityName, Long entityId, boolean isPublicOrPublish) {

		if (!ArrayUtils.contains(PUBLISHING_ENTITY_NAMES, entityName)) {
			logger.warn("Usupported publishing entity name \"{}\"", entityName);
			return;
		}

		if (StringUtils.equals(targetName, TARGET_IS_PUBLIC)) {

			if (StringUtils.equals(entityName, ENTITY_NAME_LEXEME)) {
				publishingDbService.updateLexemePublicity(entityId, isPublicOrPublish);
			} else if (StringUtils.equals(entityName, ENTITY_NAME_LEXEME_NOTE)) {
				publishingDbService.updateLexemeNotePublicity(entityId, isPublicOrPublish);
			} else if (StringUtils.equals(entityName, ENTITY_NAME_USAGE)) {
				publishingDbService.updateUsagePublicity(entityId, isPublicOrPublish);
			} else if (StringUtils.equals(entityName, ENTITY_NAME_MEANING_NOTE)) {
				publishingDbService.updateMeaningNotePublicity(entityId, isPublicOrPublish);
			} else if (StringUtils.equals(entityName, ENTITY_NAME_DEFINITION)) {
				publishingDbService.updateDefinitionPublicity(entityId, isPublicOrPublish);
			} else {
				logger.warn("Publishing entity \"{}\" doesn't appear to have publicity switch", entityName);
			}

		} else if (ArrayUtils.contains(PUBLISHING_TARGET_NAMES, targetName)) {

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

		} else {
			logger.warn("Usupported publishing target name \"{}\"", targetName);
		}

	}
}
