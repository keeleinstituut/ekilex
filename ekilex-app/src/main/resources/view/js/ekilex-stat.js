function initializeStat() {

	$('form[name="getStatForm"]').submit(function(e) {
		e.preventDefault();
		openWaitDlg();
		let form = $(this);
		$.ajax({
			url: form.attr('action'),
			data: form.serialize(),
			method: 'GET',
		}).done(function(statFragment) {
			$('#wwSearchStat').replaceWith(statFragment);
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		}).always(function() {
			closeWaitDlg();
		});
	});
}