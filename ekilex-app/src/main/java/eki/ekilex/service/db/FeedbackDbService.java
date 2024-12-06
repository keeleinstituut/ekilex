package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.FEEDBACK_LOG;
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
						FEEDBACK_LOG.SENDER_NAME,
						FEEDBACK_LOG.SENDER_EMAIL,
						FEEDBACK_LOG.DESCRIPTION,
						FEEDBACK_LOG.WORD,
						FEEDBACK_LOG.DEFINITION,
						FEEDBACK_LOG.USAGE,
						FEEDBACK_LOG.OTHER_INFO,
						FEEDBACK_LOG.LAST_SEARCH)
				.values(
						feedbackLog.getFeedbackType(),
						feedbackLog.getSenderName(),
						feedbackLog.getSenderEmail(),
						feedbackLog.getDescription(),
						feedbackLog.getWord(),
						feedbackLog.getDefinition(),
						feedbackLog.getUsage(),
						feedbackLog.getOtherInfo(),
						feedbackLog.getLastSearch())
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

		return mainDb
				.select(fl.fields())
				.select(flcf.as("feedback_log_comments"))
				.from(fl)
				.where(where)
				.orderBy(fl.CREATED_ON.desc())
				.offset(offset)
				.limit(limit)
				.fetchInto(eki.ekilex.data.FeedbackLog.class);
	}

	private Condition getFeedbackLogCond(FeedbackLog fl, String searchFilter, Boolean notCommentedFilter) {

		FeedbackLogComment flc = FEEDBACK_LOG_COMMENT.as("flc");
		Condition where = DSL.noCondition();

		if (StringUtils.isNotBlank(searchFilter)) {

			String searchCrit = "%" + searchFilter + "%";
			where = where.and(DSL.or(
					fl.SENDER_NAME.likeIgnoreCase(searchCrit),
					fl.SENDER_EMAIL.likeIgnoreCase(searchCrit),
					fl.DESCRIPTION.likeIgnoreCase(searchCrit),
					fl.WORD.likeIgnoreCase(searchCrit),
					fl.DEFINITION.likeIgnoreCase(searchCrit),
					fl.DEFINITION_SOURCE.likeIgnoreCase(searchCrit),
					fl.USAGE.likeIgnoreCase(searchCrit),
					fl.USAGE_SOURCE.likeIgnoreCase(searchCrit),
					fl.COMPANY.likeIgnoreCase(searchCrit),
					fl.LAST_SEARCH.likeIgnoreCase(searchCrit),
					DSL.exists(DSL
							.select(flc.ID)
							.from(flc)
							.where(flc.COMMENT.likeIgnoreCase(searchCrit)
									.and(flc.FEEDBACK_LOG_ID.eq(fl.ID))))));
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
