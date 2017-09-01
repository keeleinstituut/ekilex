package eki.ekilex.manual;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import eki.common.data.Classifier;
import eki.ekilex.constant.SystemConstant;

public class ClassifierCsvToSql implements SystemConstant {

	public static void main(String[] args) throws Exception {

		final String classifierFilesFolderPath = "./fileresources/csv/";
		final String[] dataset = new String[] {"eos", "ss_", "sys", "evs"};

		long t1, t2;
		t1 = System.currentTimeMillis();

		File classifierFilesFolder = new File(classifierFilesFolderPath);
		File[] classifierFiles = classifierFilesFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				boolean accept = StringUtils.startsWith(name, "classifier-") && StringUtils.endsWith(name, ".csv");
				return accept;
			}
		});

		List<Classifier> classifiers = new ArrayList<>();
		List<Classifier> loadedClassifiers;

		for (File classifierFile : classifierFiles) {

			loadedClassifiers = loadClassifiers(classifierFile);

			classifiers.addAll(loadedClassifiers);
		}

		StringBuffer sqlBuf = new StringBuffer();

		for (Classifier classifier : classifiers) {

			String name = classifier.getName();
			String code = classifier.getCode();
			String origin = classifier.getOrigin();
			String parent = classifier.getParent();
			String value = classifier.getValue();
			String lang = classifier.getLang();

			sqlBuf.append("insert into ");
			sqlBuf.append(name);
			sqlBuf.append("(code");
			//TODO impl!

			//FIXME constants!
			if (StringUtils.equals(name, "domain")) {
			} else {
				
			}
		}

		t2 = System.currentTimeMillis();

		System.out.println("Done loading classifiers at " + (t2 - t1) + " ms");
	}

	private static List<Classifier> loadClassifiers(File classifierFile) throws Exception {

		List<Classifier> classifiers = new ArrayList<>();

		String classifierFileName = classifierFile.getName();
		classifierFileName = StringUtils.substringBetween(classifierFileName, "classifier-", ".csv");
		String name = StringUtils.substringBefore(classifierFileName, "-");
		String lang = StringUtils.substringAfter(classifierFileName, "_");

		FileInputStream classifierFileInputStream = new FileInputStream(classifierFile);
		List<String> classifierFileLines = IOUtils.readLines(classifierFileInputStream, UTF_8);
		classifierFileInputStream.close();

		String[] classifierParts;
		String code, origin, parent, value;
		Classifier classifier;
		for (String classifierFileLine : classifierFileLines) {
			classifierParts = StringUtils.split(classifierFileLine, CSV_SEPARATOR);
			code = classifierParts[0];
			origin = classifierParts[1];
			parent = classifierParts[2];
			value = classifierParts[3];
			if (StringUtils.equals(parent, String.valueOf(CSV_EMPTY_CELL))) {
				parent = null;
			}
			classifier = new Classifier(name, code, origin, parent, value, lang);
			classifiers.add(classifier);
		}
		return classifiers;
	}
}
