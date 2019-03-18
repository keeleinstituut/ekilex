package eki.ekilex.constant;

import eki.common.constant.FreeformType;
import eki.common.constant.TableName;

public interface TermLoaderConstant extends TableName {

	String SQL_SELECT_COUNT_DOMAIN_BY_CODE_AND_ORIGIN = "select count(code) cnt from " + DOMAIN + " where code = :code and origin = :origin";

	String SQL_SELECT_SOURCE_BY_NAME =
			"select "
			+ "s.id "
			+ "from " + SOURCE + " s, " + SOURCE_FREEFORM + " sff "
			+ "where sff.source_id = s.id and exists ("
			+ "select sp.id "
			+ "from " + FREEFORM + " sp "
			+ "where "
			+ "sff.freeform_id = sp.id "
			+ "and sp.type = '" + FreeformType.SOURCE_NAME.name() + "' "
			+ "and sp.value_text = :sourceName)";

	String REPORT_DEFINITIONS_NOTES_MESS = "definitions_notes_mess";

	String REPORT_CREATED_MODIFIED_MESS = "created_modified_mess";

	String REPORT_ILLEGAL_CLASSIFIERS = "illegal_classifiers";

	String REPORT_DEFINITIONS_AT_TERMS = "definitions_at_terms";

	String REPORT_MISSING_SOURCE_REFS = "missing_source_refs";

	String REPORT_MULTIPLE_DEFINITIONS = "multiple_definitions";

	String REPORT_NOT_A_DEFINITION = "not_a_definition";

	String REPORT_DEFINITIONS_NOTES_MISMATCH = "definitions_notes_mismatch";

	String REPORT_MISSING_VALUE = "missing_value";

	String conceptGroupExp = "/mtf/conceptGrp";
	String langGroupExp = "languageGrp";
	String langExp = "language";
	String termGroupExp = "termGrp";
	String termExp = "term";
	String conceptExp = "concept";
	String domainExp = "descripGrp/descrip[@type='Valdkonnaviide']/xref[contains(@Tlink, 'Valdkond:')]";
	String subdomainExp = "descripGrp/descrip[@type='Alamvaldkond']";
	String sourceExp = "descripGrp/descrip[@type='Allikaviide']";
	String usageExp = "descripGrp/descrip[@type='Kontekst']";
	String definitionExp = "descripGrp/descrip[@type='Definitsioon']";
	String valueStateExp = "descripGrp/descrip[@type='Keelenditüüp']";
	String entryClassExp = "system[@type='entryClass']";
	String processStateExp = "descripGrp/descrip[@type='Staatus']";
	String meaningTypeExp = "descripGrp/descrip[@type='Mõistetüüp']";
	String createdByExp = "transacGrp/transac[@type='origination']";
	String createdOnExp = "transacGrp[transac/@type='origination']/date";
	String modifiedByExp = "transacGrp/transac[@type='modification']";
	String modifiedOnExp = "transacGrp[transac/@type='modification']/date";
	String ltbIdExp = "descripGrp/descrip[@type='ID-number']";
	String ltbSourceExp = "descripGrp/descrip[@type='Päritolu']";
	String noteExp = "descripGrp/descrip[@type='Märkus']";
	String privateNoteExp = "descripGrp/descrip[@type='Sisemärkus']";
	String listExp = "descripGrp/descrip[@type='Loend']";
	String imageExp = "descripGrp/descrip[@type='Joonis']";
	String meaningDomainExp = "descripGrp/descrip[@type='Valdkonnakood']";
	String overlapExp = "descripGrp/descrip[@type='Kattuvus']";
	String regionExp = "descripGrp/descrip[@type='Kasutus']";
	String meaningRelationExp = "descripGrp/descrip[@type='Seotud']";
	String explanationExp = "descripGrp/descrip[@type='Selgitus']";
	String unclassifiedExp = "descripGrp/descrip[@type='Tunnus']";
	String worksheetExp = "descripGrp/descrip[@type='Tööleht']";
	String ltbCreatedByExp = "descripGrp/descrip[@type='Sisestaja']";//concept
	String eõkkCreatedByExp = "descripGrp/descrip[@type='Autor']";//term
	String ltbEõkkCreatedOnExp = "descripGrp/descrip[@type='Sisestusaeg']";//concept, term
	String ltbEõkkModifiedByExp = "descripGrp/descrip[@type='Muutja']";//concept, term
	String ltbEõkkModifiedOnExp = "descripGrp/descrip[@type='Muutmisaeg']";//concept, term
	String etEnReviewedByExp = "descripGrp/descrip[@type='et-en kontrollija']";
	String etEnReviewedOnExp = "descripGrp/descrip[@type='et-en kontrollitud']";
	String enEtReviewedByExp = "descripGrp/descrip[@type='en-et kontrollija']";
	String enEtReviewedOnExp = "descripGrp/descrip[@type='en-et kontrollitud']";
	String xrefExp = "xref";

	String langTypeAttr = "type";
	String xrefTlinkAttr = "Tlink";

	String xrefTlinkSourcePrefix = "Allikas:";

	String refTypeExpert = "EKSPERT";
	String refTypeQuery = "PÄRING";

	String originLenoch = "lenoch";
	String originLtb = "ltb";

	char listingsDelimiter = '|';
}
