class ReadMore {
  constructor(element) {
    this.main = element;
    this.parent = element.parent();
    this.container = element.children('*:first');
    this.status = false;
    this.debounce;
    this.debounceTime = 300;
  }

  checkHeights() {
    if (this.status === 1) { return false; }
    if (this.main.height() < this.container.height()) {
      this.handle.show();
    } else {
      this.handle.hide();
    }
  }

  appendDots() {
    this.parent.append(this.handle = $('<div class="indicator"><i class="fa fa-ellipsis-h"></i></div>'));
  }

  bindEvents() {
    this.handle.on('click', (e) => {
      e.preventDefault();
      this.status = !this.status;
      this.detectChange();
    });
    $(window).on('resize', () => {
      clearTimeout(this.debounce);
      this.debounce = setTimeout(function() {
        this.checkHeights();
      }, this.debounceTime);
    });
  }

  detectChange() {
    if (this.status) {
      this.main.css('height', 'auto');
      this.handle.html('<i class="fa fa-angle-up"></i>Näita vähem');
    } else {
      this.main.removeAttr('style');
      this.handle.html('<i class="fa fa-ellipsis-h"></i>');
    }
  }

  initialize() {
    this.appendDots();
    this.checkHeights();
    this.bindEvents();
  }
}

$.fn.readMore = function() {
  $(this).each(function(){
    const instance = new ReadMore($(this));
    instance.initialize();
  });
}