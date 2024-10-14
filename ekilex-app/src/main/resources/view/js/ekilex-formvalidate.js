class FormValidate {
  constructor(element) {
		this.form = element;
		this.formItems = this.form.find('input, select, textarea').filter('[required]');
  }

	bindEvents() {
		this.formItems.prop('required', false);
		this.form.on('submit', (e) => {
			this.validate(e);
		});
	}

	validate(e) {
		const visibleFormItems = this.formItems.filter(':visible');
		const filledFormItems = visibleFormItems.filter(function(){
			const item = $(this);
			return item.val() !== "" && item.val() !== null;
		});
		const invalidFormItems = visibleFormItems.filter(function(){
			const item = $(this);
			return item.val() === "" || item.val() === null;
		});

		this.formItems.removeClass('invalid-form-item');
		invalidFormItems.addClass('invalid-form-item');

		if (visibleFormItems.length !== filledFormItems.length) {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	}

  initialize() {
		this.bindEvents();
  }
}

$.fn.formValidate = function() {
  $(this).each(function(){
    const instance = new FormValidate($(this));
    instance.initialize();
  });
}