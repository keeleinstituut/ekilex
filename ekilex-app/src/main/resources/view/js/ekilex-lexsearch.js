function initialise() {
    $(document).on("click", ":button[name='detailsBtn']", function() {
        var id = $(this).data('id');
        var isRestoreScrollPos = this.hasAttribute('data-refresh');
        $.get(applicationUrl + 'worddetails/' + id).done(function(data) {
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
        var orderingData = changeItemOrdering($(this), -1);
        postJson(applicationUrl + 'modify_ordering', orderingData);
    });

    $(document).on('click', '.order-down', function() {
        var orderingData = changeItemOrdering($(this), 1);
        postJson(applicationUrl + 'modify_ordering', orderingData);
    });

    $(document).on('click', '#show-all-btn', function(e) {
    	e.preventDefault();
        $('#fetchAll').val(true);
        $('#fetchAll').closest('form').find('button[type="submit"]').trigger('click');
    });

	$(document).on('show.bs.modal', '#wordLifecycleLogDlg', function(e) {
		var dlg = $(this);
		var link = $(e.relatedTarget);
		var url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});

    var detailButtons = $('#results').find('[name="detailsBtn"]');
    if (detailButtons.length === 1) {
        detailButtons.trigger('click');
    }

    var editDlg = $('#editDlg');
    editDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        alignAndFocus(e, editDlg)
    });

    var editLexemeDlg = $('#editLexemeLevelsDlg');
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
    initMultiValueAddDlg($('#lexemeClassifiersDlg'), true);
    initMultiValueAddDlg($('#wordClassifiersDlg'), true);
    initMultiValueAddDlg($('#addNewUsageMemberDlg'), true);
    initNewWordDlg();

}

function openLexemeLevelDlg(elem) {
    var targetElement = $(elem);
    var editDlg = $('#editLexemeLevelsDlg');
    editDlg.find('[name="id"]').val(targetElement.data('id'));

    editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        e.preventDefault();
        var editForm = editDlg.find('form');
        editDlg.find('[name="action"]').val($(this).data('action'));
        var url = editForm.attr('action') + '?' + editForm.serialize();
        $.post(url).done(function(data) {
            var id = $('#details_div').data('id');
            var detailsButton = $('[name="detailsBtn"][data-id="' + id + '"]');
            detailsButton.trigger('click');
            editDlg.find('button.close').trigger('click');
        }).fail(function(data) {
            alert("Andmete muutmine ebaõnnestus.");
            console.log(data);
        });
    });
}

function openClassifiersDlg(elem) {
    var theDlg = $($(elem).data('target'));
    theDlg.find('[name=id]').val($(elem).data('lexeme-id'));
    theDlg.find('[name=id2]').val($(elem).data('meaning-id'));
    theDlg.find('[name=id3]').val($(elem).data('word-id'));
}

function openAddNewWordRelationDlg(elem) {
    var addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    var selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());

    addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        e.preventDefault();
        var button = $(this);
        var content = button.html();
        button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
        var theForm = $(this).closest('form');
        var url = theForm.attr('action') + '?' + theForm.serialize();
        $.get(url).done(function(data) {
            addDlg.find('[data-name=dialogContent]').replaceWith(data);
            addDlg.find('button[data-word-id]').off('click').on('click', function(e) {
                e.preventDefault();
                var button = $(e.target);
                addDlg.find('[name=id2]').val(button.data('word-id'));
                var theForm = button.closest('form');
                submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
                    addDlg.modal('hide');
                });
            });
        }).fail(function(data) {
            console.log(data);
            alert(failMessage);
        }).always(function () {
            button.html(content);
        });
    });

    addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        addDlg.find('.form-control').first().focus();
    });
}

function openAddNewLexemeRelationDlg(elem) {
    var addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=lexemeId]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    var selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());

    addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        e.preventDefault();
        var button = $(this);
        var content = button.html();
        button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
        var theForm = $(this).closest('form');
        var url = theForm.attr('action') + '?' + theForm.serialize();
        $.get(url).done(function(data) {
            addDlg.find('[data-name=dialogContent]').replaceWith(data);
            addDlg.find('button[data-lexeme-id]').off('click').on('click', function(e) {
                e.preventDefault();
                var button = $(e.target);
                addDlg.find('[name=id2]').val(button.data('lexeme-id'));
                var theForm = button.closest('form');
                submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
                    addDlg.modal('hide');
                });
            });
        }).fail(function(data) {
            console.log(data);
            alert(failMessage);
        }).always(function () {
            button.html(content);
        });
    });

    addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        addDlg.find('.form-control').first().focus();
    });
}

function openAddNewMeaningRelationDlg(elem) {
    var addDlg = $($(elem).data('target'));
    addDlg.find('[name=id]').val($(elem).data('id'));
    addDlg.find('[name=meaningId]').val($(elem).data('id'));
    addDlg.find('.form-control').val(null);
    addDlg.find('[data-name=dialogContent]').html(null);
    var selectElem = addDlg.find('select');
    selectElem.val(selectElem.find('option').first().val());

    addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        e.preventDefault();
        var button = $(this);
        var content = button.html();
        button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
        var theForm = $(this).closest('form');
        var url = theForm.attr('action') + '?' + theForm.serialize();
        $.get(url).done(function(data) {
            addDlg.find('[data-name=dialogContent]').replaceWith(data);
            addDlg.find('button[data-meaning-id]').off('click').on('click', function(e) {
                e.preventDefault();
                var button = $(e.target);
                addDlg.find('[name=id2]').val(button.data('meaning-id'));
                var theForm = button.closest('form');
                submitForm(theForm, 'Andmete muutmine ebaõnnestus.').always(function() {
                    addDlg.modal('hide');
                });
            });
        }).fail(function(data) {
            console.log(data);
            alert(failMessage);
        }).always(function () {
            button.html(content);
        });
    });

    addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        addDlg.find('.form-control').first().focus();
    });
}
