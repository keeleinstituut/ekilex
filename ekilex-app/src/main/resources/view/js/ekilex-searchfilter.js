$.fn.selectDataSetsPlugin = function() {
	this.each(function() {
		const obj = $(this);
		const buttons = obj.find('[name=selectAll], [name=selectNone]');
		const checkboxes = obj.find(':checkbox');
		buttons.on('click', function() {
			const chkAll = this.name === 'selectAll' ? true : false;
			checkboxes.prop('checked', chkAll);
		});
	});
}

// $.fn.selectDatasets = function () {

// 	$('[name=selectAll]').on("click", function () {
// 		$('#datasetSelectDlg').find(':checkbox').prop('checked', true)
// 	});

// 	$('[name=selectNone]').on("click", function () {
// 		$('#datasetSelectDlg').find(':checkbox').prop('checked', false)
// 	});

// };

function detailSearchBtn() {

	if ($('.main-nav-tabs').find("#detailSearchModeBtn").hasClass("active")) {
		$(".detail-search-mode-less-border").removeClass("d-none");
	} else {
		$(".detail-search-mode-less-border").addClass("d-none");
	}
}

$.fn.detailSearchModePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const icon = obj.children("i");
			// Get the button's ancestor div that is siblings with other relevant elements
			const searchModeDiv = obj.closest('.detail-search-mode-less');
			const searchBoxDiv = searchModeDiv.siblings('.detail-search-box-less');
			const searchFilterItems = searchModeDiv.siblings('.detail-search-filter-items');
			if (icon.hasClass("fa-sort-desc")) {
				icon.addClass("fa-sort-asc").removeClass("fa-sort-desc");
				searchFilterItems.show();
				searchBoxDiv.hide();
			} else {
				icon.addClass("fa-sort-desc").removeClass("fa-sort-asc");
				searchFilterItems.hide();
				searchBoxDiv.show();
			}
		});
	});
}

// $.fn.detailSearchModeBtn = function () {

// 	$('.detail-search-mode-less-btn').on("click", function () {
// 		let icon = $('.detail-search-mode-less-btn').find("i");
// 		if (icon.hasClass("fa-sort-desc")) {
// 			icon.addClass("fa-sort-asc").removeClass("fa-sort-desc");
// 			$('.detail-search-filter-items').show();
// 			$('.detail-search-box-less').hide();
// 		} else {
// 			icon.addClass("fa-sort-desc").removeClass("fa-sort-asc");
// 			$('.detail-search-filter-items').hide();
// 			$('.detail-search-box-less').show();
// 		}
// 	});

// };

