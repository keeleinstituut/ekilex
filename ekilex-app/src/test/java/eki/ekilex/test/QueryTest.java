package eki.ekilex.test;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
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
import eki.ekilex.service.db.BasicDbService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
@Transactional
public class QueryTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private BasicDbService basicDbService;

	@Autowired
	private DSLContext mainDb;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testJooqQueryResultParsing() throws Exception {

		Long lexemeId = mainDb
				.select(LEXEME.ID)
				.from(LEXEME)
				.where(LEXEME.DATASET_CODE.eq("qq2"))
				.limit(1)
				.fetchOneInto(Long.class);

		assertNotNull("Incorrect query result", lexemeId);
	}

	@Test
	public void testQueryWordDefinitions() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_word_definitions.sql";
		final String wordPrefix = "hall%";
		final String labelLang = "est";
		final String labelType = "descrip";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("wordPrefix", wordPrefix);
		paramMap.put("labelLang", labelLang);
		paramMap.put("labelType", labelType);

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);
		int resultCount = results.size();

		assertEquals("Incorrect result count", 21, resultCount);

		String word;
		Object definition;
		int existingDefinitionCount = 0;

		for (Map<String, Object> result : results) {
			word = result.get("word").toString();
			definition = result.get("definition");
			if (definition != null) {
				existingDefinitionCount++;
			}
			if (StringUtils.equals(word, "hallasääsk")) {
				assertNull("Incorrect result", definition);
			}
		}

		assertEquals("Incorrect result count", 12, existingDefinitionCount);
	}

	@Test
	public void testQueryCompareDatasetsWords() throws Exception {

		final String sqlScriptFilePath1 = "./fileresources/sql/test_query_datasets_common_words.sql";
		final String sqlScriptFilePath2 = "./fileresources/sql/test_query_datasets_incommon_words.sql";
		final String dataset1 = "qq2";
		final String dataset2 = "ss_";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("dataset1", dataset1);
		paramMap.put("dataset2", dataset2);

		String sqlScript;
		List<Map<String, Object>> results;
		int resultCount;

		sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath1);
		results = basicDbService.queryList(sqlScript, paramMap);
		resultCount = results.size();

		assertEquals("Incorrect result count", 2, resultCount);

		sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath2);
		results = basicDbService.queryList(sqlScript, paramMap);
		resultCount = results.size();

		assertEquals("Incorrect result count", 13, resultCount);
	}

	@Test
	public void testQueryDefinitionWords() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_definition_words.sql";

		Map<String, String> paramMap = new HashMap<>();

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);
		int resultCount = results.size();

		assertEquals("Incorrect result count", 5, resultCount);

		Map<String, Integer> definitionWordsCountMap = new HashMap<>();
		Map<String, List<String>> definitionWordsMap = new HashMap<>();
		List<String> definitionWords;

		for (Map<String, Object> result : results) {
			String definition = result.get("definition").toString();
			String word = result.get("word").toString();
			definitionWords = definitionWordsMap.get(definition);
			if (definitionWords == null) {
				definitionWords = new ArrayList<>();
				definitionWordsMap.put(definition, definitionWords);
			}
			assertFalse("Incorrect query result", definitionWords.contains(word));
			definitionWords.add(word);
			Integer wordCount = definitionWordsCountMap.get(definition);
			if (wordCount == null) {
				wordCount = 1;
			} else {
				wordCount++;
			}
			definitionWordsCountMap.put(definition, wordCount);
		}

		for (Integer wordCount : definitionWordsCountMap.values()) {
			assertTrue("Incorrect result count", wordCount > 1);
		}
	}

	@Test
	public void testQueryDifferentLangMatchingMeaningWords() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_diff_lang_word_match.sql";
		final String lang1 = "est";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("lang1", lang1);

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);
		int resultCount = results.size();

		String word1, word2;
		for (Map<String, Object> result : results) {
			word1 = result.get("word1").toString();
			word2 = result.get("word2").toString();
			assertEquals("Incorrect result", "hall", word1);
			assertFalse("Incorrect result", StringUtils.equals(word1, word2));
		}

		assertEquals("Incorrect result count", 3, resultCount);
	}

	@Test
	public void testQueryWordForms() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_word_forms.sql";
		final String word = "väär";
		final String defaultLabelLang = "est";
		final String defaultLabelType = "descrip";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("word", word);
		paramMap.put("defaultLabelLang", defaultLabelLang);
		paramMap.put("defaultLabelType", defaultLabelType);

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);
		int resultCount = results.size();

		List<Long> wordIds = new ArrayList<>();
		List<Long> paradigmIds = new ArrayList<>();
		Long wordId;
		Long paradigmId;

		for (Map<String, Object> result : results) {
			wordId = Long.valueOf(result.get("word_id").toString());
			paradigmId = Long.valueOf(result.get("paradigm_id").toString());
			if (!wordIds.contains(wordId)) {
				wordIds.add(wordId);
			}
			if (!paradigmIds.contains(paradigmId)) {
				paradigmIds.add(paradigmId);
			}
		}

		assertEquals("Incorrect result count", 2, wordIds.size());
		assertEquals("Incorrect result count", 3, paradigmIds.size());
		assertEquals("Incorrect result count", 9, resultCount);
	}

	@Test
	public void testQueryWordsRelations() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_words_relations.sql";
		final String defaultLabelLang = "est";
		final String defaultLabelType = "descrip";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("defaultLabelLang", defaultLabelLang);
		paramMap.put("defaultLabelType", defaultLabelType);

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);

		Array relationsObj;
		String[] relations;
		List<String> relationsControlList;

		for (Map<String, Object> result : results) {
			relationsObj = (Array) result.get("relations");
			if (relationsObj == null) {
				continue;
			}
			relations = (String[]) relationsObj.getArray();
			relationsControlList = new ArrayList<>();
			for (String relation : relations) {
				if (!relationsControlList.contains(relation)) {
					relationsControlList.add(relation);
				}
			}
			assertTrue("Incorrect results", relationsControlList.size() == relations.length);
		}
	}
}
