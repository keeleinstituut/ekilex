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
  return this.each(function() {
    var obj = $(this);
    var header = obj.find('.wordetym-card__header')
    var editBtn = obj.find('.btn-edit-etym');

    header.off('click').on('click', function(e) {
      obj.toggleClass('wordetym-card--active');
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
  return this.each(function(){
    var obj = $(this);
    var wrapper = $('.wordetym-wrapper');
    var card = obj.parent().find('.wordetym-card');

    obj.off('click').on('click', function() {
      obj.toggleClass('active');
      wrapper.toggleClass('modifier-active');
      card.toggleClass('wordetym-card--active');
    });
  });
}