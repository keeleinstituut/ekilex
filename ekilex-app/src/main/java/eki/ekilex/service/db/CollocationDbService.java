package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.COLLOCATION_MEMBER;
import static eki.ekilex.data.db.main.Tables.DEFINITION;
import static eki.ekilex.data.db.main.Tables.DEFINITION_DATASET;
import static eki.ekilex.data.db.main.Tables.FORM;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.MORPH;
import static eki.ekilex.data.db.main.Tables.MORPH_LABEL;
import static eki.ekilex.data.db.main.Tables.PARADIGM;
import static eki.ekilex.data.db.main.Tables.PARADIGM_FORM;
import static eki.ekilex.data.db.main.Tables.POS_GROUP;
import static eki.ekilex.data.db.main.Tables.POS_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.REL_GROUP;
import static eki.ekilex.data.db.main.Tables.REL_GROUP_LABEL;
import static eki.ekilex.data.db.main.Tables.WORD;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.CollocMember;
import eki.ekilex.data.CollocMemberForm;
import eki.ekilex.data.CollocMemberMeaning;
import eki.ekilex.data.CollocMemberOrder;
import eki.ekilex.data.db.main.tables.CollocationMember;
import eki.ekilex.data.db.main.tables.Definition;
import eki.ekilex.data.db.main.tables.DefinitionDataset;
import eki.ekilex.data.db.main.tables.Form;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.Morph;
import eki.ekilex.data.db.main.tables.MorphLabel;
import eki.ekilex.data.db.main.tables.Paradigm;
import eki.ekilex.data.db.main.tables.ParadigmForm;
import eki.ekilex.data.db.main.tables.PosGroup;
import eki.ekilex.data.db.main.tables.PosGroupLabel;
import eki.ekilex.data.db.main.tables.RelGroup;
import eki.ekilex.data.db.main.tables.RelGroupLabel;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.service.db.util.QueryHelper;

@Component
public class CollocationDbService implements GlobalConstant, SystemConstant {

	@Autowired
	private DSLContext mainDb;

	@Autowired
	private QueryHelper queryHelper;

	public boolean isCollocationsExist(Long lexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.fetchExists(DSL
						.select(cm.ID)
						.from(cm)
						.where(cm.MEMBER_LEXEME_ID.eq(lexemeId)));
	}

	public CollocMember getCollocMember(Long id, String classifierLabelLang) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		Condition where = cm.ID.eq(id);
		Table<?> cmst = queryHelper.getCollocMemberFullJoinTable(cm, where, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);

