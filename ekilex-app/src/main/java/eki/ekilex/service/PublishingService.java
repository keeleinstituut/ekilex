package eki.ekilex.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.EkiUser;
import eki.ekilex.data.Publishing;
import eki.ekilex.service.db.PublishingDbService;

@Component
public class PublishingService {

	@Autowired
	private PublishingDbService publishingDbService;

	public void publish(EkiUser user, String targetName, String entityName, Long entityId, boolean isPublish) {

		Long publishingId = publishingDbService.getPublishingId(targetName, entityName, entityId);
		if ((publishingId == null) && isPublish) {

			String userName = user.getName();
			LocalDateTime now = LocalDateTime.now();

			Publishing publishing = new Publishing();
			publishing.setEventBy(userName);
			publishing.setEventOn(now);
			publishing.setTargetName(targetName);
			publishing.setEntityName(entityName);
			publishing.setEntityId(entityId);

			publishingDbService.createPublishing(publishing);

		} else if ((publishingId != null) && !isPublish) {

			publishingDbService.deletePublishing(publishingId);
		}
	}
}
