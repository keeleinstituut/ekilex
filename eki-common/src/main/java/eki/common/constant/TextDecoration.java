package eki.common.constant;

import eki.common.data.CodeValue;

public interface TextDecoration {

	CodeValue FOREIGN = new CodeValue("eki-foreign", "Tsitaatsõna");

	CodeValue HIGHLIGHT = new CodeValue("eki-highlight", "Esiletsõtetud märksõna");

	CodeValue SUB = new CodeValue("eki-sub", "Alaindeks");

	CodeValue SUP = new CodeValue("eki-sup", "Ülaindeks");

	CodeValue META = new CodeValue("eki-meta", "Metatähistus");

	CodeValue LINK = new CodeValue("eki-link", "Viide");

	CodeValue[] EKI_MARKUP_ELEMENTS = new CodeValue[] {
			FOREIGN, HIGHLIGHT, SUB, SUP, META, LINK
	};
}
