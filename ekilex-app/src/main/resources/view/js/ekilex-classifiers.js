function initializeClassifiers() {

	$(document).on('show.bs.modal', "#addClassifierCodeDlg", function(e) {
		let classifierName = $(e.relatedTarget).data('classifier-name');
		let form = $(this).find('form');
		form.find('input[name=classifierName]').val(classifierName);
	});

	$(document).on('show.bs.modal', ".classifier-dialog", function(e) {
		let classifierName = $(e.relatedTarget).data('classifier-name');
		let classifierCode = $(e.relatedTarget).data('classifier-code');

		let getClassifierUrl;
		if (classifierCode) {
			// edit existing
			getClassifierUrl = applicationUrl + 'classifier/' + classifierName + '/' + classifierCode;
		} else {
			// add new
			getClassifierUrl = applicationUrl + 'empty_classifier/' + classifierName;
		}

		let form = $(this).find('form');
		let classifierLabelsDiv = form.find('div[data-name=classifLabelsDiv]');
		classifierLabelsDiv.empty();

		$.get(getClassifierUrl).done(function(classifier) {
			let classifierCode = classifier.code;

			let classifierCodeInput = form.find('input[name=classifierCode]');
			classifierCodeInput.attr("data-classif-name", classifierName);
			classifierCodeInput.val(classifierCode);

			let labels = classifier.labels;
			let labelHeadingTemplate = $("#labelHeadingTemplate");
			let labelRowTemplate = $("#labelRowTemplate");

			let labelHeadingDiv = labelHeadingTemplate.clone()
			$(labelHeadingDiv).show();
			classifierLabelsDiv.append(labelHeadingDiv);

			$(labels).each(function(index, label) {
				let labelRowDiv = labelRowTemplate.clone();
				populateLabelRow(label, labelRowDiv);
				$(labelRowDiv).show();
				classifierLabelsDiv.append(labelRowDiv);
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg("Klassifikaatorite päring ebaõnnestus");
		});
	});

	$('form[name="labelsForm"]').submit(function(e) {
		e.preventDefault();
		openWaitDlg();
		let labelsForm = $(this);

		let isValid = checkRequiredFields(labelsForm);
		if (!isValid) {
			closeWaitDlg();
			return;
		}

		let classifierCodeInput = labelsForm.find('input[name="classifierCode"]');
		let classifierCode = classifierCodeInput.val();
		let classifierName = $(classifierCodeInput).attr('data-classif-name');
		let labelInputs = labelsForm.find('input[name^="labelValue"]')

		let classifLabels = [];
		labelInputs.each(function(index, input) {
			let labelType = $(input).attr('data-label-type');
			let labelLang = $(input).attr('data-label-lang');
			let labelValue = $(input).val();

			let classifLabel = {classifierName: classifierName, code: classifierCode, type: labelType, lang: labelLang, value: labelValue};
			classifLabels.push(classifLabel);
		});

		$.ajax({
			url: labelsForm.attr('action'),
			data: JSON.stringify(classifLabels),
			method: 'POST',
			contentType: 'application/json'
		}).done(function(response) {
			if (response === "OK") {
				location.reload();
			} else {
				console.log(response);
				closeWaitDlg();
				openAlertDlg("Salvestamine ebaõnnestus. Kontrolli, kas sellise koodiga klassifikaator on juba olemas");
			}
		}).fail(function(data) {
			console.log(data);
			closeWaitDlg();
			openAlertDlg("Salvestamine ebaõnnestus");
		});
	});

	$('form[name="codeForm"]').submit(function(e) {
		e.preventDefault();
		openWaitDlg();
		let codeForm = $(this);

		let isValid = checkRequiredFields(codeForm);
		if (!isValid) {
			closeWaitDlg();
			return;
		}

		$.ajax({
			url: codeForm.attr('action'),
			data: codeForm.serialize(),
			method: 'POST',
		}).done(function(response) {
			if (response === "OK") {
				location.reload();
			} else {
				console.log(response);
				closeWaitDlg();
				openAlertDlg("Salvestamine ebaõnnestus. Kontrolli, kas sellise koodiga klassifikaator on juba olemas");
			}
		}).fail(function(data) {
			console.log(data);
			closeWaitDlg();
			openAlertDlg("Salvestamine ebaõnnestus");
		});
	});

	function populateLabelRow(label, labelRow) {
		let labelType = label.type;
		let labelEst = label.labelEst;
		let labelEng = label.labelEng;
		let labelRus = label.labelRus;

		labelRow.find('span[name="labelType"]').text(labelType);

		let inputEst = labelRow.find('input[name="labelValueEst"]');
		inputEst.val(labelEst);
		inputEst.attr("data-label-type", labelType);
		inputEst.attr("data-label-lang", "est");

		let inputEng = labelRow.find('input[name="labelValueEng"]');
		inputEng.val(labelEng);
		inputEng.attr("data-label-type", labelType);
		inputEng.attr("data-label-lang", "eng");

		let inputRus = labelRow.find('input[name="labelValueRus"]');
		inputRus.val(labelRus);
		inputRus.attr("data-label-type", labelType);
		inputRus.attr("data-label-lang", "rus");
	}
}