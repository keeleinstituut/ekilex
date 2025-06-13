package eki.ekilex.cli.runner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.constant.PublishingConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.migra.DefinitionDuplicate;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.db.PublishingDbService;

@Component
public class DefinitionPurgerRunner implements GlobalConstant, LoaderConstant, SystemConstant, PublishingConstant {

	private static Logger logger = LoggerFactory.getLogger(DefinitionPurgerRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private PublishingDbService publishingDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute() throws Exception {

		logger.info("Starting purge...");

		List<DefinitionDuplicate> definitionDuplicates = migrationDbService.getDefinitionDuplicates(DATASET_EKI);
		int reassignCount = 0;
		int deleteCount = 0;

		Map<Long, List<DefinitionDuplicate>> meaningDefinitionMap = definitionDuplicates.stream()
				.collect(Collectors.groupingBy(DefinitionDuplicate::getMeaningId));

		Set<Long> meaningIds = meaningDefinitionMap.keySet();

		int articleCounter = 0;
		int articleCount = meaningIds.size();
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Long meaningId : meaningIds) {

			List<DefinitionDuplicate> meaningDefinitions = meaningDefinitionMap.get(meaningId);
			DefinitionDuplicate firstDefinition = meaningDefinitions.remove(0);
			Long primaryDefinitionId = firstDefinition.getDefinitionId();

			for (DefinitionDuplicate definitionDuplicate : meaningDefinitions) {

				Long duplicateDefinitionId = definitionDuplicate.getDefinitionId();
				Long publishingId = definitionDuplicate.getPublishingId();
				String targetName = definitionDuplicate.getTargetName();

				Long existingPublishingId = publishingDbService.getPublishingId(targetName, ENTITY_NAME_DEFINITION, primaryDefinitionId);
				if (existingPublishingId == null) {
					migrationDbService.updatePublishingEntityId(publishingId, primaryDefinitionId);
					reassignCount++;
				} else {
					publishingDbService.deletePublishing(publishingId);
				}
				cudDbService.deleteDefinition(duplicateDefinitionId);
				deleteCount++;
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				int progressPercent = articleCounter / progressIndicator;
				logger.info("{}% - {} articles processed", progressPercent, articleCounter);
			}
		}

		logger.info("Purge complete. Duplicate definition reassign count: {}; delete count: {}", reassignCount, deleteCount);
	}

}
