function initialise() {
	$(document).on("click", ":button[name='detailsBtn']", function() {
		let id = $(this).data('id');
		let isRestoreDisplayState = this.hasAttribute('data-refresh');
		let openLexemes = [];
		$('.d-none[data-lexeme-title]').each(function(index, item) {
			openLexemes.push($(item).data('toggle-name'));
		});
		$("[id^='word_select_point_']").hide();
		$("[id^='word_select_wait_']").hide();
		$("#word_select_wait_" + id).show();
		$.get(applicationUrl + 'worddetails/' + id).done(function(data) {
			let detailsDiv = $('#details_div');
			let scrollPos = detailsDiv.scrollTop();
			detailsDiv.replaceWith(data);
			initLexemeToggleButtons();
			if (isRestoreDisplayState) {
				detailsDiv.scrollTop(scrollPos);
				openLexemes.forEach(function(lexemeName) {
					$('[data-toggle-name=' + lexemeName + ']').find('.btn-toggle').trigger('click');
				})
			}
			$("#word_select_wait_" + id).hide();
			$("#word_select_point_" + id).show();
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

	$(document).on('click', '.order-up', function() {
		let orderingData = changeItemOrdering($(this), -1);
		postJson(applicationUrl + 'update_ordering', orderingData);
	});

	$(document).on('click', '.order-down', function() {
		let orderingData = changeItemOrdering($(this), 1);
		postJson(applicationUrl + 'update_ordering', orderingData);
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
		let url = applicationUrl + 'duplicatelexeme/' + lexemeId;
		$.post(url).done(function(data) {
			let response = JSON.parse(data);
			if (response.status === 'ok') {
				openMessageDlg(response.message);
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
		var url = applicationUrl + 'duplicateemptylexeme/' + lexemeId;
		$.post(url).done(function(data) {
			var response = JSON.parse(data);
			openMessageDlg(response.message);
		}).fail(function(data) {
			openAlertDlg("Tähenduse lisamine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[name="pagingBtn"]', function() {
		openWaitDlg("Palun oodake, andmete uuendamine on pooleli");
		let url = applicationUrl + "update_lex_paging";
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
			$('#details_div').empty();
		}).fail(function (data) {
			console.log(data);
			closeWaitDlg();
			openAlertDlg('Lehekülje muutmine ebaõnnestus');
		});

	});

	let detailButtons = $('#results').find('[name="detailsBtn"]');
	if (detailButtons.length === 1) {
		detailButtons.trigger('click');
	}

	initNewWordDlg();
}

function initLexemeLevelsDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let editForm = editDlg.find('form');
		editDlg.find('[name="action"]').val($(this).data('action'));
		let url = editForm.attr('action') + '?' + editForm.serialize();
		$.post(url).done(function(data) {
			let id = $('#details_div').data('id');
			let detailsButton = $('[name="detailsBtn"][data-id="' + id + '"]');
			detailsButton.trigger('click');
			editDlg.find('button.close').trigger('click');
		}).fail(function(data) {
			alert("Andmete muutmine ebaõnnestus.");
			console.log(data);
		});
	});
}

function initAddWordRelationDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'word-id');
}

function initUsageAuthorDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'source-id');
}

function initLexemeToggleButtons() {
	let toggleButtons = $('.btn-toggle');
	toggleButtons.on('click', toggleLexeme);
	if (toggleButtons.length === 2) {
		$(toggleButtons[0]).trigger('click');
	}
}

function toggleLexeme(e) {
	let elementToClose = $(e.currentTarget).closest('[data-toggle-name]');
	let targetName = $(e.currentTarget).data('toggle-target');
	let elementToShow = $('[data-toggle-name=' + targetName + ']');
	elementToClose.addClass('d-none');
	elementToShow.removeClass('d-none');
}

function doNewSearch() {
	$('#simple_search_filter').find('button[type=submit]').trigger('click');
}

function deleteLexemeAndWordAndMeaning() {
	var opName = "delete";
	var opCode = "lexeme";
	var lexemeId = $(this).attr("data-id");
	var successCallbackName = $(this).attr("data-callback");
	let successCallbackFunc = () => eval(successCallbackName)($(this));

	executeMultiConfirmPostDelete(opName, opCode, lexemeId, successCallbackFunc);
}
