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
		postJson(applicationUrl + 'modify_ordering', orderingData);
	});

	$(document).on('click', '.order-down', function() {
		let orderingData = changeItemOrdering($(this), 1);
		postJson(applicationUrl + 'modify_ordering', orderingData);
	});

	$(document).on('click', '#show-all-btn', function() {
		$('#fetchAll').val(true);
		$('#fetchAll').closest('form').find('button[type="submit"]').trigger('click');
	});

	$(document).on('click', '[name="lang-collapse-btn"]', function() {
		var lang = $(this).attr("data-lang");
		var itemData = {
			opCode: "term_user_lang",
			code: lang
		};
		postJson(applicationUrl + 'modify_item', itemData).done(function(data) {
			refreshDetails();
		});
	});

	$(document).on('click', '#meaningCopyBtn', function() {
		let url = applicationUrl + 'meaningcopy/' + $(this).data('meaning-id');
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