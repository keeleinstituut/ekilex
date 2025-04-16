package eki.ekilex.cli.runner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.data.Count;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.core.ActivityLogService;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.LookupDbService;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class OdCompWordLoaderRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(OdCompWordLoaderRunner.class);

	private static final String USER_NAME_LOADER = "Laadur";

	@Autowired
	private CudDbService cudDbService;

	@Autowired
	private MigrationDbService migrationDbService;

	@Autowired
	private LookupDbService lookupDbService;

	@Autowired
	private ActivityLogService activityLogService;

	@Transactional(rollbackOn = Exception.class)
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
					createRelation(preWordId, compWordId, WORD_REL_TYPE_CODE_HEAD, createCount, existCount);
				}
			}
			if (postWordId.compareTo(0L) > 0) {
				boolean postWordExists = migrationDbService.wordExists(postWordId);
				if (postWordExists) {
					createRelation(compWordId, postWordId, WORD_REL_TYPE_CODE_POSTCOMP, createCount, existCount);
					createRelation(postWordId, compWordId, WORD_REL_TYPE_CODE_HEAD, createCount, existCount);
				}
			}

			lineCounter++;
			if (lineCounter % progressIndicator == 0) {
				int progressPercent = lineCounter / progressIndicator;
				logger.info("{}% - {} lines iterated", progressPercent, lineCounter);
			}
		}

		logger.info("Completed load. Out of {} lines, relation create count: {}, relation exist count: {}", lineCounter, createCount.getValue(), existCount.getValue());
	}

	private void createSecurityContext() {

		EkiUser user = new EkiUser();
		user.setName(USER_NAME_LOADER);
		user.setAdmin(Boolean.TRUE);
		user.setMaster(Boolean.TRUE);
		user.setEnabled(Boolean.TRUE);

		DatasetPermission recentRole = new DatasetPermission();
		recentRole.setDatasetName(DATASET_EKI);
		recentRole.setSuperiorDataset(true);
		user.setRecentRole(recentRole);

		GrantedAuthority authority = new SimpleGrantedAuthority("import");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("cmov", user, Arrays.asList(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void createRelation(Long wordId1, Long wordId2, String relationTypeCode, Count createCount, Count existCount) throws Exception {

		ActivityLogData activityLog = activityLogService.prepareActivityLog("loadWordRelations", wordId1, ActivityOwner.WORD, DATASET_EKI, MANUAL_EVENT_ON_UPDATE_ENABLED);
		boolean relationExists = lookupDbService.wordRelationExists(wordId1, wordId2, relationTypeCode);
		if (relationExists) {
			existCount.increment();
			return;
		}
		Long wordRelationId = cudDbService.createWordRelation(wordId1, wordId2, relationTypeCode, null);
		activityLogService.createActivityLog(activityLog, wordRelationId, ActivityEntity.WORD_RELATION);
		createCount.increment();
	}

	private List<String> readFileLines(String filePath) throws Exception {
		InputStream fileInputStream = new FileInputStream(filePath);
		try {
			return IOUtils.readLines(fileInputStream, StandardCharsets.UTF_8);
		} finally {
			fileInputStream.close();
		}
	}
}
