package eki.ekilex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-service-config.xml", "classpath:db-config.xml"})
@Transactional
public class UserServiceTest {

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Autowired
	private UserService userService;

	@Before
	public void beforeTest() throws Exception {

		testEnvInitialiser.initDatabase();
	}

	@Test
	public void testGetUserByName() throws Exception {

		final String name = "Malle Paju";
		EkiUser user = userService.getUserByName(name);

		assertNotNull("Incorrect test result", user);
		assertEquals("Incorrect test result", name, user.getName());
	}
}