function detailSearchModeBtnValue() {
	let finalOutput = "";
	$(".detail-search-group").each(function (index, value) {
		let output = "";
		let selected = $(this).find("select[name$='entity'] option:selected").text() ? ($(this).find("select[name$='entity'] option:selected").text() + "-") : "";

		$('.detail-search-sub-row', this).each(function (index, value) {
			let obj = $(this);
			let outputInner = "";
			let notChksTitle = obj.find('[name$="not"]').is(':checked') ? (obj.find('[name$="not"]').attr('title').toLowerCase() + "-") : "";
			let searchKey = obj.find("select[name$='searchKey'] option:selected").text() ? (obj.find("select[name$='searchKey'] option:selected").text() + "-") : "";
			let searchOperand = obj.find("select[name$='searchOperand'] option:selected").text() ? (obj.find("select[name$='searchOperand'] option:selected").text()) : "";
			outputInner = outputInner + selected + notChksTitle + searchKey + searchOperand;

			let searchValueValItem = obj.find("input[name$='searchValue']");
			let searchValueTextItem = obj.find("select[name$='searchValue']");

			if (!((searchValueTextItem.is(":hidden") === true) || (searchValueValItem.is(":hidden") === true))) { // box exists
				let searchValueVal = searchValueValItem.val() ?? "";
				let searchValueText = searchValueTextItem.find(":selected").text() ?? "";

				if (searchValueVal.length > 0) {
					outputInner = outputInner + "-" + '"' + searchValueVal + '"' + "; ";
					output = output + outputInner;
				} else if (searchValueText.length > 0) {
					outputInner = outputInner + "-" + '"' + searchValueText + '"' + "; ";
					output = output + outputInner;
				} else {
					outputInner = "";
				}
			} else {
				output = output + outputInner + "; ";
			}

		});
		finalOutput = finalOutput + output;
	});
	$('.detail-search-box-less-value').val(finalOutput);
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
		"MANUAL_UPDATE_ON", "ATTRIBUTE_NAME"];

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
	// Moved to bottom of file as plugins
	// $('#simpleSearchModeBtn').on('click',displaySimpleSearch);
	// $('#detailSearchModeBtn').on('click',displayDetailSearch);
	// let datasetDlg = $('#datasetSelectDlg');
	// datasetDlg.on('shown.bs.modal', () => {
	// 	datasetDlg.find('.btn').first().focus();
	// });
	detailSearchModeBtnValue();

	$('#searchForm').submit(function(e){
		openWaitDlg();
		let currentSearchMode = $('#searchMode').val();
		let isSearchFilterValid = $('#isSearchFilterValid').val();
		if (currentSearchMode === 'SIMPLE' && isSearchFilterValid === 'false') {
			e.preventDefault();
			validateAndSubmitSimpleSearch();
		}
	});

	// Moved to bottom of file as plugin
	// $(document).on("click", "#share-details-link", function() {
	// 	let searchParams = new URLSearchParams(window.location.search);
	// 	let idParam = searchParams.get("id");
	// 	let detailsUri = $(this).data('details-uri');
	// 	if (idParam) {
	// 		let shareLink = applicationBaseUrl + '/' + detailsUri + '?id=' + idParam;
	// 		let tempCopyField = $("<input>");
	// 		$("body").append(tempCopyField);
	// 		tempCopyField.val(shareLink).select();
	// 		document.execCommand('copy');
	// 		tempCopyField.remove();			
	// 	}
	// });

	$.fn.manualEventOnUpdateCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			let checked = main.is(':checked');
			let manualEventOnUpdateUrl;
			if (checked == true) {
				manualEventOnUpdateUrl = applicationUrl + 'manual_event_on_update/false';
			} else {
				manualEventOnUpdateUrl = applicationUrl + 'manual_event_on_update/true';
			}
			$.get(manualEventOnUpdateUrl).done(function(data) {
				$('#manualEventOnDiv').replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg('Viga!');
			}).always(function() {
				closeWaitDlg();
			});
		});
	}
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

	// Combined and moved to bottom of page
	// $.fn.removeDetailConditionBtn = function () {
	// 	var block = $("#detail_search_filter");
	// 	block.off("click.removeDetailConditionBtn").on("click.removeDetailConditionBtn", ":button[name='removeDetailConditionBtn']", function () {
	// 		//$(document).on("click", ":button[name='removeDetailConditionBtn']", function() {
	// 		$(this).closest('[name="detailCondition"]').remove();
	// 		displayDetailConditionButtons();
	// 	});
	// };

	// $.fn.removeDetailGroupBtn = function () {
	// 	var block = $("#detail_search_filter");
	// 	block.off("click.removeDetailGroupBtn").on("click.removeDetailGroupBtn", ":button[name='removeDetailGroupBtn']", function () {
	// 		//$(document).on("click", ":button[name='removeDetailGroupBtn']", function() {
	// 		$(this).closest('[name="detailGroup"]').remove();
	// 		displayDetailGroupButtons();
	// 	});
	// };

	$("#detail_search_filter").on("change", "select[name$='entity']", function () {
		//$(document).on("change", "select[name$='entity']", function() {

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

	$("#detail_search_filter").on("change", "select[name$='searchKey']", function () {
		//$(document).on("change", "select[name$='searchKey']", function() {
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

	$("#detail_search_filter").on("change", "select[name$='searchOperand']", function () {
		//$(document).on("change", "select[name$='searchOperand']", function() {

		const textTypeSearchKeys = [
			"SOURCE_REF", "VALUE_AND_EXISTS", "SECONDARY_MEANING_WORD", "LEXEME_GRAMMAR", "LEXEME_GOVERNMENT", "ATTRIBUTE_VALUE", "MORPHOPHONO_FORM",
			"WORD_NOTE", "LEXEME_NOTE", "MEANING_NOTE", "DEFINITION_NOTE"];
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
	// Combined and moved to bottom of page
	// $.fn.addDetailConditionBtn = function () {
	// 	var block = $("#detail_search_filter");
	// 	block.off("click.addDetailConditionBtn").on("click.addDetailConditionBtn", ":button[name='addDetailConditionBtn']", function () {
	// 		//$(document).on("click", ":button[name='addDetailConditionBtn']", function() {

	// 		let detailGroupElement = $(this).closest('[name="detailGroup"]');
	// 		let addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
	// 		initCondition(addedConditionElement);
	// 		displayNotOperandChk();
	// 	});
	// };

	// $.fn.addDetailGroupBtn = function () {
	// 	var block = $("#detail_search_filter");
	// 	block.off("click.addDetailGroupBtn").on("click.addDetailGroupBtn", ":button[name='addDetailGroupBtn']", function () {
	// 		let detailSearchElement = block;
	// 		//$(document).on("click", ":button[name='addDetailGroupBtn']", function() {	
	// 		let addedGroupElement = createAndAttachCopyFromLastItem(detailSearchElement, 'detailGroup', 'criteriaGroups');
	// 		initConditionGroup(addedGroupElement);
	// 		displayNotOperandChk();
	// 	});
	// };
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

$.fn.shareDetailsLinkPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const searchParams = new URLSearchParams(window.location.search);
			const idParam = searchParams.get("id");
			const detailsUri = obj.data('details-uri');
			if (idParam) {
				const shareLink = applicationBaseUrl + '/' + detailsUri + '?id=' + idParam;
				const tempCopyField = $("<input>");
				$("body").append(tempCopyField);
				tempCopyField.val(shareLink).select();
				document.execCommand('copy');
				tempCopyField.remove();			
			}
		});
	});
}

$.fn.searchDetailOperationsPlugin = function() {
	// mainObj refers to the #detail_search_filter element
	function attachClickHandlers(obj, mainObj) {
		// Get buttons with name ending in either of two choices
		const buttons = obj.find('button[name$="DetailConditionBtn"], button[name$="DetailGroupBtn"]');
		buttons.on('click', function() {
			const clickedButton = $(this);
			switch (this.name) {
				case 'addDetailConditionBtn':
					const detailGroupElement = clickedButton.closest('[name="detailGroup"]');
					const addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
					initCondition(addedConditionElement);
					displayNotOperandChk();
					attachClickHandlers(addedConditionElement, mainObj);
					break;
				case 'addDetailGroupBtn':
					const addedGroupElement = createAndAttachCopyFromLastItem(mainObj, 'detailGroup', 'criteriaGroups');
					initConditionGroup(addedGroupElement);
					displayNotOperandChk();
					attachClickHandlers(addedGroupElement, mainObj);
					break;
				case 'removeDetailConditionBtn':
					clickedButton.closest('[name="detailCondition"]').remove();
					displayDetailConditionButtons();
					break;
				case 'removeDetailGroupBtn':
					clickedButton.closest('[name="detailGroup"]').remove();
					displayDetailGroupButtons();
					break;
			}
		});
	}
	return this.each(function() {
		const obj = $(this);
		// Giving the same element as both arguments on first call
		// The first argument will change on subsequent calls
		attachClickHandlers(obj, obj);
	});
}

$.fn.chooseSearchModePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			if (obj.attr('id') === 'simpleSearchModeBtn') {
				displaySimpleSearch();
			} else {
				displayDetailSearch();
			}
		});
	});
}

$.fn.datasetDlgFocusBtnPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('shown.bs.modal', function() {
			obj.find('.btn').first().focus();
		});
	});
}