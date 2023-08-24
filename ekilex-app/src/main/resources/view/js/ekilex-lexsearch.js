var viewType = '';

$.fn.identificator = function() {
	const main = $(this);
	const parent = main.parents('.card-body:first');
	let labels = '';
	parent.find('[data-id="identificators"]').find('span').each(function() {
		if (labels !== '') {
			labels += ', ';
		}
		labels += $(this).text();
	});
	main.attr('title', labels);
	main.tooltip();
}

$.fn.lexemePublicity = function() {
	const publicityBtn = $(this);
	const iconSpan = publicityBtn.find('span[name="icon-span"]');
	const label = publicityBtn.data('label');
	const target = publicityBtn.data('target');
	const isEditEnabled = publicityBtn.data('edit-enabled');
	const isPublic = publicityBtn.data('public');

	if (isPublic) {
		iconSpan.addClass('fa fa-unlock');
	} else {
		iconSpan.addClass('fa fa-lock');
	}
	publicityBtn.attr('title', label).tooltip();
	publicityBtn.on('click', function(e) {
		e.preventDefault();
		if (isEditEnabled) {
			$(target).modal('show', publicityBtn);
		}
	});
}

$.fn.valueStatus = function() {
	const main = $(this);
	const parent = main.parents('.card-body:first');
	const source = parent.find('[data-id="valuestatus"]');
	main.html(source.html());
	main.find('.col-w13rem').remove();
	main.find('[data-toggle="delete-confirm"]').deleteConfirm();
}

$.fn.refreshOpenPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function(e) {
			e.preventDefault();
			const lexemeId = obj.data('id');
			const lexemeLevels = obj.data('lex-levels');
			loadLexemeDetails(lexemeId, lexemeLevels, 'full');
		});
	});
}

$.fn.openLexemeDetailsPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const lexemeId = obj.data('id');
			const lexemeLevels = obj.data('lex-levels');
			const composition = obj.data('composition');
			loadLexemeDetails(lexemeId, lexemeLevels, composition);
		})
	})
}

$.fn.duplicateLexemePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			// Check whether id contains the string Empty
			const urlString = obj.attr('id').indexOf('Empty') !== -1 ? 'emptylexduplicate/' : 'lexduplicate/';
			const lexemeId = obj.data('lexeme-id');
			const url = applicationUrl + urlString + lexemeId;
			$.post(url).done(function(response) {
				if (response.status === "OK") {
					openMessageDlg(response.message);
					refreshDetailsSearch(obj.parents('[data-rel="details-area"]').attr('data-id'));
				} else {
					openAlertDlg(response.message);
				}
			}).fail(function(data) {
				openAlertDlg(messages["common.error"]);
				console.log(data);
			});
		});
	});
}

$.fn.duplicateMeaningWordAndLexemePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const successCallback = obj.data("callback");
			const successCallbackFunc = createCallback(successCallback);
			const lexemeId = obj.data('lexeme-id');
			const url = `${applicationUrl}meaningwordandlexduplicate/${lexemeId}`;
			$.post(url).done(function() {
				successCallbackFunc();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}

function loadFullLexemeDetails(lexemeId, lexemeLevels) {
	loadLexemeDetails(lexemeId, lexemeLevels, "full");
};

function loadLexemeDetails(lexemeId, lexemeLevels, composition) {
	openWaitDlg();

	const lexemeDetailsUrl = `${applicationUrl}lexemedetails/${composition}/${lexemeId}/${lexemeLevels}`;
	$.get(lexemeDetailsUrl).done(function(data) {
		// Find div and replace its html before returning it
		const detailsDiv = $('#lexeme-details-' + lexemeId).html(data);
		decorateSourceLinks(detailsDiv);
		initClassifierAutocomplete();
		$('.tooltip').remove();
		closeWaitDlg();
		$('[data-toggle="tooltip"]').tooltip({ trigger: 'hover' });
		$(window)
			.trigger('update:multiSelect')
			.trigger('sorter');
		$wpm.bindObjects();
	}).fail(function() {
		alert('Lekseemi detailide päring ebaõnnestus');
	}).always(function() {
		closeWaitDlg();
	});
};

function initLexemeLevelsDlg(editDlg) {
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const editForm = editDlg.find('form');
		editDlg.find('[name="action"]').val($(this).data('action'));
		const url = editForm.attr('action') + '?' + editForm.serialize();

		Cookies.set('details-open', $('.details-open').parent().attr('id'));

		$.post(url).done(function() {
			const detailsButton = editDlg.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first');
			detailsButton.click();
			editDlg.find('button.close').click();
		}).fail(function(data) {
			alert("Andmete muutmine ebaõnnestus.");
			console.log(data);
		});
	});
};

function initUsageAuthorDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);
	const selectElem = addDlg.find('select');
	selectElem.val(selectElem.find('option').first().val());
	initRelationDialogLogic(addDlg, 'source-id');
};

function refreshDetailsSearch(id) {
	const detailsOpen = $('.details-open');

	if (detailsOpen.length) {
		Cookies.set('details-open', detailsOpen.parent().attr('id'));
	}

	if (typeof id === 'object') {
		const obj = id;
		if (obj.attr('[data-rel]') === 'details-area') {
			id = obj.attr('data-id');
		} else {
			id = obj.parents('[data-rel="details-area"]').attr('data-id');
		}
	}

	let refreshButton = $(`#resultColumn [data-id="${id}"]:first`);
	if (!refreshButton.is('[data-rel="details-area"]')) {
		refreshButton = refreshButton.parents('[data-rel="details-area"]');
	}

	refreshButton = refreshButton.find('#refresh-details');
	refreshButton.click();
};

function doNewSearchLexDetail() {
	$('#simple_search_filter').find('button[type=submit]').click();
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
		relative.change();
	});
}

$.fn.complexityAndPublicity = function() {
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
