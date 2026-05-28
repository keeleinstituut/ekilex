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
import eki.ekilex.data.TermDatasetReportContent;
import eki.ekilex.data.TermDatasetReportParameters;
import eki.ekilex.data.TermDatasetReportRow;
import eki.ekilex.service.db.ReportDbService;
import eki.ekilex.service.db.TermDatasetReportDbService;

@Component
public class ReportGenerationService {

	private static final Logger logger = LoggerFactory.getLogger(ReportGenerationService.class);

	@Autowired
	private ReportDbService reportDbService;

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
		default -> throw new IllegalArgumentException("Report generation not implemented for type: " + reportType);
		};

		return content;
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
