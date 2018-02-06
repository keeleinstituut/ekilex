package eki.ekilex.service;

import eki.ekilex.data.OrderingData;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.UpdateDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UpdateService {

	@Autowired
	private UpdateDbService updateDbService;

	@Transactional
	public void updateUsageValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageTranslationValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateUsageDefinitionValue(Long id, String value) {
		updateDbService.updateFreeformTextValue(id, value);
	}

	@Transactional
	public void updateDefinitionValue(Long id, String value) {
		updateDbService.updateDefinitionValue(id, value);
	}

	@Transactional
	public void updateDefinitionOrdering(List<OrderingData> items) {
		updateDbService.updateDefinitionOrderby(items);
	}

	@Transactional
	public void updateLexemeRelationOrdering(List<OrderingData> items) {
		updateDbService.updateLexemeRelationOrderby(items);
	}

	@Transactional
	public void updateMeaningRelationOrdering(List<OrderingData> items) {
		updateDbService.updateMeaningRelationOrderby(items);
	}

	@Transactional
	public void updateWordRelationOrdering(List<OrderingData> items) {
		updateDbService.updateWordRelationOrderby(items);
	}

	@Transactional
	public void updateLexemeLevels(Long lexemeId, Integer level1, Integer level2, Integer level3) {

		if (lexemeId == null || level1 == null || level2 == null || level3 == null) return;

		List<WordLexeme> lexemes = updateDbService.findConnectedLexemes(lexemeId).into(WordLexeme.class);
		WordLexeme lexemeToChange = lexemes.stream().filter(l -> l.getLexemeId().equals(lexemeId)).findFirst().get();
		Optional<WordLexeme> lexemeWithSameLevels = lexemes.stream()
				.filter(l -> l.getLevel1().equals(level1) && l.getLevel2().equals(level2) && l.getLevel3().equals(level3)).findFirst();
		if (lexemeWithSameLevels.isPresent()) {
			updateDbService.updateLexemeLevels(lexemeWithSameLevels.get().getLexemeId(), lexemeToChange.getLevel1(), lexemeToChange.getLevel2(), lexemeToChange.getLevel3());
		}
		updateDbService.updateLexemeLevels(lexemeId, level1, level2, level3);
	}
}
