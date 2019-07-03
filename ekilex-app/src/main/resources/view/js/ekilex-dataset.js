String.prototype.trunc =
	function( n, useWordBoundary ){
		if (this.length <= n) { return this; }
		var subString = this.substr(0, n-1);
		return (useWordBoundary
			? subString.substr(0, subString.lastIndexOf(' '))
			: subString) + "&hellip;";
	};

function initialise() {
	$(document).on("click", "#addDatasetSubmitBtn", function(e) {
		e.preventDefault();
		openWaitDlg("Palun oodake, sõnakogu salvestamine on pooleli");
		let thisForm = $("#addDatasetForm");
		let fieldsFilled = checkRequiredFields(thisForm)

		if (fieldsFilled) {
			checkAndAddDataset(thisForm);
		}
		closeWaitDlg();
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

	$('#addDatasetForm').find('input[name="code"]').on('blur', function (e) {
		$('#codeExistsError').hide();
	});

	$('.classifier-select').selectpicker({width:'100%'});

	$(document).on("click", ".dataset-domain-select", function(e) {
		//TODO - clear domain
	});

	$(document).on("change", ".dataset-origin-select", function(e) {
		var originCode = $(this).val();
		var domains = $(this).closest('form').find('[name="selectedDomains"]');
		domains.empty();

		domains.attr('disabled', originCode == '');

		if (originCode == '') {
			domains.selectpicker('refresh');
			return;
		}

		let getOriginDomainsUrl = applicationUrl + 'data/origin_domains/' + originCode;
		$.get(getOriginDomainsUrl).done(function (response) {

			var domainOrigins = JSON.parse(response);
			$.each(domainOrigins, function (index, domain) {
				let domainOptionText = domain.value;
				if (domain.value != domain.code) {
					domainOptionText += ' [' + domain.code + ']';
				}
				domainOptionText = domainOptionText.trunc(100);
				domains.append(
					$("<option></option>")
						.attr("value", JSON.stringify(domain))
						.text(domainOptionText));
			});

			domains.selectpicker('refresh');

		}).fail(function (response) {
			console.log(response);
			openAlertDlg("Päritolu valdkondade päring ebaõnnestus");
		});

	});

	$('.dataset-domain-select').selectpicker({width:'100%'});


}
function isValidDatasetCodeFormat(code) {
	//don't allow spaces, tabls ? and %
	let pattern = /^((?!\?|\%)\S)*$/;
	return pattern.test(code);
}

function deleteDataset(datasetCode) {
	openWaitDlg("Palun oodake, sõnakogu kustutamine on pooleli");
	let deleteUrl = applicationUrl + 'delete_dictionary/' + datasetCode;

	$.get(deleteUrl).done(function (data) {
		closeWaitDlg();
		if (data === 'OK') {
			window.location = applicationUrl + 'dictionaries';
		} else {
			openAlertDlg("Sõnakogu eemaldamine ebaõnnestus.");
		}
	}).fail(function (data) {
		closeWaitDlg();
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


