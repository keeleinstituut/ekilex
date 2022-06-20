function initializeMeaningRelSelect() {
	const relationTypeSelect = $("select[name='relationType']");
	changeOppositeRelationSelectData(relationTypeSelect);
	checkEnableSubmitButtons();
};

function validateMeaningDataImportAndSubmitForm() {
	const failMessage = "Termini loomine ja mõiste andmete kopeerimine ebaõnnestus. Kontrolli, et mõistel ei oleks samakujulisi erineva sõnakogu termineid";
	const importMeaningDataInput = $("#createWordForm").find('input[name="importMeaningData"]');
	const meaningId = $('input[name="relatedMeaningId"]:checked').val();
	const validateMeaningDataImportUrl = `${applicationUrl}validatemeaningdataimport/${meaningId}`;

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
	const createWordForm = $("#createWordForm");
	const createRelation = !$("#chkNoRelation").is(":checked");
	const failMessage = "Viga! Termini loomine ebaõnnestus";
	createWordForm.find('input[name="createRelation"]').val(createRelation);


	$.ajax({
		url: createWordForm.attr('action'),
		data: JSON.stringify(createWordForm.serializeJSON()),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(response) {
		if (response.status === 'VALID') {
			window.location = `${applicationUrl}termsearch${response.uri}`;
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
	$('#submitWithRelationDiv, #meanings, #relationTypes, #oppositeRelationTypes').hide();
};

function showRelationSelect() {
	$('#submitDiv').hide();
	$('#submitWithRelationDiv, #meanings, #relationTypes').show();
	const relationTypeSelect = $("select[name='relationType']");
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