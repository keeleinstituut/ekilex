package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_FORM;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_ETYMOLOGY;
import static eki.wordweb.data.db.Tables.MVIEW_WW_WORD_RELATION;

import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.DbConstant;
import eki.common.constant.FormMode;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Form;
import eki.wordweb.data.Word;
import eki.wordweb.data.WordEtymTuple;
import eki.wordweb.data.WordRelationTuple;
import eki.wordweb.data.db.tables.MviewWwForm;
import eki.wordweb.data.db.tables.MviewWwWord;
import eki.wordweb.data.db.tables.MviewWwWordEtymology;
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
						w.DEFINITIONS,
						w.OD_WORD_RECOMMENDATIONS)
				.from(w)
				.where(w.WORD_ID.eq(wordId))
				.fetchOne()
				.into(Word.class);
	}

	public Map<Long, List<Form>> getWordForms(Long wordId, Integer maxDisplayLevel) {

		MviewWwForm f = MVIEW_WW_FORM.as("f");

		Condition where = f.WORD_ID.eq(wordId).and(f.MODE.in(FormMode.WORD.name(), FormMode.FORM.name()));
		if (maxDisplayLevel != null) {
			where = where.and(f.DISPLAY_LEVEL.le(maxDisplayLevel));
		}

		return create
				.select(
						f.PARADIGM_ID,
						f.INFLECTION_TYPE,
						f.FORM_ID,
						f.MODE,
						f.MORPH_GROUP1,
						f.MORPH_GROUP2,
						f.MORPH_GROUP3,
						f.DISPLAY_LEVEL,
						f.MORPH_CODE,
						f.MORPH_EXISTS,
						f.FORM,
						f.COMPONENTS,
						f.DISPLAY_FORM,
						f.VOCAL_FORM,
						f.AUDIO_FILE,
						f.ORDER_BY)
				.from(f)
				.where(where)
				.orderBy(f.PARADIGM_ID, f.ORDER_BY, f.FORM_ID)
				.fetchGroups(f.PARADIGM_ID, Form.class);
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


	public List<WordEtymTuple> getWordEtymologyTuples(Long wordId) {

		MviewWwWordEtymology we = MVIEW_WW_WORD_ETYMOLOGY.as("we");

		return create
				.select(
						we.WORD_ID,
						we.WORD_ETYM_ID,
						we.WORD_ETYM_WORD_ID,
						we.WORD_ETYM_WORD,
						we.WORD_ETYM_WORD_LANG,
						we.WORD_ETYM_WORD_MEANING_WORDS,
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.WORD_ETYM_COMMENT,
						we.WORD_ETYM_IS_QUESTIONABLE,
						we.WORD_ETYM_SOURCES,
						we.WORD_ETYM_RELATIONS)
				.from(we)
				.where(we.WORD_ID.eq(wordId))
				.orderBy(we.WORD_ETYM_ORDER_BY)
				.fetchInto(WordEtymTuple.class);
	}
}
