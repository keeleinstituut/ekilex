// add on click handlers to details buttons in search result table
function initialize(urlPrefix) {
    $('#results').find('[name="details"]').on('click', function (e) {
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
    })
}
