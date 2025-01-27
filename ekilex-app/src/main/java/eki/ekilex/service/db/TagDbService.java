package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_TAG;
import static eki.ekilex.data.db.main.Tables.MEANING_TAG;
import static eki.ekilex.data.db.main.Tables.TAG;

import java.util.List;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.TagType;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeTag;
import eki.ekilex.data.db.main.tables.MeaningTag;
import eki.ekilex.data.db.main.tables.Tag;
import eki.ekilex.data.db.main.tables.records.LexemeTagRecord;

@Component
public class TagDbService extends AbstractDataDbService {

	public eki.ekilex.data.Tag getTag(String tagName) {

		return mainDb
				.select(
						TAG.NAME,
						TAG.TYPE,
						TAG.SET_AUTOMATICALLY,
						TAG.REMOVE_TO_COMPLETE,
						TAG.ORDER_BY)
				.from(TAG)
				.where(TAG.NAME.eq(tagName))
				.fetchOneInto(eki.ekilex.data.Tag.class);
	}

	public List<eki.ekilex.data.Tag> getTags() {

		Lexeme l = LEXEME.as("l");
		LexemeTag lt = LEXEME_TAG.as("lt");
		MeaningTag mt = MEANING_TAG.as("mt");
		Tag t = TAG.as("t");

		Field<Integer> rnf = DSL.field(DSL.rowNumber().over(DSL.orderBy(t.ORDER_BY)));

		Field<Boolean> uf = DSL.field(DSL
				.exists(DSL
						.select(lt.ID)
						.from(lt)
						.where(lt.TAG_NAME.eq(t.NAME)))
				.orExists(DSL
						.select(mt.ID)
						.from(mt)
						.where(mt.TAG_NAME.eq(t.NAME))));

		Table<Record1<String>> tds = DSL
				.select(l.DATASET_CODE)
				.from(l, mt)
				.where(
						mt.MEANING_ID.eq(l.MEANING_ID)
								.and(mt.TAG_NAME.eq(t.NAME)))
				.groupBy(l.DATASET_CODE)
				.unionAll(
						DSL
								.select(l.DATASET_CODE)
								.from(l, lt)
								.where(
										lt.LEXEME_ID.eq(l.ID)
												.and(lt.TAG_NAME.eq(t.NAME)))
								.groupBy(l.DATASET_CODE))
				.asTable("tds");

		Field<String[]> dsf = DSL
				.select(DSL.arrayAggDistinct(tds.field("dataset_code", String.class)))
				.from(tds)
				.asField();

		return mainDb
				.select(
						rnf.as("order"),
						t.NAME,
						t.TYPE,
						t.SET_AUTOMATICALLY,
						t.REMOVE_TO_COMPLETE,
						uf.as("used"),
						dsf.as("dataset_codes"))
				.from(t)
				.orderBy(t.ORDER_BY)
				.fetchInto(eki.ekilex.data.Tag.class);
	}

	public Long getTagOrderBy(String tagName) {

		return mainDb
				.select(TAG.ORDER_BY)
				.from(TAG)
				.where(TAG.NAME.eq(tagName))
				.fetchOneInto(Long.class);
	}

	public Long getTagOrderByOrMaxOrderBy(Long tagOrder) {

		Field<Integer> rnf = DSL.field(DSL.rowNumber().over(DSL.orderBy(TAG.ORDER_BY)));

		Table<Record2<Long, Integer>> rn = DSL
				.select(
						TAG.ORDER_BY,
						rnf.as("row_num"))
				.from(TAG)
				.asTable("rn");

		Long orderBy = mainDb
				.select(rn.field("order_by", Long.class))
				.from(rn)
				.where(DSL.field("row_num").eq(tagOrder))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = mainDb
					.select(DSL.max(TAG.ORDER_BY))
					.from(TAG)
					.fetchOneInto(Long.class);
		}

		return orderBy;
	}

	public List<Long> getTagOrderByIntervalList(Long orderByMin, Long orderByMax) {

		return mainDb
				.select(TAG.ORDER_BY)
				.from(TAG)
				.where(
						TAG.ORDER_BY.greaterOrEqual(orderByMin)
								.and(TAG.ORDER_BY.lessOrEqual(orderByMax)))
				.orderBy(TAG.ORDER_BY)
				.fetchInto(Long.class);
	}

	public boolean tagExists(String tagName) {

		return mainDb
				.fetchExists(DSL
						.select(TAG.NAME)
						.from(TAG)
						.where(TAG.NAME.eq(tagName)));
	}

	public void createTag(String tagName, TagType tagType, boolean setAutomatically, boolean removeToComplete) {

		mainDb
				.insertInto(
						TAG,
						TAG.NAME,
						TAG.TYPE,
						TAG.SET_AUTOMATICALLY,
						TAG.REMOVE_TO_COMPLETE)
				.values(
						tagName,
						tagType.name(),
						setAutomatically,
						removeToComplete)
				.execute();
	}

	public List<String> createLexemeAutomaticTags(Long lexemeId) {

		LexemeTag lt = LEXEME_TAG.as("lt");
		Tag t = TAG.as("t");

		List<String> createdTagNames = mainDb
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

		mainDb
				.update(TAG)
				.set(TAG.NAME, tagName)
				.set(TAG.SET_AUTOMATICALLY, setAutomatically)
				.set(TAG.REMOVE_TO_COMPLETE, removeToComplete)
				.set(TAG.ORDER_BY, orderBy)
				.where(TAG.NAME.eq(currentTagName))
				.execute();

	}

	public void decreaseTagOrderBys(List<Long> orderByList) {

		mainDb
				.update(TAG)
				.set(TAG.ORDER_BY, TAG.ORDER_BY.minus(1))
				.where(TAG.ORDER_BY.in(orderByList))
				.execute();
	}

	public void increaseTagOrderBys(List<Long> orderByList) {

		mainDb
				.update(TAG)
				.set(TAG.ORDER_BY, TAG.ORDER_BY.plus(1))
				.where(TAG.ORDER_BY.in(orderByList))
				.execute();
	}

	public void deleteTag(String tagName) {

		mainDb
				.delete(TAG)
				.where(TAG.NAME.eq(tagName))
				.execute();
	}
}
