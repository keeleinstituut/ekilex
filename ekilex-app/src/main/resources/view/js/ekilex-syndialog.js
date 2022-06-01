$.fn.initAddSynRelationDlgPlugin = function() {
	this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initAddSynRelationDlg(obj);
		})
	})
}

// $(document).on("show.bs.modal", "[id^=addSynRelationDlg_]", function() {
// 	initAddSynRelationDlg($(this));
// });

// This element seems to be missing
// $(document).on("show.bs.modal", "[id^=editLexemeSynProcessStateDlg_]", function() {
// 	initSelectDlg($(this));
// });

// $(document).on("show.bs.modal", "[id^=addWordNoteDlg_]", function(e) {
// 	initEkiEditorDlg($(this));
// 	alignAndFocus(e, $(this));
// });

// $(document).on("show.bs.modal", "[id^=editWordNoteDlg_]", function(e) {
// 	initEkiEditorDlg($(this));
// 	alignAndFocus(e, $(this));
// });