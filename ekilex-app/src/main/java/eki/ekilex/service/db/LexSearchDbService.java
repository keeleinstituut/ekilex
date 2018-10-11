package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.COLLOCATION;
import static eki.ekilex.data.db.Tables.DATASET;
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
import static eki.ekilex.data.db.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.tables.WordGroup.WORD_GROUP;
import static eki.ekilex.data.db.tables.WordGroupMember.WORD_GROUP_MEMBER;
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
import org.jooq.Record15;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import eki.common.constant.FormMode;
import eki.common.constant.FreeformType;
import eki.ekilex.constant.SearchEntity;
import eki.ekilex.constant.SearchKey;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchCriterion;
import eki.ekilex.data.SearchCriterionGroup;
import eki.ekilex.data.SearchFilter;
import eki.ekilex.data.db.tables.Collocation;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.LexColloc;
import eki.ekilex.data.db.tables.LexCollocPosGroup;
import eki.ekilex.data.db.tables.LexCollocRelGroup;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.MeaningDomain;
import eki.ekilex.data.db.tables.MeaningFreeform;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordEtymology;
import eki.ekilex.data.db.tables.WordGroup;
import eki.ekilex.data.db.tables.WordGroupMember;
import eki.ekilex.data.db.tables.WordRelTypeLabel;

@Service
public class LexSearchDbService extends AbstractSearchDbService {

	private DSLContext create;

	public LexSearchDbService(DSLContext context) {
		create = context;
	}

	public Result<Record> findWords(SearchFilter searchFilter, List<String> datasets, boolean fetchAll) {

		List<SearchCriterionGroup> searchCriteriaGroups = searchFilter.getCriteriaGroups();

		if (hasNoSerachCriteria(searchCriteriaGroups)) {
			return create.newResult();
		}

		Word w1 = WORD.as("w1");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Condition wordCondition = createCondition(w1, searchCriteriaGroups, datasets);

		return execute(w1, p, f, wordCondition, datasets, fetchAll);
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

		Word w1 = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");
		Condition wordCondition = createCondition(w1, searchFilter.getCriteriaGroups(), datasets);

		return count(w1, p, f, wordCondition, datasets);
	}

