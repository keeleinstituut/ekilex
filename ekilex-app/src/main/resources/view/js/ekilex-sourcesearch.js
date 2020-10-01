function initEditSourcePropertyDlg(editDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(editDlg);
};

function initAddSourcePropertyDlg(addDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(addDlg);
};

function initDeleteConfirmations() {
	$('[data-toggle=delete-source-property-confirm]').confirmation({
		btnOkLabel: 'Jah',
		btnCancelLabel: 'Ei',
		title: 'Palun kinnita allika atribuudi kustutamine',
		onConfirm: deleteSourceProperty
	});
	$('[data-toggle=delete-source-confirm]').confirmation({
		btnOkLabel: 'Jah',
		btnCancelLabel: 'Ei',
		title: 'Palun kinnita allika kustutamine',
		onConfirm: executeValidateSourceDelete
	});
}

function validateAndSubmitAndUpdateSourcePropertyForm(dlg) {
	dlg.find('button[type="submit"]').off('click').on('click', function (e) {
		e.preventDefault();
		let form = dlg.find('form');
		if (!isSourcePropertyFormValid(form)) {
			return;
		}

		let sourceId = $(this).attr("data-source-id");
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function (data) {
			dlg.modal('hide');
			$('#sourceSearchResult_' + sourceId).replaceWith(data);
			initDeleteConfirmations();
		}).fail(function (data) {
			console.log(data);
			openAlertDlg('Salvestamine ebaõnnestus');
		});
	});
};

function isSourcePropertyFormValid(form) {
	validateRequiredSourceFormField(form, 'textarea', 'valueText');
	return form.find(".error-show").length == 0;
};

function validateRequiredSourceFormField(form, type, fieldName) {
	var fieldElement = form.find(type + "[name=" + fieldName + "]");
	if (fieldElement.val() === null || fieldElement.val() == "") {
		fieldElement.siblings(".errors").find(".alert-danger").addClass("error-show");
	} else {
		fieldElement.siblings(".errors").find(".alert-danger").removeClass("error-show");
	}
};

function deleteSourceProperty() {
	let sourcePropertyId = $(this).data('sourcePropertyId');
	let sourceId = $(this).data('sourceId');
	let deleteSourcePropertyUrl = applicationUrl + 'delete_source_property/' + sourcePropertyId;
	$.get(deleteSourcePropertyUrl).done(function (data) {
		$('#sourceSearchResult_' + sourceId).replaceWith(data);
		initDeleteConfirmations();
	}).fail(function (data) {
		console.log(data);
		openAlertDlg('Kustutamine ebaõnnestus');
	});
};

function initEditSourceTypeSelectDlg(selectDlg) {
	let selectControl = selectDlg.find('select');
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
	let form = selectDlg.find('form');
	let sourceId = form.find('[name=sourceId]').val();
	$.ajax({
		url: form.attr('action'),
		data: form.serialize(),
		method: 'POST',
	}).done(function (data) {
		selectDlg.modal('hide');
		$('#sourceSearchResult_' + sourceId).replaceWith(data);
		initDeleteConfirmations();
	}).fail(function (data) {
		console.log(data);
		openAlertDlg('Muutmine ebaõnnestus');
	});
};

function initialiseAddNewProperty() {
	displayButtons();

	$(document).on("click", ":button[name='removePropertyGroupBtn']", function () {
		$(this).closest('[name="sourcePropertyGroup"]').remove();
		displayButtons();
	});

	$(document).on("click", ":button[name='addPropertyGroupBtn']", function () {
		let sourcePropertyGroupElement = $("#source_property_element").find('[name="sourcePropertyGroup"]').last();
		createAndAttachCopyFromLastSourceItem(sourcePropertyGroupElement);
		displayButtons();
	});

};

function displayButtons() {
	$('[name="removePropertyGroupBtn"]').each(function (i, v) {
		if ($("#source_property_element").find('[name="propertyValue"]').length === 1) {
			$(this).hide();
		} else {
			$(this).show();
		}
	});
};

function createAndAttachCopyFromLastSourceItem(parentElement) {
	let copyOfLastElement = parentElement.clone();
	copyOfLastElement.find('textArea').val(null);
	parentElement.after(copyOfLastElement);
};

function isNewSourceFormValid(form) {
	validateRequiredSourceFormField(form, 'input', 'sourceName');
	validateRequiredSourceFormField(form, 'select', 'sourceType');
	return form.find(".error-show").length == 0;
};

function executeValidateSourceDelete() {
	let sourceId = $(this).data('sourceId');
	let validateUrl = applicationUrl + 'validate_delete_source/' + sourceId;
	let deleteUrl = applicationUrl + 'delete_source/' + sourceId;
	$.get(validateUrl).done(function (data) {
		let response = JSON.parse(data);
		if (response.status === 'ok') {
			deleteSourceAndUpdateSearch(deleteUrl);
		} else if (response.status === 'invalid') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg("Allika eemaldamine ebaõnnestus.");
		}
	}).fail(function (data) {
		openAlertDlg("Allika eemaldamine ebaõnnestus.");
		console.log(data);
	});
};

function deleteSourceAndUpdateSearch(deleteUrl) {
	$.get(deleteUrl).done(function (data) {
		$("#sourceSearchForm").submit();
	}).fail(function (data) {
		openAlertDlg("Allika eemaldamine ebaõnnestus.");
		console.log(data);
	});
};

$(function(){

	$(document).on('show.bs.modal', '#sourceActivityLogDlg', function(e) {
		var dlg = $(this);
		var link = $(e.relatedTarget);
		var url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});
	$(document).on("click", "#addSourceSubmitBtn", function () {
		let location = $(this).attr("data-location");
		let form = $("#addSourceForm");
		let dlg = $("#addSourceDlg");
		if (!isNewSourceFormValid(form)) {
			return;
		}

		$.ajax({
			url: form.attr('action'),
			data: JSON.stringify(form.serializeJSON()),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		}).done(function (sourceId) {
			if (location === "source_search") {
				window.location = applicationUrl + 'sourcesearch/' + sourceId;
			}
			if (location === "term_search" || location === "lex_search") {
				dlg.modal('hide');
				openMessageDlg("Allikas lisatud");
			}
		}).fail(function (data) {
			console.log(data);
			openAlertDlg('Allika lisamine ebaõnnestus');
		});
	});
});