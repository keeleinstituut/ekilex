class Navigation {
  static navTriggerId = "nav-menu";
  static navLinksId = "nav-menu-links";
  static menuOpen = false;

  static navTrigger = null;
  static navLinks = null;

  static init() {
    this.navTrigger = document.getElementById(this.navTriggerId);
    this.navLinks = document.getElementById(this.navLinksId);
    if (!this.navTrigger || !this.navLinks) {
      return;
    }
    this.navTrigger.addEventListener("click", () => {
      this.navLinks.classList.toggle("navigation__dropdown-menu--visible");
      this.menuOpen = !this.menuOpen;
      this.navTrigger.setAttribute("aria-expanded", this.menuOpen);
    });

    // Close menu when user clicks outside menu
    document.addEventListener("click", (e) => {
      if (
        !e.target.closest(`#${this.navLinksId}`) &&
        e.target !== this.navTrigger
      ) {
        this.navLinks.classList.remove("navigation__dropdown-menu--visible");
      }
    });

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        this.closeMenu();
      }
    });

    this.navLinks.addEventListener("focusout", (e) => {
      if (!e.relatedTarget?.closest?.("#nav-menu-links")) {
        this.closeMenu();
      }
    });
  }

  static closeMenu() {
    this.menuOpen = false;
    this.navTrigger.setAttribute("aria-expanded", this.menuOpen);
    this.navLinks.classList.remove("navigation__dropdown-menu--visible");
  }
}

$.fn.navigationPlugin = function() {
  Navigation.init();
}
