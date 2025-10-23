var Dt = Object.defineProperty;
var Ft = (e, t, n) => t in e ? Dt(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var k = (e, t, n) => Ft(e, typeof t != "symbol" ? t + "" : t, n);
function D() {
}
const Ht = (e) => e;
function Lt(e) {
  return e();
}
function ft() {
  return /* @__PURE__ */ Object.create(null);
}
function z(e) {
  e.forEach(Lt);
}
function ot(e) {
  return typeof e == "function";
}
function jt(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function qt(e) {
  return Object.keys(e).length === 0;
}
function ut(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
const Nt = typeof window < "u";
let Jt = Nt ? () => window.performance.now() : () => Date.now(), st = Nt ? (e) => requestAnimationFrame(e) : D;
const O = /* @__PURE__ */ new Set();
function It(e) {
  O.forEach((t) => {
    t.c(e) || (O.delete(t), t.f());
  }), O.size !== 0 && st(It);
}
function Zt(e) {
  let t;
  return O.size === 0 && st(It), {
    promise: new Promise((n) => {
      O.add(t = { c: e, f: n });
    }),
    abort() {
      O.delete(t);
    }
  };
}
function y(e, t) {
  e.appendChild(t);
}
function Ot(e) {
  if (!e) return document;
  const t = e.getRootNode ? e.getRootNode() : e.ownerDocument;
  return t && /** @type {ShadowRoot} */
  t.host ? (
    /** @type {ShadowRoot} */
    t
  ) : e.ownerDocument;
}
function Gt(e) {
  const t = v("style");
  return t.textContent = "/* empty */", Kt(Ot(e), t), t.sheet;
}
function Kt(e, t) {
  return y(
    /** @type {Document} */
    e.head || e,
    t
  ), t.sheet;
}
function C(e, t, n) {
  e.insertBefore(t, n || null);
}
function x(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function v(e) {
  return document.createElement(e);
}
function ht(e) {
  return document.createElementNS("http://www.w3.org/2000/svg", e);
}
function P(e) {
  return document.createTextNode(e);
}
function F() {
  return P(" ");
}
function At() {
  return P("");
}
function Qt(e, t, n, o) {
  return e.addEventListener(t, n, o), () => e.removeEventListener(t, n, o);
}
function p(e, t, n) {
  n == null ? e.removeAttribute(t) : e.getAttribute(t) !== n && e.setAttribute(t, n);
}
function Wt(e) {
  return Array.from(e.childNodes);
}
function tt(e, t) {
  t = "" + t, e.data !== t && (e.data = /** @type {string} */
  t);
}
function G(e, t, n) {
  e.classList.toggle(t, !!n);
}
function Xt(e, t, { bubbles: n = !1, cancelable: o = !1 } = {}) {
  return new CustomEvent(e, { detail: t, bubbles: n, cancelable: o });
}
function Yt(e) {
  const t = {};
  return e.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      t[n.slot || "default"] = !0;
    }
  ), t;
}
const X = /* @__PURE__ */ new Map();
let Y = 0;
function te(e) {
  let t = 5381, n = e.length;
  for (; n--; ) t = (t << 5) - t ^ e.charCodeAt(n);
  return t >>> 0;
}
function ee(e, t) {
  const n = { stylesheet: Gt(t), rules: {} };
  return X.set(e, n), n;
}
function pt(e, t, n, o, l, s, i, r = 0) {
  const a = 16.666 / o;
  let u = `{
`;
  for (let h = 0; h <= 1; h += a) {
    const b = t + (n - t) * s(h);
    u += h * 100 + `%{${i(b, 1 - b)}}
`;
  }
  const f = u + `100% {${i(n, 1 - n)}}
}`, g = `__svelte_${te(f)}_${r}`, w = Ot(e), { stylesheet: $, rules: c } = X.get(w) || ee(w, e);
  c[g] || (c[g] = !0, $.insertRule(`@keyframes ${g} ${f}`, $.cssRules.length));
  const d = e.style.animation || "";
  return e.style.animation = `${d ? `${d}, ` : ""}${g} ${o}ms linear ${l}ms 1 both`, Y += 1, g;
}
function ne(e, t) {
  const n = (e.style.animation || "").split(", "), o = n.filter(
    t ? (s) => s.indexOf(t) < 0 : (s) => s.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), l = n.length - o.length;
  l && (e.style.animation = o.join(", "), Y -= l, Y || re());
}
function re() {
  st(() => {
    Y || (X.forEach((e) => {
      const { ownerNode: t } = e.stylesheet;
      t && x(t);
    }), X.clear());
  });
}
let lt;
function V(e) {
  lt = e;
}
const I = [], gt = [];
let A = [];
const wt = [], ie = /* @__PURE__ */ Promise.resolve();
let rt = !1;
function oe() {
  rt || (rt = !0, ie.then(q));
}
function H(e) {
  A.push(e);
}
const et = /* @__PURE__ */ new Set();
let N = 0;
function q() {
  if (N !== 0)
    return;
  const e = lt;
  do {
    try {
      for (; N < I.length; ) {
        const t = I[N];
        N++, V(t), se(t.$$);
      }
    } catch (t) {
      throw I.length = 0, N = 0, t;
    }
    for (V(null), I.length = 0, N = 0; gt.length; ) gt.pop()();
    for (let t = 0; t < A.length; t += 1) {
      const n = A[t];
      et.has(n) || (et.add(n), n());
    }
    A.length = 0;
  } while (I.length);
  for (; wt.length; )
    wt.pop()();
  rt = !1, et.clear(), V(e);
}
function se(e) {
  if (e.fragment !== null) {
    e.update(), z(e.before_update);
    const t = e.dirty;
    e.dirty = [-1], e.fragment && e.fragment.p(e.ctx, t), e.after_update.forEach(H);
  }
}
function le(e) {
  const t = [], n = [];
  A.forEach((o) => e.indexOf(o) === -1 ? t.push(o) : n.push(o)), n.forEach((o) => o()), A = t;
}
let R;
function ae() {
  return R || (R = Promise.resolve(), R.then(() => {
    R = null;
  })), R;
}
function nt(e, t, n) {
  e.dispatchEvent(Xt(`${t ? "intro" : "outro"}${n}`));
}
const Q = /* @__PURE__ */ new Set();
let S;
function bt() {
  S = {
    r: 0,
    c: [],
    p: S
    // parent group
  };
}
function $t() {
  S.r || z(S.c), S = S.p;
}
function U(e, t) {
  e && e.i && (Q.delete(e), e.i(t));
}
function J(e, t, n, o) {
  if (e && e.o) {
    if (Q.has(e)) return;
    Q.add(e), S.c.push(() => {
      Q.delete(e), o && (n && e.d(1), o());
    }), e.o(t);
  } else o && o();
}
const ce = { duration: 0 };
function mt(e, t, n, o) {
  let s = t(e, n, { direction: "both" }), i = o ? 0 : 1, r = null, a = null, u = null, f;
  function g() {
    u && ne(e, u);
  }
  function w(c, d) {
    const h = (
      /** @type {Program['d']} */
      c.b - i
    );
    return d *= Math.abs(h), {
      a: i,
      b: c.b,
      d: h,
      duration: d,
      start: c.start,
      end: c.start + d,
      group: c.group
    };
  }
  function $(c) {
    const {
      delay: d = 0,
      duration: h = 300,
      easing: b = Ht,
      tick: _ = D,
      css: L
    } = s || ce, j = {
      start: Jt() + d,
      b: c
    };
    c || (j.group = S, S.r += 1), "inert" in e && (c ? f !== void 0 && (e.inert = f) : (f = /** @type {HTMLElement} */
    e.inert, e.inert = !0)), r || a ? a = j : (L && (g(), u = pt(e, i, c, h, d, b, L)), c && _(0, 1), r = w(j, h), H(() => nt(e, c, "start")), Zt((T) => {
      if (a && T > a.start && (r = w(a, h), a = null, nt(e, r.b, "start"), L && (g(), u = pt(
        e,
        i,
        r.b,
        r.duration,
        0,
        b,
        s.css
      ))), r) {
        if (T >= r.end)
          _(i = r.b, 1 - i), nt(e, r.b, "end"), a || (r.b ? g() : --r.group.r || z(r.group.c)), r = null;
        else if (T >= r.start) {
          const B = T - r.start;
          i = r.a + r.d * b(B / r.duration), _(i, 1 - i);
        }
      }
      return !!(r || a);
    }));
  }
  return {
    run(c) {
      ot(s) ? ae().then(() => {
        s = s({ direction: c ? "in" : "out" }), $(c);
      }) : $(c);
    },
    end() {
      g(), r = a = null;
    }
  };
}
function K(e) {
  return (e == null ? void 0 : e.length) !== void 0 ? e : Array.from(e);
}
function _t(e, t) {
  J(e, 1, 1, () => {
    t.delete(e.key);
  });
}
function yt(e, t, n, o, l, s, i, r, a, u, f, g) {
  let w = e.length, $ = s.length, c = w;
  const d = {};
  for (; c--; ) d[e[c].key] = c;
  const h = [], b = /* @__PURE__ */ new Map(), _ = /* @__PURE__ */ new Map(), L = [];
  for (c = $; c--; ) {
    const m = g(l, s, c), E = n(m);
    let M = i.get(E);
    M ? L.push(() => M.p(m, t)) : (M = u(E, m), M.c()), b.set(E, h[c] = M), E in d && _.set(E, Math.abs(c - d[E]));
  }
  const j = /* @__PURE__ */ new Set(), T = /* @__PURE__ */ new Set();
  function B(m) {
    U(m, 1), m.m(r, f), i.set(m.key, m), f = m.first, $--;
  }
  for (; w && $; ) {
    const m = h[$ - 1], E = e[w - 1], M = m.key, Z = E.key;
    m === E ? (f = m.first, w--, $--) : b.has(Z) ? !i.has(M) || j.has(M) ? B(m) : T.has(Z) ? w-- : _.get(M) > _.get(Z) ? (T.add(M), B(m)) : (j.add(Z), w--) : (a(E, i), w--);
  }
  for (; w--; ) {
    const m = e[w];
    b.has(m.key) || a(m, i);
  }
  for (; $; ) B(h[$ - 1]);
  return z(L), h;
}
function Ut(e) {
  e && e.c();
}
function at(e, t, n) {
  const { fragment: o, after_update: l } = e.$$;
  o && o.m(t, n), H(() => {
    const s = e.$$.on_mount.map(Lt).filter(ot);
    e.$$.on_destroy ? e.$$.on_destroy.push(...s) : z(s), e.$$.on_mount = [];
  }), l.forEach(H);
}
function ct(e, t) {
  const n = e.$$;
  n.fragment !== null && (le(n.after_update), z(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function de(e, t) {
  e.$$.dirty[0] === -1 && (I.push(e), oe(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function Pt(e, t, n, o, l, s, i = null, r = [-1]) {
  const a = lt;
  V(e);
  const u = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: s,
    update: D,
    not_equal: l,
    bound: ft(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(t.context || (a ? a.$$.context : [])),
    // everything else
    callbacks: ft(),
    dirty: r,
    skip_bound: !1,
    root: t.target || a.$$.root
  };
  i && i(u.root);
  let f = !1;
  if (u.ctx = n ? n(e, t.props || {}, (g, w, ...$) => {
    const c = $.length ? $[0] : w;
    return u.ctx && l(u.ctx[g], u.ctx[g] = c) && (!u.skip_bound && u.bound[g] && u.bound[g](c), f && de(e, g)), w;
  }) : [], u.update(), f = !0, z(u.before_update), u.fragment = o ? o(u.ctx) : !1, t.target) {
    if (t.hydrate) {
      const g = Wt(t.target);
      u.fragment && u.fragment.l(g), g.forEach(x);
    } else
      u.fragment && u.fragment.c();
    t.intro && U(e.$$.fragment), at(e, t.target, t.anchor), q();
  }
  V(a);
}
let Bt;
typeof HTMLElement == "function" && (Bt = class extends HTMLElement {
  constructor(t, n, o) {
    super();
    /** The Svelte component constructor */
    k(this, "$$ctor");
    /** Slots */
    k(this, "$$s");
    /** The Svelte component instance */
    k(this, "$$c");
    /** Whether or not the custom element is connected */
    k(this, "$$cn", !1);
    /** Component props data */
    k(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    k(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    k(this, "$$p_d", {});
    /** @type {Record<string, Function[]>} Event listeners */
    k(this, "$$l", {});
    /** @type {Map<Function, Function>} Event listener unsubscribe functions */
    k(this, "$$l_u", /* @__PURE__ */ new Map());
    this.$$ctor = t, this.$$s = n, o && this.attachShadow({ mode: "open" });
  }
  addEventListener(t, n, o) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(n), this.$$c) {
      const l = this.$$c.$on(t, n);
      this.$$l_u.set(n, l);
    }
    super.addEventListener(t, n, o);
  }
  removeEventListener(t, n, o) {
    if (super.removeEventListener(t, n, o), this.$$c) {
      const l = this.$$l_u.get(n);
      l && (l(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(i) {
        return () => {
          let r;
          return {
            c: function() {
              r = v("slot"), i !== "default" && p(r, "name", i);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(f, g) {
              C(f, r, g);
            },
            d: function(f) {
              f && x(r);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const o = {}, l = Yt(this);
      for (const i of this.$$s)
        i in l && (o[i] = [n(i)]);
      for (const i of this.attributes) {
        const r = this.$$g_p(i.name);
        r in this.$$d || (this.$$d[r] = W(r, i.value, this.$$p_d, "toProp"));
      }
      for (const i in this.$$p_d)
        !(i in this.$$d) && this[i] !== void 0 && (this.$$d[i] = this[i], delete this[i]);
      this.$$c = new this.$$ctor({
        target: this.shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: o,
          $$scope: {
            ctx: []
          }
        }
      });
      const s = () => {
        this.$$r = !0;
        for (const i in this.$$p_d)
          if (this.$$d[i] = this.$$c.$$.ctx[this.$$c.$$.props[i]], this.$$p_d[i].reflect) {
            const r = W(
              i,
              this.$$d[i],
              this.$$p_d,
              "toAttribute"
            );
            r == null ? this.removeAttribute(this.$$p_d[i].attribute || i) : this.setAttribute(this.$$p_d[i].attribute || i, r);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(s), s();
      for (const i in this.$$l)
        for (const r of this.$$l[i]) {
          const a = this.$$c.$on(i, r);
          this.$$l_u.set(r, a);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, o) {
    var l;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = W(t, o, this.$$p_d, "toProp"), (l = this.$$c) == null || l.$set({ [t]: this.$$d[t] }));
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
function W(e, t, n, o) {
  var s;
  const l = (s = n[e]) == null ? void 0 : s.type;
  if (t = l === "Boolean" && typeof t != "boolean" ? t != null : t, !o || !n[e])
    return t;
  if (o === "toAttribute")
    switch (l) {
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
    switch (l) {
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
function Rt(e, t, n, o, l, s) {
  let i = class extends Bt {
    constructor() {
      super(e, n, l), this.$$p_d = t;
    }
    static get observedAttributes() {
      return Object.keys(t).map(
        (r) => (t[r].attribute || r).toLowerCase()
      );
    }
  };
  return Object.keys(t).forEach((r) => {
    Object.defineProperty(i.prototype, r, {
      get() {
        return this.$$c && r in this.$$c ? this.$$c[r] : this.$$d[r];
      },
      set(a) {
        var u;
        a = W(r, a, t), this.$$d[r] = a, (u = this.$$c) == null || u.$set({ [r]: a });
      }
    });
  }), o.forEach((r) => {
    Object.defineProperty(i.prototype, r, {
      get() {
        var a;
        return (a = this.$$c) == null ? void 0 : a[r];
      }
    });
  }), s && (i = s(i)), e.element = /** @type {any} */
  i, i;
}
class Vt {
  constructor() {
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    k(this, "$$");
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    k(this, "$$set");
  }
  /** @returns {void} */
  $destroy() {
    ct(this, 1), this.$destroy = D;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(t, n) {
    if (!ot(n))
      return D;
    const o = this.$$.callbacks[t] || (this.$$.callbacks[t] = []);
    return o.push(n), () => {
      const l = o.indexOf(n);
      l !== -1 && o.splice(l, 1);
    };
  }
  /**
   * @param {Partial<Props>} props
   * @returns {void}
   */
  $set(t) {
    this.$$set && !qt(t) && (this.$$.skip_bound = !0, this.$$set(t), this.$$.skip_bound = !1);
  }
}
const fe = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(fe);
const ue = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.max-w-\\[335px\\]{max-width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}@media (min-width: 320px){.min-\\[320px\\]\\:right-4{right:1rem}}', it = new CSSStyleSheet();
it.replaceSync(ue);
const he = (e) => class extends e {
  constructor() {
    super(...arguments);
    k(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && it && (this.shadowRoot.adoptedStyleSheets = [it]);
  }
};
function pe(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function kt(e, { delay: t = 0, duration: n = 400, easing: o = pe, x: l = 0, y: s = 0, opacity: i = 0 } = {}) {
  const r = getComputedStyle(e), a = +r.opacity, u = r.transform === "none" ? "" : r.transform, f = a * (1 - i), [g, w] = ut(l), [$, c] = ut(s);
  return {
    delay: t,
    duration: n,
    easing: o,
    css: (d, h) => `
			transform: ${u} translate(${(1 - d) * g}${w}, ${(1 - d) * $}${c});
			opacity: ${a - f * h}`
  };
}
function vt(e) {
  let t, n = (
    /*toast*/
    e[0].title + ""
  ), o, l, s, i = !/*toast*/
  e[0].body && /*toast*/
  e[0].readMoreText && xt(e);
  return {
    c() {
      t = v("p"), o = P(n), l = F(), i && i.c(), p(t, "class", "text-sm font-medium"), p(t, "id", s = `toast-title-${/*toast*/
      e[0].id}`);
    },
    m(r, a) {
      C(r, t, a), y(t, o), y(t, l), i && i.m(t, null);
    },
    p(r, a) {
      a & /*toast*/
      1 && n !== (n = /*toast*/
      r[0].title + "") && tt(o, n), !/*toast*/
      r[0].body && /*toast*/
      r[0].readMoreText ? i ? i.p(r, a) : (i = xt(r), i.c(), i.m(t, null)) : i && (i.d(1), i = null), a & /*toast*/
      1 && s !== (s = `toast-title-${/*toast*/
      r[0].id}`) && p(t, "id", s);
    },
    d(r) {
      r && x(t), i && i.d();
    }
  };
}
function xt(e) {
  let t, n = (
    /*toast*/
    e[0].readMoreText + ""
  ), o, l, s, i;
  return {
    c() {
      t = v("a"), o = P(n), p(t, "class", "underline hover:no-underline"), p(t, "href", l = /*toast*/
      e[0].readMoreUrl), p(t, "target", s = /*toast*/
      e[0].readMoreIsExternal ? "_blank" : void 0), p(t, "rel", i = /*toast*/
      e[0].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(r, a) {
      C(r, t, a), y(t, o);
    },
    p(r, a) {
      a & /*toast*/
      1 && n !== (n = /*toast*/
      r[0].readMoreText + "") && tt(o, n), a & /*toast*/
      1 && l !== (l = /*toast*/
      r[0].readMoreUrl) && p(t, "href", l), a & /*toast*/
      1 && s !== (s = /*toast*/
      r[0].readMoreIsExternal ? "_blank" : void 0) && p(t, "target", s), a & /*toast*/
      1 && i !== (i = /*toast*/
      r[0].readMoreIsExternal ? "noreferrer" : void 0) && p(t, "rel", i);
    },
    d(r) {
      r && x(t);
    }
  };
}
function Et(e) {
  let t, n, o = (
    /*toast*/
    e[0].body + ""
  ), l, s, i, r = (
    /*toast*/
    e[0].readMoreText && Mt(e)
  );
  return {
    c() {
      t = v("p"), n = v("span"), l = P(o), i = F(), r && r.c(), p(n, "id", s = `toast-body-${/*toast*/
      e[0].id}`), p(t, "class", "break-word text-sm");
    },
    m(a, u) {
      C(a, t, u), y(t, n), y(n, l), y(t, i), r && r.m(t, null);
    },
    p(a, u) {
      u & /*toast*/
      1 && o !== (o = /*toast*/
      a[0].body + "") && tt(l, o), u & /*toast*/
      1 && s !== (s = `toast-body-${/*toast*/
      a[0].id}`) && p(n, "id", s), /*toast*/
      a[0].readMoreText ? r ? r.p(a, u) : (r = Mt(a), r.c(), r.m(t, null)) : r && (r.d(1), r = null);
    },
    d(a) {
      a && x(t), r && r.d();
    }
  };
}
function Mt(e) {
  let t, n = (
    /*toast*/
    e[0].readMoreText + ""
  ), o, l, s, i;
  return {
    c() {
      t = v("a"), o = P(n), p(t, "class", "underline hover:no-underline"), p(t, "href", l = /*toast*/
      e[0].readMoreUrl), p(t, "target", s = /*toast*/
      e[0].readMoreIsExternal ? "_blank" : void 0), p(t, "rel", i = /*toast*/
      e[0].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(r, a) {
      C(r, t, a), y(t, o);
    },
    p(r, a) {
      a & /*toast*/
      1 && n !== (n = /*toast*/
      r[0].readMoreText + "") && tt(o, n), a & /*toast*/
      1 && l !== (l = /*toast*/
      r[0].readMoreUrl) && p(t, "href", l), a & /*toast*/
      1 && s !== (s = /*toast*/
      r[0].readMoreIsExternal ? "_blank" : void 0) && p(t, "target", s), a & /*toast*/
      1 && i !== (i = /*toast*/
      r[0].readMoreIsExternal ? "noreferrer" : void 0) && p(t, "rel", i);
    },
    d(r) {
      r && x(t);
    }
  };
}
function ge(e) {
  let t, n, o, l, s, i, r, a, u, f, g, w, $, c, d = (
    /*toast*/
    e[0].title && vt(e)
  ), h = (
    /*toast*/
    e[0].body && Et(e)
  );
  return {
    c() {
      t = v("section"), n = v("div"), d && d.c(), o = F(), h && h.c(), l = F(), s = v("button"), i = ht("svg"), r = ht("path"), p(n, "class", "flex gap-1"), G(
        n,
        "flex-col",
        /*toast*/
        e[0].body
      ), G(
        n,
        "mt-2",
        /*toast*/
        e[0].title
      ), p(r, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), p(r, "fill", "currentColor"), p(i, "width", "14"), p(i, "height", "14"), p(i, "viewBox", "0 0 14 14"), p(i, "fill", "none"), p(i, "xmlns", "http://www.w3.org/2000/svg"), p(s, "class", "w-6 h-6 flex justify-center items-center"), p(s, "type", "button"), p(s, "aria-label", a = /*toast*/
      e[0].closeLabel), p(t, "class", u = /*toast*/
      e[0].class), p(t, "aria-labelledby", f = `toast-title-${/*toast*/
      e[0].id} toast-body-${/*toast*/
      e[0].id}`);
    },
    m(b, _) {
      C(b, t, _), y(t, n), d && d.m(n, null), y(n, o), h && h.m(n, null), y(t, l), y(t, s), y(s, i), y(i, r), w = !0, $ || (c = Qt(
        s,
        "click",
        /*click_handler*/
        e[2]
      ), $ = !0);
    },
    p(b, [_]) {
      /*toast*/
      b[0].title ? d ? d.p(b, _) : (d = vt(b), d.c(), d.m(n, o)) : d && (d.d(1), d = null), /*toast*/
      b[0].body ? h ? h.p(b, _) : (h = Et(b), h.c(), h.m(n, null)) : h && (h.d(1), h = null), (!w || _ & /*toast*/
      1) && G(
        n,
        "flex-col",
        /*toast*/
        b[0].body
      ), (!w || _ & /*toast*/
      1) && G(
        n,
        "mt-2",
        /*toast*/
        b[0].title
      ), (!w || _ & /*toast*/
      1 && a !== (a = /*toast*/
      b[0].closeLabel)) && p(s, "aria-label", a), (!w || _ & /*toast*/
      1 && u !== (u = /*toast*/
      b[0].class)) && p(t, "class", u), (!w || _ & /*toast*/
      1 && f !== (f = `toast-title-${/*toast*/
      b[0].id} toast-body-${/*toast*/
      b[0].id}`)) && p(t, "aria-labelledby", f);
    },
    i(b) {
      w || (b && H(() => {
        w && (g || (g = mt(t, kt, { x: 100 }, !0)), g.run(1));
      }), w = !0);
    },
    o(b) {
      b && (g || (g = mt(t, kt, { x: 100 }, !1)), g.run(0)), w = !1;
    },
    d(b) {
      b && x(t), d && d.d(), h && h.d(), b && g && g.end(), $ = !1, c();
    }
  };
}
function we(e, t, n) {
  let { toast: o } = t, { closeToast: l } = t;
  const s = () => l(o);
  return e.$$set = (i) => {
    "toast" in i && n(0, o = i.toast), "closeToast" in i && n(1, l = i.closeToast);
  }, [o, l, s];
}
class dt extends Vt {
  constructor(t) {
    super(), Pt(this, t, we, ge, jt, { toast: 0, closeToast: 1 });
  }
  get toast() {
    return this.$$.ctx[0];
  }
  set toast(t) {
    this.$$set({ toast: t }), q();
  }
  get closeToast() {
    return this.$$.ctx[1];
  }
  set closeToast(t) {
    this.$$set({ closeToast: t }), q();
  }
}
Rt(dt, { toast: {}, closeToast: {} }, [], [], !0);
function St(e, t, n) {
  const o = e.slice();
  return o[7] = t[n], o;
}
function Ct(e, t, n) {
  const o = e.slice();
  return o[7] = t[n], o;
}
function Tt(e, t) {
  let n, o, l;
  return o = new dt({
    props: {
      toast: (
        /*toast*/
        t[7]
      ),
      closeToast: (
        /*closeToast*/
        t[2]
      )
    }
  }), {
    key: e,
    first: null,
    c() {
      n = At(), Ut(o.$$.fragment), this.first = n;
    },
    m(s, i) {
      C(s, n, i), at(o, s, i), l = !0;
    },
    p(s, i) {
      t = s;
      const r = {};
      i & /*alertToasts*/
      2 && (r.toast = /*toast*/
      t[7]), o.$set(r);
    },
    i(s) {
      l || (U(o.$$.fragment, s), l = !0);
    },
    o(s) {
      J(o.$$.fragment, s), l = !1;
    },
    d(s) {
      s && x(n), ct(o, s);
    }
  };
}
function zt(e, t) {
  let n, o, l;
  return o = new dt({
    props: {
      toast: (
        /*toast*/
        t[7]
      ),
      closeToast: (
        /*closeToast*/
        t[2]
      )
    }
  }), {
    key: e,
    first: null,
    c() {
      n = At(), Ut(o.$$.fragment), this.first = n;
    },
    m(s, i) {
      C(s, n, i), at(o, s, i), l = !0;
    },
    p(s, i) {
      t = s;
      const r = {};
      i & /*statusToasts*/
      1 && (r.toast = /*toast*/
      t[7]), o.$set(r);
    },
    i(s) {
      l || (U(o.$$.fragment, s), l = !0);
    },
    o(s) {
      J(o.$$.fragment, s), l = !1;
    },
    d(s) {
      s && x(n), ct(o, s);
    }
  };
}
function be(e) {
  let t, n, o = [], l = /* @__PURE__ */ new Map(), s, i, r = [], a = /* @__PURE__ */ new Map(), u, f = K(
    /*alertToasts*/
    e[1]
  );
  const g = (c) => (
    /*toast*/
    c[7].id
  );
  for (let c = 0; c < f.length; c += 1) {
    let d = Ct(e, f, c), h = g(d);
    l.set(h, o[c] = Tt(h, d));
  }
  let w = K(
    /*statusToasts*/
    e[0]
  );
  const $ = (c) => (
    /*toast*/
    c[7].id
  );
  for (let c = 0; c < w.length; c += 1) {
    let d = St(e, w, c), h = $(d);
    a.set(h, r[c] = zt(h, d));
  }
  return {
    c() {
      t = v("div"), n = v("div");
      for (let c = 0; c < o.length; c += 1)
        o[c].c();
      s = F(), i = v("div");
      for (let c = 0; c < r.length; c += 1)
        r[c].c();
      p(n, "class", "flex flex-col gap-2"), p(n, "role", "alert"), p(i, "class", "flex flex-col gap-2"), p(i, "role", "status"), p(t, "class", "absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2");
    },
    m(c, d) {
      C(c, t, d), y(t, n);
      for (let h = 0; h < o.length; h += 1)
        o[h] && o[h].m(n, null);
      y(t, s), y(t, i);
      for (let h = 0; h < r.length; h += 1)
        r[h] && r[h].m(i, null);
      u = !0;
    },
    p(c, [d]) {
      d & /*alertToasts, closeToast*/
      6 && (f = K(
        /*alertToasts*/
        c[1]
      ), bt(), o = yt(o, d, g, 1, c, f, l, n, _t, Tt, null, Ct), $t()), d & /*statusToasts, closeToast*/
      5 && (w = K(
        /*statusToasts*/
        c[0]
      ), bt(), r = yt(r, d, $, 1, c, w, a, i, _t, zt, null, St), $t());
    },
    i(c) {
      if (!u) {
        for (let d = 0; d < f.length; d += 1)
          U(o[d]);
        for (let d = 0; d < w.length; d += 1)
          U(r[d]);
        u = !0;
      }
    },
    o(c) {
      for (let d = 0; d < o.length; d += 1)
        J(o[d]);
      for (let d = 0; d < r.length; d += 1)
        J(r[d]);
      u = !1;
    },
    d(c) {
      c && x(t);
      for (let d = 0; d < o.length; d += 1)
        o[d].d();
      for (let d = 0; d < r.length; d += 1)
        r[d].d();
    }
  };
}
const $e = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0";
function me(e, t, n) {
  let o, l, { component: s } = t, i = [], r = 0;
  const a = (f) => {
    n(5, i = i.filter((g) => g.id !== f.id)), s.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: f
      }
    ));
  }, u = (f) => {
    f.isVisible = !0, f.id ?? (f.id = r++);
    const g = [
      f.type === "error" && "bg-eki-light-red border-eki-red py-3",
      f.type === "success" && "bg-eki-light-green border-eki-green py-3",
      f.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    f.class = `${$e} ${g}`, n(5, i = [...i, f]), s.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: f
      }
    ));
  };
  return e.$$set = (f) => {
    "component" in f && n(3, s = f.component);
  }, e.$$.update = () => {
    e.$$.dirty & /*toasts*/
    32 && n(1, o = i.filter((f) => f.type && ["error", "warning"].includes(f.type))), e.$$.dirty & /*toasts*/
    32 && n(0, l = i.filter((f) => !f.type || !["error", "warning"].includes(f.type)));
  }, [l, o, a, s, u, i];
}
class _e extends Vt {
  constructor(t) {
    super(), Pt(this, t, me, be, jt, { component: 3, addToast: 4 });
  }
  get component() {
    return this.$$.ctx[3];
  }
  set component(t) {
    this.$$set({ component: t }), q();
  }
  get addToast() {
    return this.$$.ctx[4];
  }
}
customElements.define("eki-toast", Rt(_e, { component: {} }, [], ["addToast"], !0, he));
