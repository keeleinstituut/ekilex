var viewType = '';

function initializeSourceSearch() {
	initDeleteConfirmations();
	viewType = 'source';
}

function initDeleteConfirmations() {
	$('[data-toggle=delete-source-confirm]').confirmation({
		btnOkLabel : messages["common.yes"],
		btnCancelLabel : messages["common.no"],
		title : messages["source.confirm.delete"],
		onConfirm: executeValidateSourceDelete
	});
}

$.fn.addSourceAndSourceLinkPlugin = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const modal = main.closest('.modal');
		modal.find("#noSourcesFoundDiv").hide();
		// Pre-fill short name with the non-existing search term
		modal.find("#add-source-modal-short-name").val(modal.find('#source-quick-search').val());
		modal.find("#addSourceDiv").show();
		// Hide duplicate modal footer
		modal.find('.modal-footer').last().hide();
	});
};

function createAndAttachCopyFromLastSourceItem(parentElement) {
	const copyOfLastElement = parentElement.clone(true);
	copyOfLastElement.find('textArea').val(null);
	parentElement.after(copyOfLastElement);
};

function executeValidateSourceDelete() {
	const sourceId = $(this).data('sourceId');
	const validateUrl = `${applicationUrl}validate_delete_source/${sourceId}`;
	const deleteUrl = `${applicationUrl}delete_source/${sourceId}`;
	$.get(validateUrl).done(function (response) {
		if (response.status === 'OK') {
			deleteSourceAndUpdateSearch(deleteUrl);
		} else if (response.status === 'INVALID') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg(messages["common.error"]);
		}
	}).fail(function (data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
};

function deleteSourceAndUpdateSearch(deleteUrl) {
	$.get(deleteUrl).done(function () {
		window.location.reload();
	}).fail(function (data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
};

function addSource(addSourceForm) {
	if (!checkRequiredFields(addSourceForm)) {
		return;
	}

	$.ajax({
		url: addSourceForm.attr('action'),
		data: JSON.stringify(addSourceForm.serializeJSON()),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(sourceId) {
		window.location = `${applicationUrl}sourceidsearch/${sourceId}`;
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}

function addSourceAndSourceLink(addSourceForm) {
	if (!checkRequiredFields(addSourceForm)) {
		return;
	}

	const sourceModal = addSourceForm.closest('.modal');
	const sourceLinkForm = addSourceForm.closest('form[name="sourceLinkForm"]');
	const sourceOwnerId = sourceLinkForm.find('input[name="id"]').val();
	const sourceOwnerName = sourceLinkForm.find('input[name="opCode"]').val();
	addSourceForm.find('input[name="sourceOwnerId"]').val(sourceOwnerId);
	addSourceForm.find('input[name="sourceOwnerName"]').val(sourceOwnerName);

	const successCallback = sourceModal.attr("data-callback");
	let successCallbackFunc = createCallback(successCallback);
	if (!successCallbackFunc) {
		successCallbackFunc = () => $('#refresh-details').trigger('click');
	}

	$.ajax({
		url: applicationUrl + 'create_source_and_source_link',
		data: JSON.stringify(addSourceForm.serializeJSON()),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function() {
		sourceModal.modal('hide');
		successCallbackFunc();
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}

$.fn.addSourceSubmitPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const addSourceForm = obj.closest('form');
			const nameField = addSourceForm.find('[name=name]');
			const nameError = document.getElementById('add-source-short-name-error');
			if (nameField.val()?.length > 50) {
				nameError.classList.remove('d-none');
				return;
			} else {
				nameError.classList.add('d-none');
			}
			const editFld = addSourceForm.find('[data-id="editFld"]');
			const valueInput = addSourceForm.find('[name=valuePrese]');
			let editFldValue = editFld.val();
			editFldValue = cleanEkiEditorValue(editFldValue);
			valueInput.val(editFldValue);
			if (viewType === 'source') {
				addSource(addSourceForm);
			} else {
				addSourceAndSourceLink(addSourceForm);
			}
		});
	});
}
