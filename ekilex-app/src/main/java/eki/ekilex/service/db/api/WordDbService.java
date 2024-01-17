package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION;
import static eki.ekilex.data.db.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.Tables.DEFINITION_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.FREEFORM;
import static eki.ekilex.data.db.Tables.FREEFORM_SOURCE_LINK;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.LEXEME_FREEFORM;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.SOURCE;
import static eki.ekilex.data.db.Tables.WORD;
import static eki.ekilex.data.db.Tables.WORD_FORUM;
import static eki.ekilex.data.db.Tables.WORD_RELATION;
import static eki.ekilex.data.db.Tables.WORD_WORD_TYPE;

import java.util.List;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.common.constant.FreeformType;
import eki.ekilex.data.api.LexWord;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Definition;
import eki.ekilex.data.db.tables.DefinitionDataset;
import eki.ekilex.data.db.tables.DefinitionSourceLink;
import eki.ekilex.data.db.tables.Freeform;
import eki.ekilex.data.db.tables.FreeformSourceLink;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.LexemeFreeform;
import eki.ekilex.data.db.tables.Meaning;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Source;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.WordForum;
import eki.ekilex.data.db.tables.WordRelation;
import eki.ekilex.data.db.tables.WordWordType;
import eki.ekilex.data.db.tables.records.WordRecord;
import eki.ekilex.service.db.AbstractDataDbService;

@Component
public class WordDbService extends AbstractDataDbService {

	public List<eki.ekilex.data.api.Word> getPublicWords(String datasetCode) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		Paradigm p = PARADIGM.as("p");

		Field<Boolean> me = DSL.field(DSL
				.select(DSL.field(DSL.count(p.ID).gt(0)))
				.from(p)
				.where(p.WORD_ID.eq(w.ID).and(p.WORD_CLASS.isNotNull())));

		return create
				.select(
						w.ID.as("word_id"),
						w.VALUE,
						w.LANG,
						me.as("morph_exists"))
				.from(w)
				.where(
						w.IS_PUBLIC.isTrue()
								.andExists(DSL
										.select(l.ID)
										.from(l, ds)
										.where(
												l.WORD_ID.eq(w.ID)
														.and(l.DATASET_CODE.eq(datasetCode))
														.and(l.IS_PUBLIC.isTrue())
														.and(l.DATASET_CODE.eq(ds.CODE))
														.and(ds.IS_PUBLIC.isTrue()))))
				.orderBy(w.VALUE, w.HOMONYM_NR)
				.fetchInto(eki.ekilex.data.api.Word.class);
	}

	public List<Long> getWordsIds(String wordValue, String datasetCode, String lang) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");

		return create
				.select(w.ID)
				.from(w, l)
				.where(
						w.VALUE.eq(wordValue)
								.and(w.LANG.eq(lang))
								.and(w.IS_PUBLIC.isTrue())
								.and(w.ID.eq(l.WORD_ID))
								.and(l.DATASET_CODE.eq(datasetCode)))
				.groupBy(w.ID)
				.fetchInto(Long.class);
	}

	public LexWord getLexWord(Long wordId, String datasetCode) {

		Word w = WORD.as("w");
		WordWordType wwt = WORD_WORD_TYPE.as("wwt");
		WordRelation wr = WORD_RELATION.as("wr");
		WordForum wfor = WORD_FORUM.as("wfor");

		Lexeme l = LEXEME.as("l");
		LexemeFreeform lff = LEXEME_FREEFORM.as("lff");
		Meaning m = MEANING.as("m");

		Definition d = DEFINITION.as("d");
		DefinitionDataset dd = DEFINITION_DATASET.as("dd");
		DefinitionSourceLink dsl = DEFINITION_SOURCE_LINK.as("dsl");
		Freeform ff = FREEFORM.as("ff");
		FreeformSourceLink ffsl = FREEFORM_SOURCE_LINK.as("ffsl");
		Source s = SOURCE.as("s");

		Field<JSON> ffslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("sourceLinkId").value(ffsl.ID),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("sourceLinkName").value(ffsl.NAME),
										DSL.key("type").value(ffsl.TYPE)))
						.orderBy(ffsl.ORDER_BY))
				.from(ffsl, s)
				.where(
						ffsl.FREEFORM_ID.eq(ff.ID)
								.and(ffsl.SOURCE_ID.eq(s.ID)))
				.asField();

		Field<JSON> dslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("sourceLinkId").value(dsl.ID),
										DSL.key("sourceId").value(s.ID),
										DSL.key("sourceName").value(s.NAME),
										DSL.key("sourceLinkName").value(dsl.NAME),
										DSL.key("type").value(dsl.TYPE)))
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
										DSL.key("definitionId").value(d.ID),
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

		Field<JSON> uf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ff.ID),
										DSL.key("value").value(ff.VALUE_TEXT),
										DSL.key("lang").value(ff.LANG),
										DSL.key("publicity").value(ff.IS_PUBLIC),
										DSL.key("sourceLinks").value(ffslf)))
						.orderBy(ff.ORDER_BY))
				.from(ff, lff)
				.where(
						lff.LEXEME_ID.eq(l.ID)
								.and(lff.FREEFORM_ID.eq(ff.ID))
								.and(ff.TYPE.eq(FreeformType.USAGE.name())))
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

		return create
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

		WordRecord wordRecord = create.newRecord(WORD);
		updateWordRecord(wordRecord, word, valueAsWord, homonymNr);
		wordRecord.store();

		return wordRecord.getId();
	}

	public void updateWord(LexWord word, String valueAsWord) {

		Long wordId = word.getWordId();
		WordRecord wordRecord = create.fetchOne(WORD, WORD.ID.eq(wordId));
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
		wordRecord.setIsWord(Boolean.TRUE);
		wordRecord.setIsCollocation(Boolean.FALSE);
		if (homonymNr != null) {
			wordRecord.setHomonymNr(homonymNr);
		}
	}
}