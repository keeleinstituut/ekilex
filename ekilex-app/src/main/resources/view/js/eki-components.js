var kt = Object.defineProperty;
var Et = (t, e, n) => e in t ? kt(t, e, { enumerable: !0, configurable: !0, writable: !0, value: n }) : t[e] = n;
var x = (t, e, n) => Et(t, typeof e != "symbol" ? e + "" : e, n);
function U() {
}
const Ct = (t) => t;
function bt(t) {
  return t();
}
function it() {
  return /* @__PURE__ */ Object.create(null);
}
function I(t) {
  t.forEach(bt);
}
function Y(t) {
  return typeof t == "function";
}
function St(t, e) {
  return t != t ? e == e : t !== e || t && typeof t == "object" || typeof t == "function";
}
function zt(t) {
  return Object.keys(t).length === 0;
}
function rt(t) {
  const e = typeof t == "string" && t.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return e ? [parseFloat(e[1]), e[2] || "px"] : [
    /** @type {number} */
    t,
    "px"
  ];
}
const mt = typeof window < "u";
let jt = mt ? () => window.performance.now() : () => Date.now(), tt = mt ? (t) => requestAnimationFrame(t) : U;
const A = /* @__PURE__ */ new Set();
function _t(t) {
  A.forEach((e) => {
    e.c(t) || (A.delete(e), e.f());
  }), A.size !== 0 && tt(_t);
}
function Lt(t) {
  let e;
  return A.size === 0 && tt(_t), {
    promise: new Promise((n) => {
      A.add(e = { c: t, f: n });
    }),
    abort() {
      A.delete(e);
    }
  };
}
const Mt = typeof window < "u" ? window : typeof globalThis < "u" ? globalThis : (
  // @ts-ignore Node typings have this
  global
);
function y(t, e) {
  t.appendChild(e);
}
function yt(t) {
  if (!t) return document;
  const e = t.getRootNode ? t.getRootNode() : t.ownerDocument;
  return e && /** @type {ShadowRoot} */
  e.host ? (
    /** @type {ShadowRoot} */
    e
  ) : t.ownerDocument;
}
function Nt(t) {
  const e = C("style");
  return e.textContent = "/* empty */", Ot(yt(t), e), e.sheet;
}
function Ot(t, e) {
  return y(
    /** @type {Document} */
    t.head || t,
    e
  ), e.sheet;
}
function V(t, e, n) {
  t.insertBefore(e, n || null);
}
function M(t) {
  t.parentNode && t.parentNode.removeChild(t);
}
function At(t, e) {
  for (let n = 0; n < t.length; n += 1)
    t[n] && t[n].d(e);
}
function C(t) {
  return document.createElement(t);
}
function st(t) {
  return document.createElementNS("http://www.w3.org/2000/svg", t);
}
function G(t) {
  return document.createTextNode(t);
}
function D() {
  return G(" ");
}
function Tt(t, e, n, s) {
  return t.addEventListener(e, n, s), () => t.removeEventListener(e, n, s);
}
function d(t, e, n) {
  n == null ? t.removeAttribute(e) : t.getAttribute(e) !== n && t.setAttribute(e, n);
}
function It(t) {
  return Array.from(t.childNodes);
}
function et(t, e) {
  e = "" + e, t.data !== e && (t.data = /** @type {string} */
  e);
}
function Bt(t, e, { bubbles: n = !1, cancelable: s = !1 } = {}) {
  return new CustomEvent(t, { detail: e, bubbles: n, cancelable: s });
}
function Pt(t) {
  const e = {};
  return t.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      e[n.slot || "default"] = !0;
    }
  ), e;
}
const J = /* @__PURE__ */ new Map();
let Z = 0;
function Ut(t) {
  let e = 5381, n = t.length;
  for (; n--; ) e = (e << 5) - e ^ t.charCodeAt(n);
  return e >>> 0;
}
function Rt(t, e) {
  const n = { stylesheet: Nt(e), rules: {} };
  return J.set(t, n), n;
}
function at(t, e, n, s, r, c, o, i = 0) {
  const a = 16.666 / s;
  let l = `{
`;
  for (let $ = 0; $ <= 1; $ += a) {
    const k = e + (n - e) * c($);
    l += $ * 100 + `%{${o(k, 1 - k)}}
`;
  }
  const h = l + `100% {${o(n, 1 - n)}}
}`, f = `__svelte_${Ut(h)}_${i}`, p = yt(t), { stylesheet: w, rules: u } = J.get(p) || Rt(p, t);
  u[f] || (u[f] = !0, w.insertRule(`@keyframes ${f} ${h}`, w.cssRules.length));
  const g = t.style.animation || "";
  return t.style.animation = `${g ? `${g}, ` : ""}${f} ${s}ms linear ${r}ms 1 both`, Z += 1, f;
}
function Vt(t, e) {
  const n = (t.style.animation || "").split(", "), s = n.filter(
    e ? (c) => c.indexOf(e) < 0 : (c) => c.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), r = n.length - s.length;
  r && (t.style.animation = s.join(", "), Z -= r, Z || Dt());
}
function Dt() {
  tt(() => {
    Z || (J.forEach((t) => {
      const { ownerNode: e } = t.stylesheet;
      e && M(e);
    }), J.clear());
  });
}
let nt;
function P(t) {
  nt = t;
}
const O = [], lt = [];
let T = [];
const ct = [], Ft = /* @__PURE__ */ Promise.resolve();
let W = !1;
function Ht() {
  W || (W = !0, Ft.then(ot));
}
function R(t) {
  T.push(t);
}
const K = /* @__PURE__ */ new Set();
let N = 0;
function ot() {
  if (N !== 0)
    return;
  const t = nt;
  do {
    try {
      for (; N < O.length; ) {
        const e = O[N];
        N++, P(e), qt(e.$$);
      }
    } catch (e) {
      throw O.length = 0, N = 0, e;
    }
    for (P(null), O.length = 0, N = 0; lt.length; ) lt.pop()();
    for (let e = 0; e < T.length; e += 1) {
      const n = T[e];
      K.has(n) || (K.add(n), n());
    }
    T.length = 0;
  } while (O.length);
  for (; ct.length; )
    ct.pop()();
  W = !1, K.clear(), P(t);
}
function qt(t) {
  if (t.fragment !== null) {
    t.update(), I(t.before_update);
    const e = t.dirty;
    t.dirty = [-1], t.fragment && t.fragment.p(t.ctx, e), t.after_update.forEach(R);
  }
}
function Jt(t) {
  const e = [], n = [];
  T.forEach((s) => t.indexOf(s) === -1 ? e.push(s) : n.push(s)), n.forEach((s) => s()), T = e;
}
let B;
function Zt() {
  return B || (B = Promise.resolve(), B.then(() => {
    B = null;
  })), B;
}
function Q(t, e, n) {
  t.dispatchEvent(Bt(`${e ? "intro" : "outro"}${n}`));
}
const F = /* @__PURE__ */ new Set();
let z;
function Gt() {
  z = {
    r: 0,
    c: [],
    p: z
    // parent group
  };
}
function Kt() {
  z.r || I(z.c), z = z.p;
}
function H(t, e) {
  t && t.i && (F.delete(t), t.i(e));
}
function ut(t, e, n, s) {
  if (t && t.o) {
    if (F.has(t)) return;
    F.add(t), z.c.push(() => {
      F.delete(t), s && (n && t.d(1), s());
    }), t.o(e);
  } else s && s();
}
const Qt = { duration: 0 };
function dt(t, e, n, s) {
  let c = e(t, n, { direction: "both" }), o = s ? 0 : 1, i = null, a = null, l = null, h;
  function f() {
    l && Vt(t, l);
  }
  function p(u, g) {
    const $ = (
      /** @type {Program['d']} */
      u.b - o
    );
    return g *= Math.abs($), {
      a: o,
      b: u.b,
      d: $,
      duration: g,
      start: u.start,
      end: u.start + g,
      group: u.group
    };
  }
  function w(u) {
    const {
      delay: g = 0,
      duration: $ = 300,
      easing: k = Ct,
      tick: v = U,
      css: m
    } = c || Qt, j = {
      start: jt() + g,
      b: u
    };
    u || (j.group = z, z.r += 1), "inert" in t && (u ? h !== void 0 && (t.inert = h) : (h = /** @type {HTMLElement} */
    t.inert, t.inert = !0)), i || a ? a = j : (m && (f(), l = at(t, o, u, $, g, k, m)), u && v(0, 1), i = p(j, $), R(() => Q(t, u, "start")), Lt((L) => {
      if (a && L > a.start && (i = p(a, $), a = null, Q(t, i.b, "start"), m && (f(), l = at(
        t,
        o,
        i.b,
        i.duration,
        0,
        k,
        c.css
      ))), i) {
        if (L >= i.end)
          v(o = i.b, 1 - o), Q(t, i.b, "end"), a || (i.b ? f() : --i.group.r || I(i.group.c)), i = null;
        else if (L >= i.start) {
          const b = L - i.start;
          o = i.a + i.d * k(b / i.duration), v(o, 1 - o);
        }
      }
      return !!(i || a);
    }));
  }
  return {
    run(u) {
      Y(c) ? Zt().then(() => {
        c = c({ direction: u ? "in" : "out" }), w(u);
      }) : w(u);
    },
    end() {
      f(), i = a = null;
    }
  };
}
function ft(t) {
  return (t == null ? void 0 : t.length) !== void 0 ? t : Array.from(t);
}
function Wt(t, e, n) {
  const { fragment: s, after_update: r } = t.$$;
  s && s.m(e, n), R(() => {
    const c = t.$$.on_mount.map(bt).filter(Y);
    t.$$.on_destroy ? t.$$.on_destroy.push(...c) : I(c), t.$$.on_mount = [];
  }), r.forEach(R);
}
function Xt(t, e) {
  const n = t.$$;
  n.fragment !== null && (Jt(n.after_update), I(n.on_destroy), n.fragment && n.fragment.d(e), n.on_destroy = n.fragment = null, n.ctx = []);
}
function Yt(t, e) {
  t.$$.dirty[0] === -1 && (O.push(t), Ht(), t.$$.dirty.fill(0)), t.$$.dirty[e / 31 | 0] |= 1 << e % 31;
}
function te(t, e, n, s, r, c, o = null, i = [-1]) {
  const a = nt;
  P(t);
  const l = t.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: c,
    update: U,
    not_equal: r,
    bound: it(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(e.context || (a ? a.$$.context : [])),
    // everything else
    callbacks: it(),
    dirty: i,
    skip_bound: !1,
    root: e.target || a.$$.root
  };
  o && o(l.root);
  let h = !1;
  if (l.ctx = n ? n(t, e.props || {}, (f, p, ...w) => {
    const u = w.length ? w[0] : p;
    return l.ctx && r(l.ctx[f], l.ctx[f] = u) && (!l.skip_bound && l.bound[f] && l.bound[f](u), h && Yt(t, f)), p;
  }) : [], l.update(), h = !0, I(l.before_update), l.fragment = s ? s(l.ctx) : !1, e.target) {
    if (e.hydrate) {
      const f = It(e.target);
      l.fragment && l.fragment.l(f), f.forEach(M);
    } else
      l.fragment && l.fragment.c();
    e.intro && H(t.$$.fragment), Wt(t, e.target, e.anchor), ot();
  }
  P(a);
}
let xt;
typeof HTMLElement == "function" && (xt = class extends HTMLElement {
  constructor(e, n, s) {
    super();
    /** The Svelte component constructor */
    x(this, "$$ctor");
    /** Slots */
    x(this, "$$s");
    /** The Svelte component instance */
    x(this, "$$c");
    /** Whether or not the custom element is connected */
    x(this, "$$cn", !1);
    /** Component props data */
    x(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    x(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    x(this, "$$p_d", {});
    /** @type {Record<string, Function[]>} Event listeners */
    x(this, "$$l", {});
    /** @type {Map<Function, Function>} Event listener unsubscribe functions */
    x(this, "$$l_u", /* @__PURE__ */ new Map());
    this.$$ctor = e, this.$$s = n, s && this.attachShadow({ mode: "open" });
  }
  addEventListener(e, n, s) {
    if (this.$$l[e] = this.$$l[e] || [], this.$$l[e].push(n), this.$$c) {
      const r = this.$$c.$on(e, n);
      this.$$l_u.set(n, r);
    }
    super.addEventListener(e, n, s);
  }
  removeEventListener(e, n, s) {
    if (super.removeEventListener(e, n, s), this.$$c) {
      const r = this.$$l_u.get(n);
      r && (r(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(o) {
        return () => {
          let i;
          return {
            c: function() {
              i = C("slot"), o !== "default" && d(i, "name", o);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(h, f) {
              V(h, i, f);
            },
            d: function(h) {
              h && M(i);
            }
          };
        };
      };
      var e = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const s = {}, r = Pt(this);
      for (const o of this.$$s)
        o in r && (s[o] = [n(o)]);
      for (const o of this.attributes) {
        const i = this.$$g_p(o.name);
        i in this.$$d || (this.$$d[i] = q(i, o.value, this.$$p_d, "toProp"));
      }
      for (const o in this.$$p_d)
        !(o in this.$$d) && this[o] !== void 0 && (this.$$d[o] = this[o], delete this[o]);
      this.$$c = new this.$$ctor({
        target: this.shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: s,
          $$scope: {
            ctx: []
          }
        }
      });
      const c = () => {
        this.$$r = !0;
        for (const o in this.$$p_d)
          if (this.$$d[o] = this.$$c.$$.ctx[this.$$c.$$.props[o]], this.$$p_d[o].reflect) {
            const i = q(
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
  attributeChangedCallback(e, n, s) {
    var r;
    this.$$r || (e = this.$$g_p(e), this.$$d[e] = q(e, s, this.$$p_d, "toProp"), (r = this.$$c) == null || r.$set({ [e]: this.$$d[e] }));
  }
  disconnectedCallback() {
    this.$$cn = !1, Promise.resolve().then(() => {
      !this.$$cn && this.$$c && (this.$$c.$destroy(), this.$$c = void 0);
    });
  }
  $$g_p(e) {
    return Object.keys(this.$$p_d).find(
      (n) => this.$$p_d[n].attribute === e || !this.$$p_d[n].attribute && n.toLowerCase() === e
    ) || e;
  }
});
function q(t, e, n, s) {
  var c;
  const r = (c = n[t]) == null ? void 0 : c.type;
  if (e = r === "Boolean" && typeof e != "boolean" ? e != null : e, !s || !n[t])
    return e;
  if (s === "toAttribute")
    switch (r) {
      case "Object":
      case "Array":
        return e == null ? null : JSON.stringify(e);
      case "Boolean":
        return e ? "" : null;
      case "Number":
        return e ?? null;
      default:
        return e;
    }
  else
    switch (r) {
      case "Object":
      case "Array":
        return e && JSON.parse(e);
      case "Boolean":
        return e;
      case "Number":
        return e != null ? +e : e;
      default:
        return e;
    }
}
function ee(t, e, n, s, r, c) {
  let o = class extends xt {
    constructor() {
      super(t, n, r), this.$$p_d = e;
    }
    static get observedAttributes() {
      return Object.keys(e).map(
        (i) => (e[i].attribute || i).toLowerCase()
      );
    }
  };
  return Object.keys(e).forEach((i) => {
    Object.defineProperty(o.prototype, i, {
      get() {
        return this.$$c && i in this.$$c ? this.$$c[i] : this.$$d[i];
      },
      set(a) {
        var l;
        a = q(i, a, e), this.$$d[i] = a, (l = this.$$c) == null || l.$set({ [i]: a });
      }
    });
  }), s.forEach((i) => {
    Object.defineProperty(o.prototype, i, {
      get() {
        var a;
        return (a = this.$$c) == null ? void 0 : a[i];
      }
    });
  }), c && (o = c(o)), t.element = /** @type {any} */
  o, o;
}
class ne {
  constructor() {
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    x(this, "$$");
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    x(this, "$$set");
  }
  /** @returns {void} */
  $destroy() {
    Xt(this, 1), this.$destroy = U;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(e, n) {
    if (!Y(n))
      return U;
    const s = this.$$.callbacks[e] || (this.$$.callbacks[e] = []);
    return s.push(n), () => {
      const r = s.indexOf(n);
      r !== -1 && s.splice(r, 1);
    };
  }
  /**
   * @param {Partial<Props>} props
   * @returns {void}
   */
  $set(e) {
    this.$$set && !zt(e) && (this.$$.skip_bound = !0, this.$$set(e), this.$$.skip_bound = !1);
  }
}
const oe = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(oe);
function ie(t) {
  const e = t - 1;
  return e * e * e + 1;
}
function ht(t, { delay: e = 0, duration: n = 400, easing: s = ie, x: r = 0, y: c = 0, opacity: o = 0 } = {}) {
  const i = getComputedStyle(t), a = +i.opacity, l = i.transform === "none" ? "" : i.transform, h = a * (1 - o), [f, p] = rt(r), [w, u] = rt(c);
  return {
    delay: e,
    duration: n,
    easing: s,
    css: (g, $) => `
			transform: ${l} translate(${(1 - g) * f}${p}, ${(1 - g) * w}${u});
			opacity: ${a - h * $}`
  };
}
const re = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.right-4{right:1rem}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.w-\\[335px\\]{width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.text-xs{font-size:.75rem;line-height:1rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.hover\\:no-underline:hover{text-decoration-line:none}', X = new CSSStyleSheet();
X.replaceSync(re);
const se = (t) => class extends t {
  constructor() {
    super(...arguments);
    x(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && X && (this.shadowRoot.adoptedStyleSheets = [X]);
  }
}, { Boolean: ae } = Mt;
function pt(t, e, n) {
  const s = t.slice();
  return s[6] = e[n], s;
}
function wt(t) {
  let e, n = (
    /*toast*/
    t[6].title + ""
  ), s;
  return {
    c() {
      e = C("span"), s = G(n), d(e, "class", "text-sm font-medium");
    },
    m(r, c) {
      V(r, e, c), y(e, s);
    },
    p(r, c) {
      c & /*toasts*/
      1 && n !== (n = /*toast*/
      r[6].title + "") && et(s, n);
    },
    d(r) {
      r && M(e);
    }
  };
}
function gt(t) {
  let e, n = (
    /*toast*/
    t[6].readMoreText + ""
  ), s, r, c, o;
  return {
    c() {
      e = C("a"), s = G(n), d(e, "class", "underline hover:no-underline"), d(e, "href", r = /*toast*/
      t[6].readMoreUrl), d(e, "target", c = /*toast*/
      t[6].readMoreIsExternal ? "_blank" : void 0), d(e, "rel", o = /*toast*/
      t[6].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(i, a) {
      V(i, e, a), y(e, s);
    },
    p(i, a) {
      a & /*toasts*/
      1 && n !== (n = /*toast*/
      i[6].readMoreText + "") && et(s, n), a & /*toasts*/
      1 && r !== (r = /*toast*/
      i[6].readMoreUrl) && d(e, "href", r), a & /*toasts*/
      1 && c !== (c = /*toast*/
      i[6].readMoreIsExternal ? "_blank" : void 0) && d(e, "target", c), a & /*toasts*/
      1 && o !== (o = /*toast*/
      i[6].readMoreIsExternal ? "noreferrer" : void 0) && d(e, "rel", o);
    },
    d(i) {
      i && M(e);
    }
  };
}
function $t(t) {
  let e, n, s, r, c, o = (
    /*toast*/
    t[6].body + ""
  ), i, a, l, h, f, p, w, u, g, $, k, v, m, j, L, b = (
    /*toast*/
    t[6].title && wt(t)
  ), _ = (
    /*toast*/
    t[6].readMoreText && gt(t)
  );
  function vt() {
    return (
      /*click_handler*/
      t[4](
        /*toast*/
        t[6]
      )
    );
  }
  return {
    c() {
      e = C("div"), n = C("div"), b && b.c(), s = D(), r = C("p"), c = C("span"), i = G(o), a = D(), _ && _.c(), f = D(), p = C("button"), w = st("svg"), u = st("path"), $ = D(), d(r, "class", l = /*toast*/
      t[6].type === "error" ? "text-sm" : "text-xs"), d(n, "class", h = "flex gap-1 flex-col" + /*toast*/
      (t[6].title ? " mt-2" : "")), d(u, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), d(u, "fill", "currentColor"), d(w, "width", "14"), d(w, "height", "14"), d(w, "viewBox", "0 0 14 14"), d(w, "fill", "none"), d(w, "xmlns", "http://www.w3.org/2000/svg"), d(p, "class", "w-6 h-6 flex justify-center items-center"), d(p, "type", "button"), d(p, "aria-label", g = /*toast*/
      t[6].closeLabel), d(e, "class", k = /*toast*/
      t[6].class);
    },
    m(E, S) {
      V(E, e, S), y(e, n), b && b.m(n, null), y(n, s), y(n, r), y(r, c), y(c, i), y(r, a), _ && _.m(r, null), y(e, f), y(e, p), y(p, w), y(w, u), y(e, $), m = !0, j || (L = Tt(p, "click", vt), j = !0);
    },
    p(E, S) {
      t = E, /*toast*/
      t[6].title ? b ? b.p(t, S) : (b = wt(t), b.c(), b.m(n, s)) : b && (b.d(1), b = null), (!m || S & /*toasts*/
      1) && o !== (o = /*toast*/
      t[6].body + "") && et(i, o), /*toast*/
      t[6].readMoreText ? _ ? _.p(t, S) : (_ = gt(t), _.c(), _.m(r, null)) : _ && (_.d(1), _ = null), (!m || S & /*toasts*/
      1 && l !== (l = /*toast*/
      t[6].type === "error" ? "text-sm" : "text-xs")) && d(r, "class", l), (!m || S & /*toasts*/
      1 && h !== (h = "flex gap-1 flex-col" + /*toast*/
      (t[6].title ? " mt-2" : ""))) && d(n, "class", h), (!m || S & /*toasts*/
      1 && g !== (g = /*toast*/
      t[6].closeLabel)) && d(p, "aria-label", g), (!m || S & /*toasts*/
      1 && k !== (k = /*toast*/
      t[6].class)) && d(e, "class", k);
    },
    i(E) {
      m || (E && R(() => {
        m && (v || (v = dt(e, ht, { x: 100 }, !0)), v.run(1));
      }), m = !0);
    },
    o(E) {
      E && (v || (v = dt(e, ht, { x: 100 }, !1)), v.run(0)), m = !1;
    },
    d(E) {
      E && M(e), b && b.d(), _ && _.d(), E && v && v.end(), j = !1, L();
    }
  };
}
function le(t) {
  let e, n, s = ft(
    /*toasts*/
    t[0]
  ), r = [];
  for (let o = 0; o < s.length; o += 1)
    r[o] = $t(pt(t, s, o));
  const c = (o) => ut(r[o], 1, 1, () => {
    r[o] = null;
  });
  return {
    c() {
      e = C("div");
      for (let o = 0; o < r.length; o += 1)
        r[o].c();
      d(e, "class", "absolute top-14 right-4 overflow-hidden z-[1073] flex flex-col gap-2");
    },
    m(o, i) {
      V(o, e, i);
      for (let a = 0; a < r.length; a += 1)
        r[a] && r[a].m(e, null);
      n = !0;
    },
    p(o, [i]) {
      if (i & /*toasts, closeToast, undefined*/
      3) {
        s = ft(
          /*toasts*/
          o[0]
        );
        let a;
        for (a = 0; a < s.length; a += 1) {
          const l = pt(o, s, a);
          r[a] ? (r[a].p(l, i), H(r[a], 1)) : (r[a] = $t(l), r[a].c(), H(r[a], 1), r[a].m(e, null));
        }
        for (Gt(), a = s.length; a < r.length; a += 1)
          c(a);
        Kt();
      }
    },
    i(o) {
      if (!n) {
        for (let i = 0; i < s.length; i += 1)
          H(r[i]);
        n = !0;
      }
    },
    o(o) {
      r = r.filter(ae);
      for (let i = 0; i < r.length; i += 1)
        ut(r[i]);
      n = !1;
    },
    d(o) {
      o && M(e), At(r, o);
    }
  };
}
const ce = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 w-[335px] left-0";
function ue(t, e, n) {
  let { component: s } = e, r = [], c = 0;
  const o = (l) => {
    n(0, r = r.filter((h) => h.id !== l.id)), s.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, i = (l) => {
    l.isVisible = !0, l.id ?? (l.id = c++);
    const h = [
      l.type === "error" && "bg-eki-light-red border-eki-red py-3",
      !l.type && "bg-eki-white border-eki-light-blue pt-3 pb-5"
    ].filter(Boolean).join(" ");
    l.class = `${ce} ${h}`, n(0, r = [...r, l]), s.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, a = (l) => o(l);
  return t.$$set = (l) => {
    "component" in l && n(2, s = l.component);
  }, [r, o, s, i, a];
}
class de extends ne {
  constructor(e) {
    super(), te(this, e, ue, le, St, { component: 2, addToast: 3 });
  }
  get component() {
    return this.$$.ctx[2];
  }
  set component(e) {
    this.$$set({ component: e }), ot();
  }
  get addToast() {
    return this.$$.ctx[3];
  }
}
customElements.define("eki-toast", ee(de, { component: {} }, [], ["addToast"], !0, se));
