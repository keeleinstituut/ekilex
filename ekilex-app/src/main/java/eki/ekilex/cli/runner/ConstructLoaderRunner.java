package eki.ekilex.cli.runner;

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

import eki.common.data.Count;
import eki.ekilex.data.conx.Construct;
import eki.ekilex.data.conx.ConstructMember;
import eki.ekilex.data.conx.Sentence;
import eki.ekilex.data.conx.SentenceMember;
import eki.ekilex.service.db.ConstructDbService;

@Component
public class ConstructLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(ConstructLoaderRunner.class);

	@Autowired
	private ConstructDbService constructDbService;

	private List<Map<String, Object>> origConstructs;

	private List<Map<String, Object>> origConstructMembers;

	private List<Map<String, Object>> origRealizations;

	private List<Map<String, Object>> origRealizationMembers;

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

		List<Map<String, Object>> origConstructRealizations = origRealizations.stream()
				.filter(record -> record.get("construction_id") != null)
				.collect(Collectors.toList());

		for (Map<String, Object> origRealization : origConstructRealizations) {

			createSentence(origRealization, sentenceCreateCount, sentenceMemberCreateCount, sentenceMemberFirstHomonymCount, sentenceMemberMissingWordCount);
		}

		logger.info("Created constructs: {}", constructCreateCount.getValue());
		logger.info("Created construct members: {}", constructMemberCreateCount.getValue());
		logger.info("Created sentences: {}", sentenceCreateCount.getValue());
		logger.info("Created sentence members: {}", sentenceMemberCreateCount.getValue());
		logger.info("Linked first available homonym count: {}", sentenceMemberFirstHomonymCount.getValue());
		logger.info("Missing word count: {}", sentenceMemberMissingWordCount.getValue());
		logger.info("Done loading");
	}

	@SuppressWarnings("unchecked")
	public void loadDataFile(String dataFilePath) throws Exception {

		//construction, construction_relation?, conx_member, word, realization, realization_member, realization_translation?, form, statistics

		String dataJson = readFileContent(dataFilePath);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> dataMap = objectMapper.readValue(dataJson, Map.class);
		origConstructs = (List<Map<String, Object>>) dataMap.get("construction");
		origConstructMembers = (List<Map<String, Object>>) dataMap.get("conx_member");
		origRealizations = (List<Map<String, Object>>) dataMap.get("realization");
		origRealizationMembers = (List<Map<String, Object>>) dataMap.get("realization_member");
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
	}

	public void createConstructs(Count constructCreateCount, Count constructMemberCreateCount) {

		// -- constructs --

		for (Map<String, Object> origConstruct : origConstructs) {

			String origConstructId = getId(origConstruct);
			String constructName = getStringValue(origConstruct, "name_expert");
			String constructDescription = getStringValue(origConstruct, "definition");
			String constructTypeCode = getStringValue(origConstruct, "type");
			String constructSubtypeCode = getStringValue(origConstruct, "subtype");
			String schematicityCode = getStringValue(origConstruct, "schematicity");
			String proficiencyLevelCode = getStringValue(origConstruct, "language_level");
			String lang = getStringValue(origConstruct, "language");

			Construct construct = new Construct();
			construct.setName(constructName);
			construct.setDescription(constructDescription);
			construct.setConstructTypeCode(constructTypeCode);
			construct.setConstructSubtypeCode(constructSubtypeCode);
			construct.setSchematicityCode(schematicityCode);
			construct.setProficiencyLevelCode(proficiencyLevelCode);
			construct.setLang(lang);

			if (isCreate) {

				Long constructId = constructDbService.createConstruct(construct);
				constructIdMap.put(origConstructId, constructId);
				constructCreateCount.increment();
			}
		}

		// -- construct members - 

		for (Map<String, Object> origConstructMember : origConstructMembers) {

			String origConstructMemberId = getId(origConstructMember);
			String origConstructId = getId(origConstructMember, "conx_id");
			String cgovernmentCode = StringUtils.lowerCase(getStringValue(origConstructMember, "member"));
			boolean isHead = getBooleanValue(origConstructMember, "head");
			String memberRole = getStringValue(origConstructMember, "role");
			Integer memberOrder = getIntegerValue(origConstructMember, "member_order");
			// TODO semantic_role

			Map<String, Object> origConstruct = origConstructMap.get(origConstructId);
			String lang = getStringValue(origConstruct, "language");
			char classifValueSeparator = ',';
			if (StringUtils.equals(lang, "rus")) {
				classifValueSeparator = ';';
			}

			List<String> memberLemmaMorphCodes = getStringValues(origConstructMember, "morph_lemma", classifValueSeparator);
			List<String> memberMorphCodes = getStringValues(origConstructMember, "morph_possible", classifValueSeparator);
			List<String> memberPosGroupCodes = getStringValues(origConstructMember, "pos");
			List<String> memberDeprelCodes = getStringValues(origConstructMember, "deprel");

			Long constructId = constructIdMap.get(origConstructId);

			ConstructMember constructMember = new ConstructMember();
			constructMember.setConstructId(constructId);
			constructMember.setCgovernmentCode(cgovernmentCode);
			constructMember.setMemberRole(memberRole);
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
				if (CollectionUtils.isNotEmpty(memberPosGroupCodes)) {
					constructDbService.createConstructMemberPosGroups(constructMemberId, memberPosGroupCodes);
				}
				if (CollectionUtils.isNotEmpty(memberDeprelCodes)) {
					constructDbService.createConstructMemberDeprelCodes(constructMemberId, memberDeprelCodes);
				}
			}
		}
	}

	public Long createSentence(
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

		Map<String, Object> origConstruct = origConstructMap.get(origConstructId);
		String constructLang = getStringValue(origConstruct, "language");

		Long constructId = constructIdMap.get(origConstructId);

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
		}

		List<Map<String, Object>> thisOrigRealizationMembers = origRealizationMembers.stream()
				.filter(record -> idEquals(record, "realization_id", origRealizationId))
				.collect(Collectors.toList());

		// -- sentence members --

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
			String posGroupCode = null;
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
				posGroupCode = getStringValue(origRealizationMemberFormWord, "pos");

				List<Long> wordIds = migrationDbService.getWordIds(sentenceMemberWordValue, constructLang, sentenceMemberFormValue, sentenceMemberMorphCode, DATASET_EKI);

				Long wordId = null;

				if (CollectionUtils.isEmpty(wordIds)) {
					// missing matching word records
					sentenceMemberMissingWordCount.increment();
					logger.warn("Word not found: \"{} - {} - {}\"", sentenceMemberWordValue, sentenceMemberFormValue, sentenceMemberMorphCode);
					// fallback to sentence member value instead
					sentenceMemberValue = sentenceMemberFormValue;
				} else if (wordIds.size() == 1) {
					wordId = wordIds.get(0);
				} else {
					// pick first
					sentenceMemberFirstHomonymCount.increment();
					wordId = wordIds.get(0);
				}

				if (wordId != null) {

					// pick first
					List<Long> lexemeIds = migrationDbService.getLexemeIds(wordId, DATASET_EKI);
					lexemeId = lexemeIds.get(0);

					// pick first
					List<Long> formIds = migrationDbService.getFormIds(wordId, sentenceMemberFormValue, sentenceMemberMorphCode);
					formId = formIds.get(0);
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
			sentenceMember.setPosGroupCode(posGroupCode);
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

	public boolean idEquals(Map<String, Object> dataMap, String fieldName, String fieldValue) {
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

	private List<String> getStringValues(Map<String, Object> dataMap, String fieldName) {
		return getStringValues(dataMap, fieldName, ',');
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
