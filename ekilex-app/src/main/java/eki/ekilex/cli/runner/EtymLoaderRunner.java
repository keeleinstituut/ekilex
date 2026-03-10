package eki.ekilex.cli.runner;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.data.Count;

@Component
public class EtymLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(EtymLoaderRunner.class);

	private static final String REPORT_TYPE_LANGUAGE_GROUP = "lgr";

	private static final String REPORT_TYPE_GROUP_LANG = "grl";

	private final String reportFileName = "vsl-language-trees-report.txt";

	private final String reportType = REPORT_TYPE_LANGUAGE_GROUP;

	private boolean makeReport = false;

	//@Transactional(rollbackFor = Exception.class)
	public void execute(String dataXmlFilePath) throws Exception {

		logger.info("Starting loading...");

		Count totalArticleCount = new Count();
		Count skippedArticleCount = new Count();
		Count variantArticleCount = new Count();
		Count variantCount = new Count();
		Count missingLangArticleCount = new Count();
		Count missingWordArticleCount = new Count();

		FileOutputStream reportStream = null;
		OutputStreamWriter reportWriter = null;
		if (makeReport) {
			reportStream = new FileOutputStream(reportFileName);
			reportWriter = new OutputStreamWriter(reportStream, StandardCharsets.UTF_8);
		}

		Document dataDoc = readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		if (StringUtils.contains(dataXmlFilePath, "ss1")) {
			handleEtymSs1(rootElement, totalArticleCount, skippedArticleCount, variantArticleCount, variantCount, missingLangArticleCount, missingWordArticleCount, reportWriter);
		} else if (StringUtils.contains(dataXmlFilePath, "vsl")) {
			handleEtymVsl(rootElement, totalArticleCount, skippedArticleCount, variantArticleCount, variantCount, missingLangArticleCount, missingWordArticleCount, reportWriter);
		}

		if (makeReport) {
			reportWriter.flush();
			reportStream.flush();
			reportWriter.close();
			reportStream.close();
		}

		logger.info("Total article count: {}", totalArticleCount.getValue());
		logger.info("Skipped article count: {}", skippedArticleCount.getValue());
		logger.info("Variant article count: {}", variantArticleCount.getValue());
		logger.info("Variant count: {}", variantCount.getValue());
		logger.info("Missing lang article count: {}", missingLangArticleCount.getValue());
		logger.info("Missing word article count: {}", missingWordArticleCount.getValue());
	}

	private void handleEtymSs1(
			Element rootElement,
			Count totalArticleCount,
			Count skippedArticleCount,
			Count variantArticleCount,
			Count variantCount,
			Count missingLangArticleCount,
			Count missingWordArticleCount,
			OutputStreamWriter reportWriter) throws Exception {

		Map<String, List<String>> langGroupLanguagesMap = new HashMap<>();
		Map<String, List<String>> languageLangGroupsMap = new HashMap<>();

		List<Element> articleElements = rootElement.elements();

		for (Element articleElement : articleElements) {

			totalArticleCount.increment();
			Node mNode = articleElement.selectSingleNode("s:m");
			String wordValue = mNode.getText();

			List<Node> etgNodes = articleElement.selectNodes("s:etp/s:etg");
			if (CollectionUtils.isEmpty(etgNodes)) {
				skippedArticleCount.increment();
				continue;
			}

			boolean articleHasVariants = false;
			boolean missingLang = false;
			boolean missingWord = false;

			for (Node etgNode : etgNodes) {

				List<Node> etggNodes = etgNode.selectNodes("s:etgg");

				if (CollectionUtils.isEmpty(etggNodes)) {
					continue;
				}

				// keelerühmad
				List<String> langGroups = extractTextValues(etgNode, "s:kr");

				for (Node etggNode : etggNodes) {

					// keeled
					List<String> languages = extractTextValues(etggNode, "s:k");

					if (CollectionUtils.isEmpty(languages)) {
						missingLang = true;
					}

					if (CollectionUtils.isNotEmpty(languages) && (CollectionUtils.size(langGroups) == 1)) {

						for (String langGroup : langGroups) {

							List<String> langGroupLanguages = langGroupLanguagesMap.get(langGroup);
							if (langGroupLanguages == null) {
								langGroupLanguages = new ArrayList<>();
								langGroupLanguagesMap.put(langGroup, langGroupLanguages);
							}

							for (String lang : languages) {

								if (!langGroupLanguages.contains(lang)) {
									langGroupLanguages.add(lang);
								}

								List<String> languageLangGroups = languageLangGroupsMap.get(lang);
								if (languageLangGroups == null) {
									languageLangGroups = new ArrayList<>();
									languageLangGroupsMap.put(lang, languageLangGroups);
								}

								if (!languageLangGroups.contains(langGroup)) {
									languageLangGroups.add(langGroup);
								}
							}
						}
					}

					// sõna ja variandid
					List<String> wordAndVariantValues = extractTextValues(etggNode, "s:ex");
					if (CollectionUtils.isEmpty(wordAndVariantValues)) {
						missingWord = true;
					} else {
						int currentVariantCount = wordAndVariantValues.size() - 1;
						variantCount.increment(currentVariantCount);
						if (currentVariantCount > 0) {
							articleHasVariants = true;
						}
					}
				}
			}

			if (articleHasVariants) {
				variantArticleCount.increment();
			}
			if (missingLang) {
				missingLangArticleCount.increment();
			}
			if (missingWord) {
				missingWordArticleCount.increment();
			}
		}

		if (makeReport) {
			composeReportGrl(langGroupLanguagesMap, reportWriter);
			composeReportLgr(languageLangGroupsMap, reportWriter);
		}
	}

	private void handleEtymVsl(
			Element rootElement,
			Count totalArticleCount,
			Count skippedArticleCount,
			Count variantArticleCount,
			Count variantCount,
			Count missingLangArticleCount,
			Count missingWordArticleCount,
			OutputStreamWriter reportWriter) throws Exception {

		Count lastElementMissingWordArticleCount = new Count();

		List<String> allLangAndGrValues = new ArrayList<>();

		List<Element> articleElements = rootElement.elements();

		for (Element articleElement : articleElements) {

			totalArticleCount.increment();
			Node mNode = articleElement.selectSingleNode("x:m");
			String wordValue = mNode.getText();

			List<Node> etgNodes = articleElement.selectNodes("x:etg");
			if (CollectionUtils.isEmpty(etgNodes)) {
				skippedArticleCount.increment();
				continue;
			}

			List<String> articleLangAndGrValues = new ArrayList<>();
			boolean articleHasVariants = false;
			boolean missingLang = false;
			boolean missingWord = false;
			boolean lastElementMissingWord = false;

			int etgNodeCount = etgNodes.size();
			int lastEtgNodeIndex = etgNodeCount - 1;
			for (int etgNodeIndex = 0; etgNodeIndex < etgNodeCount; etgNodeIndex++) {

				Node etgNode = etgNodes.get(etgNodeIndex);
				List<Node> etggNodes = etgNode.selectNodes("x:etgg");
				if (CollectionUtils.isEmpty(etggNodes)) {
					continue;
				}

				int etggNodeCount = etggNodes.size();
				int lastEtggNodeIndex = etggNodeCount - 1;
				for (int etggNodeIndex = 0; etggNodeIndex < etggNodeCount; etggNodeIndex++) {

					Node etggNode = etggNodes.get(etggNodeIndex);

					// keel ja grupp
					List<String> langAndGrValues = extractTextValues(etggNode, "x:keel");
					if (CollectionUtils.isEmpty(langAndGrValues)) {
						missingLang = true;
					} else {
						String langAndGrValuesStr = StringUtils.join(langAndGrValues, " | ");
						if (!articleLangAndGrValues.contains(langAndGrValuesStr)) {
							articleLangAndGrValues.add(langAndGrValuesStr);
						}
					}

					// sõna ja variandid
					List<String> wordAndVariantValues = extractTextValues(etggNode, "x:ex");
					if (CollectionUtils.isEmpty(wordAndVariantValues)) {
						missingWord = true;
						if ((etgNodeIndex == lastEtgNodeIndex) && (etggNodeIndex == lastEtggNodeIndex)) {
							lastElementMissingWord = true;
						}
					} else {
						int currentVariantCount = wordAndVariantValues.size() - 1;
						variantCount.increment(currentVariantCount);
						if (currentVariantCount > 0) {
							articleHasVariants = true;
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(articleLangAndGrValues)) {
				String groupLangAndGrValuesStr = StringUtils.join(articleLangAndGrValues, " > ");
				if (!allLangAndGrValues.contains(groupLangAndGrValuesStr)) {
					allLangAndGrValues.add(groupLangAndGrValuesStr);
				}
			}

			if (articleHasVariants) {
				variantArticleCount.increment();
			}
			if (missingLang) {
				missingLangArticleCount.increment();
			}
			if (missingWord) {
				missingWordArticleCount.increment();
			}
			if (lastElementMissingWord) {
				lastElementMissingWordArticleCount.increment();
			}
		}

		Collections.sort(allLangAndGrValues);

		if (makeReport) {

			for (String langAndGrValue : allLangAndGrValues) {
				String reportLine = langAndGrValue + "\n";
				reportWriter.write(reportLine);
			}
		}

		logger.info("Last element missing word article count: {}", lastElementMissingWordArticleCount.getValue());
	}

	private void composeReportLgr(
			Map<String, List<String>> languageLangGroupsMap,
			OutputStreamWriter reportWriter) throws Exception {

		if (StringUtils.equals(REPORT_TYPE_LANGUAGE_GROUP, reportType)) {

			List<String> languages = new ArrayList<>(languageLangGroupsMap.keySet());
			Collections.sort(languages);

			for (String lang : languages) {

				List<String> langGroups = languageLangGroupsMap.get(lang);
				Collections.sort(langGroups);

				for (String langGroup : langGroups) {

					String reportLine = lang + "\t" + langGroup + "\n";
					reportWriter.write(reportLine);
				}
			}
		}
	}

	private void composeReportGrl(
			Map<String, List<String>> langGroupLanguagesMap,
			OutputStreamWriter reportWriter) throws Exception {

		if (StringUtils.equals(REPORT_TYPE_GROUP_LANG, reportType)) {

			List<String> langGroups = new ArrayList<>(langGroupLanguagesMap.keySet());
			Collections.sort(langGroups);

			for (String langGroup : langGroups) {

				List<String> langGroupLanguages = langGroupLanguagesMap.get(langGroup);
				Collections.sort(langGroupLanguages);

				for (String lang : langGroupLanguages) {

					String reportLine = langGroup + "\t" + lang + "\n";
					reportWriter.write(reportLine);
				}
			}
		}
	}

	private List<String> extractTextValues(Node etgNode, String xPathValue) {
		return etgNode
				.selectNodes(xPathValue).stream()
				.map(Node::getText)
				.collect(Collectors.toList());
	}
}
