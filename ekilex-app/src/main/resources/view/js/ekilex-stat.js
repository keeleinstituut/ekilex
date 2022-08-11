// Currently non-functional as there's no backend
$.fn.statisticsFormPlugin = function() {
	return this.each(function() {
		const form = $(this);
		form.on('submit', function(e) {
			e.preventDefault();
			openWaitDlg();
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'GET',
			}).done(function(statFragment) {
				$('#wwSearchStat').replaceWith(statFragment);
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	});
}