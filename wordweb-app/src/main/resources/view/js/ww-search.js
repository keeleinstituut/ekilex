var windowWidthTreshold = 768;

function fetchDetails(wordId, word, wordSelectUrl) {
	var detailsDiv = $('.word-details');
	var wordDetailsUrl = applicationUrl + "worddetails/" + wordId;
	$.get(wordDetailsUrl).done(function (data) {
		detailsDiv.replaceWith(data);
		if (word.indexOf('/') !== -1) {
			wordSelectUrl = wordSelectUrl.replace(word, encodeURIComponent(word));
		}
		var historyState = {
			wordId: wordId,
			word: word,
			wordSelectUrl: wordSelectUrl
		};
		history.pushState(historyState, "Sõnaveeb", wordSelectUrl);
		fetchCorpSentences(word);
		setHomonymNrVisibility();
		$('.word-details [data-toggle="tooltip"]').tooltip();
		calculateAndSetStyles();
	}).fail(function (data) {
		console.log(data);
		alert(messages.search_failure);
	})
}

function setHomonymNrVisibility() {
	var nrOfHomonyms = $(".homonym-item").length;
	if (nrOfHomonyms == 1) {
		$('.word-details .homonym-nr').addClass('d-none');
	}
}

function fetchCorpSentences(sentence) {
	var corpDiv = $("#korp");
	var corpSentencesUrl = applicationUrl + 'korp/' + encodeURIComponent(sentence);
	$.get(corpSentencesUrl).done(function (data) {
		corpDiv.replaceWith(data);
	}).fail(function (data) {
		console.log(data);
	})
}

$(function () {
	$('[data-toggle="tooltip"]').tooltip({
		container: 'body'
	});
});

$(document).on("click", ".more-btn", function () {
	$(this).parents(".meaning-panel, .dependencies, .collocations-section").toggleClass("expand");
	$(this).parents(".meaning-panel, .dependencies, .collocations-section").find(".colloc-col, .dependence, .label, .label-md, .corp-panel div:nth-child(n+5), .colloc-heading, .colloc-name, .lexeme-list b").toggleClass("fade-target");

	$(this).parent(".word-group, .corp-panel").toggleClass("expand");
	$(this).parent(".word-group").find(".word-options").toggleClass("fade-target");

	$(this).next(".morphology-section").toggleClass("expand");

	var counter = 0;
	$('.dependence').each(function (i) {
		if (i % 1000 == 0) {
			counter = 1;
		} else {
			counter++;
		}
		if (counter < 4) {
			$(this).removeClass('fade-target');
		}
	});
		$('.colloc-col').each(function (i) {
		if (i % 1000 == 0) {
			counter = 1;
		} else {
			counter++;
		}
		if (counter < 4) {
			$(this).removeClass('fade-target');
		}
	});
});

$(window).on("popstate", function (e) {
	e.preventDefault();
	var historyState = e.originalEvent.state;
	if (historyState != null) {
		var wordSelectUrl = historyState.wordSelectUrl;
		window.location = wordSelectUrl;
	}
});

$(document).on("click", "a[id^='word-details-link']", function (e) {
	var wordWrapperForm = $(this).closest("form");
	var wordId = wordWrapperForm.children("[name='word-id']").val();
	var word = wordWrapperForm.children("[name='word-value']").val();
	var wordSelectUrl = wordWrapperForm.children("[name='word-select-url']").val();
	fetchDetails(wordId, word, wordSelectUrl);
});

$(document).on("click", "button[name='colloc-usages-btn']", function (e) {
	$(this).closest("[id^='collocs-area']").find("[id^='colloc-usages-area']").fadeToggle();
});

// demo js for interactions between the mobile and desktop modes
$(document).on("click", ".back", function (e) {
	if ($(".homonym-panel").hasClass("d-none")) {
		$(".content-panel").addClass("d-none d-md-block");
		$(".homonym-panel").removeClass("d-none d-md-block");
		$(".search-panel").removeClass("d-none d-md-block");
		$('#form-words').css("margin-top", '0');
		calculateAndSetStyles();
	}
});

$(document).on("click", ".homonym-item", function (e) {
	$(".homonym-item").removeClass("selected last-selected");
	$(".homonym-item:first").removeClass("animation-target").dequeue();
	$(this).addClass("selected last-selected");
	calculateAndSetStyles();
	if ($(window).width() >= windowWidthTreshold) {
		$('.homonym-list').animate({
				scrollLeft: $('.homonym-item.selected .homonym-item-wrap').parent().position().left - $('.search-panel').offset().left + $('.homonym-list').scrollLeft()
			},
			200);
	}
	if ($(window).width() >= windowWidthTreshold) {
		$('.homonym-list').animate({
				scrollLeft: $('.homonym-item.selected .homonym-item-wrap').parent().position().left - $('.search-panel').offset().left + $('.homonym-list').scrollLeft()
			},
			200);
	} else {
		$(".homonym-panel").addClass("d-none");
		$(".search-panel").addClass("d-none");
		$('#form-words').css("margin-top", '5em');
	}
});

$(document).on("click", "[name='word-form-btn']", function (e) {
	var word = $(this).data('word');
	$("input[name = 'searchWord']").val(word);
	$('#search-btn').trigger('click');
});

