$(document).ready(function() {
	var selectedHomonymItem = getSelectedHomonym();
	selectedHomonymItem.delay(500).queue(function() { }).trigger('click');
	selectedHomonymItem.addClass("animation-target");
	setSelectedHomonymValueForMobile(getSelectedHomonym().html());
	initDatasetDropdown();
	initLangDropdown();
	searchWordAutocomplete();
	focusSearchInput();
});

function focusSearchInput() {
	const searchInput = document.getElementById('search');
	searchInput.focus();
	const textLength = searchInput.value?.length ?? 0;
	// Set text cursor to end, goes to beginning by default
	searchInput.setSelectionRange(textLength, textLength);
}

// virtual keyboard autocomplete height fix
function searchWordAutocompleteHeight() {
	if ($(".keyboard-search").hasClass("lang-open")) {
		let documentHeight = $(document).height();
		let search = $('#search');
		let virtualKeyboard = $('#KioskBoard-VirtualKeyboard');

		if (search.length && virtualKeyboard.length) {
			let heightFromTopToSearchInput = search.offset().top;
			let searchInputHeight = search.outerHeight();
			let heightKeyboard = virtualKeyboard.outerHeight();

			let calculateNewHeight = (documentHeight - heightFromTopToSearchInput - searchInputHeight - heightKeyboard);
			let maxAutocompleHeight = calculateNewHeight ? calculateNewHeight + 'px' : '';

			$('.ui-autocomplete.list-group').css({
				'overflow-y': 'scroll',
				'overflow-x': 'hidden',
				'max-height': maxAutocompleHeight
			});
		}
	}
}

function searchWordAutocomplete() {
	var searchWordAutocompleteMenuRenderer = function(ul, items) {
		var self = this;
		var groups = [];

		groups = $.map(items, function(item) {
			return item.group;
		});

		groups = $.grep(groups, function(el, index) {
			return index === $.inArray(el, groups);
		});

		ul.addClass("list-group");
		$.each(groups, function(index, group) {
			if (group == "formWord") {
				var li = $("<li>");
				li.addClass("list-group-item list-group-item-info");
				li.text(messages.this_is_form);
				ul.append(li);
			}
			$.each(items, function(index, item) {
				if (item.group == group) {
					self._renderItemData(ul, item);
				}
			});
		});
	};

	var searchWordAutocompleteConfig = {
		source: function(request, response) {
			var wordFrag = request.term;
			var searchWordFragUrlWithParams = searchWordFragUrl + "/" + wordFrag;
			if (wordFrag.indexOf('*') > -1) {
				response([]);
				return;
			}
			if (wordFrag.indexOf('?') > -1) {
				response([]);
				return;
			}
			$.ajax({
				url: searchWordFragUrlWithParams,
				type: "GET",
				success: function(data) {
					var fullList = [];
					$.each(data.prefWords, function(index, item) {
						fullList.push({
							group: "prefWord",
							label: item,
							value: item
						});
					});
					$.each(data.formWords, function(index, item) {
						fullList.push({
							group: "formWord",
							label: item,
							value: item
						});
					});
					response(fullList);
				}
			});
		},
		minLength: 3,
		create: function() {
			$(this).data('uiAutocomplete')._renderMenu = searchWordAutocompleteMenuRenderer;
		},
		select: function(event, ui) {
			if (ui.item) {
				$("input[name='searchWord']").val(ui.item.value);
				$("#search-btn").trigger('click');
			}
			return false;
		},
		open: function() {
			searchWordAutocompleteHeight();
			return false;
		},
		close: function() {
			return false;
		}
	};

	// if you remove "change" then virtual keyboard autocomplete does not work
	$(document).on("change input", "input[name='searchWord']", function() {
		if ($(".keyboard-search").hasClass("lang-open")) { // virtual keyboard enabled
			if ($('#KioskBoard-VirtualKeyboard').length) { // only run when virtual keyboard exist
				$(this).autocomplete(searchWordAutocompleteConfig).autocomplete("search");
			}
		} else {
			$(this).autocomplete(searchWordAutocompleteConfig).autocomplete("search");
		}
	});
};

