package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_CLASSIFIER;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.db.tables.MviewWwClassifier;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.type.TypeDomain;

@Component
public class CommonDataDbService implements SystemConstant, GlobalConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		MviewWwClassifier clc = MVIEW_WW_CLASSIFIER.as("clc");
		MviewWwClassifier clv = MVIEW_WW_CLASSIFIER.as("clv");
		return create
				.select(
						clc.NAME,
						clc.CODE,
						DSL.coalesce(clv.VALUE, clc.CODE).as("value"),
						clv.LANG)
				.from(clc.leftOuterJoin(clv).on(
						clv.NAME.eq(clc.NAME)
								.and(clv.CODE.eq(clc.CODE))
								.and(clv.TYPE.eq(clc.TYPE))
								.and(clv.LANG.eq(lang))))
				.where(clc.NAME.eq(name.name())
						.and(clc.CODE.eq(code))
						.and(clc.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.limit(1)
				.fetchOptionalInto(Classifier.class)
				.orElse(new Classifier(name.name(), null, null, code, code, lang));
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #origin, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String origin, String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		MviewWwClassifier clc = MVIEW_WW_CLASSIFIER.as("clc");
		MviewWwClassifier clv = MVIEW_WW_CLASSIFIER.as("clv");
		return create
				.select(
						clc.NAME,
						clc.ORIGIN,
						clc.CODE,
						DSL.coalesce(clv.VALUE, clc.CODE).as("value"),
						clv.LANG)
				.from(clc.leftOuterJoin(clv).on(
						clv.NAME.eq(clc.NAME)
								.and(clv.CODE.eq(clc.CODE))
								.and(clv.ORIGIN.eq(clc.ORIGIN))
								.and(clv.TYPE.eq(clc.TYPE))
								.and(clv.LANG.eq(lang))))
				.where(clc.NAME.eq(name.name())
						.and(clc.CODE.eq(code))
						.and(clc.ORIGIN.eq(origin))
						.and(clc.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.limit(1)
				.fetchOptionalInto(Classifier.class)
				.orElse(new Classifier(name.name(), origin, null, code, code, lang));
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #lang}")
	public List<Classifier> getClassifiers(ClassifierName name, String lang) {

		MviewWwClassifier clc = MVIEW_WW_CLASSIFIER.as("clc");
		MviewWwClassifier clv = MVIEW_WW_CLASSIFIER.as("clv");
		return create
				.select(
						clc.NAME,
						clc.CODE,
						DSL.coalesce(clv.VALUE, clc.CODE).as("value"),
						clv.LANG)
				.from(clc.leftOuterJoin(clv).on(
						clv.NAME.eq(clc.NAME)
								.and(clv.CODE.eq(clc.CODE))
								.and(clv.TYPE.eq(clc.TYPE))
								.and(clv.LANG.eq(lang))))
				.where(clc.NAME.eq(name.name())
						.and(clc.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.groupBy(clc.NAME, clc.CODE, clc.ORDER_BY, clv.VALUE, clv.LANG)
				.orderBy(clc.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #codes, #lang}")
	public List<Classifier> getClassifiersInProvidedOrder(ClassifierName name, List<String> codes, String lang) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		if (codes.size() == 1) {
			String code = codes.get(0);
			Classifier classifier = getClassifier(name, code, lang);
			if (classifier == null) {
				return Collections.emptyList();
			}
			return Arrays.asList(classifier);
		}
		String[] codesArr = new String[codes.size()];
		codesArr = codes.toArray(codesArr);
		MviewWwClassifier clc = MVIEW_WW_CLASSIFIER.as("clc");
		MviewWwClassifier clv = MVIEW_WW_CLASSIFIER.as("clv");
		return create
				.select(
						clc.NAME,
						clc.CODE,
						DSL.coalesce(clv.VALUE, clc.CODE).as("value"),
						clv.LANG)
				.from(clc.leftOuterJoin(clv).on(
						clv.NAME.eq(clc.NAME)
								.and(clv.CODE.eq(clc.CODE))
								.and(clv.TYPE.eq(clc.TYPE))
								.and(clv.LANG.eq(lang))))
				.where(clc.NAME.eq(name.name())
						.and(clc.CODE.in(codes))
						.and(clc.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.groupBy(clc.NAME, clc.CODE, clc.ORDER_BY, clv.VALUE, clv.LANG)
				.orderBy(DSL.field("array_position({0}, clc.code)", Integer.class, DSL.val(codesArr)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #codes, #lang}")
	public List<Classifier> getClassifiersInSystemOrder(ClassifierName name, List<String> codes, String lang) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		if (codes.size() == 1) {
			String code = codes.get(0);
			Classifier classifier = getClassifier(name, code, lang);
			if (classifier == null) {
				return Collections.emptyList();
			}
			return Arrays.asList(classifier);
		}
		MviewWwClassifier clc = MVIEW_WW_CLASSIFIER.as("clc");
		MviewWwClassifier clv = MVIEW_WW_CLASSIFIER.as("clv");
		return create
				.select(
						clc.NAME,
						clc.CODE,
						DSL.coalesce(clv.VALUE, clc.CODE).as("value"),
						clv.LANG)
				.from(clc.leftOuterJoin(clv).on(
						clv.NAME.eq(clc.NAME)
								.and(clv.CODE.eq(clc.CODE))
								.and(clv.TYPE.eq(clc.TYPE))
								.and(clv.LANG.eq(lang))))
				.where(clc.NAME.eq(name.name())
						.and(clc.CODE.in(codes))
						.and(clc.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.groupBy(clc.NAME, clc.CODE, clc.ORDER_BY, clv.VALUE, clv.LANG)
				.orderBy(clc.ORDER_BY)
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #codes, #lang}")
	public List<Classifier> getClassifiersWithOrigin(ClassifierName name, List<TypeDomain> codes, String lang) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = new ArrayList<>();
		for (TypeDomain originCodeTuple : codes) {
			Classifier classifier = getClassifier(name, originCodeTuple.getOrigin(), originCodeTuple.getCode(), lang);
			if (classifier != null) {
				classifiers.add(classifier);
			}
		}
		return classifiers;
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #code}")
	public Classifier getOsClassifier(ClassifierName name, String code) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		MviewWwClassifier cl = MVIEW_WW_CLASSIFIER.as("cl");
		return create
				.select(
						cl.NAME,
						cl.CODE,
						cl.VALUE,
						cl.LANG)
				.from(cl)
				.where(
						cl.NAME.eq(name.name())
								.and(cl.CODE.eq(code))
								.and(cl.TYPE.eq(OS_CLASSIF_VALUE_TYPE))
								.and(cl.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG)))
				.limit(1)
				.fetchOptionalInto(Classifier.class)
				.orElse(null);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #codes, #lang}")
	public List<Classifier> getOsClassifiersInProvidedOrder(ClassifierName name, List<String> codes) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		if (codes.size() == 1) {
			String code = codes.get(0);
			Classifier classifier = getOsClassifier(name, code);
			if (classifier == null) {
				return Collections.emptyList();
			}
			return Arrays.asList(classifier);
		}
		String[] codesArr = new String[codes.size()];
		codesArr = codes.toArray(codesArr);
		MviewWwClassifier cl = MVIEW_WW_CLASSIFIER.as("cl");
		return create
				.select(
						cl.NAME,
						cl.CODE,
						cl.VALUE,
						cl.LANG)
				.from(cl)
				.where(cl.NAME.eq(name.name())
						.and(cl.CODE.in(codes))
						.and(cl.TYPE.eq(OS_CLASSIF_VALUE_TYPE))
						.and(cl.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG)))
				.groupBy(cl.NAME, cl.CODE, cl.ORDER_BY, cl.VALUE, cl.LANG)
				.orderBy(DSL.field("array_position({0}, cl.code)", Integer.class, DSL.val(codesArr)))
				.fetchInto(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public Map<String, Long> getLangOrderByMap() {

		MviewWwClassifier cl = MVIEW_WW_CLASSIFIER.as("cl");
		return create
				.select(cl.CODE, cl.ORDER_BY)
				.from(cl)
				.where(
						cl.NAME.eq(ClassifierName.LANGUAGE.name())
								.and(cl.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cl.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.fetchMap(cl.CODE, cl.ORDER_BY);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #lang}")
	public Map<String, LanguageData> getLangDataMap(String lang) {

		MviewWwClassifier cl = MVIEW_WW_CLASSIFIER.as("cl");
		MviewWwClassifier cli = MVIEW_WW_CLASSIFIER.as("cli");
		MviewWwClassifier cll = MVIEW_WW_CLASSIFIER.as("cll");

		return create
				.select(
						cl.CODE,
						DSL.coalesce(cli.VALUE, DSL.value("?")).as("codeIso2"),
						DSL.coalesce(cll.VALUE, cl.VALUE).as("label"))
				.from(cl
						.leftOuterJoin(cli).on(
								cli.NAME.eq(cl.NAME)
										.and(cli.CODE.eq(cl.CODE))
										.and(cli.TYPE.eq(CLASSIF_VALUE_TYPE_ISO2))
										.and(cli.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG)))
						.leftOuterJoin(cll).on(
								cll.NAME.eq(cl.NAME)
										.and(cll.CODE.eq(cl.CODE))
										.and(cll.TYPE.eq(cl.TYPE))
										.and(cll.LANG.eq(lang))))
				.where(
						cl.NAME.eq(ClassifierName.LANGUAGE.name())
								.and(cl.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cl.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.fetchMap(cl.CODE, LanguageData.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #datasetCode}")
	public Dataset getDataset(String datasetCode) {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		return create
				.select(
						ds.CODE,
						ds.TYPE,
						ds.NAME,
						ds.DESCRIPTION,
						ds.IMAGE_URL,
						ds.IS_SUPERIOR)
				.from(ds)
				.where(ds.CODE.eq(datasetCode))
				.fetchOptionalInto(Dataset.class)
				.orElse(null);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #codes}")
	public List<Dataset> getDatasets(List<String> codes) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		if (codes.size() == 1) {
			String code = codes.get(0);
			Dataset dataset = getDataset(code);
			return Arrays.asList(dataset);
		}
		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		Field<Integer> dsobf = DSL
				.when(ds.CODE.eq(DATASET_EKI), DSL.value(0))
				.when(ds.CODE.eq(DATASET_ESTERM), DSL.value(1))
				.otherwise(DSL.value(2));
		List<Field<?>> orderByFields = new ArrayList<>();
		orderByFields.add(dsobf);
		orderByFields.add(ds.NAME);

		return create
				.select(
						ds.CODE,
						ds.TYPE,
						ds.NAME,
						ds.DESCRIPTION,
						ds.IS_SUPERIOR)
				.from(ds)
				.where(ds.CODE.in(codes))
				.orderBy(orderByFields)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Dataset> getDatasets() {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		Field<Integer> dsobf = DSL
				.when(ds.CODE.eq(DATASET_EKI), DSL.value(0))
				.when(ds.CODE.eq(DATASET_ESTERM), DSL.value(1))
				.otherwise(DSL.value(2));
		List<Field<?>> orderByFields = new ArrayList<>();
		orderByFields.add(dsobf);
		orderByFields.add(ds.NAME);

		return create
				.select(
						ds.CODE,
						ds.TYPE,
						ds.NAME,
						ds.DESCRIPTION,
						ds.IS_SUPERIOR)
				.from(ds)
				.orderBy(orderByFields)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<String> getDatasetCodes() {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");
		Field<Integer> dsobf = DSL
				.when(ds.CODE.eq(DATASET_EKI), DSL.value(0))
				.when(ds.CODE.eq(DATASET_ESTERM), DSL.value(1))
				.otherwise(DSL.value(2));
		List<Field<?>> orderByFields = new ArrayList<>();
		orderByFields.add(dsobf);
		orderByFields.add(ds.NAME);

		return create
				.select(ds.CODE)
				.from(ds)
				.orderBy(orderByFields)
				.fetchInto(String.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public boolean fiCollationExists() {

		Integer fiCollationCnt = create
				.selectCount()
				.from("pg_collation where lower(collcollate) = 'fi_fi.utf8'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
