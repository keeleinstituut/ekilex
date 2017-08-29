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

public class MilitermToClassifier implements SystemConstant {

	public static void main(String[] args) throws Exception {

		final String origDataFilePath = "/projects/eki/data/valdkond-militerm.csv";
		final String classifierEnFilePath = "./fileresources/csv/classifier-domain-militerm_en.csv";
		final String classifierEtFilePath = "./fileresources/csv/classifier-domain-militerm_et.csv";

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
		Map<String, String> classifierNameMap = new HashMap<>();
		Map<String, String> classifierHierarchyMap = new HashMap<>();
		String[] csvCells;
		for (String csvLine : origDataLines) {
			csvCells = StringUtils.split(csvLine, CSV_SEPARATOR);
			String code = csvCells[0];
			String name = csvCells[classifierNameColumnIndex];
			classifierOrderList.add(code);
			classifierNameMap.put(code, name);
		}
		String suggestedParentClassifierCode;
		int subClassifierCodeLength;
		for (String subClassifierCode : classifierOrderList) {
			subClassifierCodeLength = subClassifierCode.length();
			suggestedParentClassifierCode = StringUtils.left(subClassifierCode, subClassifierCodeLength - 1);
			if (classifierNameMap.containsKey(suggestedParentClassifierCode)) {
				classifierHierarchyMap.put(subClassifierCode, suggestedParentClassifierCode);
			}
		}
		File classifierFile = new File(classifierFilePath);
		FileOutputStream classifierFileOutputStream = new FileOutputStream(classifierFile);
		String classifierFileLine;
		for (String classifierCode : classifierOrderList) {
			String parentClassifierCode = classifierHierarchyMap.get(classifierCode);
			if (StringUtils.isBlank(parentClassifierCode)) {
				parentClassifierCode = String.valueOf(CSV_EMPTY_CELL);
			}
			String classifierName = classifierNameMap.get(classifierCode);
			classifierFileLine = classifierCode + CSV_SEPARATOR + parentClassifierCode + CSV_SEPARATOR + classifierName + '\n';
			IOUtils.write(classifierFileLine, classifierFileOutputStream, UTF_8);
		}
		classifierFileOutputStream.flush();
		classifierFileOutputStream.close();
	}

}
