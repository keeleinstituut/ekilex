package eki.ekilex.manual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eki.ekilex.runner.VoiceFileUpdaterRunner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.Guid;
import eki.ekilex.runner.CollocLoaderRunner;
import eki.ekilex.runner.DbReInitialiserRunner;
import eki.ekilex.runner.EstermLoaderRunner;
import eki.ekilex.runner.EstermSourceLoaderRunner;
import eki.ekilex.runner.EtymologyLoaderRunner;
import eki.ekilex.runner.Ev2LoaderRunner;
import eki.ekilex.runner.GameDataLoaderRunner;
import eki.ekilex.runner.PsvLoaderRunner;
import eki.ekilex.runner.Qq2LoaderRunner;
import eki.ekilex.runner.Ss1LoaderRunner;
import eki.ekilex.runner.TermekiRunner;
import eki.ekilex.service.MabService;

public class UltimaLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(UltimaLoader.class);

	public static void main(String[] args) {
		new UltimaLoader().execute();
	}

	@Override
	void execute() {

		List<String> successfullyLoadedDatasets = new ArrayList<>();

		try {
			initWithTermeki();

			DbReInitialiserRunner initRunner = getComponent(DbReInitialiserRunner.class);
			MabService mabService = getComponent(MabService.class);
			Ss1LoaderRunner ss1Runner = getComponent(Ss1LoaderRunner.class);
			PsvLoaderRunner psvRunner = getComponent(PsvLoaderRunner.class);
			CollocLoaderRunner kolRunner = getComponent(CollocLoaderRunner.class);
			Qq2LoaderRunner qq2Runner = getComponent(Qq2LoaderRunner.class);
			Ev2LoaderRunner ev2Runner = getComponent(Ev2LoaderRunner.class);
			EtymologyLoaderRunner etyRunner = getComponent(EtymologyLoaderRunner.class);
			EstermSourceLoaderRunner estSrcRunner = getComponent(EstermSourceLoaderRunner.class);
			EstermLoaderRunner estRunner = getComponent(EstermLoaderRunner.class);
			TermekiRunner termekiRunner = getComponent(TermekiRunner.class);
			VoiceFileUpdaterRunner voiceFileUpdaterRunner = getComponent(VoiceFileUpdaterRunner.class);
			GameDataLoaderRunner gameDataLoaderRunner = getComponent(GameDataLoaderRunner.class);

			String dataFilePath, dataFilePath2, dataset;
			Map<String, List<Guid>> ssGuidMap;

			boolean doReports = doReports();

			logger.info("Starting to clear database and load all datasets specified in ultima-loader.properties file");

			long t1, t2;
			t1 = System.currentTimeMillis();

			// db init
			initRunner.execute();

			// mab
			dataFilePath = getConfProperty("mab.data.file.1");
			dataFilePath2 = getConfProperty("mab.data.file.2");
			if (StringUtils.isNotBlank(dataFilePath) && StringUtils.isNotBlank(dataFilePath2)) {
				mabService.loadParadigms(dataFilePath, dataFilePath2, doReports);
				successfullyLoadedDatasets.add("mab");
			}

			// ss1
			dataFilePath = getConfProperty("ss1.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				ss1Runner.execute(dataFilePath, doReports);
				successfullyLoadedDatasets.add("ss1");
			}

			// psv
			dataFilePath = getConfProperty("psv.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				dataset = psvRunner.getDataset();
				ssGuidMap = getSsGuidMapFor(dataset);
				psvRunner.execute(dataFilePath, ssGuidMap, doReports);
				successfullyLoadedDatasets.add("psv");
			}

			// kol
			dataFilePath = getConfProperty("kol.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				dataset = kolRunner.getDataset();
				ssGuidMap = getSsGuidMapFor(dataset);
				kolRunner.execute(dataFilePath, ssGuidMap, doReports);
				successfullyLoadedDatasets.add("kol");
			}

			// qq2
			dataFilePath = getConfProperty("qq2.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				dataset = qq2Runner.getDataset();
				ssGuidMap = getSsGuidMapFor(dataset);
				qq2Runner.execute(dataFilePath, ssGuidMap, doReports);
				successfullyLoadedDatasets.add(dataset);
			}

			// ev2
			dataFilePath = getConfProperty("ev2.data.file.1");
			dataFilePath2 = getConfProperty("ev2.data.file.2");
			if (StringUtils.isNotBlank(dataFilePath) && StringUtils.isNotBlank(dataFilePath2)) {
				dataset = ev2Runner.getDataset();
				ssGuidMap = getSsGuidMapFor(dataset);
				ev2Runner.execute(dataFilePath, dataFilePath2, ssGuidMap, doReports);
			}

			// ety
			dataFilePath = getConfProperty("ss1.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				etyRunner.execute(dataFilePath, doReports);
				successfullyLoadedDatasets.add("ety");
			}

			// est src + est
			dataFilePath = getConfProperty("est.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				estSrcRunner.execute(dataFilePath, doReports);
				successfullyLoadedDatasets.add("est src");
				estRunner.execute(dataFilePath, doReports);
				successfullyLoadedDatasets.add("est");
			}

			// termeki
			dataFilePath = getConfProperty("termeki.data.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				termekiRunner.batchLoad(dataFilePath);
				successfullyLoadedDatasets.add("termeki");
			}

			// sound file names updater
			dataFilePath = getConfProperty("voice.index.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				voiceFileUpdaterRunner.update(dataFilePath);
				successfullyLoadedDatasets.add("voice");
			}

			// game data
			dataFilePath = getConfProperty("games.nonwords.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				gameDataLoaderRunner.execute(dataFilePath);
			}

			t2 = System.currentTimeMillis();
			float fullLoadTimeMin = ((float)(t2 - t1)) / 60000f;
			logger.info("----DONE LOADING DATASETS!!----");
			logger.info("Full load took {} min", fullLoadTimeMin);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
			logger.info("Successfully loaded datasets: {}", successfullyLoadedDatasets);
		} finally {
			shutdown();
		}
	}

}
