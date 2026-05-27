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
import eki.ekilex.data.Report;

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

  public List<Report> getReports(ReportType reportType) {

    return mainDb
        .select(
            REPORT.ID,
            REPORT.TYPE,
            REPORT.STATUS,
            REPORT.CREATED_ON,
            REPORT.COMPLETED_ON,
            EKI_USER.NAME.as("user_name"))
        .from(REPORT
            .join(EKI_USER).on(EKI_USER.ID.eq(REPORT.USER_ID)))
        .where(REPORT.TYPE.eq(reportType.name()))
        .orderBy(REPORT.ID.desc())
        .fetchInto(Report.class);
  }

  public Report getReport(Long id) {

    return mainDb.
        select(
            REPORT.ID,
            REPORT.TYPE,
            REPORT.STATUS,
            REPORT.CREATED_ON,
            REPORT.COMPLETED_ON,
            REPORT.CONTENT,
            EKI_USER.NAME.as("user_name"))
        .from(REPORT
            .join(EKI_USER).on(EKI_USER.ID.eq(REPORT.USER_ID)))
        .where(REPORT.ID.eq(id))
        .fetchOneInto(Report.class);
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
