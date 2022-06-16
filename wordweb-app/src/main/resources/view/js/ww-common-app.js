var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	$('[autofocus]:not(:focus)').eq(0).focus();
	$('.home-page #search').focus();

	// Focus trap sidebar
	$('.header-links .menu-item:last-of-type').on('focus', function(e) {
		if ($("#uiLangMenuButton").val() == "") {
			$("#uiLangMenuButton").focus();
		}
	});

	$('[data-toggle="tooltip"]').tooltip();
});

$(document).on("click", ".menu-btn", function(e) {
	$(".header-container").toggleClass("show-header");
});

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}

$(document).on("click", "button[name='feedbackSendBtn']", function(event) {

	if (feedbackServiceUrl === null) {
		console.debug('Feedback service configuration is missing.');
		alert(messages.fb_service_error);
		return;
	}
	var feedbackForm = $(this).closest('form');

	feedbackForm.addClass('was-validated');
	if (feedbackForm[0].checkValidity() === false) {
		event.preventDefault();
		event.stopPropagation();
		return;
	}

	var dataDiv = $('#feedbackModal').find('#dataDiv');
	var responseDiv = $('#feedbackModal').find('#responseDiv');
	var okMessageElement = responseDiv.find('#feedbackSuccessMsg');
	var errorMessageElement = responseDiv.find('#feedbackFailMsg');
	var okMessage = feedbackForm.find('[name=ok_message]').text();
	var acceptPrivacyStatement = feedbackForm.find('.modal-check');
	$.ajax({
		url : feedbackServiceUrl,
		data : JSON.stringify(feedbackForm.serializeJSON()),
		method : 'POST',
		dataType : 'json',
		contentType : 'application/json'
	}).done(function(data) {
		if (data.status === 'ok') {
			dataDiv.addClass('d-none');
			responseDiv.removeClass('d-none');
			okMessageElement.text(okMessage);
			okMessageElement.removeClass('d-none');
			acceptPrivacyStatement.trigger('click');
		} else {
			errorMessageElement.removeClass('d-none');
		}
	}).fail(function(data) {
		console.log("TOTAL FAIL");
		dataDiv.addClass('d-none');
		responseDiv.removeClass('d-none');
		errorMessageElement.removeClass('d-none');
		acceptPrivacyStatement.trigger('click');
	});
});

$(document).on("click", ".modal-check", function() {
	$(this).closest('form').find("button[name='feedbackSendBtn']").prop('disabled', !$(this).prop('checked'));
});

function clearMessages(modalDlg) {
	modalDlg.find('form').removeClass('was-validated');
	modalDlg.find('[name=error_message]').attr('hidden', true);
}

$(document).on("click", "#feedbackSimpleRadio", function() {
	$('#feedWord').addClass('show-section');
	$('#feedComment').removeClass('show-section');
	clearMessages($(this).closest('.modal-dialog'));
});

$(document).on("click", "#feedbackCompleteRadio", function() {
	$('#feedWord').removeClass('show-section');
	$('#feedComment').addClass('show-section');
	clearMessages($(this).closest('.modal-dialog'));
});

$(document).on('show.bs.modal', '#feedbackModal', function() {
	var fbModal = $(this);
	clearMessages(fbModal);
	fbModal.find('#dataDiv').removeClass('d-none');
	fbModal.find('#responseDiv').addClass('d-none');
	fbModal.modal('toggle');
	fbModal.off('shown.bs.modal').on('shown.bs.modal', function() {
		fbModal.find('.close').focus()
	});
});

$(document).on('show.bs.modal', '#inflections-modal', function() {
	$('.scrollable-table').trigger('scrollableTable:update');
});

$(document).on('show.bs.tab', '#markidega-tab-btn, #kaanetega-tab-btn', function() {
	$('.scrollable-table').trigger('scrollableTable:update');
});

