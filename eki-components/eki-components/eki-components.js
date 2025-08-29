var vt = Object.defineProperty;
var xt = (e, t, n) => t in e ? vt(e, t, { enumerable: !0, configurable: !0, writable: !0, value: n }) : e[t] = n;
var _ = (e, t, n) => xt(e, typeof t != "symbol" ? t + "" : t, n);
function O() {
}
const Et = (e) => e;
function bt(e) {
  return e();
}
function et() {
  return /* @__PURE__ */ Object.create(null);
}
function j(e) {
  e.forEach(bt);
}
function W(e) {
  return typeof e == "function";
}
function Mt(e, t) {
  return e != e ? t == t : e !== t || e && typeof e == "object" || typeof e == "function";
}
function Ct(e) {
  return Object.keys(e).length === 0;
}
function nt(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
const $t = typeof window < "u";
let St = $t ? () => window.performance.now() : () => Date.now(), X = $t ? (e) => requestAnimationFrame(e) : O;
const z = /* @__PURE__ */ new Set();
function mt(e) {
  z.forEach((t) => {
    t.c(e) || (z.delete(t), t.f());
  }), z.size !== 0 && X(mt);
}
function zt(e) {
  let t;
  return z.size === 0 && X(mt), {
    promise: new Promise((n) => {
      z.add(t = { c: e, f: n });
    }),
    abort() {
      z.delete(t);
    }
  };
}
const Lt = typeof window < "u" ? window : typeof globalThis < "u" ? globalThis : (
  // @ts-ignore Node typings have this
  global
);
function b(e, t) {
  e.appendChild(t);
}
function _t(e) {
  if (!e) return document;
  const t = e.getRootNode ? e.getRootNode() : e.ownerDocument;
  return t && /** @type {ShadowRoot} */
  t.host ? (
    /** @type {ShadowRoot} */
    t
  ) : e.ownerDocument;
}
function jt(e) {
  const t = k("style");
  return t.textContent = "/* empty */", Tt(_t(e), t), t.sheet;
}
function Tt(e, t) {
  return b(
    /** @type {Document} */
    e.head || e,
    t
  ), t.sheet;
}
function M(e, t, n) {
  e.insertBefore(t, n || null);
}
function x(e) {
  e.parentNode && e.parentNode.removeChild(e);
}
function Nt(e, t) {
  for (let n = 0; n < e.length; n += 1)
    e[n] && e[n].d(t);
}
function k(e) {
  return document.createElement(e);
}
function rt(e) {
  return document.createElementNS("http://www.w3.org/2000/svg", e);
}
function U(e) {
  return document.createTextNode(e);
}
function N() {
  return U(" ");
}
function It(e, t, n, i) {
  return e.addEventListener(t, n, i), () => e.removeEventListener(t, n, i);
}
function u(e, t, n) {
  n == null ? e.removeAttribute(t) : e.getAttribute(t) !== n && e.setAttribute(t, n);
}
function Ot(e) {
  return Array.from(e.childNodes);
}
function q(e, t) {
  t = "" + t, e.data !== t && (e.data = /** @type {string} */
  t);
}
function P(e, t, n) {
  e.classList.toggle(t, !!n);
}
function At(e, t, { bubbles: n = !1, cancelable: i = !1 } = {}) {
  return new CustomEvent(e, { detail: t, bubbles: n, cancelable: i });
}
function Ut(e) {
  const t = {};
  return e.childNodes.forEach(
    /** @param {Element} node */
    (n) => {
      t[n.slot || "default"] = !0;
    }
  ), t;
}
const F = /* @__PURE__ */ new Map();
let H = 0;
function Bt(e) {
  let t = 5381, n = e.length;
  for (; n--; ) t = (t << 5) - t ^ e.charCodeAt(n);
  return t >>> 0;
}
function Pt(e, t) {
  const n = { stylesheet: jt(t), rules: {} };
  return F.set(e, n), n;
}
function ot(e, t, n, i, s, a, r, o = 0) {
  const l = 16.666 / i;
  let c = `{
`;
  for (let h = 0; h <= 1; h += l) {
    const E = t + (n - t) * a(h);
    c += h * 100 + `%{${r(E, 1 - E)}}
`;
  }
  const g = c + `100% {${r(n, 1 - n)}}
}`, d = `__svelte_${Bt(g)}_${o}`, w = _t(e), { stylesheet: $, rules: p } = F.get(w) || Pt(w, e);
  p[d] || (p[d] = !0, $.insertRule(`@keyframes ${d} ${g}`, $.cssRules.length));
  const f = e.style.animation || "";
  return e.style.animation = `${f ? `${f}, ` : ""}${d} ${i}ms linear ${s}ms 1 both`, H += 1, d;
}
function Rt(e, t) {
  const n = (e.style.animation || "").split(", "), i = n.filter(
    t ? (a) => a.indexOf(t) < 0 : (a) => a.indexOf("__svelte") === -1
    // remove all Svelte animations
  ), s = n.length - i.length;
  s && (e.style.animation = i.join(", "), H -= s, H || Vt());
}
function Vt() {
  X(() => {
    H || (F.forEach((e) => {
      const { ownerNode: t } = e.stylesheet;
      t && x(t);
    }), F.clear());
  });
}
let Y;
function I(e) {
  Y = e;
}
const S = [], it = [];
let L = [];
const st = [], Dt = /* @__PURE__ */ Promise.resolve();
let K = !1;
function Ft() {
  K || (K = !0, Dt.then(tt));
}
function A(e) {
  L.push(e);
}
const Z = /* @__PURE__ */ new Set();
let C = 0;
function tt() {
  if (C !== 0)
    return;
  const e = Y;
  do {
    try {
      for (; C < S.length; ) {
        const t = S[C];
        C++, I(t), Ht(t.$$);
      }
    } catch (t) {
      throw S.length = 0, C = 0, t;
    }
    for (I(null), S.length = 0, C = 0; it.length; ) it.pop()();
    for (let t = 0; t < L.length; t += 1) {
      const n = L[t];
      Z.has(n) || (Z.add(n), n());
    }
    L.length = 0;
  } while (S.length);
  for (; st.length; )
    st.pop()();
  K = !1, Z.clear(), I(e);
}
function Ht(e) {
  if (e.fragment !== null) {
    e.update(), j(e.before_update);
    const t = e.dirty;
    e.dirty = [-1], e.fragment && e.fragment.p(e.ctx, t), e.after_update.forEach(A);
  }
}
function qt(e) {
  const t = [], n = [];
  L.forEach((i) => e.indexOf(i) === -1 ? t.push(i) : n.push(i)), n.forEach((i) => i()), L = t;
}
let T;
function Jt() {
  return T || (T = Promise.resolve(), T.then(() => {
    T = null;
  })), T;
}
function G(e, t, n) {
  e.dispatchEvent(At(`${t ? "intro" : "outro"}${n}`));
}
const R = /* @__PURE__ */ new Set();
let v;
function Zt() {
  v = {
    r: 0,
    c: [],
    p: v
    // parent group
  };
}
function Gt() {
  v.r || j(v.c), v = v.p;
}
function V(e, t) {
  e && e.i && (R.delete(e), e.i(t));
}
function lt(e, t, n, i) {
  if (e && e.o) {
    if (R.has(e)) return;
    R.add(e), v.c.push(() => {
      R.delete(e), i && (n && e.d(1), i());
    }), e.o(t);
  } else i && i();
}
const Kt = { duration: 0 };
function at(e, t, n, i) {
  let a = t(e, n, { direction: "both" }), r = i ? 0 : 1, o = null, l = null, c = null, g;
  function d() {
    c && Rt(e, c);
  }
  function w(p, f) {
    const h = (
      /** @type {Program['d']} */
      p.b - r
    );
    return f *= Math.abs(h), {
      a: r,
      b: p.b,
      d: h,
      duration: f,
      start: p.start,
      end: p.start + f,
      group: p.group
    };
  }
  function $(p) {
    const {
      delay: f = 0,
      duration: h = 300,
      easing: E = Et,
      tick: m = O,
      css: y
    } = a || Kt, J = {
      start: St() + f,
      b: p
    };
    p || (J.group = v, v.r += 1), "inert" in e && (p ? g !== void 0 && (e.inert = g) : (g = /** @type {HTMLElement} */
    e.inert, e.inert = !0)), o || l ? l = J : (y && (d(), c = ot(e, r, p, h, f, E, y)), p && m(0, 1), o = w(J, h), A(() => G(e, p, "start")), zt((B) => {
      if (l && B > l.start && (o = w(l, h), l = null, G(e, o.b, "start"), y && (d(), c = ot(
        e,
        r,
        o.b,
        o.duration,
        0,
        E,
        a.css
      ))), o) {
        if (B >= o.end)
          m(r = o.b, 1 - r), G(e, o.b, "end"), l || (o.b ? d() : --o.group.r || j(o.group.c)), o = null;
        else if (B >= o.start) {
          const kt = B - o.start;
          r = o.a + o.d * E(kt / o.duration), m(r, 1 - r);
        }
      }
      return !!(o || l);
    }));
  }
  return {
    run(p) {
      W(a) ? Jt().then(() => {
        a = a({ direction: p ? "in" : "out" }), $(p);
      }) : $(p);
    },
    end() {
      d(), o = l = null;
    }
  };
}
function ct(e) {
  return (e == null ? void 0 : e.length) !== void 0 ? e : Array.from(e);
}
function Qt(e, t, n) {
  const { fragment: i, after_update: s } = e.$$;
  i && i.m(t, n), A(() => {
    const a = e.$$.on_mount.map(bt).filter(W);
    e.$$.on_destroy ? e.$$.on_destroy.push(...a) : j(a), e.$$.on_mount = [];
  }), s.forEach(A);
}
function Wt(e, t) {
  const n = e.$$;
  n.fragment !== null && (qt(n.after_update), j(n.on_destroy), n.fragment && n.fragment.d(t), n.on_destroy = n.fragment = null, n.ctx = []);
}
function Xt(e, t) {
  e.$$.dirty[0] === -1 && (S.push(e), Ft(), e.$$.dirty.fill(0)), e.$$.dirty[t / 31 | 0] |= 1 << t % 31;
}
function Yt(e, t, n, i, s, a, r = null, o = [-1]) {
  const l = Y;
  I(e);
  const c = e.$$ = {
    fragment: null,
    ctx: [],
    // state
    props: a,
    update: O,
    not_equal: s,
    bound: et(),
    // lifecycle
    on_mount: [],
    on_destroy: [],
    on_disconnect: [],
    before_update: [],
    after_update: [],
    context: new Map(t.context || (l ? l.$$.context : [])),
    // everything else
    callbacks: et(),
    dirty: o,
    skip_bound: !1,
    root: t.target || l.$$.root
  };
  r && r(c.root);
  let g = !1;
  if (c.ctx = n ? n(e, t.props || {}, (d, w, ...$) => {
    const p = $.length ? $[0] : w;
    return c.ctx && s(c.ctx[d], c.ctx[d] = p) && (!c.skip_bound && c.bound[d] && c.bound[d](p), g && Xt(e, d)), w;
  }) : [], c.update(), g = !0, j(c.before_update), c.fragment = i ? i(c.ctx) : !1, t.target) {
    if (t.hydrate) {
      const d = Ot(t.target);
      c.fragment && c.fragment.l(d), d.forEach(x);
    } else
      c.fragment && c.fragment.c();
    t.intro && V(e.$$.fragment), Qt(e, t.target, t.anchor), tt();
  }
  I(l);
}
let yt;
typeof HTMLElement == "function" && (yt = class extends HTMLElement {
  constructor(t, n, i) {
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
    this.$$ctor = t, this.$$s = n, i && this.attachShadow({ mode: "open" });
  }
  addEventListener(t, n, i) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(n), this.$$c) {
      const s = this.$$c.$on(t, n);
      this.$$l_u.set(n, s);
    }
    super.addEventListener(t, n, i);
  }
  removeEventListener(t, n, i) {
    if (super.removeEventListener(t, n, i), this.$$c) {
      const s = this.$$l_u.get(n);
      s && (s(), this.$$l_u.delete(n));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let n = function(r) {
        return () => {
          let o;
          return {
            c: function() {
              o = k("slot"), r !== "default" && u(o, "name", r);
            },
            /**
             * @param {HTMLElement} target
             * @param {HTMLElement} [anchor]
             */
            m: function(g, d) {
              M(g, o, d);
            },
            d: function(g) {
              g && x(o);
            }
          };
        };
      };
      var t = n;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const i = {}, s = Ut(this);
      for (const r of this.$$s)
        r in s && (i[r] = [n(r)]);
      for (const r of this.attributes) {
        const o = this.$$g_p(r.name);
        o in this.$$d || (this.$$d[o] = D(o, r.value, this.$$p_d, "toProp"));
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
      const a = () => {
        this.$$r = !0;
        for (const r in this.$$p_d)
          if (this.$$d[r] = this.$$c.$$.ctx[this.$$c.$$.props[r]], this.$$p_d[r].reflect) {
            const o = D(
              r,
              this.$$d[r],
              this.$$p_d,
              "toAttribute"
            );
            o == null ? this.removeAttribute(this.$$p_d[r].attribute || r) : this.setAttribute(this.$$p_d[r].attribute || r, o);
          }
        this.$$r = !1;
      };
      this.$$c.$$.after_update.push(a), a();
      for (const r in this.$$l)
        for (const o of this.$$l[r]) {
          const l = this.$$c.$on(r, o);
          this.$$l_u.set(o, l);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  attributeChangedCallback(t, n, i) {
    var s;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = D(t, i, this.$$p_d, "toProp"), (s = this.$$c) == null || s.$set({ [t]: this.$$d[t] }));
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
function D(e, t, n, i) {
  var a;
  const s = (a = n[e]) == null ? void 0 : a.type;
  if (t = s === "Boolean" && typeof t != "boolean" ? t != null : t, !i || !n[e])
    return t;
  if (i === "toAttribute")
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
function te(e, t, n, i, s, a) {
  let r = class extends yt {
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
    Object.defineProperty(r.prototype, o, {
      get() {
        return this.$$c && o in this.$$c ? this.$$c[o] : this.$$d[o];
      },
      set(l) {
        var c;
        l = D(o, l, t), this.$$d[o] = l, (c = this.$$c) == null || c.$set({ [o]: l });
      }
    });
  }), i.forEach((o) => {
    Object.defineProperty(r.prototype, o, {
      get() {
        var l;
        return (l = this.$$c) == null ? void 0 : l[o];
      }
    });
  }), a && (r = a(r)), e.element = /** @type {any} */
  r, r;
}
class ee {
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
    Wt(this, 1), this.$destroy = O;
  }
  /**
   * @template {Extract<keyof Events, string>} K
   * @param {K} type
   * @param {((e: Events[K]) => void) | null | undefined} callback
   * @returns {() => void}
   */
  $on(t, n) {
    if (!W(n))
      return O;
    const i = this.$$.callbacks[t] || (this.$$.callbacks[t] = []);
    return i.push(n), () => {
      const s = i.indexOf(n);
      s !== -1 && i.splice(s, 1);
    };
  }
  /**
   * @param {Partial<Props>} props
   * @returns {void}
   */
  $set(t) {
    this.$$set && !Ct(t) && (this.$$.skip_bound = !0, this.$$set(t), this.$$.skip_bound = !1);
  }
}
const ne = "4";
typeof window < "u" && (window.__svelte || (window.__svelte = { v: /* @__PURE__ */ new Set() })).v.add(ne);
function re(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function dt(e, { delay: t = 0, duration: n = 400, easing: i = re, x: s = 0, y: a = 0, opacity: r = 0 } = {}) {
  const o = getComputedStyle(e), l = +o.opacity, c = o.transform === "none" ? "" : o.transform, g = l * (1 - r), [d, w] = nt(s), [$, p] = nt(a);
  return {
    delay: t,
    duration: n,
    easing: i,
    css: (f, h) => `
			transform: ${c} translate(${(1 - f) * d}${w}, ${(1 - f) * $}${p});
			opacity: ${l - g * h}`
  };
}
const oe = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.right-4{right:1rem}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.w-\\[335px\\]{width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity))}.underline{text-decoration-line:underline}.shadow{--tw-shadow: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--tw-shadow-colored: 0 1px 3px 0 var(--tw-shadow-color), 0 1px 2px -1px var(--tw-shadow-color);box-shadow:var(--tw-ring-offset-shadow, 0 0 #0000),var(--tw-ring-shadow, 0 0 #0000),var(--tw-shadow)}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}', Q = new CSSStyleSheet();
Q.replaceSync(oe);
const ie = (e) => class extends e {
  constructor() {
    super(...arguments);
    _(this, "component", this);
  }
  connectedCallback() {
    var n;
    super.connectedCallback(), (n = this.shadowRoot) != null && n.adoptedStyleSheets && Q && (this.shadowRoot.adoptedStyleSheets = [Q]);
  }
}, { Boolean: se } = Lt;
function ut(e, t, n) {
  const i = e.slice();
  return i[6] = t[n], i;
}
function ft(e) {
  let t, n = (
    /*toast*/
    e[6].title + ""
  ), i, s, a = !/*toast*/
  e[6].body && /*toast*/
  e[6].readMoreText && ht(e);
  return {
    c() {
      t = k("p"), i = U(n), s = N(), a && a.c(), u(t, "class", "text-sm font-medium");
    },
    m(r, o) {
      M(r, t, o), b(t, i), b(t, s), a && a.m(t, null);
    },
    p(r, o) {
      o & /*toasts*/
      1 && n !== (n = /*toast*/
      r[6].title + "") && q(i, n), !/*toast*/
      r[6].body && /*toast*/
      r[6].readMoreText ? a ? a.p(r, o) : (a = ht(r), a.c(), a.m(t, null)) : a && (a.d(1), a = null);
    },
    d(r) {
      r && x(t), a && a.d();
    }
  };
}
function ht(e) {
  let t, n = (
    /*toast*/
    e[6].readMoreText + ""
  ), i, s, a, r;
  return {
    c() {
      t = k("a"), i = U(n), u(t, "class", "underline hover:no-underline"), u(t, "href", s = /*toast*/
      e[6].readMoreUrl), u(t, "target", a = /*toast*/
      e[6].readMoreIsExternal ? "_blank" : void 0), u(t, "rel", r = /*toast*/
      e[6].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(o, l) {
      M(o, t, l), b(t, i);
    },
    p(o, l) {
      l & /*toasts*/
      1 && n !== (n = /*toast*/
      o[6].readMoreText + "") && q(i, n), l & /*toasts*/
      1 && s !== (s = /*toast*/
      o[6].readMoreUrl) && u(t, "href", s), l & /*toasts*/
      1 && a !== (a = /*toast*/
      o[6].readMoreIsExternal ? "_blank" : void 0) && u(t, "target", a), l & /*toasts*/
      1 && r !== (r = /*toast*/
      o[6].readMoreIsExternal ? "noreferrer" : void 0) && u(t, "rel", r);
    },
    d(o) {
      o && x(t);
    }
  };
}
function pt(e) {
  let t, n, i = (
    /*toast*/
    e[6].body + ""
  ), s, a, r = (
    /*toast*/
    e[6].readMoreText && gt(e)
  );
  return {
    c() {
      t = k("p"), n = k("span"), s = U(i), a = N(), r && r.c(), u(t, "class", "break-word text-sm");
    },
    m(o, l) {
      M(o, t, l), b(t, n), b(n, s), b(t, a), r && r.m(t, null);
    },
    p(o, l) {
      l & /*toasts*/
      1 && i !== (i = /*toast*/
      o[6].body + "") && q(s, i), /*toast*/
      o[6].readMoreText ? r ? r.p(o, l) : (r = gt(o), r.c(), r.m(t, null)) : r && (r.d(1), r = null);
    },
    d(o) {
      o && x(t), r && r.d();
    }
  };
}
function gt(e) {
  let t, n = (
    /*toast*/
    e[6].readMoreText + ""
  ), i, s, a, r;
  return {
    c() {
      t = k("a"), i = U(n), u(t, "class", "underline hover:no-underline"), u(t, "href", s = /*toast*/
      e[6].readMoreUrl), u(t, "target", a = /*toast*/
      e[6].readMoreIsExternal ? "_blank" : void 0), u(t, "rel", r = /*toast*/
      e[6].readMoreIsExternal ? "noreferrer" : void 0);
    },
    m(o, l) {
      M(o, t, l), b(t, i);
    },
    p(o, l) {
      l & /*toasts*/
      1 && n !== (n = /*toast*/
      o[6].readMoreText + "") && q(i, n), l & /*toasts*/
      1 && s !== (s = /*toast*/
      o[6].readMoreUrl) && u(t, "href", s), l & /*toasts*/
      1 && a !== (a = /*toast*/
      o[6].readMoreIsExternal ? "_blank" : void 0) && u(t, "target", a), l & /*toasts*/
      1 && r !== (r = /*toast*/
      o[6].readMoreIsExternal ? "noreferrer" : void 0) && u(t, "rel", r);
    },
    d(o) {
      o && x(t);
    }
  };
}
function wt(e) {
  let t, n, i, s, a, r, o, l, c, g, d, w, $, p, f = (
    /*toast*/
    e[6].title && ft(e)
  ), h = (
    /*toast*/
    e[6].body && pt(e)
  );
  function E() {
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
      t = k("div"), n = k("div"), f && f.c(), i = N(), h && h.c(), s = N(), a = k("button"), r = rt("svg"), o = rt("path"), c = N(), u(n, "class", "flex gap-1"), P(
        n,
        "flex-col",
        /*toast*/
        e[6].body
      ), P(
        n,
        "mt-2",
        /*toast*/
        e[6].title
      ), u(o, "d", "M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"), u(o, "fill", "currentColor"), u(r, "width", "14"), u(r, "height", "14"), u(r, "viewBox", "0 0 14 14"), u(r, "fill", "none"), u(r, "xmlns", "http://www.w3.org/2000/svg"), u(a, "class", "w-6 h-6 flex justify-center items-center"), u(a, "type", "button"), u(a, "aria-label", l = /*toast*/
      e[6].closeLabel), u(t, "class", g = /*toast*/
      e[6].class);
    },
    m(m, y) {
      M(m, t, y), b(t, n), f && f.m(n, null), b(n, i), h && h.m(n, null), b(t, s), b(t, a), b(a, r), b(r, o), b(t, c), w = !0, $ || (p = It(a, "click", E), $ = !0);
    },
    p(m, y) {
      e = m, /*toast*/
      e[6].title ? f ? f.p(e, y) : (f = ft(e), f.c(), f.m(n, i)) : f && (f.d(1), f = null), /*toast*/
      e[6].body ? h ? h.p(e, y) : (h = pt(e), h.c(), h.m(n, null)) : h && (h.d(1), h = null), (!w || y & /*toasts*/
      1) && P(
        n,
        "flex-col",
        /*toast*/
        e[6].body
      ), (!w || y & /*toasts*/
      1) && P(
        n,
        "mt-2",
        /*toast*/
        e[6].title
      ), (!w || y & /*toasts*/
      1 && l !== (l = /*toast*/
      e[6].closeLabel)) && u(a, "aria-label", l), (!w || y & /*toasts*/
      1 && g !== (g = /*toast*/
      e[6].class)) && u(t, "class", g);
    },
    i(m) {
      w || (m && A(() => {
        w && (d || (d = at(t, dt, { x: 100 }, !0)), d.run(1));
      }), w = !0);
    },
    o(m) {
      m && (d || (d = at(t, dt, { x: 100 }, !1)), d.run(0)), w = !1;
    },
    d(m) {
      m && x(t), f && f.d(), h && h.d(), m && d && d.end(), $ = !1, p();
    }
  };
}
function le(e) {
  let t, n, i = ct(
    /*toasts*/
    e[0]
  ), s = [];
  for (let r = 0; r < i.length; r += 1)
    s[r] = wt(ut(e, i, r));
  const a = (r) => lt(s[r], 1, 1, () => {
    s[r] = null;
  });
  return {
    c() {
      t = k("div");
      for (let r = 0; r < s.length; r += 1)
        s[r].c();
      u(t, "class", "absolute top-14 right-4 overflow-hidden z-[1073] flex flex-col gap-2");
    },
    m(r, o) {
      M(r, t, o);
      for (let l = 0; l < s.length; l += 1)
        s[l] && s[l].m(t, null);
      n = !0;
    },
    p(r, [o]) {
      if (o & /*toasts, closeToast, undefined*/
      3) {
        i = ct(
          /*toasts*/
          r[0]
        );
        let l;
        for (l = 0; l < i.length; l += 1) {
          const c = ut(r, i, l);
          s[l] ? (s[l].p(c, o), V(s[l], 1)) : (s[l] = wt(c), s[l].c(), V(s[l], 1), s[l].m(t, null));
        }
        for (Zt(), l = i.length; l < s.length; l += 1)
          a(l);
        Gt();
      }
    },
    i(r) {
      if (!n) {
        for (let o = 0; o < i.length; o += 1)
          V(s[o]);
        n = !0;
      }
    },
    o(r) {
      s = s.filter(se);
      for (let o = 0; o < s.length; o += 1)
        lt(s[o]);
      n = !1;
    },
    d(r) {
      r && x(t), Nt(s, r);
    }
  };
}
const ae = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 w-[335px] left-0";
function ce(e, t, n) {
  let { component: i } = t, s = [], a = 0;
  const r = (c) => {
    n(0, s = s.filter((g) => g.id !== c.id)), i.dispatchEvent(new CustomEvent(
      "eki-toast-closed",
      {
        bubbles: !0,
        composed: !0,
        detail: c
      }
    ));
  }, o = (c) => {
    c.isVisible = !0, c.id ?? (c.id = a++);
    const g = [
      c.type === "error" && "bg-eki-light-red border-eki-red py-3",
      c.type === "success" && "bg-eki-light-green border-eki-green py-3",
      c.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    c.class = `${ae} ${g}`, n(0, s = [...s, c]), i.dispatchEvent(new CustomEvent(
      "eki-toast-opened",
      {
        bubbles: !0,
        composed: !0,
        detail: c
      }
    ));
  }, l = (c) => r(c);
  return e.$$set = (c) => {
    "component" in c && n(2, i = c.component);
  }, [s, r, i, o, l];
}
class de extends ee {
  constructor(t) {
    super(), Yt(this, t, ce, le, Mt, { component: 2, addToast: 3 });
  }
  get component() {
    return this.$$.ctx[2];
  }
  set component(t) {
    this.$$set({ component: t }), tt();
  }
  get addToast() {
    return this.$$.ctx[3];
  }
}
customElements.define("eki-toast", te(de, { component: {} }, [], ["addToast"], !0, ie));
