$(document).on("change", "select.term-def-dataset-select[name='dataset']", function() {
	var datasetCode = $(this).val();
	var localForm = $(this).closest("form");
	var permLanguageSelect = localForm.find("select[name=language]")
	if (datasetCode) {
		var getLanguageSelectUrl = applicationUrl + "comp/termdeflangselect/" + datasetCode;
		$.get(getLanguageSelectUrl).done(function(data) {
			permLanguageSelect.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			alert('Viga!');
		});
	} else {
		permLanguageSelect.empty();
	}
});

//addLexemeDataDlg_ select
$(document).on("change", "select.lex-data-select[name='opCode']", function() {
	var opCode = $(this).val();
	var localForm = $(this).closest("form");
	localForm.find(".value-group").hide();
	var lexemeId = localForm.find("[name=id]").val();
	var dlgElemId = "#" + opCode + '_' + lexemeId;
	if (opCode.endsWith('Dlg')) {
		$(dlgElemId).modal("show");
		$("#addLexemeDataDlg_" + lexemeId).modal("hide");
	} else {
		$(dlgElemId).show();
	}
});

//addMeaningDataDlg_ select
$(document).on("change", "select.meaning-data-select[name='opCode']", function() {
	var opCode = $(this).val();
	var localForm = $(this).closest("form");
	localForm.find(".value-group").hide();
	var meaningId = localForm.find("[name=id2]").val();
	var dlgElemId = "#" + opCode + '_' + meaningId;
	if (opCode.endsWith('Dlg')) {
		$(dlgElemId).modal("show");
		$("#addMeaningDataDlg_" + meaningId).modal("hide");
	} else {
		$(dlgElemId).show();
	}
});

$(document).on("show.bs.modal", "[id^=addMeaningDataDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningPublicNoteDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=editMeaningPublicNoteDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningProcessLogDlg_]", function(e) {
	initEkiEditorDlg($(this));
	alignAndFocus(e, $(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningDomainDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addDefinitionSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLexemePublicNoteSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningPublicNoteSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemeRegionDlg_]", function() {
	initSelectDlg($(this));
});

