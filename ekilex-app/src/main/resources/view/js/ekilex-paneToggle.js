class paneToggle {
  constructor(button) {
    this.button = button;
    this.hideable = button.attr('data-hideable');
    this.extendable = button.attr('data-extendable');
    this.storageKey = `paneToggle-${this.hideable}`;
    this.hideableColumn = $(`#${this.hideable}:first`);
    this.extendableColumn = $(`#${this.extendable}:first`);

    this.classes = {};

    if (this.hideableColumn.length) {
      // Regex matches case insensitive classes like: col-number with number being up to 2 digits
      const hideableColumnCols = this.hideableColumn.attr('class').match(/col-\d{1,2}/igm)[0];
      const extendableColumnCols = this.extendableColumn.attr('class').match(/col-\d{1,2}/igm)[0];
      
      let extendedClass = parseInt(extendableColumnCols.replace('col-', '')) + parseInt(hideableColumnCols.replace('col-', ''));
      extendedClass = `col-${extendedClass}`;
      this.classes.extended = extendedClass;
      this.classes.hidden = hideableColumnCols;
    }

  }

  getStatus() {
    return Cookies.get(this.storageKey) || 'active';
  }

  toggleStatus() {
    const status = this.getStatus() === 'active' ? 'deactive' : 'active';
    console.log(status);
    Cookies.set(this.storageKey, status, 365);
  }

  handle() {
    if (this.getStatus() === 'active') {
      this.hideableColumn.show();
      this.extendableColumn.removeClass(this.classes.extended);
      this.extendableColumn.addClass(this.classes.hidden);
      this.button.addClass('active');
    } else {
      this.hideableColumn.hide();
      this.extendableColumn.removeClass(this.classes.hidden);
      this.extendableColumn.addClass(this.classes.extended);
      this.button.removeClass('active');
    }
  }

  bindEvents() {
    this.button.on('click', (e) => {
      e.preventDefault();
      this.toggleStatus();
      this.handle();
    });
  }

  initialize() {
    this.bindEvents();
    this.handle();
  }
}

$.fn.paneToggle = function() {
  $(this).each(function(){
    const instance = new paneToggle($(this));
    instance.initialize();
  });
}