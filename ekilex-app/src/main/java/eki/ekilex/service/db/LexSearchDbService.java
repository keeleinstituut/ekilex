package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.FORM_RELATION;
import static eki.ekilex.data.db.Tables.FORM_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.LEX_COLLOC;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_POS_GROUP;
import static eki.ekilex.data.db.Tables.LEX_COLLOC_REL_GROUP;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_DOMAIN;
import static eki.ekilex.data.db.Tables.MEANING_FREEFORM;
import static eki.ekilex.data.db.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record13;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.constant.SearchOperand;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionRefLink;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformRefLink;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.LexCollocPosGroup;
import eki.ekilex.data.db.tables.LexCollocRelGroup;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.SourceFreeform;
import eki.ekilex.data.db.tables.Word;

@Service
public class LexSearchDbService implements SystemConstant {

	private DSLContext create;

	public LexSearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record> findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();

		if (hasNoSerachCriteria(searchCriteriaGroups)) {
			return create.newResult();
		}

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchCriteriaGroups, word);

		return execute(word, paradigm, form, where, datasets, fetchAll);
	}

	private boolean hasNoSerachCriteria(List<SearchCriterionGroup> searchCriteriaGroups) {
		for (SearchCriterionGroup group : searchCriteriaGroups) {
			boolean containsValidCriterion = group.getSearchCriteria().stream()
					.anyMatch(s -> s.getSearchValue() != null && isNotBlank(s.getSearchValue().toString()));
			if (containsValidCriterion) {
				return false;
			}
		}
		return true;
	}

	public int countWords(SearchFilter searchFilter, List<String> datasets) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchFilter.getCriteriaGroups(), word);

		return count(word, paradigm, form, where, datasets);
	}

	private Condition createCondition(List<SearchCriterionGroup> searchCriterionGroups, Word word) {

		Condition where = DSL.trueCondition();

		for (SearchCriterionGroup searchCriterionGroup : searchCriterionGroups) {

			List<SearchCriterion> searchCriterions = searchCriterionGroup.getSearchCriteria();
			if (searchCriterions.isEmpty()) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			List<SearchCriterion> valueCriterions = searchCriterions.stream()
					.filter(crit -> crit.getSearchKey().equals(SearchKey.VALUE) && crit.getSearchValue() != null)
					.collect(toList());
			List<SearchCriterion> idCriterions = searchCriterions.stream()
					.filter(crit -> crit.getSearchKey().equals(SearchKey.ID) && crit.getSearchValue() != null)
					.collect(toList());
			List<SearchCriterion> domainCriterions = searchCriterions.stream()
					.filter(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN) && crit.getSearchValue() != null)
					.collect(toList());

			if (SearchEntity.WORD.equals(searchEntity)) {

				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Lexeme l3 = Lexeme.LEXEME.as("l3");
				Word w3 = Word.WORD.as("w3");
				Paradigm p3 = Paradigm.PARADIGM.as("p3");
				Form f3 = Form.FORM.as("f3");
				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(l3.MEANING_ID)).and(w3.ID.eq(l3.WORD_ID))
						.and(p3.WORD_ID.eq(w3.ID)).and(f3.PARADIGM_ID.eq(p3.ID)).and(f3.IS_WORD.isTrue());

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, f3.VALUE, where2);
				}
				where2 = applyLanguageFilter(searchCriterions, w3.LANG, where2);
				if (hasValueOrLanguageCriteria(searchCriterions)) {
					where = where.and(DSL.exists(DSL.select(l2.ID).from(l2, l3, p3, f3, w3).where(where2)));
				}
				where = applyLexemeSourceFilter(SearchKey.SOURCE_NAME, searchCriterions, word.ID, where);
				where = applyLexemeSourceFilter(SearchKey.SOURCE_CODE, searchCriterions, word.ID, where);

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Lexeme l3 = Lexeme.LEXEME.as("l3");
				Word w3 = Word.WORD.as("w3");
				Paradigm p3 = Paradigm.PARADIGM.as("p3");
				Form f3 = Form.FORM.as("f3");
				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(l3.MEANING_ID)).and(w3.ID.eq(l3.WORD_ID))
						.and(p3.WORD_ID.eq(w3.ID)).and(f3.PARADIGM_ID.eq(p3.ID));

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, f3.VALUE, where2);
				}
				where2 = applyLanguageFilter(searchCriterions, w3.LANG, where2);
				where = where.and(DSL.exists(DSL.select(l2.ID).from(l2, l3, p3, f3, w3).where(where2)));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				Definition d2 = DEFINITION.as("d2");
				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(m2.ID)).and(d2.MEANING_ID.eq(m2.ID));

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, d2.VALUE, where2);
				}
				where2 = applyLanguageFilter(searchCriterions, d2.LANG, where2);
				where2 = applyDefinitionSourceFilter(SearchKey.SOURCE_NAME, searchCriterions, d2.ID, where2);
				where2 = applyDefinitionSourceFilter(SearchKey.SOURCE_CODE, searchCriterions, d2.ID, where2);
				where = where.and(DSL.exists(DSL.select(d2.ID).from(l2, m2, d2).where(where2)));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				LexemeFreeform l2ff = LEXEME_FREEFORM.as("l2ff");
				Freeform rect2 = FREEFORM.as("rect2");
				Freeform um2 = FREEFORM.as("um2");
				Freeform u2 = FREEFORM.as("u2");

				Condition where2 =
						l2.WORD_ID.eq(word.ID)
								.and(l2ff.LEXEME_ID.eq(l2.ID))
								.and(l2ff.FREEFORM_ID.eq(rect2.ID))
								.and(rect2.TYPE.eq(FreeformType.GOVERNMENT.name()))
								.and(um2.PARENT_ID.eq(rect2.ID))
								.and(um2.TYPE.eq(FreeformType.USAGE_MEANING.name()))
								.and(u2.PARENT_ID.eq(um2.ID))
								.and(u2.TYPE.eq(FreeformType.USAGE.name()));

				for (SearchCriterion criterion : valueCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, u2.VALUE_TEXT, where2);
				}
				where2 = applyLanguageFilter(searchCriterions, u2.LANG, where2);
				where = where.and(DSL.exists(DSL.select(u2.ID).from(l2, l2ff, rect2, um2, u2).where(where2)));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				MeaningFreeform m2ff = MEANING_FREEFORM.as("m2ff");
				Freeform concept = FREEFORM.as("concept");

				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(m2.ID)).and(m2ff.MEANING_ID.eq(m2.ID))
						.and(m2ff.FREEFORM_ID.eq(concept.ID)).and(concept.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				for (SearchCriterion criterion : idCriterions) {
					SearchOperand searchOperand = criterion.getSearchOperand();
					String searchValueStr = criterion.getSearchValue().toString().toLowerCase();
					where2 = applySearchValueFilter(searchValueStr, searchOperand, concept.VALUE_TEXT, where2);
				}
				where = where.and(DSL.exists(DSL.select(concept.ID).from(l2, m2, m2ff, concept).where(where2)));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				Lexeme l2 = LEXEME.as("l2");
				Meaning m2 = MEANING.as("m2");
				MeaningDomain md = MEANING_DOMAIN.as("md");

				Condition where2 = l2.WORD_ID.eq(word.ID).and(l2.MEANING_ID.eq(m2.ID)).and(md.MEANING_ID.eq(m2.ID));

				for (SearchCriterion criterion : domainCriterions) {
					Classifier domain = (Classifier) criterion.getSearchValue();
					where2 = where2.and(md.DOMAIN_CODE.eq(domain.getCode())).and(md.DOMAIN_ORIGIN.eq(domain.getOrigin()));
				}
				where = where.and(DSL.exists(DSL.select(m2.ID).from(l2, m2, md).where(where2)));

			}
		}
		return where;
	}

	private boolean hasValueOrLanguageCriteria(List<SearchCriterion> searchCriterions) {
		return searchCriterions.stream().anyMatch(crit -> crit.getSearchKey().equals(SearchKey.VALUE) || crit.getSearchKey().equals(SearchKey.LANGUAGE));
	}

	private Condition applyDefinitionSourceFilter(SearchKey searchKey, List<SearchCriterion> searchCriterions, Field<Long> definitionIdField, Condition condition) {

		List<SearchCriterion> sourceCriterions = searchCriterions.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		if (sourceCriterions.isEmpty()) {
			return condition;
		}

		DefinitionRefLink rl = DefinitionRefLink.DEFINITION_REF_LINK.as("rl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sf = SourceFreeform.SOURCE_FREEFORM.as("sf");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition = rl.DEFINITION_ID.eq(definitionIdField).and(s.ID.eq(rl.REF_ID))
				.and(sf.SOURCE_ID.eq(s.ID)).and(ff.ID.eq(sf.FREEFORM_ID)).and(ff.TYPE.eq(searchKey.name()));

		for (SearchCriterion criterion : sourceCriterions) {
			sourceCondition = applySearchValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(rl, s, sf, ff).where(sourceCondition)));
	}

	private Condition applyLexemeSourceFilter(SearchKey searchKey, List<SearchCriterion> searchCriterions, Field<Long> wordIdField, Condition condition) {

		List<SearchCriterion> sourceCriterions = searchCriterions.stream()
				.filter(crit -> crit.getSearchKey().equals(searchKey) && crit.getSearchValue() != null && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		if (sourceCriterions.isEmpty()) {
			return condition;
		}

		Lexeme l = Lexeme.LEXEME.as("l");
		LexemeFreeform lf = LexemeFreeform.LEXEME_FREEFORM.as("lf");
		Freeform f = Freeform.FREEFORM.as("f");
		FreeformRefLink rl = FreeformRefLink.FREEFORM_REF_LINK.as("rl");
		Source s = Source.SOURCE.as("s");
		SourceFreeform sf = SourceFreeform.SOURCE_FREEFORM.as("sf");
		Freeform ff = Freeform.FREEFORM.as("ff");

		Condition sourceCondition = l.WORD_ID.eq(wordIdField).and(lf.LEXEME_ID.eq(l.ID))
				.and(f.ID.eq(lf.FREEFORM_ID)).and(f.TYPE.eq("SOURCE")).and(rl.FREEFORM_ID.eq(f.ID)).and(s.ID.eq(rl.REF_ID)).and(sf.SOURCE_ID.eq(s.ID))
				.and(ff.ID.eq(sf.FREEFORM_ID)).and(ff.TYPE.eq(searchKey.name()));

		for (SearchCriterion criterion : sourceCriterions) {
			sourceCondition = applySearchValueFilter(criterion.getSearchValue().toString(), criterion.getSearchOperand(), ff.VALUE_TEXT, sourceCondition);
		}
		return condition.and(DSL.exists(DSL.select(ff.ID).from(l, lf, f, rl, s, sf, ff).where(sourceCondition)));
	}

	private Condition applyLanguageFilter(List<SearchCriterion> searchCriterions, Field<String> languageField, Condition condition) {

		List<SearchCriterion> languageCriterions = searchCriterions.stream()
				.filter(crit -> crit.getSearchKey().equals(SearchKey.LANGUAGE) && (crit.getSearchValue() != null) && isNotBlank(crit.getSearchValue().toString()))
				.collect(toList());
		for (SearchCriterion criterion : languageCriterions) {
			condition = condition.and(languageField.eq(criterion.getSearchValue().toString()));
		}
		return condition;
	}

	private Condition applySearchValueFilter(String searchValueStr, SearchOperand searchOperand, Field<?> searchField, Condition condition) {

		if (SearchOperand.EQUALS.equals(searchOperand)) {
			condition = condition.and(searchField.equalIgnoreCase(searchValueStr));
		} else if (SearchOperand.STARTS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().startsWith(searchValueStr));
		} else if (SearchOperand.ENDS_WITH.equals(searchOperand)) {
			condition = condition.and(searchField.lower().endsWith(searchValueStr));
		} else if (SearchOperand.CONTAINS.equals(searchOperand)) {
			condition = condition.and(searchField.lower().contains(searchValueStr));
		} else if (SearchOperand.CONTAINS_WORD.equals(searchOperand)) {
			condition = condition.and(DSL.field("to_tsvector('simple',{0}) @@ to_tsquery('simple',{1})",
					Boolean.class,
					searchField, DSL.inline(searchValueStr)));
		} else {
			throw new IllegalArgumentException("Unsupported operand " + searchOperand);
		}
		return condition;
	}

	public Result<Record> findWords(String wordWithMetaCharacters, List<String> datasets, boolean fetchAll) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(wordWithMetaCharacters, form);

		return execute(word, paradigm, form, where, datasets, fetchAll);
	}

	public int countWords(String searchFilter, List<String> datasets) {

		Word word = WORD.as("w");
		Paradigm paradigm = PARADIGM.as("p");
		Form form = FORM.as("f");
		Condition where = createCondition(searchFilter, form);

		return count(word, paradigm, form, where, datasets);
	}

	private Condition createCondition(String wordWithMetaCharacters, Form form) {

		String theFilter = wordWithMetaCharacters.replace("*", "%").replace("?", "_");

		Condition where = DSL.trueCondition();

		if (StringUtils.containsAny(theFilter, '%', '_')) {
			where = where.and(form.VALUE.likeIgnoreCase(theFilter));
		} else {
			where = where.and(form.VALUE.equalIgnoreCase(theFilter));
		}
		return where;
	}

	private Result<Record> execute(Word word, Paradigm paradigm, Form form, Condition where, List<String> datasets, boolean fetchAll) {

		Field<String> wf = DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").cast(String.class);

		Table<Record> from = word.join(paradigm).on(paradigm.WORD_ID.eq(word.ID)).join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.IS_WORD.isTrue()));

		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme ld = LEXEME.as("ld");
			where = where.andExists(
						DSL.select(ld.ID).from(ld)
						.where((ld.WORD_ID.eq(word.ID))
						.and(ld.DATASET_CODE.in(datasets))));
		}

		Table<Record4<Long,String,Integer,String>> w = DSL
				.select(
					word.ID.as("word_id"),
					wf.as("word"),
					word.HOMONYM_NR,
					word.LANG)
				.from(from)
				.where(where)
				.groupBy(word.ID)
				.asTable("w");

		Field<String[]> dscf = DSL.field(DSL
				.select(DSL.arrayAggDistinct(LEXEME.DATASET_CODE))
				.from(LEXEME)
				.where(LEXEME.WORD_ID.eq(w.field("word_id").cast(Long.class)))
				.groupBy(w.field("word_id")));

		Table<?> ww = DSL
				.select(
					w.field("word_id"),
					w.field("word"),
					w.field("homonym_nr"),
					w.field("lang"),
					dscf.as("dataset_codes")
						)
				.from(w)
				.orderBy(
					w.field("word"),
					w.field("homonym_nr"))
				.asTable("ww");

		if (fetchAll) {
			return create.select(ww.fields()).from(ww).fetch();
		} else {
			return create.select(ww.fields()).from(ww).limit(MAX_RESULTS_LIMIT).fetch();
		}
	}

	private int count(Word word, Paradigm paradigm, Form form, Condition where, List<String> datasets) {

		Table<Record> from = word.join(paradigm.join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.IS_WORD.isTrue()))).on(paradigm.WORD_ID.eq(word.ID));

		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme ld = LEXEME.as("ld");
			where = where.andExists(
					DSL.select(ld.ID).from(ld)
							.where((ld.WORD_ID.eq(word.ID))
									.and(ld.DATASET_CODE.in(datasets))));
		}

		Table<Record1<Long>> w = create
				.select(
					word.ID.as("word_id")
				)
				.from(from)
				.where(where)
				.groupBy(word.ID)
				.asTable("w");

		Table<?> ww = create
				.select(
					w.field("word_id")
				)
				.from(w)
				.asTable("ww");

		return create.fetchCount(ww);
	}

	public Result<Record10<Long,String,Long,String,Boolean,String[],String,String,String,String>> findParadigmFormTuples(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						PARADIGM.INFLECTION_TYPE_NR,
						FORM.ID.as("form_id"),
						FORM.VALUE.as("form"),
						FORM.IS_WORD,
						FORM.COMPONENTS,
						FORM.DISPLAY_FORM,
						FORM.VOCAL_FORM,
						FORM.MORPH_CODE,
						MORPH_LABEL.VALUE.as("morph_value")
						)
				.from(PARADIGM, FORM, MORPH_LABEL)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(MORPH_LABEL.CODE.eq(FORM.MORPH_CODE))
						.and(MORPH_LABEL.LANG.eq(classifierLabelLang))
						.and(MORPH_LABEL.TYPE.eq(classifierLabelTypeCode))
						)
				.orderBy(PARADIGM.ID, FORM.ID)
				.fetch();
	}

	private SelectField<?>[] wordLexemeSelectFields =  {
			DSL.arrayAggDistinct(FORM.VALUE).as("words"),
			DSL.arrayAggDistinct(FORM.VOCAL_FORM).as("vocal_forms"),
			WORD.LANG.as("word_lang"),
			WORD.ID.as("word_id"),
			WORD.DISPLAY_MORPH_CODE.as("word_display_morph_code"),
			WORD.GENDER_CODE,
			LEXEME.ID.as("lexeme_id"),
			LEXEME.MEANING_ID,
			LEXEME.DATASET_CODE.as("dataset"),
			LEXEME.LEVEL1,
			LEXEME.LEVEL2,
			LEXEME.LEVEL3,
			LEXEME.VALUE_STATE_CODE.as("lexeme_value_state_code"),
			LEXEME.FREQUENCY_GROUP.as("lexeme_frequency_group_code"),
			MEANING.PROCESS_STATE_CODE.as("meaning_process_state_code")
		};

	public Result<Record> findFormMeanings(Long wordId, List<String> selectedDatasets) {

		return create.select(wordLexemeSelectFields)
			.from(FORM, PARADIGM, WORD, LEXEME, MEANING)
			.where(
					WORD.ID.eq(wordId)
					.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
					.and(FORM.IS_WORD.isTrue())
					.and(PARADIGM.WORD_ID.eq(WORD.ID))
					.and(LEXEME.WORD_ID.eq(WORD.ID))
					.and(LEXEME.MEANING_ID.eq(MEANING.ID))
					.and(LEXEME.DATASET_CODE.in(selectedDatasets)))
			.groupBy(WORD.ID, LEXEME.ID, MEANING.ID)
			.orderBy(WORD.ID, LEXEME.DATASET_CODE, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
			.fetch();
	}

	public Result<Record> findWordLexeme(Long lexemeId) {

		return create.select(wordLexemeSelectFields)
			.from(FORM, PARADIGM, WORD, LEXEME, MEANING)
			.where(
					LEXEME.ID.eq(lexemeId)
					.and(WORD.ID.eq(LEXEME.WORD_ID))
					.and(PARADIGM.WORD_ID.eq(WORD.ID))
					.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
					.and(FORM.IS_WORD.isTrue())
					.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
			.groupBy(WORD.ID, LEXEME.ID, MEANING.ID)
			.orderBy(WORD.ID)
			.fetch();
	}

	public Result<Record4<Long,String,Integer,String>> findMeaningWords(Long sourceWordId, Long meaningId, List<String> datasets) {

		return create
				.select(
						WORD.ID.as("word_id"),
						FORM.VALUE.as("word"),
						WORD.HOMONYM_NR,
						WORD.LANG
						)
				.from(LEXEME, WORD, PARADIGM, FORM)
				.where(
						FORM.PARADIGM_ID.eq(PARADIGM.ID)
						.and(FORM.IS_WORD.isTrue())
						.and(WORD.ID.ne(sourceWordId))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(meaningId))
						.and(LEXEME.DATASET_CODE.in(datasets))
				)
				.groupBy(WORD.ID, FORM.VALUE)
				.orderBy(FORM.VALUE)
				.fetch();
	}

	public Result<Record8<Long,Long,Long,Long,String,String,String,Long>> findLexemeRelations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						LEX_RELATION.ID.as("id"),
						LEXEME.ID.as("lexeme_id"),
						WORD.ID.as("word_id"),
						FORM.ID.as("form_id"),
						FORM.VALUE.as("word"),
						WORD.LANG.as("word_lang"),
						LEX_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						LEX_RELATION.ORDER_BY.as("order_by")
						)
				.from(
						LEX_RELATION.leftOuterJoin(LEX_REL_TYPE_LABEL).on(
								LEX_RELATION.LEX_REL_TYPE_CODE.eq(LEX_REL_TYPE_LABEL.CODE)
								.and(LEX_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
								.and(LEX_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						LEXEME,
						WORD,
						PARADIGM,
						FORM
						)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId)
						.and(LEX_RELATION.LEXEME2_ID.eq(LEXEME.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetch();
	}

	public Result<Record6<Long,String,Long,String,String,Long>> findWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						WORD_RELATION.ID.as("id"),
						FORM.VALUE.as("word"),
						WORD.ID.as("word_id"),
						WORD.LANG.as("word_lang"),
						WORD_REL_TYPE_LABEL.VALUE.as("rel_type_label"),
						WORD_RELATION.ORDER_BY.as("order_by")
						)
				.from(
						WORD_RELATION.leftOuterJoin(WORD_REL_TYPE_LABEL).on(
								WORD_RELATION.WORD_REL_TYPE_CODE.eq(WORD_REL_TYPE_LABEL.CODE)
								.and(WORD_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
								.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode)))),
						WORD,
						PARADIGM,
						FORM
						)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId)
						.and(WORD_RELATION.WORD2_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.IS_WORD.eq(Boolean.TRUE))
						)
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetch();
	}

	public Result<Record7<Long,Long,String,Long,String,String,String>> findWordFormRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		Form f1 = FORM.as("f1");
		Form f2 = FORM.as("f2");

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						f1.ID.as("form1_id"),
						f1.VALUE.as("form1_value"),
						f2.ID.as("form2_id"),
						f2.VALUE.as("form2_value"),
						FORM_REL_TYPE_LABEL.CODE.as("rel_type_code"),
						FORM_REL_TYPE_LABEL.VALUE.as("rel_type_label")
						)
				.from(
						PARADIGM,
						f1,
						f2,
						FORM_RELATION.leftOuterJoin(FORM_REL_TYPE_LABEL).on(
							FORM_RELATION.FORM_REL_TYPE_CODE.eq(FORM_REL_TYPE_LABEL.CODE)
							.and(FORM_REL_TYPE_LABEL.LANG.eq(classifierLabelLang)
							.and(FORM_REL_TYPE_LABEL.TYPE.eq(classifierLabelTypeCode))))
						)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
						.and(f1.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM_RELATION.FORM1_ID.eq(f1.ID))
						.and(FORM_RELATION.FORM2_ID.eq(f2.ID))
						)
				.orderBy(PARADIGM.ID, FORM_RELATION.ORDER_BY)
				.fetch();
	}

	public Result<Record13<Long,String,Long,String,BigDecimal,BigDecimal,Long,String,BigDecimal,BigDecimal,String[],Long,String>> findPrimaryCollocationTuples(Long lexemeId) {

		LexCollocPosGroup pgr1 = LEX_COLLOC_POS_GROUP.as("pgr1");
		LexCollocRelGroup rgr1 = LEX_COLLOC_REL_GROUP.as("rgr1");
		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		Collocation c = COLLOCATION.as("c");
		Lexeme l2 = LEXEME.as("l2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						pgr1.ID.as("pos_group_id"),
						pgr1.NAME.as("pos_group_name"),
						rgr1.ID.as("rel_group_id"),
						rgr1.NAME.as("rel_group_name"),
						rgr1.FREQUENCY.as("rel_group_frequency"),
						rgr1.SCORE.as("rel_group_score"),
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_word_id"),
						f2.VALUE.as("colloc_word")
						)
				.from(pgr1, rgr1, lc1, lc2, c, l2, p2, f2)
				.where(
						pgr1.LEXEME_ID.eq(lexemeId)
						.and(rgr1.POS_GROUP_ID.eq(pgr1.ID))
						.and(lc1.REL_GROUP_ID.eq(rgr1.ID))
						.and(lc1.COLLOCATION_ID.eq(c.ID))
						.and(lc2.COLLOCATION_ID.eq(c.ID))
						.and(lc2.LEXEME_ID.eq(l2.ID))
						.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
						.and(l2.WORD_ID.eq(p2.WORD_ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.IS_WORD.isTrue())
						)
				.orderBy(pgr1.ORDER_BY, rgr1.ORDER_BY, lc2.ORDER_BY, c.ORDER_BY)
				.fetch();
	}

	public Result<Record7<Long,String,BigDecimal,BigDecimal,String[],Long,String>> findSecondaryCollocationTuples(Long lexemeId) {

		LexColloc lc1 = LEX_COLLOC.as("lc1");
		LexColloc lc2 = LEX_COLLOC.as("lc2");
		Collocation c = COLLOCATION.as("c");
		Lexeme l2 = LEXEME.as("l2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_word_id"),
						f2.VALUE.as("colloc_word")
						)
				.from(lc1, lc2, c, l2, p2, f2)
				.where(
						lc1.LEXEME_ID.eq(lexemeId)
						.and(lc1.REL_GROUP_ID.isNull())
						.and(lc1.COLLOCATION_ID.eq(c.ID))
						.and(lc2.COLLOCATION_ID.eq(c.ID))
						.and(lc2.LEXEME_ID.eq(l2.ID))
						.and(lc2.LEXEME_ID.ne(lc1.LEXEME_ID))
						.and(l2.WORD_ID.eq(p2.WORD_ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.IS_WORD.isTrue())
						)
				.orderBy(c.ORDER_BY)
				.fetch();
	}
}
