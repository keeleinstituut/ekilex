// add on click handlers to details buttons in search result table
function initialise() {
    $(document).on("click", "[id^='word_details_']", function() {
        var id = $(this).data('id');
        $.get(applicationUrl + 'termdetails/' + id).done(function (data) {
            $('#details_div').replaceWith(data);
        }).fail(function (data) {
            console.log(data);
            alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
        });
    });
    var detailsButtons = $('#results').find('[name="details"]');
    if (detailsButtons.length === 1) {
        detailsButtons.trigger('click');
    }
    var editDlg = $('#editDlg');
    editDlg.on('shown.bs.modal', function() {
        editDlg.find('[name="modified_value"]').focus()
    });
}

function selectDatasets(selection) {
    $('#dataset_select').find(':checkbox').prop('checked', selection)
}

function initEditDlg(elem) {
    var targetName = $(elem).data('target-elem');
    var targetElement = $('[name="' + targetName + '"]');
    var editDlg = $('#editDlg');
    var modifyFld = editDlg.find('[name="modified_value"]');
    modifyFld.val(targetElement.data('value') != undefined ? targetElement.data('value') : targetElement.text());
    editDlg.find('[name="id"]').val(targetElement.data('id'));
    editDlg.find('[name="op_type"]').val(targetElement.data('op-type'));

    editDlg.find('button[type="submit"]').off().on('click', function(e) {
        e.preventDefault();
        var editForm = editDlg.find('form');
        var url = editForm.attr('action') + '?' + editForm.serialize();
        $.post(url).done(function (data) {
            var id = $('#details_div').data('id');
            var detailsButton = $('[name="details"][data-id="' + id + '"]');
            detailsButton.trigger('click');
            editDlg.find('button.close').trigger('click');
        }).fail(function(data) {
            alert("Andmete muutmine ebaõnnestus.");
            console.log(data);
        });
    });

}
