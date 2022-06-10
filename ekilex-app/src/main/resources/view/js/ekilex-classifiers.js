function initializeClassifiers() {

	let classifierSelect = $(document).find("select[name='classifierName']");
	updateOriginSelectVisibility(classifierSelect.val());
}

function deleteClassifier() {
	let classifierCode = $(this).attr("data-classif-code");
	let classifierName = $(this).attr("data-classif-name");
	let domainOriginCode = $(this).attr("data-domain-origin");

	let classifierFull = {
		code: classifierCode,
		name: classifierName,
		origin: domainOriginCode
	}

	$.ajax({
		url: applicationUrl + 'delete_classifier',
		data: JSON.stringify(classifierFull),
		method: 'POST',
		contentType: 'application/json'
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

$.fn.addClassifierCodePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function(e) {
			const classifierOrder = $(e.relatedTarget).data('classif-order');
			const form = obj.find('form');
			form.find('input[name=order]').val(classifierOrder);
		});
	});
}

$.fn.getClassifierFormPlugin = function() {
	return this.each(function() {
		const form = $(this);
		form.on('submit', function(e) {
			e.preventDefault();
			const classifierName = form.find('select[name="classifierName"]').val();
			let url = applicationUrl + "classifiers/" + classifierName;
			if (classifierName === "DOMAIN") {
				const domainOriginCode = form.find('select[name="domainOriginCode"]').val();
				url = url + "/" + domainOriginCode;
			}
			window.location = url;
		});
	});
}

$.fn.classifierNameSelectPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('change', function() {
				const classifierName = obj.val();
				updateOriginSelectVisibility(classifierName);
		});
	});
}

$.fn.editClassifierPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const allEditBtns = $(document).find('button[name="editClassifBtn"]');
			const allDeleteBtns = $(document).find('button[name="deleteClassifBtn"]');
			const allCodeBtns = $(document).find('button[name="addCodeBtn"]');
			const activeRow = btn.closest('tr');
			const activeRowInputs = activeRow.find('input');
			const saveRowBtn = activeRow.find('button[name="saveRowBtn"]');
			const cancelBtn = activeRow.find('button[name="cancelBtn"]');

			activeRowInputs.attr("disabled", false);
			allEditBtns.hide();
			allDeleteBtns.hide();
			allCodeBtns.hide();
			saveRowBtn.show();
			cancelBtn.show();
		});
	});
}

$.fn.cancelClassifierEditPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			location.reload();
		});
	});
}

$.fn.saveClassifierChangesPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const classifierCode = btn.attr('data-classif-code');
			const classifierName = btn.attr('data-classif-name');
			const domainOriginCode = btn.attr('data-domain-origin');
			const activeRow = btn.closest('tr');
			const labelValueInputs = activeRow.find('input[name="labelValue"]');
			const order = activeRow.find('input[name="classifierOrder"]').val()
			const classifLabels = [];

			labelValueInputs.each(function(index, input) {
				const labelType = $(input).attr('data-label-type');
				const labelLang = $(input).attr('data-label-lang');
				const labelValue = $(input).val();

				const classifLabel = {
					classifierName: classifierName,
					code: classifierCode,
					type: labelType,
					origin: domainOriginCode,
					lang: labelLang,
					value: labelValue
				};
				classifLabels.push(classifLabel);
			});

			const classifierFull = {
				name: classifierName,
				origin: domainOriginCode,
				code: classifierCode,
				order: order,
				labels: classifLabels
			}

			$.ajax({
				url: applicationUrl + 'update_classifier',
				data: JSON.stringify(classifierFull),
				method: 'POST',
				contentType: 'application/json'
			}).done(function() {
				location.reload();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg("Salvestamine ebaõnnestus");
			});
		});
	});
}

$.fn.createClassifierPlugin = function() {
	return this.each(function() {
		const form = $(this);
		form.on('submit', function(e) {
			e.preventDefault();

			const isValid = checkRequiredFields(form);
			if (!isValid) {
				return;
			}

			$.ajax({
				url: form.attr('action'),
				data: JSON.stringify(form.serializeJSON()),
				method: 'POST',
				contentType: 'application/json'
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
	});
}

$.fn.deleteClassifierPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.confirmation({
			btnOkLabel : 'Jah',
			btnCancelLabel : 'Ei',
			title : 'Klassifikaatorit on võimalik kustutada siis, kui see ei ole kasutuses. Kas soovid kustutada?',
			onConfirm : deleteClassifier
		});
	});
}