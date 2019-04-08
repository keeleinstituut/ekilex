package eki.ekilex.runner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eki.common.constant.ContentKey;
import eki.common.constant.ReferenceType;
import eki.common.data.AbstractDataObject;
import eki.common.data.Count;
import eki.common.exception.DataLoadingException;
import eki.ekilex.constant.TermLoaderConstant;
import eki.ekilex.data.transform.Meaning;
import eki.ekilex.runner.util.TermLoaderHelper;
import eki.ekilex.service.ReportComposer;

public abstract class AbstractTermLoaderRunner extends AbstractLoaderRunner implements TermLoaderConstant {

	private static Logger logger = LoggerFactory.getLogger(AbstractTermLoaderRunner.class);

	@Autowired
	private TermLoaderHelper loaderHelper;

	ReportComposer reportComposer;

	Count illegalSourceReferenceValueCount;

	String sourceFileName;

	void extractAndApplyMeaningProperties(Node conceptGroupNode, Meaning meaningObj, DateFormat dateFormat) throws Exception {

		Element valueNode;
		String valueStr;
		long valueLong;
		Timestamp valueTs;

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setCreatedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(createdOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = dateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setCreatedOn(valueTs);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedByExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			meaningObj.setModifiedBy(valueStr);
		}

		valueNode = (Element) conceptGroupNode.selectSingleNode(modifiedOnExp);
		if (valueNode != null) {
			valueStr = valueNode.getTextTrim();
			valueLong = dateFormat.parse(valueStr).getTime();
			valueTs = new Timestamp(valueLong);
			meaningObj.setModifiedOn(valueTs);
		}
	}

