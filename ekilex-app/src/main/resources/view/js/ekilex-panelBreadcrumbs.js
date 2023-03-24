class PanelBreadcrumbs {
  // Initialize data directly from storage if possible
  static breadcrumbsData = this.getSavedData();
  static activeId = {};
  static scrollIntervalInstance = undefined;
  static prevScrollValue = undefined;
  static init(breadcrumbs) {
    const detailsDiv = breadcrumbs?.closest("#details-area");
    const detailsDivParent = detailsDiv?.parent();
    const detailsDivIndex = detailsDivParent?.children()?.index(detailsDiv);
    const id = detailsDiv.data("id");
    const word = detailsDiv.data("word");
    // Set the currently active id on a per-panel (the index) basis
    this.activeId[detailsDivIndex] = id;
    // Only add words that aren't already in the list
    if (!this.isExistingWord(id, word, detailsDivIndex)) {
      this.addData(id, word, detailsDivIndex);
    }
    // Add the actual visual elements
    this.addBreadcrumbs(breadcrumbs, detailsDivIndex);
    this.addClickHandler(breadcrumbs);
    this.addScrollHandler(breadcrumbs);
  }

  static addBreadcrumbs(breadcrumbs, index) {
    const data = this?.breadcrumbsData?.[viewType]?.[index];
    const output = data
      ?.map((value, i) => {
        // Use data-current to identify the active word
        return `
      <li ${i === data?.length - 1 ? 'class="breadcrumbs--last"' : ''}>
        <button
          href="javascript:void(0);"
          data-current="${value?.id === this?.activeId?.[index]}"
          data-id="${value.id}"
          data-contextmenu:compare="Ava uues paneelis"
          data-behaviour="replace"
          data-plugin="contextmenu"
        >
        ${value.word}
        </button>
      </li>`;
      })
      .join("");
    // Add breadcrumbs after the first dots element
    breadcrumbs?.find("ul li")?.first()?.after(output);
  }

  static addClickHandler(breadcrumbs) {
    // Same click handler as old iteration
    breadcrumbs.find("button[data-id]").on("click", (e) => {
      const button = $(e?.target);
      const wordId = button.data("id");
      const behaviour = button.data("behaviour") || false;
      const lastWordId =
        behaviour === "replace"
          ? button.parents("#details-area:first").attr("data-id")
          : false;
      loadDetails(wordId, behaviour, lastWordId);
    });
  }

  static isExistingWord(id, word, index) {
    const targetData = this?.breadcrumbsData?.[viewType]?.[index];
    if (Array.isArray(targetData)) {
      // Check if any of the saved words match and exit early if they do
      return this?.breadcrumbsData?.[viewType]?.[index]?.some(
        (data) => data?.id === id && data?.word === word
      );
    }
    return false;
  }

  static addScrollHandler(breadcrumbs) {
    const breadcrumbsList = breadcrumbs?.find('ul')
    const breadcrumbsParent = breadcrumbs?.parent();
    const breadcrumbsScrollWidth = breadcrumbsList?.prop('scrollWidth');
    const parentWidth = breadcrumbsParent?.outerWidth();
    if (breadcrumbsScrollWidth > parentWidth) {
      breadcrumbsList.on("scroll", () => {
        // Recalculate as the sizes change
        const maxScroll = breadcrumbsList?.prop('scrollWidth') - breadcrumbsList?.prop('clientWidth');
        const currentScroll = breadcrumbsList?.scrollLeft();
        const areLeftDotsVisible = currentScroll > 0;
        const areRightDotsVisible = currentScroll < maxScroll;
        if (areLeftDotsVisible) {
          breadcrumbs?.addClass("breadcrumbs--left-scrollable");
        } else {
          breadcrumbs?.removeClass("breadcrumbs--left-scrollable");
        }
        if (areRightDotsVisible) {
          breadcrumbs?.addClass("breadcrumbs--right-scrollable");
        } else {
          breadcrumbs?.removeClass("breadcrumbs--right-scrollable");
        }
      });
      // Scroll to end, add the scroll to end of JS event queue
      setTimeout(() => breadcrumbsList?.scrollLeft(99999999), 0);
      this.addScrollButtonHandlers(breadcrumbsList);
    }
  }

  static removeDataByIndex(index) {
    if (index >= 0) {
      // Delete the target index
      delete this?.breadcrumbsData?.[viewType]?.[index];
      // Readjust indexes
      this.breadcrumbsData = Object.entries(this.breadcrumbsData || {})?.reduce((acc, view) => {
        // Only adjust the current view type, in case something was saved from the other view
        if (view === viewType) {
          // Recreate the object with new indexes
          const currentViewData = Object.entries(this.breadcrumbsData?.[viewType] || {})
            ?.reduce((acc, [_, val], i) => {
              return {
                ...acc,
                [i]: val
              }
            }, {});

          return {
            ...acc,
            [view]: currentViewData
          }
        }
        // Return whatever is already there if it's not the active view type
        return acc;
      }, {});
      this.saveCurrentData();
    }
  }

  static removeAllButFirstData() {
    // Get the total amount of existing data
    const dataCount = Object.values(this?.breadcrumbsData?.[viewType] || {})?.length;
    // Start loop at 1 to exclude the first index
    for (let i = 1; i < dataCount; i++) {
      delete this?.breadcrumbsData?.[viewType]?.[i];
    }
    this.saveCurrentData();
  }

  static saveCurrentData() {
    // save current data into the session storage
    // this survives page refresh, which is essentially what happens on search
    const currentData = this?.breadcrumbsData;
    const isCurrentViewTypeFilled = Object.entries(currentData?.[viewType] || {})?.length;
    if (isCurrentViewTypeFilled) {
      sessionStorage?.setItem('ekilex_breadcrumbs', JSON.stringify(currentData));
    }
  }

  static getSavedData() {
    // Get data from session storage and return it if it's not empty
    const savedData = JSON.parse(sessionStorage?.getItem('ekilex_breadcrumbs'));
    if (Object.entries(savedData || {})?.length) {
      return savedData;
    }
    // Fallback
    return {lex: {}, term: {}};
  }

  static addData(id, word, index) {
    if (!Array.isArray(this?.breadcrumbsData?.[viewType]?.[index])) {
      // Create the object only if it does not already exist.
      this.breadcrumbsData[viewType] ||= {};
      this.breadcrumbsData[viewType][index] = [];
    }
    this.breadcrumbsData[viewType][index].push({ id, word });
    this.saveCurrentData();
  }

  static addScrollButtonHandlers(breadcrumbsList) {
    breadcrumbsList?.on('mousedown', '.breadcrumbs__dots', (e) => {
      // Clear existing interval on new click
      if (this.scrollIntervalInstance !== undefined) {
        this.clearScrollInterval();
      }
      // Get the current event target
      const clickTarget = $(e?.target)
      // Interval scrolling while mouse is held down
      this.scrollIntervalInstance = setInterval(() => {
        const currentScrollValue = breadcrumbsList?.scrollLeft();
        // Get the scroll amount based on direction attribute
        const scrollAmount = clickTarget?.data('scroll-direction') === 'left' ? -1 : 1;
        const newScrollValue = currentScrollValue + scrollAmount;
        const maxScroll = breadcrumbsList?.prop('scrollWidth') - breadcrumbsList?.prop('clientWidth');
        // Stop interval if end was reached
        if (newScrollValue !== maxScroll) {
          breadcrumbsList?.scrollLeft(currentScrollValue + scrollAmount);
        } else {
          this.clearScrollInterval();
        }
      }, 50);
    });
    // Backup event listener to clear interval
    breadcrumbsList?.on('mouseup', '.breadcrumbs__dots', () => {
      this.clearScrollInterval();
    })
  }

  static clearScrollInterval() {
    if (this.scrollIntervalInstance) {
      clearInterval(this.scrollIntervalInstance);
      this.scrollIntervalInstance = undefined;
    }
  }
}

$.fn.panelBreadcrumbs = function() {
  $(this).each(function() {
    PanelBreadcrumbs.init($(this));
  });
}
