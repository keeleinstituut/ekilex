var windowWidthTreshold = 768;

function fetchDetails(wordId, word, wordSelectUrl) {
	var detailsDiv = $('.word-details');
	var wordDetailsUrl = applicationUrl + "worddetails/" + wordId;
	$.get(wordDetailsUrl).done(function(data) {
		detailsDiv.replaceWith(data);
		if (word.indexOf('/') !== -1) {
			wordSelectUrl = wordSelectUrl.replace(word, encodeURIComponent(word));
		}
		var historyState = {
			wordId : wordId,
			word : word,
			wordSelectUrl : wordSelectUrl
		};



		
var html = $('#spellingLine').html();
$('#spellingLine').html(html.replace(/\¤e/gi, '<span><u>e</u></span>'));
		history.pushState(historyState, "Sõnaveeb", wordSelectUrl);
		var lang = wordSelectUrl.split('/').find(function(e) {
			return e.indexOf('-') !== -1
		}).split('-')[0];
		fetchCorpSentences(lang, word);
		setHomonymNrVisibility();
		$('.word-details [data-toggle="tooltip"]').tooltip();
		calculateAndSetStyles();
	}).fail(function(data) {
		console.log(data);
		alert(messages.search_failure);
	})
}

function setHomonymNrVisibility() {
	var nrOfHomonyms = $(".homonym-item").length;
	if (nrOfHomonyms === 1) {
		$('.word-details-homonym-nr').addClass('d-none');
	}
}

function fetchCorpSentences(lang, sentence) {
	var corpDiv = $("#korp");
	var corpSentencesUrl = applicationUrl + 'korp/' + lang + '/' + encodeURIComponent(sentence);
	$.get(corpSentencesUrl).done(function(data) {
		corpDiv.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
	})
}

$(function() {
	$('[data-toggle="tooltip"]').tooltip({
		container : 'body'
	});
});

// $(document).on("click", "a", function() {
// 	if ($("a").attr("aria-expanded",'false')) {
// 		$(this).removeAttr("aria-expanded",'false');
// 		$(this).attr("aria-expanded",'true');
// 	} else {
// 		$(this).removeAttr("aria-expanded",'true');
// 		$(this).attr("aria-expanded",'false');
// 	}
// });


$(document).on("click", ".menu-btn", function() {
      if($(this).attr('aria-expanded')==='false'){
           //alert("true");
           $(this).attr('aria-expanded','false');
       }
       else{
            $(this).attr('aria-expanded','true');           
       }
});

$(document).on("click", ".more-btn", function() {
      if($(this).attr('aria-expanded')==='false'){
           //alert("true");
           $(this).attr('aria-expanded','true');
       }
       else{
            $(this).attr('aria-expanded','false');           
       }
});


$(document).on("click", ".more-btn", function() {
	$(this).parents(".word-relations, .dependencies, .collocations-section, .position-relative, .corp-panel").toggleClass("expand");

	$(this).parents(".word-relations, .meaning-panel, .dependencies, .collocations-section, .position-relative, .corp-panel")
			.find(".colloc-col, .dependence, .label, .label-md, .corp-panel div:nth-child(n+5), .colloc-heading, .colloc-name, .lexeme-list b, .secondary-morph, .word-options, .sentence-wrapper")
			.toggleClass("fade-target");

	$(this).parents(".word-relations, .meaning-panel, .dependencies, .collocations-section, .position-relative, .corp-panel").find(
			".colloc-fulldata .colloc-col:lt(3), .dependencies .dependence:lt(3), .full-group .word-options:lt(10), .sentence-wrapper:lt(2)")
			.removeClass("fade-target");
});

$(window).on("popstate", function(e) {
	e.preventDefault();
	var historyState = e.originalEvent.state;
	if (historyState != null) {
		window.location = historyState.wordSelectUrl;
	}
});

$(document).on("click", "a[id^='word-details-link']", function() {
	var wordWrapperForm = $(this).closest("form");
	var wordId = wordWrapperForm.children("[name='word-id']").val();
	var word = wordWrapperForm.children("[name='word-value']").val();
	var wordSelectUrl = wordWrapperForm.children("[name='word-select-url']").val();
	fetchDetails(wordId, word, wordSelectUrl);
});

$(document).on("click", "button[name='colloc-usages-btn']", function() {
	$(this).closest("[id^='collocs-area']").find("[id^='colloc-usages-area']").fadeToggle();
});

// demo js for interactions between the mobile and desktop modes
$(document).on("click", ".back", function() {
	if ($(".homonym-panel").hasClass("d-none")) {
		$(".content-panel").addClass("d-none d-md-block");
		$(".homonym-panel").removeClass("d-none d-md-block");
		$(".search-panel").removeClass("d-none d-md-block");
		$('#form-words').css("margin-top", '0');
		calculateAndSetStyles();
	}
});

$(document).on("click", ".homonym-item", function() {
	$(".homonym-item").removeClass("selected last-selected");
	$(".homonym-item:first").removeClass("animation-target").dequeue();
	$(this).addClass("selected last-selected");
	calculateAndSetStyles();
	var homonymList = $('.homonym-list');
	if ($(window).width() >= windowWidthTreshold) {
		homonymList.animate({
			scrollLeft : $('.homonym-item.selected .homonym-item-wrap').parent().position().left - $('.search-panel').offset().left + homonymList.scrollLeft()
		}, 200);
	}
	if ($(window).width() >= windowWidthTreshold) {
		homonymList.animate({
			scrollLeft : $('.homonym-item.selected .homonym-item-wrap').parent().position().left - $('.search-panel').offset().left + homonymList.scrollLeft()
		}, 200);
	} else {
		$(".homonym-panel").addClass("d-none");
		$(".search-panel").addClass("d-none");
		$('#form-words').css("margin-top", '5em');
	}
});

$(document).on("click", "[name='word-form-btn']", function() {
	var word = $(this).data('word');
	$("input[name = 'searchWord']").val(word);
	$('#search-btn').trigger('click');
});










