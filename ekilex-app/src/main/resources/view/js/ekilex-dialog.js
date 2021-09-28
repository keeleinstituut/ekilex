$(function() {

	$(document).on("show.bs.modal", "[id^=addLexemeDataDlg_]", function() {
		initAddMultiDataDlg($(this));
		$wpm.bindObjects($(this));
	});

	$(document).on("show.bs.modal", "[id^=addDefinitionDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editDefinitionDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeSourceLinkDlg_]", function() {
		initAddSourceLinkDlg($(this));
		initSourceNameAutocomplete($(this));
	});

	$(document).on("show.bs.modal", "[id^=addUsageDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editUsageDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeRelationDlg_]", function() {
		initMultiselectRelationDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordValueDlg_]", function(e) {
		initWordValueEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordGenderDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordTypeDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordLangDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemePublicityDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeValueStateCodeDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeGrammarDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemePosDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeDerivDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeRegisterDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningDomainDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningRelationDlg_]", function() {
		initMultiselectRelationDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addSynMeaningRelationDlg_]", function() {
		initMultiselectRelationDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningImageDlg_]", function(e) {
		initGenericTextAddDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningImageDlg_]", function(e) {
		initGenericTextEditDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addImageSourceLinkDlg_]", function() {
		initAddSourceLinkDlg($(this));
		initSourceNameAutocomplete($(this));
	});

	$(document).on("show.bs.modal", "[id^=addImageTitleDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editImageTitleDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningMediaDlg_]", function(e) {
		initGenericTextAddDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningMediaDlg_]", function(e) {
		initGenericTextEditDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningSemanticTypeDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningSemanticTypeDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLimTermMeaningNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editLimTermMeaningNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addDefinitionSourceLinkDlg_]", function() {
		initAddSourceLinkDlg($(this));
		initSourceNameAutocomplete($(this));
	});

	$(document).on("show.bs.modal", "[id^=addDefinitionNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editDefinitionNoteDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addDefinitionNoteSourceLinkDlg_]", function() {
		initAddSourceLinkDlg($(this));
		initSourceNameAutocomplete($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeTagDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editSynMeaningRelationWeightDlg_]", function() {
		initGenericTextEditDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editFreeformSourceLinkDlg_]", function() {
		initEditSourceLinkDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editDefinitionSourceLinkDlg_]", function() {
		initEditSourceLinkDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeSourceLinkDlg_]", function() {
		initEditSourceLinkDlg($(this));
	});

});