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
	dlg.find('select[name="preferredTagNames"]').selectpicker({
		width: '100%'
	});
}

function validateAndSubmitMeaningRelationPrefsForm(dlg) {
	let form = dlg.find('form');
	if (checkRequiredFields(form)) {
		form.trigger('submit');
	}
}

$.fn.selectMeaningRelationPrefsDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initSelectMeaningRelationPrefsDlg(obj);
		});
	});
}

$.fn.selectTagPrefsDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initSelectTagPrefsDlg(obj);
		});
	});
}

$.fn.reapplyPlugin = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const form = main.closest('form');
		if (checkRequiredFields(form)) {
			form.trigger('submit');
		}
	});
}