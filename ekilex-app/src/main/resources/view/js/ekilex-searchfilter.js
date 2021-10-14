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

function displayNotOperandChk() {
	const disabledSearchEntities = ["TAG", "CLUELESS"];
	const disabledSearchKeys = [
		"ID", "FREQUENCY", "RANK", "CREATED_OR_UPDATED_BY", "CREATED_OR_UPDATED_ON", "CREATED_BY", "CREATED_ON", "UPDATED_BY", "UPDATED_ON", "LAST_UPDATE_ON",
		"ATTRIBUTE_NAME"];

	let notChks = $('#detail_search_filter').find('[name$="not"]');
	notChks.each(function () {
		let notChk = $(this);
		let searchEntity = notChk.closest('.detail-search-group').find('[name$="entity"]').val();
		let searchKey = notChk.closest('.detail-search-sub-row').find('[name$="searchKey"]').val();

		let disable = disabledSearchEntities.includes(searchEntity) || disabledSearchKeys.includes(searchKey);
		if (disable) {
			notChk.attr('disabled', true);
			notChk.prop('checked', false);
		} else {
			notChk.removeAttr('disabled');
		}
	});
}

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

	$(document).on("click", "#share-details-link", function() {
		let searchParams = new URLSearchParams(window.location.search);
		let idParam = searchParams.get("id");
		let detailsUri = $(this).data('details-uri');
		if (idParam) {
			let shareLink = applicationBaseUrl + '/' + detailsUri + '?id=' + idParam;
			let tempCopyField = $("<input>");
			$("body").append(tempCopyField);
			tempCopyField.val(shareLink).select();
			document.execCommand('copy');
			tempCopyField.remove();			
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
	displayNotOperandChk();

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
		displayNotOperandChk();
	});

	$(document).on("change", "select[name$='searchKey']", function() {
		let detailConditionElement = $(this).closest('[name="detailCondition"]');
		let pageName = detailConditionElement.attr("data-page");
		let searchKey = $(this).val();
		let searchEntity = $(this).closest('[name="detailGroup"]').find('[name$="entity"]').val();
		let searchOperandElement = detailConditionElement.find('[name$="searchOperand"]');
		let operandTemplate = $('#searchOperandTemplates').find('[name="' + searchKey + '"]').clone();
		// NOT_CONTAINS is not implemented everywhere
		if (pageName == 'lex_search' && searchEntity == 'HEADWORD' && searchKey == 'LANGUAGE') {
			operandTemplate.find('option[value="NOT_CONTAINS"]').remove();
		}
		searchOperandElement.find('option').remove();
		searchOperandElement.append(operandTemplate.html());
		searchOperandElement.val(searchOperandElement.find('option').first().val());

		// should lookup by search key + operand
		let searchValueElement = detailConditionElement.find('[name$="searchValue"]');
		replaceSearchValueElement(searchKey, searchValueElement);
		displayNotOperandChk();
	});

	$(document).on("change", "select[name$='searchOperand']", function() {

		const textTypeSearchKeys = [
			"SOURCE_REF", "VALUE_AND_EXISTS", "SECONDARY_MEANING_WORD", "LEXEME_GRAMMAR", "LEXEME_GOVERNMENT", "OD_RECOMMENDATION", "ATTRIBUTE_VALUE"];
		const selectTypeSearchKeys = [
			"DOMAIN", "LEXEME_POS", "LEXEME_REGISTER", "LEXEME_VALUE_STATE", "WORD_TYPE", "ASPECT", "SEMANTIC_TYPE", "ATTRIBUTE_NAME", "WORD_RELATION", "MEANING_RELATION"];
		const nonValueSearchOperands = ["EXISTS", "SINGLE", "MULTIPLE"];

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

		searchValueElement.parents(".value-input-container").attr('class',templateElement.attr('class'));

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
		displayNotOperandChk();
	});

	$(document).on("click", ":button[name='addDetailGroupBtn']", function() {

		let detailSearchElement = $("#detail_search_filter");
		let addedGroupElement = createAndAttachCopyFromLastItem(detailSearchElement, 'detailGroup', 'criteriaGroups');
		initConditionGroup(addedGroupElement);
		displayNotOperandChk();
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
	copyOfLastElement.find('div.invalid-feedback').empty();
	let inputCopy = copyOfLastElement.find('input');
	let isCheckbox = inputCopy.is(':checkbox');
	if (!isCheckbox) {
		inputCopy.val(null);
	}
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
	const searchKey = searchKeySelect.find('option').first().val();
	searchKeySelect.val(searchKey);
	searchKeySelect.trigger('change');

	const templClasslist = $('#searchValueTemplates').find('[name="' + searchKey + '"]')[0].classList;
	$(conditionElement).find('.value-input-container')[0].classList = templClasslist ;
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
