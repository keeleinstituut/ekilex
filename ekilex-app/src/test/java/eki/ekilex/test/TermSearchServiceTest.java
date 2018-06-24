package eki.ekilex.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import eki.ekilex.data.TermMeaningWordTuple;
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
		String resultLang = null;
		boolean fetchAll = true;

		List<TermMeaningWordTuple> termMeanings = termSearchDbService.findMeanings(wordWithMetaCharacters, datasets, resultLang, fetchAll);

		assertEquals("Incorrect count of matches", 20, termMeanings.size());
	}
}
