class Sorter {

  constructor(element) {
    this.main = element;
    this.draggable = $(this.main.attr('data-dropper:draggable'));
    this.type = this.main.attr('data-sorter:type');
  }

  bindSortable() {
    this.main.sortable({
      items: '> .sortable-main-group',
      placeholder: "ui-state-highlight",
      start: function(event, ui){
        ui.placeholder.css({
          height: ui.item.outerHeight(),
        })
      }
    });

    if (this.type === 'lex-details') {
      this.children.each(function(){
        $(this).sortable({
          placeholder: "ui-state-highlight",
          start: function(event, ui){
            ui.placeholder.css({
              height: ui.item.outerHeight(),
            })
          }
        });
      });
    }

    this.checkRequirements();
  }
  
  checkRequirements() {
    this.canActivate = this.main.find('.details-open').length === 0;
    if (this.canActivate && this.main.sortable('instance')) {
      this.main.sortable('enable');
      this.children.each(function(){
        $(this).sortable('enable');
      });
    } else {
      this.main.sortable('disable');
      this.children.each(function(){
        $(this).sortable('disable');
      });
    }
    return this.canActivate;
  }

  differentiateLexDetails() {
    this.main.find('[id*="lexeme-details-"]').each(function(){
      const level2obj = $(this).find('[data-level2]:first');
      const level2Value = parseInt(level2obj.attr('data-level2'));
      if (level2Value > 1) {
        $(this).addClass('level-2-element');
      } else {
        $(this).addClass('level-1-element');
      }
    });

    this.main.find('.level-2-element').each(function(){
      const obj = $(this);
      if (!$(this).parent().is('.sortable-child-group')) {
        const children = obj.nextUntil('.level-1-element');
        const bound = $.merge(obj, children);
        bound.wrapAll('<div class="sortable-child-group"></div>')
      }
    });
    
    this.main.find('.level-1-element').each(function(){
      const obj = $(this);
      const children = obj.nextUntil('.level-1-element');
      const bound = $.merge(obj, children);
      bound.wrapAll('<div class="sortable-main-group"></div>')
    });
    

    

    this.children = this.main.find('.sortable-child-group');
  }

  initialize() {
    if (this.type === 'lex-details') {
      this.differentiateLexDetails();
      $(window).on('update:sorter', () => {
        this.checkRequirements();
      });
    }
    this.bindSortable();
  }
}

$.fn.sorter = function() {
  $(this).each(function(){
    const instance = new Sorter($(this));
    instance.initialize();
  });
}