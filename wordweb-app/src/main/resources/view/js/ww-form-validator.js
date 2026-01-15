class FormValidator {
	constructor(form) {
		this.form = form;
	}

	validate() {
		this.form.classList.add("was-validated");
		return this.form.checkValidity();
	}

	clearValidation() {
		this.form.classList.remove("was-validated");
	}

	reset() {
		this.form.reset();
		this.clearValidation();
	}
}
