String.prototype.trunc = function(n, useWordBoundary) {
	if (this.length <= n) {
		return this;
	}
	var subString = this.substr(0, n - 1);
	return (useWordBoundary ? subString.substr(0, subString.lastIndexOf(' ')) : subString) + "&hellip;";
};


function initializeDatasets() {
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

	initClassifierAutocomplete();

	// selector by class does not work for some reason
	$(document).on("changed.bs.select", "#datasetEditOriginsSelect, #datasetAddOriginsSelect", function(e, clickedIndex, isSelected, oldValue) {
		let selectedOriginCode = $(this).find('option').eq(clickedIndex).val();
		let isOriginSelected = isSelected;

		let domainsSelect = $(this).closest('form').find('[name="domains"]');
		let previousDomainsValue = domainsSelect.val();

		if (isOriginSelected) {
			domainsSelect.attr('disabled', false);
			populateDomains(domainsSelect, selectedOriginCode, previousDomainsValue);

		} else {
			domainsSelect.find("option[data-subtext='" + selectedOriginCode + "']").remove();
			if ($(this).find(':selected').length == 0) {
				domainsSelect.attr('disabled', true);
			}
			domainsSelect.selectpicker('refresh');
		}
	});

	$(document).on('hide.bs.modal ', ".edit-dataset-dialog", function(e) {
		emptyClassifSelect($(this), "languages");
		emptyClassifSelect($(this), "origins");

		let domains = $(this).find('select[name="domains"]');
		emptyAndDisableSelect(domains);
	});

	$(document).on('show.bs.modal', ".edit-dataset-dialog", function(e) {
		var datasetCode = $(e.relatedTarget).data('dataset-code');
		let fetchUrl = applicationUrl + 'dataset/' + datasetCode;
		let thisForm = $(this).find('form');

		$.get(fetchUrl).done(function(dataset) {
		
			thisForm.find('input[name="code"]').val(dataset.code);
			thisForm.find('input[name="name"]').val(dataset.name);
			thisForm.find('select[name="type"]').val(dataset.type);
			thisForm.find('textarea[name="description"]').val(dataset.description);
			thisForm.find('textarea[name="contact"]').val(dataset.contact);
			thisForm.find('textarea[name="imageUrl"]').val(dataset.imageUrl);
			thisForm.find('input[name="public"]').attr('checked', dataset.public);
			thisForm.find('input[name="visible"]').attr('checked', dataset.visible);

			markSelectedClassifiers(thisForm, "languages", dataset.languages);
			markClassifierDomains(thisForm, dataset);

		}).fail(function(data) {
			openAlertDlg("Sõnakogu andmete päring ebaõnnestus.");
			console.log(data);
		});

	});

	$('.dataset-domain-select').selectpicker({width : '100%'});
	$('.dataset-origin-select').selectpicker();
}

function emptyClassifSelect(modal, classifSelectName) {
	let thisForm = modal.find('form');
	let classifSelect = thisForm.find('select[name="' + classifSelectName + '"]');
	classifSelect.find("option").each(function (o) {
		$(this).removeAttr("selected");
	});
	classifSelect.selectpicker('refresh');
};

function populateDomains(domainsSelect, originCode, previousDomainsValue) {
	let getOriginDomainsUrl = applicationUrl + 'origin_domains/' + originCode;
	$.get(getOriginDomainsUrl).done(function(response) {
		var domainOrigins = JSON.parse(response);

		$.each(domainOrigins, function(index, domain) {
			let domainOptionText = domain.value;
			let domainJson = domain.jsonStr;

			if (domain.value != domain.code) {
				domainOptionText += ' [' + domain.code + ']';
			}
			domainOptionText = domainOptionText.trunc(100);
			let domainOption = $("<option></option>")
				.attr("value", domainJson)
				.attr("data-subtext", originCode)
				.text(domainOptionText);

			if (previousDomainsValue != undefined && previousDomainsValue.includes(domainJson)) {
				domainOption.attr("selected", "selected");
			}
			domainsSelect.append(domainOption);
		});
		domainsSelect.selectpicker('refresh');
	}).fail(function(response) {
		console.log(response);
		openAlertDlg("Päritolu valdkondade päring ebaõnnestus");
	});
};

function markClassifierDomains(thisForm, dataset) {

	if (dataset.origins != null) {
		let domainsSelect = thisForm.find('select[name="domains"]');
		domainsSelect.attr("disabled", false);
		let originSelect = thisForm.find('select[name="origins"]');
		$.each(dataset.origins, function (key, origin) {
			let originOption = originSelect.find("option[value='" + origin + "']");
			originOption.attr("selected", "selected");

		});
		originSelect.selectpicker('refresh');

		let previousDomainsIds = dataset.domains.map(domain => domain.jsonStr);
		originSelect.find('option:selected').each(function () {
			let originCode = $(this).val();
			populateDomains(domainsSelect, originCode, previousDomainsIds);
		});
	}
};

function markSelectedClassifiers(form, classifSelectName, classifArray) {
	let classifSelect = form.find('select[name="' + classifSelectName + '"]');

	$.each(classifArray, function (key, classif) {
		let classifOption = classifSelect.find("option[value='" + classif.jsonStr + "']");
		classifOption.attr("selected", "selected");
	});

	classifSelect.selectpicker('refresh');
};

function emptyAndDisableSelect(selectCtl) {
	selectCtl.find("option").remove();
	selectCtl.attr('disabled', true);
	selectCtl.selectpicker('refresh');
};

function isValidDatasetCodeFormat(code) {
	//don't allow spaces, tabls ? and %
	let pattern = /^((?!\?|\%)\S)*$/;
	return pattern.test(code);
};

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
};

function checkAndAddDataset(addDatasetForm) {
	let newCodeField = addDatasetForm.find('input[name="code"]');
	let validateUrl = applicationUrl + 'validate_create_dataset/' + newCodeField.val();

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
};
