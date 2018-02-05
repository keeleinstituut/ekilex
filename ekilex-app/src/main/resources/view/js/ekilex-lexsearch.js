function initialise() {
    $(document).on("click", ":button[name='detailsBtn']", function() {
        var id = $(this).data('id');
        $.get(applicationUrl + 'lexdetails/' + id).done(function(data) {
            $('#details_div').replaceWith(data);
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

    var detailButtons = $('#results').find('[name="detailsBtn"]');
    if (detailButtons.length === 1) {
        detailButtons.trigger('click');
    }

    var editDlg = $('#editDlg');
    editDlg.on('shown.bs.modal', function() {
        editDlg.find('[name="modified_value"]').focus()
    });

    var editLexemeDlg = $('#editLexemeLevelsDlg');
    editLexemeDlg.on('shown.bs.modal', function() {
        editLexemeDlg.find('[name="level1"]').focus();
    });
}

function openLexemeLevelDlg(elem) {
    var targetElement = $(elem);
    var editDlg = $('#editLexemeLevelsDlg');
    editDlg.find('[name="id"]').val(targetElement.data('id'));
    editDlg.find('[name="level1"]').val(targetElement.data('level1'));
    editDlg.find('[name="level2"]').val(targetElement.data('level2'));
    editDlg.find('[name="level3"]').val(targetElement.data('level3'));
    editDlg.find('[name="level1"]').focus();

    editDlg.find('button[type="submit"]').off().on('click', function(e) {
        e.preventDefault();
        var editForm = editDlg.find('form');
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
