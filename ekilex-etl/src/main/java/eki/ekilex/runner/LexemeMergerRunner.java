package eki.ekilex.runner;

import java.util.List;

import javax.transaction.Transactional;

public class LexemeMergerRunner extends AbstractLoaderRunner {

	private String lexemeMergeName;

	@Override
	public String getDataset() {
		return lexemeMergeName;
	}

	@Transactional
	@Override
	public void deleteDatasetData() throws Exception {
		deleteDatasetData(getDataset());
	}

	@Override
	public void initialise() throws Exception {
		
	}

	@Transactional
	public void execute(String lexemeMergeName, List<String> lexemeMergeDatasets) throws Exception {
		this.lexemeMergeName = lexemeMergeName;
		start();

		

		end();
	}

}
