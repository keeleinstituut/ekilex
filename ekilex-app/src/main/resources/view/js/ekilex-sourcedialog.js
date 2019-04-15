$(document).on("show.bs.modal", "[id^=editSourcePropertyDlg_]", function(e) {
	initEditSourcePropertyDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addSourcePropertyDlg_]", function(e) {
	initAddSourcePropertyDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=editSourceTypeDlg_]", function() {
	initEditSourceTypeSelectDlg($(this));
});