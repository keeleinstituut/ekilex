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
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.SystemConstant;

@Component
public class Qq2LoaderRunner implements InitializingBean, SystemConstant, TableName {

	private static Logger logger = LoggerFactory.getLogger(Qq2LoaderRunner.class);

	private static final String TRANSFORM_MORPH_DERIV_FILE_PATH = "./fileresources/csv/transform-morph-deriv.csv";

	@Autowired
	private BasicDbService basicDbService;

	private Map<String, String> morphToMorphMap;

	private Map<String, String> morphToDerivMap;

	@Override
	public void afterPropertiesSet() throws Exception {

		List<String> morphDerivMapLines = getContentLines(TRANSFORM_MORPH_DERIV_FILE_PATH);
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
	}

	@Transactional
	public void execute(String dataXmlFilePath, String dataLang, String[] dataset) throws Exception {

		dataLang = unifyLang(dataLang);
		final String articleExp = "/x:sr/x:A";
		final String defaultWordMorphCode = "SgN";
		final int defaultHomonymNr = 1;
		final String wordDisplayFormStripChars = ".+'()¤:";

		logger.debug("Starting loading QQ2...");

		long t1, t2;
		t1 = System.currentTimeMillis();

		SAXReader dataDocParser = new SAXReader();

		File dataDocFile = new File(dataXmlFilePath);
		FileInputStream dataDocFileInputStream = new FileInputStream(dataDocFile);
		InputStreamReader dataDocInputReader = new InputStreamReader(dataDocFileInputStream, UTF_8);
		Document dataDoc = dataDocParser.read(dataDocInputReader);
		dataDocInputReader.close();
		dataDocFileInputStream.close();

		/*
		 * String simplifiedPath = "//x:xr";
		 * logFullPaths(dataDoc, simplifiedPath);
		 */

		List<Element> articleNodes = dataDoc.selectNodes(articleExp);
		int articleCount = articleNodes.size();
		logger.debug("Extracted {} articles", articleNodes.size());

		Map<String, Map<Integer, Long>> wordHomonymWordIdMap = new HashMap<>();
		Map<Integer, Long> wordIdMap;
		Map<Long, List<Map<String, Object>>> wordIdRectionMap = new HashMap<>();
		List<Map<String, Object>> rectionObjs;
		Map<String, Object> rectionObj;
		Map<Long, List<Map<String, Object>>> wordIdGrammarMap = new HashMap<>();
		List<Map<String, Object>> grammarObjs;
		Map<String, Object> grammarObj;

		Map<String, Object> tableRowParamMap;
		Element headerNode, contentNode;
		List<Element> wordGroupNodes, grammarNodes, meaningGroupNodes, meaningNodes, wordMatchNodes;
		Element wordNode, wordVocalFormNode, morphNode, rectionNode, definitionValueNode, wordMatchValueNode;

		List<Long> newWordIds;
		String word, wordMatch, homonymNrStr, wordDisplayForm, wordVocalForm, rection, grammarLang, grammar, lexemeLevel1Str, wordMatchLang, definition;
		String sourceMorphCode, destinMorphCode, destinDerivCode;
		int homonymNr, lexemeLevel1, lexemeLevel2;
		Long wordId, paradigmId, formId, meaningId, lexemeId;
		int wordDuplicateCount = 0;

		int articleCounter = 0;
		int progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Element articleNode : articleNodes) {

			// header...
			newWordIds = new ArrayList<>();
			headerNode = (Element) articleNode.selectSingleNode("x:P");
			wordGroupNodes = headerNode.selectNodes("x:mg");

			for (Element wordGroupNode : wordGroupNodes) {

				// word, from...
				wordNode = (Element) wordGroupNode.selectSingleNode("x:m");
				word = wordDisplayForm = wordNode.getTextTrim();
				word = StringUtils.replaceChars(word, wordDisplayFormStripChars, "");
				homonymNrStr = wordNode.attributeValue("i");
				if (StringUtils.isBlank(homonymNrStr)) {
					homonymNr = 1;
				} else {
					homonymNr = Integer.parseInt(homonymNrStr);
					word = StringUtils.substringBefore(word, homonymNrStr);
				}
				wordVocalFormNode = (Element) wordGroupNode.selectSingleNode("x:hld");
				if (wordVocalFormNode == null) {
					wordVocalForm = null;
				} else {
					wordVocalForm = wordVocalFormNode.getTextTrim();
				}
				morphNode = (Element) wordGroupNode.selectSingleNode("x:vk");
				if (morphNode == null) {
					destinMorphCode = defaultWordMorphCode;
					destinDerivCode = null;
				} else {
					sourceMorphCode = morphNode.getTextTrim();
					destinMorphCode = morphToMorphMap.get(sourceMorphCode);
					destinDerivCode = morphToDerivMap.get(sourceMorphCode);
				}

				// save word+paradigm+form
				wordIdMap = wordHomonymWordIdMap.get(word);
				if (wordIdMap == null) {
					wordIdMap = new HashMap<>();
					wordHomonymWordIdMap.put(word, wordIdMap);
				}
				wordId = wordIdMap.get(homonymNr);
				if (wordId == null) {

					// word
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("lang", dataLang);
					tableRowParamMap.put("morph_code", destinMorphCode);
					tableRowParamMap.put("homonym_nr", homonymNr);
					wordId = basicDbService.create(WORD, tableRowParamMap);
					wordIdMap.put(homonymNr, wordId);

					// paradigm
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("word_id", wordId);
					paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);

					// form
					tableRowParamMap = new HashMap<>();
					tableRowParamMap.put("paradigm_id", paradigmId);
					tableRowParamMap.put("morph_code", destinMorphCode);
					tableRowParamMap.put("value", word);
					tableRowParamMap.put("display_form", wordDisplayForm);
					tableRowParamMap.put("vocal_form", wordVocalForm);
					tableRowParamMap.put("is_word", Boolean.TRUE);
					basicDbService.create(FORM, tableRowParamMap);

				} else {
					logger.warn("Word duplicate: \"{}\"", word);
					wordDuplicateCount++;
				}
				newWordIds.add(wordId);

				// further references...

				// rections...
				rectionNode = (Element) wordGroupNode.selectSingleNode("x:r");
				if (rectionNode == null) {
					rection = null;
				} else {
					rection = rectionNode.getTextTrim();

					rectionObjs = wordIdRectionMap.get(wordId);
					if (rectionObjs == null) {
						rectionObjs = new ArrayList<>();
						wordIdRectionMap.put(wordId, rectionObjs);
					}
					rectionObj = new HashMap<>();
					rectionObj.put("value", rection);
					rectionObjs.add(rectionObj);
				}

				// grammar...
				grammarNodes = wordGroupNode.selectNodes("x:grg/x:gki");
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
					grammarObj.put("dataset", dataset);
					grammarObjs.add(grammarObj);
				}
			}

			// body...
			contentNode = (Element) articleNode.selectSingleNode("x:S");

			if (contentNode == null) {
				logger.warn("No body element in article!");
			} else {

				meaningGroupNodes = contentNode.selectNodes("x:tp");

				for (Element meaningGroupNode : meaningGroupNodes) {

					lexemeLevel1Str = meaningGroupNode.attributeValue("tnr");
					lexemeLevel1 = Integer.valueOf(lexemeLevel1Str);

					meaningNodes = meaningGroupNode.selectNodes("x:tg");
					lexemeLevel2 = 0;

					for (Element meaningNode : meaningNodes) {

						// meaning
						tableRowParamMap = new HashMap<>();
						tableRowParamMap.put("dataset", dataset);
						meaningId = basicDbService.create(MEANING, tableRowParamMap);

						// new words lexemes+rections+grammar
						for (Long newWordId : newWordIds) {

							tableRowParamMap = new HashMap<>();
							tableRowParamMap.put("word_id", newWordId);
							tableRowParamMap.put("meaning_id", meaningId);
							tableRowParamMap.put("level1", lexemeLevel1);
							tableRowParamMap.put("level2", lexemeLevel2);
							tableRowParamMap.put("dataset", dataset);
							lexemeId = basicDbService.create(LEXEME, tableRowParamMap);

							// word match lexeme rections
							rectionObjs = wordIdRectionMap.get(newWordId);
							if (CollectionUtils.isNotEmpty(rectionObjs)) {
								for (Map<String, Object> newWordRectionObj : rectionObjs) {
									newWordRectionObj.put("lexeme_id", lexemeId);
									basicDbService.create(RECTION, newWordRectionObj);
								}
							}

							// word match lexeme grammars
							grammarObjs = wordIdGrammarMap.get(newWordId);
							if (CollectionUtils.isNotEmpty(grammarObjs)) {
								for (Map<String, Object> newWordGrammarObj : grammarObjs) {
									newWordGrammarObj.put("lexeme_id", lexemeId);
									basicDbService.create(GRAMMAR, newWordGrammarObj);
								}
							}
						}

						wordMatchNodes = meaningNode.selectNodes("x:xp/x:xg");
						lexemeLevel2++;

						for (Element wordMatchNode : wordMatchNodes) {

							wordMatchLang = wordMatchNode.attributeValue("lang");
							wordMatchLang = unifyLang(wordMatchLang);
							wordMatchValueNode = (Element) wordMatchNode.selectSingleNode("x:x");
							wordMatch = wordMatchValueNode.getTextTrim();
							wordMatch = StringUtils.replaceChars(wordMatch, wordDisplayFormStripChars, "");

							if (StringUtils.isBlank(wordMatch)) {
								continue;
							}

							definitionValueNode = (Element) wordMatchNode.selectSingleNode("x:xd");
							if (definitionValueNode != null) {
								definition = definitionValueNode.getTextTrim();

								// definition
								tableRowParamMap = new HashMap<>();
								tableRowParamMap.put("meaning_id", meaningId);
								tableRowParamMap.put("value", definition);
								tableRowParamMap.put("lang", wordMatchLang);
								tableRowParamMap.put("dataset", dataset);
								basicDbService.create(DEFINITION, tableRowParamMap);
							}

							// save word match word+paradigm+form
							wordIdMap = wordHomonymWordIdMap.get(wordMatch);
							if (wordIdMap == null) {
								wordIdMap = new HashMap<>();
								wordHomonymWordIdMap.put(wordMatch, wordIdMap);
							}
							wordId = wordIdMap.get(defaultHomonymNr);
							if (wordId == null) {

								// word
								tableRowParamMap = new HashMap<>();
								tableRowParamMap.put("lang", wordMatchLang);
								tableRowParamMap.put("morph_code", defaultWordMorphCode);
								tableRowParamMap.put("homonym_nr", defaultHomonymNr);
								wordId = basicDbService.create(WORD, tableRowParamMap);
								wordIdMap.put(defaultHomonymNr, wordId);

								// paradigm
								tableRowParamMap = new HashMap<>();
								tableRowParamMap.put("word_id", wordId);
								paradigmId = basicDbService.create(PARADIGM, tableRowParamMap);

								// form
								tableRowParamMap = new HashMap<>();
								tableRowParamMap.put("paradigm_id", paradigmId);
								tableRowParamMap.put("morph_code", defaultWordMorphCode);
								tableRowParamMap.put("value", wordMatch);
								tableRowParamMap.put("is_word", Boolean.TRUE);
								basicDbService.create(FORM, tableRowParamMap);

							} else {
								logger.warn("Word match duplicate: \"{}\"", wordMatch);
								wordDuplicateCount++;
							}

							// word match lexeme
							tableRowParamMap = new HashMap<>();
							tableRowParamMap.put("word_id", wordId);
							tableRowParamMap.put("meaning_id", meaningId);
							tableRowParamMap.put("dataset", dataset);
							lexemeId = basicDbService.create(LEXEME, tableRowParamMap);

							// word match lexeme rection
							rectionNode = (Element) wordMatchValueNode.selectSingleNode("x:xr");
							if (rectionNode != null) {
								rection = rectionNode.getTextTrim();
								tableRowParamMap = new HashMap<>();
								tableRowParamMap.put("lexeme_id", lexemeId);
								tableRowParamMap.put("value", rection);
								basicDbService.create(RECTION, tableRowParamMap);
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

		t2 = System.currentTimeMillis();
		logger.debug("Done loading in {} ms", (t2 - t1));
	}

	private String unifyLang(String lang) {
		Locale locale = new Locale(lang);
		lang = locale.getISO3Language();
		return lang;
	}

	private void logFullPaths(Document dataDoc, String simplifiedPath) {
		List<Element> synNodes = dataDoc.selectNodes(simplifiedPath);
		List<String> synPaths = new ArrayList<>();
		for (Element synNode : synNodes) {
			if (!synPaths.contains(synNode.getPath())) {
				synPaths.add(synNode.getPath());
			}
		}
		for (String synPath : synPaths) {
			System.out.println(synPath);
		}
	}

	private List<String> getContentLines(String resourceFilePath) throws Exception {
		InputStream resourceInputStream = new FileInputStream(resourceFilePath);
		List<String> contentLines = IOUtils.readLines(resourceInputStream, UTF_8);
		resourceInputStream.close();
		return contentLines;
	}
}
