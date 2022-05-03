var viewType = '';

$.fn.identificator = function() {
	var main = $(this);
	var parent = main.parents('.card-body:first');
	var labels = '';
	parent.find('[data-id="identificators"]').find('span').each(function() {
		if (labels !== '') {
			labels += ', ';
		}
		labels += $(this).text();
	});
	main.attr('title', labels);
	main.tooltip();
}

$.fn.status = function() {
	var main = $(this);
	var parent = main.parents('.card-body:first');
	var source = parent.find('[data-id="status"]');
	var label = source.children('span').text();
	var target = source.attr('data-target');
	if (label === 'avalik') {
		main.find('span').addClass('fa-unlock');
	} else {
		main.find('span').addClass('fa-lock');
	}
	main.on('click', function(e) {
		e.preventDefault();
		$(target).modal('show', main);
	});
	main.attr('title', label).tooltip();
}

$.fn.valueStatus = function() {
	var main = $(this);
	var parent = main.parents('.card-body:first');
	var source = parent.find('[data-id="valuestatus"]');
	main.html(source.html());
	main.find('.col-w13rem').remove();
	main.find('[data-toggle="delete-confirm"]').deleteConfirm();
}

// TODO move to ekilex-common.js?
function initializeSearch(type) {
	viewType = type;
	$(window).on('update:wordId', () => {
		const idList = [];
		$('#resultColumn').find('[data-rel="details-area"]').each((index, element) => {
			idList.push($(element).attr('data-id'));
		});
		const idString = idList.join(',');
		if (idList.length === 0) {
			QueryParams.delete('id');
		} else {
			QueryParams.set('id', idString);
		}
	});

	$(document).on('change', '[name="resultLang"]', function() {
		$(this).closest('form').submit();
	});

	$(document).on('change', '[name="resultMode"]', function() {
		$(this).closest('form').submit();
	});

	$(document).on("click", ":button[name='details-btn'], #refresh-details", function() {
		const wordId = $(this).data('id');
		const behaviour = $(this).data('behaviour') || false;
		const lastWordId = behaviour === 'replace' ? $(this).parents('#details-area:first').attr('data-id') : false;
		loadDetails(wordId, behaviour, lastWordId);
	});

	$(document).on("click", "#refresh-open", function(e) {
		e.preventDefault();
		const lexemeId = $(this).data('id');
		const lexemeLevels = $(this).data('lex-levels');
		loadLexemeDetails(lexemeId, lexemeLevels, 'full');
	});

	$(document).on("click", ":button[name='lexeme-details-btn']", function() {
		let lexemeId = $(this).data('id');
		let lexemeLevels = $(this).data('lex-levels');
		let composition = $(this).data('composition');
		loadLexemeDetails(lexemeId, lexemeLevels, composition);
	});

	$(document).on('click', '.order-up', function() {
		let orderingBtn = $(this);
		let orderingData = changeItemOrdering(orderingBtn, -1);
		postJson(applicationUrl + 'update_ordering', orderingData);
		if (orderingBtn.hasClass('do-refresh')) {
			refreshDetailsSearch(orderingBtn.parents('[data-rel="details-area"]').attr('data-id'));
		}
	});

	$(document).on('click', '.order-down', function() {
		let orderingBtn = $(this);
		let orderingData = changeItemOrdering(orderingBtn, 1);
		postJson(applicationUrl + 'update_ordering', orderingData);
		if (orderingBtn.hasClass('do-refresh')) {
			refreshDetailsSearch(orderingBtn.parents('[data-rel="details-area"]').attr('data-id'));
		}
	});

	$(document).on('click', '[name="lang-collapse-btn"]', function() {
		const btn = $(this);
		let lang = btn.attr("data-lang");
		let itemData = {
			opCode: "user_lang_selection",
			code: lang
		};
		let successCallbackName = $(this).attr("data-callback");
		let successCallbackFunc = () => eval(successCallbackName);
		postJson(applicationUrl + 'update_item', itemData).done(function() {

			if (viewType === 'term') {
				refreshDetailsSearch(btn.parents('[data-rel="details-area"]').attr('data-id'));
			} else {
				successCallbackFunc();
			}
		});
	});

	$(document).on('show.bs.modal', '#wordActivityLogDlg', function(e) {
		let dlg = $(this);
		let link = $(e.relatedTarget);
		let url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});

	$(document).on('show.bs.modal', '#meaningActivityLogDlg', function(e) {
		var dlg = $(this);
		var link = $(e.relatedTarget);
		var url = link.attr('href');
		dlg.find('.close').focus();
		dlg.find('.modal-body').html(null);
		$.get(url).done(function(data) {
			dlg.find('.modal-body').html(data);
		});
	});

	$(document).on('click', '[id^=duplicateLexemeBtn_]', function() {
		const obj = $(this);
		var lexemeId = obj.data('lexeme-id');
		let url = applicationUrl + 'lexduplicate/' + lexemeId;
		$.post(url).done(function(data) {
			let response = JSON.parse(data);
			if (response.status === 'ok') {
				openMessageDlg(response.message);
				refreshDetailsSearch(obj.parents('[data-rel="details-area"]').attr('data-id'));
			} else {
				openAlertDlg(response.message);
			}
		}).fail(function(data) {
			openAlertDlg("Tähenduse dubleerimine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[id^=duplicateEmptyLexemeBtn_]', function() {
		const obj = $(this);
		var lexemeId = obj.data('lexeme-id');
		var url = applicationUrl + 'emptylexduplicate/' + lexemeId;
		$.post(url).done(function(data) {
			var response = JSON.parse(data);
			openMessageDlg(response.message);
			refreshDetailsSearch(obj.parents('[data-rel="details-area"]').attr('data-id'));
		}).fail(function(data) {
			openAlertDlg("Tähenduse lisamine ebaõnnestus");
			console.log(data);
		});
	});

	$(document).on('click', '[id^=duplicateMeaningWordAndLexemeBtn_]', function() {
		let lexemeId = $(this).data('lexeme-id');
		let successCallbackName = $(this).data("callback");
		let successCallbackFunc = () => eval(successCallbackName);
		let url = applicationUrl + 'meaningwordandlexduplicate/' + lexemeId;
		$.post(url).done(function() {
			successCallbackFunc();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg("Vaste dubleerimine ebaõnnestus");
		});
	});

	$(document).on('click', '[name="pagingBtn"]', function() {
		openWaitDlg();

		let url = applicationUrl + "lex_paging";
		if (viewType === 'term') {
			url = applicationUrl + "term_paging";
		} else if (viewType === 'lim_term') {
			url = applicationUrl + "lim_term_paging";
		}
		let button = $(this);
		let direction = button.data("direction");
		let form = button.closest('form');
		form.find('input[name="direction"]').val(direction);

		$.ajax({
			url: url,
			data: form.serialize(),
			method: 'POST',
		}).done(function(data) {
			$('#results_div').html(data);
			$('#results_div').parent().scrollTop(0);
			$wpm.bindObjects();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Lehekülje muutmine ebaõnnestus');
		}).always(function() {
			closeWaitDlg();
		});

	});

	$(document).on("click", "#activeTagCompleteBtn", function() {
		const obj = $(this);
		let wordId = obj.data('word-id');
		let actionUrl = applicationUrl + "update_word_active_tag_complete/" + wordId;
		$.post(actionUrl).done(function(data) {
			if (data !== "{}") {
				openAlertDlg("Andmete muutmine ebaõnnestus.");
				console.log(data);
			}
			refreshDetailsSearch(obj.parents('[data-rel="details-area"]').attr('data-id'));
		}).fail(function(data) {
			openAlertDlg("Andmete muutmine ebaõnnestus.");
			console.log(data);
		});
	});

	let detailButtons = $('#results').find('[name="details-btn"]');
	if (QueryParams.get('id')) {
		const scrollableArea = $('#resultColumn .scrollable-area');
		scrollableArea.empty();
		const idList = QueryParams.get('id').split(',');
		idList.forEach((value, index) => {
			scrollableArea.append(`<div data-id="${value}" id="details-area" class="h-100 ui-sortable-placeholder" data-rel="details-area"></div>`);
			loadDetails(value, 'replace', value);
		});
	} else {
		if (detailButtons.length > 0) {
			detailButtons.eq(0).trigger('click');
		}
	}

	if (viewType === 'lim_term') {
		initNewLimTermWordDlg();
	} else {
		initNewWordDlg();
	}
	initClassifierAutocomplete();
};

function getBreadcrumbsData(detailsDiv, word) {
	const crumbs = detailsDiv.attr('data-breadcrumbs') ? JSON.parse(detailsDiv.attr('data-breadcrumbs')) : [];
	crumbs.push(word);
	return crumbs;
}

// TODO move to ekilex-common.js?
function loadDetails(wordId, task, lastWordId) {
	$("[id^='select_wait_']").hide();
	$("#select_wait_" + wordId).show();
	if (!task) {
		$('#results_div .list-group-item').removeClass('active');
	}
	$("#word-result-" + wordId).addClass('active');
	openWaitDlg();

	let wordDetailsUrl;
	if (viewType === 'term') {
		wordDetailsUrl = applicationUrl + 'termmeaningdetails/' + wordId;
	} else if (viewType === 'lim_term') {
		wordDetailsUrl = applicationUrl + 'limtermmeaningdetails/' + wordId;
	} else {
		wordDetailsUrl = applicationUrl + 'worddetails/' + wordId;
	}

	$.get(wordDetailsUrl).done(function(data) {

		closeWaitDlg();

		let detailsDiv = $('#details-area');
		let scrollPos = detailsDiv.scrollTop();

		if (!task) {
			if (detailsDiv.length === 0) {
				$('#resultColumn:first').find('.scrollable-area').append(detailsDiv = $('<div data-rel="details-area"></div>'));
			}
			$('#resultColumn:first').find('[data-rel="details-area"]').not(':first').remove();
			const dataObject = $(data);
			const breadCrumbs = getBreadcrumbsData(detailsDiv, {
				id: parseInt(wordId),
				word: dataObject.attr('data-word'),
			});
			dataObject.attr('data-breadcrumbs', JSON.stringify(breadCrumbs));
			detailsDiv.replaceWith(dataObject[0].outerHTML);
			detailsDiv = $('#details-area');
		} else {

			const dataObject = $(data);
			dataObject.find('[data-hideable="toolsColumn"]').attr('data-hideable', `toolsColumn-${wordId}`);
			dataObject.find('#toolsColumn').attr('id', `toolsColumn-${wordId}`);
			dataObject.find('[data-extendable="contentColumn"]').attr('data-extendable', `contentColumn-${wordId}`);
			dataObject.find('#contentColumn').attr('id', `contentColumn-${wordId}`);

			if (task === 'replace') {
				detailsDiv = $('#resultColumn:first').find(`[data-rel="details-area"][data-id="${lastWordId}"]`);
				const breadCrumbs = getBreadcrumbsData(detailsDiv, {
					id: parseInt(wordId),
					word: dataObject.attr('data-word'),
				});
				var retainScrollPosition = parseInt(dataObject.attr('data-id')) === parseInt(wordId);

				const scrollPosition = detailsDiv.find('.overflow-auto:first').length && retainScrollPosition ? detailsDiv.find('.overflow-auto:first')[0].scrollTop : 0;

				dataObject.attr('data-breadcrumbs', JSON.stringify(breadCrumbs));

				var newDiv;
				detailsDiv.replaceWith(newDiv = $(dataObject[0].outerHTML));
				newDiv.find('.overflow-auto:first').scrollTop(scrollPosition);
			} else {
				const lastDetailsArea = $('#resultColumn:first').find('[data-rel="details-area"]:last');

				const breadCrumbs = getBreadcrumbsData(dataObject, {
					id: parseInt(wordId),
					word: dataObject.attr('data-word'),
				});
				dataObject.attr('data-breadcrumbs', JSON.stringify(breadCrumbs));

				if (lastDetailsArea.length === 0) {
					$('#resultColumn:first').find('.scrollable-area').append(detailsDiv = $(dataObject[0].outerHTML));
				} else {
					lastDetailsArea.after(detailsDiv = $(dataObject[0].outerHTML));
				}
			}
		}

		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();
		//detailsDiv.scrollTop(scrollPos);

		$(window).trigger('update:wordId');

		$("#select_wait_" + wordId).hide();
		$('.tooltip').remove();

		$('[data-toggle="tooltip"]').tooltip({ trigger: 'hover' });

		$('#results_div .list-group-item').removeClass('active');
		$('#resultColumn:first').find('[data-rel="details-area"]').each((index, element) => {
			const id = $(element).attr('data-id');
			$("#word-result-" + id).addClass('active');
		});

		$('#results_div .list-group-item').each((index, element) => {
			const elem = $(element);
			const button = elem.find('button');
			if (elem.is('.active')) {
				button.removeAttr('data-contextmenu:compare');
				button.attr('data-contextmenu:closePanel', 'Sulge paneel');
			} else {
				button.removeAttr('data-contextmenu:closePanel');
				button.attr('data-contextmenu:compare', 'Ava uues paneelis');
			}
		});

		$wpm.bindObjects();

		setTimeout(() => {

			if (Cookies.get('details-open')) {
				
				const block = $('#'+Cookies.get('details-open'));
				if (block.children('.details-open').length == 0) {
					block.find('[name="lexeme-details-btn"]').trigger('click');
				}
				Cookies.delete('details-open');
			} else {
				closeWaitDlg();
			}
		}, 60);

	}).fail(function(data) {
		alert('Keelendi detailide päring ebaõnnestus');
		closeWaitDlg();
	});
};

function loadFullLexemeDetails(lexemeId, lexemeLevels) {
	loadLexemeDetails(lexemeId, lexemeLevels, "full");
};

function loadLexemeDetails(lexemeId, lexemeLevels, composition) {
	openWaitDlg();

	let lexemeDetailsUrl = applicationUrl + 'lexemedetails/' + composition + '/' + lexemeId + '/' + lexemeLevels;
	$.get(lexemeDetailsUrl).done(function(data) {
		let detailsDiv = $('#lexeme-details-' + lexemeId);
		detailsDiv.html(data);
		detailsDiv = $('#lexeme-details-' + lexemeId);
		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();
		$('.tooltip').remove();
		closeWaitDlg();
		$('[data-toggle="tooltip"]').tooltip({ trigger: 'hover' });
		$(window).trigger('update:multiSelect');
		$(window).trigger('sorter');
		$wpm.bindObjects();
	}).fail(function(data) {
		alert('Lekseemi detailide päring ebaõnnestus');
	}).always(function() {
		closeWaitDlg();
	});
};

function initLexemeLevelsDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let editForm = editDlg.find('form');
		editDlg.find('[name="action"]').val($(this).data('action'));
		let url = editForm.attr('action') + '?' + editForm.serialize();

		Cookies.set('details-open', $('.details-open').parent().attr('id'));

		$.post(url).done(function(data) {
			let id = editDlg.parents('[data-rel="details-area"]:first').data('id');
			let detailsButton = editDlg.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first');
			detailsButton.trigger('click');
			editDlg.find('button.close').trigger('click');
		}).fail(function(data) {
			alert("Andmete muutmine ebaõnnestus.");
			console.log(data);
		});
	});
};

function initUsageAuthorDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	let selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'source-id');
};

function initEditMeaningWordAndLexemeWeightDlg(dlg) {
	let wordValueEditFld = dlg.find('[data-name=wordValueEditFld]');
	let wordValueInput = dlg.find('[name=wordValuePrese]');
	let ekiEditorElem = dlg.find('.eki-editor');
	wordValueEditFld.removeClass('is-invalid');
	wordValueEditFld.html(wordValueInput.val());
	initEkiEditor(ekiEditorElem);

	dlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		if (wordValueEditFld.html()) {
			wordValueEditFld.removeClass('is-invalid');
			wordValueInput.val(wordValueEditFld.html());
			submitDialog(e, dlg, 'Andmete muutmine ebaõnnestus.');
		} else {
			wordValueEditFld.addClass('is-invalid');
		}
	});
};

function refreshDetailsSearch(id) {
	if ($('.details-open').length) {
		Cookies.set('details-open', $('.details-open').parent().attr('id'));
	}
	if (typeof id === 'object') {
		var obj = id;
		if (obj.attr('[data-rel]') === 'details-area') {
			id = obj.attr('data-id');
		} else {
			id = obj.parents('[data-rel="details-area"]').attr('data-id');
		}
	}
	var refreshButton = $(`#resultColumn [data-id="${id}"]:first`);
	if (!refreshButton.is('[data-rel="details-area"]')) {
		refreshButton = refreshButton.parents('[data-rel="details-area"]');
	}
	refreshButton = refreshButton.find('#refresh-details');
	refreshButton.trigger('click');
};

function doNewSearchLexDetail() {
	$('#simple_search_filter').find('button[type=submit]').trigger('click');
};


$.fn.relativeFormItem = function() {
	const main = $(this);
	const id = main.attr('data-relativeFormItem:id');

	main.on('change', function() {
		const relative = $(`#${id}`);
		const value = main.val();
		relative.val(value);
		if (relative.attr('type') === 'radio') {
			relative.prop('checked', true);
		}
		relative.trigger('change');
	});
}

$.fn.complexity = function() {
	const main = $(this);
	const text = main.text().trim();
	const types = {
		'(Detailne)': 'fa fa-hourglass',
		'(Lihtne)': 'fa fa-hourglass-o',
		'(Lihtne/Detailne)': 'fa fa-hourglass-end',
		'(Avalik)': 'fa fa-unlock',
		'(Mitteavalik)': 'fa fa-lock',
	};

	main.addClass('complexity-icon').attr('title', text).html('<i class="' + types[text] + '"></i>');
	main.tooltip();
}
