package eki.ekilex.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.ekilex.constant.ReportType;
import eki.ekilex.data.Dataset;
import eki.ekilex.data.ReportParameters;
import eki.ekilex.data.SynWorkReportContent;
import eki.ekilex.data.SynWorkReportParameters;
import eki.ekilex.data.SynWorkReportUserContribution;
import eki.ekilex.data.TermDatasetReportContent;
import eki.ekilex.data.TermDatasetReportParameters;
import eki.ekilex.data.TermDatasetReportRow;
import eki.ekilex.service.db.ReportDbService;
import eki.ekilex.service.db.SynWorkReportDbService;
import eki.ekilex.service.db.TermDatasetReportDbService;

@Component
public class ReportGenerationService {

	private static final Logger logger = LoggerFactory.getLogger(ReportGenerationService.class);

	@Autowired
	private ReportDbService reportDbService;

	@Autowired
	private SynWorkReportDbService synWorkReportDbService;

	@Autowired
	private TermDatasetReportDbService termDatasetReportDbService;

	@Autowired
	private ObjectMapper objectMapper;

	@Transactional(rollbackFor = Exception.class)
	public void generate(Long reportId, ReportType reportType, ReportParameters parameters) {

		try {
			String content = buildContent(reportType, parameters);
			reportDbService.updateReportCompleted(reportId, content);
		} catch (Exception e) {
			logger.error("Report generation failed for report with ID \"{}\"", reportId, e);
			reportDbService.updateReportFailed(reportId);
		}
	}

	private String buildContent(ReportType reportType, ReportParameters parameters) throws Exception {

		String content = switch (reportType) {
		case TERM_DATASET -> buildTermDatasetContent(parameters);
		case SYN_WORK -> buildSynWorkContent(parameters);
		default -> throw new IllegalArgumentException("Report generation not implemented for type: " + reportType);
		};

		return content;
	}

	private String buildSynWorkContent(ReportParameters parameters) throws Exception {

		SynWorkReportParameters reportParameters = (SynWorkReportParameters) parameters;

		LocalDateTime from = reportParameters.getDateFrom().atStartOfDay();
		LocalDateTime until = reportParameters.getDateUntil().plusDays(1).atStartOfDay();

		List<SynWorkReportUserContribution> userContributions = synWorkReportDbService.getUserContributions(from, until);

		SynWorkReportContent content = new SynWorkReportContent();
		content.setParameters(reportParameters);
		content.setUserContributions(userContributions);

		String contentJson = objectMapper.writeValueAsString(content);
		return contentJson;
	}

