function initNewLimTermWordDlg() {
	let newWordDlg = $('#newLimTermWordDlg');
	newWordDlg.on('shown.bs.modal', function(e) {
		newWordDlg.find('.form-control').first().focus();
		newWordDlg.find('.form-control').each(function() {
			$(this).removeClass('is-invalid');
		});
		let searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (!searchValue.includes('*') && !searchValue.includes('?')) {
			newWordDlg.find('[name=wordValue]').val(searchValue);
		} else {
			newWordDlg.find('[name=wordValue]').val(null);
		}
		let meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});

	newWordDlg.find('.form-control').on('change', function() {
		if ($(this).val()) {
			$(this).removeClass('is-invalid');
		} else {
			$(this).addClass('is-invalid');
		}
	});
	$(document).on("click", "#limTermWordFormSubmitBtn", function() {
		var addWordForm = $("#addLimTermWordForm");
		var isValid = checkRequiredFields(addWordForm);
		if (!isValid) {
			return;
		}
		addWordForm.submit();
	});
}