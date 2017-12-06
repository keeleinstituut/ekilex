// add on click handlers to details buttons in search result table
function initialize(urlPrefix) {
    var detailsDivs = $('#results').find('[name="details"]');
    detailsDivs.on('click', function (e) {
        var id = $(e.target).data('id');
        var detailsDiv = $('[name="' + id + '_details"]');
        if (detailsDiv.html() === '') {
            $.get(urlPrefix + 'details/' + id).done(function (data) {
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
}

function selectDatasets(selection) {
    $('#dataset_select').find(':checkbox').prop('checked', selection)
}
