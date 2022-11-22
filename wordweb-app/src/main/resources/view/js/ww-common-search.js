$(document).ready(function() {

  var selectedHomonymItem = getSelectedHomonym();
  selectedHomonymItem.delay(500).queue(function() { }).trigger('click');
  selectedHomonymItem.addClass("animation-target");
  setSelectedHomonymValueForMobile(getSelectedHomonym().html());

  var searchWordAutocompleteMenuRenderer = function(ul, items) {
    var self = this;
    var groups = [];

    groups = $.map(items, function(item) {
      return item.group;
    });

    groups = $.grep(groups, function(el, index) {
      return index === $.inArray(el, groups);
    });

    ul.addClass("list-group");
    $.each(groups, function(index, group) {
      if (group == "formWord") {
        var li = $("<li>");
        li.addClass("list-group-item list-group-item-info");
        li.text(messages.this_is_form);
        ul.append(li);
      }
      $.each(items, function(index, item) {
        if (item.group == group) {
          self._renderItemData(ul, item);
        }
      });
    });
  };

  var searchWordAutocompleteConfig = {
    source: function(request, response) {
      var wordFrag = request.term;
      var searchWordFragUrlWithParams = searchWordFragUrl + "/" + wordFrag;
      $.ajax({
        url: searchWordFragUrlWithParams,
        type: "GET",
        success: function(data) {
          var fullList = [];
          $.each(data.prefWords, function(index, item) {
            fullList.push({
              group: "prefWord",
              label: item,
              value: item
            });
          });
          $.each(data.formWords, function(index, item) {
            fullList.push({
              group: "formWord",
              label: item,
              value: item
            });
          });
          response(fullList);
        }
      });
    },
    minLength: 3,
    create: function() {
      $(this).data('uiAutocomplete')._renderMenu = searchWordAutocompleteMenuRenderer;
    },
    select: function(event, ui) {
      if (ui.item) {
        $("input[name='searchWord']").val(ui.item.value);
        $("#search-btn").click();
      }
      return false;
    },
    open: function() {
      return false;
    },
    close: function() {
      return false;
    }
  };

  $("input[name='searchWord']").autocomplete(searchWordAutocompleteConfig).autocomplete("instance");
});

$(document).on("click", "#clear-search-btn", function(e) {
  $("input[name='searchWord']").val('').trigger('change');
});

$(document).on("click", "eki-link", function(e) {
  const link = $(this);
  const linkType = link.attr("data-link-type");
  const linkId = link.attr("data-link-id");
  const searchLinkUrlWithParams = `${searchLinkUrl}/${linkType}/${linkId}`;
  $.get(searchLinkUrlWithParams).done(function(data) {
    if (data) {
      const searchForm = $('#hiddenSearchForm');
      searchForm.find("input[name='searchWord']").val(data.word);
      searchForm.find("input[name='selectedWordHomonymNr']").val(data.homonymNr);
      searchForm.find("input[name='linkedLexemeId']").val(data.lexemeId);
      searchForm.submit();
    } else {
      //handle incorrect link?
    }
  });
});

$(document).on('click', 'ext-link', function() {
	const link = $(this);
	const href = link.attr('href');
	if (href) {
		const target = link.attr('target');
		// Perhaps a good idea to add a regex check for valid url?
		if (href.startsWith('https://')) {
			window.open(href, target);
		} else {
			window.open(`https://${href}`, target);
		}
	} else  {
		openAlertDlg(messages["common.broken.link"]);
	}
});

$(document).on("keyup change", "input[name='searchWord']", function(e) {
  if ($(this).val()) {
    $("#clear-search-btn").show();
  } else {
    $("#clear-search-btn").hide(1000); //hack to avoid disabling the button
  }
});

$(document).on("click", "a[id^='destin-lang-']", function(e) {
  var destinLangAll = "dlall";
  var destinLang = $(this).attr("data-filter-code");
  if (destinLang == destinLangAll) {
    $("a[id^='destin-lang-']").removeClass("active");
    $(this).addClass("active");
  } else {
    if ($(this).hasClass("active")) {
      $(this).removeClass("active");
      if ($("a[id^='destin-lang-']").hasClass("active") == false) {
        $("a[id^='destin-lang-" + destinLangAll + "']").addClass("active");
      }
    } else {
      $("a[id^='destin-lang-" + destinLangAll + "']").removeClass("active");
      $(this).addClass("active");
    }
  }
  var destinLangsStr = $("a[id^='destin-lang-'].active").map(function(idx, element) {
    return $(element).attr("data-filter-code");
  }).get();
  var selectedLangs = $("a[id^='destin-lang-'].active").map(function(idx, element) {
    return $(element).text();
  }).get();
  $("input[name='destinLangsStr']").val(destinLangsStr);
  $("#selected-langs").text(selectedLangs);
  setSelectedWordHomonymNr();
  clickSearchIfInputExists();
});

