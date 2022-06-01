$(function() {
	$.fn.lexDataSelectPlugin = function() {
		this.each(function() {
			const select = $(this);
			select.on('change', function() {
				const opCode = select.val();
				const localForm = select.closest("form");
				localForm.find(".value-group").hide();
				const lexemeId = localForm.find("[name=id]").val();
				const dlgElemId = "#" + opCode + '_' + lexemeId;
				if (opCode.endsWith('Dlg')) {
					$(dlgElemId).modal("show");
					$("#addLexemeDataDlg_" + lexemeId).modal("hide");
				} else {
					$(dlgElemId).show();
				}
			})
		})
	}

	$.fn.initAddMultiDataDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initAddMultiDataDlg(obj);
			})
		})
	}

	$.fn.initAddMultiDataDlgAndBindPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initAddMultiDataDlg(obj);
				$wpm.bindObjects($(this));
			})
		})
	}

	$.fn.initEkiEditorDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initEkiEditorDlg(obj);
			})
		})
	}

	$.fn.initEkiEditorDlgAndFocusPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initEkiEditorDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initAddSourceLinkWithAutocompletePlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initAddSourceLinkDlg(obj);
				initSourceNameAutocomplete(obj);
			})
		})
	}

	$.fn.initEditSourceLinkDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initEditSourceLinkDlg(obj);
			})
		})
	}

	$.fn.initMultiselectRelationDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initMultiselectRelationDlg(obj);
			})
		})
	}

	$.fn.initWordValueEditorDlgAndFocusPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initWordValueEditorDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initSelectDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initSelectDlg(obj);
			})
		})
	}

	$.fn.initGenericTextAddDlgAndFocusPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initGenericTextAddDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initGenericTextEditDlgAndFocusPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initGenericTextEditDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initGenericTextEditDlgPlugin = function() {
		this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initGenericTextEditDlg(obj);
			})
		})
	}

	// $(document).on("show.bs.modal", "[id^=addLexemeDataDlg_]", function() {
	// 	initAddMultiDataDlg($(this));
	// 	$wpm.bindObjects($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addDefinitionDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editDefinitionDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addLexemeSourceLinkDlg_]", function() {
	// 	initAddSourceLinkDlg($(this));
	// 	initSourceNameAutocomplete($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addUsageDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editUsageDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addLexemeRelationDlg_]", function() {
	// 	initMultiselectRelationDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addLexemeNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addWordNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordValueDlg_]", function(e) {
	// 	initWordValueEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordGenderDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordTypeDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordLangDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editWordDisplayMorphDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemePublicityDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeValueStateCodeDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeProficiencyLevelDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeGrammarDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemePosDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeDerivDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeRegisterDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editMeaningDomainDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningRelationDlg_]", function() {
	// 	initMultiselectRelationDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addSynMeaningRelationDlg_]", function() {
	// 	initMultiselectRelationDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningImageDlg_]", function(e) {
	// 	initGenericTextAddDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editMeaningImageDlg_]", function(e) {
	// 	initGenericTextEditDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addImageSourceLinkDlg_]", function() {
	// 	initAddSourceLinkDlg($(this));
	// 	initSourceNameAutocomplete($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addImageTitleDlg_]", function(e) {
	// 	initGenericTextAddDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editImageTitleDlg_]", function(e) {
	// 	initGenericTextEditDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningMediaDlg_]", function(e) {
	// 	initGenericTextAddDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editMeaningMediaDlg_]", function(e) {
	// 	initGenericTextEditDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningSemanticTypeDlg_]", function() {
	// 	initAddMultiDataDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editMeaningSemanticTypeDlg_]", function() {
	// 	initSelectDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addLimTermMeaningNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editMeaningNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLimTermMeaningNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addDefinitionSourceLinkDlg_]", function() {
	// 	initAddSourceLinkDlg($(this));
	// 	initSourceNameAutocomplete($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addDefinitionNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editDefinitionNoteDlg_]", function(e) {
	// 	initEkiEditorDlg($(this));
	// 	alignAndFocus(e, $(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addDefinitionNoteSourceLinkDlg_]", function() {
	// 	initAddSourceLinkDlg($(this));
	// 	initSourceNameAutocomplete($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addLexemeTagDlg_]", function() {
	// 	initAddMultiDataDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=addMeaningTagDlg_]", function() {
	// 	initAddMultiDataDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editSynMeaningRelationWeightDlg_]", function() {
	// 	initGenericTextEditDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editFreeformSourceLinkDlg_]", function() {
	// 	initEditSourceLinkDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editDefinitionSourceLinkDlg_]", function() {
	// 	initEditSourceLinkDlg($(this));
	// });

	// $(document).on("show.bs.modal", "[id^=editLexemeSourceLinkDlg_]", function() {
	// 	initEditSourceLinkDlg($(this));
	// });

});