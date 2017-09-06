package eki.ekilex.test;

import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eki.common.service.db.BasicDbService;

//TODO under construction!!

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
	public void testQueryWord() throws Exception {

		assertTrue(true);
	}
}
