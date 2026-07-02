import styles from './etym-tree.css?inline';
import {
  mapTree,
  renderTree,
  toggleButtonInner,
  avg,
  curvePath,
  junction,
  questionJunction,
  firstNodeOf,
  levelQuestionableTip,
  type EtymTree,
  type Messages,
} from './etym-tree.core';

function safeParse(value: string): any {
  try {
    return JSON.parse(value);
  } catch {
    return null;
  }
}

export class EkiEtymTree extends HTMLElement {
  static get observedAttributes(): string[] {
    return ['data', 'messages'];
  }

  private root: ShadowRoot;
  private _data: any = null;
  private _messages: Messages = {};
  private tree: EtymTree | null = null;
  private resizeObserver: ResizeObserver | null = null;
  private drawFrame = 0;
  private listenersBound = false;

  constructor() {
    super();
    this.root = this.attachShadow({ mode: 'open' });
    this.root.addEventListener('click', this.onRootClick);
  }

  set data(value: any) {
    this._data = typeof value === 'string' ? safeParse(value) : value;
    this.update();
  }
  get data(): any {
    return this._data;
  }

  set messages(value: Messages | string) {
    this._messages = typeof value === 'string' ? safeParse(value) || {} : value || {};
    this.update();
  }
  get messages(): Messages {
    return this._messages;
  }

  attributeChangedCallback(name: string, _old: string | null, value: string | null): void {
    if (value == null) return;
    if (name === 'data') this.data = value;
    if (name === 'messages') this.messages = value;
  }

  connectedCallback(): void {
    this.upgradeProperty('data');
    this.upgradeProperty('messages');
    if (!this.listenersBound) {
      document.addEventListener('click', this.onDocClick, true);
      document.addEventListener('keydown', this.onKeyDown);
      this.listenersBound = true;
    }
    this.update();
  }

  disconnectedCallback(): void {
    this.resizeObserver?.disconnect();
    document.removeEventListener('click', this.onDocClick, true);
    document.removeEventListener('keydown', this.onKeyDown);
    this.listenersBound = false;
  }

  private upgradeProperty(prop: 'data' | 'messages'): void {
    if (Object.prototype.hasOwnProperty.call(this, prop)) {
      const value = (this as any)[prop];
      delete (this as any)[prop];
      (this as any)[prop] = value;
    }
  }

  private update(): void {
    if (!this.isConnected || !this._data) {
      return;
    }
    this.tree = mapTree(this._data, this._messages);
    const html = renderTree(this.tree, this._messages);
    this.root.innerHTML = '<style>' + styles + '</style>' + (html || '');
    this.bindToggle();
    this.scheduleDraw();
    this.observeResize();
  }

  private bindToggle(): void {
    const toggle = this.root.querySelector<HTMLButtonElement>('.etym-tree__toggle');
    const tree = this.root.querySelector<HTMLElement>('.etym-tree');
    if (!toggle || !tree) return;
    toggle.addEventListener('click', () => {
      const hidden = tree.classList.toggle('etym-tree--comments-hidden');
      const shown = !hidden;
      toggle.setAttribute('data-shown', String(shown));
      toggle.innerHTML = toggleButtonInner(shown, this._messages);
      this.scheduleDraw();
    });
  }

  private observeResize(): void {
    if (typeof ResizeObserver === 'undefined') return;
    const canvas = this.root.querySelector<HTMLElement>('.etym-tree__canvas');
    if (!canvas) return;
    this.resizeObserver?.disconnect();
    this.resizeObserver = new ResizeObserver(() => this.scheduleDraw());
    this.resizeObserver.observe(canvas);
  }

  private scheduleDraw(): void {
    if (this.drawFrame) {
      cancelAnimationFrame(this.drawFrame);
    }
    this.drawFrame = requestAnimationFrame(() => {
      this.drawFrame = requestAnimationFrame(() => {
        this.markWideComments();
        this.drawConnectors();
      });
    });
  }

  private markWideComments(): void {
    this.root.querySelectorAll<HTMLElement>('.etym-node').forEach((node) => {
      const head = node.querySelector<HTMLElement>('.etym-node__head');
      const comment = node.querySelector<HTMLElement>('.etym-node__comment');
      const wide = !!head && !!comment && comment.offsetWidth > head.offsetWidth + 1;
      node.classList.toggle('etym-node--wide-comment', wide);
    });
  }

