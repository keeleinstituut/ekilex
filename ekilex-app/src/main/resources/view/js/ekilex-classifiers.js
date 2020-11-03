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

	$(document).on('show.bs.modal', "#addClassifierCodeDlg", function(e) {
		let classifierCode = $(e.relatedTarget).attr('data-classif-code');
		let createBeforeExisting = $(e.relatedTarget).attr('data-create-before-existing');
		let form = $(this).find('form');
		form.find('input[name=existingClassifierCode]').val(classifierCode);
		form.find('input[name=createBeforeExisting]').val(createBeforeExisting);
	});

	$(document).on("click", ":button[name='editClassifBtn']", function() {
		let editClassifBtn = $(this);
		let allEditBtns = $(document).find('button[name="editClassifBtn"]');
		let allDeleteBtns = $(document).find('button[name="deleteClassifBtn"]');
		let allCodeBtns = $(document).find('button[name="addCodeBtn"]');
		let activeRow = editClassifBtn.closest('tr');
		let activeRowInputs = activeRow.find('input');
		let saveRowBtn = activeRow.find('button[name="saveRowBtn"]');
		let cancelBtn = activeRow.find('button[name="cancelBtn"]');

		activeRowInputs.attr("disabled", false);
		allEditBtns.hide();
		allDeleteBtns.hide();
		allCodeBtns.hide();
		saveRowBtn.show();
		cancelBtn.show();
	});

	$(document).on("click", ":button[name='cancelBtn']", function() {
		location.reload();
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

function deleteClassifier() {
	let classifierCode = $(this).attr("data-classif-code");
	let classifierName = $(this).attr("data-classif-name");
	let domainOriginCode = $(this).attr("data-domain-origin");

	let deleteData = {
		classifierCode: classifierCode,
		classifierName: classifierName,
		domainOriginCode: domainOriginCode
	}

	$.ajax({
		url: applicationUrl + 'delete_classifier',
		data: deleteData,
		method: 'POST',
	}).done(function(response) {
		if (response === "OK") {
			location.reload();
		} else {
			console.log(response);
			openAlertDlg("Kustutamine ebaõnnestus. Kontrolli, kas klassifikaatorit on kusagil kasutatud.");
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg("Kustutamine ebaõnnestus");
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