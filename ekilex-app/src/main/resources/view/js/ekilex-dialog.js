$(function() {
	$.fn.lexDataSelectPlugin = function() {
		return this.each(function() {
			const select = $(this);
			select.on('change', function() {
				const opCode = select.val();
				const localForm = select.closest("form");
				localForm.find(".value-group").hide();
				const lexemeId = localForm.find("[name=id]").val();
				const dlgElem = $(`#${opCode}_${lexemeId}`);
				if (opCode.endsWith('Dlg')) {
					dlgElem.modal("show");
					$("#addLexemeDataDlg_" + lexemeId).modal("hide");
				} else {
					dlgElem.show();
				}
			})
		})
	}

	$.fn.initAddMultiDataDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initAddMultiDataDlg(obj);
			})
		})
	}

	$.fn.initAddMultiDataDlgAndBindPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initAddMultiDataDlg(obj);
				$wpm.bindObjects($(this));
			})
		})
	}

	$.fn.initEkiEditorDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initEkiEditorDlg(obj);
			})
		})
	}

	$.fn.initEkiEditorDlgAndFocusPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initEkiEditorDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initAddSourceLinkWithAutocompletePlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initAddSourceLinkDlg(obj);
				initSourceNameAutocomplete(obj);
			})
		})
	}

	$.fn.initEditSourceLinkDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initEditSourceLinkDlg(obj);
			})
		})
	}

	$.fn.initMultiselectRelationDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initMultiselectRelationDlg(obj);
			})
		})
	}

	$.fn.initSelectDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initSelectDlg(obj);
			})
		})
	}

	$.fn.initGenericTextAddDlgAndFocusPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initGenericTextAddDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initGenericTextEditDlgAndFocusPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initGenericTextEditDlg(obj);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initGenericTextEditDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initGenericTextEditDlg(obj);
			})
		})
	}

	$.fn.initBasicWrappedEkiEditorDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('click', function(e) {
				initBasicWrappedEkiEditorOnContent(obj);
				e.stopImmediatePropagation();
			});
		});
	}
});