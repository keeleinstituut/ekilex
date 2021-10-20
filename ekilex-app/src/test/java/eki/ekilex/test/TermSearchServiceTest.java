package eki.ekilex.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

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
import eki.ekilex.constant.SearchResultMode;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.TermSearchResult;
import eki.ekilex.service.db.TermSearchDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
@Transactional
public class TermSearchServiceTest implements SystemConstant {

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

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		String wordWithMetaCharacters = "hall*";
		TermSearchResult termSearchResult;
		SearchResultMode resultMode;
		String resultLang = null;
		boolean noLimit = true;

		resultMode = SearchResultMode.MEANING;
		termSearchResult = termSearchDbService.getTermSearchResult(
				wordWithMetaCharacters, searchDatasetsRestriction, resultMode, resultLang, DEFAULT_OFFSET, noLimit);

		assertEquals("Incorrect count of matches", 20, termSearchResult.getMeaningCount());
		assertEquals("Incorrect count of matches", 9, termSearchResult.getWordCount());

		resultMode = SearchResultMode.WORD;
		termSearchResult = termSearchDbService.getTermSearchResult(
				wordWithMetaCharacters, searchDatasetsRestriction, resultMode, resultLang, DEFAULT_OFFSET, noLimit);

		assertEquals("Incorrect count of matches", 20, termSearchResult.getMeaningCount());
		assertEquals("Incorrect count of matches", 6, termSearchResult.getWordCount());
	}

	private SearchDatasetsRestriction createDefaultSearchDatasetsRestriction() {
		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		searchDatasetsRestriction.setFilteringDatasetCodes(new ArrayList<>());
		searchDatasetsRestriction.setUserPermDatasetCodes(new ArrayList<>());
		searchDatasetsRestriction.setAvailableDatasetCodes(new ArrayList<>());
		searchDatasetsRestriction.setNoDatasetsFiltering(true);
		searchDatasetsRestriction.setAllDatasetsPermissions(true);
		return searchDatasetsRestriction;
	}
}
