$(document).on("show.bs.modal", "#selectMeaningRelationPrefsDlg", function() {
	initSelectMeaningRelationPrefsDlg($(this));
});

$(document).on("show.bs.modal", "#selectTagPrefsDlg", function() {
	initSelectTagPrefsDlg($(this));
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

function initSelectTagPrefsDlg(dlg) {
	dlg.find('select[name="searchableTags"]').selectpicker({
		width: '100%'
	});
}

function validateAndSubmitMeaningRelationPrefsForm(dlg) {
	let form = dlg.find('form');
	if (checkRequiredFields(form)) {
		form.submit();
	}
}