$.fn.saveNewsArticlePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on("click", function(e) {
			e.preventDefault();
			const form = obj.closest("form");
			if (!checkRequiredFields(form)) {
				return;
			}
			form.submit();
		});
	});
}