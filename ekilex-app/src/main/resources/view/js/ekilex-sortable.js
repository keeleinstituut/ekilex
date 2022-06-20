$.fn.ekiSortable = function() {
  $(this).each(function(){
    $(this).sortable({
      handle: '.dragHandle',
      start: function(event, ui) {
        const origEl = $(ui.item);
        const cloneEl = $(ui.placeholder);

        cloneEl.css({
          width: origEl.width()
        })
        //ui-sortable-placeholder;
      },
      stop: function() {
        $(window).trigger('update:wordId');
      }
    });
  });
}