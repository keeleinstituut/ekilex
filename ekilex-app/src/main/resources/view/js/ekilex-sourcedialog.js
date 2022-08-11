$.fn.initEditSourcePropertyDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function(e) {
			initEditSourcePropertyDlg(obj);
			alignAndFocus(e, obj);
		});
	});
}

$.fn.initAddSourcePropertyDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function(e) {
			initAddSourcePropertyDlg(obj);
			alignAndFocus(e, obj);
		});
	});
}

$.fn.initEditSourceTypeSelectDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initEditSourceTypeSelectDlg(obj);
		});
	});
}