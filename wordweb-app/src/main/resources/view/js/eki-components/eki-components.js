var Pi = Object.defineProperty;
var dn = (e) => {
  throw TypeError(e);
};
var ji = (e, t, r) => t in e ? Pi(e, t, { enumerable: !0, configurable: !0, writable: !0, value: r }) : e[t] = r;
var M = (e, t, r) => ji(e, typeof t != "symbol" ? t + "" : t, r), Cr = (e, t, r) => t.has(e) || dn("Cannot " + r);
var a = (e, t, r) => (Cr(e, t, "read from private field"), r ? r.call(e) : t.get(e)), m = (e, t, r) => t.has(e) ? dn("Cannot add the same private member more than once") : t instanceof WeakSet ? t.add(e) : t.set(e, r), w = (e, t, r, n) => (Cr(e, t, "write to private field"), n ? n.call(e, r) : t.set(e, r), r), x = (e, t, r) => (Cr(e, t, "access private method"), r);
var Rn;
typeof window < "u" && ((Rn = window.__svelte ?? (window.__svelte = {})).v ?? (Rn.v = /* @__PURE__ */ new Set())).add("5");
const Hi = 1, Bi = 2, Ui = 16, Vi = 4, Yi = 2, In = "[", Zr = "[!", hn = "[?", Qr = "]", At = {}, L = Symbol("uninitialized"), zn = "http://www.w3.org/1999/xhtml", Dn = !1;
var Fn = Array.isArray, qi = Array.prototype.indexOf, st = Array.prototype.includes, $r = Array.from, cr = Object.keys, dr = Object.defineProperty, bt = Object.getOwnPropertyDescriptor, Gi = Object.getOwnPropertyDescriptors, Ki = Object.prototype, Wi = Array.prototype, Pn = Object.getPrototypeOf, vn = Object.isExtensible;
function Ji(e) {
  return typeof e == "function";
}
const pt = () => {
};
function Xi(e) {
  for (var t = 0; t < e.length; t++)
    e[t]();
}
function jn() {
  var e, t, r = new Promise((n, i) => {
    e = n, t = i;
  });
  return { promise: r, resolve: e, reject: t };
}
const P = 2, Ct = 4, kr = 8, Hn = 1 << 24, se = 16, $e = 32, De = 64, Lr = 128, oe = 512, I = 1024, z = 2048, ke = 4096, H = 8192, le = 16384, Ke = 32768, pn = 1 << 25, ut = 65536, hr = 1 << 17, Zi = 1 << 18, ht = 1 << 19, Qi = 1 << 20, Ie = 1 << 25, ct = 65536, vr = 1 << 21, mt = 1 << 22, Ye = 1 << 23, Nr = Symbol("$state"), es = Symbol("legacy props"), ts = Symbol(""), sr = Symbol("attributes"), Ir = Symbol("class"), rs = Symbol("style"), zt = Symbol("text"), xr = new class extends Error {
  constructor() {
    super(...arguments);
    M(this, "name", "StaleReactionError");
    M(this, "message", "The reaction that called `getAbortSignal()` was re-run or destroyed");
  }
}();
var On;
const ns = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  !!((On = globalThis.document) != null && On.contentType) && /* @__PURE__ */ globalThis.document.contentType.includes("xml")
), en = 3, Zt = 8;
function is() {
  throw new Error("https://svelte.dev/e/async_derived_orphan");
}
function ss(e, t, r) {
  throw new Error("https://svelte.dev/e/each_key_duplicate");
}
function os() {
  throw new Error("https://svelte.dev/e/effect_update_depth_exceeded");
}
function ls() {
  throw new Error("https://svelte.dev/e/hydration_failed");
}
function as() {
  throw new Error("https://svelte.dev/e/state_descriptors_fixed");
}
function fs() {
  throw new Error("https://svelte.dev/e/state_prototype_fixed");
}
function us() {
  throw new Error("https://svelte.dev/e/state_unsafe_mutation");
}
function cs() {
  throw new Error("https://svelte.dev/e/svelte_boundary_reset_onerror");
}
function ds() {
  console.warn("https://svelte.dev/e/derived_inert");
}
function Er(e) {
  console.warn("https://svelte.dev/e/hydration_mismatch");
}
function hs() {
  console.warn("https://svelte.dev/e/svelte_boundary_reset_noop");
}
let A = !1;
function ze(e) {
  A = e;
}
let C;
function Z(e) {
  if (e === null)
    throw Er(), At;
  return C = e;
}
function Sr() {
  return Z(/* @__PURE__ */ Fe(C));
}
function we(e) {
  if (A) {
    if (/* @__PURE__ */ Fe(C) !== null)
      throw Er(), At;
    C = e;
  }
}
function vs(e = 1) {
  if (A) {
    for (var t = e, r = C; t--; )
      r = /** @type {TemplateNode} */
      /* @__PURE__ */ Fe(r);
    C = r;
  }
}
function pr(e = !0) {
  for (var t = 0, r = C; ; ) {
    if (r.nodeType === Zt) {
      var n = (
        /** @type {Comment} */
        r.data
      );
      if (n === Qr) {
        if (t === 0) return r;
        t -= 1;
      } else (n === In || n === Zr || // "[1", "[2", etc. for if blocks
      n[0] === "[" && !isNaN(Number(n.slice(1)))) && (t += 1);
    }
    var i = (
      /** @type {TemplateNode} */
      /* @__PURE__ */ Fe(r)
    );
    e && r.remove(), r = i;
  }
}
function Bn(e) {
  if (!e || e.nodeType !== Zt)
    throw Er(), At;
  return (
    /** @type {Comment} */
    e.data
  );
}
function Un(e) {
  return e === this.v;
}
function ps(e, t) {
  return e != e ? t == t : e !== t || e !== null && typeof e == "object" || typeof e == "function";
}
function Vn(e) {
  return !ps(e, this.v);
}
let _s = !1, _e = null;
function Nt(e) {
  _e = e;
}
function tn(e, t = !1, r) {
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
function rn(e) {
  var t = (
    /** @type {ComponentContext} */
    _e
  ), r = t.e;
  if (r !== null) {
    t.e = null;
    for (var n of r)
      Is(n);
  }
  return e !== void 0 && (t.x = e), t.i = !0, _e = t.p, e ?? /** @type {T} */
  {};
}
function Yn() {
  return !0;
}
let Je = [];
function qn() {
  var e = Je;
  Je = [], Xi(e);
}
function ot(e) {
  if (Je.length === 0 && !Bt) {
    var t = Je;
    queueMicrotask(() => {
      t === Je && qn();
    });
  }
  Je.push(e);
}
function gs() {
  for (; Je.length > 0; )
    qn();
}
function Gn(e) {
  var t = k;
  if (t === null)
    return E.f |= Ye, e;
  if ((t.f & Ke) === 0 && (t.f & Ct) === 0)
    throw e;
  Ve(e, t);
}
function Ve(e, t) {
  for (; t !== null; ) {
    if ((t.f & Lr) !== 0) {
      if ((t.f & Ke) === 0)
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
const ws = -7169;
function R(e, t) {
  e.f = e.f & ws | t;
}
function nn(e) {
  (e.f & oe) !== 0 || e.deps === null ? R(e, I) : R(e, ke);
}
function Kn(e) {
  if (e !== null)
    for (const t of e)
      (t.f & P) === 0 || (t.f & ct) === 0 || (t.f ^= ct, Kn(
        /** @type {Derived} */
        t.deps
      ));
}
function Wn(e, t, r) {
  (e.f & z) !== 0 ? t.add(e) : (e.f & ke) !== 0 && r.add(e), Kn(e.deps), R(e, I);
}
let Mr = null, vt = null, b = null, Ht = null, F = null, zr = null, Bt = !1, Rr = !1, gt = null, or = null;
var _n = 0;
let bs = 1;
var yt, Be, Qe, $t, kt, et, xt, Ce, Et, V, qt, Ne, de, be, St, tt, T, Dr, Dt, Fr, Jn, Xn, _t, ms, Ft;
const br = class br {
  constructor() {
    m(this, T);
    M(this, "id", bs++);
    /** True as soon as `#process` was called */
    m(this, yt, !1);
    M(this, "linked", !0);
    /** @type {Batch | null} */
    m(this, Be, null);
    /** @type {Batch | null} */
    m(this, Qe, null);
    /** @type {Map<Effect, ReturnType<typeof deferred<any>>>} */
    M(this, "async_deriveds", /* @__PURE__ */ new Map());
    /**
     * The current values of any signals that are updated in this batch.
     * Tuple format: [value, is_derived] (note: is_derived is false for deriveds, too, if they were overridden via assignment)
     * They keys of this map are identical to `this.#previous`
     * @type {Map<Value, [any, boolean]>}
     */
    M(this, "current", /* @__PURE__ */ new Map());
    /**
     * The values of any signals (sources and deriveds) that are updated in this batch _before_ those updates took place.
     * They keys of this map are identical to `this.#current`
     * @type {Map<Value, any>}
     */
    M(this, "previous", /* @__PURE__ */ new Map());
    /**
     * When the batch is committed (and the DOM is updated), we need to remove old branches
     * and append new ones by calling the functions added inside (if/each/key/etc) blocks
     * @type {Set<(batch: Batch) => void>}
     */
    m(this, $t, /* @__PURE__ */ new Set());
    /**
     * If a fork is discarded, we need to destroy any effects that are no longer needed
     * @type {Set<(batch: Batch) => void>}
     */
    m(this, kt, /* @__PURE__ */ new Set());
    /**
     * Callbacks that should run only when a fork is committed.
     * @type {Set<(batch: Batch) => void>}
     */
    m(this, et, /* @__PURE__ */ new Set());
    /**
     * The number of async effects that are currently in flight
     */
    m(this, xt, 0);
    /**
     * Async effects that are currently in flight, _not_ inside a pending boundary
     * @type {Map<Effect, number>}
     */
    m(this, Ce, /* @__PURE__ */ new Map());
    /**
     * A deferred that resolves when the batch is committed, used with `settled()`
     * TODO replace with Promise.withResolvers once supported widely enough
     * @type {{ promise: Promise<void>, resolve: (value?: any) => void, reject: (reason: unknown) => void } | null}
     */
    m(this, Et, null);
    /**
     * The root effects that need to be flushed
     * @type {Effect[]}
     */
    m(this, V, []);
    /**
     * Effects created while this batch was active.
     * @type {Effect[]}
     */
    m(this, qt, []);
    /**
     * Deferred effects (which run after async work has completed) that are DIRTY
     * @type {Set<Effect>}
     */
    m(this, Ne, /* @__PURE__ */ new Set());
    /**
     * Deferred effects that are MAYBE_DIRTY
     * @type {Set<Effect>}
     */
    m(this, de, /* @__PURE__ */ new Set());
    /**
     * A map of branches that still exist, but will be destroyed when this batch
     * is committed — we skip over these during `process`.
     * The value contains child effects that were dirty/maybe_dirty before being reset,
     * so they can be rescheduled if the branch survives.
     * @type {Map<Effect, { d: Effect[], m: Effect[] }>}
     */
    m(this, be, /* @__PURE__ */ new Map());
    /**
     * Inverse of #skipped_branches which we need to tell prior batches to unskip them when committing
     * @type {Set<Effect>}
     */
    m(this, St, /* @__PURE__ */ new Set());
    M(this, "is_fork", !1);
    m(this, tt, !1);
    vt === null ? Mr = vt = this : (w(vt, Qe, this), w(this, Be, vt)), vt = this;
  }
  /**
   * Add an effect to the #skipped_branches map and reset its children
   * @param {Effect} effect
   */
  skip_effect(t) {
    a(this, be).has(t) || a(this, be).set(t, { d: [], m: [] }), a(this, St).delete(t);
  }
  /**
   * Remove an effect from the #skipped_branches map and reschedule
   * any tracked dirty/maybe_dirty child effects
   * @param {Effect} effect
   * @param {(e: Effect) => void} callback
   */
  unskip_effect(t, r = (n) => this.schedule(n)) {
    var n = a(this, be).get(t);
    if (n) {
      a(this, be).delete(t);
      for (var i of n.d)
        R(i, z), r(i);
      for (i of n.m)
        R(i, ke), r(i);
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
    t.v !== L && !this.previous.has(t) && this.previous.set(t, t.v), (t.f & Ye) === 0 && (this.current.set(t, [r, n]), F == null || F.set(t, r)), this.is_fork || (t.v = r);
  }
  activate() {
    b = this;
  }
  deactivate() {
    b = null, F = null;
  }
  flush() {
    try {
      Rr = !0, b = this, x(this, T, Dt).call(this);
    } finally {
      _n = 0, zr = null, gt = null, or = null, Rr = !1, b = null, F = null, lt.clear();
    }
  }
  discard() {
    var t;
    for (const r of a(this, kt)) r(this);
    a(this, kt).clear(), a(this, et).clear(), x(this, T, Ft).call(this), (t = a(this, Et)) == null || t.resolve();
  }
  /**
   * @param {Effect} effect
   */
  register_created_effect(t) {
    a(this, qt).push(t);
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  increment(t, r) {
    if (w(this, xt, a(this, xt) + 1), t) {
      let n = a(this, Ce).get(r) ?? 0;
      a(this, Ce).set(r, n + 1);
    }
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  decrement(t, r) {
    if (w(this, xt, a(this, xt) - 1), t) {
      let n = a(this, Ce).get(r) ?? 0;
      n === 1 ? a(this, Ce).delete(r) : a(this, Ce).set(r, n - 1);
    }
    a(this, tt) || (w(this, tt, !0), ot(() => {
      w(this, tt, !1), this.linked && this.flush();
    }));
  }
  /**
   * @param {Set<Effect>} dirty_effects
   * @param {Set<Effect>} maybe_dirty_effects
   */
  transfer_effects(t, r) {
    for (const n of t)
      a(this, Ne).add(n);
    for (const n of r)
      a(this, de).add(n);
    t.clear(), r.clear();
  }
  /** @param {(batch: Batch) => void} fn */
  oncommit(t) {
    a(this, $t).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  ondiscard(t) {
    a(this, kt).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  on_fork_commit(t) {
    a(this, et).add(t);
  }
  run_fork_commit_callbacks() {
    for (const t of a(this, et)) t(this);
    a(this, et).clear();
  }
  settled() {
    return (a(this, Et) ?? w(this, Et, jn())).promise;
  }
  static ensure() {
    if (b === null) {
      const t = b = new br();
      !Rr && !Bt && ot(() => {
        a(t, yt) || t.flush();
      });
    }
    return b;
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
    if (zr = t, (i = t.b) != null && i.is_pending && (t.f & (Ct | kr | Hn)) !== 0 && (t.f & Ke) === 0) {
      t.b.defer_effect(t);
      return;
    }
    for (var r = t; r.parent !== null; ) {
      r = r.parent;
      var n = r.f;
      if (gt !== null && r === k && (E === null || (E.f & P) === 0))
        return;
      if ((n & (De | $e)) !== 0) {
        if ((n & I) === 0)
          return;
        r.f ^= I;
      }
    }
    a(this, V).push(r);
  }
};
yt = new WeakMap(), Be = new WeakMap(), Qe = new WeakMap(), $t = new WeakMap(), kt = new WeakMap(), et = new WeakMap(), xt = new WeakMap(), Ce = new WeakMap(), Et = new WeakMap(), V = new WeakMap(), qt = new WeakMap(), Ne = new WeakMap(), de = new WeakMap(), be = new WeakMap(), St = new WeakMap(), tt = new WeakMap(), T = new WeakSet(), Dr = function() {
  if (this.is_fork) return !0;
  for (const n of a(this, Ce).keys()) {
    for (var t = n, r = !1; t.parent !== null; ) {
      if (a(this, be).has(t)) {
        r = !0;
        break;
      }
      t = t.parent;
    }
    if (!r)
      return !0;
  }
  return !1;
}, Dt = function() {
  var f, u, p, c;
  w(this, yt, !0), _n++ > 1e3 && (x(this, T, Ft).call(this), ys());
  for (const h of a(this, Ne))
    a(this, de).delete(h), R(h, z), this.schedule(h);
  for (const h of a(this, de))
    R(h, ke), this.schedule(h);
  const t = a(this, V);
  w(this, V, []), this.apply();
  var r = gt = [], n = [], i = or = [];
  for (const h of t)
    try {
      x(this, T, Fr).call(this, h, r, n);
    } catch (v) {
      throw ei(h), x(this, T, Dr).call(this) || this.discard(), v;
    }
  if (b = null, i.length > 0) {
    var s = br.ensure();
    for (const h of i)
      s.schedule(h);
  }
  if (gt = null, or = null, x(this, T, Dr).call(this)) {
    x(this, T, _t).call(this, n), x(this, T, _t).call(this, r);
    for (const [h, v] of a(this, be))
      Qn(h, v);
    i.length > 0 && /** @type {unknown} */
    x(f = b, T, Dt).call(f);
    return;
  }
  const o = x(this, T, Jn).call(this);
  if (o) {
    x(this, T, _t).call(this, n), x(this, T, _t).call(this, r), x(u = o, T, Xn).call(u, this);
    return;
  }
  a(this, Ne).clear(), a(this, de).clear();
  for (const h of a(this, $t)) h(this);
  a(this, $t).clear(), Ht = this, gn(n), gn(r), Ht = null, (p = a(this, Et)) == null || p.resolve();
  var l = (
    /** @type {Batch | null} */
    /** @type {unknown} */
    b
  );
  if (a(this, xt) === 0 && (a(this, V).length === 0 || l !== null) && x(this, T, Ft).call(this), a(this, V).length > 0)
    if (l !== null) {
      const h = l;
      a(h, V).push(...a(this, V).filter((v) => !a(h, V).includes(v)));
    } else
      l = this;
  l !== null && x(c = l, T, Dt).call(c);
}, /**
 * Traverse the effect tree, executing effects or stashing
 * them for later execution as appropriate
 * @param {Effect} root
 * @param {Effect[]} effects
 * @param {Effect[]} render_effects
 */
Fr = function(t, r, n) {
  t.f ^= I;
  for (var i = t.first; i !== null; ) {
    var s = i.f, o = (s & ($e | De)) !== 0, l = o && (s & I) !== 0, f = l || (s & H) !== 0 || a(this, be).has(i);
    if (!f && i.fn !== null) {
      o ? i.f ^= I : (s & Ct) !== 0 ? r.push(i) : Qt(i) && ((s & se) !== 0 && a(this, de).add(i), Rt(i));
      var u = i.first;
      if (u !== null) {
        i = u;
        continue;
      }
    }
    for (; i !== null; ) {
      var p = i.next;
      if (p !== null) {
        i = p;
        break;
      }
      i = i.parent;
    }
  }
}, Jn = function() {
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
Xn = function(t) {
  var n;
  for (const [i, s] of t.current)
    !this.previous.has(i) && t.previous.has(i) && this.previous.set(i, t.previous.get(i)), this.current.set(i, s);
  for (const [i, s] of t.async_deriveds) {
    const o = this.async_deriveds.get(i);
    o && s.promise.then(o.resolve).catch(o.reject);
  }
  this.transfer_effects(a(t, Ne), a(t, de));
  const r = (i) => {
    var s = i.reactions;
    if (s !== null)
      for (const f of s) {
        var o = f.f;
        if ((o & P) !== 0)
          r(
            /** @type {Derived} */
            f
          );
        else {
          var l = (
            /** @type {Effect} */
            f
          );
          o & (mt | se) && !this.async_deriveds.has(l) && (a(this, de).delete(l), R(l, z), this.schedule(l));
        }
      }
  };
  for (const i of this.current.keys())
    r(i);
  this.oncommit(() => t.discard()), x(n = t, T, Ft).call(n), b = this, x(this, T, Dt).call(this);
}, /**
 * @param {Effect[]} effects
 */
_t = function(t) {
  for (var r = 0; r < t.length; r += 1)
    Wn(t[r], a(this, Ne), a(this, de));
}, ms = function() {
  var p;
  for (let c = Mr; c !== null; c = a(c, Qe)) {
    var t = c.id < this.id, r = [];
    for (const [h, [v, d]] of this.current) {
      if (c.current.has(h)) {
        var n = (
          /** @type {[any, boolean]} */
          c.current.get(h)[0]
        );
        if (t && v !== n)
          c.current.set(h, [v, d]);
        else
          continue;
      }
      r.push(h);
    }
    if (t)
      for (const [h, v] of this.async_deriveds) {
        const d = c.async_deriveds.get(h);
        d && v.promise.then(d.resolve).catch(d.reject);
      }
    if (a(c, yt)) {
      var i = [...c.current.keys()].filter(
        (h) => !/** @type {[any, boolean]} */
        c.current.get(h)[1] && !this.current.has(h)
      );
      if (i.length === 0)
        t && c.discard();
      else if (r.length > 0) {
        if (t)
          for (const h of a(this, St))
            c.unskip_effect(h, (v) => {
              var d;
              (v.f & (se | mt)) !== 0 ? c.schedule(v) : x(d = c, T, _t).call(d, [v]);
            });
        c.activate();
        var s = /* @__PURE__ */ new Set(), o = /* @__PURE__ */ new Map();
        for (var l of r)
          Zn(l, i, s, o);
        o = /* @__PURE__ */ new Map();
        var f = [...c.current].filter(([h, v]) => {
          const d = this.current.get(h);
          return d ? d[0] !== v[0] || d[1] !== v[1] : !0;
        }).map(([h]) => h);
        if (f.length > 0)
          for (const h of a(this, qt))
            (h.f & (le | H | hr)) === 0 && sn(h, f, o) && ((h.f & (mt | se)) !== 0 ? (R(h, z), c.schedule(h)) : a(c, Ne).add(h));
        if (a(c, V).length > 0 && !a(c, tt)) {
          c.apply();
          for (var u of a(c, V))
            x(p = c, T, Fr).call(p, u, [], []);
          w(c, V, []);
        }
        c.deactivate();
      }
    }
  }
}, Ft = function() {
  if (this.linked) {
    var t = a(this, Be), r = a(this, Qe);
    t === null ? Mr = r : w(t, Qe, r), r === null ? vt = t : w(r, Be, t), this.linked = !1;
  }
};
let qe = br;
function Pr(e) {
  var t = Bt;
  Bt = !0;
  try {
    for (var r; ; ) {
      if (gs(), b === null)
        return (
          /** @type {T} */
          r
        );
      b.flush();
    }
  } finally {
    Bt = t;
  }
}
function ys() {
  try {
    os();
  } catch (e) {
    Ve(e, zr);
  }
}
let ce = null;
function gn(e) {
  var t = e.length;
  if (t !== 0) {
    for (var r = 0; r < t; ) {
      var n = e[r++];
      if ((n.f & (le | H)) === 0 && Qt(n) && (ce = /* @__PURE__ */ new Set(), Rt(n), n.deps === null && n.first === null && n.nodes === null && n.teardown === null && n.ac === null && bi(n), (ce == null ? void 0 : ce.size) > 0)) {
        lt.clear();
        for (const i of ce) {
          if ((i.f & (le | H)) !== 0) continue;
          const s = [i];
          let o = i.parent;
          for (; o !== null; )
            ce.has(o) && (ce.delete(o), s.push(o)), o = o.parent;
          for (let l = s.length - 1; l >= 0; l--) {
            const f = s[l];
            (f.f & (le | H)) === 0 && Rt(f);
          }
        }
        ce.clear();
      }
    }
    ce = null;
  }
}
function Zn(e, t, r, n) {
  if (!r.has(e) && (r.add(e), e.reactions !== null))
    for (const i of e.reactions) {
      const s = i.f;
      (s & P) !== 0 ? Zn(
        /** @type {Derived} */
        i,
        t,
        r,
        n
      ) : (s & (mt | se)) !== 0 && (s & z) === 0 && sn(i, t, n) && (R(i, z), on(
        /** @type {Effect} */
        i
      ));
    }
}
function sn(e, t, r) {
  const n = r.get(e);
  if (n !== void 0) return n;
  if (e.deps !== null)
    for (const i of e.deps) {
      if (st.call(t, i))
        return !0;
      if ((i.f & P) !== 0 && sn(
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
function on(e) {
  b.schedule(e);
}
function Qn(e, t) {
  if (!((e.f & $e) !== 0 && (e.f & I) !== 0)) {
    (e.f & z) !== 0 ? t.d.push(e) : (e.f & ke) !== 0 && t.m.push(e), R(e, I);
    for (var r = e.first; r !== null; )
      Qn(r, t), r = r.next;
  }
}
function ei(e) {
  R(e, I);
  for (var t = e.first; t !== null; )
    ei(t), t = t.next;
}
function $s(e) {
  let t = 0, r = dt(0), n;
  return () => {
    an() && (O(r), gi(() => (t === 0 && (n = Ci(() => e(() => Ut(r)))), t += 1, () => {
      ot(() => {
        t -= 1, t === 0 && (n == null || n(), n = void 0, Ut(r));
      });
    })));
  };
}
var ks = ut | ht;
function xs(e, t, r, n) {
  new Es(e, t, r, n);
}
var W, Gt, te, rt, Y, re, j, J, Me, nt, Ue, Tt, Kt, Wt, Re, mr, N, ti, ri, ni, jr, lr, ar, Hr, Br;
class Es {
  /**
   * @param {TemplateNode} node
   * @param {BoundaryProps} props
   * @param {((anchor: Node) => void)} children
   * @param {((error: unknown) => unknown) | undefined} [transform_error]
   */
  constructor(t, r, n, i) {
    m(this, N);
    /** @type {Boundary | null} */
    M(this, "parent");
    M(this, "is_pending", !1);
    /**
     * API-level transformError transform function. Transforms errors before they reach the `failed` snippet.
     * Inherited from parent boundary, or defaults to identity.
     * @type {(error: unknown) => unknown}
     */
    M(this, "transform_error");
    /** @type {TemplateNode} */
    m(this, W);
    /** @type {TemplateNode | null} */
    m(this, Gt, A ? C : null);
    /** @type {BoundaryProps} */
    m(this, te);
    /** @type {((anchor: Node) => void)} */
    m(this, rt);
    /** @type {Effect} */
    m(this, Y);
    /** @type {Effect | null} */
    m(this, re, null);
    /** @type {Effect | null} */
    m(this, j, null);
    /** @type {Effect | null} */
    m(this, J, null);
    /** @type {DocumentFragment | null} */
    m(this, Me, null);
    m(this, nt, 0);
    m(this, Ue, 0);
    m(this, Tt, !1);
    /** @type {Set<Effect>} */
    m(this, Kt, /* @__PURE__ */ new Set());
    /** @type {Set<Effect>} */
    m(this, Wt, /* @__PURE__ */ new Set());
    /**
     * A source containing the number of pending async deriveds/expressions.
     * Only created if `$effect.pending()` is used inside the boundary,
     * otherwise updating the source results in needless `Batch.ensure()`
     * calls followed by no-op flushes
     * @type {Source<number> | null}
     */
    m(this, Re, null);
    m(this, mr, $s(() => (w(this, Re, dt(a(this, nt))), () => {
      w(this, Re, null);
    })));
    var s;
    w(this, W, t), w(this, te, r), w(this, rt, (o) => {
      var l = (
        /** @type {Effect} */
        k
      );
      l.b = this, l.f |= Lr, n(o);
    }), this.parent = /** @type {Effect} */
    k.b, this.transform_error = i ?? ((s = this.parent) == null ? void 0 : s.transform_error) ?? ((o) => o), w(this, Y, fn(() => {
      if (A) {
        const o = (
          /** @type {Comment} */
          a(this, Gt)
        );
        Sr();
        const l = o.data === Zr;
        if (o.data.startsWith(hn)) {
          const u = JSON.parse(o.data.slice(hn.length));
          x(this, N, ri).call(this, u);
        } else l ? x(this, N, ni).call(this) : x(this, N, ti).call(this);
      } else
        x(this, N, jr).call(this);
    }, ks)), A && w(this, W, C);
  }
  /**
   * Defer an effect inside a pending boundary until the boundary resolves
   * @param {Effect} effect
   */
  defer_effect(t) {
    Wn(t, a(this, Kt), a(this, Wt));
  }
  /**
   * Returns `false` if the effect exists inside a boundary whose pending snippet is shown
   * @returns {boolean}
   */
  is_rendered() {
    return !this.is_pending && (!this.parent || this.parent.is_rendered());
  }
  has_pending_snippet() {
    return !!a(this, te).pending;
  }
  /**
   * Update the source that powers `$effect.pending()` inside this boundary,
   * and controls when the current `pending` snippet (if any) is removed.
   * Do not call from inside the class
   * @param {1 | -1} d
   * @param {Batch} batch
   */
  update_pending_count(t, r) {
    x(this, N, Hr).call(this, t, r), w(this, nt, a(this, nt) + t), !(!a(this, Re) || a(this, Tt)) && (w(this, Tt, !0), ot(() => {
      w(this, Tt, !1), a(this, Re) && Mt(a(this, Re), a(this, nt));
    }));
  }
  get_effect_pending() {
    return a(this, mr).call(this), O(
      /** @type {Source<number>} */
      a(this, Re)
    );
  }
  /** @param {unknown} error */
  error(t) {
    if (!a(this, te).onerror && !a(this, te).failed)
      throw t;
    b != null && b.is_fork ? (a(this, re) && b.skip_effect(a(this, re)), a(this, j) && b.skip_effect(a(this, j)), a(this, J) && b.skip_effect(a(this, J)), b.on_fork_commit(() => {
      x(this, N, Br).call(this, t);
    })) : x(this, N, Br).call(this, t);
  }
}
W = new WeakMap(), Gt = new WeakMap(), te = new WeakMap(), rt = new WeakMap(), Y = new WeakMap(), re = new WeakMap(), j = new WeakMap(), J = new WeakMap(), Me = new WeakMap(), nt = new WeakMap(), Ue = new WeakMap(), Tt = new WeakMap(), Kt = new WeakMap(), Wt = new WeakMap(), Re = new WeakMap(), mr = new WeakMap(), N = new WeakSet(), ti = function() {
  try {
    w(this, re, ie(() => a(this, rt).call(this, a(this, W))));
  } catch (t) {
    this.error(t);
  }
}, /**
 * @param {unknown} error The deserialized error from the server's hydration comment
 */
ri = function(t) {
  const r = a(this, te).failed;
  r && w(this, J, ie(() => {
    r(
      a(this, W),
      () => t,
      () => () => {
      }
    );
  }));
}, ni = function() {
  const t = a(this, te).pending;
  t && (this.is_pending = !0, w(this, j, ie(() => t(a(this, W)))), ot(() => {
    var r = w(this, Me, document.createDocumentFragment()), n = ye();
    r.append(n), w(this, re, x(this, N, ar).call(this, () => ie(() => a(this, rt).call(this, n)))), a(this, Ue) === 0 && (a(this, W).before(r), w(this, Me, null), at(
      /** @type {Effect} */
      a(this, j),
      () => {
        w(this, j, null);
      }
    ), x(this, N, lr).call(
      this,
      /** @type {Batch} */
      b
    ));
  }));
}, jr = function() {
  try {
    if (this.is_pending = this.has_pending_snippet(), w(this, Ue, 0), w(this, nt, 0), w(this, re, ie(() => {
      a(this, rt).call(this, a(this, W));
    })), a(this, Ue) > 0) {
      var t = w(this, Me, document.createDocumentFragment());
      cn(a(this, re), t);
      const r = (
        /** @type {(anchor: Node) => void} */
        a(this, te).pending
      );
      w(this, j, ie(() => r(a(this, W))));
    } else
      x(this, N, lr).call(
        this,
        /** @type {Batch} */
        b
      );
  } catch (r) {
    this.error(r);
  }
}, /**
 * @param {Batch} batch
 */
lr = function(t) {
  this.is_pending = !1, t.transfer_effects(a(this, Kt), a(this, Wt));
}, /**
 * @template T
 * @param {() => T} fn
 */
ar = function(t) {
  var r = k, n = E, i = _e;
  xe(a(this, Y)), fe(a(this, Y)), Nt(a(this, Y).ctx);
  try {
    return qe.ensure(), t();
  } catch (s) {
    return Gn(s), null;
  } finally {
    xe(r), fe(n), Nt(i);
  }
}, /**
 * Updates the pending count associated with the currently visible pending snippet,
 * if any, such that we can replace the snippet with content once work is done
 * @param {1 | -1} d
 * @param {Batch} batch
 */
Hr = function(t, r) {
  var n;
  if (!this.has_pending_snippet()) {
    this.parent && x(n = this.parent, N, Hr).call(n, t, r);
    return;
  }
  w(this, Ue, a(this, Ue) + t), a(this, Ue) === 0 && (x(this, N, lr).call(this, r), a(this, j) && at(a(this, j), () => {
    w(this, j, null);
  }), a(this, Me) && (a(this, W).before(a(this, Me)), w(this, Me, null)));
}, /**
 * @param {unknown} error
 */
Br = function(t) {
  a(this, re) && (B(a(this, re)), w(this, re, null)), a(this, j) && (B(a(this, j)), w(this, j, null)), a(this, J) && (B(a(this, J)), w(this, J, null)), A && (Z(
    /** @type {TemplateNode} */
    a(this, Gt)
  ), vs(), Z(pr()));
  var r = a(this, te).onerror;
  let n = a(this, te).failed;
  var i = !1, s = !1;
  const o = () => {
    if (i) {
      hs();
      return;
    }
    i = !0, s && cs(), a(this, J) !== null && at(a(this, J), () => {
      w(this, J, null);
    }), x(this, N, ar).call(this, () => {
      x(this, N, jr).call(this);
    });
  }, l = (f) => {
    try {
      s = !0, r == null || r(f, o), s = !1;
    } catch (u) {
      Ve(u, a(this, Y) && a(this, Y).parent);
    }
    n && w(this, J, x(this, N, ar).call(this, () => {
      try {
        return ie(() => {
          var u = (
            /** @type {Effect} */
            k
          );
          u.b = this, u.f |= Lr, n(
            a(this, W),
            () => f,
            () => o
          );
        });
      } catch (u) {
        return Ve(
          u,
          /** @type {Effect} */
          a(this, Y).parent
        ), null;
      }
    }));
  };
  ot(() => {
    var f;
    try {
      f = this.transform_error(t);
    } catch (u) {
      Ve(u, a(this, Y) && a(this, Y).parent);
      return;
    }
    f !== null && typeof f == "object" && typeof /** @type {any} */
    f.then == "function" ? f.then(
      l,
      /** @param {unknown} e */
      (u) => Ve(u, a(this, Y) && a(this, Y).parent)
    ) : l(f);
  });
};
function Ss(e, t, r, n) {
  const i = Tr;
  var s = e.filter((h) => !h.settled);
  if (r.length === 0 && s.length === 0) {
    n(t.map(i));
    return;
  }
  var o = (
    /** @type {Effect} */
    k
  ), l = Ts(), f = s.length === 1 ? s[0].promise : s.length > 1 ? Promise.all(s.map((h) => h.promise)) : null;
  function u(h) {
    if ((o.f & le) === 0) {
      l();
      try {
        n(h);
      } catch (v) {
        Ve(v, o);
      }
      _r();
    }
  }
  var p = ii();
  if (r.length === 0) {
    f.then(() => u(t.map(i))).finally(p);
    return;
  }
  function c() {
    Promise.all(r.map((h) => /* @__PURE__ */ As(h))).then((h) => u([...t.map(i), ...h])).catch((h) => Ve(h, o)).finally(p);
  }
  f ? f.then(() => {
    l(), c(), _r();
  }) : c();
}
function Ts() {
  var e = (
    /** @type {Effect} */
    k
  ), t = E, r = _e, n = (
    /** @type {Batch} */
    b
  );
  return function(s = !0) {
    xe(e), fe(t), Nt(r), s && (e.f & le) === 0 && (n == null || n.activate(), n == null || n.apply());
  };
}
function _r(e = !0) {
  xe(null), fe(null), Nt(null), e && (b == null || b.deactivate());
}
function ii() {
  var e = (
    /** @type {Effect} */
    k
  ), t = e.b, r = (
    /** @type {Batch} */
    b
  ), n = !!(t != null && t.is_rendered());
  return t == null || t.update_pending_count(1, r), r.increment(n, e), () => {
    t == null || t.update_pending_count(-1, r), r.decrement(n, e);
  };
}
// @__NO_SIDE_EFFECTS__
function Tr(e) {
  var t = P | z;
  return k !== null && (k.f |= ht), {
    ctx: _e,
    deps: null,
    effects: null,
    equals: Un,
    f: t,
    fn: e,
    reactions: null,
    rv: 0,
    v: (
      /** @type {V} */
      L
    ),
    wv: 0,
    parent: k,
    ac: null
  };
}
const er = Symbol("obsolete");
// @__NO_SIDE_EFFECTS__
function As(e, t, r) {
  let n = (
    /** @type {Effect | null} */
    k
  );
  n === null && is();
  var i = (
    /** @type {Promise<V>} */
    /** @type {unknown} */
    void 0
  ), s = dt(
    /** @type {V} */
    L
  ), o = !E, l = /* @__PURE__ */ new Set();
  return Ps(() => {
    var v, d;
    var f = (
      /** @type {Effect} */
      k
    ), u = jn();
    i = u.promise;
    try {
      Promise.resolve(e()).then(u.resolve, (_) => {
        _ !== xr && u.reject(_);
      }).finally(_r);
    } catch (_) {
      u.reject(_), _r();
    }
    var p = (
      /** @type {Batch} */
      b
    );
    if (o) {
      if ((f.f & Ke) !== 0)
        var c = ii();
      if (
        // boundary can be null if the async derived is inside an $effect.root not connected to the component render tree
        (v = n.b) != null && v.is_rendered()
      )
        (d = p.async_deriveds.get(f)) == null || d.reject(er);
      else
        for (const _ of l.values())
          _.reject(er);
      l.add(u), p.async_deriveds.set(f, u);
    }
    const h = (_, g = void 0) => {
      c == null || c(), l.delete(u), g !== er && (p.activate(), g ? (s.f |= Ye, Mt(s, g)) : ((s.f & Ye) !== 0 && (s.f ^= Ye), Mt(s, _)), p.deactivate());
    };
    u.promise.then(h, (_) => h(null, _ || "unknown"));
  }), Ls(() => {
    for (const f of l)
      f.reject(er);
  }), new Promise((f) => {
    function u(p) {
      function c() {
        p === i ? f(s) : u(i);
      }
      p.then(c, c);
    }
    u(i);
  });
}
// @__NO_SIDE_EFFECTS__
function wn(e) {
  const t = /* @__PURE__ */ Tr(e);
  return $i(t), t;
}
// @__NO_SIDE_EFFECTS__
function Cs(e) {
  const t = /* @__PURE__ */ Tr(e);
  return t.equals = Vn, t;
}
function Ns(e) {
  var t = e.effects;
  if (t !== null) {
    e.effects = null;
    for (var r = 0; r < t.length; r += 1)
      B(
        /** @type {Effect} */
        t[r]
      );
  }
}
function ln(e) {
  var t, r = k, n = e.parent;
  if (!Ge && n !== null && e.v !== L && // if it was never evaluated before, it's guaranteed to fail downstream, so we try to execute instead
  (n.f & (le | H)) !== 0)
    return ds(), e.v;
  xe(n);
  try {
    e.f &= ~ct, Ns(e), t = Si(e);
  } finally {
    xe(r);
  }
  return t;
}
function si(e) {
  var t = ln(e);
  if (!e.equals(t) && (e.wv = xi(), (!(b != null && b.is_fork) || e.deps === null) && (b !== null ? (b.capture(e, t, !0), Ht == null || Ht.capture(e, t, !0)) : e.v = t, e.deps === null))) {
    R(e, I);
    return;
  }
  Ge || (F !== null ? (an() || b != null && b.is_fork) && F.set(e, t) : nn(e));
}
function Ms(e) {
  var t, r;
  if (e.effects !== null)
    for (const n of e.effects)
      (n.teardown || n.ac) && ((t = n.teardown) == null || t.call(n), (r = n.ac) == null || r.abort(xr), n.fn !== null && (n.teardown = pt), n.ac = null, Yt(n, 0), un(n));
}
function oi(e) {
  if (e.effects !== null)
    for (const t of e.effects)
      t.teardown && t.fn !== null && Rt(t);
}
let gr = /* @__PURE__ */ new Set();
const lt = /* @__PURE__ */ new Map();
let li = !1;
function dt(e, t) {
  var r = {
    f: 0,
    // TODO ideally we could skip this altogether, but it causes type errors
    v: e,
    reactions: null,
    equals: Un,
    rv: 0,
    wv: 0
  };
  return r;
}
// @__NO_SIDE_EFFECTS__
function Ae(e, t) {
  const r = dt(e);
  return $i(r), r;
}
// @__NO_SIDE_EFFECTS__
function ai(e, t = !1, r = !0) {
  const n = dt(e);
  return t || (n.equals = Vn), n;
}
function ve(e, t, r = !1) {
  E !== null && // since we are untracking the function inside `$inspect.with` we need to add this check
  // to ensure we error if state is set inside an inspect effect
  (!pe || (E.f & hr) !== 0) && Yn() && (E.f & (P | se | mt | hr)) !== 0 && (ae === null || !st.call(ae, e)) && us();
  let n = r ? wt(t) : t;
  return Mt(e, n, or);
}
function Mt(e, t, r = null) {
  if (!e.equals(t)) {
    lt.set(e, Ge ? t : e.v);
    var n = qe.ensure();
    if (n.capture(e, t), (e.f & P) !== 0) {
      const i = (
        /** @type {Derived} */
        e
      );
      (e.f & z) !== 0 && ln(i), F === null && nn(i);
    }
    e.wv = xi(), fi(e, z, r), k !== null && (k.f & I) !== 0 && (k.f & ($e | De)) === 0 && (ee === null ? Bs([e]) : ee.push(e)), !n.is_fork && gr.size > 0 && !li && Rs();
  }
  return t;
}
function Rs() {
  li = !1;
  for (const e of gr) {
    (e.f & I) !== 0 && R(e, ke);
    let t;
    try {
      t = Qt(e);
    } catch {
      t = !0;
    }
    t && Rt(e);
  }
  gr.clear();
}
function Ut(e) {
  ve(e, e.v + 1);
}
function fi(e, t, r) {
  var n = e.reactions;
  if (n !== null)
    for (var i = n.length, s = 0; s < i; s++) {
      var o = n[s], l = o.f, f = (l & z) === 0;
      if (f && R(o, t), (l & hr) !== 0)
        gr.add(
          /** @type {Effect} */
          o
        );
      else if ((l & P) !== 0) {
        var u = (
          /** @type {Derived} */
          o
        );
        F == null || F.delete(u), (l & ct) === 0 && (l & oe && (k === null || (k.f & vr) === 0) && (o.f |= ct), fi(u, ke, r));
      } else if (f) {
        var p = (
          /** @type {Effect} */
          o
        );
        (l & se) !== 0 && ce !== null && ce.add(p), r !== null ? r.push(p) : on(p);
      }
    }
}
function wt(e) {
  if (typeof e != "object" || e === null || Nr in e)
    return e;
  const t = Pn(e);
  if (t !== Ki && t !== Wi)
    return e;
  var r = /* @__PURE__ */ new Map(), n = Fn(e), i = /* @__PURE__ */ Ae(0), s = ft, o = (l) => {
    if (ft === s)
      return l();
    var f = E, u = ft;
    fe(null), yn(s);
    var p = l();
    return fe(f), yn(u), p;
  };
  return n && r.set("length", /* @__PURE__ */ Ae(
    /** @type {any[]} */
    e.length
  )), new Proxy(
    /** @type {any} */
    e,
    {
      defineProperty(l, f, u) {
        (!("value" in u) || u.configurable === !1 || u.enumerable === !1 || u.writable === !1) && as();
        var p = r.get(f);
        return p === void 0 ? o(() => {
          var c = /* @__PURE__ */ Ae(u.value);
          return r.set(f, c), c;
        }) : ve(p, u.value, !0), !0;
      },
      deleteProperty(l, f) {
        var u = r.get(f);
        if (u === void 0) {
          if (f in l) {
            const p = o(() => /* @__PURE__ */ Ae(L));
            r.set(f, p), Ut(i);
          }
        } else
          ve(u, L), Ut(i);
        return !0;
      },
      get(l, f, u) {
        var v;
        if (f === Nr)
          return e;
        var p = r.get(f), c = f in l;
        if (p === void 0 && (!c || (v = bt(l, f)) != null && v.writable) && (p = o(() => {
          var d = wt(c ? l[f] : L), _ = /* @__PURE__ */ Ae(d);
          return _;
        }), r.set(f, p)), p !== void 0) {
          var h = O(p);
          return h === L ? void 0 : h;
        }
        return Reflect.get(l, f, u);
      },
      getOwnPropertyDescriptor(l, f) {
        var u = Reflect.getOwnPropertyDescriptor(l, f);
        if (u && "value" in u) {
          var p = r.get(f);
          p && (u.value = O(p));
        } else if (u === void 0) {
          var c = r.get(f), h = c == null ? void 0 : c.v;
          if (c !== void 0 && h !== L)
            return {
              enumerable: !0,
              configurable: !0,
              value: h,
              writable: !0
            };
        }
        return u;
      },
      has(l, f) {
        var h;
        if (f === Nr)
          return !0;
        var u = r.get(f), p = u !== void 0 && u.v !== L || Reflect.has(l, f);
        if (u !== void 0 || k !== null && (!p || (h = bt(l, f)) != null && h.writable)) {
          u === void 0 && (u = o(() => {
            var v = p ? wt(l[f]) : L, d = /* @__PURE__ */ Ae(v);
            return d;
          }), r.set(f, u));
          var c = O(u);
          if (c === L)
            return !1;
        }
        return p;
      },
      set(l, f, u, p) {
        var S;
        var c = r.get(f), h = f in l;
        if (n && f === "length")
          for (var v = u; v < /** @type {Source<number>} */
          c.v; v += 1) {
            var d = r.get(v + "");
            d !== void 0 ? ve(d, L) : v in l && (d = o(() => /* @__PURE__ */ Ae(L)), r.set(v + "", d));
          }
        if (c === void 0)
          (!h || (S = bt(l, f)) != null && S.writable) && (c = o(() => /* @__PURE__ */ Ae(void 0)), ve(c, wt(u)), r.set(f, c));
        else {
          h = c.v !== L;
          var _ = o(() => wt(u));
          ve(c, _);
        }
        var g = Reflect.getOwnPropertyDescriptor(l, f);
        if (g != null && g.set && g.set.call(p, u), !h) {
          if (n && typeof f == "string") {
            var y = (
              /** @type {Source<number>} */
              r.get("length")
            ), $ = Number(f);
            Number.isInteger($) && $ >= y.v && ve(y, $ + 1);
          }
          Ut(i);
        }
        return !0;
      },
      ownKeys(l) {
        O(i);
        var f = Reflect.ownKeys(l).filter((c) => {
          var h = r.get(c);
          return h === void 0 || h.v !== L;
        });
        for (var [u, p] of r)
          p.v !== L && !(u in l) && f.push(u);
        return f;
      },
      setPrototypeOf() {
        fs();
      }
    }
  );
}
var bn, ui, ci, di;
function Ur() {
  if (bn === void 0) {
    bn = window, ui = /Firefox/.test(navigator.userAgent);
    var e = Element.prototype, t = Node.prototype, r = Text.prototype;
    ci = bt(t, "firstChild").get, di = bt(t, "nextSibling").get, vn(e) && (e[Ir] = void 0, e[sr] = null, e[rs] = void 0, e.__e = void 0), vn(r) && (r[zt] = void 0);
  }
}
function ye(e = "") {
  return document.createTextNode(e);
}
// @__NO_SIDE_EFFECTS__
function Vt(e) {
  return (
    /** @type {TemplateNode | null} */
    ci.call(e)
  );
}
// @__NO_SIDE_EFFECTS__
function Fe(e) {
  return (
    /** @type {TemplateNode | null} */
    di.call(e)
  );
}
function He(e, t) {
  if (!A)
    return /* @__PURE__ */ Vt(e);
  var r = /* @__PURE__ */ Vt(C);
  if (r === null)
    r = C.appendChild(ye());
  else if (t && r.nodeType !== en) {
    var n = ye();
    return r == null || r.before(n), Z(n), n;
  }
  return t && _i(
    /** @type {Text} */
    r
  ), Z(r), r;
}
function Pt(e, t = 1, r = !1) {
  let n = A ? C : e;
  for (var i; t--; )
    i = n, n = /** @type {TemplateNode} */
    /* @__PURE__ */ Fe(n);
  if (!A)
    return n;
  if (r) {
    if ((n == null ? void 0 : n.nodeType) !== en) {
      var s = ye();
      return n === null ? i == null || i.after(s) : n.before(s), Z(s), s;
    }
    _i(
      /** @type {Text} */
      n
    );
  }
  return Z(n), n;
}
function hi(e) {
  e.textContent = "";
}
function vi() {
  return !1;
}
function pi(e, t, r) {
  return (
    /** @type {T extends keyof HTMLElementTagNameMap ? HTMLElementTagNameMap[T] : Element} */
    document.createElementNS(zn, e, void 0)
  );
}
function _i(e) {
  if (
    /** @type {string} */
    e.nodeValue.length < 65536
  )
    return;
  let t = e.nextSibling;
  for (; t !== null && t.nodeType === en; )
    t.remove(), e.nodeValue += /** @type {string} */
    t.nodeValue, t = e.nextSibling;
}
function Ar(e) {
  var t = E, r = k;
  fe(null), xe(null);
  try {
    return e();
  } finally {
    fe(t), xe(r);
  }
}
function Os(e, t) {
  var r = t.last;
  r === null ? t.last = t.first = e : (r.next = e, e.prev = r, t.last = e);
}
function Ee(e, t) {
  var r = k;
  r !== null && (r.f & H) !== 0 && (e |= H);
  var n = {
    ctx: _e,
    deps: null,
    nodes: null,
    f: e | z | oe,
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
  b == null || b.register_created_effect(n);
  var i = n;
  if ((e & Ct) !== 0)
    gt !== null ? gt.push(n) : qe.ensure().schedule(n);
  else if (t !== null) {
    try {
      Rt(n);
    } catch (o) {
      throw B(n), o;
    }
    i.deps === null && i.teardown === null && i.nodes === null && i.first === i.last && // either `null`, or a singular child
    (i.f & ht) === 0 && (i = i.first, (e & se) !== 0 && (e & ut) !== 0 && i !== null && (i.f |= ut));
  }
  if (i !== null && (i.parent = r, r !== null && Os(i, r), E !== null && (E.f & P) !== 0 && (e & De) === 0)) {
    var s = (
      /** @type {Derived} */
      E
    );
    (s.effects ?? (s.effects = [])).push(i);
  }
  return n;
}
function an() {
  return E !== null && !pe;
}
function Ls(e) {
  const t = Ee(kr, null);
  return R(t, I), t.teardown = e, t;
}
function Is(e) {
  return Ee(Ct | Qi, e);
}
function zs(e) {
  qe.ensure();
  const t = Ee(De | ht, e);
  return () => {
    B(t);
  };
}
function Ds(e) {
  qe.ensure();
  const t = Ee(De | ht, e);
  return (r = {}) => new Promise((n) => {
    r.outro ? at(t, () => {
      B(t), n(void 0);
    }) : (B(t), n(void 0));
  });
}
function Fs(e) {
  return Ee(Ct, e);
}
function Ps(e) {
  return Ee(mt | ht, e);
}
function gi(e, t = 0) {
  return Ee(kr | t, e);
}
function Lt(e, t = [], r = [], n = []) {
  Ss(n, t, r, (i) => {
    Ee(kr, () => e(...i.map(O)));
  });
}
function fn(e, t = 0) {
  var r = Ee(se | t, e);
  return r;
}
function ie(e) {
  return Ee($e | ht, e);
}
function wi(e) {
  var t = e.teardown;
  if (t !== null) {
    const r = Ge, n = E;
    mn(!0), fe(null);
    try {
      t.call(null);
    } finally {
      mn(r), fe(n);
    }
  }
}
function un(e, t = !1) {
  var r = e.first;
  for (e.first = e.last = null; r !== null; ) {
    const i = r.ac;
    i !== null && Ar(() => {
      i.abort(xr);
    });
    var n = r.next;
    (r.f & De) !== 0 ? r.parent = null : B(r, t), r = n;
  }
}
function js(e) {
  for (var t = e.first; t !== null; ) {
    var r = t.next;
    (t.f & $e) === 0 && B(t), t = r;
  }
}
function B(e, t = !0) {
  var r = !1;
  (t || (e.f & Zi) !== 0) && e.nodes !== null && e.nodes.end !== null && (Hs(
    e.nodes.start,
    /** @type {TemplateNode} */
    e.nodes.end
  ), r = !0), R(e, pn), un(e, t && !r), Yt(e, 0);
  var n = e.nodes && e.nodes.t;
  if (n !== null)
    for (const s of n)
      s.stop();
  wi(e), e.f ^= pn, e.f |= le;
  var i = e.parent;
  i !== null && i.first !== null && bi(e), e.next = e.prev = e.teardown = e.ctx = e.deps = e.fn = e.nodes = e.ac = e.b = null;
}
function Hs(e, t) {
  for (; e !== null; ) {
    var r = e === t ? null : /* @__PURE__ */ Fe(e);
    e.remove(), e = r;
  }
}
function bi(e) {
  var t = e.parent, r = e.prev, n = e.next;
  r !== null && (r.next = n), n !== null && (n.prev = r), t !== null && (t.first === e && (t.first = n), t.last === e && (t.last = r));
}
function at(e, t, r = !0) {
  var n = [];
  mi(e, n, !0);
  var i = () => {
    r && B(e), t && t();
  }, s = n.length;
  if (s > 0) {
    var o = () => --s || i();
    for (var l of n)
      l.out(o);
  } else
    i();
}
function mi(e, t, r) {
  if ((e.f & H) === 0) {
    e.f ^= H;
    var n = e.nodes && e.nodes.t;
    if (n !== null)
      for (const l of n)
        (l.is_global || r) && t.push(l);
    for (var i = e.first; i !== null; ) {
      var s = i.next;
      if ((i.f & De) === 0) {
        var o = (i.f & ut) !== 0 || // If this is a branch effect without a block effect parent,
        // it means the parent block effect was pruned. In that case,
        // transparency information was transferred to the branch effect.
        (i.f & $e) !== 0 && (e.f & se) !== 0;
        mi(i, t, o ? r : !1);
      }
      i = s;
    }
  }
}
function wr(e) {
  yi(e, !0);
}
function yi(e, t) {
  if ((e.f & H) !== 0) {
    e.f ^= H, (e.f & I) === 0 && (R(e, z), qe.ensure().schedule(e));
    for (var r = e.first; r !== null; ) {
      var n = r.next, i = (r.f & ut) !== 0 || (r.f & $e) !== 0;
      yi(r, i ? t : !1), r = n;
    }
    var s = e.nodes && e.nodes.t;
    if (s !== null)
      for (const o of s)
        (o.is_global || t) && o.in();
  }
}
function cn(e, t) {
  if (e.nodes)
    for (var r = e.nodes.start, n = e.nodes.end; r !== null; ) {
      var i = r === n ? null : /* @__PURE__ */ Fe(r);
      t.append(r), r = i;
    }
}
let fr = !1, Ge = !1;
function mn(e) {
  Ge = e;
}
let E = null, pe = !1;
function fe(e) {
  E = e;
}
let k = null;
function xe(e) {
  k = e;
}
let ae = null;
function $i(e) {
  E !== null && (ae === null ? ae = [e] : ae.push(e));
}
let q = null, K = 0, ee = null;
function Bs(e) {
  ee = e;
}
let ki = 1, Xe = 0, ft = Xe;
function yn(e) {
  ft = e;
}
function xi() {
  return ++ki;
}
function Qt(e) {
  var t = e.f;
  if ((t & z) !== 0)
    return !0;
  if (t & P && (e.f &= ~ct), (t & ke) !== 0) {
    for (var r = (
      /** @type {Value[]} */
      e.deps
    ), n = r.length, i = 0; i < n; i++) {
      var s = r[i];
      if (Qt(
        /** @type {Derived} */
        s
      ) && si(
        /** @type {Derived} */
        s
      ), s.wv > e.wv)
        return !0;
    }
    (t & oe) !== 0 && // During time traveling we don't want to reset the status so that
    // traversal of the graph in the other batches still happens
    F === null && R(e, I);
  }
  return !1;
}
function Ei(e, t, r = !0) {
  var n = e.reactions;
  if (n !== null && !(ae !== null && st.call(ae, e)))
    for (var i = 0; i < n.length; i++) {
      var s = n[i];
      (s.f & P) !== 0 ? Ei(
        /** @type {Derived} */
        s,
        t,
        !1
      ) : t === s && (r ? R(s, z) : (s.f & I) !== 0 && R(s, ke), on(
        /** @type {Effect} */
        s
      ));
    }
}
function Si(e) {
  var _;
  var t = q, r = K, n = ee, i = E, s = ae, o = _e, l = pe, f = ft, u = e.f;
  q = /** @type {null | Value[]} */
  null, K = 0, ee = null, E = (u & ($e | De)) === 0 ? e : null, ae = null, Nt(e.ctx), pe = !1, ft = ++Xe, e.ac !== null && (Ar(() => {
    e.ac.abort(xr);
  }), e.ac = null);
  try {
    e.f |= vr;
    var p = (
      /** @type {Function} */
      e.fn
    ), c = p();
    e.f |= Ke;
    var h = e.deps, v = b == null ? void 0 : b.is_fork;
    if (q !== null) {
      var d;
      if (v || Yt(e, K), h !== null && K > 0)
        for (h.length = K + q.length, d = 0; d < q.length; d++)
          h[K + d] = q[d];
      else
        e.deps = h = q;
      if (an() && (e.f & oe) !== 0)
        for (d = K; d < h.length; d++)
          ((_ = h[d]).reactions ?? (_.reactions = [])).push(e);
    } else !v && h !== null && K < h.length && (Yt(e, K), h.length = K);
    if (Yn() && ee !== null && !pe && h !== null && (e.f & (P | ke | z)) === 0)
      for (d = 0; d < /** @type {Source[]} */
      ee.length; d++)
        Ei(
          ee[d],
          /** @type {Effect} */
          e
        );
    if (i !== null && i !== e) {
      if (Xe++, i.deps !== null)
        for (let g = 0; g < r; g += 1)
          i.deps[g].rv = Xe;
      if (t !== null)
        for (const g of t)
          g.rv = Xe;
      ee !== null && (n === null ? n = ee : n.push(.../** @type {Source[]} */
      ee));
    }
    return (e.f & Ye) !== 0 && (e.f ^= Ye), c;
  } catch (g) {
    return Gn(g);
  } finally {
    e.f ^= vr, q = t, K = r, ee = n, E = i, ae = s, Nt(o), pe = l, ft = f;
  }
}
function Us(e, t) {
  let r = t.reactions;
  if (r !== null) {
    var n = qi.call(r, e);
    if (n !== -1) {
      var i = r.length - 1;
      i === 0 ? r = t.reactions = null : (r[n] = r[i], r.pop());
    }
  }
  if (r === null && (t.f & P) !== 0 && // Destroying a child effect while updating a parent effect can cause a dependency to appear
  // to be unused, when in fact it is used by the currently-updating parent. Checking `new_deps`
  // allows us to skip the expensive work of disconnecting and immediately reconnecting it
  (q === null || !st.call(q, t))) {
    var s = (
      /** @type {Derived} */
      t
    );
    (s.f & oe) !== 0 && (s.f ^= oe, s.f &= ~ct), s.v !== L && nn(s), Ms(s), Yt(s, 0);
  }
}
function Yt(e, t) {
  var r = e.deps;
  if (r !== null)
    for (var n = t; n < r.length; n++)
      Us(e, r[n]);
}
function Rt(e) {
  var t = e.f;
  if ((t & le) === 0) {
    R(e, I);
    var r = k, n = fr;
    k = e, fr = !0;
    try {
      (t & (se | Hn)) !== 0 ? js(e) : un(e), wi(e);
      var i = Si(e);
      e.teardown = typeof i == "function" ? i : null, e.wv = ki;
      var s;
      Dn && _s && (e.f & z) !== 0 && e.deps;
    } finally {
      fr = n, k = r;
    }
  }
}
function O(e) {
  var t = e.f, r = (t & P) !== 0;
  if (E !== null && !pe) {
    var n = k !== null && (k.f & le) !== 0;
    if (!n && (ae === null || !st.call(ae, e))) {
      var i = E.deps;
      if ((E.f & vr) !== 0)
        e.rv < Xe && (e.rv = Xe, q === null && i !== null && i[K] === e ? K++ : q === null ? q = [e] : q.push(e));
      else {
        E.deps ?? (E.deps = []), st.call(E.deps, e) || E.deps.push(e);
        var s = e.reactions;
        s === null ? e.reactions = [E] : st.call(s, E) || s.push(E);
      }
    }
  }
  if (Ge && lt.has(e))
    return lt.get(e);
  if (r) {
    var o = (
      /** @type {Derived} */
      e
    );
    if (Ge) {
      var l = o.v;
      return ((o.f & I) === 0 && o.reactions !== null || Ai(o)) && (l = ln(o)), lt.set(o, l), l;
    }
    var f = (o.f & oe) === 0 && !pe && E !== null && (fr || (E.f & oe) !== 0), u = (o.f & Ke) === 0;
    Qt(o) && (f && (o.f |= oe), si(o)), f && !u && (oi(o), Ti(o));
  }
  if (F != null && F.has(e))
    return F.get(e);
  if ((e.f & Ye) !== 0)
    throw e.v;
  return e.v;
}
function Ti(e) {
  if (e.f |= oe, e.deps !== null)
    for (const t of e.deps)
      (t.reactions ?? (t.reactions = [])).push(e), (t.f & P) !== 0 && (t.f & oe) === 0 && (oi(
        /** @type {Derived} */
        t
      ), Ti(
        /** @type {Derived} */
        t
      ));
}
function Ai(e) {
  if (e.v === L) return !0;
  if (e.deps === null) return !1;
  for (const t of e.deps)
    if (lt.has(t) || (t.f & P) !== 0 && Ai(
      /** @type {Derived} */
      t
    ))
      return !0;
  return !1;
}
function Ci(e) {
  var t = pe;
  try {
    return pe = !0, e();
  } finally {
    pe = t;
  }
}
const Ze = Symbol("events"), Ni = /* @__PURE__ */ new Set(), Vr = /* @__PURE__ */ new Set();
function Vs(e, t, r) {
  (t[Ze] ?? (t[Ze] = {}))[e] = r;
}
function Ys(e) {
  for (var t = 0; t < e.length; t++)
    Ni.add(e[t]);
  for (var r of Vr)
    r(e);
}
let $n = null;
function kn(e) {
  var _, g;
  var t = this, r = (
    /** @type {Node} */
    t.ownerDocument
  ), n = e.type, i = ((_ = e.composedPath) == null ? void 0 : _.call(e)) || [], s = (
    /** @type {null | Element} */
    i[0] || e.target
  );
  $n = e;
  var o = 0, l = $n === e && e[Ze];
  if (l) {
    var f = i.indexOf(l);
    if (f !== -1 && (t === document || t === /** @type {any} */
    window)) {
      e[Ze] = t;
      return;
    }
    var u = i.indexOf(t);
    if (u === -1)
      return;
    f <= u && (o = f);
  }
  if (s = /** @type {Element} */
  i[o] || e.target, s !== t) {
    dr(e, "currentTarget", {
      configurable: !0,
      get() {
        return s || r;
      }
    });
    var p = E, c = k;
    fe(null), xe(null);
    try {
      for (var h, v = []; s !== null && s !== t; ) {
        try {
          var d = (g = s[Ze]) == null ? void 0 : g[n];
          d != null && (!/** @type {any} */
          s.disabled || // DOM could've been updated already by the time this is reached, so we check this as well
          // -> the target could not have been disabled because it emits the event in the first place
          e.target === s) && d.call(s, e);
        } catch (y) {
          h ? v.push(y) : h = y;
        }
        if (e.cancelBubble) break;
        o++, s = o < i.length ? (
          /** @type {Element} */
          i[o]
        ) : null;
      }
      if (h) {
        for (let y of v)
          queueMicrotask(() => {
            throw y;
          });
        throw h;
      }
    } finally {
      e[Ze] = t, delete e.currentTarget, fe(p), xe(c);
    }
  }
}
var Ln;
const Or = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  ((Ln = globalThis == null ? void 0 : globalThis.window) == null ? void 0 : Ln.trustedTypes) && /* @__PURE__ */ globalThis.window.trustedTypes.createPolicy("svelte-trusted-html", {
    /** @param {string} html */
    createHTML: (e) => e
  })
);
function qs(e) {
  return (
    /** @type {string} */
    (Or == null ? void 0 : Or.createHTML(e)) ?? e
  );
}
function Gs(e) {
  var t = pi("template");
  return t.innerHTML = qs(e.replaceAll("<!>", "<!---->")), t.content;
}
function Yr(e, t) {
  var r = (
    /** @type {Effect} */
    k
  );
  r.nodes === null && (r.nodes = { start: e, end: t, a: null, t: null });
}
// @__NO_SIDE_EFFECTS__
function Ot(e, t) {
  var r = (t & Yi) !== 0, n, i = !e.startsWith("<!>");
  return () => {
    if (A)
      return Yr(C, null), C;
    n === void 0 && (n = Gs(i ? e : "<!>" + e), n = /** @type {TemplateNode} */
    /* @__PURE__ */ Vt(n));
    var s = (
      /** @type {TemplateNode} */
      r || ui ? document.importNode(n, !0) : n.cloneNode(!0)
    );
    return Yr(s, s), s;
  };
}
function We(e, t) {
  if (A) {
    var r = (
      /** @type {Effect & { nodes: EffectNodes }} */
      k
    );
    ((r.f & Ke) === 0 || r.nodes.end === null) && (r.nodes.end = C), Sr();
    return;
  }
  e !== null && e.before(
    /** @type {Node} */
    t
  );
}
const Ks = ["touchstart", "touchmove"];
function Ws(e) {
  return Ks.includes(e);
}
let qr = !0;
function tr(e, t) {
  var r = t == null ? "" : typeof t == "object" ? `${t}` : t;
  r !== /** @type {any} */
  (e[zt] ?? (e[zt] = e.nodeValue)) && (e[zt] = r, e.nodeValue = `${r}`);
}
function Mi(e, t) {
  return Ri(e, t);
}
function Js(e, t) {
  Ur(), t.intro = t.intro ?? !1;
  const r = t.target, n = A, i = C;
  try {
    for (var s = /* @__PURE__ */ Vt(r); s && (s.nodeType !== Zt || /** @type {Comment} */
    s.data !== In); )
      s = /* @__PURE__ */ Fe(s);
    if (!s)
      throw At;
    ze(!0), Z(
      /** @type {Comment} */
      s
    );
    const o = Ri(e, { ...t, anchor: s });
    return ze(!1), /**  @type {Exports} */
    o;
  } catch (o) {
    if (o instanceof Error && o.message.split(`
`).some((l) => l.startsWith("https://svelte.dev/e/")))
      throw o;
    return o !== At && console.warn("Failed to hydrate: ", o), t.recover === !1 && ls(), Ur(), hi(r), ze(!1), Mi(e, t);
  } finally {
    ze(n), Z(i);
  }
}
const rr = /* @__PURE__ */ new Map();
function Ri(e, { target: t, anchor: r, props: n = {}, events: i, context: s, intro: o = !0, transformError: l }) {
  Ur();
  var f = void 0, u = Ds(() => {
    var p = r ?? t.appendChild(ye());
    xs(
      /** @type {TemplateNode} */
      p,
      {
        pending: () => {
        }
      },
      (v) => {
        tn({});
        var d = (
          /** @type {ComponentContext} */
          _e
        );
        if (s && (d.c = s), i && (n.$$events = i), A && Yr(
          /** @type {TemplateNode} */
          v,
          null
        ), qr = o, f = e(v, n) || {}, qr = !0, A && (k.nodes.end = C, C === null || C.nodeType !== Zt || /** @type {Comment} */
        C.data !== Qr))
          throw Er(), At;
        rn();
      },
      l
    );
    var c = /* @__PURE__ */ new Set(), h = (v) => {
      for (var d = 0; d < v.length; d++) {
        var _ = v[d];
        if (!c.has(_)) {
          c.add(_);
          var g = Ws(_);
          for (const S of [t, document]) {
            var y = rr.get(S);
            y === void 0 && (y = /* @__PURE__ */ new Map(), rr.set(S, y));
            var $ = y.get(_);
            $ === void 0 ? (S.addEventListener(_, kn, { passive: g }), y.set(_, 1)) : y.set(_, $ + 1);
          }
        }
      }
    };
    return h($r(Ni)), Vr.add(h), () => {
      var g;
      for (var v of c)
        for (const y of [t, document]) {
          var d = (
            /** @type {Map<string, number>} */
            rr.get(y)
          ), _ = (
            /** @type {number} */
            d.get(v)
          );
          --_ == 0 ? (y.removeEventListener(v, kn), d.delete(v), d.size === 0 && rr.delete(y)) : d.set(v, _);
        }
      Vr.delete(h), p !== r && ((g = p.parentNode) == null || g.removeChild(p));
    };
  });
  return Gr.set(f, u), f;
}
let Gr = /* @__PURE__ */ new WeakMap();
function Xs(e, t) {
  const r = Gr.get(e);
  return r ? (Gr.delete(e), r(t)) : Promise.resolve();
}
var he, me, X, it, Jt, Xt, yr;
class Zs {
  /**
   * @param {TemplateNode} anchor
   * @param {boolean} transition
   */
  constructor(t, r = !0) {
    /** @type {TemplateNode} */
    M(this, "anchor");
    /** @type {Map<Batch, Key>} */
    m(this, he, /* @__PURE__ */ new Map());
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
    m(this, me, /* @__PURE__ */ new Map());
    /**
     * Similar to #onscreen with respect to the keys, but contains branches that are not yet
     * in the DOM, because their insertion is deferred.
     * @type {Map<Key, Branch>}
     */
    m(this, X, /* @__PURE__ */ new Map());
    /**
     * Keys of effects that are currently outroing
     * @type {Set<Key>}
     */
    m(this, it, /* @__PURE__ */ new Set());
    /**
     * Whether to pause (i.e. outro) on change, or destroy immediately.
     * This is necessary for `<svelte:element>`
     */
    m(this, Jt, !0);
    /**
     * @param {Batch} batch
     */
    m(this, Xt, (t) => {
      if (a(this, he).has(t)) {
        var r = (
          /** @type {Key} */
          a(this, he).get(t)
        ), n = a(this, me).get(r);
        if (n)
          wr(n), a(this, it).delete(r);
        else {
          var i = a(this, X).get(r);
          i && (wr(i.effect), a(this, me).set(r, i.effect), a(this, X).delete(r), i.fragment.lastChild.remove(), this.anchor.before(i.fragment), n = i.effect);
        }
        for (const [s, o] of a(this, he)) {
          if (a(this, he).delete(s), s === t)
            break;
          const l = a(this, X).get(o);
          l && (B(l.effect), a(this, X).delete(o));
        }
        for (const [s, o] of a(this, me)) {
          if (s === r || a(this, it).has(s)) continue;
          const l = () => {
            if (Array.from(a(this, he).values()).includes(s)) {
              var u = document.createDocumentFragment();
              cn(o, u), u.append(ye()), a(this, X).set(s, { effect: o, fragment: u });
            } else
              B(o);
            a(this, it).delete(s), a(this, me).delete(s);
          };
          a(this, Jt) || !n ? (a(this, it).add(s), at(o, l, !1)) : l();
        }
      }
    });
    /**
     * @param {Batch} batch
     */
    m(this, yr, (t) => {
      a(this, he).delete(t);
      const r = Array.from(a(this, he).values());
      for (const [n, i] of a(this, X))
        r.includes(n) || (B(i.effect), a(this, X).delete(n));
    });
    this.anchor = t, w(this, Jt, r);
  }
  /**
   *
   * @param {any} key
   * @param {null | ((target: TemplateNode) => void)} fn
   */
  ensure(t, r) {
    var n = (
      /** @type {Batch} */
      b
    ), i = vi();
    if (r && !a(this, me).has(t) && !a(this, X).has(t))
      if (i) {
        var s = document.createDocumentFragment(), o = ye();
        s.append(o), a(this, X).set(t, {
          effect: ie(() => r(o)),
          fragment: s
        });
      } else
        a(this, me).set(
          t,
          ie(() => r(this.anchor))
        );
    if (a(this, he).set(n, t), i) {
      for (const [l, f] of a(this, me))
        l === t ? n.unskip_effect(f) : n.skip_effect(f);
      for (const [l, f] of a(this, X))
        l === t ? n.unskip_effect(f.effect) : n.skip_effect(f.effect);
      n.oncommit(a(this, Xt)), n.ondiscard(a(this, yr));
    } else
      A && (this.anchor = C), a(this, Xt).call(this, n);
  }
}
he = new WeakMap(), me = new WeakMap(), X = new WeakMap(), it = new WeakMap(), Jt = new WeakMap(), Xt = new WeakMap(), yr = new WeakMap();
function nr(e, t, r = !1) {
  var n;
  A && (n = C, Sr());
  var i = new Zs(e), s = r ? ut : 0;
  function o(l, f) {
    if (A) {
      var u = Bn(
        /** @type {TemplateNode} */
        n
      );
      if (l !== parseInt(u.substring(1))) {
        var p = pr();
        Z(p), i.anchor = p, ze(!1), i.ensure(l, f), ze(!0);
        return;
      }
    }
    i.ensure(l, f);
  }
  fn(() => {
    var l = !1;
    t((f, u = 0) => {
      l = !0, o(u, f);
    }), l || o(-1, null);
  }, s);
}
function Qs(e, t, r) {
  for (var n = [], i = t.length, s, o = t.length, l = 0; l < i; l++) {
    let c = t[l];
    at(
      c,
      () => {
        if (s) {
          if (s.pending.delete(c), s.done.add(c), s.pending.size === 0) {
            var h = (
              /** @type {Set<EachOutroGroup>} */
              e.outrogroups
            );
            Kr(e, $r(s.done)), h.delete(s), h.size === 0 && (e.outrogroups = null);
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
      ), p = (
        /** @type {Element} */
        u.parentNode
      );
      hi(p), p.append(u), e.items.clear();
    }
    Kr(e, t, !f);
  } else
    s = {
      pending: new Set(t),
      done: /* @__PURE__ */ new Set()
    }, (e.outrogroups ?? (e.outrogroups = /* @__PURE__ */ new Set())).add(s);
}
function Kr(e, t, r = !0) {
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
      s.f |= Ie;
      const o = document.createDocumentFragment();
      cn(s, o);
    } else
      B(t[i], r);
  }
}
var xn;
function En(e, t, r, n, i, s = null) {
  var o = e, l = /* @__PURE__ */ new Map();
  {
    var f = (
      /** @type {Element} */
      e
    );
    o = A ? Z(/* @__PURE__ */ Vt(f)) : f.appendChild(ye());
  }
  A && Sr();
  var u = null, p = /* @__PURE__ */ Cs(() => {
    var $ = r();
    return Fn($) ? $ : $ == null ? [] : $r($);
  }), c, h = /* @__PURE__ */ new Map(), v = !0;
  function d($) {
    (y.effect.f & le) === 0 && (y.pending.delete($), y.fallback = u, eo(y, c, o, t, n), u !== null && (c.length === 0 ? (u.f & Ie) === 0 ? wr(u) : (u.f ^= Ie, jt(u, null, o)) : at(u, () => {
      u = null;
    })));
  }
  function _($) {
    y.pending.delete($);
  }
  var g = fn(() => {
    c = /** @type {V[]} */
    O(p);
    var $ = c.length;
    let S = !1;
    if (A) {
      var D = Bn(o) === Zr;
      D !== ($ === 0) && (o = pr(), Z(o), ze(!1), S = !0);
    }
    for (var U = /* @__PURE__ */ new Set(), ue = (
      /** @type {Batch} */
      b
    ), Pe = vi(), Q = 0; Q < $; Q += 1) {
      A && C.nodeType === Zt && /** @type {Comment} */
      C.data === Qr && (o = /** @type {Comment} */
      C, S = !0, ze(!1));
      var G = c[Q], Se = n(G, Q), Te = v ? null : l.get(Se);
      Te ? (Te.v && Mt(Te.v, G), Te.i && Mt(Te.i, Q), Pe && ue.unskip_effect(Te.e)) : (Te = to(
        l,
        v ? o : xn ?? (xn = ye()),
        G,
        Se,
        Q,
        i,
        t,
        r
      ), v || (Te.e.f |= Ie), l.set(Se, Te)), U.add(Se);
    }
    if ($ === 0 && s && !u && (v ? u = ie(() => s(o)) : (u = ie(() => s(xn ?? (xn = ye()))), u.f |= Ie)), $ > U.size && ss(), A && $ > 0 && Z(pr()), !v)
      if (h.set(ue, U), Pe) {
        for (const [Di, Fi] of l)
          U.has(Di) || ue.skip_effect(Fi.e);
        ue.oncommit(d), ue.ondiscard(_);
      } else
        d(ue);
    S && ze(!0), O(p);
  }), y = { effect: g, items: l, pending: h, outrogroups: null, fallback: u };
  v = !1, A && (o = C);
}
function It(e) {
  for (; e !== null && (e.f & $e) === 0; )
    e = e.next;
  return e;
}
function eo(e, t, r, n, i) {
  var Q;
  var s = t.length, o = e.items, l = It(e.effect.first), f, u = null, p = [], c = [], h, v, d, _;
  for (_ = 0; _ < s; _ += 1) {
    if (h = t[_], v = i(h, _), d = /** @type {EachItem} */
    o.get(v).e, e.outrogroups !== null)
      for (const G of e.outrogroups)
        G.pending.delete(d), G.done.delete(d);
    if ((d.f & H) !== 0 && wr(d), (d.f & Ie) !== 0)
      if (d.f ^= Ie, d === l)
        jt(d, null, r);
      else {
        var g = u ? u.next : l;
        d === e.effect.last && (e.effect.last = d.prev), d.prev && (d.prev.next = d.next), d.next && (d.next.prev = d.prev), je(e, u, d), je(e, d, g), jt(d, g, r), u = d, p = [], c = [], l = It(u.next);
        continue;
      }
    if (d !== l) {
      if (f !== void 0 && f.has(d)) {
        if (p.length < c.length) {
          var y = c[0], $;
          u = y.prev;
          var S = p[0], D = p[p.length - 1];
          for ($ = 0; $ < p.length; $ += 1)
            jt(p[$], y, r);
          for ($ = 0; $ < c.length; $ += 1)
            f.delete(c[$]);
          je(e, S.prev, D.next), je(e, u, S), je(e, D, y), l = y, u = D, _ -= 1, p = [], c = [];
        } else
          f.delete(d), jt(d, l, r), je(e, d.prev, d.next), je(e, d, u === null ? e.effect.first : u.next), je(e, u, d), u = d;
        continue;
      }
      for (p = [], c = []; l !== null && l !== d; )
        (f ?? (f = /* @__PURE__ */ new Set())).add(l), c.push(l), l = It(l.next);
      if (l === null)
        continue;
    }
    (d.f & Ie) === 0 && p.push(d), u = d, l = It(d.next);
  }
  if (e.outrogroups !== null) {
    for (const G of e.outrogroups)
      G.pending.size === 0 && (Kr(e, $r(G.done)), (Q = e.outrogroups) == null || Q.delete(G));
    e.outrogroups.size === 0 && (e.outrogroups = null);
  }
  if (l !== null || f !== void 0) {
    var U = [];
    if (f !== void 0)
      for (d of f)
        (d.f & H) === 0 && U.push(d);
    for (; l !== null; )
      (l.f & H) === 0 && l !== e.fallback && U.push(l), l = It(l.next);
    var ue = U.length;
    if (ue > 0) {
      var Pe = s === 0 ? r : null;
      Qs(e, U, Pe);
    }
  }
}
function to(e, t, r, n, i, s, o, l) {
  var f = (o & Hi) !== 0 ? (o & Ui) === 0 ? /* @__PURE__ */ ai(r, !1, !1) : dt(r) : null, u = (o & Bi) !== 0 ? dt(i) : null;
  return {
    v: f,
    i: u,
    e: ie(() => (s(t, f ?? r, u ?? i, l), () => {
      e.delete(n);
    }))
  };
}
function jt(e, t, r) {
  if (e.nodes)
    for (var n = e.nodes.start, i = e.nodes.end, s = t && (t.f & Ie) === 0 ? (
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
function je(e, t, r) {
  t === null ? e.effect.first = r : t.next = r, r === null ? e.effect.last = t : r.prev = t;
}
const ro = () => performance.now(), Le = {
  // don't access requestAnimationFrame eagerly outside method
  // this allows basic testing of user code without JSDOM
  // bunder will eval and remove ternary when the user's app is built
  tick: (
    /** @param {any} _ */
    (e) => requestAnimationFrame(e)
  ),
  now: () => ro(),
  tasks: /* @__PURE__ */ new Set()
};
function Oi() {
  const e = Le.now();
  Le.tasks.forEach((t) => {
    t.c(e) || (Le.tasks.delete(t), t.f());
  }), Le.tasks.size !== 0 && Le.tick(Oi);
}
function no(e) {
  let t;
  return Le.tasks.size === 0 && Le.tick(Oi), {
    promise: new Promise((r) => {
      Le.tasks.add(t = { c: e, f: r });
    }),
    abort() {
      Le.tasks.delete(t);
    }
  };
}
function ir(e, t) {
  Ar(() => {
    e.dispatchEvent(new CustomEvent(t));
  });
}
function io(e) {
  if (e === "float") return "cssFloat";
  if (e === "offset") return "cssOffset";
  if (e.startsWith("--")) return e;
  const t = e.split("-");
  return t.length === 1 ? t[0] : t[0] + t.slice(1).map(
    /** @param {any} word */
    (r) => r[0].toUpperCase() + r.slice(1)
  ).join("");
}
function Sn(e) {
  const t = {}, r = e.split(";");
  for (const n of r) {
    const [i, s] = n.split(":");
    if (!i || s === void 0) break;
    const o = io(i.trim());
    t[o] = s.trim();
  }
  return t;
}
const so = (e) => e;
function oo(e, t, r, n) {
  var g;
  var i = (e & Vi) !== 0, s = "both", o, l = t.inert, f = t.style.overflow, u, p;
  function c() {
    return Ar(() => o ?? (o = r()(t, (n == null ? void 0 : n()) ?? /** @type {P} */
    {}, {
      direction: s
    })));
  }
  var h = {
    is_global: i,
    in() {
      t.inert = l, u = Wr(
        t,
        c(),
        p,
        1,
        () => {
          ir(t, "introstart");
        },
        () => {
          ir(t, "introend"), u == null || u.abort(), u = o = void 0, t.style.overflow = f;
        }
      );
    },
    out(y) {
      t.inert = !0, p = Wr(
        t,
        c(),
        u,
        0,
        () => {
          ir(t, "outrostart");
        },
        () => {
          ir(t, "outroend"), y == null || y();
        }
      );
    },
    stop: () => {
      u == null || u.abort(), p == null || p.abort();
    }
  }, v = (
    /** @type {Effect & { nodes: EffectNodes }} */
    k
  );
  if (((g = v.nodes).t ?? (g.t = [])).push(h), qr) {
    var d = i;
    if (!d) {
      for (var _ = (
        /** @type {Effect | null} */
        v.parent
      ); _ && (_.f & ut) !== 0; )
        for (; (_ = _.parent) && (_.f & se) === 0; )
          ;
      d = !_ || (_.f & Ke) !== 0;
    }
    d && Fs(() => {
      Ci(() => h.in());
    });
  }
}
function Wr(e, t, r, n, i, s) {
  var o = n === 1;
  if (Ji(t)) {
    var l, f = !1;
    return ot(() => {
      if (!f) {
        var y = t({ direction: o ? "in" : "out" });
        l = Wr(e, y, r, n, i, s);
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
      abort: pt,
      deactivate: pt,
      reset: pt,
      t: () => n
    };
  const { delay: u = 0, css: p, tick: c, easing: h = so } = t;
  var v = [];
  if (o && r === void 0 && (c && c(0, 1), p)) {
    var d = Sn(p(0, 1));
    v.push(d, d);
  }
  var _ = () => 1 - n, g = e.animate(v, { duration: u, fill: "forwards" });
  return g.onfinish = () => {
    g.cancel(), i();
    var y = (r == null ? void 0 : r.t()) ?? 1 - n;
    r == null || r.abort();
    var $ = n - y, S = (
      /** @type {number} */
      t.duration * Math.abs($)
    ), D = [];
    if (S > 0) {
      var U = !1;
      if (p)
        for (var ue = Math.ceil(S / 16.666666666666668), Pe = 0; Pe <= ue; Pe += 1) {
          var Q = y + $ * h(Pe / ue), G = Sn(p(Q, 1 - Q));
          D.push(G), U || (U = G.overflow === "hidden");
        }
      U && (e.style.overflow = "hidden"), _ = () => {
        var Se = (
          /** @type {number} */
          /** @type {globalThis.Animation} */
          g.currentTime
        );
        return y + $ * h(Se / S);
      }, c && no(() => {
        if (g.playState !== "running") return !1;
        var Se = _();
        return c(Se, 1 - Se), !0;
      });
    }
    g = e.animate(D, { duration: S, fill: "forwards" }), g.onfinish = () => {
      _ = () => n, c == null || c(n, 1 - n), s();
    };
  }, {
    abort: () => {
      g && (g.cancel(), g.effect = null, g.onfinish = pt);
    },
    deactivate: () => {
      s = pt;
    },
    reset: () => {
      n === 0 && (c == null || c(1, 0));
    },
    t: () => _()
  };
}
function Li(e) {
  var t, r, n = "";
  if (typeof e == "string" || typeof e == "number") n += e;
  else if (typeof e == "object") if (Array.isArray(e)) {
    var i = e.length;
    for (t = 0; t < i; t++) e[t] && (r = Li(e[t])) && (n && (n += " "), n += r);
  } else for (r in e) e[r] && (n && (n += " "), n += r);
  return n;
}
function lo() {
  for (var e, t, r = 0, n = "", i = arguments.length; r < i; r++) (e = arguments[r]) && (t = Li(e)) && (n && (n += " "), n += t);
  return n;
}
function ao(e) {
  return typeof e == "object" ? lo(e) : e ?? "";
}
const Tn = [...` 	
\r\f \v\uFEFF`];
function fo(e, t, r) {
  var n = e == null ? "" : "" + e;
  if (t && (n = n ? n + " " + t : t), r) {
    for (var i of Object.keys(r))
      if (r[i])
        n = n ? n + " " + i : i;
      else if (n.length)
        for (var s = i.length, o = 0; (o = n.indexOf(i, o)) >= 0; ) {
          var l = o + s;
          (o === 0 || Tn.includes(n[o - 1])) && (l === n.length || Tn.includes(n[l])) ? n = (o === 0 ? "" : n.substring(0, o)) + n.substring(l + 1) : o = l;
        }
  }
  return n === "" ? null : n;
}
function An(e, t, r, n, i, s) {
  var o = (
    /** @type {any} */
    e[Ir]
  );
  if (A || o !== r || o === void 0) {
    var l = fo(r, n, s);
    (!A || l !== e.getAttribute("class")) && (l == null ? e.removeAttribute("class") : e.className = l), e[Ir] = r;
  } else if (s && i !== s)
    for (var f in s) {
      var u = !!s[f];
      (i == null || u !== !!i[f]) && e.classList.toggle(f, u);
    }
  return s;
}
const uo = Symbol("is custom element"), co = Symbol("is html"), ho = ns ? "link" : "LINK";
function ge(e, t, r, n) {
  var i = vo(e);
  A && (i[t] = e.getAttribute(t), t === "src" || t === "srcset" || t === "href" && e.nodeName === ho) || i[t] !== (i[t] = r) && (t === "loading" && (e[ts] = r), r == null ? e.removeAttribute(t) : typeof r != "string" && po(e).includes(t) ? e[t] = r : e.setAttribute(t, r));
}
function vo(e) {
  return (
    /** @type {Record<string | symbol, unknown>} **/
    /** @type {any} */
    e[sr] ?? (e[sr] = {
      [uo]: e.nodeName.includes("-"),
      [co]: e.namespaceURI === zn
    })
  );
}
var Cn = /* @__PURE__ */ new Map();
function po(e) {
  var t = e.getAttribute("is") || e.nodeName, r = Cn.get(t);
  if (r) return r;
  Cn.set(t, r = []);
  for (var n, i = e, s = Element.prototype; s !== i; ) {
    n = Gi(i);
    for (var o in n)
      n[o].set && // better safe than sorry, we don't want spread attributes to mess with HTML content
      o !== "innerHTML" && o !== "textContent" && o !== "innerText" && r.push(o);
    i = Pn(i);
  }
  return r;
}
function Nn(e, t, r, n) {
  var i = (
    /** @type {V} */
    n
  ), s = !0, o = () => (s && (s = !1, i = /** @type {V} */
  n), i);
  e[t];
  var l;
  l = () => {
    var c = (
      /** @type {V} */
      e[t]
    );
    return c === void 0 ? o() : (s = !0, c);
  };
  var f = !1, u = /* @__PURE__ */ Tr(() => (f = !1, l())), p = (
    /** @type {Effect} */
    k
  );
  return (
    /** @type {() => V} */
    (function(c, h) {
      if (arguments.length > 0) {
        const v = h ? O(u) : c;
        return ve(u, v), f = !0, i !== void 0 && (i = v), c;
      }
      return Ge && f || (p.f & le) !== 0 ? u.v : O(u);
    })
  );
}
function _o(e) {
  return new go(e);
}
var Oe, ne;
class go {
  /**
   * @param {ComponentConstructorOptions & {
   *  component: any;
   * }} options
   */
  constructor(t) {
    /** @type {any} */
    m(this, Oe);
    /** @type {Record<string, any>} */
    m(this, ne);
    var s;
    var r = /* @__PURE__ */ new Map(), n = (o, l) => {
      var f = /* @__PURE__ */ ai(l, !1, !1);
      return r.set(o, f), f;
    };
    const i = new Proxy(
      { ...t.props || {}, $$events: {} },
      {
        get(o, l) {
          return O(r.get(l) ?? n(l, Reflect.get(o, l)));
        },
        has(o, l) {
          return l === es ? !0 : (O(r.get(l) ?? n(l, Reflect.get(o, l))), Reflect.has(o, l));
        },
        set(o, l, f) {
          return ve(r.get(l) ?? n(l, f), f), Reflect.set(o, l, f);
        }
      }
    );
    w(this, ne, (t.hydrate ? Js : Mi)(t.component, {
      target: t.target,
      anchor: t.anchor,
      props: i,
      context: t.context,
      intro: t.intro ?? !1,
      recover: t.recover,
      transformError: t.transformError
    })), (!((s = t == null ? void 0 : t.props) != null && s.$$host) || t.sync === !1) && Pr(), w(this, Oe, i.$$events);
    for (const o of Object.keys(a(this, ne)))
      o === "$set" || o === "$destroy" || o === "$on" || dr(this, o, {
        get() {
          return a(this, ne)[o];
        },
        /** @param {any} value */
        set(l) {
          a(this, ne)[o] = l;
        },
        enumerable: !0
      });
    a(this, ne).$set = /** @param {Record<string, any>} next */
    (o) => {
      Object.assign(i, o);
    }, a(this, ne).$destroy = () => {
      Xs(a(this, ne));
    };
  }
  /** @param {Record<string, any>} props */
  $set(t) {
    a(this, ne).$set(t);
  }
  /**
   * @param {string} event
   * @param {(...args: any[]) => any} callback
   * @returns {any}
   */
  $on(t, r) {
    a(this, Oe)[t] = a(this, Oe)[t] || [];
    const n = (...i) => r.call(this, ...i);
    return a(this, Oe)[t].push(n), () => {
      a(this, Oe)[t] = a(this, Oe)[t].filter(
        /** @param {any} fn */
        (i) => i !== n
      );
    };
  }
  $destroy() {
    a(this, ne).$destroy();
  }
}
Oe = new WeakMap(), ne = new WeakMap();
let Ii;
typeof HTMLElement == "function" && (Ii = class extends HTMLElement {
  /**
   * @param {*} $$componentCtor
   * @param {*} $$slots
   * @param {ShadowRootInit | undefined} shadow_root_init
   */
  constructor(t, r, n) {
    super();
    /** The Svelte component constructor */
    M(this, "$$ctor");
    /** Slots */
    M(this, "$$s");
    /** @type {any} The Svelte component instance */
    M(this, "$$c");
    /** Whether or not the custom element is connected */
    M(this, "$$cn", !1);
    /** @type {Record<string, any>} Component props data */
    M(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    M(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    M(this, "$$p_d", {});
    /** @type {Record<string, EventListenerOrEventListenerObject[]>} Event listeners */
    M(this, "$$l", {});
    /** @type {Map<EventListenerOrEventListenerObject, Function>} Event listener unsubscribe functions */
    M(this, "$$l_u", /* @__PURE__ */ new Map());
    /** @type {any} The managed render effect for reflecting attributes */
    M(this, "$$me");
    /** @type {ShadowRoot | null} The ShadowRoot of the custom element */
    M(this, "$$shadowRoot", null);
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
          const l = pi("slot");
          s !== "default" && (l.name = s), We(o, l);
        };
      };
      var t = r;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const n = {}, i = wo(this);
      for (const s of this.$$s)
        s in i && (s === "default" && !this.$$d.children ? (this.$$d.children = r(s), n.default = !0) : n[s] = r(s));
      for (const s of this.attributes) {
        const o = this.$$g_p(s.name);
        o in this.$$d || (this.$$d[o] = ur(o, s.value, this.$$p_d, "toProp"));
      }
      for (const s in this.$$p_d)
        !(s in this.$$d) && this[s] !== void 0 && (this.$$d[s] = this[s], delete this[s]);
      this.$$c = _o({
        component: this.$$ctor,
        target: this.$$shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: n,
          $$host: this
        }
      }), this.$$me = zs(() => {
        gi(() => {
          var s;
          this.$$r = !0;
          for (const o of cr(this.$$c)) {
            if (!((s = this.$$p_d[o]) != null && s.reflect)) continue;
            this.$$d[o] = this.$$c[o];
            const l = ur(
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
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = ur(t, n, this.$$p_d, "toProp"), (i = this.$$c) == null || i.$set({ [t]: this.$$d[t] }));
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
    return cr(this.$$p_d).find(
      (r) => this.$$p_d[r].attribute === t || !this.$$p_d[r].attribute && r.toLowerCase() === t
    ) || t;
  }
});
function ur(e, t, r, n) {
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
function wo(e) {
  const t = {};
  return e.childNodes.forEach((r) => {
    t[
      /** @type {Element} node */
      r.slot || "default"
    ] = !0;
  }), t;
}
function zi(e, t, r, n, i, s) {
  let o = class extends Ii {
    constructor() {
      super(e, r, i), this.$$p_d = t;
    }
    static get observedAttributes() {
      return cr(t).map(
        (l) => (t[l].attribute || l).toLowerCase()
      );
    }
  };
  return cr(t).forEach((l) => {
    dr(o.prototype, l, {
      get() {
        return this.$$c && l in this.$$c ? this.$$c[l] : this.$$d[l];
      },
      set(f) {
        var c;
        f = ur(l, f, t), this.$$d[l] = f;
        var u = this.$$c;
        if (u) {
          var p = (c = bt(u, l)) == null ? void 0 : c.get;
          p ? u[l] = f : u.$set({ [l]: f });
        }
      }
    });
  }), n.forEach((l) => {
    dr(o.prototype, l, {
      get() {
        var f;
        return (f = this.$$c) == null ? void 0 : f[l];
      }
    });
  }), s && (o = s(o)), e.element = /** @type {any} */
  o, o;
}
const bo = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.absolute{position:absolute}.left-0{left:0}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.h-6{height:1.5rem}.w-6{width:1.5rem}.max-w-\\[335px\\]{max-width:335px}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity, 1))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity, 1))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity, 1))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity, 1))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity, 1))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity, 1))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity, 1))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity, 1))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity, 1))}.underline{text-decoration-line:underline}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}@media(min-width:320px){.min-\\[320px\\]\\:right-4{right:1rem}}', Jr = new CSSStyleSheet();
Jr.replaceSync(bo);
const mo = (e) => class extends e {
  connectedCallback() {
    var t, r;
    (t = super.connectedCallback) == null || t.call(this), (r = this.shadowRoot) != null && r.adoptedStyleSheets && Jr && (this.shadowRoot.adoptedStyleSheets = [Jr]);
  }
};
function yo(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function Mn(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
function $o(e, { delay: t = 0, duration: r = 400, easing: n = yo, x: i = 0, y: s = 0, opacity: o = 0 } = {}) {
  const l = getComputedStyle(e), f = +l.opacity, u = l.transform === "none" ? "" : l.transform, p = f * (1 - o), [c, h] = Mn(i), [v, d] = Mn(s);
  return {
    delay: t,
    duration: r,
    easing: n,
    css: (_, g) => `
			transform: ${u} translate(${(1 - _) * c}${h}, ${(1 - _) * v}${d});
			opacity: ${f - p * g}`
  };
}
var ko = /* @__PURE__ */ Ot('<a class="underline hover:no-underline"> </a>'), xo = /* @__PURE__ */ Ot('<p class="text-sm font-medium"> <!></p>'), Eo = /* @__PURE__ */ Ot('<a class="underline hover:no-underline"> </a>'), So = /* @__PURE__ */ Ot('<p class="break-word text-sm"><span> </span> <!></p>'), To = /* @__PURE__ */ Ot('<section><div><!> <!></div> <button class="w-6 h-6 flex justify-center items-center" type="button"><svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z" fill="currentColor"></path></svg></button></section>');
function Xr(e, t) {
  tn(t, !0);
  let r = Nn(t, "toast"), n = Nn(t, "closeToast");
  var i = {
    get toast() {
      return r();
    },
    set toast(v) {
      r(v), Pr();
    },
    get closeToast() {
      return n();
    },
    set closeToast(v) {
      n(v), Pr();
    }
  }, s = To(), o = He(s);
  let l;
  var f = He(o);
  {
    var u = (v) => {
      var d = xo(), _ = He(d), g = Pt(_);
      {
        var y = ($) => {
          var S = ko(), D = He(S, !0);
          we(S), Lt(() => {
            ge(S, "href", r().readMoreUrl), ge(S, "target", r().readMoreIsExternal ? "_blank" : void 0), ge(S, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), tr(D, r().readMoreText);
          }), We($, S);
        };
        nr(g, ($) => {
          !r().body && r().readMoreText && $(y);
        });
      }
      we(d), Lt(() => {
        ge(d, "id", `toast-title-${r().id}`), tr(_, `${r().title ?? ""} `);
      }), We(v, d);
    };
    nr(f, (v) => {
      r().title && v(u);
    });
  }
  var p = Pt(f, 2);
  {
    var c = (v) => {
      var d = So(), _ = He(d), g = He(_, !0);
      we(_);
      var y = Pt(_, 2);
      {
        var $ = (S) => {
          var D = Eo(), U = He(D, !0);
          we(D), Lt(() => {
            ge(D, "href", r().readMoreUrl), ge(D, "target", r().readMoreIsExternal ? "_blank" : void 0), ge(D, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), tr(U, r().readMoreText);
          }), We(S, D);
        };
        nr(y, (S) => {
          r().readMoreText && S($);
        });
      }
      we(d), Lt(() => {
        ge(_, "id", `toast-body-${r().id}`), tr(g, r().body);
      }), We(v, d);
    };
    nr(p, (v) => {
      r().body && v(c);
    });
  }
  we(o);
  var h = Pt(o, 2);
  return we(s), Lt(() => {
    An(s, 1, ao(r().class)), ge(s, "aria-labelledby", `toast-title-${r().id} toast-body-${r().id}`), l = An(o, 1, "flex gap-1", null, l, { "flex-col": r().body, "mt-2": r().title }), ge(h, "aria-label", r().closeLabel);
  }), Vs("click", h, () => n()(r())), oo(3, s, () => $o, () => ({ x: 100 })), We(e, s), rn(i);
}
Ys(["click"]);
zi(Xr, { toast: {}, closeToast: {} }, [], [], { mode: "open" });
var Ao = /* @__PURE__ */ Ot('<div class="absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2"><div class="flex flex-col gap-2" role="alert"></div> <div class="flex flex-col gap-2" role="status"></div></div>');
function Co(e, t) {
  tn(t, !0);
  const r = t.$$host;
  let n = /* @__PURE__ */ Ae(wt([]));
  const i = /* @__PURE__ */ wn(() => O(n).filter((v) => v.type && ["error", "warning"].includes(v.type))), s = /* @__PURE__ */ wn(() => O(n).filter((v) => !v.type || !["error", "warning"].includes(v.type)));
  let o = 0;
  const l = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0", f = (v) => {
    ve(n, O(n).filter((d) => d.id !== v.id), !0), r.dispatchEvent(new CustomEvent("eki-toast-closed", { bubbles: !0, composed: !0, detail: v }));
  }, u = (v) => {
    v.isVisible = !0, v.id ?? (v.id = o++);
    const d = [
      v.type === "error" && "bg-eki-light-red border-eki-red py-3",
      v.type === "success" && "bg-eki-light-green border-eki-green py-3",
      v.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    v.class = `${l} ${d}`, ve(n, [...O(n), v], !0), r.dispatchEvent(new CustomEvent("eki-toast-opened", { bubbles: !0, composed: !0, detail: v }));
  };
  r.addToast = u;
  var p = Ao(), c = He(p);
  En(c, 21, () => O(i), (v) => v.id, (v, d) => {
    Xr(v, {
      get toast() {
        return O(d);
      },
      closeToast: f
    });
  }), we(c);
  var h = Pt(c, 2);
  En(h, 21, () => O(s), (v) => v.id, (v, d) => {
    Xr(v, {
      get toast() {
        return O(d);
      },
      closeToast: f
    });
  }), we(h), we(p), We(e, p), rn();
}
customElements.define("eki-toast", zi(Co, {}, [], [], { mode: "open" }, mo));
