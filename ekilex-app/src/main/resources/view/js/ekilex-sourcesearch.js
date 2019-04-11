$(function () {
	$('[data-toggle=delete-source-property-confirm]').confirmation({
		btnOkLabel: 'Jah',
		btnCancelLabel: 'Ei',
		title: 'Palun kinnita atribuudi kustutamine',
		onConfirm: function () {
			var sourceId = $(this).data('sourceId');
			var sourcePropertyId = $(this).data('sourcePropertyId');
			var count = $(this).data('count');
			deleteSourceProperty(sourceId, sourcePropertyId, count);
		}
	});
});

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
			alert('Salvestamine ebaõnnestus');
		});
	});
}

function isSourcePropertyFormValid(form) {
	validateRequiredFormField(form, 'valueText');
	return form.find(".error-show").length == 0;
}

function validateRequiredFormField(form, fieldName) {
	var fieldElement = form.find("input[name=" + fieldName + "]");
	if (fieldElement.val() == "") {
		fieldElement.siblings(".errors").find(".alert-danger").addClass("error-show");
	} else {
		fieldElement.siblings(".errors").find(".alert-danger").removeClass("error-show");
	}
}

function deleteSourceProperty(sourceId, sourcePropertyId, count) {
	let deleteSourcePropertyUrl = applicationUrl + 'delete_source_property/' + sourceId + '/' + sourcePropertyId + '/' + count;
	$.get(deleteSourcePropertyUrl).done(function (data) {
		$('#sourceSearchResult_' + sourceId).replaceWith(data);
	}).fail(function (data) {
		console.log(data);
		alert('Kustutamine ebaõnnestus');
	});
}