$.fn.scrollableTable = function() {
	$(this).each(function() {
		var main = $(this);
		var scroller = main.find('.scrollable-table--scroller');
		var id = 'scrollable-' + Math.random().toString().substr(2);
		var overflowIndicatorRight, overflowIndicatorLeft;
		var overflowable = false;
		var safeArea = 20;
		var table = main.find('table');
		var tableCloneParent;
		var fixColumn = main.find('table').attr('data-fixColumn') ? true : false;

		main.prepend(overflowIndicatorRight = $('<div class="scrollable-table--indicatorRight"></div>'));
		main.prepend(overflowIndicatorLeft = $('<div class="scrollable-table--indicatorLeft"></div>'));

		if (fixColumn) {
			main.prepend(tableCloneParent = $('<div class="tableClone">' + table[0].outerHTML + '</div>'));
		}

		$(window).on('resize', function() {
			calculateDimensions();
		});

		main.on('scrollableTable:update', function() {
			setTimeout(function() {
				calculateDimensions();
				scroller[0].scrollTo(0, 0);
			}, 60);
		});
		main.on('scrollableTable:quickUpdate', function() {
			calculateDimensions();
		});

		function calculateDimensions() {
			var mainWidth = main.width();
			var tableWidth = scroller.find('table').outerWidth();
			if (mainWidth === tableWidth || mainWidth === 0) {
				overflowIndicatorRight.hide();
				overflowIndicatorLeft.hide();
				overflowable = false;
			} else {
				overflowIndicatorRight.show();
				overflowIndicatorLeft.show();
				overflowable = true;
			}
			if (fixColumn) {
				var width = table.find('th:first span:first').outerWidth() + 24;

				tableCloneParent.find('table').css({
					width: table.width(),
				});

				tableCloneParent.css({
					width: width + 1,
				});

				tableCloneParent.find('table:first > tbody > tr').children('td:first, th:first').css({
					width: width,
				});

				overflowIndicatorLeft.css({
					visibility: 'hidden',
				});

			}
			scroller.trigger('scroll');
		}

		scroller.on('scroll', function() {
			var mainWidth = main.width();
			var tableWidth = scroller.find('table').outerWidth();
			var scrollPos = scroller.scrollLeft();
			if (overflowable) {
				if (mainWidth + scrollPos >= tableWidth - safeArea) {
					overflowIndicatorRight.hide();
				} else {
					overflowIndicatorRight.show();
				}

				if (scrollPos <= safeArea) {
					overflowIndicatorLeft.hide();
				} else {
					overflowIndicatorLeft.show();
				}
			}
		});
		setTimeout(function(){
			calculateDimensions();
		}, 30);
	});
}

// Custom collapse for expandable text
$(document).on('click', '[data-toggle="collapse-text"]', function() {
	const btn = $(this);
	const targetText = $(btn.attr('data-target'));
	let targetTextBaseHeight = targetText.attr('data-base-height');
	const targetTextMaxHeight = targetText.attr('data-max-height');
	const addedData = btn.siblings('.large-text-container');
	const addedText = addedData.find('.large-text-addition');
	const placeholder = addedData.find('.large-text-placeholder');
	if (!targetTextBaseHeight) {
		const targetHeight = targetText.prop('offsetHeight');
		targetText.attr('data-base-height', targetHeight);
		targetTextBaseHeight = targetHeight;
	}

	if (btn.attr('aria-expanded') === 'true') {
			targetText.css('height', `${targetTextBaseHeight}px`);
			btn.attr('aria-expanded', false);
			// Delay removal of text until the animation is done
			setTimeout(() => {
				targetText.html(targetText.html().substring(0, 500));
			}, 250);
	} else {
			if (targetTextMaxHeight) {
				targetText.css('height', `${targetTextMaxHeight}px`);
			} else {
				// Create a temp div to get the resulting height
				const tempDiv = $("<div/>").css({
					position: 'absolute',
					visibility: 'hidden',
					width: targetText.width()
				}).addClass(targetText.attr('class'))
					.html(targetText.html() + addedText.html())
					.append(placeholder.clone());
				targetText.after(tempDiv);
				
				const tempDivHeight = tempDiv.prop('offsetHeight');
				targetText.attr('data-max-height', tempDivHeight);
				targetText.css('height', `${targetTextBaseHeight}px`);
				// Delay the second height change to allow for animation
				setTimeout(() => {
					targetText.css('height', `${tempDivHeight}px`);
				}, 0);
			}
			// Combine the two texts and add placeholder at the end
			targetText.html(targetText.html() + addedText.html()).append(placeholder.clone());
			btn.attr('aria-expanded', true);
	}
});