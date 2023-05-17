$.fn.initTermMeaningTableEkiEditorDlgOnClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function(e) {
			const isEditEnabled = obj.data('edit-enabled');
			if (isEditEnabled) {
				const meaningTableRow = $(this).closest(".meaning-table-row");
				const callbackFunc = () => submitTermMeaningTableMeaning(meaningTableRow);
				initBasicInlineEkiEditorOnContent(obj, callbackFunc);
				e.stopImmediatePropagation();
			}
		});
	});
}

$.fn.meaningRowItemPublicity = function() {
	const publicityBtn = $(this);
	const iconSpan = publicityBtn.find('span[name="icon-span"]');
	const label = publicityBtn.data('label');
	const isPublic = publicityBtn.data('public');
	const isEditEnabled = publicityBtn.data('edit-enabled');
	const publicityInputId = publicityBtn.data('publicity-input-id');
	const meaningId = publicityBtn.data('meaning-id');

	if (isPublic) {
		iconSpan.addClass('fa fa-unlock');
	} else {
		iconSpan.addClass('fa fa-lock');
	}
	publicityBtn.attr('title', label).tooltip();

	publicityBtn.on('click', function(e) {
		e.preventDefault();
		if (isEditEnabled) {
			const editPublicityDlg = $("#editRowItemPublicityDlg");
			editPublicityDlg.find("input[name='publicityInputId']").val(publicityInputId);
			editPublicityDlg.find("input[name='meaningId']").val(meaningId);
			if (isPublic) {
				editPublicityDlg.find("select[name='public']").val("true");
			} else {
				editPublicityDlg.find("select[name='public']").val("false");
			}
			editPublicityDlg.modal('show', publicityBtn);
		}
	});
}

$.fn.initMeaningRowItemPublicityDlgPlugin = function() {
	return this.each(function() {
		const editPublicityDlg = $(this);
		editPublicityDlg.on('show.bs.modal', function() {
			let selectControl = editPublicityDlg.find('select');
			configureSelectDlg(selectControl, editPublicityDlg);

			selectControl.off('click').on('click', function(e) {
				submitRowItemPublicity(editPublicityDlg);
			});
			selectControl.off('changed.bs.select').on('changed.bs.select', function(e) {
				submitRowItemPublicity(editPublicityDlg);

			});
			selectControl.off('keydown').on('keydown', function(e) {
				if (e.key === "Enter") {
					submitRowItemPublicity(editPublicityDlg);
				}
			});
		})
	})
}

$.fn.meaningTableItemsPublicity = function() {
	const publicityBtn = $(this);
	const type = publicityBtn.data('type');

	publicityBtn.on('click', function(e) {
		e.preventDefault();
		const editPublicityDlg = $("#editTableItemsPublicityDlg");
		editPublicityDlg.find("input[name='type']").val(type);
		editPublicityDlg.modal('show', publicityBtn);
	});
}

$.fn.initMeaningTableItemsPublicityDlgPlugin = function() {
	return this.each(function() {
		const editPublicityDlg = $(this);
		editPublicityDlg.on('show.bs.modal', function() {
			let selectControl = editPublicityDlg.find('select');
			configureSelectDlg(selectControl, editPublicityDlg);

			selectControl.off('click').on('click', function(e) {
				submitTableItemsPublicity(editPublicityDlg);
			});
			selectControl.off('changed.bs.select').on('changed.bs.select', function(e) {
				submitTableItemsPublicity(editPublicityDlg);

			});
			selectControl.off('keydown').on('keydown', function(e) {
				if (e.key === "Enter") {
					submitTableItemsPublicity(editPublicityDlg);
				}
			});
		})
	})
}

function submitRowItemPublicity(editPublicityDlg) {
	const selectedValue = editPublicityDlg.find("select[name='public']").val();
	const publicityInputId = editPublicityDlg.find("input[name='publicityInputId']").val();
	const meaningId = editPublicityDlg.find("input[name='meaningId']").val();
	const publicityInput = $("#" + publicityInputId);
	const meaningTableRow = $("#" + meaningId);

	publicityInput.val(selectedValue);
	editPublicityDlg.modal('hide');
	submitTermMeaningTableMeaning(meaningTableRow);
}

function submitTableItemsPublicity(editPublicityDlg) {
	const selectedValue = editPublicityDlg.find("select[name='public']").val();
	const type = editPublicityDlg.find("input[name='type']").val();
	let ids;
	let form;
	if (type === "definition") {
		ids = $("input[name='definitionIds']");
		form = $("#updateDefinitionsPublicityForm")
	} else if (type === "lexeme") {
		ids = $("input[name='lexemeIds']");
		form = $("#updateLexemesPublicityForm")
	} else if (type === "usage") {
		ids = $("input[name='usageIds']");
		form = $("#updateUsagesPublicityForm")
	}
	const publicityInput = form.find("input[name='public']");
	form.append(ids);
	publicityInput.val(selectedValue);
	form.submit();
}

function submitTermMeaningTableMeaning(meaningTableRow) {
	openWaitDlg();
	const meaningDataValueSpans = meaningTableRow.find(".value-span");
	meaningDataValueSpans.each(function() {
		const span = $(this);
		span.parent().find(".meaning-data-input").val(span.html());
	});

	const form = $("#updateMeaningForm");
	const meaningDataInputs = meaningTableRow.find(".meaning-data-input");
	form.append(meaningDataInputs);
	return $.ajax({
		url: form.attr('action'),
		data: form.serialize(),
		method: 'POST'
	}).done(function(updatedMeaningTableRow) {
		meaningTableRow.replaceWith(updatedMeaningTableRow);
		form.html("");
		closeWaitDlg();
		$wpm.bindObjects();
	}).fail(function(data) {
		console.log(data);
	});
}