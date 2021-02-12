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

    if (this.type === 'syn') {
      this.main.sortable({
        items: '> .sortable-child-group',
        placeholder: "ui-state-highlight",
        handle: '.syn-handle',
        cancel: '',
        start: function(event, ui) {
          ui.placeholder.css({
            display: 'inline-block',
            width: ui.item.outerWidth(),
            height: ui.item.outerHeight(),
          })
        }
      });
      this.bindSynonyms();
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
  }

  /* Custom syn functionality */
  bindSynonyms() {
    const main = this.main;
    const meaningWordSynOriginalOrder = [];
    const meaningRelSynOriginalOrder = [];

    main.find('.sortable-main-group').each(function() {
      meaningWordSynOriginalOrder.push($(this).attr('data-orderby'));
    });

    main.find('.sortable-child-group').each(function() {
      meaningRelSynOriginalOrder.push($(this).attr('data-orderby'));
    });

    main.on('sortstop', function(event, ui) {
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
          main.find('.sortable-main-group').each(function(index){
            let lexemeId = $(this).find('input[name="synword-lexeme-id"]').val();
            data.items.push({
              id: lexemeId,
              orderby: meaningWordSynOriginalOrder[index],
              text: $(this).find('button:first').text(),
            });
          });
        } else {
          main.find('.sortable-child-group').each(function(index){
            data.items.push({
              id: $(this).attr('data-relation-id'),
              orderby: meaningRelSynOriginalOrder[index],
              text: $(this).find('button:first').text(),
            });
          });
        }

        openWaitDlg();
        postJson(applicationUrl + 'update_ordering', data).done(function() {
          if (viewType === 'lex') {
            let successCallbackName = orderingBtn.attr("data-callback");
            let successCallbackFunc = () => eval(successCallbackName);
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
      let synType = $(this).attr('data-syn-type');
      if (synType === 'MEANING_WORD') {
        $(this).addClass('sortable-main-group');
      } else if (synType === 'MEANING_REL') {
        $(this).addClass('sortable-child-group');
      }
    });
  }

  initialize() {
    if (this.type === 'lex-details') {
      this.differentiateLexDetails();
      $(window).on('update:sorter', () => {
        this.checkRequirements();
      });
    } else if (this.type === 'syn') {
      this.differentiateSynType();
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