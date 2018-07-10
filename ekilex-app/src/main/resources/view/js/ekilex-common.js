// Common javascript methods

function selectDatasets(selection) {
    $('#dataset_select').find(':checkbox').prop('checked', selection)
}

function displayDetailConditionButtons() {
    $('[name="removeDetailConditionBtn"]').each(function(i, v) {
        var groupElement = $(this).closest('[name="detailGroup"]');
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
    var currentSearchMode = $('#searchMode').val();
    if (currentSearchMode === 'SIMPLE') {
        displayDetailSearch();
    } else {
        displaySimpleSearch();
    }
}

function initaliseSearchForm() {
    $('#searchModeBtn').on('click', toggleSearch);
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

    	var searchEntityVal = $(this).val();
        var detailGroupElement = $(this).closest('[name="detailGroup"]');
        while (detailGroupElement.find('[name="detailCondition"]').length > 1) {
            detailGroupElement.find('[name="detailCondition"]').last().remove();
        }
        var conditionElement = detailGroupElement.find('[name="detailCondition"]').first();
        var searchKeyElement = conditionElement.find('[name$="searchKey"]');
        var keyTemplate = $('#searchKeyTemplates').find('[name="' + searchEntityVal + '"]');
        searchKeyElement.find('option').remove();
        searchKeyElement.append(keyTemplate.html());
        searchKeyElement.val(searchKeyElement.find('option').first().val());
        initCondition(conditionElement);
    });

    $(document).on("change", "select[name$='searchKey']", function() {

    	var searchKeyVal = $(this).val();
        var searchOperandElement = $(this).closest('[name="detailCondition"]').find('[name$="searchOperand"]');
        var operandTemplate = $('#searchOperandTemplates').find('[name="' + searchKeyVal + '"]');
        searchOperandElement.find('option').remove();
        searchOperandElement.append(operandTemplate.html());
        searchOperandElement.val(searchOperandElement.find('option').first().val());

        // should lookup by search key + operand
        var searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
        var templateElement = $('#searchValueTemplates').find('[name="' + searchKeyVal + '"]');
        var copyOfValueTemplate = $(templateElement.html());
        copyOfValueTemplate.attr('name', searchValueElement.attr('name'));
        searchValueElement.closest('div').attr('class', templateElement.attr('class'));
        searchValueElement.replaceWith(copyOfValueTemplate);
    });

    $(document).on("change", "select[name$='searchOperand']", function() {

    	var searchOperandVal = $(this).val();
    	var searchKeyElement = $(this).closest('[name="detailCondition"]').find('[name$="searchKey"] option:selected');
    	var searchKeyVal = searchKeyElement.val();

    	var searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
    	if (searchOperandVal == 'NOT_EXISTS') {
    		searchValueElement.empty();
    		searchValueElement.prop('hidden', true);
    	} else {
    		var templateElement = $('#searchValueTemplates').find('[name="' + searchKeyVal + '"]');
            var copyOfValueTemplate = $(templateElement.html());
            copyOfValueTemplate.attr('name', searchValueElement.attr('name'));
            searchValueElement.closest('div').attr('class', templateElement.attr('class'));
            searchValueElement.replaceWith(copyOfValueTemplate);
    	}
    });

    $(document).on("click", ":button[name='addDetailConditionBtn']", function() {
        var detailGroupElement = $(this).closest('[name="detailGroup"]');
        var addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
        initCondition(addedConditionElement);
    });

    $(document).on("click", ":button[name='addDetailGroupBtn']", function() {
        var detailSearchElement = $("#detail_search_filter");
        var addedGroupElement = createAndAttachCopyFromLastItem(detailSearchElement, 'detailGroup', 'criteriaGroups');
        initConditionGroup(addedGroupElement);
    });
}

function createAndAttachCopyFromLastItem(parentElement, itemName, indexName) {
    var lastElement = parentElement.find('[name="' + itemName + '"]').last();
    var copyOfLastElement = lastElement.clone();
    var oldIndex = copyOfLastElement.data('index');
    var newIndex = oldIndex + 1;
    var oldIndexVal = indexName + '[' + oldIndex + ']';
    var newIndexVal = indexName + '[' + newIndex + ']';
    copyOfLastElement.attr('data-index', newIndex);
    copyOfLastElement.find('[name*="' + indexName + '["]').each(function(i, v) {
        $(this).attr('name', $(this).attr('name').replace(oldIndexVal, newIndexVal))
    });
    copyOfLastElement.find('input').val(null);
    lastElement.after(copyOfLastElement);
    return parentElement.find('[name="' + itemName + '"]').last();
}

