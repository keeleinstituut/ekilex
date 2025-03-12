$.fn.wwStatSearchPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			openWaitDlg();
			const form = btn.closest('form');
			form.trigger('submit');
		});
	});
}
