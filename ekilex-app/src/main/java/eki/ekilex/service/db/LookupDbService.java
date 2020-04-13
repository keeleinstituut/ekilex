package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.FORM;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEX_RELATION;
import static eki.ekilex.data.db.Tables.LEX_REL_MAPPING;
import static eki.ekilex.data.db.Tables.LEX_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.MEANING_RELATION;
import static eki.ekilex.data.db.Tables.MEANING_REL_MAPPING;
import static eki.ekilex.data.db.Tables.MEANING_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_GROUP;
import static eki.ekilex.data.db.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_REL_MAPPING;
import static eki.ekilex.data.db.Tables.WORD_REL_TYPE_LABEL;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;
import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.springframework.stereotype.Component;

import eki.common.constant.Complexity;
import eki.common.constant.FormMode;
import eki.common.constant.LexemeType;
import eki.ekilex.data.Classifier;
import eki.ekilex.data.SearchDatasetsRestriction;
import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.WordStress;
import eki.ekilex.data.db.tables.Form;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;

@Component
public class LookupDbService extends AbstractDataDbService {

	public WordStress getWordStressData(Long wordId) {

		return create
				.select(FORM.ID.as("form_id"), FORM.VALUE_PRESE, FORM.DISPLAY_FORM)
				.from(FORM, PARADIGM)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.fetchSingleInto(WordStress.class);
	}

