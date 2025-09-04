$(document).ready(function() {
	focusSearchInput();
	searchOsWordAutocomplete();
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
			if (group == "infixWordRelation") {
				var li = $("<li>");
				li.addClass("list-group-item list-group-item-info py-1");
				li.text("Seotud sõnad");
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
							group: "infixWord",
							label: item,
							value: item
						});
					});
					$.each(data.infixWordRelations, function(index, item) {
						fullList.push({
							group: "infixWordRelation",
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
