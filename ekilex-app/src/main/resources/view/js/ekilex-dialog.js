$(document).on("show.bs.modal", "[id^=addLexemeDataDlg_]", function() {
	initAddMultiDataDlg($(this));
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

$(document).on("show.bs.modal", "[id^=addLexemePublicNoteDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addLexemeProcessStateDlg]", function() {
	initGenericTextAddDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemePublicNoteDlg_]", function(e) {
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

$(document).on("show.bs.modal", "[id^=editLexemeProcessStateDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeValueStateCodeDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeGrammarDlg_]", function() {
	initGenericTextEditDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeFrequencyGroupDlg_]", function() {
	initSelectDlg($(this));
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

$(document).on("show.bs.modal", "[id^=addLearnerCommentDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=editLearnerCommentDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addImageSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addImageTitleDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=editImageTitleDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningSemanticTypeDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editMeaningSemanticTypeDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeSynProcessStateDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addWordProcessLogDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});