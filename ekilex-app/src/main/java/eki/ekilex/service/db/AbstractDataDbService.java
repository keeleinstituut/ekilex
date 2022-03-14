package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.DOMAIN_LABEL;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.POS_LABEL;
import static eki.ekilex.data.db.Tables.PROFICIENCY_LEVEL_LABEL;
import static eki.ekilex.data.db.Tables.REGION;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.VALUE_STATE_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_LAST_ACTIVITY_LOG;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LastActivityType;
import eki.common.constant.TableName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.tables.ActivityLog;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordLastActivityLog;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;

public abstract class AbstractDataDbService implements SystemConstant, GlobalConstant {

	@Autowired
	protected DSLContext create;

	public SimpleWord getSimpleWord(Long wordId) {
		Word w = WORD.as("w");
		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.LANG)
				.from(w)
				.where(w.ID.eq(wordId))
				.fetchOneInto(SimpleWord.class);
	}

	public SimpleWord getLexemeSimpleWord(Long lexemeId) {
		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.LANG)
				.from(l, w)
				.where(l.ID.eq(lexemeId).and(l.WORD_ID.eq(w.ID)))
				.fetchOneInto(SimpleWord.class);
	}

	public List<String> getWordsValues(List<Long> wordIds) {
		return create
				.select(WORD.VALUE)
				.from(WORD)
				.where(WORD.ID.in(wordIds))
				.fetchInto(String.class);
	}

	public List<String> getLexemesWordValues(List<Long> lexemeIds) {
		return create
				.select(WORD.VALUE)
				.from(LEXEME, WORD)
				.where(LEXEME.ID.in(lexemeIds).and(LEXEME.WORD_ID.eq(WORD.ID)))
				.fetchInto(String.class);
	}

	protected Field<String[]> getWordTypesField(Field<Long> wordIdField) {
		Field<String[]> wtf = DSL.field(DSL
				.select(DSL.arrayAgg(WORD_WORD_TYPE.WORD_TYPE_CODE))
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordIdField))
				.groupBy(wordIdField));
		return wtf;
	}

	protected Field<Boolean> getWordIsPrefixoidField(Field<Long> wordIdField) {
		Field<Boolean> wtpf = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_PREFIXOID)))));
		return wtpf;
	}

	protected Field<Boolean> getWordIsSuffixoidField(Field<Long> wordIdField) {
		Field<Boolean> wtsf = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(WORD_TYPE_CODE_SUFFIXOID)))));
		return wtsf;
	}

	protected Field<Boolean> getWordIsForeignField(Field<Long> wordIdField) {
		Field<Boolean> wtz = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODES_FOREIGN)))));
		return wtz;
	}

	protected Field<Timestamp> getWordLastActivityEventOnField(Field<Long> wordIdField) {
		WordLastActivityLog wlal = WORD_LAST_ACTIVITY_LOG.as("wlal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Field<Timestamp> wlaeof = DSL.field(DSL
				.select(al.EVENT_ON)
				.from(wlal, al)
				.where(
						wlal.WORD_ID.eq(wordIdField)
						.and(wlal.ACTIVITY_LOG_ID.eq(al.ID)))
				.limit(1));
		return wlaeof;
	}

	protected Field<TypeClassifierRecord[]> getLexemePosField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.POS.name())), POS_LABEL.CODE, POS_LABEL.VALUE).toString();
		Field<TypeClassifierRecord[]> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier "
						+ "order by " + TableName.LEXEME_POS + ".order_by)",
				TypeClassifierRecord[].class);

		Field<TypeClassifierRecord[]> clf = DSL
				.select(claggf)
				.from(LEXEME_POS, POS_LABEL)
				.where(
						LEXEME_POS.LEXEME_ID.eq(lexemeIdField)
								.and(POS_LABEL.CODE.eq(LEXEME_POS.POS_CODE))
								.and(POS_LABEL.LANG.eq(classifierLabelLang))
								.and(POS_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	protected Field<TypeClassifierRecord[]> getLexemeDerivsField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.DERIV.name())), DERIV_LABEL.CODE, DERIV_LABEL.VALUE).toString();
		Field<TypeClassifierRecord[]> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier "
						+ "order by " + TableName.LEXEME_DERIV + ".order_by)",
				TypeClassifierRecord[].class);

		Field<TypeClassifierRecord[]> clf = DSL
				.select(claggf)
				.from(LEXEME_DERIV, DERIV_LABEL)
				.where(
						LEXEME_DERIV.LEXEME_ID.eq(lexemeIdField)
								.and(DERIV_LABEL.CODE.eq(LEXEME_DERIV.DERIV_CODE))
								.and(DERIV_LABEL.LANG.eq(classifierLabelLang))
								.and(DERIV_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	protected Field<TypeClassifierRecord[]> getLexemeRegistersField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.REGISTER.name())), REGISTER_LABEL.CODE, REGISTER_LABEL.VALUE).toString();
		Field<TypeClassifierRecord[]> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier "
						+ "order by " + TableName.LEXEME_REGISTER + ".order_by)",
				TypeClassifierRecord[].class);

		Field<TypeClassifierRecord[]> clf = DSL
				.select(claggf)
				.from(LEXEME_REGISTER, REGISTER_LABEL)
				.where(
						LEXEME_REGISTER.LEXEME_ID.eq(lexemeIdField)
								.and(REGISTER_LABEL.CODE.eq(LEXEME_REGISTER.REGISTER_CODE))
								.and(REGISTER_LABEL.LANG.eq(classifierLabelLang))
								.and(REGISTER_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	protected Field<TypeClassifierRecord[]> getLexemeRegionsField(Field<Long> lexemeIdField) {
		
		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.REGION.name())), REGION.CODE, REGION.CODE).toString();
		Field<TypeClassifierRecord[]> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier "
						+ "order by " + TableName.LEXEME_REGION + ".order_by)",
				TypeClassifierRecord[].class);

		Field<TypeClassifierRecord[]> clf = DSL
				.select(claggf)
				.from(LEXEME_REGION, REGION)
				.where(
						LEXEME_REGION.LEXEME_ID.eq(lexemeIdField)
								.and(REGION.CODE.eq(LEXEME_REGION.REGION_CODE)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	protected Field<TypeClassifierRecord> getLexemeValueStateField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.VALUE_STATE.name())), VALUE_STATE_LABEL.CODE, VALUE_STATE_LABEL.VALUE).toString();
		Field<TypeClassifierRecord> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier)",
				TypeClassifierRecord.class);

		return DSL
				.select(claggf)
				.from(LEXEME, VALUE_STATE_LABEL)
				.where(
						LEXEME.ID.eq(lexemeIdField)
								.and(VALUE_STATE_LABEL.CODE.eq(LEXEME.VALUE_STATE_CODE))
								.and(VALUE_STATE_LABEL.LANG.eq(classifierLabelLang))
								.and(VALUE_STATE_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.asField();
	}

	protected Field<TypeClassifierRecord> getLexemeProficiencyLevelField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.PROFICIENCY_LEVEL.name())), PROFICIENCY_LEVEL_LABEL.CODE, PROFICIENCY_LEVEL_LABEL.VALUE).toString();
		Field<TypeClassifierRecord> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier)",
				TypeClassifierRecord.class);

		return DSL
				.select(claggf)
				.from(LEXEME, PROFICIENCY_LEVEL_LABEL)
				.where(
						LEXEME.ID.eq(lexemeIdField)
								.and(PROFICIENCY_LEVEL_LABEL.CODE.eq(LEXEME.PROFICIENCY_LEVEL_CODE))
								.and(PROFICIENCY_LEVEL_LABEL.LANG.eq(classifierLabelLang))
								.and(PROFICIENCY_LEVEL_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.asField();
	}

	protected Field<TypeClassifierRecord[]> getMeaningDomainsField(Field<Long> meaningIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		String clrowsql = DSL.row(DSL.field(DSL.value(ClassifierName.DOMAIN.name())), DOMAIN_LABEL.CODE, DOMAIN_LABEL.VALUE).toString();
		Field<TypeClassifierRecord[]> claggf = DSL.field(
				"array_agg("
						+ clrowsql
						+ "::type_classifier "
						+ "order by " + TableName.MEANING_DOMAIN + ".order_by)",
				TypeClassifierRecord[].class);

		Field<TypeClassifierRecord[]> clf = DSL
				.select(claggf)
				.from(MEANING_DOMAIN, DOMAIN_LABEL)
				.where(
						MEANING_DOMAIN.MEANING_ID.eq(meaningIdField)
								.and(DOMAIN_LABEL.ORIGIN.eq(MEANING_DOMAIN.DOMAIN_ORIGIN))
								.and(DOMAIN_LABEL.CODE.eq(MEANING_DOMAIN.DOMAIN_CODE))
								.and(DOMAIN_LABEL.LANG.eq(classifierLabelLang))
								.and(DOMAIN_LABEL.TYPE.eq(classifierLabelTypeCode)))
				.groupBy(meaningIdField)
				.asField();
		return clf;
	}

	protected Field<Timestamp> getMeaningLastActivityEventOnField(Field<Long> meaningIdField, LastActivityType lastActivityType) {
		MeaningLastActivityLog mlal = MEANING_LAST_ACTIVITY_LOG.as("mlal");
		ActivityLog al = ACTIVITY_LOG.as("al");
		Field<Timestamp> wlaeof = DSL.field(DSL
				.select(al.EVENT_ON)
				.from(mlal, al)
				.where(
						mlal.MEANING_ID.eq(meaningIdField)
						.and(mlal.TYPE.eq(lastActivityType.name()))
						.and(mlal.ACTIVITY_LOG_ID.eq(al.ID)))
				.limit(1));
		return wlaeof;
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	protected boolean fiCollationExists() {
		Integer fiCollationCnt = create
				.selectCount()
				.from("pg_collation where lower(collcollate) = 'fi_fi.utf8'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