function initConditionGroup(groupElement) {
    var entitySelect = groupElement.find('select[name$="entity"]');
    entitySelect.val(entitySelect.find('option').first().val());
    entitySelect.trigger('change');
    displayDetailGroupButtons();
}

function initCondition(conditionElement) {
    var searchKeySelect = conditionElement.find('select[name$="searchKey"]');
    searchKeySelect.val(searchKeySelect.find('option').first().val());
    searchKeySelect.trigger('change');
    displayDetailConditionButtons();
}

function changeItemOrdering(target, delta) {
    var orderBlock = target.closest('.orderable');
    var opCode = orderBlock.attr("data-op-code");
    var itemToMove = target.closest('[data-orderby]');
    var items = orderBlock.find('[data-orderby]');
    var itemToMovePos = items.index(itemToMove);
    var orderedItems = [];
    if (itemToMovePos + delta >= 0 && itemToMovePos + delta < items.length) {
        var orderby = $(items.get(itemToMovePos + delta)).attr('data-orderby');
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
            var itemData = {};
            itemData.id = $(item).attr('data-id');
            itemData.code = $(item).attr('data-code');
            itemData.orderby = $(item).attr('data-orderby');
            orderedItems.push(itemData);
        });
    }
    return {opCode: opCode, items: orderedItems};
}

function postJson(url, dataObject) {
    return $.ajax({
        url: url,
        data: JSON.stringify(dataObject),
        method: 'POST',
        dataType: 'json',
        contentType: 'application/json'
    }).fail(function (data) {
        console.log(data);
        alert('Salvestamine ebaõnnestus.');
    });
}

function openEditDlg(elem) {
	var targetName = $(elem).data('target-elem');
	var targetElement = $('[name="' + targetName + '"]');
	var editDlg = $('#editDlg');
	var modifyFld = editDlg.find('[name=value]');
	modifyFld.val(targetElement.data('value') != undefined ? targetElement.data('value') : targetElement.text());
	editDlg.find('[name=id]').val(targetElement.data('id'));
	editDlg.find('[name=opCode]').val(targetElement.data('op-code'));
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
}

function performDelete() {
    var targetName = $(this)[0].getAttribute('data-target-elem');
    var targetElement = $('[name="' + targetName + '"]');
    var currentValue = typeof targetElement.data('value') === 'object' ? JSON.stringify(targetElement.data('value')) : targetElement.data('value');
    var url = applicationUrl + 'remove_item?opCode=' + targetElement.data('op-code') + '&id=' + targetElement.data('id') + '&value=' + encodeURIComponent(currentValue);
    $.post(url).done(function(data) {
        var refreshButton = $('#refresh-details');
        refreshButton.trigger('click');
    }).fail(function(data) {
        alert("Andmete eemaldamine ebaõnnestus.");
        console.log(data);
    });
}

function openAddDlg(elem) {
    var addDlg = $($(elem).data('target'));
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
    var selectDlg = $($(elem).data('target'));
    var targetElement = $(elem);
    if ($(elem).data('target-elem')) {
        targetElement = $('[name=' + $(elem).data('target-elem') + ']');
    }
    var currentValue = typeof targetElement.data('value') === 'object' ? JSON.stringify(targetElement.data('value')) : targetElement.data('value');
    selectDlg.find('[name=id]').val(targetElement.data('id'));
    selectDlg.find('[name=currentValue]').val(currentValue);
    var selectElem = selectDlg.find('select');
    if (currentValue === undefined) {
        selectElem.val(selectElem.find('option').first().val());
    } else {
        selectElem.val(currentValue);
    }
}

function initSelectDlg(selectDlg) {
    var selectControl = selectDlg.find('select');
    var maxItemLength = 0;
    selectControl.find('option').each(function(indx, item) {
        var itemLenght = $(item).text().length;
        if (itemLenght > maxItemLength) {
            maxItemLength = itemLenght;
        }
    });
    var dlgWidth = maxItemLength > 80 ? '85ch' : maxItemLength + 5 + 'ch';
    var numberOfOptins = selectControl.find('option').length;
    selectControl.attr('size', numberOfOptins > 20 ? 20 : numberOfOptins);

    selectControl.off('click').on('click', function(e) {submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')});
    selectControl.off('keydown').on('keydown', function(e) {
        if (e.key === "Enter") {
            submitDialog(e, selectDlg, 'Andmete muutmine ebaõnnestus.')
        }
    });
    selectDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        var dlgTop =  $(e.relatedTarget).offset().top;
        var dlgLeft =  $(e.relatedTarget).offset().left - selectDlg.find('.modal-dialog').offset().left;
        selectDlg.find('.modal-content').css('top', dlgTop - 30);
        selectDlg.find('.modal-content').css('left', dlgLeft);
        selectDlg.find('.modal-content').css('width', dlgWidth);
        selectDlg.find('.form-control').first().focus();
        $('.modal-backdrop').css('opacity', 0);
    });
}

