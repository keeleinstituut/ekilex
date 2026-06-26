package eki.ekilex.cli.runner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import eki.common.constant.SourceType;
import eki.common.data.Count;
import eki.ekilex.data.IdPair;
import eki.ekilex.data.Source;
import eki.ekilex.data.conx.Construct;
import eki.ekilex.data.conx.ConstructCommentType;
import eki.ekilex.data.conx.ConstructDescriptionType;
import eki.ekilex.data.conx.ConstructMember;
import eki.ekilex.data.conx.ConstructMemberStat;
import eki.ekilex.data.conx.Sentence;
import eki.ekilex.data.conx.SentenceMember;
import eki.ekilex.data.conx.SentenceRelationType;
import eki.ekilex.data.migra.Word;
import eki.ekilex.service.db.ConstructDbService;
import eki.ekilex.service.db.SourceDbService;

@Component
public class ConstructLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(ConstructLoaderRunner.class);

	private static final char VALUE_SEPARATOR_1 = ',';

	private static final char VALUE_SEPARATOR_2 = ';';

	private static final String[] SUPPORTED_LANGUAGES = {"est", "rus"};

	@Autowired
	private ConstructDbService constructDbService;

	@Autowired
	private SourceDbService sourceDbService;

	private List<Map<String, Object>> origConstructs;

	private List<Map<String, Object>> origConstructMembers;

	private List<Map<String, Object>> origConstructRelations;

	private List<Map<String, Object>> origConstructMemberStats;

	private List<Map<String, Object>> origRealizations;

	private List<Map<String, Object>> origRealizationMembers;

	private List<Map<String, Object>> origRealizationTranslations;

	private List<Map<String, Object>> origWords;

	private List<Map<String, Object>> origForms;

	private Map<String, Map<String, Object>> origConstructMap;

	private Map<String, Map<String, Object>> origRealizationMap;

	private Map<String, Map<String, Object>> origWordMap;

	private Map<String, Map<String, Object>> origFormMap;

	private Map<String, Long> constructIdMap;

	private Map<String, Long> constructMemberIdMap;

	private Map<String, Long> sentenceIdMap;

	private Map<String, Long> sentenceMemberIdMap;

	private Map<String, Long> formIdMap;

	private Map<Long, Long> formToLexemeIdMap;

	private boolean isCreate = true;

	@Transactional(rollbackFor = Exception.class)
	public void execute(String dataFilePath) throws Exception {

		loadDataFile(dataFilePath);

		Count constructCreateCount = new Count();
		Count constructMemberCreateCount = new Count();
		Count sentenceCreateCount = new Count();
		Count sentenceMemberCreateCount = new Count();
		Count sentenceMemberFirstHomonymCount = new Count();
		Count sentenceMemberMissingWordCount = new Count();

		createConstructs(constructCreateCount, constructMemberCreateCount);
		createConstructRelations();
		createSentences(sentenceCreateCount, sentenceMemberCreateCount, sentenceMemberFirstHomonymCount, sentenceMemberMissingWordCount);
		createSentenceTranslations();
		createConstructMemberStat();

		logger.info("Created constructs: {}", constructCreateCount.getValue());
		logger.info("Created construct members: {}", constructMemberCreateCount.getValue());
		logger.info("Created sentences: {}", sentenceCreateCount.getValue());
		logger.info("Created sentence members: {}", sentenceMemberCreateCount.getValue());
		logger.info("Linked first available homonym count: {}", sentenceMemberFirstHomonymCount.getValue());
		logger.info("Missing word count: {}", sentenceMemberMissingWordCount.getValue());
		logger.info("Done loading");
	}

	@SuppressWarnings("unchecked")
	private void loadDataFile(String dataFilePath) throws Exception {

		String dataJson = readFileContent(dataFilePath);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> dataMap = objectMapper.readValue(dataJson, Map.class);
		origConstructs = (List<Map<String, Object>>) dataMap.get("construction");
		origConstructMembers = (List<Map<String, Object>>) dataMap.get("conx_member");
		origConstructRelations = (List<Map<String, Object>>) dataMap.get("construction_relation");
		origConstructMemberStats = (List<Map<String, Object>>) dataMap.get("statistics");
		origRealizations = (List<Map<String, Object>>) dataMap.get("realization");
		origRealizationMembers = (List<Map<String, Object>>) dataMap.get("realization_member");
		origRealizationTranslations = (List<Map<String, Object>>) dataMap.get("realization_translation");
		origWords = (List<Map<String, Object>>) dataMap.get("word");
		origForms = (List<Map<String, Object>>) dataMap.get("form");

		origConstructMap = toIdMap(origConstructs);
		origRealizationMap = toIdMap(origRealizations);
		origWordMap = toIdMap(origWords);
		origFormMap = toIdMap(origForms);

		constructIdMap = new HashMap<>();
		constructMemberIdMap = new HashMap<>();
		sentenceIdMap = new HashMap<>();
		sentenceMemberIdMap = new HashMap<>();
		formIdMap = new HashMap<>();
		formToLexemeIdMap = new HashMap<>();
	}

	private void createConstructs(Count constructCreateCount, Count constructMemberCreateCount) {

		// -- constructs --

		for (Map<String, Object> origConstruct : origConstructs) {

			String origConstructId = getId(origConstruct);
			String constructNameSimple = getStringValue(origConstruct, "name");
			String constructNameDetail = getStringValue(origConstruct, "name_expert");
			String constructDescriptionSimple = getStringValue(origConstruct, "definition");
			String constructDescriptionDetail = getStringValue(origConstruct, "definition_expert");
			String constructTypeCode = getStringValue(origConstruct, "type");
			String constructSubtypeCode = getStringValue(origConstruct, "subtype");
			String schematicityCode = getStringValue(origConstruct, "schematicity");
			String proficiencyLevelCode = getStringValue(origConstruct, "language_level");
			String lang = getStringValue(origConstruct, "language");
			String constructCommentPublic = getStringValue(origConstruct, "study_comment");
			List<String> constructCommentsPrivate = getStringValues(origConstruct, "inside_comment", VALUE_SEPARATOR_2);
			String attrMoccaId = getStringValue(origConstruct, "MOCCA_ID");
			String sourceName = getStringValue(origConstruct, "reference");

			Construct construct = new Construct();
			construct.setNameSimple(constructNameSimple);
			construct.setNameDetail(constructNameDetail);
			construct.setConstructTypeCode(constructTypeCode);
			construct.setConstructSubtypeCode(constructSubtypeCode);
			construct.setSchematicityCode(schematicityCode);
			construct.setProficiencyLevelCode(proficiencyLevelCode);
			construct.setLang(lang);

			if (isCreate) {

				Long constructId = constructDbService.createConstruct(construct);
				constructIdMap.put(origConstructId, constructId);
				constructCreateCount.increment();

				if (StringUtils.isNotBlank(constructDescriptionSimple)) {
					constructDbService.createConstructDescription(constructId, ConstructDescriptionType.SIMPLE, constructDescriptionSimple);
				}
				if (StringUtils.isNotBlank(constructDescriptionDetail)) {
					constructDbService.createConstructDescription(constructId, ConstructDescriptionType.DETAIL, constructDescriptionDetail);
				}
				if (StringUtils.isNotBlank(constructCommentPublic)) {
					constructDbService.createConstructComment(constructId, ConstructCommentType.PUBLIC, constructCommentPublic);
				}
				if (CollectionUtils.isNotEmpty(constructCommentsPrivate)) {
					for (String constructCommentPrivate : constructCommentsPrivate) {
						constructDbService.createConstructComment(constructId, ConstructCommentType.PRIVATE, constructCommentPrivate);
					}
				}
				if (StringUtils.isNotBlank(attrMoccaId)) {
					constructDbService.createConstructAttr(constructId, "mocca_id", attrMoccaId);
				}
				if (StringUtils.isNotBlank(sourceName)) {
					Long sourceId = migrationDbService.getSourceId(sourceName, DATASET_EKI);
					if (sourceId == null) {
						Source source = new Source();
						source.setDatasetCode(DATASET_EKI);
						source.setType(SourceType.DOCUMENT);
						source.setName(sourceName);
						source.setValue(sourceName);
						source.setValuePrese(sourceName);
						sourceId = sourceDbService.createSource(source);
					}
					constructDbService.createConstructSourceLink(constructId, sourceId, sourceName);
				}
			}
		}

		// -- construct members - 

		for (Map<String, Object> origConstructMember : origConstructMembers) {

			String origConstructMemberId = getId(origConstructMember);
			String origConstructId = getId(origConstructMember, "conx_id");
			String cgovernmentCode = StringUtils.lowerCase(getStringValue(origConstructMember, "member"));
			boolean isHead = getBooleanValue(origConstructMember, "head");
			String memberRole = getStringValue(origConstructMember, "role");
			String semanticRoleCode = StringUtils.lowerCase(getStringValue(origConstructMember, "semantic_role"));
			List<String> semanticTypeCodes = getStringValues(origConstructMember, "semantic_type", VALUE_SEPARATOR_1);
			Integer memberOrder = getIntegerValue(origConstructMember, "member_order");

			Map<String, Object> origConstruct = origConstructMap.get(origConstructId);
			String lang = getStringValue(origConstruct, "language");
			char classifValueSeparator = VALUE_SEPARATOR_1;
			if (StringUtils.equals(lang, "rus")) {
				classifValueSeparator = VALUE_SEPARATOR_2;
			}

			List<String> memberLemmaMorphCodes = getStringValues(origConstructMember, "morph_lemma", classifValueSeparator);
			List<String> memberMorphCodes = getStringValues(origConstructMember, "morph_possible", classifValueSeparator);
			List<String> memberPosCodes = getStringValues(origConstructMember, "pos", VALUE_SEPARATOR_1);
			List<String> memberDeprelCodes = getStringValues(origConstructMember, "deprel", VALUE_SEPARATOR_1);

			Long constructId = constructIdMap.get(origConstructId);

			ConstructMember constructMember = new ConstructMember();
			constructMember.setConstructId(constructId);
			constructMember.setCgovernmentCode(cgovernmentCode);
			constructMember.setMemberRole(memberRole);
			constructMember.setSemanticRoleCode(semanticRoleCode);
			constructMember.setHead(isHead);
			constructMember.setMemberOrder(memberOrder);

			if (isCreate) {

				Long constructMemberId = constructDbService.createConstructMember(constructId, constructMember);
				constructMemberIdMap.put(origConstructMemberId, constructMemberId);
				constructMemberCreateCount.increment();

				if (CollectionUtils.isNotEmpty(memberLemmaMorphCodes)) {
					constructDbService.createConstructMemberLemmaMorphs(constructMemberId, memberLemmaMorphCodes);
				}
				if (CollectionUtils.isNotEmpty(memberMorphCodes)) {
					constructDbService.createConstructMemberMorphs(constructMemberId, memberMorphCodes);
				}
				if (CollectionUtils.isNotEmpty(memberPosCodes)) {
					constructDbService.createConstructMemberPosCodes(constructMemberId, memberPosCodes);
				}
				if (CollectionUtils.isNotEmpty(memberDeprelCodes)) {
					constructDbService.createConstructMemberDeprelCodes(constructMemberId, memberDeprelCodes);
				}
				if (CollectionUtils.isNotEmpty(semanticTypeCodes)) {
					constructDbService.createConstructMemberSemanticTypeCodes(constructMemberId, semanticTypeCodes);
				}
			}
		}
	}

	private void createConstructRelations() {

		final String constructParentRelationCode = "ülemmõiste";
		List<String> constructGroupTypeCodes = migrationDbService.getConstructGroupTypeCodes();
		Map<String, List<IdPair>> constructTypePairsMap = new HashMap<>();

		for (Map<String, Object> origConstructRelation : origConstructRelations) {

			String origConstruct1Id = getId(origConstructRelation, "construction1_id");
			String origConstruct2Id = getId(origConstructRelation, "construction2_id");
			String constructGroupTypeCode = getStringValue(origConstructRelation, "relation");

			Long construct1Id = constructIdMap.get(origConstruct1Id);
			Long construct2Id = constructIdMap.get(origConstruct2Id);

			if (StringUtils.equals(constructParentRelationCode, constructGroupTypeCode)) {

				migrationDbService.updateParentConstructId(construct2Id, construct1Id);

			} else if (constructGroupTypeCodes.contains(constructGroupTypeCode)) {

				IdPair pair1 = new IdPair(construct1Id, construct2Id);
				IdPair pair2 = new IdPair(construct2Id, construct1Id);
				List<IdPair> constructTypePairs = constructTypePairsMap.get(constructGroupTypeCode);

				if (constructTypePairs == null) {
					constructTypePairs = new ArrayList<>();
					constructTypePairsMap.put(constructGroupTypeCode, constructTypePairs);
				}

				if (!constructTypePairs.contains(pair1) && !constructTypePairs.contains(pair2)) {

					Long constructGroupId = constructDbService.createConstructGroup(constructGroupTypeCode);
					constructDbService.createConstructGroupMember(constructGroupId, construct1Id);
					constructDbService.createConstructGroupMember(constructGroupId, construct2Id);
					constructTypePairs.add(pair1);
				}

			} else {
				continue;
			}
		}
	}

	private void createConstructMemberStat() {

		for (Map<String, Object> origConstructMemberStat : origConstructMemberStats) {

			String origFormId = getId(origConstructMemberStat, "form_id");
			String origConstructMemberId = getId(origConstructMemberStat, "conx_member_id");
			Long frequency = getLongValue(origConstructMemberStat, "frequency");
			BigDecimal salience = getBigDecimalValue(origConstructMemberStat, "salience");
			String proficiencyLevelCode = getStringValue(origConstructMemberStat, "language_level");

			Long constructMemberId = constructMemberIdMap.get(origConstructMemberId);
			Long formId = formIdMap.get(origFormId);
			Long lexemeId = formToLexemeIdMap.get(formId);

			if (formId == null) {
				continue;
			}
			if (lexemeId == null) {
				continue;
			}

			ConstructMemberStat constructMemberStat = new ConstructMemberStat();
			constructMemberStat.setConstructMemberId(constructMemberId);
			constructMemberStat.setLexemeId(lexemeId);
			constructMemberStat.setFormId(formId);
			constructMemberStat.setFrequency(frequency);
			constructMemberStat.setSalience(salience);
			constructMemberStat.setProficiencyLevelCode(proficiencyLevelCode);

			constructDbService.createConstructMemberStat(constructMemberStat);
		}
	}

	private void createSentences(
			Count sentenceCreateCount,
			Count sentenceMemberCreateCount,
			Count sentenceMemberFirstHomonymCount,
			Count sentenceMemberMissingWordCount) {

		List<Map<String, Object>> origConstructRealizations;

		origConstructRealizations = origRealizations.stream()
				.filter(record -> record.get("construction_id") != null)
				.collect(Collectors.toList());

		for (Map<String, Object> origRealization : origConstructRealizations) {

			createSentence(origRealization, sentenceCreateCount, sentenceMemberCreateCount, sentenceMemberFirstHomonymCount, sentenceMemberMissingWordCount);
		}

		origConstructRealizations = origRealizations.stream()
				.filter(record -> record.get("construction_id") == null)
				.collect(Collectors.toList());

		for (Map<String, Object> origRealization : origConstructRealizations) {

			createSentence(origRealization, sentenceCreateCount, sentenceMemberCreateCount, sentenceMemberFirstHomonymCount, sentenceMemberMissingWordCount);
		}
	}

	private Long createSentence(
			Map<String, Object> origRealization,
			Count sentenceCreateCount,
			Count sentenceMemberCreateCount,
			Count sentenceMemberFirstHomonymCount,
			Count sentenceMemberMissingWordCount) {

		// -- sentence --

		String origRealizationId = getId(origRealization);
		String origConstructId = getId(origRealization, "construction_id");
		String sentenceType = getStringValue(origRealization, "state");
		String proficiencyLevelCode = getStringValue(origRealization, "proficiency_level_code");
		String sentenceValue = getStringValue(origRealization, "value");
		String origParentConstructId = getId(origRealization, "example_of_realization_id");
		Long constructId = null;

		if (StringUtils.isNotBlank(origConstructId)) {
			constructId = constructIdMap.get(origConstructId);
		}

		Sentence sentence = new Sentence();
		sentence.setConstructId(constructId);
		sentence.setType(sentenceType);
		sentence.setProficiencyLevelCode(proficiencyLevelCode);
		sentence.setValue(sentenceValue);

		Long sentenceId = null;
		if (isCreate) {

			sentenceId = constructDbService.createSentence(sentence);
			sentenceIdMap.put(origRealizationId, sentenceId);
			sentenceCreateCount.increment();

			if (StringUtils.isNotEmpty(origParentConstructId)) {

				Long parentSentenceId = sentenceIdMap.get(origParentConstructId);
				constructDbService.createSentenceRelation(parentSentenceId, sentenceId, SentenceRelationType.USAGE);
			}
		}

		// -- sentence members --

		List<Map<String, Object>> thisOrigRealizationMembers = origRealizationMembers.stream()
				.filter(record -> idEquals(record, "realization_id", origRealizationId))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(thisOrigRealizationMembers)) {
			return sentenceId;
		}

		int memberOrder = 0;

		for (Map<String, Object> origRealizationMember : thisOrigRealizationMembers) {

			String origRealizationMemberId = getId(origRealizationMember);
			String origConstructMemberId = getId(origRealizationMember, "conx_member_id");
			String origMemberFormId = getId(origRealizationMember, "member_form_id");
			String origMemberRealizationId = getId(origRealizationMember, "realization_as_member_id");
			String origMemberWordId = getId(origRealizationMember, "word_id");
			String deprelCode = getStringValue(origRealizationMember, "deprel");
			String memberRole = null;// currently unavailable
			//Integer memberOrder = getIntegerValue(origRealizationMember, "member_order");//unreliable
			memberOrder++;

			Long constructMemberId = constructMemberIdMap.get(origConstructMemberId);
			Long memberSentenceId = null;
			Long lexemeId = null;
			Long formId = null;
			String posCode = null;
			String sentenceMemberValue = null;

			if (origMemberRealizationId != null) {

				Map<String, Object> origMemberRealization = origRealizationMap.get(origMemberRealizationId);
				sentenceMemberValue = getStringValue(origMemberRealization, "value");

			} else if (origMemberFormId != null) {

				Map<String, Object> origForm = origFormMap.get(origMemberFormId);
				String origRealizationMemberFormWordId = getStringValue(origForm, "word_id");
				String sentenceMemberFormValue = getStringValue(origForm, "value");
				String sentenceMemberMorphCode = getStringValue(origForm, "morph_code");

				Map<String, Object> origRealizationMemberFormWord = origWordMap.get(origRealizationMemberFormWordId);
				String sentenceMemberWordValue = getStringValue(origRealizationMemberFormWord, "value");
				posCode = getStringValue(origRealizationMemberFormWord, "pos");

				List<Word> words = getWordCandidates(sentenceMemberWordValue, sentenceMemberFormValue, sentenceMemberMorphCode);

				Word word = null;
				Long wordId = null;

				if (CollectionUtils.isEmpty(words)) {
					// missing matching word records
					sentenceMemberMissingWordCount.increment();
					logger.warn("Word not found: \"{} - {} - {}\"", sentenceMemberWordValue, sentenceMemberFormValue, sentenceMemberMorphCode);
					// fallback to sentence member value instead
					sentenceMemberValue = sentenceMemberFormValue;
				} else if (words.size() == 1) {
					word = words.get(0);
					wordId = word.getId();
				} else {
					// pick first
					sentenceMemberFirstHomonymCount.increment();
					word = words.get(0);
					wordId = word.getId();
				}

				if (wordId != null) {

					// pick first
					List<Long> formIds = migrationDbService.getFormIds(wordId, sentenceMemberFormValue, sentenceMemberMorphCode);
					formId = formIds.get(0);
					formIdMap.put(origMemberFormId, formId);

					// pick first
					List<Long> lexemeIds = migrationDbService.getLexemeIdsByWord(wordId, DATASET_EKI);
					lexemeId = lexemeIds.get(0);
					formToLexemeIdMap.put(formId, lexemeId);
				}

			} else if (origMemberWordId != null) {

				// why?
				logger.error("Unsupported condition (1) met at {}", origRealizationMember);

			} else {

				// what?!
				logger.error("Unsupported condition (2) met at {}", origRealizationMember);
			}

			SentenceMember sentenceMember = new SentenceMember();
			sentenceMember.setSentenceId(sentenceId);
			sentenceMember.setConstructMemberId(constructMemberId);
			sentenceMember.setValue(sentenceMemberValue);
			sentenceMember.setMemberSentenceId(memberSentenceId);
			sentenceMember.setMemberLexemeId(lexemeId);
			sentenceMember.setMemberFormId(formId);
			sentenceMember.setPosCode(posCode);
			sentenceMember.setDeprelCode(deprelCode);
			sentenceMember.setMemberRole(memberRole);
			sentenceMember.setMemberOrder(memberOrder);

			if (isCreate) {

				Long sentenceMemberId = constructDbService.createSentenceMember(sentenceId, constructMemberId, sentenceMember);
				sentenceMemberIdMap.put(origRealizationMemberId, sentenceMemberId);
				sentenceMemberCreateCount.increment();
			}
		}

		return sentenceId;
	}

	private void createSentenceTranslations() {

		for (Map<String, Object> origRealizationTranslation : origRealizationTranslations) {

			String origRealizationId = getId(origRealizationTranslation, "realization_id");
			String value = getStringValue(origRealizationTranslation, "value");
			String lang = getStringValue(origRealizationTranslation, "lang");

			Long sentenceId = sentenceIdMap.get(origRealizationId);
			if (sentenceId == null) {
				continue;
			}
			constructDbService.createSentenceTranslation(sentenceId, value, lang);
		}
	}

	private List<Word> getWordCandidates(String wordValue, String formValue, String morphCode) {

		for (String lang : SUPPORTED_LANGUAGES) {

			List<Long> wordIds = migrationDbService.getWordIds(wordValue, lang, formValue, morphCode, DATASET_EKI);

			if (CollectionUtils.isNotEmpty(wordIds)) {

				List<Word> words = wordIds.stream()
						.map(wordId -> new Word(wordId, wordValue, lang))
						.collect(Collectors.toList());
				return words;
			}
		}
		return null;
	}

	private boolean idEquals(Map<String, Object> dataMap, String fieldName, String fieldValue) {
		String id = getId(dataMap, fieldName);
		return StringUtils.equals(id, fieldValue);
	}

	private Map<String, Map<String, Object>> toIdMap(List<Map<String, Object>> rows) {
		return rows.stream().collect(Collectors.toMap(row -> row.get("id").toString(), row -> row));
	}

	private boolean getBooleanValue(Map<String, Object> dataMap, String fieldName) {
		return valueUtil.toBoolean(dataMap.get(fieldName));
	}

	private String getId(Map<String, Object> dataMap) {
		return getId(dataMap, "id");
	}

	private String getId(Map<String, Object> dataMap, String fieldName) {
		Integer id = getIntegerValue(dataMap, fieldName);
		if (id == null) {
			return null;
		}
		return id.toString();
	}

	private String getStringValue(Map<String, Object> dataMap, String fieldName) {
		return valueUtil.toString(dataMap.get(fieldName));
	}

	private Integer getIntegerValue(Map<String, Object> dataMap, String fieldName) {
		return valueUtil.toInteger(dataMap.get(fieldName));
	}

	private Long getLongValue(Map<String, Object> dataMap, String fieldName) {
		return valueUtil.toLong(dataMap.get(fieldName));
	}

	private BigDecimal getBigDecimalValue(Map<String, Object> dataMap, String fieldName) {
		return valueUtil.toBigDecimal(dataMap.get(fieldName));
	}

	private List<String> getStringValues(Map<String, Object> dataMap, String fieldName, char separator) {
		String stringValuesStr = getStringValue(dataMap, fieldName);
		if (StringUtils.isBlank(stringValuesStr)) {
			return null;
		}
		String[] stringValuesArr = StringUtils.split(stringValuesStr, separator);
		List<String> stringValues = Arrays.stream(stringValuesArr)
				.map(StringUtils::trim)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
		return stringValues;
	}
}
