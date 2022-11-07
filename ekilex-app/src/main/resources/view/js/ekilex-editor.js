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
	editFld.val(valueInput.val());

	const complexityBtns = editDlg.find('[name="complexity"]');
	if (complexityBtns.filter(':checked').length === 0){
		complexityBtns.eq(complexityBtns.length-1).prop('checked', true);
	}
	initCkEditor(editFld, editorOptions);

	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		if (editFld.val()) {
			let editFldValue = editFld.val();
			// Remove empty values, eki-selected class and empty elements.
			editFldValue = editFldValue
				.replace("<br>", "")
				.replaceAll("&nbsp;", " ")
				.replaceAll('class="eki-selected"', '')
				.replace(/<[^/>][^>]*><\/[^>]+>/gm, '');
			valueInput.val(editFldValue);
			editFld.removeClass('is-invalid');
			submitDialog(e, editDlg, messages["common.data.update.error"]);
		} else {
			e.preventDefault();
			editFld.addClass('is-invalid');
		}
	});
};
