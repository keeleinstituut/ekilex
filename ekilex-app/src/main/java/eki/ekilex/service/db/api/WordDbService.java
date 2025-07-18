package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MEANING;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE;
import static eki.ekilex.data.db.main.Tables.USAGE_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_FORUM;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_WORD_TYPE;
import static eki.ekilex.data.db.main.Tables.WORD_TAG;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.api.LexWord;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.DefinitionSourceLink;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Meaning;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.Usage;
import eki.ekilex.data.db.main.tables.UsageSourceLink;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordForum;
import eki.ekilex.data.db.main.tables.WordRelation;
import eki.ekilex.data.db.main.tables.WordTag;
import eki.ekilex.data.db.main.tables.WordWordType;
import eki.ekilex.data.db.main.tables.records.WordRecord;
import eki.ekilex.service.db.AbstractDataDbService;

@Component
public class WordDbService extends AbstractDataDbService {

	public List<eki.ekilex.data.api.Word> getPublicWords(String datasetCode, String tagName) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		Paradigm p = PARADIGM.as("p");
		WordTag wt = WORD_TAG.as("wt");

		Field<String[]> wtf = queryHelper.getWordTagsField(w.ID);

		Field<Boolean> mef = DSL.field(DSL
				.exists(DSL
						.select(p.ID)
						.from(p)
						.where(
								p.WORD_ID.eq(w.ID)
										.and(p.WORD_CLASS.isNotNull()))));

		Condition where = w.IS_PUBLIC.isTrue()
				.andExists(DSL
						.select(l.ID)
						.from(l, ds)
						.where(
								l.WORD_ID.eq(w.ID)
										.and(l.DATASET_CODE.eq(datasetCode))
										.and(l.IS_PUBLIC.isTrue())
										.and(l.IS_WORD.isTrue())
										.and(l.DATASET_CODE.eq(ds.CODE))
										.and(ds.IS_PUBLIC.isTrue())));

