function initialise() {
	$(document).on("click", "#addDatasetSubmitBtn", function(e) {
		var thisForm = $("#addDatasetForm");
		let isValid = checkRequiredFields(thisForm);
		if (!isValid) {
			e.preventDefault();
			return;
		}
		thisForm.submit();
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

}

function deleteDataset(datasetCode) {
	let deleteUrl = applicationUrl + 'delete_dictionary/' + datasetCode;
	$.get(deleteUrl).done(function (data) {
		let response = JSON.parse(data);
		if (response.status === 'ok') {
			window.location = applicationUrl + 'dictionaries';
		} else if (response.status === 'invalid') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		}
	}).fail(function (data) {
		openAlertDlg("Allika eemaldamine ebaõnnestus.");
		console.log(data);
	});
}




