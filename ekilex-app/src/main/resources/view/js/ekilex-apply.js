$.fn.applyPlugin = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const form = main.closest('form');
		if (checkRequiredFields(form)) {
			form.trigger('submit');
		}
	});
}