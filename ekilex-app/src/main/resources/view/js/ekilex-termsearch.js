// add on click handlers to details buttons in search result table
function initialise() {
	$(document).on("click", ":button[name='detailsBtn']", function() {
		var id = $(this).data('id');
		$.get(applicationUrl + 'termdetails/' + id).done(function(data) {
			$('#details_div').replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});
    $(document).on('click', '#show-all-btn', function() {
        $('#fetchAll').val(true);
        $('#fetchAll').closest('form').find('button[type="submit"]').trigger('click');
    });
	var detailsButtons = $('#results').find('[name="detailsBtn"]');
	if (detailsButtons.length === 1) {
		detailsButtons.trigger('click');
	}
	var editDlg = $('#editDlg');
	editDlg.on('shown.bs.modal', function() {
		editDlg.find('[name="modified_value"]').focus()
	});
}
