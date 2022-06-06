String.prototype.trunc = function(n, useWordBoundary) {
	if (this.length <= n) {
		return this;
	}
	var subString = this.substr(0, n - 1);
	return (useWordBoundary ? subString.substr(0, subString.lastIndexOf(' ')) : subString) + "&hellip;";
};


function initializeDatasets() {

	$('#addDatasetForm').find('input[name="code"]').on('blur', function(e) {
		$('#codeExistsError').hide();
	});

	initClassifierAutocomplete();

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

$.fn.addDatasetSubmitPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function(e) {
			e.preventDefault();
			const thisForm = obj.closest("#addDatasetForm");
			const fieldsFilled = checkRequiredFields(thisForm);

			if (fieldsFilled) {
				checkAndAddDataset(thisForm);
			}
		});
	});
}

$.fn.saveDatasetPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function(e) {
			e.preventDefault();
			openWaitDlg("Palun oodake, sõnakogu salvestamine on pooleli");
			const thisForm = obj.closest('form');
			thisForm.submit();
		});
	});
}

$.fn.datasetOriginsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('changed.bs.select', function(e, clickedIndex, isSelected, oldValue) {
			const selectedOriginCode = obj.find('option').eq(clickedIndex).val();
			const isOriginSelected = isSelected;

			const domainsSelect = obj.closest('form').find('[name="domains"]');
			const previousDomainsValue = domainsSelect.val();

			if (isOriginSelected) {
				domainsSelect.attr('disabled', false);
				populateDomains(domainsSelect, selectedOriginCode, previousDomainsValue);

			} else {
				domainsSelect.find("option[data-subtext='" + selectedOriginCode + "']").remove();
				if (obj.find(':selected').length == 0) {
					domainsSelect.attr('disabled', true);
				}
				domainsSelect.selectpicker('refresh');
			}
		});
	});
}

$.fn.editDatasetDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('hide.bs.modal', function() {
			emptyClassifSelect(obj, "languages");
			emptyClassifSelect(obj, "origins");

			const domains = obj.find('select[name="domains"]');
			emptyAndDisableSelect(domains);
		});

		obj.on('show.bs.modal', function(e) {
			const datasetCode = $(e.relatedTarget).data('dataset-code');
			const fetchUrl = applicationUrl + 'dataset/' + datasetCode;
			const form = obj.find('form');

			$.get(fetchUrl).done(function(dataset) {
			
				form.find('input[name="code"]').val(dataset.code);
				form.find('input[name="name"]').val(dataset.name);
				form.find('select[name="type"]').val(dataset.type);
				form.find('textarea[name="description"]').val(dataset.description);
				form.find('textarea[name="contact"]').val(dataset.contact);
				form.find('textarea[name="imageUrl"]').val(dataset.imageUrl);
				form.find('input[name="public"]').attr('checked', dataset.public);
				form.find('input[name="visible"]').attr('checked', dataset.visible);
				markSelectedClassifiers(form, "languages", dataset.languages);
				markClassifierDomains(form, dataset);

			}).fail(function(data) {
				openAlertDlg("Sõnakogu andmete päring ebaõnnestus.");
				console.log(data);
			});
		});
	});
}

$.fn.deleteDatasetConfirmPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.confirmation({
			btnOkLabel : 'Jah',
			btnCancelLabel : 'Ei',
			title : 'Kas kustutame sõnakogu?',
			onConfirm : function() {
				const code = obj.data('code');
				deleteDataset(code);
			}
		});
	});
}