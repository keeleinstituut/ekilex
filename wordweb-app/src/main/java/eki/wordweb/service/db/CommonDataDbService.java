package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_CLASSIFIER;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.TypeDomain;

@Component
public class CommonDataDbService implements SystemConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_DATASET, key = "{#code, #lang}")
	public String getDatasetName(String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		Record1<String> result = create
				.select(MVIEW_WW_DATASET.NAME)
				.from(MVIEW_WW_DATASET)
				.where(
						MVIEW_WW_DATASET.CODE.eq(code)
						.and(MVIEW_WW_DATASET.LANG.eq(lang)))
				.fetchOne();
		if (result == null) {
			return null;
		}
		return result.into(String.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #origin, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String origin, String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		Condition where =
				MVIEW_WW_CLASSIFIER.NAME.eq(name.name())
				.and(MVIEW_WW_CLASSIFIER.CODE.eq(code))
				.and(MVIEW_WW_CLASSIFIER.LANG.eq(lang));
		if (StringUtils.isNotBlank(origin)) {
			where = where.and(MVIEW_WW_CLASSIFIER.ORIGIN.eq(origin));
		}
		Record5<String, String, String, String, String> result = create
				.select(
						MVIEW_WW_CLASSIFIER.NAME,
						MVIEW_WW_CLASSIFIER.ORIGIN,
						MVIEW_WW_CLASSIFIER.CODE,
						MVIEW_WW_CLASSIFIER.VALUE,
						MVIEW_WW_CLASSIFIER.LANG
						)
				.from(MVIEW_WW_CLASSIFIER)
				.where(where)
				.fetchOne();
		if (result == null) {
			return null;
		}
		return result.into(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String code, String lang) {
		return getClassifier(name, null, code, lang);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #origin, #codes, #lang}")
	public List<Classifier> getClassifiers(ClassifierName name, String origin, List<String> codes, String lang) {

		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		Condition where =
				MVIEW_WW_CLASSIFIER.NAME.eq(name.name())
				.and(MVIEW_WW_CLASSIFIER.CODE.in(codes))
				.and(MVIEW_WW_CLASSIFIER.LANG.eq(lang));
		if (StringUtils.isNotBlank(origin)) {
			where = where.and(MVIEW_WW_CLASSIFIER.ORIGIN.eq(origin));
		}
		return create
				.select(
						MVIEW_WW_CLASSIFIER.NAME,
						MVIEW_WW_CLASSIFIER.ORIGIN,
						MVIEW_WW_CLASSIFIER.CODE,
						MVIEW_WW_CLASSIFIER.VALUE,
						MVIEW_WW_CLASSIFIER.LANG
						)
				.from(MVIEW_WW_CLASSIFIER)
				.where(where)
				.fetch().into(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #codes, #lang}")
	public List<Classifier> getClassifiers(ClassifierName name, List<String> codes, String lang) {
		return getClassifiers(name, null, codes, lang);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #codes, #lang}")
	public List<Classifier> getClassifiersWithOrigin(ClassifierName name, List<TypeDomain> codes, String lang) {
		if (CollectionUtils.isEmpty(codes)) {
			return Collections.emptyList();
		}
		List<Classifier> classifiers = new ArrayList<>();
		for (TypeDomain originCodeTuple : codes) {
			Classifier classifier = getClassifier(name, originCodeTuple.getOrigin(), originCodeTuple.getCode(), lang);
			classifiers.add(classifier);
		}
		return classifiers;
	}
}
