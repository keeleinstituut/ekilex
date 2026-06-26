package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.CONSTRUCT;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_ATTR;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_COMMENT;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_DESCRIPTION;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_GROUP;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_GROUP_MEMBER;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_DEPREL;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_LEMMA_MORPH;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_MORPH;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_POS;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_SEMANTIC_TYPE;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_STAT;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_SOURCE_LINK;
import static eki.ekilex.data.db.main.Tables.SENTENCE;
import static eki.ekilex.data.db.main.Tables.SENTENCE_MEMBER;
import static eki.ekilex.data.db.main.Tables.SENTENCE_RELATION;
import static eki.ekilex.data.db.main.Tables.SENTENCE_TRANSLATION;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.data.conx.ConstructCommentType;
import eki.ekilex.data.conx.ConstructDescriptionType;
import eki.ekilex.data.conx.SentenceRelationType;

@Component
public class ConstructDbService {

	@Autowired
	private DSLContext mainDb;

	public Long createConstruct(eki.ekilex.data.conx.Construct construct) {

		return mainDb
				.insertInto(
						CONSTRUCT,
						CONSTRUCT.NAME_SIMPLE,
						CONSTRUCT.NAME_DETAIL,
						CONSTRUCT.CONSTRUCT_TYPE_CODE,
						CONSTRUCT.CONSTRUCT_SUBTYPE_CODE,
						CONSTRUCT.SCHEMATICITY_CODE,
						CONSTRUCT.PROFICIENCY_LEVEL_CODE,
						CONSTRUCT.LANG)
				.values(
						construct.getNameSimple(),
						construct.getNameDetail(),
						construct.getConstructTypeCode(),
						construct.getConstructSubtypeCode(),
						construct.getSchematicityCode(),
						construct.getProficiencyLevelCode(),
						construct.getLang())
				.returning(CONSTRUCT.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructDescription(Long constructId, ConstructDescriptionType constructDescriptionType, String value) {

		return mainDb
				.insertInto(
						CONSTRUCT_DESCRIPTION,
						CONSTRUCT_DESCRIPTION.CONSTRUCT_ID,
						CONSTRUCT_DESCRIPTION.TYPE,
						CONSTRUCT_DESCRIPTION.VALUE)
				.values(
						constructId,
						constructDescriptionType.name(),
						value)
				.returning(CONSTRUCT_DESCRIPTION.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructComment(Long constructId, ConstructCommentType constructCommentType, String value) {

		return mainDb
				.insertInto(
						CONSTRUCT_COMMENT,
						CONSTRUCT_COMMENT.CONSTRUCT_ID,
						CONSTRUCT_COMMENT.TYPE,
						CONSTRUCT_COMMENT.VALUE)
				.values(
						constructId,
						constructCommentType.name(),
						value)
				.returning(CONSTRUCT_COMMENT.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructAttr(Long constructId, String attrName, String attrValue) {

		return mainDb
				.insertInto(
						CONSTRUCT_ATTR,
						CONSTRUCT_ATTR.CONSTRUCT_ID,
						CONSTRUCT_ATTR.NAME,
						CONSTRUCT_ATTR.VALUE)
				.values(
						constructId,
						attrName,
						attrValue)
				.returning(CONSTRUCT_ATTR.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructSourceLink(Long constructId, Long sourceId, String sourceLinkName) {

		return mainDb
				.insertInto(
						CONSTRUCT_SOURCE_LINK,
						CONSTRUCT_SOURCE_LINK.CONSTRUCT_ID,
						CONSTRUCT_SOURCE_LINK.SOURCE_ID,
						CONSTRUCT_SOURCE_LINK.NAME)
				.values(
						constructId,
						sourceId,
						sourceLinkName)
				.returning(CONSTRUCT_SOURCE_LINK.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructMember(Long constructId, eki.ekilex.data.conx.ConstructMember constructMember) {

		return mainDb
				.insertInto(
						CONSTRUCT_MEMBER,
						CONSTRUCT_MEMBER.CONSTRUCT_ID,
						CONSTRUCT_MEMBER.CGOVERNMENT_CODE,
						CONSTRUCT_MEMBER.IS_HEAD,
						CONSTRUCT_MEMBER.MEMBER_ROLE,
						CONSTRUCT_MEMBER.SEMANTIC_ROLE_CODE,
						CONSTRUCT_MEMBER.MEMBER_ORDER)
				.values(
						constructId,
						constructMember.getCgovernmentCode(),
						constructMember.isHead(),
						constructMember.getMemberRole(),
						constructMember.getSemanticRoleCode(),
						constructMember.getMemberOrder())
				.returning(CONSTRUCT_MEMBER.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructGroup(String constructGroupTypeCode) {

		return mainDb
				.insertInto(
						CONSTRUCT_GROUP,
						CONSTRUCT_GROUP.CONSTRUCT_GROUP_TYPE_CODE)
				.values(constructGroupTypeCode)
				.returning(CONSTRUCT_GROUP.ID)
				.fetchOne()
				.getId();
	}

	public Long createConstructGroupMember(Long constructGroupId, Long constructId) {

		return mainDb
				.insertInto(
						CONSTRUCT_GROUP_MEMBER,
						CONSTRUCT_GROUP_MEMBER.CONSTRUCT_GROUP_ID,
						CONSTRUCT_GROUP_MEMBER.CONSTRUCT_ID)
				.values(
						constructGroupId,
						constructId)
				.returning(CONSTRUCT_GROUP_MEMBER.ID)
				.fetchOne()
				.getId();
	}

	public void createConstructMemberLemmaMorphs(Long constructMemberId, List<String> memberLemmaMorphCodes) {

		for (String memberLemmaMorphCode : memberLemmaMorphCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_LEMMA_MORPH,
							CONSTRUCT_MEMBER_LEMMA_MORPH.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_LEMMA_MORPH.MORPH_CODE)
					.values(
							constructMemberId,
							memberLemmaMorphCode)
					.execute();
		}
	}

	public void createConstructMemberMorphs(Long constructMemberId, List<String> memberMorphCodes) {

		for (String memberMorphCode : memberMorphCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_MORPH,
							CONSTRUCT_MEMBER_MORPH.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_MORPH.MORPH_CODE)
					.values(
							constructMemberId,
							memberMorphCode)
					.execute();
		}
	}

	public void createConstructMemberPosCodes(Long constructMemberId, List<String> memberPosCodes) {

		for (String memberPosGroupCode : memberPosCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_POS,
							CONSTRUCT_MEMBER_POS.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_POS.POS_CODE)
					.values(
							constructMemberId,
							memberPosGroupCode)
					.execute();
		}
	}

	public void createConstructMemberDeprelCodes(Long constructMemberId, List<String> memberDeprelCodes) {

		for (String memberDeprelCode : memberDeprelCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_DEPREL,
							CONSTRUCT_MEMBER_DEPREL.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_DEPREL.DEPREL_CODE)
					.values(
							constructMemberId,
							memberDeprelCode)
					.execute();
		}
	}

	public void createConstructMemberSemanticTypeCodes(Long constructMemberId, List<String> semanticTypeCodes) {

		for (String semanticTypeCode : semanticTypeCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_SEMANTIC_TYPE,
							CONSTRUCT_MEMBER_SEMANTIC_TYPE.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_SEMANTIC_TYPE.SEMANTIC_TYPE_CODE)
					.values(
							constructMemberId,
							semanticTypeCode)
					.execute();
		}
	}

	public Long createConstructMemberStat(eki.ekilex.data.conx.ConstructMemberStat constructMemberStat) {

		return mainDb
				.insertInto(
						CONSTRUCT_MEMBER_STAT,
						CONSTRUCT_MEMBER_STAT.CONSTRUCT_MEMBER_ID,
						CONSTRUCT_MEMBER_STAT.LEXEME_ID,
						CONSTRUCT_MEMBER_STAT.FORM_ID,
						CONSTRUCT_MEMBER_STAT.FREQUENCY,
						CONSTRUCT_MEMBER_STAT.SALIENCE,
						CONSTRUCT_MEMBER_STAT.PROFICIENCY_LEVEL_CODE)
				.values(
						constructMemberStat.getConstructMemberId(),
						constructMemberStat.getLexemeId(),
						constructMemberStat.getFormId(),
						constructMemberStat.getFrequency(),
						constructMemberStat.getSalience(),
						constructMemberStat.getProficiencyLevelCode())
				.returning(CONSTRUCT_MEMBER_STAT.ID)
				.fetchOne()
				.getId();
	}

	public Long createSentence(eki.ekilex.data.conx.Sentence sentence) {

		return mainDb
				.insertInto(
						SENTENCE,
						SENTENCE.CONSTRUCT_ID,
						SENTENCE.TYPE,
						SENTENCE.PROFICIENCY_LEVEL_CODE,
						SENTENCE.VALUE)
				.values(
						sentence.getConstructId(),
						sentence.getType(),
						sentence.getProficiencyLevelCode(),
						sentence.getValue())
				.returning(SENTENCE.ID)
				.fetchOne()
				.getId();
	}

	public Long createSentenceMember(Long sentenceId, Long constructMemberId, eki.ekilex.data.conx.SentenceMember sentenceMember) {

		return mainDb
				.insertInto(
						SENTENCE_MEMBER,
						SENTENCE_MEMBER.SENTENCE_ID,
						SENTENCE_MEMBER.CONSTRUCT_MEMBER_ID,
						SENTENCE_MEMBER.VALUE,
						SENTENCE_MEMBER.MEMBER_SENTENCE_ID,
						SENTENCE_MEMBER.MEMBER_LEXEME_ID,
						SENTENCE_MEMBER.MEMBER_FORM_ID,
						SENTENCE_MEMBER.POS_CODE,
						SENTENCE_MEMBER.DEPREL_CODE,
						SENTENCE_MEMBER.MEMBER_ROLE,
						SENTENCE_MEMBER.MEMBER_ORDER)
				.values(
						sentenceId,
						constructMemberId,
						sentenceMember.getValue(),
						sentenceMember.getMemberSentenceId(),
						sentenceMember.getMemberLexemeId(),
						sentenceMember.getMemberFormId(),
						sentenceMember.getPosCode(),
						sentenceMember.getDeprelCode(),
						sentenceMember.getMemberRole(),
						sentenceMember.getMemberOrder())
				.returning(SENTENCE_MEMBER.ID)
				.fetchOne()
				.getId();
	}

	public Long createSentenceTranslation(Long sentenceId, String value, String lang) {

		return mainDb
				.insertInto(
						SENTENCE_TRANSLATION,
						SENTENCE_TRANSLATION.SENTENCE_ID,
						SENTENCE_TRANSLATION.VALUE,
						SENTENCE_TRANSLATION.LANG)
				.values(
						sentenceId,
						value,
						lang)
				.returning(SENTENCE_TRANSLATION.ID)
				.fetchOne()
				.getId();
	}

	public Long createSentenceRelation(Long sentence1Id, Long sentence2Id, SentenceRelationType sentenceRelationType) {

		return mainDb
				.insertInto(
						SENTENCE_RELATION,
						SENTENCE_RELATION.SENTENCE1_ID,
						SENTENCE_RELATION.SENTENCE2_ID,
						SENTENCE_RELATION.TYPE)
				.values(
						sentence1Id,
						sentence2Id,
						sentenceRelationType.name())
				.returning(SENTENCE_RELATION.ID)
				.fetchOne()
				.getId();
	}
}
