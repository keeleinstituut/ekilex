$.fn.publishingHandler = function () {
  $(this).each(function () {
    const instance = new PublishingHandler($(this));
    instance.init();
  });
};

class PublishingHandler {
  static triggerSelector = '[data-toggle="publishing-menu"]';
  static menuClass = "publishing__menu";
  static targetConstantsMap = {
    public: "is_public",
    ww: "ww_unif",
    lite: "ww_lite",
    od: "ww_od",
    ww_unif: "ww",
    ww_lite: "lite",
    ww_od: "od",
  };
  container;
  trigger;
  menu;
  lastMenuItem;
  entityName;
  id;

  constructor(container) {
    this.container = container;
    this.trigger = container.find(PublishingHandler.triggerSelector);
    this.menu = container.find(`.${PublishingHandler.menuClass}`);
    this.entityName = container.attr("data-entity-name");
    this.id = container.attr("data-entity-id");
    if (!this.trigger.length) {
      console.error("Could not find trigger for publishing: ", this.container);
    }
    if (!this.menu.length) {
      console.error("Could not find menu for publishing: ", this.container);
    }
    if (!this.entityName || !this.id) {
      console.error(
        "Could not find an entity name for publishing: ",
        this.container
      );
    }

    if (!this.id) {
      console.error("Could not find an id for publishing: ", this.container);
    }
  }

  init() {
    this.bindMenuListeners();
  }

  bindMenuListeners() {
    let popperInstance;
    this.trigger.on("show.bs.dropdown", () => {
      // Append to body because parent could have overflow clipped
      this.menu
        .appendTo("body")
        .addClass(`${PublishingHandler.menuClass}--visible`);
      // This relies on popper being available, currently being added to window via bootstrap bundle
      popperInstance = new Popper(this.trigger.get(0), this.menu.get(0), {
        placement: "bottom-end",
        modifiers: {
          preventOverflow: {
            boundariesElement: "viewport",
          },
        },
      });
      this.trigger.attr("aria-expanded", true);
      requestAnimationFrame(() => {
        const menuChildren = this.menu.children();
        menuChildren.get(0).focus();
      });
    });

    this.trigger.on("hide.bs.dropdown", () => {
      if (!popperInstance) {
        return;
      }
      popperInstance.destroy();
      popperInstance = null;

      // Move menu back on close
      this.menu
        .removeClass(`${PublishingHandler.menuClass}--visible`)
        .insertAfter(this.trigger);
      this.trigger.attr("aria-expanded", false);
    });

    this.trigger.on("click", () => {
      if (popperInstance) {
        this.closeMenu();
      } else {
        this.trigger.trigger("show.bs.dropdown");
      }
    });

    $(document).on("click", (e) => {
      if (
        e.target !== this.trigger.get(0) &&
        !e.target.closest(`.${PublishingHandler.menuClass}`)
      ) {
        this.closeMenu();
      }
    });

    this.container.on("click", this.handleItemClick.bind(this));
    this.menu.children().on("click", this.handleItemClick.bind(this));
  }

  handleItemClick(e) {
    const targetName = e.target.dataset?.publishingItem;
    const currentValue = e.target.dataset?.publishingItemActive;
    // Icon buttons always turn off, menu buttons toggle
    const newValue = e.target.classList.contains("publishing__button--icon")
      ? false
      : currentValue !== "true";
    if (targetName && currentValue !== undefined) {
      this.setTarget(
        PublishingHandler.targetConstantsMap[targetName],
        newValue
      );
    }
  }

  closeMenu() {
    this.trigger.trigger("hide.bs.dropdown");
  }

  setTarget(targetName, value) {
    if (!this.entityName || !this.id) {
      return;
    }
    const url = `${applicationUrl}publish_item`;
    $.ajax({
      url,
      data: JSON.stringify({
        targetName,
        entityName: this.entityName,
        id: this.id,
        value,
      }),
      method: "POST",
      contentType: "application/json",
    }).done(() => {
      this.closeMenu();
      // Since closing menu involves literally moving the element, push state changes to the back
      requestAnimationFrame(() => {
        this.container
          .find(
            `[data-publishing-item='${PublishingHandler.targetConstantsMap[targetName]}']`
          )
          .attr("data-publishing-item-active", value);
      });
    });
  }
}
