document.addEventListener('DOMContentLoaded', () => {
  const sideMenu = document.querySelector('.dropdown-aside')

  nestedList = $(sideMenu).find('li.has-submenu')
  var mdBreakpoint = 768
  $(nestedList).on('click', function (e) {
    var target = $(e.target).closest('li')
    if ($(target).hasClass('has-submenu')) {
      if ($(window).width() < mdBreakpoint) {
        e.stopImmediatePropagation()
      }
      if ($(target).hasClass('open')) {
        $(target).removeClass('open')
        $(e.target).attr("aria-expanded", false);
      } else {
        $(nestedList).removeClass('open')
        $(target).addClass('open')
        var tabID = $(target).find('a').attr('href')
        $('.tab-pane').removeClass('show').removeClass('active')
        $(tabID).tab('show')
        $(e.target).attr("aria-expanded", true);

      }
    }
  })


  let navLinks = sideMenu.querySelectorAll('.nav-link')
  navLinks.forEach(function (el, i) {

    el.addEventListener('click', () => {
      if ($(el).closest('ul').hasClass('dropdown-menu')) {
        $('.tab-content').animate({
          scrollTop: $(window).scrollTop(0)
        })
      }

      navLinks.forEach(function (el, i) {
        el.classList.remove('active')
      })

      const sideMenuHeading = sideMenu.querySelector('.dropdown-toggle')
      var parentList = $(el).closest('.nav-link').closest('ul')
      
      var dropdownTitle
        if (!parentList.hasClass('dropdown-menu')) {
          dropdownTitle = $(el).closest('ul').parent().find('.nav-link[data-toggle="pill"]').contents().get(0).nodeValue
      } else {
        dropdownTitle = $(el).contents().get(0).nodeValue
      }
      sideMenuHeading.innerHTML = dropdownTitle
    })
  })

  var menuItems = document.querySelectorAll('li.has-submenu');
  Array.prototype.forEach.call(menuItems, function (el, i) {
    var activatingA = el.querySelector('a');
    var btn = '<button><span><span class="sr-only">show submenu for “' + activatingA.text + '”</span></span></button>';
    activatingA.insertAdjacentHTML('beforeend', btn);
  });

  // sub-menu scrolling toggling active class
  var findPos = function (obj) {
    var curleft = 0,
      curtop = 0;
    if (obj.offsetParent) {
      curleft = obj.offsetLeft;
      curtop = obj.offsetTop;
      while ((obj = obj.offsetParent)) {
        curleft += obj.offsetLeft;
        curtop += obj.offsetTop;
      }
    }
    return [curleft, curtop];
  };

  var findClosestAnchor = function (anchors) {

    var sortByDistance = function (element1, element2) {
      var pos1 = findPos(element1),
        pos2 = findPos(element2);
      var vect1 = [
          window.scrollX - pos1[0],
          window.scrollY - pos1[1]
        ],
        vect2 = [
          window.scrollX - pos2[0],
          window.scrollY - pos2[1]
        ];

      // we compare the length of the vectors using only the sum of their components squared
      // no need to find the magnitude of each (this was inspired by Mageek’s answer)
      var sqDist1 = vect1[0] * vect1[0] + vect1[1] * vect1[1],
        sqDist2 = vect2[0] * vect2[0] + vect2[1] * vect2[1];

      if (sqDist1 < sqDist2) return -1;
      else if (sqDist1 > sqDist2) return 1;
      else return 0;
    };

    return Array.prototype.slice.call(anchors).sort(sortByDistance)[0];
  };
  var anchors = document.body.querySelectorAll('.tab-content a[id]');
  
  var onScroll = function (e) {
    e.preventDefault();
    var closest = findClosestAnchor(anchors);
    var value = $(closest).attr('id')
    $('.has-submenu.open').find('a').removeClass('active')
    var activeLink = $('.has-submenu.open').find('a[href="#' + value + '"]')
    $(activeLink).addClass('active')
  };

  window.addEventListener('scroll', function (e) {
    if ($('.has-submenu.open').length && $(window).width() > mdBreakpoint) {
      onScroll(e)
    }
  })
})