/*redundant*/
// add on click handlers to details buttons in search result table
function initializeTermSearch() {
	$(document).on('change', '[name="resultLang"]', function() {
		$(this).closest('form').submit();
	});

	$(document).on('change', '[name="resultMode"]', function() {
		$(this).closest('form').submit();
	});

	const detailsButtons = $('#results').find('[name="meaning-details-btn"]');
	if (detailsButtons.length > 0) {
		detailsButtons.first().click();
	}

	initNewWordDlg();
	initClassifierAutocomplete();
};

$.fn.approveMeaning = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const form = main.closest('form');
		const meaningId = main.data("meaning-id");
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function() {
			loadDetails(meaningId, 'replace', meaningId);			
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		});
	});
}

$.fn.queueTermSearchResults = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const form = main.closest('form');
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'POST',
		}).done(function() {
			$("#queueTermSearchResultsBtn").hide();
			$("#queueTermSearchResultsMsg").show();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		});
	});
}

$.fn.tableViewTermSearchResults = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const resultCount = main.data('result-count');
		if (resultCount > 50) {
			openAlertDlg(messages["term.meaning.table.max.meanings.exceeded"]);
		} else {
			main.closest('form').submit();
		}
	});
}

function refreshDetailsTermsSearch() {
	$('#refresh-details').click();
};

function doNewSearchTermSearch() {
	$('#simple_search_filter').find('button[type=submit]').click();
};

function deleteMeaningAndLexemesAndWords() {
	const $this = $(this);
	const opName = "delete";
	const opCode = "meaning";
	const meaningId = $this.attr("data-id");
	const successCallback = $this.attr("data-callback");
	let successCallbackFunc = createCallback(successCallback, $this);
	executeMultiConfirmPostDelete(opName, opCode, meaningId, successCallbackFunc, true);
};

function duplicateMeaning() {
	const url = `${applicationUrl}meaningduplicate/${$(this).data('meaning-id')}`;
	$.post(url).done(function(response) {
		if (response.status === "OK") {
			let duplicateMeaningId = response.id;
			loadDetails(duplicateMeaningId, 'compare');
			openMessageDlg(response.message);
		} else {
			openAlertDlg(response.message);
		}
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
}

function getTermSearchBackUri() {
	const pathname = window.location.pathname;
	const backUriStartIndex = pathname.indexOf('/termsearch');
	let backUri = pathname.substring(backUriStartIndex);
	const selectedIds = QueryParams.get('id');
	const pageNum = getCurrentPageNum();

	if (selectedIds !== undefined) {
		backUri += "?id=" + selectedIds;
		if (pageNum !== undefined) {
			backUri += "&p=" + pageNum;
		}
	} else {
		if (pageNum !== undefined) {
			backUri += "?p=" + pageNum;
		}
	}
	return backUri;
}
