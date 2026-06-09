package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.LANGUAGE_GROUP;
import static eki.ekilex.data.db.main.Tables.LANGUAGE_LABEL;
import static eki.ekilex.data.db.main.Tables.LEXEME;
import static eki.ekilex.data.db.main.Tables.LEXEME_VARIANT;
import static eki.ekilex.data.db.main.Tables.WORD;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_COMMENT;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP_TREE;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_NOTE;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_SOURCE_LINK;
import static org.jooq.meta.postgres.information_schema.Tables.TABLES;

import org.jooq.CommonTableExpression;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record4;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.GlobalConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.constant.WordEtymGroupType;
import eki.ekilex.data.Note;
import eki.ekilex.data.db.main.tables.LanguageGroup;
import eki.ekilex.data.db.main.tables.LanguageLabel;
import eki.ekilex.data.db.main.tables.Lexeme;
import eki.ekilex.data.db.main.tables.LexemeVariant;
import eki.ekilex.data.db.main.tables.Word;
import eki.ekilex.data.db.main.tables.WordEtym;
import eki.ekilex.data.db.main.tables.WordEtymComment;
import eki.ekilex.data.db.main.tables.WordEtymGroup;
import eki.ekilex.data.db.main.tables.WordEtymGroupMember;
import eki.ekilex.data.db.main.tables.WordEtymGroupTree;
import eki.ekilex.data.db.main.tables.WordEtymNote;
import eki.ekilex.data.db.main.tables.WordEtymSourceLink;

// TODO separate service until new etym generation is introduced
@Component
public class EtymDbService implements SystemConstant, GlobalConstant {

	@Autowired
	protected DSLContext mainDb;

