package eki.ekilex.test;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.constant.ActivityEntity;
import eki.common.constant.LifecycleLogOwner;
import eki.common.test.TestEnvInitialiser;
import eki.ekilex.data.ActivityLog;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.TypeActivityLogDiff;
import eki.ekilex.service.ActivityLogService;
import eki.ekilex.service.db.CudDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@Transactional
public class ActivityLogTest extends AbstractTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private CudDbService cudDbService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
		initSecurity();
	}

	@Test
	public void testWordActivityLog() throws Exception {

		final Long wordId = 1001L;
		final Long ownerId = new Long(wordId);
		final LifecycleLogOwner ownerName = LifecycleLogOwner.WORD;

		String functName;
		Long entityId;
		ActivityEntity entityName;
		ActivityLogData activityLog;
		ActivityLog commitedActivityLog;
		TypeActivityLogDiff currWordTypeLogDiff;
		String modifiedWordTypeCode;

		functName = "updateWordLang";
		entityId = new Long(wordId);
		entityName = ActivityEntity.WORD;
		String originalLang = "est";
		String modifiedLang = "eng";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		cudDbService.updateWordLang(wordId, modifiedLang);
		commitedActivityLog = activityLogService.createActivityLog(activityLog, entityId, entityName);

		String prevLang = commitedActivityLog.getPrevDiffs().stream().map(TypeActivityLogDiff::getValue).findFirst().get();
		String currLang = commitedActivityLog.getCurrDiffs().stream().map(TypeActivityLogDiff::getValue).findFirst().get();
		int associatedLexemeCount = activityLog.getPrevWlmIds().getLexemeIds().size();
		int associatedMeaningCount = activityLog.getPrevWlmIds().getMeaningIds().size();

		assertEquals("Unexpected update result", originalLang, prevLang);
		assertEquals("Unexpected update result", modifiedLang, currLang);
		assertEquals("Unexpected update result", 4, associatedLexemeCount);
		assertEquals("Unexpected update result", 4, associatedMeaningCount);

		functName = "createWordType";
		entityName = ActivityEntity.WORD_TYPE;
		modifiedWordTypeCode = "vv";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		entityId = cudDbService.createWordType(wordId, modifiedWordTypeCode);
		commitedActivityLog = activityLogService.createActivityLog(activityLog, entityId, entityName);

		long prevWordTypeCount = commitedActivityLog.getPrevDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/word/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).count();
		currWordTypeLogDiff = commitedActivityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/word/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).findFirst().get();

		assertEquals("Unexpected update result", 0, prevWordTypeCount);
		assertEquals("Unexpected update result", "[" + modifiedWordTypeCode + "]", currWordTypeLogDiff.getValue());

		functName = "createWordType";
		entityName = ActivityEntity.WORD_TYPE;
		modifiedWordTypeCode = "rs";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		entityId = cudDbService.createWordType(wordId, modifiedWordTypeCode);
		commitedActivityLog = activityLogService.createActivityLog(activityLog, entityId, entityName);

		currWordTypeLogDiff = commitedActivityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/word/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).findFirst().get();

		assertEquals("Unexpected update result", modifiedWordTypeCode, currWordTypeLogDiff.getValue());

		functName = "deleteWordWordType";
		entityId = new Long(10001L);
		entityName = ActivityEntity.WORD_TYPE;

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		cudDbService.deleteWordWordType(entityId);
		commitedActivityLog = activityLogService.createActivityLog(activityLog, entityId, entityName);

		long currWordTypeCount = commitedActivityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/word/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).count();

		assertEquals("Unexpected update result", 0, currWordTypeCount);
	}
}
