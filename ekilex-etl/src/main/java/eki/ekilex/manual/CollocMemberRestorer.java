package eki.ekilex.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.util.ConsolePromptUtil;
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

			String colFilePath = confService.getMandatoryConfProperty("kol.data.file");
			String collocRestoreMappingFilePath = ConsolePromptUtil.promptDataFilePath("Kollokatsioonigruppide ilmikukandidaatide fail? (/rada/fail.csv)");

			/*
			 * for testing and analysing purposes only
			 * 
			String originalSourceFileReportFilePath = "missing_colloc_member_candidates_by_source_file.txt";
			String originalImportFileReportFilePath = "missing_colloc_member_candidates_by_import_file.txt";
			String originalLexemeDataFilePath = "/projects/eki/data/dictionaries/imp/sss_19-09-27/lexeme.json";
			String originalParadigmDataFilePath = "/projects/eki/data/dictionaries/imp/sss_19-09-27/paradigm.json";
			String collocRestoreMappingFilePath = "/projects/eki/data/dictionaries/colrestore/puuduvadkollid.csv";
			String transformedLexemeDataFilePath = "lexeme-transform.json";
			String transformedParadigmDataFilePath = "paradigm-transform.json";
			*/

			collocMemberRestorerRunner.initialise();
			/*
			 * for testing and analysing purposes only
			 * 
			collocMemberRestorerRunner.analyseOriginalSourceFile(colFilePath, originalSourceFileReportFilePath);
			collocMemberRestorerRunner.transformLexemeExportFile(originalLexemeDataFilePath, transformedLexemeDataFilePath);
			collocMemberRestorerRunner.transformParadigmExportFile(originalParadigmDataFilePath, transformedParadigmDataFilePath);
			collocMemberRestorerRunner.analyseOriginalImportFile(transformedLexemeDataFilePath, transformedParadigmDataFilePath, originalImportFileReportFilePath);
			*/
			collocMemberRestorerRunner.restoreCollocData(colFilePath, collocRestoreMappingFilePath);

		} catch (Exception e) {
			logger.error("Unexpected behaviour of the system", e);
		} finally {
			shutdown();
		}
	}

}
