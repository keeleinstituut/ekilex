$.fn.deleteConfirm = function() {
	$(this).confirmation({
		btnOkLabel : messages["common.yes"],
		btnCancelLabel : messages["common.no"],
		title : messages["common.confirm.delete"],
		onConfirm : executeDelete
	});
};

function postJson(url, dataObject, failMessage = messages["common.save.error"], callback) {
	return $.ajax({
		url: url,
		data: JSON.stringify(dataObject),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json',
		success: function(){
			if (callback) {
				callback();
			}
		},
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
};

function doPostDelete(deleteUrl, callback, force) {
	Cookies.set('details-open', $('.details-open').parent().attr('id'));
	$.post(deleteUrl).done(function(response) {
		const parsedParams = QueryParams.parseParams(deleteUrl);
		if (response.status === "OK") {
			if (response.message != null) {
				openMessageDlg(response.message);
			}

			if (parsedParams.id && !force) {
				if (parsedParams.opCode === 'syn_meaning_relation') {
					const word = $(`[data-id="${parsedParams.id}"]:first`);
					word.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first, [name="synDetailsBtn"]:first').click();
				}
				else if ($(`#lexeme-details-${parsedParams.id}`).length) {
					const elem = $(`#lexeme-details-${parsedParams.id}`);
					const parent = elem.parents('[data-rel="details-area"]');
					parent.find('[name="details-btn"]:first').click();
				} else {

					let elem = $(`#resultColumn [data-id*="${parsedParams.id}"]:first`);
					if (!elem.length) {
						elem = $(`#resultColumn [id*="${parsedParams.id}"]:first`);
					}
					let parent = elem.parents('.details-open:first');

					if (elem.is('[data-rel="details-area"]')) {
						elem.find('[name="details-btn"]:first, [name="synDetailsBtn"]:first').click();
					}
					else if (!parent.length) {
						parent = elem.parents('[data-rel="details-area"]:first');

						const results = parent.find('[name="details-btn"]:first, [name="synDetailsBtn"]:first');
						if (results.length) {
							results.click();
						} else {
							$('#refresh-details').click();
						}
					} else {
						parent.find('#refresh-open:first').click();
					}
				}

			} else if (callback) {
				callback();
			}
		} else {
			openAlertDlg(messages["common.error"]);
			console.log(response);
		}
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
};

function submitDialog(e, dlg, failMessage) {
	e.preventDefault();
	const form = dlg.find('form');
	if (!checkRequiredFields(form)) {
		return;
	}

	const successCallback = dlg.attr("data-callback");
	let successCallbackFunc = createCallback(successCallback);
	submitForm(form, failMessage, successCallbackFunc).always(function() {
		dlg.modal('hide');
	});
};

function submitForm(form, failMessage, callback) {
	const data = JSON.stringify(form.serializeJSON());
	return $.ajax({
		url: form.attr('action'),
		data: data,
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function() {
		if (typeof callback === 'function') {
			callback();
		} else {
			const detailsOpen = $('.details-open');
			if (detailsOpen.length) {
				Cookies.set('details-open', detailsOpen.parent().attr('id'));
			}
			ScrollStore.saveActiveScroll();
			form
				.parents('#details-area:first, #meaning-details-area:first, #syn-details-area:first')
				.find('#refresh-details')
				.click();
		}
	}).fail(function(data) {
		console.log(data);
		alert(failMessage);
	});
};

function alignAndFocus(e, dlg) {
	dlg.find('.form-control').first().focus();
	if (e.relatedTarget) {
		let dlgTop = ($(e.relatedTarget).offset().top - $(window).scrollTop()) - dlg.find('.modal-content').height() - 30;
		if (dlgTop < 0) {
			dlgTop = 0;
		}
		//dlg.find('.modal-content').css('top', dlgTop);
	}
};

// Do not change the selector to '.required-field' as it causes selectpicker to fire change event twice
$(document).on("change", "input.required-field, select.required-field, textarea.required-field", function() {
	const requiredField = $(this);
	let isSelectPicker = requiredField.hasClass('classifier-select');
	let markableField = isSelectPicker ? requiredField.parent() : requiredField;

	if (requiredField.val()) {
		markableField.removeClass('is-invalid');
	} else {
		markableField.addClass('is-invalid');
	}

	let errorSmallElem = requiredField.closest('.form-group').find('.field-error');
	if (errorSmallElem.length) {
		errorSmallElem.html('').hide();
	}
});

/**
 * Adds an error class to the given field
 * Finds a <small class="field-error"> element inside of a <div class="form-group"> with the given field. If found shows it with the given text.
 * @param field
 * @param errorText
 */
function showFieldError(field, errorText) {
	const errorSmallElem = field.closest('.form-group').find('.field-error');
	if (errorSmallElem.length) {
		errorSmallElem.html(errorText).show();
		field.addClass('is-invalid');
	}
};

function changeItemOrdering(target, delta) {
	let orderBlock = target.closest('.orderable');
	let opCode = orderBlock.attr("data-op-code");
	let itemToMove = target.closest('[data-orderby]');
	let additionalInfo = orderBlock.attr('data-additional-info');
	let items = orderBlock.find('[data-orderby]');
	items = $.grep(items, function(item) {
		return $(item).parent().attr("data-op-code") === opCode;
	});
	items = $(items);
	let itemToMovePos = items.index(itemToMove);
	let orderedItems = [];
	if (itemToMovePos + delta >= 0 && itemToMovePos + delta < items.length) {
		// Named with dollar sign as I'm not sure what the contents are
		const $itemToMovePosDelta = $(items.get(itemToMovePos + delta));
		const $itemToMovePos = $(items.get(itemToMovePos));
		let orderby = $itemToMovePosDelta.attr('data-orderby');
		$itemToMovePosDelta.attr('data-orderby', $itemToMovePos.attr('data-orderby'));
		$itemToMovePos.attr('data-orderby', orderby);
		if (delta > 0) {
			$itemToMovePosDelta.after($itemToMovePos);
		} else {
			$itemToMovePosDelta.before($itemToMovePos);
		}
		items = orderBlock.find('[data-orderby]');
		items.each(function(indx) {
			const item = $(this);
			item.find('.order-up').prop('hidden', indx == 0);
			item.find('.order-down').prop('hidden', indx == items.length - 1);
			let itemData = {};
			itemData.id = item.attr('data-id');
			itemData.code = item.attr('data-code');
			itemData.orderby = item.attr('data-orderby');
			orderedItems.push(itemData);
		});
	}
	return {
		opCode: opCode,
		items: orderedItems,
		additionalInfo: additionalInfo
	};
};

function executeDelete(deleteUrl) {
	const $this = $(this);
	if (deleteUrl === undefined) {
		const opCode = $this.attr("data-op-code");
		const id = $this.attr("data-id");
		const value = $this.attr("data-value");
		deleteUrl = `${applicationUrl}delete_item?opCode=${opCode}&id=${id}`;
		if (value !== undefined) {
			deleteUrl = `${deleteUrl}&value=${encodeURIComponent(value)}`;
		}
	}
	const successCallback = $this.attr("data-callback");
	let successCallbackFunc = createCallback(successCallback);
	if (!successCallback) {
		successCallbackFunc = () => $('#refresh-details').click();
	}
	doPostDelete(deleteUrl, successCallbackFunc);
};

function initAddMultiDataDlg(theDlg) {

	theDlg.find('select.classifier-select').off('changed.bs.select').on('changed.bs.select', function() {
		theDlg.find('[name=value]').val($(this).val());
	});

	theDlg.find('.value-select').off('change').on('change', function() {
		theDlg.find('[name=value]').val($(this).val());
	});
	theDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, theDlg, messages["common.data.add.error"]);
	});
	theDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		theDlg.find('.form-control').each(function() {
			$(this).val(null);
		});
		theDlg.find('select').each(function() {
			const item = $(this);
			item.val(item.find('option').first().val());
		});
		alignAndFocus(e, theDlg);
	});
};

