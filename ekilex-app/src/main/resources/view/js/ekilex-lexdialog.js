//addLexemeDataDlg_ select
$(function() {
	$(document).on("change", "select.lex-data-select[name='opCode']", function() {
		var opCode = $(this).val();
		var localForm = $(this).closest("form");
		localForm.find(".value-group").hide();
		var lexemeId = localForm.find("[name=id]").val();
		var dlgElemId = "#" + opCode + '_' + lexemeId;
		if (opCode.endsWith('Dlg')) {
			$(dlgElemId).modal("show");
			$("#addLexemeDataDlg_" + lexemeId).modal("hide");
		} else {
			$(dlgElemId).show();
		}
	});

	$(document).on("show.bs.modal", "[id^=addGovernmentDlg_]", function(e) {
		initGenericTextAddDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editGovernmentDlg_]", function(e) {
		initGenericTextEditDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addUsageAuthorDlg_]", function() {
		initUsageAuthorDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addUsageMemberDlg_]", function() {
		initEkiEditorDlg($(this));
		initUsageMemberDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editUsageTranslationDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editUsageDefinitionDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addGroupWordRelationDlg_]", function() {
		initMultiselectRelationDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addPrimaryWordRelationDlg_]", function() {
		initMultiselectRelationDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordAspectDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordAspectDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordDisplayMorphDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordLangDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeLevelsDlg_]", function() {
		initLexemeLevelsDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeComplexityDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemePosDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeDerivDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeRegisterDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addMeaningDomainDlg_]", function() {
		initAddMultiDataDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLexemeGrammarDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordVocalFormDlg_]", function() {
		initGenericTextEditDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordVocalFormDlg_]", function() {
		initGenericTextEditDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordGenderDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addWordTypeDlg_]", function() {
		initSelectDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addOdWordRecommendationDlg_]", function(e) {
		initEkiEditorDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editOdWordRecommendationDlg_]", function(e) {
		initEkiEditorDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addOdLexemeRecommendationDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editOdLexemeRecommendationDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editOdUsageDefinitionDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editOdUsageAlternativeDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editLexemeWeightDlg_]", function() {
		initGenericTextEditDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=editMeaningWordAndLexemeWeightDlg_]", function() {
		initEditMeaningWordAndLexemeWeightDlg($(this));
	});

	$(document).on("show.bs.modal", "[id^=addLearnerCommentDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editLearnerCommentDlg_]", function(e) {
		initEkiEditorDlg($(this));
		alignAndFocus(e, $(this));
	});

	$(document).on("show.bs.modal", "[id^=editWordManualEventOnDlg_]", function() {
		initGenericTextEditDlg($(this));
	});
});