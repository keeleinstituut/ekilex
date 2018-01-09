// add on click handlers to details buttons in search result table
function initialize(urlPrefix) {
    var detailsButtons = $('#results').find('[name="details"]');
    detailsButtons.on('click', function (e) {
        var id = $(this).data('id');
        $.get(urlPrefix + 'lexdetails/' + id).done(function (data) {
            $('#details_div').replaceWith(data);
        }).fail(function (data) {
            console.log(data);
            alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
        });
    });
    if (detailsButtons.length === 1) {
        detailsButtons.trigger('click');
    }
}

function selectDatasets(selection) {
    $('#dataset_select').find(':checkbox').prop('checked', selection)
}
