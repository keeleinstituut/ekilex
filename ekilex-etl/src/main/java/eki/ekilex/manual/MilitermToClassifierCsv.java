package eki.ekilex.manual;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import eki.ekilex.constant.SystemConstant;

//TODO functionality will be moved to MilitermToDomainCsvRunner
@Deprecated
public class MilitermToClassifierCsv implements SystemConstant {

	private static final String ORIGIN = "militerm";

	public static void main(String[] args) throws Exception {

		final String origDataFilePath = "/projects/eki/data/valdkond-militerm.csv";
		final String classifierEnFilePath = "./fileresources/csv/classifier-domain-" + ORIGIN + "_en.csv";
		final String classifierEtFilePath = "./fileresources/csv/classifier-domain-" + ORIGIN + "_et.csv";

		long t1, t2;
		t1 = System.currentTimeMillis();

		File origDataFile = new File(origDataFilePath);
		FileInputStream origDataFileInputStream = new FileInputStream(origDataFile);
		List<String> origDataLines = IOUtils.readLines(origDataFileInputStream, UTF_8);
		origDataFileInputStream.close();
		origDataLines.remove(0);

		composeClassifierFile(origDataLines, 1, classifierEnFilePath);
		composeClassifierFile(origDataLines, 2, classifierEtFilePath);

		t2 = System.currentTimeMillis();

		System.out.println("Done transforming classifiers at " + (t2 - t1) + " ms");
	}

	private static void composeClassifierFile(
			List<String> origDataLines, final int classifierNameColumnIndex, final String classifierFilePath) throws Exception {

		List<String> classifierOrderList = new ArrayList<>();
		Map<String, String> classifierValueMap = new HashMap<>();
		Map<String, String> classifierHierarchyMap = new HashMap<>();
		String[] csvCells;
		for (String csvLine : origDataLines) {
			csvCells = StringUtils.split(csvLine, CSV_SEPARATOR);
			String code = csvCells[0];
			String value = csvCells[classifierNameColumnIndex];
			classifierOrderList.add(code);
			classifierValueMap.put(code, value);
		}
		String suggestedParentClassifierCode;
		int subClassifierCodeLength;
		for (String subClassifierCode : classifierOrderList) {
			subClassifierCodeLength = subClassifierCode.length();
			suggestedParentClassifierCode = StringUtils.left(subClassifierCode, subClassifierCodeLength - 1);
			if (classifierValueMap.containsKey(suggestedParentClassifierCode)) {
				classifierHierarchyMap.put(subClassifierCode, suggestedParentClassifierCode);
			}
		}
		File classifierFile = new File(classifierFilePath);
		FileOutputStream classifierFileOutputStream = new FileOutputStream(classifierFile);
		String classifierFileLine;
		StringBuffer classifierLineBuf;
		for (String classifierCode : classifierOrderList) {
			String parentClassifierCode = classifierHierarchyMap.get(classifierCode);
			if (StringUtils.isBlank(parentClassifierCode)) {
				parentClassifierCode = String.valueOf(CSV_EMPTY_CELL);
			}
			String classifierValue = classifierValueMap.get(classifierCode);
			classifierLineBuf = new StringBuffer();
			classifierLineBuf.append(classifierCode);
			classifierLineBuf.append(CSV_SEPARATOR);
			classifierLineBuf.append(ORIGIN);
			classifierLineBuf.append(CSV_SEPARATOR);
			classifierLineBuf.append(parentClassifierCode);
			classifierLineBuf.append(CSV_SEPARATOR);
			classifierLineBuf.append(classifierValue);
			classifierLineBuf.append('\n');
			classifierFileLine = classifierLineBuf.toString();
			IOUtils.write(classifierFileLine, classifierFileOutputStream, UTF_8);
		}
		classifierFileOutputStream.flush();
		classifierFileOutputStream.close();
	}

}