$(document).on("click", "#clear-search-btn", function(e) {
	const searchInput = $("input[name='searchWord']");
	searchInput.val('').trigger('change');
	searchInput.trigger('focus');
});

$(document).on("click", "eki-link", function(e) {
	const link = $(this);
	const linkType = link.attr("data-link-type");
	const linkId = link.attr("data-link-id");
	const searchLinkUrlWithParams = `${searchLinkUrl}/${linkType}/${linkId}`;
	$.get(searchLinkUrlWithParams).done(function(data) {
		if (data) {
			const searchForm = $('#hiddenSearchForm');
			searchForm.find("input[name='searchWord']").val(data.word);
			searchForm.find("input[name='selectedWordHomonymNr']").val(data.homonymNr);
			searchForm.find("input[name='selectedWordLang']").val(data.lang);
			searchForm.find("input[name='linkedLexemeId']").val(data.lexemeId);
			searchForm.submit();
		} else {
			//handle incorrect link?
		}
	});
});

$(document).on('click', 'ext-link', function() {
	const link = $(this);
	const href = link.attr('href');
	if (href) {
		const target = link.attr('target');
		// Perhaps a good idea to add a regex check for valid url?
		if (href.startsWith('https://')) {
			window.open(href, target);
		} else {
			window.open(`https://${href}`, target);
		}
	} else {
		openAlertDlg(messages["common.broken.link"]);
	}
});

function setSelectedWordHomonymNrAndLang() {
	const selectedWordHomonymNr = $("#selected-word-homonym-nr").val();
	const selectedWordLang = $("#selected-word-lang").val();
	$("input[name='selectedWordHomonymNr']").val(selectedWordHomonymNr);
	$("input[name='selectedWordLang']").val(selectedWordLang);
}

function clickSearchIfInputExists() {
	var tempSearchWord = $("input[name='searchWord']").val();
	if (tempSearchWord) {
		$("#search-btn").trigger('click');
	}
}

function getSelectedHomonym() {
	var selectedHomonymItem = $(".homonym-item").filter(function() {
		var isHomonymSelected = $(this).closest("form").find("input[name='word-selected']").val();
		return isHomonymSelected == "true";
	}).filter(":first");
	if (selectedHomonymItem.get().length == 0) {
		selectedHomonymItem = $(".homonym-item:first");
	}
	return selectedHomonymItem;
}

function setSelectedHomonymValueForMobile(inputHTML) {
	var isMultiHomonym = $(".homonym-item").length > 1;
	if (isMultiHomonym) {
		$("#homonymListToggleButton").html(inputHTML);
	}
}

$(document).on("click", "#homonymListToggleButton", function() {
	$(".homonym-list").toggleClass("expand");
});

$(document).on("shown.bs.modal", "[id^='morpho-modal-']", function() {
	var main = $(this);
	var paradigmId = main.attr("data-paradigm-id");
	if (main.find('.modal-dialog').length) {
		main.find('.modal-dialog').remove();
		main.append('<div class="morpho-content"></div>');
	}
	var morphoContentDiv = main.find(".morpho-content");
	var morphoUrlWithParams = morphoUrl + "/" + paradigmId + "/" + currentWordLang;
	$.get(morphoUrlWithParams).done(function(data) {
		morphoContentDiv.replaceWith(data);
		$('[data-toggle="tooltip"]').tooltip({
			container: 'body'
		});
		main.find('[data-plugin="tableTogglers"]').tableTogglers();
		main.find('.scrollable-table').scrollableTable();
	});
});

$(document).on("show.bs.collapse", ".user-preference", function(e) {
	if ($(e.target).hasClass("user-preference")) {
		var elementName = $(this).data("name");
		var userPrefUrlWithParams = userPrefUrl + "/" + elementName + "/open"
		$.ajax({
			url: userPrefUrlWithParams,
			data: {},
			method: 'POST'
		});
	}
});

$(document).on("hide.bs.collapse", ".user-preference", function(e) {
	if ($(e.target).hasClass("user-preference")) {
		var elementName = $(this).data("name");
		var userPrefUrlWithParams = userPrefUrl + "/" + elementName + "/close"
		$.ajax({
			url: userPrefUrlWithParams,
			data: {},
			method: 'POST'
		});
	}
});