		return mainDb
				.selectFrom(cmst)
				.fetchOptionalInto(CollocMember.class)
				.orElse(null);
	}

	public List<CollocMember> getCollocationMembers(Long lexemeId, String classifierLabelLang) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		Condition where = cm.COLLOC_LEXEME_ID.eq(lexemeId);
		Table<?> cmst = queryHelper.getCollocMemberFullJoinTable(cm, where, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);

		return mainDb
				.selectFrom(cmst)
				.orderBy(cmst.field("member_order"))
				.fetchInto(CollocMember.class);
	}

	public List<eki.ekilex.data.CollocPosGroup> getPrimaryCollocations(Long lexemeId, String classifierLabelLang) {

		PosGroup pg = POS_GROUP.as("pg");
		PosGroupLabel pgl = POS_GROUP_LABEL.as("pgl");
		RelGroup rg = REL_GROUP.as("rg");
		RelGroupLabel rgl = REL_GROUP_LABEL.as("rgl");
		CollocationMember cm = COLLOCATION_MEMBER.as("cm");
		CollocationMember cm1 = COLLOCATION_MEMBER.as("cm1");
		CollocationMember cm3 = COLLOCATION_MEMBER.as("cm3");
		Word cw = WORD.as("cw");
		Lexeme cl = LEXEME.as("cl");

		Field<JSON> usaf = queryHelper.getSimpleLexemeUsagesField(cl.ID);
		Field<JSON> memf = queryHelper.getCollocationMembersField(cl.ID, classifierLabelLang, CLASSIF_LABEL_TYPE_DESCRIP);

		Field<Long> hwmemidf = DSL
				.select(cm3.ID)
				.from(cm3)
				.where(
						cm3.MEMBER_LEXEME_ID.eq(lexemeId)
								.and(cm3.COLLOC_LEXEME_ID.eq(cl.ID)))
				.limit(1)
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
										DSL.key("groupOrder").value(cm1.GROUP_ORDER),
										DSL.key("headwordCollocMemberId").value(hwmemidf)))
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
										.and(rgl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.whereExists(DSL
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
										.and(pgl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.whereExists(DSL
						.select(cm.ID)
						.from(cm)
						.where(
								cm.MEMBER_LEXEME_ID.eq(lexemeId)
										.and(cm.POS_GROUP_CODE.eq(pg.CODE))))
				.orderBy(pg.ORDER_BY)
				.fetchInto(eki.ekilex.data.CollocPosGroup.class);

	}

	public List<eki.ekilex.data.Colloc> getSecondaryCollocations(Long lexemeId) {

		CollocationMember cm1 = COLLOCATION_MEMBER.as("cm1");
		CollocationMember cm3 = COLLOCATION_MEMBER.as("cm3");
		Word cw = WORD.as("cw");
		Lexeme cl = LEXEME.as("cl");

		Field<JSON> usaf = queryHelper.getSimpleLexemeUsagesField(cl.ID);
		Field<JSON> memf = queryHelper.getCollocationMembersField(cl.ID, null, null);

		Field<Long> hwmemidf = DSL
				.select(cm3.ID)
				.from(cm3)
				.where(
						cm3.MEMBER_LEXEME_ID.eq(lexemeId)
								.and(cm3.COLLOC_LEXEME_ID.eq(cl.ID)))
				.limit(1)
				.asField();

		return mainDb
				.select(
						cl.ID.as("lexeme_id"),
						cw.ID.as("word_id"),
						cw.VALUE.as("word_value"),
						usaf.as("usages"),
						memf.as("members"),
						hwmemidf.as("headword_colloc_member_id"))
				.from(cw, cl, cm1)
				.where(
						cm1.MEMBER_LEXEME_ID.eq(lexemeId)
								.and(cm1.POS_GROUP_CODE.isNull())
								.and(cm1.COLLOC_LEXEME_ID.eq(cl.ID))
								.and(cl.WORD_ID.eq(cw.ID)))
				.orderBy(cw.VALUE)
				.fetchInto(eki.ekilex.data.Colloc.class);
	}

	public List<CollocMemberMeaning> getCollocationMemberMeanings(Long collocMemberLexemeId) {

		Lexeme l1 = LEXEME.as("l1");
		Lexeme l2 = LEXEME.as("l2");
		Definition d = DEFINITION.as("d");

		Field<String[]> dvf = DSL
				.select(DSL.arrayAgg(d.VALUE))
				.from(d)
				.where(d.MEANING_ID.eq(l2.MEANING_ID))
				.asField();

		return mainDb
				.select(
						l2.WORD_ID,
						l2.ID.as("lexeme_id"),
						l2.MEANING_ID,
						dvf.as("definition_values"))
				.from(l2)
				.where(DSL
						.exists(DSL
								.select(l1.ID)
								.from(l1)
								.where(
										l1.ID.eq(collocMemberLexemeId)
												.and(l1.WORD_ID.eq(l2.WORD_ID))
												.and(l1.DATASET_CODE.eq(l2.DATASET_CODE)))))
				.orderBy(l2.LEVEL1, l2.LEVEL2)
				.fetchInto(CollocMemberMeaning.class);

	}

	public void deleteLexemeCollocMembers(Long lexemeId) {
		mainDb
				.delete(COLLOCATION_MEMBER)
				.where(COLLOCATION_MEMBER.MEMBER_LEXEME_ID.eq(lexemeId))
				.execute();
	}

	public void deleteCollocMember(Long collocMemberId) {
		mainDb
				.delete(COLLOCATION_MEMBER)
				.where(COLLOCATION_MEMBER.ID.eq(collocMemberId))
				.execute();
	}

	public List<CollocMemberOrder> getCollocMemberOrders(Long collocLexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.selectFrom(cm)
				.where(cm.COLLOC_LEXEME_ID.eq(collocLexemeId))
				.orderBy(cm.MEMBER_ORDER)
				.fetchInto(CollocMemberOrder.class);
	}

	public List<CollocMemberOrder> getCollocMemberOrdersOfRelGroup(Long collocLexemeId, Long memberLexemeId) {

		CollocationMember cm1 = COLLOCATION_MEMBER.as("cm1");
		CollocationMember cm2 = COLLOCATION_MEMBER.as("cm2");

		return mainDb
				.selectFrom(cm1)
				.where(
						cm1.MEMBER_LEXEME_ID.eq(memberLexemeId)
								.andExists(DSL
										.select(cm2.ID)
										.from(cm2)
										.where(
												cm2.COLLOC_LEXEME_ID.eq(collocLexemeId)
														.and(cm2.MEMBER_LEXEME_ID.eq(cm1.MEMBER_LEXEME_ID))
														.and(cm2.POS_GROUP_CODE.eq(cm1.POS_GROUP_CODE))
														.and(cm2.REL_GROUP_CODE.eq(cm1.REL_GROUP_CODE)))))
				.orderBy(cm1.GROUP_ORDER)
				.fetchInto(CollocMemberOrder.class);
	}

	public void updateCollocMemberPosGroup(Long collocMemberId, String posGroupCode) {
		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.POS_GROUP_CODE, posGroupCode)
				.where(COLLOCATION_MEMBER.ID.eq(collocMemberId))
				.execute();
	}

	public void updateCollocMemberRelGroup(Long collocMemberId, String relGroupCode) {
		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.REL_GROUP_CODE, relGroupCode)
				.where(COLLOCATION_MEMBER.ID.eq(collocMemberId))
				.execute();
	}

	public void updateLexemeCollocMemberOrder(Long collocMemberId, Integer memberOrder) {
		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.MEMBER_ORDER, memberOrder)
				.where(COLLOCATION_MEMBER.ID.eq(collocMemberId))
				.execute();
	}

	public void updateLexemeCollocMemberGroupOrder(Long collocMemberId, Integer groupOrder) {
		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.GROUP_ORDER, groupOrder)
				.where(COLLOCATION_MEMBER.ID.eq(collocMemberId))
				.execute();
	}

	public void moveCollocMember(List<Long> collocLexemeIds, Long sourceCollocMemberLexemeId, Long targetCollocMemberLexemeId) {

		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.MEMBER_LEXEME_ID, targetCollocMemberLexemeId)
				.where(
						COLLOCATION_MEMBER.MEMBER_LEXEME_ID.eq(sourceCollocMemberLexemeId)
								.and(COLLOCATION_MEMBER.COLLOC_LEXEME_ID.in(collocLexemeIds)))
				.execute();
	}

	public Long createCollocMember(CollocMember collocMember) {

		return mainDb
				.insertInto(
						COLLOCATION_MEMBER,
						COLLOCATION_MEMBER.COLLOC_LEXEME_ID,
						COLLOCATION_MEMBER.MEMBER_LEXEME_ID,
						COLLOCATION_MEMBER.MEMBER_FORM_ID,
						COLLOCATION_MEMBER.CONJUNCT_LEXEME_ID,
						COLLOCATION_MEMBER.POS_GROUP_CODE,
						COLLOCATION_MEMBER.REL_GROUP_CODE,
						COLLOCATION_MEMBER.WEIGHT,
						COLLOCATION_MEMBER.MEMBER_ORDER,
						COLLOCATION_MEMBER.GROUP_ORDER)
				.values(
						collocMember.getCollocLexemeId(),
						collocMember.getMemberLexemeId(),
						collocMember.getMemberFormId(),
						collocMember.getConjunctLexemeId(),
						collocMember.getPosGroupCode(),
						collocMember.getRelGroupCode(),
						collocMember.getWeight(),
						collocMember.getMemberOrder(),
						collocMember.getGroupOrder())
				.returning(COLLOCATION_MEMBER.ID)
				.fetchOne()
				.getId();
	}

	public void updateCollocMember(CollocMember collocMember) {

		mainDb
				.update(COLLOCATION_MEMBER)
				.set(COLLOCATION_MEMBER.MEMBER_LEXEME_ID, collocMember.getMemberLexemeId())
				.set(COLLOCATION_MEMBER.MEMBER_FORM_ID, collocMember.getMemberFormId())
				.set(COLLOCATION_MEMBER.CONJUNCT_LEXEME_ID, collocMember.getConjunctLexemeId())
				.set(COLLOCATION_MEMBER.POS_GROUP_CODE, collocMember.getPosGroupCode())
				.set(COLLOCATION_MEMBER.REL_GROUP_CODE, collocMember.getRelGroupCode())
				.set(COLLOCATION_MEMBER.WEIGHT, collocMember.getWeight())
				.where(COLLOCATION_MEMBER.ID.eq(collocMember.getId()))
				.execute();
	}

	public Integer getMaxGroupOrder(Long memberLexemeId, String posGroupCode, String relGroupCode) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.select(DSL.max(cm.GROUP_ORDER))
				.from(cm)
				.where(
						cm.MEMBER_LEXEME_ID.eq(memberLexemeId)
								.and(cm.POS_GROUP_CODE.eq(posGroupCode))
								.and(cm.REL_GROUP_CODE.eq(relGroupCode)))
				.fetchSingleInto(Integer.class);
	}

	public Integer getMaxMemberOrder(Long collocLexemeId) {

		CollocationMember cm = COLLOCATION_MEMBER.as("cm");

		return mainDb
				.select(DSL.max(cm.MEMBER_ORDER))
				.from(cm)
				.where(cm.COLLOC_LEXEME_ID.eq(collocLexemeId))
				.fetchSingleInto(Integer.class);
	}

	public List<CollocMemberForm> getCollocMemberForms(String formValue, String lang, String datasetCode, String classifierLabelLang) {

		Field<String> formValueLower = DSL.lower(formValue);

		Word w = WORD.as("w");
		Paradigm p = PARADIGM.as("p");
		ParadigmForm pf = PARADIGM_FORM.as("pf");
		Form f = FORM.as("f");
		Lexeme l = LEXEME.as("l");
		Morph mf = MORPH.as("mf");
		MorphLabel mflbl = MORPH_LABEL.as("mflbl");
		Definition d = DEFINITION.as("d");
		DefinitionDataset dds = DEFINITION_DATASET.as("dds");

		Field<String[]> dvf = DSL
				.select(DSL
						.arrayAgg(d.VALUE)
						.orderBy(d.ORDER_BY))
				.from(d)
				.where(
						d.MEANING_ID.eq(l.MEANING_ID)
								.andExists(DSL
										.select(dds.DEFINITION_ID)
										.from(dds)
										.where(
												dds.DEFINITION_ID.eq(d.ID)
														.and(dds.DATASET_CODE.eq(l.DATASET_CODE)))))
				.asField();

		Field<JSON> cmmf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("wordId").value(l.WORD_ID),
										DSL.key("lexemeId").value(l.ID),
										DSL.key("meaningId").value(l.MEANING_ID),
										DSL.key("definitionValues").value(dvf)))
						.orderBy(l.LEVEL1, l.LEVEL2))
				.from(l)
				.where(
						l.WORD_ID.eq(w.ID)
								.and(l.IS_WORD.isTrue())
								.and(l.DATASET_CODE.eq(datasetCode)))
				.asField();

		return mainDb
				.select(
						w.ID.as("word_id"),
						w.VALUE.as("wordValue"),
						f.ID.as("form_id"),
						f.VALUE.as("form_value"),
						f.MORPH_CODE,
						mflbl.VALUE.as("morph_value"),
						cmmf.as("colloc_member_meanings"))
				.from(w, p, pf, f, mf, mflbl)
				.where(
						w.LANG.eq(lang)
								.and(pf.PARADIGM_ID.eq(p.ID))
								.and(pf.FORM_ID.eq(f.ID))
								.and(p.WORD_ID.eq(w.ID))
								.and(DSL.lower(f.VALUE).eq(formValueLower))
								.and(f.MORPH_CODE.ne(MORPH_CODE_UNKNOWN))
								.and(mf.CODE.eq(f.MORPH_CODE))
								.and(mflbl.CODE.eq(f.MORPH_CODE))
								.and(mflbl.LANG.eq(classifierLabelLang))
								.and(mflbl.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))
								.andExists(DSL
										.select(l.ID)
										.from(l)
										.where(
												l.WORD_ID.eq(w.ID)
														.and(l.IS_WORD.isTrue())
														.and(l.DATASET_CODE.eq(datasetCode)))))
				.groupBy(w.ID, f.ID, mf.CODE, mflbl.VALUE)
				.orderBy(w.VALUE, mf.ORDER_BY)
				.fetchInto(CollocMemberForm.class);
	}

	public String getWordValueByLexemeId(Long lexemeId) {

		Lexeme l = LEXEME.as("l");
		Word w = WORD.as("w");
		return mainDb
				.select(w.VALUE)
				.from(l, w)
				.where(
						l.ID.eq(lexemeId)
								.and(l.WORD_ID.eq(w.ID)))
				.limit(1)
				.fetchOptionalInto(String.class)
				.orElse(null);
	}

}
