package eki.ekilex.test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.LexSearchDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
@Transactional
public class LexSearchServiceTest implements SystemConstant {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private LexSearchDbService lexSearchDbService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testSimpleSearchByHeadwordAndDatasetAndPerm() throws Exception {

		String searchFilter = "hall*";

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();
		searchDatasetsRestriction.setNoDatasetsFiltering(false);
		searchDatasetsRestriction.setAllDatasetsPermissions(false);

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		filteringDatasetCodes.add("ss_");
		filteringDatasetCodes.add("qq2");

		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();

		List<Word> words;

		// some perms
		userPermDatasetCodes.clear();
		userPermDatasetCodes.add("qq2");

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 6, words.size());

		// perms removed
		userPermDatasetCodes.clear();

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 4, words.size());

		// full perms
		userPermDatasetCodes.clear();
		searchDatasetsRestriction.setAllDatasetsPermissions(true);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 6, words.size());
	}

	@Test
	public void testSimpleSearchByHeadwordAndDataset() throws Exception {

		String searchFilter = "hall*";

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();
		searchDatasetsRestriction.setNoDatasetsFiltering(false);
		searchDatasetsRestriction.setAllDatasetsPermissions(true);

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		filteringDatasetCodes.add("ss_");
		List<Word> words;

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 2, words.size());
	}

	@Test
	public void testSearchByHeadword() throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		SearchFilter searchFilter = new SearchFilter();
		SearchCriterionGroup wordGroup = new SearchCriterionGroup();
		wordGroup.setEntity(SearchEntity.HEADWORD);
		wordGroup.setSearchCriteria(new ArrayList<>());
		searchFilter.setCriteriaGroups(asList(wordGroup));

		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;

		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 2, words.size());
	}

	@Test
	public void testSearchByWord() throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		SearchFilter searchFilter = new SearchFilter();
		SearchCriterionGroup wordGroup = new SearchCriterionGroup();
		wordGroup.setEntity(SearchEntity.WORD);
		wordGroup.setSearchCriteria(new ArrayList<>());
		searchFilter.setCriteriaGroups(asList(wordGroup));

		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;

		// case #1
		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 3, words.size());

		// case #2
		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.STARTS_WITH;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 3, words.size());

		// case #3
		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.ENDS_WITH;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 5, words.size());

		// case #4
		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("ha");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 5, words.size());

		// case #5
		wordGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("ii");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.ENDS_WITH;
		searchValue = new String("ll");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		wordGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 2, words.size());
		assertEquals("Incorrect match", "hiirjas", words.get(0).getWordValue());
	}

	@Test
	public void testSearchByForm() throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		SearchFilter searchFilter = new SearchFilter();
		SearchCriterionGroup formGroup = new SearchCriterionGroup();
		formGroup.setEntity(SearchEntity.FORM);
		formGroup.setSearchCriteria(new ArrayList<>());
		searchFilter.setCriteriaGroups(asList(formGroup));

		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		formGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("väära");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		formGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "väär", word.getWordValue());

		// case #2
		formGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("halla");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		formGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getWordValue());

		// case #3
		formGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("halli");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		formGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getWordValue());
		assertEquals("Incorrect match", Integer.valueOf(2), word.getHomonymNr());
	}

	@Test
	public void testSearchByDefinition() throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		SearchFilter searchFilter = new SearchFilter();
		SearchCriterionGroup definitionGroup = new SearchCriterionGroup();
		definitionGroup.setEntity(SearchEntity.DEFINITION);
		definitionGroup.setSearchCriteria(new ArrayList<>());
		searchFilter.setCriteriaGroups(asList(definitionGroup));

		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		definitionGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE_AND_EXISTS;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("ESIK");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		definitionGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 2, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getWordValue());
		word = words.get(1);
		assertEquals("Incorrect match", "холл", word.getWordValue());
	}

	@Test
	public void testSearchByUsage() throws Exception {

		SearchDatasetsRestriction searchDatasetsRestriction = createDefaultSearchDatasetsRestriction();

		SearchFilter searchFilter = new SearchFilter();
		SearchCriterionGroup usageGroup = new SearchCriterionGroup();
		usageGroup.setEntity(SearchEntity.USAGE);
		usageGroup.setSearchCriteria(new ArrayList<>());
		searchFilter.setCriteriaGroups(asList(usageGroup));

		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		usageGroup.getSearchCriteria().clear();
		searchKey = SearchKey.VALUE_AND_EXISTS;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("haned");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		usageGroup.getSearchCriteria().add(searchCriterion);

		words = lexSearchDbService.getWords(searchFilter, searchDatasetsRestriction, null, null, false, DEFAULT_OFFSET, DEFAULT_MAX_RESULTS_LIMIT);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getWordValue());
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
