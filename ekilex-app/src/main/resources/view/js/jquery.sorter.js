class Sorter {

  constructor(element) {
    this.main = element;
    this.draggable = $(this.main.attr('data-dropper:draggable'));
    this.type = this.main.attr('data-sorter:type');
    this.handle = this.main.attr('data-sorter:handle') || false;
    this.id = Math.random().toString().substring(2);
  }

  /* Unified sunctionality */
  bindSortable() {
    if (this.type === 'syn') {
      this.main.find('.first-group-items').sortable({
        items: '.sortable-first-group',
        placeholder: "ui-state-highlight",
        cancel: '',
        handle: this.handle,
        tolerance: 'pointer',
        start: function(event, ui) {
          ui.placeholder.css({
            display: 'inline-block',
            width: ui.item.outerWidth(),
            height: ui.item.outerHeight(),
          })
        }
      });
      this.main.find('.second-group-items').sortable({
        items: '.sortable-second-group',
        placeholder: "ui-state-highlight",
        cancel: '',
        handle: this.handle,
        tolerance: 'pointer',
        start: function(event, ui) {
          ui.placeholder.css({
            display: 'inline-block',
            width: ui.item.outerWidth(),
            height: ui.item.outerHeight(),
          })
        }
      });
      this.main.find('.third-group-items').sortable({
        items: '.sortable-third-group',
        placeholder: "ui-state-highlight",
        cancel: '',
        handle: this.handle,
        tolerance: 'pointer',
        start: function(event, ui) {
          ui.placeholder.css({
            display: 'inline-block',
            width: ui.item.outerWidth(),
            height: ui.item.outerHeight(),
          })
        }
      });
      this.bindSynonyms();
    } else {
      this.main.sortable({
        items: '> .sortable-main-group',
        placeholder: "ui-state-highlight",
        handle: this.handle,
        tolerance: 'pointer',
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
    }

    if (this.type === 'relations') {
      this.bindRelations();
    }

    if (this.type === 'lex-details') {
      this.bindLexDetails();
      this.checkRequirements();
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


    const main = this.main;
    this.main.on('sortupdate', function(event, ui){

      if (!main.find('.details-open').length) {      
        const items = main.find('[data-level1]');
        items.each(function(index){
          $(this).attr('data-order', index);
        });
        const activeItem = $(ui.item).find('[data-level1]');
        const id = activeItem.attr('data-id');
        const order = activeItem.attr('data-order');

        const data = {
          lexemeId: id,
          position: order,
        };
        $.ajax({
          url: applicationUrl + 'update_lexeme_levels',
          data: JSON.stringify(data),
          method: 'POST',
          dataType: 'json',
          contentType: 'application/json',
          complete: function(){
            main.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first').click();
          },
        });
      }
    });
  }

  bindRelations() {
    const main = this.main;
    const originalOrder = [];
    
    main.find('.sortable-main-group').each(function(){
      originalOrder.push($(this).attr('data-orderby'));
    });

    main.on('sortupdate', function(event, ui) {
      const data = {
        opCode: 'word_relation',
        items: [],
      }
      main.find('.sortable-main-group').each(function(index){
        data.items.push({
          id: $(this).attr('data-id'),
          orderby: originalOrder[index],
        })
      });
      openWaitDlg();
      postJson(applicationUrl + 'update_ordering', data).done(function() {
        closeWaitDlg();
        main.parents('[data-rel="details-area"]:first').find('[name="details-btn"]:first').trigger('click');
      });
    });
  }

  /* Custom syn functionality */
  bindSynonyms() {
    const main = this.main;
    const meaningWordSynOriginalOrder = [];
    const meaningRelSynOriginalOrder = [];

    main.find('.sortable-first-group').each(function() {
      meaningWordSynOriginalOrder.push($(this).attr('data-orderby'));
    });

    main.find('.sortable-second-group').each(function() {
      meaningRelSynOriginalOrder.push($(this).attr('data-orderby'));
    });

    main.find('.sortable-third-group').each(function() {
      meaningRelSynOriginalOrder.push($(this).attr('data-orderby'));
    });

    main.on('sortupdate', function(event, ui) {
      setTimeout(function() {
        const orderingBtn = ui.item;
        const synType = orderingBtn.attr('data-syn-type');
        const isMeaningWordOrdering = synType === 'MEANING_WORD';
        const opCode = isMeaningWordOrdering ? 'lexeme_meaning_word' : 'meaning_relation';
        let mainwordLexemeId = orderingBtn.attr('data-lexeme-id');
        const data = {
          additionalInfo: mainwordLexemeId,
          opCode: opCode,
          items: [],
        };

        if (isMeaningWordOrdering) {
          main.find('.sortable-first-group').each(function(index) {
            const elem = $(this);
            const lexemeId = elem.find('input[name="synword-lexeme-id"]').val();
            data.items.push({
              id: lexemeId,
              orderby: meaningWordSynOriginalOrder[index],
              text: elem.find('button:first').text(),
            });
          });
        } else {
          main.find('.sortable-second-group').each(function(index) {
            const elem = $(this);
            data.items.push({
              id: elem.attr('data-relation-id'),
              orderby: meaningRelSynOriginalOrder[index],
              text: elem.find('button:first').text(),
            });
          });
        }

        if (opCode === 'meaning_relation') {
          main.find('.sortable-third-group').each(function(index) {
            const elem = $(this);
            data.items.push({
              id: elem.attr('data-id'),
              orderby: meaningRelSynOriginalOrder[index],
              text: elem.find('button:first').text(),
            });
          });
        }

        openWaitDlg();
        postJson(applicationUrl + 'update_ordering', data).done(function() {
          if (viewType === 'lex') {
            const successCallback = orderingBtn.attr("data-callback");
            const successCallbackFunc = createCallback(successCallback);
            successCallbackFunc();
          } else {
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
      const siblings = obj.nextUntil('.level-1-element');
      const bound = $.merge(obj, siblings);
      bound.wrapAll('<div class="sortable-main-group"></div>')
    });

    this.children = this.main.find('.sortable-child-group');
  }

  differentiateSynType() {
    this.main.children().each(function() {
      const $this = $(this);
      let synType = $this.attr('data-syn-type');
      if (synType === 'MEANING_WORD') {
        $this.addClass('sortable-first-group');
      } else if (synType === 'MEANING_REL') {
        $this.addClass('sortable-second-group');
      } else if (synType === 'INEXACT_SYN_MEANING_REL') {
        $this.addClass('sortable-third-group');
      }
    });
    this.main.find('.sortable-first-group').wrapAll('<span class="first-group-items">');
    this.main.find('.sortable-second-group').wrapAll('<span class="second-group-items">');
    this.main.find('.sortable-third-group').wrapAll('<span class="third-group-items">');
    this.main.find('.third-group-items').prepend('<i class="less-than-equal-icon" aria-hidden="true"></i>');
  }

  initialize() {
    if (this.type === 'lex-details') {
      if (this.main.parents('.scrollable-area:first').find('.details-open').length === 0) {
        this.differentiateLexDetails();
        $(window).on(`sorter.${this.id}`, () => {
          if (this.main.is('.ui-sortable')) {
            this.checkRequirements();
          }
        });
        this.bindSortable();
      }
    } else if (this.type === 'syn') {
      this.differentiateSynType();
      this.bindSortable();
    } else {
      this.main.children().addClass('sortable-main-group');
      this.bindSortable();
    }
  }
}

$.fn.sorter = function() {
  $(this).each(function(){
    const instance = new Sorter($(this));
    instance.initialize();
  });
}