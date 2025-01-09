var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	$('[autofocus]:not(:focus)').eq(0).trigger('focus');
	$('.home-page #search').trigger('focus');

	// Focus trap sidebar
	$('.header-links .menu-item:last-of-type').on('focus', function(e) {
		if ($("#uiLangMenuButton").val() == "") {
			$("#uiLangMenuButton").trigger('focus');
		}
	});

	$('[data-toggle="tooltip"]').tooltip();
	Feedback.init(feedbackServiceUrl);
	closeSurveyBanner();
	handleVirtualKeyboard();
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

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}

class Feedback {
  static serviceUrl = "";
  static elementIds = [
    ["modal", "feedback-modal"],
    ["form", "feedback-form"],
    ["checkboxGroup", "feedback-modal-checkbox-group"],
    ["checkbox", "feedback-email-checkbox"],
    ["submitButton", "feedback-submit"],
    ["repeatButton", "feedback-repeat"],
    ["emailInput", "feedback-modal-email"],
    ["data", "feedback-data"],
    ["response", "feedback-response"],
    ["successMessage", "feedback-success-message"],
    ["errorMessage", "feedback-error-message"],
    ["title", "feedback-modal-title"],
    ["successTitle", "feedback-modal-success-title"],
  ];

  static modalElements = {};

  static init(serviceUrl) {
    this.serviceUrl = serviceUrl;
    this.initElements();
    if (!this.modalElements.modal) {
      return;
    }
    this.initListeners();
  }

  static initElements() {
    // Get all elements by their ids and combine into one object
    this.modalElements = this.elementIds.reduce((acc, [key, val]) => {
      if (acc.modal) {
        acc[key] = acc.modal.querySelector(`#${val}`);
      } else {
        acc[key] = document.getElementById(val);
      }
      return acc;
    }, {});
  }

  static initListeners() {
    this.modalElements.emailInput.addEventListener(
      "keyup",
      this.handleEmailInput.bind(this)
    );

    $(this.modalElements.modal).on(
      "show.bs.modal",
      this.handleModalOpen.bind(this)
    );
    $(this.modalElements.modal).on(
      "hide.bs.modal",
      this.handleModalClose.bind(this)
    );

    this.modalElements.submitButton.addEventListener(
      "click",
      this.handleSubmit.bind(this)
    );
    this.modalElements.repeatButton.addEventListener("click", () => {
      this.handleModalClose();
      this.handleModalOpen(true);
    });
  }

  static handleEmailInput(event) {
    // Toggle the need of checkbox based on whether email has a value
    if (event.target.value) {
      this.modalElements.checkboxGroup.classList.remove("d-none");
      this.modalElements.checkbox.required = true;
    } else {
      this.modalElements.checkboxGroup.classList.add("d-none");
      this.modalElements.checkbox.required = false;
    }
  }

  static handleModalOpen(skipToggle) {
    this.clearMessages(this.modalElements.modal);
    this.modalElements.data.classList.remove("d-none");
    this.modalElements.response.classList.add("d-none");
    if (!skipToggle) {
      $(this.modalElements.modal).modal("toggle");
    }
  }

  static handleModalClose() {
    if (!this) {
      return;
    }
    this.clearMessages();
    const inputs = this.modalElements.form.querySelectorAll?.(
      "input:not([type='hidden'])"
    );
    // Reset form state on close
    this.modalElements.checkboxGroup.classList.add("d-none");
    this.modalElements.checkbox.required = false;
    [...inputs].forEach((input) => {
      input.value = "";
    });
    this.modalElements.successMessage.classList.add("d-none");
    this.modalElements.successTitle.classList.add("d-none");
    this.modalElements.title.classList.remove("d-none");
    this.modalElements.errorMessage.classList.add("d-none");
  }

  static handleSubmit(event) {
    if (this.serviceUrl === null) {
      console.debug("Feedback service configuration is missing.");
      alert(messages.feedback_missing_service);
      return;
    }

    this.modalElements.form.classList.add("was-validated");
    if (this.modalElements.form[0].checkValidity() === false) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }

