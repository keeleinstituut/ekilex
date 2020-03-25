package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.ekilex.runner.CollocMemberRestorerRunner;

public class CollocMemberRestorer extends AbstractLoader {

	private static Logger logger = LoggerFactory.getLogger(CollocMemberRestorer.class);

	public static void main(String[] args) {
		new CollocMemberRestorer().execute(args);
	}

	@Override
	void execute(String[] args) {
		try {
			initDefault();

			CollocMemberRestorerRunner collocMemberRestorerRunner = getComponent(CollocMemberRestorerRunner.class);

			String kolFilePath = confService.getMandatoryConfProperty("kol.data.file");
			String originalSourceFileReportFilePath = "missing_colloc_member_candidates_by_source_file.txt";
			String originalImportFileReportFilePath = "missing_colloc_member_candidates_by_import_file.txt";
			String originalLexemeDataFilePath = "/projects/eki/data/dictionaries/imp/sss_19-09-27/lexeme.json";
			String originalParadigmDataFilePath = "/projects/eki/data/dictionaries/imp/sss_19-09-27/paradigm.json";
			String transformedLexemeDataFilePath = "lexeme-transform.json";
			String transformedParadigmDataFilePath = "paradigm-transform.json";

			collocMemberRestorerRunner.initialise();
			collocMemberRestorerRunner.analyseOriginalSourceFile(kolFilePath, originalSourceFileReportFilePath);
			//collocMemberRestorerRunner.transformLexemeExportFile(originalLexemeDataFilePath, transformedLexemeDataFilePath);
			//collocMemberRestorerRunner.transformParadigmExportFile(originalParadigmDataFilePath, transformedParadigmDataFilePath);
			collocMemberRestorerRunner.analyseOriginalImportFile(transformedLexemeDataFilePath, transformedParadigmDataFilePath, originalImportFileReportFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
