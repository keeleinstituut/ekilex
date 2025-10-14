$(document).ready(function() {
	focusSearchInput();
	searchOsWordAutocomplete();
	initHomonymMobileSelect();
});

function focusSearchInput() {
	const searchInput = document.getElementById('searchInput');
	searchInput.focus();
	const textLength = searchInput.value?.length ?? 0;
	// Set text cursor to end, goes to beginning by default
	searchInput.setSelectionRange(textLength, textLength);
}

$(document).on('click', 'ext-link', function() {
	const link = $(this);
	const href = link.attr('href');
	if (href) {
		const target = link.attr('target');
		if (href.startsWith('https://')) {
			window.open(href, target);
		} else {
			window.open(`https://${href}`, target);
		}
	} else {
		openAlertDlg(messages["common.broken.link"]);
	}
});


function searchOsWordAutocomplete() {

	const wordRelValuesTitle = "Artiklis esitatud sõnad";
	const wordRelComponentsTitle = "Sõna osad";

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
			if (group == "wordRelValues") {
				var li = $("<li>");
				li.addClass("list-group-item list-group-item-info py-1");
				li.css("pointer-events", "none");
				li.text(wordRelValuesTitle);
				ul.append(li);
			}
			if (group == "wordRelComponents") {
				var li = $("<li>");
				li.addClass("list-group-item list-group-item-info py-1");
				li.css("pointer-events", "none");
				li.text(wordRelComponentsTitle);
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
					$.each(data.infixWords, function(index, item) {
						fullList.push({
							group: "infixWords",
							label: item,
							value: item
						});
					});
					$.each(data.wordRelValues, function(index, item) {
						fullList.push({
							group: "wordRelValues",
							label: item,
							value: item
						});
					});
					$.each(data.wordRelComponents, function(index, item) {
						fullList.push({
							group: "wordRelComponents",
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
				$("input[name='searchValue']").val(ui.item.value);
				$("#search-btn").trigger('click');
			}
			return false;
		},
		open: function() {
			//searchWordAutocompleteHeight();
			return false;
		},
		close: function() {
			return false;
		}
	};

	$(document).on("change input", "input[name='searchValue']", function() {
		$(this).autocomplete(searchWordAutocompleteConfig).autocomplete("search");
	});
};


function initHomonymMobileSelect() {
	const toggle = document.getElementById('os-homonym-toggle');
	const list = document.getElementById('os-homonym-list');
	if (!toggle || !list) {
		return;
	}

	const selectedItem = list.querySelector('.selected a');

	if (selectedItem) {
		toggle.innerHTML = selectedItem.innerHTML;
	}

	toggle.addEventListener('click', () => {
		const isOpen = list.getAttribute('aria-expanded') === 'true';
		if (isOpen) {
			list.classList.remove('expand');
			list.setAttribute('aria-expanded', false);
		} else {
			list.classList.add('expand');
			list.setAttribute('aria-expanded', true);
		}
	});

	
	const listItemLinks = Array.from(list.querySelectorAll('[role="menuitem"]'));
	toggle.addEventListener('keydown', (e) => {
		if (e.key === 'Escape') {
			list.classList.remove('expand');
			list.setAttribute('aria-expanded', false);
		} else if (e.key === 'ArrowDown') {
			const firstItem = listItemLinks[0];
			if (firstItem) {
				e.preventDefault();
				firstItem.focus();
			}
		}
	});
	listItemLinks.forEach(item => {
		item.addEventListener('keydown', (e) => {
			const listItem = e.target?.closest('li');
			if (['ArrowDown', 'ArrowUp', 'Home', 'End'].includes(e.key)) {
				e.preventDefault();
			}
			switch (e.key) {
				case 'ArrowDown': {
					const nextItem = listItem.nextElementSibling;
					if (nextItem) {
						const newTarget = nextItem.querySelector('a');
						newTarget.focus();
					}
					break;
				};
				case 'ArrowUp': {
					const prevItem = listItem.previousElementSibling;
					if (prevItem) {
						const newTarget = prevItem.querySelector('a');
						newTarget.focus();
					}
					break;
				}
				case 'Home': {
					const firstItem = listItemLinks[0];
					if (firstItem) {
						firstItem.focus();
					}
					break;
				}
				case 'End': {
					const lastItem = listItemLinks.at(-1);
					if (lastItem) {
						lastItem.focus();
					}
					break;
				}
			}
		});
	});

	document.addEventListener('keydown', (e) => {
		if (e.key === 'Escape') {
			list.classList.remove('expand');
			list.setAttribute('aria-expanded', false);
			toggle.focus();
		}
	});
}
