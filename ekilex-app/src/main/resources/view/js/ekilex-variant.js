$.fn.createWordVariantPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function(e) {
			e.preventDefault();

			const actionModal = btn.closest('.modal');
			const actionForm = btn.closest('form[name="createWordVariantForm"]');
			const successCallback = actionModal.attr("data-callback");
			let successCallbackFunc = createCallback(successCallback);
			const actionUrl = actionForm.attr('action');
			const headwordLexemeId = actionForm.find("input[name='headwordLexemeId']").val();

			const isValid = checkRequiredFields(actionForm);
			if (!isValid) {
				return;
			}

			let formData = actionForm.serialize();

			$.ajax({
				url: actionUrl,
				data: formData,
				method: 'POST'
			}).done(function(data) {
				if (data.status == 'OK') {
					actionModal.modal('hide');
					$('#select_word_variant_section_' + headwordLexemeId).empty();
					successCallbackFunc();
					openMessageDlg(data.message);
				} else if (data.status == 'INVALID') {
					actionModal.modal('hide');
					initWordVariantSelecthModal(headwordLexemeId, formData);
				}
				actionForm[0].reset();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}

function initWordVariantSelecthModal(headwordLexemeId, formData) {

	$('#selectWordVariantDlg_' + headwordLexemeId).modal('show');
	openWaitDlg();

	let actionUrl = applicationUrl + 'search_word_variant';

	$.ajax({
		url: actionUrl,
		data: formData,
		method: 'POST'
	}).done(function(data) {
		closeWaitDlg();
		$('#select_word_variant_section_' + headwordLexemeId).html(data);
		$wpm.bindObjects();
	}).fail(function(data) {
		console.log(data);
		closeWaitDlg();
		openAlertDlg(messages["common.error"]);
	});
}
