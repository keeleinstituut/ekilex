package eki.ekilex.manual;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.data.Count;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.service.XmlReader;

//TODO temporary for testing, remove later
public class MeaningMappingAnalyser extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(MeaningMappingAnalyser.class);

	public static void main(String[] args) {
		new MeaningMappingAnalyser().execute(args);
	}

	@Override
	void execute(String[] args) {

		try {
			initDefault();

			String ssMeaningMapPath = "/projects/eki/data/dictionaries/tahendus_xxx_selgroog.txt";
			String ssDataPath = "/projects/eki/data/dictionaries/ss11.tahendus.xml";
			String psDataPath = "/projects/eki/data/dictionaries/ps21.tahendus.xml";
			String kolDataPath = "/projects/eki/data/dictionaries/kol1.tahendus.xml";
			String ev1DataPath = "/projects/eki/data/dictionaries/ev21.tahendus.xml";
			String ev2DataPath = "/projects/eki/data/dictionaries/ev22.tahendus.xml";
			String qqDataPath = "/projects/eki/data/dictionaries/qq21.tahendus.xml";

			logger.debug("Starting...");

			// meaning map
			FileInputStream ssMeaningMapStream = new FileInputStream(ssMeaningMapPath);
			List<String> ssMeaningMapLines = IOUtils.readLines(ssMeaningMapStream, UTF_8);
			ssMeaningMapStream.close();

			Map<String, List<Guid>> ssMnrMap = new HashMap<>();
			Set<String> tpMnrs = new HashSet<>();
			Set<String> tgMnrs = new HashSet<>();

			for (String meaningMapLine : ssMeaningMapLines) {
				if (StringUtils.isBlank(meaningMapLine)) {
					continue;
				}
				String[] meaningMapParts = StringUtils.split(meaningMapLine, CSV_SEPARATOR);
				String sourceMnr = meaningMapParts[0];
				String targetMnr = meaningMapParts[1];
				String word = meaningMapParts[2];
				List<Guid> mappedMnrs = ssMnrMap.get(sourceMnr);
				if (mappedMnrs == null) {
					mappedMnrs = new ArrayList<>();
					ssMnrMap.put(sourceMnr, mappedMnrs);
				}
				Guid mnr = new Guid();
				mnr.setValue(targetMnr);
				mnr.setWord(word);
				if (!mappedMnrs.contains(mnr)) {
					mappedMnrs.add(mnr);
				}
			}

			XmlReader xmlReader = new XmlReader();

			Document dataDoc;
			Element rootElement;
			List<Node> articleNodes;
			int articleCount;
			long articleCounter;
			long progressIndicator;

			// ss
			dataDoc = xmlReader.readDocument(ssDataPath);
			rootElement = dataDoc.getRootElement();
			articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
			articleCount = articleNodes.size();

			logger.debug("ss article count {}", articleCount);

			articleCounter = 0;
			progressIndicator = articleCount / Math.min(articleCount, 100);

			for (Node articleNode : articleNodes) {

				List<Node> meaningGroupNodes = articleNode.selectNodes(".//s:tp");
				for (Node meaningGroupNode : meaningGroupNodes) {
					Element meaningGroupElem = (Element) meaningGroupNode;
					String meaningGrMeaningNr = meaningGroupElem.attributeValue("tahendusnr");
					tpMnrs.add(meaningGrMeaningNr);
					List<Node> meaningNrNodes = meaningGroupElem.selectNodes("s:tg/@s:tahendusnr");
					int meaningNrCount = meaningNrNodes.size();
					if (meaningNrCount > 0) {
						List<String> meaningNumbers = meaningNrNodes.stream().map(node -> node.getText().trim()).collect(Collectors.toList());
						tgMnrs.addAll(meaningNumbers);
					}
				}

				// progress
				articleCounter++;
				if (articleCounter % progressIndicator == 0) {
					long progressPercent = articleCounter / progressIndicator;
					logger.debug("{}% - {} articles; tpMnrs: {}; tgMnrs: {}", progressPercent, articleCounter, tpMnrs.size(), tgMnrs.size());
				}
			}

			logger.info("ssMnrMap size: {}", ssMnrMap.size());
			logger.info("ss tpMnrs size: {}", tpMnrs.size());
			logger.info("ss tgMnrs size: {}", tgMnrs.size());

			handling("qq", qqDataPath, ssMnrMap, tpMnrs, tgMnrs);

			handling("kol", kolDataPath, ssMnrMap, tpMnrs, tgMnrs);

			logger.debug("End");
		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

	private void handling(String dataset, String qqDataPath, Map<String, List<Guid>> ssMnrMap, Set<String> tpMnrs, Set<String> tgMnrs) throws Exception {

		XmlReader xmlReader = new XmlReader();

		Document dataDoc = xmlReader.readDocument(qqDataPath);
		Element rootElement = dataDoc.getRootElement();
		List<Node> articleNodes = rootElement.content().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
		int articleCount = articleNodes.size();

		logger.debug("qq article count {}", articleCount);

		Count meaningMappingExistsCount = new Count();
		Count meaningMappingMissingCount = new Count();
		Count meaningMappingSuccessCount = new Count();
		Count meaningMappingOneToOneCount = new Count();
		Count meaningMappingManyToOneCount = new Count();
		Count meaningMappingOneToManyCount = new Count();
		Count meaningMappingTpToTpCount = new Count();
		Count meaningMappingTpToTgCount = new Count();
		Count meaningDirectMatchCount = new Count();

		long articleCounter = 0;
		long progressIndicator = articleCount / Math.min(articleCount, 100);

		for (Node articleNode : articleNodes) {

			List<Node> meaningGroupNodes = articleNode.selectNodes(".//x:tp");
			for (Node meaningGroupNode : meaningGroupNodes) {
				Element meaningGroupElem = (Element) meaningGroupNode;
				String meaningGrMeaningNr = meaningGroupElem.attributeValue("tahendusnr");
				if (ssMnrMap.containsKey(meaningGrMeaningNr)) {
					meaningMappingExistsCount.increment();
					List<Guid> ssMnrObjs = ssMnrMap.get(meaningGrMeaningNr);
					List<String> mappedMnrs = ssMnrObjs.stream().map(Guid::getValue).collect(Collectors.toList());
					if (mappedMnrs.size() == 1) {
						meaningMappingOneToOneCount.increment();
					} else {
						for (String mappedMnr : mappedMnrs) {
							if (tpMnrs.contains(mappedMnr)) {
								meaningMappingTpToTpCount.increment();
							}
							if (tgMnrs.contains(mappedMnr)) {
								meaningMappingTpToTgCount.increment();
							}
						}
					}
				} else {
					meaningMappingMissingCount.increment();
				}
				List<Node> meaningNodes = meaningGroupElem.selectNodes("x:tg");
				if (meaningNodes.size() == 1) {
					if (ssMnrMap.containsKey(meaningGrMeaningNr)) {
						meaningMappingSuccessCount.increment();
					}
					
				} else {
					// actually, @ qq, kol there are no mnr-s @ tg
					/*
					List<String> meaningNrs = meaningNodes.stream().map(node -> ((Element) node).attributeValue("tahendusnr")).filter(mnr -> StringUtils.isNotBlank(mnr)).collect(Collectors.toList());
					for (String meaningNr : meaningNrs) {
						if (ssMnrMap.containsKey(meaningNr)) {
							meaningMappingExistsCount.increment();
							meaningMappingSuccessCount.increment();
						} else {
							meaningMappingMissingCount.increment();
							if (tpMnrs.contains(meaningNr)) {
								meaningDirectMatchCount.increment();
							}
							if (tgMnrs.contains(meaningNr)) {
								meaningDirectMatchCount.increment();
							}
						}
					}
					*/
				}
			}

			// progress
			articleCounter++;
			if (articleCounter % progressIndicator == 0) {
				long progressPercent = articleCounter / progressIndicator;
				logger.debug("{}% - {} articles iterated", progressPercent, articleCounter);
			}
		}
		
		logger.info("{} meaningMappingExistsCount: {}", dataset, meaningMappingExistsCount.toString());
		logger.info("{} meaningMappingMissingCount: {}", dataset, meaningMappingMissingCount.toString());
		logger.info("{} meaningMappingSuccessCount: {}", dataset, meaningMappingSuccessCount.toString());
		logger.info("{} meaningMappingOneToOneCount: {}", dataset, meaningMappingOneToOneCount.toString());
		//logger.info("{} meaningMappingManyToOneCount: {}", dataset, meaningMappingManyToOneCount.toString());
		//logger.info("{} meaningMappingOneToManyCount: {}", dataset, meaningMappingOneToManyCount.toString());
		logger.info("{} meaningMappingTpToTpCount: {}", dataset, meaningMappingTpToTpCount.toString());
		logger.info("{} meaningMappingTpToTgCount: {}", dataset, meaningMappingTpToTgCount.toString());
		logger.info("{} meaningDirectMatchCount: {}", dataset, meaningDirectMatchCount.toString());
	}

}
