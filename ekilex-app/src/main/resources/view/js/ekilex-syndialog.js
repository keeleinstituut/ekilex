$(document).on("show.bs.modal", "[id^=addSynRelationDlg_]", function() {
	initAddSynRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeSynProcessStateDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addWordNoteDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=editWordNoteDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});