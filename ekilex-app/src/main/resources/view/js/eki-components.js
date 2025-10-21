var Dt = Object.defineProperty;
var Ft = (e, t, n) => t in e ? Dt(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var y = (e, t, n) => Ft(e, typeof t != "symbol" ? t + "" : t, n);
function D() {
}
const Ht = (e) => e;
function Lt(e) {
  return e();
}
function ut() {
  return /* @__PURE__ */ Object.create(null);
}
function z(e) {
  e.forEach(Lt);
}
function it(e) {
  return typeof e == "function";
}
function jt(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function qt(e) {
  return Object.keys(e).length === 0;
}
function dt(e) {
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
function _(e, t) {
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
  const t = k("style");
  return t.textContent = "/* empty */", Kt(Ot(e), t), t.sheet;
}
function Kt(e, t) {
  return _(
    /** @type {Document} */
    e.head || e,
    t
  ), t.sheet;
}
function S(e, t, n) {
  e.insertBefore(t, n || null);
}
function v(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function k(e) {
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
function Qt(e, t, n, i) {
  return e.addEventListener(t, n, i), () => e.removeEventListener(t, n, i);
}
function g(e, t, n) {
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
function Xt(e, t, { bubbles: n = !1, cancelable: i = !1 } = {}) {
  return new CustomEvent(e, { detail: t, bubbles: n, cancelable: i });
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
function pt(e, t, n, i, a, s, r, o = 0) {
  const f = 16.666 / i;
  let h = `{
`;
  for (let d = 0; d <= 1; d += f) {
    const m = t + (n - t) * s(d);
    h += d * 100 + `%{${r(m, 1 - m)}}
`;
  }
  const u = h + `100% {${r(n, 1 - n)}}
}`, p = `__svelte_${te(u)}_${o}`, w = Ot(e), { stylesheet: b, rules: l } = X.get(w) || ee(w, e);
  l[p] || (l[p] = !0, b.insertRule(`@keyframes ${p} ${u}`, b.cssRules.length));
  const c = e.style.animation || "";
  return e.style.animation = `${c ? `${c}, ` : ""}${p} ${i}ms linear ${a}ms 1 both`, Y += 1, p;
}
function ne(e, t) {
  const n = (e.style.animation || "").split(", "), i = n.filter(
    t ? (s) => s.indexOf(t) < 0 : (s) => s.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), a = n.length - i.length;
  a && (e.style.animation = i.join(", "), Y -= a, Y || re());
}
function re() {
  st(() => {
    Y || (X.forEach((e) => {
      const { ownerNode: t } = e.stylesheet;
      t && v(t);
    }), X.clear());
  });
}
let lt;
function V(e) {
  lt = e;
}
const I = [], gt = [];
let A = [];
const wt = [], oe = /* @__PURE__ */ Promise.resolve();
let rt = !1;
function ie() {
  rt || (rt = !0, oe.then(q));
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
  A.forEach((i) => e.indexOf(i) === -1 ? t.push(i) : n.push(i)), n.forEach((i) => i()), A = t;
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
let M;
function bt() {
  M = {
    r: 0,
    c: [],
    p: M
    // parent group
  };
}
function mt() {
  M.r || z(M.c), M = M.p;
}
function U(e, t) {
  e && e.i && (Q.delete(e), e.i(t));
}
function J(e, t, n, i) {
  if (e && e.o) {
    if (Q.has(e)) return;
    Q.add(e), M.c.push(() => {
      Q.delete(e), i && (n && e.d(1), i());
    }), e.o(t);
  } else i && i();
}
const ce = { duration: 0 };
function $t(e, t, n, i) {
  let s = t(e, n, { direction: "both" }), r = i ? 0 : 1, o = null, f = null, h = null, u;
  function p() {
    h && ne(e, h);
  }
  function w(l, c) {
    const d = (
      /** @type {Program['d']} */
      l.b - r
    );
    return c *= Math.abs(d), {
      a: r,
      b: l.b,
      d,
      duration: c,
      start: l.start,
      end: l.start + c,
      group: l.group
    };
  }
  function b(l) {
    const {
      delay: c = 0,
      duration: d = 300,
      easing: m = Ht,
      tick: C = D,
      css: L
    } = s || ce, j = {
      start: Jt() + c,
      b: l
    };
    l || (j.group = M, M.r += 1), "inert" in e && (l ? u !== void 0 && (e.inert = u) : (u = /** @type {HTMLElement} */
    e.inert, e.inert = !0)), o || f ? f = j : (L && (p(), h = pt(e, r, l, d, c, m, L)), l && C(0, 1), o = w(j, d), H(() => nt(e, l, "start")), Zt((T) => {
      if (f && T > f.start && (o = w(f, d), f = null, nt(e, o.b, "start"), L && (p(), h = pt(
        e,
        r,
        o.b,
        o.duration,
        0,
        m,
        s.css
      ))), o) {
        if (T >= o.end)
          C(r = o.b, 1 - r), nt(e, o.b, "end"), f || (o.b ? p() : --o.group.r || z(o.group.c)), o = null;
        else if (T >= o.start) {
          const B = T - o.start;
          r = o.a + o.d * m(B / o.duration), C(r, 1 - r);
        }
      }
      return !!(o || f);
    }));
  }
  return {
    run(l) {
      it(s) ? ae().then(() => {
        s = s({ direction: l ? "in" : "out" }), b(l);
      }) : b(l);
    },
    end() {
      p(), o = f = null;
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
function yt(e, t, n, i, a, s, r, o, f, h, u, p) {
  let w = e.length, b = s.length, l = w;
  const c = {};
  for (; l--; ) c[e[l].key] = l;
  const d = [], m = /* @__PURE__ */ new Map(), C = /* @__PURE__ */ new Map(), L = [];
  for (l = b; l--; ) {
    const $ = p(a, s, l), x = n($);
    let E = r.get(x);
    E ? L.push(() => E.p($, t)) : (E = h(x, $), E.c()), m.set(x, d[l] = E), x in c && C.set(x, Math.abs(l - c[x]));
  }
  const j = /* @__PURE__ */ new Set(), T = /* @__PURE__ */ new Set();
  function B($) {
    U($, 1), $.m(o, u), r.set($.key, $), u = $.first, b--;
  }
  for (; w && b; ) {
    const $ = d[b - 1], x = e[w - 1], E = $.key, Z = x.key;
    $ === x ? (u = $.first, w--, b--) : m.has(Z) ? !r.has(E) || j.has(E) ? B($) : T.has(Z) ? w-- : C.get(E) > C.get(Z) ? (T.add(E), B($)) : (j.add(Z), w--) : (f(x, r), w--);
  }
  for (; w--; ) {
    const $ = e[w];
    m.has($.key) || f($, r);
  }
  for (; b; ) B(d[b - 1]);
  return z(L), d;
}
function Ut(e) {
  e && e.c();
}
function at(e, t, n) {
  const { fragment: i, after_update: a } = e.$$;
  i && i.m(t, n), H(() => {
    const s = e.$$.on_mount.map(Lt).filter(it);
    e.$$.on_destroy ? e.$$.on_destroy.push(...s) : z(s), e.$$.on_mount = [];
  }), a.forEach(H);
}
function ct(e, t) {
  const n = e.$$;
  n.fragment !== null && (le(n.after_update), z(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function fe(e, t) {
  e.$$.dirty[0] === -1 && (I.push(e), ie(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function Pt(e, t, n, i, a, s, r = null, o = [-1]) {
  const f = lt;
  V(e);
  const h = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: s,
    update: D,
    not_equal: a,
    bound: ut(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(t.context || (f ? f.$$.context : [])),
    // everything else
    callbacks: ut(),
    dirty: o,
    skip_bound: !1,
    root: t.target || f.$$.root
  };
  r && r(h.root);
  let u = !1;
  if (h.ctx = n ? n(e, t.props || {}, (p, w, ...b) => {
    const l = b.length ? b[0] : w;
    return h.ctx && a(h.ctx[p], h.ctx[p] = l) && (!h.skip_bound && h.bound[p] && h.bound[p](l), u && fe(e, p)), w;
  }) : [], h.update(), u = !0, z(h.before_update), h.fragment = i ? i(h.ctx) : !1, t.target) {
    if (t.hydrate) {
      const p = Wt(t.target);
      h.fragment && h.fragment.l(p), p.forEach(v);
    } else
      h.fragment && h.fragment.c();
    t.intro && U(e.$$.fragment), at(e, t.target, t.anchor), q();
  }
  V(f);
}
let Bt;
typeof HTMLElement == "function" && (Bt = class extends HTMLElement {
  constructor(t, n, i) {
    super();
    /** The Svelte component constructor */
    y(this, "$$ctor");
    /** Slots */
    y(this, "$$s");
    /** The Svelte component instance */
    y(this, "$$c");
    /** Whether or not the custom element is connected */
    y(this, "$$cn", !1);
    /** Component props data */
    y(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    y(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    y(this, "$$p_d", {});
    /** @type {Record<string, Function[]>} Event listeners */
    y(this, "$$l", {});
    /** @type {Map<Function, Function>} Event listener unsubscribe functions */
    y(this, "$$l_u", /* @__PURE__ */ new Map());
    this.$$ctor = t, this.$$s = n, i && this.attachShadow({ mode: "open" });
  }
  addEventListener(t, n, i) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(n), this.$$c) {
      const a = this.$$c.$on(t, n);
      this.$$l_u.set(n, a);
    }
    super.addEventListener(t, n, i);
  }
  removeEventListener(t, n, i) {
    if (super.removeEventListener(t, n, i), this.$$c) {
      const a = this.$$l_u.get(n);
      a && (a(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(r) {
        return () => {
          let o;
          return {
            c: function() {
              o = k("slot"), r !== "default" && g(o, "name", r);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(u, p) {
              S(u, o, p);
            },
            d: function(u) {
              u && v(o);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const i = {}, a = Yt(this);
      for (const r of this.$$s)
        r in a && (i[r] = [n(r)]);
      for (const r of this.attributes) {
        const o = this.$$g_p(r.name);
        o in this.$$d || (this.$$d[o] = W(o, r.value, this.$$p_d, "toProp"));
      }
      for (const r in this.$$p_d)
        !(r in this.$$d) && this[r] !== void 0 && (this.$$d[r] = this[r], delete this[r]);
      this.$$c = new this.$$ctor({
        target: this.shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: i,
          $$scope: {
            ctx: []
          }
        }
      });
      const s = () => {
        this.$$r = !0;
        for (const r in this.$$p_d)
          if (this.$$d[r] = this.$$c.$$.ctx[this.$$c.$$.props[r]], this.$$p_d[r].reflect) {
            const o = W(
              r,
              this.$$d[r],
              this.$$p_d,
              "toAttribute"
            );
            o == null ? this.removeAttribute(this.$$p_d[r].attribute || r) : this.setAttribute(this.$$p_d[r].attribute || r, o);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(s), s();
      for (const r in this.$$l)
        for (const o of this.$$l[r]) {
          const f = this.$$c.$on(r, o);
          this.$$l_u.set(o, f);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, i) {
    var a;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = W(t, i, this.$$p_d, "toProp"), (a = this.$$c) == null || a.$set({ [t]: this.$$d[t] }));
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
function W(e, t, n, i) {
  var s;
  const a = (s = n[e]) == null ? void 0 : s.type;
  if (t = a === "Boolean" && typeof t != "boolean" ? t != null : t, !i || !n[e])
    return t;
  if (i === "toAttribute")
    switch (a) {
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
    switch (a) {
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
function Rt(e, t, n, i, a, s) {
  let r = class extends Bt {
    constructor() {
      super(e, n, a), this.$$p_d = t;
    }
    static get observedAttributes() {
      return Object.keys(t).map(
        (o) => (t[o].attribute || o).toLowerCase()
      );
    }
  };
  return Object.keys(t).forEach((o) => {
    Object.defineProperty(r.prototype, o, {
      get() {
        return this.$$c && o in this.$$c ? this.$$c[o] : this.$$d[o];
      },
      set(f) {
        var h;
        f = W(o, f, t), this.$$d[o] = f, (h = this.$$c) == null || h.$set({ [o]: f });
      }
    });
  }), i.forEach((o) => {
    Object.defineProperty(r.prototype, o, {
      get() {
        var f;
        return (f = this.$$c) == null ? void 0 : f[o];
      }
    });
  }), s && (r = s(r)), e.element = /** @type {any} */
  r, r;
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
    y(this, "$$");
    /**
     * ### PRIVATE API
     *
     * Do not use, may change at any time
     *
     * @type {any}
     */
    y(this, "$$set");
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
    if (!it(n))
      return D;
    const i = this.$$.callbacks[t] || (this.$$.callbacks[t] = []);
    return i.push(n), () => {
      const a = i.indexOf(n);
      a !== -1 && i.splice(a, 1);
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
const ue = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(ue);
const de = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.max-w-\\[335px\\]{max-width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}@media (min-width: 320px){.min-\\[320px\\]\\:right-4{right:1rem}}', ot = new CSSStyleSheet();
ot.replaceSync(de);
const he = (e) => class extends e {
  constructor() {
    super(...arguments);
    y(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && ot && (this.shadowRoot.adoptedStyleSheets = [ot]);
  }
};
function pe(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function kt(e, { delay: t = 0, duration: n = 400, easing: i = pe, x: a = 0, y: s = 0, opacity: r = 0 } = {}) {
  const o = getComputedStyle(e), f = +o.opacity, h = o.transform === "none" ? "" : o.transform, u = f * (1 - r), [p, w] = dt(a), [b, l] = dt(s);
  return {
    delay: t,
    duration: n,
    easing: i,
    css: (c, d) => `
			transform: ${h} translate(${(1 - c) * p}${w}, ${(1 - c) * b}${l});
			opacity: ${f - u * d}`
  };
}
function vt(e) {
  let t, n = (
    /*toast*/
    e[0].title + ""
  ), i, a, s = !/*toast*/
  e[0].body && /*toast*/
  e[0].readMoreText && xt(e);
  return {
    c() {
      t = k("p"), i = P(n), a = F(), s && s.c(), g(t, "class", "text-sm font-medium");
    },
    m(r, o) {
      S(r, t, o), _(t, i), _(t, a), s && s.m(t, null);
    },
    p(r, o) {
      o & /*toast*/
      1 && n !== (n = /*toast*/
      r[0].title + "") && tt(i, n), !/*toast*/
      r[0].body && /*toast*/
      r[0].readMoreText ? s ? s.p(r, o) : (s = xt(r), s.c(), s.m(t, null)) : s && (s.d(1), s = null);
    },
    d(r) {
      r && v(t), s && s.d();
    }
  };
}
function xt(e) {
  let t, n = (
    /*toast*/
    e[0].readMoreText + ""
  ), i, a, s, r;
  return {
    c() {
      t = k("a"), i = P(n), g(t, "class", "underline hover:no-underline"), g(t, "href", a = /*toast*/
      e[0].readMoreUrl), g(t, "target", s = /*toast*/
      e[0].readMoreIsExternal ? "_blank" : void 0), g(t, "rel", r = /*toast*/
      e[0].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(o, f) {
      S(o, t, f), _(t, i);
    },
    p(o, f) {
      f & /*toast*/
      1 && n !== (n = /*toast*/
      o[0].readMoreText + "") && tt(i, n), f & /*toast*/
      1 && a !== (a = /*toast*/
      o[0].readMoreUrl) && g(t, "href", a), f & /*toast*/
      1 && s !== (s = /*toast*/
      o[0].readMoreIsExternal ? "_blank" : void 0) && g(t, "target", s), f & /*toast*/
      1 && r !== (r = /*toast*/
      o[0].readMoreIsExternal ? "noreferrer" : void 0) && g(t, "rel", r);
    },
    d(o) {
      o && v(t);
    }
  };
}
function Et(e) {
  let t, n, i = (
    /*toast*/
    e[0].body + ""
  ), a, s, r = (
    /*toast*/
    e[0].readMoreText && Mt(e)
  );
  return {
    c() {
      t = k("p"), n = k("span"), a = P(i), s = F(), r && r.c(), g(t, "class", "break-word text-sm");
    },
    m(o, f) {
      S(o, t, f), _(t, n), _(n, a), _(t, s), r && r.m(t, null);
    },
    p(o, f) {
      f & /*toast*/
      1 && i !== (i = /*toast*/
      o[0].body + "") && tt(a, i), /*toast*/
      o[0].readMoreText ? r ? r.p(o, f) : (r = Mt(o), r.c(), r.m(t, null)) : r && (r.d(1), r = null);
    },
    d(o) {
      o && v(t), r && r.d();
    }
  };
}
function Mt(e) {
  let t, n = (
    /*toast*/
    e[0].readMoreText + ""
  ), i, a, s, r;
  return {
    c() {
      t = k("a"), i = P(n), g(t, "class", "underline hover:no-underline"), g(t, "href", a = /*toast*/
      e[0].readMoreUrl), g(t, "target", s = /*toast*/
      e[0].readMoreIsExternal ? "_blank" : void 0), g(t, "rel", r = /*toast*/
      e[0].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(o, f) {
      S(o, t, f), _(t, i);
    },
    p(o, f) {
      f & /*toast*/
      1 && n !== (n = /*toast*/
      o[0].readMoreText + "") && tt(i, n), f & /*toast*/
      1 && a !== (a = /*toast*/
      o[0].readMoreUrl) && g(t, "href", a), f & /*toast*/
      1 && s !== (s = /*toast*/
      o[0].readMoreIsExternal ? "_blank" : void 0) && g(t, "target", s), f & /*toast*/
      1 && r !== (r = /*toast*/
      o[0].readMoreIsExternal ? "noreferrer" : void 0) && g(t, "rel", r);
    },
    d(o) {
      o && v(t);
    }
  };
}
function ge(e) {
  let t, n, i, a, s, r, o, f, h, u, p, w, b, l = (
    /*toast*/
    e[0].title && vt(e)
  ), c = (
    /*toast*/
    e[0].body && Et(e)
  );
  return {
    c() {
      t = k("div"), n = k("div"), l && l.c(), i = F(), c && c.c(), a = F(), s = k("button"), r = ht("svg"), o = ht("path"), g(n, "class", "flex gap-1"), G(
        n,
        "flex-col",
        /*toast*/
        e[0].body
      ), G(
        n,
        "mt-2",
        /*toast*/
        e[0].title
      ), g(o, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), g(o, "fill", "currentColor"), g(r, "width", "14"), g(r, "height", "14"), g(r, "viewBox", "0 0 14 14"), g(r, "fill", "none"), g(r, "xmlns", "http://www.w3.org/2000/svg"), g(s, "class", "w-6 h-6 flex justify-center items-center"), g(s, "type", "button"), g(s, "aria-label", f = /*toast*/
      e[0].closeLabel), g(t, "class", h = /*toast*/
      e[0].class);
    },
    m(d, m) {
      S(d, t, m), _(t, n), l && l.m(n, null), _(n, i), c && c.m(n, null), _(t, a), _(t, s), _(s, r), _(r, o), p = !0, w || (b = Qt(
        s,
        "click",
        /*click_handler*/
        e[2]
      ), w = !0);
    },
    p(d, [m]) {
      /*toast*/
      d[0].title ? l ? l.p(d, m) : (l = vt(d), l.c(), l.m(n, i)) : l && (l.d(1), l = null), /*toast*/
      d[0].body ? c ? c.p(d, m) : (c = Et(d), c.c(), c.m(n, null)) : c && (c.d(1), c = null), (!p || m & /*toast*/
      1) && G(
        n,
        "flex-col",
        /*toast*/
        d[0].body
      ), (!p || m & /*toast*/
      1) && G(
        n,
        "mt-2",
        /*toast*/
        d[0].title
      ), (!p || m & /*toast*/
      1 && f !== (f = /*toast*/
      d[0].closeLabel)) && g(s, "aria-label", f), (!p || m & /*toast*/
      1 && h !== (h = /*toast*/
      d[0].class)) && g(t, "class", h);
    },
    i(d) {
      p || (d && H(() => {
        p && (u || (u = $t(t, kt, { x: 100 }, !0)), u.run(1));
      }), p = !0);
    },
    o(d) {
      d && (u || (u = $t(t, kt, { x: 100 }, !1)), u.run(0)), p = !1;
    },
    d(d) {
      d && v(t), l && l.d(), c && c.d(), d && u && u.end(), w = !1, b();
    }
  };
}
function we(e, t, n) {
  let { toast: i } = t, { closeToast: a } = t;
  const s = () => a(i);
  return e.$$set = (r) => {
    "toast" in r && n(0, i = r.toast), "closeToast" in r && n(1, a = r.closeToast);
  }, [i, a, s];
}
class ft extends Vt {
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
Rt(ft, { toast: {}, closeToast: {} }, [], [], !0);
function St(e, t, n) {
  const i = e.slice();
  return i[7] = t[n], i;
}
function Ct(e, t, n) {
  const i = e.slice();
  return i[7] = t[n], i;
}
function Tt(e, t) {
  let n, i, a;
  return i = new ft({
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
      n = At(), Ut(i.$$.fragment), this.first = n;
    },
    m(s, r) {
      S(s, n, r), at(i, s, r), a = !0;
    },
    p(s, r) {
      t = s;
      const o = {};
      r & /*alertToasts*/
      2 && (o.toast = /*toast*/
      t[7]), i.$set(o);
    },
    i(s) {
      a || (U(i.$$.fragment, s), a = !0);
    },
    o(s) {
      J(i.$$.fragment, s), a = !1;
    },
    d(s) {
      s && v(n), ct(i, s);
    }
  };
}
function zt(e, t) {
  let n, i, a;
  return i = new ft({
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
      n = At(), Ut(i.$$.fragment), this.first = n;
    },
    m(s, r) {
      S(s, n, r), at(i, s, r), a = !0;
    },
    p(s, r) {
      t = s;
      const o = {};
      r & /*statusToasts*/
      1 && (o.toast = /*toast*/
      t[7]), i.$set(o);
    },
    i(s) {
      a || (U(i.$$.fragment, s), a = !0);
    },
    o(s) {
      J(i.$$.fragment, s), a = !1;
    },
    d(s) {
      s && v(n), ct(i, s);
    }
  };
}
function be(e) {
  let t, n, i = [], a = /* @__PURE__ */ new Map(), s, r, o = [], f = /* @__PURE__ */ new Map(), h, u = K(
    /*alertToasts*/
    e[1]
  );
  const p = (l) => (
    /*toast*/
    l[7].id
  );
  for (let l = 0; l < u.length; l += 1) {
    let c = Ct(e, u, l), d = p(c);
    a.set(d, i[l] = Tt(d, c));
  }
  let w = K(
    /*statusToasts*/
    e[0]
  );
  const b = (l) => (
    /*toast*/
    l[7].id
  );
  for (let l = 0; l < w.length; l += 1) {
    let c = St(e, w, l), d = b(c);
    f.set(d, o[l] = zt(d, c));
  }
  return {
    c() {
      t = k("div"), n = k("div");
      for (let l = 0; l < i.length; l += 1)
        i[l].c();
      s = F(), r = k("div");
      for (let l = 0; l < o.length; l += 1)
        o[l].c();
      g(n, "class", "flex flex-col gap-2"), g(n, "role", "alert"), g(r, "class", "flex flex-col gap-2"), g(r, "role", "status"), g(t, "class", "absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2");
    },
    m(l, c) {
      S(l, t, c), _(t, n);
      for (let d = 0; d < i.length; d += 1)
        i[d] && i[d].m(n, null);
      _(t, s), _(t, r);
      for (let d = 0; d < o.length; d += 1)
        o[d] && o[d].m(r, null);
      h = !0;
    },
    p(l, [c]) {
      c & /*alertToasts, closeToast*/
      6 && (u = K(
        /*alertToasts*/
        l[1]
      ), bt(), i = yt(i, c, p, 1, l, u, a, n, _t, Tt, null, Ct), mt()), c & /*statusToasts, closeToast*/
      5 && (w = K(
        /*statusToasts*/
        l[0]
      ), bt(), o = yt(o, c, b, 1, l, w, f, r, _t, zt, null, St), mt());
    },
    i(l) {
      if (!h) {
        for (let c = 0; c < u.length; c += 1)
          U(i[c]);
        for (let c = 0; c < w.length; c += 1)
          U(o[c]);
        h = !0;
      }
    },
    o(l) {
      for (let c = 0; c < i.length; c += 1)
        J(i[c]);
      for (let c = 0; c < o.length; c += 1)
        J(o[c]);
      h = !1;
    },
    d(l) {
      l && v(t);
      for (let c = 0; c < i.length; c += 1)
        i[c].d();
      for (let c = 0; c < o.length; c += 1)
        o[c].d();
    }
  };
}
const me = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0";
function $e(e, t, n) {
  let i, a, { component: s } = t, r = [], o = 0;
  const f = (u) => {
    n(5, r = r.filter((p) => p.id !== u.id)), s.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: u
      }
    ));
  }, h = (u) => {
    u.isVisible = !0, u.id ?? (u.id = o++);
    const p = [
      u.type === "error" && "bg-eki-light-red border-eki-red py-3",
      u.type === "success" && "bg-eki-light-green border-eki-green py-3",
      u.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    u.class = `${me} ${p}`, n(5, r = [...r, u]), s.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: u
      }
    ));
  };
  return e.$$set = (u) => {
    "component" in u && n(3, s = u.component);
  }, e.$$.update = () => {
    e.$$.dirty & /*toasts*/
    32 && n(1, i = r.filter((u) => u.type && ["error", "warning"].includes(u.type))), e.$$.dirty & /*toasts*/
    32 && n(0, a = r.filter((u) => !u.type || !["error", "warning"].includes(u.type)));
  }, [a, i, f, s, h, r];
}
class _e extends Vt {
  constructor(t) {
    super(), Pt(this, t, $e, be, jt, { component: 3, addToast: 4 });
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
