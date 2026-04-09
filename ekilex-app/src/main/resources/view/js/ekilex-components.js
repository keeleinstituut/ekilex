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
