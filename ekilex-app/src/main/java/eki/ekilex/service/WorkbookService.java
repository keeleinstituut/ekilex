package eki.ekilex.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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

import eki.ekilex.data.WorkloadActivityReport;
import eki.ekilex.data.WorkloadFunctionReport;
import eki.ekilex.data.WorkloadReport;
import eki.ekilex.data.WorkloadReportUser;

@Component
public class WorkbookService {

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

	public String getMessage(String messageKey) {

		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(messageKey, new Object[0], locale);
	}
}
