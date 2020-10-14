package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.LABEL_TYPE;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.ekilex.data.ClassifierLabel;

@Component
public class ClassifierDbService extends AbstractDataDbService {

	@Autowired
	private DSLContext create;

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

	public String getClassifierLabelsAggregation(String classifierName, String classifierCode) {

		String labelTableName = getLabelTableName(classifierName);
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");
		Field<Object> valueField = DSL.field("value");

		String labelsAggregation = create
				.select(DSL.field(PostgresDSL.arrayToString(DSL.arrayAgg(DSL.concat(
						typeField, DSL.val(" - "),
						langField, DSL.val(": "),
						valueField)), ", ")))
				.from(labelTableName)
				.where(codeField.eq(classifierCode))
				.fetchOptionalInto(String.class)
				.orElse(null);

		return labelsAggregation;
	}

	@Cacheable(value = CACHE_KEY_LABEL_TYPE)
	public List<String> getLabelTypeCodes() {

		List<String> labelTypeCodes = create
				.select(LABEL_TYPE.CODE)
				.from(LABEL_TYPE)
				.fetchInto(String.class);

		return labelTypeCodes;
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

	public void createOrUpdateClassifierLabel(ClassifierLabel classifierLabel) {

		String classifierName = classifierLabel.getClassifierName().name();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();
		String value = classifierLabel.getValue();

		String labelTableName = getLabelTableName(classifierName);
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");
		Field<Object> valueField = DSL.field("value");

		create
				.insertInto(DSL.table(labelTableName))
				.columns(codeField, typeField, langField, valueField)
				.values(code, type, lang, value)
				.onConflict(codeField, langField, typeField)
				.doUpdate()
				.set(codeField, code)
				.set(typeField, type)
				.set(langField, lang)
				.set(valueField, value)
				.execute();
	}

	public void deleteClassifierLabel(ClassifierLabel classifierLabel) {

		String classifierName = classifierLabel.getClassifierName().name();
		String code = classifierLabel.getCode();
		String type = classifierLabel.getType();
		String lang = classifierLabel.getLang();

		String labelTableName = getLabelTableName(classifierName);
		Field<Object> codeField = DSL.field("code");
		Field<Object> typeField = DSL.field("type");
		Field<Object> langField = DSL.field("lang");

		create
				.delete(DSL.table(labelTableName))
				.where(
						codeField.eq(code)
								.and(typeField.eq(type))
								.and(langField.eq(lang)))
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

	private String getLabelTableName(String classifierName) {
		return classifierName + "_label";
	}

}
