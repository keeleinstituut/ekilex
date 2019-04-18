function initEditSourcePropertyDlg(editDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(editDlg);
}

function initAddSourcePropertyDlg(addDlg) {
	validateAndSubmitAndUpdateSourcePropertyForm(addDlg);
}

function validateAndSubmitAndUpdateSourcePropertyForm(dlg) {
	dlg.find('button[type="submit"]').off('click').on('click', function (e) {
		e.preventDefault();
		let form = dlg.find('form');
		if (!isSourcePropertyFormValid(form)) {
			return;
		}

		let sourceId = form.find('[name=sourceId]').val();
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function (data) {
			dlg.modal('hide');
			$('#sourceSearchResult_' + sourceId).replaceWith(data);
		}).fail(function (data) {
			console.log(data);
			openAlertDlg('Salvestamine ebaõnnestus');
		});
	});
}

function isSourcePropertyFormValid(form) {
	validateRequiredFormField(form, 'input', 'valueText');
	return form.find(".error-show").length == 0;
}

function validateRequiredFormField(form, type, fieldName) {
	var fieldElement = form.find(type + "[name=" + fieldName + "]");
	if (fieldElement.val() === null || fieldElement.val() == "") {
		fieldElement.siblings(".errors").find(".alert-danger").addClass("error-show");
	} else {
		fieldElement.siblings(".errors").find(".alert-danger").removeClass("error-show");
	}
}

function deleteSourceProperty(sourceId, sourcePropertyId, sourcePropertyType, count) {
	let deleteSourcePropertyUrl = applicationUrl + 'delete_source_property/' + sourceId + '/' + sourcePropertyId + '/' + sourcePropertyType + '/' + count;
	$.get(deleteSourcePropertyUrl).done(function (data) {
		$('#sourceSearchResult_' + sourceId).replaceWith(data);
	}).fail(function (data) {
		console.log(data);
		openAlertDlg('Kustutamine ebaõnnestus');
	});
}

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
}

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
	}).fail(function (data) {
		console.log(data);
		openAlertDlg('Muutmine ebaõnnestus');
	});
}

function initialiseAddNewProperty() {
	displayButtons();

	$(document).on("click", ":button[name='removePropertyGroupBtn']", function () {
		$(this).closest('[name="sourcePropertyGroup"]').remove();
		displayButtons();
	});

	$(document).on("click", ":button[name='addPropertyGroupBtn']", function () {
		let sourcePropertyGroupElement = $("#source_property_element").find('[name="sourcePropertyGroup"]').last();
		createAndAttachCopyFromLastItem(sourcePropertyGroupElement);
		displayButtons();
	});

}

function displayButtons() {
	$('[name="removePropertyGroupBtn"]').each(function (i, v) {
		if ($("#source_property_element").find('[name="propertyValue"]').length === 1) {
			$(this).hide();
		} else {
			$(this).show();
		}
	});
}

function createAndAttachCopyFromLastItem(parentElement) {
	let copyOfLastElement = parentElement.clone();
	copyOfLastElement.find('textArea').val(null);
	parentElement.after(copyOfLastElement);
}

$(document).on("click", "#addSourceSubmitBtn", function () {
	let form = $("#addSourceForm");
	if (!isNewSourceFormValid(form)) {
		return;
	}

	let sourceName = form.find('[name=sourceName]').val();
	$.ajax({
		url: form.attr('action'),
		data: form.serialize(),
		method: 'POST',
	}).done(function (data) {
		console.log(data);
		searchSourceByValue(sourceName);
	}).fail(function (data) {
		alert("viga");
		console.log(data);
		openAlertDlg('Allika lisamine ebaõnnestus');
	});
});

function searchSourceByValue(searchValue) {
	let form = $("#sourceSearchForm");
	form.find('[name=searchFilter]').val(searchValue);
	form.submit();
}

function isNewSourceFormValid(form) {
	validateRequiredFormField(form, 'input', 'sourceName');
	validateRequiredFormField(form, 'select', 'sourceType');
	return form.find(".error-show").length == 0;
}

function executeValidateSourceDelete(sourceId) {
	let validateUrl = applicationUrl + 'validate_source_delete/' + sourceId;
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
}

function deleteSourceAndUpdateSearch(deleteUrl) {
	$.get(deleteUrl).done(function (data) {
		let form = $("#sourceSearchForm");
		form.find('[name=searchFilter]').val();
		form.submit();
	}).fail(function (data) {
		openAlertDlg("Allika eemaldamine ebaõnnestus.");
		console.log(data);
	});
}

$(document).on('show.bs.modal', '#sourceLifecycleLogDlg', function(e) {
	var dlg = $(this);
	var link = $(e.relatedTarget);
	var url = link.attr('href');
	dlg.find('.close').focus();
	dlg.find('.modal-body').html(null);
	$.get(url).done(function(data) {
		dlg.find('.modal-body').html(data);
	});
});