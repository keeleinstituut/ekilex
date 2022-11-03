var viewType = '';

function initEditSourcePropertyDlg(editDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(editDlg);
};

function initAddSourcePropertyDlg(addDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(addDlg);
};

function initializeSourceSearch() {
	initDeleteConfirmations();
	viewType = 'source';
}

function initDeleteConfirmations() {
	$('[data-toggle=delete-source-property-confirm]').confirmation({
		btnOkLabel : messages["common.yes"],
		btnCancelLabel : messages["common.no"],
		title : messages["source.attribute.confirm.delete"],
		onConfirm: deleteSourceProperty
	});
	$('[data-toggle=delete-source-confirm]').confirmation({
		btnOkLabel : messages["common.yes"],
		btnCancelLabel : messages["common.no"],
		title : messages["source.confirm.delete"],
		onConfirm: executeValidateSourceDelete
	});
}

function validateAndSubmitAndUpdateSourcePropertyForm(dlg) {
	dlg.find('button[type="submit"]').off('click').on('click', function (e) {
		e.preventDefault();
		const form = dlg.find('form');
		if (!checkRequiredFields(form)) {
			return;
		}

		const sourceId = $(this).attr("data-source-id");
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function (data) {
			dlg.modal('hide');
			$(`#sourceSearchResult_${sourceId}`).replaceWith(data);
			initDeleteConfirmations();
			// Reattach plugins after elements change
			$wpm.bindObjects();
		}).fail(function (data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		});
	});
};

function deleteSourceProperty() {
	const sourcePropertyId = $(this).data('sourcePropertyId');
	const sourceId = $(this).data('sourceId');
	const deleteSourcePropertyUrl = `${applicationUrl}delete_source_property/${sourcePropertyId}`;
	$.get(deleteSourcePropertyUrl).done(function (data) {
		$(`#sourceSearchResult_${sourceId}`).replaceWith(data);
		initDeleteConfirmations();
	}).fail(function (data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
};

function initEditSourceTypeSelectDlg(selectDlg) {
	const selectControl = selectDlg.find('select');
	configureSelectDlg(selectControl, selectDlg);

	selectControl.off('click').on('click', function (e) {
		submitAndUpdateSourceType(selectDlg)
	});
	selectControl.off('keydown').on('keydown', function (e) {
		if (e.key === "Enter") {
			submitAndUpdateSourceType(selectDlg)
		}
	});
};

function submitAndUpdateSourceType(selectDlg) {
	const form = selectDlg.find('form');
	const sourceId = form.find('[name=sourceId]').val();
	$.ajax({
		url: form.attr('action'),
		data: form.serialize(),
		method: 'POST',
	}).done(function (data) {
		selectDlg.modal('hide');
		$(`#sourceSearchResult_${sourceId}`).replaceWith(data);
		initDeleteConfirmations();
		// Reattach plugins after change
		$wpm.bindObjects();
	}).fail(function (data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
};

$.fn.addSourceAndSourceLinkPlugin = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		$("#noSourcesFoundDiv").hide();
		$("#addSourceDiv").show();
		const duplicateCancelBtnFooter = main.closest('.modal-content').find('.modal-footer').last();
		duplicateCancelBtnFooter.hide();
		displayRemoveButtons();
	});
};

$.fn.addSourcePropertyGroup = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const sourcePropertyGroupElement = $("#source_property_element").find('[name="sourcePropertyGroup"]').last();
		createAndAttachCopyFromLastSourceItem(sourcePropertyGroupElement);
		displayRemoveButtons();
	});
};

$.fn.removePropertyGroupPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const btn = $(this);
			const elements = btn.closest('#source_property_element').find('[name="sourcePropertyGroup"]');
			// Do not remove unless there's more than one as otherwise the entire section is gone from that source
			if (elements.length > 1) {
				btn.closest('[name="sourcePropertyGroup"]').remove();
				displayRemoveButtons();
			}
		});
	});
}

function displayRemoveButtons() {
	$('[name="removePropertyGroupBtn"]').each(function() {
		if ($("#source_property_element").find('[name="propertyValue"]').length === 1) {
			$(this).hide();
		} else {
			$(this).show();
		}
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
		window.location = `${applicationUrl}sourcesearch/${sourceId}`;
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
	const addSourceLinkForm = addSourceForm.closest('form[name="sourceLinkForm"]');
	const sourceOwnerIdInput = addSourceLinkForm.find('input[name="id"]');
	const sourceOwnerCodeInput = addSourceLinkForm.find('input[name="opCode"]');

	addSourceForm.append(sourceOwnerIdInput);
	addSourceForm.append(sourceOwnerCodeInput);

	const successCallback = sourceModal.attr("data-callback");
	let successCallbackFunc = createCallback(successCallback);
	if (!successCallbackFunc) {
		successCallbackFunc = () => $('#refresh-details').click();
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
			if (viewType === 'source') {
				addSource(addSourceForm);
			} else {
				addSourceAndSourceLink(addSourceForm);
			}
		});
	});
}