$(document).on("show.bs.modal", "#selectMeaningRelationPrefsDlg", function() {
	initSelectMeaningRelationPrefsDlg($(this));
});

function initSelectMeaningRelationPrefsDlg(dlg) {
	dlg.find(".classifier-select").selectpicker({
		width: '100%'
	});

	dlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		validateAndSubmitMeaningRelationPrefsForm(dlg);
	});
}

function validateAndSubmitMeaningRelationPrefsForm(dlg) {
	let form = dlg.find('form');
	if (checkRequiredFields(form)) {
		form.submit();
	}
}