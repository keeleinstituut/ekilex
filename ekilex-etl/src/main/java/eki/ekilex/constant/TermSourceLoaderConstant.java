package eki.ekilex.constant;

public interface TermSourceLoaderConstant {

	String sourceConceptGroupExp = "/mtf/conceptGrp[languageGrp/language/@type='Allikas']";
	String conceptExp = "concept";
	String entryClassExp = "system[@type='entryClass']";
	String createdByExp = "transacGrp/transac[@type='origination']";
	String createdOnExp = "transacGrp[transac/@type='origination']/date";
	String modifiedByExp = "transacGrp/transac[@type='modification']";
	String modifiedOnExp = "transacGrp[transac/@type='modification']/date";
	/*
	 * Such field has never been found.
	 * Instead, source type is set programmatically.
	 * If the field will be valued in the future, some sort of mapping must be implemented.
	 */
	@Deprecated
	String sourceTypeExp = "descripGrp/descrip[@type='Tüüp']";
	String termGroupExp = "languageGrp/termGrp";
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
}
