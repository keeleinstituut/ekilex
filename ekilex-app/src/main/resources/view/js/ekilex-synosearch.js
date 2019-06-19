function initialise() {
	$(document).on("click", ":button[name='synoDetailsBtn']", function() {
		let id = $(this).data('id');

		$("[id^='syno_select_point_']").hide();
		$("[id^='syno_select_wait_']").hide();
		$("#syno_select_wait_" + id).show();
		$.get(applicationUrl + 'syno_worddetails/' + id).done(function(data) {
			let detailsDiv = $('#syno_details_div');
			detailsDiv.replaceWith(data);
			$("#syno_select_wait_" + id).hide();
			$("#syno_select_point_" + id).show();
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

}