  private drawConnectors(): void {
    const canvas = this.root.querySelector<HTMLElement>('.etym-tree__canvas');
    const svg = this.root.querySelector<SVGSVGElement>('.etym-tree__links');
    const levelsEl = this.root.querySelector<HTMLElement>('.etym-tree__levels');
    if (!canvas || !svg || !levelsEl) return;

    const levelEls = Array.from(levelsEl.children);
    const width = canvas.clientWidth;
    const height = canvas.clientHeight;
    svg.setAttribute('width', String(width));
    svg.setAttribute('height', String(height));
    svg.setAttribute('viewBox', '0 0 ' + width + ' ' + height);

    if (levelEls.length < 2) {
      svg.innerHTML = '';
      return;
    }

    const canvasRect = canvas.getBoundingClientRect();
    const portsOf = (levelEl: Element) =>
      Array.from(levelEl.children).map((item) => {
        const rect = item.getBoundingClientRect();
        return {
          x: rect.left + rect.width / 2 - canvasRect.left,
          top: rect.top - canvasRect.top,
          bottom: rect.bottom - canvasRect.top,
        };
      });

    const levels = (this.tree && this.tree.levels) || [];
    const parts: string[] = [];
    for (let i = 0; i < levelEls.length - 1; i++) {
      const upper = portsOf(levelEls[i]);
      const lower = portsOf(levelEls[i + 1]);
      if (!upper.length || !lower.length) continue;

      const midX = (avg(upper.map((p) => p.x)) + avg(lower.map((p) => p.x))) / 2;
      const upperY = Math.max.apply(null, upper.map((p) => p.bottom));
      const lowerY = Math.min.apply(null, lower.map((p) => p.top));
      const midY = (upperY + lowerY) / 2;
      const type = levelEls[i + 1].getAttribute('data-connector') === 'merge' ? 'merge' : 'chain';
      const questionableTip = levelQuestionableTip(levels[i + 1]);

      upper.forEach((p) => parts.push(curvePath(p.x, p.bottom, midX, midY, !!questionableTip)));
      lower.forEach((p) => parts.push(curvePath(midX, midY, p.x, p.top, !!questionableTip)));

      if (questionableTip) {
        parts.push(questionJunction(midX, midY, questionableTip));
      } else {
        const upperHasNode = levels[i] && firstNodeOf(levels[i].items);
        const lowerHasNode = levels[i + 1] && firstNodeOf(levels[i + 1].items);
        const tip = upperHasNode && lowerHasNode ? (this._messages['lex.wordetym.origin.link'] || '') : '';
        parts.push(junction(midX, midY, type, tip));
      }
    }
    svg.innerHTML = parts.join('');
  }

  private onRootClick = (event: Event): void => {
    const tip = this.tipEl();
    const path = event.composedPath();
    if (tip && path.indexOf(tip) !== -1) {
      return; // click inside the tooltip (e.g. a source link) — keep it open
    }
    const trigger = path.find(
      (el): el is Element => el instanceof Element && el.hasAttribute('data-tip')
    );
    if (trigger) {
      this.toggleTip(trigger);
    } else {
      this.hideTip();
    }
  };

  private onDocClick = (event: Event): void => {
    if (event.composedPath().indexOf(this) === -1) {
      this.hideTip();
    }
  };

  private onKeyDown = (event: KeyboardEvent): void => {
    if (event.key === 'Escape') {
      this.hideTip();
    }
  };

  private tipEl(): HTMLElement | null {
    return this.root.querySelector<HTMLElement>('.etym-tip');
  }

  private toggleTip(trigger: Element): void {
    const tip = this.tipEl();
    if (!tip) return;
    const already = tip.classList.contains('etym-tip--visible') && tip.dataset.for === triggerKey(trigger);
    if (already) {
      this.hideTip();
    } else {
      this.showTip(trigger);
    }
  }

  private showTip(trigger: Element): void {
    const tip = this.tipEl();
    const tree = this.root.querySelector<HTMLElement>('.etym-tree');
    if (!tip || !tree) return;
    const html = trigger.getAttribute('data-tip') || '';
    tip.innerHTML = html;
    tip.dataset.for = triggerKey(trigger);
    tip.classList.add('etym-tip--visible');

    const wrapRect = tree.getBoundingClientRect();
    const tRect = trigger.getBoundingClientRect();
    const tipRect = tip.getBoundingClientRect();
    let left = tRect.left - wrapRect.left + tRect.width / 2 - tipRect.width / 2;
    left = Math.max(0, Math.min(left, wrapRect.width - tipRect.width));
    let top = tRect.top - wrapRect.top - tipRect.height - 8;
    if (top < 0) {
      top = tRect.bottom - wrapRect.top + 8;
    }
    tip.style.left = left + 'px';
    tip.style.top = top + 'px';
  }

  private hideTip(): void {
    const tip = this.tipEl();
    if (!tip) return;
    tip.classList.remove('etym-tip--visible');
    delete tip.dataset.for;
  }
}

let triggerSeq = 0;
function triggerKey(el: Element): string {
  const e = el as HTMLElement;
  if (!e.dataset.tipKey) {
    e.dataset.tipKey = String(++triggerSeq);
  }
  return e.dataset.tipKey;
}

if (!customElements.get('eki-etym-tree')) {
  customElements.define('eki-etym-tree', EkiEtymTree);
}
