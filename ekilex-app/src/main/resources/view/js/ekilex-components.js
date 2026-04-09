$.fn.actionDropdownHandler = function () {
  $(this).each(function () {
    const instance = new ActionDropdown($(this));
    instance.init();
  });
};

class ActionDropdown {
  triggerClass = "action-dropdown__trigger";
  menuClass = "action-dropdown__menu";
  static clickOutsideInitialized = false;
  static popperInstance;
  static defaultOptions = {
    triggerClass: "action-dropdown__trigger",
    menuClass: "action-dropdown__menu",
    placement: "bottom-end",
  };

  constructor(container, options = {}) {
    this.container = container;
    this.options = { ...ActionDropdown.defaultOptions, ...options };
    this.triggerClass = this.options.triggerClass;
    this.menuClass = this.options.menuClass;
    this.trigger = container.find(`.${this.triggerClass}`);
    this.menu = container.find(`.${this.menuClass}`);

    if (!this.trigger.length) {
      console.error(
        "Could not find trigger for action dropdown: ",
        this.container,
      );
    }
    if (!this.menu.length) {
      console.error(
        "Could not find menu for action dropdown: ",
        this.container,
      );
    }
  }

  init() {
    this.bindDropdown();
  }

  bindDropdown() {
    this.trigger.on("show.bs.dropdown", () => {
      // Append to body because parent could have overflow clipped
      this.menu.appendTo("body").addClass(`${this.menuClass}--visible`);
      // This relies on popper being available, currently being added to window via bootstrap bundle
      ActionDropdown.popperInstance = new Popper(
        this.trigger.get(0),
        this.menu.get(0),
        {
          placement: this.options.placement,
          modifiers: {
            preventOverflow: {
              boundariesElement: "viewport",
            },
          },
        },
      );
      this.trigger.attr("aria-expanded", true);
      requestAnimationFrame(() => {
        const firstChild = this.menu.children().first();
        if (firstChild) {
          firstChild.focus();
        }
      });
    });

    this.trigger.on("hide.bs.dropdown", () => {
      if (!ActionDropdown.popperInstance) {
        return;
      }
      const menu = ActionDropdown.popperInstance.popper;
      const trigger = ActionDropdown.popperInstance.reference;
      ActionDropdown.popperInstance.destroy();
      ActionDropdown.popperInstance = null;

      // Move menu back on close
      $(menu)
        .removeClass(`${this.menuClass}--visible`)
        .insertAfter(this.trigger);
      $(trigger).attr("aria-expanded", false);
    });

    this.trigger.on("click", () => {
      let openNew = false;
      if (this.trigger.attr("aria-expanded") === "false") {
        openNew = true;
      }
      if (ActionDropdown.popperInstance) {
        this.closeMenu();
      }
      if (openNew) {
        requestAnimationFrame(() => {
          this.trigger.trigger("show.bs.dropdown");
        });
      }
    });

    if (!ActionDropdown.clickOutsideInitialized) {
      $(document).on("click", (e) => {
        if (!e.target.classList.contains(this.triggerClass)) {
          this.closeMenu();
        }
      });
      ActionDropdown.clickOutsideInitialized = true;
    }
  }

  closeMenu() {
    $(`.${this.triggerClass}`).trigger("hide.bs.dropdown");
  }
}

/**
 * Generic segmented switch: toggles [data-segment-panel] visibility
 * based on radio [data-segment-target] selection within a [data-segment-container].
 *
 * Triggers 'segment:change' event on the container with the selected value.
 * Returns { updatePanels } for programmatic control.
 */