$(document).on("click", "a[id^='dataset-']", function(e) {
  var datasetCodeAll = "dsall";
  var datasetCode = $(this).attr("data-filter-code");
  if (datasetCode == datasetCodeAll) {
    $("a[id^='dataset-']").removeClass("active");
    $(this).addClass("active");
  } else {
    if ($(this).hasClass("active")) {
      $(this).removeClass("active");
      if ($("a[id^='dataset-']").hasClass("active") == false) {
        $("a[id^='dataset-" + datasetCodeAll + "']").addClass("active");
      }
    } else {
      $("a[id^='dataset-" + datasetCodeAll + "']").removeClass("active");
      $(this).addClass("active");
    }
  }
  var datasetCodesStr = $("a[id^='dataset-'].active").map(function(idx, element) {
    return $(element).attr("data-filter-code");
  }).get();
  var selectedDatasetsStr = $("a[id^='dataset-'].active").text();
  var selectedDatasetCount = $("a[id^='dataset-'].active").length;
  if (selectedDatasetCount > 1) {
    selectedDatasetsStr = selectedDatasetCount;
  }
  $("input[name='datasetCodesStr']").val(datasetCodesStr);
  $("#selected-datasets").text(selectedDatasetsStr);
  setSelectedWordHomonymNr();
  clickSearchIfInputExists();
});

function setSelectedWordHomonymNr() {
  var selectedHomonymNr = $("#selected-word-homonym-nr").val();
  $("input[name='selectedWordHomonymNr']").val(selectedHomonymNr);
}

function clickSearchIfInputExists() {
  var tempSearchWord = $("input[name='searchWord']").val();
  if (tempSearchWord) {
    $("#search-btn").click();
  }
}

function getSelectedHomonym() {
  var selectedHomonymItem = $(".homonym-item").filter(function() {
    var isHomonymSelected = $(this).closest("form").find("input[name='word-selected']").val();
    return isHomonymSelected == "true";
  }).filter(":first");
  if (selectedHomonymItem.get().length == 0) {
    selectedHomonymItem = $(".homonym-item:first");
  }
  return selectedHomonymItem;
}

function setSelectedHomonymValueForMobile(inputHTML) {
  var isMultiHomonym = $(".homonym-item").length > 1;
  if (isMultiHomonym) {
    $("#homonymListToggleButton").html(inputHTML);
  }
}

$(document).on("click", "#homonymListToggleButton", function() {
  $(".homonym-list").toggleClass("expand");
});

$(document).on("shown.bs.modal", "[id^='morpho-modal-']", function() {
  var main = $(this);
  var paradigmId = main.attr("data-paradigm-id");
  if (main.find('.modal-dialog').length) {
    main.find('.modal-dialog').remove();
    main.append('<div class="morpho-content"></div>');
  }
  var morphoContentDiv = main.find(".morpho-content");
  var morphoUrlWithParams = morphoUrl + "/" + paradigmId + "/" + currentWordLang;
  $.get(morphoUrlWithParams).done(function(data) {
    morphoContentDiv.replaceWith(data);
    $('[data-toggle="tooltip"]').tooltip({
      container: 'body'
    });
    main.find('[data-plugin="tableTogglers"]').tableTogglers();
    main.find('.scrollable-table').scrollableTable();
  });
});

$.fn.tableTogglers = function() {
  var main = $(this);
  var buttons = main.find('button');
  var parent = main.parents('.modal-content:first');

  function checkStates() {
    var activeButtons = buttons.filter('.active');

    if (buttons.filter('[data-rel="hideColumn"]').length) {
      if (activeButtons.is('[data-rel="hideColumn"]')) {
        parent.find('.tableClone').show();
        parent.find('[data-fixcolumn]').find('tr').find('td:first, th:first').show();
      } else {
        parent.find('.tableClone').hide();
        parent.find('[data-fixcolumn]').find('tr').find('td:first, th:first').hide();
      }
    }

    if (activeButtons.is('[data-rel="marks"]')) {
      parent.find(".form-value-field").each(function(indx, item) {
        $(item).hide();
      });
      parent.find(".form-display-form-field").each(function(indx, item) {
        $(item).show();
      });
    } else {
      parent.find(".form-display-form-field").each(function(indx, item) {
        $(item).hide();
      });
      parent.find(".form-value-field").each(function(indx, item) {
        $(item).show();
      });
    }

    parent.find('.scrollable-table').trigger('scrollableTable:quickUpdate');
  }

  buttons.on('click', function(e) {
    e.preventDefault();
    $(this).toggleClass('active');
    checkStates();
  });
}
