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
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.CommonDataDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@Transactional
public class LexSearchServiceTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private CommonDataDbService commonDataDbService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testSearchByWord() throws Exception {

		SearchFilter searchFilter = new SearchFilter();
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		searchFilter.setSearchCriteria(searchCriteria);

		List<String> datasets = new ArrayList<>();
		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;

		// case #1
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 2, words.size());

		// case #2
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.STARTS_WITH;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 6, words.size());

		// case #3
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.ENDS_WITH;
		searchValue = new String("hall");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 7, words.size());

		// case #4
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("aha");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 2, words.size());

		// case #5
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("ii");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		searchKey = SearchKey.VALUE; //SearchKey.WORD_VALUE;;
		searchOperand = SearchOperand.ENDS_WITH;
		searchValue = new String("ll");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		assertEquals("Incorrect match", "hiirhall", words.get(0).getValue());
	}

	@Test
	public void testSearchByForm() throws Exception {

		SearchFilter searchFilter = new SearchFilter();
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		searchFilter.setSearchCriteria(searchCriteria);

		List<String> datasets = new ArrayList<>();
		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.FORM_VALUE;;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("väära");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "väär", word.getValue());

		// case #2
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.FORM_VALUE;;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("halla");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getValue());
		assertEquals("Incorrect match", new Integer(1), word.getHomonymNumber());

		// case #3
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.FORM_VALUE;;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("halli");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getValue());
		assertEquals("Incorrect match", new Integer(2), word.getHomonymNumber());
	}

	@Test
	public void testSearchByDefinition() throws Exception {

		SearchFilter searchFilter = new SearchFilter();
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		searchFilter.setSearchCriteria(searchCriteria);

		List<String> datasets = new ArrayList<>();
		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.DEFINITION_VALUE;;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("ESIK");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 2, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getValue());
		word = words.get(1);
		assertEquals("Incorrect match", "холл", word.getValue());
	}

	@Test
	public void testSearchByUsage() throws Exception {

		SearchFilter searchFilter = new SearchFilter();
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		searchFilter.setSearchCriteria(searchCriteria);

		List<String> datasets = new ArrayList<>();
		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		searchCriteria.clear();
		searchKey = SearchKey.VALUE; //SearchKey.USAGE_VALUE;
		searchOperand = SearchOperand.CONTAINS;
		searchValue = new String("haned");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "hall", word.getValue());
	}

	@Test
	public void testSearchByConceptId() throws Exception {

		SearchFilter searchFilter = new SearchFilter();
		List<SearchCriterion> searchCriteria = new ArrayList<>();
		searchFilter.setSearchCriteria(searchCriteria);

		List<String> datasets = new ArrayList<>();
		SearchCriterion searchCriterion;
		SearchKey searchKey;
		SearchOperand searchOperand;
		Object searchValue;
		List<Word> words;
		Word word;

		// case #1
		searchCriteria.clear();
		searchKey = SearchKey.ID; //.CONCEPT_ID;
		searchOperand = SearchOperand.EQUALS;
		searchValue = new String("123456");

		searchCriterion = new SearchCriterion();
		searchCriterion.setSearchKey(searchKey);
		searchCriterion.setSearchOperand(searchOperand);
		searchCriterion.setSearchValue(searchValue);
		searchCriteria.add(searchCriterion);

		words = commonDataDbService.findWords(searchFilter, datasets, false).into(Word.class);

		assertEquals("Incorrect count of matches", 1, words.size());
		word = words.get(0);
		assertEquals("Incorrect match", "tumehall", word.getValue());
	}
}