    $.ajax({
      url: feedbackServiceUrl,
      data: JSON.stringify($(this.modalElements.form).serializeJSON()),
      method: "POST",
      dataType: "json",
      contentType: "application/json",
    })
      .done((data) => {
        // Switch to showing response
        this.modalElements.data.classList.add("d-none");
        this.modalElements.response.classList.remove("d-none");
        if (data.status === "ok") {
          this.modalElements.successMessage.classList.remove("d-none");
          this.modalElements.title.classList.add("d-none");
          this.modalElements.successTitle.classList.remove("d-none");
        } else {
          this.modalElements.errorMessage.classList.remove("d-none");
        }
      })
      .fail(() => {
        this.modalElements.data.classList.add("d-none");
        this.modalElements.response.classList.remove("d-none");
        this.modalElements.errorMessage.classList.remove("d-none");
      });
  }

  static clearMessages() {
    // Clear out any form errors
    this.modalElements.form.classList.remove("was-validated");
    const messages = this.modalElements.modal.querySelectorAll(
      "[name=error_message]"
    );
    [...messages].forEach((message) => {
      message.hidden = true;
    });
  }
}

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

function closeSurveyBanner() {
	const banner = $('.survey-banner');
	const closeBtn = banner.find('.close-banner');

	if (closeBtn) {
		closeBtn.on('click', function() {
			banner.addClass('hide');
			$('body').removeClass('survey-active');
		});
	}
}


function calculateMaxHeightCollapseText(element, targetTextBox) {
	if (!element.attr("data-max-height")) {
		const targetTextBoxWidth = targetTextBox.outerWidth(); // get maximum width, to calculate height correctly
		element.css("width", targetTextBoxWidth + "px");
		element.removeClass('d-none');
		let dataHeight = element.outerHeight();
		element.attr('data-max-height', dataHeight);
		element.addClass('d-none');
	}
}


function handleVirtualKeyboard() {

	const allBtns = $(".main-search-btns");
	const widthOfAllBtn = allBtns.outerWidth();
	const widthVirtualKeyboard = allBtns.find('#keyboard-search-btn').outerWidth();
	const widthOfAllBtnWithoutVirtualKeyboard = widthOfAllBtn - widthVirtualKeyboard;
	let keyBoardpView = window.matchMedia('(min-width: 1025px)');
	let noKeyBoardView = window.matchMedia('(min-width: 768px)');

	$(document).on('click', '#keyboard-search-btn , #start-rec-btn, #stop-rec-btn', function() {
		calview(widthOfAllBtnWithoutVirtualKeyboard, widthOfAllBtn, keyBoardpView, noKeyBoardView);
	});

	$(window).on('resize', function() {
		calview(widthOfAllBtnWithoutVirtualKeyboard, widthOfAllBtn, keyBoardpView, noKeyBoardView);
	});

	function calview(widthOfAllBtnWithoutVirtualKeyboard, widthOfAllBtn, keyBoardpView, noKeyBoardView) {

		if (keyBoardpView.matches) {
			calPading(widthOfAllBtn, 192);
		} else if (noKeyBoardView.matches) {
			calPading(widthOfAllBtnWithoutVirtualKeyboard, 152);
		} else {
			$('.main-search-input').css({ 'padding-right': '' });
		}
	}


	function calPading(allClosedMainSearchBtnsWidth, mainSearchInputPadding) {
		setTimeout(() => {
			$("#keyboard-lang-search").promise().done(function() {

				if ($('.keyboard-search').hasClass('lang-open') || $("#stop-rec-btn").is(":visible")) {

					let mainSearchBtnsWidth = $(".main-search-btns").outerWidth(); // current 
					let clearSearchBtn = $('#clear-search-btn').outerWidth();

					let newWidth = $(document).find('#clear-search-btn').is(":visible") ?
						(mainSearchBtnsWidth - allClosedMainSearchBtnsWidth - clearSearchBtn) :
						(mainSearchBtnsWidth - allClosedMainSearchBtnsWidth);

					let final = newWidth + mainSearchInputPadding;

					$('.main-search-input').css({ 'padding-right': final });

				} else {
					$('.main-search-input').css({ 'padding-right': '' });
				}
			});
		}, "500")
	}
}
