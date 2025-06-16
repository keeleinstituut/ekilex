$.fn.initAddSourcePlugin = function() {
	return this.each(function() {
		const form = $(this);
		const editFld = form.find('[data-id="editFld"]');
		initCkEditor(editFld);
	});
}

$.fn.editSourcePlugin = function() {
	return this.each(function() {
		const form = $(this);
		const sourceId = form.find('[name=id]').val();
		const editFld = form.find('[data-id="editFld"]');
		const valueInput = form.find('[name=valuePrese]');
		editFld.val(valueInput.val());
		initCkEditor(editFld);

		form.on('submit', function(e) {
			e.preventDefault();
			const isValid = checkRequiredFields(form);
			if (!isValid) {
				return;
			}

			const nameField = form.find('[name=name]');
			const nameError = form.find('[data-id=edit-source-short-name-error]');
			if (nameField.val()?.length > 50) {
				nameError.removeClass('d-none');
				return;
			} else {
				nameError.addClass('d-none');
			}

			let editFldValue = editFld.val();
			editFldValue = cleanEkiEditorValue(editFldValue);
			valueInput.val(editFldValue);

			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function (data) {
				let dlg = form.parents('.modal');
				dlg.modal('hide');
				$("#sourceDetails_" + sourceId).replaceWith(data);
				initDeleteConfirmations();
				$wpm.bindObjects();
			}).fail(function (data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}
