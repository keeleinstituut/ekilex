// add on click handlers to details buttons in search result table
function initialize(urlPrefix) {
    var detailsDivs = $('#results').find('[name="details"]');
    detailsDivs.on('click', function (e) {
        var id = $(e.target).data('id');
        var detailsDiv = $('[name="' + id + '_details"]');
        if (detailsDiv.html() === '' || detailsDiv.data('reset')) {
            $.get(urlPrefix + 'termdetails/' + id).done(function (data) {
                detailsDiv.replaceWith(data);
            }).fail(function (data) {
                console.log(data);
                alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
            });
        } else {
            detailsDiv.toggle();
        }
    });
    if (detailsDivs.length === 1) {
        detailsDivs.trigger('click');
    }
    var editDlg = $('#editDlg');
    editDlg.on('shown.bs.modal', function() {
        editDlg.find('[name="modified_value"]').focus()
    })
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
            var detailsButton = targetElement.closest('[name="word_form"]').find('[name="details"]');
            var detailsDiv = $('[name="' + detailsButton.data('id') + '_details"]');
            detailsDiv.data('reset', 'true');
            detailsButton.trigger('click');
            editDlg.find('button.close').trigger('click');
        }).fail(function(data) {
            alert("Andmete muutmine ebaõnnestus.");
            console.log(data);
        });
    });

}
