class panelBreadcrumbs {
  constructor(obj) {
    this.main = obj;
    this.panel = this.main.parents('[data-rel="details-area"]:first');
  }

  getData() {
    const breadCrumbs = this.panel.attr('data-breadcrumbs') ? JSON.parse(this.panel.attr('data-breadcrumbs')) : [];
    const output = breadCrumbs.map((value, index) => {
      return `<li><button href="javascript:void(0);" data-index="${index}" data-id="${value.id}" data-contextmenu:compare="Ava uues paneelis" data-behaviour="replace" data-plugin="contextmenu">${value.word}</button></li>`;
    }).join('');

    this.main.find('ul').html(output);
    this.main[0].scrollLeft = 999999999;
    this.bindEvents();
  }

  bindEvents() {
    $wpm.bindObjects(this.main);

    const breadCrumbs = this.panel.attr('data-breadcrumbs') ? JSON.parse(this.panel.attr('data-breadcrumbs')) : [];

    this.main.find('button').on('click', (e) => {
      const wordId = $(e.target).data('id');
      const behaviour = $(e.target).data('behaviour') || false;
      const lastWordId = behaviour === 'replace' ? $(e.target).parents('#details-area:first').attr('data-id') : false;
      const index = $(e.target).attr('data-index');

      $(e.target).parents('#details-area:first').attr('data-breadcrumbs', JSON.stringify(breadCrumbs.slice(0, index)));
      loadDetails(wordId, behaviour, lastWordId);
    });

  }

  initialize() {
    this.getData();
  }
}
class PanelBreadcrumbs {
  static breadcrumbsData = {};
  // static breadcrumbsData = [
  //     {
  //       id: 1760998,
  //       word: "auto",
  //     },
  //     {
  //       id: 159100,
  //       word: "auto",
  //     },
  //     {
  //       id: 333861,
  //       word: "auto",
  //     },
  //     {
  //       id: 1416657,
  //       word: "auto",
  //     },
  //     {
  //       id: 1480299,
  //       word: "auto",
  //     },
  //     {
  //       id: 1483238,
  //       word: "auto",
  //     },
  //     {
  //       id: 1487200,
  //       word: "auto",
  //     },
  //     {
  //       id: 1501687,
  //       word: "auto",
  //     },
  //     {
  //       id: 1502217,
  //       word: "auto",
  //     },
  //     {
  //       id: 1508541,
  //       word: "auto",
  //     },
  //     {
  //       id: 1512948,
  //       word: "auto",
  //     },
  //     {
  //       id: 1522562,
  //       word: "auto",
  //     },
  //     {
  //       id: 1531594,
  //       word: "auto",
  //     },
  //     {
  //       id: 1569268,
  //       word: "auto",
  //     },
  //     {
  //       id: 1599917,
  //       word: "auto",
  //     },
  //     {
  //       id: 1620980,
  //       word: "auto",
  //     },
  // ];
  static skipAddingBreadcrumb = false;
  static activeId = {};
  static init(breadcrumbs) {
    const detailsDiv = breadcrumbs?.closest("#details-area");
    const detailsDivParent = detailsDiv?.parent();
    const detailsDivIndex = detailsDivParent?.children()?.index(detailsDiv);
    const id = detailsDiv.data("id");
    const word = detailsDiv.data("word");
    PanelBreadcrumbs.loadSavedData();
    PanelBreadcrumbs.activeId[detailsDivIndex] = id;
    console.log(PanelBreadcrumbs.breadcrumbsData)
    if (!PanelBreadcrumbs.isExistingWord(id, word, detailsDivIndex)) {
      PanelBreadcrumbs.addData(id, word, detailsDivIndex);
    }
    console.log("init", PanelBreadcrumbs.breadcrumbsData);
    PanelBreadcrumbs.addBreadcrumbs(breadcrumbs, detailsDivIndex);
    PanelBreadcrumbs.addClickHandler(breadcrumbs);
    PanelBreadcrumbs.addScrollHandler(breadcrumbs);
  }

  static addBreadcrumbs(breadcrumbs, index) {
    const output = PanelBreadcrumbs?.breadcrumbsData?.[index]
      ?.map((value) => {
        return `
      <li>
        <button
          href="javascript:void(0);"
          data-current="${value?.id === PanelBreadcrumbs?.activeId?.[index]}"
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
    breadcrumbs?.find("ul li")?.first()?.after(output);
  }

  static addClickHandler(breadcrumbs) {
    breadcrumbs.find("button").on("click", (e) => {
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
    const targetData = PanelBreadcrumbs?.breadcrumbsData?.[index];
    if (Array.isArray(targetData)) {
      return PanelBreadcrumbs?.breadcrumbsData?.[index]?.some(
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
        const maxScroll = breadcrumbsList?.prop('scrollWidth') - breadcrumbsParent?.outerWidth();
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
      breadcrumbsList.trigger('scroll');
    }
  }

  static removeDataByIndex(index) {
    if (index >= 0) {
      // Delete the target index
      delete PanelBreadcrumbs?.breadcrumbsData?.[index];
      // Readjust indexes
      Object.entries(PanelBreadcrumbs.breadcrumbsData || {})
      ?.filter((_, i) => i !== index)
      ?.forEach(([_, val], i) => PanelBreadcrumbs.breadcrumbsData[i] = val);
      PanelBreadcrumbs.saveCurrentData();
    }
  }

  static removeAllButFirstData() {
    // Get the total amount of existing data
    const dataCount = Object.values(PanelBreadcrumbs?.breadcrumbsData || {})?.length;
    // Start loop at 1 to exclude the first index
    for (let i = 1; i < dataCount; i++) {
      delete PanelBreadcrumbs?.breadcrumbsData?.[i];
    }
    PanelBreadcrumbs.saveCurrentData();
  }

  static saveCurrentData() {
    // save current data into the session storage
    // this survives page refresh but not tab closes etc
    const currentData = PanelBreadcrumbs?.breadcrumbsData;
    if (Object.entries(currentData || {})?.length) {
      sessionStorage?.setItem('ekilex_breadcrumbs', JSON.stringify(currentData));
    }
  }

  static loadSavedData() {
    // Get data from session storage and set it if it's not empty
    const savedData = JSON.parse(sessionStorage?.getItem('ekilex_breadcrumbs'));
    if (Object.entries(savedData || {})?.length) {
      PanelBreadcrumbs.breadcrumbsData = savedData;
    }
  }

  static addData(id, word, index) {
    if (!Array.isArray(PanelBreadcrumbs?.breadcrumbsData?.[index])) {
      PanelBreadcrumbs.breadcrumbsData[index] = [];
    }
    PanelBreadcrumbs.breadcrumbsData[index].push({ id, word });
  }
}

$.fn.panelBreadcrumbs = function() {
  $(this).each(function() {
    PanelBreadcrumbs.init($(this));
      // const instance = new panelBreadcrumbs($(this));
      // instance.initialize();
  });
}
