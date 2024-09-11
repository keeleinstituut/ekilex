String.prototype.trunc = function(n, useWordBoundary) {
	if (this.length <= n) {
		return this;
	}
	// Using just n as substring is up to but not including
	const subString = this.substring(0, n);
	return (useWordBoundary ? subString.substring(0, subString.lastIndexOf(' ')) : subString) + "&hellip;";
};


function initializeDatasets() {

	$('#addDatasetForm').find('input[name="code"]').on('blur', function(e) {
		$('#codeExistsError').hide();
	});

	initClassifierAutocomplete();

	$('.dataset-domain-select').selectpicker({ width: '100%' });
	$('.dataset-origin-select').selectpicker();
}

function populateDomains(domainsSelect, originCode, previousDomainsValue) {
	const getOriginDomainsUrl = applicationUrl + 'origin_domains/' + originCode;
	$.get(getOriginDomainsUrl).done(function(response) {
		const domainOrigins = JSON.parse(response);

		$.each(domainOrigins, function(index, domain) {
			const domainJson = domain.jsonStr;
			let domainOptionText = domain.value;

			if (domain.value != domain.code) {
				domainOptionText += ` [${domain.code}]`;
			}
			domainOptionText = domainOptionText.trunc(100);
			const domainOption = $("<option></option>")
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
		openAlertDlg(messages["common.error"]);
	});
};

function markClassifierDomains(form, dataset) {

	if (dataset.origins != null) {
		const domainsSelect = form.find('select[name="domains"]');
		const originSelect = form.find('select[name="origins"]');
		domainsSelect.attr("disabled", false);
		$.each(dataset.origins, function(key, origin) {
			let originOption = originSelect.find("option[value='" + origin + "']");
			originOption.attr("selected", "selected");

		});
		originSelect.selectpicker('refresh');

		const previousDomainsIds = dataset.domains.map(domain => domain.jsonStr);
		originSelect.find('option:selected').each(function() {
			const originCode = $(this).val();
			populateDomains(domainsSelect, originCode, previousDomainsIds);
		});
	}
};

function markSelectedClassifiers(form, classifSelectName, classifArray) {
	const classifSelect = form.find('select[name="' + classifSelectName + '"]');
	$.each(classifArray, function(key, classif) {
		const classifOption = classifSelect.find("option[value='" + classif.jsonStr + "']");
		classifOption.attr("selected", "selected");
	});
	classifSelect.selectpicker('refresh');
};

function emptyClassifSelect(modal, classifSelectName) {
	const form = modal.find('form');
	const classifSelect = form.find(`select[name=${classifSelectName}]`);
	classifSelect.find("option").each(function() {
		$(this).removeAttr("selected");
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
	openWaitDlg(messages["common.please.wait"]);
	let deleteUrl = `${applicationUrl}delete_dataset/${datasetCode}`;

	$.get(deleteUrl).done(function(data) {
		closeWaitDlg();
		if (data === 'OK') {
			window.location = applicationUrl + 'datasets';
		} else {
			openAlertDlg(messages["common.error"]);
		}
	}).fail(function(data) {
		closeWaitDlg();
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
};

function checkAndAddDataset(addDatasetForm) {
	const newCodeField = addDatasetForm.find('input[name="code"]');
	const validateUrl = applicationUrl + 'validate_create_dataset/' + newCodeField.val();

	if (!isValidDatasetCodeFormat(newCodeField.val())) {
		showFieldError(newCodeField, messages["datasets.code.format.validation"]);
		return;
	}

	$.get(validateUrl).done(function(data) {
		const responseCode = data;

		if (responseCode === 'OK') {
			openWaitDlg(messages["common.please.wait"]);
			addDatasetForm.submit();
			closeWaitDlg();
		} else if (responseCode === 'CODE_EXISTS') {
			showFieldError(newCodeField, messages["datasets.code.exists.validation"]);
		} else {
			openAlertDlg(messages["common.error"]);
		}
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
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
			openWaitDlg(messages["common.please.wait"]);
			const form = obj.closest('form');
			form.submit();
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
				domainsSelect.find(`option[data-subtext=${selectedOriginCode}]`).remove();
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
			emptyClassifSelect(obj, "wordFreeformTypes");
			emptyClassifSelect(obj, "lexemeFreeformTypes");
			emptyClassifSelect(obj, "meaningFreeformTypes");
			emptyClassifSelect(obj, "definitionFreeformTypes");
			emptyClassifSelect(obj, "origins");
			const domains = obj.find('select[name="domains"]');
			emptyAndDisableSelect(domains);
		});

		obj.on('show.bs.modal', function(e) {

			const datasetCode = $(e.relatedTarget).data('dataset-code');
			const fetchUrl = `${applicationUrl}dataset/${datasetCode}`;
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
				markSelectedClassifiers(form, "wordFreeformTypes", dataset.wordFreeformTypes);
				markSelectedClassifiers(form, "lexemeFreeformTypes", dataset.lexemeFreeformTypes);
				markSelectedClassifiers(form, "meaningFreeformTypes", dataset.meaningFreeformTypes);
				markSelectedClassifiers(form, "definitionFreeformTypes", dataset.definitionFreeformTypes);
				markClassifierDomains(form, dataset);

			}).fail(function(data) {
				openAlertDlg(messages["common.error"]);
				console.log(data);
			});
		});
	});
}

$.fn.deleteDatasetConfirmPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.confirmation({
			btnOkLabel: messages["common.yes"],
			btnCancelLabel: messages["common.no"],
			title: messages["common.confirm.delete"],
			onConfirm: function() {
				const code = obj.data('code');
				deleteDataset(code);
			}
		});
	});
}