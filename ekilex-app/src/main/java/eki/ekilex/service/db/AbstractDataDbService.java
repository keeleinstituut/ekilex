package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATASET;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import eki.common.constant.FreeformConstant;
import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.SimpleWord;
import eki.ekilex.data.db.main.tables.Dataset;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.service.db.util.QueryHelper;

public abstract class AbstractDataDbService implements SystemConstant, GlobalConstant, FreeformConstant {

	@Autowired
	protected DSLContext mainDb;

	@Autowired
	protected QueryHelper queryHelper;

	public SimpleWord getSimpleWord(Long wordId) {
		Word w = WORD.as("w");
		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.LANG)
				.from(w)
				.where(w.ID.eq(wordId))
				.fetchOneInto(SimpleWord.class);
	}

	public SimpleWord getLexemeSimpleWord(Long lexemeId) {
		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("word_value"),
						w.LANG)
				.from(l, w)
				.where(l.ID.eq(lexemeId).and(l.WORD_ID.eq(w.ID)))
				.fetchOneInto(SimpleWord.class);
	}

	public List<String> getWordsValues(List<Long> wordIds) {
		return mainDb
				.select(WORD.VALUE)
				.from(WORD)
				.where(WORD.ID.in(wordIds))
				.fetchInto(String.class);
	}

	public List<String> getLexemesWordValues(List<Long> lexemeIds) {
		return mainDb
				.select(WORD.VALUE)
				.from(LEXEME, WORD)
				.where(LEXEME.ID.in(lexemeIds).and(LEXEME.WORD_ID.eq(WORD.ID)))
				.fetchInto(String.class);
	}

	public List<String> getMeaningWordValues(Long meaningId, String... langs) {
		return mainDb
				.select(WORD.VALUE)
				.from(LEXEME, WORD)
				.where(LEXEME.MEANING_ID.eq(meaningId).and(LEXEME.WORD_ID.eq(WORD.ID)).and(WORD.LANG.in(langs)))
				.fetchInto(String.class);
	}

	public int getWordNextHomonymNr(String wordValue, String wordLang) {

		Integer currentHomonymNr = mainDb
				.select(DSL.max(WORD.HOMONYM_NR))
				.from(WORD)
				.where(
						WORD.LANG.eq(wordLang)
								.and(WORD.VALUE.eq(wordValue))
								.and(WORD.IS_PUBLIC.isTrue()))
				.fetchOneInto(Integer.class);

		int homonymNr = 1;
		if (currentHomonymNr != null) {
			homonymNr = currentHomonymNr + 1;
		}
		return homonymNr;
	}

	public eki.ekilex.data.Lexeme getLexeme(Long lexemeId, String classifierLabelLang) {

		Lexeme l = LEXEME.as("l");
		Dataset ds = DATASET.as("ds");
		List<Field<?>> lexemeFields = queryHelper.getLexemeFields(l, ds, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);

		return mainDb
				.select(lexemeFields)
				.from(l, ds)
				.where(
						l.ID.eq(lexemeId)
								.and(l.DATASET_CODE.eq(ds.CODE)))
				.fetchOptional(record -> {
					eki.ekilex.data.Lexeme pojo = record.into(eki.ekilex.data.Lexeme.class);
					queryHelper.replaceNullCollections(pojo);
					return pojo;
				})
				.orElse(null);
	}

	public eki.ekilex.data.Word getWord(Long wordId) {

		Word w = WORD.as("w");
		Lexeme l = LEXEME.as("l");
		List<Field<?>> wordFields = queryHelper.getWordFields(w);

		return mainDb
				.select(wordFields)
				.from(w)
				.where(w.ID.eq(wordId)
						.andExists(DSL
								.select(l.ID)
								.from(l)
								.where(l.WORD_ID.eq(w.ID))))
				.fetchOptionalInto(eki.ekilex.data.Word.class)
				.orElse(null);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	protected boolean fiCollationExists() {
		Integer fiCollationCnt = mainDb
				.selectCount()
				.from("pg_collation where lower(collcollate) = 'fi_fi.utf8'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
