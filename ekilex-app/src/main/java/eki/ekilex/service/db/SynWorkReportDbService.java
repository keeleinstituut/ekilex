package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.data.SynWorkReportUserContribution;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeTag;

@Component
public class SynWorkReportDbService implements GlobalConstant {

	@Autowired
	private DSLContext mainDb;

	public List<SynWorkReportUserContribution> getUserContributions(LocalDateTime from, LocalDateTime until) {

		LexemeTag lt = LEXEME_TAG.as("lt");
		Lexeme l = LEXEME.as("l");

		return mainDb
				.select(
						lt.CREATED_BY.as("user_name"),
						DSL.count(lt.ID).as("completed_lexeme_count"))
				.from(lt, l)
				.where(
						lt.LEXEME_ID.eq(l.ID)
								.and(lt.TAG_NAME.eq("süno valmis"))
								.and(lt.CREATED_ON.ge(from))
								.and(lt.CREATED_ON.lt(until))
								.and(l.DATASET_CODE.eq(DATASET_EKI)))
				.groupBy(lt.CREATED_BY)
				.orderBy(DSL.field("completed_lexeme_count").desc())
				.fetchInto(SynWorkReportUserContribution.class);
	}
}
