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

function initForm() {
  $('#term-dataset-select').selectpicker({width: '100%'});
}