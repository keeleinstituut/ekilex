$(document).on("change", "select.term-def-dataset-select[name='dataset']", function() {
	var datasetCode = $(this).val();
	var permLanguageSelect = $("#definitionPermLanguageSelect");
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

$(document).on("show.bs.modal", "[id^=addLexemeDataDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addDefinitionDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editDefinitionDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLexemeSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editUsageDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLexemeRelationDlg_]", function() {
	initAddLexemeRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLexemePublicNoteDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLexemePublicNoteDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningDomainDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editMeaningDomainDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addDefinitionSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLearnerCommentDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editLearnerCommentDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addMeaningRelationDlg_]", function() {
	initAddMeaningRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addPublicNoteSourceLinkDlg_]", function() {
	initAddSourceLinkDlg($(this));
});

