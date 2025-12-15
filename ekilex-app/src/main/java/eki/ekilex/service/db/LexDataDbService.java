package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_ETYMOLOGY_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_RELATION;
import static eki.ekilex.data.db.main.Tables.WORD_REL_TYPE_LABEL;

import java.util.List;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordEtymology;
import eki.ekilex.data.db.main.tables.WordEtymologyRelation;
import eki.ekilex.data.db.main.tables.WordEtymologySourceLink;
import eki.ekilex.data.db.main.tables.WordGroup;
import eki.ekilex.data.db.main.tables.WordGroupMember;
import eki.ekilex.data.db.main.tables.WordRelTypeLabel;
import eki.ekilex.data.db.main.tables.WordRelation;

@Component
public class LexDataDbService extends AbstractDataDbService {

	public List<eki.ekilex.data.WordRelation> getWordGroupMembers(Long wordId, String classifierLabelLang) {

		WordGroupMember wgrm1 = WORD_GROUP_MEMBER.as("wgrm1");
		WordGroupMember wgrm2 = WORD_GROUP_MEMBER.as("wgrm2");
		WordGroup wgr = WORD_GROUP.as("wgr");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel wrtl = WORD_REL_TYPE_LABEL.as("wrtl");

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(w2.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w2.ID);

		return mainDb
				.selectDistinct(
						wgrm2.ID,
						wgr.ID.as("group_id"),
						wgr.WORD_REL_TYPE_CODE.as("group_word_rel_type_code"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						w2.ASPECT_CODE.as("word_aspect_code"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						wrtl.VALUE.as("rel_type_label"),
						wgrm2.ORDER_BY)
				.from(
						wgr
								.innerJoin(wgrm1).on(wgrm1.WORD_GROUP_ID.eq(wgr.ID))
								.innerJoin(wgrm2).on(wgrm2.WORD_GROUP_ID.eq(wgr.ID))
								.innerJoin(w2).on(w2.ID.eq(wgrm2.WORD_ID))
								.leftOuterJoin(wrtl).on(
										wgr.WORD_REL_TYPE_CODE.eq(wrtl.CODE)
												.and(wrtl.LANG.eq(classifierLabelLang))
												.and(wrtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(wgrm1.WORD_ID.eq(wordId))
				.orderBy(wgrm2.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	public List<eki.ekilex.data.WordRelation> getWordRelations(Long wordId, String classifierLabelLang) {

		WordRelation r = WORD_RELATION.as("r");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");

		Field<String[]> wtf = queryHelper.getWordTypeCodesField(w2.ID);
		Field<Boolean> wtpf = queryHelper.getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = queryHelper.getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = queryHelper.getWordIsForeignField(w2.ID);
		Field<Boolean> wwupf = queryHelper.getPublishingField(TARGET_NAME_WW_UNIF, ENTITY_NAME_WORD_RELATION, r.ID);
		Field<Boolean> wwlpf = queryHelper.getPublishingField(TARGET_NAME_WW_LITE, ENTITY_NAME_WORD_RELATION, r.ID);
		Field<Boolean> wwopf = queryHelper.getPublishingField(TARGET_NAME_WW_OS, ENTITY_NAME_WORD_RELATION, r.ID);

		return mainDb
				.selectDistinct(
						r.ID.as("id"),
						w2.ID.as("word_id"),
						w2.VALUE.as("word_value"),
						w2.VALUE_PRESE.as("word_value_prese"),
						w2.LANG.as("word_lang"),
						wtf.as("word_type_codes"),
						wtpf.as("prefixoid"),
						wtsf.as("suffixoid"),
						wtz.as("foreign"),
						r.WORD_REL_TYPE_CODE.as("rel_type_code"),
						rtl.VALUE.as("rel_type_label"),
						r.RELATION_STATUS,
						r.ORDER_BY,
						wwupf.as("is_ww_unif"),
						wwlpf.as("is_ww_lite"),
						wwopf.as("is_ww_os"))
				.from(
						r
								.innerJoin(w2).on(
										w2.ID.eq(r.WORD2_ID)
												.andExists(DSL
														.select(l2.ID)
														.from(l2)
														.where(l2.WORD_ID.eq(w2.ID))))
								.leftOuterJoin(rtl).on(
										r.WORD_REL_TYPE_CODE.eq(rtl.CODE)
												.and(rtl.LANG.eq(classifierLabelLang))
												.and(rtl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(r.WORD1_ID.eq(wordId))
				.orderBy(r.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	@Deprecated
	public List<WordEtymTuple> getWordEtymology(Long wordId) {

		WordEtymology we = WORD_ETYMOLOGY.as("we");
		WordEtymologySourceLink wesl = WORD_ETYMOLOGY_SOURCE_LINK.as("wesl");
		WordEtymologyRelation wer = WORD_ETYMOLOGY_RELATION.as("wer");
		Word w2 = WORD.as("w2");
		Source s = SOURCE.as("s");

		return mainDb
				.select(
						we.ID.as("word_etym_id"),
						we.ETYMOLOGY_TYPE_CODE,
						we.ETYMOLOGY_YEAR,
						we.COMMENT_PRESE.as("word_etym_comment"),
						we.IS_QUESTIONABLE.as("word_etym_questionable"),
						wesl.ID.as("word_etym_source_link_id"),
						s.ID.as("word_etym_source_id"),
						s.NAME.as("word_etym_source_name"),
						wer.ID.as("word_etym_rel_id"),
						wer.COMMENT_PRESE.as("word_etym_rel_comment"),
						wer.IS_QUESTIONABLE.as("word_etym_rel_questionable"),
						wer.IS_COMPOUND.as("word_etym_rel_compound"),
						w2.ID.as("related_word_id"),
						w2.VALUE.as("related_word"),
						w2.LANG.as("related_word_lang"))
				.from(we
						.leftOuterJoin(wesl).on(wesl.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(s).on(wesl.SOURCE_ID.eq(s.ID))
						.leftOuterJoin(wer).on(wer.WORD_ETYM_ID.eq(we.ID))
						.leftOuterJoin(w2).on(w2.ID.eq(wer.RELATED_WORD_ID)))
				.where(we.WORD_ID.eq(wordId))
				.orderBy(we.ORDER_BY, wesl.ORDER_BY, wer.ORDER_BY)
				.fetchInto(WordEtymTuple.class);
	}

}
