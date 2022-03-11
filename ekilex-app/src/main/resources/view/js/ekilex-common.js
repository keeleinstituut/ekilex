$.fn.deleteConfirm = function() {
	$(this).confirmation({
		btnOkLabel : 'Jah',
		btnCancelLabel : 'Ei',
		title : 'Kinnita kustutamine',
		onConfirm : executeDelete
	});
};

function postJson(url, dataObject, failMessage = 'Salvestamine ebaõnnestus.', callback) {
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
	})
	
	.fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
};

function doPostDelete(deleteUrl, callback, force) {
	Cookies.set('details-open', $('.details-open').parent().attr('id'));
	$.post(deleteUrl).done(function(data) {
		if (data === "OK") {

			if (QueryParams.parseParams(deleteUrl).id && !force) {
				if (QueryParams.parseParams(deleteUrl).opCode === 'syn_meaning_relation') {
					const word = $(`[data-id="${QueryParams.parseParams(deleteUrl).id}"]:first`);
					word.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first, [name="synDetailsBtn"]:first').trigger('click');
				}
				else if ($(`#lexeme-details-${QueryParams.parseParams(deleteUrl).id}`).length) {
					let elem = $(`#lexeme-details-${QueryParams.parseParams(deleteUrl).id}`);
					let parent = elem.parents('[data-rel="details-area"]');
					parent.find('[name="details-btn"]:first').trigger('click');
				} else {

					let elem = $(`#resultColumn [data-id*="${QueryParams.parseParams(deleteUrl).id}"]:first`);
					if (!elem.length) {
						elem = $(`#resultColumn [id*="${QueryParams.parseParams(deleteUrl).id}"]:first`);
					}
					let parent = elem.parents('.details-open:first');

					if (elem.is('[data-rel="details-area"]')) {
						elem.find('[name="details-btn"]:first, [name="synDetailsBtn"]:first').trigger('click');
					}
					else if (!parent.length) {
						parent = elem.parents('[data-rel="details-area"]:first');
						parent.find('[name="details-btn"]:first, [name="synDetailsBtn"]:first').trigger('click');
					} else {
						parent.find('#refresh-open:first').trigger('click');
					}
				}

			} else {
				callback();
			}
		} else {
			openAlertDlg("Andmete eemaldamine ebaõnnestus.");
			console.log(data);
		}
	}).fail(function(data) {
		openAlertDlg("Andmete eemaldamine ebaõnnestus.");
		console.log(data);
	});
};

function submitDialog(e, dlg, failMessage) {
	e.preventDefault();
	let theForm = dlg.find('form');
	if (!checkRequiredFields(theForm)) {
		return;
	}

	var successCallbackName = dlg.attr("data-callback");
	var successCallbackFunc = undefined;
	if (successCallbackName) {
		successCallbackFunc = () => eval(successCallbackName);
	}

	submitForm(theForm, failMessage, successCallbackFunc).always(function() {
		dlg.modal('hide');
	});
};