	public Map<String, Integer[]> getMeaningsWordsWithMultipleHomonymNumbers(List<Long> meaningIds) {

		Field<String> wordValue = FORM.VALUE.as("word_value");
		Field<Integer[]> homonymNumbers = DSL.arrayAggDistinct(WORD.HOMONYM_NR).as("homonym_numbers");

		Table<Record2<String, Integer[]>> wv = DSL
				.select(wordValue, homonymNumbers)
				.from(LEXEME, WORD, PARADIGM, FORM)
				.where(
						LEXEME.MEANING_ID.in(meaningIds)
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(WORD.ID.eq(LEXEME.WORD_ID))
								.and(PARADIGM.WORD_ID.eq(WORD.ID))
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.WORD.name())))
				.groupBy(FORM.VALUE)
				.asTable("wv");

		return create
				.selectFrom(wv)
				.where(PostgresDSL.arrayLength(wv.field(homonymNumbers)).gt(1))
				.fetchMap(wordValue, homonymNumbers);
	}

	public List<Long> getWordIdsOfJoinCandidates(eki.ekilex.data.Word targetWord, List<String> userPrefDatasetCodes, List<String> userPermDatasetCodes) {

		String wordValue = targetWord.getWordValue();
		String wordLang = targetWord.getLang();
		Long wordIdToExclude = targetWord.getWordId();
		boolean isPrefixoid = targetWord.isPrefixoid();
		boolean isSuffixoid = targetWord.isSuffixoid();

		Condition whereCondition =
				FORM.VALUE.like(wordValue)
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID)).and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(WORD.ID.ne(wordIdToExclude))
						.and(WORD.LANG.eq(wordLang))
						.andExists(DSL
								.select(LEXEME.ID)
								.from(LEXEME)
								.where(
										LEXEME.WORD_ID.eq(WORD.ID)
												.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
												.and(LEXEME.DATASET_CODE.in(userPrefDatasetCodes))));

		if (isPrefixoid) {
			whereCondition = whereCondition.andExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_PREFIXOID)));
		} else if (isSuffixoid) {
			whereCondition = whereCondition.andExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_SUFFIXOID)));
		} else {
			whereCondition = whereCondition.andNotExists(DSL
					.select(WORD_WORD_TYPE.ID)
					.from(WORD_WORD_TYPE)
					.where(WORD_WORD_TYPE.WORD_ID.eq(WORD.ID))
					.and(WORD_WORD_TYPE.WORD_TYPE_CODE.in(WORD_TYPE_CODE_PREFIXOID, WORD_TYPE_CODE_SUFFIXOID)));
		}

		Table<Record4<Long, Long, String, String>> wl = DSL
				.select(
						WORD.ID.as("word_id"),
						LEXEME.ID.as("lexeme_id"),
						LEXEME.DATASET_CODE.as("dataset_code"),
						LEXEME.PROCESS_STATE_CODE.as("process_state_code"))
				.from(WORD, PARADIGM, FORM, LEXEME)
				.where(whereCondition)
				.asTable("wl");

		return create
				.selectDistinct(wl.field("word_id"))
				.from(wl)
				.where(wl.field("process_state_code", String.class).eq(PROCESS_STATE_PUBLIC)
						.or(wl.field("dataset_code", String.class).in(userPermDatasetCodes)))
				.fetchInto(Long.class);
	}

	public List<WordLexemeMeaningIdTuple> getWordLexemeMeaningIds(Long meaningId, String datasetCode) {

		return create
				.select(
						LEXEME.WORD_ID,
						LEXEME.MEANING_ID,
						LEXEME.ID.as("lexeme_id"))
				.from(LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetchInto(WordLexemeMeaningIdTuple.class);
	}

	public List<eki.ekilex.data.Meaning> getMeanings(String searchFilter, SearchDatasetsRestriction searchDatasetsRestriction, Long excludedMeaningId) {

		String maskedSearchFilter = searchFilter.replace("*", "%").replace("?", "_").toLowerCase();
		List<String> userPermDatasetCodes = searchDatasetsRestriction.getUserPermDatasetCodes();

		Meaning m = MEANING.as("m");
		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		Form f = FORM.as("f");

		Condition whereFormValue;
		if (StringUtils.containsAny(maskedSearchFilter, '%', '_')) {
			whereFormValue = f.VALUE.lower().like(maskedSearchFilter);
		} else {
			whereFormValue = f.VALUE.lower().equal(maskedSearchFilter);
		}

		Condition whereLexemeDataset = composeLexemeDatasetsCondition(l, searchDatasetsRestriction);

		Condition whereExcludeMeaningId = DSL.noCondition();
		if (excludedMeaningId != null) {
			whereExcludeMeaningId = m.ID.ne(excludedMeaningId);
		}

		Table<Record1<Long>> mid = DSL
				.selectDistinct(m.ID.as("meaning_id"))
				.from(m, l, w, p, f)
				.where(
						l.MEANING_ID.eq(m.ID)
								.and(l.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(w.ID.eq(l.WORD_ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(f.PARADIGM_ID.eq(p.ID))
								.and(f.MODE.in(FormMode.WORD.name(), FormMode.AS_WORD.name()))
								.and(whereFormValue)
								.and(whereLexemeDataset)
								.and(whereExcludeMeaningId))
				.asTable("mid");

		return create
				.select(
						MEANING.ID.as("meaning_id"),
						DSL.arrayAggDistinct(LEXEME.ID).orderBy(LEXEME.ID).as("lexeme_ids"))
				.from(MEANING, LEXEME, mid)
				.where(
						MEANING.ID.eq(mid.field("meaning_id", Long.class))
								.and(LEXEME.MEANING_ID.eq(MEANING.ID))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY))
								.and(LEXEME.PROCESS_STATE_CODE.eq(PROCESS_STATE_PUBLIC)
										.or(LEXEME.DATASET_CODE.in(userPermDatasetCodes))))
				.groupBy(MEANING.ID)
				.fetchInto(eki.ekilex.data.Meaning.class);
	}

	public Long getMeaningId(Long lexemeId) {

		return create
				.select(LEXEME.MEANING_ID)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(Long.class);
	}

	public List<Classifier> getLexemeOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {
		
		return create
				.select(LEX_REL_TYPE_LABEL.CODE, LEX_REL_TYPE_LABEL.VALUE)
				.from(LEX_REL_MAPPING, LEX_REL_TYPE_LABEL)
				.where(
						LEX_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(LEX_REL_TYPE_LABEL.CODE.eq(LEX_REL_MAPPING.CODE2))
								.and(LEX_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(LEX_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getWordOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {

		return create
				.select(WORD_REL_TYPE_LABEL.CODE, WORD_REL_TYPE_LABEL.VALUE)
				.from(WORD_REL_MAPPING, WORD_REL_TYPE_LABEL)
				.where(
						WORD_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(WORD_REL_TYPE_LABEL.CODE.eq(WORD_REL_MAPPING.CODE2))
								.and(WORD_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(WORD_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
	}

	public List<Classifier> getMeaningOppositeRelations(String relationTypeCode, String classifLabelLang, String classifLabelType) {

		return create
				.select(MEANING_REL_TYPE_LABEL.CODE, MEANING_REL_TYPE_LABEL.VALUE)
				.from(MEANING_REL_MAPPING, MEANING_REL_TYPE_LABEL)
				.where(
						MEANING_REL_MAPPING.CODE1.eq(relationTypeCode)
								.and(MEANING_REL_TYPE_LABEL.CODE.eq(MEANING_REL_MAPPING.CODE2))
								.and(MEANING_REL_TYPE_LABEL.LANG.eq(classifLabelLang))
								.and(MEANING_REL_TYPE_LABEL.TYPE.eq(classifLabelType)))
				.fetchInto(Classifier.class);
	}

	public int getWordLexemesMaxLevel1(Long wordId, String datasetCode) {

		return create
				.select(DSL.max(LEXEME.LEVEL1))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.eq(wordId)
								.and(LEXEME.DATASET_CODE.eq(datasetCode))
								.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))
				.fetchOptionalInto(Integer.class)
				.orElse(0);
	}

	public LexemeType getLexemeType(Long lexemeId) {

		return create
				.select(LEXEME.TYPE)
				.from(LEXEME)
				.where(LEXEME.ID.eq(lexemeId))
				.fetchSingleInto(LexemeType.class);
	}

	public boolean meaningHasWord(Long meaningId, String wordValue, String language) {

		return create
				.select(DSL.field(DSL.count(WORD.ID).gt(0)).as("word_exists"))
				.from(LEXEME, WORD, PARADIGM, FORM)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
						.and(LEXEME.WORD_ID.eq(WORD.ID))
						.and(PARADIGM.WORD_ID.eq(WORD.ID))
						.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
						.and(FORM.MODE.eq(FormMode.WORD.name()))
						.and(FORM.VALUE.eq(wordValue))
						.and(WORD.LANG.eq(language))
						)
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordHasForms(Long wordId) {

		return create
				.select(field(DSL.count(FORM.ID).gt(0)).as("has_forms"))
				.from(PARADIGM, FORM)
				.where(
						PARADIGM.WORD_ID.eq(wordId)
								.and(FORM.PARADIGM_ID.eq(PARADIGM.ID))
								.and(FORM.MODE.eq(FormMode.FORM.name())))
				.fetchSingleInto(Boolean.class);
	}

	public boolean isOnlyLexemeForWord(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = create
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.WORD_ID.eq(l2.WORD_ID))
										.and(l1.ID.ne(l2.ID))));
		return count == 0;
	}

	public boolean isOnlyLexemeForMeaning(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = create
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.MEANING_ID.eq(l2.MEANING_ID))
										.and(l1.ID.ne(l2.ID))));
		return count == 0;
	}

	public boolean isOnlyPrimaryLexemeForWord(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = create
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.WORD_ID.eq(l2.WORD_ID))
										.and(l1.ID.ne(l2.ID))
										.and(l1.TYPE.eq(LexemeType.PRIMARY.name()))
										.and(l2.TYPE.eq(LexemeType.PRIMARY.name()))));
		return count == 0;
	}

	public boolean isOnlyPrimaryLexemeForMeaning(Long lexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		int count = create
				.fetchCount(DSL
						.select(l2.ID)
						.from(l1, l2)
						.where(
								l1.ID.eq(lexemeId)
										.and(l1.MEANING_ID.eq(l2.MEANING_ID))
										.and(l1.ID.ne(l2.ID))
										.and(l1.TYPE.eq(LexemeType.PRIMARY.name()))
										.and(l2.TYPE.eq(LexemeType.PRIMARY.name()))));
		return count == 0;
	}

	public boolean isOnlyLexemesForMeaning(Long meaningId, String datasetCode) {

		boolean noOtherDatasetsExist = create
				.select(DSL.field(DSL.count(LEXEME.ID).eq(0)).as("no_other_datasets_exist"))
				.from(LEXEME)
				.where(LEXEME.MEANING_ID.eq(meaningId).and(LEXEME.DATASET_CODE.ne(datasetCode)))
				.fetchSingleInto(Boolean.class);

		return noOtherDatasetsExist;
	}

	public boolean isOnlyPrimaryLexemesForWords(Long meaningId, String datasetCode) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");

		boolean noOtherMeaningsExist = create
				.select(DSL.field(DSL.countDistinct(l2.WORD_ID).eq(0)).as("no_other_meanings_exist"))
				.from(l1, l2)
				.where(
						l1.MEANING_ID.eq(meaningId)
								.and(l1.WORD_ID.eq(l2.WORD_ID))
								.and(l1.DATASET_CODE.eq(datasetCode))
								.and(l1.ID.ne(l2.ID))
								.and(l1.TYPE.eq(LexemeType.PRIMARY.name()))
								.and(l2.TYPE.eq(LexemeType.PRIMARY.name()))
								.and(DSL.or(
										l1.MEANING_ID.ne(l2.MEANING_ID),
										l1.MEANING_ID.eq(l2.MEANING_ID).and(l1.DATASET_CODE.ne(l2.DATASET_CODE))))

				)
				.fetchSingleInto(Boolean.class);

		return noOtherMeaningsExist;
	}

	public boolean isMemberOfWordRelationGroup(Long groupId, Long wordId) {
		Long id = create.select(WORD_GROUP.ID)
				.from(WORD_GROUP.join(WORD_GROUP_MEMBER).on(WORD_GROUP_MEMBER.WORD_GROUP_ID.eq(WORD_GROUP.ID)))
				.where(WORD_GROUP.ID.eq(groupId).and(WORD_GROUP_MEMBER.WORD_ID.eq(wordId)))
				.fetchOneInto(Long.class);
		boolean exists = (id != null);
		return exists;
	}

	public boolean secondaryMeaningLexemeExists(Long meaningId, String datasetCode) {

		return create
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("secondary_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.MEANING_ID.eq(meaningId)
								.and(LEXEME.TYPE.eq(LexemeType.SECONDARY.name()))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean secondaryWordLexemeExists(List<Long> wordIds, String datasetCode) {
		return create
				.select(DSL.field(DSL.count(LEXEME.ID).gt(0)).as("secondary_lexeme_exists"))
				.from(LEXEME)
				.where(
						LEXEME.WORD_ID.in(wordIds)
								.and(LEXEME.TYPE.eq(LexemeType.SECONDARY.name()))
								.and(LEXEME.DATASET_CODE.eq(datasetCode)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordLexemeExists(Long wordId, String datasetCode) {

		return create
				.select(field(DSL.count(WORD.ID).gt(0)).as("word_lexeme_exists"))
				.from(WORD)
				.where(
						WORD.ID.eq(wordId)
								.andExists(DSL
										.select(LEXEME.ID)
										.from(LEXEME)
										.where(LEXEME.WORD_ID.eq(WORD.ID)
												.and(LEXEME.DATASET_CODE.eq(datasetCode))
												.and(LEXEME.TYPE.eq(LEXEME_TYPE_PRIMARY)))))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordRelationExists(Long wordId1, Long wordId2, String relationType) {

		return create
				.select(field(DSL.count(WORD_RELATION.ID).eq(1)).as("relation_exists"))
				.from(WORD_RELATION)
				.where(
						WORD_RELATION.WORD1_ID.eq(wordId1)
								.and(WORD_RELATION.WORD2_ID.eq(wordId2))
								.and(WORD_RELATION.WORD_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean lexemeRelationExists(Long lexemeId1, Long lexemeId2, String relationType) {

		return create
				.select(field(DSL.count(LEX_RELATION.ID).eq(1)).as("relation_exists"))
				.from(LEX_RELATION)
				.where(
						LEX_RELATION.LEXEME1_ID.eq(lexemeId1)
								.and(LEX_RELATION.LEXEME2_ID.eq(lexemeId2))
								.and(LEX_RELATION.LEX_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean meaningRelationExists(Long meaningId1, Long meaningId2, String relationType) {

		return create
				.select(field(DSL.count(MEANING_RELATION.ID).eq(1)).as("relation_exists"))
				.from(MEANING_RELATION)
				.where(
						MEANING_RELATION.MEANING1_ID.eq(meaningId1)
								.and(MEANING_RELATION.MEANING2_ID.eq(meaningId2))
								.and(MEANING_RELATION.MEANING_REL_TYPE_CODE.eq(relationType)))
				.fetchSingleInto(Boolean.class);
	}

	public boolean wordPrimaryLexemesComplexityExists(Long lexemeId, Complexity complexity) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");

		return create
				.select(DSL.field(DSL.count(l2.ID).gt(0)).as("complexity_exists"))
				.from(l1, l2)
				.where(
						l1.ID.eq(lexemeId)
								.and(l2.WORD_ID.eq(l1.WORD_ID))
								.and(l2.TYPE.eq(LexemeType.PRIMARY.name()))
								.and(l2.COMPLEXITY.eq(complexity.name())))
				.fetchSingleInto(Boolean.class);
	}

}
