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

function detailSearchModeBtnValue() {
	let output = "";
	$(".detail-search-group").each(function () {
		const group = $(this);
		let selected = group.find("select[name$='entity'] option:selected").text() ? (group.find("select[name$='entity'] option:selected").text() + "-") : "";

		group.find('.detail-search-sub-row').each(function () {
			const obj = $(this);
			const notChksTitle = obj.find('[name$="not"]').is(':checked') ? (obj.find('[name$="not"]').attr('title').toLowerCase() + "-") : "";
			const searchKey = obj.find("select[name$='searchKey'] option:selected").text() ? (obj.find("select[name$='searchKey'] option:selected").text() + "-") : "";
			const searchOperand = obj.find("select[name$='searchOperand'] option:selected").text() ? (obj.find("select[name$='searchOperand'] option:selected").text()) : "";
			let searchValueValItem = obj.find("input[name$='searchValue']");
			let searchValueTextItem = obj.find("select[name$='searchValue']");
			if (!(searchValueTextItem.is(":hidden") || searchValueValItem.is(":hidden"))) { // box exists
				let searchValueVal = searchValueValItem.val() ?? "";
				let searchValueText = searchValueTextItem.find(":selected").text() ?? "";

				if (searchValueVal.length > 0 || searchValueText.length > 0) {
					output += `${selected + notChksTitle + searchKey + searchOperand}-"${searchValueVal}"; `;
				}
			} else {
				output += `${selected + notChksTitle + searchKey + searchOperand}; `;
			}

		});
	});
	$('.detail-search-box-less-value').val(output);
}

