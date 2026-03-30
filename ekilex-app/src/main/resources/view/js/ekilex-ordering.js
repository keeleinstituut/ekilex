class EkiOrdering {
  constructor(container) {
    this.container = container;
    this.draggedItem = null;
    this.placeholder = null;
    this.oldIndex = null;
    this.originalNextSibling = null;
    this.offsetX = 0;
    this.offsetY = 0;

    this._onPointerMove = this.onPointerMove.bind(this);
    this._onPointerUp = this.onPointerUp.bind(this);
    this._onKeyDown = this.onKeyDown.bind(this);
    this._onContextMenu = this.onContextMenu.bind(this);
  }

  getItems() {
    return this.container.find("[data-ordering-item]");
  }

  getIndexOf(el) {
    return this.getItems().index(el);
  }

  isHorizontal() {
    const items = this.getItems();
    if (items.length < 2) return false;
    const a = items.eq(0).offset();
    const b = items.eq(1).offset();
    return Math.abs(a.top - b.top) < 5;
  }

  getDropTarget(clientX, clientY) {
    const horizontal = this.isHorizontal();
    const placeholder = this.placeholder[0];
    const dragged = this.draggedItem[0];
    let closest = null;
    let closestDist = Infinity;
    let before = true;

    this.getItems().each(function () {
      if (this === placeholder || this === dragged) return;
      const rect = this.getBoundingClientRect();
      const mid = horizontal
        ? rect.left + rect.width / 2
        : rect.top + rect.height / 2;
      const pos = horizontal ? clientX : clientY;
      const dist = Math.abs(pos - mid);

      if (dist < closestDist) {
        closestDist = dist;
        closest = this;
        before = pos < mid;
      }
    });

    return { target: closest, before: before };
  }

  onPointerDown(e) {
    // Don't start drag on right click
    if (e.button !== 0) return;

    const hasHandles = this.container.find("[data-ordering-handle]").length > 0;
    if (hasHandles && !$(e.target).closest("[data-ordering-handle]").length)
      return;

    const item = $(e.target).closest("[data-ordering-item]");
    if (!item.length) return;

    e.preventDefault();

    this.draggedItem = item;
    this.oldIndex = this.getIndexOf(item);
    this.originalNextSibling = item[0].nextElementSibling;

    const rect = item[0].getBoundingClientRect();
    this.offsetX = e.clientX - rect.left;
    this.offsetY = e.clientY - rect.top;

    // Lock child cell widths before lifting (preserves table column sizes)
    item.children().each(function () {
      $(this).css("width", $(this).outerWidth());
    });

    // Placeholder must be the same element type (e.g. <tr> for tables)
    const tag = item.prop("tagName").toLowerCase();
    this.placeholder = $(
      "<" +
        tag +
        " data-ordering-item data-ordering-placeholder></" +
        tag +
        ">",
    );
    this.placeholder.css({
      width: rect.width,
      height: rect.height,
      boxSizing: "border-box",
    });

    // For table rows, add the right number of empty cells so the row takes space
    if (tag === "tr") {
      const colCount = item.children("td, th").length;
      for (let i = 0; i < colCount; i++) {
        this.placeholder.append("<td></td>");
      }
    }

    item.before(this.placeholder);

    // Lift the item out of the flow
    item.attr("data-ordering-dragging", "");
    item.css({
      position: "fixed",
      zIndex: 9999,
      width: rect.width,
      height: rect.height,
      left: rect.left,
      top: rect.top,
      pointerEvents: "none",
      margin: 0,
    });

    document.addEventListener("pointermove", this._onPointerMove);
    document.addEventListener("pointerup", this._onPointerUp);
    document.addEventListener("keydown", this._onKeyDown);
    document.addEventListener("contextmenu", this._onContextMenu);
  }

  onPointerMove(e) {
    if (!this.draggedItem) return;

    this.draggedItem.css({
      left: e.clientX - this.offsetX,
      top: e.clientY - this.offsetY,
    });

    const containerRect = this.container[0].getBoundingClientRect();
    const offsetBuffer = 100;
    if (
      e.clientX < containerRect.left - offsetBuffer ||
      e.clientX > containerRect.right + offsetBuffer ||
      e.clientY < containerRect.top - offsetBuffer ||
      e.clientY > containerRect.bottom + offsetBuffer
    ) {
      this.cancelDrag();
      return;
    }

    const { target, before } = this.getDropTarget(e.clientX, e.clientY);
    if (!target || target === this.placeholder[0]) return;

    // Clear previous over state and mark new target
    this.getItems().not(this.placeholder).removeAttr("data-ordering-over");
    $(target).attr("data-ordering-over", "");

    if (before) {
      this.placeholder.insertBefore(target);
    } else {
      this.placeholder.insertAfter(target);
    }
  }

  removeListeners() {
    document.removeEventListener("pointermove", this._onPointerMove);
    document.removeEventListener("pointerup", this._onPointerUp);
    document.removeEventListener("keydown", this._onKeyDown);
    document.removeEventListener("contextmenu", this._onContextMenu);
  }

  resetDraggedItemStyles() {
    this.draggedItem.removeAttr("data-ordering-dragging style");
    this.draggedItem.children().css("width", "");
    this.getItems().removeAttr("data-ordering-over");
  }

  clearState() {
    this.draggedItem = null;
    this.placeholder = null;
    this.oldIndex = null;
    this.originalNextSibling = null;
  }

  onKeyDown(e) {
    if (e.key === "Escape") {
      this.cancelDrag();
    }
  }

  onContextMenu(e) {
    e.preventDefault();
    this.cancelDrag();
  }

  cancelDrag() {
    if (!this.draggedItem) return;

    this.removeListeners();
    this.placeholder.remove();
    this.resetDraggedItemStyles();

    // Restore to original position
    if (this.originalNextSibling) {
      $(this.originalNextSibling).before(this.draggedItem);
    } else {
      this.container.append(this.draggedItem);
    }

    this.clearState();
  }

  onPointerUp() {
    if (!this.draggedItem) return;

    this.removeListeners();

    // Put the item back where the placeholder is
    this.placeholder.replaceWith(this.draggedItem);
    this.resetDraggedItemStyles();

    const newIndex = this.getIndexOf(this.draggedItem);
    if (this.oldIndex !== newIndex) {
      this.container.trigger("ordering:change", [
        {
          item: this.draggedItem,
          oldIndex: this.oldIndex,
          newIndex: newIndex,
        },
      ]);
    }

    this.clearState();
  }

  bindEvents() {
    this.container.on(
      "pointerdown.ekiOrdering",
      "[data-ordering-item]",
      (e) => {
        this.onPointerDown(e);
      },
    );
  }

  initialize() {
    this.bindEvents();
  }
}

$.fn.ekiOrdering = function () {
  $(this).each(function () {
    const instance = new EkiOrdering($(this));
    instance.initialize();
  });
};
