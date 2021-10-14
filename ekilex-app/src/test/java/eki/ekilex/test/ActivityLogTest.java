package eki.ekilex.test;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.constant.ActivityEntity;
import eki.common.constant.ActivityOwner;
import eki.common.test.TestEnvInitialiser;
import eki.ekilex.app.EkilexApplication;
import eki.ekilex.data.ActivityLogData;
import eki.ekilex.data.TypeActivityLogDiff;
import eki.ekilex.service.ActivityLogService;
import eki.ekilex.service.db.CudDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
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
	public void testNullContentActivityLog() throws Exception {

		final Long ownerId = 123456L;

		activityLogService.createActivityLog("dummy", ownerId, ActivityOwner.LEXEME);
		activityLogService.createActivityLog("dummy", ownerId, ActivityOwner.WORD);
		activityLogService.createActivityLog("dummy", ownerId, ActivityOwner.MEANING);
		activityLogService.createActivityLog("dummy", ownerId, ActivityOwner.SOURCE);
	}

	@Test
	public void testWordActivityLog() throws Exception {

		final Long wordId = 1001L;
		final Long ownerId = Long.valueOf(wordId);
		final ActivityOwner ownerName = ActivityOwner.WORD;

		String functName;
		Long entityId;
		ActivityEntity entityName;
		ActivityLogData activityLog;
		TypeActivityLogDiff currWordTypeLogDiff;
		String modifiedWordTypeCode;

		functName = "updateWordLang";
		entityId = Long.valueOf(wordId);
		entityName = ActivityEntity.WORD;
		String originalLang = "est";
		String modifiedLang = "eng";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		cudDbService.updateWordLang(wordId, modifiedLang);
		activityLogService.createActivityLog(activityLog, entityId, entityName);

		String prevLang = activityLog.getPrevDiffs().stream().map(TypeActivityLogDiff::getValue).findFirst().get();
		String currLang = activityLog.getCurrDiffs().stream().map(TypeActivityLogDiff::getValue).findFirst().get();
		int associatedLexemeCount = activityLog.getPrevWlmIds().getLexemeIds().length;
		int associatedMeaningCount = activityLog.getPrevWlmIds().getMeaningIds().length;

		assertEquals("Unexpected update result", originalLang, prevLang);
		assertEquals("Unexpected update result", modifiedLang, currLang);
		assertEquals("Unexpected update result", 4, associatedLexemeCount);
		assertEquals("Unexpected update result", 4, associatedMeaningCount);

		functName = "createWordType";
		entityName = ActivityEntity.WORD_TYPE;
		modifiedWordTypeCode = "vv";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		entityId = cudDbService.createWordType(wordId, modifiedWordTypeCode);
		activityLogService.createActivityLog(activityLog, entityId, entityName);

		long prevWordTypeCount = activityLog.getPrevDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).count();
		currWordTypeLogDiff = activityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).findFirst().get();

		assertEquals("Unexpected update result", 0, prevWordTypeCount);
		assertEquals("Unexpected update result", "[" + modifiedWordTypeCode + "]", currWordTypeLogDiff.getValue());

		functName = "createWordType";
		entityName = ActivityEntity.WORD_TYPE;
		modifiedWordTypeCode = "rs";

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		entityId = cudDbService.createWordType(wordId, modifiedWordTypeCode);
		activityLogService.createActivityLog(activityLog, entityId, entityName);

		currWordTypeLogDiff = activityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).findFirst().get();

		assertEquals("Unexpected update result", modifiedWordTypeCode, currWordTypeLogDiff.getValue());

		functName = "deleteWordWordType";
		entityId = Long.valueOf(10001L);
		entityName = ActivityEntity.WORD_TYPE;

		activityLog = activityLogService.prepareActivityLog(functName, ownerId, ownerName);
		cudDbService.deleteWordWordType(entityId);
		activityLogService.createActivityLog(activityLog, entityId, entityName);

		long currWordTypeCount = activityLog.getCurrDiffs().stream()
				.filter(logDiff -> StringUtils.startsWith(logDiff.getPath(), "/wordTypeCodes") && !StringUtils.equals("-", logDiff.getValue())).count();

		assertEquals("Unexpected update result", 0, currWordTypeCount);
	}
}
