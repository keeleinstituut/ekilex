package eki.wwexam.service.db;

import static eki.wwexam.data.db.Tables.WW_CLASSIFIER;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import eki.common.constant.ClassifierName;
import eki.common.constant.GlobalConstant;
import eki.common.data.Classifier;
import eki.wwexam.constant.SystemConstant;
import eki.wwexam.data.db.tables.WwClassifier;

@Component
public class CommonDataDbService implements SystemConstant, GlobalConstant {

	@Autowired
	private DSLContext create;

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #code, #lang}")
	public Classifier getClassifier(ClassifierName name, String code, String lang) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		WwClassifier clc = WW_CLASSIFIER.as("clc");
		WwClassifier clv = WW_CLASSIFIER.as("clv");
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

	@Cacheable(value = CACHE_KEY_CLASSIF, key = "{#root.methodName, #name, #code}")
	public Classifier getOsClassifier(ClassifierName name, String code) {

		if (StringUtils.isBlank(code)) {
			return null;
		}
		WwClassifier cl = WW_CLASSIFIER.as("cl");
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
		WwClassifier cl = WW_CLASSIFIER.as("cl");
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
	public boolean fiCollationExists() {

		Integer fiCollationCnt = create
				.selectCount()
				.from("pg_collation where lower(collcollate) = 'fi_fi.utf8'")
				.fetchSingleInto(Integer.class);
		return fiCollationCnt > 0;
	}
}
