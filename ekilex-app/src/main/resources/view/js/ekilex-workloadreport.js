$.fn.workloadReportDatasetSelectPlugin = function() {
	return this.each(function() {
		const datasetSelect = $(this);
		datasetSelect.on('changed.bs.select', function() {
			const datasetUsersDiv = $('#dataset-users-div');
			const datasetCodes = datasetSelect.val();
			if (datasetCodes.length === 0) {
				datasetUsersDiv.html("");
			} else {
				const selectedUsers = $('#users-select').val();
				const getDatasetUsersUrl = `${applicationUrl}workloadreport/dataset_users/${datasetCodes}`;
				$.get(getDatasetUsersUrl).done(function(datasetUsersSelectHtml) {
					datasetUsersDiv.html(datasetUsersSelectHtml);
					initDatasetUsersSelect();
					$.each(selectedUsers, function(index, selectedUser) {
						$("#users-select option[value='" + selectedUser + "']").prop('selected', true);
					});
					$('#users-select').selectpicker('refresh');
					$wpm.bindObjects();
				}).fail(function(data) {
					openAlertDlg(messages["common.error"]);
					console.log(data);
				});
			}
		});
	});
}

function initializeWorkloadReport() {
	initDatasetSelect();
	initDatasetUsersSelect();
}

function initDatasetSelect() {
	$('.dataset-select').selectpicker({width: '100%'});
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