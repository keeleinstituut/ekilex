package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.Word;

@Component
public class CollocationDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext mainDb;

	public void moveCollocMember(List<Long> collocLexemeIds, Long sourceCollocMemberLexemeId, Long targetCollocMemberLexemeId) {

		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.MEMBER_LEXEME_ID, targetCollocMemberLexemeId)
				.where(
						COLLOCATION_MEMBER.MEMBER_LEXEME_ID.eq(sourceCollocMemberLexemeId)
								.and(COLLOCATION_MEMBER.COLLOC_LEXEME_ID.in(collocLexemeIds)))
				.execute();
	}

	public List<CollocMemberForm> getCollocMemberForms(String formValue, String lang, String datasetCode, String classifierLabelLang) {

		Field<String> formValueLower = DSL.lower(formValue);

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");
		Lexeme l = LEXEME.as("l");
		MorphLabel mlbl = MORPH_LABEL.as("mlbl");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dds = DEFINITION_DATASET.as("dds");

		Field<String[]> dvf = DSL
				.select(DSL
						.arrayAgg(d.VALUE)
						.orderBy(d.ORDER_BY))
				.from(d)
				.where(
						d.MEANING_ID.eq(l.MEANING_ID)
								.andExists(DSL
										.select(dds.DEFINITION_ID)
										.from(dds)
										.where(
												dds.DEFINITION_ID.eq(d.ID)
														.and(dds.DATASET_CODE.eq(l.DATASET_CODE)))))
				.asField();

		Field<JSON> cmmf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(l.WORD_ID),
										DSL.key("lexemeId").value(l.ID),
										DSL.key("meaningId").value(l.MEANING_ID),
										DSL.key("definitionValues").value(dvf)))
						.orderBy(l.LEVEL1, l.LEVEL2))
				.from(l)
				.where(
						l.WORD_ID.eq(w.ID)
								.and(l.IS_WORD.isTrue())
								.and(l.DATASET_CODE.eq(datasetCode)))
				.asField();

		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("wordValue"),
						f.ID.as("form_id"),
						f.VALUE.as("form_value"),
						f.MORPH_CODE,
						mlbl.VALUE.as("morph_value"),
						cmmf.as("colloc_member_meanings"))
				.from(w, p, pf, f, mlbl)
				.where(
						w.LANG.eq(lang)
								.and(pf.PARADIGM_ID.eq(p.ID))
								.and(pf.FORM_ID.eq(f.ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(DSL.lower(f.VALUE).eq(formValueLower))
								.and(f.MORPH_CODE.ne(MORPH_CODE_UNKNOWN))
								.and(mlbl.CODE.eq(f.MORPH_CODE))
								.and(mlbl.LANG.eq(classifierLabelLang))
								.and(mlbl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
								.andExists(DSL
										.select(l.ID)
										.from(l)
										.where(
												l.WORD_ID.eq(w.ID)
														.and(l.IS_WORD.isTrue())
														.and(l.DATASET_CODE.eq(datasetCode)))))
				.orderBy(w.VALUE)
				.fetchInto(CollocMemberForm.class);
	}
}
