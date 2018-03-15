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
    $('[name="simpleSearchFilter"]').prop('hidden', false);
    $('[name="detailSearchFilter"]').prop('hidden', true);
    $('#searchMode').val('SIMPLE');
    $('#searchModeBtn').text('Detailotsing');
}

function displayDetailSearch() {
    $('[name="simpleSearchFilter"]').prop('hidden', true);
    $('[name="detailSearchFilter"]').prop('hidden', false);
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
    $(document).on("change", "select[name$='searchKey']", function() {
        var searchOperandElement = $(this).closest('[name="detailCondition"]').find('[name$="searchOperand"]');
        var operandTemplate = $('#searchOperandTemplates').find('[name="' + $(this).val() + '"]');
        searchOperandElement.find('option').remove();
        searchOperandElement.append(operandTemplate.html());
        searchOperandElement.val(searchOperandElement.find('option').first().val());

        var searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
        var templateElement = $('#searchValueTemplates').find('[name="' + $(this).val() + '"]');
        var copyOfValueTemplate = $(templateElement.html());
        copyOfValueTemplate.attr('name' ,searchValueElement.attr('name'));
        searchValueElement.closest('div').attr('class', templateElement.attr('class'));
        searchValueElement.replaceWith(copyOfValueTemplate);
    });
    $(document).on("change", "select[name$='entity']", function() {
        var detailGroupElement = $(this).closest('[name="detailGroup"]');
        while (detailGroupElement.find('[name="detailCondition"]').length > 1) {
            detailGroupElement.find('[name="detailCondition"]').last().remove();
        }
        var conditionElement = detailGroupElement.find('[name="detailCondition"]').first();
        var searchKeyElement = conditionElement.find('[name$="searchKey"]');
        var keyTemplate = $('#searchKeyTemplates').find('[name="' + $(this).val() + '"]');
        searchKeyElement.find('option').remove();
        searchKeyElement.append(keyTemplate.html());
        searchKeyElement.val(searchKeyElement.find('option').first().val());
        initCondition(conditionElement);
    });
    $(document).on("click", ":button[name='addDetailConditionBtn']", function() {
        var detailGroupElement = $(this).closest('[name="detailGroup"]');
        var addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
        initCondition(addedConditionElement);
    });
    $(document).on("click", ":button[name='addDetailGroupBtn']", function() {
        var detailSearchElement = $(this).closest('[name="detailSearchFilter"]');
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
            itemData.orderby = $(item).attr('data-orderby');
            orderedItems.push(itemData);
        });
    }
    return {opcode: opCode, items: orderedItems};
}

function postJson(url, dataObject) {
    $.ajax({
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
    var modifyFld = editDlg.find('[name="modified_value"]');
    modifyFld.val(targetElement.data('value') != undefined ? targetElement.data('value') : targetElement.text());
    editDlg.find('[name="id"]').val(targetElement.data('id'));
    editDlg.find('[name="op_type"]').val(targetElement.data('op-type'));

    editDlg.find('button[type="submit"]').off().on('click', function(e) {
        e.preventDefault();
        var editForm = editDlg.find('form');
        var url = editForm.attr('action') + '?' + editForm.serialize();
        $.post(url).done(function(data) {
            var id = $('#details_div').data('id');
            var detailsButton = $('[name="detailsBtn"][data-id="' + id + '"]');
            detailsButton.trigger('click');
            editDlg.find('button.close').trigger('click');
        }).fail(function(data) {
            alert("Andmete muutmine ebaõnnestus.");
            console.log(data);
        });
    });
}
