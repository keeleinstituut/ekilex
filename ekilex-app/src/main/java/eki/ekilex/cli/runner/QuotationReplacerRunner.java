package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import eki.ekilex.data.migra.ValueId;
import eki.ekilex.service.db.MigrationDbService;

@Component
public class QuotationReplacerRunner implements GlobalConstant, SystemConstant {

	private static Logger logger = LoggerFactory.getLogger(QuotationReplacerRunner.class);

	private static final char[] ALT_QUOTATION_CHARS = {'„', '”', '“', '″', '\''};

	private static final char DEFAULT_QUOTATION_CHAR = '"';

	private static final char REPLACE_OPEN_QUOTATION_CHAR = '„';

	private static final char REPLACE_CLOSE_QUOTATION_CHAR = '“';

	@Autowired
	private MigrationDbService migrationDbService;

	private final String reportFileName = "double-quotation-replace-report.txt";

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

		final String defaultQuotationStr = String.valueOf(DEFAULT_QUOTATION_CHAR);
		List<ValueId> valueIds;
		Count recordCount = new Count();

		valueIds = migrationDbService.getWordValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getDefinitionValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getUsageValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getWordEkiRecommendationValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getWordOsUsageValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getMeaningNoteValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getLexemeNoteValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getMeaningForumValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getLearnerCommentValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getGrammarValues(defaultQuotationStr);
		handleValues(valueIds, recordCount, reportWriter);
		valueIds = migrationDbService.getSourceValues(defaultQuotationStr);
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
		valueIds = valueIds.stream()
				.filter(valueId -> StringUtils.equals(LANGUAGE_CODE_EST, valueId.getLang()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(valueIds)) {
			return;
		}

		Count matchCount = new Count();
		String tableName = null;

		recordCount.increment(valueIds.size());

		for (ValueId valueId : valueIds) {

			tableName = valueId.getTableName();
			Long id = valueId.getId();
			String value = valueId.getValue();
			String valuePrese = valueId.getValuePrese();

			boolean containsAltQuotationChars = StringUtils.containsAny(value, ALT_QUOTATION_CHARS);
			if (containsAltQuotationChars) {
				continue;
			}
			int defaultQuotationCharCount = StringUtils.countMatches(value, DEFAULT_QUOTATION_CHAR);
			if (defaultQuotationCharCount == 2) {
				matchCount.increment();
				String newValue = replaceQuotation(value);
				String newValuePrese = replaceQuotation(valuePrese);
				migrationDbService.updateValue(tableName, id, newValue, newValuePrese);
				if (makeReport) {
					String reportLine = tableName + "\t" + id + "\t" + value + "\t" + newValue + "\t" + newValuePrese + "\n";
					reportWriter.write(reportLine);
				}
			}
		}

		logger.info("{} quoted values: {}, matching quoatations: {}", tableName, valueIds.size(), matchCount.getValue());
	}

	private String replaceQuotation(String value) {

		StringBuffer valueBuf = new StringBuffer();
		char[] valueChars = value.toCharArray();
		boolean isPairOpen = false;
		for (char ch : valueChars) {
			if (ch == DEFAULT_QUOTATION_CHAR) {
				if (isPairOpen) {
					valueBuf.append(REPLACE_CLOSE_QUOTATION_CHAR);
					isPairOpen = false;
				} else {
					valueBuf.append(REPLACE_OPEN_QUOTATION_CHAR);
					isPairOpen = true;
				}
			} else {
				valueBuf.append(ch);
			}
		}
		String newValue = valueBuf.toString();
		return newValue;
	}
}
