package eki.ekilex.test;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.test.TestEnvInitialiser;
import eki.ekilex.app.EkilexApplication;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SynRelation;
import eki.ekilex.service.db.SynSearchDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
@Transactional
public class SynSearchServiceTest extends AbstractTest implements SystemConstant {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Before
	public void beforeTest() throws Exception {
		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testGetSynRelationsTuples() {
		List<SynRelation> relations = synSearchDbService.getWordSynRelations(1003L, "raw", "qq2", Collections.singletonList("est"), true, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
		assertThat(relations.size()).isEqualTo(2);
	}
}
