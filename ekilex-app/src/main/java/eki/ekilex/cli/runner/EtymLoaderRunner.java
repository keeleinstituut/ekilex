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

	private boolean makeReport = true;

	//@Transactional(rollbackFor = Exception.class)
	public void execute(String dataXmlFilePath) throws Exception {

		logger.info("Starting loading...");

		Count totalArticleCount = new Count();
		Count skippedArticleCount = new Count();

		FileOutputStream reportStream = null;
		OutputStreamWriter reportWriter = null;
		if (makeReport) {
			reportStream = new FileOutputStream(reportFileName);
			reportWriter = new OutputStreamWriter(reportStream, StandardCharsets.UTF_8);
		}

		Document dataDoc = readDocument(dataXmlFilePath);
		Element rootElement = dataDoc.getRootElement();

		if (StringUtils.contains(dataXmlFilePath, "ss1")) {
			handleEtymSs1(rootElement, totalArticleCount, skippedArticleCount, reportWriter);
		} else if (StringUtils.contains(dataXmlFilePath, "vsl")) {
			handleEtymVsl(rootElement, totalArticleCount, skippedArticleCount, reportWriter);
		}

		if (makeReport) {
			reportWriter.flush();
			reportStream.flush();
			reportWriter.close();
			reportStream.close();
		}

		logger.info("Total article count: {}", totalArticleCount.getValue());
		logger.info("Skipped article count: {}", skippedArticleCount.getValue());
	}

	private void handleEtymSs1(
			Element rootElement,
			Count totalArticleCount,
			Count skippedArticleCount,
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

			for (Node etgNode : etgNodes) {

				// keelerühmad
				List<String> langGroups = extractTextValues(etgNode, "s:kr");

				// keeled
				List<String> languages = extractTextValues(etgNode, "s:etgg/s:k");

				if (CollectionUtils.isEmpty(langGroups)) {
					continue;
				}
				if (CollectionUtils.isEmpty(languages)) {
					continue;
				}
				if (langGroups.size() > 1) {
					continue;
				}

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
			OutputStreamWriter reportWriter) throws Exception {

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

			for (Node etgNode : etgNodes) {

				List<Node> etggNodes = etgNode.selectNodes("x:etgg");
				if (CollectionUtils.isEmpty(etggNodes)) {
					continue;
				}

				for (Node etggNode : etggNodes) {
					List<String> langAndGrValues = extractTextValues(etggNode, "x:keel");
					if (CollectionUtils.isNotEmpty(langAndGrValues)) {
						String langAndGrValuesStr = StringUtils.join(langAndGrValues, " | ");
						if (!articleLangAndGrValues.contains(langAndGrValuesStr)) {
							articleLangAndGrValues.add(langAndGrValuesStr);
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
		}

		Collections.sort(allLangAndGrValues);

		if (makeReport) {

			for (String langAndGrValue : allLangAndGrValues) {
				String reportLine = langAndGrValue + "\n";
				reportWriter.write(reportLine);
			}
		}
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
