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
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import eki.common.constant.ClassifierName;
import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.common.constant.LastActivityType;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.tables.ActivityLog;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.MeaningLastActivityLog;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordLastActivityLog;

public abstract class AbstractDataDbService implements SystemConstant, GlobalConstant, FreeformConstant {

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

	public List<String> getMeaningWordValues(Long meaningId, String... langs) {
		return create
				.select(WORD.VALUE)
				.from(LEXEME, WORD)
				.where(LEXEME.MEANING_ID.eq(meaningId).and(LEXEME.WORD_ID.eq(WORD.ID)).and(WORD.LANG.in(langs)))
				.fetchInto(String.class);
	}

	public int getWordNextHomonymNr(String wordValue, String wordLang) {

		Integer currentHomonymNr = create
				.select(DSL.max(WORD.HOMONYM_NR))
				.from(WORD)
				.where(
						WORD.LANG.eq(wordLang)
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchOneInto(Integer.class);

		int homonymNr = 1;
		if (currentHomonymNr != null) {
			homonymNr = currentHomonymNr + 1;
		}
		return homonymNr;
	}

	protected Field<String[]> getWordTypesField(Field<Long> wordIdField) {
		Field<String[]> wtf = DSL.field(DSL
				.select(DSL.arrayAgg(WORD_WORD_TYPE.WORD_TYPE_CODE))
				.from(WORD_WORD_TYPE)
				.where(WORD_WORD_TYPE.WORD_ID.eq(wordIdField))
				.groupBy(wordIdField));
		return wtf;
	}

	protected Field<Boolean> getWordTypeExists(Field<Long> wordIdField, String wordType) {
		Field<Boolean> wtef = DSL.field(DSL.exists(DSL
				.select(WORD_WORD_TYPE.ID)
				.from(WORD_WORD_TYPE)
				.where(
						WORD_WORD_TYPE.WORD_ID.eq(wordIdField)
								.and(WORD_WORD_TYPE.WORD_TYPE_CODE.eq(wordType)))));
		return wtef;
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

	protected Field<JSON> getLexemePosField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonArrayAgg(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.POS.name()),
								DSL.key("code").value(POS_LABEL.CODE),
								DSL.key("value").value(POS_LABEL.VALUE))));

		Field<JSON> clf = DSL
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

	protected Field<JSON> getLexemeDerivsField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonArrayAgg(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.DERIV.name()),
								DSL.key("code").value(DERIV_LABEL.CODE),
								DSL.key("value").value(DERIV_LABEL.VALUE))));

		Field<JSON> clf = DSL
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

	protected Field<JSON> getLexemeRegistersField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonArrayAgg(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.REGISTER.name()),
								DSL.key("code").value(REGISTER_LABEL.CODE),
								DSL.key("value").value(REGISTER_LABEL.VALUE))));

		Field<JSON> clf = DSL
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

	protected Field<JSON> getLexemeRegionsField(Field<Long> lexemeIdField) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonArrayAgg(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.REGION.name()),
								DSL.key("code").value(REGION.CODE),
								DSL.key("value").value(REGION.CODE))));

		Field<JSON> clf = DSL
				.select(claggf)
				.from(LEXEME_REGION, REGION)
				.where(
						LEXEME_REGION.LEXEME_ID.eq(lexemeIdField)
								.and(REGION.CODE.eq(LEXEME_REGION.REGION_CODE)))
				.groupBy(lexemeIdField)
				.asField();
		return clf;
	}

	protected Field<JSON> getLexemeValueStateField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonObject(
						DSL.key("name").value(ClassifierName.VALUE_STATE.name()),
						DSL.key("code").value(VALUE_STATE_LABEL.CODE),
						DSL.key("value").value(VALUE_STATE_LABEL.VALUE)));

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

	protected Field<JSON> getLexemeProficiencyLevelField(Field<Long> lexemeIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonObject(
						DSL.key("name").value(ClassifierName.PROFICIENCY_LEVEL.name()),
						DSL.key("code").value(PROFICIENCY_LEVEL_LABEL.CODE),
						DSL.key("value").value(PROFICIENCY_LEVEL_LABEL.VALUE)));

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

	protected Field<JSON> getMeaningDomainsField(Field<Long> meaningIdField, String classifierLabelLang, String classifierLabelTypeCode) {

		Field<JSON> claggf = DSL.field(DSL
				.jsonArrayAgg(DSL
						.jsonObject(
								DSL.key("name").value(ClassifierName.DOMAIN.name()),
								DSL.key("code").value(DOMAIN_LABEL.CODE),
								DSL.key("value").value(DOMAIN_LABEL.VALUE))));

		Field<JSON> clf = DSL
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
