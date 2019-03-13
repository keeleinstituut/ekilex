package eki.ekilex.runner;

import eki.ekilex.service.ReportComposer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MilitermLoaderRunner extends AbstractLoaderRunner {

	private static Logger logger = LoggerFactory.getLogger(MilitermLoaderRunner.class);

	private ReportComposer reportComposer;

	@Override
	public String getDataset() {
		return null; // TODO
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	public void initialise() throws Exception {
		// TODO
	}

	@Transactional
	public void execute(String milFilePath, boolean doReports) throws Exception {

		this.doReports = doReports;
		if (doReports) {
			reportComposer = new ReportComposer(getDataset() + " loader", "TODO"); // TODO
		}
		start();

		Document dataDoc = xmlReader.readDocument(milFilePath);

		Element rootElement = dataDoc.getRootElement();
		// TODO continue
		end();
	}
}
