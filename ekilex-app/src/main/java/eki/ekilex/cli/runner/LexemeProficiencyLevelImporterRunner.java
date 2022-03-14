package eki.ekilex.cli.runner;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.WordLexeme;
import eki.ekilex.service.db.LexSearchDbService;

@Component
public class LexemeProficiencyLevelImporterRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(LexemeProficiencyLevelImporterRunner.class);

	@Autowired
	protected LexSearchDbService lexSearchDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String importFilePath) throws Exception {

		File importFile = new File(importFilePath);
		List<String> lexemeProficiencyLevelMappingLines = readFileLines(importFile);
		lexemeProficiencyLevelMappingLines.remove(0);//remove header

		SearchDatasetsRestriction searchDatasetsRestriction = new SearchDatasetsRestriction();
		searchDatasetsRestriction.setNoDatasetsFiltering(true);
		searchDatasetsRestriction.setAllDatasetsPermissions(true);

		int lineCounter = 0;
		for (String lexemeProficiencyLevelMappingLine : lexemeProficiencyLevelMappingLines) {

			lineCounter++;
			if (StringUtils.isBlank(lexemeProficiencyLevelMappingLine)) {
				continue;
			}
			String[] lexemeProficiencyLevelMappingCells = StringUtils.splitPreserveAllTokens(lexemeProficiencyLevelMappingLine, CSV_SEPARATOR);
			if (lexemeProficiencyLevelMappingCells.length != 3) {
				logger.warn("# {} - Incorrect line format: \"{}\"", lineCounter, lexemeProficiencyLevelMappingLine);
				continue;
			}
			String providedWordValue = lexemeProficiencyLevelMappingCells[0].trim();
			String proficiencyLevelCode = lexemeProficiencyLevelMappingCells[1].trim();
			Long wordId = Long.valueOf(lexemeProficiencyLevelMappingCells[2].trim());

			List<WordLexeme> wordLexemes = lexSearchDbService.getWordLexemes(wordId, searchDatasetsRestriction, CLASSIF_LABEL_LANG_EST, CLASSIF_LABEL_TYPE_DESCRIP);
			if (CollectionUtils.isEmpty(wordLexemes)) {
				logger.warn("# {} - \"{}\" - \"{}\" - No results for id", lineCounter, providedWordValue, wordId);
				continue;
			}
			List<String> existingWordValues = wordLexemes.stream().map(WordLexeme::getWordValue).distinct().collect(Collectors.toList());
			String existingWordValue;
			if (existingWordValues.size() > 1) {
				logger.warn("# {} - \"{}\" - \"{}\" - Multiple values exist: {}", lineCounter, providedWordValue, wordId, existingWordValues);
				continue;
			}
			existingWordValue = existingWordValues.get(0);
			if (!StringUtils.equals(providedWordValue, existingWordValue)) {
				logger.warn("# {} - \"{}\" - \"{}\" - Word value mismatch: \"{}\"", lineCounter, providedWordValue, wordId, existingWordValue);
				continue;
			}

			WordLexeme firstWordLexeme = wordLexemes.stream()
					.filter(wordLexeme -> Complexity.ANY.equals(wordLexeme.getComplexity()) || Complexity.SIMPLE.equals(wordLexeme.getComplexity()))
					.findFirst()
					.orElse(null);

			if (firstWordLexeme == null) {
				firstWordLexeme = wordLexemes.get(0);
			}

			//TODO add proficiency
		}

		logger.info("There are {} mappings", lexemeProficiencyLevelMappingLines.size());
	}
}
