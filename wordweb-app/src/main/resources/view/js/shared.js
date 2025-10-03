const windowWidthTreshold = 768;

$(document).ready(function () {
  initHomonymList();
});

function initHomonymList() {
  const selectedHomonymItem = getSelectedHomonym();
  if (document.getElementsByClassName("os-results").length === 0) {
    selectedHomonymItem
      .delay(500)
      .queue(function () {})
      .trigger("click");
    selectedHomonymItem.addClass("animation-target");
  }

  setSelectedHomonymValueForMobile(selectedHomonymItem.html());
}

function getSelectedHomonym() {
  let selectedHomonymItem = $(".homonym-item")
    .filter(function () {
      const isHomonymSelected = $(this)
        .closest("form")
        .find("input[name='word-selected']")
        .val();
      return isHomonymSelected === "true";
    })
    .filter(":first");
  if (selectedHomonymItem.get().length == 0) {
    selectedHomonymItem = $(".homonym-item:first");
  }
  return selectedHomonymItem;
}

function setSelectedHomonymValueForMobile(inputHTML) {
  const isMultiHomonym = $(".homonym-item").length > 1;
  if (isMultiHomonym) {
    $("#homonymListToggleButton").html(inputHTML);
  }
}

$(document).on("click", "#homonymListToggleButton", function () {
  $(".homonym-list").toggleClass("expand");
});

$(document).on("click", ".homonym-item", function () {
  $(".homonym-list-item").removeClass("selected last-selected");
  $(".homonym-item:first").removeClass("animation-target").dequeue();
  $(this).parents(".homonym-list-item").addClass("selected last-selected");
  const homonymList = $(".homonym-list");
  if ($(window).width() >= windowWidthTreshold) {
    const bodyParentPositionLeft = $(
      ".homonym-list-item.selected .homonym__body"
    )
      .parent()
      .position()?.left;
    if (!bodyParentPositionLeft) {
      return;
    }
    homonymList.animate(
      {
        scrollLeft:
          bodyParentPositionLeft -
          $("#word-details, .os-results").offset().left +
          homonymList.scrollLeft(),
      },
      200
    );
  }
  setSelectedHomonymValueForMobile($(this).html());
  $(".homonym-list").removeClass("expand");
});


function announceForScreenReader(text) {
  const announcer = document.getElementById('aria-live-announcer');
  if (!announcer) {
    console.error('Attempted to announce for screen reader but could not find announcer element');
    return;
  }
  announcer.textContent = text;
}
