// add on click handlers to details buttons in search result table
function initialise() {
	$(document).on("click", ":button[name='detailsBtn']", function() {
		var id = $(this).data('id');
		var isRestoreScrollPos = this.hasAttribute('data-refresh');
		$.get(applicationUrl + 'meaningdetails/' + id).done(function(data) {
			var scrollPos = $('#details_div').scrollTop();
			$('#details_div').replaceWith(data);
			if (isRestoreScrollPos) {
				$('#details_div').scrollTop(scrollPos);
			}
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
			opCode : "term_user_lang",
			code : lang
		};
		postJson(applicationUrl + 'modify_item', itemData).done(function(data) {
			refreshDetails();
        });
	});

	$(document).on('click', '#meaningCopyBtn', function() {
		let url = applicationUrl + 'meaningcopy/' +  $(this).data('meaning-id');
		$.post(url).done(function(data) {
			let response = JSON.parse(data);
			openMessageDlg(response.message);
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

    var editDlg = $('#editDlg');
	editDlg.find('[name=value]').attr("rows", 4);
    editDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        alignAndFocus(e, editDlg)
    });

    $('#ekiEditorDlg').find('[name=editFld]').removeClass('edit-area-sm').addClass('edit-area-lg');
    $('#addNewDefinitionDlg').find('[name=editFld]').removeClass('edit-area-sm').addClass('edit-area-lg');
    $('#addNewUsageDlg').find('[name=editFld]').removeClass('edit-area-sm').addClass('edit-area-lg');
    $('#addNewGovernmentUsageDlg').find('[name=value]').attr("rows", 4);
    initNewWordDlg();
    initSelectDlg($('#meaningDomainDlg'));
    initMultiValueAddDlg($('#termMeaningClassifiersDlg'));
    initMultiValueAddDlg($('#termLexemeClassifiersDlg'));
	initSelectDlg($('#lexemeFrequencyDlg'));
	initSelectDlg($('#lexemePosDlg'));
	initSelectDlg($('#lexemeDerivDlg'));
	initSelectDlg($('#lexemeRegisterDlg'));
	initSelectDlg($('#wordGenderDlg'));
	initSelectDlg($('#wordTypeDlg'));
	initSelectDlg($('#lexemeValueStateCodeDlg'));
	initSelectDlg($('#lexemeProcessStateCodeDlg'));
}

function updateTermUserLangWrapup(clickable) {
	var langWrapupArr = clickable.closest('.orderable').find("[data-value]").filter(function() {
		return $(this).find("input[name='term_user_lang_check']").is(":checked");
	}).map(function() {
		return $(this).attr("data-value");
	}).get();
	var languagesWrapupLimit = 4;
	var langWrapup;
	if (langWrapupArr.length > languagesWrapupLimit) {
		langWrapup = langWrapupArr.slice(0, languagesWrapupLimit).join(", ") + " ...";
	} else {
		langWrapup = langWrapupArr.join(", ");
	}
	$("#term_user_lang_wrapup").text(langWrapup);
}

function refreshDetails() {
	var refreshButton = $('#refresh-details');
    refreshButton.trigger('click');
}