	List<Content> extractContentAndRefs(Node rootContentNode, String lang, String term, boolean logWarrnings) throws Exception {

		List<Content> contentList = new ArrayList<>();
		Iterator<Node> contentNodeIter = ((Element) rootContentNode).nodeIterator();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;
		Content contentObj = null;
		Ref refObj = null;
		boolean isRefOn = false;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				valueStr = StringUtils.replaceChars(valueStr, '\n', ' ');
				valueStr = StringUtils.trim(valueStr);
				boolean isListing = loaderHelper.isListing(valueStr);
				boolean isRefEnd = loaderHelper.isRefEnd(valueStr);
				boolean isValued = StringUtils.isNotEmpty(valueStr);
				String content = loaderHelper.getContent(valueStr);
				boolean contentExists = StringUtils.isNotBlank(content);
				if (isListing) {
					continue;
				}
				if (!isRefOn && isRefEnd && logWarrnings) {
					logger.warn("Illegal ref end notation @ \"{}\" : {}", term, rootContentNode.asXML());
				}
				if (isRefOn && isValued) {
					String minorRef;
					if (isRefEnd) {
						minorRef = loaderHelper.collectMinorRef(valueStr);
					} else {
						minorRef = loaderHelper.cleanupResidue(valueStr);
					}
					if (StringUtils.isNotBlank(minorRef)) {
						refObj.setMinorRef(minorRef);
					}
				}
				if (contentExists) {
					if (contentObj == null) {
						contentObj = newContent(lang, content);
						contentList.add(contentObj);
					} else if (!isRefOn) {
						content = contentObj.getValue() + '\n' + content;
						contentObj.setValue(content);
					} else {
						contentObj = newContent(lang, content);
						contentList.add(contentObj);
					}
				}
				isRefOn = false;
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						String sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						if (contentObj == null) {
							contentObj = newContent(lang, "-");
							contentList.add(contentObj);
							if (logWarrnings) {
								logger.warn("Source reference for empty content @ \"{}\"-\"{}\"", term, sourceName);
							}
						}
						isRefOn = true;
						ReferenceType refType;
						if (StringUtils.equalsIgnoreCase(refTypeExpert, sourceName)) {
							refType = ReferenceType.EXPERT;
						} else if (StringUtils.equalsIgnoreCase(refTypeQuery, sourceName)) {
							refType = ReferenceType.QUERY;
						} else {
							refType = ReferenceType.ANY;
						}
						refObj = new Ref();
						refObj.setMajorRef(sourceName);
						refObj.setType(refType);
						contentObj.getRefs().add(refObj);
					} else {
						appendToReport(doReports, REPORT_ILLEGAL_SOURCE_REF, term, "Allikaviitel on sobimatu väärtus: " + tlinkAttrValue);
						illegalSourceReferenceValueCount.increment();
					}
				}
			}
		}
		return contentList;
	}

	//TODO should be replaced by separate ref links handling later
	@Deprecated
	String handleFreeformRefLinks(Node mixedContentNode, Long ownerId) throws Exception {

		Iterator<Node> contentNodeIter = ((Element) mixedContentNode).nodeIterator();
		StringBuffer contentBuf = new StringBuffer();
		DefaultText textContentNode;
		DefaultElement elemContentNode;
		String valueStr;

		while (contentNodeIter.hasNext()) {
			Node contentNode = contentNodeIter.next();
			if (contentNode instanceof DefaultText) {
				textContentNode = (DefaultText) contentNode;
				valueStr = textContentNode.getText();
				contentBuf.append(valueStr);
			} else if (contentNode instanceof DefaultElement) {
				elemContentNode = (DefaultElement) contentNode;
				valueStr = elemContentNode.getTextTrim();
				if (StringUtils.equalsIgnoreCase(xrefExp, elemContentNode.getName())) {
					String tlinkAttrValue = elemContentNode.attributeValue(xrefTlinkAttr);
					if (StringUtils.startsWith(tlinkAttrValue, xrefTlinkSourcePrefix)) {
						String sourceName = StringUtils.substringAfter(tlinkAttrValue, xrefTlinkSourcePrefix);
						Long sourceId = getSource(sourceName);
						if (sourceId == null) {
							contentBuf.append(valueStr);
						} else {
							Long refLinkId = createFreeformSourceLink(ownerId, ReferenceType.ANY, sourceId, null, valueStr);
							//simulating markdown link syntax
							contentBuf.append("[");
							contentBuf.append(valueStr);
							contentBuf.append("]");
							contentBuf.append("(");
							contentBuf.append(ContentKey.FREEFORM_SOURCE_LINK);
							contentBuf.append(":");
							contentBuf.append(refLinkId);
							contentBuf.append(")");
						}
					} else {
						// unknown ref type
						contentBuf.append(valueStr);
					}
				} else {
					throw new DataLoadingException("Unsupported mixed content node name: " + contentNode.getName());
				}
			} else {
				throw new DataLoadingException("Unsupported mixed content node type: " + contentNode.getClass());
			}
		}
		valueStr = contentBuf.toString();
		return valueStr;
	}

	boolean isLanguageTypeConcept(Node conceptGroupNode) {

		String valueStr;
		List<Node> valueNodes = conceptGroupNode.selectNodes(langGroupExp + "/" + langExp);
		for (Node langNode : valueNodes) {
			valueStr = ((Element) langNode).attributeValue(langTypeAttr);
			boolean isLang = isLang(valueStr);
			if (isLang) {
				continue;
			}
			return false;
		}
		return true;
	}

	boolean domainExists(String domainCode, String domainOrigin) throws Exception {

		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("origin", domainOrigin);
		tableRowParamMap.put("code", domainCode);
		Map<String, Object> tableRowValueMap = basicDbService.queryForMap(SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN, tableRowParamMap);
		boolean domainExists = ((Long) tableRowValueMap.get("cnt")) > 0;
		return domainExists;
	}

	Long getSource(String sourceName) {

		if (sourceFileName == null) {
			throw new RuntimeException("sourceFileName is not initialized");
		}
		Map<String, Object> tableRowParamMap = new HashMap<>();
		tableRowParamMap.put("sourceName", sourceName);
		tableRowParamMap.put("sourceFileName", sourceFileName);
		List<Map<String, Object>> results = basicDbService.queryList(SQL_SELECT_SOURCE_BY_NAME_AND_FILE_NAME, tableRowParamMap);
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		if (results.size() > 1) {
			logger.warn("Several sources match the \"{}\"", sourceName);
		}
		Map<String, Object> result = results.get(0);
		Long sourceId = Long.valueOf(result.get("source_id").toString());
		return sourceId;
	}

	private Content newContent(String lang, String content) {

		Content contentObj;
		contentObj = new Content();
		contentObj.setValue(content);
		contentObj.setLang(lang);
		contentObj.setRefs(new ArrayList<>());
		return contentObj;
	}

	void appendToReport(boolean doReports, String reportName, String... reportCells) throws Exception {

		if (!doReports) {
			return;
		}
		String logRow = StringUtils.join(reportCells, CSV_SEPARATOR);
		reportComposer.append(reportName, logRow);
	}

	class Content extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private Long id;

		private String value;

		private String lang;

		private List<Ref> refs;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public List<Ref> getRefs() {
			return refs;
		}

		public void setRefs(List<Ref> refs) {
			this.refs = refs;
		}
	}

	class Ref extends AbstractDataObject {

		private static final long serialVersionUID = 1L;

		private String minorRef;

		private String majorRef;

		private ReferenceType type;

		public String getMinorRef() {
			return minorRef;
		}

		public void setMinorRef(String minorRef) {
			this.minorRef = minorRef;
		}

		public String getMajorRef() {
			return majorRef;
		}

		public void setMajorRef(String majorRef) {
			this.majorRef = majorRef;
		}

		public ReferenceType getType() {
			return type;
		}

		public void setType(ReferenceType type) {
			this.type = type;
		}
	}

	enum SourceOwner {
		LEXEME, DEFINITION, USAGE, PUBLIC_NOTE
	}
}
