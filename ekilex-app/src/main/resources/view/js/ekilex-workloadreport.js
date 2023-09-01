$.fn.workloadReportDatasetSelectPlugin = function() {
	return this.each(function() {
		const datasetSelect = $(this);
		datasetSelect.on('change', function() {
			const datasetCode = datasetSelect.val();
			const getDatasetUsersUrl = `${applicationUrl}workloadreport/dataset_users/${datasetCode}`;
			$.get(getDatasetUsersUrl).done(function(datasetUsersSelectHtml) {
				const datasetUsersDiv = $('#dataset-users-div');
				datasetUsersDiv.html(datasetUsersSelectHtml);
				$('.users-select').selectpicker({width: '100%'});
				initDatasetUsersSelect();
				$wpm.bindObjects();
			}).fail(function(data) {
				openAlertDlg(messages["common.error"]);
				console.log(data);
			});
		});
	});
}

function initDatasetUsersSelect() {
	$('.users-select').selectpicker({width: '100%'});
}

$.fn.workloadReportSearchPlugin = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const form = main.closest('form');
		if (checkRequiredFields(form)) {
			openWaitDlg();
			form.submit();
		}
	});
}