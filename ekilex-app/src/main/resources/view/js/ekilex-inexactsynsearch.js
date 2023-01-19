function postInexactSynDataAndUpdateDlg(url, data) {
	const inexactSynDlg = $('#inexactSynDlg');
	$.ajax({
		url: url,
		data: data,
		method: 'POST'
	}).done(function(dlgData) {
		inexactSynDlg.html(dlgData);
		inexactSynDlg.modal('show');
		$wpm.bindObjects();
	}).fail(function(data) {
		inexactSynDlg.modal('hide');
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}

function initInexactSynSearchDlg(targetMeaningId, targetLang, wordRelationId) {
	const initInexactSearchUrl = applicationUrl + 'inexact_syn_init';
	const initData = {
		targetMeaningId: targetMeaningId,
		targetLang: targetLang,
		wordRelationId: wordRelationId
	};
	postInexactSynDataAndUpdateDlg(initInexactSearchUrl, initData);
}

$.fn.initInexactSynUpdateSearchBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function(e) {
			e.preventDefault();
			const targetMeaningId = btn.data('target-meaning-id');
			const targetLang = btn.data('target-lang');
			const wordRelationId = btn.data('word-relation-id');
			initInexactSynSearchDlg(targetMeaningId, targetLang, wordRelationId);
		});
	});
}

$.fn.submitInexactSynSearchMeaningsPlugin = function() {
	return this.each(function() {
		const form = $(this);
		form.on('submit', function(e) {
			e.preventDefault();
			const formData = form.serialize();
			const url = form.attr('action');
			postInexactSynDataAndUpdateDlg(url, formData);
		});
	});
}

$.fn.enableSubmitInexactSynMeaningBtnPlugin = function() {
	return this.each(function() {
		const radioBtn = $(this);
		radioBtn.on('click', function() {
			$(document).find('button[name="submitInexactMeaningBtn"]').removeAttr("disabled");
		});
	});
}

$.fn.submitInexactSynMeaningSelectPlugin = function() {
	return this.each(function() {
		const submitBtn = $(this);
		submitBtn.on('click', function(e) {
			e.preventDefault();
			const form = $('#inexactSynMeaningForm');
			const selectedMeaningIdInput = form.find('input[name=inexactSynMeaningId]:checked');
			if (selectedMeaningIdInput.val() === 'new') {
				selectedMeaningIdInput.val(null);
			}
			const formData = form.serialize();
			const url = form.attr('action');
			postInexactSynDataAndUpdateDlg(url, formData);
		});
	});
}

$.fn.enableSubmitWordBtnPlugin = function() {
	return this.each(function() {
		const radioBtn = $(this);
		radioBtn.on('click', function() {
			const selectedWordCandidatesDiv = radioBtn.closest('div[name="wordCandidatesDiv"]');
			const selectedWordCandidatesDivId = selectedWordCandidatesDiv.attr('id');
			const selectionInProgressIcon = selectedWordCandidatesDiv.find('i[name="wordSelectionInProgressIcon"]');
			const selectionCompleteIcon = selectedWordCandidatesDiv.find('i[name="wordSelectionCompleteIcon"]');
			const isTargetLangWordSelect = selectedWordCandidatesDivId === 'targetLangWordCandidatesDiv';
			const isTranslationLangWordSelect = selectedWordCandidatesDivId === 'translationLangWordCandidatesDiv';
			const otherWordCandidateSelectionExists = $('div[name="wordCandidatesDiv"]').length === 2;

			let isWordSelectionSubmitEnabled = true;
			if (otherWordCandidateSelectionExists) {
				if (isTargetLangWordSelect) {
					isWordSelectionSubmitEnabled = $('input[type=radio][name="translationLangWordId"]:checked').length === 1;
				} else if (isTranslationLangWordSelect) {
					isWordSelectionSubmitEnabled = $('input[type=radio][name="targetLangWordId"]:checked').length === 1;
				}
			}
			if (isWordSelectionSubmitEnabled) {
				$('button[name="submitInexactMeaningBtn"]').removeAttr("disabled");
			}

			selectionInProgressIcon.hide();
			selectionCompleteIcon.show();
		});
	});
}

$.fn.submitWordBtnPlugin = function() {
	return this.each(function() {
		const submitBtn = $(this);
		submitBtn.on('click', function(e) {
			e.preventDefault();
			const form = $('#inexactSynWordForm');
			const targetLangWordIdInput = form.find('input[name=targetLangWordId]:checked');
			const translationLangWordIdInput = form.find('input[name=translationLangWordId]:checked');
			const inexactSynDefInput = $('#inexactSynDef');
			const inexactSynDefInputExists = inexactSynDefInput.length === 1;
			if (inexactSynDefInputExists) {
				const inexactSynDefValue = inexactSynDefInput.val();
				if (!inexactSynDefValue) {
					inexactSynDefInput.addClass('is-invalid');
					return;
				} else {
					inexactSynDefInput.removeClass('is-invalid');
					form.find('input[name=inexactSynDef]').val(inexactSynDefValue);
				}
			}
			if (translationLangWordIdInput.val() === 'new') {
				translationLangWordIdInput.val(null);
			}
			if (targetLangWordIdInput.val() === 'new') {
				targetLangWordIdInput.val(null);
			}

			const formData = form.serialize();
			const url = form.attr('action');
			postInexactSynDataAndUpdateDlg(url, formData);
		});
	});
}

$.fn.relationSelectPlugin = function() {
	return this.each(function() {
		const select = $(this);
		select.on('change', function() {
			$(document).find('button[name="submitRelationSelectBtn"]').removeAttr("disabled");
		});
	});
}

$.fn.submitRelationSelectPlugin = function() {
	return this.each(function() {
		const submitBtn = $(this);
		submitBtn.on('click', function(e) {
			e.preventDefault();
			const form = $('#relationForm');
			const formData = form.serialize();
			const url = form.attr('action');

			const inexactSynDlg = $('#inexactSynDlg');
			$.ajax({
				url: url,
				data: formData,
				method: 'POST'
			}).done(function(response) {
				inexactSynDlg.modal('hide');
				refreshSynDetails();
				if (response.status === 'OK') {
					openMessageDlg(response.message);
				} else if (response.status === 'ERROR') {
					openAlertDlg(response.message);
				}
			}).fail(function(data) {
				inexactSynDlg.modal('hide');
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}
