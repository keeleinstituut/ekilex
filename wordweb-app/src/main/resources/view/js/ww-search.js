var windowWidthTreshold = 768;

function fetchDetails(wordId, word, lang, wordSelectUrl) {
    var detailsDiv = $('.word-details');
    var wordDetailsUrlWithParams = wordDetailsUrl + "/" + wordId;
    $.get(wordDetailsUrlWithParams).done(function (data) {
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
        fetchCorpSentences(lang, word);
        setHomonymNrVisibility();
        $('.word-details [data-toggle="tooltip"]').tooltip();
        $('[data-toggle="popover"]').popover({
            placement: 'top',
            trigger: 'focus'
        });
        var lightbox = new SimpleLightbox(
            '.gallery-image',
            {
                history: false,
                captionPosition: 'outside',
                navText: ['<i class="fas fa-arrow-left"></i>', '<i class="fas fa-arrow-right"></i>'],
                closeText: '<i class="fas fa-times"></i>'
            });
        activateCollapseBtn();
        $("#mainContentArea").removeClass("loading");
    }).fail(function (data) {
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

function fetchCorpSentences(lang, word) {
    var corpDiv = $("#korp");
    var corpSentencesUrl = applicationUrl + 'korp/' + lang + '/' + encodeURIComponent(word);
    $.get(corpSentencesUrl).done(function (data) {
        corpDiv.replaceWith(data);
    }).fail(function (data) {
    })
}

$(function () {
    $('[data-toggle="tooltip"]').tooltip({
        container: 'body'
    });
});

$(document).on("click", ".menu-btn", function () {
    if ($(this).attr('aria-expanded') === 'false') {
        $(this).attr('aria-expanded', 'false');
    } else {
        $(this).attr('aria-expanded', 'true');
    }
});
function activateCollapseBtn(){
    $(".btn-collapse").on("click", function () {
        $(this).toggleClass('show');

        if ($(this).data("see-less") && $(this).data("see-more")) {
            let _txt;
            if ($(this).hasClass('show')) {
                _txt = $(this).data("see-less");
            } else {
                _txt = $(this).data("see-more");
            }

            $(this).find('.btn-txt').text(_txt);
        }
        if($(this).data("dynamic-text")){
            $(this).find('.btn-content').toggleClass('d-none');
        }
    });
}

$(document).on("click", ".show-more", function () {

    // button behaviour
    let buttonText = '';
    if ($(this).attr('aria-expanded') === 'false') {
        $(this).attr('aria-expanded', 'true');
        $(this).find("i").removeClass("fa-angle-down").addClass("fa-angle-up");
        buttonText = $(this).data("see-less");
    } else {
        $(this).attr('aria-expanded', 'false');
        $(this).find("i").removeClass("fa-angle-up").addClass("fa-angle-down");
        buttonText = $(this).data("see-more");
    }
    $(this).find("span").text(buttonText);

    if ($(this).data("target") !== undefined) {
        $($(this).data("target")).toggleClass('collapse');
    } else {
        // TODO: Refactor all of this selector mess
        $(this).parents(".dependencies, .collocations-section, .corp-panel")
            .toggleClass("expand");

        $(this).parents(".meaning-panel, .dependencies, .collocations-section, .corp-panel")
            .find(".colloc-col, .label, .label-md, .corp-panel div:nth-child(n+5), .colloc-heading, .colloc-name, .secondary-morph, .word-options, .sentence-wrapper")
            .toggleClass("fade-target");

        $(this).parents(".meaning-panel, .dependencies, .collocations-section, .corp-panel")
            .find(".colloc-fulldata .colloc-col:lt(3), .dependencies .full-group .word-options:lt(10), .sentence-wrapper:lt(2)")
            .removeClass("fade-target");
    }
});

$(document).on("click", "button[name='expand-btn']", function() {
	$(this).parent().find(".collapsable[data-collapse='true']").fadeToggle("slow", "linear");
});

$(window).on("popstate", function (e) {
    e.preventDefault();
    var historyState = e.originalEvent.state;
    if (historyState != null) {
        window.location = historyState.wordSelectUrl;
    }
});

$(document).on("click", "a[id^='word-details-link']", function () {
    var wordWrapperForm = $(this).closest("form");
    var wordId = wordWrapperForm.children("[name='word-id']").val();
    var word = wordWrapperForm.children("[name='word-value']").val();
    var lang = wordWrapperForm.children("[name='word-lang']").val();
    var wordSelectUrl = wordWrapperForm.children("[name='word-select-url']").val();
    fetchDetails(wordId, word, lang, wordSelectUrl);
});

$(document).on("click", ".homonym-item", function () {
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

$(document).on("click", "[name='word-form-btn']", function () {
    var word = $(this).data('word');
    $("input[name = 'searchWord']").val(word);
    $('#search-btn').trigger('click');
});
