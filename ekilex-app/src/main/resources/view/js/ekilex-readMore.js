class ReadMore {
  constructor(element) {
    this.main = element;
    this.parent = element.parent();
    this.container = element.children('*:first');
    this.status = false;
    this.debounce;
    this.debounceTime = 300;
    this.hidden = 0;
  }

  appendDots() {
    const mainHeight = this.main.height();
    this.hidden = 0;
    this.main.find('div:first').children("*").show();
    this.main.find('div:first').children("*").find('.btn-custom').show();

    this.main.find('div:first').children("*").each((index, e) => {
      const obj = $(e);
      if (Math.ceil(obj.position().top) >= mainHeight){
        this.hidden = this.hidden + 1;
        obj.hide();
      }
    });
    this.main.find('div:first').children("*").find('.btn-custom').each((index, e) => {
      const obj = $(e);
      if (Math.ceil(obj.position().top) >= mainHeight){
        this.hidden = this.hidden + 1;
        obj.hide();
      }
    });
    if (this.hidden > 0) {
      const lastVisible = this.main.find('div:first').children("*").filter(':visible').last();
      lastVisible.after(this.handle = $('<div class="indicator"><i class="fa fa-ellipsis-h"></i></div>'));
    }
  }

  appendLabel() {
    this.main.find('div:first').append(this.handle = $('<div class="indicator indicator-with-label"><i class="fa fa-angle-up"></i>Näita vähem</div>'));
  }

  bindHandle() {
    if (this.handle) {
      this.handle.on('click', (e) => {
        e.preventDefault();
        this.status = !this.status;
        this.detectChange();
      });
    }
  }
  bindEvents() {
    this.bindHandle();
    $(window).on('resize', () => {
      clearTimeout(this.debounce);
      this.debounce = setTimeout(function() {
        // this.checkHeights();
      }, this.debounceTime);
    });
  }

  detectChange() {
    if (this.status) {
      this.main.find('div:first').children("*").show();
      this.handle.remove();
      this.handle = false;
      this.main.css('max-height', '99999px');
      this.appendLabel();
      this.bindHandle();
    } else {
      this.handle.remove();
      this.handle = false;
      this.main.removeAttr('style');
      this.appendDots();
      this.bindHandle();
    }
  }

  initialize() {
    this.appendDots();
    this.bindEvents();
  }
}

$.fn.readMore = function() {
  $(this).each(function(){
    const instance = new ReadMore($(this));
    instance.initialize();
  });
}