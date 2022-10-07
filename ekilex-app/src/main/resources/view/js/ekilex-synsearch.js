function doPostRelationChange(actionUrl, callbackFunc) {

	$.post(actionUrl).done(function(data) {
		if (data != '{}') {
			openAlertDlg(messages["common.error"]);
			console.log(data);
		}
		callbackFunc();
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
}

function refreshSynDetails() {
	const selectedWordId = $('#syn-details-area').data('id');
	const refreshButton = $(`[name="synDetailsBtn"][data-id="${selectedWordId}"]`).first();

	refreshButton.click();

}

function updateWordSynRelationsStatusDeleted() {

	const wordId = $(this).data('word-id');
	const actionUrl = `${applicationUrl}syn_relation_status/delete?wordId=${wordId}`;
	const callbackFunc = () => refreshSynDetails();

	doPostRelationChange(actionUrl, callbackFunc);
}

$.fn.updateSynTagCompletePlugin = function() {
	return this.each(function() {
		const button = $(this);
		button.on('click', function() {
			const wordId = button.data('word-id');
			const actionUrl = `${applicationUrl}update_word_active_tag_complete/${wordId}`;
			const callbackFunc = () => refreshSynDetails();
			doPostRelationChange(actionUrl, callbackFunc);
		})
	})
}

$.fn.changeSynRelationPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const status = obj.data('status');
			const id = obj.data('id');
			const actionUrl = `${applicationUrl}syn_relation_status?id=${id}&status=${status}`;
			const callbackFunc = () => refreshSynDetails();
			doPostRelationChange(actionUrl, callbackFunc);
		})
	});
}
