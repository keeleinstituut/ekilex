package eki.ekilex.cli.runner;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.common.service.AbstractLoaderCommons;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Collocation;
import eki.ekilex.data.CollocationTuple;
import eki.ekilex.data.Word;
import eki.ekilex.service.db.MigrationDbService;

// under construction!
@Component
public class CollocationMoverRunner extends AbstractLoaderCommons implements SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(CollocationMoverRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	@Transactional(rollbackOn = Exception.class)
	public void execute(String importFilePath) throws Exception {

		logger.info("Collecting and moving collocations...");

		File importFile = new File(importFilePath);
		List<String> collocMemberFormMappingLines = readFileLines(importFile);
		collocMemberFormMappingLines.remove(0);//remove header
		
		List<CollocationTuple> collocMemberFormTuples = new ArrayList<>();
		
		for (String collocMemberFormMappingLine : collocMemberFormMappingLines) {
		
			if (StringUtils.isBlank(collocMemberFormMappingLine)) {
				continue;
			}
			String[] collocMemberFormMappingCells = StringUtils.splitPreserveAllTokens(collocMemberFormMappingLine, CSV_SEPARATOR);
			if (collocMemberFormMappingCells.length != 8) {
				System.out.println(collocMemberFormMappingLine + "; " + collocMemberFormMappingCells.length);
				continue;
			}
		
			Long collocId = Long.valueOf(StringUtils.trim(collocMemberFormMappingCells[0]));
			String collocValue = StringUtils.trim(collocMemberFormMappingCells[1]);
			Long collocMemberWordId = Long.valueOf(StringUtils.trim(collocMemberFormMappingCells[2]));
			String collocMemberFormValue = StringUtils.trim(collocMemberFormMappingCells[5]);
			String collocMemberMorphCode = StringUtils.trim(collocMemberFormMappingCells[7]);
		
			CollocationTuple collocMemberFormTuple = new CollocationTuple();
			collocMemberFormTuple.setCollocId(collocId);
			collocMemberFormTuple.setCollocValue(collocValue);
			collocMemberFormTuple.setCollocMemberWordId(collocMemberWordId);
			collocMemberFormTuple.setCollocMemberFormValue(collocMemberFormValue);
			collocMemberFormTuple.setCollocMemberMorphCode(collocMemberMorphCode);
		
			collocMemberFormTuples.add(collocMemberFormTuple);
		}
		
		Map<Long, List<CollocationTuple>> collocMemberFormMap = collocMemberFormTuples.stream().collect(Collectors.groupingBy(CollocationTuple::getCollocId));
		
		logger.debug("Loaded colloc form mappings file");

		collectAndLogStats();

		// handle existing collocations

		List<String> missingMembersCollocValues = new ArrayList<>();
		List<String> existingCollocValueWordsLog = new ArrayList<>();
		List<String> tooManyCollocValueWordsLog = new ArrayList<>();

		List<Long> collocIds = migrationDbService.getCollocationIds();

		for (Long collocId : collocIds) {

			Collocation collocation = migrationDbService.getCollocation(collocId);
			List<CollocationTuple> collocationAndMembers = migrationDbService.getCollocationAndMembers(collocId);
			String collocValue = collocation.getValue();

			if (CollectionUtils.isEmpty(collocationAndMembers)) {
				if (!missingMembersCollocValues.contains(collocValue)) {
					missingMembersCollocValues.add(collocValue);
				}
			} else {
				/*
				for (CollocationTuple collocationTuple : collocationAndMembers) {
					
				}
				*/
			}
			List<Word> collocValueWords = migrationDbService.getWords(collocValue);
			if (CollectionUtils.isNotEmpty(collocValueWords)) {
				for (Word word : collocValueWords) {
					String logRow = word.getWordId() + "\t" + word.getWordValue() + "\t" + StringUtils.join(word.getDatasetCodes(), ",");
					existingCollocValueWordsLog.add(logRow);

				}
				if (collocValueWords.size() > 1) {
					String logRow = collocValue + "\t" + collocValueWords.size();
					tooManyCollocValueWordsLog.add(logRow);
				}
			}
		}

		Collections.sort(missingMembersCollocValues);
		File missingMembersLogFile = new File("missing-members-colloc-values.txt");
		FileUtils.writeLines(missingMembersLogFile, "UTF-8", missingMembersCollocValues);

		File existingCollocValueWordsLogFile = new File("existing-colloc-value-words.txt");
		FileUtils.writeLines(existingCollocValueWordsLogFile, "UTF-8", existingCollocValueWordsLog);

		File tooManyCollocValueWordsLogFile = new File("toomany-colloc-value-words.txt");
		FileUtils.writeLines(tooManyCollocValueWordsLogFile, "UTF-8", tooManyCollocValueWordsLog);

		/*
		Count collocMemberSingleFormMatchCount = new Count();
		Count collocMemberMultipleFormMatchCount = new Count();
		Count collocMemberMissingFormMatchCount = new Count();
		Count collocMemberResolvedFormMatchCount = new Count();
		Count collocMemberFormMappingMissingCount = new Count();
		Count collocMemberFormMappingOverloadCount = new Count();
		
		for (Long collocId : validCollocIds) {
		
			List<CollocationTuple> collocationAndMembers = collocationDbService.getCollocationAndMembers(collocId);
		
			for (CollocationTuple collocMember : collocationAndMembers) {
		
				Long collocMemberWordId = collocMember.getCollocMemberWordId();
				String collocMemberFormValue = collocMember.getCollocMemberFormValue();
				List<Long> collocMemberFormIds = collocationDbService.getFormIds(collocMemberWordId, collocMemberFormValue, null);
				if (CollectionUtils.isEmpty(collocMemberFormIds)) {
					// missing morpho
				} else if (collocMemberFormIds.size() == 1) {
					// perfect
				} else {
					List<CollocationTuple> mappedCollocMemberForms = collocMemberFormMap.get(collocId);
					if (CollectionUtils.isEmpty(mappedCollocMemberForms)) {
						// missing any mappings for that colloc
					} else {
						Map<Long, CollocationTuple> mappedCollocMmeberFormMap = mappedCollocMemberForms.stream().collect(Collectors.toMap(CollocationTuple::getCollocMemberWordId, tuple -> tuple));
						CollocationTuple mappedCollocMemberForm = mappedCollocMmeberFormMap.get(collocMemberWordId);
						if (mappedCollocMemberForm == null) {
							// missing mapping for that member
						} else {
							String mappedCollocMemberMorphCode = mappedCollocMemberForm.getCollocMemberMorphCode();
							List<Long> mappedCollocMemberFormIds = collocationDbService.getFormIds(collocMemberWordId, collocMemberFormValue, mappedCollocMemberMorphCode);
							if (CollectionUtils.isEmpty(mappedCollocMemberFormIds)) {
								// missing morpho with that code
							} else if (mappedCollocMemberFormIds.size() == 1) {
								// perfect
							} else {
								// morpho overload. choose any one ?
							}
						}
					}
				}
			}
		}
		*/

		/*
		List<Long> collocationIds = collocationDbService.getCollocationIds();
		
		for (Long collocId : collocationIds) {
		
			List<CollocationTuple> collocationAndMembers = collocationDbService.getCollocationAndMembers(collocId);
		
			for (CollocationTuple collocMember : collocationAndMembers) {
		
				Long collocMemberWordId = collocMember.getCollocMemberWordId();
				String collocMemberFormValue = collocMember.getCollocMemberFormValue();
				List<Long> collocMemberFormIds = collocationDbService.getFormIds(collocMemberWordId, collocMemberFormValue, null);
		
				if (CollectionUtils.isEmpty(collocMemberFormIds)) {
					collocMemberMissingFormMatchCount.increment();
				} else if (collocMemberFormIds.size() == 1) {
					collocMemberSingleFormMatchCount.increment();
				} else {
					collocMemberMultipleFormMatchCount.increment();
					List<CollocationTuple> mappedCollocMemberForms = collocMemberFormMap.get(collocId);
					if (CollectionUtils.isEmpty(mappedCollocMemberForms)) {
						collocMemberFormMappingMissingCount.increment();
					} else {
						Map<Long, CollocationTuple> mappedCollocMmeberFormMap = mappedCollocMemberForms.stream().collect(Collectors.toMap(CollocationTuple::getCollocMemberWordId, tuple -> tuple));
						CollocationTuple mappedCollocMemberForm = mappedCollocMmeberFormMap.get(collocMemberWordId);
						if (mappedCollocMemberForm == null) {
							collocMemberFormMappingMissingCount.increment();
						} else {
							String mappedCollocMemberMorphCode = mappedCollocMemberForm.getCollocMemberMorphCode();
							List<Long> mappedCollocMemberFormIds = collocationDbService.getFormIds(collocMemberWordId, collocMemberFormValue, mappedCollocMemberMorphCode);
							if (CollectionUtils.isEmpty(mappedCollocMemberFormIds)) {
								collocMemberFormMappingMissingCount.increment();
							} else if (mappedCollocMemberFormIds.size() == 1) {
								collocMemberResolvedFormMatchCount.increment();
							} else {
								collocMemberFormMappingOverloadCount.increment();
							}
						}
					}
				}
			}
		}
		*/

		/*
		logger.info("colloc count: {}", collocationIds.size());
		logger.info("collocMemberSingleFormMatchCount: {}", collocMemberSingleFormMatchCount.getValue());
		logger.info("collocMemberMultipleFormMatchCount: {}", collocMemberMultipleFormMatchCount.getValue());
		logger.info("collocMemberMissingFormMatchCount: {}", collocMemberMissingFormMatchCount.getValue());
		logger.info("collocMemberResolvedFormMatchCount: {}", collocMemberResolvedFormMatchCount.getValue());
		logger.info("collocMemberFormMappingMissingCount: {}", collocMemberFormMappingMissingCount.getValue());
		logger.info("collocMemberFormMappingOverloadCount: {}", collocMemberFormMappingOverloadCount.getValue());
		*/

		logger.info("Done");
	}

	private void collectAndLogStats() throws Exception {

		// collect stats for all collocations. extract valid collocations

		File logFile = new File("colloc-mover.log");
		FileWriter logFileWriter = new FileWriter(logFile, StandardCharsets.UTF_8);

		List<Long> collocationIds = migrationDbService.getCollocationIds();
		List<String> collocationValues = migrationDbService.getCollocationValues();
		List<Long> incompleteCollocIds = new ArrayList<>();
		List<Long> duplicateCollocIds = new ArrayList<>();
		List<Long> suspiciousCollocIds = new ArrayList<>();
		List<Long> validCollocIds = new ArrayList<>();
		Count collocValueWordCount = new Count();

		for (String collocationValue : collocationValues) {

			boolean wordExists = migrationDbService.wordExists(collocationValue);
			if (wordExists) {
				collocValueWordCount.increment();
			}
			List<CollocationTuple> collocationsAndMembers = migrationDbService.getCollocationsAndMembers(collocationValue);
			Map<Long, List<CollocationTuple>> availableCollocsMap = collocationsAndMembers.stream().collect(Collectors.groupingBy(CollocationTuple::getCollocId));
			int maxMemberCount = availableCollocsMap.values().stream().mapToInt(tuples -> tuples.size()).max().orElse(0);
			if (maxMemberCount == 0) {
				logger.warn("No members exist for collocation \"{}\"", collocationValue);
				continue;
			}
			List<String> collocMemberWordIdCombinations = new ArrayList<>();
			for (Entry<Long, List<CollocationTuple>> availableCollocsEntry : availableCollocsMap.entrySet()) {
				Long collocId = availableCollocsEntry.getKey();
				List<CollocationTuple> collocMembers = availableCollocsEntry.getValue();
				List<Long> collocMemberWordIds = collocMembers.stream().map(CollocationTuple::getCollocMemberWordId).sorted().collect(Collectors.toList());
				String collocMemberWordIdCombination = StringUtils.join(collocMemberWordIds, ",");
				if ((collocMembers.size() == maxMemberCount) && (maxMemberCount < 2)) {
					suspiciousCollocIds.add(collocId);
					continue;
				} else if (collocMembers.size() < maxMemberCount) {
					incompleteCollocIds.add(collocId);
					continue;
				} else if (collocMemberWordIdCombinations.contains(collocMemberWordIdCombination)) {
					addLogRow(logFileWriter, collocMembers);
					duplicateCollocIds.add(collocId);
					continue;
				}
				collocMemberWordIdCombinations.add(collocMemberWordIdCombination);
				validCollocIds.add(collocId);
			}
		}

		logFileWriter.flush();
		logFileWriter.close();

		logger.info("Colloc count: {}", collocationIds.size());
		logger.info("Altogether distinct colloc values count: {}", collocationValues.size());
		logger.info("Incomplete colloc count: {}", incompleteCollocIds.size());
		logger.info("Suspicious colloc count: {}", suspiciousCollocIds.size());
		logger.info("Duplicate colloc count: {}", duplicateCollocIds.size());
		logger.info("Valid colloc count: {}", validCollocIds.size());
		logger.info("Colloc value word count: {}", collocValueWordCount.getValue());
	}

	private void addLogRow(FileWriter logFileWriter, List<CollocationTuple> collocMembers) throws Exception {

		StringBuffer sbuf = new StringBuffer();
		CollocationTuple colloc = collocMembers.get(0);
		sbuf.append(colloc.getCollocId());
		sbuf.append("\t");
		sbuf.append(colloc.getCollocValue());
		sbuf.append("\t");
		for (CollocationTuple tuple : collocMembers) {
			sbuf.append(tuple.getCollocMemberWordId());
			sbuf.append("\t");
			sbuf.append(tuple.getCollocMemberWordValue());
			sbuf.append("\t");
			sbuf.append(tuple.getCollocMemberFormValue());
			sbuf.append("\t");
			sbuf.append(tuple.getCollocMemberLexemeId());
			sbuf.append("\t");
			sbuf.append(tuple.getCollocMemberWeight());
		}
		sbuf.append("\n");
		logFileWriter.write(sbuf.toString());
	}
}
