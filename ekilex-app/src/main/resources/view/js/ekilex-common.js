function postJson(url, dataObject, failMessage = 'Salvestamine ebaõnnestus.') {
	return $.ajax({
		url: url,
		data: JSON.stringify(dataObject),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(failMessage);
	});
}

function doPostDelete(deleteUrl, callbackFunc) {
	$.post(deleteUrl).done(function() {
		callbackFunc();
	}).fail(function(data) {
		openAlertDlg("Andmete eemaldamine ebaõnnestus.");
		console.log(data);
	});
}

function submitDialog(e, dlg, failMessage, callback = $.noop) {
	e.preventDefault();
	let theForm = dlg.find('form');

	submitForm(theForm, failMessage, callback).always(function() {
		dlg.modal('hide');
	});
}

function submitForm(theForm, failMessage, callback = $.noop) {
	var data = JSON.stringify(theForm.serializeJSON());
	return $.ajax({
		url: theForm.attr('action'),
		data: data,
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(data) {
		$('#refresh-details').trigger('click');
		callback();
	}).fail(function(data) {
		console.log(data);
		alert(failMessage);
	});
}

function alignAndFocus(e, dlg) {
	dlg.find('.form-control').first().focus();
	if (e.relatedTarget) {
		let dlgTop = ($(e.relatedTarget).offset().top - $(window).scrollTop()) - dlg.find('.modal-content').height() - 30;
		if (dlgTop < 0) {
			dlgTop = 0;
		}
		dlg.find('.modal-content').css('top', dlgTop);
	}
}

$(document).on("change", "select.common-word-dataset-select[name='dataset']", function() {
	var datasetCode = $(this).val();
	var permLanguageSelect = $("#wordPermLanguageSelect");
	if (datasetCode) {
		var getLanguageSelectUrl = applicationUrl + "comp/commonwordlangselect/" + datasetCode;
		$.get(getLanguageSelectUrl).done(function(data) {
			permLanguageSelect.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			alert('Viga!');
		});
	} else {
		permLanguageSelect.empty();
	}
});

function changeItemOrdering(target, delta) {
	let orderBlock = target.closest('.orderable');
	let opCode = orderBlock.attr("data-op-code");
	let itemToMove = target.closest('[data-orderby]');
	let items = orderBlock.find('[data-orderby]');
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
		items: orderedItems
	};
}

function executeValidateDelete() {
	var opCode = $(this).attr("data-op-code");
	var id = $(this).attr("data-id");
	var callback = $(this).attr("data-callback");
	let callbackFunc = () => eval(callback)($(this))
	let validateUrl = applicationUrl + 'validate_delete_item?opCode=' + opCode + '&id=' + id;
	let deleteUrl = applicationUrl + 'delete_item?opCode=' + opCode + '&id=' + id;
	$.post(validateUrl).done(function(data) {
		let response = JSON.parse(data);
		if (response.status === 'ok') {
			doPostDelete(deleteUrl, callbackFunc);
		} else if (response.status === 'confirm') {
			openConfirmDlg(response.question, function() {
				doPostDelete(deleteUrl, callbackFunc)
			});
		} else if (response.status === 'invalid') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg("Andmete eemaldamine ebaõnnestus.");
		}
	}).fail(function(data) {
		openAlertDlg("Andmete eemaldamine ebaõnnestus.");
		console.log(data);
	});
}

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
	let callbackFunc = () => $('#refresh-details').trigger('click');
	doPostDelete(deleteUrl, callbackFunc);
}

function initAddMultiDataDlg(theDlg) {
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
}

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
}

function initGenericTextEditDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
}

function initSelectDlg(selectDlg) {
	let selectControl = selectDlg.find('select');
	configureSelectDlg(selectControl, selectDlg);
	selectControl.off('click').on('click', function(e) {
		submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
	});
	selectControl.off('keydown').on('keydown', function(e) {
		if (e.key === "Enter") {
			submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
		}
	});
}

function configureSelectDlg(selectControl, selectDlg) {
	let maxItemLength = 0;
	selectControl.find('option').each(function (indx, item) {
		let itemLenght = $(item).text().length;
		if (itemLenght > maxItemLength) {
			maxItemLength = itemLenght;
		}
	});
	let dlgWidth = maxItemLength > 80 ? '85ch' : maxItemLength + 5 + 'ch';
	let numberOfOptins = selectControl.find('option').length;
	selectControl.attr('size', numberOfOptins > 20 ? 20 : numberOfOptins);
	selectDlg.off('shown.bs.modal').on('shown.bs.modal', function (e) {
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
}

function initNewWordDlg() {
	let newWordDlg = $('#newWordDlg');
	newWordDlg.on('shown.bs.modal', function(e) {
		newWordDlg.find('.form-control').first().focus();
		let searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (!searchValue.includes('*') && !searchValue.includes('?')) {
			newWordDlg.find('[name=value]').val(searchValue);
		} else {
			newWordDlg.find('[name=value]').val(null);
		}
		let meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});
	$(document).on("click", "#addWordSubmitBtn", function() {
		var addWordForm = $("#addWordForm");
		var formVal;
		formVal = addWordForm.find("select[name='dataset']").val();
		if (formVal == '') {
			return;
		}
		formVal = addWordForm.find("select[name='language']").val();
		if (formVal == '') {
			return;
		}
		formVal = addWordForm.find("select[name='morphCode']").val();
		if (formVal == '') {
			return;
		}
		formVal = addWordForm.find("input[name='value']").val();
		if (formVal == '') {
			return;
		}
		addWordForm.submit();
	});
}

function initAddSourceLinkDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=sourceLinkDlgContent]').html(null);

	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let button = $(this);
		let content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		let theForm = $(this).closest('form');
		let url = theForm.attr('action') + '?' + theForm.serialize();
		$.get(url).done(function(data) {
			addDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
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
				submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
					addDlg.modal('hide');
				});
			});
		}).fail(function(data) {
			console.log(data);
			alert(failMessage);
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
}

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
				submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
					addDlg.modal('hide');
				});
			});
		}).fail(function(data) {
			console.log(data);
			alert(failMessage);
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
}

function initAddMeaningRelationDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'meaning-id');
}

function initAddLexemeRelationDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'lexeme-id');
}

function decorateSourceLinks() {
	let detailsDiv = $('#details_div');
	detailsDiv.find('a').each(function(indx, item) {
		let theLink = $(item);
		if (theLink.attr('href').includes('_source_link:')) {
			theLink.attr('data-target', '#sourceDetailsDlg');
			theLink.attr('data-toggle', 'modal');
			theLink.on('click', function(e) {
				openSourceDetails(e.target);
			});
		}
	});
}

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
}

function openMessageDlg(message) {
	openAlertDlg(message, false);
}

function openAlertDlg(alertMessage, showAsAlert = true) {
	let alertDlg = $('#alertDlg');
	alertDlg.find(('[name=alert_message]')).text(alertMessage);
	alertDlg.find('.alert-warning').prop('hidden', !showAsAlert);
	alertDlg.find('.alert-success').prop('hidden', showAsAlert);
	alertDlg.modal('show');
	alertDlg.find('.modal-footer button').focus();
}

function openConfirmDlg(confirmQuestion, callback) {
	let alertDlg = $('#confirmDlg');
	alertDlg.find(('[name=confirm_question]')).text(confirmQuestion);
	alertDlg.modal('show');
	let okBtn = alertDlg.find('.modal-footer [name=ok]');
	okBtn.focus();
	okBtn.off('click').on('click', function() {
		alertDlg.modal('hide');
		callback();
	});
}