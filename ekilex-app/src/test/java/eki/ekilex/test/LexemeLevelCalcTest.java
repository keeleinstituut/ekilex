package eki.ekilex.test;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import eki.ekilex.app.EkilexApplication;
import eki.ekilex.data.Lexeme;
import eki.ekilex.service.util.LexemeLevelCalcUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:test-ekilex-app.properties")
@ContextConfiguration(classes = EkilexApplication.class)
@Transactional
public class LexemeLevelCalcTest extends AbstractTest {

	@Autowired
	private LexemeLevelCalcUtil lexemeLevelCalcUtil;

	@Before
	public void beforeTest() throws Exception {

	}

	@Test
	public void changeLevels_level1Up() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2));
		lexemes.add(createLexeme(3L, 2, 1));
		lexemes.add(createLexeme(4L, 3, 1));

		lexemeLevelCalcUtil.recalculateLevels(3L, lexemes, "up");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel1()).isEqualTo(3);
	}

	@Test
	public void changeLevels_level2Up() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2));

		lexemeLevelCalcUtil.recalculateLevels(2L, lexemes, "up");

		assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
	}

	// TODO create correct tests when expected result is known

	// @Test
	// public void changeLevels_levelUp_noChange() {
	// 	List<WordLexeme> lexemes = new ArrayList<>();
	// 	lexemes.add(createLexeme(1L, 1, 1));
	// 	lexemes.add(createLexeme(2L, 1, 2));
	// 	lexemes.add(createLexeme(3L, 2, 1));
	// 	lexemes.add(createLexeme(4L, 2, 2));
	//
	// 	lexemeLevelCalcUtil.recalculateLevels(3L, lexemes, "up");
	//
	// 	assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
	// 	assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	// }

	@Test
	public void changeLevels_level1Down() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1));
		lexemes.add(createLexeme(3L, 2, 2));
		lexemes.add(createLexeme(4L, 3, 1));

		lexemeLevelCalcUtil.recalculateLevels(1L, lexemes, "down");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(1);
	}

	// @Test
	// public void changeLevels_level2Down() {
	// 	List<WordLexeme> lexemes = new ArrayList<>();
	// 	lexemes.add(createLexeme(1L, 1, 1));
	// 	lexemes.add(createLexeme(2L, 1, 2));
	//
	// 	lexemeLevelCalcUtil.recalculateLevels(1L, lexemes, "down");
	//
	// 	assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
	// 	assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
	// }

	@Test
	public void changeLevels_levelDown_noChange() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2));
		lexemes.add(createLexeme(3L, 2, 1));
		lexemes.add(createLexeme(4L, 2, 2));

		lexemeLevelCalcUtil.recalculateLevels(2L, lexemes, "down");

		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level1Pop_noChanges() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1));
		lexemes.add(createLexeme(2L, 2, 2));

		lexemeLevelCalcUtil.recalculateLevels(1L, lexemes, "pop");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	// @Test
	// public void changeLevels_level2Pop() {
	// 	List<WordLexeme> lexemes = new ArrayList<>();
	// 	lexemes.add(createLexeme(1L, 1, 1));
	// 	lexemes.add(createLexeme(2L, 2, 1));
	// 	lexemes.add(createLexeme(3L, 2, 2));
	// 	lexemes.add(createLexeme(4L, 2, 2));
	//
	// 	lexemeLevelCalcUtil.recalculateLevels(2L, lexemes, "pop");
	//
	// 	assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
	// 	assertThat(lexemes.get(1).getLevel1()).isEqualTo(3);
	// 	assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	// 	assertThat(lexemes.get(2).getLevel2()).isEqualTo(1);
	// 	assertThat(lexemes.get(3).getLevel1()).isEqualTo(2);
	// 	assertThat(lexemes.get(3).getLevel2()).isEqualTo(1);
	// }

	@Test
	public void changeLevels_level1Push() {
		List<Lexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1));
		lexemes.add(createLexeme(3L, 3, 1));

		lexemeLevelCalcUtil.recalculateLevels(1L, lexemes, "push");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	// @Test
	// public void changeLevels_level2Push() {
	// 	List<WordLexeme> lexemes = new ArrayList<>();
	// 	lexemes.add(createLexeme(1L, 1, 1));
	// 	lexemes.add(createLexeme(2L, 2, 1));
	// 	lexemes.add(createLexeme(3L, 2, 2));
	// 	lexemes.add(createLexeme(4L, 2, 3));
	//
	// 	lexemeLevelCalcUtil.recalculateLevels(2L, lexemes, "push");
	//
	// 	assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
	// 	assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
	// 	assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	// 	assertThat(lexemes.get(2).getLevel2()).isEqualTo(1);
	// 	assertThat(lexemes.get(3).getLevel1()).isEqualTo(2);
	// 	assertThat(lexemes.get(3).getLevel2()).isEqualTo(2);
	// }

	private Lexeme createLexeme(Long id, Integer level1, Integer level2) {
		Lexeme wordLexeme = new Lexeme();
		wordLexeme.setLexemeId(id);
		wordLexeme.setLevel1(level1);
		wordLexeme.setLevel2(level2);
		return wordLexeme;
	}
}