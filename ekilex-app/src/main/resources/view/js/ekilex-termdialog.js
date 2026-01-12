$.fn.meaningDataSelectPlugin = function() {
	return this.each(function() {
		const select = $(this);
		select.on('change', function() {
			const opCode = select.val();
			const localForm = select.closest("form");
			localForm.find(".value-group").hide();
			const meaningId = localForm.find("[name=id2]").val();
			const dlgElemId = `#${opCode}_${meaningId}`;
			if (opCode.endsWith('Dlg')) {
				$(dlgElemId).modal("show");
				$("#addMeaningDataDlg_" + meaningId).modal("hide");
			} else {
				$(dlgElemId).show();
			}
		})
	})
}
