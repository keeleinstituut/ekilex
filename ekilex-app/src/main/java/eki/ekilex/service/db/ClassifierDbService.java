package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Public.PUBLIC;
import static eki.ekilex.data.db.main.Tables.DOMAIN;
import static eki.ekilex.data.db.main.Tables.DOMAIN_LABEL;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.ClassifierFull;
import eki.ekilex.data.ClassifierLabel;

@Component
public class ClassifierDbService extends AbstractDataDbService {

	private static final Field<String> CODE_FIELD = DSL.field("code", String.class);

	private static final Field<String> TYPE_FIELD = DSL.field("type", String.class);

	private static final Field<String> LANG_FIELD = DSL.field("lang", String.class);

	private static final Field<String> VALUE_FIELD = DSL.field("value", String.class);

	private static final Field<Integer> ROW_NUM_FIELD = DSL.field("row_num", Integer.class);

	private static final Field<String> ORIGIN_FIELD = DSL.field("origin", String.class);

	private static final Field<Object> DATASETS_FIELD = DSL.field("datasets");

	private static final Field<Long> ORDER_BY_FIELD = DSL.field("order_by", Long.class);

	public List<ClassifierFull> getClassifierFulls(ClassifierName classifierName, String origin, List<String> labelTypes) {

		String classifierTableName = getTableName(classifierName);
		String classifierLabelTableName = getLabelTableName(classifierName);
		Table<?> cl = PUBLIC.getTable(classifierTableName).as("cl");
		Table<?> cll = PUBLIC.getTable(classifierLabelTableName).as("cll");

		Field<JSON> cllrf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("code").value(cll.field("code", String.class)),
										DSL.key("origin").value(cll.field("origin", String.class)),
										DSL.key("value").value(cll.field("value", String.class)),
										DSL.key("lang").value(cll.field("lang", String.class)),
										DSL.key("type").value(cll.field("type", String.class)))))
				.from(cll)
				.where(
						cll.field("code", String.class).eq(cl.field("code", String.class))
								.and(cll.field("origin", String.class).eq(cl.field("origin", String.class)))
								.and(cll.field("type", String.class).in(labelTypes)))
				.asField();

		return mainDb
				.select(
						DSL.val(classifierName.name()).as("name"),
						DSL.val(origin).as("origin"),
						cl.field("code", String.class),
						cllrf.as("labels"),
						DSL.rowNumber().over(DSL.orderBy(cl.field("order_by"))).as("order"))
				.from(cl)
				.where(cl.field("origin", String.class).eq(origin))
				.orderBy(cl.field("order_by"))
				.fetchInto(ClassifierFull.class);
	}

	public List<ClassifierFull> getClassifierFulls(ClassifierName classifierName, List<String> labelTypes) {

		boolean hasLabel = classifierName.hasLabel();
		String classifierTableName = getTableName(classifierName);
		Table<?> cl = PUBLIC.getTable(classifierTableName).as("cl");
		List<Field<?>> fields;

		if (hasLabel) {

			String classifierLabelTableName = getLabelTableName(classifierName);
			Table<?> cll = PUBLIC.getTable(classifierLabelTableName).as("cll");
			Field<JSON> cllrf = DSL
					.select(DSL
							.jsonArrayAgg(DSL
									.jsonObject(
											DSL.key("code").value(cll.field("code", String.class)),
											DSL.key("value").value(cll.field("value", String.class)),
											DSL.key("lang").value(cll.field("lang", String.class)),
											DSL.key("type").value(cll.field("type", String.class)))))
					.from(cll)
					.where(
							cll.field("code", String.class).eq(cl.field("code", String.class))
									.and(cll.field("type", String.class).in(labelTypes)))
					.asField();

			fields = Arrays.asList(
					DSL.val(classifierName.name()).as("name"),
					cl.field("code", String.class),
					cllrf.as("labels"),
					DSL.rowNumber().over(DSL.orderBy(cl.field("order_by"))).as("order"));

		} else {

			fields = Arrays.asList(
					DSL.val(classifierName.name()).as("name"),
					cl.field("code", String.class),
					DSL.rowNumber().over(DSL.orderBy(cl.field("order_by"))).as("order"));
		}

		return mainDb
				.select(fields)
				.from(cl)
				.orderBy(cl.field("order_by"))
				.fetchInto(ClassifierFull.class);
	}

	public Long getClassifierOrderBy(String classifierName, String classifierCode) {

		return mainDb
				.select(ORDER_BY_FIELD)
				.from(classifierName)
				.where(CODE_FIELD.eq(classifierCode))
				.fetchOneInto(Long.class);
	}

	public Long getDomainOrderBy(String domainOrigin, String domainCode) {

		return mainDb
				.select(DOMAIN.ORDER_BY)
				.from(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOrigin).and(DOMAIN.CODE.eq(domainCode)))
				.fetchOneInto(Long.class);
	}

	public Long getClassifierOrderByOrMaxOrderBy(String classifierName, Integer order) {

		Table<Record2<Long, Integer>> rn = DSL
				.select(ORDER_BY_FIELD, DSL.rowNumber().over(DSL.orderBy(ORDER_BY_FIELD)).as(ROW_NUM_FIELD))
				.from(classifierName)
				.asTable("rn");

		Long orderBy = mainDb
				.select(ORDER_BY_FIELD)
				.from(rn)
				.where(ROW_NUM_FIELD.eq(order))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = mainDb
					.select(DSL.max(ORDER_BY_FIELD))
					.from(classifierName)
					.fetchOneInto(Long.class);
		}

		return orderBy;
	}

	public Long getDomainOrderByOrMaxOrderBy(String domainOrigin, Integer domainOrder) {

		Table<Record2<Long, Integer>> rn = DSL
				.select(ORDER_BY_FIELD, DSL.rowNumber().over(DSL.orderBy(ORDER_BY_FIELD)).as(ROW_NUM_FIELD))
				.from(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOrigin))
				.asTable("rn");

		Long orderBy = mainDb
				.select(ORDER_BY_FIELD)
				.from(rn)
				.where(ROW_NUM_FIELD.eq(domainOrder))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = mainDb
					.select(DSL.max(ORDER_BY_FIELD))
					.from(DOMAIN)
					.where(DOMAIN.ORIGIN.eq(domainOrigin))
					.fetchOneInto(Long.class);
		}

		return orderBy;
	}

	public List<Long> getClassifierOrderByIntervalList(String classifierName, Long orderByMin, Long orderByMax) {

		return mainDb
				.select(ORDER_BY_FIELD)
				.from(classifierName)
				.where(
						ORDER_BY_FIELD.greaterOrEqual(orderByMin)
								.and(ORDER_BY_FIELD.lessOrEqual(orderByMax)))
				.orderBy(ORDER_BY_FIELD)
				.fetchInto(Long.class);
	}

	public List<Long> getDomainOrderByIntervalList(String domainOrigin, Long orderByMin, Long orderByMax) {

		return mainDb
				.select(ORDER_BY_FIELD)
				.from(DOMAIN)
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(ORDER_BY_FIELD.greaterOrEqual(orderByMin))
								.and(ORDER_BY_FIELD.lessOrEqual(orderByMax)))
				.orderBy(ORDER_BY_FIELD)
				.fetchInto(Long.class);
	}

	public void createClassifier(String classifierName, String classifierCode) {

		String[] emptyArray = new String[0];
		mainDb
				.insertInto(DSL.table(classifierName))
				.columns(CODE_FIELD, DATASETS_FIELD)
				.values(classifierCode, emptyArray)
				.execute();
	}

	public void createDomain(String domainOrigin, String domainCode) {

		String[] emptyArray = new String[0];
		mainDb
				.insertInto(DOMAIN)
				.columns(DOMAIN.ORIGIN, DOMAIN.CODE, DOMAIN.DATASETS)
				.values(domainOrigin, domainCode, emptyArray)
				.execute();
	}

	public void createOrUpdateClassifierLabel(ClassifierLabel classifierLabel) {

		ClassifierName classifierName = classifierLabel.getClassifierName();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();
		String value = classifierLabel.getValue();
		String origin = classifierLabel.getOrigin();
		String labelTableName = getLabelTableName(classifierName);

		if (StringUtils.isNotBlank(origin)) {
			mainDb
					.insertInto(DSL.table(labelTableName))
					.columns(CODE_FIELD, TYPE_FIELD, LANG_FIELD, VALUE_FIELD, ORIGIN_FIELD)
					.values(code, type, lang, value, origin)
					.onConflict(CODE_FIELD, ORIGIN_FIELD, LANG_FIELD, TYPE_FIELD)
					.doUpdate()
					.set(VALUE_FIELD, value)
					.execute();
			return;
		}

		mainDb
				.insertInto(DSL.table(labelTableName))
				.columns(CODE_FIELD, TYPE_FIELD, LANG_FIELD, VALUE_FIELD)
				.values(code, type, lang, value)
				.onConflict(CODE_FIELD, LANG_FIELD, TYPE_FIELD)
				.doUpdate()
				.set(VALUE_FIELD, value)
				.execute();
	}

	public void updateClassifierOrderBy(String classifierName, String classifierCode, Long orderBy) {

		mainDb
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, orderBy)
				.where(CODE_FIELD.eq(classifierCode))
				.execute();
	}

	public void updateDomainOrderBy(String domainOrigin, String domainCode, Long orderBy) {

		mainDb
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, orderBy)
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(DOMAIN.CODE.eq(domainCode)))
				.execute();
	}

	public void increaseClassifierOrderBys(String classifierName, List<Long> orderByList) {

		mainDb
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.plus(1))
				.where(ORDER_BY_FIELD.in(orderByList))
				.execute();
	}

	public void increaseDomainOrderBys(String domainOrigin, List<Long> orderByList) {

		mainDb
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.plus(1))
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(ORDER_BY_FIELD.in(orderByList)))
				.execute();
	}

	public void reduceClassifierOrderBys(String classifierName, List<Long> orderByList) {

		mainDb
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.minus(1))
				.where(ORDER_BY_FIELD.in(orderByList))
				.execute();
	}

	public void reduceDomainOrderBys(String domainOrigin, List<Long> orderByList) {

		mainDb
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.minus(1))
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(ORDER_BY_FIELD.in(orderByList)))
				.execute();
	}

	public void deleteClassifierLabel(ClassifierLabel classifierLabel) {

		ClassifierName classifierName = classifierLabel.getClassifierName();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();
		String origin = classifierLabel.getOrigin();
		String labelTableName = getLabelTableName(classifierName);

		Condition deleteWhere = CODE_FIELD.eq(code)
				.and(TYPE_FIELD.eq(type))
				.and(LANG_FIELD.eq(lang));

		if (StringUtils.isNotBlank(origin)) {
			deleteWhere = deleteWhere.and(ORIGIN_FIELD.eq(origin));
		}

		mainDb
				.delete(DSL.table(labelTableName))
				.where(deleteWhere)
				.execute();
	}

	public void deleteClassifier(String classifierName, String classifierCode) {

		mainDb
				.delete(DSL.table(classifierName))
				.where(CODE_FIELD.eq(classifierCode))
				.execute();
	}

	public void deleteDomain(String domainOrigin, String classifierCode) {

		mainDb
				.delete(DOMAIN_LABEL)
				.where(DOMAIN_LABEL.ORIGIN.eq(domainOrigin).and(DOMAIN_LABEL.CODE.eq(classifierCode)))
				.execute();

		mainDb
				.delete(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOrigin).and(DOMAIN.CODE.eq(classifierCode)))
				.execute();
	}

	public boolean classifierExists(String classifierName, String classifierCode) {

		return mainDb
				.fetchExists(DSL
						.select(CODE_FIELD)
						.from(classifierName)
						.where(CODE_FIELD.eq(classifierCode)));
	}

	public boolean domainExists(String domainOrigin, String classifierCode) {

		return mainDb
				.fetchExists(DSL
						.select(DOMAIN.CODE)
						.from(DOMAIN)
						.where(DOMAIN.ORIGIN.eq(domainOrigin).and(DOMAIN.CODE.eq(classifierCode))));
	}

	private String getTableName(ClassifierName classifierName) {
		return classifierName.name().toLowerCase();
	}

	private String getLabelTableName(ClassifierName classifierName) {
		return getTableName(classifierName) + "_label";
	}

}
