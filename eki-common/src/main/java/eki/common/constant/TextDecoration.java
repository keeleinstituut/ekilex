package eki.common.constant;

import eki.common.data.CodeValue;

public interface TextDecoration {

	CodeValue FOREIGN = new CodeValue("eki-foreign", "Tsitaatsõna");

	CodeValue HIGHLIGHT = new CodeValue("eki-highlight", "Esiletõstetud märksõna");

	CodeValue STRESS = new CodeValue("eki-stress", "Rõhk");

	CodeValue SUB = new CodeValue("eki-sub", "Alaindeks");

	CodeValue SUP = new CodeValue("eki-sup", "Ülaindeks");

	CodeValue META = new CodeValue("eki-meta", "Metatähistus");

	CodeValue LINK = new CodeValue("eki-link", "Viide");
	
	CodeValue FORM = new CodeValue("eki-form", "Formatiiv");

	CodeValue[] EKI_MARKUP_ELEMENTS = new CodeValue[] {
			FOREIGN, HIGHLIGHT, STRESS, SUB, SUP, META, LINK, FORM
	};
}
