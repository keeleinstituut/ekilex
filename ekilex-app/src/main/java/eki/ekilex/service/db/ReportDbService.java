package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.EKI_USER;
import static eki.ekilex.data.db.main.Tables.REPORT;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.ReportStatus;
import eki.ekilex.constant.ReportType;
import eki.ekilex.data.db.main.tables.EkiUser;
import eki.ekilex.data.db.main.tables.Report;

@Component
public class ReportDbService {

	@Autowired
	protected DSLContext mainDb;

	public Long createReport(ReportType reportType, Long userId) {

		LocalDateTime now = LocalDateTime.now();

		return mainDb
				.insertInto(
						REPORT,
						REPORT.USER_ID,
						REPORT.TYPE,
						REPORT.STATUS,
						REPORT.CREATED_ON)
				.values(
						userId,
						reportType.name(),
						ReportStatus.PENDING.name(),
						now)
				.returning(REPORT.ID)
				.fetchOne()
				.getId();
	}

	public List<eki.ekilex.data.Report> getReports(ReportType reportType) {

		Report r = REPORT.as("r");
		EkiUser eu = EKI_USER.as("eu");

		return mainDb
				.select(
						r.ID,
						r.TYPE,
						r.STATUS,
						r.CREATED_ON,
						r.COMPLETED_ON,
						eu.NAME.as("user_name"))
				.from(r, eu)
				.where(
						r.TYPE.eq(reportType.name())
								.and(r.USER_ID.eq(eu.ID)))
				.orderBy(r.ID.desc())
				.fetchInto(eki.ekilex.data.Report.class);
	}

	public eki.ekilex.data.Report getReport(Long id) {

		Report r = REPORT.as("r");
		EkiUser eu = EKI_USER.as("eu");

		return mainDb.select(
				r.ID,
				r.TYPE,
				r.STATUS,
				r.CREATED_ON,
				r.COMPLETED_ON,
				r.CONTENT,
				eu.NAME.as("user_name"))
				.from(r, eu)
				.where(
						r.ID.eq(id)
								.and(r.USER_ID.eq(eu.ID)))
				.fetchOneInto(eki.ekilex.data.Report.class);
	}

	public void updateReportCompleted(Long reportId, String content) {

		LocalDateTime now = LocalDateTime.now();

		mainDb
				.update(REPORT)
				.set(REPORT.STATUS, ReportStatus.COMPLETED.name())
				.set(REPORT.CONTENT, JSONB.valueOf(content))
				.set(REPORT.COMPLETED_ON, now)
				.where(REPORT.ID.eq(reportId))
				.execute();
	}

	public void updateReportFailed(Long reportId) {

		LocalDateTime now = LocalDateTime.now();

		mainDb
				.update(REPORT)
				.set(REPORT.STATUS, ReportStatus.FAILED.name())
				.set(REPORT.COMPLETED_ON, now)
				.where(REPORT.ID.eq(reportId))
				.execute();
	}

	public void deleteReport(Long reportId) {

		mainDb
				.deleteFrom(REPORT)
				.where(REPORT.ID.eq(reportId))
				.execute();
	}
}
