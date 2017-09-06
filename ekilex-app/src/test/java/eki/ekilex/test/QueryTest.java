package eki.ekilex.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eki.common.service.db.BasicDbService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-service-config.xml", "classpath:db-config.xml"})
@Transactional
public class QueryTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private BasicDbService basicDbService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testQueryByWordComponents() throws Exception {

		final String sqlScriptFilePath = "./fileresources/sql/test_query_by_word_comp.sql";
		final String compPrefix = "hall%";

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("compPrefix", compPrefix);

		String sqlScript = testEnvInitialiser.getSqlScript(sqlScriptFilePath);
		List<Map<String, Object>> results = basicDbService.queryList(sqlScript, paramMap);

		List<String> resultWords = new ArrayList<>();
		String word;
		for (Map<String, Object> result : results) {
			word = result.get("word").toString();
			resultWords.add(word);
		}

		assertTrue("Incorrect query result", resultWords.contains("hall"));
		assertTrue("Incorrect query result", resultWords.contains("hallasääsk"));
		assertTrue("Incorrect query result", resultWords.contains("linnahall"));
		assertTrue("Incorrect query result", resultWords.contains("tumehall"));
	}
}