function initGenericTextAddDlg(addDlg) {
	addDlg.find('[name=value]').val(null);
	addDlg.find('select').each(function() {
		const item = $(this);
		item.val(item.find('option').first().val());
	});
	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, addDlg, messages["common.data.add.error"]);
	});
	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		alignAndFocus(e, addDlg)
	});
};

function initGenericTextEditDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, editDlg, messages["common.data.update.error"])
	});
};

function initSelectDlg(selectDlg) {
	let selectControl = selectDlg.find('select');
	configureSelectDlg(selectControl, selectDlg);
	selectControl.off('click').on('click', function(e) {
		submitDialog(e, selectDlg, messages["common.data.update.error"])
	});
	selectControl.off('changed.bs.select').on('changed.bs.select', function(e) {
		submitDialog(e, selectDlg, messages["common.data.update.error"]);
	});
	selectControl.off('keydown').on('keydown', function(e) {
		if (e.key === "Enter") {
			submitDialog(e, selectDlg, messages["common.data.update.error"]);
		}
	});
};

function configureSelectDlg(selectControl, selectDlg) {
	let maxItemLength = 0;
	selectControl.find('option').each(function() {
		let itemLength = $(this).text().length;
		if (itemLength > maxItemLength) {
			maxItemLength = itemLength;
		}
	});
	let dlgWidth = maxItemLength > 80 ? '85ch' : `${maxItemLength + 5}ch`;
	let numberOfOptions = selectControl.find('option').length;
	selectControl.attr('size', numberOfOptions > 20 ? 20 : numberOfOptions);
	selectDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		const dlg = $(e.relatedTarget);
		let dlgTop = dlg.offset().top - $(window).scrollTop();
		let dlgLeft = dlg.offset().left - selectDlg.find('.modal-dialog').offset().left;
		let modalContent = selectDlg.find('.modal-content');
		modalContent.css('top', dlgTop - 30);
		modalContent.css('left', dlgLeft);
		modalContent.css('width', dlgWidth);
		let overTheEdge = (modalContent.offset().left + modalContent.width()) - window.innerWidth;
		if (overTheEdge > 0) {
			modalContent.css('left', dlgLeft - modalContent.width());
		}
		selectDlg.find('.form-control').first().focus();
		$('.modal-backdrop').css('opacity', 0);
	});
};

