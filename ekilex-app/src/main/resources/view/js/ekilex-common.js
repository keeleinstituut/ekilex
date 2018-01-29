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
    $('[name="simpleSearchFilter"]').prop('hidden', null);
    $('[name="detailSearchFilter"]').prop('hidden', 'hidden');
    $('#searchMode').val('SIMPLE');
    $('#searchModeBtn').text('Detailotsing');
}

function displayDetailSearch() {
    $('[name="simpleSearchFilter"]').prop('hidden', 'hidden');
    $('[name="detailSearchFilter"]').prop('hidden', null);
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