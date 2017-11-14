package eki.eve.test.service;

import eki.common.test.TestEnvInitialiser;
import eki.eve.data.Word;
import eki.eve.service.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class SearchServiceTest {

	@Autowired
	private SearchService service;

	@Autowired
	private TestEnvInitialiser testEnvInitialiser;

	@Before
	public void beforeTest() throws Exception {
		testEnvInitialiser.initDatabase();
	}

	@Test
	public void findWords() throws Exception {

		List<Word> words = service.findWords("hall");

		assertThat(words).hasSize(2);
		assertThat(words.get(0).getValue()).isEqualTo("hall");
		assertThat(words.get(1).getValue()).isEqualTo("hall");
	}

}