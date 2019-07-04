package eki.ekilex.web.util;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;

@Component
public class ClassifierUtil {
	public String toJson(Classifier classifier) {
		return new ReflectionToStringBuilder(classifier, ToStringStyle.JSON_STYLE).setExcludeFieldNames("value").toString();
	}
}