function submitForm(theForm, failMessage, callback) {
	var data = JSON.stringify(theForm.serializeJSON());
	return $.ajax({
		url: theForm.attr('action'),
		data: data,
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(data) {
		if (typeof callback === 'function') {
			callback();
		} else {
			if ($('.details-open').length) {
				Cookies.set('details-open', $('.details-open').parent().attr('id'));
			}
			theForm.parents('#details-area:first, #meaning-details-area:first, #syn-details-area:first').find('#refresh-details').trigger('click');
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
	let isSelectPicker = $(this).hasClass('classifier-select');
	let markableField = isSelectPicker ? $(this).parent() : $(this);

	if ($(this).val()) {
		markableField.removeClass('is-invalid');
	} else {
		markableField.addClass('is-invalid');
	}

	let errorSmallElem = $(this).closest('.form-group').find('.field-error');
	if (errorSmallElem.length) {
		errorSmallElem.html('');
		errorSmallElem.hide();
	}
});

/**
 * Adds an error class to the given field
 * Finds a <small class="field-error"> element inside of a <div class="form-group"> with the given field. If found shows it with the given text.
 * @param field
 * @param errorText
 */
function showFieldError(field, errorText) {
	let errorSmallElem = field.closest('.form-group').find('.field-error');
	if (errorSmallElem.length) {
		errorSmallElem.html(errorText);
		errorSmallElem.show();
		field.addClass('is-invalid');
	}
};

function changeItemOrdering(target, delta) {
	let orderBlock = target.closest('.orderable');
	let opCode = orderBlock.attr("data-op-code");
	let itemToMove = target.closest('[data-orderby]');
	let additionalInfo = orderBlock.attr('data-additional-info');
	let items = orderBlock.find('[data-orderby]');
	items = $.grep(items, function(item, index) {
		return $(item).parent().attr("data-op-code") === opCode;
	});
	items = $(items);
	let itemToMovePos = items.index(itemToMove);
	let orderedItems = [];
	if (itemToMovePos + delta >= 0 && itemToMovePos + delta < items.length) {
		let orderby = $(items.get(itemToMovePos + delta)).attr('data-orderby');
		$(items.get(itemToMovePos + delta)).attr('data-orderby', $(items.get(itemToMovePos)).attr('data-orderby'));
		$(items.get(itemToMovePos)).attr('data-orderby', orderby);
		if (delta > 0) {
			$(items.get(itemToMovePos + delta)).after($(items.get(itemToMovePos)));
		} else {
			$(items.get(itemToMovePos + delta)).before($(items.get(itemToMovePos)));
		}
		items = orderBlock.find('[data-orderby]');
		items.each(function(indx, item) {
			$(item).find('.order-up').prop('hidden', indx == 0);
			$(item).find('.order-down').prop('hidden', indx == items.length - 1);
			let itemData = {};
			itemData.id = $(item).attr('data-id');
			itemData.code = $(item).attr('data-code');
			itemData.orderby = $(item).attr('data-orderby');
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
	if (deleteUrl === undefined) {
		var opCode = $(this).attr("data-op-code");
		var id = $(this).attr("data-id");
		var value = $(this).attr("data-value");
		deleteUrl = applicationUrl + 'delete_item?opCode=' + opCode + '&id=' + id;
		if (value !== undefined) {
			deleteUrl = deleteUrl + '&value=' + encodeURIComponent(value);
		}
	}
	var successCallbackName = $(this).attr("data-callback");
	var successCallbackFunc = undefined;
	if (successCallbackName) {
		successCallbackFunc = () => eval(successCallbackName);
	} else {
		successCallbackFunc = () => $('#refresh-details').trigger('click');
	}
	doPostDelete(deleteUrl, successCallbackFunc);
};

function initAddMultiDataDlg(theDlg) {

	theDlg.find('select.classifier-select').off('changed.bs.select').on('changed.bs.select', function(e) {
		theDlg.find('[name=value]').val($(this).val());
	});

	theDlg.find('.value-select').off('change').on('change', function(e) {
		theDlg.find('[name=value]').val($(this).val());
	});
	theDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, theDlg, 'Andmete lisamine ebaõnnestus.');
	});
	theDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		theDlg.find('.form-control').each(function(indx, item) {
			$(item).val(null);
		});
		theDlg.find('select').each(function(indx, item) {
			$(item).val($(item).find('option').first().val());
		});
		alignAndFocus(e, theDlg);
	});
};

function initGenericTextAddDlg(addDlg) {
	addDlg.find('[name=value]').val(null);
	addDlg.find('select').each(function(indx, item) {
		$(item).val($(item).find('option').first().val());
	});
	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, addDlg, 'Andmete lisamine ebaõnnestus.')
	});
	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		alignAndFocus(e, addDlg)
	});
};

function initGenericTextEditDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
};

function initSelectDlg(selectDlg) {
	let selectControl = selectDlg.find('select');
	configureSelectDlg(selectControl, selectDlg);
	selectControl.off('click').on('click', function(e) {
		submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
	});
	selectControl.off('changed.bs.select').on('changed.bs.select', function(e) {
		submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
	});
	selectControl.off('keydown').on('keydown', function(e) {
		if (e.key === "Enter") {
			submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
		}
	});
};

function configureSelectDlg(selectControl, selectDlg) {
	let maxItemLength = 0;
	selectControl.find('option').each(function(indx, item) {
		let itemLenght = $(item).text().length;
		if (itemLenght > maxItemLength) {
			maxItemLength = itemLenght;
		}
	});
	let dlgWidth = maxItemLength > 80 ? '85ch' : maxItemLength + 5 + 'ch';
	let numberOfOptins = selectControl.find('option').length;
	selectControl.attr('size', numberOfOptins > 20 ? 20 : numberOfOptins);
	selectDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		console.log(e.relatedTarget);
		let dlgTop = $(e.relatedTarget).offset().top - $(window).scrollTop();
		let dlgLeft = $(e.relatedTarget).offset().left - selectDlg.find('.modal-dialog').offset().left;
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
	let newWordDlg = $('#newWordDlg');
	newWordDlg.on('shown.bs.modal', function(e) {
		newWordDlg.find('.form-control').first().focus();
		newWordDlg.find('.form-control').each(function() {
			$(this).removeClass('is-invalid');
		});
		let searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (!searchValue.includes('*') && !searchValue.includes('?')) {
			newWordDlg.find('[name=wordValue]').val();
		} else {
			newWordDlg.find('[name=wordValue]').val(null);
		}
		let meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});

	newWordDlg.find('.form-control').on('change', function() {
		if ($(this).val()) {
			$(this).removeClass('is-invalid');
		} else {
			$(this).addClass('is-invalid');
		}
	});
	$(document).on("click", "#addWordSubmitBtn", function() {
		var addWordForm = $("#addWordForm");
		var isValid = checkRequiredFields(addWordForm);
		if (!isValid) {
			return;
		}
		addWordForm.submit();
	});
};

