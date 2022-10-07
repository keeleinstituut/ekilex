function initializeFullSynSearch() {
	let activeSearchResultID;
	let sidebarScrollPosition = {};

	$(document).on("click", ":button[name='synDetailsBtn']", function() {

		// TODO remove unnecessary (keyboard mode) code and move common code with partsyn together to ekilex-synsearch.js

		const button = $(this);
		let savedScrollPositions = getScrollPositions();

		const id = button.data('id');
		$('#synSearchResultsDiv .list-group-item')
			.removeClass('keyboard-nav-list-item-active active')
			.removeAttr('data-navigate-selected');
		// Make all results with the same id active, in case there are multiple with same id
		$(`#synSearchResultsDiv button[data-id=${id}]`).parent().addClass('active').attr('data-navigate-selected', true);

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
				helper: "clone"
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
							// TODO is dragging always enabled?
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
					const draggableCandidate = ui.draggable;
					const wordRelationId = draggableCandidate.data('syn-relation-id');
					const wordCount = draggableCandidate.data('word-count');

					if (wordCount > 1) {
						const synCreateMeaningWordUrl = `${applicationUrl}full_syn_search_words/${targetMeaningId}/${wordRelationId}`;
						$.post(synCreateMeaningWordUrl).done(function(wordSelectDlgHtml) {
							const wordSelectDlg = $('#wordSelectDlg');
							wordSelectDlg.html(wordSelectDlgHtml);
							wordSelectDlg.modal('show');
						}).fail(function(data) {
							openAlertDlg(messages["common.error"]);
							console.log(data);
						});
					} else {
						const synCreateMeaningWordUrl = `${applicationUrl}syn_create_meaning_word/${targetMeaningId}/${wordRelationId}`;
						openWaitDlg();
						const callbackFunc = () => refreshSynDetails();
						doPostRelationChange(synCreateMeaningWordUrl, callbackFunc);
					}
				}
			});

			//KEEP TRACK OF WHAT WAS THE LAST SEARCH RESULT DISPLAYED
			if (activeSearchResultID !== id) {
				activeSearchResultID = id;
			}
			//IF AN ALLREADY ACTIVE DETAILS VIEW WAS SELECTED KEEP THE SCROLLPOSITIONS
			else if (activeSearchResultID === id) {
				setScrollPositions(savedScrollPositions);
			}

		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
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

function initializeFullSynWordSearch() {
	$wpm.bindObjects();
}

$.fn.submitSynWordBtnPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = $('#submitSynWordForm');
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function() {
				const wordSelectDlg = $('#wordSelectDlg');
				wordSelectDlg.modal('hide');
				refreshSynDetails();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	})
}

$.fn.enableSelectSynWordBtnPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			enableSelectSynWordBtn();
		});
	});
}

function enableSelectSynWordBtn() {
	if ($('input[type=radio][name="wordId"]:checked').length === 1) {
		$("#selectWordBtn").removeAttr("disabled");
	}
}
