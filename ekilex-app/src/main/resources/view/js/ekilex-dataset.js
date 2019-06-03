function initialise() {
	$(document).on("click", "#addDatasetSubmitBtn", function(e) {
		e.preventDefault();
		let thisForm = $("#addDatasetForm");

		let fieldsFilled = checkRequiredFields(thisForm)

		if (fieldsFilled) {
			checkAndAddDataset(thisForm);
		}
	});

	$('.delete-dataset-confirm').confirmation({
		btnOkLabel : 'Jah',
		btnCancelLabel : 'Ei',
		title : 'Kas kustutame sõnakogu?',
		onConfirm : function() {
			let code = $(this).data('code');
			deleteDataset(code);
		}
	});

	$('#addDatasetForm').find('input[name="code"]').on('blur', function (e) {
		$('#codeExistsError').hide();
	})

}
function isValidDatasetCodeFormat(code) {
	//don't allow spaces, tabls ? and %
	let pattern = /^((?!\?|\%)\S)*$/;
	return pattern.test(code);
}

function deleteDataset(datasetCode) {
	let deleteUrl = applicationUrl + 'delete_dictionary/' + datasetCode;

	$.get(deleteUrl).done(function (data) {
		if (data === 'OK') {
			window.location = applicationUrl + 'dictionaries';
		} else {
			openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		}
	}).fail(function (data) {
		openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		console.log(data);
	});
}

function checkAndAddDataset(addDatasetForm) {
	let newCodeField = addDatasetForm.find('input[name="code"]');
	let validateUrl = applicationUrl + 'data/validate_create_dictionary/' + newCodeField.val();

	if (!isValidDatasetCodeFormat(newCodeField.val())) {
		showFieldError(newCodeField, "Kood tohib sisaldada ainult tähti ja numbreid.");
		return;
	}

	$.get(validateUrl).done(function (data) {
		let responseCode = data;

		if (responseCode === 'OK') {
			addDatasetForm.submit();
		} else if (responseCode === 'CODE_EXISTS') {
			showFieldError(newCodeField, "Sellise koodiga sõnakogu on olemas.");
		} else {
			openAlertDlg("Sõnakogu lisamine ebaõnnestus, veakood: '" + responseCode + "'");
		}
	}).fail(function (data) {
		openAlertDlg("Sõnakogu lisamine ebaõnnestus.");
		console.log(data);
	});

}


