package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.GlobalConstant;
import eki.common.data.Count;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.migra.Colloc;
import eki.ekilex.data.migra.CollocMember;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class CollocationDuplicatePurgerRunner implements GlobalConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(CollocationDuplicatePurgerRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	@Autowired
	private CudDbService cudDbService;

	@Transactional(rollbackFor = Exception.class)
	public void execute() throws Exception {

		logger.info("Starting purge...");

		Count singleCount = new Count();
		Count fullMatchCount = new Count();
		Count subMatchCount = new Count();
		Count discardCount = new Count();
		Count keepCount = new Count();
		Count deleteCount = new Count();

		final String reportFileName = "report.txt";
		FileOutputStream reportStream = new FileOutputStream(reportFileName);
		OutputStreamWriter reportWriter = new OutputStreamWriter(reportStream, StandardCharsets.UTF_8);
		writeReportHeader(reportWriter);

		List<Colloc> collocs = migrationDbService.getCollocationsWithDuplicates();
		applyHash(collocs);
		Map<String, List<Colloc>> collocsByValueMap = collocs.stream()
				.collect(Collectors.groupingBy(Colloc::getCollocWordValue));
		List<String> collocValues = new ArrayList<>(collocsByValueMap.keySet());
		Collections.sort(collocValues);

		for (String collocValue : collocValues) {

			List<Colloc> duplCollocsByValue = collocsByValueMap.get(collocValue);
			Map<String, List<Colloc>> collocsByHashMap = duplCollocsByValue.stream()
					.collect(Collectors.groupingBy(Colloc::getCollocHash));

			for (String collocHash : collocsByHashMap.keySet()) {

				List<Colloc> duplCollocsByHash = collocsByHashMap.get(collocHash);
				List<String> collocMembersHashes = duplCollocsByHash.stream().map(Colloc::getMembersHash)
						.distinct()
						.sorted(Comparator.comparingInt(String::length).reversed())
						.collect(Collectors.toList());
				String mostCompletecollocMembersHash = collocMembersHashes.get(0);

				if (duplCollocsByHash.size() == 1) {
					// single colloc
					singleCount.increment();
					continue;
				}

				Colloc bestVersionColloc = null;
				List<Colloc> deleteDuplCollocs = new ArrayList<>();

				for (Colloc colloc : duplCollocsByHash) {

					String collocMembersHash = colloc.getMembersHash();
					if (StringUtils.equals(mostCompletecollocMembersHash, collocMembersHash)) {
						// full match
						if (bestVersionColloc == null) {
							bestVersionColloc = colloc;
						} else {
							deleteDuplCollocs.add(colloc);
						}
						fullMatchCount.increment();
					} else if (StringUtils.contains(mostCompletecollocMembersHash, collocMembersHash)) {
						// subcontaining
						deleteDuplCollocs.add(colloc);
						subMatchCount.increment();
					} else {
						// completely off
						discardCount.increment();
					}
				}

				if (CollectionUtils.isEmpty(deleteDuplCollocs)) {
					// nothing to delete
					continue;
				}

				keepCount.increment();
				deleteCount.increment(deleteDuplCollocs.size());

				writeReportLine(reportWriter, bestVersionColloc, deleteDuplCollocs);

				// TODO delete
			}
		}

		reportWriter.flush();
		reportStream.flush();
		reportWriter.close();
		reportStream.close();

		logger.info("Single count: {}. Full match count: {}. Submatch count: {}. Discard count: {}",
				singleCount.getValue(), fullMatchCount.getValue(), subMatchCount.getValue(), discardCount.getValue());
		logger.info("Duplicate count: {}. Value count: {}. Keep count: {}. Delete count: {}",
				collocs.size(), collocValues.size(), keepCount.getValue(), deleteCount.getValue());

		/*
		Single count: 5053. Full match count: 27002. Submatch count: 16005. Discard count: 14624
		Duplicate count: 62684. Value count: 29186. Keep count: 15664. Delete count: 16005
		*/
	}

	private void writeReportHeader(OutputStreamWriter reportWriter) throws Exception {

		String headerLine = "colloc_word_value\tcolloc_word_id\tcolloc_lexeme_id\tdelete_colloc_lexeme_ids\n";
		reportWriter.write(headerLine);
	}

	private void writeReportLine(
			OutputStreamWriter reportWriter,
			Colloc bestVersionColloc,
			List<Colloc> deleteDuplCollocs) throws Exception {

		List<Long> deleteDuplCollocLexemeIds = deleteDuplCollocs.stream()
				.map(Colloc::getCollocLexemeId)
				.collect(Collectors.toList());
		String collocWordValue = bestVersionColloc.getCollocWordValue();
		Long collocWordId = bestVersionColloc.getCollocWordId();
		Long collocLexemeId = bestVersionColloc.getCollocLexemeId();
		String deleteDuplCollocLexemeIdsStr = StringUtils.join(deleteDuplCollocLexemeIds, ", ");
		String reportLine = collocWordValue + "\t" + collocWordId + "\t" + collocLexemeId + "\t" + deleteDuplCollocLexemeIdsStr + "\n";
		reportWriter.write(reportLine);
	}

	private void applyHash(List<Colloc> collocs) {

		for (Colloc colloc : collocs) {
			applyHash(colloc);
		}
	}

	private void applyHash(Colloc colloc) {

		String collocWordValue = colloc.getCollocWordValue();
		List<String> usageValues = colloc.getUsageValues();
		List<CollocMember> collocMembers = colloc.getCollocMembers();

		List<Object> hashList;
		List<Object> subHashList;
		String hash;

		hashList = new ArrayList<>();
		hashList.add(collocWordValue);

		if (CollectionUtils.isNotEmpty(usageValues)) {
			hash = StringUtils.join(usageValues, "-");
			hashList.add(hash);
		}

		hash = StringUtils.join(hashList, "|");
		colloc.setCollocHash(hash);

		hashList = new ArrayList<>();

		for (CollocMember collocMember : collocMembers) {

			subHashList = new ArrayList<>();
			subHashList.add(collocMember.getMemberLexemeId());
			subHashList.add(collocMember.getConjunctLexemeId());
			subHashList.add(collocMember.getMemberFormId());
			subHashList.add(collocMember.getPosGroupCode());
			subHashList.add(collocMember.getRelGroupCode());
			subHashList.add(collocMember.getWeight());

			hash = StringUtils.join(subHashList, "-");
			hashList.add(hash);
		}

		hash = StringUtils.join(hashList, "|");
		colloc.setMembersHash(hash);
	}
}
