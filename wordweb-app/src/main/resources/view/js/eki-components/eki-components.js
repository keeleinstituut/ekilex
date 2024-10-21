var xt = Object.defineProperty;
var vt = (e, t, n) => t in e ? xt(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var _ = (e, t, n) => vt(e, typeof t != "symbol" ? t + "" : t, n);
function R() {
}
const kt = (e) => e;
function $t(e) {
  return e();
}
function it() {
  return /* @__PURE__ */ Object.create(null);
}
function A(e) {
  e.forEach($t);
}
function Y(e) {
  return typeof e == "function";
}
function Et(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function zt(e) {
  return Object.keys(e).length === 0;
}
function rt(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
const gt = typeof window < "u";
let Ct = gt ? () => window.performance.now() : () => Date.now(), tt = gt ? (e) => requestAnimationFrame(e) : R;
const O = /* @__PURE__ */ new Set();
function bt(e) {
  O.forEach((t) => {
    t.c(e) || (O.delete(t), t.f());
  }), O.size !== 0 && tt(bt);
}
function Lt(e) {
  let t;
  return O.size === 0 && tt(bt), {
    promise: new Promise((n) => {
      O.add(t = { c: e, f: n });
    }),
    abort() {
      O.delete(t);
    }
  };
}
function b(e, t) {
  e.appendChild(t);
}
function mt(e) {
  if (!e) return document;
  const t = e.getRootNode ? e.getRootNode() : e.ownerDocument;
  return t && /** @type {ShadowRoot} */
  t.host ? (
    /** @type {ShadowRoot} */
    t
  ) : e.ownerDocument;
}
function St(e) {
  const t = k("style");
  return t.textContent = "/* empty */", jt(mt(e), t), t.sheet;
}
function jt(e, t) {
  return b(
    /** @type {Document} */
    e.head || e,
    t
  ), t.sheet;
}
function Z(e, t, n) {
  e.insertBefore(t, n || null);
}
function T(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function Mt(e, t) {
  for (let n = 0; n < e.length; n += 1)
    e[n] && e[n].d(t);
}
function k(e) {
  return document.createElement(e);
}
function ot(e) {
  return document.createElementNS("http://www.w3.org/2000/svg", e);
}
function q(e) {
  return document.createTextNode(e);
}
function U() {
  return q(" ");
}
function Ot(e, t, n, o) {
  return e.addEventListener(t, n, o), () => e.removeEventListener(t, n, o);
}
function d(e, t, n) {
  n == null ? e.removeAttribute(t) : e.getAttribute(t) !== n && e.setAttribute(t, n);
}
function Nt(e) {
  return Array.from(e.childNodes);
}
function Q(e, t) {
  t = "" + t, e.data !== t && (e.data = /** @type {string} */
  t);
}
function At(e, t, { bubbles: n = !1, cancelable: o = !1 } = {}) {
  return new CustomEvent(e, { detail: t, bubbles: n, cancelable: o });
}
function Tt(e) {
  const t = {};
  return e.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      t[n.slot || "default"] = !0;
    }
  ), t;
}
const H = /* @__PURE__ */ new Map();
let J = 0;
function It(e) {
  let t = 5381, n = e.length;
  for (; n--; ) t = (t << 5) - t ^ e.charCodeAt(n);
  return t >>> 0;
}
function Pt(e, t) {
  const n = { stylesheet: St(t), rules: {} };
  return H.set(e, n), n;
}
function st(e, t, n, o, s, c, r, i = 0) {
  const a = 16.666 / o;
  let l = `{
`;
  for (let $ = 0; $ <= 1; $ += a) {
    const E = t + (n - t) * c($);
    l += $ * 100 + `%{${r(E, 1 - E)}}
`;
  }
  const h = l + `100% {${r(n, 1 - n)}}
}`, f = `__svelte_${It(h)}_${i}`, m = mt(e), { stylesheet: p, rules: u } = H.get(m) || Pt(m, e);
  u[f] || (u[f] = !0, p.insertRule(`@keyframes ${f} ${h}`, p.cssRules.length));
  const w = e.style.animation || "";
  return e.style.animation = `${w ? `${w}, ` : ""}${f} ${o}ms linear ${s}ms 1 both`, J += 1, f;
}
function Rt(e, t) {
  const n = (e.style.animation || "").split(", "), o = n.filter(
    t ? (c) => c.indexOf(t) < 0 : (c) => c.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), s = n.length - o.length;
  s && (e.style.animation = o.join(", "), J -= s, J || Bt());
}
function Bt() {
  tt(() => {
    J || (H.forEach((e) => {
      const { ownerNode: t } = e.stylesheet;
      t && T(t);
    }), H.clear());
  });
}
let et;
function P(e) {
  et = e;
}
const M = [], at = [];
let N = [];
const lt = [], Ut = /* @__PURE__ */ Promise.resolve();
let W = !1;
function Vt() {
  W || (W = !0, Ut.then(nt));
}
function B(e) {
  N.push(e);
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
    for (let t = 0; t < N.length; t += 1) {
      const n = N[t];
      G.has(n) || (G.add(n), n());
    }
    N.length = 0;
  } while (M.length);
  for (; lt.length; )
    lt.pop()();
  W = !1, G.clear(), P(e);
}
function Dt(e) {
  if (e.fragment !== null) {
    e.update(), A(e.before_update);
    const t = e.dirty;
    e.dirty = [-1], e.fragment && e.fragment.p(e.ctx, t), e.after_update.forEach(B);
  }
}
function Ft(e) {
  const t = [], n = [];
  N.forEach((o) => e.indexOf(o) === -1 ? t.push(o) : n.push(o)), n.forEach((o) => o()), N = t;
}
let I;
function qt() {
  return I || (I = Promise.resolve(), I.then(() => {
    I = null;
  })), I;
}
function K(e, t, n) {
  e.dispatchEvent(At(`${t ? "intro" : "outro"}${n}`));
}
const V = /* @__PURE__ */ new Set();
let z;
function Ht() {
  z = {
    r: 0,
    c: [],
    p: z
    // parent group
  };
}
function Jt() {
  z.r || A(z.c), z = z.p;
}
function D(e, t) {
  e && e.i && (V.delete(e), e.i(t));
}
function ct(e, t, n, o) {
  if (e && e.o) {
    if (V.has(e)) return;
    V.add(e), z.c.push(() => {
      V.delete(e), o && (n && e.d(1), o());
    }), e.o(t);
  } else o && o();
}
const Zt = { duration: 0 };
function ut(e, t, n, o) {
  let c = t(e, n, { direction: "both" }), r = o ? 0 : 1, i = null, a = null, l = null, h;
  function f() {
    l && Rt(e, l);
  }
  function m(u, w) {
    const $ = (
      /** @type {Program['d']} */
      u.b - r
    );
    return w *= Math.abs($), {
      a: r,
      b: u.b,
      d: $,
      duration: w,
      start: u.start,
      end: u.start + w,
      group: u.group
    };
  }
  function p(u) {
    const {
      delay: w = 0,
      duration: $ = 300,
      easing: E = kt,
      tick: y = R,
      css: x
    } = c || Zt, C = {
      start: Ct() + w,
      b: u
    };
    u || (C.group = z, z.r += 1), "inert" in e && (u ? h !== void 0 && (e.inert = h) : (h = /** @type {HTMLElement} */
    e.inert, e.inert = !0)), i || a ? a = C : (x && (f(), l = st(e, r, u, $, w, E, x)), u && y(0, 1), i = m(C, $), B(() => K(e, u, "start")), Lt((L) => {
      if (a && L > a.start && (i = m(a, $), a = null, K(e, i.b, "start"), x && (f(), l = st(
        e,
        r,
        i.b,
        i.duration,
        0,
        E,
        c.css
      ))), i) {
        if (L >= i.end)
          y(r = i.b, 1 - r), K(e, i.b, "end"), a || (i.b ? f() : --i.group.r || A(i.group.c)), i = null;
        else if (L >= i.start) {
          const g = L - i.start;
          r = i.a + i.d * E(g / i.duration), y(r, 1 - r);
        }
      }
      return !!(i || a);
    }));
  }
  return {
    run(u) {
      Y(c) ? qt().then(() => {
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
  const { fragment: o, after_update: s } = e.$$;
  o && o.m(t, n), B(() => {
    const c = e.$$.on_mount.map($t).filter(Y);
    e.$$.on_destroy ? e.$$.on_destroy.push(...c) : A(c), e.$$.on_mount = [];
  }), s.forEach(B);
}
function Kt(e, t) {
  const n = e.$$;
  n.fragment !== null && (Ft(n.after_update), A(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function Qt(e, t) {
  e.$$.dirty[0] === -1 && (M.push(e), Vt(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function Wt(e, t, n, o, s, c, r = null, i = [-1]) {
  const a = et;
  P(e);
  const l = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: c,
    update: R,
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
  r && r(l.root);
  let h = !1;
  if (l.ctx = n ? n(e, t.props || {}, (f, m, ...p) => {
    const u = p.length ? p[0] : m;
    return l.ctx && s(l.ctx[f], l.ctx[f] = u) && (!l.skip_bound && l.bound[f] && l.bound[f](u), h && Qt(e, f)), m;
  }) : [], l.update(), h = !0, A(l.before_update), l.fragment = o ? o(l.ctx) : !1, t.target) {
    if (t.hydrate) {
      const f = Nt(t.target);
      l.fragment && l.fragment.l(f), f.forEach(T);
    } else
      l.fragment && l.fragment.c();
    t.intro && D(e.$$.fragment), Gt(e, t.target, t.anchor), nt();
  }
  P(a);
}
let _t;
typeof HTMLElement == "function" && (_t = class extends HTMLElement {
  constructor(t, n, o) {
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
    this.$$ctor = t, this.$$s = n, o && this.attachShadow({ mode: "open" });
  }
  addEventListener(t, n, o) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(n), this.$$c) {
      const s = this.$$c.$on(t, n);
      this.$$l_u.set(n, s);
    }
    super.addEventListener(t, n, o);
  }
  removeEventListener(t, n, o) {
    if (super.removeEventListener(t, n, o), this.$$c) {
      const s = this.$$l_u.get(n);
      s && (s(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(r) {
        return () => {
          let i;
          return {
            c: function() {
              i = k("slot"), r !== "default" && d(i, "name", r);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(h, f) {
              Z(h, i, f);
            },
            d: function(h) {
              h && T(i);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const o = {}, s = Tt(this);
      for (const r of this.$$s)
        r in s && (o[r] = [n(r)]);
      for (const r of this.attributes) {
        const i = this.$$g_p(r.name);
        i in this.$$d || (this.$$d[i] = F(i, r.value, this.$$p_d, "toProp"));
      }
      for (const r in this.$$p_d)
        !(r in this.$$d) && this[r] !== void 0 && (this.$$d[r] = this[r], delete this[r]);
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
      const c = () => {
        this.$$r = !0;
        for (const r in this.$$p_d)
          if (this.$$d[r] = this.$$c.$$.ctx[this.$$c.$$.props[r]], this.$$p_d[r].reflect) {
            const i = F(
              r,
              this.$$d[r],
              this.$$p_d,
              "toAttribute"
            );
            i == null ? this.removeAttribute(this.$$p_d[r].attribute || r) : this.setAttribute(this.$$p_d[r].attribute || r, i);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(c), c();
      for (const r in this.$$l)
        for (const i of this.$$l[r]) {
          const a = this.$$c.$on(r, i);
          this.$$l_u.set(i, a);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, o) {
    var s;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = F(t, o, this.$$p_d, "toProp"), (s = this.$$c) == null || s.$set({ [t]: this.$$d[t] }));
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
function F(e, t, n, o) {
  var c;
  const s = (c = n[e]) == null ? void 0 : c.type;
  if (t = s === "Boolean" && typeof t != "boolean" ? t != null : t, !o || !n[e])
    return t;
  if (o === "toAttribute")
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
function Xt(e, t, n, o, s, c) {
  let r = class extends _t {
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
    Object.defineProperty(r.prototype, i, {
      get() {
        return this.$$c && i in this.$$c ? this.$$c[i] : this.$$d[i];
      },
      set(a) {
        var l;
        a = F(i, a, t), this.$$d[i] = a, (l = this.$$c) == null || l.$set({ [i]: a });
      }
    });
  }), o.forEach((i) => {
    Object.defineProperty(r.prototype, i, {
      get() {
        var a;
        return (a = this.$$c) == null ? void 0 : a[i];
      }
    });
  }), c && (r = c(r)), e.element = /** @type {any} */
  r, r;
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
    Kt(this, 1), this.$destroy = R;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(t, n) {
    if (!Y(n))
      return R;
    const o = this.$$.callbacks[t] || (this.$$.callbacks[t] = []);
    return o.push(n), () => {
      const s = o.indexOf(n);
      s !== -1 && o.splice(s, 1);
    };
  }
  /**
   * @param {Partial<Props>} props
   * @returns {void}
   */
  $set(t) {
    this.$$set && !zt(t) && (this.$$.skip_bound = !0, this.$$set(t), this.$$.skip_bound = !1);
  }
}
const te = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(te);
function ee(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function dt(e, { delay: t = 0, duration: n = 400, easing: o = ee, x: s = 0, y: c = 0, opacity: r = 0 } = {}) {
  const i = getComputedStyle(e), a = +i.opacity, l = i.transform === "none" ? "" : i.transform, h = a * (1 - r), [f, m] = rt(s), [p, u] = rt(c);
  return {
    delay: t,
    duration: n,
    easing: o,
    css: (w, $) => `
			transform: ${l} translate(${(1 - w) * f}${m}, ${(1 - w) * p}${u});
			opacity: ${a - h * $}`
  };
}
const ne = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.right-4{right:1rem}.top-14{top:3.5rem}.z-10{z-index:10}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.w-\\[335px\\]{width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-4{padding-top:1rem;padding-bottom:1rem}.pl-7{padding-left:1.75rem}.pr-2{padding-right:.5rem}.text-sm{font-size:.875rem;line-height:1.25rem}.text-xs{font-size:.75rem;line-height:1rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter}', X = new CSSStyleSheet();
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
  const o = e.slice();
  return o[6] = t[n], o;
}
function pt(e) {
  let t, n = (
    /*toast*/
    e[6].readMoreText + ""
  ), o, s, c, r;
  return {
    c() {
      t = k("a"), o = q(n), d(t, "class", "underline"), d(t, "href", s = /*toast*/
      e[6].readMoreUrl), d(t, "target", c = /*toast*/
      e[6].readMoreIsExternal ? "_blank" : void 0), d(t, "rel", r = /*toast*/
      e[6].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(i, a) {
      Z(i, t, a), b(t, o);
    },
    p(i, a) {
      a & /*toasts*/
      1 && n !== (n = /*toast*/
      i[6].readMoreText + "") && Q(o, n), a & /*toasts*/
      1 && s !== (s = /*toast*/
      i[6].readMoreUrl) && d(t, "href", s), a & /*toasts*/
      1 && c !== (c = /*toast*/
      i[6].readMoreIsExternal ? "_blank" : void 0) && d(t, "target", c), a & /*toasts*/
      1 && r !== (r = /*toast*/
      i[6].readMoreIsExternal ? "noreferrer" : void 0) && d(t, "rel", r);
    },
    d(i) {
      i && T(t);
    }
  };
}
function wt(e) {
  let t, n, o, s = (
    /*toast*/
    e[6].title + ""
  ), c, r, i, a, l = (
    /*toast*/
    e[6].body + ""
  ), h, f, m, p, u, w, $, E, y, x, C, L, g = (
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
      t = k("div"), n = k("div"), o = k("span"), c = q(s), r = U(), i = k("p"), a = k("span"), h = q(l), f = U(), g && g.c(), m = U(), p = k("button"), u = ot("svg"), w = ot("path"), E = U(), d(o, "class", "text-sm font-medium"), d(i, "class", "text-xs"), d(n, "class", "flex gap-1 flex-col"), d(w, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), d(w, "fill", "currentColor"), d(u, "width", "14"), d(u, "height", "14"), d(u, "viewBox", "0 0 14 14"), d(u, "fill", "none"), d(u, "xmlns", "http://www.w3.org/2000/svg"), d(p, "class", "w-6 h-6 flex justify-center items-center"), d(p, "type", "button"), d(p, "aria-label", $ = /*toast*/
      e[6].closeLabel), d(t, "class", "bg-eki-white border border-eki-light-blue text-eki-dark-blue-text rounded-lg pl-7 pr-2 py-4 grid grid-cols-[1fr_24px] gap-2 w-[335px] left-0");
    },
    m(v, S) {
      Z(v, t, S), b(t, n), b(n, o), b(o, c), b(n, r), b(n, i), b(i, a), b(a, h), b(i, f), g && g.m(i, null), b(t, m), b(t, p), b(p, u), b(u, w), b(t, E), x = !0, C || (L = Ot(p, "click", yt), C = !0);
    },
    p(v, S) {
      e = v, (!x || S & /*toasts*/
      1) && s !== (s = /*toast*/
      e[6].title + "") && Q(c, s), (!x || S & /*toasts*/
      1) && l !== (l = /*toast*/
      e[6].body + "") && Q(h, l), /*toast*/
      e[6].readMoreText ? g ? g.p(e, S) : (g = pt(e), g.c(), g.m(i, null)) : g && (g.d(1), g = null), (!x || S & /*toasts*/
      1 && $ !== ($ = /*toast*/
      e[6].closeLabel)) && d(p, "aria-label", $);
    },
    i(v) {
      x || (v && B(() => {
        x && (y || (y = ut(t, dt, { x: 100 }, !0)), y.run(1));
      }), x = !0);
    },
    o(v) {
      v && (y || (y = ut(t, dt, { x: 100 }, !1)), y.run(0)), x = !1;
    },
    d(v) {
      v && T(t), g && g.d(), v && y && y.end(), C = !1, L();
    }
  };
}
function re(e) {
  let t, n, o = ft(
    /*toasts*/
    e[0]
  ), s = [];
  for (let r = 0; r < o.length; r += 1)
    s[r] = wt(ht(e, o, r));
  const c = (r) => ct(s[r], 1, 1, () => {
    s[r] = null;
  });
  return {
    c() {
      t = k("div");
      for (let r = 0; r < s.length; r += 1)
        s[r].c();
      d(t, "class", "absolute top-14 right-4 overflow-hidden z-10 flex flex-col gap-2");
    },
    m(r, i) {
      Z(r, t, i);
      for (let a = 0; a < s.length; a += 1)
        s[a] && s[a].m(t, null);
      n = !0;
    },
    p(r, [i]) {
      if (i & /*toasts, closeToast, undefined*/
      3) {
        o = ft(
          /*toasts*/
          r[0]
        );
        let a;
        for (a = 0; a < o.length; a += 1) {
          const l = ht(r, o, a);
          s[a] ? (s[a].p(l, i), D(s[a], 1)) : (s[a] = wt(l), s[a].c(), D(s[a], 1), s[a].m(t, null));
        }
        for (Ht(), a = o.length; a < s.length; a += 1)
          c(a);
        Jt();
      }
    },
    i(r) {
      if (!n) {
        for (let i = 0; i < o.length; i += 1)
          D(s[i]);
        n = !0;
      }
    },
    o(r) {
      s = s.filter(Boolean);
      for (let i = 0; i < s.length; i += 1)
        ct(s[i]);
      n = !1;
    },
    d(r) {
      r && T(t), Mt(s, r);
    }
  };
}
function oe(e, t, n) {
  let { component: o } = t, s = [], c = 0;
  const r = (l) => {
    n(0, s = s.filter((h) => h.id !== l.id)), o.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, i = (l) => {
    l.isVisible = !0, l.id ?? (l.id = c++), n(0, s = [...s, l]), o.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: l
      }
    ));
  }, a = (l) => r(l);
  return e.$$set = (l) => {
    "component" in l && n(2, o = l.component);
  }, [s, r, o, i, a];
}
class se extends Yt {
  constructor(t) {
    super(), Wt(this, t, oe, re, Et, { component: 2, addToast: 3 });
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
