function initialise() {
	$(document).on("click", ":button[name='synoDetailsBtn']", function() {
		let id = $(this).data('id');
		let isRestoreDisplayState = this.hasAttribute('data-refresh');
		let openLexemes = [];
		$('.d-none[data-lexeme-title]').each(function(index, item) {
			openLexemes.push($(item).data('toggle-name'));
		});
		$("[id^='syno_select_point_']").hide();
		$("[id^='syno_select_wait_']").hide();
		$("#syno_select_wait_" + id).show();
		$.get(applicationUrl + 'worddetails/' + id).done(function(data) {
			let detailsDiv = $('#syno_details_div');
			let scrollPos = detailsDiv.scrollTop();

			console.log(data);

			detailsDiv.replaceWith(data);

			if (isRestoreDisplayState) {
				detailsDiv.scrollTop(scrollPos);
				openLexemes.forEach(function(lexemeName) {
					$('[data-toggle-name=' + lexemeName + ']').find('.btn-toggle').trigger('click');
				})
			}
			$("#syno_select_wait_" + id).hide();
			$("#syno_select_point_" + id).show();
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

}
