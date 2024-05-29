package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_CLASSIFIER;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;
import static eki.wordweb.data.db.Tables.MVIEW_WW_NEWS_ARTICLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.NewsArticleType;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.NewsArticle;
import eki.wordweb.data.db.tables.MviewWwClassifier;
import eki.wordweb.data.db.tables.MviewWwDataset;
import eki.wordweb.data.db.tables.MviewWwNewsArticle;
import eki.wordweb.data.type.TypeDomain;

@Component
public class CommonDataDbService implements SystemConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #code, #lang}")
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

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #origin, #code, #lang}")
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

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #lang}")
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

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #codes, #lang}")
	public List<Classifier> getClassifiers(ClassifierName name, List<String> codes, String lang) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		if (codes.size() == 1) {
			String code = codes.get(0);
			Classifier classifier = getClassifier(name, code, lang);
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

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #codes, #lang}")
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

		MviewWwClassifier cllbldflt = MVIEW_WW_CLASSIFIER.as("cllbldflt");
		MviewWwClassifier cllbllang = MVIEW_WW_CLASSIFIER.as("cllbllang");
		MviewWwClassifier cliso = MVIEW_WW_CLASSIFIER.as("cliso");

		return create
				.select(
						cllbldflt.CODE,
						cliso.VALUE.as("codeIso2"),
						DSL.coalesce(cllbllang.VALUE, cllbldflt.VALUE).as("label"))
				.from(
						cliso,
						cllbldflt.leftOuterJoin(cllbllang).on(
								cllbllang.NAME.eq(cllbldflt.NAME)
										.and(cllbllang.CODE.eq(cllbldflt.CODE))
										.and(cllbllang.TYPE.eq(cllbldflt.TYPE))
										.and(cllbllang.LANG.eq(lang))))
				.where(
						cllbldflt.NAME.eq(ClassifierName.LANGUAGE.name())
								.and(cllbldflt.NAME.eq(cliso.NAME))
								.and(cllbldflt.CODE.eq(cliso.CODE))
								.and(cllbldflt.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cliso.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cllbldflt.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE))
								.and(cliso.TYPE.eq(CLASSIF_VALUE_TYPE_ISO2)))
				.fetchMap(cllbldflt.CODE, LanguageData.class);
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
		return create
				.select(
						ds.CODE,
						ds.TYPE,
						ds.NAME,
						ds.DESCRIPTION,
						ds.IS_SUPERIOR)
				.from(ds)
				.where(ds.CODE.in(codes))
				.orderBy(ds.NAME)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Dataset> getDatasets() {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");

		return create
				.select(
						ds.CODE,
						ds.TYPE,
						ds.NAME,
						ds.DESCRIPTION,
						ds.IS_SUPERIOR)
				.from(ds)
				.orderBy(ds.NAME)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<String> getDatasetCodes() {

		MviewWwDataset ds = MVIEW_WW_DATASET.as("ds");

		return create
				.select(ds.CODE)
				.from(ds)
				.orderBy(ds.NAME)
				.fetchInto(String.class);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #lang}")
	public NewsArticle getLatestWordwebNewsArticle(String lang) {

		MviewWwNewsArticle na = MVIEW_WW_NEWS_ARTICLE.as("na");

		return create
				.selectFrom(na)
				.where(
						na.TYPE.eq(NewsArticleType.WORDWEB.name())
								.and(na.LANG.eq(lang)))
				.orderBy(na.CREATED.desc())
				.limit(1)
				.fetchOptionalInto(NewsArticle.class)
				.orElse(null);
	}

	@Cacheable(value = CACHE_KEY_GENERIC, key = "{#root.methodName, #lang}")
	public List<NewsArticle> getWordwebNewsArticles(String lang) {

		MviewWwNewsArticle na = MVIEW_WW_NEWS_ARTICLE.as("na");

		return create
				.selectFrom(na)
				.where(
						na.TYPE.eq(NewsArticleType.WORDWEB.name())
								.and(na.LANG.eq(lang)))
				.orderBy(na.CREATED.desc())
				.fetchInto(NewsArticle.class);
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