function initSegmentedSwitch(context) {
  const container = $(context).is("[data-segment-container]")
    ? $(context)
    : $(context).find("[data-segment-container]");

  if (!container.length) {
    return null;
  }

  const radios = container.find("[data-segment-target]");

  function updatePanels() {
    const selected = container.find("[data-segment-target]:checked");
    const targetValue = selected.attr("data-segment-target");

    container.find("[data-segment-panel]").each(function () {
      const panel = $(this);
      const panelValue = panel.attr("data-segment-panel");
      // Hide panels that don't match and disable their inputs
      if (panelValue === targetValue) {
        panel.removeAttr("hidden").attr("aria-hidden", "false");
        panel
          .find("input, select, textarea")
          .not('[type="file"]')
          .prop("disabled", false);
      } else {
        panel.attr("hidden", "").attr("aria-hidden", "true");
        panel
          .find("input, select, textarea")
          .not('[type="file"]')
          .prop("disabled", true);
      }
    });

    container.trigger("segment:change", [targetValue]);
  }

  radios.off("change.segmented").on("change.segmented", function () {
    updatePanels();
  });

  container
    .off("keydown.segmented")
    .on("keydown.segmented", "[data-segment-target]", function (e) {
      const index = radios.index(this);
      let nextIndex = -1;
      if (e.key === "ArrowRight" || e.key === "ArrowDown") {
        nextIndex = (index + 1) % radios.length;
      } else if (e.key === "ArrowLeft" || e.key === "ArrowUp") {
        nextIndex = (index - 1 + radios.length) % radios.length;
      }
      if (nextIndex >= 0) {
        e.preventDefault();
        radios.eq(nextIndex).prop("checked", true).trigger("change").focus();
      }
    });

  updatePanels();

  return { updatePanels };
}

/**
 * Searchable select: combobox with text filtering and Popper-positioned dropdown
 *
 * Reads options from <li> elements inside the menu
 * Stores the selected value in a hidden input for form
 * Supports keyboard navigation (arrows, enter, escape)
 */
$.fn.searchableSelectHandler = function () {
  $(this).each(function () {
    new SearchableSelect($(this)).init();
  });
};

class SearchableSelect {
  static openInstance = null;

  constructor(container) {
    this.container = container;
    this.hiddenInput = container.find(".searchable-select__value");
    this.control = container.find(".searchable-select__control");
    this.textInput = container.find(".searchable-select__input");
    this.menu = container.find(".searchable-select__menu");
    this.options = this.menu.find(".searchable-select__option");
    this.popperInstance = null;
    this.focusedIndex = -1;
  }

  init() {
    this.textInput.on("input", () => {
      this.filter(this.textInput.val());
      if (!this.isOpen()) {
        this.open();
      }
      // Clear selection when user modifies text
      if (this.hiddenInput.val()) {
        const currentLabel = this.options
          .filter(`[data-value="${this.hiddenInput.val()}"]`)
          .text();
        if (this.textInput.val() !== currentLabel) {
          this.hiddenInput.val("");
          this.options.attr("aria-selected", "false");
        }
      }
    });

    this.textInput.on("focus", () => {
      this.open();
    });

    this.textInput.on("keydown", (e) => {
      this.handleKeydown(e);
    });

    this.menu.on("click", ".searchable-select__option", (e) => {
      this.select($(e.currentTarget));
    });

    $(document).on("click", (e) => {
      if (
        !this.container.get(0).contains(e.target) &&
        !this.menu.get(0).contains(e.target)
      ) {
        this.close();
      }
    });

    // Restore display text if a value is pre-selected
    const preselected = this.hiddenInput.val();
    if (preselected) {
      const opt = this.options.filter(`[data-value="${preselected}"]`);
      if (opt.length) {
        this.textInput.val(opt.text());
        opt.attr("aria-selected", "true");
      }
    }
  }

  isOpen() {
    return this.menu.hasClass("searchable-select__menu--visible");
  }

  open() {
    if (this.isOpen()) {
      return;
    }
    // Close any other open searchable select
    if (
      SearchableSelect.openInstance &&
      SearchableSelect.openInstance !== this
    ) {
      SearchableSelect.openInstance.close();
    }
    SearchableSelect.openInstance = this;

    this.filter(this.textInput.val());
    this.menu.appendTo("body").addClass("searchable-select__menu--visible");
    this.popperInstance = new Popper(this.control.get(0), this.menu.get(0), {
      placement: "bottom-start",
      modifiers: {
        preventOverflow: { boundariesElement: "viewport" },
        setWidth: {
          // Change the order to ensure it runs later than usual
          order: 840,
          enabled: true,
          fn: (data) => {
            data.styles.width = `${data.offsets.reference.width}px`;
            return data;
          },
        },
        offset: {
          // 4px vertical offset
          offset: "0,4",
        },
      },
    });
    this.control.attr("aria-expanded", "true");
    this.focusedIndex = -1;
  }

