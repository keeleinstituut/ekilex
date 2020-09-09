
  document.addEventListener('DOMContentLoaded', () => {

    const sideMenu = document.querySelector('.dropdown--aside')
    const nestedList  = sideMenu.querySelectorAll('li.has-submenu')

    nestedList.forEach((element) => {
      element.addEventListener('click', (element) => {
      element.stopPropagation()
      let target = element.target.closest('li');
  
      !target.classList.contains('open') ? target.classList.add('open') : target.classList.remove('open')
  
      })
    })
    
    sideMenu.addEventListener('click', (e) => {
      let navLinks = sideMenu.querySelectorAll('.nav-link')
      const sideMenuHeading = sideMenu.querySelector('.dropdown-toggle')
      navLinks.forEach(function ( el,i){ 
        el.classList.remove('active')
        el.addEventListener('click', () => {
          el.classList.add('active')
          var dropdownTitle = [].reduce.call(el.childNodes, function(a, b) { return a + (b.nodeType === 3 ? b.textContent : ''); }, '');
          sideMenuHeading.innerHTML = dropdownTitle
        })
      })
    })
  
    var menuItems = document.querySelectorAll('li.has-submenu');
    Array.prototype.forEach.call(menuItems, function (el, i) {
      var activatingA = el.querySelector('a');
      var btn = '<button><span><span class="sr-only">show submenu for “' + activatingA.text + '”</span></span></button>';
      activatingA.insertAdjacentHTML('beforeend', btn);  
    });
  })
