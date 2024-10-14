$.fn.duplicateLimTermMeaningPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const url = `${applicationUrl}meaningduplicate/${obj.data('meaning-id')}`;
			$.post(url).done(function(response) {
				if (response.status === "OK") {
					openMessageDlg(response.message);
					const duplicateMeaningId = response.id;
					setTimeout(function() {
						window.location = `${applicationUrl}limtermmeaningback/${duplicateMeaningId}`;
					}, 1500);
				} else {
					openAlertDlg(response.message);
				}
			}).fail(function(data) {
				openAlertDlg(messages["common.error"]);
				console.log(data);
			});
		});
	});
}

function initNewLimTermWordDlg() {
	const newWordDlg = $('#newLimTermWordDlg');
	newWordDlg.on('shown.bs.modal', function(e) {
		newWordDlg.find('.form-control').first().trigger('focus');
		newWordDlg.find('.form-control').each(function() {
			$(this).removeClass('is-invalid');
		});

		const searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (!searchValue.includes('*') && !searchValue.includes('?')) {
			newWordDlg.find('[name=wordValue]').val(searchValue);
		} else {
			newWordDlg.find('[name=wordValue]').val(null);
		}
		const meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});

	newWordDlg.find('.form-control').on('change', function() {
		const formControl = $(this);
		if (formControl.val()) {
			formControl.removeClass('is-invalid');
		} else {
			formControl.addClass('is-invalid');
		}
	});
}

$.fn.limTermWordFormSubmitPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const addWordForm = obj.closest('#addLimTermWordForm');
			const isValid = checkRequiredFields(addWordForm);
			if (!isValid) {
				return;
			}
			addWordForm.trigger('submit');
		});
	});
}