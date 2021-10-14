package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.Tables.MEANING_TAG;
import static eki.ekilex.data.db.Tables.TAG;

import java.util.List;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.TagType;
import eki.ekilex.data.Tag;
import eki.ekilex.data.db.tables.LexemeTag;
import eki.ekilex.data.db.tables.records.LexemeTagRecord;

@Component
public class TagDbService extends AbstractDataDbService {

	public Tag getTag(String tagName) {

		return create
				.select(TAG.NAME, TAG.TYPE, TAG.SET_AUTOMATICALLY, TAG.REMOVE_TO_COMPLETE, TAG.ORDER_BY)
				.from(TAG)
				.where(TAG.NAME.eq(tagName))
				.fetchOneInto(Tag.class);
	}

	public List<Tag> getTags() {

		Field<Boolean> isUsed = DSL.field(DSL
						.exists(DSL
								.select(LEXEME_TAG.ID)
								.from(LEXEME_TAG)
								.where(LEXEME_TAG.TAG_NAME.eq(TAG.NAME)))
						.orExists(DSL
								.select(MEANING_TAG.ID)
								.from(MEANING_TAG)
								.where(MEANING_TAG.TAG_NAME.eq(TAG.NAME))));

		return create
				.select(
						DSL.rowNumber().over(DSL.orderBy(TAG.ORDER_BY)).as("order"),
						TAG.NAME,
						TAG.TYPE,
						TAG.SET_AUTOMATICALLY,
						TAG.REMOVE_TO_COMPLETE,
						isUsed.as("used")
				)
				.from(TAG)
				.orderBy(TAG.ORDER_BY)
				.fetchInto(Tag.class);
	}

	public Long getTagOrderBy(String tagName) {

		return create
				.select(TAG.ORDER_BY)
				.from(TAG)
				.where(TAG.NAME.eq(tagName))
				.fetchOneInto(Long.class);
	}

	public Long getTagOrderByOrMaxOrderBy(Long tagOrder) {

		Table<Record2<Long, Integer>> rn = DSL
				.select(TAG.ORDER_BY, DSL.rowNumber().over(DSL.orderBy(TAG.ORDER_BY)).as("row_num"))
				.from(TAG)
				.asTable("rn");

		Long orderBy = create
				.select(rn.field("order_by", Long.class))
				.from(rn)
				.where(DSL.field("row_num").eq(tagOrder))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = create
					.select(DSL.max(TAG.ORDER_BY))
					.from(TAG)
					.fetchOneInto(Long.class);
		}

		return orderBy;
	}

	public List<Long> getTagOrderByIntervalList(Long orderByMin, Long orderByMax) {

		return create
				.select(TAG.ORDER_BY)
				.from(TAG)
				.where(
						TAG.ORDER_BY.greaterOrEqual(orderByMin)
								.and(TAG.ORDER_BY.lessOrEqual(orderByMax)))
				.orderBy(TAG.ORDER_BY)
				.fetchInto(Long.class);
	}

	public boolean tagExists(String tagName) {

		return create
				.fetchExists(DSL
						.select(TAG.NAME)
						.from(TAG)
						.where(TAG.NAME.eq(tagName)));
	}

	public void createTag(String tagName, TagType tagType, boolean setAutomatically, boolean removeToComplete) {

		create
				.insertInto(TAG)
				.columns(TAG.NAME, TAG.TYPE, TAG.SET_AUTOMATICALLY, TAG.REMOVE_TO_COMPLETE)
				.values(tagName, tagType.name(), setAutomatically, removeToComplete)
				.execute();
	}

	public List<String> createLexemeAutomaticTags(Long lexemeId) {

		LexemeTag lt = LEXEME_TAG.as("lt");
		eki.ekilex.data.db.tables.Tag t = TAG.as("t");

		List<String> createdTagNames = create
				.insertInto(LEXEME_TAG, LEXEME_TAG.LEXEME_ID, LEXEME_TAG.TAG_NAME)
				.select(DSL
						.select(DSL.val(lexemeId), t.NAME)
						.from(t)
						.where(
								t.SET_AUTOMATICALLY.isTrue()
										.andNotExists(DSL
												.select(lt.ID)
												.from(lt)
												.where(lt.LEXEME_ID.eq(lexemeId)
														.and(lt.TAG_NAME.eq(t.NAME))))))
				.returning(LEXEME_TAG.TAG_NAME)
				.fetch()
				.map(LexemeTagRecord::getTagName);

		return createdTagNames;
	}

	public void updateTag(String currentTagName, String tagName, boolean setAutomatically, boolean removeToComplete, Long orderBy) {

		create
				.update(TAG)
				.set(TAG.NAME, tagName)
				.set(TAG.SET_AUTOMATICALLY, setAutomatically)
				.set(TAG.REMOVE_TO_COMPLETE, removeToComplete)
				.set(TAG.ORDER_BY, orderBy)
				.where(TAG.NAME.eq(currentTagName))
				.execute();

	}

	public void reduceTagOrderBys(List<Long> orderByList) {

		create
				.update(TAG)
				.set(TAG.ORDER_BY, TAG.ORDER_BY.minus(1))
				.where(TAG.ORDER_BY.in(orderByList))
				.execute();
	}

	public void increaseTagOrderBys(List<Long> orderByList) {

		create
				.update(TAG)
				.set(TAG.ORDER_BY, TAG.ORDER_BY.plus(1))
				.where(TAG.ORDER_BY.in(orderByList))
				.execute();
	}

	public void deleteTag(String tagName) {

		create
				.delete(TAG)
				.where(TAG.NAME.eq(tagName))
				.execute();
	}
}
