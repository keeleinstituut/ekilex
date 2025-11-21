package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import eki.ekilex.data.migra.ValueId;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class QuotationReplacerRunner implements GlobalConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(QuotationReplacerRunner.class);

	@Autowired
	private MigrationDbService migrationDbService;

	//private final String reportFileName = "odd-num-of-quotation-report.txt";
	private final String reportFileName = "more-than-2-quotation-report.txt";

	private boolean makeReport = false;

	@Transactional(rollbackFor = Exception.class)
	public void execute() throws Exception {

		logger.info("Replacing quotation marks in all texts...");

		FileOutputStream reportStream = null;
		OutputStreamWriter reportWriter = null;
		if (makeReport) {
			reportStream = new FileOutputStream(reportFileName);
			reportWriter = new OutputStreamWriter(reportStream, StandardCharsets.UTF_8);
		}

		final String usualQuotationMark = "\"";
		List<ValueId> valueIds;
		Count recordCount = new Count();

		valueIds = migrationDbService.getWordValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getDefinitionValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getUsageValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getWordEkiRecommendationValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getWordOsUsageValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getMeaningNoteValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getLexemeNoteValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getMeaningForumValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getLearnerCommentValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getGrammarValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getSourceValues(usualQuotationMark);
		handleValues(valueIds, recordCount, reportWriter);

		if (makeReport) {
			reportWriter.flush();
			reportStream.flush();
			reportWriter.close();
			reportStream.close();
		}

		logger.info("Altogether handled {} records", recordCount.getValue());
	}

	private void handleValues(List<ValueId> valueIds, Count recordCount, OutputStreamWriter reportWriter) throws Exception {

		if (CollectionUtils.isEmpty(valueIds)) {
			return;
		}

		final char[] quotationAltChars = {'"', '„', '”', '“', '″'};

		Count singleCount = new Count();
		Count doubleCount = new Count();
		Count overDoubleCount = new Count();
		String entityName = null;

		recordCount.increment(valueIds.size());

		for (ValueId valueId : valueIds) {

			entityName = valueId.getEntityName();
			String value = valueId.getValue();
			int quotationCount = 0;
			for (char quotationAltChar : quotationAltChars) {
				int quotationAltCount = StringUtils.countMatches(value, quotationAltChar);
				quotationCount = quotationCount + quotationAltCount;
			}
			if (quotationCount == 1) {
				singleCount.increment();
			} else if (quotationCount == 2) {
				doubleCount.increment();
			} else if (quotationCount > 2) {
				overDoubleCount.increment();
				if (makeReport) {
					writeReportLine(valueId, reportWriter);
				}
			}
		}

		logger.info("{} quoted values: {}, single quotations: {}, double quoatations: {}, overloaded quotatations: {}",
				entityName, valueIds.size(), singleCount.getValue(), doubleCount.getValue(), overDoubleCount.getValue());

		/*
		word quoted values: 549, single quotations: 10, double quoatations: 498, overloaded quotatations: 41
		definition quoted values: 1157, single quotations: 12, double quoatations: 940, overloaded quotatations: 205
		usage quoted values: 3485, single quotations: 52, double quoatations: 2810, overloaded quotatations: 623
		word_eki_recommendation quoted values: 285, single quotations: 0, double quoatations: 185, overloaded quotatations: 100
		meaning_note quoted values: 2944, single quotations: 14, double quoatations: 1749, overloaded quotatations: 1181
		lexeme_note quoted values: 2776, single quotations: 7, double quoatations: 1606, overloaded quotatations: 1163
		meaning_forum quoted values: 1090, single quotations: 2, double quoatations: 471, overloaded quotatations: 617
		grammar quoted values: 8, single quotations: 0, double quoatations: 4, overloaded quotatations: 4
		source quoted values: 535, single quotations: 23, double quoatations: 448, overloaded quotatations: 64
		Altogether handled 12829 records
		*/
	}

	private void writeReportLine(ValueId valueId, OutputStreamWriter reportWriter) throws Exception {

		String entityName = valueId.getEntityName();
		Long id = valueId.getId();
		String value = valueId.getValue();
		String reportLine = entityName + "\t" + id + "\t" + value + "\n";
		reportWriter.write(reportLine);
	}
}
