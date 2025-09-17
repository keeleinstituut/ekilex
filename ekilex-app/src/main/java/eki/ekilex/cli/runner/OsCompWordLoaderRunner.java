package eki.ekilex.cli.runner;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.PublishingConstant;
import eki.common.data.Count;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.Publishing;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.PublishingDbService;

@Component
public class OsCompWordLoaderRunner extends AbstractLoaderRunner implements PublishingConstant {

	private static Logger logger = LoggerFactory.getLogger(OsCompWordLoaderRunner.class);

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private PublishingDbService publishingDbService;

	@Autowired
	private ActivityLogService activityLogService;

	@Transactional(rollbackFor = Exception.class)
	public void execute(String compWordTsvFilePath) throws Exception {

		logger.info("Starting loading...");

		createSecurityContext();

		List<String> compWordTsvLines = readFileLines(compWordTsvFilePath);

		Count createCount = new Count();
		Count existCount = new Count();

		int lineCounter = 0;
		int lineCount = compWordTsvLines.size();
		int progressIndicator = lineCount / Math.min(lineCount, 100);

		for (String compWordTsvLine : compWordTsvLines) {

			if (StringUtils.isBlank(compWordTsvLine)) {
				continue;
			}
			if (StringUtils.startsWith(compWordTsvLine, "#")) {
				continue;
			}

			String[] compWordTsvCells = StringUtils.splitPreserveAllTokens(compWordTsvLine, CSV_SEPARATOR);
			Long compWordId = Long.valueOf(StringUtils.trim(compWordTsvCells[0]));
			Long preWordId = Long.valueOf(StringUtils.trim(compWordTsvCells[1]));
			Long postWordId = Long.valueOf(StringUtils.trim(compWordTsvCells[2]));

			boolean compWordExists = migrationDbService.wordExists(compWordId);
			if (!compWordExists) {
				continue;
			}

			if (preWordId.compareTo(0L) > 0) {
				boolean preWordExists = migrationDbService.wordExists(preWordId);
				if (preWordExists) {
					createRelation(compWordId, preWordId, WORD_REL_TYPE_CODE_PRECOMP, createCount, existCount);
					createRelation(preWordId, compWordId, WORD_REL_TYPE_CODE_WPRECOMP, createCount, existCount);
				}
			}

			if (postWordId.compareTo(0L) > 0) {
				boolean postWordExists = migrationDbService.wordExists(postWordId);
				if (postWordExists) {
					createRelation(compWordId, postWordId, WORD_REL_TYPE_CODE_POSTCOMP, createCount, existCount);
					createRelation(postWordId, compWordId, WORD_REL_TYPE_CODE_WPOSTCOMP, createCount, existCount);
				}
			}

			lineCounter++;
			if (lineCounter % progressIndicator == 0) {
				int progressPercent = lineCounter / progressIndicator;
				logger.info("{}% - {} lines processed", progressPercent, lineCounter);
			}
		}

		logger.info("Completed load. Out of {} lines, relation create count: {}, relation exist count: {}", lineCounter, createCount.getValue(), existCount.getValue());
	}

	private void createRelation(Long wordId1, Long wordId2, String relationTypeCode, Count createCount, Count existCount) throws Exception {

		boolean relationExists = lookupDbService.wordRelationExists(wordId1, wordId2, relationTypeCode);
		if (relationExists) {
			existCount.increment();
			return;
		}
		ActivityLogData activityLog = activityLogService.prepareActivityLog("loadWordRelations", wordId1, ActivityOwner.WORD, DATASET_EKI, MANUAL_EVENT_ON_UPDATE_ENABLED);
		Long wordRelationId = cudDbService.createWordRelation(wordId1, wordId2, relationTypeCode, null);
		createPublishing(TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_RELATION, wordRelationId);
		activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		createCount.increment();
	}

	private void createPublishing(String targetName, String entityName, Long entityId) {

		String userName = userContext.getUserName();
		LocalDateTime now = LocalDateTime.now();

		Publishing publishing = new Publishing();
		publishing.setEventBy(userName);
		publishing.setEventOn(now);
		publishing.setTargetName(targetName);
		publishing.setEntityName(entityName);
		publishing.setEntityId(entityId);

		publishingDbService.createPublishing(publishing);
	}
}
