var W = Object.defineProperty;
var tt = (e, t, n) => t in e ? W(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var g = (e, t, n) => tt(e, typeof t != "symbol" ? t + "" : t, n);
function M() {
}
function Y(e, t) {
  for (const n in t) e[n] = t[n];
  return (
    /** @type {T & S} */
    e
  );
}
function G(e) {
  return e();
}
function H() {
  return /* @__PURE__ */ Object.create(null);
}
function C(e) {
  e.forEach(G);
}
function K(e) {
  return typeof e == "function";
}
function et(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function nt(e) {
  return Object.keys(e).length === 0;
}
function J(e) {
  const t = {};
  for (const n in e) n[0] !== "$" && (t[n] = e[n]);
  return t;
}
function w(e, t) {
  e.appendChild(t);
}
function B(e, t, n) {
  e.insertBefore(t, n || null);
}
function j(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function x(e) {
  return document.createElement(e);
}
function X(e) {
  return document.createElementNS("http://www.w3.org/2000/svg", e);
}
function S(e) {
  return document.createTextNode(e);
}
function I() {
  return S(" ");
}
function rt(e, t, n, r) {
  return e.addEventListener(t, n, r), () => e.removeEventListener(t, n, r);
}
function d(e, t, n) {
  n == null ? e.removeAttribute(t) : e.getAttribute(t) !== n && e.setAttribute(t, n);
}
function it(e) {
  return Array.from(e.childNodes);
}
function A(e, t) {
  t = "" + t, e.data !== t && (e.data = /** @type {string} */
  t);
}
function q(e, t, n) {
  e.classList.toggle(t, !!n);
}
function ot(e) {
  const t = {};
  return e.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      t[n.slot || "default"] = !0;
    }
  ), t;
}
let V;
function L(e) {
  V = e;
}
const v = [], F = [];
let E = [];
const Z = [], st = /* @__PURE__ */ Promise.resolve();
let T = !1;
function at() {
  T || (T = !0, st.then(y));
}
function U(e) {
  E.push(e);
}
const N = /* @__PURE__ */ new Set();
let _ = 0;
function y() {
  if (_ !== 0)
    return;
  const e = V;
  do {
    try {
      for (; _ < v.length; ) {
        const t = v[_];
        _++, L(t), lt(t.$$);
      }
    } catch (t) {
      throw v.length = 0, _ = 0, t;
    }
    for (L(null), v.length = 0, _ = 0; F.length; ) F.pop()();
    for (let t = 0; t < E.length; t += 1) {
      const n = E[t];
      N.has(n) || (N.add(n), n());
    }
    E.length = 0;
  } while (v.length);
  for (; Z.length; )
    Z.pop()();
  T = !1, N.clear(), L(e);
}
function lt(e) {
  if (e.fragment !== null) {
    e.update(), C(e.before_update);
    const t = e.dirty;
    e.dirty = [-1], e.fragment && e.fragment.p(e.ctx, t), e.after_update.forEach(U);
  }
}
function ct(e) {
  const t = [], n = [];
  E.forEach((r) => e.indexOf(r) === -1 ? t.push(r) : n.push(r)), n.forEach((r) => r()), E = t;
}
const dt = /* @__PURE__ */ new Set();
function ut(e, t) {
  e && e.i && (dt.delete(e), e.i(t));
}
function ft(e, t, n) {
  const { fragment: r, after_update: s } = e.$$;
  r && r.m(t, n), U(() => {
    const l = e.$$.on_mount.map(G).filter(K);
    e.$$.on_destroy ? e.$$.on_destroy.push(...l) : C(l), e.$$.on_mount = [];
  }), s.forEach(U);
}
function ht(e, t) {
  const n = e.$$;
  n.fragment !== null && (ct(n.after_update), C(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function wt(e, t) {
  e.$$.dirty[0] === -1 && (v.push(e), at(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function bt(e, t, n, r, s, l, i = null, o = [-1]) {
  const u = V;
  L(e);
  const c = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: l,
    update: M,
    not_equal: s,
    bound: H(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(t.context || (u ? u.$$.context : [])),
    // everything else
    callbacks: H(),
    dirty: o,
    skip_bound: !1,
    root: t.target || u.$$.root
  };
  i && i(c.root);
  let b = !1;
  if (c.ctx = n ? n(e, t.props || {}, (f, p, ...a) => {
    const $ = a.length ? a[0] : p;
    return c.ctx && s(c.ctx[f], c.ctx[f] = $) && (!c.skip_bound && c.bound[f] && c.bound[f]($), b && wt(e, f)), p;
  }) : [], c.update(), b = !0, C(c.before_update), c.fragment = r ? r(c.ctx) : !1, t.target) {
    if (t.hydrate) {
      const f = it(t.target);
      c.fragment && c.fragment.l(f), f.forEach(j);
    } else
      c.fragment && c.fragment.c();
    t.intro && ut(e.$$.fragment), ft(e, t.target, t.anchor), y();
  }
  L(u);
}
let Q;
typeof HTMLElement == "function" && (Q = class extends HTMLElement {
  constructor(t, n, r) {
    super();
    /** The Svelte component constructor */
    g(this, "$$ctor");
    /** Slots */
    g(this, "$$s");
    /** The Svelte component instance */
    g(this, "$$c");
    /** Whether or not the custom element is connected */
    g(this, "$$cn", !1);
    /** Component props data */
    g(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    g(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    g(this, "$$p_d", {});
    /** @type {Record<string, Function[]>} Event listeners */
    g(this, "$$l", {});
    /** @type {Map<Function, Function>} Event listener unsubscribe functions */
    g(this, "$$l_u", /* @__PURE__ */ new Map());
    this.$$ctor = t, this.$$s = n, r && this.attachShadow({ mode: "open" });
  }
  addEventListener(t, n, r) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(n), this.$$c) {
      const s = this.$$c.$on(t, n);
      this.$$l_u.set(n, s);
    }
    super.addEventListener(t, n, r);
  }
  removeEventListener(t, n, r) {
    if (super.removeEventListener(t, n, r), this.$$c) {
      const s = this.$$l_u.get(n);
      s && (s(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(i) {
        return () => {
          let o;
          return {
            c: function() {
              o = x("slot"), i !== "default" && d(o, "name", i);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(b, f) {
              B(b, o, f);
            },
            d: function(b) {
              b && j(o);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const r = {}, s = ot(this);
      for (const i of this.$$s)
        i in s && (r[i] = [n(i)]);
      for (const i of this.attributes) {
        const o = this.$$g_p(i.name);
        o in this.$$d || (this.$$d[o] = z(o, i.value, this.$$p_d, "toProp"));
      }
      for (const i in this.$$p_d)
        !(i in this.$$d) && this[i] !== void 0 && (this.$$d[i] = this[i], delete this[i]);
      this.$$c = new this.$$ctor({
        target: this.shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: r,
          $$scope: {
            ctx: []
          }
        }
      });
      const l = () => {
        this.$$r = !0;
        for (const i in this.$$p_d)
          if (this.$$d[i] = this.$$c.$$.ctx[this.$$c.$$.props[i]], this.$$p_d[i].reflect) {
            const o = z(
              i,
              this.$$d[i],
              this.$$p_d,
              "toAttribute"
            );
            o == null ? this.removeAttribute(this.$$p_d[i].attribute || i) : this.setAttribute(this.$$p_d[i].attribute || i, o);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(l), l();
      for (const i in this.$$l)
        for (const o of this.$$l[i]) {
          const u = this.$$c.$on(i, o);
          this.$$l_u.set(o, u);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, r) {
    var s;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = z(t, r, this.$$p_d, "toProp"), (s = this.$$c) == null || s.$set({ [t]: this.$$d[t] }));
  }
  disconnectedCallback() {
    this.$$cn = !1, Promise.resolve().then(() => {
      !this.$$cn && this.$$c && (this.$$c.$destroy(), this.$$c = void 0);
    });
  }
  $$g_p(t) {
    return Object.keys(this.$$p_d).find(
      (n) => this.$$p_d[n].attribute === t || !this.$$p_d[n].attribute && n.toLowerCase() === t
    ) || t;
  }
});
function z(e, t, n, r) {
  var l;
  const s = (l = n[e]) == null ? void 0 : l.type;
  if (t = s === "Boolean" && typeof t != "boolean" ? t != null : t, !r || !n[e])
    return t;
  if (r === "toAttribute")
    switch (s) {
      case "Object":
      case "Array":
        return t == null ? null : JSON.stringify(t);
      case "Boolean":
        return t ? "" : null;
      case "Number":
        return t ?? null;
      default:
        return t;
    }
  else
    switch (s) {
      case "Object":
      case "Array":
        return t && JSON.parse(t);
      case "Boolean":
        return t;
      case "Number":
        return t != null ? +t : t;
      default:
        return t;
    }
}
function pt(e, t, n, r, s, l) {
  let i = class extends Q {
    constructor() {
      super(e, n, s), this.$$p_d = t;
    }
    static get observedAttributes() {
      return Object.keys(t).map(
        (o) => (t[o].attribute || o).toLowerCase()
      );
    }
  };
  return Object.keys(t).forEach((o) => {
    Object.defineProperty(i.prototype, o, {
      get() {
        return this.$$c && o in this.$$c ? this.$$c[o] : this.$$d[o];
      },
      set(u) {
        var c;
        u = z(o, u, t), this.$$d[o] = u, (c = this.$$c) == null || c.$set({ [o]: u });
      }
    });
  }), r.forEach((o) => {
    Object.defineProperty(i.prototype, o, {
      get() {
        var u;
        return (u = this.$$c) == null ? void 0 : u[o];
      }
    });
  }), l && (i = l(i)), e.element = /** @type {any} */
  i, i;
}
class gt {
  constructor() {
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    g(this, "$$");
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    g(this, "$$set");
  }
  /** @returns {void} */
  $destroy() {
    ht(this, 1), this.$destroy = M;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(t, n) {
    if (!K(n))
      return M;
    const r = this.$$.callbacks[t] || (this.$$.callbacks[t] = []);
    return r.push(n), () => {
      const s = r.indexOf(n);
      s !== -1 && r.splice(s, 1);
    };
  }
  /**
   * @param {Partial<Props>} props
   * @returns {void}
   */
  $set(t) {
    this.$$set && !nt(t) && (this.$$.skip_bound = !0, this.$$set(t), this.$$.skip_bound = !1);
  }
}
const mt = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(mt);
const $t = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]{display:none}.absolute{position:absolute}.left-0{left:0}.right-\\[15px\\]{right:15px}.top-\\[53px\\]{top:53px}.z-10{z-index:10}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.w-\\[335px\\]{width:335px}.translate-x-\\[calc\\(100\\%\\+15px\\)\\]{--tw-translate-x: calc(100% + 15px) ;transform:translate(var(--tw-translate-x),var(--tw-translate-y)) rotate(var(--tw-rotate)) skew(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y))}.transform{transform:translate(var(--tw-translate-x),var(--tw-translate-y)) rotate(var(--tw-rotate)) skew(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y))}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-4{padding-top:1rem;padding-bottom:1rem}.pl-7{padding-left:1.75rem}.pr-2{padding-right:.5rem}.text-sm{font-size:.875rem;line-height:1.25rem}.text-xs{font-size:.75rem;line-height:1rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.transition-\\[transform\\]{transition-property:transform;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}*{font-family:Inter}', P = new CSSStyleSheet();
P.replaceSync($t);
const xt = (e) => class extends e {
  constructor() {
    super(...arguments);
    g(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && P && (this.shadowRoot.adoptedStyleSheets = [P]);
  }
};
function D(e) {
  let t, n, r, s;
  return {
    c() {
      t = x("a"), n = S(
        /*readMoreText*/
        e[3]
      ), d(t, "class", "underline"), d(
        t,
        "href",
        /*readMoreUrl*/
        e[2]
      ), d(t, "target", r = /*readMoreIsExternal*/
      e[4] ? "_blank" : void 0), d(t, "rel", s = /*readMoreIsExternal*/
      e[4] ? "noreferrer" : void 0);
    },
    m(l, i) {
      B(l, t, i), w(t, n);
    },
    p(l, i) {
      i & /*readMoreText*/
      8 && A(
        n,
        /*readMoreText*/
        l[3]
      ), i & /*readMoreUrl*/
      4 && d(
        t,
        "href",
        /*readMoreUrl*/
        l[2]
      ), i & /*readMoreIsExternal*/
      16 && r !== (r = /*readMoreIsExternal*/
      l[4] ? "_blank" : void 0) && d(t, "target", r), i & /*readMoreIsExternal*/
      16 && s !== (s = /*readMoreIsExternal*/
      l[4] ? "noreferrer" : void 0) && d(t, "rel", s);
    },
    d(l) {
      l && j(t);
    }
  };
}
function yt(e) {
  let t, n, r, s, l, i, o, u, c, b, f, p, a, $, O, R, h = (
    /*readMoreText*/
    e[3] && D(e)
  );
  return {
    c() {
      t = x("div"), n = x("div"), r = x("div"), s = x("span"), l = S(
        /*title*/
        e[0]
      ), i = I(), o = x("p"), u = x("span"), c = S(
        /*body*/
        e[1]
      ), b = I(), h && h.c(), f = I(), p = x("button"), a = X("svg"), $ = X("path"), d(s, "class", "text-sm font-medium"), d(o, "class", "text-xs"), d(r, "class", "flex gap-1 flex-col"), d($, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), d($, "fill", "currentColor"), d(a, "width", "14"), d(a, "height", "14"), d(a, "viewBox", "0 0 14 14"), d(a, "fill", "none"), d(a, "xmlns", "http://www.w3.org/2000/svg"), d(p, "class", "w-6 h-6 flex justify-center items-center"), d(p, "type", "button"), d(
        p,
        "aria-label",
        /*closeLabel*/
        e[5]
      ), d(n, "class", "bg-eki-white border border-eki-light-blue text-eki-dark-blue-text rounded-lg pl-7 pr-2 py-4 grid grid-cols-[1fr_24px] gap-2 transition-[transform] w-[335px] left-0"), q(n, "translate-x-[calc(100%+15px)]", !/*isVisible*/
      e[7]), d(t, "class", "absolute top-[53px] right-[15px] overflow-hidden z-10");
    },
    m(m, k) {
      B(m, t, k), w(t, n), w(n, r), w(r, s), w(s, l), w(r, i), w(r, o), w(o, u), w(u, c), w(o, b), h && h.m(o, null), w(n, f), w(n, p), w(p, a), w(a, $), O || (R = rt(
        p,
        "click",
        /*click_handler*/
        e[9]
      ), O = !0);
    },
    p(m, [k]) {
      k & /*title*/
      1 && A(
        l,
        /*title*/
        m[0]
      ), k & /*body*/
      2 && A(
        c,
        /*body*/
        m[1]
      ), /*readMoreText*/
      m[3] ? h ? h.p(m, k) : (h = D(m), h.c(), h.m(o, null)) : h && (h.d(1), h = null), k & /*closeLabel*/
      32 && d(
        p,
        "aria-label",
        /*closeLabel*/
        m[5]
      ), k & /*isVisible*/
      128 && q(n, "translate-x-[calc(100%+15px)]", !/*isVisible*/
      m[7]);
    },
    i: M,
    o: M,
    d(m) {
      m && j(t), h && h.d(), O = !1, R();
    }
  };
}
function kt(e, t, n) {
  let { component: r } = t, { title: s } = t, { body: l } = t, { readMoreUrl: i = t["read-more-url"] } = t, { readMoreText: o = t["read-more-text"] } = t, { readMoreIsExternal: u = t["read-more-is-external"] } = t, { closeLabel: c = t["close-label"] } = t, b = !1;
  const f = (a) => {
    const $ = typeof a == "function" ? a(b) : a;
    n(7, b = $), $ ? r.dispatchEvent(new CustomEvent("eki-toast-opened", { bubbles: !0, composed: !0 })) : r.dispatchEvent(new CustomEvent("eki-toast-closed", { bubbles: !0, composed: !0 }));
  }, p = () => f(!1);
  return e.$$set = (a) => {
    n(10, t = Y(Y({}, t), J(a))), "component" in a && n(8, r = a.component), "title" in a && n(0, s = a.title), "body" in a && n(1, l = a.body), "readMoreUrl" in a && n(2, i = a.readMoreUrl), "readMoreText" in a && n(3, o = a.readMoreText), "readMoreIsExternal" in a && n(4, u = a.readMoreIsExternal), "closeLabel" in a && n(5, c = a.closeLabel);
  }, t = J(t), [
    s,
    l,
    i,
    o,
    u,
    c,
    f,
    b,
    r,
    p
  ];
}
class _t extends gt {
  constructor(t) {
    super(), bt(this, t, kt, yt, et, {
      component: 8,
      title: 0,
      body: 1,
      readMoreUrl: 2,
      readMoreText: 3,
      readMoreIsExternal: 4,
      closeLabel: 5,
      setVisibility: 6
    });
  }
  get component() {
    return this.$$.ctx[8];
  }
  set component(t) {
    this.$$set({ component: t }), y();
  }
  get title() {
    return this.$$.ctx[0];
  }
  set title(t) {
    this.$$set({ title: t }), y();
  }
  get body() {
    return this.$$.ctx[1];
  }
  set body(t) {
    this.$$set({ body: t }), y();
  }
  get readMoreUrl() {
    return this.$$.ctx[2];
  }
  set readMoreUrl(t) {
    this.$$set({ readMoreUrl: t }), y();
  }
  get readMoreText() {
    return this.$$.ctx[3];
  }
  set readMoreText(t) {
    this.$$set({ readMoreText: t }), y();
  }
  get readMoreIsExternal() {
    return this.$$.ctx[4];
  }
  set readMoreIsExternal(t) {
    this.$$set({ readMoreIsExternal: t }), y();
  }
  get closeLabel() {
    return this.$$.ctx[5];
  }
  set closeLabel(t) {
    this.$$set({ closeLabel: t }), y();
  }
  get setVisibility() {
    return this.$$.ctx[6];
  }
}
customElements.define("eki-toast", pt(_t, { component: {}, title: {}, body: {}, readMoreUrl: {}, readMoreText: {}, readMoreIsExternal: {}, closeLabel: {} }, [], ["setVisibility"], !0, xt));
