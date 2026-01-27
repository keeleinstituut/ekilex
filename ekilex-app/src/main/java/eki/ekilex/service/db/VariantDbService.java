package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_VARIANT;
import static eki.ekilex.data.db.main.Tables.VARIANT_TYPE_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeVariant;
import eki.ekilex.data.db.main.tables.VariantTypeLabel;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.service.db.util.QueryHelper;

@Component
public class VariantDbService implements SystemConstant {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private QueryHelper queryHelper;

	public Long createLexemeVariant(Long lexemeId, Long variantLexemeId, String variantTypeCode) {

		return mainDb
				.insertInto(
						LEXEME_VARIANT,
						LEXEME_VARIANT.LEXEME_ID,
						LEXEME_VARIANT.VARIANT_LEXEME_ID,
						LEXEME_VARIANT.VARIANT_TYPE_CODE)
				.values(
						lexemeId,
						variantLexemeId,
						variantTypeCode)
				.returning(LEXEME_VARIANT.ID)
				.fetchOne()
				.getId();
	}

	public void deleteLexemeVariant(Long id) {

		mainDb
				.deleteFrom(LEXEME_VARIANT)
				.where(LEXEME_VARIANT.ID.eq(id))
				.execute();
	}

	public List<eki.ekilex.data.LexemeVariant> getLexemeVariants(Long lexemeId, String classifierLabelLang) {

		Word w2 = WORD.as("w2");
		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		LexemeVariant lv = LEXEME_VARIANT.as("lv");
		VariantTypeLabel vtl = VARIANT_TYPE_LABEL.as("vtl");
		Field<String> vtvf = DSL.coalesce(vtl.VALUE, lv.VARIANT_TYPE_CODE);

		return mainDb
				.select(
						lv.ID,
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.HOMONYM_NR,
						w2.LANG,
						lv.LEXEME_ID,
						lv.VARIANT_LEXEME_ID,
						lv.VARIANT_TYPE_CODE,
						vtvf.as("variant_type_value"))
				.from(lv
						.innerJoin(l1).on(l1.ID.eq(lv.LEXEME_ID))
						.innerJoin(l2).on(l2.ID.eq(lv.VARIANT_LEXEME_ID))
						.innerJoin(w2).on(w2.ID.eq(l2.WORD_ID))
						.leftOuterJoin(vtl).on(
								vtl.CODE.eq(lv.VARIANT_TYPE_CODE)
										.and(vtl.LANG.eq(classifierLabelLang))
										.and(vtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(lv.LEXEME_ID.eq(lexemeId))
				.orderBy(lv.ORDER_BY)
				.fetchInto(eki.ekilex.data.LexemeVariant.class);
	}

	public Long getLexemeVariantLexemeId(Long lexemeVariantId) {

		LexemeVariant lv = LEXEME_VARIANT.as("lv");

		return mainDb
				.select(lv.VARIANT_LEXEME_ID)
				.from(lv)
				.where(lv.ID.eq(lexemeVariantId))
				.fetchOptionalInto(Long.class)
				.orElse(null);
	}

	public List<eki.ekilex.data.Word> getWords(String wordValue, String lang) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		Field<String> wordValueCrit = DSL.lower(wordValue);
		Condition where = DSL
				.lower(w.VALUE).eq(wordValueCrit)
				.and(w.LANG.eq(lang))
				.andExists(DSL
						.select(l.ID)
						.from(l)
						.where(
								l.WORD_ID.eq(w.ID)
										.and(l.IS_WORD.isTrue())));

		List<Field<?>> wordFields = queryHelper.getWordFields(w);

		return mainDb
				.select(wordFields)
				.from(w)
				.where(where)
				.orderBy(w.HOMONYM_NR)
				.fetchInto(eki.ekilex.data.Word.class);
	}

	public List<eki.ekilex.data.Lexeme> getWordLexemes(Long wordId, String classifierLabelLang) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		List<Field<?>> lexemeFields = queryHelper.getLexemeFields(l, ds, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);

		Condition where = l.WORD_ID.eq(wordId)
				.and(l.DATASET_CODE.eq(ds.CODE));

		return mainDb
				.select(lexemeFields)
				.from(l, ds)
				.where(where)
				.orderBy(l.WORD_ID, l.LEVEL1, l.LEVEL2)
				.fetch(record -> {
					eki.ekilex.data.Lexeme pojo = record.into(eki.ekilex.data.Lexeme.class);
					queryHelper.replaceNullCollections(pojo);
					return pojo;
				});
	}
}
