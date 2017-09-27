package eki.ekilex.manual;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import eki.common.constant.TableName;
import eki.common.data.Classifier;
import eki.ekilex.constant.SystemConstant;

public class ClassifierCsvToSql implements SystemConstant, TableName {

	public static void main(String[] args) throws Exception {

		final String classifierFilesFolderPath = "./fileresources/csv/";
		final String dataSqlFilePath = "./fileresources/sql/classifier_data_autom.sql";
		final String labelType = "descrip";
		final String[] datasets = new String[] {"eos", "ss_", "sys", "evs"};
		final String datasetArrayValue = asArrayField(datasets);

		long t1, t2;
		t1 = System.currentTimeMillis();

		File classifierFilesFolder = new File(classifierFilesFolderPath);
		File[] classifierFiles = extractClassifierFiles(classifierFilesFolder);

		List<Classifier> classifiers = new ArrayList<>();
		List<Classifier> loadedClassifiers;
		List<String> alreadyExistingClassifers = new ArrayList<>();
		String alreadyExistingClassifierKey;

		// load classifiers

		for (File classifierFile : classifierFiles) {

			loadedClassifiers = loadClassifiers(classifierFile);
			classifiers.addAll(loadedClassifiers);
		}

		File dataFile = new File(dataSqlFilePath);
		FileOutputStream dataFileOutputStream = new FileOutputStream(dataFile);
		String dataFileLine;
		StringBuffer dataLineBuf;

		// create classifiers

		for (Classifier classifier : classifiers) {

			String name = classifier.getName();
			String code = classifier.getCode();
			String origin = classifier.getOrigin();
			String parent = classifier.getParent();

			if (StringUtils.equals(name, DOMAIN)) {
				alreadyExistingClassifierKey = origin + "-" + name + "-" + code;
			} else {
				alreadyExistingClassifierKey = name + "-" + code;
			}
			if (alreadyExistingClassifers.contains(alreadyExistingClassifierKey)) {
				continue;
			} else {
				alreadyExistingClassifers.add(alreadyExistingClassifierKey);
			}

			dataLineBuf = new StringBuffer();
			dataLineBuf.append("insert into ");
			dataLineBuf.append(name);
			dataLineBuf.append(" ");

			if (StringUtils.equals(name, DOMAIN)) {
				dataLineBuf.append("(code, origin, parent_code, parent_origin, datasets) values (");
				dataLineBuf.append(asStringField(code));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(origin));
				dataLineBuf.append(", ");
				if (StringUtils.isBlank(parent)) {
					dataLineBuf.append("null, null, ");
				} else {
					dataLineBuf.append(asStringField(parent));
					dataLineBuf.append(", ");
					dataLineBuf.append(asStringField(origin));
					dataLineBuf.append(", ");
				}
				dataLineBuf.append(datasetArrayValue);
				dataLineBuf.append(");");
			} else {
				dataLineBuf.append("(code, datasets) values (");
				dataLineBuf.append(asStringField(code));
				dataLineBuf.append(", ");
				dataLineBuf.append(datasetArrayValue);
				dataLineBuf.append(");");
			}
			dataLineBuf.append('\n');
			dataFileLine = dataLineBuf.toString();
			IOUtils.write(dataFileLine, dataFileOutputStream, UTF_8);
		}
		IOUtils.write("\n", dataFileOutputStream, UTF_8);
		dataFileOutputStream.flush();

		// create classifiers labels

		for (Classifier classifier : classifiers) {

			String name = classifier.getName();
			String code = classifier.getCode();
			String origin = classifier.getOrigin();
			String value = classifier.getValue();
			value = StringUtils.replace(value, "'", "''");
			String lang = classifier.getLang();

			dataLineBuf = new StringBuffer();
			dataLineBuf.append("insert into ");
			dataLineBuf.append(name);
			dataLineBuf.append("_label ");

			if (StringUtils.equals(name, DOMAIN)) {
				dataLineBuf.append("(code, origin, value, lang, type) values (");
				dataLineBuf.append(asStringField(code));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(origin));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(value));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(lang));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(labelType));
				dataLineBuf.append(");");
			} else {
				dataLineBuf.append("(code, value, lang, type) values (");
				dataLineBuf.append(asStringField(code));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(value));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(lang));
				dataLineBuf.append(", ");
				dataLineBuf.append(asStringField(labelType));
				dataLineBuf.append(");");
			}
			dataLineBuf.append('\n');
			dataFileLine = dataLineBuf.toString();
			IOUtils.write(dataFileLine, dataFileOutputStream, UTF_8);
		}
		IOUtils.write("\n", dataFileOutputStream, UTF_8);
		dataFileOutputStream.flush();
		dataFileOutputStream.close();

		t2 = System.currentTimeMillis();

		System.out.println("Done loading classifiers at " + (t2 - t1) + " ms");
	}

	private static File[] extractClassifierFiles(File classifierFilesFolder) {
		File[] classifierFiles = classifierFilesFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean accept = StringUtils.startsWith(name, "classifier-") && StringUtils.endsWith(name, ".csv");
				return accept;
			}
		});
		return classifierFiles;
	}

	private static String asStringField(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return "'" + value + "'";
	}

	private static String asArrayField(String[] values) {

		String arrayValue = "'{" + StringUtils.join(values, ", ") + "}'";
		return arrayValue;
	}

	private static List<Classifier> loadClassifiers(File classifierFile) throws Exception {

		List<Classifier> classifiers = new ArrayList<>();

		String classifierFileName = classifierFile.getName();
		classifierFileName = StringUtils.substringBetween(classifierFileName, "classifier-", ".csv");
		String name = StringUtils.substringBefore(classifierFileName, "-");
		String lang = StringUtils.substringAfter(classifierFileName, "_");
		lang = new Locale(lang).getISO3Language();

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
