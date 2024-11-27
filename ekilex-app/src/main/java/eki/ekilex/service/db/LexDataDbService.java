package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.POS_GROUP;
import static eki.ekilex.data.db.main.Tables.POS_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.REL_GROUP;
import static eki.ekilex.data.db.main.Tables.REL_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.SOURCE;
import static eki.ekilex.data.db.main.Tables.USAGE;
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
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import eki.ekilex.data.WordEtymTuple;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.PosGroup;
import eki.ekilex.data.db.main.tables.PosGroupLabel;
import eki.ekilex.data.db.main.tables.RelGroup;
import eki.ekilex.data.db.main.tables.RelGroupLabel;
import eki.ekilex.data.db.main.tables.Source;
import eki.ekilex.data.db.main.tables.Usage;
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

	public List<eki.ekilex.data.WordRelation> getWordGroupMembers(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordGroupMember wgrm1 = WORD_GROUP_MEMBER.as("wgrm1");
		WordGroupMember wgrm2 = WORD_GROUP_MEMBER.as("wgrm2");
		WordGroup wgr = WORD_GROUP.as("wgr");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel wrtl = WORD_REL_TYPE_LABEL.as("wrtl");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

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
												.and(wrtl.TYPE.eq(classifierLabelTypeCode))))
				.where(wgrm1.WORD_ID.eq(wordId))
				.orderBy(wgrm2.ORDER_BY)
				.fetchInto(eki.ekilex.data.WordRelation.class);
	}

	public List<eki.ekilex.data.WordRelation> getWordRelations(Long wordId, String classifierLabelLang, String classifierLabelTypeCode) {

		WordRelation r = WORD_RELATION.as("r");
		Lexeme l2 = LEXEME.as("l2");
		Word w2 = WORD.as("w2");
		WordRelTypeLabel rtl = WORD_REL_TYPE_LABEL.as("rtl");

		Field<String[]> wtf = getWordTypesField(w2.ID);
		Field<Boolean> wtpf = getWordIsPrefixoidField(w2.ID);
		Field<Boolean> wtsf = getWordIsSuffixoidField(w2.ID);
		Field<Boolean> wtz = getWordIsForeignField(w2.ID);

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
						r.ORDER_BY)
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
												.and(rtl.TYPE.eq(classifierLabelTypeCode))))
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
						wesl.TYPE.as("word_etym_source_link_type"),
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

	public boolean isCollocationsExist(Long lexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.fetchExists(DSL
						.select(cm.ID)
						.from(cm)
						.where(cm.MEMBER_LEXEME_ID.eq(lexemeId)));
	}

	public List<eki.ekilex.data.CollocPosGroup> getPrimaryCollocations(Long lexemeId, String classifierLabelLang, String classifierLabelTypeCode) {

		PosGroup pg = POS_GROUP.as("pg");
		PosGroupLabel pgl = POS_GROUP_LABEL.as("pgl");
		RelGroup rg = REL_GROUP.as("rg");
		RelGroupLabel rgl = REL_GROUP_LABEL.as("rgl");
		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		CollocationMember cm1 = COLLOCATION_MEMBER.as("cm1");
		CollocationMember cm2 = COLLOCATION_MEMBER.as("cm2");
		Word cw = WORD.as("cw");
		Word mw = WORD.as("mw");
		Lexeme cl = LEXEME.as("cl");
		Lexeme ml = LEXEME.as("ml");
		Form mf = FORM.as("mf");
		Usage u = USAGE.as("u");

		Field<JSON> usaf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("complexity").value(u.COMPLEXITY),
										DSL.key("orderBy").value(u.ORDER_BY)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(cl.ID))
				.asField();

		Field<JSON> memf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("conjunct").value(cm2.CONJUNCT),
										DSL.key("lexemeId").value(ml.ID),
										DSL.key("wordId").value(mw.ID),
										DSL.key("wordValue").value(mw.VALUE),
										DSL.key("formId").value(mf.ID),
										DSL.key("formValue").value(mf.VALUE),
										DSL.key("morphCode").value(mf.MORPH_CODE),
										DSL.key("weight").value(cm2.WEIGHT),
										DSL.key("memberOrder").value(cm2.MEMBER_ORDER)))
						.orderBy(cm2.MEMBER_ORDER))
				.from(mf, mw, ml, cm2)
				.where(
						cm2.COLLOC_LEXEME_ID.eq(cl.ID)
								.and(cm2.MEMBER_LEXEME_ID.eq(ml.ID))
								.and(cm2.MEMBER_FORM_ID.eq(mf.ID))
								.and(ml.WORD_ID.eq(mw.ID)))
				.asField();

		Field<JSON> collf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("lexemeId").value(cl.ID),
										DSL.key("wordId").value(cw.ID),
										DSL.key("wordValue").value(cw.VALUE),
										DSL.key("usages").value(usaf),
										DSL.key("members").value(memf),
										DSL.key("groupOrder").value(cm1.GROUP_ORDER)))
						.orderBy(cm1.GROUP_ORDER))
				.from(cw, cl, cm1)
				.where(
						cm1.MEMBER_LEXEME_ID.eq(lexemeId)
								.and(cm1.POS_GROUP_CODE.eq(pg.CODE))
								.and(cm1.REL_GROUP_CODE.eq(rg.CODE))
								.and(cm1.COLLOC_LEXEME_ID.eq(cl.ID))
								.and(cl.WORD_ID.eq(cw.ID)))
				.asField();

		Field<JSON> rgf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("relGroupCode").value(rg.CODE),
										DSL.key("relGroupValue").value(DSL.coalesce(rgl.VALUE, rg.CODE)),
										DSL.key("collocations").value(collf)))
						.orderBy(rg.ORDER_BY))
				.from(rg
						.leftOuterJoin(rgl).on(
								rgl.CODE.eq(rg.CODE)
										.and(rgl.LANG.eq(classifierLabelLang))
										.and(rgl.TYPE.eq(classifierLabelTypeCode))))
				.whereExists(
						DSL
								.select(cm.ID)
								.from(cm)
								.where(
										cm.MEMBER_LEXEME_ID.eq(lexemeId)
												.and(cm.POS_GROUP_CODE.eq(pg.CODE))
												.and(cm.REL_GROUP_CODE.eq(rg.CODE))))
				.asField();

		return mainDb
				.select(
						pg.CODE.as("pos_group_code"),
						DSL.coalesce(pgl.VALUE, pg.CODE).as("pos_group_value"),
						rgf.as("rel_groups"))
				.from(pg
						.leftOuterJoin(pgl).on(
								pgl.CODE.eq(pg.CODE)
										.and(pgl.LANG.eq(classifierLabelLang))
										.and(pgl.TYPE.eq(classifierLabelTypeCode))))
				.whereExists(
						DSL
								.select(cm.ID)
								.from(cm)
								.where(
										cm.MEMBER_LEXEME_ID.eq(lexemeId)
												.and(cm.POS_GROUP_CODE.eq(pg.CODE))))
				.orderBy(pg.ORDER_BY)
				.fetchInto(eki.ekilex.data.CollocPosGroup.class);

	}

	public List<eki.ekilex.data.Collocation> getSecondaryCollocations(Long lexemeId) {

		CollocationMember cm1 = COLLOCATION_MEMBER.as("cm1");
		CollocationMember cm2 = COLLOCATION_MEMBER.as("cm2");
		Word cw = WORD.as("cw");
		Word mw = WORD.as("mw");
		Lexeme cl = LEXEME.as("cl");
		Lexeme ml = LEXEME.as("ml");
		Form mf = FORM.as("mf");
		Usage u = USAGE.as("u");

		Field<JSON> usaf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(u.ID),
										DSL.key("value").value(u.VALUE),
										DSL.key("valuePrese").value(u.VALUE_PRESE),
										DSL.key("lang").value(u.LANG),
										DSL.key("complexity").value(u.COMPLEXITY),
										DSL.key("orderBy").value(u.ORDER_BY)))
						.orderBy(u.ORDER_BY))
				.from(u)
				.where(u.LEXEME_ID.eq(cl.ID))
				.asField();

		Field<JSON> memf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("conjunct").value(cm2.CONJUNCT),
										DSL.key("lexemeId").value(ml.ID),
										DSL.key("wordId").value(mw.ID),
										DSL.key("wordValue").value(mw.VALUE),
										DSL.key("formId").value(mf.ID),
										DSL.key("formValue").value(mf.VALUE),
										DSL.key("morphCode").value(mf.MORPH_CODE),
										DSL.key("weight").value(cm2.WEIGHT),
										DSL.key("memberOrder").value(cm2.MEMBER_ORDER)))
						.orderBy(cm2.MEMBER_ORDER))
				.from(mf, mw, ml, cm2)
				.where(
						cm2.COLLOC_LEXEME_ID.eq(cl.ID)
								.and(cm2.MEMBER_LEXEME_ID.eq(ml.ID))
								.and(cm2.MEMBER_FORM_ID.eq(mf.ID))
								.and(ml.WORD_ID.eq(mw.ID)))
				.asField();

		return mainDb
				.select(
						cl.ID.as("lexeme_id"),
						cw.ID.as("word_id"),
						cw.VALUE.as("word_value"),
						usaf.as("usages"),
						memf.as("members"))
				.from(cw, cl, cm1)
				.where(
						cm1.MEMBER_LEXEME_ID.eq(lexemeId)
								.and(cm1.POS_GROUP_CODE.isNull())
								.and(cm1.COLLOC_LEXEME_ID.eq(cl.ID))
								.and(cl.WORD_ID.eq(cw.ID)))
				.orderBy(cw.VALUE)
				.fetchInto(eki.ekilex.data.Collocation.class);
	}
}
