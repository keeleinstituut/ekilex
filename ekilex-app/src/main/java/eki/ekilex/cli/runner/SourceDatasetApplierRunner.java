package eki.ekilex.cli.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Source;
import eki.ekilex.data.migra.MigraSourceLink;
import eki.ekilex.data.migra.SourceLinkOwner;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.db.SourceDbService;

@Component
public class SourceDatasetApplierRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(SourceDatasetApplierRunner.class);

	private static final String DEFAULT_DATASET_CODE_ON_NO_SOURCE_LINKS = "esterm";

	@Autowired
	private SourceDbService sourceDbService;

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute() throws Exception {

		logger.info("Collecting sources and source links...");

		Count unusedSourceCount = new Count();
		Count datasetOverloadSourceCount = new Count();
		Count duplicatedSourceCount = new Count();

		List<Source> sources = migrationDbService.getSources();

		int sourceCounter = 0;
		int sourceCount = sources.size();
		int progressIndicator = sourceCount / Math.min(sourceCount, 100);

		for (Source source : sources) {

			Long sourceId = source.getId();

			List<MigraSourceLink> allSourceLinks = new ArrayList<>();

			for (SourceLinkOwner sourceLinkOwner : SourceLinkOwner.values()) {

				List<MigraSourceLink> sourceLinks = migrationDbService.getSourceLinks(sourceId, sourceLinkOwner);
				if (CollectionUtils.isNotEmpty(sourceLinks)) {
					allSourceLinks.addAll(sourceLinks);
				}
			}

			if (CollectionUtils.isEmpty(allSourceLinks)) {

				unusedSourceCount.increment();
				migrationDbService.setSourceDataset(sourceId, DEFAULT_DATASET_CODE_ON_NO_SOURCE_LINKS);
				
			} else {

				Map<String, List<MigraSourceLink>> datasetSourceLinkMap = allSourceLinks.stream()
						.collect(Collectors.groupingBy(MigraSourceLink::getDatasetCode));

				if (datasetSourceLinkMap.keySet().size() > 1) {
					datasetOverloadSourceCount.increment();
				}

				List<String> datasetCodes = new ArrayList<>(datasetSourceLinkMap.keySet());

				for (int datasetCodeIndex = 0; datasetCodeIndex < datasetCodes.size(); datasetCodeIndex++) {

					String datasetCode = datasetCodes.get(datasetCodeIndex);
					List<MigraSourceLink> datasetSourceLinks = datasetSourceLinkMap.get(datasetCode);

					if (datasetCodeIndex == 0) {
						migrationDbService.setSourceDataset(sourceId, datasetCode);
					} else {

						duplicatedSourceCount.increment();
						source.setDatasetCode(datasetCode);
						Long clonedSourceId = sourceDbService.createSource(source);
						migrationDbService.duplicateActivityLog(sourceId, clonedSourceId);

						for (MigraSourceLink sourceLink : datasetSourceLinks) {

							migrationDbService.relinkSourceLink(clonedSourceId, sourceLink);
						}
					}
				}
			}

			sourceCounter++;
			if (sourceCounter % progressIndicator == 0) {
				int progressPercent = sourceCounter / progressIndicator;
				logger.info("{}% - {} sources iterated", progressPercent, sourceCounter);
			}
		}

		logger.info("Source count: {}", sourceCount);
		logger.info("Unused source count: {}", unusedSourceCount.getValue());
		logger.info("Duplicated source count: {}", duplicatedSourceCount.getValue());
		logger.info("Dataset overload source count: {}", datasetOverloadSourceCount.getValue());
	}
}
