package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DOMAIN;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierLabel;

@Component
public class ClassifierDbService extends AbstractDataDbService {

	@Autowired
	private DSLContext create;

	public List<String> getClassifierCodes(String classifierName) {

		Field<Object> codeField = DSL.field("code");
		Field<Object> orderByField = DSL.field("order_by");

		List<String> classifierCodes = create
				.select(codeField)
				.from(classifierName)
				.orderBy(orderByField)
				.fetchInto(String.class);

		return classifierCodes;
	}

	public List<String> getDomainCodes(String domainOriginCode) {

		List<String> domainCodes = create
				.select(DOMAIN.CODE)
				.from(DOMAIN)
				.where(DOMAIN.ORIGIN.eq(domainOriginCode))
				.orderBy(DOMAIN.ORDER_BY)
				.fetchInto(String.class);

		return domainCodes;
	}

	public List<ClassifierLabel> getClassifierLabels(String classifierName, String classifierCode) {

		String labelTableName = getLabelTableName(classifierName);
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");
		Field<Object> valueField = DSL.field("value");

		List<ClassifierLabel> classifierLabels = create
				.select(codeField, typeField, langField, valueField)
				.from(labelTableName)
				.where(codeField.eq(classifierCode))
				.fetchInto(ClassifierLabel.class);

		return classifierLabels;
	}

	public List<ClassifierLabel> getDomainLabels(String domainOrigin, String domainCode, String labelTypeCode) {

		List<ClassifierLabel> classifierLabels = create
				.select(DOMAIN_LABEL.CODE, DOMAIN_LABEL.TYPE, DOMAIN_LABEL.LANG, DOMAIN_LABEL.VALUE)
				.from(DOMAIN_LABEL)
				.where(
						DOMAIN_LABEL.CODE.eq(domainCode)
								.and(DOMAIN_LABEL.ORIGIN.eq(domainOrigin))
								.and(DOMAIN_LABEL.TYPE.eq(labelTypeCode)))
				.fetchInto(ClassifierLabel.class);

		return classifierLabels;
	}

	public void createClassifier(String classifierName, String classifierCode) {

		Field<Object> codeField = DSL.field("code");
		Field<Object> datasetsField = DSL.field("datasets");
		String[] emptyArray = new String[0];

		create
				.insertInto(DSL.table(classifierName))
				.columns(codeField, datasetsField)
				.values(classifierCode, emptyArray)
				.execute();
	}

	public void createDomainClassifier(String domainOriginCode, String classifierCode) {

		String[] emptyArray = new String[0];

		create
				.insertInto(DOMAIN)
				.columns(DOMAIN.ORIGIN, DOMAIN.CODE, DOMAIN.DATASETS)
				.values(domainOriginCode, classifierCode, emptyArray)
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
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");
		Field<Object> valueField = DSL.field("value");

		if (StringUtils.isNotBlank(origin)) {
			Field<Object> originField = DSL.field("origin");

			create
					.insertInto(DSL.table(labelTableName))
					.columns(codeField, typeField, langField, valueField, originField)
					.values(code, type, lang, value, origin)
					.onConflict(codeField, originField, langField, typeField)
					.doUpdate()
					.set(valueField, value)
					.execute();
			return;
		}

		create
				.insertInto(DSL.table(labelTableName))
				.columns(codeField, typeField, langField, valueField)
				.values(code, type, lang, value)
				.onConflict(codeField, langField, typeField)
				.doUpdate()
				.set(valueField, value)
				.execute();
	}

	public void deleteClassifierLabel(ClassifierLabel classifierLabel) {

		String classifierName = classifierLabel.getClassifierName().name();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();
		String origin = classifierLabel.getOrigin();

		String labelTableName = getLabelTableName(classifierName);
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");

		Condition deleteWhere = codeField.eq(code)
				.and(typeField.eq(type))
				.and(langField.eq(lang));

		if (StringUtils.isNotBlank(origin)) {
			Field<Object> originField = DSL.field("origin");
			deleteWhere = deleteWhere.and(originField.eq(origin));
		}

		create
				.delete(DSL.table(labelTableName))
				.where(deleteWhere)
				.execute();
	}

	public boolean classifierExists(String classifierName, String classifierCode) {

		Field<Object> codeField = DSL.field("code");
		return create
				.fetchExists(DSL
						.select(codeField)
						.from(classifierName)
						.where(codeField.eq(classifierCode)));
	}

	public boolean domainClassifierExists(String domainOriginCode, String classifierCode) {

		return create
				.fetchExists(DSL
						.select(DOMAIN.CODE)
						.from(DOMAIN)
						.where(DOMAIN.ORIGIN.eq(domainOriginCode).and(DOMAIN.CODE.eq(classifierCode))));
	}

	private String getLabelTableName(String classifierName) {
		return classifierName + "_label";
	}

}
