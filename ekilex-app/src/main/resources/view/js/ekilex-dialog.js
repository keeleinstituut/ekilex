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


	$.fn.initEkiEditorDlgEtym = function() {
		const editorOptions = {
			height: '5em',
			toolbarGroups: [
				{
					name: 'eki-styles',
					groups: ['ekiStyles', 'ekiLink', 'cleanup', 'undo']
				}
			]
		}

		return this.each(function() {
			const obj = $(this);
			const container = obj.parents().find('.wordetym-card')
			obj.on('click', function(e) {
				initEkiEditorDlg(container, editorOptions);
			})
		})
	}

	$.fn.initLexWordValueEditorDlgAndFocusPlugin = function() {
		const editorOptions = {
			width: '100%',
			height: '5em',
			extraPlugins: 'ekiStyles',
			extraAllowedContent: '',
			removePlugins: 'sourcearea, elementspath',
			resize_enabled: false,
			toolbarGroups: [
				{
					name: 'eki-styles',
					groups: ['ekiStyles']
				}
			]
		};
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initEkiEditorDlg(obj, editorOptions);
				alignAndFocus(e, obj);
			})
		})
	}

	$.fn.initTermWordValueEditorDlgPlugin = function() {
		const editorOptions = {
			width: '100%',
			height: '5em',
			extraPlugins: 'ekiStyles',
			extraAllowedContent: '',
			removePlugins: 'sourcearea, elementspath',
			resize_enabled: false,
			toolbarGroups: [
				{
					name: 'eki-styles',
					groups: ['ekiStyles']
				}
			]
		};
		return this.each(function() {
			const editDlg = $(this);
			editDlg.on('show.bs.modal', function() {
				const backUri = getTermSearchBackUri();
				const uriParams = window.location.search;
				const backUriFld = editDlg.find('input[name="backUri"]');
				const uriParamsFld = editDlg.find('input[name="uriParams"]');
				const editFld = editDlg.find('[data-id="editFld"]');
				const valueInput = editDlg.find('[name=wordValuePrese]');
				editFld.val(valueInput.val());
				backUriFld.val(backUri);
				uriParamsFld.val(uriParams);
				initCkEditor(editFld, editorOptions);

				editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
					e.preventDefault();
					const submitBtn = $(this);
					if (editFld.val()) {
						const editFldValue = editFld.val();
						valueInput.val(editFldValue);
						const editWordForm = submitBtn.closest('form');
						const isValid = checkRequiredFields(editWordForm);
						if (isValid) {
							editWordForm.submit();
						}
					}
				});
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

	$.fn.initBasicInlineEkiEditorDlgOnClickPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('click', function(e) {
				initBasicInlineEkiEditorOnContent(obj);
				e.stopImmediatePropagation();
			});
		});
	}
});