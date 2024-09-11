package eki.ekilex.web.util;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;

@Component
public class ClassifierUtil {

	public String toJson(Classifier classifier) {
		ReflectionToStringBuilder jsonBuilder = new ReflectionToStringBuilder(classifier, ToStringStyle.JSON_STYLE);
		jsonBuilder.setExcludeNullValues(true);
		jsonBuilder.setExcludeFieldNames("datasets", "value", "jsonStr");
		return jsonBuilder.toString();
	}

	public void populateClassifierJson(List<Classifier> classifiers) {
		classifiers.forEach(classif -> classif.setJsonStr(toJson(classif)));
	}
}
