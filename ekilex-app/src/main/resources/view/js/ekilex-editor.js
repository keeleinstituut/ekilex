// Javascript methods for Ekilex custom editor component and dialogs are using it

function initUsageMemberDlg(dlg) {
	let usageMemberType = $('#usageMemberTypeSelect').find(':selected').val();
	toggleUsageMemberAdditionalFields(dlg, usageMemberType);

	dlg.find('[name=opCode]').off('change').on('change', function(e) {
		let usageMemberType = $(e.target).val();
		toggleUsageMemberAdditionalFields(dlg, usageMemberType);
	});
}

function toggleUsageMemberAdditionalFields(dlg, usageMemberType) {
	dlg.find('.usage-member-additional-fields').hide();
	dlg.find('[data-id=' + usageMemberType + ']').show();
}

function initEkiEditorDlg(editDlg, editorOptions) {
	let editFld = editDlg.find('[data-id="editFld"]');
	let valueInput = editDlg.find('[name=value]');
	let footer = editDlg.find('.modal-footer');
	let cancelBtn = footer.find('[data-dismiss=modal]');
	let errorText = messages["editor.error.add.note"];
	let errorTemplate = '<span class="error-text">' + errorText + '</span>';
	editFld.val(valueInput.val());

	const complexityBtns = editDlg.find('[name="complexity"]');
	if (complexityBtns.filter(':checked').length === 0){
		complexityBtns.eq(complexityBtns.length-1).prop('checked', true);
	}
	initCkEditor(editFld, editorOptions);

	cancelBtn.off('click').on('click', function(){
		if(errorTemplate) {
			footer.find('.error-text').remove();
		};
	});

	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		if (editFld.val()) {
			let editFldValue = editFld.val();
			editFldValue = cleanEkiEditorValue(editFldValue);
			valueInput.val(editFldValue);
			footer.find('.error-text').remove();
			editFld.removeClass('is-invalid');
			submitDialog(e, editDlg, messages["common.data.update.error"]);
		} else {
			e.preventDefault();
			editFld.addClass('is-invalid');
			footer.prepend(errorTemplate);
		}
	});
};

function cleanEkiEditorValue(editFldValue) {

	// Remove empty values, eki-selected class and empty elements.
	return editFldValue
		.replace("<br>", "")
		.replaceAll("&nbsp;", " ")
		.replaceAll('class="eki-selected"', '')
		.replace(/<[^/>][^>]*><\/[^>]+>/gm, '');
}
