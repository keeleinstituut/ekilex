package eki.ekilex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import eki.ekilex.data.SynWorkReportContent;
import eki.ekilex.data.SynWorkReportParameters;
import eki.ekilex.data.SynWorkReportUserContribution;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import eki.ekilex.data.TermDatasetReportContent;
import eki.ekilex.data.TermDatasetReportParameters;
import eki.ekilex.data.TermDatasetReportRow;
import eki.ekilex.data.WorkloadActivityReport;
import eki.ekilex.data.WorkloadFunctionReport;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.data.WorkloadReportUser;

@Component
public class WorkbookService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Autowired
	protected MessageSource messageSource;

	public Workbook toWorkbook(WorkloadReport workloadReport, LocalDate dateFrom, LocalDate dateUntil, List<String> datasetCodes) {

		List<String> userNames = workloadReport.getUserNames();
		List<WorkloadActivityReport> activityReports = workloadReport.getActivityReports();

		final String sheetName = getMessage("workloadreport.title");
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);
		CellStyle dateCellStyle = createDateCellStyle(workbook);
		CellStyle boldCellStyle = createBoldCellStyle(workbook);

		int rowIndex = 0;
		int cellIndex;
		Row row;

		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("workloadreport.period.from"));
		createCell(row, cellIndex++, dateFrom, dateCellStyle);

		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("workloadreport.period.until"));
		createCell(row, cellIndex++, dateUntil, dateCellStyle);

		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("workloadreport.datasets"));
		createCell(row, cellIndex++, datasetCodes);

		row = sheet.createRow(rowIndex++);

		row = sheet.createRow(rowIndex++);
		cellIndex = 1;
		for (String userName : userNames) {
			createCell(row, cellIndex++, userName, boldCellStyle);
		}
		createCell(row, cellIndex++, getMessage("workloadreport.total"), boldCellStyle);

		for (WorkloadActivityReport activityReport : activityReports) {
			List<WorkloadReportUser> activityReportUsers = activityReport.getActivityReportUsers();
			int activityReportTotalCount = activityReport.getTotalCount();
			List<WorkloadFunctionReport> functionReports = activityReport.getFunctionReports();

			row = sheet.createRow(rowIndex++);
			cellIndex = 0;

			String activityName = getMessage("workloadreport.owner.type." + activityReport.getActivityOwner() + "." + activityReport.getActivityType());
			createCell(row, cellIndex++, activityName, boldCellStyle);

			for (WorkloadReportUser activityReportUser : activityReportUsers) {
				int activityReportUserCount = activityReportUser.getCount();
				createCell(row, cellIndex++, activityReportUserCount, boldCellStyle);
			}
			createCell(row, cellIndex++, activityReportTotalCount, boldCellStyle);

			for (WorkloadFunctionReport functionReport : functionReports) {
				String functName = functionReport.getFunctName();
				List<WorkloadReportUser> functionReportUsers = functionReport.getFunctionReportUsers();

				row = sheet.createRow(rowIndex++);
				cellIndex = 0;
				createCell(row, cellIndex++, functName);

				for (WorkloadReportUser functionReportUser : functionReportUsers) {
					int functionReportUserCount = functionReportUser.getCount();
					createCell(row, cellIndex++, functionReportUserCount);
				}
			}
		}

		return workbook;
	}

	public Workbook toSynWorkWorkbook(SynWorkReportContent content) {

		SynWorkReportParameters parameters = content.getParameters();
		List<SynWorkReportUserContribution> userContributions = content.getUserContributions();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(getMessage("report.synwork.sheet.title"));
		CellStyle boldCellStyle = createBoldCellStyle(workbook);

		int rowIndex = 0;
		int cellIndex;
		Row row;

		String reportPeriod = parameters.getDateFrom().format(DATE_FORMATTER) + " - " + parameters.getDateUntil().format(DATE_FORMATTER);
		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("report.period"));
		createCell(row, cellIndex++, reportPeriod);

		sheet.createRow(rowIndex++);

		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("report.synwork.user"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.synwork.completed.word.count"), boldCellStyle);

		for (SynWorkReportUserContribution userContribution : userContributions) {
			row = sheet.createRow(rowIndex++);
			cellIndex = 0;
			createCell(row, cellIndex++, userContribution.getUserName());
			createCell(row, cellIndex++, userContribution.getCompletedWordCount());
		}

		return workbook;
	}

	public Workbook toTermDatasetWorkbook(TermDatasetReportContent content) {

		TermDatasetReportParameters parameters = content.getParameters();
		List<TermDatasetReportRow> rows = content.getRows();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(getMessage("report.termdataset.sheet.title"));
		CellStyle boldCellStyle = createBoldCellStyle(workbook);

		int rowIndex = 0;
		int cellIndex;
		Row row;

		String reportPeriod = parameters.getDateFrom().format(DATE_FORMATTER) + " - " + parameters.getDateUntil().format(DATE_FORMATTER);
		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("report.period"));
		createCell(row, cellIndex++, reportPeriod);

		int datasetCount = parameters.getDatasetCodes().size();
		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("report.datasets"));
		createCell(row, cellIndex++, datasetCount);

		sheet.createRow(rowIndex++);

		row = sheet.createRow(rowIndex++);
		cellIndex = 0;
		createCell(row, cellIndex++, getMessage("report.termdataset.dataset"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.public.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.all.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.public.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.all.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.create.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.update.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.domain.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.domain.meaning.percent"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.domain.update.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.domain.update.meaning.percent"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.domain.term.sample"), boldCellStyle);

		createCell(row, cellIndex++, getMessage("report.termdataset.single.term.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.single.term.meaning.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.single.lang.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.single.lang.meaning.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.specific.char.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.specific.char.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.cap.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.cap.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.meaning.update.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.meaning.update.term.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.meaning.update.term.sample"), boldCellStyle);

		createCell(row, cellIndex++, getMessage("report.termdataset.with.definition.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.definition.update.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.definition.meaning.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.definition.update.meaning.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.punctuation.definition.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.punctuation.definition.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.cap.definition.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.cap.definition.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.enumeration.definition.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.initial.enumeration.definition.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.definition.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.definition.meaning.update.definition.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.all.definition.count"), boldCellStyle);

		createCell(row, cellIndex++, getMessage("report.termdataset.with.usage.meaning.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.usage.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.with.source.link.usage.meaning.update.usage.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.usage.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.usage.meaning.update.usage.count"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.usage.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.without.source.link.usage.meaning.update.term.sample"), boldCellStyle);
		createCell(row, cellIndex++, getMessage("report.termdataset.all.usage.count"), boldCellStyle);

		for (TermDatasetReportRow reportRow : rows) {
			row = sheet.createRow(rowIndex++);
			cellIndex = 0;

			createCell(row, cellIndex++, reportRow.getDatasetName());
			createCell(row, cellIndex++, reportRow.getPublicMeaningCount());
			createCell(row, cellIndex++, reportRow.getAllMeaningCount());
			createCell(row, cellIndex++, reportRow.getPublicTermCount());
			createCell(row, cellIndex++, reportRow.getAllTermCount());
			createCell(row, cellIndex++, reportRow.getCreateMeaningCount());
			createCell(row, cellIndex++, reportRow.getUpdateMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithDomainMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithDomainMeaningPercent());
			createCell(row, cellIndex++, reportRow.getWithDomainUpdateMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithDomainUpdateMeaningPercent());
			createCell(row, cellIndex++, reportRow.getWithoutDomainTermSample());

			createCell(row, cellIndex++, reportRow.getSingleTermMeaningCount());
			createCell(row, cellIndex++, reportRow.getSingleTermMeaningTermSample());
			createCell(row, cellIndex++, reportRow.getSingleLangMeaningCount());
			createCell(row, cellIndex++, reportRow.getSingleLangMeaningTermSample());
			createCell(row, cellIndex++, reportRow.getSpecificCharTermCount());
			createCell(row, cellIndex++, reportRow.getSpecificCharTermSample());
			createCell(row, cellIndex++, reportRow.getInitialCapTermCount());
			createCell(row, cellIndex++, reportRow.getInitialCapTermSample());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkTermCount());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkMeaningUpdateTermCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkTermCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkMeaningUpdateTermCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkMeaningUpdateTermSample());

			createCell(row, cellIndex++, reportRow.getWithDefinitionMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithDefinitionUpdateMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithoutDefinitionMeaningTermSample());
			createCell(row, cellIndex++, reportRow.getWithoutDefinitionUpdateMeaningTermSample());
			createCell(row, cellIndex++, reportRow.getWithPunctuationDefinitionCount());
			createCell(row, cellIndex++, reportRow.getWithPunctuationDefinitionTermSample());
			createCell(row, cellIndex++, reportRow.getInitialCapDefinitionCount());
			createCell(row, cellIndex++, reportRow.getInitialCapDefinitionTermSample());
			createCell(row, cellIndex++, reportRow.getInitialEnumerationDefinitionCount());
			createCell(row, cellIndex++, reportRow.getInitialEnumerationDefinitionTermSample());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkDefinitionCount());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkDefinitionMeaningUpdateDefinitionCount());
			createCell(row, cellIndex++, reportRow.getAllDefinitionCount());

			createCell(row, cellIndex++, reportRow.getWithUsageMeaningCount());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkUsageCount());
			createCell(row, cellIndex++, reportRow.getWithSourceLinkUsageMeaningUpdateUsageCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkUsageCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkUsageMeaningUpdateUsageCount());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkUsageTermSample());
			createCell(row, cellIndex++, reportRow.getWithoutSourceLinkUsageMeaningUpdateTermSample());
			createCell(row, cellIndex++, reportRow.getAllUsageCount());
		}

		return workbook;
	}

	private CellStyle createDateCellStyle(Workbook workbook) {

		CellStyle cellStyle = workbook.createCellStyle();
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat("dd.MM.yyyy"));
		return cellStyle;
	}

	private CellStyle createBoldCellStyle(Workbook workbook) {

		Font font = workbook.createFont();
		font.setBold(true);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(font);
		return cellStyle;
	}

	private void createCell(Row row, int cellIndex, String cellValue) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
	}

	private void createCell(Row row, int cellIndex, String cellValue, CellStyle cellStyle) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	private void createCell(Row row, int cellIndex, int cellValue) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
	}

	private void createCell(Row row, int cellIndex, int cellValue, CellStyle cellStyle) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	private void createCell(Row row, int cellIndex, LocalDate cellValue, CellStyle cellStyle) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	private void createCell(Row row, int cellIndex, List<String> cellValues) {

		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(StringUtils.join(cellValues, ", "));
	}

	private void createCell(Row row, int cellIndex, BigDecimal cellValue) {

		Cell cell = row.createCell(cellIndex);
		if (cellValue != null) {
			cell.setCellValue(cellValue.doubleValue());
		}
	}

	public String getMessage(String messageKey) {

		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(messageKey, new Object[0], locale);
	}
}
