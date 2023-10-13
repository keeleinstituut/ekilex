$.fn.initEditSourcePropertyDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function(e) {
			initEditSourcePropertyDlg(obj);
			alignAndFocus(e, obj);
		});
	});
}

$.fn.initAddSourcePropertyDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function(e) {
			initAddSourcePropertyDlg(obj);
			alignAndFocus(e, obj);
		});
	});
}

$.fn.editSourcePlugin = function() {
	return this.each(function() {
		const form = $(this);
		const sourceId = form.find('[name=sourceId]').val();
		form.on('submit', function(e) {
			e.preventDefault();
			const isValid = checkRequiredFields(form);
			if (!isValid) {
				return;
			}

			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function (data) {
				let dlg = form.parents('.modal');
				dlg.modal('hide');
				$(`#sourceSearchResult_${sourceId}`).replaceWith(data);
				initDeleteConfirmations();
				$wpm.bindObjects();
			}).fail(function (data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}