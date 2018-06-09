package eki.ekilex.service;

import eki.ekilex.data.WordLexeme;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class UpdateServiceTest {

	private UpdateService updateService;

	@Before
	public void beforeTest() {
		updateService = new UpdateService(null);
	}

	@Test
	public void changeLevels_level1Up() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));
		lexemes.add(createLexeme(3L, 2, 1, 1));
		lexemes.add(createLexeme(4L, 3, 1, 1));

		updateService.changeLevels(lexemes, 3L, "up");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel1()).isEqualTo(3);
	}

	@Test
	public void changeLevels_level2Up() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));

		updateService.changeLevels(lexemes, 2L, "up");

		assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
	}

	@Test
	public void changeLevels_levelUp_noChange() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));
		lexemes.add(createLexeme(3L, 2, 1, 1));
		lexemes.add(createLexeme(4L, 2, 2, 1));

		updateService.changeLevels(lexemes, 3L, "up");

		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level1Down() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1, 1));
		lexemes.add(createLexeme(3L, 2, 2, 1));
		lexemes.add(createLexeme(4L, 3, 1, 1));

		updateService.changeLevels(lexemes, 1L, "down");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(1);
	}

	@Test
	public void changeLevels_level2Down() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));

		updateService.changeLevels(lexemes, 1L, "down");

		assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
	}

	@Test
	public void changeLevels_levelDown_noChange() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));
		lexemes.add(createLexeme(3L, 2, 1, 1));
		lexemes.add(createLexeme(4L, 2, 2, 1));

		updateService.changeLevels(lexemes, 2L, "down");

		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level1Pop_noChanges() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1, 1));
		lexemes.add(createLexeme(2L, 2, 2, 1));

		updateService.changeLevels(lexemes, 1L, "pop");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level2Pop() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1, 1));
		lexemes.add(createLexeme(3L, 2, 2, 1));
		lexemes.add(createLexeme(4L, 2, 2, 2));

		updateService.changeLevels(lexemes, 2L, "pop");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(3);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel3()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(3).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel3()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level3Pop() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 1, 2, 1));
		lexemes.add(createLexeme(3L, 1, 2, 2));
		lexemes.add(createLexeme(4L, 1, 2, 3));

		updateService.changeLevels(lexemes, 3L, "pop");

		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel3()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel2()).isEqualTo(3);
		assertThat(lexemes.get(2).getLevel3()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(3).getLevel3()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level1Push() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1, 1));
		lexemes.add(createLexeme(3L, 3, 1, 1));

		updateService.changeLevels(lexemes, 1L, "push");

		assertThat(lexemes.get(0).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(0).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel1()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
	}

	@Test
	public void changeLevels_level2Push() {
		List<WordLexeme> lexemes = new ArrayList<>();
		lexemes.add(createLexeme(1L, 1, 1, 1));
		lexemes.add(createLexeme(2L, 2, 1, 1));
		lexemes.add(createLexeme(3L, 2, 2, 1));
		lexemes.add(createLexeme(4L, 2, 3, 1));

		updateService.changeLevels(lexemes, 2L, "push");

		assertThat(lexemes.get(1).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(1).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(1).getLevel3()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(2).getLevel2()).isEqualTo(1);
		assertThat(lexemes.get(2).getLevel3()).isEqualTo(1);
		assertThat(lexemes.get(3).getLevel1()).isEqualTo(2);
		assertThat(lexemes.get(3).getLevel2()).isEqualTo(2);
		assertThat(lexemes.get(3).getLevel3()).isEqualTo(1);
	}

	private WordLexeme createLexeme(Long id, Integer level1, Integer level2, Integer level3) {
		WordLexeme wordLexeme = new WordLexeme();
		wordLexeme.setLexemeId(id);
		wordLexeme.setLevel1(level1);
		wordLexeme.setLevel2(level2);
		wordLexeme.setLevel3(level3);
		return wordLexeme;
	}
}