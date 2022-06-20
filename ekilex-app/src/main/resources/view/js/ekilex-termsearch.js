/*redundant*/
// add on click handlers to details buttons in search result table
function initializeTermSearch() {
	// $(document).on('click', '[name="lang-collapse-btn"]', function() {
	// 	let lang = $(this).attr("data-lang");
	// 	let itemData = {
	// 		opCode: "user_lang_selection",
	// 		code: lang
	// 	};
	// 	postJson(applicationUrl + 'update_item', itemData).done(function(data) {
	// 		refreshDetailsTermsSearch();
	// 	});
	// });

	// $(document).on('click', '[name="pagingBtn"]', function() {
	// 	openWaitDlg();
	// 	let url = applicationUrl + "term_paging";
	// 	let button = $(this);
	// 	let direction = button.data("direction");
	// 	let form = button.closest('form');
	// 	form.find('input[name="direction"]').val(direction);

	// 	$.ajax({
	// 		url: url,
	// 		data: form.serialize(),
	// 		method: 'POST',
	// 	}).done(function (data) {
	// 		closeWaitDlg();
	// 		$('#results_div').html(data);
	// 		$('#results_div').parent().scrollTop(0);
	// 		$('#details-area').empty();
	// 	}).fail(function (data) {
	// 		console.log(data);
	// 		closeWaitDlg();
	// 		openAlertDlg('Lehekülje muutmine ebaõnnestus');
	// 	});

	// });

	// Moved to common as plugin
	// $(document).on("click", "#activeTagCompleteBtn", function() {
	// 	let meaningId = $(this).data('meaning-id');
	// 	let actionUrl = applicationUrl + "update_meaning_active_tag_complete/" + meaningId;
	// 	$.post(actionUrl).done(function(data) {
	// 		if (data !== "{}") {
	// 			openAlertDlg("Andmete muutmine ebaõnnestus.");
	// 			console.log(data);
	// 		}
	// 		refreshDetailsTermsSearch();
	// 	}).fail(function(data) {
	// 		openAlertDlg("Andmete muutmine ebaõnnestus.");
	// 		console.log(data);
	// 	});
	// });

	$(document).on('change', '[name="resultLang"]', function() {
		$(this).closest('form').submit();
	});

	$(document).on('change', '[name="resultMode"]', function() {
		$(this).closest('form').submit();
	});

	// Replaced by plugin from common
	// $(document).on('show.bs.modal', '#meaningActivityLogDlg', function(e) {
	// 	var dlg = $(this);
	// 	var link = $(e.relatedTarget);
	// 	var url = link.attr('href');
	// 	dlg.find('.close').focus();
	// 	dlg.find('.modal-body').html(null);
	// 	$.get(url).done(function(data) {
	// 		dlg.find('.modal-body').html(data);
	// 	});
	// });

	var detailsButtons = $('#results').find('[name="meaning-details-btn"]');
	if (detailsButtons.length > 0) {
		detailsButtons.first().trigger('click');
	}

	initNewWordDlg();
	initClassifierAutocomplete();
};

$.fn.approveMeaning = function() {
	var main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		var form = main.closest('form');
		var meaningId = main.data("meaning-id");
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
	var main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		var form = main.closest('form');
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

function refreshDetailsTermsSearch() {
	var refreshButton = $('#refresh-details');
	refreshButton.trigger('click');
};

function doNewSearchTermSearch() {
	$('#simple_search_filter').find('button[type=submit]').trigger('click');
};

function deleteMeaningAndLexemesAndWords() {
	var opName = "delete";
	var opCode = "meaning";
	var meaningId = $(this).attr("data-id");
	var successCallbackName = $(this).attr("data-callback");
	let successCallbackFunc = () => eval(successCallbackName)($(this));
	executeMultiConfirmPostDelete(opName, opCode, meaningId, successCallbackFunc, true);
};

function dublicateMeaning() {
	let url = applicationUrl + 'duplicatemeaning/' + $(this).data('meaning-id');
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
