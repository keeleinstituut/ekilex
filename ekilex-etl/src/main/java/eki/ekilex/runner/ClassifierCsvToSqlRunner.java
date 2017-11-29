package eki.ekilex.runner;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.ekilex.data.transform.Classifier;

// does not handle LANG, DOMAIN, LABEL_TYPE
@Component
public class ClassifierCsvToSqlRunner extends AbstractClassifierRunner {

	private static Logger logger = LoggerFactory.getLogger(ClassifierCsvToSqlRunner.class);
	
	private static final String INSERT_CLASSIFIER_TEMPLATE = "insert into ${name} (code, datasets) values ('${code}', '{}');";

	private static final String INSERT_CLASSIF_W_ORIGIN_TEMPLATE = "insert into ${name} (origin, code, datasets) values ('${origin}', '${code}', '{}');";

	private static final String INSERT_CLASSIF_W_PARENT_AND_ORIGIN_TEMPLATE =
			"insert into ${name} (origin, code, parent_origin, parent_code, datasets) values ('${origin}', '${code}', '${parentOrigin}', '${parentCode}', '{}');";

	private static final String INSERT_CLASSIFIER_LABEL_TEMPLATE = "insert into ${name}_label (code, value, lang, type) values ('${code}', '${value}', '${lang}', '${type}');";

	private static final String INSERT_CLASSIF_W_ORIGIN_LABEL_TEMPLATE =
			"insert into ${name}_label (origin, code, value, lang, type) values ('${origin}', '${code}', '${value}', '${lang}', '${type}');";

	@Override
	void initialise() throws Exception {

	}

	public void execute() throws Exception {

		List<Classifier> classifiers;
		StringBuffer insertClassifBuf;
		StringBuffer insertClassifLabelBuf;

		classifiers = loadExistingMainClassifiers();
		insertClassifBuf = new StringBuffer();
		insertClassifLabelBuf = new StringBuffer();
		composeClassifierSql(classifiers, insertClassifBuf, insertClassifLabelBuf);
		writeToFile(CLASSIFIER_MAIN_SQL_PATH, insertClassifBuf, insertClassifLabelBuf);

		classifiers = loadExistingDomainClassifiers();
		insertClassifBuf = new StringBuffer();
		insertClassifLabelBuf = new StringBuffer();
		composeClassifierSql(classifiers, insertClassifBuf, insertClassifLabelBuf);
		writeToFile(CLASSIFIER_DOMAIN_SQL_PATH, insertClassifBuf, insertClassifLabelBuf);

		logger.debug("Done creating SQL");
	}

	private void writeToFile(String filePath, StringBuffer insertClassifBuf, StringBuffer insertClassifLabelBuf) throws Exception {

		FileOutputStream classifierSqlStream = new FileOutputStream(filePath);

		IOUtils.write(insertClassifBuf, classifierSqlStream, StandardCharsets.UTF_8);
		IOUtils.write("\n", classifierSqlStream, StandardCharsets.UTF_8);
		IOUtils.write(insertClassifLabelBuf, classifierSqlStream, StandardCharsets.UTF_8);

		classifierSqlStream.flush();
		classifierSqlStream.close();
	}

	private void composeClassifierSql(List<Classifier> classifiers, StringBuffer insertClassifBuf, StringBuffer insertClassifLabelBuf) {

		List<String> insertClassifiers = new ArrayList<>();
		String sql;

		for (Classifier classifier : classifiers) {

			String origin = classifier.getOrigin();
			String name = classifier.getName();
			String code = classifier.getCode();
			String parent = classifier.getParent();
			String value = classifier.getValue();
			String valueLang = unifyLang(classifier.getValueLang());
			String valueType = classifier.getValueType();

			if (isValued(name)
					&& isValued(code)
					&& isValued(value)
					&& isValued(valueLang)
					&& isValued(valueType)) { // code + label

				ClassifierName classifierName = ClassifierName.valueOf(name.toUpperCase());
				String classifKey = composeRow(CLASSIFIER_KEY_SEPARATOR, origin, name, code);
				if (!insertClassifiers.contains(classifKey)) {
					insertClassifiers.add(classifKey);
					sql = composeInsertClassifier(origin, name, code, parent);
					insertClassifBuf.append(sql);
					insertClassifBuf.append('\n');
				}
				if (!classifierName.hasLabel()) {
					logger.warn("Unable to create label for classifier {} since it has no label", name);
					continue;
				}
				sql = composeInsertClassifierLabel(origin, name, code, value, valueLang, valueType);
				insertClassifLabelBuf.append(sql);
				insertClassifLabelBuf.append('\n');

			} else if (isValued(name) && isValued(code)) { // no label, only code

				String classifKey = composeRow(CLASSIFIER_KEY_SEPARATOR, name, code);
				if (!insertClassifiers.contains(classifKey)) {
					insertClassifiers.add(classifKey);
					sql = composeInsertClassifier(origin, name, code, parent);
					insertClassifBuf.append(sql);
					insertClassifBuf.append('\n');
				}

			} else {
				logger.warn("Unmapped data row: \"{}\"", classifier.toString());
				continue;
			}
		}
	}

	private String composeInsertClassifier(String origin, String name, String code, String parent) {

		String sql;
		if (isValued(origin) && isValued(parent)) {
			sql = new String(INSERT_CLASSIF_W_PARENT_AND_ORIGIN_TEMPLATE);
			sql = StringUtils.replace(sql, "${origin}", origin);
			sql = StringUtils.replace(sql, "${parentCode}", parent);
			sql = StringUtils.replace(sql, "${parentOrigin}", origin);
		} else if (isValued(origin)) {
			sql = new String(INSERT_CLASSIF_W_ORIGIN_TEMPLATE);
			sql = StringUtils.replace(sql, "${origin}", origin);
		} else {
			sql = new String(INSERT_CLASSIFIER_TEMPLATE);
		}
		sql = StringUtils.replace(sql, "${name}", name);
		sql = StringUtils.replace(sql, "${code}", code);
		return sql;
	}

	private String composeInsertClassifierLabel(String origin, String name, String code, String value, String lang, String type) {

		String sql;
		if (isValued(origin)) {
			sql = new String(INSERT_CLASSIF_W_ORIGIN_LABEL_TEMPLATE);
			sql = StringUtils.replace(sql, "${origin}", origin);
		} else {
			sql = new String(INSERT_CLASSIFIER_LABEL_TEMPLATE);
		}
		value = StringUtils.replace(value, "'", "''");
		sql = StringUtils.replace(sql, "${name}", name);
		sql = StringUtils.replace(sql, "${code}", code);
		sql = StringUtils.replace(sql, "${value}", value);
		sql = StringUtils.replace(sql, "${lang}", lang);
		sql = StringUtils.replace(sql, "${type}", type);
		return sql;
	}

}
