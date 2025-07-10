package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_OD_LEXEME_MEANING;
import static eki.wordweb.data.db.Tables.MVIEW_OD_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_OD_WORD_OD_MORPH;
import static eki.wordweb.data.db.Tables.MVIEW_OD_WORD_OD_RECOMMEND;
import static eki.wordweb.data.db.Tables.MVIEW_OD_WORD_OD_USAGE;
import static eki.wordweb.data.db.Tables.MVIEW_OD_WORD_RELATION;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.wordweb.data.db.tables.MviewOdLexemeMeaning;
import eki.wordweb.data.db.tables.MviewOdWord;
import eki.wordweb.data.db.tables.MviewOdWordOdMorph;
import eki.wordweb.data.db.tables.MviewOdWordOdRecommend;
import eki.wordweb.data.db.tables.MviewOdWordOdUsage;
import eki.wordweb.data.db.tables.MviewOdWordRelation;
import eki.wordweb.data.od.OdWord;

@Component
public class OdDbService {

	@Autowired
	private DSLContext create;

	public List<OdWord> getWords(String searchValue) {

		Field<String> searchValueLowerField = DSL.lower(searchValue);

		MviewOdWord w = MVIEW_OD_WORD.as("w");
		MviewOdLexemeMeaning lm = MVIEW_OD_LEXEME_MEANING.as("lm");

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
						w.VALUE,
						w.HOMONYM_NR)
				.fetchInto(OdWord.class);
	}

	public OdWord getOdWord(Long wordId) {

		MviewOdWord w = MVIEW_OD_WORD.as("w");
		MviewOdWordOdMorph wom = MVIEW_OD_WORD_OD_MORPH.as("wom");
		MviewOdWordOdUsage wou = MVIEW_OD_WORD_OD_USAGE.as("wou");
		MviewOdWordOdRecommend wor = MVIEW_OD_WORD_OD_RECOMMEND.as("wor");
		MviewOdLexemeMeaning lm = MVIEW_OD_LEXEME_MEANING.as("lm");
		MviewOdWordRelation wr = MVIEW_OD_WORD_RELATION.as("wr");

		Field<JSON> womf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("wordId").value(wom.WORD_ID),
								DSL.key("wordOdMorphId").value(wom.WORD_OD_MORPH_ID),
								DSL.key("value").value(wom.VALUE),
								DSL.key("valuePrese").value(wom.VALUE_PRESE)))
				.from(wom)
				.where(wom.WORD_ID.eq(w.WORD_ID))
				.limit(1)
				.asField();

		Field<JSON> wouf = DSL
				.select(wou.WORD_OD_USAGES)
				.from(wou)
				.where(wou.WORD_ID.eq(w.WORD_ID))
				.asField();

		Field<JSON> worf = DSL
				.select(DSL
						.jsonObject(
								DSL.key("wordId").value(wor.WORD_ID),
								DSL.key("wordOdRecommendId").value(wor.WORD_OD_RECOMMEND_ID),
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
						womf.as("word_od_morph"),
						wouf.as("word_od_usages"),
						worf.as("word_od_recommend"),
						lmf.as("lexeme_meanings"),
						wrf.as("word_relation_groups"))
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOptionalInto(OdWord.class)
				.orElse(null);
	}
}
