$(document).on("show.bs.modal", "[id^=editSourcePropertyDlg_]", function() {
	initEditSourcePropertyDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addSourcePropertyDlg_]", function() {
	initAddSourcePropertyDlg($(this));
});