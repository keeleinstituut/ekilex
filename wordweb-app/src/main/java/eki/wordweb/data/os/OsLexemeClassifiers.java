package eki.wordweb.data.os;

import java.util.List;

import eki.common.data.Classifier;

public interface OsLexemeClassifiers {

	String getValueStateCode();

	void setValueState(Classifier valueState);

	List<String> getRegisterCodes();

	void setRegisters(List<Classifier> registers);
}
