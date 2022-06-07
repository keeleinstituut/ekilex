function initializeMeaningRelSelect() {
	let relationTypeSelect = $("select[name='relationType']");
	changeOppositeRelationSelectData(relationTypeSelect);
	checkEnableSubmitButtons();

	// Moved to bottom of file as plugin
	// $('#chkNoRelation').change(function() {
	// 	if (this.checked) {
	// 		hideRelationSelect();
	// 	} else {
	// 		showRelationSelect();
	// 	}
	// });

	// Moved to common as changeOppositeRelationSelectDataPlugin
	// $(document).on("change", "select[name='relationType']", function() {
	// 	let relationTypeSelect = $(this);
	// 	changeOppositeRelationSelectData(relationTypeSelect);
	// });

	// Moved to bottom of file as plugin
	// $(document).on("click", "[name='relatedMeaningId']", function() {
	// 	checkEnableSubmitButtons();
	// });

	// Moved to bottom of file as plugin
	// $("#submitDiv").find("button").click(function() {
	// 	submitFormMeaning();
	// });

	// Moved to bottom of file as plugin
	// $("#submitWithRelationDiv").find("button").click(function() {
	// 	let createWordForm = $("#createWordForm");
	// 	if (checkRequiredFields(createWordForm)) {
	// 		createWordForm.find('select[name="oppositeRelationType"]').prop('disabled', false);
	// 		if ($(this).attr("name") === "importDataBtn") {
	// 			createWordForm.find('input[name="importMeaningData"]').val("true");
	// 			validateMeaningDataImportAndSubmitForm();
	// 		} else {
	// 			submitFormMeaning();
	// 		}
	// 	}
	// });
};

function validateMeaningDataImportAndSubmitForm() {
	let failMessage = "Termini loomine ja mõiste andmete kopeerimine ebaõnnestus. Kontrolli, et mõistel ei oleks samakujulisi erineva sõnakogu termineid";
	let importMeaningDataInput = $("#createWordForm").find('input[name="importMeaningData"]');
	let meaningId = $('input[name="relatedMeaningId"]:checked').val();
	let validateMeaningDataImportUrl = applicationUrl + "validatemeaningdataimport/" + meaningId;

	$.get(validateMeaningDataImportUrl).done(function(response) {
		if (response === "OK") {
			submitFormMeaning();
		} else {
			console.log(response);
			openAlertDlg(failMessage);
			importMeaningDataInput.val("false");
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
		importMeaningDataInput.val("false");
	});
};

function submitFormMeaning() {
	let createWordForm = $("#createWordForm");
	let createRelation = !$("#chkNoRelation").is(":checked");
	createWordForm.find('input[name="createRelation"]').val(createRelation);
	let failMessage = "Viga! Termini loomine ebaõnnestus";

	$.ajax({
		url: createWordForm.attr('action'),
		data: JSON.stringify(createWordForm.serializeJSON()),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(response) {
		if (response.status === 'VALID') {
			let searchUri = response.uri;
			window.location = applicationUrl + 'termsearch' + searchUri;
		} else if (response.status === 'INVALID') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg(failMessage);
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
};

function hideRelationSelect() {
	$('#submitDiv').show();
	$('#submitWithRelationDiv').hide();
	$('#meanings').hide();
	$('#relationTypes').hide();
	$('#oppositeRelationTypes').hide();
};

function showRelationSelect() {
	$('#submitDiv').hide();
	$('#submitWithRelationDiv').show();
	$('#meanings').show();
	$('#relationTypes').show();
	let relationTypeSelect = $("select[name='relationType']");
	changeOppositeRelationSelectData(relationTypeSelect);
};

function checkEnableSubmitButtons() {
	if ($('input[name="relatedMeaningId"]:checked').length === 1) {
		$("#submitWithRelationDiv").find("button").removeAttr("disabled");
	}
};

$.fn.chkNoRelationPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('change', function() {
			if (this.checked) {
				hideRelationSelect();
			} else {
				showRelationSelect();
			}
		});
	});
}

$.fn.checkEnableSubmitButtonsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			checkEnableSubmitButtons();
		});
	});
}

$.fn.submitFormMeaningPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			submitFormMeaning()
		});
	});
}

$.fn.submitWithRelationPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const createWordForm = obj.closest('#createWordForm');
			if (checkRequiredFields(createWordForm)) {
				createWordForm.find('select[name="oppositeRelationType"]').prop('disabled', false);
				if (obj.attr("name") === "importDataBtn") {
					createWordForm.find('input[name="importMeaningData"]').val("true");
					validateMeaningDataImportAndSubmitForm();
				} else {
					submitFormMeaning();
				}
			}
		});
	});
}