$.fn.tableTogglers = function() {
	var main = $(this);
	var buttons = main.find('button');
	var parent = main.parents('.modal-content:first');

	function checkStates() {
		var activeButtons = buttons.filter('.active');

		if (buttons.filter('[data-rel="hideColumn"]').length) {
			if (activeButtons.is('[data-rel="hideColumn"]')) {
				parent.find('.tableClone').show();
				parent.find('[data-fixcolumn]').find('tr').find('td:first, th:first').show();
			} else {
				parent.find('.tableClone').hide();
				parent.find('[data-fixcolumn]').find('tr').find('td:first, th:first').hide();
			}
		}

		if (activeButtons.is('[data-rel="marks"]')) {
			parent.find(".form-value-field").each(function(indx, item) {
				$(item).hide();
			});
			parent.find(".form-display-form-field").each(function(indx, item) {
				$(item).show();
			});
		} else {
			parent.find(".form-display-form-field").each(function(indx, item) {
				$(item).hide();
			});
			parent.find(".form-value-field").each(function(indx, item) {
				$(item).show();
			});
		}

		parent.find('.scrollable-table').trigger('scrollableTable:quickUpdate');
	}

	buttons.on('click', function(e) {
		e.preventDefault();
		$(this).toggleClass('active');
		checkStates();
	});
}


const datasetAllCode = 'dsall'

function getActiveDatasetCodes(container) {
	if (!container) {
		return [datasetAllCode];
	}
	const activeCheckboxes = container?.querySelectorAll("input:checked");
  if (!activeCheckboxes.length) {
    return [datasetAllCode];
  }
	// Join all dataset codes with a comma
  const dataCodes = Array.from(activeCheckboxes).reduce((acc, checkbox, i) => {
    acc += `${i > 0 ? "," : ""}${checkbox.dataset?.filterCode}`;
    return acc;
  }, "");
  return [dataCodes, activeCheckboxes.length];
}

function updateDatasetFilterCount(codes, count) {
	const element = document.getElementById('dataset-filter-count');
	if (element) {
		element.innerText = !codes.startsWith(datasetAllCode) ? count : '';
	}
}


function initDatasetDropdown() {
	// Prevent checkbox label from closing menu
	$(document).on('click', '#dataset-filter-wrapper .search-filter__menu', function(e) {
		e.stopPropagation();
	});

	let menuContainer = document.getElementById('dataset-filters-popover')?.nextElementSibling
	$(document).on("change", "#dataset-filter-wrapper .search-filter__menu input", function (e) {
    e.stopPropagation();
    if (!menuContainer) {
      menuContainer = document.getElementById(
        "dataset-filters-popover"
      )?.nextElementSibling;
    }
		
		const inputs = Array.from(menuContainer.getElementsByTagName("input"));
		if (e.target?.dataset?.filterCode === datasetAllCode) {
			// Set all other inputs if the "All" choice is selected
      inputs.forEach((input) => {
				input.checked = e.target?.checked;
      });
    } else {
			const inputAll = document.getElementById("dataset-input-dsall");
			// Set input all to checked if all other checkboxes are checked
			inputAll.checked = inputs.every(input => input === inputAll || input.checked);
		}
  });


	let [dataCodes, datasetCount] = getActiveDatasetCodes(menuContainer);
	updateDatasetFilterCount(dataCodes, datasetCount);
	$(document).on("hide.bs.dropdown", "#dataset-filter-wrapper", function () {
    const [newCodes, newCount] = getActiveDatasetCodes(menuContainer);
    if (newCodes !== dataCodes) {
			// Push new search to another tick, otherwise the dropdown event keeps repeating
      requestAnimationFrame(() => {
				// Keep track of active codes so we don't search again if nothing changed
				dataCodes = newCodes
				datasetCount = newCount
				updateDatasetFilterCount(dataCodes, datasetCount)
        $("input[name='datasetCodesStr']").val(dataCodes);
        setSelectedWordHomonymNrAndLang();
        clickSearchIfInputExists();
      });
    }
  });
}


const langAllCode = 'dlall'

