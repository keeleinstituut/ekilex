package eki.ekilex.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.test.TestEnvInitialiser;
import eki.ekilex.data.WordTuple;
import eki.ekilex.service.db.TermSearchDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@Transactional
public class TermSearchServiceTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private TermSearchDbService termSearchDbService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testSearchTermMeaningsByWord() throws Exception {

		String wordWithMetaCharacters = "hall*";
		List<String> datasets = new ArrayList<>();
		boolean fetchAll = true;

		Map<Long, List<WordTuple>> termMeaningsMap = termSearchDbService.findMeaningsAsMap(wordWithMetaCharacters, datasets, fetchAll);

		assertEquals("Incorrect count of matches", 20, termMeaningsMap.size());
	}
}
