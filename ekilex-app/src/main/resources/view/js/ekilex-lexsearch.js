function initialise() {
    $(document).on("click", ":button[name='detailsBtn']", function() {
        let id = $(this).data('id');
        let isRestoreDisplayState = this.hasAttribute('data-refresh');
        let openLexemes = [];
        $('.d-none[data-lexeme-title]').each(function (index, item) {
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
                openLexemes.forEach(function (lexemeName) {
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
        postJson(applicationUrl + 'modify_ordering', orderingData);
    });

    $(document).on('click', '.order-down', function() {
        let orderingData = changeItemOrdering($(this), 1);
        postJson(applicationUrl + 'modify_ordering', orderingData);
    });

    $(document).on('click', '#show-all-btn', function(e) {
    	e.preventDefault();
        let fetchAll = $('#fetchAll');
        fetchAll.val(true);
        fetchAll.closest('form').find('button[type="submit"]').trigger('click');
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

    $(document).on('click', '#lexemeCopyBtn', function() {
        let url = applicationUrl + 'lexemecopy/' +  $(this).data('lexeme-id');
        $.post(url).done(function(data) {
            let response = JSON.parse(data);
            if (response.status === 'ok') {
                openMessageDlg(response.message);
            } else {
                openAlertDlg(response.message);
            }
        }).fail(function(data) {
            openAlertDlg("Lekseemi dubleerimine ebaõnnestus");
            console.log(data);
        });
    });

    let detailButtons = $('#results').find('[name="detailsBtn"]');
    if (detailButtons.length === 1) {
        detailButtons.trigger('click');
    }

    let editDlg = $('#editDlg');
    editDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        alignAndFocus(e, editDlg)
    });

    let editLexemeDlg = $('#editLexemeLevelsDlg');
    editLexemeDlg.on('shown.bs.modal', function() {
        editLexemeDlg.find('[name=level1]').focus();
    });

    initSelectDlg($('#lexemeFrequencyDlg'));
    initSelectDlg($('#lexemePosDlg'));
    initSelectDlg($('#lexemeDerivDlg'));
    initSelectDlg($('#lexemeRegisterDlg'));
    initSelectDlg($('#wordGenderDlg'));
    initSelectDlg($('#wordTypeDlg'));
    initSelectDlg($('#wordAspectDlg'));
    initSelectDlg($('#meaningDomainDlg'));
    initSelectDlg($('#lexemeValueStateCodeDlg'));
    initSelectDlg($('#lexemeProcessStateCodeDlg'));
    initMultiValueAddDlg($('#wordClassifiersDlg'));
    initUsageMemberDlg($('#addNewUsageMemberDlg'));
    initNewWordDlg();
}

function openLexemeLevelDlg(elem) {
    let targetElement = $(elem);
    let editDlg = $('#editLexemeLevelsDlg');
    editDlg.find('[name="id"]').val(targetElement.data('id'));

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

function openAddNewWordRelationDlg(elem) {
    let addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    let selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());
    initRelationDialogLogic(addDlg, 'word-id');
}

function initAddWordRelationDlg(addDlg) {
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    let selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());
    initRelationDialogLogic(addDlg, 'word-id');
}

function openUsageAuthorDlg(elem) {
    let addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    let selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());
    initRelationDialogLogic(addDlg, 'source-id');
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
