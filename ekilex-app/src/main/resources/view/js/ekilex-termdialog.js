$.fn.meaningDataSelectPlugin = function() {
	this.each(function() {
		const select = $(this);
		select.on('change', function() {
			const opCode = select.val();
			const localForm = select.closest("form");
			localForm.find(".value-group").hide();
			const meaningId = localForm.find("[name=id2]").val();
			const dlgElemId = "#" + opCode + '_' + meaningId;
			if (opCode.endsWith('Dlg')) {
				$(dlgElemId).modal("show");
				$("#addMeaningDataDlg_" + meaningId).modal("hide");
			} else {
				$(dlgElemId).show();
			}
		})
	})
}

$.fn.limTermMeaningDataSelectPlugin = function() {
	this.each(function() {
		const select = $(this);
		select.on('change', function() {
			const opCode = select.val();
			const localForm = select.closest("form");
			localForm.find(".value-group").hide();
			const meaningId = localForm.find("[name=id2]").val();
			const dlgElemId = "#" + opCode + '_' + meaningId;
			if (opCode.endsWith('Dlg')) {
				$(dlgElemId).modal("show");
				$("#addLimTermMeaningDataDlg_" + meaningId).modal("hide");
			} else {
				$(dlgElemId).show();
			}
		})
	})
}

//addLexemeDataDlg_ select

// $(document).on("change", "select.lex-data-select[name='opCode']", function() {
// 	var opCode = $(this).val();
// 	var localForm = $(this).closest("form");
// 	localForm.find(".value-group").hide();
// 	var lexemeId = localForm.find("[name=id]").val();
// 	var dlgElemId = "#" + opCode + '_' + lexemeId;
// 	if (opCode.endsWith('Dlg')) {
//     $(dlgElemId).modal("show");
// 		$("#addLexemeDataDlg_" + lexemeId).modal("hide");
// 	} else {
// 		$(dlgElemId).show();
// 	}
// });

//addMeaningDataDlg_ select
// $(document).on("change", "select.meaning-data-select[name='opCode']", function() {
// 	var opCode = $(this).val();
// 	var localForm = $(this).closest("form");
// 	localForm.find(".value-group").hide();
// 	var meaningId = localForm.find("[name=id2]").val();
// 	var dlgElemId = "#" + opCode + '_' + meaningId;
// 	if (opCode.endsWith('Dlg')) {
// 		$(dlgElemId).modal("show");
// 		$("#addMeaningDataDlg_" + meaningId).modal("hide");
// 	} else {
// 		$(dlgElemId).show();
// 	}
// });

// $(document).on("change", "select.lim-term-meaning-data-select[name='opCode']", function() {
// 	var opCode = $(this).val();
// 	var localForm = $(this).closest("form");
// 	localForm.find(".value-group").hide();
// 	var meaningId = localForm.find("[name=id2]").val();
// 	var dlgElemId = "#" + opCode + '_' + meaningId;
// 	if (opCode.endsWith('Dlg')) {
// 		$(dlgElemId).modal("show");
// 		$("#addLimTermMeaningDataDlg_" + meaningId).modal("hide");
// 	} else {
// 		$(dlgElemId).show();
// 	}
// });

// $(document).on("show.bs.modal", "[id^=addMeaningDataDlg_]", function() {
// 	initAddMultiDataDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=addLimTermMeaningDataDlg_]", function() {
// 	initAddMultiDataDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=addUsageSourceLinkDlg_]", function() {
// 	initAddSourceLinkDlg($(this));
// 	initSourceNameAutocomplete($(this));
// });

// $(document).on("show.bs.modal", "[id^=addMeaningDomainDlg_]", function() {
// 	initAddMultiDataDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=addLexemeNoteSourceLinkDlg_]", function() {
// 	initAddSourceLinkDlg($(this));
// 	initSourceNameAutocomplete($(this));
// });

// $(document).on("show.bs.modal", "[id^=addMeaningNoteSourceLinkDlg_]", function() {
// 	initAddSourceLinkDlg($(this));
// 	initSourceNameAutocomplete($(this));
// });

// $(document).on("show.bs.modal", "[id^=editLexemeRegionDlg_]", function() {
// 	initSelectDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=editLexemeReliabilityDlg_]", function() {
// 	initSelectDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=editMeaningManualEventOnDlg_]", function() {
// 	initGenericTextEditDlg($(this));
// });
