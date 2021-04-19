package eki.ekilex.constant;

public interface TermLoaderConstant {

	String sourceConceptGroupExp = "/mtf/conceptGrp[languageGrp/language/@type='Allikas']";
	/*
	 * Such field has never been found.
	 * Instead, source type is set programmatically.
	 * If the field will be valued in the future, some sort of mapping must be implemented.
	 */
	@Deprecated
	String sourceTypeExp = "descripGrp/descrip[@type='Tüüp']";
	String termValueExp = "term";
	String sourceLtbSourceExp = "descripGrp/descrip[@type='Päritolu']";
	String sourceRtExp = "descripGrp/descrip[@type='RT']";
	String sourceCelexExp = "descripGrp/descrip[@type='CELEX']";
	String sourceWwwExp = "descripGrp/descrip[@type='WWW']";
	String sourceAuthorExp = "descripGrp/descrip[@type='Autor']";
	String sourceIsbnExp = "descripGrp/descrip[@type='ISBN']";
	String sourceIssnExp = "descripGrp/descrip[@type='ISSN']";
	String sourcePublisherExp = "descripGrp/descrip[@type='Kirjastus']";
	String sourcePublicationYearExp = "descripGrp/descrip[@type='Ilmumisaasta']";
	String sourcePublicationPlaceExp = "descripGrp/descrip[@type='Ilmumiskoht']";
	String sourcePublicationNameExp = "descripGrp/descrip[@type='Väljaande nimi, nr']";
	String sourceNoteExp = "descripGrp/descrip[@type='Märkus']";
	String sourceExplanationExp = "descripGrp/descrip[@type='Selgitus']";
	String sourceArticleTitleExp = "descripGrp/descrip[@type='Artikli pealkiri']";
	String sourceArticleAuthorExp = "descripGrp/descrip[@type='Artikli autor']";

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
	String readinessProcessStateExp = "descripGrp/descrip[@type='Valmidus']";
	String createdByExp = "transacGrp/transac[@type='origination']";
	String createdOnExp = "transacGrp[transac/@type='origination']/date";
	String modifiedByExp = "transacGrp/transac[@type='modification']";
	String modifiedOnExp = "transacGrp[transac/@type='modification']/date";
	String legacyIdExp = "descripGrp/descrip[@type='id']";
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
	String originMiliterm = "militerm";
	String definitionTypeCodeDefinition = "definitsioon";
	String definitionTypeCodeExplanation = "selgitus";
	String processStateCodePublic = "avalik";

	String DEFAULT_DEFINITION_TYPE_CODE = "määramata";
	String UNIFIED_AFIXOID_SYMBOL = "-";
	String PREFIXOID_WORD_TYPE_CODE = "pf";
	String SUFFIXOID_WORD_TYPE_CODE = "sf";

	String EMPTY_CONTENT = "-";

	char listingsDelimiter = '|';
	char meaningDelimiter = ';';
	char tlinkDelimiter = ':';

	String CLASSIFIERS_MAPPING_FILE_PATH = "./fileresources/csv/classifier-main-map.csv";

	String EXT_SOURCE_ID_NA = "n/a";
	String EKI_CLASSIFIER_STAATUS = "staatus";
	String EKI_CLASSIFIER_MÕISTETÜÜP = "mõistetüüp";
	String EKI_CLASSIFIER_KEELENDITÜÜP = "keelenditüüp";
	String EKI_CLASSIFIER_LIIKTYYP = "liik_tyyp";
	String EKI_CLASSIFIER_DKTYYP = "dk_tyyp";
	String EKI_CLASSIFIER_SLTYYP = "sl_tyyp";
	String EKI_CLASSIFIER_ASTYYP = "as_tyyp";
	String EKI_CLASSIFIER_VKTYYP = "vk_tyyp";
	String EKI_CLASSIFIER_MSAGTYYP = "msag_tyyp";
	String EKI_CLASSIFIER_STYYP = "s_tyyp";
	String EKI_CLASSIFIER_ETYMPLTYYP = "etympl_tyyp";
	String EKI_CLASSIIFER_ETYMKEELTYYP = "etymkeel_tyyp";
	String EKI_CLASSIFIER_ENTRY_CLASS = "entry class";
	String EKI_CLASSIFIER_VALMIDUS = "valmidus";
}
