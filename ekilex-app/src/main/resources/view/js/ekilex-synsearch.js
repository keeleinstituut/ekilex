function initialise() {
	$(document).on("click", ":button[name='synDetailsBtn']", function() {
		let id = $(this).data('id');

		$("[id^='syn_select_point_']").hide();
		$("[id^='syn_select_wait_']").hide();
		$("#syn_select_wait_" + id).show();
		$.get(applicationUrl + 'syn_worddetails/' + id).done(function(data) {
			let detailsDiv = $('#syn_details_div');
			detailsDiv.replaceWith(data);
			$("#syn_select_wait_" + id).hide();
			$("#syn_select_point_" + id).show();
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

}
