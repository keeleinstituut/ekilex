var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	$('[autofocus]:not(:focus)').eq(0).trigger('focus');
	$('.home-page #search').trigger('focus');
	$('[data-toggle="tooltip"]').tooltip();
});

// Remove other tooltips when a new one opens
$(document).on('show.bs.tooltip', function() {
	$('.tooltip').not(this).remove();
});

$(document).on("click", ".menu-btn", function() {
	const headerContainer = document.querySelector('.header-container');
	if (headerContainer.classList.contains('show-header')) {
		document.body.classList.remove('overflow-hidden');
		headerContainer.classList.remove('show-header');
	} else {
		document.body.classList.add('overflow-hidden');
		headerContainer.classList.add('show-header');
	}
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
		setTimeout(function() {
			calculateDimensions();
		}, 30);
	});
}


