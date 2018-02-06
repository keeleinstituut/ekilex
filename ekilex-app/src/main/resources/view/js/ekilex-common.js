// Common javascript methods

function selectDatasets(selection) {
    $('#dataset_select').find(':checkbox').prop('checked', selection)
}

function displayDetailSearchButtons() {
    if ($('[name="detailCondition"]').length === 1 ) {
        $('[name="removeDetailConditionBtn"]').hide();
    } else {
        $('[name="removeDetailConditionBtn"]').show();
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
    displayDetailSearchButtons();
    $(document).on("click", ":button[name='removeDetailConditionBtn']", function() {
        $(this).closest('[name="detailCondition"]').remove();
        displayDetailSearchButtons();
    });
    $(document).on("change", "select[name$='searchKey']", function() {
        var searchOperandElement = $(this).closest('[name="detailCondition"]').find('[name$="searchOperand"]');
        var operandTemplate = $('#searchOperandTemplates').find('[name="' + $(this).val() + '"]');
        searchOperandElement.find('option').remove();
        searchOperandElement.append(operandTemplate.html());
        searchOperandElement.val(searchOperandElement.find('option').first().val());
    });
    $(document).on("click", ":button[name='addDetailConditionBtn']", function() {
        var detailSearchElement = $(this).closest('[name="detailSearchFilter"]');
        var lastConditionElement = detailSearchElement.find('[name="detailCondition"]').last();
        var copyOfLastElement = lastConditionElement.clone();
        var oldIndex = copyOfLastElement.data('index');
        var newIndex = oldIndex + 1;
        var oldIndexVal = '[' + oldIndex + ']';
        var newIndexVal = '[' + newIndex + ']';
        copyOfLastElement.attr('data-index', newIndex);
        copyOfLastElement.find('[name^="searchCriteria["]').each(function(i, v) {
           $(this).attr('name', $(this).attr('name').replace(oldIndexVal, newIndexVal))
        });
        copyOfLastElement.find('input').val(null);
        lastConditionElement.after(copyOfLastElement);
        lastConditionElement = detailSearchElement.find('[name="detailCondition"]').last();
        var searchKeySelect = lastConditionElement.find('select[name$="searchKey"]');
        searchKeySelect.val(searchKeySelect.find('option').first().val());
        searchKeySelect.trigger('change');
        displayDetailSearchButtons();
    });
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
