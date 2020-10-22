function initializeClassifiers() {

	let classifierSelect = $(document).find("select[name='classifierName']");
	updateOriginSelectVisibility(classifierSelect.val());

	$(document).on("change", "select[name='classifierName']", function() {
		let classifierName = $(this).val();
		updateOriginSelectVisibility(classifierName);
	});

	$('form[name="getClassifierForm"]').submit(function(e) {
		e.preventDefault();
		let form = $(this);
		let classifierName = form.find('select[name="classifierName"]').val();
		let url = applicationUrl + "classifiers/" + classifierName;
		if ("DOMAIN" === classifierName) {
			let domainOriginCode = form.find('select[name="domainOriginCode"]').val();
			url = url + "/" + domainOriginCode;
		}
		window.location = url;
	});

	$(document).on("click", ":button[name='editRowBtn']", function() {
		let editRowBtn = $(this);
		let allEditBtns = $(document).find('button[name="editRowBtn"]');
		let activeRow = editRowBtn.closest('tr');
		let activeRowInputs = activeRow.find('input');
		let saveRowBtn = activeRow.find('button[name="saveRowBtn"]');

		activeRowInputs.attr("disabled", false);
		allEditBtns.hide();
		saveRowBtn.show();
	});

	$(document).on("click", ":button[name='saveRowBtn']", function() {
		let saveRowBtn = $(this);
		let classifierCode = saveRowBtn.attr('data-classif-code')
		let classifierName = saveRowBtn.attr('data-classif-name')
		let domainOriginCode = saveRowBtn.attr('data-domain-origin')
		let activeRow = saveRowBtn.closest('tr');
		let labelValueInputs = activeRow.find('input[name="labelValue"]')
		let classifLabels = [];

		labelValueInputs.each(function(index, input) {
			let labelType = $(input).attr('data-label-type');
			let labelLang = $(input).attr('data-label-lang');
			let labelValue = $(input).val();

			let classifLabel = {
				classifierName: classifierName,
				code: classifierCode,
				type: labelType,
				origin: domainOriginCode,
				lang: labelLang,
				value: labelValue
			};
			classifLabels.push(classifLabel);
		});

		$.ajax({
			url: applicationUrl + 'update_classifier',
			data: JSON.stringify(classifLabels),
			method: 'POST',
			contentType: 'application/json'
		}).done(function() {
			location.reload();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg("Salvestamine ebaõnnestus");
		});
	});

	$('form[name="createClassifierForm"]').submit(function(e) {
		e.preventDefault();
		let form = $(this);

		let isValid = checkRequiredFields(form);
		if (!isValid) {
			return;
		}

		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function(response) {
			if (response === "OK") {
				location.reload();
			} else {
				console.log(response);
				openAlertDlg("Salvestamine ebaõnnestus. Kontrolli, kas sellise koodiga klassifikaator on juba olemas");
			}
		}).fail(function(data) {
			console.log(data);
			openAlertDlg("Salvestamine ebaõnnestus");
		});
	});
}

function updateOriginSelectVisibility(classifierName) {
	let originCodeDiv = $("#originCodeDiv");
	if ("DOMAIN" === classifierName) {
		originCodeDiv.show();
	} else {
		originCodeDiv.hide();
	}
}