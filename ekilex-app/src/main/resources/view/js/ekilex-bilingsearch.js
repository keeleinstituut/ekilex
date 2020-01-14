$(document).on("click", "#updateBilingCandidateLangsSubmitBtn", function(e) {
	e.preventDefault();
	let dlg = $("#selectBilingCandidateLangDlg");
	validateAndSubmitLangSelectForm(dlg);
});

$(document).on("click", "#updateBilingMeaningWordLangsBtn", function(e) {
	e.preventDefault();
	let dlg = $("#selectBilingMeaningWordLangDlg");
	validateAndSubmitLangSelectForm(dlg);
});

function validateAndSubmitLangSelectForm(dlg) {
	let form = dlg.find('form');
	if (checkRequiredFields(form)) {
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function() {
			dlg.modal('hide');
			refreshDetails();
		}).fail(function(data) {
			dlg.modal('hide');
			console.log(data);
			openAlertDlg('Viga! Keele valik ebaõnnestus');
		});
	}
}