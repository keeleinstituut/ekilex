package eki.ekilex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import eki.common.test.TestEnvInitialiser;
import eki.ekilex.app.EkilexApplication;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
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

		final String name = "Test Test";
		final String email = "test@test.com";
		EkiUser user = userService.getUserByEmail(email);

		assertNotNull("Incorrect test result", user);
		assertEquals("Incorrect test result", name, user.getName());
	}
}
