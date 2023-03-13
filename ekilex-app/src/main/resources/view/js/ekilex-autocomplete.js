function initSourceNameAutocomplete(dlg) {

	let searchWordAutocompleteConfig = {
		source: function(request, response) {
			let searchFilter = request.term;
			let searchUrl = applicationUrl + 'sourcenamesearch/' + searchFilter;

			$.ajax({
				url: searchUrl,
				type: "GET",
				success: function(sourceNames) {
					let fullList = [];
					$.each(sourceNames, function(index, sourceName) {
						let label;
						if (sourceName.length > 101) {
							label = sourceName.substr(0, 100) + '...';
						} else {
							label = sourceName;
						}
						fullList.push({
							label: label,
							value: sourceName
						});
					});
					response(fullList);
				}
			});
		},
		minLength: 2,
		create: function() {
			return false;
		},
		select: function(event, ui) {
			if (ui.item) {
				dlg.find("input[name='searchFilter']").val(ui.item.value);
				// Using data attribute as an additional selector to avoid also pressing the submit button that adds selection
				dlg.find('button[type="submit"][data-submit-id="source_search_form"]').click();
			}
			return false;
		},
		open: function() {
			return false;
		},
		close: function() {
			return false;
		}
	};

	dlg.find("input[name='searchFilter']").autocomplete(searchWordAutocompleteConfig).autocomplete("instance");
};