// function initializeRegister() {
// 	$(document).on("click", "#registerBtn", function() {
// 		let registerForm = $("#registerForm");
// 		let code = $(this).data('code');
// 		let action = applicationUrl + "register/" + code;
// 		if (checkRequiredFields(registerForm)) {
// 			registerForm.attr("action", action);
// 			registerForm.submit();
// 		}
// 	});

// 	let agreementChk = $(document).find('input[name="agreement"]');
// 	agreementChk.change(function() {
// 		if (this.checked) {
// 			$('#registerBtn').prop("disabled", false);
// 		} else {
// 			$('#registerBtn').prop("disabled", true);
// 		}
// 	});
// };

$.fn.registerClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const registerForm = obj.closest("#registerForm");
			const code = obj.data('code');
			const action = applicationUrl + "register/" + code;
			if (checkRequiredFields(registerForm)) {
				registerForm.attr("action", action);
				registerForm.submit();
			}
		});
	});
}

$.fn.agreementChkPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('change', function() {
			if (this.checked) {
				$('#registerBtn').prop("disabled", false);
			} else {
				$('#registerBtn').prop("disabled", true);
			}
		});
	});
}