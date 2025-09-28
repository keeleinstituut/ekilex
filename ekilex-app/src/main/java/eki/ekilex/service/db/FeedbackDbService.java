package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_ATTR;
import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG_COMMENT;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.db.main.tables.FeedbackLog;
import eki.ekilex.data.db.main.tables.FeedbackLogAttr;
import eki.ekilex.data.db.main.tables.FeedbackLogComment;

@Component
public class FeedbackDbService {

	@Autowired
	private DSLContext mainDb;

	public void createFeedbackLog(eki.ekilex.data.FeedbackLog feedbackLog) {

		mainDb
				.insertInto(
						FEEDBACK_LOG,
						FEEDBACK_LOG.FEEDBACK_TYPE,
						FEEDBACK_LOG.SENDER_EMAIL,
						FEEDBACK_LOG.LAST_SEARCH,
						FEEDBACK_LOG.DESCRIPTION)
				.values(
						feedbackLog.getFeedbackType(),
						feedbackLog.getSenderEmail(),
						feedbackLog.getLastSearch(),
						feedbackLog.getDescription())
				.execute();
	}

	public void deleteFeedbackLog(Long feedbackLogId) {

		mainDb
				.delete(FEEDBACK_LOG)
				.where(FEEDBACK_LOG.ID.eq(feedbackLogId))
				.execute();
	}

	public long getFeedbackLogCount(String searchFilter, Boolean notCommentedFilter) {

		FeedbackLog fl = FEEDBACK_LOG.as("fl");
		Condition where = getFeedbackLogCond(fl, searchFilter, notCommentedFilter);

		return mainDb
				.select(DSL.count(fl.ID))
				.from(fl)
				.where(where)
				.fetchOneInto(Long.class);
	}

	public List<eki.ekilex.data.FeedbackLog> getFeedbackLogs(String searchFilter, Boolean notCommentedFilter, int offset, int limit) {

		FeedbackLog fl = FEEDBACK_LOG.as("fl");
		FeedbackLogAttr fla = FEEDBACK_LOG_ATTR.as("fla");
		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");
		Condition where = getFeedbackLogCond(fl, searchFilter, notCommentedFilter);

		Field<JSON> flcf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(flc.ID),
										DSL.key("feedbackLogId").value(flc.FEEDBACK_LOG_ID),
										DSL.key("createdOn").value(flc.CREATED_ON),
										DSL.key("comment").value(flc.COMMENT),
										DSL.key("userName").value(flc.USER_NAME)))
						.orderBy(flc.CREATED_ON.desc()))
				.from(flc)
				.where(flc.FEEDBACK_LOG_ID.eq(fl.ID))
				.asField();

		Field<JSON> flaf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(fla.ID),
										DSL.key("feedbackLogId").value(fla.FEEDBACK_LOG_ID),
										DSL.key("name").value(fla.NAME),
										DSL.key("value").value(fla.VALUE)))
						.orderBy(fla.ID.desc()))
				.from(fla)
				.where(fla.FEEDBACK_LOG_ID.eq(fl.ID))
				.asField();

		return mainDb
				.select(fl.fields())
				.select(
						flaf.as("feedback_log_attrs"),
						flcf.as("feedback_log_comments"))
				.from(fl)
				.where(where)
				.orderBy(fl.CREATED_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(eki.ekilex.data.FeedbackLog.class);
	}

	private Condition getFeedbackLogCond(FeedbackLog fl, String searchFilter, Boolean notCommentedFilter) {

		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");
		FeedbackLogAttr fla = FEEDBACK_LOG_ATTR.as("fla");
		Condition where = DSL.noCondition();

		if (StringUtils.isNotBlank(searchFilter)) {

			String searchCrit = "%" + searchFilter + "%";
			where = where.and(DSL.or(
					fl.SENDER_EMAIL.likeIgnoreCase(searchCrit),
					fl.DESCRIPTION.likeIgnoreCase(searchCrit),
					fl.LAST_SEARCH.likeIgnoreCase(searchCrit),
					DSL.exists(DSL
							.select(fla.ID)
							.from(fla)
							.where(
									fla.FEEDBACK_LOG_ID.eq(fl.ID)
											.and(fla.VALUE.likeIgnoreCase(searchCrit)))),
					DSL.exists(DSL
							.select(flc.ID)
							.from(flc)
							.where(flc.FEEDBACK_LOG_ID.eq(fl.ID)
									.and(flc.COMMENT.likeIgnoreCase(searchCrit))))));
		}
		if (Boolean.TRUE.equals(notCommentedFilter)) {

			where = where.andNotExists(DSL
					.select(flc.ID)
					.from(flc)
					.where(flc.FEEDBACK_LOG_ID.eq(fl.ID)));
		}
		return where;
	}

	public void createFeedbackLogComment(Long feedbackLogId, String comment, String userName) {

		mainDb
				.insertInto(
						FEEDBACK_LOG_COMMENT,
						FEEDBACK_LOG_COMMENT.FEEDBACK_LOG_ID,
						FEEDBACK_LOG_COMMENT.COMMENT,
						FEEDBACK_LOG_COMMENT.USER_NAME)
				.values(
						feedbackLogId,
						comment,
						userName)
				.execute();
	}

	public List<eki.ekilex.data.FeedbackLogComment> getFeedbackLogComments(Long feedbackLogId) {

		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");

		return mainDb
				.selectFrom(flc)
				.where(flc.FEEDBACK_LOG_ID.eq(feedbackLogId))
				.orderBy(flc.CREATED_ON.desc())
				.fetchInto(eki.ekilex.data.FeedbackLogComment.class);
	}

}
