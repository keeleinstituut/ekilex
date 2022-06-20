class MultiSelect {
  constructor(element) {
    this.main = element;
    this.id = Math.random().toString().substr(2);
    this.list = [];
    this.contextMenuItems = {
      'data-contextmenu:multiDelete': 'Kustuta',
    };
  }

  getElements() {
    this.elements = this.main.find('[id*="lexeme-details-"]');
    this.elements.each(function() {
      const element = $(this);
      const id = element.attr('id').split('-');
      element.attr('data-id', id[id.length-1]);
    });
  }

  bindEvents() {
    this.elements.on(`mousedown.multiselect-${this.id}`, (e) => {
      this.onClick(e);
    });

    this.elements.on(`contextmenu.multiselect-${this.id}`, (e) => {
      this.onRightClick(e);
    });
  }

  onClick(e) {
    if (e.shiftKey) {
      e.preventDefault();
      e.stopImmediatePropagation();
      this.selectElement(e);
    }
  }

  onRightClick(e) {
    const element = $(e.currentTarget);
    if (element.is('.multiselect-active')) {
      e.preventDefault();
      e.stopImmediatePropagation();
      this.contextMenu.cursorPosition = {
        x: e.originalEvent.pageX,
        y: e.originalEvent.pageY,
      };
      this.contextMenu.showMenu();

    }
  }

  selectElement(e) {
    const element = $(e.currentTarget);
    element.toggleClass('multiselect-active');
    element.is('.multiselect-active') ? this.addToList(e) : this.removeFromList(e);
    this.updateAttribute();
  }

  updateAttribute() {
    this.main.attr({
      'data-multiSelectValues': JSON.stringify(this.list),
    });
  }

  addToList(e) {
    this.list.push($(e.currentTarget).attr('data-id'));
  }

  removeFromList(e) {
    const id = $(e.currentTarget).attr('data-id');
    const index = this.list.indexOf(id);
    this.list.splice(index, 1);
  }
  
  bindContextMenu() {
    this.main.attr(this.contextMenuItems);

    this.contextMenu = new ContextMenu(this.main);

  }

  initialize() {
    this.getElements();
    this.bindEvents();
    this.bindContextMenu();
    $(window).on('update:multiSelect', () => {
      console.log(this.main);
      this.getElements();
      this.bindEvents();
    });
  }
}

$.fn.multiSelect = function() {
  $(this).each(function(){
    const instance = new MultiSelect($(this));
    instance.initialize();
  });
}