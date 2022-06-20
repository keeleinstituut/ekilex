// function initializeStat() {

// 	$('form[name="getStatForm"]').submit(function(e) {
// 		e.preventDefault();
// 		openWaitDlg();
// 		let form = $(this);
// 		$.ajax({
// 			url: form.attr('action'),
// 			data: form.serialize(),
// 			method: 'GET',
// 		}).done(function(statFragment) {
// 			$('#wwSearchStat').replaceWith(statFragment);
// 		}).fail(function(data) {
// 			console.log(data);
// 			openAlertDlg('Viga!');
// 		}).always(function() {
// 			closeWaitDlg();
// 		});
// 	});
// }

// Currently fails with a java error "URI is not absolute" and dialog will not close.
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