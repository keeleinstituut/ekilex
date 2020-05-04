package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DERIV_LABEL;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_DERIV;
import static eki.ekilex.data.db.Tables.LEXEME_POS;
import static eki.ekilex.data.db.Tables.LEXEME_REGION;
import static eki.ekilex.data.db.Tables.LEXEME_REGISTER;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.POS_LABEL;
import static eki.ekilex.data.db.Tables.REGION;
import static eki.ekilex.data.db.Tables.REGISTER_LABEL;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ClassifierName;
import eki.common.constant.FormMode;
import eki.common.constant.GlobalConstant;
import eki.common.constant.TableName;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.udt.records.TypeClassifierRecord;

public abstract class AbstractDataDbService implements SystemConstant, GlobalConstant {

	@Autowired
	protected DSLContext create;

	public SimpleWord getSimpleWord(Long wordId) {
		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		return create
				.select(
						w.ID.as("word_id"),
						DSL.field("(array_agg(distinct f.value))[1]", String.class).as("word_value"),
						w.LANG)
				.from(w, p, f)
				.where(
						w.ID.eq(wordId)
								.and(p.WORD_ID.eq(w.ID))
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.eq(FormMode.WORD.name())))
				.groupBy(w.ID)
				.fetchOneInto(SimpleWord.class);
	}

	public List<String> getWordsValues(List<Long> wordIds) {
		return create
				.select(DSL.field("(array_agg(distinct form.value))[1]", String.class))
				.from(WORD, PARADIGM, FORM)
				.where(
						WORD.ID.in(wordIds)
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.groupBy(WORD.ID)
				.fetchInto(String.class);
	}

	public List<String> getLexemesWordValues(List<Long> lexemeIds) {
		return create
				.select(DSL.field("(array_agg(distinct form.value))[1]", String.class))
				.from(LEXEME, WORD, PARADIGM, FORM)
				.where(
						LEXEME.ID.in(lexemeIds)
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.groupBy(WORD.ID)
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
}
