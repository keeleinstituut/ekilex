function initializeFullSynSearch() {
	let sidebarScrollPosition = {};

	$(document).on("click", ":button[name='synDetailsBtn']", function() {

		const button = $(this);
		const id = button.data('id');

		$("[id^='syn_select_wait_']").hide();
		$(`#syn_select_wait_${id}`).show();
		openWaitDlg();

		let detailsUrl = `${applicationUrl}fullsyn_worddetails/${id}`;

		sidebarScrollPosition = {
			id: $('#syn-details-area').attr('data-id'),
			pos: $("#synCandidatesListDiv").scrollTop(),
		}

		$.get(detailsUrl).done(function(data) {
			let detailsDivParent = $('#syn-details-area').parent();
			detailsDivParent.html(data);
			$('.tooltip').remove();
			closeWaitDlg();
			$(`#syn_select_wait_${id}`).hide();
			$('[data-toggle="tooltip"]').tooltip({trigger: 'hover'});
			decorateSourceLinks($('#syn-details-area'));

			$wpm.bindObjects();

			if ($('#syn-details-area').attr('data-id') === sidebarScrollPosition.id) {
				$('#synCandidatesListDiv').scrollTop(sidebarScrollPosition.pos);
			}

			$('.syn-stats-popover').popover({
				template: '<div class="popover popover-inverted synonym-statistics-popover" role="tooltip"><div class="arrow"></div><div class="popover-head"><h3 class="popover-header"></h3></div><div class="popover-body"></div></div>',
				placement: 'top',
				content: function() {
					// Get the content from the hidden sibling.
					return $(this).siblings('.syn-stats-content').html();
				}
			});

			$(document).find('.draggable-synonym').draggable({
				revert: "invalid",
				appendTo: "body",
				containment: "window",
				helper: "clone",
				delay: 100
			});

			let scrollDebounce;
			$('.lexeme-list').off('scroll.droppable').on('scroll.droppable', function(){
				const main = $(this);
				clearTimeout(scrollDebounce);
				scrollDebounce = setTimeout(function(){
					const elements = main.find('.lexeme-list-item');
					const scrollTop = main.scrollTop();
					const mainHeight = main.height();
					const topPos = main.offset().top + scrollTop;
					elements.each(function(){
						const obj = $(this);
						const objTop = obj.offset().top + scrollTop;
						if (objTop + 100 > topPos && objTop - 100 < topPos + mainHeight) {
							obj.addClass('canAccept');
							obj.find('.droppable-lexeme, .droppable-syn-rel').addClass('canAccept');
						} else {
							obj.removeClass('canAccept');
							obj.find('.droppable-lexeme, .droppable-syn-rel').removeClass('canAccept');
						}
					});
				}, 150);
			}).scroll();

			$(document).find('.droppable-lexeme').droppable({
				accept: function(draggableDiv) {
					const $this = $(this);
					if ($this.is('.canAccept')) {
						if (draggableDiv.hasClass("draggable-synonym")) {
							return true;
						}
					}
				},
				classes: {
					"ui-droppable-active": "ui-state-active",
					"ui-droppable-hover": "ui-state-hover"
				},
				tolerance: 'pointer',
				drop: function(event, ui) {

					const targetMeaningId = $(this).data('meaning-id');
					const targetLang = $(this).data('word-lang');
					const draggableCandidate = ui.draggable;
					const wordRelationId = draggableCandidate.data('syn-relation-id');
					const wordCount = draggableCandidate.data('word-count');
					const isInexactSyn = draggableCandidate.find("input[name='inexactSynSwitch']").is(':checked');

					if (isInexactSyn) {
						initInexactSynSearchDlg(targetMeaningId, targetLang, wordRelationId);
						return;
					}

					if (wordCount === 0) {
						createMeaningWordWithCandidateData(targetMeaningId, wordRelationId);
					} else {
						displayRelationWordSelect(targetMeaningId, wordRelationId);
					}
				}
			});
		}).fail(function(data) {
			console.log(data);
			alert(messages["common.error"]);
		});
	});

	$(document).find('.draggable-synonym').draggable();
	$(document).find('.draggable-syn-rel').draggable();

	if ($('#synSearchResultsDiv').html() == undefined) {
		$(document).find('input[name="simpleSearchFilter"]').focus();
	}

	const detailButtons = $('#results').find('[name="synDetailsBtn"]');
	if (detailButtons.length >= 1) {
		detailButtons.eq(0).click();
	}
	detailSearchBtn();
}

function createMeaningWordWithCandidateData(targetMeaningId, wordRelationId) {
	const synCreateMeaningWordUrl = `${applicationUrl}syn_create_meaning_word_with_candidate_data/${targetMeaningId}/${wordRelationId}`;
	openWaitDlg();
	const callbackFunc = () => refreshSynDetails();
	doPostRelationChange(synCreateMeaningWordUrl, callbackFunc);
}

function displayRelationWordSelect(targetMeaningId, wordRelationId) {
	const synSearchWordsUrl = `${applicationUrl}full_syn_search_words/${targetMeaningId}/${wordRelationId}`;
	$.post(synSearchWordsUrl).done(function(wordSelectDlgHtml) {
		const dlg = $('#relationWordSelectDlg');
		dlg.html(wordSelectDlgHtml);
		dlg.modal('show');
		$wpm.bindObjects();
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
}

$.fn.submitRelationExistingWordBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = $('#submitRelationExistingWordForm');
			submitSynWordForm(form);
		})
	})
}

$.fn.submitRelationHomonymBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = $('#submitRelationHomonymForm');
			submitSynWordForm(form);
		})
	})
}

$.fn.submitUserExistingWordBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = $('#submitUserExistingWordForm');
			submitSynWordForm(form);
		})
	})
}

$.fn.submitUserHomonymBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = $('#submitUserHomonymForm');
			submitSynWordForm(form);
		})
	})
}

function submitSynWordForm(form) {
	$.ajax({
		url: form.attr('action'),
		data: form.serialize(),
		method: 'POST',
	}).done(function() {
		let dlg = form.parents('.modal');
		dlg.modal('hide');
		refreshSynDetails();
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}

$.fn.enableSelectSynWordBtnPlugin = function() {
	return this.each(function() {
		const radioBtn = $(this);
		radioBtn.on('click', function() {
			const form = radioBtn.closest('form');
			enableSelectSynWordBtn(form);
		});
	});
}

function enableSelectSynWordBtn(form) {
	if (form.find('input[type=radio][name="wordId"]:checked').length === 1) {
		$(document).find('button[name="submitSelectedSynWordBtn"]').removeAttr("disabled");
	}
}

function deleteSynLexeme() {
	const opName = "delete";
	const opCode = "lexeme";
	const lexemeId = $(this).attr("data-id");
	const callbackFunc = () => refreshSynDetails();

	executeMultiConfirmPostDelete(opName, opCode, lexemeId, callbackFunc);
}

$.fn.initAddSynMeaningWordDlgPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('show.bs.modal', function() {
			initAddSynMeaningWordDlg(obj);
		})
	})
}

function initAddSynMeaningWordDlg(addDlg) {
	$(document).find('button[name="submitSelectedSynWordBtn"]').attr("disabled", true);
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);

	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const button = $(this);
		const content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		const form = button.closest('form');
		const url = form.attr('action') + '?' + form.serialize();

		$.get(url).done(function(data) {
			addDlg.find('[data-name=dialogContent]').replaceWith(data);
			$wpm.bindObjects();
		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function() {
			button.html(content);
		});
	});

}
