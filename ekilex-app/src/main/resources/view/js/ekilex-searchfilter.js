function selectDatasets(selection) {
	$('#datasetSelectDlg').find(':checkbox').prop('checked', selection)
};

function displayDetailConditionButtons() {
	$('[name="removeDetailConditionBtn"]').each(function(i, v) {
		let groupElement = $(this).closest('[name="detailGroup"]');
		if (groupElement.find('[name="detailCondition"]').length === 1) {
			$(this).hide();
		} else {
			$(this).show();
		}
	});
};

function displayDetailGroupButtons() {
	if ($('[name="detailGroup"]').length === 1) {
		$('[name="removeDetailGroupBtn"]').hide();
	} else {
		$('[name="removeDetailGroupBtn"]').show();
	}
};

function displaySimpleSearch() {
	$('#searchMode').val('SIMPLE');
};

function displayDetailSearch() {
	$('#searchMode').val('DETAIL');
};

function initialiseSearchForm() {
	$('#simpleSearchModeBtn').on('click',displaySimpleSearch);
	$('#detailSearchModeBtn').on('click',displayDetailSearch);
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
};

function validateAndSubmitSimpleSearch() {
	let searchForm = $('#searchForm');
	let searchFilter = searchForm.find('input[name="simpleSearchFilter"]').val();
	let isSearchFilterValid = validateSearchFilter(searchFilter);
	if (isSearchFilterValid) {
		$('#isSearchFilterValid').val('true');
		searchForm.submit();
	}
};

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

		let detailConditionElement = $(this).closest('[name="detailCondition"]');
		let pageName = detailConditionElement.attr("data-page");
		let searchKey = $(this).val();
		let searchEntity = $(this).closest('[name="detailGroup"]').find('[name$="entity"]').val();
		let searchOperandElement = detailConditionElement.find('[name$="searchOperand"]');
		let operandTemplate = $('#searchOperandTemplates').find('[name="' + searchKey + '"]').clone();
		// NOT_EXISTS and NOT_EQUALS is not implemented everywhere
		if (pageName == 'lex_search' && searchEntity == 'HEADWORD' && searchKey == 'LANGUAGE') {
			operandTemplate.find('option[value="NOT_EQUALS"]').remove();
		}
		if (pageName == 'lex_search' && searchEntity == 'WORD' && searchKey == 'SOURCE_REF') {
			operandTemplate.find('option[value="NOT_EXISTS"]').remove();
		}
		if (pageName == 'term_search' && searchEntity == 'TERM' && searchKey == 'LANGUAGE') {
			operandTemplate.find('option[value="NOT_EQUALS"]').remove();
		}
		searchOperandElement.find('option').remove();
		searchOperandElement.append(operandTemplate.html());
		searchOperandElement.val(searchOperandElement.find('option').first().val());

		// should lookup by search key + operand
		let searchValueElement = detailConditionElement.find('[name$="searchValue"]');
		replaceSearchValueElement(searchKey, searchValueElement);
	});

	$(document).on("change", "select[name$='searchOperand']", function() {

		const textTypeSearchKeys = ["SOURCE_REF", "VALUE_AND_EXISTS", "SECONDARY_MEANING_WORD", "LEXEME_GRAMMAR", "OD_RECOMMENDATION"];
		const selectTypeSearchKeys = ["DOMAIN", "LEXEME_POS", "LEXEME_REGISTER", "WORD_TYPE", "ASPECT"];
		const nonValueSearchOperands = ["NOT_EXISTS", "EXISTS", "SINGLE", "MULTIPLE"];

		let detailConditionElement = $(this).closest('[name="detailCondition"]');
		let searchOperand = $(this).val();
		let searchKeyElement = detailConditionElement.find('[name$="searchKey"] option:selected');
		let searchKey = searchKeyElement.val();
		let searchValueElement = detailConditionElement.find('[name$="searchValue"]');

		let isTextTypeSearch = textTypeSearchKeys.includes(searchKey);
		let isSelectTypeSearch = selectTypeSearchKeys.includes(searchKey);
		let isNonValueSearch = nonValueSearchOperands.includes(searchOperand);

		if (isTextTypeSearch && isNonValueSearch) {
			searchValueElement.empty();
			searchValueElement.prop('hidden', true);
		} else if (isSelectTypeSearch && isNonValueSearch) {
			searchValueElement.empty();
			searchValueElement.parent().prop('hidden', true);
			searchValueElement.selectpicker('refresh');
		} else {
			searchValueElement.prop('hidden', false);
			replaceSearchValueElement(searchKey, searchValueElement);
		}
	});

	function replaceSearchValueElement(searchKey, searchValueElement) {

		let templateElement = $('#searchValueTemplates').find('[name="' + searchKey + '"]');
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
			copyOfValueTemplate.selectpicker({width: '100%'})
		}

	};

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
};

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
};

function initConditionGroup(groupElement) {
	let entitySelect = groupElement.find('select[name$="entity"]');
	entitySelect.val(entitySelect.find('option').first().val());
	entitySelect.trigger('change');
	displayDetailGroupButtons();
};

function initCondition(conditionElement) {
	let searchKeySelect = conditionElement.find('select[name$="searchKey"]');
	searchKeySelect.val(searchKeySelect.find('option').first().val());
	searchKeySelect.trigger('change');
	displayDetailConditionButtons();
};

function validateSearchFilter(searchFilter) {
	if (searchFilter === '*') {
		closeWaitDlg();
		openMessageDlg('Palun täiendage otsingu parameetrit.');
		return false;
	}
	return true;
};
