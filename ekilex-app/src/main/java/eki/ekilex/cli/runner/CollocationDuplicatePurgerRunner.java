package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
		Count keepCount = new Count();

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

		List<Long> totalDeleteCollocLexemeId = new ArrayList<>();

		for (String collocValue : collocValues) {

			List<Colloc> duplCollocsByValue = collocsByValueMap.get(collocValue);
			Map<String, List<Colloc>> collocsByHashMap = duplCollocsByValue.stream()
					.collect(Collectors.groupingBy(Colloc::getHash));

			for (String collocHash : collocsByHashMap.keySet()) {

				List<Colloc> duplCollocsByHash = collocsByHashMap.get(collocHash);

				if (duplCollocsByHash.size() == 1) {
					// single colloc
					singleCount.increment();
					continue;
				}

				Map<List<Long>, List<Colloc>> duplCollocMap = new HashMap<>();

				for (Colloc colloc : duplCollocsByHash) {

					Long collocLexemeId = colloc.getCollocLexemeId();
					List<String> collocMemberHashes = colloc.getCollocMemberHashes();
					List<Colloc> duplCollocs = new ArrayList<>();
					duplCollocs.add(colloc);

					for (Colloc duplCandColloc : duplCollocsByHash) {

						Long duplCandCollocLexemeId = duplCandColloc.getCollocLexemeId();
						List<String> duplCandCollocMemberHashes = duplCandColloc.getCollocMemberHashes();
						if (duplCandCollocLexemeId.equals(collocLexemeId)) {
							continue;
						}
						boolean isMemberMatch = CollectionUtils.containsAll(collocMemberHashes, duplCandCollocMemberHashes);
						if (isMemberMatch) {
							duplCollocs.add(duplCandColloc);
						}
					}

					List<Long> duplCollocLexemeIds = duplCollocs.stream()
							.map(Colloc::getCollocLexemeId)
							.sorted()
							.collect(Collectors.toList());

					// TODO remove later
					// System.out.println(collocLexemeId + " : " + duplCollocLexemeIds);

					if (duplCollocLexemeIds.size() == 1) {
						// single colloc
						singleCount.increment();
						continue;
					} else if (duplCollocMap.containsKey(duplCollocLexemeIds)) {
						continue;
					}

					duplCollocMap.put(duplCollocLexemeIds, duplCollocs);
				}

				for (List<Colloc> duplCollocs : duplCollocMap.values()) {

					Colloc bestVersionColloc = duplCollocs.stream()
							.max(Comparator.comparingInt(Colloc::getCollocMemberCount).reversed())
							.get();
					Long bestVersionCollocLexemeId = bestVersionColloc.getCollocLexemeId();
					List<Colloc> deleteDuplCollocs = duplCollocs.stream()
							.filter(colloc -> !colloc.getCollocLexemeId().equals(bestVersionCollocLexemeId))
							.collect(Collectors.toList());
					List<Long> deleteDuplCollocLexemeIds = deleteDuplCollocs.stream()
							.map(Colloc::getCollocLexemeId)
							.collect(Collectors.toList());
					totalDeleteCollocLexemeId.addAll(deleteDuplCollocLexemeIds);
					keepCount.increment();
					writeReportLine(reportWriter, bestVersionColloc, deleteDuplCollocs);

					// TODO delete
				}
			}
		}

		long uniqueDeleteCollocLexemeCount = totalDeleteCollocLexemeId.stream().distinct().count();

		reportWriter.flush();
		reportStream.flush();
		reportWriter.close();
		reportStream.close();

		logger.info("Single count: {}. Duplicate count: {}. Value count: {}. Keep count: {}. Delete count: {}",
				singleCount.getValue(), collocs.size(), collocValues.size(), keepCount.getValue(), uniqueDeleteCollocLexemeCount);

		//Single count: 45393. Duplicate count: 62684. Value count: 29186. Keep count: 17289. Delete count: 16442
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

		List<CollocMember> collocMembers = colloc.getCollocMembers();

		composeHash(colloc);
		composeHash(collocMembers);

		List<String> collocMemberHashes = collocMembers.stream()
				.map(CollocMember::getHash)
				.collect(Collectors.toList());
		colloc.setCollocMemberHashes(collocMemberHashes);
	}

	private void composeHash(Colloc colloc) {

		String collocWordValue = colloc.getCollocWordValue();
		List<String> usageValues = colloc.getUsageValues();
		boolean isWwUnif = colloc.isWwUnif();
		boolean isWwLite = colloc.isWwLite();

		String hash;

		List<Object> hashList = new ArrayList<>();
		hashList.add(collocWordValue);
		hashList.add("unif:" + isWwUnif);
		hashList.add("lite:" + isWwLite);

		if (CollectionUtils.isNotEmpty(usageValues)) {
			hash = StringUtils.join(usageValues, "-");
			hashList.add(hash);
		}

		hash = StringUtils.join(hashList, "|");
		colloc.setHash(hash);
	}

	private void composeHash(List<CollocMember> collocMembers) {

		for (CollocMember collocMember : collocMembers) {

			List<Object> hashList = new ArrayList<>();
			hashList.add(collocMember.getMemberLexemeId());
			hashList.add(collocMember.getConjunctLexemeId());
			hashList.add(collocMember.getMemberFormId());
			hashList.add(collocMember.getPosGroupCode());
			hashList.add(collocMember.getRelGroupCode());
			hashList.add(collocMember.getWeight());

			String hash = StringUtils.join(hashList, "-");
			collocMember.setHash(hash);
		}
	}
}