	private String buildTermDatasetContent(ReportParameters parameters) throws Exception {

		TermDatasetReportParameters reportParameters = (TermDatasetReportParameters) parameters;

		List<String> datasetCodes = reportParameters.getDatasetCodes();
		LocalDateTime from = reportParameters.getDateFrom().atStartOfDay();
		LocalDateTime until = reportParameters.getDateUntil().plusDays(1).atStartOfDay();

		List<Dataset> datasets = termDatasetReportDbService.getDatasets(datasetCodes);
		Map<String, Integer> publicMeaningCounts = termDatasetReportDbService.getPublicMeaningCounts(datasetCodes);
		Map<String, Integer> allMeaningCounts = termDatasetReportDbService.getAllMeaningCounts(datasetCodes);
		Map<String, Integer> publicTermCounts = termDatasetReportDbService.getPublicTermCounts(datasetCodes);
		Map<String, Integer> allTermCounts = termDatasetReportDbService.getAllTermCounts(datasetCodes);
		Map<String, Integer> createMeaningCounts = termDatasetReportDbService.getCreateMeaningCounts(datasetCodes, from, until);
		Map<String, Integer> updateMeaningCounts = termDatasetReportDbService.getUpdateMeaningCounts(datasetCodes, from, until);
		Map<String, Integer> withDomainMeaningCounts = termDatasetReportDbService.getWithDomainMeaningCounts(datasetCodes);
		Map<String, Integer> withDomainUpdateMeaningCounts = termDatasetReportDbService.getWithDomainUpdateMeaningCounts(datasetCodes, from, until);
		Map<String, String> withoutDomainTermSamples = termDatasetReportDbService.getWithoutDomainTermSamples(datasetCodes);

		Map<String, Integer> singleTermMeaningCounts = termDatasetReportDbService.getSingleTermMeaningCounts(datasetCodes);
		Map<String, String> singleTermMeaningTermSamples = termDatasetReportDbService.getSingleTermMeaningTermSamples(datasetCodes);
		Map<String, Integer> singleLangMeaningCounts = termDatasetReportDbService.getSingleLangMeaningCounts(datasetCodes);
		Map<String, String> singleLangMeaningTermSamples = termDatasetReportDbService.getSingleLangMeaningTermSamples(datasetCodes);
		Map<String, Integer> specificCharTermCounts = termDatasetReportDbService.getSpecificCharTermCounts(datasetCodes);
		Map<String, String> specificCharTermSamples = termDatasetReportDbService.getSpecificCharTermSamples(datasetCodes);
		Map<String, Integer> initialCapTermCounts = termDatasetReportDbService.getInitialCapTermCounts(datasetCodes);
		Map<String, String> initialCapTermSamples = termDatasetReportDbService.getInitialCapTermSamples(datasetCodes);
		Map<String, Integer> withSourceLinkTermCounts = termDatasetReportDbService.getWithSourceLinkTermCounts(datasetCodes);
		Map<String, Integer> withSourceLinkMeaningUpdateTermCounts = termDatasetReportDbService.getWithSourceLinkMeaningUpdateTermCounts(datasetCodes, from, until);
		Map<String, Integer> withoutSourceLinkTermCounts = termDatasetReportDbService.getWithoutSourceLinkTermCounts(datasetCodes);
		Map<String, Integer> withoutSourceLinkMeaningUpdateTermCounts = termDatasetReportDbService.getWithoutSourceLinkMeaningUpdateTermCounts(datasetCodes, from, until);
		Map<String, String> withoutSourceLinkMeaningUpdateTermSamples = termDatasetReportDbService.getWithoutSourceLinkMeaningUpdateTermSamples(datasetCodes, from, until);

		Map<String, Integer> withDefinitionMeaningCounts = termDatasetReportDbService.getWithDefinitionMeaningCounts(datasetCodes);
		Map<String, Integer> withDefinitionUpdateMeaningCounts = termDatasetReportDbService.getWithDefinitionUpdateMeaningCounts(datasetCodes, from, until);
		Map<String, String> withoutDefinitionMeaningTermSamples = termDatasetReportDbService.getWithoutDefinitionMeaningTermSamples(datasetCodes);
		Map<String, String> withoutDefinitionUpdateMeaningTermSamples = termDatasetReportDbService
				.getWithoutDefinitionUpdateMeaningTermSamples(datasetCodes, from, until);
		Map<String, Integer> withPunctuationDefinitionCounts = termDatasetReportDbService.getWithPunctuationDefinitionCounts(datasetCodes);
		Map<String, String> withPunctuationDefinitionTermSamples = termDatasetReportDbService.getWithPunctuationDefinitionTermSamples(datasetCodes);
		Map<String, Integer> initialCapDefinitionCounts = termDatasetReportDbService.getInitialCapDefinitionCounts(datasetCodes);
		Map<String, String> initialCapDefinitionTermSamples = termDatasetReportDbService.getInitialCapDefinitionTermSamples(datasetCodes);
		Map<String, Integer> initialEnumerationDefinitionCounts = termDatasetReportDbService.getInitialEnumerationDefinitionCounts(datasetCodes);
		Map<String, String> initialEnumerationDefinitionTermSamples = termDatasetReportDbService.getInitialEnumerationDefinitionTermSamples(datasetCodes);
		Map<String, Integer> withSourceLinkDefinitionCounts = termDatasetReportDbService.getWithSourceLinkDefinitionCounts(datasetCodes);
		Map<String, Integer> withSourceLinkDefinitionMeaningUpdateDefinitionCounts = termDatasetReportDbService
				.getWithSourceLinkDefinitionMeaningUpdateDefinitionCounts(datasetCodes, from, until);
		Map<String, Integer> allDefinitionCounts = termDatasetReportDbService.getAllDefinitionCounts(datasetCodes);

		Map<String, Integer> withUsageMeaningCounts = termDatasetReportDbService.getWithUsageMeaningCounts(datasetCodes);
		Map<String, Integer> withSourceLinkUsageCounts = termDatasetReportDbService.getWithSourceLinkUsageCounts(datasetCodes);
		Map<String, Integer> withSourceLinkUsageMeaningUpdateUsageCounts = termDatasetReportDbService
				.getWithSourceLinkUsageMeaningUpdateUsageCounts(datasetCodes, from, until);
		Map<String, Integer> withoutSourceLinkUsageCounts = termDatasetReportDbService.getWithoutSourceLinkUsageCounts(datasetCodes);
		Map<String, Integer> withoutSourceLinkUsageMeaningUpdateUsageCounts = termDatasetReportDbService
				.getWithoutSourceLinkUsageMeaningUpdateUsageCounts(datasetCodes, from, until);
		Map<String, String> withoutSourceLinkUsageTermSamples = termDatasetReportDbService.getWithoutSourceLinkUsageTermSamples(datasetCodes);
		Map<String, String> withoutSourceLinkUsageMeaningUpdateTermSamples = termDatasetReportDbService
				.getWithoutSourceLinkUsageMeaningUpdateTermSamples(datasetCodes, from, until);
		Map<String, Integer> allUsageCounts = termDatasetReportDbService.getAllUsageCounts(datasetCodes);

		List<TermDatasetReportRow> datasetRows = new ArrayList<>();

		for (Dataset dataset : datasets) {
			String datasetCode = dataset.getCode();

			int publicMeaningCount = publicMeaningCounts.getOrDefault(datasetCode, 0);
			int updateMeaningCount = updateMeaningCounts.getOrDefault(datasetCode, 0);
			int withDomainMeaningCount = withDomainMeaningCounts.getOrDefault(datasetCode, 0);
			int withDomainUpdateMeaningCount = withDomainUpdateMeaningCounts.getOrDefault(datasetCode, 0);
			BigDecimal withDomainMeaningPercent = computePercent(withDomainMeaningCount, publicMeaningCount);
			BigDecimal withDomainUpdateMeaningPercent = computePercent(withDomainUpdateMeaningCount, updateMeaningCount);

			TermDatasetReportRow row = new TermDatasetReportRow();
			row.setDatasetCode(datasetCode);
			row.setDatasetName(dataset.getName());

			row.setPublicMeaningCount(publicMeaningCount);
			row.setAllMeaningCount(allMeaningCounts.getOrDefault(datasetCode, 0));
			row.setPublicTermCount(publicTermCounts.getOrDefault(datasetCode, 0));
			row.setAllTermCount(allTermCounts.getOrDefault(datasetCode, 0));
			row.setCreateMeaningCount(createMeaningCounts.getOrDefault(datasetCode, 0));
			row.setUpdateMeaningCount(updateMeaningCount);
			row.setWithDomainMeaningCount(withDomainMeaningCount);
			row.setWithDomainUpdateMeaningCount(withDomainUpdateMeaningCount);
			row.setWithDomainMeaningPercent(withDomainMeaningPercent);
			row.setWithDomainUpdateMeaningPercent(withDomainUpdateMeaningPercent);
			row.setWithoutDomainTermSample(withoutDomainTermSamples.get(datasetCode));

			row.setSingleTermMeaningCount(singleTermMeaningCounts.getOrDefault(datasetCode, 0));
			row.setSingleTermMeaningTermSample(singleTermMeaningTermSamples.get(datasetCode));
			row.setSingleLangMeaningCount(singleLangMeaningCounts.getOrDefault(datasetCode, 0));
			row.setSingleLangMeaningTermSample(singleLangMeaningTermSamples.get(datasetCode));
			row.setSpecificCharTermCount(specificCharTermCounts.getOrDefault(datasetCode, 0));
			row.setSpecificCharTermSample(specificCharTermSamples.get(datasetCode));
			row.setInitialCapTermCount(initialCapTermCounts.getOrDefault(datasetCode, 0));
			row.setInitialCapTermSample(initialCapTermSamples.get(datasetCode));
			row.setWithSourceLinkTermCount(withSourceLinkTermCounts.getOrDefault(datasetCode, 0));
			row.setWithSourceLinkMeaningUpdateTermCount(withSourceLinkMeaningUpdateTermCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkTermCount(withoutSourceLinkTermCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkMeaningUpdateTermCount(withoutSourceLinkMeaningUpdateTermCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkMeaningUpdateTermSample(withoutSourceLinkMeaningUpdateTermSamples.get(datasetCode));

			row.setWithDefinitionMeaningCount(withDefinitionMeaningCounts.getOrDefault(datasetCode, 0));
			row.setWithDefinitionUpdateMeaningCount(withDefinitionUpdateMeaningCounts.getOrDefault(datasetCode, 0));
			row.setWithoutDefinitionMeaningTermSample(withoutDefinitionMeaningTermSamples.get(datasetCode));
			row.setWithoutDefinitionUpdateMeaningTermSample(withoutDefinitionUpdateMeaningTermSamples.get(datasetCode));
			row.setWithPunctuationDefinitionCount(withPunctuationDefinitionCounts.getOrDefault(datasetCode, 0));
			row.setWithPunctuationDefinitionTermSample(withPunctuationDefinitionTermSamples.get(datasetCode));
			row.setInitialCapDefinitionCount(initialCapDefinitionCounts.getOrDefault(datasetCode, 0));
			row.setInitialCapDefinitionTermSample(initialCapDefinitionTermSamples.get(datasetCode));
			row.setInitialEnumerationDefinitionCount(initialEnumerationDefinitionCounts.getOrDefault(datasetCode, 0));
			row.setInitialEnumerationDefinitionTermSample(initialEnumerationDefinitionTermSamples.get(datasetCode));
			row.setWithSourceLinkDefinitionCount(withSourceLinkDefinitionCounts.getOrDefault(datasetCode, 0));
			row.setWithSourceLinkDefinitionMeaningUpdateDefinitionCount(withSourceLinkDefinitionMeaningUpdateDefinitionCounts.getOrDefault(datasetCode, 0));
			row.setAllDefinitionCount(allDefinitionCounts.getOrDefault(datasetCode, 0));

			row.setWithUsageMeaningCount(withUsageMeaningCounts.getOrDefault(datasetCode, 0));
			row.setWithSourceLinkUsageCount(withSourceLinkUsageCounts.getOrDefault(datasetCode, 0));
			row.setWithSourceLinkUsageMeaningUpdateUsageCount(withSourceLinkUsageMeaningUpdateUsageCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkUsageCount(withoutSourceLinkUsageCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkUsageMeaningUpdateUsageCount(withoutSourceLinkUsageMeaningUpdateUsageCounts.getOrDefault(datasetCode, 0));
			row.setWithoutSourceLinkUsageTermSample(withoutSourceLinkUsageTermSamples.get(datasetCode));
			row.setWithoutSourceLinkUsageMeaningUpdateTermSample(withoutSourceLinkUsageMeaningUpdateTermSamples.get(datasetCode));
			row.setAllUsageCount(allUsageCounts.getOrDefault(datasetCode, 0));

			datasetRows.add(row);
		}

		TermDatasetReportContent content = new TermDatasetReportContent();
		content.setParameters(reportParameters);
		content.setRows(datasetRows);

		String contentJson = objectMapper.writeValueAsString(content);
		return contentJson;
	}

	private BigDecimal computePercent(int part, int total) {

		if (total == 0) {
			return BigDecimal.ZERO.setScale(2);
		}

		BigDecimal partValue = BigDecimal.valueOf(part);
		BigDecimal totalValue = BigDecimal.valueOf(total);

		BigDecimal percent = partValue.divide(totalValue, 4, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100))
				.setScale(2, RoundingMode.HALF_UP);

		return percent;
	}

}
