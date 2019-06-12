package eki.ekilex.data.editor;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eki.ekilex.data.Classifier;

@Component
public class ClassifierEditor extends PropertyEditorSupport {

	private static final String FIELDS_SEPARATOR = "|";

	private static final int NAME_POS = 0;
	private static final int CODE_POS = 1;
	private static final int ORIGIN_POS = 2;
	private static final int VALUE_POS = 3;

	@Override
	public String getAsText() {
		Classifier classifier = (Classifier)this.getValue();

		return classifier.getCode() + FIELDS_SEPARATOR + classifier.getOrigin() + FIELDS_SEPARATOR + classifier.getValue();

	}

	@Override
	public void setAsText(String tokenizedClassifierData) throws IllegalArgumentException {
		Classifier classifier = new Classifier();
		if (StringUtils.isNotBlank(tokenizedClassifierData)) {
			String[] classiferData = tokenizedClassifierData.split("\\|", -1);
			classifier.setName(classiferData[NAME_POS]);
			classifier.setCode(classiferData[CODE_POS]);
			if (classiferData.length > ORIGIN_POS) {
				classifier.setOrigin(classiferData[ORIGIN_POS]);
			}
			if (classiferData.length > VALUE_POS) {
				classifier.setValue(classiferData[VALUE_POS]);
			}
		}

		this.setValue(classifier);
	}
}
