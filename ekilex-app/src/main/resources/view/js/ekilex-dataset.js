function initialise() {
	$(document).on("click", "#addDatasetSubmitBtn", function(e) {
		e.preventDefault();
		let thisForm = $("#addDatasetForm");
		let fieldsFilled = checkRequiredFields(thisForm)

		if (fieldsFilled) {
			checkAndAddDataset(thisForm);
		}
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

	$(document).on("change", ".dataset-origin-select", function(e) {
		var originCode = $(this).val();
		var domains = $(this).closest('form').find('[name="selectedDomains"]');

		let getOriginDomainsUrl = applicationUrl + 'data/origin_domains/' + originCode;
		$.get(getOriginDomainsUrl).done(function (response) {
			domains.empty();

			var domainOrigins = JSON.parse(response);
			$.each(domainOrigins, function (index, domain) {
				domains.append($("<option></option>")
					.attr("value", JSON.stringify(domain)).text(domain.value + '[' + domain.code + ']'));
			});

			domains.selectpicker('refresh');

		}).fail(function (response) {
			console.log(response);
			openAlertDlg("Päritolu valdkondade päring ebaõnnestus");
		});

	});

	$('.dataset-domain-select').selectpicker({width:'100%'});

	// 	//.selectpicker('refresh')
	// 	.selectpicker({width:'100%'});
	//TODO - label texts to messages.properties

	// $('.dataset-domain-select')
	// 	.selectpicker('refresh')
	// 	.selectpicker({width:'100%'})
	// 	.ajaxSelectPicker({
	// 		minLength : 3,
	// 		locale :  {
	// 			currentlySelected: 'Valitud',
	// 			emptyTitle: '** Tekst puudub **',
	// 			errorText: 'Viga otsingul',
	// 			searchPlaceholder: 'Sisesta otsitav valdkond...',
	// 			statusInitialized: '',
	// 			statusNoResults: 'Ei leitud midagi',
	// 			statusSearching: 'Oota...',
	// 			statusTooShort: 'Sisesta vähemalt 3 tähemärki'
	// 		},
	//
	// 		ajax: {
	// 			url: applicationUrl + 'data/search_domains',
	// 			type: 'POST',
	// 			dataType: 'json',
	// 			data: {
	// 				searchText: '{{{q}}}'
	// 			}
	// 		},
	// 		preprocessData: function (data) {
	// 			var i, l = data.length, dropdownValues = [];
	// 			if (l) {
	// 				for (i = 0; i < l; i++) {
	// 					dropdownValues.push($.extend(true, data[i], {
	// 						text : data[i].value,
	// 						value: JSON.stringify(data[i]),
	// 						data : {
	// 							subtext: data[i].origin
	// 						}
	// 					}));
	// 				}
	// 			}
	//
	// 			return dropdownValues;
	// 		}
	//
	// });
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


