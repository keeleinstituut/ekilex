$(document).on("show.bs.modal", "#selectBilingCandidateLangDlg", function() {
	$(this).find(".classifier-select").selectpicker({
		width: '100%'
	});
});

$(document).on("show.bs.modal", "#selectBilingMeaningWordLangDlg", function() {
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

$(document).on("show.bs.modal", "[id^=editLexemeBilingProcessStateDlg_]", function() {
	initSelectDlg($(this));
});