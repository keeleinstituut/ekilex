$.fn.collocMemberMovePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const collocMemberMoveForm = obj.closest('form');
			const collocMemberMoveModal = collocMemberMoveForm.closest('.modal');
			const actionUrl = collocMemberMoveForm.attr('action');
			const successCallback = collocMemberMoveModal.attr("data-callback");
			let successCallbackFunc = createCallback(successCallback);

			let collocLexemeIdArr = [];
			$.each($("input[name='collocLexemeIds']:checked"), function() {
				collocLexemeIdArr.push($(this).val());
			});
			let collocLexemeIds = collocLexemeIdArr.join(",");
			collocMemberMoveForm.find('input[name="collocLexemeIds"]').val(collocLexemeIds);

			$.ajax({
				url: actionUrl,
				data: collocMemberMoveForm.serialize(),
				method: 'POST'
			}).done(function() {
				collocMemberMoveModal.modal('hide');
				successCallbackFunc();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}
