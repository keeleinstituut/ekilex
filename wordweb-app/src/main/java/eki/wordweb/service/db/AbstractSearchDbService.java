package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.DbConstant;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordRelation;

public abstract class AbstractSearchDbService implements DbConstant, SystemConstant {

	@Autowired
	protected DSLContext create;

	public Word getWord(Long wordId) {

		MviewWwWord w = MVIEW_WW_WORD.as("w");

		return create
				.select(
						w.WORD_ID,
						w.WORD,
						w.HOMONYM_NR,
						w.WORD_CLASS,
						w.LANG,
						w.WORD_TYPE_CODES,
						w.MORPH_CODE,
						w.DISPLAY_MORPH_CODE,
						w.ASPECT_CODE,
						w.MEANING_WORDS,
						w.DEFINITIONS)
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOne()
				.into(Word.class);
	}

	public List<WordRelationTuple> getWordRelationTuples(Long wordId) {

		MviewWwWordRelation wr = MVIEW_WW_WORD_RELATION.as("wr");

		return create
				.select(
						wr.WORD_ID,
						wr.RELATED_WORDS,
						wr.WORD_GROUP_ID,
						wr.WORD_REL_TYPE_CODE,
						wr.WORD_GROUP_MEMBERS)
				.from(wr)
				.where(wr.WORD_ID.eq(wordId))
				.fetch()
				.into(WordRelationTuple.class);
	}
}
