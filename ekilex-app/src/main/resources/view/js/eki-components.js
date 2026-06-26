;(function(){
var Qi = Object.defineProperty;
var gn = (e) => {
  throw TypeError(e);
};
var es = (e, t, r) => t in e ? Qi(e, t, { enumerable: !0, configurable: !0, writable: !0, value: r }) : e[t] = r;
var T = (e, t, r) => es(e, typeof t != "symbol" ? t + "" : t, r), Or = (e, t, r) => t.has(e) || gn("Cannot " + r);
var a = (e, t, r) => (Or(e, t, "read from private field"), r ? r.call(e) : t.get(e)), x = (e, t, r) => t.has(e) ? gn("Cannot add the same private member more than once") : t instanceof WeakSet ? t.add(e) : t.set(e, r), m = (e, t, r, n) => (Or(e, t, "write to private field"), n ? n.call(e, r) : t.set(e, r), r), $ = (e, t, r) => (Or(e, t, "access private method"), r);
var Hn;
typeof window < "u" && ((Hn = window.__svelte ?? (window.__svelte = {})).v ?? (Hn.v = /* @__PURE__ */ new Set())).add("5");
const ts = 1, rs = 2, ns = 16, is = 4, ss = 2, Yn = "[", rn = "[!", _n = "[?", nn = "]", At = {}, I = Symbol("uninitialized"), Un = "http://www.w3.org/1999/xhtml", Gn = !1;
var Kn = Array.isArray, os = Array.prototype.indexOf, ot = Array.prototype.includes, Er = Array.from, hr = Object.keys, pr = Object.defineProperty, yt = Object.getOwnPropertyDescriptor, ls = Object.getOwnPropertyDescriptors, as = Object.prototype, fs = Array.prototype, Wn = Object.getPrototypeOf, mn = Object.isExtensible;
function us(e) {
  return typeof e == "function";
}
const gt = () => {
};
function cs(e) {
  for (var t = 0; t < e.length; t++)
    e[t]();
}
function Zn() {
  var e, t, r = new Promise((n, i) => {
    e = n, t = i;
  });
  return { promise: r, resolve: e, reject: t };
}
const q = 2, Mt = 4, Tr = 8, Jn = 1 << 24, le = 16, ke = 32, De = 64, jr = 128, ae = 512, j = 1024, P = 2048, $e = 4096, B = 8192, fe = 16384, We = 32768, wn = 1 << 25, ct = 65536, vr = 1 << 17, ds = 1 << 18, pt = 1 << 19, hs = 1 << 20, je = 1 << 25, dt = 65536, gr = 1 << 21, bt = 1 << 22, Ue = 1 << 23, Nr = Symbol("$state"), ps = Symbol("legacy props"), vs = Symbol(""), lr = Symbol("attributes"), Pr = Symbol("class"), gs = Symbol("style"), jt = Symbol("text"), Sr = new class extends Error {
  constructor() {
    super(...arguments);
    T(this, "name", "StaleReactionError");
    T(this, "message", "The reaction that called `getAbortSignal()` was re-run or destroyed");
  }
}();
var Bn;
const _s = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  !!((Bn = globalThis.document) != null && Bn.contentType) && /* @__PURE__ */ globalThis.document.contentType.includes("xml")
), sn = 3, Qt = 8;
function ms() {
  throw new Error("https://svelte.dev/e/async_derived_orphan");
}
function ws(e, t, r) {
  throw new Error("https://svelte.dev/e/each_key_duplicate");
}
function ys() {
  throw new Error("https://svelte.dev/e/effect_update_depth_exceeded");
}
function bs() {
  throw new Error("https://svelte.dev/e/hydration_failed");
}
function xs() {
  throw new Error("https://svelte.dev/e/state_descriptors_fixed");
}
function ks() {
  throw new Error("https://svelte.dev/e/state_prototype_fixed");
}
function $s() {
  throw new Error("https://svelte.dev/e/state_unsafe_mutation");
}
function Es() {
  throw new Error("https://svelte.dev/e/svelte_boundary_reset_onerror");
}
function Ts() {
  console.warn("https://svelte.dev/e/derived_inert");
}
function Cr(e) {
  console.warn("https://svelte.dev/e/hydration_mismatch");
}
function Ss() {
  console.warn("https://svelte.dev/e/svelte_boundary_reset_noop");
}
let A = !1;
function Pe(e) {
  A = e;
}
let M;
function ee(e) {
  if (e === null)
    throw Cr(), At;
  return M = e;
}
function Ar() {
  return ee(/* @__PURE__ */ Fe(M));
}
function we(e) {
  if (A) {
    if (/* @__PURE__ */ Fe(M) !== null)
      throw Cr(), At;
    M = e;
  }
}
function Cs(e = 1) {
  if (A) {
    for (var t = e, r = M; t--; )
      r = /** @type {TemplateNode} */
      /* @__PURE__ */ Fe(r);
    M = r;
  }
}
function _r(e = !0) {
  for (var t = 0, r = M; ; ) {
    if (r.nodeType === Qt) {
      var n = (
        /** @type {Comment} */
        r.data
      );
      if (n === nn) {
        if (t === 0) return r;
        t -= 1;
      } else (n === Yn || n === rn || // "[1", "[2", etc. for if blocks
      n[0] === "[" && !isNaN(Number(n.slice(1)))) && (t += 1);
    }
    var i = (
      /** @type {TemplateNode} */
      /* @__PURE__ */ Fe(r)
    );
    e && r.remove(), r = i;
  }
}
function Xn(e) {
  if (!e || e.nodeType !== Qt)
    throw Cr(), At;
  return (
    /** @type {Comment} */
    e.data
  );
}
function Qn(e) {
  return e === this.v;
}
function As(e, t) {
  return e != e ? t == t : e !== t || e !== null && typeof e == "object" || typeof e == "function";
}
function ei(e) {
  return !As(e, this.v);
}
let Ms = !1, _e = null;
function Lt(e) {
  _e = e;
}
function on(e, t = !1, r) {
  _e = {
    p: _e,
    i: !1,
    c: null,
    e: null,
    s: e,
    x: null,
    r: (
      /** @type {Effect} */
      k
    ),
    l: null
  };
}
function ln(e) {
  var t = (
    /** @type {ComponentContext} */
    _e
  ), r = t.e;
  if (r !== null) {
    t.e = null;
    for (var n of r)
      Ws(n);
  }
  return e !== void 0 && (t.x = e), t.i = !0, _e = t.p, e ?? /** @type {T} */
  {};
}
function ti() {
  return !0;
}
let Je = [];
function ri() {
  var e = Je;
  Je = [], cs(e);
}
function lt(e) {
  if (Je.length === 0 && !Bt) {
    var t = Je;
    queueMicrotask(() => {
      t === Je && ri();
    });
  }
  Je.push(e);
}
function Ls() {
  for (; Je.length > 0; )
    ri();
}
function ni(e) {
  var t = k;
  if (t === null)
    return E.f |= Ue, e;
  if ((t.f & We) === 0 && (t.f & Mt) === 0)
    throw e;
  Ye(e, t);
}
function Ye(e, t) {
  for (; t !== null; ) {
    if ((t.f & jr) !== 0) {
      if ((t.f & We) === 0)
        throw e;
      try {
        t.b.error(e);
        return;
      } catch (r) {
        e = r;
      }
    }
    t = t.parent;
  }
  throw e;
}
const Os = -7169;
function N(e, t) {
  e.f = e.f & Os | t;
}
function an(e) {
  (e.f & ae) !== 0 || e.deps === null ? N(e, j) : N(e, $e);
}
function ii(e) {
  if (e !== null)
    for (const t of e)
      (t.f & q) === 0 || (t.f & dt) === 0 || (t.f ^= dt, ii(
        /** @type {Derived} */
        t.deps
      ));
}
function si(e, t, r) {
  (e.f & P) !== 0 ? t.add(e) : (e.f & $e) !== 0 && r.add(e), ii(e.deps), N(e, j);
}
let Rr = null, vt = null, y = null, Ht = null, F = null, Dr = null, Bt = !1, zr = !1, mt = null, ar = null;
var yn = 0;
let Ns = 1;
var xt, Be, et, kt, $t, tt, Et, Le, Tt, Y, Gt, Oe, he, ye, St, rt, C, Fr, Pt, qr, oi, li, _t, Rs, Dt;
const xr = class xr {
  constructor() {
    x(this, C);
    T(this, "id", Ns++);
    /** True as soon as `#process` was called */
    x(this, xt, !1);
    T(this, "linked", !0);
    /** @type {Batch | null} */
    x(this, Be, null);
    /** @type {Batch | null} */
    x(this, et, null);
    /** @type {Map<Effect, ReturnType<typeof deferred<any>>>} */
    T(this, "async_deriveds", /* @__PURE__ */ new Map());
    /**
     * The current values of any signals that are updated in this batch.
     * Tuple format: [value, is_derived] (note: is_derived is false for deriveds, too, if they were overridden via assignment)
     * They keys of this map are identical to `this.#previous`
     * @type {Map<Value, [any, boolean]>}
     */
    T(this, "current", /* @__PURE__ */ new Map());
    /**
     * The values of any signals (sources and deriveds) that are updated in this batch _before_ those updates took place.
     * They keys of this map are identical to `this.#current`
     * @type {Map<Value, any>}
     */
    T(this, "previous", /* @__PURE__ */ new Map());
    /**
     * When the batch is committed (and the DOM is updated), we need to remove old branches
     * and append new ones by calling the functions added inside (if/each/key/etc) blocks
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, kt, /* @__PURE__ */ new Set());
    /**
     * If a fork is discarded, we need to destroy any effects that are no longer needed
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, $t, /* @__PURE__ */ new Set());
    /**
     * Callbacks that should run only when a fork is committed.
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, tt, /* @__PURE__ */ new Set());
    /**
     * The number of async effects that are currently in flight
     */
    x(this, Et, 0);
    /**
     * Async effects that are currently in flight, _not_ inside a pending boundary
     * @type {Map<Effect, number>}
     */
    x(this, Le, /* @__PURE__ */ new Map());
    /**
     * A deferred that resolves when the batch is committed, used with `settled()`
     * TODO replace with Promise.withResolvers once supported widely enough
     * @type {{ promise: Promise<void>, resolve: (value?: any) => void, reject: (reason: unknown) => void } | null}
     */
    x(this, Tt, null);
    /**
     * The root effects that need to be flushed
     * @type {Effect[]}
     */
    x(this, Y, []);
    /**
     * Effects created while this batch was active.
     * @type {Effect[]}
     */
    x(this, Gt, []);
    /**
     * Deferred effects (which run after async work has completed) that are DIRTY
     * @type {Set<Effect>}
     */
    x(this, Oe, /* @__PURE__ */ new Set());
    /**
     * Deferred effects that are MAYBE_DIRTY
     * @type {Set<Effect>}
     */
    x(this, he, /* @__PURE__ */ new Set());
    /**
     * A map of branches that still exist, but will be destroyed when this batch
     * is committed — we skip over these during `process`.
     * The value contains child effects that were dirty/maybe_dirty before being reset,
     * so they can be rescheduled if the branch survives.
     * @type {Map<Effect, { d: Effect[], m: Effect[] }>}
     */
    x(this, ye, /* @__PURE__ */ new Map());
    /**
     * Inverse of #skipped_branches which we need to tell prior batches to unskip them when committing
     * @type {Set<Effect>}
     */
    x(this, St, /* @__PURE__ */ new Set());
    T(this, "is_fork", !1);
    x(this, rt, !1);
    vt === null ? Rr = vt = this : (m(vt, et, this), m(this, Be, vt)), vt = this;
  }
  /**
   * Add an effect to the #skipped_branches map and reset its children
   * @param {Effect} effect
   */
  skip_effect(t) {
    a(this, ye).has(t) || a(this, ye).set(t, { d: [], m: [] }), a(this, St).delete(t);
  }
  /**
   * Remove an effect from the #skipped_branches map and reschedule
   * any tracked dirty/maybe_dirty child effects
   * @param {Effect} effect
   * @param {(e: Effect) => void} callback
   */
  unskip_effect(t, r = (n) => this.schedule(n)) {
    var n = a(this, ye).get(t);
    if (n) {
      a(this, ye).delete(t);
      for (var i of n.d)
        N(i, P), r(i);
      for (i of n.m)
        N(i, $e), r(i);
    }
    a(this, St).add(t);
  }
  /**
   * Associate a change to a given source with the current
   * batch, noting its previous and current values
   * @param {Value} source
   * @param {any} value
   * @param {boolean} [is_derived]
   */
  capture(t, r, n = !1) {
    t.v !== I && !this.previous.has(t) && this.previous.set(t, t.v), (t.f & Ue) === 0 && (this.current.set(t, [r, n]), F == null || F.set(t, r)), this.is_fork || (t.v = r);
  }
  activate() {
    y = this;
  }
  deactivate() {
    y = null, F = null;
  }
  flush() {
    try {
      zr = !0, y = this, $(this, C, Pt).call(this);
    } finally {
      yn = 0, Dr = null, mt = null, ar = null, zr = !1, y = null, F = null, at.clear();
    }
  }
  discard() {
    var t;
    for (const r of a(this, $t)) r(this);
    a(this, $t).clear(), a(this, tt).clear(), $(this, C, Dt).call(this), (t = a(this, Tt)) == null || t.resolve();
  }
  /**
   * @param {Effect} effect
   */
  register_created_effect(t) {
    a(this, Gt).push(t);
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  increment(t, r) {
    if (m(this, Et, a(this, Et) + 1), t) {
      let n = a(this, Le).get(r) ?? 0;
      a(this, Le).set(r, n + 1);
    }
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  decrement(t, r) {
    if (m(this, Et, a(this, Et) - 1), t) {
      let n = a(this, Le).get(r) ?? 0;
      n === 1 ? a(this, Le).delete(r) : a(this, Le).set(r, n - 1);
    }
    a(this, rt) || (m(this, rt, !0), lt(() => {
      m(this, rt, !1), this.linked && this.flush();
    }));
  }
  /**
   * @param {Set<Effect>} dirty_effects
   * @param {Set<Effect>} maybe_dirty_effects
   */
  transfer_effects(t, r) {
    for (const n of t)
      a(this, Oe).add(n);
    for (const n of r)
      a(this, he).add(n);
    t.clear(), r.clear();
  }
  /** @param {(batch: Batch) => void} fn */
  oncommit(t) {
    a(this, kt).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  ondiscard(t) {
    a(this, $t).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  on_fork_commit(t) {
    a(this, tt).add(t);
  }
  run_fork_commit_callbacks() {
    for (const t of a(this, tt)) t(this);
    a(this, tt).clear();
  }
  settled() {
    return (a(this, Tt) ?? m(this, Tt, Zn())).promise;
  }
  static ensure() {
    if (y === null) {
      const t = y = new xr();
      !zr && !Bt && lt(() => {
        a(t, xt) || t.flush();
      });
    }
    return y;
  }
  apply() {
    {
      F = null;
      return;
    }
  }
  /**
   *
   * @param {Effect} effect
   */
  schedule(t) {
    var i;
    if (Dr = t, (i = t.b) != null && i.is_pending && (t.f & (Mt | Tr | Jn)) !== 0 && (t.f & We) === 0) {
      t.b.defer_effect(t);
      return;
    }
    for (var r = t; r.parent !== null; ) {
      r = r.parent;
      var n = r.f;
      if (mt !== null && r === k && (E === null || (E.f & q) === 0))
        return;
      if ((n & (De | ke)) !== 0) {
        if ((n & j) === 0)
          return;
        r.f ^= j;
      }
    }
    a(this, Y).push(r);
  }
};
xt = new WeakMap(), Be = new WeakMap(), et = new WeakMap(), kt = new WeakMap(), $t = new WeakMap(), tt = new WeakMap(), Et = new WeakMap(), Le = new WeakMap(), Tt = new WeakMap(), Y = new WeakMap(), Gt = new WeakMap(), Oe = new WeakMap(), he = new WeakMap(), ye = new WeakMap(), St = new WeakMap(), rt = new WeakMap(), C = new WeakSet(), Fr = function() {
  if (this.is_fork) return !0;
  for (const n of a(this, Le).keys()) {
    for (var t = n, r = !1; t.parent !== null; ) {
      if (a(this, ye).has(t)) {
        r = !0;
        break;
      }
      t = t.parent;
    }
    if (!r)
      return !0;
  }
  return !1;
}, Pt = function() {
  var f, u, v, h;
  m(this, xt, !0), yn++ > 1e3 && ($(this, C, Dt).call(this), zs());
  for (const d of a(this, Oe))
    a(this, he).delete(d), N(d, P), this.schedule(d);
  for (const d of a(this, he))
    N(d, $e), this.schedule(d);
  const t = a(this, Y);
  m(this, Y, []), this.apply();
  var r = mt = [], n = [], i = ar = [];
  for (const d of t)
    try {
      $(this, C, qr).call(this, d, r, n);
    } catch (p) {
      throw ui(d), $(this, C, Fr).call(this) || this.discard(), p;
    }
  if (y = null, i.length > 0) {
    var s = xr.ensure();
    for (const d of i)
      s.schedule(d);
  }
  if (mt = null, ar = null, $(this, C, Fr).call(this)) {
    $(this, C, _t).call(this, n), $(this, C, _t).call(this, r);
    for (const [d, p] of a(this, ye))
      fi(d, p);
    i.length > 0 && /** @type {unknown} */
    $(f = y, C, Pt).call(f);
    return;
  }
  const o = $(this, C, oi).call(this);
  if (o) {
    $(this, C, _t).call(this, n), $(this, C, _t).call(this, r), $(u = o, C, li).call(u, this);
    return;
  }
  a(this, Oe).clear(), a(this, he).clear();
  for (const d of a(this, kt)) d(this);
  a(this, kt).clear(), Ht = this, bn(n), bn(r), Ht = null, (v = a(this, Tt)) == null || v.resolve();
  var l = (
    /** @type {Batch | null} */
    /** @type {unknown} */
    y
  );
  if (a(this, Et) === 0 && (a(this, Y).length === 0 || l !== null) && $(this, C, Dt).call(this), a(this, Y).length > 0)
    if (l !== null) {
      const d = l;
      a(d, Y).push(...a(this, Y).filter((p) => !a(d, Y).includes(p)));
    } else
      l = this;
  l !== null && $(h = l, C, Pt).call(h);
}, /**
 * Traverse the effect tree, executing effects or stashing
 * them for later execution as appropriate
 * @param {Effect} root
 * @param {Effect[]} effects
 * @param {Effect[]} render_effects
 */
qr = function(t, r, n) {
  t.f ^= j;
  for (var i = t.first; i !== null; ) {
    var s = i.f, o = (s & (ke | De)) !== 0, l = o && (s & j) !== 0, f = l || (s & B) !== 0 || a(this, ye).has(i);
    if (!f && i.fn !== null) {
      o ? i.f ^= j : (s & Mt) !== 0 ? r.push(i) : er(i) && ((s & le) !== 0 && a(this, he).add(i), Nt(i));
      var u = i.first;
      if (u !== null) {
        i = u;
        continue;
      }
    }
    for (; i !== null; ) {
      var v = i.next;
      if (v !== null) {
        i = v;
        break;
      }
      i = i.parent;
    }
  }
}, oi = function() {
  for (var t = a(this, Be); t !== null; ) {
    if (!t.is_fork) {
      for (const [r, [, n]] of this.current)
        if (t.current.has(r) && !n)
          return t;
    }
    t = a(t, Be);
  }
  return null;
}, /**
 * @param {Batch} batch
 */
li = function(t) {
  var n;
  for (const [i, s] of t.current)
    !this.previous.has(i) && t.previous.has(i) && this.previous.set(i, t.previous.get(i)), this.current.set(i, s);
  for (const [i, s] of t.async_deriveds) {
    const o = this.async_deriveds.get(i);
    o && s.promise.then(o.resolve).catch(o.reject);
  }
  this.transfer_effects(a(t, Oe), a(t, he));
  const r = (i) => {
    var s = i.reactions;
    if (s !== null)
      for (const f of s) {
        var o = f.f;
        if ((o & q) !== 0)
          r(
            /** @type {Derived} */
            f
          );
        else {
          var l = (
            /** @type {Effect} */
            f
          );
          o & (bt | le) && !this.async_deriveds.has(l) && (a(this, he).delete(l), N(l, P), this.schedule(l));
        }
      }
  };
  for (const i of this.current.keys())
    r(i);
  this.oncommit(() => t.discard()), $(n = t, C, Dt).call(n), y = this, $(this, C, Pt).call(this);
}, /**
 * @param {Effect[]} effects
 */
_t = function(t) {
  for (var r = 0; r < t.length; r += 1)
    si(t[r], a(this, Oe), a(this, he));
}, Rs = function() {
  var v;
  for (let h = Rr; h !== null; h = a(h, et)) {
    var t = h.id < this.id, r = [];
    for (const [d, [p, c]] of this.current) {
      if (h.current.has(d)) {
        var n = (
          /** @type {[any, boolean]} */
          h.current.get(d)[0]
        );
        if (t && p !== n)
          h.current.set(d, [p, c]);
        else
          continue;
      }
      r.push(d);
    }
    if (t)
      for (const [d, p] of this.async_deriveds) {
        const c = h.async_deriveds.get(d);
        c && p.promise.then(c.resolve).catch(c.reject);
      }
    if (a(h, xt)) {
      var i = [...h.current.keys()].filter(
        (d) => !/** @type {[any, boolean]} */
        h.current.get(d)[1] && !this.current.has(d)
      );
      if (i.length === 0)
        t && h.discard();
      else if (r.length > 0) {
        if (t)
          for (const d of a(this, St))
            h.unskip_effect(d, (p) => {
              var c;
              (p.f & (le | bt)) !== 0 ? h.schedule(p) : $(c = h, C, _t).call(c, [p]);
            });
        h.activate();
        var s = /* @__PURE__ */ new Set(), o = /* @__PURE__ */ new Map();
        for (var l of r)
          ai(l, i, s, o);
        o = /* @__PURE__ */ new Map();
        var f = [...h.current].filter(([d, p]) => {
          const c = this.current.get(d);
          return c ? c[0] !== p[0] || c[1] !== p[1] : !0;
        }).map(([d]) => d);
        if (f.length > 0)
          for (const d of a(this, Gt))
            (d.f & (fe | B | vr)) === 0 && fn(d, f, o) && ((d.f & (bt | le)) !== 0 ? (N(d, P), h.schedule(d)) : a(h, Oe).add(d));
        if (a(h, Y).length > 0 && !a(h, rt)) {
          h.apply();
          for (var u of a(h, Y))
            $(v = h, C, qr).call(v, u, [], []);
          m(h, Y, []);
        }
        h.deactivate();
      }
    }
  }
}, Dt = function() {
  if (this.linked) {
    var t = a(this, Be), r = a(this, et);
    t === null ? Rr = r : m(t, et, r), r === null ? vt = t : m(r, Be, t), this.linked = !1;
  }
};
let Ge = xr;
function Hr(e) {
  var t = Bt;
  Bt = !0;
  try {
    for (var r; ; ) {
      if (Ls(), y === null)
        return (
          /** @type {T} */
          r
        );
      y.flush();
    }
  } finally {
    Bt = t;
  }
}
function zs() {
  try {
    ys();
  } catch (e) {
    Ye(e, Dr);
  }
}
let de = null;
function bn(e) {
  var t = e.length;
  if (t !== 0) {
    for (var r = 0; r < t; ) {
      var n = e[r++];
      if ((n.f & (fe | B)) === 0 && er(n) && (de = /* @__PURE__ */ new Set(), Nt(n), n.deps === null && n.first === null && n.nodes === null && n.teardown === null && n.ac === null && Ai(n), (de == null ? void 0 : de.size) > 0)) {
        at.clear();
        for (const i of de) {
          if ((i.f & (fe | B)) !== 0) continue;
          const s = [i];
          let o = i.parent;
          for (; o !== null; )
            de.has(o) && (de.delete(o), s.push(o)), o = o.parent;
          for (let l = s.length - 1; l >= 0; l--) {
            const f = s[l];
            (f.f & (fe | B)) === 0 && Nt(f);
          }
        }
        de.clear();
      }
    }
    de = null;
  }
}
function ai(e, t, r, n) {
  if (!r.has(e) && (r.add(e), e.reactions !== null))
    for (const i of e.reactions) {
      const s = i.f;
      (s & q) !== 0 ? ai(
        /** @type {Derived} */
        i,
        t,
        r,
        n
      ) : (s & (bt | le)) !== 0 && (s & P) === 0 && fn(i, t, n) && (N(i, P), un(
        /** @type {Effect} */
        i
      ));
    }
}
function fn(e, t, r) {
  const n = r.get(e);
  if (n !== void 0) return n;
  if (e.deps !== null)
    for (const i of e.deps) {
      if (ot.call(t, i))
        return !0;
      if ((i.f & q) !== 0 && fn(
        /** @type {Derived} */
        i,
        t,
        r
      ))
        return r.set(
          /** @type {Derived} */
          i,
          !0
        ), !0;
    }
  return r.set(e, !1), !1;
}
function un(e) {
  y.schedule(e);
}
function fi(e, t) {
  if (!((e.f & ke) !== 0 && (e.f & j) !== 0)) {
    (e.f & P) !== 0 ? t.d.push(e) : (e.f & $e) !== 0 && t.m.push(e), N(e, j);
    for (var r = e.first; r !== null; )
      fi(r, t), r = r.next;
  }
}
function ui(e) {
  N(e, j);
  for (var t = e.first; t !== null; )
    ui(t), t = t.next;
}
function Is(e) {
  let t = 0, r = ht(0), n;
  return () => {
    dn() && (R(r), Si(() => (t === 0 && (n = Di(() => e(() => Vt(r)))), t += 1, () => {
      lt(() => {
        t -= 1, t === 0 && (n == null || n(), n = void 0, Vt(r));
      });
    })));
  };
}
var js = ct | pt;
function Ps(e, t, r, n) {
  new Ds(e, t, r, n);
}
var J, Kt, ne, nt, U, ie, H, X, Ne, it, Ve, Ct, Wt, Zt, Re, kr, L, ci, di, hi, Br, fr, ur, Vr, Yr;
class Ds {
  /**
   * @param {TemplateNode} node
   * @param {BoundaryProps} props
   * @param {((anchor: Node) => void)} children
   * @param {((error: unknown) => unknown) | undefined} [transform_error]
   */
  constructor(t, r, n, i) {
    x(this, L);
    /** @type {Boundary | null} */
    T(this, "parent");
    T(this, "is_pending", !1);
    /**
     * API-level transformError transform function. Transforms errors before they reach the `failed` snippet.
     * Inherited from parent boundary, or defaults to identity.
     * @type {(error: unknown) => unknown}
     */
    T(this, "transform_error");
    /** @type {TemplateNode} */
    x(this, J);
    /** @type {TemplateNode | null} */
    x(this, Kt, A ? M : null);
    /** @type {BoundaryProps} */
    x(this, ne);
    /** @type {((anchor: Node) => void)} */
    x(this, nt);
    /** @type {Effect} */
    x(this, U);
    /** @type {Effect | null} */
    x(this, ie, null);
    /** @type {Effect | null} */
    x(this, H, null);
    /** @type {Effect | null} */
    x(this, X, null);
    /** @type {DocumentFragment | null} */
    x(this, Ne, null);
    x(this, it, 0);
    x(this, Ve, 0);
    x(this, Ct, !1);
    /** @type {Set<Effect>} */
    x(this, Wt, /* @__PURE__ */ new Set());
    /** @type {Set<Effect>} */
    x(this, Zt, /* @__PURE__ */ new Set());
    /**
     * A source containing the number of pending async deriveds/expressions.
     * Only created if `$effect.pending()` is used inside the boundary,
     * otherwise updating the source results in needless `Batch.ensure()`
     * calls followed by no-op flushes
     * @type {Source<number> | null}
     */
    x(this, Re, null);
    x(this, kr, Is(() => (m(this, Re, ht(a(this, it))), () => {
      m(this, Re, null);
    })));
    var s;
    m(this, J, t), m(this, ne, r), m(this, nt, (o) => {
      var l = (
        /** @type {Effect} */
        k
      );
      l.b = this, l.f |= jr, n(o);
    }), this.parent = /** @type {Effect} */
    k.b, this.transform_error = i ?? ((s = this.parent) == null ? void 0 : s.transform_error) ?? ((o) => o), m(this, U, hn(() => {
      if (A) {
        const o = (
          /** @type {Comment} */
          a(this, Kt)
        );
        Ar();
        const l = o.data === rn;
        if (o.data.startsWith(_n)) {
          const u = JSON.parse(o.data.slice(_n.length));
          $(this, L, di).call(this, u);
        } else l ? $(this, L, hi).call(this) : $(this, L, ci).call(this);
      } else
        $(this, L, Br).call(this);
    }, js)), A && m(this, J, M);
  }
  /**
   * Defer an effect inside a pending boundary until the boundary resolves
   * @param {Effect} effect
   */
  defer_effect(t) {
    si(t, a(this, Wt), a(this, Zt));
  }
  /**
   * Returns `false` if the effect exists inside a boundary whose pending snippet is shown
   * @returns {boolean}
   */
  is_rendered() {
    return !this.is_pending && (!this.parent || this.parent.is_rendered());
  }
  has_pending_snippet() {
    return !!a(this, ne).pending;
  }
  /**
   * Update the source that powers `$effect.pending()` inside this boundary,
   * and controls when the current `pending` snippet (if any) is removed.
   * Do not call from inside the class
   * @param {1 | -1} d
   * @param {Batch} batch
   */
  update_pending_count(t, r) {
    $(this, L, Vr).call(this, t, r), m(this, it, a(this, it) + t), !(!a(this, Re) || a(this, Ct)) && (m(this, Ct, !0), lt(() => {
      m(this, Ct, !1), a(this, Re) && Ot(a(this, Re), a(this, it));
    }));
  }
  get_effect_pending() {
    return a(this, kr).call(this), R(
      /** @type {Source<number>} */
      a(this, Re)
    );
  }
  /** @param {unknown} error */
  error(t) {
    if (!a(this, ne).onerror && !a(this, ne).failed)
      throw t;
    y != null && y.is_fork ? (a(this, ie) && y.skip_effect(a(this, ie)), a(this, H) && y.skip_effect(a(this, H)), a(this, X) && y.skip_effect(a(this, X)), y.on_fork_commit(() => {
      $(this, L, Yr).call(this, t);
    })) : $(this, L, Yr).call(this, t);
  }
}
J = new WeakMap(), Kt = new WeakMap(), ne = new WeakMap(), nt = new WeakMap(), U = new WeakMap(), ie = new WeakMap(), H = new WeakMap(), X = new WeakMap(), Ne = new WeakMap(), it = new WeakMap(), Ve = new WeakMap(), Ct = new WeakMap(), Wt = new WeakMap(), Zt = new WeakMap(), Re = new WeakMap(), kr = new WeakMap(), L = new WeakSet(), ci = function() {
  try {
    m(this, ie, oe(() => a(this, nt).call(this, a(this, J))));
  } catch (t) {
    this.error(t);
  }
}, /**
 * @param {unknown} error The deserialized error from the server's hydration comment
 */
di = function(t) {
  const r = a(this, ne).failed;
  r && m(this, X, oe(() => {
    r(
      a(this, J),
      () => t,
      () => () => {
      }
    );
  }));
}, hi = function() {
  const t = a(this, ne).pending;
  t && (this.is_pending = !0, m(this, H, oe(() => t(a(this, J)))), lt(() => {
    var r = m(this, Ne, document.createDocumentFragment()), n = xe();
    r.append(n), m(this, ie, $(this, L, ur).call(this, () => oe(() => a(this, nt).call(this, n)))), a(this, Ve) === 0 && (a(this, J).before(r), m(this, Ne, null), ft(
      /** @type {Effect} */
      a(this, H),
      () => {
        m(this, H, null);
      }
    ), $(this, L, fr).call(
      this,
      /** @type {Batch} */
      y
    ));
  }));
}, Br = function() {
  try {
    if (this.is_pending = this.has_pending_snippet(), m(this, Ve, 0), m(this, it, 0), m(this, ie, oe(() => {
      a(this, nt).call(this, a(this, J));
    })), a(this, Ve) > 0) {
      var t = m(this, Ne, document.createDocumentFragment());
      vn(a(this, ie), t);
      const r = (
        /** @type {(anchor: Node) => void} */
        a(this, ne).pending
      );
      m(this, H, oe(() => r(a(this, J))));
    } else
      $(this, L, fr).call(
        this,
        /** @type {Batch} */
        y
      );
  } catch (r) {
    this.error(r);
  }
}, /**
 * @param {Batch} batch
 */
fr = function(t) {
  this.is_pending = !1, t.transfer_effects(a(this, Wt), a(this, Zt));
}, /**
 * @template T
 * @param {() => T} fn
 */
ur = function(t) {
  var r = k, n = E, i = _e;
  Ee(a(this, U)), ce(a(this, U)), Lt(a(this, U).ctx);
  try {
    return Ge.ensure(), t();
  } catch (s) {
    return ni(s), null;
  } finally {
    Ee(r), ce(n), Lt(i);
  }
}, /**
 * Updates the pending count associated with the currently visible pending snippet,
 * if any, such that we can replace the snippet with content once work is done
 * @param {1 | -1} d
 * @param {Batch} batch
 */
Vr = function(t, r) {
  var n;
  if (!this.has_pending_snippet()) {
    this.parent && $(n = this.parent, L, Vr).call(n, t, r);
    return;
  }
  m(this, Ve, a(this, Ve) + t), a(this, Ve) === 0 && ($(this, L, fr).call(this, r), a(this, H) && ft(a(this, H), () => {
    m(this, H, null);
  }), a(this, Ne) && (a(this, J).before(a(this, Ne)), m(this, Ne, null)));
}, /**
 * @param {unknown} error
 */
Yr = function(t) {
  a(this, ie) && (V(a(this, ie)), m(this, ie, null)), a(this, H) && (V(a(this, H)), m(this, H, null)), a(this, X) && (V(a(this, X)), m(this, X, null)), A && (ee(
    /** @type {TemplateNode} */
    a(this, Kt)
  ), Cs(), ee(_r()));
  var r = a(this, ne).onerror;
  let n = a(this, ne).failed;
  var i = !1, s = !1;
  const o = () => {
    if (i) {
      Ss();
      return;
    }
    i = !0, s && Es(), a(this, X) !== null && ft(a(this, X), () => {
      m(this, X, null);
    }), $(this, L, ur).call(this, () => {
      $(this, L, Br).call(this);
    });
  }, l = (f) => {
    try {
      s = !0, r == null || r(f, o), s = !1;
    } catch (u) {
      Ye(u, a(this, U) && a(this, U).parent);
    }
    n && m(this, X, $(this, L, ur).call(this, () => {
      try {
        return oe(() => {
          var u = (
            /** @type {Effect} */
            k
          );
          u.b = this, u.f |= jr, n(
            a(this, J),
            () => f,
            () => o
          );
        });
      } catch (u) {
        return Ye(
          u,
          /** @type {Effect} */
          a(this, U).parent
        ), null;
      }
    }));
  };
  lt(() => {
    var f;
    try {
      f = this.transform_error(t);
    } catch (u) {
      Ye(u, a(this, U) && a(this, U).parent);
      return;
    }
    f !== null && typeof f == "object" && typeof /** @type {any} */
    f.then == "function" ? f.then(
      l,
      /** @param {unknown} e */
      (u) => Ye(u, a(this, U) && a(this, U).parent)
    ) : l(f);
  });
};
function Fs(e, t, r, n) {
  const i = Mr;
  var s = e.filter((d) => !d.settled);
  if (r.length === 0 && s.length === 0) {
    n(t.map(i));
    return;
  }
  var o = (
    /** @type {Effect} */
    k
  ), l = qs(), f = s.length === 1 ? s[0].promise : s.length > 1 ? Promise.all(s.map((d) => d.promise)) : null;
  function u(d) {
    if ((o.f & fe) === 0) {
      l();
      try {
        n(d);
      } catch (p) {
        Ye(p, o);
      }
      mr();
    }
  }
  var v = pi();
  if (r.length === 0) {
    f.then(() => u(t.map(i))).finally(v);
    return;
  }
  function h() {
    Promise.all(r.map((d) => /* @__PURE__ */ Hs(d))).then((d) => u([...t.map(i), ...d])).catch((d) => Ye(d, o)).finally(v);
  }
  f ? f.then(() => {
    l(), h(), mr();
  }) : h();
}
function qs() {
  var e = (
    /** @type {Effect} */
    k
  ), t = E, r = _e, n = (
    /** @type {Batch} */
    y
  );
  return function(s = !0) {
    Ee(e), ce(t), Lt(r), s && (e.f & fe) === 0 && (n == null || n.activate(), n == null || n.apply());
  };
}
function mr(e = !0) {
  Ee(null), ce(null), Lt(null), e && (y == null || y.deactivate());
}
function pi() {
  var e = (
    /** @type {Effect} */
    k
  ), t = e.b, r = (
    /** @type {Batch} */
    y
  ), n = !!(t != null && t.is_rendered());
  return t == null || t.update_pending_count(1, r), r.increment(n, e), () => {
    t == null || t.update_pending_count(-1, r), r.decrement(n, e);
  };
}
// @__NO_SIDE_EFFECTS__
function Mr(e) {
  var t = q | P;
  return k !== null && (k.f |= pt), {
    ctx: _e,
    deps: null,
    effects: null,
    equals: Qn,
    f: t,
    fn: e,
    reactions: null,
    rv: 0,
    v: (
      /** @type {V} */
      I
    ),
    wv: 0,
    parent: k,
    ac: null
  };
}
const tr = Symbol("obsolete");
// @__NO_SIDE_EFFECTS__
function Hs(e, t, r) {
  let n = (
    /** @type {Effect | null} */
    k
  );
  n === null && ms();
  var i = (
    /** @type {Promise<V>} */
    /** @type {unknown} */
    void 0
  ), s = ht(
    /** @type {V} */
    I
  ), o = !E, l = /* @__PURE__ */ new Set();
  return Qs(() => {
    var p, c;
    var f = (
      /** @type {Effect} */
      k
    ), u = Zn();
    i = u.promise;
    try {
      Promise.resolve(e()).then(u.resolve, (g) => {
        g !== Sr && u.reject(g);
      }).finally(mr);
    } catch (g) {
      u.reject(g), mr();
    }
    var v = (
      /** @type {Batch} */
      y
    );
    if (o) {
      if ((f.f & We) !== 0)
        var h = pi();
      if (
        // boundary can be null if the async derived is inside an $effect.root not connected to the component render tree
        (p = n.b) != null && p.is_rendered()
      )
        (c = v.async_deriveds.get(f)) == null || c.reject(tr);
      else
        for (const g of l.values())
          g.reject(tr);
      l.add(u), v.async_deriveds.set(f, u);
    }
    const d = (g, _ = void 0) => {
      h == null || h(), l.delete(u), _ !== tr && (v.activate(), _ ? (s.f |= Ue, Ot(s, _)) : ((s.f & Ue) !== 0 && (s.f ^= Ue), Ot(s, g)), v.deactivate());
    };
    u.promise.then(d, (g) => d(null, g || "unknown"));
  }), Ks(() => {
    for (const f of l)
      f.reject(tr);
  }), new Promise((f) => {
    function u(v) {
      function h() {
        v === i ? f(s) : u(i);
      }
      v.then(h, h);
    }
    u(i);
  });
}
// @__NO_SIDE_EFFECTS__
function xn(e) {
  const t = /* @__PURE__ */ Mr(e);
  return Oi(t), t;
}
// @__NO_SIDE_EFFECTS__
function Bs(e) {
  const t = /* @__PURE__ */ Mr(e);
  return t.equals = ei, t;
}
function Vs(e) {
  var t = e.effects;
  if (t !== null) {
    e.effects = null;
    for (var r = 0; r < t.length; r += 1)
      V(
        /** @type {Effect} */
        t[r]
      );
  }
}
function cn(e) {
  var t, r = k, n = e.parent;
  if (!Ke && n !== null && e.v !== I && // if it was never evaluated before, it's guaranteed to fail downstream, so we try to execute instead
  (n.f & (fe | B)) !== 0)
    return Ts(), e.v;
  Ee(n);
  try {
    e.f &= ~dt, Vs(e), t = Ii(e);
  } finally {
    Ee(r);
  }
  return t;
}
function vi(e) {
  var t = cn(e);
  if (!e.equals(t) && (e.wv = Ri(), (!(y != null && y.is_fork) || e.deps === null) && (y !== null ? (y.capture(e, t, !0), Ht == null || Ht.capture(e, t, !0)) : e.v = t, e.deps === null))) {
    N(e, j);
    return;
  }
  Ke || (F !== null ? (dn() || y != null && y.is_fork) && F.set(e, t) : an(e));
}
function Ys(e) {
  var t, r;
  if (e.effects !== null)
    for (const n of e.effects)
      (n.teardown || n.ac) && ((t = n.teardown) == null || t.call(n), (r = n.ac) == null || r.abort(Sr), n.fn !== null && (n.teardown = gt), n.ac = null, Ut(n, 0), pn(n));
}
function gi(e) {
  if (e.effects !== null)
    for (const t of e.effects)
      t.teardown && t.fn !== null && Nt(t);
}
let wr = /* @__PURE__ */ new Set();
const at = /* @__PURE__ */ new Map();
let _i = !1;
function ht(e, t) {
  var r = {
    f: 0,
    // TODO ideally we could skip this altogether, but it causes type errors
    v: e,
    reactions: null,
    equals: Qn,
    rv: 0,
    wv: 0
  };
  return r;
}
// @__NO_SIDE_EFFECTS__
function Me(e, t) {
  const r = ht(e);
  return Oi(r), r;
}
// @__NO_SIDE_EFFECTS__
function mi(e, t = !1, r = !0) {
  const n = ht(e);
  return t || (n.equals = ei), n;
}
function ve(e, t, r = !1) {
  E !== null && // since we are untracking the function inside `$inspect.with` we need to add this check
  // to ensure we error if state is set inside an inspect effect
  (!ge || (E.f & vr) !== 0) && ti() && (E.f & (q | le | bt | vr)) !== 0 && (ue === null || !ot.call(ue, e)) && $s();
  let n = r ? wt(t) : t;
  return Ot(e, n, ar);
}
function Ot(e, t, r = null) {
  if (!e.equals(t)) {
    at.set(e, Ke ? t : e.v);
    var n = Ge.ensure();
    if (n.capture(e, t), (e.f & q) !== 0) {
      const i = (
        /** @type {Derived} */
        e
      );
      (e.f & P) !== 0 && cn(i), F === null && an(i);
    }
    e.wv = Ri(), wi(e, P, r), k !== null && (k.f & j) !== 0 && (k.f & (ke | De)) === 0 && (re === null ? ro([e]) : re.push(e)), !n.is_fork && wr.size > 0 && !_i && Us();
  }
  return t;
}
function Us() {
  _i = !1;
  for (const e of wr) {
    (e.f & j) !== 0 && N(e, $e);
    let t;
    try {
      t = er(e);
    } catch {
      t = !0;
    }
    t && Nt(e);
  }
  wr.clear();
}
function Vt(e) {
  ve(e, e.v + 1);
}
function wi(e, t, r) {
  var n = e.reactions;
  if (n !== null)
    for (var i = n.length, s = 0; s < i; s++) {
      var o = n[s], l = o.f, f = (l & P) === 0;
      if (f && N(o, t), (l & vr) !== 0)
        wr.add(
          /** @type {Effect} */
          o
        );
      else if ((l & q) !== 0) {
        var u = (
          /** @type {Derived} */
          o
        );
        F == null || F.delete(u), (l & dt) === 0 && (l & ae && (k === null || (k.f & gr) === 0) && (o.f |= dt), wi(u, $e, r));
      } else if (f) {
        var v = (
          /** @type {Effect} */
          o
        );
        (l & le) !== 0 && de !== null && de.add(v), r !== null ? r.push(v) : un(v);
      }
    }
}
function wt(e) {
  if (typeof e != "object" || e === null || Nr in e)
    return e;
  const t = Wn(e);
  if (t !== as && t !== fs)
    return e;
  var r = /* @__PURE__ */ new Map(), n = Kn(e), i = /* @__PURE__ */ Me(0), s = ut, o = (l) => {
    if (ut === s)
      return l();
    var f = E, u = ut;
    ce(null), En(s);
    var v = l();
    return ce(f), En(u), v;
  };
  return n && r.set("length", /* @__PURE__ */ Me(
    /** @type {any[]} */
    e.length
  )), new Proxy(
    /** @type {any} */
    e,
    {
      defineProperty(l, f, u) {
        (!("value" in u) || u.configurable === !1 || u.enumerable === !1 || u.writable === !1) && xs();
        var v = r.get(f);
        return v === void 0 ? o(() => {
          var h = /* @__PURE__ */ Me(u.value);
          return r.set(f, h), h;
        }) : ve(v, u.value, !0), !0;
      },
      deleteProperty(l, f) {
        var u = r.get(f);
        if (u === void 0) {
          if (f in l) {
            const v = o(() => /* @__PURE__ */ Me(I));
            r.set(f, v), Vt(i);
          }
        } else
          ve(u, I), Vt(i);
        return !0;
      },
      get(l, f, u) {
        var p;
        if (f === Nr)
          return e;
        var v = r.get(f), h = f in l;
        if (v === void 0 && (!h || (p = yt(l, f)) != null && p.writable) && (v = o(() => {
          var c = wt(h ? l[f] : I), g = /* @__PURE__ */ Me(c);
          return g;
        }), r.set(f, v)), v !== void 0) {
          var d = R(v);
          return d === I ? void 0 : d;
        }
        return Reflect.get(l, f, u);
      },
      getOwnPropertyDescriptor(l, f) {
        var u = Reflect.getOwnPropertyDescriptor(l, f);
        if (u && "value" in u) {
          var v = r.get(f);
          v && (u.value = R(v));
        } else if (u === void 0) {
          var h = r.get(f), d = h == null ? void 0 : h.v;
          if (h !== void 0 && d !== I)
            return {
              enumerable: !0,
              configurable: !0,
              value: d,
              writable: !0
            };
        }
        return u;
      },
      has(l, f) {
        var d;
        if (f === Nr)
          return !0;
        var u = r.get(f), v = u !== void 0 && u.v !== I || Reflect.has(l, f);
        if (u !== void 0 || k !== null && (!v || (d = yt(l, f)) != null && d.writable)) {
          u === void 0 && (u = o(() => {
            var p = v ? wt(l[f]) : I, c = /* @__PURE__ */ Me(p);
            return c;
          }), r.set(f, u));
          var h = R(u);
          if (h === I)
            return !1;
        }
        return v;
      },
      set(l, f, u, v) {
        var S;
        var h = r.get(f), d = f in l;
        if (n && f === "length")
          for (var p = u; p < /** @type {Source<number>} */
          h.v; p += 1) {
            var c = r.get(p + "");
            c !== void 0 ? ve(c, I) : p in l && (c = o(() => /* @__PURE__ */ Me(I)), r.set(p + "", c));
          }
        if (h === void 0)
          (!d || (S = yt(l, f)) != null && S.writable) && (h = o(() => /* @__PURE__ */ Me(void 0)), ve(h, wt(u)), r.set(f, h));
        else {
          d = h.v !== I;
          var g = o(() => wt(u));
          ve(h, g);
        }
        var _ = Reflect.getOwnPropertyDescriptor(l, f);
        if (_ != null && _.set && _.set.call(v, u), !d) {
          if (n && typeof f == "string") {
            var w = (
              /** @type {Source<number>} */
              r.get("length")
            ), b = Number(f);
            Number.isInteger(b) && b >= w.v && ve(w, b + 1);
          }
          Vt(i);
        }
        return !0;
      },
      ownKeys(l) {
        R(i);
        var f = Reflect.ownKeys(l).filter((h) => {
          var d = r.get(h);
          return d === void 0 || d.v !== I;
        });
        for (var [u, v] of r)
          v.v !== I && !(u in l) && f.push(u);
        return f;
      },
      setPrototypeOf() {
        ks();
      }
    }
  );
}
var kn, yi, bi, xi;
function Ur() {
  if (kn === void 0) {
    kn = window, yi = /Firefox/.test(navigator.userAgent);
    var e = Element.prototype, t = Node.prototype, r = Text.prototype;
    bi = yt(t, "firstChild").get, xi = yt(t, "nextSibling").get, mn(e) && (e[Pr] = void 0, e[lr] = null, e[gs] = void 0, e.__e = void 0), mn(r) && (r[jt] = void 0);
  }
}
function xe(e = "") {
  return document.createTextNode(e);
}
// @__NO_SIDE_EFFECTS__
function Yt(e) {
  return (
    /** @type {TemplateNode | null} */
    bi.call(e)
  );
}
// @__NO_SIDE_EFFECTS__
function Fe(e) {
  return (
    /** @type {TemplateNode | null} */
    xi.call(e)
  );
}
function He(e, t) {
  if (!A)
    return /* @__PURE__ */ Yt(e);
  var r = /* @__PURE__ */ Yt(M);
  if (r === null)
    r = M.appendChild(xe());
  else if (t && r.nodeType !== sn) {
    var n = xe();
    return r == null || r.before(n), ee(n), n;
  }
  return t && Ti(
    /** @type {Text} */
    r
  ), ee(r), r;
}
function Ft(e, t = 1, r = !1) {
  let n = A ? M : e;
  for (var i; t--; )
    i = n, n = /** @type {TemplateNode} */
    /* @__PURE__ */ Fe(n);
  if (!A)
    return n;
  if (r) {
    if ((n == null ? void 0 : n.nodeType) !== sn) {
      var s = xe();
      return n === null ? i == null || i.after(s) : n.before(s), ee(s), s;
    }
    Ti(
      /** @type {Text} */
      n
    );
  }
  return ee(n), n;
}
function ki(e) {
  e.textContent = "";
}
function $i() {
  return !1;
}
function Ei(e, t, r) {
  return (
    /** @type {T extends keyof HTMLElementTagNameMap ? HTMLElementTagNameMap[T] : Element} */
    document.createElementNS(Un, e, void 0)
  );
}
function Ti(e) {
  if (
    /** @type {string} */
    e.nodeValue.length < 65536
  )
    return;
  let t = e.nextSibling;
  for (; t !== null && t.nodeType === sn; )
    t.remove(), e.nodeValue += /** @type {string} */
    t.nodeValue, t = e.nextSibling;
}
function Lr(e) {
  var t = E, r = k;
  ce(null), Ee(null);
  try {
    return e();
  } finally {
    ce(t), Ee(r);
  }
}
function Gs(e, t) {
  var r = t.last;
  r === null ? t.last = t.first = e : (r.next = e, e.prev = r, t.last = e);
}
function Se(e, t) {
  var r = k;
  r !== null && (r.f & B) !== 0 && (e |= B);
  var n = {
    ctx: _e,
    deps: null,
    nodes: null,
    f: e | P | ae,
    first: null,
    fn: t,
    last: null,
    next: null,
    parent: r,
    b: r && r.b,
    prev: null,
    teardown: null,
    wv: 0,
    ac: null
  };
  y == null || y.register_created_effect(n);
  var i = n;
  if ((e & Mt) !== 0)
    mt !== null ? mt.push(n) : Ge.ensure().schedule(n);
  else if (t !== null) {
    try {
      Nt(n);
    } catch (o) {
      throw V(n), o;
    }
    i.deps === null && i.teardown === null && i.nodes === null && i.first === i.last && // either `null`, or a singular child
    (i.f & pt) === 0 && (i = i.first, (e & le) !== 0 && (e & ct) !== 0 && i !== null && (i.f |= ct));
  }
  if (i !== null && (i.parent = r, r !== null && Gs(i, r), E !== null && (E.f & q) !== 0 && (e & De) === 0)) {
    var s = (
      /** @type {Derived} */
      E
    );
    (s.effects ?? (s.effects = [])).push(i);
  }
  return n;
}
function dn() {
  return E !== null && !ge;
}
function Ks(e) {
  const t = Se(Tr, null);
  return N(t, j), t.teardown = e, t;
}
function Ws(e) {
  return Se(Mt | hs, e);
}
function Zs(e) {
  Ge.ensure();
  const t = Se(De | pt, e);
  return () => {
    V(t);
  };
}
function Js(e) {
  Ge.ensure();
  const t = Se(De | pt, e);
  return (r = {}) => new Promise((n) => {
    r.outro ? ft(t, () => {
      V(t), n(void 0);
    }) : (V(t), n(void 0));
  });
}
function Xs(e) {
  return Se(Mt, e);
}
function Qs(e) {
  return Se(bt | pt, e);
}
function Si(e, t = 0) {
  return Se(Tr | t, e);
}
function zt(e, t = [], r = [], n = []) {
  Fs(n, t, r, (i) => {
    Se(Tr, () => e(...i.map(R)));
  });
}
function hn(e, t = 0) {
  var r = Se(le | t, e);
  return r;
}
function oe(e) {
  return Se(ke | pt, e);
}
function Ci(e) {
  var t = e.teardown;
  if (t !== null) {
    const r = Ke, n = E;
    $n(!0), ce(null);
    try {
      t.call(null);
    } finally {
      $n(r), ce(n);
    }
  }
}
function pn(e, t = !1) {
  var r = e.first;
  for (e.first = e.last = null; r !== null; ) {
    const i = r.ac;
    i !== null && Lr(() => {
      i.abort(Sr);
    });
    var n = r.next;
    (r.f & De) !== 0 ? r.parent = null : V(r, t), r = n;
  }
}
function eo(e) {
  for (var t = e.first; t !== null; ) {
    var r = t.next;
    (t.f & ke) === 0 && V(t), t = r;
  }
}
function V(e, t = !0) {
  var r = !1;
  (t || (e.f & ds) !== 0) && e.nodes !== null && e.nodes.end !== null && (to(
    e.nodes.start,
    /** @type {TemplateNode} */
    e.nodes.end
  ), r = !0), N(e, wn), pn(e, t && !r), Ut(e, 0);
  var n = e.nodes && e.nodes.t;
  if (n !== null)
    for (const s of n)
      s.stop();
  Ci(e), e.f ^= wn, e.f |= fe;
  var i = e.parent;
  i !== null && i.first !== null && Ai(e), e.next = e.prev = e.teardown = e.ctx = e.deps = e.fn = e.nodes = e.ac = e.b = null;
}
function to(e, t) {
  for (; e !== null; ) {
    var r = e === t ? null : /* @__PURE__ */ Fe(e);
    e.remove(), e = r;
  }
}
function Ai(e) {
  var t = e.parent, r = e.prev, n = e.next;
  r !== null && (r.next = n), n !== null && (n.prev = r), t !== null && (t.first === e && (t.first = n), t.last === e && (t.last = r));
}
function ft(e, t, r = !0) {
  var n = [];
  Mi(e, n, !0);
  var i = () => {
    r && V(e), t && t();
  }, s = n.length;
  if (s > 0) {
    var o = () => --s || i();
    for (var l of n)
      l.out(o);
  } else
    i();
}
function Mi(e, t, r) {
  if ((e.f & B) === 0) {
    e.f ^= B;
    var n = e.nodes && e.nodes.t;
    if (n !== null)
      for (const l of n)
        (l.is_global || r) && t.push(l);
    for (var i = e.first; i !== null; ) {
      var s = i.next;
      if ((i.f & De) === 0) {
        var o = (i.f & ct) !== 0 || // If this is a branch effect without a block effect parent,
        // it means the parent block effect was pruned. In that case,
        // transparency information was transferred to the branch effect.
        (i.f & ke) !== 0 && (e.f & le) !== 0;
        Mi(i, t, o ? r : !1);
      }
      i = s;
    }
  }
}
function yr(e) {
  Li(e, !0);
}
function Li(e, t) {
  if ((e.f & B) !== 0) {
    e.f ^= B, (e.f & j) === 0 && (N(e, P), Ge.ensure().schedule(e));
    for (var r = e.first; r !== null; ) {
      var n = r.next, i = (r.f & ct) !== 0 || (r.f & ke) !== 0;
      Li(r, i ? t : !1), r = n;
    }
    var s = e.nodes && e.nodes.t;
    if (s !== null)
      for (const o of s)
        (o.is_global || t) && o.in();
  }
}
function vn(e, t) {
  if (e.nodes)
    for (var r = e.nodes.start, n = e.nodes.end; r !== null; ) {
      var i = r === n ? null : /* @__PURE__ */ Fe(r);
      t.append(r), r = i;
    }
}
let cr = !1, Ke = !1;
function $n(e) {
  Ke = e;
}
let E = null, ge = !1;
function ce(e) {
  E = e;
}
let k = null;
function Ee(e) {
  k = e;
}
let ue = null;
function Oi(e) {
  E !== null && (ue === null ? ue = [e] : ue.push(e));
}
let G = null, Z = 0, re = null;
function ro(e) {
  re = e;
}
let Ni = 1, Xe = 0, ut = Xe;
function En(e) {
  ut = e;
}
function Ri() {
  return ++Ni;
}
function er(e) {
  var t = e.f;
  if ((t & P) !== 0)
    return !0;
  if (t & q && (e.f &= ~dt), (t & $e) !== 0) {
    for (var r = (
      /** @type {Value[]} */
      e.deps
    ), n = r.length, i = 0; i < n; i++) {
      var s = r[i];
      if (er(
        /** @type {Derived} */
        s
      ) && vi(
        /** @type {Derived} */
        s
      ), s.wv > e.wv)
        return !0;
    }
    (t & ae) !== 0 && // During time traveling we don't want to reset the status so that
    // traversal of the graph in the other batches still happens
    F === null && N(e, j);
  }
  return !1;
}
function zi(e, t, r = !0) {
  var n = e.reactions;
  if (n !== null && !(ue !== null && ot.call(ue, e)))
    for (var i = 0; i < n.length; i++) {
      var s = n[i];
      (s.f & q) !== 0 ? zi(
        /** @type {Derived} */
        s,
        t,
        !1
      ) : t === s && (r ? N(s, P) : (s.f & j) !== 0 && N(s, $e), un(
        /** @type {Effect} */
        s
      ));
    }
}
function Ii(e) {
  var g;
  var t = G, r = Z, n = re, i = E, s = ue, o = _e, l = ge, f = ut, u = e.f;
  G = /** @type {null | Value[]} */
  null, Z = 0, re = null, E = (u & (ke | De)) === 0 ? e : null, ue = null, Lt(e.ctx), ge = !1, ut = ++Xe, e.ac !== null && (Lr(() => {
    e.ac.abort(Sr);
  }), e.ac = null);
  try {
    e.f |= gr;
    var v = (
      /** @type {Function} */
      e.fn
    ), h = v();
    e.f |= We;
    var d = e.deps, p = y == null ? void 0 : y.is_fork;
    if (G !== null) {
      var c;
      if (p || Ut(e, Z), d !== null && Z > 0)
        for (d.length = Z + G.length, c = 0; c < G.length; c++)
          d[Z + c] = G[c];
      else
        e.deps = d = G;
      if (dn() && (e.f & ae) !== 0)
        for (c = Z; c < d.length; c++)
          ((g = d[c]).reactions ?? (g.reactions = [])).push(e);
    } else !p && d !== null && Z < d.length && (Ut(e, Z), d.length = Z);
    if (ti() && re !== null && !ge && d !== null && (e.f & (q | $e | P)) === 0)
      for (c = 0; c < /** @type {Source[]} */
      re.length; c++)
        zi(
          re[c],
          /** @type {Effect} */
          e
        );
    if (i !== null && i !== e) {
      if (Xe++, i.deps !== null)
        for (let _ = 0; _ < r; _ += 1)
          i.deps[_].rv = Xe;
      if (t !== null)
        for (const _ of t)
          _.rv = Xe;
      re !== null && (n === null ? n = re : n.push(.../** @type {Source[]} */
      re));
    }
    return (e.f & Ue) !== 0 && (e.f ^= Ue), h;
  } catch (_) {
    return ni(_);
  } finally {
    e.f ^= gr, G = t, Z = r, re = n, E = i, ue = s, Lt(o), ge = l, ut = f;
  }
}
function no(e, t) {
  let r = t.reactions;
  if (r !== null) {
    var n = os.call(r, e);
    if (n !== -1) {
      var i = r.length - 1;
      i === 0 ? r = t.reactions = null : (r[n] = r[i], r.pop());
    }
  }
  if (r === null && (t.f & q) !== 0 && // Destroying a child effect while updating a parent effect can cause a dependency to appear
  // to be unused, when in fact it is used by the currently-updating parent. Checking `new_deps`
  // allows us to skip the expensive work of disconnecting and immediately reconnecting it
  (G === null || !ot.call(G, t))) {
    var s = (
      /** @type {Derived} */
      t
    );
    (s.f & ae) !== 0 && (s.f ^= ae, s.f &= ~dt), s.v !== I && an(s), Ys(s), Ut(s, 0);
  }
}
function Ut(e, t) {
  var r = e.deps;
  if (r !== null)
    for (var n = t; n < r.length; n++)
      no(e, r[n]);
}
function Nt(e) {
  var t = e.f;
  if ((t & fe) === 0) {
    N(e, j);
    var r = k, n = cr;
    k = e, cr = !0;
    try {
      (t & (le | Jn)) !== 0 ? eo(e) : pn(e), Ci(e);
      var i = Ii(e);
      e.teardown = typeof i == "function" ? i : null, e.wv = Ni;
      var s;
      Gn && Ms && (e.f & P) !== 0 && e.deps;
    } finally {
      cr = n, k = r;
    }
  }
}
function R(e) {
  var t = e.f, r = (t & q) !== 0;
  if (E !== null && !ge) {
    var n = k !== null && (k.f & fe) !== 0;
    if (!n && (ue === null || !ot.call(ue, e))) {
      var i = E.deps;
      if ((E.f & gr) !== 0)
        e.rv < Xe && (e.rv = Xe, G === null && i !== null && i[Z] === e ? Z++ : G === null ? G = [e] : G.push(e));
      else {
        E.deps ?? (E.deps = []), ot.call(E.deps, e) || E.deps.push(e);
        var s = e.reactions;
        s === null ? e.reactions = [E] : ot.call(s, E) || s.push(E);
      }
    }
  }
  if (Ke && at.has(e))
    return at.get(e);
  if (r) {
    var o = (
      /** @type {Derived} */
      e
    );
    if (Ke) {
      var l = o.v;
      return ((o.f & j) === 0 && o.reactions !== null || Pi(o)) && (l = cn(o)), at.set(o, l), l;
    }
    var f = (o.f & ae) === 0 && !ge && E !== null && (cr || (E.f & ae) !== 0), u = (o.f & We) === 0;
    er(o) && (f && (o.f |= ae), vi(o)), f && !u && (gi(o), ji(o));
  }
  if (F != null && F.has(e))
    return F.get(e);
  if ((e.f & Ue) !== 0)
    throw e.v;
  return e.v;
}
function ji(e) {
  if (e.f |= ae, e.deps !== null)
    for (const t of e.deps)
      (t.reactions ?? (t.reactions = [])).push(e), (t.f & q) !== 0 && (t.f & ae) === 0 && (gi(
        /** @type {Derived} */
        t
      ), ji(
        /** @type {Derived} */
        t
      ));
}
function Pi(e) {
  if (e.v === I) return !0;
  if (e.deps === null) return !1;
  for (const t of e.deps)
    if (at.has(t) || (t.f & q) !== 0 && Pi(
      /** @type {Derived} */
      t
    ))
      return !0;
  return !1;
}
function Di(e) {
  var t = ge;
  try {
    return ge = !0, e();
  } finally {
    ge = t;
  }
}
const Qe = Symbol("events"), Fi = /* @__PURE__ */ new Set(), Gr = /* @__PURE__ */ new Set();
function io(e, t, r) {
  (t[Qe] ?? (t[Qe] = {}))[e] = r;
}
function so(e) {
  for (var t = 0; t < e.length; t++)
    Fi.add(e[t]);
  for (var r of Gr)
    r(e);
}
let Tn = null;
function Sn(e) {
  var g, _;
  var t = this, r = (
    /** @type {Node} */
    t.ownerDocument
  ), n = e.type, i = ((g = e.composedPath) == null ? void 0 : g.call(e)) || [], s = (
    /** @type {null | Element} */
    i[0] || e.target
  );
  Tn = e;
  var o = 0, l = Tn === e && e[Qe];
  if (l) {
    var f = i.indexOf(l);
    if (f !== -1 && (t === document || t === /** @type {any} */
    window)) {
      e[Qe] = t;
      return;
    }
    var u = i.indexOf(t);
    if (u === -1)
      return;
    f <= u && (o = f);
  }
  if (s = /** @type {Element} */
  i[o] || e.target, s !== t) {
    pr(e, "currentTarget", {
      configurable: !0,
      get() {
        return s || r;
      }
    });
    var v = E, h = k;
    ce(null), Ee(null);
    try {
      for (var d, p = []; s !== null && s !== t; ) {
        try {
          var c = (_ = s[Qe]) == null ? void 0 : _[n];
          c != null && (!/** @type {any} */
          s.disabled || // DOM could've been updated already by the time this is reached, so we check this as well
          // -> the target could not have been disabled because it emits the event in the first place
          e.target === s) && c.call(s, e);
        } catch (w) {
          d ? p.push(w) : d = w;
        }
        if (e.cancelBubble) break;
        o++, s = o < i.length ? (
          /** @type {Element} */
          i[o]
        ) : null;
      }
      if (d) {
        for (let w of p)
          queueMicrotask(() => {
            throw w;
          });
        throw d;
      }
    } finally {
      e[Qe] = t, delete e.currentTarget, ce(v), Ee(h);
    }
  }
}
var Vn;
const Ir = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  ((Vn = globalThis == null ? void 0 : globalThis.window) == null ? void 0 : Vn.trustedTypes) && /* @__PURE__ */ globalThis.window.trustedTypes.createPolicy("svelte-trusted-html", {
    /** @param {string} html */
    createHTML: (e) => e
  })
);
function oo(e) {
  return (
    /** @type {string} */
    (Ir == null ? void 0 : Ir.createHTML(e)) ?? e
  );
}
function lo(e) {
  var t = Ei("template");
  return t.innerHTML = oo(e.replaceAll("<!>", "<!---->")), t.content;
}
function Kr(e, t) {
  var r = (
    /** @type {Effect} */
    k
  );
  r.nodes === null && (r.nodes = { start: e, end: t, a: null, t: null });
}
// @__NO_SIDE_EFFECTS__
function Rt(e, t) {
  var r = (t & ss) !== 0, n, i = !e.startsWith("<!>");
  return () => {
    if (A)
      return Kr(M, null), M;
    n === void 0 && (n = lo(i ? e : "<!>" + e), n = /** @type {TemplateNode} */
    /* @__PURE__ */ Yt(n));
    var s = (
      /** @type {TemplateNode} */
      r || yi ? document.importNode(n, !0) : n.cloneNode(!0)
    );
    return Kr(s, s), s;
  };
}
function Ze(e, t) {
  if (A) {
    var r = (
      /** @type {Effect & { nodes: EffectNodes }} */
      k
    );
    ((r.f & We) === 0 || r.nodes.end === null) && (r.nodes.end = M), Ar();
    return;
  }
  e !== null && e.before(
    /** @type {Node} */
    t
  );
}
const ao = ["touchstart", "touchmove"];
function fo(e) {
  return ao.includes(e);
}
let Wr = !0;
function rr(e, t) {
  var r = t == null ? "" : typeof t == "object" ? `${t}` : t;
  r !== /** @type {any} */
  (e[jt] ?? (e[jt] = e.nodeValue)) && (e[jt] = r, e.nodeValue = `${r}`);
}
function qi(e, t) {
  return Hi(e, t);
}
function uo(e, t) {
  Ur(), t.intro = t.intro ?? !1;
  const r = t.target, n = A, i = M;
  try {
    for (var s = /* @__PURE__ */ Yt(r); s && (s.nodeType !== Qt || /** @type {Comment} */
    s.data !== Yn); )
      s = /* @__PURE__ */ Fe(s);
    if (!s)
      throw At;
    Pe(!0), ee(
      /** @type {Comment} */
      s
    );
    const o = Hi(e, { ...t, anchor: s });
    return Pe(!1), /**  @type {Exports} */
    o;
  } catch (o) {
    if (o instanceof Error && o.message.split(`
`).some((l) => l.startsWith("https://svelte.dev/e/")))
      throw o;
    return o !== At && console.warn("Failed to hydrate: ", o), t.recover === !1 && bs(), Ur(), ki(r), Pe(!1), qi(e, t);
  } finally {
    Pe(n), ee(i);
  }
}
const nr = /* @__PURE__ */ new Map();
function Hi(e, { target: t, anchor: r, props: n = {}, events: i, context: s, intro: o = !0, transformError: l }) {
  Ur();
  var f = void 0, u = Js(() => {
    var v = r ?? t.appendChild(xe());
    Ps(
      /** @type {TemplateNode} */
      v,
      {
        pending: () => {
        }
      },
      (p) => {
        on({});
        var c = (
          /** @type {ComponentContext} */
          _e
        );
        if (s && (c.c = s), i && (n.$$events = i), A && Kr(
          /** @type {TemplateNode} */
          p,
          null
        ), Wr = o, f = e(p, n) || {}, Wr = !0, A && (k.nodes.end = M, M === null || M.nodeType !== Qt || /** @type {Comment} */
        M.data !== nn))
          throw Cr(), At;
        ln();
      },
      l
    );
    var h = /* @__PURE__ */ new Set(), d = (p) => {
      for (var c = 0; c < p.length; c++) {
        var g = p[c];
        if (!h.has(g)) {
          h.add(g);
          var _ = fo(g);
          for (const S of [t, document]) {
            var w = nr.get(S);
            w === void 0 && (w = /* @__PURE__ */ new Map(), nr.set(S, w));
            var b = w.get(g);
            b === void 0 ? (S.addEventListener(g, Sn, { passive: _ }), w.set(g, 1)) : w.set(g, b + 1);
          }
        }
      }
    };
    return d(Er(Fi)), Gr.add(d), () => {
      var _;
      for (var p of h)
        for (const w of [t, document]) {
          var c = (
            /** @type {Map<string, number>} */
            nr.get(w)
          ), g = (
            /** @type {number} */
            c.get(p)
          );
          --g == 0 ? (w.removeEventListener(p, Sn), c.delete(p), c.size === 0 && nr.delete(w)) : c.set(p, g);
        }
      Gr.delete(d), v !== r && ((_ = v.parentNode) == null || _.removeChild(v));
    };
  });
  return Zr.set(f, u), f;
}
let Zr = /* @__PURE__ */ new WeakMap();
function co(e, t) {
  const r = Zr.get(e);
  return r ? (Zr.delete(e), r(t)) : Promise.resolve();
}
var pe, be, Q, st, Jt, Xt, $r;
class ho {
  /**
   * @param {TemplateNode} anchor
   * @param {boolean} transition
   */
  constructor(t, r = !0) {
    /** @type {TemplateNode} */
    T(this, "anchor");
    /** @type {Map<Batch, Key>} */
    x(this, pe, /* @__PURE__ */ new Map());
    /**
     * Map of keys to effects that are currently rendered in the DOM.
     * These effects are visible and actively part of the document tree.
     * Example:
     * ```
     * {#if condition}
     * 	foo
     * {:else}
     * 	bar
     * {/if}
     * ```
     * Can result in the entries `true->Effect` and `false->Effect`
     * @type {Map<Key, Effect>}
     */
    x(this, be, /* @__PURE__ */ new Map());
    /**
     * Similar to #onscreen with respect to the keys, but contains branches that are not yet
     * in the DOM, because their insertion is deferred.
     * @type {Map<Key, Branch>}
     */
    x(this, Q, /* @__PURE__ */ new Map());
    /**
     * Keys of effects that are currently outroing
     * @type {Set<Key>}
     */
    x(this, st, /* @__PURE__ */ new Set());
    /**
     * Whether to pause (i.e. outro) on change, or destroy immediately.
     * This is necessary for `<svelte:element>`
     */
    x(this, Jt, !0);
    /**
     * @param {Batch} batch
     */
    x(this, Xt, (t) => {
      if (a(this, pe).has(t)) {
        var r = (
          /** @type {Key} */
          a(this, pe).get(t)
        ), n = a(this, be).get(r);
        if (n)
          yr(n), a(this, st).delete(r);
        else {
          var i = a(this, Q).get(r);
          i && (yr(i.effect), a(this, be).set(r, i.effect), a(this, Q).delete(r), i.fragment.lastChild.remove(), this.anchor.before(i.fragment), n = i.effect);
        }
        for (const [s, o] of a(this, pe)) {
          if (a(this, pe).delete(s), s === t)
            break;
          const l = a(this, Q).get(o);
          l && (V(l.effect), a(this, Q).delete(o));
        }
        for (const [s, o] of a(this, be)) {
          if (s === r || a(this, st).has(s)) continue;
          const l = () => {
            if (Array.from(a(this, pe).values()).includes(s)) {
              var u = document.createDocumentFragment();
              vn(o, u), u.append(xe()), a(this, Q).set(s, { effect: o, fragment: u });
            } else
              V(o);
            a(this, st).delete(s), a(this, be).delete(s);
          };
          a(this, Jt) || !n ? (a(this, st).add(s), ft(o, l, !1)) : l();
        }
      }
    });
    /**
     * @param {Batch} batch
     */
    x(this, $r, (t) => {
      a(this, pe).delete(t);
      const r = Array.from(a(this, pe).values());
      for (const [n, i] of a(this, Q))
        r.includes(n) || (V(i.effect), a(this, Q).delete(n));
    });
    this.anchor = t, m(this, Jt, r);
  }
  /**
   *
   * @param {any} key
   * @param {null | ((target: TemplateNode) => void)} fn
   */
  ensure(t, r) {
    var n = (
      /** @type {Batch} */
      y
    ), i = $i();
    if (r && !a(this, be).has(t) && !a(this, Q).has(t))
      if (i) {
        var s = document.createDocumentFragment(), o = xe();
        s.append(o), a(this, Q).set(t, {
          effect: oe(() => r(o)),
          fragment: s
        });
      } else
        a(this, be).set(
          t,
          oe(() => r(this.anchor))
        );
    if (a(this, pe).set(n, t), i) {
      for (const [l, f] of a(this, be))
        l === t ? n.unskip_effect(f) : n.skip_effect(f);
      for (const [l, f] of a(this, Q))
        l === t ? n.unskip_effect(f.effect) : n.skip_effect(f.effect);
      n.oncommit(a(this, Xt)), n.ondiscard(a(this, $r));
    } else
      A && (this.anchor = M), a(this, Xt).call(this, n);
  }
}
pe = new WeakMap(), be = new WeakMap(), Q = new WeakMap(), st = new WeakMap(), Jt = new WeakMap(), Xt = new WeakMap(), $r = new WeakMap();
function ir(e, t, r = !1) {
  var n;
  A && (n = M, Ar());
  var i = new ho(e), s = r ? ct : 0;
  function o(l, f) {
    if (A) {
      var u = Xn(
        /** @type {TemplateNode} */
        n
      );
      if (l !== parseInt(u.substring(1))) {
        var v = _r();
        ee(v), i.anchor = v, Pe(!1), i.ensure(l, f), Pe(!0);
        return;
      }
    }
    i.ensure(l, f);
  }
  hn(() => {
    var l = !1;
    t((f, u = 0) => {
      l = !0, o(u, f);
    }), l || o(-1, null);
  }, s);
}
function po(e, t, r) {
  for (var n = [], i = t.length, s, o = t.length, l = 0; l < i; l++) {
    let h = t[l];
    ft(
      h,
      () => {
        if (s) {
          if (s.pending.delete(h), s.done.add(h), s.pending.size === 0) {
            var d = (
              /** @type {Set<EachOutroGroup>} */
              e.outrogroups
            );
            Jr(e, Er(s.done)), d.delete(s), d.size === 0 && (e.outrogroups = null);
          }
        } else
          o -= 1;
      },
      !1
    );
  }
  if (o === 0) {
    var f = n.length === 0 && r !== null;
    if (f) {
      var u = (
        /** @type {Element} */
        r
      ), v = (
        /** @type {Element} */
        u.parentNode
      );
      ki(v), v.append(u), e.items.clear();
    }
    Jr(e, t, !f);
  } else
    s = {
      pending: new Set(t),
      done: /* @__PURE__ */ new Set()
    }, (e.outrogroups ?? (e.outrogroups = /* @__PURE__ */ new Set())).add(s);
}
function Jr(e, t, r = !0) {
  var n;
  if (e.pending.size > 0) {
    n = /* @__PURE__ */ new Set();
    for (const o of e.pending.values())
      for (const l of o)
        n.add(
          /** @type {EachItem} */
          e.items.get(l).e
        );
  }
  for (var i = 0; i < t.length; i++) {
    var s = t[i];
    if (n != null && n.has(s)) {
      s.f |= je;
      const o = document.createDocumentFragment();
      vn(s, o);
    } else
      V(t[i], r);
  }
}
var Cn;
function An(e, t, r, n, i, s = null) {
  var o = e, l = /* @__PURE__ */ new Map();
  {
    var f = (
      /** @type {Element} */
      e
    );
    o = A ? ee(/* @__PURE__ */ Yt(f)) : f.appendChild(xe());
  }
  A && Ar();
  var u = null, v = /* @__PURE__ */ Bs(() => {
    var b = r();
    return Kn(b) ? b : b == null ? [] : Er(b);
  }), h, d = /* @__PURE__ */ new Map(), p = !0;
  function c(b) {
    (w.effect.f & fe) === 0 && (w.pending.delete(b), w.fallback = u, vo(w, h, o, t, n), u !== null && (h.length === 0 ? (u.f & je) === 0 ? yr(u) : (u.f ^= je, qt(u, null, o)) : ft(u, () => {
      u = null;
    })));
  }
  function g(b) {
    w.pending.delete(b);
  }
  var _ = hn(() => {
    h = /** @type {V[]} */
    R(v);
    var b = h.length;
    let S = !1;
    if (A) {
      var z = Xn(o) === rn;
      z !== (b === 0) && (o = _r(), ee(o), Pe(!1), S = !0);
    }
    for (var D = /* @__PURE__ */ new Set(), K = (
      /** @type {Batch} */
      y
    ), O = $i(), te = 0; te < b; te += 1) {
      A && M.nodeType === Qt && /** @type {Comment} */
      M.data === nn && (o = /** @type {Comment} */
      M, S = !0, Pe(!1));
      var W = h[te], Ce = n(W, te), Ae = p ? null : l.get(Ce);
      Ae ? (Ae.v && Ot(Ae.v, W), Ae.i && Ot(Ae.i, te), O && K.unskip_effect(Ae.e)) : (Ae = go(
        l,
        p ? o : Cn ?? (Cn = xe()),
        W,
        Ce,
        te,
        i,
        t,
        r
      ), p || (Ae.e.f |= je), l.set(Ce, Ae)), D.add(Ce);
    }
    if (b === 0 && s && !u && (p ? u = oe(() => s(o)) : (u = oe(() => s(Cn ?? (Cn = xe()))), u.f |= je)), b > D.size && ws(), A && b > 0 && ee(_r()), !p)
      if (d.set(K, D), O) {
        for (const [Ji, Xi] of l)
          D.has(Ji) || K.skip_effect(Xi.e);
        K.oncommit(c), K.ondiscard(g);
      } else
        c(K);
    S && Pe(!0), R(v);
  }), w = { effect: _, items: l, pending: d, outrogroups: null, fallback: u };
  p = !1, A && (o = M);
}
function It(e) {
  for (; e !== null && (e.f & ke) === 0; )
    e = e.next;
  return e;
}
function vo(e, t, r, n, i) {
  var te;
  var s = t.length, o = e.items, l = It(e.effect.first), f, u = null, v = [], h = [], d, p, c, g;
  for (g = 0; g < s; g += 1) {
    if (d = t[g], p = i(d, g), c = /** @type {EachItem} */
    o.get(p).e, e.outrogroups !== null)
      for (const W of e.outrogroups)
        W.pending.delete(c), W.done.delete(c);
    if ((c.f & B) !== 0 && yr(c), (c.f & je) !== 0)
      if (c.f ^= je, c === l)
        qt(c, null, r);
      else {
        var _ = u ? u.next : l;
        c === e.effect.last && (e.effect.last = c.prev), c.prev && (c.prev.next = c.next), c.next && (c.next.prev = c.prev), qe(e, u, c), qe(e, c, _), qt(c, _, r), u = c, v = [], h = [], l = It(u.next);
        continue;
      }
    if (c !== l) {
      if (f !== void 0 && f.has(c)) {
        if (v.length < h.length) {
          var w = h[0], b;
          u = w.prev;
          var S = v[0], z = v[v.length - 1];
          for (b = 0; b < v.length; b += 1)
            qt(v[b], w, r);
          for (b = 0; b < h.length; b += 1)
            f.delete(h[b]);
          qe(e, S.prev, z.next), qe(e, u, S), qe(e, z, w), l = w, u = z, g -= 1, v = [], h = [];
        } else
          f.delete(c), qt(c, l, r), qe(e, c.prev, c.next), qe(e, c, u === null ? e.effect.first : u.next), qe(e, u, c), u = c;
        continue;
      }
      for (v = [], h = []; l !== null && l !== c; )
        (f ?? (f = /* @__PURE__ */ new Set())).add(l), h.push(l), l = It(l.next);
      if (l === null)
        continue;
    }
    (c.f & je) === 0 && v.push(c), u = c, l = It(c.next);
  }
  if (e.outrogroups !== null) {
    for (const W of e.outrogroups)
      W.pending.size === 0 && (Jr(e, Er(W.done)), (te = e.outrogroups) == null || te.delete(W));
    e.outrogroups.size === 0 && (e.outrogroups = null);
  }
  if (l !== null || f !== void 0) {
    var D = [];
    if (f !== void 0)
      for (c of f)
        (c.f & B) === 0 && D.push(c);
    for (; l !== null; )
      (l.f & B) === 0 && l !== e.fallback && D.push(l), l = It(l.next);
    var K = D.length;
    if (K > 0) {
      var O = s === 0 ? r : null;
      po(e, D, O);
    }
  }
}
function go(e, t, r, n, i, s, o, l) {
  var f = (o & ts) !== 0 ? (o & ns) === 0 ? /* @__PURE__ */ mi(r, !1, !1) : ht(r) : null, u = (o & rs) !== 0 ? ht(i) : null;
  return {
    v: f,
    i: u,
    e: oe(() => (s(t, f ?? r, u ?? i, l), () => {
      e.delete(n);
    }))
  };
}
function qt(e, t, r) {
  if (e.nodes)
    for (var n = e.nodes.start, i = e.nodes.end, s = t && (t.f & je) === 0 ? (
      /** @type {EffectNodes} */
      t.nodes.start
    ) : r; n !== null; ) {
      var o = (
        /** @type {TemplateNode} */
        /* @__PURE__ */ Fe(n)
      );
      if (s.before(n), n === i)
        return;
      n = o;
    }
}
function qe(e, t, r) {
  t === null ? e.effect.first = r : t.next = r, r === null ? e.effect.last = t : r.prev = t;
}
const _o = () => performance.now(), Ie = {
  // don't access requestAnimationFrame eagerly outside method
  // this allows basic testing of user code without JSDOM
  // bunder will eval and remove ternary when the user's app is built
  tick: (
    /** @param {any} _ */
    (e) => requestAnimationFrame(e)
  ),
  now: () => _o(),
  tasks: /* @__PURE__ */ new Set()
};
function Bi() {
  const e = Ie.now();
  Ie.tasks.forEach((t) => {
    t.c(e) || (Ie.tasks.delete(t), t.f());
  }), Ie.tasks.size !== 0 && Ie.tick(Bi);
}
function mo(e) {
  let t;
  return Ie.tasks.size === 0 && Ie.tick(Bi), {
    promise: new Promise((r) => {
      Ie.tasks.add(t = { c: e, f: r });
    }),
    abort() {
      Ie.tasks.delete(t);
    }
  };
}
function sr(e, t) {
  Lr(() => {
    e.dispatchEvent(new CustomEvent(t));
  });
}
function wo(e) {
  if (e === "float") return "cssFloat";
  if (e === "offset") return "cssOffset";
  if (e.startsWith("--")) return e;
  const t = e.split("-");
  return t.length === 1 ? t[0] : t[0] + t.slice(1).map(
    /** @param {any} word */
    (r) => r[0].toUpperCase() + r.slice(1)
  ).join("");
}
function Mn(e) {
  const t = {}, r = e.split(";");
  for (const n of r) {
    const [i, s] = n.split(":");
    if (!i || s === void 0) break;
    const o = wo(i.trim());
    t[o] = s.trim();
  }
  return t;
}
const yo = (e) => e;
function bo(e, t, r, n) {
  var _;
  var i = (e & is) !== 0, s = "both", o, l = t.inert, f = t.style.overflow, u, v;
  function h() {
    return Lr(() => o ?? (o = r()(t, (n == null ? void 0 : n()) ?? /** @type {P} */
    {}, {
      direction: s
    })));
  }
  var d = {
    is_global: i,
    in() {
      t.inert = l, u = Xr(
        t,
        h(),
        v,
        1,
        () => {
          sr(t, "introstart");
        },
        () => {
          sr(t, "introend"), u == null || u.abort(), u = o = void 0, t.style.overflow = f;
        }
      );
    },
    out(w) {
      t.inert = !0, v = Xr(
        t,
        h(),
        u,
        0,
        () => {
          sr(t, "outrostart");
        },
        () => {
          sr(t, "outroend"), w == null || w();
        }
      );
    },
    stop: () => {
      u == null || u.abort(), v == null || v.abort();
    }
  }, p = (
    /** @type {Effect & { nodes: EffectNodes }} */
    k
  );
  if (((_ = p.nodes).t ?? (_.t = [])).push(d), Wr) {
    var c = i;
    if (!c) {
      for (var g = (
        /** @type {Effect | null} */
        p.parent
      ); g && (g.f & ct) !== 0; )
        for (; (g = g.parent) && (g.f & le) === 0; )
          ;
      c = !g || (g.f & We) !== 0;
    }
    c && Xs(() => {
      Di(() => d.in());
    });
  }
}
function Xr(e, t, r, n, i, s) {
  var o = n === 1;
  if (us(t)) {
    var l, f = !1;
    return lt(() => {
      if (!f) {
        var w = t({ direction: o ? "in" : "out" });
        l = Xr(e, w, r, n, i, s);
      }
    }), {
      abort: () => {
        f = !0, l == null || l.abort();
      },
      deactivate: () => l.deactivate(),
      reset: () => l.reset(),
      t: () => l.t()
    };
  }
  if (r == null || r.deactivate(), !(t != null && t.duration) && !(t != null && t.delay))
    return i(), s(), {
      abort: gt,
      deactivate: gt,
      reset: gt,
      t: () => n
    };
  const { delay: u = 0, css: v, tick: h, easing: d = yo } = t;
  var p = [];
  if (o && r === void 0 && (h && h(0, 1), v)) {
    var c = Mn(v(0, 1));
    p.push(c, c);
  }
  var g = () => 1 - n, _ = e.animate(p, { duration: u, fill: "forwards" });
  return _.onfinish = () => {
    _.cancel(), i();
    var w = (r == null ? void 0 : r.t()) ?? 1 - n;
    r == null || r.abort();
    var b = n - w, S = (
      /** @type {number} */
      t.duration * Math.abs(b)
    ), z = [];
    if (S > 0) {
      var D = !1;
      if (v)
        for (var K = Math.ceil(S / 16.666666666666668), O = 0; O <= K; O += 1) {
          var te = w + b * d(O / K), W = Mn(v(te, 1 - te));
          z.push(W), D || (D = W.overflow === "hidden");
        }
      D && (e.style.overflow = "hidden"), g = () => {
        var Ce = (
          /** @type {number} */
          /** @type {globalThis.Animation} */
          _.currentTime
        );
        return w + b * d(Ce / S);
      }, h && mo(() => {
        if (_.playState !== "running") return !1;
        var Ce = g();
        return h(Ce, 1 - Ce), !0;
      });
    }
    _ = e.animate(z, { duration: S, fill: "forwards" }), _.onfinish = () => {
      g = () => n, h == null || h(n, 1 - n), s();
    };
  }, {
    abort: () => {
      _ && (_.cancel(), _.effect = null, _.onfinish = gt);
    },
    deactivate: () => {
      s = gt;
    },
    reset: () => {
      n === 0 && (h == null || h(1, 0));
    },
    t: () => g()
  };
}
function Vi(e) {
  var t, r, n = "";
  if (typeof e == "string" || typeof e == "number") n += e;
  else if (typeof e == "object") if (Array.isArray(e)) {
    var i = e.length;
    for (t = 0; t < i; t++) e[t] && (r = Vi(e[t])) && (n && (n += " "), n += r);
  } else for (r in e) e[r] && (n && (n += " "), n += r);
  return n;
}
function xo() {
  for (var e, t, r = 0, n = "", i = arguments.length; r < i; r++) (e = arguments[r]) && (t = Vi(e)) && (n && (n += " "), n += t);
  return n;
}
function ko(e) {
  return typeof e == "object" ? xo(e) : e ?? "";
}
const Ln = [...` 	
\r\f \v\uFEFF`];
function $o(e, t, r) {
  var n = e == null ? "" : "" + e;
  if (t && (n = n ? n + " " + t : t), r) {
    for (var i of Object.keys(r))
      if (r[i])
        n = n ? n + " " + i : i;
      else if (n.length)
        for (var s = i.length, o = 0; (o = n.indexOf(i, o)) >= 0; ) {
          var l = o + s;
          (o === 0 || Ln.includes(n[o - 1])) && (l === n.length || Ln.includes(n[l])) ? n = (o === 0 ? "" : n.substring(0, o)) + n.substring(l + 1) : o = l;
        }
  }
  return n === "" ? null : n;
}
function On(e, t, r, n, i, s) {
  var o = (
    /** @type {any} */
    e[Pr]
  );
  if (A || o !== r || o === void 0) {
    var l = $o(r, n, s);
    (!A || l !== e.getAttribute("class")) && (l == null ? e.removeAttribute("class") : e.className = l), e[Pr] = r;
  } else if (s && i !== s)
    for (var f in s) {
      var u = !!s[f];
      (i == null || u !== !!i[f]) && e.classList.toggle(f, u);
    }
  return s;
}
const Eo = Symbol("is custom element"), To = Symbol("is html"), So = _s ? "link" : "LINK";
function me(e, t, r, n) {
  var i = Co(e);
  A && (i[t] = e.getAttribute(t), t === "src" || t === "srcset" || t === "href" && e.nodeName === So) || i[t] !== (i[t] = r) && (t === "loading" && (e[vs] = r), r == null ? e.removeAttribute(t) : typeof r != "string" && Ao(e).includes(t) ? e[t] = r : e.setAttribute(t, r));
}
function Co(e) {
  return (
    /** @type {Record<string | symbol, unknown>} **/
    /** @type {any} */
    e[lr] ?? (e[lr] = {
      [Eo]: e.nodeName.includes("-"),
      [To]: e.namespaceURI === Un
    })
  );
}
var Nn = /* @__PURE__ */ new Map();
function Ao(e) {
  var t = e.getAttribute("is") || e.nodeName, r = Nn.get(t);
  if (r) return r;
  Nn.set(t, r = []);
  for (var n, i = e, s = Element.prototype; s !== i; ) {
    n = ls(i);
    for (var o in n)
      n[o].set && // better safe than sorry, we don't want spread attributes to mess with HTML content
      o !== "innerHTML" && o !== "textContent" && o !== "innerText" && r.push(o);
    i = Wn(i);
  }
  return r;
}
function Rn(e, t, r, n) {
  var i = (
    /** @type {V} */
    n
  ), s = !0, o = () => (s && (s = !1, i = /** @type {V} */
  n), i);
  e[t];
  var l;
  l = () => {
    var h = (
      /** @type {V} */
      e[t]
    );
    return h === void 0 ? o() : (s = !0, h);
  };
  var f = !1, u = /* @__PURE__ */ Mr(() => (f = !1, l())), v = (
    /** @type {Effect} */
    k
  );
  return (
    /** @type {() => V} */
    (function(h, d) {
      if (arguments.length > 0) {
        const p = d ? R(u) : h;
        return ve(u, p), f = !0, i !== void 0 && (i = p), h;
      }
      return Ke && f || (v.f & fe) !== 0 ? u.v : R(u);
    })
  );
}
function Mo(e) {
  return new Lo(e);
}
var ze, se;
class Lo {
  /**
   * @param {ComponentConstructorOptions & {
   *  component: any;
   * }} options
   */
  constructor(t) {
    /** @type {any} */
    x(this, ze);
    /** @type {Record<string, any>} */
    x(this, se);
    var s;
    var r = /* @__PURE__ */ new Map(), n = (o, l) => {
      var f = /* @__PURE__ */ mi(l, !1, !1);
      return r.set(o, f), f;
    };
    const i = new Proxy(
      { ...t.props || {}, $$events: {} },
      {
        get(o, l) {
          return R(r.get(l) ?? n(l, Reflect.get(o, l)));
        },
        has(o, l) {
          return l === ps ? !0 : (R(r.get(l) ?? n(l, Reflect.get(o, l))), Reflect.has(o, l));
        },
        set(o, l, f) {
          return ve(r.get(l) ?? n(l, f), f), Reflect.set(o, l, f);
        }
      }
    );
    m(this, se, (t.hydrate ? uo : qi)(t.component, {
      target: t.target,
      anchor: t.anchor,
      props: i,
      context: t.context,
      intro: t.intro ?? !1,
      recover: t.recover,
      transformError: t.transformError
    })), (!((s = t == null ? void 0 : t.props) != null && s.$$host) || t.sync === !1) && Hr(), m(this, ze, i.$$events);
    for (const o of Object.keys(a(this, se)))
      o === "$set" || o === "$destroy" || o === "$on" || pr(this, o, {
        get() {
          return a(this, se)[o];
        },
        /** @param {any} value */
        set(l) {
          a(this, se)[o] = l;
        },
        enumerable: !0
      });
    a(this, se).$set = /** @param {Record<string, any>} next */
    (o) => {
      Object.assign(i, o);
    }, a(this, se).$destroy = () => {
      co(a(this, se));
    };
  }
  /** @param {Record<string, any>} props */
  $set(t) {
    a(this, se).$set(t);
  }
  /**
   * @param {string} event
   * @param {(...args: any[]) => any} callback
   * @returns {any}
   */
  $on(t, r) {
    a(this, ze)[t] = a(this, ze)[t] || [];
    const n = (...i) => r.call(this, ...i);
    return a(this, ze)[t].push(n), () => {
      a(this, ze)[t] = a(this, ze)[t].filter(
        /** @param {any} fn */
        (i) => i !== n
      );
    };
  }
  $destroy() {
    a(this, se).$destroy();
  }
}
ze = new WeakMap(), se = new WeakMap();
let Yi;
typeof HTMLElement == "function" && (Yi = class extends HTMLElement {
  /**
   * @param {*} $$componentCtor
   * @param {*} $$slots
   * @param {ShadowRootInit | undefined} shadow_root_init
   */
  constructor(t, r, n) {
    super();
    /** The Svelte component constructor */
    T(this, "$$ctor");
    /** Slots */
    T(this, "$$s");
    /** @type {any} The Svelte component instance */
    T(this, "$$c");
    /** Whether or not the custom element is connected */
    T(this, "$$cn", !1);
    /** @type {Record<string, any>} Component props data */
    T(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    T(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    T(this, "$$p_d", {});
    /** @type {Record<string, EventListenerOrEventListenerObject[]>} Event listeners */
    T(this, "$$l", {});
    /** @type {Map<EventListenerOrEventListenerObject, Function>} Event listener unsubscribe functions */
    T(this, "$$l_u", /* @__PURE__ */ new Map());
    /** @type {any} The managed render effect for reflecting attributes */
    T(this, "$$me");
    /** @type {ShadowRoot | null} The ShadowRoot of the custom element */
    T(this, "$$shadowRoot", null);
    this.$$ctor = t, this.$$s = r, n && (this.$$shadowRoot = this.attachShadow(n));
  }
  /**
   * @param {string} type
   * @param {EventListenerOrEventListenerObject} listener
   * @param {boolean | AddEventListenerOptions} [options]
   */
  addEventListener(t, r, n) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(r), this.$$c) {
      const i = this.$$c.$on(t, r);
      this.$$l_u.set(r, i);
    }
    super.addEventListener(t, r, n);
  }
  /**
   * @param {string} type
   * @param {EventListenerOrEventListenerObject} listener
   * @param {boolean | AddEventListenerOptions} [options]
   */
  removeEventListener(t, r, n) {
    if (super.removeEventListener(t, r, n), this.$$c) {
      const i = this.$$l_u.get(r);
      i && (i(), this.$$l_u.delete(r));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let r = function(s) {
        return (o) => {
          const l = Ei("slot");
          s !== "default" && (l.name = s), Ze(o, l);
        };
      };
      var t = r;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const n = {}, i = Oo(this);
      for (const s of this.$$s)
        s in i && (s === "default" && !this.$$d.children ? (this.$$d.children = r(s), n.default = !0) : n[s] = r(s));
      for (const s of this.attributes) {
        const o = this.$$g_p(s.name);
        o in this.$$d || (this.$$d[o] = dr(o, s.value, this.$$p_d, "toProp"));
      }
      for (const s in this.$$p_d)
        !(s in this.$$d) && this[s] !== void 0 && (this.$$d[s] = this[s], delete this[s]);
      this.$$c = Mo({
        component: this.$$ctor,
        target: this.$$shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: n,
          $$host: this
        }
      }), this.$$me = Zs(() => {
        Si(() => {
          var s;
          this.$$r = !0;
          for (const o of hr(this.$$c)) {
            if (!((s = this.$$p_d[o]) != null && s.reflect)) continue;
            this.$$d[o] = this.$$c[o];
            const l = dr(
              o,
              this.$$d[o],
              this.$$p_d,
              "toAttribute"
            );
            l == null ? this.removeAttribute(this.$$p_d[o].attribute || o) : this.setAttribute(this.$$p_d[o].attribute || o, l);
          }
          this.$$r = !1;
        });
      });
      for (const s in this.$$l)
        for (const o of this.$$l[s]) {
          const l = this.$$c.$on(s, o);
          this.$$l_u.set(o, l);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  /**
   * @param {string} attr
   * @param {string} _oldValue
   * @param {string} newValue
   */
  attributeChangedCallback(t, r, n) {
    var i;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = dr(t, n, this.$$p_d, "toProp"), (i = this.$$c) == null || i.$set({ [t]: this.$$d[t] }));
  }
  disconnectedCallback() {
    this.$$cn = !1, Promise.resolve().then(() => {
      !this.$$cn && this.$$c && (this.$$c.$destroy(), this.$$me(), this.$$c = void 0);
    });
  }
  /**
   * @param {string} attribute_name
   */
  $$g_p(t) {
    return hr(this.$$p_d).find(
      (r) => this.$$p_d[r].attribute === t || !this.$$p_d[r].attribute && r.toLowerCase() === t
    ) || t;
  }
});
function dr(e, t, r, n) {
  var s;
  const i = (s = r[e]) == null ? void 0 : s.type;
  if (t = i === "Boolean" && typeof t != "boolean" ? t != null : t, !n || !r[e])
    return t;
  if (n === "toAttribute")
    switch (i) {
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
    switch (i) {
      case "Object":
      case "Array":
        return t && JSON.parse(t);
      case "Boolean":
        return t;
      // conversion already handled above
      case "Number":
        return t != null ? +t : t;
      default:
        return t;
    }
}
function Oo(e) {
  const t = {};
  return e.childNodes.forEach((r) => {
    t[
      /** @type {Element} node */
      r.slot || "default"
    ] = !0;
  }), t;
}
function Ui(e, t, r, n, i, s) {
  let o = class extends Yi {
    constructor() {
      super(e, r, i), this.$$p_d = t;
    }
    static get observedAttributes() {
      return hr(t).map(
        (l) => (t[l].attribute || l).toLowerCase()
      );
    }
  };
  return hr(t).forEach((l) => {
    pr(o.prototype, l, {
      get() {
        return this.$$c && l in this.$$c ? this.$$c[l] : this.$$d[l];
      },
      set(f) {
        var h;
        f = dr(l, f, t), this.$$d[l] = f;
        var u = this.$$c;
        if (u) {
          var v = (h = yt(u, l)) == null ? void 0 : h.get;
          v ? u[l] = f : u.$set({ [l]: f });
        }
      }
    });
  }), n.forEach((l) => {
    pr(o.prototype, l, {
      get() {
        var f;
        return (f = this.$$c) == null ? void 0 : f[l];
      }
    });
  }), s && (o = s(o)), e.element = /** @type {any} */
  o, o;
}
const No = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.static{position:static}.absolute{position:absolute}.left-0{left:0}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.\\!hidden{display:none!important}.hidden{display:none}.h-6{height:1.5rem}.w-6{width:1.5rem}.max-w-\\[335px\\]{max-width:335px}.transform{transform:translate(var(--tw-translate-x),var(--tw-translate-y)) rotate(var(--tw-rotate)) skew(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y))}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity, 1))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity, 1))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity, 1))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity, 1))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity, 1))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity, 1))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity, 1))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity, 1))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity, 1))}.underline{text-decoration-line:underline}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}@media(min-width:320px){.min-\\[320px\\]\\:right-4{right:1rem}}', Qr = new CSSStyleSheet();
Qr.replaceSync(No);
const Ro = (e) => class extends e {
  connectedCallback() {
    var t, r;
    (t = super.connectedCallback) == null || t.call(this), (r = this.shadowRoot) != null && r.adoptedStyleSheets && Qr && (this.shadowRoot.adoptedStyleSheets = [Qr]);
  }
};
function zo(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function zn(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
function Io(e, { delay: t = 0, duration: r = 400, easing: n = zo, x: i = 0, y: s = 0, opacity: o = 0 } = {}) {
  const l = getComputedStyle(e), f = +l.opacity, u = l.transform === "none" ? "" : l.transform, v = f * (1 - o), [h, d] = zn(i), [p, c] = zn(s);
  return {
    delay: t,
    duration: r,
    easing: n,
    css: (g, _) => `
			transform: ${u} translate(${(1 - g) * h}${d}, ${(1 - g) * p}${c});
			opacity: ${f - v * _}`
  };
}
var jo = /* @__PURE__ */ Rt('<a class="underline hover:no-underline"> </a>'), Po = /* @__PURE__ */ Rt('<p class="text-sm font-medium"> <!></p>'), Do = /* @__PURE__ */ Rt('<a class="underline hover:no-underline"> </a>'), Fo = /* @__PURE__ */ Rt('<p class="break-word text-sm"><span> </span> <!></p>'), qo = /* @__PURE__ */ Rt('<section><div><!> <!></div> <button class="w-6 h-6 flex justify-center items-center" type="button"><svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z" fill="currentColor"></path></svg></button></section>');
function en(e, t) {
  on(t, !0);
  let r = Rn(t, "toast"), n = Rn(t, "closeToast");
  var i = {
    get toast() {
      return r();
    },
    set toast(p) {
      r(p), Hr();
    },
    get closeToast() {
      return n();
    },
    set closeToast(p) {
      n(p), Hr();
    }
  }, s = qo(), o = He(s);
  let l;
  var f = He(o);
  {
    var u = (p) => {
      var c = Po(), g = He(c), _ = Ft(g);
      {
        var w = (b) => {
          var S = jo(), z = He(S, !0);
          we(S), zt(() => {
            me(S, "href", r().readMoreUrl), me(S, "target", r().readMoreIsExternal ? "_blank" : void 0), me(S, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), rr(z, r().readMoreText);
          }), Ze(b, S);
        };
        ir(_, (b) => {
          !r().body && r().readMoreText && b(w);
        });
      }
      we(c), zt(() => {
        me(c, "id", `toast-title-${r().id}`), rr(g, `${r().title ?? ""} `);
      }), Ze(p, c);
    };
    ir(f, (p) => {
      r().title && p(u);
    });
  }
  var v = Ft(f, 2);
  {
    var h = (p) => {
      var c = Fo(), g = He(c), _ = He(g, !0);
      we(g);
      var w = Ft(g, 2);
      {
        var b = (S) => {
          var z = Do(), D = He(z, !0);
          we(z), zt(() => {
            me(z, "href", r().readMoreUrl), me(z, "target", r().readMoreIsExternal ? "_blank" : void 0), me(z, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), rr(D, r().readMoreText);
          }), Ze(S, z);
        };
        ir(w, (S) => {
          r().readMoreText && S(b);
        });
      }
      we(c), zt(() => {
        me(g, "id", `toast-body-${r().id}`), rr(_, r().body);
      }), Ze(p, c);
    };
    ir(v, (p) => {
      r().body && p(h);
    });
  }
  we(o);
  var d = Ft(o, 2);
  return we(s), zt(() => {
    On(s, 1, ko(r().class)), me(s, "aria-labelledby", `toast-title-${r().id} toast-body-${r().id}`), l = On(o, 1, "flex gap-1", null, l, { "flex-col": r().body, "mt-2": r().title }), me(d, "aria-label", r().closeLabel);
  }), io("click", d, () => n()(r())), bo(3, s, () => Io, () => ({ x: 100 })), Ze(e, s), ln(i);
}
so(["click"]);
Ui(en, { toast: {}, closeToast: {} }, [], [], { mode: "open" });
var Ho = /* @__PURE__ */ Rt('<div class="absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2"><div class="flex flex-col gap-2" role="alert"></div> <div class="flex flex-col gap-2" role="status"></div></div>');
function Bo(e, t) {
  on(t, !0);
  const r = t.$$host;
  let n = /* @__PURE__ */ Me(wt([]));
  const i = /* @__PURE__ */ xn(() => R(n).filter((p) => p.type && ["error", "warning"].includes(p.type))), s = /* @__PURE__ */ xn(() => R(n).filter((p) => !p.type || !["error", "warning"].includes(p.type)));
  let o = 0;
  const l = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0", f = (p) => {
    ve(n, R(n).filter((c) => c.id !== p.id), !0), r.dispatchEvent(new CustomEvent("eki-toast-closed", { bubbles: !0, composed: !0, detail: p }));
  }, u = (p) => {
    p.isVisible = !0, p.id ?? (p.id = o++);
    const c = [
      p.type === "error" && "bg-eki-light-red border-eki-red py-3",
      p.type === "success" && "bg-eki-light-green border-eki-green py-3",
      p.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    p.class = `${l} ${c}`, ve(n, [...R(n), p], !0), r.dispatchEvent(new CustomEvent("eki-toast-opened", { bubbles: !0, composed: !0, detail: p }));
  };
  r.addToast = u;
  var v = Ho(), h = He(v);
  An(h, 21, () => R(i), (p) => p.id, (p, c) => {
    en(p, {
      get toast() {
        return R(c);
      },
      closeToast: f
    });
  }), we(h);
  var d = Ft(h, 2);
  An(d, 21, () => R(s), (p) => p.id, (p, c) => {
    en(p, {
      get toast() {
        return R(c);
      },
      closeToast: f
    });
  }), we(d), we(v), Ze(e, v), ln();
}
customElements.define("eki-toast", Ui(Bo, {}, [], [], { mode: "open" }, Ro));
const Vo = ":host{--color-dark-blue: #173148;--color-gray-1000: #0e1013;--color-gray-400: #5d606e;--color-gray-200: #ccd9e0;--color-blue-300: #2c6fb6;--color-white: #fff;--color-hall-500: #f9f9f9;--color-olive-brown: #94690d;display:block;font-family:inherit}*,*:before,*:after{box-sizing:border-box}.etym-tree{position:relative;display:flex;flex-direction:column;padding:4px 0 8px;color:var(--color-dark-blue)}.etym-tree__header{display:flex;flex-direction:column;gap:4px;margin-bottom:8px}.etym-tree__header-row{display:flex;align-items:center;gap:8px}.etym-tree__type{font-size:14px;line-height:21px;color:var(--color-gray-1000)}.etym-tree__toggle{display:inline-flex;align-items:center;gap:4px;padding:0;border:0;background:none;color:var(--color-blue-300);font-size:12px;line-height:21px;cursor:pointer}.etym-tree__icon{flex:none}.etym-tree__hint{font-size:12px;line-height:21px;color:var(--color-gray-400)}.etym-tree__canvas{position:relative;width:100%}.etym-tree__links{position:absolute;top:0;left:0;overflow:visible;pointer-events:none}.etym-tree__line{fill:none;stroke:var(--color-gray-200);stroke-width:2}.etym-tree__junction circle{fill:var(--color-white);stroke:var(--color-gray-200);stroke-width:1}.etym-tree__junction--interactive{pointer-events:auto;cursor:pointer}.etym-tree__junction--interactive:focus{outline:none}.etym-tree__junction-icon{fill:var(--color-dark-blue)}.etym-tree__levels{display:flex;flex-direction:column;align-items:center;gap:40px}.etym-tree__level{display:flex;justify-content:center;align-items:flex-start;gap:20px}.etym-tree__level>*{max-width:100%;min-width:0}.etym-tree--comments-hidden .etym-node__comments{display:none}.etym-tree--comments-hidden .etym-node__head{border-bottom:1px solid var(--color-gray-200);border-radius:4px}.etym-tree__error{padding:8px;color:var(--color-gray-400)}.etym-node{display:flex;flex-direction:column;align-items:center}.etym-node--wide-comment .etym-node__comment:first-child{border-top-left-radius:4px;border-top-right-radius:4px}.etym-node__head{display:flex;align-items:stretch;overflow:hidden;border:1px solid var(--color-gray-200);border-bottom:0;border-radius:4px 4px 0 0;cursor:pointer}.etym-node--no-comment .etym-node__head{border-bottom:1px solid var(--color-gray-200);border-radius:4px}.etym-node__lang{display:flex;align-items:center;padding:2px 4px 3px 6px;background:var(--color-hall-500);border-right:1px solid var(--color-gray-200);font-size:14px;line-height:21px}.etym-node__value{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:2px 6px 3px 4px;background:var(--color-white);font-size:14px;line-height:21px;text-align:center;overflow-wrap:anywhere;word-break:break-word}.etym-node__variant{color:var(--color-gray-400)}.etym-node__comments{width:100%}.etym-node__comment{padding:2px 6px 3px;border:1px solid var(--color-gray-200);font-size:12px;line-height:21px;text-align:center;overflow-wrap:anywhere;word-break:break-word}.etym-node__comment:not(:last-child){border-bottom:0}.etym-node__comment:last-child{border-bottom-left-radius:4px;border-bottom-right-radius:4px}.etym-label{padding:2px 8px 3px;background:var(--color-hall-500);border:1px solid var(--color-gray-200);border-radius:4px;font-size:14px;line-height:21px;color:var(--color-dark-blue);text-align:center}.etym-group{display:flex;flex-direction:column;align-items:center}.etym-group__name{align-self:stretch;padding:2px 8px 3px;background:var(--color-hall-500);border:1px solid var(--color-gray-200);border-radius:4px 4px 0 0;font-size:14px;line-height:21px;text-align:center}.etym-group__nodes{display:flex;flex-direction:column;align-items:center;gap:6px;padding:6px 20px;border:1px solid var(--color-gray-200);border-top:0;border-radius:0 0 4px 4px}.etym-questionable-connector{display:flex;flex-direction:column;align-items:center}.etym-questionable-connector__line{width:0;height:14px;border-left:2px solid var(--color-olive-brown)}.etym-questionable{display:inline-flex;align-items:center;justify-content:center;width:24px;height:24px;border-radius:50%;background:var(--color-olive-brown);color:var(--color-white);font-size:16px;font-weight:700;line-height:1;cursor:pointer;-webkit-user-select:none;-moz-user-select:none;user-select:none}.etym-questionable:focus{outline:none}.etym-questionable__note{color:var(--color-olive-brown);font-style:italic}.etym-tip{position:absolute;z-index:20;display:none;max-width:320px;padding:10px 16px;border-radius:8px;background:var(--color-white);box-shadow:0 4px 16px #00000026;font-size:14px;line-height:21px;color:var(--color-dark-blue)}.etym-tip--visible{display:block}.etym-tip .etym-tooltip__row+.etym-tooltip__row{margin-top:16px}.etym-tip .etym-tooltip__label{font-size:10px;line-height:18px;text-transform:uppercase;color:var(--color-gray-400)}.etym-tip .etym-tooltip__value{display:flex;flex-direction:column;gap:12px;font-size:14px;line-height:21px;color:var(--color-dark-blue)}";
function Te(e) {
  const t = document.createElement("div");
  return t.textContent = e == null ? "" : String(e), t.innerHTML;
}
function br(e) {
  return Te(e).replace(/"/g, "&quot;");
}
function Yo(e, t) {
  const r = e && e.levels || [], n = [];
  return r.forEach((i, s) => {
    const o = i.groups || [], l = o.find((f) => f.groupType === "COMPOUND");
    if (l) {
      const f = Uo(r[s - 1]);
      n.push({
        level: i.level,
        connector: "chain",
        items: [{ kind: "label", text: f ? f + " keel" : "" }]
      }), n.push({
        level: i.level,
        connector: "merge",
        items: (l.groupMembers || []).map((u) => tn(u, l, t))
      });
    } else
      n.push({ level: i.level, connector: "chain", items: Ko(o, t) });
  }), { typeLabel: Go(r), levels: n };
}
function Uo(e) {
  const t = (e && e.groups || []).find((r) => r.groupMembers && r.groupMembers.length);
  return t && t.groupMembers[0].langValue || "";
}
function Go(e) {
  const t = e[0];
  return !t || !t.groups || !t.groups.length ? "" : (t.groups.find((n) => n.groupType === "ROOT") || t.groups[0]).etymologyTypeCode || "";
}
function Ko(e, t) {
  const r = [];
  return e.forEach((n) => {
    n.groupType === "LANGUAGE_GROUP" ? r.push({
      kind: "group",
      name: n.languageGroupName,
      nodes: (n.groupMembers || []).map((i) => tn(i, void 0, t)),
      questionableTooltip: Gi(n, t)
    }) : (n.groupMembers || []).forEach((i) => r.push(tn(i, n, t)));
  }), r;
}
function Gi(e, t) {
  if (!e || !e.questionable)
    return null;
  const r = t["lex.wordetym.questionable.tooltip"], n = '<span class="etym-questionable__note">' + Te(r) + "</span>";
  if (e.groupType === "LANGUAGE_GROUP") {
    const i = t["lex.wordetym.questionable.languagegroup"], s = e.languageGroupName || "";
    return {
      html: i.replace("{0}", () => Te(s)).replace("{1}", () => n),
      text: i.replace("{0}", () => s).replace("{1}", () => r)
    };
  }
  return { html: n, text: r };
}
function tn(e, t, r) {
  const n = {
    kind: "node",
    id: e.id,
    wordId: e.wordId,
    language: e.lang,
    langValue: e.langValue,
    year: e.etymologyYear,
    value: e.valuePrese,
    variants: (e.variantWords || []).map((i) => i.valuePrese).filter(Boolean),
    comments: (e.comments || []).map((i) => i.valuePrese).filter(Boolean),
    sources: (e.sourceLinks || []).map((i) => i.name || i.sourceName).filter(Boolean),
    notes: (e.notes || []).map((i) => i.valuePrese).filter(Boolean),
    questionableTooltip: Gi(t, r),
    detailTip: ""
  };
  return n.detailTip = rl(n, r), n;
}
function Wo(e, t) {
  return e.levels.length ? '<div class="etym-tree">' + Xo(e, t) + '<div class="etym-tree__canvas"><svg class="etym-tree__links" aria-hidden="true"></svg><div class="etym-tree__levels">' + e.levels.map(Qo).join("") + '</div></div><div class="etym-tip" role="tooltip"></div></div>' : "";
}
const Zo = '<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17a5 5 0 110-10 5 5 0 010 10zm0-8a3 3 0 100 6 3 3 0 000-6z"/></svg>', Jo = '<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 7a5 5 0 015 5c0 .65-.13 1.26-.36 1.83l2.92 2.92A11.8 11.8 0 0023 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16A5 5 0 0112 7zM2 4.27l2.28 2.28.46.46A11.8 11.8 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zm5.53 5.53l1.55 1.55a3 3 0 003.65 3.65l1.55 1.55A5 5 0 017.53 9.8zM12 9a3 3 0 013 3l-3-3z"/></svg>';
function Ki(e, t) {
  const r = e ? Jo : Zo, n = e ? t["lex.wordetym.comments.hide"] : t["lex.wordetym.comments.show"];
  return r + '<span class="etym-tree__toggle-text">' + n + "</span>";
}
function Xo(e, t) {
  return '<div class="etym-tree__header"><div class="etym-tree__header-row">' + (e.typeLabel ? '<span class="etym-tree__type">' + Te(e.typeLabel) + "</span>" : "") + '<button type="button" class="etym-tree__toggle" data-shown="true">' + Ki(!0, t) + '</button></div><div class="etym-tree__hint">' + (t["lex.wordetym.detail.hint"] || "") + "</div></div>";
}
function Qo(e) {
  return '<div class="etym-tree__level"' + (e.connector === "merge" ? ' data-connector="merge"' : "") + ">" + e.items.map(el).join("") + "</div>";
}
function el(e) {
  return e.kind === "group" ? tl(e) : e.kind === "label" ? '<div class="etym-label">' + Te(e.text) + "</div>" : Wi(e);
}
function tl(e) {
  return '<div class="etym-group"><div class="etym-group__name">' + Te(e.name) + '</div><div class="etym-group__nodes">' + e.nodes.map(Wi).join("") + "</div>" + Zi(e.questionableTooltip) + "</div>";
}
function Wi(e) {
  const t = e.comments.length > 0, r = e.variants.map((s) => '<span class="etym-node__variant">' + s + "</span>").join(""), n = '<div class="etym-node__head" tabindex="0" role="button" data-tip="' + br(e.detailTip) + '"><span class="etym-node__lang">' + Te(e.language) + '</span><span class="etym-node__value"><span class="etym-node__word">' + (e.value || "") + "</span>" + r + "</span></div>", i = t ? '<div class="etym-node__comments">' + e.comments.map((s) => '<div class="etym-node__comment">' + s + "</div>").join("") + "</div>" : "";
  return '<div class="etym-node' + (t ? "" : " etym-node--no-comment") + '">' + n + i + Zi(e.questionableTooltip) + "</div>";
}
function rl(e, t) {
  const r = [or(t["lex.wordetym.lang"], [Te(e.langValue)])];
  return e.year && r.push(or(t["lex.wordetym.year"], [Te(e.year)])), e.sources.length && r.push(or(t["lex.source.link"], e.sources)), e.notes.length && r.push(or(t["lex.wordetym.notes"], e.notes)), r.join("");
}
function or(e, t) {
  const r = t.map((n) => '<div class="etym-tooltip__item">' + n + "</div>").join("");
  return '<div class="etym-tooltip__row"><div class="etym-tooltip__label">' + (e || "") + '</div><div class="etym-tooltip__value">' + r + "</div></div>";
}
function Zi(e) {
  return e ? '<div class="etym-questionable-connector"><span class="etym-questionable-connector__line" aria-hidden="true"></span><span class="etym-questionable" tabindex="0" role="button" aria-label="' + br(e.text) + '" data-tip="' + br(e.html) + '">?</span></div>' : "";
}
const nl = "M4.425 10.5C4.425 9.2175 5.4675 8.175 6.75 8.175H9.75V6.75H6.75C4.68 6.75 3 8.43 3 10.5 3 12.57 4.68 14.25 6.75 14.25H9.75V12.825H6.75C5.4675 12.825 4.425 11.7825 4.425 10.5ZM7.5 11.25H13.5V9.75H7.5V11.25ZM14.25 6.75H11.25V8.175H14.25C15.5325 8.175 16.575 9.2175 16.575 10.5 16.575 11.7825 15.5325 12.825 14.25 12.825H11.25V14.25H14.25C16.32 14.25 18 12.57 18 10.5 18 8.43 16.32 6.75 14.25 6.75Z", il = "M17 20.41 18.41 19 15 15.59 13.59 17 17 20.41ZM7.5 8H11v5.59L5.59 19 7 20.41l6-6V8h3.5L12 3.5 7.5 8Z";
function In(e) {
  return e.reduce((t, r) => t + r, 0) / e.length;
}
function jn(e, t, r, n) {
  const i = (t + n) / 2;
  return '<path class="etym-tree__line" d="M ' + e + " " + t + " C " + e + " " + i + ", " + r + " " + i + ", " + r + " " + n + '" />';
}
function sl(e, t, r, n) {
  const i = r === "merge", s = i ? il : nl, o = i ? "translate(-8,-8) scale(0.667)" : "translate(-9.45,-9.45) scale(0.9)";
  return '<g class="etym-tree__junction' + (n ? ' etym-tree__junction--interactive" tabindex="0" role="button" data-tip="' + br(n) + '"' : '"') + ' transform="translate(' + e + "," + t + ')"><circle r="12" /><path class="etym-tree__junction-icon" transform="' + o + '" d="' + s + '" /></g>';
}
function Pn(e) {
  for (let t = 0; t < e.length; t++) {
    const r = e[t];
    if (r.kind === "node")
      return r;
    if (r.kind === "group" && r.nodes && r.nodes.length)
      return r.nodes[0];
  }
  return null;
}
function Dn(e) {
  return "<strong>" + (e.value || "") + " (" + Te(e.language) + ")</strong>";
}
function ol(e, t, r) {
  return (r["lex.wordetym.origin.sentence"] || "").replace("{0}", () => Dn(e)).replace("{1}", () => Dn(t));
}
function Fn(e) {
  try {
    return JSON.parse(e);
  } catch {
    return null;
  }
}
class ll extends HTMLElement {
  constructor() {
    super();
    T(this, "root");
    T(this, "_data", null);
    T(this, "_messages", {});
    T(this, "tree", null);
    T(this, "resizeObserver", null);
    T(this, "drawFrame", 0);
    T(this, "listenersBound", !1);
    T(this, "onRootClick", (r) => {
      const n = this.tipEl(), i = r.composedPath();
      if (n && i.indexOf(n) !== -1)
        return;
      const s = i.find(
        (o) => o instanceof Element && o.hasAttribute("data-tip")
      );
      s ? this.toggleTip(s) : this.hideTip();
    });
    T(this, "onDocClick", (r) => {
      r.composedPath().indexOf(this) === -1 && this.hideTip();
    });
    T(this, "onKeyDown", (r) => {
      r.key === "Escape" && this.hideTip();
    });
    this.root = this.attachShadow({ mode: "open" }), this.root.addEventListener("click", this.onRootClick);
  }
  static get observedAttributes() {
    return ["data", "messages"];
  }
  set data(r) {
    this._data = typeof r == "string" ? Fn(r) : r, this.update();
  }
  get data() {
    return this._data;
  }
  set messages(r) {
    this._messages = typeof r == "string" ? Fn(r) || {} : r || {}, this.update();
  }
  get messages() {
    return this._messages;
  }
  attributeChangedCallback(r, n, i) {
    i != null && (r === "data" && (this.data = i), r === "messages" && (this.messages = i));
  }
  connectedCallback() {
    this.upgradeProperty("data"), this.upgradeProperty("messages"), this.listenersBound || (document.addEventListener("click", this.onDocClick, !0), document.addEventListener("keydown", this.onKeyDown), this.listenersBound = !0), this.update();
  }
  disconnectedCallback() {
    var r;
    (r = this.resizeObserver) == null || r.disconnect(), document.removeEventListener("click", this.onDocClick, !0), document.removeEventListener("keydown", this.onKeyDown), this.listenersBound = !1;
  }
  upgradeProperty(r) {
    if (Object.prototype.hasOwnProperty.call(this, r)) {
      const n = this[r];
      delete this[r], this[r] = n;
    }
  }
  update() {
    if (!this.isConnected || !this._data)
      return;
    this.tree = Yo(this._data, this._messages);
    const r = Wo(this.tree, this._messages);
    this.root.innerHTML = "<style>" + Vo + "</style>" + (r || ""), this.bindToggle(), this.scheduleDraw(), this.observeResize();
  }
  bindToggle() {
    const r = this.root.querySelector(".etym-tree__toggle"), n = this.root.querySelector(".etym-tree");
    !r || !n || r.addEventListener("click", () => {
      const s = !n.classList.toggle("etym-tree--comments-hidden");
      r.setAttribute("data-shown", String(s)), r.innerHTML = Ki(s, this._messages), this.scheduleDraw();
    });
  }
  observeResize() {
    var n;
    if (typeof ResizeObserver > "u") return;
    const r = this.root.querySelector(".etym-tree__canvas");
    r && ((n = this.resizeObserver) == null || n.disconnect(), this.resizeObserver = new ResizeObserver(() => this.scheduleDraw()), this.resizeObserver.observe(r));
  }
  scheduleDraw() {
    this.drawFrame && cancelAnimationFrame(this.drawFrame), this.drawFrame = requestAnimationFrame(() => {
      this.drawFrame = requestAnimationFrame(() => {
        this.markWideComments(), this.drawConnectors();
      });
    });
  }
  markWideComments() {
    this.root.querySelectorAll(".etym-node").forEach((r) => {
      const n = r.querySelector(".etym-node__head"), i = r.querySelector(".etym-node__comment"), s = !!n && !!i && i.offsetWidth > n.offsetWidth + 1;
      r.classList.toggle("etym-node--wide-comment", s);
    });
  }
  drawConnectors() {
    const r = this.root.querySelector(".etym-tree__canvas"), n = this.root.querySelector(".etym-tree__links"), i = this.root.querySelector(".etym-tree__levels");
    if (!r || !n || !i) return;
    const s = Array.from(i.children), o = r.clientWidth, l = r.clientHeight;
    if (n.setAttribute("width", String(o)), n.setAttribute("height", String(l)), n.setAttribute("viewBox", "0 0 " + o + " " + l), s.length < 2) {
      n.innerHTML = "";
      return;
    }
    const f = r.getBoundingClientRect(), u = (d) => Array.from(d.children).map((p) => {
      const c = p.getBoundingClientRect();
      return {
        x: c.left + c.width / 2 - f.left,
        top: c.top - f.top,
        bottom: c.bottom - f.top
      };
    }), v = this.tree && this.tree.levels || [], h = [];
    for (let d = 0; d < s.length - 1; d++) {
      const p = u(s[d]), c = u(s[d + 1]);
      if (!p.length || !c.length) continue;
      const g = (In(p.map((O) => O.x)) + In(c.map((O) => O.x))) / 2, _ = Math.max.apply(null, p.map((O) => O.bottom)), w = Math.min.apply(null, c.map((O) => O.top)), b = (_ + w) / 2, S = s[d + 1].getAttribute("data-connector") === "merge" ? "merge" : "chain";
      p.forEach((O) => h.push(jn(O.x, O.bottom, g, b))), c.forEach((O) => h.push(jn(g, b, O.x, O.top)));
      const z = v[d] ? Pn(v[d].items) : null, D = v[d + 1] ? Pn(v[d + 1].items) : null, K = z && D ? ol(z, D, this._messages) : "";
      h.push(sl(g, b, S, K));
    }
    n.innerHTML = h.join("");
  }
  tipEl() {
    return this.root.querySelector(".etym-tip");
  }
  toggleTip(r) {
    const n = this.tipEl();
    if (!n) return;
    n.classList.contains("etym-tip--visible") && n.dataset.for === qn(r) ? this.hideTip() : this.showTip(r);
  }
  showTip(r) {
    const n = this.tipEl(), i = this.root.querySelector(".etym-tree");
    if (!n || !i) return;
    const s = r.getAttribute("data-tip") || "";
    n.innerHTML = s, n.dataset.for = qn(r), n.classList.add("etym-tip--visible");
    const o = i.getBoundingClientRect(), l = r.getBoundingClientRect(), f = n.getBoundingClientRect();
    let u = l.left - o.left + l.width / 2 - f.width / 2;
    u = Math.max(0, Math.min(u, o.width - f.width));
    let v = l.top - o.top - f.height - 8;
    v < 0 && (v = l.bottom - o.top + 8), n.style.left = u + "px", n.style.top = v + "px";
  }
  hideTip() {
    const r = this.tipEl();
    r && (r.classList.remove("etym-tip--visible"), delete r.dataset.for);
  }
}
let al = 0;
function qn(e) {
  const t = e;
  return t.dataset.tipKey || (t.dataset.tipKey = String(++al)), t.dataset.tipKey;
}
customElements.get("eki-etym-tree") || customElements.define("eki-etym-tree", ll);

})();
