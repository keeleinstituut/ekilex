package eki.ekilex.web.util;

import java.beans.PropertyEditorSupport;

import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;
import eki.ekilex.service.util.ConversionUtil;

@Component
public class ClassifierEditor extends PropertyEditorSupport {

	private ConversionUtil conversionUtil = new ConversionUtil();

	@Override
	public String getAsText() {
		Classifier classifier = (Classifier)this.getValue();
		return classifier.toIdString();
	}

	@Override
	public void setAsText(String classifierIdString) throws IllegalArgumentException {
		Classifier classifier = conversionUtil.classifierFromIdString(classifierIdString);
		this.setValue(classifier);
	}
}
