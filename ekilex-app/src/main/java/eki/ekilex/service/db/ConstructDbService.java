package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.CONSTRUCT;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_DEPREL;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_LEMMA_MORPH;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_MORPH;
import static eki.ekilex.data.db.main.Tables.CONSTRUCT_MEMBER_POS_GROUP;
import static eki.ekilex.data.db.main.Tables.SENTENCE;
import static eki.ekilex.data.db.main.Tables.SENTENCE_MEMBER;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConstructDbService {

	@Autowired
	private DSLContext mainDb;

	public Long createConstruct(eki.ekilex.data.conx.Construct construct) {

		return mainDb
				.insertInto(
						CONSTRUCT,
						CONSTRUCT.NAME,
						CONSTRUCT.DESCRIPTION,
						CONSTRUCT.CONSTRUCT_TYPE_CODE,
						CONSTRUCT.CONSTRUCT_SUBTYPE_CODE,
						CONSTRUCT.SCHEMATICITY_CODE,
						CONSTRUCT.PROFICIENCY_LEVEL_CODE,
						CONSTRUCT.LANG)
				.values(
						construct.getName(),
						construct.getDescription(),
						construct.getConstructTypeCode(),
						construct.getConstructSubtypeCode(),
						construct.getSchematicityCode(),
						construct.getProficiencyLevelCode(),
						construct.getLang())
				.returning(CONSTRUCT.ID)
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
						CONSTRUCT_MEMBER.MEMBER_ORDER)
				.values(
						constructId,
						constructMember.getCgovernmentCode(),
						constructMember.isHead(),
						constructMember.getMemberRole(),
						constructMember.getMemberOrder())
				.returning(CONSTRUCT_MEMBER.ID)
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
						SENTENCE_MEMBER.POS_GROUP_CODE,
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
						sentenceMember.getPosGroupCode(),
						sentenceMember.getDeprelCode(),
						sentenceMember.getMemberRole(),
						sentenceMember.getMemberOrder())
				.returning(SENTENCE_MEMBER.ID)
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

	public void createConstructMemberPosGroups(Long constructMemberId, List<String> memberPosGroupCodes) {

		for (String memberPosGroupCode : memberPosGroupCodes) {

			mainDb
					.insertInto(
							CONSTRUCT_MEMBER_POS_GROUP,
							CONSTRUCT_MEMBER_POS_GROUP.CONSTRUCT_MEMBER_ID,
							CONSTRUCT_MEMBER_POS_GROUP.POS_GROUP_CODE)
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
}