function checkRequiredFields(thisForm) {

	let isValid = true;

	// Do not change the selector to '.required-field' as it causes selectpicker to fire change event twice
	let requiredFields = thisForm.find('input.required-field:not(:hidden), select.required-field:not(:hidden), textarea.required-field:not(:hidden)');

	requiredFields.each(function() {
		let isRequiredRange = $(this).hasClass('required-range');
		let isSelectPicker = $(this).hasClass('classifier-select');
		let isMultiselect = isSelectPicker && $(this).hasClass('multi-select');
		let markableField = isSelectPicker ? $(this).parent() : $(this);

		let fldVal = $(this).val();

		if (!fldVal) {
			markableField.addClass('is-invalid');
			isValid = false;
		} else {
			markableField.removeClass('is-invalid');
		}

		if (isValid && isRequiredRange) {
			let minValue = $(this).attr('min');
			let maxValue = $(this).attr('max');
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
		let searchInput = addDlg.find("input[name='searchFilter']");
		if (searchInput.autocomplete('instance')) {
			searchInput.autocomplete('close');
		}
		let button = $(this);
		let content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		let theForm = $(this).closest('form');
		let url = theForm.attr('action') + '?' + theForm.serialize();
		$.get(url).done(function(data) {
			addDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
			$wpm.bindObjects();
			addDlg.find('button[data-source-id]').off('click').on('click', function(e) {
				e.preventDefault();
				let button = $(e.target);
				let sourceName = button.closest('.form-group').find('.form-control').val();
				let sourceId = button.data('source-id');
				let selectedSourceNameId = $("[name='source_" + sourceId + "']:checked").val();
				addDlg.find('[name=id2]').val(sourceId);
				addDlg.find('[name=id3]').val(selectedSourceNameId);
				addDlg.find('[name=value]').val(sourceName);
				let theForm = button.closest('form');

				if (checkRequiredFields(theForm)) {
					var successCallbackName = addDlg.attr("data-callback");
					var successCallbackFunc = undefined;
					if (successCallbackName) {
						successCallbackFunc = () => eval(successCallbackName);
					}
					submitForm(theForm, 'Andmete muutmine ebaõnnestus.', successCallbackFunc).always(function() {
						addDlg.modal('hide');
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
};

function initEditSourceLinkDlg(editDlg) {
	let sourceLinkContentKey = editDlg.find('input[name="opCode"]').val();
	let sourceLinkId = editDlg.find('input[name="id"]').val();
	let getSourceAndSourceLinkUrl = applicationUrl + 'source_and_source_link/' + sourceLinkContentKey + '/' + sourceLinkId;

	$.get(getSourceAndSourceLinkUrl).done(function(data) {
		editDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
}

$.fn.updateSourceLink = function() {
	var main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		let updateSourceLinkForm = main.closest('form[name="sourceLinkForm"]');
		let updateSourceLinkModal = updateSourceLinkForm.closest('.modal');
		let successCallbackName = updateSourceLinkModal.attr("data-callback");
		let successCallbackFunc = undefined;
		if (successCallbackName) {
			successCallbackFunc = () => eval(successCallbackName);
		}
		submitForm(updateSourceLinkForm, 'Andmete muutmine ebaõnnestus.', successCallbackFunc).always(function() {
			updateSourceLinkModal.modal('hide');
		});
	});
};

function initRelationDialogLogic(addDlg, idElementName) {
	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let button = $(this);
		let content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		let theForm = $(this).closest('form');
		let url = theForm.attr('action') + '?' + theForm.serialize();
		$.get(url).done(function(data) {
			addDlg.find('[data-name=dialogContent]').replaceWith(data);
			addDlg.find('button[data-' + idElementName + ']').off('click').on('click', function(e) {
				e.preventDefault();
				let button = $(e.target);
				addDlg.find('[name=id2]').val(button.data(idElementName));
				let theForm = button.closest('form');
				if (checkRequiredFields(theForm)) {
					submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
						addDlg.modal('hide');
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
};

function initMultiselectRelationDlg(dlg) {

	dlg.find('.form-control').val(null);
	dlg.find('[data-name=dialogContent]').html(null);
	dlg.find('[data-name=oppositeRelation]').hide();
	let selectElem = dlg.find('select');
	selectElem.val(selectElem.find('option').first().val());

	dlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let searchBtn = $(this);
		let content = searchBtn.html();
		searchBtn.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		let searchForm = $(this).closest('form');
		let searchUrl = searchForm.attr('action') + '?' + searchForm.serialize();
		const id = searchForm.parents('[data-id]:first').attr('data-id');

		$.get(searchUrl).done(function(data) {
			dlg.find('[data-name=dialogContent]').replaceWith(data);
			let addRelationsBtn = dlg.find('button[name="addRelationsBtn"]');

			let idsChk = dlg.find('input[name="ids"]');
			idsChk.on('change', function() {
				addRelationsBtn.prop('disabled', !idsChk.filter(":checked").length);
			});

			addRelationsBtn.off('click').on('click', function(e) {
				e.preventDefault();
				let selectRelationsForm = addRelationsBtn.closest('form');
				if (checkRequiredFields(selectRelationsForm)) {
					selectRelationsForm.find('select[name="oppositeRelationType"]').prop('disabled', false);
					$.ajax({
						url: selectRelationsForm.attr('action'),
						data: selectRelationsForm.serialize(),
						method: 'POST',
					}).done(function(data) {
						var successCallbackName = dlg.attr("data-callback");
						if (successCallbackName) {
							var successCallbackFunc = undefined;
							if (successCallbackName) {
								successCallbackFunc = () => eval(successCallbackName);
								successCallbackFunc();
							}
						}else{
							refreshDetailsSearch(id);
						}
						dlg.modal('hide');


					}).fail(function(data) {
						dlg.modal('hide');
						console.log(data);
						openAlertDlg('Seoste lisamine ebaõnnestus');
					});
				}
			});
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		}).always(function() {
			searchBtn.html(content);
		});
	});

	dlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		dlg.find('.form-control').first().focus();
		let relationTypeSelect = dlg.find("select[name='relationType']");
		changeOppositeRelationSelectData(relationTypeSelect);
	});

	$(document).on("change", "select[name='relationType']", function() {
		let relationTypeSelect = $(this);
		changeOppositeRelationSelectData(relationTypeSelect);
	});
};

function changeOppositeRelationSelectData(relationTypeSelect) {
	let relationTypeValue = relationTypeSelect.find('option:selected').val();
	let form = relationTypeSelect.closest('form');
	let oppositeRelationDiv = form.find('[data-name=oppositeRelation]');
	let entity = oppositeRelationDiv.data('entity');
	let oppositeRelationSelect = oppositeRelationDiv.find('select[name="oppositeRelationType"]');
	let getOppositeClassifiersUrl = applicationUrl + "oppositerelations";

	$.ajax({
		url: getOppositeClassifiersUrl,
		data: {entity: entity, relationType: relationTypeValue},
		method: 'GET',
	}).done(function(classifiers) {
		oppositeRelationSelect.children().remove();
		if (classifiers.length === 0) {
			oppositeRelationDiv.hide();
		} else if (classifiers.length === 1) {
			oppositeRelationSelect.append(new Option(classifiers[0].value, classifiers[0].code));
			oppositeRelationSelect.attr('disabled', 'disabled');
			oppositeRelationDiv.show();
		} else {
			oppositeRelationSelect.append(new Option('vali väärtus...', '', true, true));
			oppositeRelationSelect.children("option:selected").attr('disabled', 'disabled').attr('hidden', 'hidden');
			$.each(classifiers, function(index, classifier) {
				oppositeRelationSelect.append(new Option(classifier.value, classifier.code));
			});
			oppositeRelationSelect.removeAttr('disabled');
			oppositeRelationDiv.show();
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg("Viga vastassuuna andmete päringuga!");
	});
};

function decorateSourceLinks(detailsDiv) {
	detailsDiv.find('a[href]').each(function(indx, item) {
		let theLink = $(item);
		let href = theLink.attr('href');
		if (href.includes('_source_link:')) {
			theLink.attr('data-target', '#sourceDetailsDlg');
			theLink.attr('data-toggle', 'modal');
			theLink.on('click', function(e) {
				openSourceDetails(e.target);
			});
		}
	});
};

function openSourceDetails(elem) {
	let dlg = $($(elem).data('target'));
	let url = $(elem).attr('href');
	dlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});
};

function openMessageDlg(message) {
	openAlertDlg(message, false);
};

function openAlertDlg(alertMessage, showAsAlert = true) {
	let alertDlg = $('#alertDlg');
	alertDlg.find(('[name=alert_message]')).text(alertMessage);
	alertDlg.find('.alert-warning').prop('hidden', !showAsAlert);
	alertDlg.find('.alert-success').prop('hidden', showAsAlert);
	alertDlg.modal('show');
	alertDlg.find('.modal-footer button').focus();
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
	let isModalOpened = $("#waitDlg").hasClass('show');
	let timeout = 100;
	if (!isModalOpened) {
		timeout = 500;
	}
	setTimeout(function() {
		$("#waitDlg").modal("hide");
		$("body").css("cursor", "default");
	}, timeout);
};

function openConfirmDlg(confirmDlgHtml, callback, ...callbackArgs) {
	$('#confirmDlg').html(confirmDlgHtml);
	var confirmDlg = $('#confirmDlg');
	confirmDlg.modal('show');
	let okBtn = confirmDlg.find('.modal-footer [name=ok]');
	okBtn.focus();
	okBtn.off('click').on('click', function() {
		confirmDlg.modal('hide');
		callback(...callbackArgs);
	});
};

function initWordValueEditorDlg(dlg) {
	let editFld = dlg.find('[data-name=editFld]');
	let valueInput = dlg.find('[name=value]');
	let ekiEditorElem = dlg.find('.eki-editor');
	editFld.removeClass('is-invalid');
	editFld.html(valueInput.val());
	initEkiEditor(ekiEditorElem);

	dlg.find('button[name="saveWordValueBtn"]').off('click').on('click', function() {
		let form = dlg.find('form');
		if (editFld.html()) {
			let wordValue = editFld.html();
			wordValue = wordValue.replaceAll("&nbsp;", " ")
			valueInput.val(wordValue);
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function(data) {
				dlg.modal('hide');
				let wordId = dlg.find('[name=wordId]').val();
				let wordValueSpan = $('#word-value-' + wordId);
				wordValueSpan.html(data);
			}).fail(function(data) {
				dlg.modal('hide');
				console.log(data);
				openAlertDlg('Salvestamine ebaõnnestus');
			});
		} else {
			editFld.addClass('is-invalid');
		}
	});
};

function deleteLexemeAndWordAndMeaning() {
	var opName = "delete";
	var opCode = "lexeme";
	var lexemeId = $(this).attr("data-id");
	var successCallbackName = $(this).attr("data-callback");
	let successCallbackFunc = () => eval(successCallbackName)($(this));

	executeMultiConfirmPostDelete(opName, opCode, lexemeId, successCallbackFunc);
};

function deleteLexemeAndRusMeaningLexemes() {
	var opName = "delete";
	var opCode = "rus_meaning_lexemes";
	var element = $(this);
	var lexemeId = element.attr("data-id");
	var successCallbackName = $(this).attr("data-callback");
	let successCallbackFunc = () => eval(successCallbackName)($(this));


	executeMultiConfirmPostDelete(opName, opCode, lexemeId, function() {
		const detailsLength = element.parents('[data-rel="details-area"]:first').find('[id*="lexeme-details-"]').length;
		if (detailsLength <= 1) {
			successCallbackFunc();
		} else {
			element.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first').trigger('click');
		}
	}, true);
};

function executeMultiConfirmPostDelete(opName, opCode, id, successCallbackFunc, force) {
	let deleteUrl = applicationUrl + 'delete_item?opCode=' + opCode + '&id=' + id;
	var confirmationOpUrl = applicationUrl + "confirm_op";
	var dataObj = {
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
		openAlertDlg("Kustutamine ebaõnnestus");
	});
};

function initClassifierAutocomplete() {
	$('.classifier-select').selectpicker({
		width: '100%',
		container: 'body'
	});
};

function refreshDetails() {
	$('#refresh-details').trigger('click');
}

function validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage) {
	$.ajax({
		url: validateJoinUrl,
		data: joinForm.serialize(),
		method: 'POST',
	}).done(function(data) {
		let response = JSON.parse(data);
		if (response.status === 'valid') {
			joinForm.submit();
		} else if (response.status === 'invalid') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg(failMessage);
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
};