function displayDetailConditionButtons() {
	$('[name="removeDetailConditionBtn"]').each(function() {
		const btn = $(this);
		const groupElement = btn.closest('[name="detailGroup"]');
		if (groupElement.find('[name="detailCondition"]').length === 1) {
			btn.hide();
		} else {
			btn.show();
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

	const notChks = $('#detail_search_filter').find('[name$="not"]');
	notChks.each(function () {
		const notChk = $(this);
		const searchEntity = notChk.closest('.detail-search-group').find('[name$="entity"]').val();
		const searchKey = notChk.closest('.detail-search-sub-row').find('[name$="searchKey"]').val();
		const disable = disabledSearchEntities.includes(searchEntity) || disabledSearchKeys.includes(searchKey);

		if (disable) {
			notChk.attr('disabled', true);
			notChk.prop('checked', false);
		} else {
			notChk.removeAttr('disabled');
		}
	});
}

function initialiseSearchForm() {
	detailSearchModeBtnValue();
  manualEventOnUpdateCheckCheckboxValueUpdate();
};

$.fn.manualEventOnDivView = function () {
	const obj = $(this);
	const dataViewType = $("#searchForm").attr('action');
	if (dataViewType) {
		if ((dataViewType.includes("/lexsearch")) || (dataViewType.includes("/termsearch"))) {
			obj.removeClass("d-none");
		};
	};
};

function manualEventOnUpdateCheckCheckboxValueUpdate() {
	let data = $("#manualEventOnDiv").find(".date-check-input");
	if (data.is(':checked')) {
		$(".date-check-i").addClass("fa fa-check");
	} else {
		$(".date-check-i").removeClass("fa fa-check");
	}
};

function manualEventOnUpdateItemsShow(obj) {
	obj.find(".date-text").removeClass("d-none");
	obj.find(".date-check").removeClass("d-none");
	obj.find(".date-i-edit")
		.removeClass("date-i-edit-small")
		.addClass("date-i-edit-big");
}
function manualEventOnUpdateItemsHide(obj) {
	obj.find(".date-text").addClass("d-none");
	obj.find(".date-check").addClass("d-none");
	obj.find(".date-i-edit")
		.addClass("date-i-edit-small")
		.removeClass("date-i-edit-big");
}

$.fn.mouseManualEventOnUpdateCheck = function (e) {
	let obj = $(this);
	let data = obj.find(".date-check-input");
	obj.on("click", function (e) { //box on click does not do activate check-box "checked"
		e.preventDefault();
	});

	if (data.is(':checked')) {
		obj.animate({ width: "168", marginLeft: 0 }, { duration: 225 });
		manualEventOnUpdateItemsShow(obj);
	} else {
		let timeout;
		obj.on("mouseenter", function () {
			if (timeout != null) clearTimeout(timeout);

			timeout = setTimeout(function () {
				obj.animate({ width: "168", marginLeft: 0 }, { duration: 225 });
				manualEventOnUpdateItemsShow(obj);
			}, 225);
		});

		obj.on("mouseleave", function () {
			if (timeout != null) {
				clearTimeout(timeout);
				obj.animate({ width: "32", marginLeft: 0 }, { duration: 225 });
				manualEventOnUpdateItemsHide(obj);
				timeout = null;
			}
		});
	}
};

$.fn.manualEventOnUpdateCheck = function () {
	const main = $(this);
	main.on('click', function (e) {
		e.preventDefault();
		openWaitDlg();
		const checked = main.is(':checked');
		let manualEventOnUpdateUrl;
		if (checked) {
			manualEventOnUpdateUrl = applicationUrl + 'manual_event_on_update/false';
		} else {
			manualEventOnUpdateUrl = applicationUrl + 'manual_event_on_update/true';
		}
		$.get(manualEventOnUpdateUrl).done(function (data) {
			$('#manualEventOnDiv').replaceWith(data);
			$wpm.bindObjects();
		}).fail(function (data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function () {
			manualEventOnUpdateCheckCheckboxValueUpdate();
			closeWaitDlg();
		});
	});
};

function validateAndSubmitSimpleSearch() {
	const searchForm = $('#searchForm');
	const searchFilter = searchForm.find('input[name="simpleSearchFilter"]').val();
	const isSearchFilterValid = validateSearchFilter(searchFilter);
	if (isSearchFilterValid) {
		$('#isSearchFilterValid').val('true');
		searchForm.submit();
	}
};

function replaceSearchValueElement(searchKey, searchValueElement) {

	const templateElement = $('#searchValueTemplates').find(`[name="${searchKey}"]`);
	const copyOfValueTemplate = $(templateElement.html());
	const isAutofillElement = copyOfValueTemplate.attr('data-live-search') != undefined;
	const previousElementWasAutofill = searchValueElement.parent().hasClass('bootstrap-select');

	searchValueElement.parents(".value-input-container").attr('class', templateElement.attr('class'));

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

function initialiseDetailSearch() {
	displayDetailConditionButtons();
	displayDetailGroupButtons();
	displayNotOperandChk();

	$('[data-live-search="true"]:not(:hidden)').each(function () {
		$(this).selectpicker({width: '100%'});
	})
};

function createAndAttachCopyFromLastItem(parentElement, itemName, indexName) {

	const lastElement = parentElement.find(`[name="${itemName}"]`).last();
	const copyOfLastElement = lastElement.clone();
	const oldIndex = copyOfLastElement.data('index');
	const newIndex = oldIndex + 1;
	const oldIndexVal = `${indexName}[${oldIndex}]`;
	const newIndexVal = `${indexName}[${newIndex}]`;
	copyOfLastElement.attr('data-index', newIndex);
	copyOfLastElement.find('[name*="' + indexName + '["]').each(function() {
		const element = $(this);
		element.attr('name', element.attr('name').replace(oldIndexVal, newIndexVal));
	});
	copyOfLastElement.find('div.invalid-feedback').empty();
	const inputCopy = copyOfLastElement.find('input');
	const isCheckbox = inputCopy.is(':checkbox');
	if (!isCheckbox) {
		inputCopy.val(null);
	}
	lastElement.after(copyOfLastElement);
	return parentElement.find(`[name="${itemName}"]`).last();
};

function initConditionGroup(groupElement) {
	const entitySelect = groupElement.find('select[name$="entity"]');
	entitySelect.val(entitySelect.find('option').first().val());
	entitySelect.change();
	displayDetailGroupButtons();
};

function initCondition(conditionElement) {
	const searchKeySelect = conditionElement.find('select[name$="searchKey"]');
	const searchKey = searchKeySelect.find('option').first().val();
	searchKeySelect.val(searchKey);
	searchKeySelect.change();
	const templClasslist = $('#searchValueTemplates').find(`[name="${searchKey}"]`)[0].classList;
	$(conditionElement).find('.value-input-container')[0].classList = templClasslist ;
	displayDetailConditionButtons();
};

function validateSearchFilter(searchFilter) {
	if (searchFilter === '*') {
		closeWaitDlg();
		openMessageDlg(messages["common.search.add.parameter"]);
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
				const shareLink = `${applicationBaseUrl}/${detailsUri}?id=${idParam}`;
				const tempCopyField = $("<input>");
				$("body").append(tempCopyField);
				tempCopyField.val(shareLink).select();
				document.execCommand('copy');
				tempCopyField.remove();			
			}
		});
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

$.fn.searchFormSubmitPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('submit', function(e) {
			openWaitDlg();
			const currentSearchMode = obj.find('#searchMode').val();
			const isSearchFilterValid = obj.find('#isSearchFilterValid').val();
			if (currentSearchMode === 'SIMPLE' && isSearchFilterValid === 'false') {
				e.preventDefault();
				validateAndSubmitSimpleSearch();
			}
		});
	});
}

function handleEntityChange(entity) {
	const entityValue = entity.val();
	const detailGroupElement = entity.closest('[name="detailGroup"]');
	const detailConditionElements = detailGroupElement.find('[name="detailCondition"]');
	const conditionElement = detailConditionElements.first();
	// Delete all elements after the first one
	detailConditionElements.slice(1).remove();
	const searchKeyElement = conditionElement.find('[name$="searchKey"]');
	const keyTemplate = $('#searchKeyTemplates').find(`[name="${entityValue}"]`);
	searchKeyElement.find('option').remove();
	searchKeyElement.append(keyTemplate.html());
	searchKeyElement.val(searchKeyElement.find('option').first().val());
	initCondition(conditionElement);
	displayNotOperandChk();
}

function handleSearchKeyChange(searchKey) {
	const detailConditionElement = searchKey.closest('[name="detailCondition"]');
	const pageName = detailConditionElement.attr("data-page");
	const searchKeyValue = searchKey.val();
	const searchEntity = searchKey.closest('[name="detailGroup"]').find('[name$="entity"]').val();
	const searchOperandElement = detailConditionElement.find('[name$="searchOperand"]');
	const operandTemplate = $('#searchOperandTemplates').find(`[name="${searchKeyValue}"]`);
	// NOT_CONTAINS is not implemented everywhere
	if (pageName == 'lex_search' && searchEntity == 'HEADWORD' && searchKeyValue == 'LANGUAGE') {
		operandTemplate.find('option[value="NOT_CONTAINS"]').remove();
	}
	searchOperandElement.find('option').remove();
	searchOperandElement.append(operandTemplate.html());
	searchOperandElement.val(searchOperandElement.find('option').first().val());

	// should lookup by search key + operand
	const searchValueElement = detailConditionElement.find('[name$="searchValue"]');
	replaceSearchValueElement(searchKeyValue, searchValueElement);
	displayNotOperandChk();
}

function handleSearchOperandChange(searchOperand) {
	const textTypeSearchKeys = [
		"SOURCE_REF", "VALUE_AND_EXISTS", "SECONDARY_MEANING_WORD",
		"LEXEME_GRAMMAR", "LEXEME_GOVERNMENT", "ATTRIBUTE_VALUE", "MORPHOPHONO_FORM",
		"WORD_FORUM", "MEANING_FORUM", "LEXEME_NOTE", "MEANING_NOTE", "DEFINITION_NOTE"
	];
	const selectTypeSearchKeys = [
		"DOMAIN", "LEXEME_POS", "LEXEME_REGISTER", "LEXEME_VALUE_STATE", "WORD_TYPE",
		"ASPECT", "SEMANTIC_TYPE", "ATTRIBUTE_NAME", "WORD_RELATION", "MEANING_RELATION"
	];
	const nonValueSearchOperands = ["EXISTS", "SINGLE", "MULTIPLE"];

	const detailConditionElement = searchOperand.closest('[name="detailCondition"]');
	const searchOperandValue = searchOperand.val();
	const searchKeyElement = detailConditionElement.find('[name$="searchKey"] option:selected');
	const searchKey = searchKeyElement.val();
	const searchValueElement = detailConditionElement.find('[name$="searchValue"]');

	const isTextTypeSearch = textTypeSearchKeys.includes(searchKey);
	const isSelectTypeSearch = selectTypeSearchKeys.includes(searchKey);
	const isNonValueSearch = nonValueSearchOperands.includes(searchOperandValue);

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
}

function addDetailCondition(button) {
	const detailGroupElement = button.closest('[name="detailGroup"]');
	const addedConditionElement = createAndAttachCopyFromLastItem(detailGroupElement, 'detailCondition', 'searchCriteria');
	initCondition(addedConditionElement);
	displayNotOperandChk();
}

function addDetailGroup() {
	const detailSearchElement = $('#detail_search_filter');
	const addedGroupElement = createAndAttachCopyFromLastItem(detailSearchElement, 'detailGroup', 'criteriaGroups');
	initConditionGroup(addedGroupElement);
	displayNotOperandChk();
}

function removeDetailCondition(button) {
	button.closest('[name="detailCondition"]').remove();
	displayDetailConditionButtons();
}

function removeDetailGroup(button) {
	button.closest('[name="detailGroup"]').remove();
	displayDetailGroupButtons();
}

$.fn.detailedSearchPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('change', function(e) {
			const target = $(e.target);
			// Split the name attribute by decimals and get the last part
			const targetName = target.prop('name').split('.').at(-1);
			switch (targetName) {
				case 'entity':
					handleEntityChange(target);
					break;
				case 'searchKey':
					handleSearchKeyChange(target);
					break;
				case 'searchOperand':
					handleSearchOperandChange(target);
					break;
			}
		});

		const buttonsSelector = `
		button[name="addDetailConditionBtn"],
		button[name="addDetailGroupBtn"],
		button[name="removeDetailConditionBtn"],
		button[name="removeDetailGroupBtn"]
		`
		obj.on('click', buttonsSelector, function(e) {
			const button = $(e.currentTarget);
			const buttonName = button.prop('name');
			switch (buttonName) {
				case 'addDetailConditionBtn':
					addDetailCondition(button);
					break;
				case 'addDetailGroupBtn':
					addDetailGroup();
					break;
				case 'removeDetailConditionBtn':
					removeDetailCondition(button);
					break;
				case 'removeDetailGroupBtn':
					removeDetailGroup(button);
					break;
			}
		});
	});
}