$.fn.actionDropdownHandler = function () {
  $(this).each(function () {
    const instance = new ActionDropdown($(this));
    instance.init();
  });
};

class ActionDropdown {
  static triggerClass = "action-dropdown__trigger";
  static menuClass = "action-dropdown__menu";
  static clickOutsideInitialized = false;
  static popperInstance;

  constructor(container) {
    this.container = container;
    this.trigger = container.find(`.${ActionDropdown.triggerClass}`);
    this.menu = container.find(`.${ActionDropdown.menuClass}`);

    if (!this.trigger.length) {
      console.error(
        "Could not find trigger for action dropdown: ",
        this.container
      );
    }
    if (!this.menu.length) {
      console.error(
        "Could not find menu for action dropdown: ",
        this.container
      );
    }
  }

  init() {
    this.bindDropdown();
  }

  bindDropdown() {
    this.trigger.on("show.bs.dropdown", () => {
      // Append to body because parent could have overflow clipped
      this.menu
        .appendTo("body")
        .addClass(`${ActionDropdown.menuClass}--visible`);
      // This relies on popper being available, currently being added to window via bootstrap bundle
      ActionDropdown.popperInstance = new Popper(
        this.trigger.get(0),
        this.menu.get(0),
        {
          placement: "bottom-end",
          modifiers: {
            preventOverflow: {
              boundariesElement: "viewport",
            },
          },
        }
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
        .removeClass(`${ActionDropdown.menuClass}--visible`)
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
        if (!e.target.classList.contains(ActionDropdown.triggerClass)) {
          this.closeMenu();
        }
      });
      ActionDropdown.clickOutsideInitialized = true;
    }
  }

  closeMenu() {
    $(`.${ActionDropdown.triggerClass}`).trigger("hide.bs.dropdown");
  }
}
