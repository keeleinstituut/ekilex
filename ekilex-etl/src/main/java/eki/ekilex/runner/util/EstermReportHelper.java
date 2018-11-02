package eki.ekilex.runner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.common.data.Count;
import eki.common.service.db.BasicDbService;
import eki.ekilex.constant.EstermLoaderConstant;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.service.ReportComposer;

// everything here is on standby until actually requested by Esterm team
@Component
public class EstermReportHelper implements EstermLoaderConstant, SystemConstant {

	@Autowired
	protected BasicDbService basicDbService;

	private ReportComposer reportComposer;

	private Map<String, String> processStateCodes;

	private Map<String, String> valueStateCodes;

	public void setup(
			ReportComposer reportComposer,
			Map<String, String> processStateCodes,
			Map<String, String> valueStateCodes) {
		this.reportComposer = reportComposer;
		this.processStateCodes = processStateCodes;
		this.valueStateCodes = valueStateCodes;
	}

	public void detectAndReportConceptGrp(String concept, Node conceptGroupNode, Count dataErrorCount) throws Exception {

		if (true) {
			//TODO until further notice...
			return;
		}

		Map<String, Object> tableRowParamMap;
		Map<String, Object> tableRowValueMap;
		List<Node> valueNodes;
		Element valueNode;
		List<String> values;
		String valueStr;

		valueNode = (Element) conceptGroupNode.selectSingleNode(processStateExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			if (!processStateCodes.containsKey(valueStr)) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "tundmatu staatus: " + valueStr);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(domainExp);
		values = new ArrayList<>();
		for (Node domainNode : valueNodes) {
			valueStr = ((Element)domainNode).getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "korduv valdkonnaviide: " + valueStr);
				continue;
			}
			values.add(valueStr);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("origin", originLenoch);
			tableRowParamMap.put("code", valueStr);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			boolean exists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (!exists) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "tundmatu valdkonnaviide: " + valueStr);
			}
		}

		valueNodes = conceptGroupNode.selectNodes(subdomainExp);
		values = new ArrayList<>();
		for (Node domainNode : valueNodes) {
			valueStr = ((Element)domainNode).getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "korduv alamvaldkonnaviide: " + valueStr);
				continue;
			}
			values.add(valueStr);
			tableRowParamMap = new HashMap<>();
			tableRowParamMap.put("origin", originLtb);
			tableRowParamMap.put("code", valueStr);
			tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
			boolean exists = ((Long) tableRowValueMap.get("cnt")) > 0;
			if (!exists) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_ILLEGAL_CLASSIFIERS, concept, "tundmatu alamvaldkonnaviide: " + valueStr);
			}
		}

		values = new ArrayList<>();
		List<Node> createdByNodes = conceptGroupNode.selectNodes(ltbCreatedByExp);
		List<Node> createdOnNodes = conceptGroupNode.selectNodes(ltbEõkkCreatedOnExp);
		int createdByCount = createdByNodes.size();
		int createdOnCount = createdOnNodes.size();
		boolean isTooManyCreates = createdByCount > 1 && createdOnCount > 1;
		boolean isIncompleteCreate = (createdByCount == 1 || createdOnCount == 1) && (createdByCount != createdOnCount);
		if (isTooManyCreates || isIncompleteCreate) {
			for (Node createdNode : createdByNodes) {
				valueStr = ((Element)createdNode).getTextTrim();
				values.add(valueStr);
			}
			for (Node createdNode : createdOnNodes) {
				valueStr = ((Element)createdNode).getTextTrim();
				values.add(valueStr);
			}
			if (isTooManyCreates) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_CREATED_MODIFIED_MESS, concept, "sisestajaid ja sisestusaegu on liiga palju: " + values);
			}
			if (isIncompleteCreate) {
				dataErrorCount.increment();
				appendToReport(true, REPORT_CREATED_MODIFIED_MESS, concept, "puudulik sisestaja ja sisestusaja komplekt: " + values);
			}
		}
	}

	public void detectAndReportLanguageGrp(String concept, Node langGroupNode, Count dataErrorCount) throws Exception {

		if (true) {
			//TODO until further notice...
			return;
		}

		StringBuffer logBuf = new StringBuffer();

		List<Node> definitionNodes = langGroupNode.selectNodes(definitionExp);
		List<Node> definitionNoteNodes = langGroupNode.selectNodes(noteExp);
		int definitionCount = definitionNodes.size();
		int definitionNoteCount = definitionNoteNodes.size();
		if (definitionCount > 1 && definitionNoteCount > 0) {
			dataErrorCount.increment();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("definitsioonid (");
			logBuf.append(definitionCount);
			logBuf.append(") ja nende märkused (");
			logBuf.append(definitionNoteCount);
			logBuf.append(") ei kohaldu");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_DEFINITIONS_NOTES_MESS, logRow);
		}

		composeDefinitionsAndNotesReports(concept, "*", langGroupNode);
	}

	public void detectAndReportTermGrp(String concept, String term, int homonymNr, String lang, Node termGroupNode, Count dataErrorCount) throws Exception {

		if (true) {
			//TODO until further notice...
			return;
		}

		StringBuffer logBuf = new StringBuffer();

		List<Node> valueNodes;
		List<String> values;
		String valueStr;

		valueNodes = termGroupNode.selectNodes(valueStateExp);
		values = new ArrayList<>();
		for (Node lexemeTypeNode : valueNodes) {
			valueStr = ((Element)lexemeTypeNode).getTextTrim();
			if (values.contains(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(term);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("korduv keelenditüüp: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
				continue;
			}
			values.add(valueStr);
			if (!valueStateCodes.containsKey(valueStr)) {
				dataErrorCount.increment();
				logBuf = new StringBuffer();
				logBuf.append(concept);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append(term);
				logBuf.append(CSV_SEPARATOR);
				logBuf.append("tundmatu väärtuse olek: ");
				logBuf.append(valueStr);
				String logRow = logBuf.toString();
				reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
			}
		}
		if (valueNodes.size() > 1) {
			dataErrorCount.increment();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append(term);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("mitu keelenditüüpi");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_ILLEGAL_CLASSIFIERS, logRow);
		}

		valueNodes = termGroupNode.selectNodes(definitionExp);
		if (CollectionUtils.isNotEmpty(valueNodes)) {
			dataErrorCount.increment();
			valueStr = ((Element)valueNodes.get(0)).getTextTrim();
			logBuf = new StringBuffer();
			logBuf.append(concept);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append(term);
			logBuf.append(CSV_SEPARATOR);
			logBuf.append("definitsioonid on seotud termini juurde (");
			logBuf.append(valueStr);
			logBuf.append("...)");
			String logRow = logBuf.toString();
			reportComposer.append(REPORT_DEFINITIONS_AT_TERMS, logRow);
		}

		composeDefinitionsAndNotesReports(concept, term, termGroupNode);
	}

	private void composeDefinitionsAndNotesReports(String concept, String term, Node sourceNode) throws Exception {

		String valueStr;
		List<Node> definitionNodes = sourceNode.selectNodes(definitionExp);

		if (CollectionUtils.isNotEmpty(definitionNodes)) {
			List<Node> notesSourceNodes = sourceNode.selectNodes("descripGrp/descrip[@type='Märkus']/xref[contains(@Tlink,'Allikas')]");

			List<String> definitionSourceNames = new ArrayList<>();

			for (Node definitionNode : definitionNodes) {

				Iterator<Node> contentNodeIter = ((Element)definitionNode).nodeIterator();
				DefaultText textContentNode;
				DefaultElement elemContentNode;
				String recentDefinition = null;
				boolean isMultipleDefinitions = false;
	
				while (contentNodeIter.hasNext()) {
					Node contentNode = contentNodeIter.next();
					if (contentNode instanceof DefaultText) {
						textContentNode = (DefaultText) contentNode;
						valueStr = textContentNode.getText();
						valueStr = StringUtils.trim(valueStr);
						if (StringUtils.equalsAny(valueStr, "[", "]", ";")) {
							continue;
						}
						boolean containsWord = containsWord(valueStr);
						if (containsWord) {
							if (StringUtils.isNotBlank(recentDefinition) && !isMultipleDefinitions) {
								isMultipleDefinitions = true;
								appendToReport(true, REPORT_MULTIPLE_DEFINITIONS, concept, term, definitionNode.asXML());
							}
							recentDefinition = valueStr;
						} else {
							appendToReport(true, REPORT_NOT_A_DEFINITION, concept, term, definitionNode.asXML());
						}
					} else if (contentNode instanceof DefaultElement) {
						elemContentNode = (DefaultElement) contentNode;
						valueStr = elemContentNode.getTextTrim();
						if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
							String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
							if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
								String sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
								definitionSourceNames.add(sourceName);
							}
						}
					}
				}
			}

			for (Node noteSourceNode : notesSourceNodes) {
				String tlinkAttrValue = ((Element)noteSourceNode).attributeValue(xrefTlinkAttr);
				String sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
				if (definitionSourceNames.contains(sourceName)) {
					int sourceCodeOccCount = Collections.frequency(definitionSourceNames, sourceName);
					if (sourceCodeOccCount > 1) {
						appendToReport(true, REPORT_DEFINITIONS_NOTES_MISMATCH, concept, term, sourceName, "mitu selle märkuse allikaviitega seletust");
					}
				} else {
					appendToReport(true, REPORT_DEFINITIONS_NOTES_MISMATCH, concept, term, sourceName, "pole selle märkuse allikaviitega seletust");
				}
			}
		}
	}

	private boolean containsWord(String value) {

		if (StringUtils.length(value) == 1) {
			return false;
		}
		char[] chars = value.toCharArray();
		for (char ch : chars) {
			if (Character.isAlphabetic(ch)) {
				return true;
			}
		}
		return false;
	}

	public void appendToReport(boolean doReports, String reportName, String ... reportCells) throws Exception {
		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}
}
