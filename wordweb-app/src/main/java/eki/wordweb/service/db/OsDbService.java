package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.OS_LEXEME_MEANING;
import static eki.wordweb.data.db.Tables.OS_WORD;
import static eki.wordweb.data.db.Tables.OS_WORD_OS_MORPH;
import static eki.wordweb.data.db.Tables.OS_WORD_OS_RECOMMEND;
import static eki.wordweb.data.db.Tables.OS_WORD_OS_USAGE;
import static eki.wordweb.data.db.Tables.OS_WORD_RELATION;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.db.tables.OsLexemeMeaning;
import eki.wordweb.data.db.tables.OsWord;
import eki.wordweb.data.db.tables.OsWordOsMorph;
import eki.wordweb.data.db.tables.OsWordOsRecommend;
import eki.wordweb.data.db.tables.OsWordOsUsage;
import eki.wordweb.data.db.tables.OsWordRelation;

@Component
public class OsDbService {

	@Autowired
	private DSLContext create;

	public List<eki.wordweb.data.os.OsWord> getWords(String searchValue) {

		Field<String> searchValueLowerField = DSL.lower(searchValue);

		OsWord w = OS_WORD.as("w");
		OsLexemeMeaning lm = OS_LEXEME_MEANING.as("lm");

		Field<JSON> lmf = DSL
				.select(lm.LEXEME_MEANINGS)
				.from(lm)
				.where(lm.WORD_ID.eq(w.WORD_ID))
				.asField();

		return create
				.select(w.fields())
				.select(lmf.as("lexeme_meanings"))
				.from(w)
				.where(DSL.lower(w.VALUE).eq(searchValueLowerField))
				.orderBy(
						w.VALUE.collate("fi_FI"),
						w.HOMONYM_NR)
				.fetchInto(eki.wordweb.data.os.OsWord.class);
	}

	public eki.wordweb.data.os.OsWord getOdWord(Long wordId) {

		OsWord w = OS_WORD.as("w");
		OsWordOsMorph wom = OS_WORD_OS_MORPH.as("wom");
		OsWordOsUsage wou = OS_WORD_OS_USAGE.as("wou");
		OsWordOsRecommend wor = OS_WORD_OS_RECOMMEND.as("wor");
		OsLexemeMeaning lm = OS_LEXEME_MEANING.as("lm");
		OsWordRelation wr = OS_WORD_RELATION.as("wr");

		Field<JSON> womf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("wordId").value(wom.WORD_ID),
								DSL.key("wordOsMorphId").value(wom.WORD_OS_MORPH_ID),
								DSL.key("value").value(wom.VALUE),
								DSL.key("valuePrese").value(wom.VALUE_PRESE)))
				.from(wom)
				.where(wom.WORD_ID.eq(w.WORD_ID))
				.limit(1)
				.asField();

		Field<JSON> wouf = DSL
				.select(wou.WORD_OS_USAGES)
				.from(wou)
				.where(wou.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<JSON> worf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("wordId").value(wor.WORD_ID),
								DSL.key("wordOsRecommendId").value(wor.WORD_OS_RECOMMEND_ID),
								DSL.key("value").value(wor.VALUE),
								DSL.key("valuePrese").value(wor.VALUE_PRESE),
								DSL.key("optValue").value(wor.OPT_VALUE),
								DSL.key("optValuePrese").value(wor.OPT_VALUE_PRESE)))
				.from(wor)
				.where(wor.WORD_ID.eq(w.WORD_ID))
				.limit(1)
				.asField();

		Field<JSON> lmf = DSL
				.select(lm.LEXEME_MEANINGS)
				.from(lm)
				.where(lm.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<JSON> wrf = DSL
				.select(wr.WORD_RELATION_GROUPS)
				.from(wr)
				.where(wr.WORD_ID.eq(w.WORD_ID))
				.asField();

		return create
				.select(w.fields())
				.select(
						womf.as("word_os_morph"),
						wouf.as("word_os_usages"),
						worf.as("word_os_recommend"),
						lmf.as("lexeme_meanings"),
						wrf.as("word_relation_groups"))
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOptionalInto(eki.wordweb.data.os.OsWord.class)
				.orElse(null);
	}
}
