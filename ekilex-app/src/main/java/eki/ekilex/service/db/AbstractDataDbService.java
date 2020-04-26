package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.FormMode;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

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

	protected Condition composeLexemeDatasetsCondition(Lexeme lexeme, SearchDatasetsRestriction searchDatasetsRestriction) {

		List<String> filteringDatasetCodes = searchDatasetsRestriction.getFilteringDatasetCodes();
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();
		boolean noDatasetsFiltering = searchDatasetsRestriction.isNoDatasetsFiltering();
		boolean allDatasetsPermissions = searchDatasetsRestriction.isAllDatasetsPermissions();

		Condition dsFiltWhere;

		if (noDatasetsFiltering) {
			if (allDatasetsPermissions) {
				//no restrictions
				dsFiltWhere = DSL.trueCondition();
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//all visible ds, only public
				dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
						.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue())));
			} else {
				//all visible ds, selected perm
				dsFiltWhere = DSL.or(
						lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
								.andExists(DSL.select(DATASET.CODE).from(DATASET).where(DATASET.CODE.eq(lexeme.DATASET_CODE).and(DATASET.IS_VISIBLE.isTrue()))),
						lexeme.DATASET_CODE.in(userPermDatasetCodes));
			}
		} else {
			if (allDatasetsPermissions) {
				//selected ds, full perm
				dsFiltWhere = lexeme.DATASET_CODE.in(filteringDatasetCodes);
			} else if (CollectionUtils.isEmpty(userPermDatasetCodes)) {
				//selected ds, only public
				dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes));
			} else {
				Collection<String> filteringPermDatasetCodes = CollectionUtils.intersection(filteringDatasetCodes, userPermDatasetCodes);
				if (CollectionUtils.isEmpty(filteringPermDatasetCodes)) {
					//selected ds, only public
					dsFiltWhere = lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes));
				} else {
					//selected ds, some perm, some public
					dsFiltWhere = DSL.or(
							lexeme.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC).and(lexeme.DATASET_CODE.in(filteringDatasetCodes)),
							lexeme.DATASET_CODE.in(filteringPermDatasetCodes));
				}
			}
		}
		return dsFiltWhere;
	}

}
