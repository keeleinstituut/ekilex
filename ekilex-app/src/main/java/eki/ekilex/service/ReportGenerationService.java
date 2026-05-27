package eki.ekilex.service;

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

    List<TermDatasetReportRow> datasetRows = new ArrayList<>();

    for (Dataset dataset : datasets) {
      String datasetCode = dataset.getCode();

      TermDatasetReportRow row = new TermDatasetReportRow();
      row.setDatasetCode(datasetCode);
      row.setDatasetName(dataset.getName());

      row.setPublicMeaningCount(publicMeaningCounts.getOrDefault(datasetCode, 0));
      row.setAllMeaningCount(allMeaningCounts.getOrDefault(datasetCode, 0));
      row.setPublicTermCount(publicTermCounts.getOrDefault(datasetCode, 0));
      row.setAllTermCount(allTermCounts.getOrDefault(datasetCode, 0));
      row.setCreateMeaningCount(createMeaningCounts.getOrDefault(datasetCode, 0));
      row.setUpdateMeaningCount(updateMeaningCounts.getOrDefault(datasetCode, 0));

      datasetRows.add(row);
    }

    TermDatasetReportContent content = new TermDatasetReportContent();
    content.setParameters(reportParameters);
    content.setRows(datasetRows);

    String contentJson = objectMapper.writeValueAsString(content);
    return contentJson;
  }

}
