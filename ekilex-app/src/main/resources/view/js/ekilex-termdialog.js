$(document).on("show.bs.modal", "[id^=addMeaningDomainDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editMeaningDomainDlg_]", function() {
	initSelectDlg($(this));
});
