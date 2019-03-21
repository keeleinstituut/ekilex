//component based interaction logic

$(document).on("change", "select[name='dataset']", function() {
	var datasetCode = $(this).val();
	if (datasetCode) {
		var getLanguageSelectUrl = applicationUrl + 'comp/langselect/' + datasetCode;
		$.get(getLanguageSelectUrl).done(function(data) {
			var permLanguageSelectArea = $('#permLanguageSelect');
			permLanguageSelectArea.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			alert('Viga!');
		});
	} else {
		$("#permLanguageSelect").empty();
	}
});
