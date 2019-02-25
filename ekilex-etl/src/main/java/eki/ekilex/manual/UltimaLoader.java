package eki.ekilex.manual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.data.transform.DatasetId;
import eki.ekilex.data.transform.Guid;
import eki.ekilex.data.transform.Mnr;
import eki.ekilex.runner.CollocLoaderRunner;
import eki.ekilex.runner.DbReInitialiserRunner;
import eki.ekilex.runner.EstermLoaderRunner;
import eki.ekilex.runner.EstermSourceLoaderRunner;
import eki.ekilex.runner.EtymologyLoaderRunner;
import eki.ekilex.runner.Ev2LoaderRunner;
import eki.ekilex.runner.GameDataLoaderRunner;
import eki.ekilex.runner.MabLoaderRunner;
import eki.ekilex.runner.PsvLoaderRunner;
import eki.ekilex.runner.Qq2LoaderRunner;
import eki.ekilex.runner.Ss1LoaderRunner;
import eki.ekilex.runner.TermekiRunner;
import eki.ekilex.runner.VoiceFileUpdaterRunner;
import eki.ekilex.service.MabService;

public class UltimaLoader extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(UltimaLoader.class);

	//maven: mvn exec:java -P<profile> -Dexec.args="<dataset1> <dataset2> <dataset3>"
	public static void main(String[] args) {
		new UltimaLoader().execute(args);
	}

	@Override
	void execute(String[] args) {

		String[] acquiredDatasets = args;
		String aquiredDatasetsLog;
		if (ArrayUtils.isEmpty(acquiredDatasets)) {
			aquiredDatasetsLog = "*";
		} else {
			aquiredDatasetsLog = StringUtils.join(acquiredDatasets, ", ");
		}

		List<String> successfullyLoadedDatasets = new ArrayList<>();

		try {
			initWithTermeki();

			DbReInitialiserRunner initRunner = getComponent(DbReInitialiserRunner.class);
			MabService mabService = getComponent(MabService.class);
			MabLoaderRunner mabRunner = getComponent(MabLoaderRunner.class);
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
			Map<String, List<Mnr>> ssMnrMap;

			boolean doReports = doReports();
			boolean isFullReload = isFullReload();

			logger.info("Starting loading datasets from sources specified in ultima-loader.properties file");
			logger.info("Acquired datasets are: \"{}\"", aquiredDatasetsLog);

			long t1, t2;
			t1 = System.currentTimeMillis();

			// db init
			if (isFullReload) {
				initRunner.execute();
			}

			// mab
			if (isFullReload) {
				String[] mabDataFilePaths = getMabDataFilePaths();
				if (ArrayUtils.isNotEmpty(mabDataFilePaths)) {
					mabRunner.execute(mabDataFilePaths, doReports);
					successfullyLoadedDatasets.add("mab");
				}
			}
			mabService.initialise();

			// ss1 - only when full reload
			dataset = ss1Runner.getDataset();
			if (isFullReload) {
				dataFilePath = getConfProperty("ss1.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					ss1Runner.execute(dataFilePath, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			ssMnrMap = getSsMnrMap();

			// psv
			dataset = psvRunner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("psv.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					ssGuidMap = getSsGuidMapFor(dataset);
					if (!isFullReload) {
						psvRunner.deleteDatasetData();
					}
					psvRunner.execute(dataFilePath, ssGuidMap, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// kol
			dataset = kolRunner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("kol.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					ssGuidMap = getSsGuidMapFor(dataset);
					if (!isFullReload) {
						kolRunner.deleteDatasetData();
					}
					kolRunner.execute(dataFilePath, ssGuidMap, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// qq2
			dataset = qq2Runner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("qq2.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					ssGuidMap = getSsGuidMapFor(dataset);
					if (!isFullReload) {
						qq2Runner.deleteDatasetData();
					}
					qq2Runner.execute(dataFilePath, ssGuidMap, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// ev2
			dataset = ev2Runner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("ev2.data.file.1");
				dataFilePath2 = getConfProperty("ev2.data.file.2");
				if (StringUtils.isNotBlank(dataFilePath) && StringUtils.isNotBlank(dataFilePath2)) {
					ssGuidMap = getSsGuidMapFor(dataset);
					if (!isFullReload) {
						ev2Runner.deleteDatasetData();
					}
					ev2Runner.execute(dataFilePath, dataFilePath2, ssGuidMap, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// ety
			dataset = etyRunner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("ss1.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					if (!isFullReload) {
						etyRunner.deleteDatasetData();
					}
					etyRunner.execute(dataFilePath, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// est src + est
			dataset = estRunner.getDataset();
			if (doLoad(dataset, acquiredDatasets)) {
				dataFilePath = getConfProperty("est.data.file");
				if (StringUtils.isNotBlank(dataFilePath)) {
					if (!isFullReload) {
						estRunner.deleteDatasetData();
					}
					estSrcRunner.execute(dataFilePath, doReports);
					estRunner.execute(dataFilePath, doReports);
					successfullyLoadedDatasets.add(dataset);
				}
			}

			// termeki
			List<DatasetId> termekiIds = getTermekiIds();
			if (CollectionUtils.isNotEmpty(termekiIds)) {
				for (DatasetId datasetId : termekiIds) {
					Integer termekiId = datasetId.getId();
					dataset = datasetId.getDataset();
					if (doLoad(dataset, acquiredDatasets)) {
						if (!isFullReload) {
							termekiRunner.deleteTermekiDatasetData(dataset);
						}
						termekiRunner.execute(termekiId, dataset);
						successfullyLoadedDatasets.add(dataset);
					}
				}
			}

			// sound file names updater
			dataFilePath = getConfProperty("voice.index.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				if (isFullReload) {
					voiceFileUpdaterRunner.update(dataFilePath);
				}
			}

			// game data
			dataFilePath = getConfProperty("games.nonwords.file");
			if (StringUtils.isNotBlank(dataFilePath)) {
				if (isFullReload) {
					gameDataLoaderRunner.execute(dataFilePath);
				}
			}

			t2 = System.currentTimeMillis();
			float fullLoadTimeMin = ((float) (t2 - t1)) / 60000f;
			logger.info("----DONE LOADING DATASETS!!----");
			logger.info("Full load took {} min", fullLoadTimeMin);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
			logger.info("Successfully loaded datasets: {}", successfullyLoadedDatasets);
		} finally {
			shutdown();
		}
	}

	private boolean doLoad(String dataset, String[] acquiredDatasets) {
		if (ArrayUtils.isEmpty(acquiredDatasets)) {
			return true;
		}
		return ArrayUtils.contains(acquiredDatasets, dataset);
	}
}
