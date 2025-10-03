// The function actually applying the offset
function offsetAnchor() {
  const mdBreakpoint = 768;

  if (location.hash.length !== 0) {
    window.scrollTo(window.scrollX, window.scrollY - 100);
  }

  if (window.innerWidth < mdBreakpoint) {
    window.scrollTo(window.scrollX, window.scrollY - 40);
  }
}

// Set the offset when entering page with hash present in the url
requestAnimationFrame(offsetAnchor);

function initKeyboardListeners() {
  const sidebar = document.getElementById("sidebar");
  const content = document.getElementById("main");
  if (!sidebar || !content) {
    return;
  }
  let lastActiveLink = sidebar.querySelector("a");
  content.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") {
      lastActiveLink ??= sidebar.querySelector("a");
      lastActiveLink?.focus?.();
    }
  });

  sidebar.addEventListener("keydown", (e) => {
    if (e.key === "ArrowRight") {
      lastActiveLink = e.target?.querySelector("a") || e.target;
      content.focus();
    }
  });
}

document.addEventListener(
  "DOMContentLoaded",
  () => {
    const topUp = document.getElementById("topUp");
    topUp.addEventListener("click", function () {
      window.scrollTo(window.scrollX, 0);
    });

    // Captures click events of all <a> elements with href starting with #
    document.addEventListener("click", function (e) {
      const link = e.target?.href || e.target?.querySelector("a")?.href;
      if (link?.includes("#")) {
        requestAnimationFrame(offsetAnchor);
      }
    });
    initKeyboardListeners();
  },
  { once: true }
);
