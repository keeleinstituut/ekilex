class Sorter {

  constructor(element) {
    this.main = element;
    this.draggable = $(this.main.attr('data-dropper:draggable'));
    this.type = this.main.attr('data-sorter:type');
  }

  /* Unified sunctionality */
  bindSortable() {
    this.main.sortable({
      items: '> .sortable-main-group',
      placeholder: "ui-state-highlight",
      start: function(event, ui){
        if (this.type === 'lex-details') {
          ui.placeholder.css({
            height: ui.item.outerHeight(),
          })
        } else {
          ui.placeholder.css({
            display: 'inline-block',
            width: ui.item.outerWidth(),
            height: ui.item.outerHeight(),
          })
        }
      }
    });

    if (this.type === 'lex-details') {
      this.bindLexDetails();
      this.checkRequirements();
    } else if (this.type === 'meaning_relation') {
      this.bindMeaningRelations();
    }

  }

  /* Custom lex details functionality */
  bindLexDetails() {
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

  /* Custom meaning relations functionality */
  bindMeaningRelations() {
    const main = this.main;
    const originalOrder = [];

    main.find('.sortable-main-group').each(function(index){
      originalOrder.push($(this).attr('data-orderby'));
    });

    this.main.on('sortstop', function(event, ui){
      setTimeout(function(){
        const data = {
          additionalInfo: main.attr('data-additional-info'),
          opCode: 'meaning_relation',
          items: [],
        };

        main.find('.sortable-main-group').each(function(index){
          data.items.push({
            id: $(this).attr('data-relation-id'),
            orderby: originalOrder[index],
            text: $(this).find('button:first').text(),
          });
        });

        console.log(data);
        const orderingBtn = ui.item;
        
        openWaitDlg();
        postJson(applicationUrl + 'update_ordering', data, 'Salvestamine ebaõnnestus.', function(){
          if (orderingBtn.hasClass('do-refresh')) {
            refreshSynDetails();
          }
        });
        
      }, 60);
    });
  }
  
  checkRequirements() {
    this.canActivate = this.main.find('.details-open').length === 0;
    if (this.canActivate && this.main.sortable('instance')) {
      this.main.sortable('enable');
      if (this.children) {
        this.children.each(function(){
          $(this).sortable('enable');
        });
      }
    } else {
      this.main.sortable('disable');
      if (this.children) {
        this.children.each(function(){
          $(this).sortable('disable');
        });
      }
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
    console.log(this.type);
    if (this.type === 'lex-details') {
      this.differentiateLexDetails();
      $(window).on('update:sorter', () => {
        this.checkRequirements();
      });
    } else {
      this.main.children().addClass('sortable-main-group');
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