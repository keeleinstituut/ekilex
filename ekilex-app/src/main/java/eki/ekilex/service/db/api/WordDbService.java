package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.LEXEME;
import static eki.ekilex.data.db.Tables.MEANING;
import static eki.ekilex.data.db.Tables.PARADIGM;
import static eki.ekilex.data.db.Tables.WORD;

import java.util.List;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordLexemeMeaningIdTuple;
import eki.ekilex.data.db.tables.Dataset;
import eki.ekilex.data.db.tables.Lexeme;
import eki.ekilex.data.db.tables.Paradigm;
import eki.ekilex.data.db.tables.Word;
import eki.ekilex.data.db.tables.records.LexemeRecord;
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

	public WordLexemeMeaningIdTuple createWordAndLexemeAndMeaning(eki.ekilex.data.api.Word word, String valueAsWord) {

		WordLexemeMeaningIdTuple wordLexemeMeaningId = new WordLexemeMeaningIdTuple();
		Long meaningId = word.getMeaningId();
		String lexemeDataset = word.getLexemeDataset();
		String value = word.getValue();
		String valuePrese = word.getValuePrese();
		String lang = word.getLang();
		String displayMorphCode = word.getDisplayMorphCode();
		String genderCode = word.getGenderCode();
		String aspectCode = word.getAspectCode();
		String vocalForm = word.getVocalForm();
		String morphophonoForm = word.getMorphophonoForm();
		int homonymNr = getWordNextHomonymNr(value, lang);

		WordRecord wordRecord = create.newRecord(WORD);
		wordRecord.setValue(value);
		wordRecord.setValuePrese(valuePrese);
		wordRecord.setValueAsWord(valueAsWord);
		wordRecord.setLang(lang);
		wordRecord.setHomonymNr(homonymNr);
		wordRecord.setDisplayMorphCode(displayMorphCode);
		wordRecord.setGenderCode(genderCode);
		wordRecord.setAspectCode(aspectCode);
		wordRecord.setVocalForm(vocalForm);
		wordRecord.setMorphophonoForm(morphophonoForm);
		wordRecord.store();
		Long wordId = wordRecord.getId();

		if (meaningId == null) {
			meaningId = create.insertInto(MEANING).defaultValues().returning(MEANING.ID).fetchOne().getId();
		}

		LexemeRecord lexemeRecord = create.newRecord(LEXEME);
		lexemeRecord.setMeaningId(meaningId);
		lexemeRecord.setWordId(wordId);
		lexemeRecord.setDatasetCode(lexemeDataset);
		lexemeRecord.setLevel1(1);
		lexemeRecord.setLevel2(1);
		lexemeRecord.setIsPublic(PUBLICITY_PUBLIC);
		lexemeRecord.setComplexity(COMPLEXITY_DETAIL);
		lexemeRecord.store();
		Long lexemeId = lexemeRecord.getId();

		wordLexemeMeaningId.setWordId(wordId);
		wordLexemeMeaningId.setLexemeId(lexemeId);
		wordLexemeMeaningId.setMeaningId(meaningId);

		return wordLexemeMeaningId;
	}

	public void updateWord(eki.ekilex.data.api.Word word, String valueAsWord) {

		Long wordId = word.getWordId();
		String value = word.getValue();
		String valuePrese = word.getValuePrese();
		String lang = word.getLang();
		String displayMorphCode = word.getDisplayMorphCode();
		String genderCode = word.getGenderCode();
		String aspectCode = word.getAspectCode();
		String vocalForm = word.getVocalForm();
		String morphophonoForm = word.getMorphophonoForm();

		create.update(WORD)
				.set(WORD.VALUE, value)
				.set(WORD.VALUE_PRESE, valuePrese)
				.set(WORD.VALUE_AS_WORD, valueAsWord)
				.set(WORD.LANG, lang)
				.set(WORD.DISPLAY_MORPH_CODE, displayMorphCode)
				.set(WORD.GENDER_CODE, genderCode)
				.set(WORD.ASPECT_CODE, aspectCode)
				.set(WORD.VOCAL_FORM, vocalForm)
				.set(WORD.MORPHOPHONO_FORM, morphophonoForm)
				.where(WORD.ID.eq(wordId))
				.execute();
	}

}