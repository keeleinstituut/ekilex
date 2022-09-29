$.fn.initAddSynRelationDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initAddSynRelationDlg(obj);
		})
	})
}