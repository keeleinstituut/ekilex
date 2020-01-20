function initialise() {
	$(document).on("click", "#registerBtn", function() {
		let registerForm = $("#registerForm");
		let code = $(this).data('code');
		let action = applicationUrl + "register/" + code;
		if (checkRequiredFields(registerForm)) {
			registerForm.attr("action", action);
			registerForm.submit();
		}
	});
}