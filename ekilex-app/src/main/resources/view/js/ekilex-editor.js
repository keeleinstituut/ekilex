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

function initEkiEditorDlg(editDlg) {
	let modifyFld = editDlg.find('[data-id="editFld"]');
	modifyFld.val(editDlg.find('[name=value]').val());

	const complexityBtns = editDlg.find('[name="complexity"]');
	if (complexityBtns.filter(':checked').length === 0){
		complexityBtns.eq(complexityBtns.length-1).prop('checked', true);
	}
	initCkEditor(modifyFld);

	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		if (modifyFld.val()) {
			let content = modifyFld.val();
			content = content.replace("<br>", "").replaceAll("&nbsp;", " ").replaceAll('class="eki-selected"', '');
			editDlg.find('[name=value]').val(content);
			modifyFld.removeClass('is-invalid');
			submitDialog(e, editDlg, messages["common.data.update.error"]);
		} else {
			e.preventDefault();
			modifyFld.addClass('is-invalid');
		}
	});
};
