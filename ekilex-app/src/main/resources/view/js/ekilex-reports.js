$.fn.reportTypeSelectPlugin = function() {
  const typeSelect = $(this);
  typeSelect.on('change', function() {
    const reportType = typeSelect.val();
    window.location.href = reportType
        ? applicationUrl + 'reports/' + reportType
        : applicationUrl + 'reports';
  });
}

$.fn.generateReportPlugin = function() {
  const form = $(this);
  form.on('submit', function(e) {
    if (!checkRequiredFields(form)) {
      e.preventDefault();
      e.stopImmediatePropagation();
    }
  });
}

$.fn.deleteReportPlugin = function() {
  const btn = $(this);
  btn.confirmation({
    btnOkLabel: messages['common.yes'],
    btnCancelLabel: messages['common.no'],
    title: messages['common.confirm.delete'],
    placement: 'left',
    onConfirm: function() {
      const reportId = btn.data('report-id');
      deleteReport(reportId);
    }
  });
}

function initForm() {
  $('#term-dataset-select').selectpicker({width: '100%'});
}

function deleteReport(reportId) {
  $.get(applicationUrl + 'reports/delete/' + reportId)
  .done(function(data) {
    $('#report-list').replaceWith(data);
    $wpm.bindObjects();
  }).fail(function(data) {
    console.log(data);
    openAlertDlg(messages['common.error']);
  });
}