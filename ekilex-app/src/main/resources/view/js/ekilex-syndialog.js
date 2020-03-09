$(document).on("show.bs.modal", "#selectSynCandidateLangDlg", function() {
	$(this).find(".classifier-select").selectpicker({
		width: '100%'
	});
});

$(document).on("show.bs.modal", "#selectSynMeaningWordLangDlg", function() {
	$(this).find(".classifier-select").selectpicker({
		width: '100%'
	});
});

$(document).on("show.bs.modal", "[id^=addSynRelationDlg_]", function() {
	initAddSynRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editSynLexemeWeightDlg_]", function() {
	initGenericTextEditDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeSynProcessStateDlg_]", function() {
	initSelectDlg($(this));
});