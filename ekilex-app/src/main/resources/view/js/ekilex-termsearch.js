// add on click handlers to details buttons in search result table
function initialise() {
	$(document).on("click", ":button[name='detailsBtn']", function() {
		var id = $(this).data('id');
		var isRestoreScrollPos = this.hasAttribute('data-refresh');
		$("[id^='meaning_select_point_']").hide();
		$("[id^='meaning_select_wait_']").hide();
		$("#meaning_select_wait_" + id).show();
		$.get(applicationUrl + 'meaningdetails/' + id).done(function(data) {
			var scrollPos = $('#details_div').scrollTop();
			$('#details_div').replaceWith(data);
			if (isRestoreScrollPos) {
				$('#details_div').scrollTop(scrollPos);
			}
			$("#meaning_select_wait_" + id).hide();
			$("#meaning_select_point_" + id).show();
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

	$(document).on('click', '[name="lang-collapse-btn"]', function() {
		var lang = $(this).attr("data-lang");
		var itemData = {
			opCode: "term_user_lang",
			code: lang
		};
		postJson(applicationUrl + 'update_item', itemData).done(function(data) {
			refreshDetails();
		});
	});

	$(document).on('click', '#duplicateMeaningBtn', function() {
		let url = applicationUrl + 'duplicatemeaning/' + $(this).data('meaning-id');
		$.post(url).done(function(data) {
			let response = JSON.parse(data);
			if (response.status === 'ok') {
				openMessageDlg(response.message);
			} else {
				openAlertDlg(response.message);
			}
		}).fail(function(data) {
			openAlertDlg("Mõiste dubleerimine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[name="pagingBtn"]', function() {
		openWaitDlg("Palun oodake, andmete uuendamine on pooleli");
		let url = applicationUrl + "term_paging";
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

	$(document).on('change', '#meaning-other-words-visible-check', function() {
		if (this.checked) {
			$(".other-words").fadeIn();
		} else {
			$(".other-words").fadeOut();
		}
	});

	$(document).on('change', '#resultLang', function() {
		$(this).closest('form').submit();
	});

	$(document).on('show.bs.modal', '#meaningLifecycleLogDlg', function(e) {
		var dlg = $(this);
		var link = $(e.relatedTarget);
		var url = link.attr('href');
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

	var detailsButtons = $('#results').find('[name="detailsBtn"]');
	if (detailsButtons.length === 1) {
		detailsButtons.trigger('click');
	}

	initNewWordDlg();
}

function refreshDetails() {
	var refreshButton = $('#refresh-details');
	refreshButton.trigger('click');
}

function doNewSearch() {
	$('#simple_search_filter').find('button[type=submit]').trigger('click');
}

function deleteMeaningAndLexemesAndWords() {
	var opName = "delete";
	var opCode = "meaning";
	var meaningId = $(this).attr("data-id");
	var successCallbackName = $(this).attr("data-callback");
	let successCallbackFunc = () => eval(successCallbackName)($(this));

	executeMultiConfirmPostDelete(opName, opCode, meaningId, successCallbackFunc);
}