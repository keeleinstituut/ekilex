function selectDatasets(selection) {
	$('#datasetSelectDlg').find(':checkbox').prop('checked', selection)
}

function displayDetailConditionButtons() {
	$('[name="removeDetailConditionBtn"]').each(function(i, v) {
		let groupElement = $(this).closest('[name="detailGroup"]');
		if (groupElement.find('[name="detailCondition"]').length === 1) {
			$(this).hide();
		} else {
			$(this).show();
		}
	});
}

function displayDetailGroupButtons() {
	if ($('[name="detailGroup"]').length === 1) {
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

function initialiseSearchForm() {
	$('#searchModeBtn').on('click', toggleSearch);
	let datasetDlg = $('#datasetSelectDlg');
	datasetDlg.on('shown.bs.modal', () => {
		datasetDlg.find('.btn').first().focus();
	});

	$('#searchForm').submit(function(e){
		openWaitDlg();
		let currentSearchMode = $('#searchMode').val();
		let isSearchFilterValid = $('#isSearchFilterValid').val();
		if (currentSearchMode === 'SIMPLE' && isSearchFilterValid === 'false') {
			e.preventDefault();
			validateAndSubmitSimpleSearch();
		}
	});
}

function validateAndSubmitSimpleSearch() {
	let searchForm = $('#searchForm');
	let searchFilter = searchForm.find('input[name="simpleSearchFilter"]').val();
	let isSearchFilterValid = validateSearchFilter(searchFilter);
	if (isSearchFilterValid) {
		$('#isSearchFilterValid').val('true');
		searchForm.submit();
	}
}

function initialiseDetailSearch() {
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
		let searchEntity = $(this).closest('[name="detailGroup"]').find('[name$="entity"]').val();
		let searchOperandElement = $(this).closest('[name="detailCondition"]').find('[name$="searchOperand"]');
		let operandTemplate = $('#searchOperandTemplates').find('[name="' + searchKeyVal + '"]').clone();
		// currently only lexsearch WORD->LANGUAGE->NOT_EXISTS implemented
		if (searchEntity == 'HEADWORD') {
			operandTemplate.find('option[value="NOT_EXISTS"]').remove();
		}
		searchOperandElement.find('option').remove();
		searchOperandElement.append(operandTemplate.html());
		searchOperandElement.val(searchOperandElement.find('option').first().val());

		// should lookup by search key + operand
		let searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
		replaceSearchValueElement(searchKeyVal, searchValueElement);
	});

	$(document).on("change", "select[name$='searchOperand']", function() {

		let searchOperandVal = $(this).val();
		let searchKeyElement = $(this).closest('[name="detailCondition"]').find('[name$="searchKey"] option:selected');
		let searchKeyVal = searchKeyElement.val();

		let searchValueElement = $(this).closest('[name="detailCondition"]').find('[name$="searchValue"]');
		if (searchKeyVal == 'DOMAIN' && searchOperandVal == 'NOT_EXISTS') {
			searchValueElement.empty();
			searchValueElement.parent().prop('hidden', true);
			searchValueElement.selectpicker('refresh');
		} else {
			replaceSearchValueElement(searchKeyVal, searchValueElement);
		}
	});

	function replaceSearchValueElement(searchKeyVal, searchValueElement) {
		let templateElement = $('#searchValueTemplates').find('[name="' + searchKeyVal + '"]');
		let copyOfValueTemplate = $(templateElement.html());

		let isAutofillElement = copyOfValueTemplate.attr('data-live-search') != undefined;
		let previousElementWasAutofill = searchValueElement.parent().hasClass('bootstrap-select');

		if (copyOfValueTemplate.hasClass('date')) {
			copyOfValueTemplate.children().attr('name', searchValueElement.attr('name'));
		} else {
			copyOfValueTemplate.attr('name', searchValueElement.attr('name'));
		}

		if (previousElementWasAutofill) {
			searchValueElement.closest('div').parent().attr('class', templateElement.attr('class'));
		} else {
			searchValueElement.closest('div').not('.date').attr('class', templateElement.attr('class'));

		}

		if (searchValueElement.parent().hasClass('date') || previousElementWasAutofill) {
			searchValueElement.parent().replaceWith(copyOfValueTemplate);
		} else {
			searchValueElement.replaceWith(copyOfValueTemplate);
		}

		if (isAutofillElement) {
			copyOfValueTemplate.selectpicker({width: '100%'});
		}

	}

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

	$('[data-live-search="true"]:not(:hidden)').each(function () {
		$(this).selectpicker({width: '100%'});
	})
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

function validateSearchFilter(searchFilter) {
	if (searchFilter === '*') {
		openMessageDlg('Palun täiendage otsingu parameetrit.');
		return false;
	}
	return true;
}