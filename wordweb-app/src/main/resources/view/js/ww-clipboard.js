let clipboardTimeout;

function clipboardCloseCallback(event) {
  if (!event.target.closest(".clipboard") && this.destroy) {
    this.destroy();
    document.removeEventListener("click", clipboardCloseCallback);
    clearTimeout(clipboardTimeout);
  }
}

$(document).on("click", ".clipboard__trigger", function () {
  const button = this;
  const popup = this.closest(".clipboard").querySelector(".clipboard__popup");
  if (!popup) {
    return;
  }
  const popperInstance = new Popper(button, popup, {
    placement: "bottom",
    modifiers: [{ name: "offset", options: { offset: [24, 24] } }],
  });
  clearTimeout(clipboardTimeout);
  const announcePrefix = popup.getAttribute("data-announce-prefix") ?? "";
  const clipboardText = popup.getAttribute("data-clipboard-text");
  try {
    navigator.clipboard.writeText(clipboardText);
  } catch (err) {
    console.error("Failed to copy: ", err);
  }
  if (window.announceForScreenReader) {
		window.announceForScreenReader(announcePrefix + clipboardText);
	}
  clipboardTimeout = setTimeout(() => {
    popperInstance.destroy();
    document.removeEventListener("click", clipboardCloseCallback);
  }, 5000);
  document.addEventListener(
    "click",
    clipboardCloseCallback.bind(popperInstance)
  );
});
