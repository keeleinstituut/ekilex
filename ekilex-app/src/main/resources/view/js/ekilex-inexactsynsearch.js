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

$.fn.submitInexactSynSearchMeaningsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('submit', function(e) {
			e.preventDefault();
			const form = $('#inexactSynSearchMeaningsForm');
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

$.fn.submitInexactSynSearchMeaningsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('submit', function(e) {
			e.preventDefault();
			const form = $('#inexactSynSearchMeaningsForm');
			const formData = form.serialize();
			const url = form.attr('action');
			postInexactSynDataAndUpdateDlg(url, formData);
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

			// TODO post data, close modal, update page, display returned message
			const inexactSynDlg = $('#inexactSynDlg');
			$.ajax({
				url: url,
				data: formData,
				method: 'POST'
			}).done(function(message) {
				inexactSynDlg.modal('hide');
				refreshSynDetails();
				openMessageDlg(message);
			}).fail(function(data) {
				inexactSynDlg.modal('hide');
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}
