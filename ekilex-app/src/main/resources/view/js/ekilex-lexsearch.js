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
}