	private Condition createCondition(Word w1, List<SearchCriterionGroup> searchCriteriaGroups, List<String> datasets) {

		Condition wordCondition = DSL.trueCondition();

		for (SearchCriterionGroup searchCriterionGroup : searchCriteriaGroups) {

			List<SearchCriterion> searchCriteria = searchCriterionGroup.getSearchCriteria();
			if (searchCriteria.isEmpty()) {
				continue;
			}
			SearchEntity searchEntity = searchCriterionGroup.getEntity();

			if (SearchEntity.HEADWORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(p1.WORD_ID.eq(w1.ID))
						.and(f1.PARADIGM_ID.eq(p1.ID))
						.and(f1.MODE.eq(FormMode.WORD.name()));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_REF, searchCriteria, l1.ID, where1);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, l1.ID, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(l1.ID).from(l1, p1, f1).where(where1)));

			} else if (SearchEntity.WORD.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Lexeme l2 = Lexeme.LEXEME.as("l2");
				Word w2 = Word.WORD.as("w2");
				Paradigm p2 = Paradigm.PARADIGM.as("p2");
				Form f2 = Form.FORM.as("f2");
				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(l2.MEANING_ID))
						.and(w2.ID.eq(l2.WORD_ID))
						.and(p2.WORD_ID.eq(w2.ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.MODE.eq(FormMode.WORD.name()));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
					where1 = where1.and(l2.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f2.VALUE, where1);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w2.LANG, where1);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_REF, searchCriteria, l2.ID, where1);
				where1 = applyLexemeSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, l2.ID, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(l1.ID).from(l1, l2, p2, f2, w2).where(where1)));

			} else if (SearchEntity.FORM.equals(searchEntity)) {

				Lexeme l1 = Lexeme.LEXEME.as("l1");
				Paradigm p1 = Paradigm.PARADIGM.as("p1");
				Form f1 = Form.FORM.as("f1");
				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(p1.WORD_ID.eq(w1.ID))
						.and(f1.PARADIGM_ID.eq(p1.ID));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, f1.VALUE, where1);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, w1.LANG, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(l1.ID).from(l1, p1, f1).where(where1)));

			} else if (SearchEntity.MEANING.equals(searchEntity)) {

				List<SearchCriterion> domainCriterions = searchCriteria.stream()
						.filter(crit -> crit.getSearchKey().equals(SearchKey.DOMAIN) && crit.getSearchValue() != null)
						.collect(toList());

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				MeaningDomain m1d = MEANING_DOMAIN.as("m1d");
				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(m1d.MEANING_ID.eq(m1.ID));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				for (SearchCriterion criterion : domainCriterions) {
					Classifier domain = (Classifier) criterion.getSearchValue();
					where1 = where1.and(m1d.DOMAIN_CODE.eq(domain.getCode())).and(m1d.DOMAIN_ORIGIN.eq(domain.getOrigin()));
				}
				wordCondition = wordCondition.and(DSL.exists(DSL.select(m1.ID).from(l1, m1, m1d).where(where1)));

			} else if (SearchEntity.DEFINITION.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				Definition d1 = DEFINITION.as("d1");
				Condition where1 = l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(d1.MEANING_ID.eq(m1.ID))
						.and(d1.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, d1.VALUE, where1);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, d1.LANG, where1);
				where1 = applyDefinitionSourceFilters(SearchKey.SOURCE_REF, searchCriteria, d1.ID, where1);
				where1 = applyDefinitionSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, d1.ID, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(d1.ID).from(l1, m1, d1).where(where1)));

			} else if (SearchEntity.USAGE.equals(searchEntity)) {

				Lexeme l1 = LEXEME.as("l1");
				LexemeFreeform l1ff = LEXEME_FREEFORM.as("l1ff");
				Freeform u1 = FREEFORM.as("u1");

				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(l1ff.LEXEME_ID.eq(l1.ID))
						.and(l1ff.FREEFORM_ID.eq(u1.ID))
						.and(u1.TYPE.eq(FreeformType.USAGE.name()))
						.and(u1.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.VALUE, searchCriteria, u1.VALUE_TEXT, where1);
				where1 = applyValueFilters(SearchKey.LANGUAGE, searchCriteria, u1.LANG, where1);
				where1 = applyFreeformSourceFilters(SearchKey.SOURCE_NAME, searchCriteria, u1.ID, where1);
				where1 = applyFreeformSourceFilters(SearchKey.SOURCE_REF, searchCriteria, u1.ID, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(u1.ID).from(l1, l1ff, u1).where(where1)));

			} else if (SearchEntity.CONCEPT_ID.equals(searchEntity)) {

				// this type of search is not actually available

				Lexeme l1 = LEXEME.as("l1");
				Meaning m1 = MEANING.as("m1");
				MeaningFreeform m1ff = MEANING_FREEFORM.as("m1ff");
				Freeform c1 = FREEFORM.as("c1");
				Condition where1 =
						l1.WORD_ID.eq(w1.ID)
						.and(l1.MEANING_ID.eq(m1.ID))
						.and(m1ff.MEANING_ID.eq(m1.ID))
						.and(m1ff.FREEFORM_ID.eq(c1.ID))
						.and(c1.TYPE.eq(FreeformType.CONCEPT_ID.name()));

				if (CollectionUtils.isNotEmpty(datasets)) {
					where1 = where1.and(l1.DATASET_CODE.in(datasets));
				}

				where1 = applyValueFilters(SearchKey.ID, searchCriteria, c1.VALUE_TEXT, where1);

				wordCondition = wordCondition.and(DSL.exists(DSL.select(c1.ID).from(l1, m1, m1ff, c1).where(where1)));
			}
		}
		return wordCondition;
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

	private Result<Record> execute(Word w1, Paradigm p1, Form f1, Condition where, List<String> datasets, boolean fetchAll) {

		Field<String> wf = DSL.field("array_to_string(array_agg(distinct f.value), ',', '*')").cast(String.class);

		Table<Record> from = w1.join(p1).on(p1.WORD_ID.eq(w1.ID)).join(f1).on(f1.PARADIGM_ID.eq(p1.ID).and(f1.MODE.eq(FormMode.WORD.name())));

		if (CollectionUtils.isNotEmpty(datasets)) {
			Lexeme ld = LEXEME.as("ld");
			where = where.andExists(
						DSL.select(ld.ID).from(ld)
						.where((ld.WORD_ID.eq(w1.ID))
						.and(ld.DATASET_CODE.in(datasets))));
		}

		Table<Record4<Long,String,Integer,String>> w = DSL
				.select(
					w1.ID.as("word_id"),
					wf.as("word"),
					w1.HOMONYM_NR,
					w1.LANG)
				.from(from)
				.where(where)
				.groupBy(w1.ID)
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

		Table<Record> from = word.join(paradigm.join(form).on(form.PARADIGM_ID.eq(paradigm.ID).and(form.MODE.eq(FormMode.WORD.name())))).on(paradigm.WORD_ID.eq(word.ID));

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

	public Result<Record10<Long,String,Long,String,String,String[],String,String,String,String>> findParadigmFormTuples(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		return create
				.select(
						PARADIGM.ID.as("paradigm_id"),
						PARADIGM.INFLECTION_TYPE_NR,
						FORM.ID.as("form_id"),
						FORM.VALUE.as("form"),
						FORM.MODE,
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

	public Result<Record> findWordLexemes(Long wordId, List<String> datasets) {

		Condition datasetCondition = DSL.trueCondition();
		if (CollectionUtils.isNotEmpty(datasets)) {
			datasetCondition = datasetCondition.and(LEXEME.DATASET_CODE.in(datasets));
		}

		return create.select(wordLexemeSelectFields)
			.from(FORM, PARADIGM, WORD, LEXEME, MEANING, DATASET)
			.where(
					WORD.ID.eq(wordId)
					.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
					.and(FORM.MODE.eq(FormMode.WORD.name()))
					.and(PARADIGM.WORD_ID.eq(WORD.ID))
					.and(LEXEME.WORD_ID.eq(WORD.ID))
					.and(LEXEME.MEANING_ID.eq(MEANING.ID))
					.and(LEXEME.DATASET_CODE.eq(DATASET.CODE))
					.and(datasetCondition))
			.groupBy(WORD.ID, LEXEME.ID, MEANING.ID, DATASET.CODE)
			.orderBy(WORD.ID, DATASET.ORDER_BY, LEXEME.LEVEL1, LEXEME.LEVEL2, LEXEME.LEVEL3)
			.fetch();
	}

	public Record findLexeme(Long lexemeId) {

		return create.select(wordLexemeSelectFields)
			.from(FORM, PARADIGM, WORD, LEXEME, MEANING)
			.where(
					LEXEME.ID.eq(lexemeId)
					.and(WORD.ID.eq(LEXEME.WORD_ID))
					.and(PARADIGM.WORD_ID.eq(WORD.ID))
					.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
					.and(FORM.MODE.eq(FormMode.WORD.name()))
					.and(LEXEME.MEANING_ID.eq(MEANING.ID)))
			.groupBy(WORD.ID, LEXEME.ID, MEANING.ID)
			.orderBy(WORD.ID)
			.fetchSingle();
	}

	public Result<Record4<Long,String,Integer,String>> findMeaningWords(Long sourceWordId, Long meaningId, List<String> datasets) {

		Condition datasetCondition = DSL.trueCondition();
		if (CollectionUtils.isNotEmpty(datasets)) {
			datasetCondition = datasetCondition.and(LEXEME.DATASET_CODE.in(datasets));
		}

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
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						.and(WORD.ID.ne(sourceWordId))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(LEXEME.MEANING_ID.eq(meaningId))
						.and(datasetCondition)
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
						.and(LEX_RELATION.PROCESS_STATE_CODE.isDistinctFrom(PROCESS_STATE_DELETED))
						.and(LEX_RELATION.LEXEME2_ID.eq(LEXEME.ID))
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						)
				.orderBy(LEX_RELATION.ORDER_BY)
				.fetch();
	}

	public Result<Record8<Long,Long,Long,Long,String,String,String,Long>> findWordGroupMembers(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordGroupMember wgrm1 = WORD_GROUP_MEMBER.as("wgrm1");
		WordGroupMember wgrm2 = WORD_GROUP_MEMBER.as("wgrm2");
		WordGroup wgr = WORD_GROUP.as("wgr");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");
		WordRelTypeLabel wrtl = WORD_REL_TYPE_LABEL.as("wrtl");

		return create
				.select(
						wgrm2.ID.as("id"),
						wgr.ID.as("group_id"),
						w2.ID.as("word_id"),
						f2.ID.as("form_id"),
						f2.VALUE.as("word"),
						w2.LANG.as("word_lang"),
						wrtl.VALUE.as("rel_type_label"),
						wgrm2.ORDER_BY.as("order_by")
				)
				.from(
						wgr.leftOuterJoin(wrtl).on(
								wgr.WORD_REL_TYPE_CODE.eq(wrtl.CODE)
								.and(wrtl.LANG.eq(classifierLabelLang)
								.and(wrtl.TYPE.eq(classifierLabelTypeCode)))),
						wgrm1,
						wgrm2,
						w2,
						p2,
						f2
				)
				.where(
						wgrm1.WORD_ID.eq(wordId)
						.and(wgrm1.WORD_GROUP_ID.eq(wgr.ID))
						.and(wgrm2.WORD_GROUP_ID.eq(wgr.ID))
						.and(w2.ID.eq(wgrm2.WORD_ID))
						.and(p2.WORD_ID.eq(w2.ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.MODE.eq(FormMode.WORD.name()))
				)
				.orderBy(wgrm1.WORD_GROUP_ID)
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
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						)
				.orderBy(WORD_RELATION.ORDER_BY)
				.fetch();
	}

	public Result<Record9<Long,Long,String,String,String,String[],Boolean,Boolean,Long>> findWordEtymology(Long wordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		Word w2 = WORD.as("w2");
		Paradigm p2 = PARADIGM.as("p2");
		Form f2 = FORM.as("f2");

		return create
				.select(
						we.ID.as("word_etymology_id"),
						w2.ID.as("word_id"),
						f2.VALUE.as("word"),
						w2.LANG.as("word_lang"),
						we.ETYMOLOGY_TYPE_CODE,
						we.COMMENTS,
						we.IS_QUESTIONABLE,
						we.IS_COMPOUND,
						we.ORDER_BY
						)
				.from(we, w2, p2, f2)
				.where(
						we.WORD1_ID.eq(wordId)
						.and(we.WORD2_ID.eq(w2.ID))
						.and(p2.WORD_ID.eq(w2.ID))
						.and(f2.PARADIGM_ID.eq(p2.ID))
						.and(f2.MODE.eq(FormMode.WORD.name()))
						)
				.orderBy(we.ID)
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

	public Result<Record15<Long,String,Long,String,BigDecimal,BigDecimal,Long,String,String,BigDecimal,BigDecimal,String[],Long,String,BigDecimal>> findPrimaryCollocationTuples(Long lexemeId) {

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
						pgr1.POS_GROUP_CODE.as("pos_group_code"),
						rgr1.ID.as("rel_group_id"),
						rgr1.NAME.as("rel_group_name"),
						rgr1.FREQUENCY.as("rel_group_frequency"),
						rgr1.SCORE.as("rel_group_score"),
						c.ID.as("colloc_id"),
						c.VALUE.as("colloc_value"),
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_member_word_id"),
						f2.VALUE.as("colloc_member_word"),
						lc2.WEIGHT.as("colloc_member_weight")
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
						.and(f2.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						)
				.orderBy(pgr1.ORDER_BY, rgr1.ORDER_BY, lc1.GROUP_ORDER, c.ID, lc2.MEMBER_ORDER)
				.fetch();
	}

	public Result<Record9<Long,String,String,BigDecimal,BigDecimal,String[],Long,String,BigDecimal>> findSecondaryCollocationTuples(Long lexemeId) {

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
						c.DEFINITION.as("colloc_definition"),
						c.FREQUENCY.as("colloc_frequency"),
						c.SCORE.as("colloc_score"),
						c.USAGES.as("colloc_usages"),
						l2.WORD_ID.as("colloc_member_word_id"),
						f2.VALUE.as("colloc_member_word"),
						lc2.WEIGHT.as("colloc_member_weight")
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
						.and(f2.MODE.in(FormMode.WORD.name(), FormMode.UNKNOWN.name()))
						)
				.orderBy(c.ID, lc2.MEMBER_ORDER)
				.fetch();
	}

}
