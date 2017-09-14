// add on click handlers to details buttons in search result table
function initialize() {
    $('#results').find('[name="details"]').on('click', function (e) {
        var id = $(e.target).data('id');
        $.get('/details/' + id).done(function (data) {
            $('[name="' + id + '_details"]').replaceWith(data);
        }).fail(function (data) {
            console.log(data);
            alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
        });
    })
}
