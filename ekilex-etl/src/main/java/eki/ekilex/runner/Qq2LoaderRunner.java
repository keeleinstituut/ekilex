package eki.ekilex.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.constant.TableName;
import eki.common.data.Count;
import eki.common.data.PgVarcharArray;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;

@Component
public class Qq2LoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String SQL_SELECT_WORD_BY_FORM_AND_HOMONYM = "sql/select_word_by_form_and_homonym.sql";

	private static final String SQL_SELECT_WORD_MAX_HOMONYM = "sql/select_word_max_homonym.sql";

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "csv/transform-morph-deriv.csv";

	@Autowired
	private BasicDbService basicDbService;

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	private String sqlSelectWordByFormAndHomonym;

	private String sqlSelectWordMaxHomonym;

	@Override
	public void afterPropertiesSet() throws Exception {

		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream resourceFileInputStream;

		resourceFileInputStream = classLoader.getResourceAsStream(TRANSFORM_MORPH_DERIV_FILE_PATH);
		List<String> morphDerivMapLines = getContentLines(resourceFileInputStream);
		morphToMorphMap = new HashMap<>();
		morphToDerivMap = new HashMap<>();
		for (String morphDerivMapLine : morphDerivMapLines) {
			if (StringUtils.isBlank(morphDerivMapLine)) {
				continue;
			}
			String[] morphDerivMapLineParts = StringUtils.split(morphDerivMapLine, CSV_SEPARATOR);
			String sourceMorphCode = morphDerivMapLineParts[0];
			String destinMorphCode = morphDerivMapLineParts[1];
			String destinDerivCode = morphDerivMapLineParts[2];
			morphToMorphMap.put(sourceMorphCode, destinMorphCode);
			if (!StringUtils.equals(destinDerivCode, String.valueOf(CSV_EMPTY_CELL))) {
				morphToDerivMap.put(sourceMorphCode, destinDerivCode);
			}
		}

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_BY_FORM_AND_HOMONYM);
		sqlSelectWordByFormAndHomonym = getContent(resourceFileInputStream);

		resourceFileInputStream = classLoader.getResourceAsStream(SQL_SELECT_WORD_MAX_HOMONYM);
		sqlSelectWordMaxHomonym = getContent(resourceFileInputStream);
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String[] datasets) throws Exception {

		logger.debug("Starting loading QQ2...");

		final String articleExp = "/x:sr/x:A";
		final String articleHeaderExp = "x:P";
		final String wordGroupExp = "x:mg";
		final String wordExp = "x:m";
		final String wordVocalFormExp = "x:hld";
		final String wordMorphExp = "x:vk";
		final String wordRectionExp = "x:r";
		final String wordGrammarExp = "x:grg/x:gki";
		final String articleBodyExp = "x:S";
		final String meaningGroupExp = "x:tp";
		final String meaningExp = "x:tg";
		final String wordMatchExpr = "x:xp/x:xg";
		final String wordMatchValueExp = "x:x";
		final String definitionValueExp = "x:xd";
		final String wordMatchRectionExp = "x:xr";
		final String synonymExp = "x:syn";

		final String pseudoHomonymAttr = "i";
		final String lexemeLevel1Attr = "tnr";

		final String defaultWordMorphCode = "SgN";
		final String wordDisplayFormStripChars = ".+'()¤:_";
		final int defaultHomonymNr = 1;

		long t1, t2;
		t1 = System.currentTimeMillis();

		dataLang = unifyLang(dataLang);
		Document dataDoc = readDocument(dataXmlFilePath);

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleNodes.size());

		Map<Long, List<Map<String, Object>>> wordIdRectionMap = new HashMap<>();
		Map<Long, List<Map<String, Object>>> wordIdGrammarMap = new HashMap<>();

		Element headerNode, contentNode;
		List<Element> wordGroupNodes, rectionNodes, grammarNodes, meaningGroupNodes, meaningNodes, definitionValueNodes, wordMatchNodes, synonymNodes;
		Element wordNode, wordVocalFormNode, morphNode, wordMatchValueNode;

		List<Long> newWordIds, synonymLevel1WordIds, synonymLevel2WordIds;
		String word, wordMatch, pseudoHomonymNr, wordDisplayForm, wordVocalForm, lexemeLevel1Str, wordMatchLang;
		String sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr, lexemeLevel1, lexemeLevel2, lexemeLevel3;
		Long wordId, meaningId, lexemeId;

		Count wordDuplicateCount = new Count();
		Count lexemeDuplicateCount = new Count();

		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			contentNode = (Element) articleNode.selectSingleNode(articleBodyExp);
			if (contentNode == null) {
				continue;
			}

			// header...
			newWordIds = new ArrayList<>();
			headerNode = (Element) articleNode.selectSingleNode(articleHeaderExp);
			wordGroupNodes = headerNode.selectNodes(wordGroupExp);
			word = null;
			List<String> tmpWords = new ArrayList<>();

			for (Element wordGroupNode : wordGroupNodes) {

				// word, form...
				wordNode = (Element) wordGroupNode.selectSingleNode(wordExp);
				word = wordDisplayForm = wordNode.getTextTrim();
				word = StringUtils.replaceChars(word, wordDisplayFormStripChars, "");
				pseudoHomonymNr = wordNode.attributeValue(pseudoHomonymAttr);
				if (StringUtils.isNotBlank(pseudoHomonymNr)) {
					word = StringUtils.substringBefore(word, pseudoHomonymNr);
				}
				homonymNr = getWordMaxHomonymNr(word, dataLang);
				homonymNr++;
				wordVocalFormNode = (Element) wordGroupNode.selectSingleNode(wordVocalFormExp);
				if (wordVocalFormNode == null) {
					wordVocalForm = null;
				} else {
					wordVocalForm = wordVocalFormNode.getTextTrim();
				}
				morphNode = (Element) wordGroupNode.selectSingleNode(wordMorphExp);
				if (morphNode == null) {
					destinMorphCode = defaultWordMorphCode;
					destinDerivCode = null;
				} else {
					sourceMorphCode = morphNode.getTextTrim();
					destinMorphCode = morphToMorphMap.get(sourceMorphCode);
					destinDerivCode = morphToDerivMap.get(sourceMorphCode);//currently not used
				}

				// save word+paradigm+form
				wordId = saveWord(word, wordDisplayForm, wordVocalForm, homonymNr, destinMorphCode, dataLang, wordDuplicateCount);
				newWordIds.add(wordId);
				tmpWords.add(word);

				// further references...

				// rections...
				rectionNodes = wordGroupNode.selectNodes(wordRectionExp);
				extractRections(rectionNodes, wordId, wordIdRectionMap);

				// grammar...
				grammarNodes = wordGroupNode.selectNodes(wordGrammarExp);
				extractGrammar(grammarNodes, wordId, datasets, wordIdGrammarMap);
			}

			// body...

			synonymNodes = contentNode.selectNodes(synonymExp);
			synonymLevel1WordIds = saveWords(synonymNodes, defaultHomonymNr, defaultWordMorphCode, dataLang, wordDuplicateCount);

			meaningGroupNodes = contentNode.selectNodes(meaningGroupExp);//x:tp

			for (Element meaningGroupNode : meaningGroupNodes) {

				lexemeLevel1Str = meaningGroupNode.attributeValue(lexemeLevel1Attr);
				lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

				meaningNodes = meaningGroupNode.selectNodes(meaningExp);//x:tg
				lexemeLevel2 = 0;

				for (Element meaningNode : meaningNodes) {

					lexemeLevel2++;
					lexemeLevel3 = 0;

					synonymNodes = meaningNode.selectNodes(synonymExp);
					synonymLevel2WordIds = saveWords(synonymNodes, defaultHomonymNr, defaultWordMorphCode, dataLang, wordDuplicateCount);

					wordMatchNodes = meaningNode.selectNodes(wordMatchExpr);//x:xp/x:xg

					for (Element wordMatchNode : wordMatchNodes) {

						lexemeLevel3++;

						wordMatchLang = wordMatchNode.attributeValue("lang");
						wordMatchLang = unifyLang(wordMatchLang);
						wordMatchValueNode = (Element) wordMatchNode.selectSingleNode(wordMatchValueExp);
						wordMatch = wordMatchValueNode.getTextTrim();
						wordMatch = StringUtils.replaceChars(wordMatch, wordDisplayFormStripChars, "");

						if (StringUtils.isBlank(wordMatch)) {
							continue;
						}

						wordId = saveWord(wordMatch, null, null, defaultHomonymNr, defaultWordMorphCode, wordMatchLang, wordDuplicateCount);

						// meaning
						meaningId = createMeaning(datasets);

						// definitions
						definitionValueNodes = wordMatchNode.selectNodes(definitionValueExp);
						saveDefinitions(definitionValueNodes, meaningId, wordMatchLang, datasets);

						// word match lexeme
						lexemeId = createLexeme(wordId, meaningId, null, null, null, datasets);
						if (lexemeId == null) {
							lexemeDuplicateCount.increment();
						} else {

							// word match lexeme rection
							rectionNodes = wordMatchValueNode.selectNodes(wordMatchRectionExp);
							saveRections(rectionNodes, lexemeId);
						}

						// new words lexemes+rections+grammar
						for (Long newWordId : newWordIds) {

							lexemeId = createLexeme(newWordId, meaningId, lexemeLevel1, lexemeLevel2, lexemeLevel3, datasets);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							} else {

								// word match lexeme rections
								createRections(wordIdRectionMap, lexemeId, newWordId);

								// word match lexeme grammars
								createGrammars(wordIdGrammarMap, lexemeId, newWordId);
							}
						}

						for (Long synonymWordId : synonymLevel1WordIds) {
							lexemeId = createLexeme(synonymWordId, meaningId, null, null, null, datasets);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							}
						}

						for (Long synonymWordId : synonymLevel2WordIds) {
							lexemeId = createLexeme(synonymWordId, meaningId, null, null, null, datasets);
							if (lexemeId == null) {
								lexemeDuplicateCount.increment();
							}
						}
					}
				}
			}

			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				logger.debug("{} articles iterated", articleCounter);
			}
		}

		logger.debug("Found {} word duplicates", wordDuplicateCount);
		logger.debug("Found {} lexeme duplicates", lexemeDuplicateCount);

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private List<Long> saveWords(List<Element> synonymNodes, int homonymNr, String wordMorphCode, String lang, Count wordDuplicateCount) throws Exception {

		List<Long> synonymWordIds = new ArrayList<>();
		String synonym;
		Long wordId;

		for (Element synonymNode : synonymNodes) {

			synonym = synonymNode.getTextTrim();
			wordId = saveWord(synonym, null, null, homonymNr, wordMorphCode, lang, wordDuplicateCount);
			synonymWordIds.add(wordId);
		}
		return synonymWordIds;
	}

	private Long saveWord(String word, String wordDisplayForm, String wordVocalForm, int homonymNr, String wordMorphCode, String lang, Count wordDuplicateCount) throws Exception {

		Map<String, Object> tableRowValueMap = getWord(word, homonymNr, lang);
		Long wordId;

		if (tableRowValueMap == null) {

			// word
			wordId = createWord(wordMorphCode, homonymNr, lang);

			// paradigm
			Long paradigmId = createParadigm(wordId);

			// form
			createForm(word, wordDisplayForm, wordVocalForm, wordMorphCode, paradigmId);

		} else {
			wordId = (Long) tableRowValueMap.get("id");
			wordDuplicateCount.increment();
		}
		return wordId;
	}

	private void saveDefinitions(List<Element> definitionValueNodes, Long meaningId, String wordMatchLang, String[] datasets) throws Exception {

		if (definitionValueNodes == null) {
			return;
		}
		for (Element definitionValueNode : definitionValueNodes) {
			String definition = definitionValueNode.getTextTrim();
			createDefinition(meaningId, definition, wordMatchLang, datasets);
		}
	}

	private void saveRections(List<Element> rectionNodes, Long lexemeId) throws Exception {

		if (rectionNodes == null) {
			return;
		}
		for (Element rectionNode : rectionNodes) {
			String rection = rectionNode.getTextTrim();
			createRection(lexemeId, rection);
		}
	}

	private void extractGrammar(List<Element> grammarNodes, Long wordId, String[] datasets, Map<Long, List<Map<String, Object>>> wordIdGrammarMap) {

		List<Map<String, Object>> grammarObjs;
		Map<String, Object> grammarObj;
		String grammarLang;
		String grammar;

		for (Element grammarNode : grammarNodes) {

			grammarLang = grammarNode.attributeValue("lang");
			grammarLang = unifyLang(grammarLang);
			grammar = grammarNode.getTextTrim();

			grammarObjs = wordIdGrammarMap.get(wordId);
			if (grammarObjs == null) {
				grammarObjs = new ArrayList<>();
				wordIdGrammarMap.put(wordId, grammarObjs);
			}
			grammarObj = new HashMap<>();
			grammarObj.put("lang", grammarLang);
			grammarObj.put("value", grammar);
			grammarObj.put("datasets", new PgVarcharArray(datasets));
			grammarObjs.add(grammarObj);
		}
	}

	private void extractRections(List<Element> rectionNodes, Long wordId, Map<Long, List<Map<String, Object>>> wordIdRectionMap) {

		if (rectionNodes == null) {
			return;
		}
		List<Map<String, Object>> rectionObjs = wordIdRectionMap.get(wordId);
		if (rectionObjs == null) {
			rectionObjs = new ArrayList<>();
			wordIdRectionMap.put(wordId, rectionObjs);
		}
		for (Element rectionNode : rectionNodes) {
			String rection = rectionNode.getTextTrim();
			Map<String, Object> rectionObj = new HashMap<>();
			rectionObj.put("value", rection);
			rectionObjs.add(rectionObj);
		}
	}

	private int getWordMaxHomonymNr(String word, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordMaxHomonym, tableRowParamMap);
		int homonymNr = (int) tableRowValueMap.get("max_homonym_nr");
		return homonymNr;
	}

	private Map<String, Object> getWord(String word, int homonymNr, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word", word);
		tableRowParamMap.put("homonymNr", homonymNr);
		tableRowParamMap.put("lang", lang);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(sqlSelectWordByFormAndHomonym, tableRowParamMap);
		return tableRowValueMap;
	}

	private Long createMeaning(String[] datasets) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("datasets", new PgVarcharArray(datasets));
		Long meaningId = basicDbService.create(MEANING, tableRowParamMap);
		return meaningId;
	}

	private Long createLexeme(Long wordId, Long meaningId, Integer lexemeLevel1, Integer lexemeLevel2, Integer lexemeLevel3, String[] datasets) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		tableRowParamMap.put("meaning_id", meaningId);
		if (lexemeLevel1 != null) {
			tableRowParamMap.put("level1", lexemeLevel1);
		}
		if (lexemeLevel2 != null) {
			tableRowParamMap.put("level2", lexemeLevel2);
		}
		if (lexemeLevel3 != null) {
			tableRowParamMap.put("level3", lexemeLevel3);
		}
		tableRowParamMap.put("datasets", new PgVarcharArray(datasets));
		Long lexemeId = basicDbService.createIfNotExists(LEXEME, tableRowParamMap);
		return lexemeId;
	}

	private void createDefinition(Long meaningId, String definition, String lang, String[] datasets) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("meaning_id", meaningId);
		tableRowParamMap.put("value", definition);
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("datasets", new PgVarcharArray(datasets));
		basicDbService.create(DEFINITION, tableRowParamMap);
	}

	private void createForm(String word, String wordDisplayForm, String wordVocalForm, String morphCode, Long paradigmId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("paradigm_id", paradigmId);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("value", word);
		if (StringUtils.isNotBlank(wordDisplayForm)) {
			tableRowParamMap.put("display_form", wordDisplayForm);
		}
		if (StringUtils.isNotBlank(wordVocalForm)) {
			tableRowParamMap.put("vocal_form", wordVocalForm);
		}
		tableRowParamMap.put("is_word", Boolean.TRUE);
		basicDbService.create(FORM, tableRowParamMap);
	}

	private Long createParadigm(Long wordId) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("word_id", wordId);
		Long paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);
		return paradigmId;
	}

	private Long createWord(final String morphCode, final int homonymNr, String lang) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lang", lang);
		tableRowParamMap.put("morph_code", morphCode);
		tableRowParamMap.put("homonym_nr", homonymNr);
		Long wordId = basicDbService.create(WORD, tableRowParamMap);
		return wordId;
	}

	private void createGrammars(Map<Long, List<Map<String, Object>>> wordIdGrammarMap, Long lexemeId, Long wordId) throws Exception {

		List<Map<String, Object>> grammarObjs = wordIdGrammarMap.get(wordId);
		if (CollectionUtils.isNotEmpty(grammarObjs)) {
			for (Map<String, Object> grammarObj : grammarObjs) {
				grammarObj.put("lexeme_id", lexemeId);
				basicDbService.createIfNotExists(GRAMMAR, grammarObj);
			}
		}
	}

	private void createRections(Map<Long, List<Map<String, Object>>> wordIdRectionMap, Long lexemeId, Long wordId) throws Exception {

		List<Map<String, Object>> rectionObjs = wordIdRectionMap.get(wordId);
		if (CollectionUtils.isNotEmpty(rectionObjs)) {
			for (Map<String, Object> rectionObj : rectionObjs) {
				rectionObj.put("lexeme_id", lexemeId);
				basicDbService.createIfNotExists(RECTION, rectionObj);
			}
		}
	}

	private void createRection(Long lexemeId, String rection) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("lexeme_id", lexemeId);
		tableRowParamMap.put("value", rection);
		basicDbService.createIfNotExists(RECTION, tableRowParamMap);
	}

	private Document readDocument(String dataXmlFilePath) throws Exception {

		SAXReader dataDocParser = new SAXReader();
		File dataDocFile = new File(dataXmlFilePath);
		FileInputStream dataDocFileInputStream = new FileInputStream(dataDocFile);
		InputStreamReader dataDocInputReader = new InputStreamReader(dataDocFileInputStream, UTF_8);
		Document dataDoc = dataDocParser.read(dataDocInputReader);
		dataDocInputReader.close();
		dataDocFileInputStream.close();
		return dataDoc;
	}

	private String unifyLang(String lang) {
		Locale locale = new Locale(lang);
		lang = locale.getISO3Language();
		return lang;
	}

	private List<String> getContentLines(InputStream resourceInputStream) throws Exception {
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}

	private String getContent(InputStream resourceInputStream) throws Exception {
		String content = IOUtils.toString(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return content;
	}
}
