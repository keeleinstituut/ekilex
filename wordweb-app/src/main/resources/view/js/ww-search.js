var windowWidthTreshold = 768;

$(window).on("popstate", function(e) {
	e.preventDefault();
	var historyState = e.originalEvent.state;
	if (historyState != null) {
		window.location = historyState.wordSelectUrl;
	}
});

//Dismiss popovers when clicked outside of it
$(document).on('click', function(e) {
	$('[data-toggle="popover"],[data-original-title]').each(function() {
		//the 'is' for buttons that trigger popups
		//the 'has' for icons within a button that triggers a popup
		if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
			if ($(this).data('bs.popover')) {
				(($(this).popover('hide').data('bs.popover') || {}).inState || {}).click = false // fix for BS 3.3.6
			}
		}
	});
});

//Hide tooltips on click on mobile
$(document).on("click", ".btn-ellipsis", function(e) {
	$(this).tooltip('hide');
});

function fetchDetails(wordId, wordSelectUrl) {
	var wordDetailsUrlWithParams = wordDetailsUrl + "/" + wordId;
	$.get(wordDetailsUrlWithParams).done(function(data) {
		$('.word-details').replaceWith(data);
		fetchCorpSentences();
		fetchCorpTranslations();
		updateBrowserHistory(wordSelectUrl);
		setHomonymNrVisibility();
		$('.word-details [data-toggle="tooltip"], [data-tooltip="tooltip"]').tooltip();
		$('[data-toggle="popover"]').popover({
			placement: 'top'
		});
		var lightbox = new SimpleLightbox('.gallery-image', {
			history: false,
			captionPosition: 'outside',
			navText: ['<i class="fas fa-arrow-left"></i>', '<i class="fas fa-arrow-right"></i>'],
			closeText: '<i class="fas fa-times"></i>'
		});
		$("#mainContentArea").removeClass("loading");
	}).fail(function(data) {
		alert(messages.search_failure);
		$("#mainContentArea").removeClass("loading");
	})
}

function setHomonymNrVisibility() {
	var nrOfHomonyms = $(".homonym-item").length;
	if (nrOfHomonyms === 1) {
		$('.word-details-homonym-nr').addClass('d-none');
	}
}

function fetchCorpSentences() {
	var corpDiv = $("#corp");
	var corpSentencesUrl = corpUrl + '/' + encodeURIComponent(currentWord) + '/' + currentWordLang;
	$.get(corpSentencesUrl).done(function(data) {
		corpDiv.replaceWith(data);
	}).fail(function(data) {
	})
}

function fetchCorpTranslations() {
	var corpTransDiv = $("#corpTrans");
	var corpTranslationsUrl = corpTransUrl + '/' + currentWordId + '/' + encodeURIComponent(currentWord) + '/' + currentWordLang;
	$.get(corpTranslationsUrl).done(function(data) {
		corpTransDiv.replaceWith(data);
	}).fail(function(data) {
	})
}

function updateBrowserHistory(wordSelectUrl) {
	if (currentWord.indexOf('/') !== -1) {
		wordSelectUrl = wordSelectUrl.replace(currentWord, encodeURIComponent(currentWord));
	}
	var historyState = {
		wordId: currentWordId,
		word: currentWord,
		wordSelectUrl: wordSelectUrl
	};
	history.pushState(historyState, "Sõnaveeb", wordSelectUrl);
}

function scrollToLexeme(lexemeId) {
	if (lexemeId) {
		$([document.documentElement, document.body]).animate({
			scrollTop: $("#lexeme-section-" + lexemeId).offset().top - 100
		}, 1000);
	}
}

$(document).on("click", ".menu-btn", function() {
	if ($(this).attr('aria-expanded') === 'false') {
		$(this).attr('aria-expanded', 'false');
	} else {
		$(this).attr('aria-expanded', 'true');
	}
});

$(document).on("click", "button[name='expand-btn']", function() {
	$(this).parent().find(".collapsable[data-collapse='true']").fadeToggle("slow", "linear");
});

$(document).on("click", "a[id^='word-details-link']", function() {
	var wordWrapperForm = $(this).closest("form");
	var wordId = wordWrapperForm.children("[name='word-id']").val();
	var wordSelectUrl = wordWrapperForm.children("[name='word-select-url']").val();
	fetchDetails(wordId, wordSelectUrl);
});

$(document).on("click", "a[id^='feedback-link']", function() {
	$("button[name='feedback-btn']").trigger('click');
});

$(document).on("click", ".homonym-item", function() {
	$(".homonym-list-item").removeClass("selected last-selected");
	$(".homonym-item:first").removeClass("animation-target").dequeue();
	$(this).parents(".homonym-list-item").addClass("selected last-selected");
	var homonymList = $('.homonym-list');
	if ($(window).width() >= windowWidthTreshold) {
		homonymList.animate({
			scrollLeft: $('.homonym-list-item.selected .homonym-item-wrap').parent().position().left - $('.search-panel').offset().left + homonymList.scrollLeft()
		}, 200);
	}
	setSelectedHomonymValueForMobile($(this).html());
	$(".homonym-list").removeClass("expand");
});

$(document).on("click", ".word-form", function() {
	var word = $(this).data('word');
	$("input[name = 'searchWord']").val(word);
	$('#search-btn').trigger('click');
});

$(document).on("click", ".word-grouper-wrapper .btn-collapse", function() {
	let item = $(this);
	let middle = item.parent().find('.limit');
	if (item.attr("aria-expanded") === "true") {
		middle.removeClass('limit-collapsed');
	} else {
		middle.addClass('limit-collapsed');
	}
});


$(window).on('load', function() {
	initStickyScrollPanel();
})



function initStickyScrollPanel() {
	const panel = document.querySelector(".sticky-scroll-panel");
	const tags = panel?.querySelector(".sticky-scroll-panel__tags");
  if (!tags) {
		return;
  }
	const panelHeight = panel.offsetHeight ?? 0;
	let stickyScrollTimeout;
  const links = [...tags.children];
	links.forEach(link => {
		link.addEventListener('click', e => {
			e.preventDefault();
			const target = document.getElementById(link.href.split("#")?.[1]);
			if (target) {
				const elementPosition = target.getBoundingClientRect().top;
				// Scroll to element, subtracting the sticky panels height and a little extra
				const offsetPosition = elementPosition + window.scrollY - panelHeight - 48;
			
				window.scrollTo({
					top: offsetPosition,
					behavior: "smooth"
				});
			}
		})
	})
	const linkTargetCache = {};

  const activeClass = "sticky-scroll-panel__tag--active";
  document.addEventListener("scroll", () => {
    // Debounce scroll events
    clearTimeout(stickyScrollTimeout);
    stickyScrollTimeout = setTimeout(() => {
      const element = [];
      links.forEach((link) => {
        let target = linkTargetCache[link.href];
        if (!target) {
          const block = document.getElementById(link.href.split("#")?.[1]);
          if (block) {
            linkTargetCache[link.href] = block;
            target = block;
          }
        }
        link.classList.remove(activeClass);
				if (!target) {
					return;
				}
        // Check if element is in viewport
        const targetTop = target.getBoundingClientRect().top;
        // Get the closest element to the top of viewport
        if (
          targetTop <= target.offsetHeight + 64 &&
          (element[0] < targetTop || element[0] === undefined)
        ) {
          element[0] = targetTop;
          element[1] = link;
        }
      });
      if (element[1]) {
        element[1].classList.add(activeClass);
      }
    }, 50);
  });
}
