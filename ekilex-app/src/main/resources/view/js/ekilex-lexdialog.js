//component based interaction logic

$(document).on("change", "select[name='dataset']", function() {
	var datasetCode = $(this).val();
	if (datasetCode) {
		var getLanguageSelectUrl = applicationUrl + 'comp/langselect/' + datasetCode;
		$.get(getLanguageSelectUrl).done(function(data) {
			var permLanguageSelectArea = $('#permLanguageSelect');
			permLanguageSelectArea.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			alert('Viga!');
		});
	} else {
		$("#permLanguageSelect").empty();
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

$(document).on("change", "select.word-data-select[name='opCode']", function() {
	var opCode = $(this).val();
	var localForm = $(this).closest("form");
	localForm.find(".value-group").hide();
	var wordId = localForm.find("[name=id]").val();
	var dlgElemId = "#" + opCode + '_' + wordId;
	$(dlgElemId).show();
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

$(document).on("show.bs.modal", "[id^=addSourceLinkDlg_]", function() {
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

$(document).on("show.bs.modal", "[id^=addGovernmentDlg_]", function() {
	initGenericTextAddDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editGovernmentDlg_]", function() {
	initGenericTextEditDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageAuthorDlg_]", function() {
	initUsageAuthorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editUsageDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addUsageMemberDlg_]", function() {
	initUsageMemberDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editUsageTranslationDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editUsageDefinitionDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addPublicNoteDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editPublicNoteDlg_]", function() {
	initEkiEditorDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addLexemeRelationDlg_]", function() {
	initAddLexemeRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addWordDataDlg_]", function() {
	initAddMultiDataDlg($(this));
});

$(document).on("show.bs.modal", "[id^=addWordRelationDlg_]", function() {
	initAddWordRelationDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editWordGenderDlg_]", function() {
	initSelectDlg($(this));
});

$(document).on("show.bs.modal", "[id^=editWordAspectDlg_]", function() {
	initSelectDlg($(this));
});