		if (StringUtils.isNotBlank(tagName)) {
			where = where.andExists(DSL
					.select(wt.ID)
					.from(wt)
					.where(
							wt.WORD_ID.eq(w.ID)
									.and(wt.TAG_NAME.eq(tagName))));
		}

		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE,
						w.VALUE_PRESE,
						w.LANG,
						w.HOMONYM_NR,
						w.DISPLAY_MORPH_CODE,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.VOCAL_FORM,
						w.MORPHOPHONO_FORM,
						wtf.as("tags"),
						mef.as("morph_exists"))
				.from(w)
				.where(where)
				.orderBy(
						w.VALUE,
						w.HOMONYM_NR)
				.fetchInto(eki.ekilex.data.api.Word.class);
	}

	public List<Long> getWordsIds(String wordValue, String datasetCode, String lang) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		return mainDb
				.select(w.ID)
				.from(w, l)
				.where(
						w.VALUE.eq(wordValue)
								.and(w.LANG.eq(lang))
								.and(w.IS_PUBLIC.isTrue())
								.and(w.ID.eq(l.WORD_ID))
								.and(l.DATASET_CODE.eq(datasetCode))
								.and(l.IS_WORD.isTrue()))
				.groupBy(w.ID)
				.fetchInto(Long.class);
	}

	public LexWord getLexWord(Long wordId, String datasetCode) {

		Word w = WORD.as("w");
		WordWordType wwt = WORD_WORD_TYPE.as("wwt");
		WordRelation wr = WORD_RELATION.as("wr");
		WordForum wfor = WORD_FORUM.as("wfor");
		Lexeme l = LEXEME.as("l");
		Usage u = USAGE.as("u");
		UsageSourceLink usl = USAGE_SOURCE_LINK.as("usl");
		Meaning m = MEANING.as("m");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Source s = SOURCE.as("s");

		Field<JSON> dslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(dsl.ID),
										DSL.key("name").value(dsl.NAME),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(dsl.ORDER_BY)))
						.orderBy(dsl.ORDER_BY))
				.from(dsl, s)
				.where(
						dsl.DEFINITION_ID.eq(d.ID)
								.and(dsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> wwtf = DSL
				.select(DSL
						.jsonArrayAgg(wwt.WORD_TYPE_CODE)
						.orderBy(wwt.ORDER_BY))
				.from(wwt)
				.where(wwt.WORD_ID.eq(w.ID))
				.asField();

		Field<JSON> wforf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wfor.ID),
										DSL.key("value").value(wfor.VALUE)))
						.orderBy(wfor.ORDER_BY))
				.from(wfor)
				.where(wfor.WORD_ID.eq(w.ID))
				.asField();

		Field<JSON> wrf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wr.ID),
										DSL.key("wordId").value(wr.WORD1_ID),
										DSL.key("targetWordId").value(wr.WORD2_ID),
										DSL.key("relationTypeCode").value(wr.WORD_REL_TYPE_CODE)))
						.orderBy(wr.ORDER_BY))
				.from(wr)
				.where(wr.WORD1_ID.eq(w.ID))
				.asField();

		Field<JSON> df = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(d.ID),
										DSL.key("value").value(d.VALUE),
										DSL.key("lang").value(d.LANG),
										DSL.key("definitionTypeCode").value(d.DEFINITION_TYPE_CODE),
										DSL.key("sourceLinks").value(dslf)))
						.orderBy(d.ORDER_BY))
				.from(d)
				.where(
						d.MEANING_ID.eq(m.ID)
								.andExists(DSL
										.select(dd.DEFINITION_ID)
										.from(dd)
										.where(
												dd.DEFINITION_ID.eq(d.ID)
														.and(dd.DATASET_CODE.eq(datasetCode)))))
				.asField();

		Field<JSON> uslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(usl.ID),
										DSL.key("name").value(usl.NAME),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("orderBy").value(usl.ORDER_BY)))
						.orderBy(usl.ORDER_BY))
				.from(usl, s)
				.where(
						usl.USAGE_ID.eq(u.ID)
								.and(usl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> uf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("public").value(u.IS_PUBLIC),
										DSL.key("sourceLinks").value(uslf)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(l.ID))
				.asField();

		Field<JSON> mf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("lexemeId").value(l.ID),
										DSL.key("meaningId").value(m.ID),
										DSL.key("definitions").value(df),
										DSL.key("usages").value(uf)))
						.orderBy(l.ORDER_BY))
				.from(m, l)
				.where(
						l.WORD_ID.eq(w.ID)
								.and(l.MEANING_ID.eq(m.ID))
								.and(l.DATASET_CODE.eq(datasetCode)))
				.asField();

		return mainDb
				.select(
						w.ID.as("word_id"),
						DSL.val(datasetCode).as("dataset_code"),
						w.VALUE.as("word_value"),
						w.VALUE_PRESE.as("word_value_prese"),
						w.LANG,
						w.GENDER_CODE,
						w.ASPECT_CODE,
						w.VOCAL_FORM,
						w.MORPHOPHONO_FORM,
						w.DISPLAY_MORPH_CODE,
						wwtf.as("word_type_codes"),
						wforf.as("forums"),
						wrf.as("relations"),
						mf.as("meanings"))
				.from(w)
				.where(w.ID.eq(wordId))
				.fetchOptionalInto(LexWord.class)
				.orElse(null);
	}

	public Long createWord(LexWord word, String valueAsWord) {

		String value = word.getWordValue();
		String lang = word.getLang();
		int homonymNr = getWordNextHomonymNr(value, lang);

		WordRecord wordRecord = mainDb.newRecord(WORD);
		updateWordRecord(wordRecord, word, valueAsWord, homonymNr);
		wordRecord.store();

		return wordRecord.getId();
	}

	public void updateWord(LexWord word, String valueAsWord) {

		Long wordId = word.getWordId();
		WordRecord wordRecord = mainDb.fetchOne(WORD, WORD.ID.eq(wordId));
		updateWordRecord(wordRecord, word, valueAsWord, null);
		wordRecord.store();
	}

	private void updateWordRecord(WordRecord wordRecord, LexWord word, String valueAsWord, Integer homonymNr) {

		wordRecord.setValue(word.getWordValue());
		wordRecord.setValuePrese(word.getWordValuePrese());
		wordRecord.setValueAsWord(valueAsWord);
		wordRecord.setLang(word.getLang());
		wordRecord.setDisplayMorphCode(word.getDisplayMorphCode());
		wordRecord.setGenderCode(word.getGenderCode());
		wordRecord.setAspectCode(word.getAspectCode());
		wordRecord.setVocalForm(word.getVocalForm());
		wordRecord.setMorphophonoForm(word.getMorphophonoForm());
		if (homonymNr != null) {
			wordRecord.setHomonymNr(homonymNr);
		}
	}
}