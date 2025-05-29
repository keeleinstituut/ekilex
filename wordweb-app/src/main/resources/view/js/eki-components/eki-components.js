var xt = Object.defineProperty;
var vt = (e, t, n) => t in e ? xt(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var _ = (e, t, n) => vt(e, typeof t != "symbol" ? t + "" : t, n);
function U() {
}
const kt = (e) => e;
function gt(e) {
  return e();
}
function it() {
  return /* @__PURE__ */ Object.create(null);
}
function A(e) {
  e.forEach(gt);
}
function Y(e) {
  return typeof e == "function";
}
function Et(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function St(e) {
  return Object.keys(e).length === 0;
}
function ot(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
const $t = typeof window < "u";
let zt = $t ? () => window.performance.now() : () => Date.now(), tt = $t ? (e) => requestAnimationFrame(e) : U;
const N = /* @__PURE__ */ new Set();
function mt(e) {
  N.forEach((t) => {
    t.c(e) || (N.delete(t), t.f());
  }), N.size !== 0 && tt(mt);
}
function Ct(e) {
  let t;
  return N.size === 0 && tt(mt), {
    promise: new Promise((n) => {
      N.add(t = { c: e, f: n });
    }),
    abort() {
      N.delete(t);
    }
  };
}
function m(e, t) {
  e.appendChild(t);
}
function bt(e) {
  if (!e) return document;
  const t = e.getRootNode ? e.getRootNode() : e.ownerDocument;
  return t && /** @type {ShadowRoot} */
  t.host ? (
    /** @type {ShadowRoot} */
    t
  ) : e.ownerDocument;
}
function Lt(e) {
  const t = k("style");
  return t.textContent = "/* empty */", jt(bt(e), t), t.sheet;
}
function jt(e, t) {
  return m(
    /** @type {Document} */
    e.head || e,
    t
  ), t.sheet;
}
function Z(e, t, n) {
  e.insertBefore(t, n || null);
}
function I(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function Mt(e, t) {
  for (let n = 0; n < e.length; n += 1)
    e[n] && e[n].d(t);
}
function k(e) {
  return document.createElement(e);
}
function rt(e) {
  return document.createElementNS("http://www.w3.org/2000/svg", e);
}
function H(e) {
  return document.createTextNode(e);
}
function B() {
  return H(" ");
}
function Nt(e, t, n, r) {
  return e.addEventListener(t, n, r), () => e.removeEventListener(t, n, r);
}
function d(e, t, n) {
  n == null ? e.removeAttribute(t) : e.getAttribute(t) !== n && e.setAttribute(t, n);
}
function Ot(e) {
  return Array.from(e.childNodes);
}
function Q(e, t) {
  t = "" + t, e.data !== t && (e.data = /** @type {string} */
  t);
}
function At(e, t, { bubbles: n = !1, cancelable: r = !1 } = {}) {
  return new CustomEvent(e, { detail: t, bubbles: n, cancelable: r });
}
function It(e) {
  const t = {};
  return e.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      t[n.slot || "default"] = !0;
    }
  ), t;
}
const q = /* @__PURE__ */ new Map();
let J = 0;
function Tt(e) {
  let t = 5381, n = e.length;
  for (; n--; ) t = (t << 5) - t ^ e.charCodeAt(n);
  return t >>> 0;
}
function Pt(e, t) {
  const n = { stylesheet: Lt(t), rules: {} };
  return q.set(e, n), n;
}
function st(e, t, n, r, s, c, o, i = 0) {
  const a = 16.666 / r;
  let l = `{
`;
  for (let g = 0; g <= 1; g += a) {
    const E = t + (n - t) * c(g);
    l += g * 100 + `%{${o(E, 1 - E)}}
`;
  }
  const h = l + `100% {${o(n, 1 - n)}}
}`, f = `__svelte_${Tt(h)}_${i}`, b = bt(e), { stylesheet: p, rules: u } = q.get(b) || Pt(b, e);
  u[f] || (u[f] = !0, p.insertRule(`@keyframes ${f} ${h}`, p.cssRules.length));
  const w = e.style.animation || "";
  return e.style.animation = `${w ? `${w}, ` : ""}${f} ${r}ms linear ${s}ms 1 both`, J += 1, f;
}
function Ut(e, t) {
  const n = (e.style.animation || "").split(", "), r = n.filter(
    t ? (c) => c.indexOf(t) < 0 : (c) => c.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), s = n.length - r.length;
  s && (e.style.animation = r.join(", "), J -= s, J || Rt());
}
function Rt() {
  tt(() => {
    J || (q.forEach((e) => {
      const { ownerNode: t } = e.stylesheet;
      t && I(t);
    }), q.clear());
  });
}
let et;
function P(e) {
  et = e;
}
const M = [], at = [];
let O = [];
const lt = [], Bt = /* @__PURE__ */ Promise.resolve();
let W = !1;
function Vt() {
  W || (W = !0, Bt.then(nt));
}
function R(e) {
  O.push(e);
}
const G = /* @__PURE__ */ new Set();
let j = 0;
function nt() {
  if (j !== 0)
    return;
  const e = et;
  do {
    try {
      for (; j < M.length; ) {
        const t = M[j];
        j++, P(t), Dt(t.$$);
      }
    } catch (t) {
      throw M.length = 0, j = 0, t;
    }
    for (P(null), M.length = 0, j = 0; at.length; ) at.pop()();
    for (let t = 0; t < O.length; t += 1) {
      const n = O[t];
      G.has(n) || (G.add(n), n());
    }
    O.length = 0;
  } while (M.length);
  for (; lt.length; )
    lt.pop()();
  W = !1, G.clear(), P(e);
}
function Dt(e) {
  if (e.fragment !== null) {
    e.update(), A(e.before_update);
    const t = e.dirty;
    e.dirty = [-1], e.fragment && e.fragment.p(e.ctx, t), e.after_update.forEach(R);
  }
}
function Ft(e) {
  const t = [], n = [];
  O.forEach((r) => e.indexOf(r) === -1 ? t.push(r) : n.push(r)), n.forEach((r) => r()), O = t;
}
let T;
function Ht() {
  return T || (T = Promise.resolve(), T.then(() => {
    T = null;
  })), T;
}
function K(e, t, n) {
  e.dispatchEvent(At(`${t ? "intro" : "outro"}${n}`));
}
const V = /* @__PURE__ */ new Set();
let S;
function qt() {
  S = {
    r: 0,
    c: [],
    p: S
    // parent group
  };
}
function Jt() {
  S.r || A(S.c), S = S.p;
}
function D(e, t) {
  e && e.i && (V.delete(e), e.i(t));
}
function ct(e, t, n, r) {
  if (e && e.o) {
    if (V.has(e)) return;
    V.add(e), S.c.push(() => {
      V.delete(e), r && (n && e.d(1), r());
    }), e.o(t);
  } else r && r();
}
const Zt = { duration: 0 };
function ut(e, t, n, r) {
  let c = t(e, n, { direction: "both" }), o = r ? 0 : 1, i = null, a = null, l = null, h;
  function f() {
    l && Ut(e, l);
  }
  function b(u, w) {
    const g = (
      /** @type {Program['d']} */
      u.b - o
    );
    return w *= Math.abs(g), {
      a: o,
      b: u.b,
      d: g,
      duration: w,
      start: u.start,
      end: u.start + w,
      group: u.group
    };
  }
  function p(u) {
    const {
      delay: w = 0,
      duration: g = 300,
      easing: E = kt,
      tick: y = U,
      css: x
    } = c || Zt, z = {
      start: zt() + w,
      b: u
    };
    u || (z.group = S, S.r += 1), "inert" in e && (u ? h !== void 0 && (e.inert = h) : (h = /** @type {HTMLElement} */
    e.inert, e.inert = !0)), i || a ? a = z : (x && (f(), l = st(e, o, u, g, w, E, x)), u && y(0, 1), i = b(z, g), R(() => K(e, u, "start")), Ct((C) => {
      if (a && C > a.start && (i = b(a, g), a = null, K(e, i.b, "start"), x && (f(), l = st(
        e,
        o,
        i.b,
        i.duration,
        0,
        E,
        c.css
      ))), i) {
        if (C >= i.end)
          y(o = i.b, 1 - o), K(e, i.b, "end"), a || (i.b ? f() : --i.group.r || A(i.group.c)), i = null;
        else if (C >= i.start) {
          const $ = C - i.start;
          o = i.a + i.d * E($ / i.duration), y(o, 1 - o);
        }
      }
      return !!(i || a);
    }));
  }
  return {
    run(u) {
      Y(c) ? Ht().then(() => {
        c = c({ direction: u ? "in" : "out" }), p(u);
      }) : p(u);
    },
    end() {
      f(), i = a = null;
    }
  };
}
function ft(e) {
  return (e == null ? void 0 : e.length) !== void 0 ? e : Array.from(e);
}
function Gt(e, t, n) {
  const { fragment: r, after_update: s } = e.$$;
  r && r.m(t, n), R(() => {
    const c = e.$$.on_mount.map(gt).filter(Y);
    e.$$.on_destroy ? e.$$.on_destroy.push(...c) : A(c), e.$$.on_mount = [];
  }), s.forEach(R);
}
function Kt(e, t) {
  const n = e.$$;
  n.fragment !== null && (Ft(n.after_update), A(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function Qt(e, t) {
  e.$$.dirty[0] === -1 && (M.push(e), Vt(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function Wt(e, t, n, r, s, c, o = null, i = [-1]) {
  const a = et;
  P(e);
  const l = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: c,
    update: U,
    not_equal: s,
    bound: it(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(t.context || (a ? a.$$.context : [])),
    // everything else
    callbacks: it(),
    dirty: i,
    skip_bound: !1,
    root: t.target || a.$$.root
  };
  o && o(l.root);
  let h = !1;
  if (l.ctx = n ? n(e, t.props || {}, (f, b, ...p) => {
    const u = p.length ? p[0] : b;
    return l.ctx && s(l.ctx[f], l.ctx[f] = u) && (!l.skip_bound && l.bound[f] && l.bound[f](u), h && Qt(e, f)), b;
  }) : [], l.update(), h = !0, A(l.before_update), l.fragment = r ? r(l.ctx) : !1, t.target) {
    if (t.hydrate) {
      const f = Ot(t.target);
      l.fragment && l.fragment.l(f), f.forEach(I);
    } else
      l.fragment && l.fragment.c();
    t.intro && D(e.$$.fragment), Gt(e, t.target, t.anchor), nt();
  }
  P(a);
}
let _t;
typeof HTMLElement == "function" && (_t = class extends HTMLElement {
  constructor(t, n, r) {
    super();
    /** The Svelte component constructor */
    _(this, "$$ctor");
    /** Slots */
    _(this, "$$s");
    /** The Svelte component instance */
    _(this, "$$c");
    /** Whether or not the custom element is connected */
    _(this, "$$cn", !1);
    /** Component props data */
    _(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    _(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    _(this, "$$p_d", {});
    /** @type {Record<string, Function[]>} Event listeners */
    _(this, "$$l", {});
    /** @type {Map<Function, Function>} Event listener unsubscribe functions */
    _(this, "$$l_u", /* @__PURE__ */ new Map());
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
      let n = function(o) {
        return () => {
          let i;
          return {
            c: function() {
              i = k("slot"), o !== "default" && d(i, "name", o);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(h, f) {
              Z(h, i, f);
            },
            d: function(h) {
              h && I(i);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const r = {}, s = It(this);
      for (const o of this.$$s)
        o in s && (r[o] = [n(o)]);
      for (const o of this.attributes) {
        const i = this.$$g_p(o.name);
        i in this.$$d || (this.$$d[i] = F(i, o.value, this.$$p_d, "toProp"));
      }
      for (const o in this.$$p_d)
        !(o in this.$$d) && this[o] !== void 0 && (this.$$d[o] = this[o], delete this[o]);
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
      const c = () => {
        this.$$r = !0;
        for (const o in this.$$p_d)
          if (this.$$d[o] = this.$$c.$$.ctx[this.$$c.$$.props[o]], this.$$p_d[o].reflect) {
            const i = F(
              o,
              this.$$d[o],
              this.$$p_d,
              "toAttribute"
            );
            i == null ? this.removeAttribute(this.$$p_d[o].attribute || o) : this.setAttribute(this.$$p_d[o].attribute || o, i);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(c), c();
      for (const o in this.$$l)
        for (const i of this.$$l[o]) {
          const a = this.$$c.$on(o, i);
          this.$$l_u.set(i, a);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, r) {
    var s;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = F(t, r, this.$$p_d, "toProp"), (s = this.$$c) == null || s.$set({ [t]: this.$$d[t] }));
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
function F(e, t, n, r) {
  var c;
  const s = (c = n[e]) == null ? void 0 : c.type;
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
function Xt(e, t, n, r, s, c) {
  let o = class extends _t {
    constructor() {
      super(e, n, s), this.$$p_d = t;
    }
    static get observedAttributes() {
      return Object.keys(t).map(
        (i) => (t[i].attribute || i).toLowerCase()
      );
    }
  };
  return Object.keys(t).forEach((i) => {
    Object.defineProperty(o.prototype, i, {
      get() {
        return this.$$c && i in this.$$c ? this.$$c[i] : this.$$d[i];
      },
      set(a) {
        var l;
        a = F(i, a, t), this.$$d[i] = a, (l = this.$$c) == null || l.$set({ [i]: a });
      }
    });
  }), r.forEach((i) => {
    Object.defineProperty(o.prototype, i, {
      get() {
        var a;
        return (a = this.$$c) == null ? void 0 : a[i];
      }
    });
  }), c && (o = c(o)), e.element = /** @type {any} */
  o, o;
}
class Yt {
  constructor() {
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    _(this, "$$");
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    _(this, "$$set");
  }
  /** @returns {void} */
  $destroy() {
    Kt(this, 1), this.$destroy = U;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(t, n) {
    if (!Y(n))
      return U;
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
    this.$$set && !St(t) && (this.$$.skip_bound = !0, this.$$set(t), this.$$.skip_bound = !1);
  }
}
const te = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(te);
function ee(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function dt(e, { delay: t = 0, duration: n = 400, easing: r = ee, x: s = 0, y: c = 0, opacity: o = 0 } = {}) {
  const i = getComputedStyle(e), a = +i.opacity, l = i.transform === "none" ? "" : i.transform, h = a * (1 - o), [f, b] = ot(s), [p, u] = ot(c);
  return {
    delay: t,
    duration: n,
    easing: r,
    css: (w, g) => `
			transform: ${l} translate(${(1 - w) * f}${b}, ${(1 - w) * p}${u});
			opacity: ${a - h * g}`
  };
}
const ne = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.right-4{right:1rem}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.w-\\[335px\\]{width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.text-xs{font-size:.75rem;line-height:1rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.hover\\:no-underline:hover{text-decoration-line:none}', X = new CSSStyleSheet();
X.replaceSync(ne);
const ie = (e) => class extends e {
  constructor() {
    super(...arguments);
    _(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && X && (this.shadowRoot.adoptedStyleSheets = [X]);
  }
};
function ht(e, t, n) {
  const r = e.slice();
  return r[6] = t[n], r;
}
function pt(e) {
  let t, n = (
    /*toast*/
    e[6].readMoreText + ""
  ), r, s, c, o;
  return {
    c() {
      t = k("a"), r = H(n), d(t, "class", "underline hover:no-underline"), d(t, "href", s = /*toast*/
      e[6].readMoreUrl), d(t, "target", c = /*toast*/
      e[6].readMoreIsExternal ? "_blank" : void 0), d(t, "rel", o = /*toast*/
      e[6].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(i, a) {
      Z(i, t, a), m(t, r);
    },
    p(i, a) {
      a & /*toasts*/
      1 && n !== (n = /*toast*/
      i[6].readMoreText + "") && Q(r, n), a & /*toasts*/
      1 && s !== (s = /*toast*/
      i[6].readMoreUrl) && d(t, "href", s), a & /*toasts*/
      1 && c !== (c = /*toast*/
      i[6].readMoreIsExternal ? "_blank" : void 0) && d(t, "target", c), a & /*toasts*/
      1 && o !== (o = /*toast*/
      i[6].readMoreIsExternal ? "noreferrer" : void 0) && d(t, "rel", o);
    },
    d(i) {
      i && I(t);
    }
  };
}
function wt(e) {
  let t, n, r, s = (
    /*toast*/
    e[6].title + ""
  ), c, o, i, a, l = (
    /*toast*/
    e[6].body + ""
  ), h, f, b, p, u, w, g, E, y, x, z, C, $ = (
    /*toast*/
    e[6].readMoreText && pt(e)
  );
  function yt() {
    return (
      /*click_handler*/
      e[4](
        /*toast*/
        e[6]
      )
    );
  }
  return {
    c() {
      t = k("div"), n = k("div"), r = k("span"), c = H(s), o = B(), i = k("p"), a = k("span"), h = H(l), f = B(), $ && $.c(), b = B(), p = k("button"), u = rt("svg"), w = rt("path"), E = B(), d(r, "class", "text-sm font-medium"), d(i, "class", "text-xs"), d(n, "class", "flex gap-1 flex-col mt-2"), d(w, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), d(w, "fill", "currentColor"), d(u, "width", "14"), d(u, "height", "14"), d(u, "viewBox", "0 0 14 14"), d(u, "fill", "none"), d(u, "xmlns", "http://www.w3.org/2000/svg"), d(p, "class", "w-6 h-6 flex justify-center items-center"), d(p, "type", "button"), d(p, "aria-label", g = /*toast*/
      e[6].closeLabel), d(t, "class", "bg-eki-white border border-eki-light-blue text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] pb-5 pt-3 grid grid-cols-[1fr_24px] gap-2 w-[335px] left-0");
    },
    m(v, L) {
      Z(v, t, L), m(t, n), m(n, r), m(r, c), m(n, o), m(n, i), m(i, a), m(a, h), m(i, f), $ && $.m(i, null), m(t, b), m(t, p), m(p, u), m(u, w), m(t, E), x = !0, z || (C = Nt(p, "click", yt), z = !0);
    },
    p(v, L) {
      e = v, (!x || L & /*toasts*/
      1) && s !== (s = /*toast*/
      e[6].title + "") && Q(c, s), (!x || L & /*toasts*/
      1) && l !== (l = /*toast*/
      e[6].body + "") && Q(h, l), /*toast*/
      e[6].readMoreText ? $ ? $.p(e, L) : ($ = pt(e), $.c(), $.m(i, null)) : $ && ($.d(1), $ = null), (!x || L & /*toasts*/
      1 && g !== (g = /*toast*/
      e[6].closeLabel)) && d(p, "aria-label", g);
    },
    i(v) {
      x || (v && R(() => {
        x && (y || (y = ut(t, dt, { x: 100 }, !0)), y.run(1));
      }), x = !0);
    },
    o(v) {
      v && (y || (y = ut(t, dt, { x: 100 }, !1)), y.run(0)), x = !1;
    },
    d(v) {
      v && I(t), $ && $.d(), v && y && y.end(), z = !1, C();
    }
  };
}
function oe(e) {
  let t, n, r = ft(
    /*toasts*/
    e[0]
  ), s = [];
  for (let o = 0; o < r.length; o += 1)
    s[o] = wt(ht(e, r, o));
  const c = (o) => ct(s[o], 1, 1, () => {
    s[o] = null;
  });
  return {
    c() {
      t = k("div");
      for (let o = 0; o < s.length; o += 1)
        s[o].c();
      d(t, "class", "absolute top-14 right-4 overflow-hidden z-[1073] flex flex-col gap-2");
    },
    m(o, i) {
      Z(o, t, i);
      for (let a = 0; a < s.length; a += 1)
        s[a] && s[a].m(t, null);
      n = !0;
    },
    p(o, [i]) {
      if (i & /*toasts, closeToast, undefined*/
      3) {
        r = ft(
          /*toasts*/
          o[0]
        );
        let a;
        for (a = 0; a < r.length; a += 1) {
          const l = ht(o, r, a);
          s[a] ? (s[a].p(l, i), D(s[a], 1)) : (s[a] = wt(l), s[a].c(), D(s[a], 1), s[a].m(t, null));
        }
        for (qt(), a = r.length; a < s.length; a += 1)
          c(a);
        Jt();
      }
    },
    i(o) {
      if (!n) {
        for (let i = 0; i < r.length; i += 1)
          D(s[i]);
        n = !0;
      }
    },
    o(o) {
      s = s.filter(Boolean);
      for (let i = 0; i < s.length; i += 1)
        ct(s[i]);
      n = !1;
    },
    d(o) {
      o && I(t), Mt(s, o);
    }
  };
}
function re(e, t, n) {
  let { component: r } = t, s = [], c = 0;
  const o = (l) => {
    n(0, s = s.filter((h) => h.id !== l.id)), r.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, i = (l) => {
    l.isVisible = !0, l.id ?? (l.id = c++), n(0, s = [...s, l]), r.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, a = (l) => o(l);
  return e.$$set = (l) => {
    "component" in l && n(2, r = l.component);
  }, [s, o, r, i, a];
}
class se extends Yt {
  constructor(t) {
    super(), Wt(this, t, re, oe, Et, { component: 2, addToast: 3 });
  }
  get component() {
    return this.$$.ctx[2];
  }
  set component(t) {
    this.$$set({ component: t }), nt();
  }
  get addToast() {
    return this.$$.ctx[3];
  }
}
customElements.define("eki-toast", Xt(se, { component: {} }, [], ["addToast"], !0, ie));
