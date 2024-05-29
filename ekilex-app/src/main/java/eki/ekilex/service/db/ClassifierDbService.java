package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierLabel;

@Component
public class ClassifierDbService extends AbstractDataDbService {

	private static final Field<Object> CODE_FIELD = DSL.field("code");

	private static final Field<Object> TYPE_FIELD = DSL.field("type");

	private static final Field<Object> LANG_FIELD = DSL.field("lang");

	private static final Field<Object> VALUE_FIELD = DSL.field("value");

	private static final Field<Object> ROW_NUM_FIELD = DSL.field("row_num");

	private static final Field<Object> ORIGIN_FIELD = DSL.field("origin");

	private static final Field<Object> DATASETS_FIELD = DSL.field("datasets");

	private static final Field<Long> ORDER_BY_FIELD = DSL.field("order_by", Long.class);

	public List<String> getClassifierCodes(String classifierName) {

		return create
				.select(CODE_FIELD)
				.from(classifierName)
				.orderBy(ORDER_BY_FIELD)
				.fetchInto(String.class);
	}

	public List<ClassifierLabel> getClassifierLabels(String classifierName, String classifierCode) {

		String labelTableName = getLabelTableName(classifierName);

		return create
				.select(CODE_FIELD, TYPE_FIELD, LANG_FIELD, VALUE_FIELD)
				.from(labelTableName)
				.where(CODE_FIELD.eq(classifierCode))
				.fetchInto(ClassifierLabel.class);
	}

