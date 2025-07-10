package eki.wordweb.service.util;

import java.util.List;

import eki.common.data.Classifier;

public interface OdLexemeClassifiers {

	String getValueStateCode();

	void setValueState(Classifier valueState);

	List<String> getRegisterCodes();

	void setRegisters(List<Classifier> registers);
}
