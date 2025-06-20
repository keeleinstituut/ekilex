$.fn.registerClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const registerForm = obj.closest("#registerForm");
			const code = obj.data('code');
			const action = `${applicationUrl}register/${code}`;
			if (checkRequiredFields(registerForm)) {
				registerForm.attr("action", action);
				registerForm.trigger('submit');
			}
		});
	});
}

$.fn.agreementChkPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		const input = obj.find('input').get(0);
		obj.on('change', function() {
			if (input?.checked) {
				$('#registerBtn').prop("disabled", false);
			} else {
				$('#registerBtn').prop("disabled", true);
			}
		});
	});
}
