$(function() {
  // $('.btn-edit-etym').off('click').on('click', function(e) {
  //  e.preventDefault();
  //  var obj = $(this);
  //  var linksBlock = obj.parent();
  //  var container = obj.parents().find('.wordetym-card');
    
  //  obj.toggleClass('active');
  //  linksBlock.toggleClass('active');
  //  container.toggleClass('wordetym-card--active');
  // });
});

$.fn.wordetymCard = function() {
  $(this).each(function() {
    var obj = $(this);
    var parent = obj.parent();
    var header = obj.find('.wordetym-card__header')
    var editBtn = obj.find('.btn-edit-etym');
    var cancel = obj.parent().find('.cancel-add-edit');

    header.off('click').on('click', function(e) {
      obj.toggleClass('wordetym-card--active');

      if(obj.hasClass('add-edit-open')){
        obj.toggleClass('add-edit-open');
      }

      if (parent.hasClass('small') ) {
        parent.toggleClass('active');
      }

      $('.wordetym-card__header').not($(this)).parent().removeClass('wordetym-card--active');
    });

    cancel.on('click', function() {
      obj.removeClass('add-edit-open');
      editBtn.removeClass('active');
    });

    $(window).on('click', function(e){
      if($(e.target).parents('.wordetym-card').length == 0 && !$('body').hasClass('modal-open')){
        obj.removeClass('wordetym-card--active');
        obj.removeClass('add-edit-open');
      };
    });

    editBtn.off('click').on('click', function(e) {
      e.preventDefault();
      var btn = $(this);
      btn.toggleClass('active');
      obj.toggleClass('add-edit-open');
    });
  });
}

$.fn.etymLinkModifier = function() {
  $(this).each(function(){
    var obj = $(this);
    var wrapper = $('.wordetym-wrapper');
    var card = obj.parent().find('.wordetym-card');
    var parent = obj.parent();
    var state = false;

    obj.off('click').on('click', function(e) {
      state = !state;
      var cards = $('.btn-add-etym-step').not($(this)).parent().find('.wordetym-card');
      var buttons = $('.btn-add-etym-step').not($(this));
      
      if (state) {
        console.log('poop')
        obj.addClass('active');
        wrapper.addClass('modifier-active');
        card.addClass('wordetym-card--active');
        parent.addClass('edit-tree');
        cards.removeClass('wordetym-card--active');
        buttons.removeClass('active');
      } else {
        obj.removeClass('active');
        wrapper.removeClass('modifier-active');
        card.removeClass('wordetym-card--active');
        parent.removeClass('edit-tree');
      }
    });

    $(window).on('click', function(e){
      if($(e.target).parents('.tree-edit-link').length == 0){
        wrapper.removeClass('modifier-active');
        card.removeClass('wordetym-card--active');
        obj.removeClass('active');
        parent.removeClass('edit-tree');
        state = false;
      };
    });
  });
}
