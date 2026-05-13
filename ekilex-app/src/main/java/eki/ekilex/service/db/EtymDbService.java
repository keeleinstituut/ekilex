package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.WORD_ETYM;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_COMMENT;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_GROUP_TREE;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_NOTE;
import static eki.ekilex.data.db.main.Tables.WORD_ETYM_SOURCE_LINK;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.WordEtymGroupType;
import eki.ekilex.data.Note;
import eki.ekilex.data.etym2.WordEtymGroup;

// TODO separate service until new etym generation is introduced
@Component
public class EtymDbService {

	@Autowired
	protected DSLContext mainDb;

	public Long createWordEtym(Long wordId, eki.ekilex.data.etym2.WordEtym wordEtym) {

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

	public Long createWordEtymGroup(WordEtymGroup wordEtymGroup) {

		Long wordEtymGroupId = mainDb
				.insertInto(
						WORD_ETYM_GROUP,
						WORD_ETYM_GROUP.GROUP_TYPE,
						WORD_ETYM_GROUP.ETYMOLOGY_TYPE_CODE,
						WORD_ETYM_GROUP.LANGUAGE_GROUP_MEMBER_ID,
						WORD_ETYM_GROUP.IS_QUESTIONABLE)
				.values(
						wordEtymGroup.getGroupType().name(),
						wordEtymGroup.getEtymologyTypeCode(),
						wordEtymGroup.getLanguageGroupMemberId(),
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

}
