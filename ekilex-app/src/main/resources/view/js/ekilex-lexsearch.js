function initialise() {
	var detailButtons = $('#results').find('[name="detailsBtn"]');
	if (detailButtons.length === 1) {
		detailButtons.trigger('click');
	}
}

$(document).on("click", ":button[name='detailsBtn']", function() {
	var id = $(this).data('id');
	$.get(applicationUrl + 'lexdetails/' + id).done(function(data) {
		$('#details_div').replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
	});
});