function initNewWordDlg() {
	const newWordDlg = $('#newWordDlg');
	const formControl = newWordDlg.find('.form-control');
	newWordDlg.on('shown.bs.modal', function(e) {
		formControl.first().focus();
		formControl.each(function() {
			$(this).removeClass('is-invalid');
		});
		const searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (searchValue.includes('*') && searchValue.includes('?')) {
			newWordDlg.find('[name=wordValue]').val(null);
		}
		const meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});

	formControl.on('change', function() {
		const dlg = $(this);
		if (dlg.val()) {
			dlg.removeClass('is-invalid');
		} else {
			dlg.addClass('is-invalid');
		}
	});

	$(document).on("click", "#addWordSubmitBtn", function() {
		const addWordForm = $("#addWordForm");
		if (!checkRequiredFields(addWordForm)) {
			return;
		}
		addWordForm.submit();
	});
};

function checkRequiredFields(form) {

	let isValid = true;

	// Do not change the selector to '.required-field' as it causes selectpicker to fire change event twice
	const requiredFields = form.find('input.required-field:not(:hidden), select.required-field:not(:hidden), textarea.required-field:not(:hidden)');

	requiredFields.each(function() {
		const requiredField = $(this);
		const isRequiredRange = requiredField.hasClass('required-range');
		const isSelectPicker = requiredField.hasClass('classifier-select');
		const isMultiselect = isSelectPicker && requiredField.hasClass('multi-select');
		const markableField = isSelectPicker ? requiredField.parent() : requiredField;
		const fldVal = requiredField.val();

		if (!fldVal) {
			markableField.addClass('is-invalid');
			isValid = false;
		} else {
			markableField.removeClass('is-invalid');
		}

		if (isValid && isRequiredRange) {
			const minValue = requiredField.attr('min');
			const maxValue = requiredField.attr('max');
			if (!($.isNumeric(fldVal) && parseFloat(fldVal) >= minValue && parseFloat(fldVal) <= maxValue)) {
				markableField.addClass('is-invalid');
				isValid = false;
			}
		}

		if (isValid && isMultiselect && fldVal.length === 0) {
			markableField.addClass('is-invalid');
			isValid = false;
		}
	});
	return isValid;
};

function initAddSourceLinkDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=sourceLinkDlgContent]').html(null);

	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const searchInput = addDlg.find("input[name='searchFilter']");
		if (searchInput.autocomplete('instance')) {
			searchInput.autocomplete('close');
		}
		const button = $(this);
		const content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		const form = button.closest('form');
		const url = form.attr('action') + '?' + form.serialize();
		$.get(url).done(function(data) {
			addDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
			$wpm.bindObjects();
			addDlg.find('button[data-source-id]').off('click').on('click', function(e) {
				e.preventDefault();
				const button = $(e.target);
				const sourceName = button.closest('.form-group').find('.form-control').val();
				const sourceId = button.data('source-id');
				const selectedSourceNameId = $(`[name='source_${sourceId}']:checked`).val();
				addDlg.find('[name=id2]').val(sourceId);
				addDlg.find('[name=id3]').val(selectedSourceNameId);
				addDlg.find('[name=value]').val(sourceName);
				const form = button.closest('form');

				if (checkRequiredFields(form)) {
					const successCallback = addDlg.attr("data-callback");
					let successCallbackFunc = createCallback(successCallback);
					submitForm(form, messages["common.data.update.error"], successCallbackFunc).always(function() {
						addDlg.modal('hide');
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function() {
		addDlg.find('.form-control').first().focus();
	});
};

function initEditSourceLinkDlg(editDlg) {
	const sourceLinkContentKey = editDlg.find('input[name="opCode"]').val();
	const sourceLinkId = editDlg.find('input[name="id"]').val();
	const getSourceAndSourceLinkUrl = `${applicationUrl}source_and_source_link/${sourceLinkContentKey}/${sourceLinkId}`;

	$.get(getSourceAndSourceLinkUrl).done(function(data) {
		editDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}

$.fn.updateSourceLink = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const updateSourceLinkForm = main.closest('form[name="sourceLinkForm"]');
		const updateSourceLinkModal = updateSourceLinkForm.closest('.modal');
		const successCallback = updateSourceLinkModal.attr("data-callback");
		let successCallbackFunc = createCallback(successCallback);
		submitForm(updateSourceLinkForm, messages["common.data.update.error"], successCallbackFunc).always(function() {
			updateSourceLinkModal.modal('hide');
		});
	});
};

function initRelationDialogLogic(addDlg, idElementName) {
	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const button = $(this);
		const content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		const form = button.closest('form');
		const url = form.attr('action') + '?' + form.serialize();
		$.get(url).done(function(data) {
			addDlg.find('[data-name=dialogContent]').replaceWith(data);
			addDlg.find(`button[data-${idElementName}]`).off('click').on('click', function(e) {
				e.preventDefault();
				const button = $(e.target);
				addDlg.find('[name=id2]').val(button.data(idElementName));
				const form = button.closest('form');
				if (checkRequiredFields(form)) {
					submitForm(form, messages["common.data.update.error"]).always(function() {
						addDlg.modal('hide');
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function() {
		addDlg.find('.form-control').first().focus();
	});
};

function initMultiselectRelationDlg(dlg) {

	dlg.find('.form-control').val(null);
	dlg.find('[data-name=dialogContent]').html(null);
	dlg.find('[data-name=oppositeRelation]').hide();
	const selectElem = dlg.find('select');
	selectElem.val(selectElem.find('option').first().val());

	dlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const searchBtn = $(this);
		const content = searchBtn.html();
		searchBtn.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		const searchForm = searchBtn.closest('form');
		const searchUrl = searchForm.attr('action') + '?' + searchForm.serialize();
		const id = searchForm.parents('[data-id]:first').attr('data-id');

		$.get(searchUrl).done(function(data) {
			dlg.find('[data-name=dialogContent]').replaceWith(data);
			const addRelationsBtn = dlg.find('button[name="addRelationsBtn"]');

			const idsChk = dlg.find('input[name="ids"]');
			idsChk.on('change', function() {
				addRelationsBtn.prop('disabled', !idsChk.filter(":checked").length);
			});

			addRelationsBtn.off('click').on('click', function(e) {
				e.preventDefault();
				const selectRelationsForm = addRelationsBtn.closest('form');
				if (checkRequiredFields(selectRelationsForm)) {
					selectRelationsForm.find('select[name="oppositeRelationType"]').prop('disabled', false);
					$.ajax({
						url: selectRelationsForm.attr('action'),
						data: selectRelationsForm.serialize(),
						method: 'POST',
					}).done(function() {
						const successCallback = dlg.attr("data-callback");
						let successCallbackFunc = createCallback(successCallback);
						if (successCallbackFunc) {
							successCallbackFunc();
						} else {
							refreshDetailsSearch(id);
						}
						dlg.modal('hide');
					}).fail(function(data) {
						dlg.modal('hide');
						console.log(data);
						openAlertDlg(messages["common.error"]);
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function() {
			searchBtn.html(content);
		});
	});

	dlg.off('shown.bs.modal').on('shown.bs.modal', function() {
		dlg.find('.form-control').first().focus();
		const relationTypeSelect = dlg.find("select[name='relationType']");
		changeOppositeRelationSelectData(relationTypeSelect);
	});
};

function changeOppositeRelationSelectData(relationTypeSelect) {
	const relationTypeValue = relationTypeSelect.find('option:selected').val();
	const form = relationTypeSelect.closest('form');
	const oppositeRelationDiv = form.find('[data-name=oppositeRelation]');
	const entity = oppositeRelationDiv.data('entity');
	const oppositeRelationSelect = oppositeRelationDiv.find('select[name="oppositeRelationType"]');
	const getOppositeClassifiersUrl = applicationUrl + "oppositerelations";

	$.ajax({
		url: getOppositeClassifiersUrl,
		data: {entity: entity, relationType: relationTypeValue},
		method: 'GET',
	}).done(function(classifiers) {
		oppositeRelationSelect.children().remove();
		switch (classifiers.length) {
			case 0:
				oppositeRelationDiv.hide();
				break;
			case 1:
				oppositeRelationSelect.append(new Option(classifiers[0].value, classifiers[0].code));
				oppositeRelationSelect.attr('disabled', 'disabled');
				oppositeRelationDiv.show();
				break;
			default:
				oppositeRelationSelect.append(new Option('vali väärtus...', '', true, true));
				oppositeRelationSelect.children("option:selected").attr('disabled', 'disabled').attr('hidden', 'hidden');
				$.each(classifiers, function(index, classifier) {
					oppositeRelationSelect.append(new Option(classifier.value, classifier.code));
				});
				oppositeRelationSelect.removeAttr('disabled');
				oppositeRelationDiv.show();
				break;
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
};

function decorateSourceLinks(detailsDiv) {
	detailsDiv.find('a[href]').each(function() {
		const link = $(this);
		const href = link.attr('href');
		if (href.includes('_source_link:')) {
			link.attr('data-target', '#sourceDetailsDlg');
			link.attr('data-toggle', 'modal');
			link.on('click', function(e) {
				openSourceDetails(e.target);
			});
		}
	});
};

function openSourceDetails(elem) {
	const $elem = $(elem);
	const dlg = $($elem.data('target'));
	const url = $elem.attr('href');
	dlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});
};
/*
function openMessageDlg(message) {
	openAlertDlg(message, false);
};

function openAlertDlg(alertMessage, showAsAlert = true) {
	const alertDlg = $('#alertDlg');
	alertDlg.find(('[name=alert_message]')).text(alertMessage);
	alertDlg.find('.alert-warning').prop('hidden', !showAsAlert);
	alertDlg.find('.alert-success').prop('hidden', showAsAlert);
	alertDlg.modal('show');
	alertDlg.find('.modal-footer button').focus();
};
*/

function openMessageDlg(message, smallAlert = true) {
	openAlertDlg(message, smallAlert, false);
};

function openAlertDlg(alertMessage, smallAlert = true, showAsAlert = true) {
	let warning = null;
	let success = null;
	let alertDlg = null;
	if (smallAlert) {
	
		alertDlg = $('#alertSmall');
		warning = '.alert-custom-warning-hide';
		success = '.alert-custom-success-hide';
	} else {
	
		alertDlg = $('#alertDlg');
		warning = '.alert-warning';
		success = '.alert-success';
	}

	alertDlg.find(('[name=alert_message]')).text(alertMessage);
	alertDlg.find(warning).prop('hidden', !showAsAlert);
	alertDlg.find(success).prop('hidden', showAsAlert);

	if (smallAlert) {
		let addCss = alertDlg.find(".alert-small-custom-content");
		if (showAsAlert) {
			addCss.addClass('border-color-red-400');
			addCss.removeClass('border-color-green-400');
		} else {
		addCss.removeClass('border-color-red-400');
			addCss.addClass('border-color-green-400');
		}
		alertDlg.show();

		if(success) {
			setTimeout(function () { alertDlg.hide(); }, 5000);
		}else {
			setTimeout(function () { alertDlg.hide(); }, 7000);
		}
	} else {
		alertDlg.modal('show');
		alertDlg.find('.modal-footer button').focus();
	}

};

$.fn.costomAlertClose = function () {
	const obj = $(this);
	obj.on('click', function () {
		obj.closest('#alertSmall').hide();
	});
};

function openWaitDlg(message) {
	if (message) {
		$("#waitMessageDiv").show();
		$("#waitMessage").text(message);
	} else {
		$("#waitMessageDiv").hide();
	}
	$("#waitDlg").modal("show");
	$("body").css("cursor", "progress");
}

function closeWaitDlg() {
	const waitDlg = $("#waitDlg");
	const isModalOpened = waitDlg.hasClass('show');
	let timeout = 100;
	if (!isModalOpened) {
		timeout = 500;
	}
	setTimeout(function() {
		waitDlg.modal("hide");
		$("body").css("cursor", "default");
	}, timeout);
};

function openConfirmDlg(confirmDlgHtml, callback, ...callbackArgs) {
	const confirmDlg = $('#confirmDlg');
	confirmDlg.html(confirmDlgHtml);
	confirmDlg.modal('show');
	const okBtn = confirmDlg.find('.modal-footer [name=ok]');
	okBtn.focus();
	okBtn.off('click').on('click', function() {
		confirmDlg.modal('hide');
		callback(...callbackArgs);
	});
};

function deleteLexemeAndWordAndMeaning() {
	const opName = "delete";
	const opCode = "lexeme";
	const element = $(this);
	const lexemeId = element.attr("data-id");
	const successCallback = element.attr("data-callback");
	const successCallbackFunc = createCallback(successCallback, element);

	executeMultiConfirmPostDelete(opName, opCode, lexemeId, successCallbackFunc);
};

function deleteLexemeAndRusMeaningLexemes() {
	const opName = "delete";
	const opCode = "rus_meaning_lexemes";
	const element = $(this);
	const lexemeId = element.attr("data-id");
	const successCallback = element.attr("data-callback");
	const successCallbackFunc = createCallback(successCallback, element);


	executeMultiConfirmPostDelete(opName, opCode, lexemeId, function() {
		const detailsLength = element.parents('[data-rel="details-area"]:first').find('[id*="lexeme-details-"]').length;
		if (detailsLength <= 1 && successCallbackFunc) {
			successCallbackFunc();
		} else {
			element.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first').trigger('click');
		}
	}, true);
};

function executeMultiConfirmPostDelete(opName, opCode, id, successCallbackFunc, force) {
	const deleteUrl = `${applicationUrl}delete_item?opCode=${opCode}&id=${id}`;
	const confirmationOpUrl = applicationUrl + "confirm_op";
	const dataObj = {
		opName: opName,
		opCode: opCode,
		id: id
	};
	$.ajax({
		url: confirmationOpUrl,
		data: JSON.stringify(dataObj),
		method: 'POST',
		contentType: 'application/json'
	}).done(function(data) {
		openConfirmDlg(data, doPostDelete, deleteUrl, successCallbackFunc, force);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
};

function initClassifierAutocomplete() {
	$('.classifier-select').selectpicker({
		width: '100%',
		container: 'body'
	});
};

function refreshDetails() {
	$('#refresh-details').click();
}

function validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage) {
	$.ajax({
		url: validateJoinUrl,
		data: joinForm.serialize(),
		method: 'POST',
	}).done(function(response) {
		if (response.status === "VALID") {
			joinForm.submit();
		} else if (response.status === "INVALID") {
			openAlertDlg(response.message);
		} else {
			openAlertDlg(failMessage);
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
};

$.fn.changeSubmitFormPlugin = function() {
	return this.each(function() {
		$(this).on('change', function() {
			this.form.submit();
		});
	});
}

$.fn.loadDetailsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const wordOrMeaningId = obj.data('id');
			const behaviour = obj.data('behaviour') || false;
			const lastWordOrMeaningId = behaviour === 'replace' ? obj.parents('#details-area:first').attr('data-id') : false;
			loadDetails(wordOrMeaningId, behaviour, lastWordOrMeaningId);
		})
	})
}

$.fn.activityLogDlgPlugin = function() {
	return this.each(function() {
		const dlg = $(this);
		dlg.on('show.bs.modal', function(e) {
			const link = $(e.relatedTarget);
			const url = link.attr('href');
			dlg.find('.close').focus();
			dlg.find('.modal-body').html(null);
			$.get(url).done(function(data) {
				dlg.find('.modal-body').html(data);
			});
		});
	});
}

$.fn.langCollapsePlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const lang = btn.attr("data-lang");
			const itemData = {
				opCode: "user_lang_selection",
				code: lang
			};
			const successCallback = btn.attr("data-callback");
			const successCallbackFunc = createCallback(successCallback);
			postJson(applicationUrl + 'update_item', itemData).done(function() {
				if (viewType === 'term') {
					refreshDetailsSearch(btn.parents('[data-rel="details-area"]').attr('data-id'));
				} else if (successCallbackFunc) {
					successCallbackFunc();
				}
			});
		})
	})
}

$.fn.changeItemOrderingPlugin = function() {
	return this.each(function() {
		const orderingBtn = $(this);
		orderingBtn.on('click', function() {
			const delta = orderingBtn.hasClass('order-up') ? -1 : 1;
			const orderingData = changeItemOrdering(orderingBtn, delta);

			postJson(applicationUrl + 'update_ordering', orderingData);
			if (orderingBtn.hasClass('do-refresh')) {
				refreshDetailsSearch(orderingBtn.parents('[data-rel="details-area"]').attr('data-id'));
			}
		});
	});
}

$.fn.pagingBtnPlugin = function () {
	// Allows for adding plugin to an array
	return this.each(function () {
		const button = $(this);
		button.on('click', function () {
			openWaitDlg();
			let run = true;
			let url;
			const form = button.closest('form');
			const direction = button.data("direction");
			const syn = button.closest('#synSearchResultsDiv');
			if (syn.length) {
				url = applicationUrl + 'syn_paging';
			} else if (viewType.length) {
				url = applicationUrl + viewType + '_paging';
			}

			if (direction === 'page') {
				const inputPageValue = form.find('.paging-input').val().trim();
				const totalPages = form.find('input[name="totalPages"]').val();

				if (($.isNumeric(inputPageValue)) && ($.isNumeric(totalPages))) {
					const inputPageValueInt = parseInt(inputPageValue);
					const totalPagesInt = parseInt(totalPages);
					if (1 <= inputPageValueInt && inputPageValueInt <= totalPagesInt) {
						form.find('input[name="direction"]').val(direction);
						form.find('input[name="pageNum"]').val(inputPageValueInt);
					} else {
						run = false;
					}
				} else {
					run = false;
				}
			} else {
				form.find('input[name="direction"]').val(direction);
			}
			if (run) {
				$.ajax({
					url: url,
					data: form.serialize(),
					method: 'POST',
				}).done(function (data) {
					closeWaitDlg();
					if (syn.length) {
						$('#synSearchResultsDiv')
							.html(data)
							.parent()
							.scrollTop(0);
						$('#syn-details-area').empty();
					} else {
						$('#results_div')
							.html(data)
							.parent()
							.scrollTop(0);
					}

					$wpm.bindObjects();
				}).fail(function (data) {
					console.log(data);
					closeWaitDlg();
					openAlertDlg(messages["common.error"]);
				});
			} else {
				closeWaitDlg();
			}
		});
	});
}

$.fn.pagingInputPlugin = function () {
	return this.each(function() {
		const input = $(this);
		input.on('input', function () {
			const inputPageChkLength = input.val().trim().length;
			if (inputPageChkLength > 0) {
				$('.paging-submit').css("visibility", "visible");
			} else {
				$('.paging-submit').css("visibility", "hidden");
			}
		});
	
		input.on('keydown', function(e) {
			if (e.which === 13 || e.keyCode === 13) {
				e.preventDefault();
				input.siblings('.paging-submit').click();
			}
		})
	})
}

$.fn.changeOppositeRelationSelectDataPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('change', function() {
			changeOppositeRelationSelectData(obj);
		});
	});
}

// Perhaps possible to combine the plugin with the one from syn page
// They currently have minor differences in execution, which would require a way to check which page it is
$.fn.updateTagCompletePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const wordId = obj.data('word-id');
			const actionUrl = applicationUrl + "update_word_active_tag_complete/" + wordId;
			$.post(actionUrl).done(function(data) {
				if (data !== "{}") {
					openAlertDlg(messages["common.error"]);
					console.log(data);
				}
				refreshDetailsSearch(obj.parents('[data-rel="details-area"]').attr('data-id'));
			}).fail(function(data) {
				openAlertDlg(messages["common.error"]);
				console.log(data);
			});
		});
	});
}

// Example input:
// {
// 	'searchKey': 'ID',
// 	'searchOperand': 'EQUALS',
// 	'searchValue': '3123'
// }
function submitDetailedSearch(options) {
	const form = $('#searchForm');
	if (!form) {
		return;
	}

	// Could also use selectors directly
	const searchParameters = {
		'entity': 'select[name$="entity"]',
		'not': 'input[name$=".not"]:checkbox',
		'searchKey' : 'select[name$="searchKey"]',
		'searchOperand': 'select[name$="searchOperand"]',
		'searchValue': 'input[name$="searchValue"]'
	}

	form.find('#searchMode').val('DETAIL');
	const formDetails = form.find('div[name="detailGroup"]');
	for (const option in options) {
		formDetails.find(searchParameters[option]).val(options[option]);
	}

	form.submit();
}

// Using a document event listener to make sure link clicks are always caught
$(document).on('click', 'eki-link', function() {
	const link = $(this);
	const id = link.attr('data-link-id');
	if (id) {
		const linkType = link.attr('data-link-type');
		
		switch(linkType) {
			case 'word':
				if (viewType === 'lex') {
					// Open in new panel
					loadDetails(id, 'compare');
				} else {
					submitDetailedSearch({
						'searchKey': 'ID',
						'searchOperand': 'EQUALS',
						'searchValue': id
					});
				}
				break;
			case 'meaning':
				if (viewType === 'term') {
					loadDetails(id, 'compare');
				} else {
					submitDetailedSearch({
						'entity': 'MEANING',
						'searchKey': 'ID',
						'searchOperand': 'EQUALS',
						'searchValue': id
					});
				}
				break;
			default:
				openAlertDlg(messages["common.broken.link"]);
				break;
		}
	} else {
		openAlertDlg(messages["common.broken.link"]);
	}
});

$(document).on('click', 'ext-link', function() {
	const link = $(this);
	const href = link.attr('href');
	if (href) {
		const target = link.attr('target');
		// Perhaps a good idea to add a regex check for valid url?
		if (href.startsWith('https://')) {
			window.open(href, target);
		} else {
			window.open(`https://${href}`, target);
		}
	} else  {
		openAlertDlg(messages["common.broken.link"]);
	}
});

function initializeSearch(type) {
	viewType = type;
	$(window).on('update:wordId', () => {
		const idList = [];
		$('#resultColumn').find('[data-rel="details-area"]').each(function() {
			idList.push($(this).attr('data-id'));
		});
		const idString = idList.join(',');
		if (idList.length === 0) {
			QueryParams.delete('id');
		} else {
			QueryParams.set('id', idString);
		}
	});

	// This seems to be for a display: none element
	$(document).on('change', '[name="resultLang"]', function() {
		$(this).closest('form').submit();
	});

	// Same as resultLang
	$(document).on('change', '[name="resultMode"]', function() {
		$(this).closest('form').submit();
	});

	let detailButtons = $('#results').find('[name="details-btn"]');
	if (QueryParams.get('id')) {
		const scrollableArea = $('#resultColumn .scrollable-area');
		scrollableArea.empty();
		const idList = QueryParams.get('id').split(',');
		idList.forEach(value => {
			scrollableArea.append(`<div data-id="${value}" id="details-area" class="h-100 ui-sortable-placeholder" data-rel="details-area"></div>`);
			loadDetails(value, 'replace', value);
		});
	} else {
		if (detailButtons.length > 0) {
			detailButtons.eq(0).trigger('click');
		}
	}

	if (viewType === 'lim_term') {
		initNewLimTermWordDlg();
	} else {
		initNewWordDlg();
	}
	initClassifierAutocomplete();
};

function getBreadcrumbsData(detailsDiv, word) {
	const crumbsData = detailsDiv.attr('data-breadcrumbs');
	const crumbs = crumbsData ? JSON.parse(crumbsData) : [];
	crumbs.push(word);
	return crumbs;
}

// Scroll to result when searching by id in lex view
function scrollDetails(div, scrollPosition) {
	let overflowDiv = div.find('.overflow-auto').first();
	if (viewType === 'lex') {
		const searchedId = $('#lex-meaning-id-search-meaning-id');
		if (searchedId.length) {
			const searchResult = div.find(`#lexeme-meaning-${searchedId.attr('data-result-id')}`);
			const scrollOffset = searchResult.offset().top - overflowDiv.offset().top;
			overflowDiv.scrollTop(scrollOffset);
		} else {
			overflowDiv.scrollTop(scrollPosition);
		}
	} else if (viewType === 'term') {
		overflowDiv.scrollTop(scrollPosition);
	}
}

function loadDetails(wordOrMeaningId, task, lastWordOrMeaningId) {
	// Hide all existing loading spinners and show current one
	$("[id^='select_wait_']").hide();
	$("#select_wait_" + wordOrMeaningId).show();
	if (!task) {
		$('#results_div .list-group-item').removeClass('active');
	}
	openWaitDlg();
	console.log("viewType: " + viewType);

	let detailsUrl;
	switch (viewType) {
		case 'term':
			detailsUrl = applicationUrl + 'termmeaningdetails/' + wordOrMeaningId;
			break;
		case 'lim_term':
			detailsUrl = applicationUrl + 'limtermmeaningdetails/' + wordOrMeaningId;
			break;
		default:
			detailsUrl = applicationUrl + 'worddetails/' + wordOrMeaningId;
			let selectedMeaningIdInput = $('#selectedMeaningId');
			const selectedMeaningId = selectedMeaningIdInput.val();
			if (selectedMeaningId) {
				detailsUrl += '/' + selectedMeaningId;
				selectedMeaningIdInput.val('');
			}
			break;
	}

	$.get(detailsUrl).done(function(data) {

		closeWaitDlg();

		let detailsDiv = $('#details-area');
		const resultColumn = $('#resultColumn:first');
		const dataObject = $(data);
		if (!task) {
			if (detailsDiv.length === 0) {
				detailsDiv = $('<div data-rel="details-area"></div>');
				resultColumn.find('.scrollable-area').append(detailsDiv);
			}
			PanelBreadcrumbs.removeAllButFirstData();
			resultColumn.find('[data-rel="details-area"]').slice(1).remove();
			detailsDiv.replaceWith(dataObject[0].outerHTML);
			detailsDiv = $('#details-area');
			ScrollStore.loadPrevScroll();
		} else {
			dataObject.find('[data-hideable="toolsColumn"]').attr('data-hideable', `toolsColumn-${wordOrMeaningId}`);
			dataObject.find('#toolsColumn').attr('id', `toolsColumn-${wordOrMeaningId}`);
			dataObject.find('[data-extendable="contentColumn"]').attr('data-extendable', `contentColumn-${wordOrMeaningId}`);
			dataObject.find('#contentColumn').attr('id', `contentColumn-${wordOrMeaningId}`);
			if (task === 'replace') {
				detailsDiv = resultColumn.find(`[data-rel="details-area"][data-id="${lastWordOrMeaningId}"]`);
				const retainScrollPosition = parseInt(dataObject.attr('data-id')) === parseInt(wordOrMeaningId);
				const contentDiv = detailsDiv.find('.overflow-auto:first');
				const scrollPosition = contentDiv.length 
					&& retainScrollPosition 
					? contentDiv[0].scrollTop 
					: 0;
				const newDiv = $(dataObject[0].outerHTML);
				detailsDiv.replaceWith(newDiv);
				detailsDiv = newDiv;
				scrollDetails(detailsDiv, scrollPosition);
			} else {
				detailsDiv = $(dataObject?.[0]?.outerHTML);
				resultColumn.find('.scrollable-area').append(detailsDiv);
			}
		}

		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();

		$(window).trigger('update:wordId');

		$("#select_wait_" + wordOrMeaningId).hide();
		$('.tooltip').remove();

		$('[data-toggle="tooltip"]').tooltip({ trigger: 'hover' });

		$('#results_div .list-group-item').removeClass('active');
		resultColumn.find('[data-rel="details-area"]').each(function() {
			const id = $(this).attr('data-id');
			$(`#results button[data-id=${id}]`).parent().addClass('active');
		});

		$('#results_div .list-group-item').each(function() {
			const elem = $(this);
			const button = elem.find('button');
			if (elem.is('.active')) {
				button.removeAttr('data-contextmenu:compare');
				button.attr('data-contextmenu:closePanel', 'Sulge paneel');
			} else {
				button.removeAttr('data-contextmenu:closePanel');
				button.attr('data-contextmenu:compare', 'Ava uues paneelis');
			}
		});

		detailSearchBtn();

		$wpm.bindObjects();

		setTimeout(() => {

			if (Cookies.get('details-open')) {
				
				const block = $('#'+Cookies.get('details-open'));
				if (block.children('.details-open').length == 0) {
					block.find('[name="lexeme-details-btn"]').trigger('click');
				}
				Cookies.delete('details-open');
			} else {
				closeWaitDlg();
			}
		}, 60);

	}).fail(function(data) {
		alert('Keelendi detailide päring ebaõnnestus');
		closeWaitDlg();
	});
};

// Create an editor in place of the target
function initBasicInlineEkiEditorOnContent(obj, callback) {
	const options = {
		width: '100%',
		height: obj.parent().height() - 44,
		extraPlugins: 'ekiStyles',
		extraAllowedContent: '',
		removePlugins: 'sourcearea, elementspath',
		resize_enabled: false,
		toolbarGroups: [
			{
				name: 'eki-styles',
				groups: ['ekiStyles']
			}
		]
	};

	const editField = $('<textarea/>').val(obj.html());
	obj.hide();
	obj.after(editField);
	// Leave only basic styling buttons
	const editor = initCkEditor(editField, options);
	$(document).on('click.replace.eki.editor', function(e) {
		const isObjParentClosest = $(e.target).closest(obj.parent()).length;
		if (!isObjParentClosest) {
			const content = editor.getData();
			obj.html(content);
			editor.destroy();
			editField.remove();
			obj.show();
			$(document).off('click.replace.eki.editor');

			if (callback) {
				callback();
			}
		}
	});
}

/**
 * @param data Success callback created by BE and added to an element attribute
 * @param optionalArgs Any optional args
 * @returns Function that will trigger the callback with args applied
 */
function createCallback(data, optionalArgs) {
	if (!data) return undefined;
	let func;
	let argsString = '';

	if (data.includes('(')) {
		// Remove end parenthesis and split string from the start of function args
		[func, argsString] = data.replace(')', '').split('(');
	} else {
		func = data;
	}
	
	// Check if the function exists
	if (window[func]) {
		// Split args by comma
		const splitArgsString = argsString.replace(', ', ',').split(',');
		// Check if first element of args has an actual length and return empty array if not
		const callbackArgs = splitArgsString[0].toString().length ? [...splitArgsString] : [];
		// Include optional argument if needed
		const args = callbackArgs;
		if (optionalArgs) {
			if (Array.isArray(optionalArgs)) {
				args.push(...optionalArgs);
			} else {
				args.push(optionalArgs);
			}
		}
		// Return a function that uses callback with provided args
		return () => window[func].apply(null, args);
	}
	return undefined
}

/**
 * Class for storing scroll value
 */
class ScrollStore {
	prevScrollValue = 0;

	static setPrevScroll(scroll) {
		this.prevScrollValue = scroll;
	};

	static getContentScroll() {
		const detailsArea = $('#syn-details-area');
		const detailsAreaScrollContainer = detailsArea.find('.overflow-auto').first();
		const detailsAreaScroll = detailsAreaScrollContainer.scrollTop();
		// Return 0 if scroll value is nullish
		return detailsAreaScroll ?? 0;
	}

	static setContentScroll(scroll) {
		const detailsArea = $('#syn-details-area');
		const detailsAreaScrollContainer = detailsArea.find('.overflow-auto').first();
		detailsAreaScrollContainer.scrollTop(scroll);
	}

	static loadPrevScroll() {
		this.setContentScroll(this.prevScrollValue);
		this.prevScrollValue = 0;
	}

	static saveActiveScroll() {
		this.prevScrollValue = this.getContentScroll();
	}
}

let scrollStoreInstance;
if (!scrollStoreInstance) {
	// Conditionally creating new instance, we only need one.	
	scrollStoreInstance = new ScrollStore();
}
