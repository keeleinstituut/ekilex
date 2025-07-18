$.fn.publishingHandler = function() {
	$(this).each(function() {
		const instance = new PublishingHandler($(this));
		instance.init();
	});
};

class PublishingHandler {
	static triggerSelector = '[data-toggle="publishing-menu"]';
	static menuClass = "publishing__menu";
	static targetConstantsMap = {
		public: "is_public",
		unif: "ww_unif",
		lite: "ww_lite",
		od: "ww_od",
	};
	static replaceData = {
		word_relation: {
			endpoint: "wordrelation",
		},
	};

	static container;
	trigger;
	menu;
	lastMenuItem;
	entityName;
	entityId;
	replaceSectionId;
	replaceOwnerId;
	callback;
	initAccordion;
	requestInProgress = {};

	constructor(container) {
		this.container = container;
		this.trigger = container.find(PublishingHandler.triggerSelector);
		this.menu = container.find(`.${PublishingHandler.menuClass}`);
		this.entityName = container.attr("data-entity-name");
		this.entityId = container.attr("data-entity-id");
		this.replaceSectionId = container.attr("data-replace-section-id");
		this.replaceOwnerId = container.attr("data-replace-owner-id");
		this.callback = container.attr("data-callback");
		this.initAccordion = container.attr("data-init-accordion") === "true";

		if (!this.trigger.length) {
			console.error("Could not find trigger for publishing: ", this.container);
		}
		if (!this.menu.length) {
			console.error("Could not find menu for publishing: ", this.container);
		}
		if (!this.entityName) {
			console.error("Could not find an entity name for publishing: ", this.container);
		}
		if (!this.entityId) {
			console.error("Could not find an entity id for publishing: ", this.container);
		}
		if (!this.replaceSectionId && !this.callback) {
			console.error("No replace section nor callback have been defined: ", this.container);
		}
	}

	init() {
		this.bindMenuListeners();
	}

	bindMenuListeners() {
		let popperInstance;
		this.trigger.on("show.bs.dropdown", () => {
			// Append to body because parent could have overflow clipped
			this.menu
				.appendTo("body")
				.addClass(`${PublishingHandler.menuClass}--visible`);
			// This relies on popper being available, currently being added to window via bootstrap bundle
			popperInstance = new Popper(this.trigger.get(0), this.menu.get(0), {
				placement: "bottom-end",
				modifiers: {
					preventOverflow: {
						boundariesElement: "viewport",
					},
				},
			});
			this.trigger.attr("aria-expanded", true);
			requestAnimationFrame(() => {
				const menuChildren = this.menu.children();
				menuChildren.get(0).focus();
			});
		});

		this.trigger.on("hide.bs.dropdown", () => {
			if (!popperInstance) {
				return;
			}
			popperInstance.destroy();
			popperInstance = null;

			// Move menu back on close
			this.menu
				.removeClass(`${PublishingHandler.menuClass}--visible`)
				.insertAfter(this.trigger);
			this.trigger.attr("aria-expanded", false);
		});

		this.trigger.on("click", () => {
			if (popperInstance) {
				this.closeMenus();
			} else {
				this.trigger.trigger("show.bs.dropdown");
			}
		});

		if (!window.publishingClickOutsideActive) {
			$(document).on("click", (e) => {
				if (
					e.target.getAttribute("data-toggle") !== "publishing-menu" &&
					!e.target.closest(`.${PublishingHandler.menuClass}`)
				) {
					this.closeMenus();
				}
			});
			window.publishingClickOutsideActive = true;
		}

		// Make sure click only selects buttons
		this.menu.children().on("click", this.handleItemClick.bind(this));
	}

	handleItemClick(e) {
		// Exclude input clicks, as checkbox will trigger an event for both the label and input
		if (e.target?.tagName === 'INPUT') {
			return;
		}

		// currentTarget would refer to the actual button if user happened to press span etc
		const target = e.currentTarget ?? e.target;
		const targetName = target.dataset?.publishingItem;
		const currentValue = target.dataset?.publishingItemActive;
		const newValue = currentValue !== "true";
		if (targetName && currentValue !== undefined) {
			this.setTarget(
				PublishingHandler.targetConstantsMap[targetName],
				newValue
			);
		}
	}

	closeMenus() {
		$('[data-toggle="publishing-menu"]').trigger("hide.bs.dropdown");
	}

	setTarget(targetName, value) {
		if (!this.entityName || !this.entityId) {
			return;
		}
		if (this.requestInProgress[targetName]) {
			return;
		}
		this.requestInProgress[targetName] = true;
		const url = `${applicationUrl}publish_item`;
		$.ajax({
			url,
			data: JSON.stringify({
				targetName,
				entityName: this.entityName,
				entityId: this.entityId,
				value,
			}),
			method: "POST",
			contentType: "application/json",
		}).done(() => {
			this.closeMenus();
			this.handleDataReload();
		}).always(() => {
			this.requestInProgress[targetName] = false;
		});
	}

	handleDataReload() {
		if (this.callback) {
			this.reloadFromCallback();
		} else if (this.replaceSectionId) {
			this.replaceSectionContainer();
		}
	}

	replaceSectionContainer() {
		if (!this.replaceSectionId) {
			console.error("No replace section have been defined for ", this.entityName);
			return;
		}
		const endpoint = PublishingHandler.replaceData[this.entityName]?.endpoint;
		if (!endpoint) {
			console.error("Failed to find replace endpoint for ", this.entityName);
			return;
		}
		const url = `${applicationUrl}${endpoint}/${this.replaceOwnerId}`;
		$.ajax({
			url,
		}).done((res) => {
			// Using data attribute because eki accordion overrides id, which would break consequent uses
			const targetSelector = `[data-replace-id='${this.replaceSectionId}']`;
			const target = $(targetSelector);
			target.replaceWith(res);
			const newContainer = $(targetSelector);
			$wpm.bindObjects();
			if (this.initAccordion) {
				newContainer.ekiAccordion();
			}
		});
	}

	reloadFromCallback() {
		if (!this.callback) {
			console.error("No callback function have been defined for ", this.entityName);
			return;
		}
		const reloadCallbackFunc = createCallback(this.callback);
		reloadCallbackFunc();
	}
}