function getActiveLangCodes(container) {
	if (!container) {
		return [langAllCode];
	}
	const foundCheckboxes = container?.querySelectorAll("input:checked");
  if (!foundCheckboxes.length) {
    return [langAllCode];
  }
	const activeCheckboxes = Array.from(foundCheckboxes);
	// Join all dataset codes with a comma
  const dataCodes = activeCheckboxes.reduce((acc, checkbox, i) => {
    acc += `${i > 0 ? "," : ""}${checkbox.dataset?.filterCode}`;
    return acc;
  }, "");
  return [dataCodes, activeCheckboxes];
}

function updateLangFilterStates(codes, checkboxes) {
	const countElement = document.getElementById('lang-filter-count');
	if (countElement) {
		countElement.innerText = !codes.startsWith(langAllCode) ? checkboxes.length : '';
	}

	const tagsElement = document.getElementById('lang-filter-tags');
	if (tagsElement) {
		if (codes.startsWith(langAllCode)) {
			// Delete tag elements if the "All" choice is selected
			tagsElement.replaceChildren();
		} else {
			const fragment = document.createDocumentFragment();
			// Create tags for each active checkbox
			checkboxes.forEach(checkbox => {
				const langButton = document.createElement('button')
				langButton.classList.add('search-filter__tag');
				langButton.dataset.filterCode = checkbox.dataset?.filterCode
				langButton.innerHTML = `<span>${checkbox.name}</span><i class="fa fa-times"></i>`;
				fragment.appendChild(langButton)
			})
			tagsElement.replaceChildren(fragment)
		}
	}
}

function initLangDropdown() {
	// Prevent checkbox label from closing menu
	$(document).on('click', '#lang-filter-wrapper .search-filter__menu', function(e) {
		e.stopPropagation();
	});
	let menuContainer = document.getElementById('lang-filters-popover')?.nextElementSibling
	$(document).on("change", "#lang-filter-wrapper .search-filter__menu input", function (e) {
    e.stopPropagation();
    if (!menuContainer) {
			menuContainer = document.getElementById(
				"lang-filters-popover"
      )?.nextElementSibling;
    }
		const inputs = Array.from(menuContainer.getElementsByTagName("input"));
    if (e.target?.dataset?.filterCode === langAllCode) {
			// Set all other inputs if the "All" choice is selected
      inputs.forEach((input) => {
				input.checked = e.target?.checked;
      });
    } else {
			const inputAll = document.getElementById("lang-input-dlall");
			inputAll.checked = inputs.every(input => input === inputAll || input.checked);
		}
  });


	let [langCodes, langCheckboxes] = getActiveLangCodes(menuContainer);
	updateLangFilterStates(langCodes, langCheckboxes);
	$(document).on("hide.bs.dropdown", "#lang-filter-wrapper", function () {
    const [newCodes, newLangCheckboxes] = getActiveLangCodes(menuContainer);
    if (newCodes !== langCodes) {
			// Push new search to another tick, otherwise the dropdown event keeps repeating
      requestAnimationFrame(() => {
				// Keep track of active codes so we don't search again if nothing changed
				langCodes = newCodes
				langCheckboxes = newLangCheckboxes
				updateLangFilterStates(langCodes, langCheckboxes)
        $("input[name='destinLangsStr']").val(langCodes);
        setSelectedWordHomonymNrAndLang();
        clickSearchIfInputExists();
      });
    }
  });

	$(document).on('click', '#lang-filter-tags button', function(e) {
		const targetCheckbox = menuContainer.querySelector(`input[data-filter-code=${e.currentTarget.dataset?.filterCode}]`)
		// Handle button as if the checkbox with the same value was pressed instead
		if (targetCheckbox) {
			targetCheckbox.checked = false;
			const [newCodes, newLangCheckboxes] = getActiveLangCodes(menuContainer);
			langCodes = newCodes
			langCheckboxes = newLangCheckboxes
			if (!langCheckboxes?.length) {
				const inputAll = document.getElementById("lang-input-dlall");
				if (inputAll) {
					inputAll.checked = true;
				}
			}
			updateLangFilterStates(langCodes, langCheckboxes)
			$("input[name='destinLangsStr']").val(langCodes);
			setSelectedWordHomonymNrAndLang();
			clickSearchIfInputExists();
		}
	})
}
