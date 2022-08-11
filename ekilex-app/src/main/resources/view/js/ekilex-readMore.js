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
    this.main.find('div:first').children().show();
    this.main.find('div:first').children().find('.btn-custom').show();

    const maxHeight = parseInt(this.main.css('max-height'));

    $(this.main.find('div:first').children().get().reverse()).each((index, e) => {
      const obj = $(e);
      if (this.parent.parent().height() >= mainHeight && mainHeight >= maxHeight){
        this.hidden = this.hidden + 1;
        obj.hide();
      }
    });

    $(this.main.find('div:first .btn-custom').get().reverse()).each((index, e) => {
      const obj = $(e);
      if (this.parent.parent().height() >= mainHeight && mainHeight >= maxHeight){
        this.hidden = this.hidden + 1;
        obj.hide();
      }
    });
    if (this.hidden > 0) {
      const lastVisible = this.main.find('div:first').children().filter(':visible').last();
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
    $(window).on('resize update:wordId', () => {
      clearTimeout(this.debounce);
      this.debounce = setTimeout(() =>{
        this.detectChange()
      }, this.debounceTime);
    });
  }

  detectChange() {
    if (this.status) {
      this.main.find('div:first').children().show();
      if (this.handle) {
        this.handle.remove();
        this.handle = false;
      }
      this.main.css('max-height', '99999px');
      this.appendLabel();
      this.bindHandle();
    } else {
      if (this.handle) {
        this.handle.remove();
        this.handle = false;
      }
      this.main.removeAttr('style');
      this.appendDots();
      this.bindHandle();
    }
  }

  initialize() {
    setTimeout(() =>{
      this.appendDots();
      this.bindEvents();
    }, 100);
  }
}

$.fn.readMore = function() {
  $(this).each(function(){
    const instance = new ReadMore($(this));
    instance.initialize();
  });
}