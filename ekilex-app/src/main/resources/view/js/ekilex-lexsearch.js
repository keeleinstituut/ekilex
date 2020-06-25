function initializeLexSearch() {

	$(document).on("click", ":button[name='word-details-btn']", function() {
		let wordId = $(this).data('id');
		loadWordDetails(wordId);
	});

	$(document).on("click", ":button[name='lexeme-details-btn']", function() {
		let lexemeId = $(this).data('id');
		let lexemeLevels = $(this).data('lex-levels');
		let composition = $(this).data('composition');
		loadLexemeDetails(lexemeId, lexemeLevels, composition);
	});

	$(document).on('click', '.order-up', function() {
		let orderingBtn = $(this);
		let orderingData = changeItemOrdering(orderingBtn, -1);
		postJson(applicationUrl + 'update_ordering', orderingData);
		if (orderingBtn.hasClass('do-refresh')) {
			refreshDetailsLexSearch();
		}
	});

	$(document).on('click', '.order-down', function() {
		let orderingBtn = $(this);
		let orderingData = changeItemOrdering(orderingBtn, 1);
		postJson(applicationUrl + 'update_ordering', orderingData);
		if (orderingBtn.hasClass('do-refresh')) {
			refreshDetailsLexSearch();
		}
	});

	$(document).on('click', '[name="lang-collapse-btn"]', function() {
		let lang = $(this).attr("data-lang");
		let itemData = {
			opCode: "user_lang_selection",
			code: lang
		};
		let successCallbackName = $(this).attr("data-callback");
		let	successCallbackFunc = () => eval(successCallbackName);
		postJson(applicationUrl + 'update_item', itemData).done(function() {
			successCallbackFunc();
		});
	});

	$(document).on('show.bs.modal', '#wordLifecycleLogDlg', function(e) {
		let dlg = $(this);
		let link = $(e.relatedTarget);
		let url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});

	$(document).on('show.bs.modal', '#processLogDlg', function(e) {
		var dlg = $(this);
		var link = $(e.relatedTarget);
		var url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});

	$(document).on('click', '[id^=duplicateLexemeBtn_]', function() {
		var lexemeId = $(this).data('lexeme-id');
		let url = applicationUrl + 'lexduplicate/' + lexemeId;
		$.post(url).done(function(data) {
			let response = JSON.parse(data);
			if (response.status === 'ok') {
				openMessageDlg(response.message);
				refreshDetailsLexSearch();
			} else {
				openAlertDlg(response.message);
			}
		}).fail(function(data) {
			openAlertDlg("Tähenduse dubleerimine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[id^=duplicateEmptyLexemeBtn_]', function() {
		var lexemeId = $(this).data('lexeme-id');
		var url = applicationUrl + 'emptylexduplicate/' + lexemeId;
		$.post(url).done(function(data) {
			var response = JSON.parse(data);
			openMessageDlg(response.message);
			refreshDetailsLexSearch();
		}).fail(function(data) {
			openAlertDlg("Tähenduse lisamine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[id^=duplicateMeaningWordAndLexemeBtn_]', function() {
		let lexemeId = $(this).data('lexeme-id');
		let successCallbackName = $(this).data("callback");
		let	successCallbackFunc = () => eval(successCallbackName);
		let url = applicationUrl + 'meaningwordandlexduplicate/' + lexemeId;
		$.post(url).done(function() {
			successCallbackFunc();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg("Vaste dubleerimine ebaõnnestus");
		});
	});

	$(document).on('click', '[name="pagingBtn"]', function() {
		openWaitDlg();
		let url = applicationUrl + "lex_paging";
		let button = $(this);
		let direction = button.data("direction");
		let form = button.closest('form');
		form.find('input[name="direction"]').val(direction);

		$.ajax({
			url: url,
			data: form.serialize(),
			method: 'POST',
		}).done(function (data) {
			closeWaitDlg();
			$('#results_div').html(data);
			$('#results_div').parent().scrollTop(0);
			$('#word-details-area').empty();
		}).fail(function (data) {
			console.log(data);
			closeWaitDlg();
			openAlertDlg('Lehekülje muutmine ebaõnnestus');
		});

	});

	let detailButtons = $('#results').find('[name="word-details-btn"]');
	if (detailButtons.length === 1) {
		detailButtons.trigger('click');
	}

	initNewWordDlg();
	initClassifierAutocomplete();
};

function loadWordDetails(wordId) {
	$("[id^='word_select_wait_']").hide();
	$("#word_select_wait_" + wordId).show();
	$('#results_div .list-group-item').removeClass('active');
	$("#word-result-" + wordId).addClass('active');
	openWaitDlg();
	let wordDetailsUrl = applicationUrl + 'worddetails/' + wordId;
	$.get(wordDetailsUrl).done(function(data) {
		let detailsDiv = $('#word-details-area');
		let scrollPos = detailsDiv.scrollTop();
		detailsDiv.replaceWith(data);
		detailsDiv = $('#word-details-area');
		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();
		detailsDiv.scrollTop(scrollPos);
		$("#word_select_wait_" + wordId).hide();
		$('.tooltip').remove();
		closeWaitDlg();

		$('[data-toggle="tooltip"]').tooltip({trigger:'hover'});
	}).fail(function(data) {
		console.log(data);
		closeWaitDlg();
		alert('Keelendi detailide päring ebaõnnestus');
	});
};

function loadFullLexemeDetails(lexemeId, lexemeLevels) {
	loadLexemeDetails(lexemeId, lexemeLevels, "full");
};

function loadLexemeDetails(lexemeId, lexemeLevels, composition) {
	openWaitDlg();
	let lexemeDetailsUrl = applicationUrl + 'lexemedetails/' + composition + '/' + lexemeId + '/' + lexemeLevels;
	$.get(lexemeDetailsUrl).done(function(data) {
		let detailsDiv = $('#lexeme-details-' + lexemeId);
		detailsDiv.html(data);
		detailsDiv = $('#lexeme-details-' + lexemeId);
		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();
		$('.tooltip').remove();
		closeWaitDlg();
		$('[data-toggle="tooltip"]').tooltip({trigger:'hover'});
	}).fail(function(data) {
		console.log(data);
		closeWaitDlg();
		alert('Lekseemi detailide päring ebaõnnestus');
	});
};

function initLexemeLevelsDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let editForm = editDlg.find('form');
		editDlg.find('[name="action"]').val($(this).data('action'));
		let url = editForm.attr('action') + '?' + editForm.serialize();
		$.post(url).done(function(data) {
			let id = $('#word-details-area').data('id');
			let detailsButton = $('[name="word-details-btn"][data-id="' + id + '"]');
			detailsButton.trigger('click');
			editDlg.find('button.close').trigger('click');
		}).fail(function(data) {
			alert("Andmete muutmine ebaõnnestus.");
			console.log(data);
		});
	});
};

function initUsageAuthorDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'source-id');
};

function initEditMeaningWordAndLexemeWeightDlg(dlg) {
	let wordValueEditFld = dlg.find('[data-name=wordValueEditFld]');
	let wordValueInput = dlg.find('[name=wordValuePrese]');
	let ekiEditorElem = dlg.find('.eki-editor');
	wordValueEditFld.removeClass('is-invalid');
	wordValueEditFld.html(wordValueInput.val());
	initEkiEditor(ekiEditorElem);

	dlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		if (wordValueEditFld.html()) {
			wordValueEditFld.removeClass('is-invalid');
			wordValueInput.val(wordValueEditFld.html());
			submitDialog(e, dlg, 'Andmete muutmine ebaõnnestus.');
		} else {
			wordValueEditFld.addClass('is-invalid');
		}
	});
};

function refreshDetailsLexSearch() {
	var refreshButton = $('#refresh-details');
	refreshButton.trigger('click');
};

function doNewSearchLexDetail() {
	$('#simple_search_filter').find('button[type=submit]').trigger('click');
};
