String.prototype.trunc = function(n, useWordBoundary) {
	if (this.length <= n) {
		return this;
	}
	var subString = this.substr(0, n - 1);
	return (useWordBoundary ? subString.substr(0, subString.lastIndexOf(' ')) : subString) + "&hellip;";
};

function initialise() {
	$(document).on("click", "#addDatasetSubmitBtn", function(e) {
		e.preventDefault();

		let thisForm = $("#addDatasetForm");
		let fieldsFilled = checkRequiredFields(thisForm)

		if (fieldsFilled) {
			checkAndAddDataset(thisForm);
		}
	});

	$(document).on("click", ".dataset-save-btn", function(e) {
		e.preventDefault();
		openWaitDlg("Palun oodake, sõnakogu salvestamine on pooleli");
		let thisForm = $(this).closest('form');
		thisForm.submit();
		closeWaitDlg();
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

	$('#addDatasetForm').find('input[name="code"]').on('blur', function(e) {
		$('#codeExistsError').hide();
	});

	$('.classifier-select').selectpicker({
		width : '100%'
	});

	$(document).on("change", ".dataset-origin-select", function(e) {
		var originCode = $(this).val();
		var domains = $(this).closest('form').find('[name="selectedDomains"]');
		let previousDomains = domains.val();
		domains.empty();

		domains.attr('disabled', originCode == '');

		if (originCode == '') {
			domains.selectpicker('refresh');
			return;
		}

		let getOriginDomainsUrl = applicationUrl + 'data/origin_domains/' + originCode;
		$.get(getOriginDomainsUrl).done(function(response) {

			var domainOrigins = JSON.parse(response);
			$.each(domainOrigins, function(index, domain) {
				let domainOptionText = domain.value;
				let domainJson = domain.jsonStr;

				if (domain.value != domain.code) {
					domainOptionText += ' [' + domain.code + ']';
				}
				domainOptionText = domainOptionText.trunc(100);
				let domainOption = $("<option></option>").attr("value", domainJson).text(domainOptionText);

				if (previousDomains != undefined && previousDomains.includes(domainJson)) {
					domainOption.attr("selected", "selected");
				}
				domains.append(domainOption);
			});

			domains.selectpicker('refresh');

		}).fail(function(response) {
			console.log(response);
			openAlertDlg("Päritolu valdkondade päring ebaõnnestus");
		});

	});

	$(document).on('show.bs.modal', ".edit-dataset-modal", function(e) {
		var datasetOrigin = $(e.relatedTarget).data('origin');
		if (datasetOrigin != undefined) {
			$(this).find('.dataset-origin-select').trigger('change');
		}
	});

	$(document).on('hide.bs.modal ', ".edit-dataset-dialog", function(e) {
		emptyClassifSelect($(this), "selectedLanguages");
		emptyClassifSelect($(this), "selectedProcessStates");
	});

	$(document).on('show.bs.modal', ".edit-dataset-dialog", function(e) {
		var datasetCode = $(e.relatedTarget).data('dataset-code');
		//alert(datasetCode);

		let fetchUrl = applicationUrl + 'dataset/' + datasetCode;
		let thisForm = $(this).find('form');

		$.get(fetchUrl).done(function(dataset) {
			console.log('dataset \n' + JSON.stringify(dataset));
			thisForm.find('input[name="code"]').val(dataset.code);
			thisForm.find('input[name="name"]').val(dataset.name);
			thisForm.find('textarea[name="description"]').val(dataset.description);
			thisForm.find('input[name="public"]').attr('checked', dataset.public);
			thisForm.find('input[name="visible"]').attr('checked', dataset.visible);

			markSelectedClassifiers(thisForm, "selectedLanguages", dataset.selectedLanguages);
			markSelectedClassifiers(thisForm, "selectedProcessStates", dataset.selectedProcessStates);

		}).fail(function(data) {
			openAlertDlg("Sõnakogu andmete päring ebaõnnestus.");
			console.log(data);
		});

	});

	$('.dataset-domain-select').selectpicker({
		width : '100%'
	});

}

function emptyClassifSelect(modal, classifSelectName) {
	let thisForm = modal.find('form');
	let classifSelect = thisForm.find('select[name="' + classifSelectName + '"]');
	classifSelect.find("option").each(function (o) {
		$(this).removeAttr("selected");
	});
}

function markSelectedClassifiers(form, classifSelectName, classifArray) {
	let classifSelect = form.find('select[name="' + classifSelectName + '"]'); //.val(JSON.stringify(dataset.selectedLanguages));

	$.each(classifArray, function (key, classif) {
		let classifOption = classifSelect.find("option[value='" + classif.jsonStr + "']");
		classifOption.attr("selected", "selected");
	});

	form.find('select[name="' + classifSelectName + '"]').selectpicker('refresh');
}

function isValidDatasetCodeFormat(code) {
	//don't allow spaces, tabls ? and %
	let pattern = /^((?!\?|\%)\S)*$/;
	return pattern.test(code);
}

function deleteDataset(datasetCode) {
	openWaitDlg("Palun oodake, sõnakogu kustutamine on pooleli");
	let deleteUrl = applicationUrl + 'delete_dataset/' + datasetCode;

	$.get(deleteUrl).done(function(data) {
		closeWaitDlg();
		if (data === 'OK') {
			window.location = applicationUrl + 'datasets';
		} else {
			openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		}
	}).fail(function(data) {
		closeWaitDlg();
		openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		console.log(data);
	});
}

function checkAndAddDataset(addDatasetForm) {
	let newCodeField = addDatasetForm.find('input[name="code"]');
	let validateUrl = applicationUrl + 'data/validate_create_dataset/' + newCodeField.val();

	if (!isValidDatasetCodeFormat(newCodeField.val())) {
		showFieldError(newCodeField, "Kood tohib sisaldada ainult tähti ja numbreid.");
		return;
	}

	$.get(validateUrl).done(function(data) {
		let responseCode = data;

		if (responseCode === 'OK') {
			openWaitDlg("Palun oodake, sõnakogu salvestamine on pooleli");
			addDatasetForm.submit();
			closeWaitDlg();
		} else if (responseCode === 'CODE_EXISTS') {
			showFieldError(newCodeField, "Sellise koodiga sõnakogu on olemas.");
		} else {
			openAlertDlg("Sõnakogu lisamine ebaõnnestus, veakood: '" + responseCode + "'");
		}
	}).fail(function(data) {
		openAlertDlg("Sõnakogu lisamine ebaõnnestus.");
		console.log(data);
	});
}