	public eki.ekilex.data.etym2.WordEtymTree getWordEtymTree(Long wordId, String classifierLabelLang) {

		boolean etymTablesExist = etymTablesExist();

		if (!etymTablesExist) {
			return null;
		}

		Word hw = WORD.as("hw");
		Word ew = WORD.as("ew");
		Word ewvw = WORD.as("ewvw");
		Lexeme ewl = LEXEME.as("ewl");
		Lexeme ewvl = LEXEME.as("ewvl");
		LexemeVariant ewvlv = LEXEME_VARIANT.as("ewvlv");
		LanguageLabel hwll = LANGUAGE_LABEL.as("hwll");
		LanguageLabel ewll = LANGUAGE_LABEL.as("ewll");
		LanguageLabel ewvwll = LANGUAGE_LABEL.as("ewvwll");
		LanguageGroup lg = LANGUAGE_GROUP.as("lg");

		WordEtymGroupTree wegt = WORD_ETYM_GROUP_TREE.as("wegt");
		WordEtymGroup hweg = WORD_ETYM_GROUP.as("hweg");
		WordEtymGroup eweg = WORD_ETYM_GROUP.as("eweg");
		WordEtymGroupMember hwegm = WORD_ETYM_GROUP_MEMBER.as("hwegm");
		WordEtymGroupMember ewegm = WORD_ETYM_GROUP_MEMBER.as("ewegm");
		WordEtym hwe = WORD_ETYM.as("hwe");
		WordEtym ewe = WORD_ETYM.as("ewe");
		WordEtymComment wec = WORD_ETYM_COMMENT.as("wec");
		WordEtymNote wen = WORD_ETYM_NOTE.as("wen");
		WordEtymSourceLink wesl = WORD_ETYM_SOURCE_LINK.as("wesl");

		CommonTableExpression<Record4<Long, Long, Long, Integer>> werec = DSL
				.name("werec")
				.fields(
						"headword_id",
						"word_etym_group_id",
						"word_etym_group_order_by",
						"level")
				.as(DSL
						.select(
								hwe.WORD_ID.as("headword_id"),
								hweg.ID.as("word_etym_group_id"),
								DSL.val(1L).as("word_etym_group_order_by"),
								DSL.val(1).as("level"))
						.from(
								hweg,
								hwegm,
								hwe)
						.where(
								hwe.WORD_ID.eq(wordId)
										.and(hwegm.WORD_ETYM_ID.eq(hwe.ID))
										.and(hwegm.WORD_ETYM_GROUP_ID.eq(hweg.ID)))
						.unionAll(DSL
								.select(
										DSL.field("werec.headword_id", Long.class),
										eweg.ID.as("word_etym_group_id"),
										wegt.ORDER_BY.as("word_etym_group_order_by"),
										DSL.field("werec.level", Integer.class).add(1).as("level"))
								.from(
										DSL.table("werec")
												.leftOuterJoin(wegt).on(wegt.PARENT_WORD_ETYM_GROUP_ID.eq(DSL.field("werec.word_etym_group_id", Long.class)))
												.leftOuterJoin(eweg).on(eweg.ID.eq(wegt.CHILD_WORD_ETYM_GROUP_ID)))
								.where(eweg.ID.isNotNull())
								.orderBy(wegt.ORDER_BY)

						));

		Field<JSON> ewvwf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ewvw.ID),
										DSL.key("value").value(ewvw.VALUE),
										DSL.key("valuePrese").value(ewvw.VALUE_PRESE),
										DSL.key("lang").value(ewvw.LANG),
										DSL.key("langValue").value(ewvwll.VALUE)))
						.orderBy(
								ewvl.LEVEL1,
								ewvl.LEVEL2,
								ewvl.ID))
				.from(
						ewl,
						ewvl,
						ewvw,
						ewvlv,
						ewvwll)
				.where(
						ewl.WORD_ID.eq(ew.ID)
								.and(ewl.DATASET_CODE.eq(DATASET_ETY))
								.and(ewl.MEANING_ID.eq(ewvl.MEANING_ID))
								.and(ewvl.DATASET_CODE.eq(DATASET_ETY))
								.and(ewvl.WORD_ID.eq(ewvw.ID))
								.and(ewvlv.LEXEME_ID.eq(ewl.ID))
								.and(ewvlv.VARIANT_LEXEME_ID.eq(ewvl.ID))
								.and(ewvwll.CODE.eq(ewvw.LANG))
								.and(ewvwll.LANG.eq(classifierLabelLang))
								.and(ewvwll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP)))
				.asField();

		Field<JSON> wecf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wec.ID),
										DSL.key("wordEtymId").value(wec.WORD_ETYM_ID),
										DSL.key("value").value(wec.VALUE),
										DSL.key("valuePrese").value(wec.VALUE_PRESE),
										DSL.key("orderBy").value(wec.ORDER_BY)))
						.orderBy(wec.ORDER_BY))
				.from(wec)
				.where(wec.WORD_ETYM_ID.eq(ewe.ID))
				.asField();

		Field<JSON> wenf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wen.ID),
										DSL.key("wordEtymId").value(wen.WORD_ETYM_ID),
										DSL.key("value").value(wen.VALUE),
										DSL.key("valuePrese").value(wen.VALUE_PRESE),
										DSL.key("createdBy").value(wen.CREATED_BY),
										DSL.key("createdOn").value(wen.CREATED_ON),
										DSL.key("modifiedBy").value(wen.MODIFIED_BY),
										DSL.key("modifiedOn").value(wen.MODIFIED_ON),
										DSL.key("orderBy").value(wen.ORDER_BY)))
						.orderBy(wen.ORDER_BY))
				.from(wen)
				.where(wen.WORD_ETYM_ID.eq(ewe.ID))
				.asField();

		Field<JSON> weslf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(wesl.ID),
										DSL.key("wordEtymId").value(wesl.WORD_ETYM_ID),
										DSL.key("sourceId").value(wesl.SOURCE_ID),
										DSL.key("name").value(wesl.NAME),
										DSL.key("value").value(wesl.VALUE),
										DSL.key("orderBy").value(wesl.ORDER_BY)))
						.orderBy(wesl.ORDER_BY))
				.from(wesl)
				.where(wesl.WORD_ETYM_ID.eq(ewe.ID))
				.asField();

		Field<JSON> wegmf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(ewe.ID),
										DSL.key("wordId").value(ew.ID),
										DSL.key("value").value(ew.VALUE),
										DSL.key("valuePrese").value(ew.VALUE_PRESE),
										DSL.key("lang").value(ew.LANG),
										DSL.key("langValue").value(ewll.VALUE),
										DSL.key("etymologyYear").value(ewe.ETYMOLOGY_YEAR),
										DSL.key("questionable").value(ewegm.IS_QUESTIONABLE),
										DSL.key("variantWords").value(ewvwf),
										DSL.key("comments").value(wecf),
										DSL.key("notes").value(wenf),
										DSL.key("sourceLinks").value(weslf)))
						.orderBy(ewegm.ORDER_BY))
				.from(ewegm
						.innerJoin(ewe).on(ewe.ID.eq(ewegm.WORD_ETYM_ID))
						.innerJoin(ew).on(ew.ID.eq(ewe.WORD_ID))
						.leftOuterJoin(ewll).on(
								ewll.CODE.eq(ew.LANG)
										.and(ewll.LANG.eq(classifierLabelLang))
										.and(ewll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(ewegm.WORD_ETYM_GROUP_ID.eq(eweg.ID))
				.asField();

		Field<JSON> wegf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("id").value(eweg.ID),
										DSL.key("groupType").value(eweg.GROUP_TYPE),
										DSL.key("etymologyTypeCode").value(eweg.ETYMOLOGY_TYPE_CODE),
										DSL.key("languageGroupId").value(lg.ID),
										DSL.key("languageGroupName").value(lg.NAME),
										DSL.key("questionable").value(eweg.IS_QUESTIONABLE),
										DSL.key("groupMembers").value(wegmf),
										DSL.key("orderBy").value(werec.field("word_etym_group_order_by", Long.class))))
						.orderBy(werec.field("word_etym_group_order_by")))
				.from(eweg
						.leftOuterJoin(lg).on(lg.ID.eq(eweg.LANGUAGE_GROUP_ID)))
				.where(eweg.ID.eq(werec.field("word_etym_group_id", Long.class)))
				.asField();

		Field<Integer> wegcf = DSL
				.select(DSL.count(eweg.ID))
				.from(eweg)
				.where(eweg.ID.eq(werec.field("word_etym_group_id", Long.class)))
				.asField();

		Field<Boolean> wesgf = DSL
				.select(DSL.field(DSL.count(eweg.ID).eq(1)))
				.from(eweg)
				.where(eweg.ID.eq(werec.field("word_etym_group_id", Long.class)))
				.asField();

		Field<JSON> elf = DSL
				.select(DSL
						.jsonArrayAgg(DSL
								.jsonObject(
										DSL.key("level").value(werec.field("level", Integer.class)),
										DSL.key("groups").value(wegf),
										DSL.key("groupCount").value(wegcf),
										DSL.key("singleGroup").value(wesgf)))
						.orderBy(werec.field("level")))
				.from(werec)
				.where(werec.field("headword_id", Long.class).eq(hw.ID))
				.asField();

		Field<JSON> hwf = DSL.field(DSL
				.jsonObject(
						DSL.key("id").value(hw.ID),
						DSL.key("value").value(hw.VALUE),
						DSL.key("valuePrese").value(hw.VALUE_PRESE),
						DSL.key("lang").value(hw.LANG),
						DSL.key("langValue").value(hwll.VALUE)));

		return mainDb
				.withRecursive(werec)
				.select(
						hwf.as("headword"),
						elf.as("levels"))
				.from(hw
						.leftOuterJoin(hwll).on(
								hwll.CODE.eq(hw.LANG)
										.and(hwll.LANG.eq(classifierLabelLang))
										.and(hwll.TYPE.eq(CLASSIF_LABEL_TYPE_DESCRIP))))
				.where(hw.ID.eq(wordId))
				.fetchOptionalInto(eki.ekilex.data.etym2.WordEtymTree.class)
				.orElse(null);
	}

	public eki.ekilex.data.etym2.WordEtymGroupMember getWordEtymForWord(Long wordId) {

		WordEtym we = WORD_ETYM.as("we");

		return mainDb
				.selectFrom(we)
				.where(we.WORD_ID.eq(wordId))
				.orderBy(we.ID)
				.limit(1)
				.fetchOptionalInto(eki.ekilex.data.etym2.WordEtymGroupMember.class)
				.orElse(null);
	}

	public Long createWordEtym(Long wordId, eki.ekilex.data.etym2.WordEtymGroupMember wordEtym) {

		Long wordEtymId = mainDb
				.insertInto(
						WORD_ETYM,
						WORD_ETYM.WORD_ID,
						WORD_ETYM.ETYMOLOGY_YEAR)
				.values(
						wordId,
						wordEtym.getEtymologyYear())
				.returning(WORD_ETYM.ID)
				.fetchOne()
				.getId();

		return wordEtymId;
	}

	public Long createWordEtymComment(Long wordEtymId, String value, String valuePrese, String origName) {

		Long wordEtymCommentId = mainDb
				.insertInto(
						WORD_ETYM_COMMENT,
						WORD_ETYM_COMMENT.WORD_ETYM_ID,
						WORD_ETYM_COMMENT.VALUE,
						WORD_ETYM_COMMENT.VALUE_PRESE,
						WORD_ETYM_COMMENT.ORIG_NAME)
				.values(
						wordEtymId,
						value,
						valuePrese,
						origName)
				.returning(WORD_ETYM_COMMENT.ID)
				.fetchOne()
				.getId();

		return wordEtymCommentId;
	}

	public Long createWordEtymNote(Long wordEtymId, Note wordEtymNote) {

		Long wordEtymNoteId = mainDb
				.insertInto(
						WORD_ETYM_NOTE,
						WORD_ETYM_NOTE.WORD_ETYM_ID,
						WORD_ETYM_NOTE.VALUE,
						WORD_ETYM_NOTE.VALUE_PRESE,
						WORD_ETYM_NOTE.LANG,
						WORD_ETYM_NOTE.IS_PUBLIC,
						WORD_ETYM_NOTE.CREATED_BY,
						WORD_ETYM_NOTE.CREATED_ON,
						WORD_ETYM_NOTE.MODIFIED_BY,
						WORD_ETYM_NOTE.MODIFIED_ON)
				.values(
						wordEtymId,
						wordEtymNote.getValue(),
						wordEtymNote.getValuePrese(),
						wordEtymNote.getLang(),
						wordEtymNote.isPublic(),
						wordEtymNote.getCreatedBy(),
						wordEtymNote.getCreatedOn(),
						wordEtymNote.getModifiedBy(),
						wordEtymNote.getModifiedOn())
				.returning(WORD_ETYM_NOTE.ID)
				.fetchOne()
				.getId();

		return wordEtymNoteId;
	}

	public Long createWordEtymSourceLink(Long wordEtymId, Long sourceId, String sourceName) {
		return mainDb
				.insertInto(
						WORD_ETYM_SOURCE_LINK,
						WORD_ETYM_SOURCE_LINK.WORD_ETYM_ID,
						WORD_ETYM_SOURCE_LINK.SOURCE_ID,
						WORD_ETYM_SOURCE_LINK.NAME)
				.values(
						wordEtymId,
						sourceId,
						sourceName)
				.returning(WORD_ETYM_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public Long createWordEtymGroup(eki.ekilex.data.etym2.WordEtymGroup wordEtymGroup) {

		Long wordEtymGroupId = mainDb
				.insertInto(
						WORD_ETYM_GROUP,
						WORD_ETYM_GROUP.GROUP_TYPE,
						WORD_ETYM_GROUP.ETYMOLOGY_TYPE_CODE,
						WORD_ETYM_GROUP.LANGUAGE_GROUP_ID,
						WORD_ETYM_GROUP.IS_QUESTIONABLE)
				.values(
						wordEtymGroup.getGroupType().name(),
						wordEtymGroup.getEtymologyTypeCode(),
						wordEtymGroup.getLanguageGroupId(),
						wordEtymGroup.isQuestionable())
				.returning(WORD_ETYM_GROUP.ID)
				.fetchOne()
				.getId();

		return wordEtymGroupId;
	}

	public void updateWordEtymGroup(Long wordEtymGroupId, WordEtymGroupType wordEtymGroupType) {

		mainDb
				.update(WORD_ETYM_GROUP)
				.set(WORD_ETYM_GROUP.GROUP_TYPE, wordEtymGroupType.name())
				.where(WORD_ETYM_GROUP.ID.eq(wordEtymGroupId))
				.execute();
	}

	public Long createWordEtymGroupMember(Long wordEtymId, Long wordEtymGroupId, boolean isQuestionable) {

		Long wordEtymGroupMemberId = mainDb
				.insertInto(
						WORD_ETYM_GROUP_MEMBER,
						WORD_ETYM_GROUP_MEMBER.WORD_ETYM_GROUP_ID,
						WORD_ETYM_GROUP_MEMBER.WORD_ETYM_ID,
						WORD_ETYM_GROUP_MEMBER.IS_QUESTIONABLE)
				.values(
						wordEtymGroupId,
						wordEtymId,
						isQuestionable)
				.returning(WORD_ETYM_GROUP_MEMBER.ID)
				.fetchOne()
				.getId();

		return wordEtymGroupMemberId;
	}

	public Long createWordEtymGroupTree(Long parentWordEtymGroupId, Long childWordEtymGroupId) {

		Long wordEtymGroupTreeId = mainDb
				.insertInto(
						WORD_ETYM_GROUP_TREE,
						WORD_ETYM_GROUP_TREE.PARENT_WORD_ETYM_GROUP_ID,
						WORD_ETYM_GROUP_TREE.CHILD_WORD_ETYM_GROUP_ID)
				.values(
						parentWordEtymGroupId,
						childWordEtymGroupId)
				.returning(WORD_ETYM_GROUP_TREE.ID)
				.fetchOne()
				.getId();

		return wordEtymGroupTreeId;
	}

	public boolean etymTablesExist() {
		return mainDb
				.fetchExists(DSL
						.selectOne()
						.from(TABLES.getSchema() + "." + TABLES.getName())
						.where(TABLES.TABLE_NAME.eq(WORD_ETYM.getName())));
	}
}
