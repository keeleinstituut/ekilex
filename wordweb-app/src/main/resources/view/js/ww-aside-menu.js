  $(document).ready(function () {

    const sideMenu = document.querySelector('.dropdown-aside')
    const sideMenuHeading = sideMenu.querySelector('.dropdown-toggle')
    var mdBreakpoint = 768

    let navLinks = sideMenu.querySelectorAll('.nav-link')
    navLinks.forEach(function (el, i) {

      el.addEventListener('click', (element) => {
        element.preventDefault()    
        var dropdownTitle
        var target = $(element.target)
        $('.nav-link.active').not($(target).closest('.nav-link')).removeClass('active')
        $('.has-submenu.open').not($(target).closest('.nav-link').parents('.has-submenu')).removeClass('open')

        if ( $(target).hasClass('active') && $(target).parent().hasClass('has-submenu')){
          element.stopImmediatePropagation()
          element.stopPropagation()
          $(target).parent().toggleClass('open')
          $(target).toggleClass('active')
          return;
        }

        if (!$(target).hasClass('nav-link')) {
          element.stopImmediatePropagation()
          element.stopPropagation()
          target = $(target).closest('.nav-link')
          $(target).toggleClass('active')
          $(target).parent().toggleClass('open')
          return;
        }    

        targetHref = $(target).closest('.nav-link').attr('href');
        location.hash = targetHref;

        if (!$(el).closest('ul').hasClass('dropdown-menu')) {
          $('.nav-link').removeClass('active')
          $(target).addClass('active')
          showPanel()
          return
        }

        var navLinkIcon = $('.nav-link-icon')

        $(navLinkIcon).on('click', function (element) {
          iconClick(element)
        })

        function iconClick(element) {
          var target = $(element.target).closest('.nav-link').closest('.has-submenu')
          if ($(target).hasClass('open')) {
            $(target).find('.nav-link').removeClass('active')
            $(target).removeClass('open')
            element.stopImmediatePropagation()
          } else {
            if ($(window).width() < mdBreakpoint) {
              var targetLink = $(element.target).parent()
              if ($(targetLink).hasClass('active')) {
                targetLink.removeClass('active')
                $(targetLink).parent().removeClass('open')
                element.stopImmediatePropagation()
                return
              } else {
                $('.nav-link.active').removeClass('active')
                targetLink.addClass('active')
                $(targetLink).parent().addClass('open')
                element.stopImmediatePropagation()
                return
              }
            }
          }
        }

        function linkActive(element) {
          if ($(element.target).is('.nav-link-icon')) {
            element.stopImmediatePropagation()
            iconClick(element)
          } else {
            var targetHref = targetHref
            $('.has-submenu').removeClass('open')
            $('a[href="' + targetHref + '"]').addClass('active')
            showPanel()
          }
        }

        window.setTimeout(linkActive(element), 50);

        if ($(el).parents().hasClass('has-submenu') && $(window).width() <= mdBreakpoint) {
          return;
        }

        var parentList = $(el).closest('.nav-link').closest('ul')

        if (!parentList.hasClass('dropdown-menu')) {
          var subMenuLink = $(el).closest('ul').parent().find('.nav-link[data-toggle="pill"]')
          $('.has-submenu.open').removeClass('open')
          if ($(window).width() < mdBreakpoint) {
            $(subMenuLink).addClass('active')
          }
        } else {
          dropdownTitle = $(el).contents().get(0).nodeValue
        }
        sideMenuHeading.innerHTML = dropdownTitle
      })
    })

    function showPanel() {
      var hash = window.location.hash.substring(1)
      if (hash == 'undefined' || hash == '') {
        return;
      } else {
        var target = $('#' + hash)
        var closestTab = $(target).closest('.tab-pane')
        var targetLink = $("a[href$='" + hash + "']")
        sideMenuHeading.innerHTML = targetLink.eq(0).text()
        $('.nav-link').removeClass('active')
        targetLink.eq(0).addClass('active')
        if ($(targetLink).closest('.has-submenu').length) {
          $(targetLink).closest('.has-submenu').addClass('open')
        }
        $('.tab-pane').removeClass('show').removeClass('active')
        $(closestTab).addClass('show active')

  
        // move focus to first meaningful element inside the tab
        const hashId = window.location.hash.replace('#', '');
        const hashTarget = document.getElementById(hashId);

        // focus anchor if it exists and is inside the active tab
        if (hashTarget && closestTab[0].contains(hashTarget)) {
          hashTarget.setAttribute('tabindex', '-1');
          hashTarget.focus();
        } else {
          // otherwise focus first heading in the tab
          const heading = closestTab.find('h1, h2, h3, h4, h5, h6').first();
          if (heading.length) {
            heading.attr('tabindex', '-1');
            heading[0].focus();
          }
        }

        if ($(window).width() <= mdBreakpoint) {
          setTimeout(() => {
            $('html, body').animate({
              scrollTop: target.offset().top - 120
            });
          }, 0);
        }
        }
    }


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
    var anchors = document.body.querySelectorAll('.tab-content :is(a, h1, h2, h3, h4, h5, h6)[id]');

    var onScroll = function (e) {
      var closest = findClosestAnchor(anchors);
      var value = $(closest).attr('id')
      $('.has-submenu.open').find('a').removeClass('active')
      var activeLink = $('.has-submenu.open').find('a[href="#' + value + '"]')
      if ($(activeLink).text() !== '') {
        sideMenuHeading.innerHTML = ''
        sideMenuHeading.innerHTML = $(activeLink).text()
      }
      $(activeLink).addClass('active')
    };

    window.addEventListener('wheel', function (e) {
      if ($('.has-submenu.open').length && !$('.dropdown-aside.show').length) {
        onScroll(e)
      }
    });

    $(window).on('load', function() {
      showPanel();
    })

  });