  close() {
    if (!this.isOpen()) {
      return;
    }
    if (this.popperInstance) {
      this.popperInstance.destroy();
      this.popperInstance = null;
    }
    this.menu
      .removeClass("searchable-select__menu--visible")
      .insertAfter(this.control);
    this.control.attr("aria-expanded", "false");
    this.clearFocus();

    if (SearchableSelect.openInstance === this) {
      SearchableSelect.openInstance = null;
    }

    // If no valid selection, clear the text
    if (!this.hiddenInput.val()) {
      this.textInput.val("");
    }
  }

  filter(query = "") {
    const lowercaseQuery = query.toLowerCase().trim();
    let hasVisibleOptions = false;
    this.options.each(function () {
      const text = this.textContent.toLowerCase();
      if (!lowercaseQuery || text.includes(lowercaseQuery)) {
        this.removeAttribute("hidden");
        hasVisibleOptions = true;
      } else {
        this.setAttribute("hidden", "");
      }
    });
    // Show/hide no-results message
    const noResults = this.menu.find(".searchable-select__no-results");
    if (!hasVisibleOptions && lowercaseQuery) {
      noResults?.show();
      noResults?.removeAttr("hidden");
    } else {
      noResults?.hide();
      noResults?.attr("hidden", "");
    }
    this.focusedIndex = -1;
    this.clearFocus();
  }

  select(option) {
    const value = option.attr("data-value");
    const label = option.text();
    this.hiddenInput.val(value).trigger("change");
    this.textInput.val(label);
    this.options.attr("aria-selected", "false");
    option.attr("aria-selected", "true");
    this.control.removeClass("is-invalid");
    this.close();
  }

  handleKeydown(e) {
    const visibleOptions = this.options.filter(":not([hidden])");
    if (!visibleOptions.length && e.key !== "Escape" && e.key !== "Tab") {
      return;
    }

    switch (e.key) {
      case "ArrowDown":
        e.preventDefault();
        if (!this.isOpen()) {
          this.open();
        }
        this.moveFocus(visibleOptions, 1);
        break;
      case "ArrowUp":
        e.preventDefault();
        if (!this.isOpen()) {
          this.open();
        }
        this.moveFocus(visibleOptions, -1);
        break;
      case "Enter":
        e.preventDefault();
        if (
          this.focusedIndex >= 0 &&
          this.focusedIndex < visibleOptions.length
        ) {
          this.select(visibleOptions.eq(this.focusedIndex));
        }
        break;
      case "Escape":
        e.preventDefault();
        this.close();
        break;
      case "Tab":
        this.close();
        break;
    }
  }

  moveFocus(visible, direction) {
    this.focusedIndex += direction;
    if (this.focusedIndex < 0) {
      this.focusedIndex = visible.length - 1;
    }
    if (this.focusedIndex >= visible.length) {
      this.focusedIndex = 0;
    }
    this.clearFocus();
    const focused = visible.eq(this.focusedIndex);
    focused.addClass("searchable-select__option--focused");
    this.textInput.attr("aria-activedescendant", focused.attr("id") || "");
    // Scroll into view
    const menu = this.menu.get(0);
    const option = focused.get(0);
    if (option && menu) {
      if (option.offsetTop < menu.scrollTop) {
        menu.scrollTop = option.offsetTop;
      } else if (
        option.offsetTop + option.offsetHeight >
        menu.scrollTop + menu.clientHeight
      ) {
        menu.scrollTop =
          option.offsetTop + option.offsetHeight - menu.clientHeight;
      }
    }
  }

  clearFocus() {
    this.options.removeClass("searchable-select__option--focused");
    this.textInput.removeAttr("aria-activedescendant");
  }
}

/**
 * Generic media input: triggers file dialog for [data-media-input] containers.
 *
 * Clicking [data-media-btn] opens the [data-media-file] file picker.
 * Returns { fileInput, setFileName(name), reset() }.
 */
function initMediaInput(context) {
  const container = $(context).is("[data-media-input]")
    ? $(context)
    : $(context).find("[data-media-input]");

  if (!container.length) {
    return null;
  }

  const btn = container.find("[data-media-btn]");
  const fileInput = container.find("[data-media-file]");
  const nameDisplay = container.find("[data-media-name]");

  btn.off("click.media").on("click.media", function () {
    fileInput[0].click();
  });

  return {
    fileInput: fileInput,
    setFileName: function (name) {
      nameDisplay.text(name || "");
    },
    reset: function () {
      fileInput.val("");
      nameDisplay.text("");
    },
  };
}
