$.fn.addNewsSectionPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on("click", function(e) {
			e.preventDefault();
			const form = obj.closest("form");
			const areaId = form.find('input[name="newsArticleEditAreaId"]').val();
			const contentArea = $("#" + areaId);
			const formOpName = obj.attr("name");
			form.find('input[name="formOpName"]').val(formOpName);
			$.ajax({
				url: form.attr("action"),
				data: form.serialize(),
				method: "POST",
			}).done(function(data) {
				contentArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}

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