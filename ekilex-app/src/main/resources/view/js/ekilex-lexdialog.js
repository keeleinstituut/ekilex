$(function() {
	$.fn.initUsageAuthorDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initUsageAuthorDlg(obj);
			})
		})
	}

	$.fn.addUsageMemberDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function(e) {
				initEkiEditorDlg(obj);
				initUsageMemberDlg(obj);
			})
		})
	}

	$.fn.initLexemeLevelsDlgPlugin = function() {
		return this.each(function() {
			const obj = $(this);
			obj.on('show.bs.modal', function() {
				initLexemeLevelsDlg(obj);
			})
		})
	}

});