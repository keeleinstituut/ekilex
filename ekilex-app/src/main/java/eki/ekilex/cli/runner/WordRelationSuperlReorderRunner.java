package eki.ekilex.cli.runner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.constant.PublishingConstant;
import eki.common.data.Count;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.migra.Word;
import eki.ekilex.data.migra.WordRelation;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class WordRelationSuperlReorderRunner implements GlobalConstant, LoaderConstant, SystemConstant, PublishingConstant {

	private static Logger logger = LoggerFactory.getLogger(WordRelationSuperlReorderRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	@Autowired
	private CudDbService cudDbService;

	@Transactional(rollbackFor = Exception.class)
	public void execute() throws Exception {

		logger.info("Starting \"superl\" word relation re-ordering...");

		final String relatedWordValuePrefix = "Kõige";
		List<WordRelation> wordRelations = migrationDbService.getWordRelationsByContaingPrefix(WORD_REL_TYPE_CODE_SUPERLATIVE, relatedWordValuePrefix);

		Map<Long, String> wordIdValueMap = wordRelations.stream()
				.map(rel -> new Word(rel.getWord1Id(), rel.getWord1Value()))
				.distinct()
				.collect(Collectors.toMap(Word::getId, Word::getValue));

		Map<Long, List<WordRelation>> wordRelOwnerMap = wordRelations.stream()
				.collect(Collectors.groupingBy(WordRelation::getWord1Id));

		Count updateCount = new Count();

		String reportFilename = "report.txt";
		File reportFile = new File(reportFilename);
		FileOutputStream fos = new FileOutputStream(reportFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		BufferedWriter writer = new BufferedWriter(osw);

		for (Long wordId : wordRelOwnerMap.keySet()) {

			String ownerValue = wordIdValueMap.get(wordId);
			List<WordRelation> ownerRelations = wordRelOwnerMap.get(wordId);

			if (ownerRelations.size() == 1) {
				continue;
			}

			List<WordRelation> sortedRelations = ownerRelations.stream()
					.sorted(new Comparator<WordRelation>() {

						@Override
						public int compare(WordRelation r1, WordRelation r2) {
							String relatedWordValue1 = r1.getWord2Value();
							String relatedWordValue2 = r2.getWord2Value();
							if (StringUtils.startsWithIgnoreCase(relatedWordValue1, relatedWordValuePrefix)) {
								if (StringUtils.startsWithIgnoreCase(relatedWordValue2, relatedWordValuePrefix)) {
									return 0;
								} else {
									return -1;
								}
							} else if (StringUtils.startsWithIgnoreCase(relatedWordValue2, relatedWordValuePrefix)) {
								if (StringUtils.startsWithIgnoreCase(relatedWordValue1, relatedWordValuePrefix)) {
									return 0;
								} else {
									return 1;
								}
							}
							return 0;
						}
					})
					.collect(Collectors.toList());

			List<Long> sortedOrderBys = sortedRelations.stream()
					.map(WordRelation::getOrderBy)
					.sorted()
					.collect(Collectors.toList());

			boolean reorderExists = false;

			for (int relIndex = 0; relIndex < sortedRelations.size(); relIndex++) {

				WordRelation sortedRelation = sortedRelations.get(relIndex);
				Long wordRelationId = sortedRelation.getId();
				Long existingOrderBy = sortedRelation.getOrderBy();
				Long suggestedOrderBy = sortedOrderBys.get(relIndex);

				if (existingOrderBy.compareTo(suggestedOrderBy) != 0) {
					cudDbService.updateWordRelationOrderBy(wordRelationId, suggestedOrderBy);
					reorderExists = true;
					updateCount.increment();
				}
			}

			if (reorderExists) {

				String existingRelValuesStr = ownerRelations.stream().map(WordRelation::getWord2Value).collect(Collectors.joining(", "));
				String sortedRelValuesStr = sortedRelations.stream().map(WordRelation::getWord2Value).collect(Collectors.joining(", "));

				StringBuffer sbuf = new StringBuffer();
				sbuf.append(wordId);
				sbuf.append('\t');
				sbuf.append(ownerValue);
				sbuf.append('\t');
				sbuf.append(existingRelValuesStr);
				sbuf.append('\t');
				sbuf.append(sortedRelValuesStr);
				sbuf.append('\n');
				writer.write(sbuf.toString());
			}
		}

		writer.flush();
		writer.close();
		osw.close();
		fos.close();

		logger.info("Out of {} relations of {} words {} relation reorder updates were made",
				wordRelations.size(), wordRelOwnerMap.size(), updateCount.getValue());

		logger.info("Word relation re-ordering complete. Results are in file \"{}\"", reportFilename);
	}

}
