package eki.ekilex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import eki.common.test.TestEnvInitialiser;
import eki.ekilex.data.EkiUser;
import eki.ekilex.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
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

		final String name = "Kusti Tähetark";
		final String email = "kusti@nowhere.com";
		EkiUser user = userService.getUserByEmail(email);

		assertNotNull("Incorrect test result", user);
		assertEquals("Incorrect test result", name, user.getName());
	}
}
