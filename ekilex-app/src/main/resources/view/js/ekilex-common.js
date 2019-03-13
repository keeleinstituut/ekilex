// Common javascript methods

function selectDatasets(selection) {
    $('#datasetSelectDlg').find(':checkbox').prop('checked', selection)
}

function displayDetailConditionButtons() {
    $('[name="removeDetailConditionBtn"]').each(function(i, v) {
        let groupElement = $(this).closest('[name="detailGroup"]');
        if (groupElement.find('[name="detailCondition"]').length === 1 ) {
            $(this).hide();
        } else {
            $(this).show();
        }
    });
}

function displayDetailGroupButtons() {
    if ($('[name="detailGroup"]').length === 1 ) {
        $('[name="removeDetailGroupBtn"]').hide();
    } else {
        $('[name="removeDetailGroupBtn"]').show();
    }
}

function displaySimpleSearch() {
    $("#simple_search_filter").prop('hidden', false);
    $("#detail_search_filter").prop('hidden', true);
    $('#searchMode').val('SIMPLE');
    $('#searchModeBtn').text('Detailotsing');
}

function displayDetailSearch() {
    $("#simple_search_filter").prop('hidden', true);
    $("#detail_search_filter").prop('hidden', false);
    $('#searchMode').val('DETAIL');
    $('#searchModeBtn').text('Lihtotsing');
}

function toggleSearch() {
    let currentSearchMode = $('#searchMode').val();
    if (currentSearchMode === 'SIMPLE') {
        displayDetailSearch();
    } else {
        displaySimpleSearch();
    }
}

function initaliseSearchForm() {
    $('#searchModeBtn').on('click', toggleSearch);
    let datasetDlg = $('#datasetSelectDlg');
    datasetDlg.on('shown.bs.modal', () => {
        datasetDlg.find('.btn').first().focus();
    })
}

function initialiseDeatailSearch() {
    displayDetailConditionButtons();
    displayDetailGroupButtons();

    $(document).on("click", ":button[name='removeDetailConditionBtn']", function() {
        $(this).closest('[name="detailCondition"]').remove();
        displayDetailConditionButtons();
    });

    $(document).on("click", ":button[name='removeDetailGroupBtn']", function() {
        $(this).closest('[name="detailGroup"]').remove();
        displayDetailGroupButtons();
    });

    $(document).on("change", "select[name$='entity']", function() {

    	let searchEntityVal = $(this).val();
        let detailGroupElement = $(this).closest('[name="detailGroup"]');
        while (detailGroupElement.find('[name="detailCondition"]').length > 1) {
            detailGroupElement.find('[name="detailCondition"]').last().remove();
        }
        let conditionElement = detailGroupElement.find('[name="detailCondition"]').first();
        let searchKeyElement = conditionElement.find('[name$="searchKey"]');
        let keyTemplate = $('#searchKeyTemplates').find('[name="' + searchEntityVal + '"]');
        searchKeyElement.find('option').remove();
        searchKeyElement.append(keyTemplate.html());
        searchKeyElement.val(searchKeyElement.find('option').first().val());
        initCondition(conditionElement);
    });

    $(document).on("change", "select[name$='searchKey']", function() {

    	let searchKeyVal = $(this).val();
        let searchOperandElement = $(this).closest('[name="detailCondition"]').find('[name$="searchOperand"]');
        let operandTemplate = $('#searchOperandTemplates').find('[name="' + searchKeyVal + '"]');
        searchOperandElement.find('option').remove();
        searchOperandElement.append(operandTemplate.html());
        searchOperandElement.val(searchOperandElement.find('option').first().val());

        // should lookup by search key + operand
        let searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
        let templateElement = $('#searchValueTemplates').find('[name="' + searchKeyVal + '"]');
        let copyOfValueTemplate = $(templateElement.html());
        copyOfValueTemplate.attr('name', searchValueElement.attr('name'));
        searchValueElement.closest('div').attr('class', templateElement.attr('class'));
        searchValueElement.replaceWith(copyOfValueTemplate);
    });

    $(document).on("change", "select[name$='searchOperand']", function() {

    	let searchOperandVal = $(this).val();
    	let searchKeyElement = $(this).closest('[name="detailCondition"]').find('[name$="searchKey"] option:selected');
    	let searchKeyVal = searchKeyElement.val();

    	let searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
    	if (searchOperandVal == 'NOT_EXISTS') {
    		searchValueElement.empty();
    		searchValueElement.prop('hidden', true);
    	} else {
    		let templateElement = $('#searchValueTemplates').find('[name="' + searchKeyVal + '"]');
            let copyOfValueTemplate = $(templateElement.html());
            copyOfValueTemplate.attr('name', searchValueElement.attr('name'));
            searchValueElement.closest('div').attr('class', templateElement.attr('class'));
            searchValueElement.replaceWith(copyOfValueTemplate);
    	}
    });

    $(document).on("click", ":button[name='addDetailConditionBtn']", function() {
        let detailGroupElement = $(this).closest('[name="detailGroup"]');
        let addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
        initCondition(addedConditionElement);
    });

    $(document).on("click", ":button[name='addDetailGroupBtn']", function() {
        let detailSearchElement = $("#detail_search_filter");
        let addedGroupElement = createAndAttachCopyFromLastItem(detailSearchElement, 'detailGroup', 'criteriaGroups');
        initConditionGroup(addedGroupElement);
    });
}

