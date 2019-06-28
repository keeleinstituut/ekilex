package eki.ekilex.test;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.test.TestEnvInitialiser;
import eki.ekilex.data.SynRelation;
import eki.ekilex.data.SynRelationParamTuple;
import eki.ekilex.service.SynSearchDbService;
import eki.ekilex.service.SynSearchService;
import eki.ekilex.service.UserService;
import eki.ekilex.service.util.ConversionUtil;

//import static com.google.common.truth.Truth.;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@Transactional
public class SynSearchServiceTest extends AbstractTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private SynSearchDbService synSearchDbService;

	@Autowired
	private SynSearchService synSearchService;

	@Autowired
	private ConversionUtil conversionUtil;

	@Autowired
	private UserService userService;

	@Before
	public void beforeTest() throws Exception {
		testEnvInitialiser.initDatabase();
		initSecurity();
	}

	// @Test
	// public void testGetExistingOppositeRelation() {
	// 	Relation relation = synSearchDbService.getOppositeRelation(1003L, 1006L, "raw", "est", "descrip");
	// 	assertThat(relation).isNotNull();
	// 	assertThat(relation.getWord()).isEqualTo("hallakord");
	// }
	//
	// @Test
	// public void testGetNotExistingOppositeRelation() {
	// 	Relation relation = synSearchDbService.getOppositeRelation(1003L, 1004L, "raw", "est", "descrip");
	// 	assertThat(relation).isNull();
	// }
	//
	// @Test
	// public void testWordRelationParams() {
	// 	List<RelationParam> relationParameters = synSearchDbService.getRelationParameters(1001L);
	// 	assertThat(relationParameters.size()).isEqualTo(2);
	// 	assertThat(relationParameters.get(1).getValue()).isEqualTo("0.003");
	// }
	//
	// @Test
	// public void testSynRelationPopulation() {
	//
	// 	WordDetails details = synSearchService.getWordDetailsSynonyms(1003L, new ArrayList<>());
	//
	// 	assertThat(details).isNotNull();
	// }
	//
	@Test
	public void testGetSynRelationsTuples() {
		List<SynRelationParamTuple> paramTuples = synSearchDbService.getWordSynRelations(1003L, "raw", "est", "descrip");
		List<SynRelation> relations = conversionUtil.composeSynRelations(paramTuples);

		assertThat(paramTuples).isNotNull();
		assertThat(relations).isNotNull();
	}
}