function submitDialog(e, dlg, failMessage) {
    e.preventDefault();
    var theForm = dlg.find('form');

    submitForm(theForm, failMessage).always(function () {
        dlg.modal('hide');
    });
}

function submitForm(theForm, failMessage) {
    return $.ajax({
        url: theForm.attr('action'),
        data: JSON.stringify(theForm.serializeJSON()),
        method: 'POST',
        dataType: 'json',
        contentType: 'application/json'
    }).done(function(data) {
        $('#refresh-details').trigger('click');
    }).fail(function (data) {
        console.log(data);
        alert(failMessage);
    });
}

function alignAndFocus(e, dlg) {
    dlg.find('.form-control').first().focus();
    var dlgTop =  $(e.relatedTarget).offset().top - dlg.find('.modal-content').height() - 30;
    dlg.find('.modal-content').css('top', dlgTop);
}

function toggleValueGroup(dlg, groupName) {
    dlg.find('.value-group').hide();
    dlg.find('[data-id=' + groupName + ']').show();
    dlg.find('[data-id=' + groupName + ']').find('.value-select').trigger('change');
}

function openUsageMemberDlg(elem) {
    var theDlg = $($(elem).data('target'));
    theDlg.find('[name=id]').val($(elem).data('id'));
}

function initMultiValueAddDlg(theDlg, resetElements) {
    theDlg.find('[name=opCode]').off('change').on('change', function(e) {toggleValueGroup(theDlg, $(e.target).val())});
    theDlg.find('.value-select').off('change').on('change', function(e) {
        theDlg.find('[name=value]').val($(this).val());
    });
    theDlg.find('button[type="submit"]').off('click').on('click', function(e) {submitDialog(e, theDlg, 'Andmete lisamine ebaõnnestus.')});
    theDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        if (resetElements) {
            theDlg.find('.form-control').each(function (indx, item) {
                $(item).val(null);
            });
            theDlg.find('select').each(function (indx, item) {
                $(item).val($(item).find('option').first().val());
            });
            toggleValueGroup(theDlg, theDlg.find('[name=opCode]').val());
        }
        alignAndFocus(e, theDlg);
    });
}

function decorateSourceLinks() {
	var detailsDiv = $('#details_div');
	detailsDiv.find('a').each(function(indx, item) {
		var theLink = $(item);
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
    var newWordDlg = $('#newWordDlg');
    newWordDlg.on('shown.bs.modal', function() {
        newWordDlg.find('.form-control').first().focus();
        var searchValue = $("input[name='simpleSearchFilter']").val() || '';
        if (!searchValue.includes('*') && !searchValue.includes('?')) {
            newWordDlg.find('[name=value]').val(searchValue);
        } else {
            newWordDlg.find('[name=value]').val(null);
        }
        var firstSelectedDataset = $('[name=selectedDatasets]:checked').val();
        $('[name=dataset]').val(firstSelectedDataset);
        $('[name=morphCode]').val('??');
    });
}

function openAddSourceLinkDlg(elem) {
    var addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=opCode]').val($(elem).data('op-code'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=sourceLinkDlgContent]').html(null);

    addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        e.preventDefault();
        var button = $(this);
        var content = button.html();
        button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
        var theForm = $(this).closest('form');
        var url = theForm.attr('action') + '?' + theForm.serialize();
        $.get(url).done(function(data) {
            addDlg.find('[data-name=sourceLinkDlgContent]').replaceWith(data);
            addDlg.find('button[data-source-id]').off('click').on('click', function(e) {
                e.preventDefault();
                var button = $(e.target);
                var sourceName = button.closest('.form-group').find('.form-control').val();
                addDlg.find('[name=id2]').val(button.data('source-id'));
                addDlg.find('[name=value]').val(sourceName);
                var theForm = button.closest('form');
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
    var dlg = $($(elem).data('target'));
    var url = $(elem).attr('href');

    dlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        dlg.find('.close').focus();
        dlg.find('.modal-body').html(null);
        $.get(url).done(function(data) {
            dlg.find('.modal-body').html(data);
        });
    });
}