	public List<String> getDomainCodes(String domainOrigin) {

		return create
				.select(DOMAIN.CODE)
				.from(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOrigin))
				.orderBy(DOMAIN.ORDER_BY)
				.fetchInto(String.class);
	}

	public List<ClassifierLabel> getDomainLabels(String domainOrigin, String domainCode, String labelTypeCode) {

		return create
				.select(DOMAIN_LABEL.CODE, DOMAIN_LABEL.TYPE, DOMAIN_LABEL.LANG, DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.where(
						DOMAIN_LABEL.CODE.eq(domainCode)
								.and(DOMAIN_LABEL.ORIGIN.eq(domainOrigin))
								.and(DOMAIN_LABEL.TYPE.eq(labelTypeCode)))
				.fetchInto(ClassifierLabel.class);
	}

	public Long getClassifierOrderBy(String classifierName, String classifierCode) {

		return create
				.select(ORDER_BY_FIELD)
				.from(classifierName)
				.where(CODE_FIELD.eq(classifierCode))
				.fetchOneInto(Long.class);
	}

	public Long getDomainOrderBy(String domainOrigin, String domainCode) {

		return create
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

		Long orderBy = create
				.select(ORDER_BY_FIELD)
				.from(rn)
				.where(ROW_NUM_FIELD.eq(order))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = create
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

		Long orderBy = create
				.select(ORDER_BY_FIELD)
				.from(rn)
				.where(ROW_NUM_FIELD.eq(domainOrder))
				.fetchOneInto(Long.class);

		if (orderBy == null) {
			orderBy = create
					.select(DSL.max(ORDER_BY_FIELD))
					.from(DOMAIN)
					.where(DOMAIN.ORIGIN.eq(domainOrigin))
					.fetchOneInto(Long.class);
		}

		return orderBy;
	}

	public List<Long> getClassifierOrderByIntervalList(String classifierName, Long orderByMin, Long orderByMax) {

		return create
				.select(ORDER_BY_FIELD)
				.from(classifierName)
				.where(
						ORDER_BY_FIELD.greaterOrEqual(orderByMin)
								.and(ORDER_BY_FIELD.lessOrEqual(orderByMax)))
				.orderBy(ORDER_BY_FIELD)
				.fetchInto(Long.class);
	}

	public List<Long> getDomainOrderByIntervalList(String domainOrigin, Long orderByMin, Long orderByMax) {

		return create
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
		create
				.insertInto(DSL.table(classifierName))
				.columns(CODE_FIELD, DATASETS_FIELD)
				.values(classifierCode, emptyArray)
				.execute();
	}

	public void createDomain(String domainOrigin, String domainCode) {

		String[] emptyArray = new String[0];
		create
				.insertInto(DOMAIN)
				.columns(DOMAIN.ORIGIN, DOMAIN.CODE, DOMAIN.DATASETS)
				.values(domainOrigin, domainCode, emptyArray)
				.execute();
	}

	public void createOrUpdateClassifierLabel(ClassifierLabel classifierLabel) {

		String classifierName = classifierLabel.getClassifierName().name();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();
		String value = classifierLabel.getValue();
		String origin = classifierLabel.getOrigin();

		String labelTableName = getLabelTableName(classifierName);

		if (StringUtils.isNotBlank(origin)) {
			create
					.insertInto(DSL.table(labelTableName))
					.columns(CODE_FIELD, TYPE_FIELD, LANG_FIELD, VALUE_FIELD, ORIGIN_FIELD)
					.values(code, type, lang, value, origin)
					.onConflict(CODE_FIELD, ORIGIN_FIELD, LANG_FIELD, TYPE_FIELD)
					.doUpdate()
					.set(VALUE_FIELD, value)
					.execute();
			return;
		}

		create
				.insertInto(DSL.table(labelTableName))
				.columns(CODE_FIELD, TYPE_FIELD, LANG_FIELD, VALUE_FIELD)
				.values(code, type, lang, value)
				.onConflict(CODE_FIELD, LANG_FIELD, TYPE_FIELD)
				.doUpdate()
				.set(VALUE_FIELD, value)
				.execute();
	}

	public void updateClassifierOrderBy(String classifierName, String classifierCode, Long orderBy) {

		create
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, orderBy)
				.where(CODE_FIELD.eq(classifierCode))
				.execute();
	}

	public void updateDomainOrderBy(String domainOrigin, String domainCode, Long orderBy) {

		create
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, orderBy)
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(DOMAIN.CODE.eq(domainCode)))
				.execute();
	}

	public void increaseClassifierOrderBys(String classifierName, List<Long> orderByList) {

		create
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.plus(1))
				.where(ORDER_BY_FIELD.in(orderByList))
				.execute();
	}

	public void increaseDomainOrderBys(String domainOrigin, List<Long> orderByList) {

		create
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.plus(1))
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(ORDER_BY_FIELD.in(orderByList)))
				.execute();
	}

	public void reduceClassifierOrderBys(String classifierName, List<Long> orderByList) {

		create
				.update(DSL.table(classifierName))
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.minus(1))
				.where(ORDER_BY_FIELD.in(orderByList))
				.execute();
	}

	public void reduceDomainOrderBys(String domainOrigin, List<Long> orderByList) {

		create
				.update(DOMAIN)
				.set(ORDER_BY_FIELD, ORDER_BY_FIELD.minus(1))
				.where(
						DOMAIN.ORIGIN.eq(domainOrigin)
								.and(ORDER_BY_FIELD.in(orderByList)))
				.execute();
	}

	public void deleteClassifierLabel(ClassifierLabel classifierLabel) {

		String classifierName = classifierLabel.getClassifierName().name();
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

		create
				.delete(DSL.table(labelTableName))
				.where(deleteWhere)
				.execute();
	}

	public void deleteClassifier(String classifierName, String classifierCode) {

		create
				.delete(DSL.table(classifierName))
				.where(CODE_FIELD.eq(classifierCode))
				.execute();
	}

	public void deleteDomain(String domainOrigin, String classifierCode) {

		create
				.delete(DOMAIN_LABEL)
				.where(DOMAIN_LABEL.ORIGIN.eq(domainOrigin).and(DOMAIN_LABEL.CODE.eq(classifierCode)))
				.execute();

		create
				.delete(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOrigin).and(DOMAIN.CODE.eq(classifierCode)))
				.execute();
	}

	public boolean classifierExists(String classifierName, String classifierCode) {

		return create
				.fetchExists(DSL
						.select(CODE_FIELD)
						.from(classifierName)
						.where(CODE_FIELD.eq(classifierCode)));
	}

	public boolean domainExists(String domainOrigin, String classifierCode) {

		return create
				.fetchExists(DSL
						.select(DOMAIN.CODE)
						.from(DOMAIN)
						.where(DOMAIN.ORIGIN.eq(domainOrigin).and(DOMAIN.CODE.eq(classifierCode))));
	}

	private String getLabelTableName(String classifierName) {
		return classifierName + "_label";
	}

}
