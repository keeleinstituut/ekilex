package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_CLASSIFIER;
import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.data.Classifier;
import eki.wordweb.constant.SystemConstant;
import eki.wordweb.data.Dataset;
import eki.wordweb.data.LanguageData;
import eki.wordweb.data.TypeDomain;
import eki.wordweb.data.db.tables.MviewWwClassifier;

@Component
public class CommonDataDbService implements SystemConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #origin, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String origin, String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		Condition where = MVIEW_WW_CLASSIFIER.NAME.eq(name.name())
				.and(MVIEW_WW_CLASSIFIER.CODE.eq(code))
				.and(MVIEW_WW_CLASSIFIER.LANG.eq(lang))
				.and(MVIEW_WW_CLASSIFIER.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE));
		if (StringUtils.isNotBlank(origin)) {
			where = where.and(MVIEW_WW_CLASSIFIER.ORIGIN.eq(origin));
		}
		Record5<String, String, String, String, String> result = create
				.select(
						MVIEW_WW_CLASSIFIER.NAME,
						MVIEW_WW_CLASSIFIER.ORIGIN,
						MVIEW_WW_CLASSIFIER.CODE,
						MVIEW_WW_CLASSIFIER.VALUE,
						MVIEW_WW_CLASSIFIER.LANG)
				.from(MVIEW_WW_CLASSIFIER)
				.where(where)
				.fetchOne();
		if (result == null) {
			return null;
		}
		return result.into(Classifier.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#name, #lang}")
	public List<Classifier> getClassifiers(ClassifierName name, String lang) {

		return create
				.select(
						MVIEW_WW_CLASSIFIER.NAME,
						MVIEW_WW_CLASSIFIER.ORIGIN,
						MVIEW_WW_CLASSIFIER.CODE,
						MVIEW_WW_CLASSIFIER.VALUE,
						MVIEW_WW_CLASSIFIER.LANG)
				.from(MVIEW_WW_CLASSIFIER)
				.where(MVIEW_WW_CLASSIFIER.NAME.eq(name.name())
						.and(MVIEW_WW_CLASSIFIER.LANG.eq(lang))
						.and(MVIEW_WW_CLASSIFIER.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.orderBy(MVIEW_WW_CLASSIFIER.ORDER_BY)
				.fetch().into(Classifier.class);
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
		Condition where = MVIEW_WW_CLASSIFIER.NAME.eq(name.name())
				.and(MVIEW_WW_CLASSIFIER.CODE.in(codes))
				.and(MVIEW_WW_CLASSIFIER.LANG.eq(lang))
				.and(MVIEW_WW_CLASSIFIER.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE));
		if (StringUtils.isNotBlank(origin)) {
			where = where.and(MVIEW_WW_CLASSIFIER.ORIGIN.eq(origin));
		}
		return create
				.select(
						MVIEW_WW_CLASSIFIER.NAME,
						MVIEW_WW_CLASSIFIER.ORIGIN,
						MVIEW_WW_CLASSIFIER.CODE,
						MVIEW_WW_CLASSIFIER.VALUE,
						MVIEW_WW_CLASSIFIER.LANG)
				.from(MVIEW_WW_CLASSIFIER)
				.where(where)
				.orderBy(MVIEW_WW_CLASSIFIER.ORDER_BY)
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
			if (classifier != null) {
				classifiers.add(classifier);
			}
		}
		return classifiers;
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public Map<String, Long> getLangOrderByMap() {
		return create
				.select(MVIEW_WW_CLASSIFIER.CODE, MVIEW_WW_CLASSIFIER.ORDER_BY)
				.from(MVIEW_WW_CLASSIFIER)
				.where(
						MVIEW_WW_CLASSIFIER.NAME.eq(ClassifierName.LANGUAGE.name())
								.and(MVIEW_WW_CLASSIFIER.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(MVIEW_WW_CLASSIFIER.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE)))
				.fetchMap(MVIEW_WW_CLASSIFIER.CODE, MVIEW_WW_CLASSIFIER.ORDER_BY);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public Map<String, LanguageData> getLangDataMap() {
		MviewWwClassifier cllbl = MVIEW_WW_CLASSIFIER.as("cllbl");
		MviewWwClassifier cliso = MVIEW_WW_CLASSIFIER.as("cliso");
		return create
				.select(cllbl.CODE, cliso.VALUE.as("codeIso2"), cllbl.VALUE.as("label"))
				.from(cllbl, cliso)
				.where(
						cllbl.NAME.eq(ClassifierName.LANGUAGE.name())
								.and(cllbl.NAME.eq(cliso.NAME))
								.and(cllbl.CODE.eq(cliso.CODE))
								.and(cllbl.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cliso.LANG.eq(DEFAULT_CLASSIF_VALUE_LANG))
								.and(cllbl.TYPE.eq(DEFAULT_CLASSIF_VALUE_TYPE))
								.and(cliso.TYPE.eq(CLASSIF_VALUE_TYPE_ISO2)))
				.fetchMap(cllbl.CODE, LanguageData.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<Dataset> getDatasets() {
		return create
				.select(
						MVIEW_WW_DATASET.CODE,
						MVIEW_WW_DATASET.TYPE,
						MVIEW_WW_DATASET.NAME,
						MVIEW_WW_DATASET.DESCRIPTION,
						MVIEW_WW_DATASET.IS_SUPERIOR)
				.from(MVIEW_WW_DATASET)
				.orderBy(MVIEW_WW_DATASET.ORDER_BY)
				.fetchInto(Dataset.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public List<String> getDatasetCodes() {
		return create
				.select(MVIEW_WW_DATASET.CODE)
				.from(MVIEW_WW_DATASET)
				.orderBy(MVIEW_WW_DATASET.ORDER_BY)
				.fetchInto(String.class);
	}

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "#root.methodName")
	public boolean fiCollationExists() {
		Integer fiCollationCnt = create
				.selectCount()
				.from("pg_collation where collname like 'fi_FI%'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