function createAndAttachCopyFromLastItem(parentElement, itemName, indexName) {
    let lastElement = parentElement.find('[name="' + itemName + '"]').last();
    let copyOfLastElement = lastElement.clone();
    let oldIndex = copyOfLastElement.data('index');
    let newIndex = oldIndex + 1;
    let oldIndexVal = indexName + '[' + oldIndex + ']';
    let newIndexVal = indexName + '[' + newIndex + ']';
    copyOfLastElement.attr('data-index', newIndex);
    copyOfLastElement.find('[name*="' + indexName + '["]').each(function(i, v) {
        $(this).attr('name', $(this).attr('name').replace(oldIndexVal, newIndexVal))
    });
    copyOfLastElement.find('input').val(null);
    lastElement.after(copyOfLastElement);
    return parentElement.find('[name="' + itemName + '"]').last();
}

function initConditionGroup(groupElement) {
    let entitySelect = groupElement.find('select[name$="entity"]');
    entitySelect.val(entitySelect.find('option').first().val());
    entitySelect.trigger('change');
    displayDetailGroupButtons();
}

function initCondition(conditionElement) {
    let searchKeySelect = conditionElement.find('select[name$="searchKey"]');
    searchKeySelect.val(searchKeySelect.find('option').first().val());
    searchKeySelect.trigger('change');
    displayDetailConditionButtons();
}

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
        items.each(function (indx, item) {
            $(item).find('.order-up').prop('hidden', indx == 0);
            $(item).find('.order-down').prop('hidden', indx == items.length - 1);
            let itemData = {};
            itemData.id = $(item).attr('data-id');
            itemData.code = $(item).attr('data-code');
            itemData.orderby = $(item).attr('data-orderby');
            orderedItems.push(itemData);
        });
    }
    return {opCode: opCode, items: orderedItems};
}

function postJson(url, dataObject, failMessage = 'Salvestamine ebaõnnestus.') {
    return $.ajax({
        url: url,
        data: JSON.stringify(dataObject),
        method: 'POST',
        dataType: 'json',
        contentType: 'application/json'
    }).fail(function (data) {
        console.log(data);
        openAlertDlg(failMessage);
    });
}

function openEditDlg(elem) {
	let targetName = $(elem).data('target-elem');
	let targetElement = $('[name="' + targetName + '"]');
	let editDlg = $($(elem).data('target'));
	let modifyFld = editDlg.find('[name=value]');
	modifyFld.val(targetElement.data('value') === undefined ? targetElement.text() : targetElement.data('value'));
	editDlg.find('[name=id]').val(targetElement.data('id'));
	editDlg.find('[name=opCode]').val(targetElement.data('op-code'));
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
}

function performDelete() {
    let targetName = $(this)[0].getAttribute('data-target-elem');
    let targetElement = targetName === null ? $(this) : $('[name="' + targetName + '"]');
    let currentValue = typeof targetElement.data('value') === 'object' ? JSON.stringify(targetElement.data('value')) : targetElement.data('value');
    let removeUrl = applicationUrl + 'remove_item?opCode=' + targetElement.data('op-code') + '&id=' + targetElement.data('id') + '&value=' + encodeURIComponent(currentValue);
    let validateUrl = applicationUrl + 'remove_item_validate?opCode=' + targetElement.data('op-code') + '&id=' + targetElement.data('id');
    let callbackFunc = $.noop;
    let callbackName = targetElement.data('callback');
    if (callbackName !== undefined) {
        callbackFunc = () => eval(callbackName)($(this))
    }
    $.post(validateUrl).done(function(data) {
        let response = JSON.parse(data);
        if (response.status === 'ok') {
            doPostDelete(removeUrl, callbackFunc);
        } else if (response.status === 'confirm') {
            openConfirmDlg(response.question, function () {
                doPostDelete(removeUrl, callbackFunc)
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

function doPostDelete(removeUrl, callbackFunc) {
    $.post(removeUrl).done(function() {
        $('#refresh-details').trigger('click');
        callbackFunc();
    }).fail(function(data) {
        openAlertDlg("Andmete eemaldamine ebaõnnestus.");
        console.log(data);
    });
}

function removeClosestRow(elem) {
    elem.closest('tr').remove();
}

function openAddDlg(elem) {
    let addDlg = $($(elem).data('target'));
	addDlg.find('[name=id]').val($(elem).data('id'));
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

function openSelectDlg(elem) {
    let selectDlg = $($(elem).data('target'));
    let targetElement = $(elem);
    if ($(elem).data('target-elem')) {
        targetElement = $('[name=' + $(elem).data('target-elem') + ']');
    }
    let currentValue = typeof targetElement.data('value') === 'object' ? JSON.stringify(targetElement.data('value')) : targetElement.data('value');
    selectDlg.find('[name=id]').val(targetElement.data('id'));
    selectDlg.find('[name=currentValue]').val(currentValue);
    let selectElem = selectDlg.find('select');
    if (currentValue === undefined) {
        selectElem.val(selectElem.find('option').first().val());
    } else {
        selectElem.val(currentValue);
    }
}

function initSelectDlg(selectDlg) {
    let selectControl = selectDlg.find('select');
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

    selectControl.off('click').on('click', function(e) {submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')});
    selectControl.off('keydown').on('keydown', function(e) {
        if (e.key === "Enter") {
            submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
        }
    });
    selectDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        let dlgTop =  $(e.relatedTarget).offset().top;
        let dlgLeft =  $(e.relatedTarget).offset().left - selectDlg.find('.modal-dialog').offset().left;
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

function submitDialog(e, dlg, failMessage, callback = $.noop) {
    e.preventDefault();
    let theForm = dlg.find('form');

    submitForm(theForm, failMessage, callback).always(function () {
        dlg.modal('hide');
    });
}

function submitForm(theForm, failMessage, callback = $.noop) {
    return $.ajax({
        url: theForm.attr('action'),
        data: JSON.stringify(theForm.serializeJSON()),
        method: 'POST',
        dataType: 'json',
        contentType: 'application/json'
    }).done(function(data) {
        $('#refresh-details').trigger('click');
        callback();
    }).fail(function (data) {
        console.log(data);
        alert(failMessage);
    });
}

function alignAndFocus(e, dlg) {
    dlg.find('.form-control').first().focus();
    let dlgTop =  $(e.relatedTarget).offset().top - dlg.find('.modal-content').height() - 30;
    if (dlgTop < 0 ) {
        dlgTop = 0;
    }
    dlg.find('.modal-content').css('top', dlgTop);
}

function toggleValueGroup(dlg, groupName) {
    if (groupName.startsWith('#')) {
        const lexemeId = dlg.find('[name=id]').val();
        let lexemeDetailDiv = $('[data-toggle-name=lexBody_' + lexemeId + ']');
        let addNewElementBtn = lexemeDetailDiv.find('[data-btn-id="' + groupName + '"]');
        addNewElementBtn.trigger('click');
        dlg.modal('hide');
    } else {
        dlg.find('.value-group').hide();
        dlg.find('[data-id=' + groupName + ']').show();
        dlg.find('[data-id=' + groupName + ']').find('.value-select').trigger('change');
    }
}

function initMultiValueAddDlg(theDlg) {
    theDlg.find('[name=opCode]').off('change').on('change', function(e) {toggleValueGroup(theDlg, $(e.target).val())});
    theDlg.find('.value-select').off('change').on('change', function(e) {
        theDlg.find('[name=value]').val($(this).val());
    });
    theDlg.find('button[type="submit"]').off('click').on('click', function(e) {submitDialog(e, theDlg, 'Andmete lisamine ebaõnnestus.')});
    theDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        theDlg.find('.form-control').each(function (indx, item) {
            $(item).val(null);
        });
        theDlg.find('select').each(function (indx, item) {
            $(item).val($(item).find('option').first().val());
        });
        toggleValueGroup(theDlg, theDlg.find('[name=opCode]').val());
        alignAndFocus(e, theDlg);
    });
}

function decorateSourceLinks() {
	let detailsDiv = $('#details_div');
	detailsDiv.find('a').each(function(indx, item) {
		let theLink = $(item);
		if (theLink.attr('href').includes('_source_link:')) {
			theLink.attr('data-target', '#detailsDlg');
			theLink.attr('data-toggle', 'modal');
			theLink.on('click', function(e) {
				openDetailsDiv(e.target);
			});
		}
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
}

function openAddSourceLinkDlg(elem) {
    let addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=opCode]').val($(elem).data('op-code'));
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
                addDlg.find('[name=id2]').val(button.data('source-id'));
                addDlg.find('[name=value]').val(sourceName);
                let theForm = button.closest('form');
                submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
                    addDlg.modal('hide');
                });
            });
        }).fail(function(data) {
            console.log(data);
            alert(failMessage);
        }).always(function () {
            button.html(content);
        });
    });

    addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        addDlg.find('.form-control').first().focus();
    });
}

function openDetailsDiv(elem) {
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

function openClassifiersDlg(elem) {
    let theDlg = $($(elem).data('target'));
    theDlg.find('[name=id]').val($(elem).data('lexeme-id'));
    theDlg.find('[name=id2]').val($(elem).data('meaning-id'));
    theDlg.find('[name=id3]').val($(elem).data('word-id'));
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
        }).always(function () {
            button.html(content);
        });
    });

    addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        addDlg.find('.form-control').first().focus();
    });
}

function openAddNewMeaningRelationDlg(elem) {
    let addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=meaningId]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    let selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());
    initRelationDialogLogic(addDlg, 'meaning-id');
}

function openAddNewLexemeRelationDlg(elem) {
    let addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=lexemeId]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    let selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());
    initRelationDialogLogic(addDlg, 'lexeme-id');
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
    okBtn.off('click').on('click', function () {
        alertDlg.modal('hide');
        callback();
    });
}
